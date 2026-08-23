package com.example.acopiodeleche.domain.model

/**
 * Entidad Productor del dominio.
 *
 * Representa a las personas que entregan leche al centro de acopio.
 * Atributos definidos según el README del proyecto.
 */
data class Productor(
    val idProductor: String,
    val nombres: String,
    val apellidos: String,
    val dni: String,
    val telefono: String,
    val direccion: String,
    val comunidad: String,
    val estado: Boolean = true          // true = activo, false = inactivo
) {
    /** Nombre completo listo para mostrar en la UI. */
    val nombreCompleto: String
        get() = "$nombres $apellidos"

    /** Estado legible para la UI. */
    val estadoTexto: String
        get() = if (estado) "Activo" else "Inactivo"

    /** Valida que los campos obligatorios no estén en blanco. */
    val esValido: Boolean
        get() = idProductor.isNotBlank()
                && nombres.isNotBlank()
                && apellidos.isNotBlank()
                && dni.isNotBlank()
}
