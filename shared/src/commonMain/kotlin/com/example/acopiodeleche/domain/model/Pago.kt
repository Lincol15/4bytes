package com.example.acopiodeleche.domain.model

/**
 * Entidad Pago — digitaliza el ticket físico de la planta.
 *
 * Ejemplo real (ticket Ecolácteos Huata):
 * Periodo: 27 ago — 02 sep
 * Precio: S/ 1.70 por litro
 * Cantidad: 89.5 L → Total: S/ 152.10
 * Detalle: Jue 24, Vie 10, Sáb 9, Dom 10, Lun 8, Mar 8.5, Mié 20
 */
data class Pago(
    val id: String,
    val idProductor: String,
    val codigoPago: String,             // ej: "H-11" (código del ticket)
    val periodoDesde: String,           // ej: "27/08/2026"
    val periodoHasta: String,           // ej: "02/09/2026"
    val precioPorLitro: Double,         // ej: 1.70
    val litrosTotales: Double,          // ej: 89.5
    val detalleDiario: Map<String, Double> = emptyMap(), // {"Jueves": 24.0, "Viernes": 10.0, ...}
    val descuento: Double = 0.0,
    val llevadoAPlanta: Double = 0.0,
    val estado: EstadoPago = EstadoPago.PENDIENTE,
    val fechaPago: String? = null
) {
    /** Total calculado automáticamente */
    val totalCalculado: Double
        get() = (litrosTotales * precioPorLitro) - descuento - llevadoAPlanta

    val totalFormateado: String
        get() = "S/ ${"%.2f".let { totalCalculado.toString() }}"

    val litrosFormateados: String
        get() = "$litrosTotales L"

    val precioPorLitroFormateado: String
        get() = "S/ $precioPorLitro"
}

enum class EstadoPago(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    PAGADO("Pagado"),
    PARCIAL("Parcial")
}
