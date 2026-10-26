package com.example.acopiodeleche.data.sync

import com.example.acopiodeleche.data.remote.ApiResult
import com.example.acopiodeleche.data.remote.EstadoConexion
import com.example.acopiodeleche.data.repository.RecoleccionRepository
import com.example.acopiodeleche.domain.model.Rol
import com.example.acopiodeleche.domain.model.SesionActual
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

/** Consulta que se hace por adelantado para que la pantalla tenga datos aunque se abra sin conexión. */
typealias Precarga = suspend () -> ApiResult<*>

/**
 * Sincronización en segundo plano mientras la app está abierta con sesión:
 * 1. sube las recolecciones guardadas sin conexión;
 * 2. descarga los datos del rol (precargas) para llenar la base local.
 */
class Sincronizador(
    private val recolecciones: RecoleccionRepository,
    private val conexion: EstadoConexion,
    private val precargasPorRol: (Rol) -> List<Precarga>
) {
    private val mutex = Mutex()

    private val _cambios = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    /** Emite cuando se subieron recolecciones, para que las pantallas recarguen la lista del servidor. */
    val cambios: SharedFlow<Unit> = _cambios.asSharedFlow()

    /** Una pasada completa; si ya hay una en curso, no hace nada. */
    suspend fun sincronizar() {
        // Sin red según el sistema operativo: no se gasta un intento (vuelve a correr al recuperarla)
        if (!conexion.hayRed.value) return
        if (!mutex.tryLock()) return
        try {
            val rol = SesionActual.rol ?: return
            if (recolecciones.enviarPendientes() > 0) _cambios.tryEmit(Unit)
            for (precarga in precargasPorRol(rol)) {
                // Sin red no tiene sentido seguir: la próxima pasada lo intentará otra vez.
                if (precarga() is ApiResult.ErrorConexion) break
            }
        } finally {
            mutex.unlock()
        }
    }

    /** Sincroniza cada cierto tiempo y en cuanto vuelve la conexión, hasta que se cancele (cierre de sesión). */
    suspend fun ejecutarMientrasHaySesion() = coroutineScope {
        // El sistema avisa que volvió la red (ConnectivityManager / NWPathMonitor)
        launch {
            conexion.hayRed.drop(1).filter { it }.collect { sincronizar() }
        }
        // Una petición volvió a llegar al servidor
        launch {
            conexion.enLinea.drop(1).filter { it }.collect { sincronizar() }
        }
        while (isActive) {
            sincronizar()
            delay(if (conexion.enLinea.value && conexion.hayRed.value) INTERVALO_EN_LINEA_MS else INTERVALO_SIN_CONEXION_MS)
        }
    }

    private companion object {
        const val INTERVALO_EN_LINEA_MS = 60_000L
        const val INTERVALO_SIN_CONEXION_MS = 20_000L
    }
}
