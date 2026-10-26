package com.example.acopiodeleche.domain.usecase

import com.example.acopiodeleche.domain.ApiResult
import com.example.acopiodeleche.domain.model.EstadoSync
import com.example.acopiodeleche.domain.model.RegistroAcopio
import com.example.acopiodeleche.domain.repository.RecoleccionRepositorio
import kotlinx.coroutines.flow.Flow

/** Casos de uso de la recolección de leche (offline-first). */
class ObtenerRecolecciones(private val repositorio: RecoleccionRepositorio) {
    suspend operator fun invoke(recibido: Boolean? = null, desde: String? = null): ApiResult<List<RegistroAcopio>> =
        repositorio.listar(recibido = recibido, desde = desde)
}

/**
 * Une las recolecciones del servidor con las guardadas en el teléfono: primero las locales
 * (pendientes o rechazadas) y sin repetir las que el servidor ya tiene (mismo client_uuid).
 */
class CombinarRecolecciones {
    operator fun invoke(servidor: List<RegistroAcopio>, locales: List<RegistroAcopio>): List<RegistroAcopio> {
        val enServidor = servidor.mapNotNull { it.clientUuid }.toSet()
        return locales.filter { it.clientUuid !in enServidor } + servidor
    }
}

class ObservarRecoleccionesPendientes(private val repositorio: RecoleccionRepositorio) {
    operator fun invoke(): Flow<List<RegistroAcopio>> = repositorio.pendientes()
}

class RegistrarRecoleccion(private val repositorio: RecoleccionRepositorio) {
    suspend operator fun invoke(
        idProductor: String,
        litros: Double,
        observacion: String?,
        nombreProductor: String? = null,
        zona: String? = null
    ): ApiResult<RegistroAcopio> {
        if (idProductor.isBlank()) return ApiResult.ErrorValidacion("Seleccione un productor.")
        if (litros <= 0.0 || litros.isNaN()) return ApiResult.ErrorValidacion("La cantidad de litros debe ser mayor a 0.")
        if (litros > LITROS_MAXIMOS) {
            return ApiResult.ErrorValidacion("La cantidad máxima permitida es ${LITROS_MAXIMOS.toInt()} litros.")
        }
        return repositorio.registrar(
            idProductor = idProductor,
            litros = litros,
            observacion = observacion?.trim()?.ifBlank { null },
            nombreProductor = nombreProductor,
            zona = zona
        )
    }

    private companion object {
        /** Tope por defecto de Laravel (litros_maximos_entrega); el servidor valida el valor configurado. */
        const val LITROS_MAXIMOS = 10_000.0
    }
}

/** Vuelve a poner en cola una recolección que el servidor rechazó. */
class ReintentarRecoleccion(private val repositorio: RecoleccionRepositorio) {
    operator fun invoke(registro: RegistroAcopio) {
        if (registro.estadoSync == EstadoSync.ERROR) repositorio.reintentar(registro)
    }
}

/** Elimina del teléfono una recolección que no llegó al servidor (las ya sincronizadas no se tocan). */
class DescartarRecoleccion(private val repositorio: RecoleccionRepositorio) {
    operator fun invoke(registro: RegistroAcopio) {
        if (registro.esLocal) repositorio.descartar(registro)
    }
}
