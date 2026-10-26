package com.example.acopiodeleche.data.remote

import com.example.acopiodeleche.data.remote.dto.ActualizarRecoleccionRequestDto
import com.example.acopiodeleche.data.remote.dto.DataDto
import com.example.acopiodeleche.data.remote.dto.PaginaDto
import com.example.acopiodeleche.data.remote.dto.RecoleccionDto
import com.example.acopiodeleche.data.remote.dto.RegistrarRecoleccionRequestDto
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/** Endpoints /api/v1/deliveries (recolecciones / entregas de leche). */
class RecoleccionApi(private val cliente: ApiClient) {

    /**
     * Lista paginada. Laravel filtra por rol: el acopiador recibe solo las suyas.
     * @param desde/hasta fechas "yyyy-MM-dd"; @param recibido true/false para filtrar por recepción en planta.
     */
    suspend fun listar(
        pagina: Int = 1,
        desde: String? = null,
        hasta: String? = null,
        recibido: Boolean? = null
    ): ApiResult<PaginaDto<RecoleccionDto>> =
        cliente.ejecutar {
            get("deliveries") {
                parameter("page", pagina)
                parameter("per_page", 100)
                desde?.let { parameter("from", it) }
                hasta?.let { parameter("to", it) }
                recibido?.let { parameter("recibido", if (it) 1 else 0) }
            }
        }

    suspend fun obtener(id: Int): ApiResult<RecoleccionDto> =
        cliente.ejecutar<DataDto<RecoleccionDto>> { get("deliveries/$id") }.map { it.data }

    suspend fun registrar(peticion: RegistrarRecoleccionRequestDto): ApiResult<RecoleccionDto> =
        cliente.ejecutar<DataDto<RecoleccionDto>> {
            post("deliveries") {
                contentType(ContentType.Application.Json)
                setBody(peticion)
            }
        }.map { it.data }

    suspend fun actualizar(id: Int, peticion: ActualizarRecoleccionRequestDto): ApiResult<RecoleccionDto> =
        cliente.ejecutar<DataDto<RecoleccionDto>> {
            patch("deliveries/$id") {
                contentType(ContentType.Application.Json)
                setBody(peticion)
            }
        }.map { it.data }

    /** Registra la recepción de la entrega en planta. */
    suspend fun registrarRecepcion(id: Int): ApiResult<RecoleccionDto> =
        cliente.ejecutar<DataDto<RecoleccionDto>> { patch("deliveries/$id/receive") }.map { it.data }
}
