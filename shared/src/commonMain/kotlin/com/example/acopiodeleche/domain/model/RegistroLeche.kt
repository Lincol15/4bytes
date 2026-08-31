package com.example.acopiodeleche.domain.model

/**
 * Entidad RegistroLeche del dominio.
 *
 * Es una data class porque su único propósito es transportar datos.
 * Kotlin genera automáticamente equals(), hashCode(), toString() y copy().
 */
data class RegistroLeche(
    val id: String,
    val proveedor: String,
    val litros: Double,
    val categoria: Categoria,
    val observacion: String? = null,   // puede faltar; valor por defecto = null
    val activo: Boolean = true         // valor por defecto razonable
) {
    /** Operador Elvis: si observacion es null, usa el texto por defecto. */
    val observacionCorta: String
        get() = observacion ?: "Sin observación"

    /** Regla de negocio: el registro es válido si está activo y tiene litros positivos. */
    val esValido: Boolean
        get() = activo && litros > 0

    /** Texto listo para mostrar en la futura interfaz. */
    val litrosFormateados: String
        get() = "$litros L"
}


