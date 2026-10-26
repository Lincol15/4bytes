package com.example.acopiodeleche.data.repository

import com.example.acopiodeleche.domain.ApiResult
import com.example.acopiodeleche.data.remote.RutaApi
import com.example.acopiodeleche.data.remote.dto.toDomain
import com.example.acopiodeleche.domain.map
import com.example.acopiodeleche.domain.model.ParadaRuta
import com.example.acopiodeleche.domain.model.RutaRecoleccion

class RutaRepository(private val api: RutaApi) {

    suspend fun listar(): ApiResult<List<RutaRecoleccion>> =
        obtenerTodasLasPaginas { pagina -> api.listar(pagina) }.map { lista -> lista.map { it.toDomain() } }

    suspend fun actualizarParada(parada: ParadaRuta, estado: String): ApiResult<ParadaRuta> {
        val id = idNumerico(parada.id) ?: return ERROR_ID_INVALIDO
        return api.actualizarParada(id, estado).map { it.toDomain() }
    }
}
