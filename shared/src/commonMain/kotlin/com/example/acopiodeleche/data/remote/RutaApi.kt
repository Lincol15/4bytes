package com.example.acopiodeleche.data.remote

import com.example.acopiodeleche.data.remote.dto.ActualizarParadaRequestDto
import com.example.acopiodeleche.data.remote.dto.DataDto
import com.example.acopiodeleche.data.remote.dto.PaginaDto
import com.example.acopiodeleche.data.remote.dto.ParadaDto
import com.example.acopiodeleche.data.remote.dto.RutaDto
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import com.example.acopiodeleche.domain.ApiResult
import com.example.acopiodeleche.domain.map

/** Endpoints /api/v1/routes y /api/v1/route-stops. */
class RutaApi(private val cliente: ApiClient) {

    suspend fun listar(pagina: Int = 1): ApiResult<PaginaDto<RutaDto>> =
        cliente.ejecutar { get("routes") { parameter("page", pagina) } }

    suspend fun obtener(id: Int): ApiResult<RutaDto> =
        cliente.ejecutar<DataDto<RutaDto>> { get("routes/$id") }.map { it.data }

    suspend fun actualizarParada(idParada: Int, estado: String): ApiResult<ParadaDto> =
        cliente.ejecutar<DataDto<ParadaDto>> {
            patch("route-stops/$idParada") {
                contentType(ContentType.Application.Json)
                setBody(ActualizarParadaRequestDto(estado))
            }
        }.map { it.data }
}
