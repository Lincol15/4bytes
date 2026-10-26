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
    val fechaPago: String? = null,
    val totalServidor: Double? = null,  // total liquidado por el backend (incluye bonos)
    val nombreProductor: String? = null // viene resuelto desde el backend
) {
    /** Total del backend si existe; si no, cálculo local */
    val totalCalculado: Double
        get() = totalServidor ?: ((litrosTotales * precioPorLitro) - descuento - llevadoAPlanta)

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

/** Liquidación que generaría el backend para un productor en un período (sin guardarla). */
data class VistaPreviaPago(
    val idProductor: String,
    val nombreProductor: String,
    val comunidad: String,
    val litros: Double,
    val precioPorLitro: Double,
    val total: Double,
    val puedeGenerar: Boolean,
    val yaGenerado: Boolean
)

/** Sueldo mensual de un acopiador (planilla generada por el backend). */
data class PagoAcopiador(
    val id: String,
    val nombreAcopiador: String,
    val periodo: String,
    val sueldoBase: Double,
    val total: Double,
    val litrosRecolectados: Double,
    val entregas: Int,
    val estado: EstadoPago,
    val fechaPago: String?
)
