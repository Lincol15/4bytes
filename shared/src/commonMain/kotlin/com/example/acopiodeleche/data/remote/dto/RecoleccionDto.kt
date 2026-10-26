package com.example.acopiodeleche.data.remote.dto

import com.example.acopiodeleche.domain.model.RegistroAcopio
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Entrega/recolección de leche (tabla milk_deliveries). */
@Serializable
data class RecoleccionDto(
    val id: Int,
    @SerialName("producer_id") val idProductor: Int,
    @SerialName("producer_name") val nombreProductor: String? = null,
    @SerialName("collector_id") val idAcopiador: Int? = null,
    @SerialName("collector_name") val nombreAcopiador: String? = null,
    @SerialName("route_stop_id") val idParada: Int? = null,
    val zona: String? = null,
    val liters: Double,
    @SerialName("total_amount") val montoTotal: Double = 0.0,
    @SerialName("delivery_date") val fechaEntrega: String,
    @SerialName("delivery_time") val horaEntrega: String? = null,
    @SerialName("vehicle_plate") val vehiculo: String? = null,
    val observations: String? = null,
    val status: String = "registrado",
    val recibido: Boolean = false
)

/**
 * Registro desde el acopiador. Fecha, hora, precio, vehículo y zona los asigna Laravel
 * (StoreMilkDeliveryRequest + MilkDeliveryService), no la app.
 */
@Serializable
data class RegistrarRecoleccionRequestDto(
    @SerialName("producer_id") val idProductor: Int,
    val liters: Double,
    val observations: String? = null,
    @SerialName("route_stop_id") val idParada: Int? = null,
    @SerialName("collection_route_id") val idRuta: Int? = null
)

@Serializable
data class ActualizarRecoleccionRequestDto(
    val liters: Double? = null,
    val observations: String? = null
)

/** "2026-10-12" -> "12/10/2026" (formato que ya usan las pantallas). */
internal fun fechaApiAPantalla(fecha: String): String {
    val partes = fecha.take(10).split("-")
    return if (partes.size == 3) "${partes[2]}/${partes[1]}/${partes[0]}" else fecha
}

fun RecoleccionDto.toDomain(): RegistroAcopio = RegistroAcopio(
    id = id.toString(),
    idProductor = idProductor.toString(),
    acopiador = nombreAcopiador.orEmpty(),
    vehiculo = vehiculo.orEmpty(),
    zona = zona.orEmpty(),
    fecha = fechaApiAPantalla(fechaEntrega),
    hora = horaEntrega.orEmpty(),
    litros = liters,
    observacion = observations,
    recibido = recibido,
    nombreProductor = nombreProductor,
    estado = status,
    idAcopiador = idAcopiador?.toString()
)
