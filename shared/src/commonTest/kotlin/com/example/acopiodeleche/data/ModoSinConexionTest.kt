package com.example.acopiodeleche.data

import app.cash.sqldelight.db.SqlDriver
import com.example.acopiodeleche.data.local.AlmacenSeguroEnMemoria
import com.example.acopiodeleche.data.local.AlmacenSesionSql
import com.example.acopiodeleche.data.local.CacheRespuestasSql
import com.example.acopiodeleche.data.remote.ApiClient
import com.example.acopiodeleche.domain.ApiResult
import com.example.acopiodeleche.data.remote.ProductorApi
import com.example.acopiodeleche.data.remote.RecoleccionApi
import com.example.acopiodeleche.data.repository.ProductorRepository
import com.example.acopiodeleche.data.repository.RecoleccionRepository
import com.example.acopiodeleche.db.AcopioDatabase
import com.example.acopiodeleche.domain.model.EstadoSync
import com.example.acopiodeleche.domain.model.RegistroAcopio
import com.example.acopiodeleche.domain.model.Rol
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.domain.model.SesionUsuario
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Modo sin conexión con la base SQLite real (en memoria) en cada plataforma (commonTest). */
class ModoSinConexionTest {

    private lateinit var driver: SqlDriver
    private lateinit var db: AcopioDatabase
    private var hayRed = false
    private var reloj = 1_760_000_000_000L
    private val enviados = mutableListOf<String>()
    private var responderEntrega: suspend MockRequestHandleScope.(String) -> HttpResponseData = { cuerpo -> creada(cuerpo) }

    private val usuario = Usuario(
        id = "7", nombres = "Carlos", apellidos = "Mamani", dni = "", telefono = "",
        correo = "c@x.com", contrasena = "", rol = Rol.ACOPIADOR, comunidad = "Huata", vehiculo = "ABC-123"
    )

    private fun MockRequestHandleScope.json(cuerpo: String, estado: HttpStatusCode = HttpStatusCode.OK) =
        respond(cuerpo, estado, headersOf(HttpHeaders.ContentType, "application/json"))

    private fun MockRequestHandleScope.creada(cuerpo: String): HttpResponseData {
        val uuid = Regex("\"client_uuid\":\"([^\"]+)\"").find(cuerpo)?.groupValues?.get(1)
        return json(
            """{"data":{"id":${enviados.size},"client_uuid":"$uuid","producer_id":3,"liters":40,"delivery_date":"2026-10-12","status":"registrado"}}""",
            HttpStatusCode.Created
        )
    }

    private fun repositorio(): RecoleccionRepository {
        val cliente = ApiClient(
            engine = MockEngine { peticion ->
                if (!hayRed) throw RuntimeException("Network is unreachable")
                when {
                    peticion.method == HttpMethod.Post && peticion.url.encodedPath.endsWith("deliveries") -> {
                        val cuerpo = peticion.body.toByteArray().decodeToString()
                        enviados += cuerpo
                        responderEntrega(cuerpo)
                    }
                    else -> json("""{"data":[{"id":1,"code":"P-1","name":"Pedro","status":"activo"}]}""")
                }
            },
            baseUrl = { "http://servidor.test/api/v1/" },
            cache = CacheRespuestasSql(db, { reloj }),
            ahora = { reloj }
        )
        return RecoleccionRepository(RecoleccionApi(cliente), db, ahora = { reloj })
    }

    @BeforeTest
    fun crearBase() {
        driver = crearDriverEnMemoria()
        db = AcopioDatabase(driver)
        SesionActual.abrir(SesionUsuario(usuario, token = "1|a", refreshToken = "2|r", tokenExpiraEn = Long.MAX_VALUE, refreshExpiraEn = Long.MAX_VALUE))
    }

    @AfterTest
    fun limpiar() {
        SesionActual.cerrar()
        SesionActual.almacen = null
    }

    private suspend fun RecoleccionRepository.locales(): List<RegistroAcopio> = pendientes().first()

    @Test
    fun sin_conexion_la_recoleccion_se_guarda_en_sqlite_como_pendiente() = runTest {
        val repositorio = repositorio()

        val resultado = repositorio.registrar("3", 40.0, "Mañana", nombreProductor = "Pedro Quispe", zona = "Huata")

        val local = assertIs<ApiResult.Exito<RegistroAcopio>>(resultado).datos
        assertEquals(EstadoSync.PENDIENTE, local.estadoSync)
        assertEquals("Pedro Quispe", local.nombreProductor)
        assertEquals("ABC-123", local.vehiculo)
        assertEquals(listOf(local.clientUuid), repositorio.locales().map { it.clientUuid })
        assertTrue(enviados.isEmpty())
    }

    @Test
    fun al_volver_la_conexion_se_envian_las_pendientes_con_su_uuid_y_hora_real() = runTest {
        val repositorio = repositorio()
        val local = assertIs<ApiResult.Exito<RegistroAcopio>>(repositorio.registrar("3", 40.0, null)).datos
        reloj += 3_600_000

        hayRed = true
        val subidas = repositorio.enviarPendientes()

        assertEquals(1, subidas)
        assertTrue(enviados.single().contains("\"client_uuid\":\"${local.clientUuid}\""))
        assertTrue(enviados.single().contains("\"collected_at\":\"2025-10-09T08:53:20Z\""))
        assertTrue(repositorio.locales().isEmpty())
    }

    @Test
    fun con_conexion_se_envia_de_inmediato_y_no_queda_nada_pendiente() = runTest {
        hayRed = true
        val repositorio = repositorio()

        val resultado = repositorio.registrar("3", 40.0, null)

        assertEquals(EstadoSync.SINCRONIZADO, assertIs<ApiResult.Exito<RegistroAcopio>>(resultado).datos.estadoSync)
        assertTrue(repositorio.locales().isEmpty())
    }

    @Test
    fun con_conexion_un_rechazo_del_servidor_se_muestra_y_no_se_guarda() = runTest {
        hayRed = true
        responderEntrega = {
            json("""{"message":"x","errors":{"producer_id":["El productor no está asignado a su cuenta."]}}""", HttpStatusCode.UnprocessableEntity)
        }
        val repositorio = repositorio()

        val resultado = repositorio.registrar("3", 40.0, null)

        assertEquals("El productor no está asignado a su cuenta.", assertIs<ApiResult.ErrorValidacion>(resultado).mensaje)
        assertTrue(repositorio.locales().isEmpty())
    }

    @Test
    fun una_pendiente_rechazada_al_sincronizar_queda_con_error_y_se_puede_reintentar() = runTest {
        val repositorio = repositorio()
        repositorio.registrar("3", 40.0, null)
        hayRed = true
        responderEntrega = {
            json("""{"message":"x","errors":{"producer_id":["El productor no está asignado a su cuenta."]}}""", HttpStatusCode.UnprocessableEntity)
        }

        assertEquals(0, repositorio.enviarPendientes())
        val rechazada = repositorio.locales().single()
        assertEquals(EstadoSync.ERROR, rechazada.estadoSync)
        assertEquals("El productor no está asignado a su cuenta.", rechazada.errorSync)

        responderEntrega = { cuerpo -> creada(cuerpo) }
        repositorio.reintentar(rechazada)
        assertEquals(1, repositorio.enviarPendientes())
        assertTrue(repositorio.locales().isEmpty())
    }

    @Test
    fun error_temporal_del_servidor_deja_la_pendiente_para_el_proximo_intento() = runTest {
        val repositorio = repositorio()
        repositorio.registrar("3", 40.0, null)
        hayRed = true
        responderEntrega = { json("""{"message":"Server Error"}""", HttpStatusCode.InternalServerError) }

        assertEquals(0, repositorio.enviarPendientes())

        assertEquals(EstadoSync.PENDIENTE, repositorio.locales().single().estadoSync)
    }

    @Test
    fun las_pendientes_son_de_cada_usuario() = runTest {
        val repositorio = repositorio()
        repositorio.registrar("3", 40.0, null)

        SesionActual.abrir(SesionActual.sesion!!.copy(usuario = usuario.copy(id = "8")))

        assertTrue(repositorio.locales().isEmpty())
        hayRed = true
        assertEquals(0, repositorio.enviarPendientes())
    }

    @Test
    fun la_cache_en_sqlite_permite_consultar_sin_conexion() = runTest {
        hayRed = true
        val cliente = ApiClient(
            engine = MockEngine {
                if (!hayRed) throw RuntimeException("Network is unreachable")
                json("""{"data":[{"id":1,"code":"P-1","name":"Pedro","status":"activo"}]}""")
            },
            baseUrl = { "http://servidor.test/api/v1/" },
            cache = CacheRespuestasSql(db, { reloj }),
            ahora = { reloj }
        )
        val productores = ProductorRepository(ProductorApi(cliente))
        productores.listar()

        hayRed = false
        val sinRed = productores.listar()

        assertEquals("Pedro", assertIs<ApiResult.Exito<List<com.example.acopiodeleche.domain.model.Productor>>>(sinRed).datos.single().nombres)
    }

    @Test
    fun la_sesion_se_guarda_en_el_dispositivo_y_se_restaura_al_abrir_la_app() {
        val seguro = AlmacenSeguroEnMemoria()
        SesionActual.almacen = AlmacenSesionSql(db, seguro)
        SesionActual.abrir(SesionUsuario(usuario, "1|a", "2|r", tokenExpiraEn = reloj, refreshExpiraEn = reloj + 1_000))

        // Simula reabrir la app: la sesión en memoria se pierde, la guardada no.
        val enDisco = AlmacenSesionSql(db, seguro).leer()
        assertNotNull(enDisco)
        assertEquals("2|r", enDisco.refreshToken)
        assertEquals("ABC-123", enDisco.usuario.vehiculo)

        SesionActual.restaurar(ahora = reloj + 500)
        assertEquals("7", SesionActual.usuario?.id)

        // Con el refresh token vencido no se restaura y se borra del dispositivo (también los tokens).
        SesionActual.restaurar(ahora = reloj + 2_000)
        assertNull(AlmacenSesionSql(db, seguro).leer())
        assertNull(seguro.leer("refresh_token"))
    }

    @Test
    fun los_tokens_no_se_guardan_en_sqlite_sino_en_el_almacen_seguro() {
        val seguro = AlmacenSeguroEnMemoria()
        AlmacenSesionSql(db, seguro).guardar(SesionUsuario(usuario, "1|secreto", "2|secreto", 1, 2))

        assertEquals("1|secreto", seguro.leer("access_token"))
        assertEquals("2|secreto", seguro.leer("refresh_token"))
        // Ninguna columna de la tabla sesion contiene los tokens
        val columnas = driver.executeQuery(null, "SELECT * FROM sesion", { cursor ->
            val valores = mutableListOf<String?>()
            while (cursor.next().value) {
                for (i in 0 until 13) valores += cursor.getString(i)
            }
            app.cash.sqldelight.db.QueryResult.Value(valores)
        }, 0).value
        assertTrue(columnas.isNotEmpty())
        assertTrue(columnas.none { it?.contains("secreto") == true })
    }

    @Test
    fun sin_tokens_en_el_almacen_seguro_no_hay_sesion_que_restaurar() {
        val seguro = AlmacenSeguroEnMemoria()
        val almacen = AlmacenSesionSql(db, seguro)
        almacen.guardar(SesionUsuario(usuario, "1|a", "2|r", 1, 2))

        // P. ej. la clave del Keystore se regeneró y los tokens ya no se pueden leer
        seguro.borrar("access_token")

        assertNull(almacen.leer())
    }

    @Test
    fun la_migracion_1_a_2_quita_los_tokens_en_texto_plano() {
        val anterior = crearDriverEnMemoria(conEsquema = false)
        // Tabla sesion de la versión 1 (con tokens en texto plano)
        anterior.execute(
            null,
            "CREATE TABLE sesion (id INTEGER NOT NULL PRIMARY KEY, id_usuario TEXT NOT NULL, nombres TEXT NOT NULL, " +
                "apellidos TEXT NOT NULL, dni TEXT NOT NULL, telefono TEXT NOT NULL, correo TEXT NOT NULL, rol TEXT NOT NULL, " +
                "id_productor TEXT, comunidad TEXT, vehiculo TEXT, access_token TEXT NOT NULL, refresh_token TEXT NOT NULL, " +
                "access_expira_en INTEGER NOT NULL, refresh_expira_en INTEGER NOT NULL)",
            0
        )
        anterior.execute(null, "INSERT INTO sesion VALUES (1,'7','C','M','','','c@x.com','ACOPIADOR',NULL,NULL,NULL,'1|plano','2|plano',1,2)", 0)

        AcopioDatabase.Schema.migrate(anterior, 1, AcopioDatabase.Schema.version)

        val columnas = anterior.executeQuery(null, "PRAGMA table_info(sesion)", { cursor ->
            val nombres = mutableListOf<String>()
            while (cursor.next().value) nombres += cursor.getString(1)!!
            app.cash.sqldelight.db.QueryResult.Value(nombres)
        }, 0).value
        assertTrue("access_token" !in columnas && "refresh_token" !in columnas)
        assertNull(AcopioDatabase(anterior).sesionQueries.obtener().executeAsOneOrNull())
    }
}
