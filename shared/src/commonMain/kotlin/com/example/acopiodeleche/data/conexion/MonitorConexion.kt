package com.example.acopiodeleche.data.conexion

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Estado de la red que informa el sistema operativo (WiFi o datos con acceso a internet).
 * Android usa ConnectivityManager e iOS NWPathMonitor; así la app sabe que está sin conexión
 * antes de intentar una petición y sincroniza en cuanto vuelve la red.
 */
interface MonitorConexion {
    val hayRed: StateFlow<Boolean>
}

/** Desktop y pruebas: estado fijo o controlado a mano. */
class MonitorConexionManual(inicial: Boolean = true) : MonitorConexion {
    private val estado = MutableStateFlow(inicial)
    override val hayRed: StateFlow<Boolean> = estado.asStateFlow()

    fun cambiar(hayRed: Boolean) {
        estado.value = hayRed
    }
}
