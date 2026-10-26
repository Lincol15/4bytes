package com.example.acopiodeleche.data

import com.example.acopiodeleche.data.local.CacheRespuestas
import com.example.acopiodeleche.data.remote.ApiClient
import com.example.acopiodeleche.domain.ApiResult
import com.example.acopiodeleche.data.remote.ProductorApi
import com.example.acopiodeleche.data.remote.RecoleccionApi
import com.example.acopiodeleche.data.repository.ProductorRepository
import com.example.acopiodeleche.data.repository.RecoleccionRepository
import com.example.acopiodeleche.domain.model.Rol
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.domain.model.SesionUsuario
import com.example.acopiodeleche.domain.model.Usuario
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Renovación con refresh token y lectura de la copia local cuando no hay conexión. */
class SesionYCacheTest {

    private val peticiones = mutableListOf<HttpRequestData>()
    private var reloj = 1_000_000L
    private val cache = CacheEnMemoria()

    private val usuario = Usuario(
        id = "7", nombres = "Carlos", apellidos = "Mamani", dni = "", telefono = "",
        correo = "c@x.com", contrasena = "", rol = Rol.ACOPIADOR
    )

    private class CacheEnMemoria : CacheRespuestas {
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
                responder(peticion)
            },
            baseUrl = { "http://servidor.test/api/v1/" },
            cache = cache,
            ahora = { reloj }
        )

    private fun MockRequestHandleScope.json(cuerpo: String, estado: HttpStatusCode = HttpStatusCode.OK) =
        respond(cuerpo, estado, headersOf(HttpHeaders.ContentType, "application/json"))

    private fun MockRequestHandleScope.parNuevo() = json(
        """{"data":{"access_token":"3|nuevo","refresh_token":"4|ref2","token_type":"Bearer","expires_in":3600,"refresh_expires_in":2592000,"user":{"id":7,"name":"Carlos","lastname":"Mamani","email":"c@x.com","role":"acopiador"}}}"""
    )

    private val listaProductores = """{"data":[{"id":1,"code":"P-1","name":"Pedro","status":"activo"}]}"""

    private val HttpRequestData.esRefresh get() = url.encodedPath.endsWith("auth/refresh")

    @BeforeTest
    fun abrirSesion() {
        SesionActual.abrir(
            SesionUsuario(usuario, token = "1|viejo", refreshToken = "2|ref", tokenExpiraEn = reloj + 3_600_000, refreshExpiraEn = reloj + 10_000_000)
        )
    }

    @AfterTest
    fun limpiarSesion() = SesionActual.cerrar()

    @Test
    fun token_vencido_se_renueva_con_el_refresh_token_y_la_peticion_se_reintenta() = runTest {
        val cliente = cliente { peticion ->
            when {
                peticion.esRefresh -> parNuevo()
                peticion.headers[HttpHeaders.Authorization] == "Bearer 3|nuevo" -> json(listaProductores)
                else -> json("""{"message":"Unauthenticated."}""", HttpStatusCode.Unauthorized)
            }
        }

        val resultado = ProductorRepository(ProductorApi(cliente)).listar()

        assertEquals(1, assertIs<ApiResult.Exito<List<*>>>(resultado).datos.size)
        val refresh = peticiones.single { it.esRefresh }
        assertEquals("Bearer 2|ref", refresh.headers[HttpHeaders.Authorization])
        assertEquals("3|nuevo", SesionActual.sesion?.token)
        assertEquals("4|ref2", SesionActual.sesion?.refreshToken)
        assertEquals(reloj + 3_600_000, SesionActual.sesion?.tokenExpiraEn)
    }

    @Test
    fun varias_peticiones_con_token_vencido_renuevan_una_sola_vez() = runTest {
        val cliente = cliente { peticion ->
            when {
                peticion.esRefresh -> parNuevo()
                peticion.headers[HttpHeaders.Authorization] == "Bearer 3|nuevo" -> json(listaProductores)
                else -> json("""{"message":"Unauthenticated."}""", HttpStatusCode.Unauthorized)
            }
        }
        val repositorio = ProductorRepository(ProductorApi(cliente))

        val resultados = List(3) { async { repositorio.listar() } }.awaitAll()

        resultados.forEach { assertIs<ApiResult.Exito<*>>(it) }
        assertEquals(1, peticiones.count { it.esRefresh })
    }

    @Test
    fun token_por_vencer_se_renueva_antes_de_enviar_la_peticion() = runTest {
        SesionActual.abrir(SesionActual.sesion!!.copy(tokenExpiraEn = reloj + 10_000))
        val cliente = cliente { peticion -> if (peticion.esRefresh) parNuevo() else json(listaProductores) }

        ProductorRepository(ProductorApi(cliente)).listar()

        assertTrue(peticiones.first().esRefresh)
        assertEquals("Bearer 3|nuevo", peticiones.last().headers[HttpHeaders.Authorization])
    }

    @Test
    fun refresh_token_rechazado_cierra_la_sesion_como_expirada() = runTest {
        val cliente = cliente { json("""{"message":"Unauthenticated."}""", HttpStatusCode.Unauthorized) }

        val resultado = ProductorRepository(ProductorApi(cliente)).listar()

        assertIs<ApiResult.NoAutenticado>(resultado)
        assertNull(SesionActual.sesion)
        assertTrue(SesionActual.sesionExpirada.value)
    }

    @Test
    fun sin_conexion_las_consultas_devuelven_la_ultima_copia_guardada() = runTest {
        var hayRed = true
        val cliente = cliente { if (hayRed) json(listaProductores) else throw RuntimeException("Network is unreachable") }
        val repositorio = ProductorRepository(ProductorApi(cliente))

        assertIs<ApiResult.Exito<*>>(repositorio.listar())
        hayRed = false
        val sinRed = repositorio.listar()

        val productores = assertIs<ApiResult.Exito<List<com.example.acopiodeleche.domain.model.Productor>>>(sinRed).datos
        assertEquals("Pedro", productores.single().nombres)
        assertFalse(cliente.conexion.enLinea.value)
    }

    @Test
    fun recien_caida_la_red_la_copia_local_se_usa_sin_volver_a_intentar_la_red() = runTest {
        var hayRed = true
        val cliente = cliente { if (hayRed) json(listaProductores) else throw RuntimeException("Network is unreachable") }
        val repositorio = ProductorRepository(ProductorApi(cliente))
        repositorio.listar()
        hayRed = false
        repositorio.listar()
        val intentosAntes = peticiones.size

        reloj += 5_000
        assertIs<ApiResult.Exito<*>>(repositorio.listar())
        assertEquals(intentosAntes, peticiones.size)

        // Pasada la ventana se vuelve a probar la red y, si respondió, se marca en línea.
        reloj += 20_000
        hayRed = true
        repositorio.listar()
        assertEquals(intentosAntes + 1, peticiones.size)
        assertTrue(cliente.conexion.enLinea.value)
    }

    @Test
    fun la_copia_local_es_por_url_y_no_sirve_para_otras_consultas() = runTest {
        val cliente = cliente { throw RuntimeException("Network is unreachable") }

        assertIs<ApiResult.ErrorConexion>(RecoleccionRepository(RecoleccionApi(cliente)).listar(recibido = false))
        assertTrue(cache.datos.isEmpty())
    }
}
