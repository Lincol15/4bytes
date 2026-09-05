package com.example.acopiodeleche.domain.model

/**
 * Entidad Notificación — avisos de la planta a los productores.
 */
data class Notificacion(
    val id: String,
    val titulo: String,
    val mensaje: String,
    val tipo: TipoNotificacion,
    val fecha: String,
    val hora: String,
    val leida: Boolean = false,
    val destinatarioRol: Rol? = null,   // null = para todos
    val idDestinatario: String? = null  // null = broadcast
)

enum class TipoNotificacion(val etiqueta: String, val icono: String) {
    AVISO_GENERAL("Aviso general", "📢"),
    CAMBIO_PRECIO("Cambio de precio", "💰"),
    PAGO("Pago", "💳"),
    REUNION("Reunión", "👥"),
    SUSPENSION("Suspensión de recolección", "🚫"),
    CALIDAD("Resultado de calidad", "🔬"),
    MANTENIMIENTO("Mantenimiento", "🔧"),
    EVENTO("Evento", "📅")
}
