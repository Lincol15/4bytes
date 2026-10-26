package com.example.acopiodeleche.data.remote

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Conexión de la app en dos niveles:
 * - [hayRed]: lo que informa el sistema operativo (MonitorConexion, API nativa);
 * - [enLinea]: si la última petición llegó al servidor (puede haber red pero el servidor estar caído).
 */
class EstadoConexion(
    val hayRed: StateFlow<Boolean> = MutableStateFlow(true)
) {
    private val _enLinea = MutableStateFlow(true)
    val enLinea: StateFlow<Boolean> = _enLinea.asStateFlow()

    private var ultimaFallaEn: Long? = null

    fun marcarEnLinea() {
        ultimaFallaEn = null
        _enLinea.value = true
    }

    fun marcarSinConexion(ahora: Long) {
        ultimaFallaEn = ahora
        _enLinea.value = false
    }

    /**
     * true si el sistema dice que no hay red o si la red falló hace menos de [VENTANA_MS]: las
     * consultas con copia local se responden de inmediato, sin esperar un tiempo de espera.
     */
    fun sinConexionReciente(ahora: Long): Boolean =
        !hayRed.value || (ultimaFallaEn?.let { ahora - it < VENTANA_MS } ?: false)

    private companion object {
        const val VENTANA_MS = 15_000L
    }
}
