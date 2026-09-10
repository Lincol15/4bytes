package com.example.acopiodeleche.domain.model

/**
 * Entidad Sancion — el admin puede emitir advertencias o retiros
 * cuando un productor tiene múltiples adulteraciones.
 */
data class Sancion(
    val id: String,
    val idProductor: String,
    val tipo: TipoSancion,
    val motivo: String,
    val descripcion: String,
    val fecha: String,
    val idAdministrador: String,
    val activa: Boolean = true
)

enum class TipoSancion(val etiqueta: String, val icono: String, val color: String) {
    ADVERTENCIA("Advertencia", "⚠️", "amarillo"),
    SUSPENSION_TEMPORAL("Suspensión temporal", "🚫", "naranja"),
    RETIRO("Retiro del programa", "❌", "rojo")
}
