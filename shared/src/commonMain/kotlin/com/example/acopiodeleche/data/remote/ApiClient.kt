package com.example.acopiodeleche.data.remote

import com.example.acopiodeleche.data.local.CacheRespuestas
import com.example.acopiodeleche.data.remote.dto.ErrorDto
import com.example.acopiodeleche.domain.model.SesionActual
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.time.Clock

/**
 * Cliente HTTP multiplataforma (Ktor) hacia la API REST de Laravel.
 *
 * El motor lo aporta cada plataforma (OkHttp en Android/JVM, Darwin en iOS);
 * en tests se inyecta un MockEngine. Además:
 * - envía el access token de la sesión y, si vence, lo renueva con el refresh token y reintenta;
 * - guarda cada respuesta GET en [cache] y la devuelve cuando no hay conexión (offline-first).
 */
class ApiClient(
    engine: HttpClientEngine? = null,
    private val baseUrl: () -> String = { ApiConfig.baseUrl },
    private val token: () -> String? = { SesionActual.sesion?.token?.ifBlank { null } },
    private val cache: CacheRespuestas? = null,
    val conexion: EstadoConexion = EstadoConexion(),
    private val ahora: () -> Long = { Clock.System.now().toEpochMilliseconds() }
) {
    val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    /** Pone el token de la sesión (si la petición no trae uno propio) y anota la URL de los GET para la caché. */
    private val sesionYCache = createClientPlugin("SesionYCacheOffline") {
        onRequest { peticion, _ ->
            val enCurso = currentCoroutineContext()[PeticionEnCurso]
            if (!peticion.headers.contains(HttpHeaders.Authorization)) {
                token()?.let {
                    peticion.headers.append(HttpHeaders.Authorization, "Bearer $it")
                    enCurso?.tokenUsado = it
                }
            }
            if (enCurso != null && peticion.method == HttpMethod.Get) {
                val clave = peticion.url.buildString()
                enCurso.claveCache = clave
                // Sin red hace un momento: se responde con la copia local sin esperar otro timeout.
                if (conexion.sinConexionReciente(ahora()) && leerCache(clave) != null) throw SinConexionReciente()
            }
        }
    }

    private val configuracion: HttpClientConfig<*>.() -> Unit = {
        expectSuccess = false
        install(ContentNegotiation) { json(json) }
        install(HttpTimeout) {
            connectTimeoutMillis = 8_000
            requestTimeoutMillis = 20_000
        }
        defaultRequest {
            url(baseUrl())
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        install(sesionYCache)
    }

    val http: HttpClient = engine?.let { HttpClient(it, configuracion) } ?: HttpClient(configuracion)

    private val renovador = RenovadorSesion(this, ahora)

    /** Ejecuta una petición y convierte la respuesta JSON en [T] o en un [ApiResult.Error]. */
    suspend inline fun <reified T> ejecutar(noinline peticion: suspend HttpClient.() -> HttpResponse): ApiResult<T> =
        llamar(peticion) { json.decodeFromString<T>(it) }

    /** Igual que [ejecutar] para respuestas sin cuerpo (p. ej. 204 No Content). */
    suspend fun ejecutarSinCuerpo(peticion: suspend HttpClient.() -> HttpResponse): ApiResult<Unit> =
        llamar(peticion) { }

    suspend fun <T> llamar(peticion: suspend HttpClient.() -> HttpResponse, leer: (String) -> T): ApiResult<T> {
        if (baseUrl().isBlank()) {
            return ApiResult.ErrorConexion("La URL del servidor no está configurada.")
        }
        renovador.renovarSiEstaPorVencer()
        val intento = intentar(peticion, leer)
        val tokenRechazado = intento.tokenUsado
        if (intento.resultado is ApiResult.NoAutenticado && tokenRechazado != null && renovador.renovar(tokenRechazado)) {
            return intentar(peticion, leer).resultado
        }
        return intento.resultado
    }

    private suspend fun <T> intentar(
        peticion: suspend HttpClient.() -> HttpResponse,
        leer: (String) -> T
    ): Intento<T> {
        val enCurso = PeticionEnCurso()
        val resultado: ApiResult<T> = try {
            val respuesta = withContext(enCurso) { http.peticion() }
            conexion.marcarEnLinea()
            when {
                respuesta.status.isSuccess() -> {
                    val texto = respuesta.bodyAsText()
                    val datos = leer(texto)
                    enCurso.claveCache?.let { guardarCache(it, texto) }
                    ApiResult.Exito(datos)
                }
                // Servidor caído (5xx): para consultas se muestra la última copia guardada.
                respuesta.status.value >= 500 -> desdeCache(enCurso, leer) ?: errorDesde(respuesta)
                else -> errorDesde(respuesta)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: SinConexionReciente) {
            desdeCache(enCurso, leer) ?: ApiResult.ErrorConexion(MENSAJE_SIN_CONEXION)
        } catch (e: SerializationException) {
            ApiResult.ErrorServidor(0, "Respuesta inesperada del servidor.")
        } catch (e: HttpRequestTimeoutException) {
            conexion.marcarSinConexion(ahora())
            desdeCache(enCurso, leer)
                ?: ApiResult.ErrorConexion("El servidor tardó demasiado en responder. Intente nuevamente.")
        } catch (e: Exception) {
            conexion.marcarSinConexion(ahora())
            desdeCache(enCurso, leer) ?: ApiResult.ErrorConexion(MENSAJE_SIN_CONEXION)
        }
        return Intento(resultado, enCurso.tokenUsado)
    }

    private fun <T> desdeCache(enCurso: PeticionEnCurso, leer: (String) -> T): ApiResult<T>? {
        val texto = enCurso.claveCache?.let(::leerCache) ?: return null
        return runCatching { ApiResult.Exito(leer(texto)) }.getOrNull()
    }

    // Un fallo de la base local nunca debe tumbar una petición a la API.
    private fun leerCache(clave: String): String? = runCatching { cache?.leer(clave) }.getOrNull()

    private fun guardarCache(clave: String, cuerpo: String) {
        runCatching { cache?.guardar(clave, cuerpo) }
    }

    suspend fun errorDesde(respuesta: HttpResponse): ApiResult.Error {
        val error = try {
            respuesta.body<ErrorDto>()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ErrorDto()
        }
        val codigo = respuesta.status.value
        return when (codigo) {
            401 -> ApiResult.NoAutenticado("Su sesión expiró. Inicie sesión nuevamente.")
            403 -> ApiResult.SinPermiso(error.message ?: "No tiene permisos para esta operación.")
            422 -> ApiResult.ErrorValidacion(
                mensaje = error.errors.values.firstOrNull()?.firstOrNull() ?: error.message ?: "Datos inválidos.",
                errores = error.errors
            )
            404 -> ApiResult.ErrorServidor(codigo, "El registro solicitado no existe.")
            429 -> ApiResult.ErrorServidor(codigo, "Demasiados intentos. Espere un momento e intente nuevamente.")
            else -> ApiResult.ErrorServidor(codigo, "Error del servidor ($codigo). Intente más tarde.")
        }
    }

    private class Intento<T>(val resultado: ApiResult<T>, val tokenUsado: String?)

    /** Datos de la petición en curso que el plugin anota (URL para la caché y token enviado). */
    private class PeticionEnCurso : AbstractCoroutineContextElement(PeticionEnCurso) {
        companion object Clave : CoroutineContext.Key<PeticionEnCurso>

        var claveCache: String? = null
        var tokenUsado: String? = null
    }

    private class SinConexionReciente : Exception()

    private companion object {
        const val MENSAJE_SIN_CONEXION = "No se pudo conectar con el servidor. Verifique su conexión."
    }
}
