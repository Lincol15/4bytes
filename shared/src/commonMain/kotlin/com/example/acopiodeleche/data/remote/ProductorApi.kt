package com.example.acopiodeleche.data.remote

import com.example.acopiodeleche.data.remote.dto.ActualizarProductorRequestDto
import com.example.acopiodeleche.data.remote.dto.CambiarEstadoProductorRequestDto
import com.example.acopiodeleche.data.remote.dto.DataDto
import com.example.acopiodeleche.data.remote.dto.PaginaDto
import com.example.acopiodeleche.data.remote.dto.ProductorDto
import com.example.acopiodeleche.data.remote.dto.RegistrarProductorRequestDto
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import com.example.acopiodeleche.domain.ApiResult
import com.example.acopiodeleche.domain.map

/** Endpoints /api/v1/producers. Para el acopiador, Laravel devuelve solo sus productores asignados. */
class ProductorApi(private val cliente: ApiClient) {

    suspend fun listar(pagina: Int = 1, busqueda: String? = null): ApiResult<PaginaDto<ProductorDto>> =
        cliente.ejecutar {
            get("producers") {
                parameter("page", pagina)
                parameter("per_page", 100)
                busqueda?.takeIf { it.isNotBlank() }?.let { parameter("search", it) }
            }
        }

    suspend fun obtener(id: Int): ApiResult<ProductorDto> =
        cliente.ejecutar<DataDto<ProductorDto>> { get("producers/$id") }.map { it.data }

    suspend fun registrar(peticion: RegistrarProductorRequestDto): ApiResult<ProductorDto> =
        cliente.ejecutar<DataDto<ProductorDto>> {
            post("producers") {
                contentType(ContentType.Application.Json)
                setBody(peticion)
            }
        }.map { it.data }

    suspend fun actualizar(id: Int, peticion: ActualizarProductorRequestDto): ApiResult<ProductorDto> =
        cliente.ejecutar<DataDto<ProductorDto>> {
            put("producers/$id") {
                contentType(ContentType.Application.Json)
                setBody(peticion)
            }
        }.map { it.data }

    /** Laravel solo permite eliminar productores sin entregas, pagos ni sanciones (responde 422). */
    suspend fun eliminar(id: Int): ApiResult<Unit> =
        cliente.ejecutarSinCuerpo { delete("producers/$id") }

    suspend fun cambiarEstado(id: Int, activo: Boolean): ApiResult<ProductorDto> =
        cliente.ejecutar<DataDto<ProductorDto>> {
            patch("producers/$id/status") {
                contentType(ContentType.Application.Json)
                setBody(CambiarEstadoProductorRequestDto(if (activo) "activo" else "inactivo"))
            }
        }.map { it.data }
}
