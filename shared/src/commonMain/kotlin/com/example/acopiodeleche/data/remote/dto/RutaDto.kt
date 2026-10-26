package com.example.acopiodeleche.data.remote.dto

import com.example.acopiodeleche.domain.model.ParadaRuta
import com.example.acopiodeleche.domain.model.RutaRecoleccion
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Ruta de recolección (tabla collection_routes) con sus paradas. */
@Serializable
data class RutaDto(
    val id: Int,
    val code: String,
    val name: String,
    val description: String? = null,
    val day: String? = null,
    @SerialName("start_time") val horaInicio: String? = null,
    @SerialName("vehicle_plate") val vehiculo: String? = null,
    val status: String,
    val stops: List<ParadaDto> = emptyList()
)

@Serializable
data class ParadaDto(
    val id: Int,
    @SerialName("stop_order") val orden: Int,
    @SerialName("estimated_liters") val litrosEstimados: Double = 0.0,
    @SerialName("special_instructions") val instrucciones: String? = null,
    val status: String,
    val producer: ProductorDto? = null
)

@Serializable
data class ActualizarParadaRequestDto(val status: String)

fun ParadaDto.toDomain(): ParadaRuta = ParadaRuta(
    id = id.toString(),
    orden = orden,
    productor = producer?.toDomain(),
    litrosEstimados = litrosEstimados,
    instrucciones = instrucciones,
    estado = status
)

fun RutaDto.toDomain(): RutaRecoleccion = RutaRecoleccion(
    id = id.toString(),
    codigo = code,
    nombre = name,
    descripcion = description,
    dia = day,
    horaInicio = horaInicio?.take(5),
    vehiculo = vehiculo,
    estado = status,
    paradas = stops.sortedBy { it.orden }.map { it.toDomain() }
)
