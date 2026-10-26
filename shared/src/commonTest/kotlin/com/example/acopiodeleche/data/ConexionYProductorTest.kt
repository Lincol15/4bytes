package com.example.acopiodeleche.data

import com.example.acopiodeleche.data.conexion.MonitorConexionManual
import com.example.acopiodeleche.data.local.CacheRespuestas
import com.example.acopiodeleche.data.remote.ApiClient
import com.example.acopiodeleche.data.remote.ApiResult
import com.example.acopiodeleche.data.remote.EstadoConexion
import com.example.acopiodeleche.data.remote.ProductorApi
import com.example.acopiodeleche.data.remote.RecoleccionApi
import com.example.acopiodeleche.data.repository.ProductorRepository
import com.example.acopiodeleche.data.repository.RecoleccionRepository
import com.example.acopiodeleche.data.sync.Sincronizador
import com.example.acopiodeleche.domain.model.Productor
import com.example.acopiodeleche.domain.model.Rol
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.domain.model.Usuario
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/** Conectividad informada por el sistema (MonitorConexion) y edición/eliminación de productores. */
class ConexionYProductorTest {

    private val peticiones = mutableListOf<HttpRequestData>()
    private val cuerpos = mutableListOf<String>()
    private val monitor = MonitorConexionManual(inicial = true)

    private val cache = object : CacheRespuestas {
        val datos = mutableMapOf<String, String>()
        override fun leer(clave: String) = datos[clave]
        override fun guardar(clave: String, cuerpo: String) {
            datos[clave] = cuerpo
        }
    }

    private fun cliente(responder: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData) =
        ApiClient(
            engine = MockEngine { peticion ->
                peticiones += peticion
                cuerpos += peticion.body.toByteArray().decodeToString()
                responder(peticion)
            },
            baseUrl = { "http://servidor.test/api/v1/" },
            cache = cache,
            conexion = EstadoConexion(monitor.hayRed)
        )

    private fun MockRequestHandleScope.json(cuerpo: String, estado: HttpStatusCode = HttpStatusCode.OK) =
        respond(cuerpo, estado, headersOf(HttpHeaders.ContentType, "application/json"))

    private val productorJson = """{"id":5,"code":"P-5","name":"Juana","lastname":"Mamani","dni":"12345678","phone":"912345678","address":"Jr. Puno 200","comunidad":"Huata Alta","status":"activo"}"""

    @AfterTest
    fun limpiar() = SesionActual.cerrar()

    @Test
    fun sin_red_segun_el_sistema_la_consulta_usa_la_copia_local_sin_intentar_la_red() = runTest {
        val cliente = cliente { json("""{"data":[$productorJson]}""") }
        val repositorio = ProductorRepository(ProductorApi(cliente))
        repositorio.listar()
        val intentos = peticiones.size

        monitor.cambiar(hayRed = false)
        val sinRed = repositorio.listar()

        assertEquals("Juana", assertIs<ApiResult.Exito<List<Productor>>>(sinRed).datos.single().nombres)
        assertEquals(intentos, peticiones.size)
    }

    @Test
    fun el_sincronizador_no_intenta_nada_mientras_el_sistema_dice_que_no_hay_red() = runTest {
        SesionActual.iniciar(
            Usuario("7", "Carlos", "Mamani", "", "", "c@x.com", "", Rol.ACOPIADOR),
            token = "1|a"
        )
        val conexion = EstadoConexion(monitor.hayRed)
        var precargas = 0
        val sincronizador = Sincronizador(
            RecoleccionRepository(RecoleccionApi(cliente { json("""{"data":[]}""") })),
            conexion
        ) { listOf({ precargas++; ApiResult.Exito(Unit) }) }

        monitor.cambiar(hayRed = false)
        sincronizador.sincronizar()
        assertEquals(0, precargas)

        monitor.cambiar(hayRed = true)
        sincronizador.sincronizar()
        assertEquals(1, precargas)
    }

    @Test
    fun editar_productor_envia_put_con_los_datos_personales() = runTest {
        val cliente = cliente { json("""{"data":$productorJson}""") }
        val editado = Productor("5", "Juana", "Mamani", "12345678", "912345678", "Jr. Puno 200", "Huata Alta", estado = true)

        val resultado = ProductorRepository(ProductorApi(cliente)).actualizar(editado)

        assertEquals(HttpMethod.Put, peticiones.single().method)
        assertEquals("/api/v1/producers/5", peticiones.single().url.encodedPath)
        assertTrue(cuerpos.single().contains("\"phone\":\"912345678\""))
        assertTrue(cuerpos.single().contains("\"status\":\"activo\""))
        assertEquals("Huata Alta", assertIs<ApiResult.Exito<Productor>>(resultado).datos.comunidad)
    }

    @Test
    fun eliminar_productor_con_historial_devuelve_el_motivo_de_laravel() = runTest {
        val cliente = cliente { peticion ->
            if (peticion.method == HttpMethod.Delete) {
                json(
                    """{"message":"x","errors":{"producer":["El productor tiene entregas, pagos o sanciones registradas. Desactívelo en lugar de eliminarlo."]}}""",
                    HttpStatusCode.UnprocessableEntity
                )
            } else {
                json("{}")
            }
        }
        val productor = Productor("5", "Juana", "Mamani", "12345678", "912345678", "", "Huata", estado = true)

        val resultado = ProductorRepository(ProductorApi(cliente)).eliminar(productor)

        assertEquals("/api/v1/producers/5", peticiones.single().url.encodedPath)
        assertTrue(assertIs<ApiResult.ErrorValidacion>(resultado).mensaje.startsWith("El productor tiene entregas"))
    }

    @Test
    fun eliminar_productor_sin_historial_responde_sin_cuerpo() = runTest {
        val cliente = cliente { respond("", HttpStatusCode.NoContent) }
        val productor = Productor("9", "Ana", "Q", "87654321", "999999999", "", "Huata", estado = true)

        assertIs<ApiResult.Exito<Unit>>(ProductorRepository(ProductorApi(cliente)).eliminar(productor))
        assertEquals(HttpMethod.Delete, peticiones.single().method)
    }
}
