package com.example.acopiodeleche.data.remote.dto

import com.example.acopiodeleche.domain.model.Productor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductorDto(
    val id: Int,
    val code: String,
    val name: String? = null,
    val lastname: String? = null,
    val dni: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
    @SerialName("farm_name") val nombreFinca: String? = null,
    val comunidad: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val status: String = "activo"
)

/** Datos requeridos por StoreProducerRequest de Laravel (crea usuario + ficha de productor). */
@Serializable
data class RegistrarProductorRequestDto(
    val name: String,
    val lastname: String,
    val dni: String,
    val email: String,
    val phone: String,
    val address: String? = null,
    val comunidad: String,
    val password: String,
    @SerialName("password_confirmation") val passwordConfirmation: String
)

@Serializable
data class CambiarEstadoProductorRequestDto(val status: String)

fun ProductorDto.toDomain(): Productor = Productor(
    idProductor = id.toString(),
    nombres = name.orEmpty(),
    apellidos = lastname.orEmpty(),
    dni = dni.orEmpty(),
    telefono = phone.orEmpty(),
    direccion = address.orEmpty(),
    comunidad = comunidad.orEmpty(),
    estado = status == "activo"
)
