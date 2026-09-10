package com.example.acopiodeleche.domain.model

/**
 * Entidad Queja — el productor puede enviar quejas al administrador.
 */
data class Queja(
    val id: String,
    val idProductor: String,
    val titulo: String,
    val descripcion: String,
    val fecha: String,
    val hora: String,
    val estado: EstadoQueja = EstadoQueja.PENDIENTE,
    val respuesta: String? = null,
    val fechaRespuesta: String? = null
)

enum class EstadoQueja(val etiqueta: String, val icono: String) {
    PENDIENTE("Pendiente", "⏳"),
    EN_REVISION("En revisión", "🔍"),
    RESPONDIDA("Respondida", "✅"),
    CERRADA("Cerrada", "🔒")
}
