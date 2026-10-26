package com.example.acopiodeleche.data.remote.dto

import com.example.acopiodeleche.domain.model.InsumoPlanta
import com.example.acopiodeleche.domain.model.LoteProduccion
import com.example.acopiodeleche.domain.model.ProductoPlanta
import com.example.acopiodeleche.domain.model.Receta
import com.example.acopiodeleche.domain.model.RecetaDetalle
import com.example.acopiodeleche.domain.model.TipoProducto
import com.example.acopiodeleche.domain.model.VentaSalida
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Categoría de products en Laravel -> tipo de producto de la app. */
fun tipoProductoDesdeApi(categoria: String?): TipoProducto = when (categoria) {
    "queso" -> TipoProducto.QUESO
    "yogur" -> TipoProducto.YOGUR
    "mantequilla" -> TipoProducto.MANTEQUILLA
    else -> TipoProducto.OTRO
}

@Serializable
data class ProductoDto(
    val id: Int,
    val name: String,
    val category: String? = null,
    val unit: String? = null,
    @SerialName("unit_price") val precio: Double = 0.0,
    val emoji: String? = null,
    val stock: Double = 0.0
)

fun ProductoDto.toDomain() = ProductoPlanta(
    id = id.toString(), nombre = name, tipo = tipoProductoDesdeApi(category),
    unidad = unit.orEmpty(), precio = precio, stock = stock
)

@Serializable
data class InsumoDto(
    val id: Int,
    val name: String,
    val unit: String,
    @SerialName("is_milk") val esLeche: Boolean = false,
    val stock: Double = 0.0
)

fun InsumoDto.toDomain() = InsumoPlanta(id.toString(), name, unit, esLeche, stock)

@Serializable
data class InsumoRecetaDto(
    @SerialName("ingredient_id") val idInsumo: Int,
    val name: String? = null,
    val unit: String? = null,
    @SerialName("quantity_per_unit") val cantidad: Double
)

@Serializable
data class RecetaDto(
    val id: Int,
    val name: String,
    @SerialName("product_id") val idProducto: Int,
    @SerialName("product_name") val nombreProducto: String? = null,
    @SerialName("product_category") val categoria: String? = null,
    val instructions: String? = null,
    val active: Boolean = true,
    val ingredients: List<InsumoRecetaDto> = emptyList()
)

fun RecetaDto.toDomain(): Pair<Receta, List<RecetaDetalle>> {
    val receta = Receta(
        id = id.toString(),
        nombre = name,
        producto = tipoProductoDesdeApi(categoria),
        descripcion = instructions,
        activa = active,
        idProducto = idProducto.toString(),
        nombreProducto = nombreProducto
    )
    val detalles = ingredients.mapIndexed { i, ins ->
        RecetaDetalle("$id-$i", id.toString(), ins.name.orEmpty(), ins.cantidad, ins.unit.orEmpty())
    }
    return receta to detalles
}

@Serializable
data class RegistrarRecetaRequestDto(
    @SerialName("product_id") val idProducto: Int,
    val name: String,
    val instructions: String? = null,
    val ingredients: List<InsumoRecetaDto>
)

@Serializable
data class LoteDto(
    val id: Int,
    @SerialName("batch_number") val codigo: String,
    @SerialName("product_id") val idProducto: Int,
    @SerialName("product_name") val nombreProducto: String? = null,
    @SerialName("product_category") val categoria: String? = null,
    val unit: String? = null,
    @SerialName("input_milk_liters") val litros: Double = 0.0,
    @SerialName("output_units") val unidades: Double = 0.0,
    val stock: Double = 0.0,
    @SerialName("production_date") val fecha: String? = null,
    @SerialName("expiration_date") val vencimiento: String? = null,
    val status: String = "terminado",
    @SerialName("recipe_notes") val notas: String? = null
)

fun LoteDto.toDomain() = LoteProduccion(
    id = id.toString(),
    codigoLote = codigo,
    producto = tipoProductoDesdeApi(categoria),
    litrosUsados = litros,
    cantidadProducida = unidades,
    unidadMedida = unit.orEmpty(),
    fecha = fecha?.let(::fechaApiAPantalla).orEmpty(),
    idTrabajador = "",
    observacion = notas,
    fechaVencimiento = vencimiento?.let(::fechaApiAPantalla),
    stock = stock,
    idProducto = idProducto.toString(),
    nombreProducto = nombreProducto
)

@Serializable
data class ItemProduccionDto(
    @SerialName("product_id") val idProducto: Int,
    @SerialName("output_units") val unidades: Double
)

/** Carrito de producción: Laravel toma la leche de las entregas disponibles y descuenta los insumos. */
@Serializable
data class RegistrarProduccionRequestDto(
    @SerialName("production_date") val fecha: String,
    val status: String = "terminado",
    @SerialName("recipe_notes") val notas: String? = null,
    val items: List<ItemProduccionDto>
)

@Serializable
data class ItemVentaDto(
    @SerialName("product_id") val idProducto: Int,
    @SerialName("product_name") val nombreProducto: String? = null,
    @SerialName("product_category") val categoria: String? = null,
    val unit: String? = null,
    @SerialName("production_batch_id") val idLote: Int? = null,
    val quantity: Double,
    @SerialName("unit_price") val precio: Double
)

@Serializable
data class VentaDto(
    val id: Int,
    @SerialName("invoice_number") val factura: String,
    @SerialName("client_name") val cliente: String,
    @SerialName("sale_date") val fecha: String? = null,
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    @SerialName("total_amount") val total: Double = 0.0,
    @SerialName("served_by") val atendidoPor: Int? = null,
    val notes: String? = null,
    val items: List<ItemVentaDto> = emptyList()
)

/** Cada ítem de la venta se muestra como una salida; el total con IGV queda en la venta. */
fun VentaDto.toDomain(): List<VentaSalida> = items.mapIndexed { i, item ->
    VentaSalida(
        id = "$id-$i",
        idLote = item.idLote?.toString().orEmpty(),
        producto = tipoProductoDesdeApi(item.categoria),
        cliente = cliente,
        cantidad = item.quantity,
        unidadMedida = item.unit.orEmpty(),
        precioPorUnidad = item.precio,
        fecha = fecha?.let(::fechaApiAPantalla).orEmpty(),
        idTrabajador = atendidoPor?.toString().orEmpty(),
        observacion = notes,
        codigoFactura = factura,
        nombreProducto = item.nombreProducto
    )
}

@Serializable
data class ItemVentaRequestDto(
    @SerialName("product_id") val idProducto: Int,
    @SerialName("batch_id") val idLote: Int? = null,
    val quantity: Double,
    @SerialName("unit_price") val precio: Double
)

/** El formulario móvil no pide estado de pago ni tipo de venta: se registra como despacho en mostrador pendiente de cobro. */
@Serializable
data class RegistrarVentaRequestDto(
    @SerialName("client_name") val cliente: String,
    @SerialName("sale_date") val fecha: String,
    @SerialName("payment_status") val estadoPago: String = "pendiente",
    @SerialName("payment_method") val metodoPago: String = "efectivo",
    @SerialName("sale_type") val tipoVenta: String = "mostrador",
    val notes: String? = null,
    val items: List<ItemVentaRequestDto>
)
