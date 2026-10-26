package com.example.acopiodeleche.data.remote

import com.example.acopiodeleche.data.remote.dto.ErrorDto
import com.example.acopiodeleche.domain.model.SesionActual
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/**
 * Cliente HTTP multiplataforma (Ktor) hacia la API REST de Laravel.
 *
 * El motor lo aporta cada plataforma (OkHttp en Android/JVM, Darwin en iOS);
 * en tests se inyecta un MockEngine. El token Bearer se lee de la sesión activa.
 */
class ApiClient(
    engine: HttpClientEngine? = null,
    @PublishedApi internal val baseUrl: () -> String = { ApiConfig.baseUrl },
    private val token: () -> String? = { SesionActual.sesion?.token?.ifBlank { null } }
) {
    val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private val configuracion: HttpClientConfig<*>.() -> Unit = {
        expectSuccess = false
        install(ContentNegotiation) { json(json) }
        install(HttpTimeout) {
            connectTimeoutMillis = 10_000
            requestTimeoutMillis = 20_000
        }
        defaultRequest {
            url(baseUrl())
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            token()?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }
    }

    val http: HttpClient = engine?.let { HttpClient(it, configuracion) } ?: HttpClient(configuracion)

    /** Ejecuta una petición y convierte la respuesta JSON en [T] o en un [ApiResult.Error]. */
    suspend inline fun <reified T> ejecutar(crossinline peticion: suspend HttpClient.() -> HttpResponse): ApiResult<T> =
        llamar(peticion) { it.body<T>() }

    /** Igual que [ejecutar] para respuestas sin cuerpo (p. ej. 204 No Content). */
    suspend inline fun ejecutarSinCuerpo(crossinline peticion: suspend HttpClient.() -> HttpResponse): ApiResult<Unit> =
        llamar(peticion) { }

    suspend inline fun <T> llamar(
        crossinline peticion: suspend HttpClient.() -> HttpResponse,
        crossinline leer: suspend (HttpResponse) -> T
    ): ApiResult<T> {
        if (baseUrl().isBlank()) {
            return ApiResult.ErrorConexion("La URL del servidor no está configurada.")
        }
        return try {
            val respuesta = http.peticion()
            if (respuesta.status.isSuccess()) ApiResult.Exito(leer(respuesta)) else errorDesde(respuesta)
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpRequestTimeoutException) {
            ApiResult.ErrorConexion("El servidor tardó demasiado en responder. Intente nuevamente.")
        } catch (e: SerializationException) {
            ApiResult.ErrorServidor(0, "Respuesta inesperada del servidor.")
        } catch (e: Exception) {
            ApiResult.ErrorConexion("No se pudo conectar con el servidor. Verifique su conexión.")
        }
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
}
