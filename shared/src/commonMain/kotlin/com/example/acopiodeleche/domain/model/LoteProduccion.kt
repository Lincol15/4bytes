package com.example.acopiodeleche.domain.model

/**
 * Entidad LoteProduccion — registra la producción de queso o yogur.
 *
 * Flujo: leche recibida → lote producido → stock disponible → venta
 */
data class LoteProduccion(
    val id: String,
    val codigoLote: String,             // ej: "Q-2026-001"
    val producto: TipoProducto,
    val litrosUsados: Double,           // litros de leche usados
    val cantidadProducida: Double,      // kg de queso o litros de yogur
    val unidadMedida: String,           // "kg", "L", "unidades"
    val fecha: String,                  // "dd/MM/yyyy"
    val idTrabajador: String,           // quien lo registró
    val observacion: String? = null,
    val fechaVencimiento: String? = null
) {
    val cantidadFormateada: String
        get() = "$cantidadProducida $unidadMedida"

    val resumen: String
        get() = "${producto.etiqueta} · $cantidadFormateada · $fecha"
}

enum class TipoProducto(val etiqueta: String, val icono: String) {
    QUESO("Queso", "🧀"),
    YOGUR("Yogur", "🥛"),
    MANTEQUILLA("Mantequilla", "🧈"),
    OTRO("Otro", "📦")
}
