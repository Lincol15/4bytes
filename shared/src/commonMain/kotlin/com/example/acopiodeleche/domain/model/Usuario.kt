package com.example.acopiodeleche.domain.model

/**
 * Entidad Usuario del sistema.
 *
 * Representa a cualquier persona con acceso al sistema.
 * Se relaciona con Productor via [idProductor] cuando el rol es PRODUCTOR.
 */
data class Usuario(
    val id: String,
    val nombres: String,
    val apellidos: String,
    val dni: String,
    val telefono: String,
    val correo: String,
    val contrasena: String,         // en producción se almacenará hasheada
    val rol: Rol,
    val estado: Boolean = true,     // true = activo, false = desactivado
    val idProductor: String? = null // referencia a Productor si rol == PRODUCTOR
) {
    val nombreCompleto: String
        get() = "$nombres $apellidos"

    val estadoTexto: String
        get() = if (estado) "Activo" else "Inactivo"

    val esValido: Boolean
        get() = nombres.isNotBlank()
                && apellidos.isNotBlank()
                && correo.isNotBlank()
                && contrasena.isNotBlank()
}
