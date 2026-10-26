package com.example.acopiodeleche.data.repository

import com.example.acopiodeleche.data.remote.ApiResult
import com.example.acopiodeleche.data.remote.RecoleccionApi
import com.example.acopiodeleche.data.remote.dto.RegistrarRecoleccionRequestDto
import com.example.acopiodeleche.data.remote.dto.toDomain
import com.example.acopiodeleche.data.remote.map
import com.example.acopiodeleche.domain.model.RegistroAcopio

class RecoleccionRepository(private val api: RecoleccionApi) {

    /** Recolecciones visibles para el usuario actual (Laravel aplica el filtro por rol). */
    suspend fun listar(recibido: Boolean? = null, desde: String? = null): ApiResult<List<RegistroAcopio>> =
        obtenerTodasLasPaginas { pagina -> api.listar(pagina = pagina, desde = desde, recibido = recibido) }
            .map { lista -> lista.map { it.toDomain() } }

    /**
     * Envía la recolección al backend. Fecha, hora, vehículo, zona y precio los fija Laravel;
     * la respuesta trae el registro tal como quedó guardado.
     */
    suspend fun registrar(
        idProductor: String,
        litros: Double,
        observacion: String?,
        idParada: String? = null
    ): ApiResult<RegistroAcopio> {
        val productor = idNumerico(idProductor) ?: return ERROR_ID_INVALIDO
        return api.registrar(
            RegistrarRecoleccionRequestDto(
                idProductor = productor,
                liters = litros,
                observations = observacion?.ifBlank { null },
                idParada = idParada?.let(::idNumerico)
            )
        ).map { it.toDomain() }
    }

    suspend fun registrarRecepcion(registro: RegistroAcopio): ApiResult<RegistroAcopio> {
        val id = idNumerico(registro.id) ?: return ERROR_ID_INVALIDO
        return api.registrarRecepcion(id).map { it.toDomain() }
    }
}
