package com.example.acopiodeleche.domain.model

/**
 * Configuración global del sistema — Ecolácteos Huata.
 * El administrador puede modificar todos estos valores desde la app.
 */
object ConfiguracionPlanta {

    // ── Información de la empresa ──────────────────────────────────────────
    var nombre:    String = "Ecolácteos Huata"
    var slogan:    String = "Disfrute lo Natural"
    var ruc:       String = "20601234567"
    var direccion: String = "Av. Principal S/N — Huata, Puno"
    var telefono:  String = "051-123456"
    var correo:    String = "info@ecolacteoshuata.com"
    var distrito:  String = "Huata"
    var provincia: String = "Puno"
    var region:    String = "Puno"

    // ── Precios ────────────────────────────────────────────────────────────
    var precioPorLitro: Double = 1.70   // precio base de compra al productor

    // ── Límites operativos ─────────────────────────────────────────────────
    var litrosMinimosEntrega: Double = 0.0
    var litrosMaximosEntrega: Double = 5000.0
    var diasPagoCiclo:        Int    = 7          // ciclo semanal por defecto

    // ── Versión del sistema ────────────────────────────────────────────────
    const val VERSION_APP  = "1.0.0"
    const val VERSION_BD   = "SQLite / SQLDelight"
    const val DESARROLLADOR = "Equipo 4bytes"

    // Mantener compatibilidad con código que use las constantes antiguas
    val NOMBRE  get() = nombre
    val SLOGAN  get() = slogan

    fun calcularTotal(litros: Double): Double = litros * precioPorLitro

    val precioPorLitroFormateado: String
        get() = "S/ $precioPorLitro"
}
