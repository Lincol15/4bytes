package com.example.acopiodeleche.data.conexion

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Network.nw_path_get_status
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_status_satisfied
import platform.darwin.dispatch_get_main_queue

/** Red del sistema con NWPathMonitor (framework Network de iOS). */
class MonitorConexionIos : MonitorConexion {

    private val estado = MutableStateFlow(true)
    override val hayRed: StateFlow<Boolean> = estado.asStateFlow()

    private val monitor = nw_path_monitor_create()

    init {
        nw_path_monitor_set_update_handler(monitor) { ruta ->
            estado.value = nw_path_get_status(ruta) == nw_path_status_satisfied
        }
        nw_path_monitor_set_queue(monitor, dispatch_get_main_queue())
        nw_path_monitor_start(monitor)
    }
}
