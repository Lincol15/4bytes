package com.example.acopiodeleche.data.remote

import com.example.acopiodeleche.data.remote.dto.DataDto
import com.example.acopiodeleche.data.remote.dto.LoginResponseDto
import com.example.acopiodeleche.data.remote.dto.aSesion
import com.example.acopiodeleche.data.remote.dto.rolDesdeApi
import com.example.acopiodeleche.data.remote.dto.toDomain
import com.example.acopiodeleche.domain.model.SesionActual
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Cambia el refresh token por un par nuevo (POST auth/refresh). Laravel rota el refresh token
 * en cada uso, así que solo una renovación puede estar en curso: las demás esperan y reutilizan
 * el resultado.
 */
internal class RenovadorSesion(
    private val cliente: ApiClient,
    private val ahora: () -> Long
) {
    private val mutex = Mutex()

    /** Renueva antes de enviar si el access token vence en menos de [MARGEN_MS]. */
    suspend fun renovarSiEstaPorVencer() {
        val sesion = SesionActual.sesion ?: return
        if (sesion.refreshToken.isBlank() || sesion.tokenExpiraEn - MARGEN_MS > ahora()) return
        // Sin conexión no se puede renovar; la petición seguirá y usará la copia local.
        if (cliente.conexion.sinConexionReciente(ahora())) return
        renovar(sesion.token)
    }

    /**
     * Renueva la sesión si [tokenRechazado] sigue siendo el token actual.
     * @return true si hay un access token nuevo con el que reintentar.
     */
    suspend fun renovar(tokenRechazado: String): Boolean = mutex.withLock {
        val sesion = SesionActual.sesion ?: return false
        // Otra petición ya renovó mientras esta esperaba el turno.
        if (sesion.token != tokenRechazado) return true
        if (sesion.refreshToken.isBlank()) return false

        val respuesta = try {
            cliente.http.post("auth/refresh") { header(HttpHeaders.Authorization, "Bearer ${sesion.refreshToken}") }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            cliente.conexion.marcarSinConexion(ahora())
            return false
        }
        cliente.conexion.marcarEnLinea()

        when (respuesta.status.value) {
            in 200..299 -> {
                val datos = try {
                    cliente.json.decodeFromString<DataDto<LoginResponseDto>>(respuesta.bodyAsText()).data
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    return false
                }
                // Si el usuario sigue logueado, se conservan sus datos y se actualizan con los del servidor.
                val usuario = rolDesdeApi(datos.user.role)?.let { datos.user.toDomain(it) } ?: sesion.usuario
                SesionActual.abrir(datos.aSesion(usuario, ahora()))
                true
            }
            // Refresh token vencido o revocado, o cuenta desactivada: hay que iniciar sesión de nuevo.
            401, 403 -> {
                SesionActual.expirar()
                false
            }
            else -> false
        }
    }

    private companion object {
        const val MARGEN_MS = 30_000L
    }
}
