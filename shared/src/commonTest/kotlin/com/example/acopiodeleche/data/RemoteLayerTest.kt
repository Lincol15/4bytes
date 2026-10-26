package com.example.acopiodeleche.data

import com.example.acopiodeleche.data.remote.ApiClient
import com.example.acopiodeleche.data.remote.ApiResult
import com.example.acopiodeleche.data.remote.AuthApi
import com.example.acopiodeleche.data.remote.ProductorApi
import com.example.acopiodeleche.data.remote.RecoleccionApi
import com.example.acopiodeleche.data.repository.AuthRepository
import com.example.acopiodeleche.data.repository.ProductorRepository
import com.example.acopiodeleche.data.repository.RecoleccionRepository
import com.example.acopiodeleche.domain.model.Rol
import com.example.acopiodeleche.domain.model.SesionActual
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RemoteLayerTest {

    private val peticiones = mutableListOf<HttpRequestData>()
    private val cuerpos = mutableListOf<String>()

    private fun cliente(responder: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData) =
        ApiClient(
            engine = MockEngine { peticion ->
                peticiones += peticion
                cuerpos += peticion.body.toByteArray().decodeToString()
                responder(peticion)
            },
            baseUrl = { "http://servidor.test/api/v1/" }
        )

    private fun MockRequestHandleScope.json(cuerpo: String, estado: HttpStatusCode = HttpStatusCode.OK) =
        respond(cuerpo, estado, headersOf(HttpHeaders.ContentType, "application/json"))

    @AfterTest
    fun limpiarSesion() = SesionActual.cerrar()

    @Test
    fun login_guarda_token_y_las_peticiones_siguientes_lo_envian() = runTest {
        val cliente = cliente { peticion ->
            if (peticion.url.encodedPath.endsWith("auth/login")) {
                json("""{"data":{"token":"1|abc","token_type":"Bearer","user":{"id":7,"name":"Carlos","lastname":"Mamani","email":"c@x.com","role":"acopiador","active":true,"comunidad":"Huata","vehiculo":"ABC-123"}}}""")
            } else {
                json("""{"data":[]}""")
            }
        }

        val resultado = AuthRepository(AuthApi(cliente)).iniciarSesion("c@x.com", "secreto")

        val usuario = assertIs<ApiResult.Exito<*>>(resultado).datos as com.example.acopiodeleche.domain.model.Usuario
        assertEquals(Rol.ACOPIADOR, usuario.rol)
        assertEquals("7", usuario.id)
        assertEquals("", usuario.contrasena)
        assertEquals("1|abc", SesionActual.sesion?.token)
        assertTrue(cuerpos.first().contains("\"login\":\"c@x.com\""))

        ProductorRepository(ProductorApi(cliente)).listar()
        assertEquals("Bearer 1|abc", peticiones.last().headers[HttpHeaders.Authorization])
        assertEquals("application/json", peticiones.last().headers[HttpHeaders.Accept])
    }

    @Test
    fun error_422_se_convierte_en_error_de_validacion_con_el_mensaje_de_laravel() = runTest {
        val cliente = cliente {
            json(
                """{"message":"La cantidad mínima es 0.1 litros","errors":{"liters":["La cantidad mínima es 0.1 litros"]}}""",
                HttpStatusCode.UnprocessableEntity
            )
        }

        val resultado = RecoleccionRepository(RecoleccionApi(cliente)).registrar("3", 0.0, null)

        val error = assertIs<ApiResult.ErrorValidacion>(resultado)
        assertEquals("La cantidad mínima es 0.1 litros", error.mensaje)
        assertEquals(listOf("La cantidad mínima es 0.1 litros"), error.errores["liters"])
    }

    @Test
    fun errores_401_403_y_sin_conexion_se_clasifican() = runTest {
        val noAutenticado = cliente { json("""{"message":"Unauthenticated."}""", HttpStatusCode.Unauthorized) }
        assertIs<ApiResult.NoAutenticado>(ProductorRepository(ProductorApi(noAutenticado)).listar())

        val sinPermiso = cliente { json("""{"message":"No tiene permisos"}""", HttpStatusCode.Forbidden) }
        assertEquals(
            "No tiene permisos",
            assertIs<ApiResult.SinPermiso>(ProductorRepository(ProductorApi(sinPermiso)).listar()).mensaje
        )

        val sinConexion = cliente { throw RuntimeException("Connection refused") }
        assertIs<ApiResult.ErrorConexion>(ProductorRepository(ProductorApi(sinConexion)).listar())
    }

    @Test
    fun registrar_recoleccion_envia_post_y_mapea_respuesta_al_dominio() = runTest {
        val cliente = cliente {
            json(
                """{"data":{"id":15,"producer_id":3,"producer_name":"Pedro Quispe","collector_name":"Carlos Mamani","zona":"Huata","liters":50,"total_amount":85,"delivery_date":"2026-10-12","delivery_time":"08:30","vehicle_plate":"ABC-123","observations":null,"status":"registrado","recibido":false}}""",
                HttpStatusCode.Created
            )
        }

        val resultado = RecoleccionRepository(RecoleccionApi(cliente)).registrar("3", 50.0, "  ")

        assertEquals(HttpMethod.Post, peticiones.single().method)
        assertEquals("/api/v1/deliveries", peticiones.single().url.encodedPath)
        assertEquals("""{"producer_id":3,"liters":50.0}""", cuerpos.single())
        val registro = assertIs<ApiResult.Exito<com.example.acopiodeleche.domain.model.RegistroAcopio>>(resultado).datos
        assertEquals("15", registro.id)
        assertEquals("12/10/2026", registro.fecha)
        assertEquals("Pedro Quispe", registro.nombreProductor)
        assertNull(registro.observacion)
    }

    @Test
    fun listar_recorre_todas_las_paginas() = runTest {
        val cliente = cliente { peticion ->
            val pagina = peticion.url.parameters["page"]
            json(
                """{"data":[{"id":${if (pagina == "1") 1 else 2},"code":"P-$pagina","name":"N","status":"activo"}],"meta":{"current_page":$pagina,"last_page":2,"total":2}}"""
            )
        }

        val resultado = ProductorRepository(ProductorApi(cliente)).listar()

        assertEquals(listOf("1", "2"), assertIs<ApiResult.Exito<List<com.example.acopiodeleche.domain.model.Productor>>>(resultado).datos.map { it.idProductor })
        assertEquals(2, peticiones.size)
    }
}
