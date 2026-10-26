package com.example.acopiodeleche.data.remote

import com.example.acopiodeleche.data.remote.dto.DataDto
import com.example.acopiodeleche.data.remote.dto.InsumoDto
import com.example.acopiodeleche.data.remote.dto.LoteDto
import com.example.acopiodeleche.data.remote.dto.PaginaDto
import com.example.acopiodeleche.data.remote.dto.ProductoDto
import com.example.acopiodeleche.data.remote.dto.RecetaDto
import com.example.acopiodeleche.data.remote.dto.RegistrarProduccionRequestDto
import com.example.acopiodeleche.data.remote.dto.RegistrarRecetaRequestDto
import com.example.acopiodeleche.data.remote.dto.RegistrarVentaRequestDto
import com.example.acopiodeleche.data.remote.dto.VentaDto
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import com.example.acopiodeleche.domain.ApiResult
import com.example.acopiodeleche.domain.map

/** Endpoints de planta (/api/v1/plant): catálogo, recetas, producción y ventas. */
class PlantaApi(private val cliente: ApiClient) {

    suspend fun productos(): ApiResult<List<ProductoDto>> =
        cliente.ejecutar<DataDto<List<ProductoDto>>> { get("plant/products") }.map { it.data }

    suspend fun insumos(): ApiResult<List<InsumoDto>> =
        cliente.ejecutar<DataDto<List<InsumoDto>>> { get("plant/ingredients") }.map { it.data }

    suspend fun recetas(): ApiResult<List<RecetaDto>> =
        cliente.ejecutar<DataDto<List<RecetaDto>>> { get("plant/recipes") }.map { it.data }

    suspend fun registrarReceta(peticion: RegistrarRecetaRequestDto): ApiResult<RecetaDto> =
        cliente.ejecutar<DataDto<RecetaDto>> {
            post("plant/recipes") {
                contentType(ContentType.Application.Json)
                setBody(peticion)
            }
        }.map { it.data }

    suspend fun lotes(): ApiResult<PaginaDto<LoteDto>> = cliente.ejecutar { get("plant/batches") }

    suspend fun registrarProduccion(peticion: RegistrarProduccionRequestDto): ApiResult<List<LoteDto>> =
        cliente.ejecutar<DataDto<List<LoteDto>>> {
            post("plant/production") {
                contentType(ContentType.Application.Json)
                setBody(peticion)
            }
        }.map { it.data }

    suspend fun ventas(): ApiResult<PaginaDto<VentaDto>> = cliente.ejecutar { get("plant/sales") }

    suspend fun registrarVenta(peticion: RegistrarVentaRequestDto): ApiResult<VentaDto> =
        cliente.ejecutar<DataDto<VentaDto>> {
            post("plant/sales") {
                contentType(ContentType.Application.Json)
                setBody(peticion)
            }
        }.map { it.data }
}
