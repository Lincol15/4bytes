package com.example.acopiodeleche.data.repository

import com.example.acopiodeleche.data.remote.ApiResult
import com.example.acopiodeleche.data.remote.PlantaApi
import com.example.acopiodeleche.data.remote.dto.InsumoRecetaDto
import com.example.acopiodeleche.data.remote.dto.ItemProduccionDto
import com.example.acopiodeleche.data.remote.dto.ItemVentaRequestDto
import com.example.acopiodeleche.data.remote.dto.RegistrarProduccionRequestDto
import com.example.acopiodeleche.data.remote.dto.RegistrarRecetaRequestDto
import com.example.acopiodeleche.data.remote.dto.RegistrarVentaRequestDto
import com.example.acopiodeleche.data.remote.dto.fechaPantallaAApi
import com.example.acopiodeleche.data.remote.dto.toDomain
import com.example.acopiodeleche.data.remote.map
import com.example.acopiodeleche.domain.model.InsumoPlanta
import com.example.acopiodeleche.domain.model.LoteProduccion
import com.example.acopiodeleche.domain.model.ProductoPlanta
import com.example.acopiodeleche.domain.model.Receta
import com.example.acopiodeleche.domain.model.RecetaDetalle
import com.example.acopiodeleche.domain.model.VentaSalida

private val ERROR_FECHA = ApiResult.ErrorValidacion("La fecha debe tener el formato dd/MM/yyyy.")

class PlantaRepository(private val api: PlantaApi) {

    suspend fun productos(): ApiResult<List<ProductoPlanta>> = api.productos().map { l -> l.map { it.toDomain() } }

    suspend fun insumos(): ApiResult<List<InsumoPlanta>> = api.insumos().map { l -> l.map { it.toDomain() } }

    suspend fun recetas(): ApiResult<List<Pair<Receta, List<RecetaDetalle>>>> = api.recetas().map { l -> l.map { it.toDomain() } }

    /** [insumos]: id de insumo del catálogo -> cantidad por unidad producida. */
    suspend fun registrarReceta(
        nombre: String,
        idProducto: String,
        descripcion: String?,
        insumos: Map<String, Double>
    ): ApiResult<Pair<Receta, List<RecetaDetalle>>> {
        val producto = idNumerico(idProducto) ?: return ERROR_ID_INVALIDO
        return api.registrarReceta(
            RegistrarRecetaRequestDto(
                idProducto = producto,
                name = nombre.trim(),
                instructions = descripcion?.ifBlank { null },
                ingredients = insumos.mapNotNull { (id, cantidad) -> idNumerico(id)?.let { InsumoRecetaDto(it, cantidad = cantidad) } }
            )
        ).map { it.toDomain() }
    }

    suspend fun lotes(): ApiResult<List<LoteProduccion>> = api.lotes().map { p -> p.data.map { it.toDomain() } }

    /** [unidades]: id de producto -> unidades producidas. Laravel asigna la leche y descuenta insumos. */
    suspend fun registrarProduccion(fecha: String, unidades: Map<String, Double>, observacion: String?): ApiResult<List<LoteProduccion>> {
        val fechaApi = fechaPantallaAApi(fecha) ?: return ERROR_FECHA
        val items = unidades.filterValues { it > 0 }.mapNotNull { (id, cant) -> idNumerico(id)?.let { ItemProduccionDto(it, cant) } }
        if (items.isEmpty()) return ApiResult.ErrorValidacion("Indica al menos una cantidad a producir.")
        return api.registrarProduccion(RegistrarProduccionRequestDto(fechaApi, notas = observacion?.ifBlank { null }, items = items))
            .map { l -> l.map { it.toDomain() } }
    }

    suspend fun ventas(): ApiResult<List<VentaSalida>> = api.ventas().map { p -> p.data.flatMap { it.toDomain() } }

    suspend fun registrarVenta(
        lote: LoteProduccion,
        cliente: String,
        cantidad: Double,
        precio: Double,
        fecha: String,
        observacion: String?
    ): ApiResult<List<VentaSalida>> {
        val idProducto = lote.idProducto?.let(::idNumerico) ?: return ERROR_ID_INVALIDO
        val fechaApi = fechaPantallaAApi(fecha) ?: return ERROR_FECHA
        return api.registrarVenta(
            RegistrarVentaRequestDto(
                cliente = cliente.trim(),
                fecha = fechaApi,
                notes = observacion?.ifBlank { null },
                items = listOf(ItemVentaRequestDto(idProducto, idNumerico(lote.id), cantidad, precio))
            )
        ).map { it.toDomain() }
    }
}
