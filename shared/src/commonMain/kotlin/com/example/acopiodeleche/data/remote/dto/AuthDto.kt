package com.example.acopiodeleche.data.remote.dto

import com.example.acopiodeleche.domain.model.Rol
import com.example.acopiodeleche.domain.model.Usuario
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    /** Correo electrónico o DNI, igual que en el login web. */
    val login: String,
    val password: String,
    @SerialName("device_name") val dispositivo: String = "app-movil"
)

@Serializable
data class LoginResponseDto(
    val token: String,
    @SerialName("token_type") val tipoToken: String,
    val user: UsuarioDto
)

@Serializable
data class UsuarioDto(
    val id: Int,
    val name: String,
    val lastname: String? = null,
    val dni: String? = null,
    val email: String,
    val phone: String? = null,
    val role: String,
    val active: Boolean = true,
    val comunidad: String? = null,
    val vehiculo: String? = null,
    @SerialName("producer_id") val idProductor: Int? = null
)

/** Roles del backend (users.role) -> roles de la app. */
fun rolDesdeApi(role: String): Rol? = when (role) {
    "admin" -> Rol.ADMINISTRADOR
    "gerente" -> Rol.GERENTE
    "acopiador" -> Rol.ACOPIADOR
    "productor" -> Rol.PRODUCTOR
    "control_calidad" -> Rol.CONTROL_CALIDAD
    "trabajador_planta" -> Rol.TRABAJADOR_PLANTA
    else -> null
}

/** La contraseña nunca se guarda en el dispositivo: la sesión se mantiene con el token. */
fun UsuarioDto.toDomain(rol: Rol): Usuario = Usuario(
    id = id.toString(),
    nombres = name,
    apellidos = lastname.orEmpty(),
    dni = dni.orEmpty(),
    telefono = phone.orEmpty(),
    correo = email,
    contrasena = "",
    rol = rol,
    estado = active,
    idProductor = idProductor?.toString(),
    comunidad = comunidad,
    vehiculo = vehiculo
)
