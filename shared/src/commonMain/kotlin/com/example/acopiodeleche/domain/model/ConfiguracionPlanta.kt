package com.example.acopiodeleche.domain.model

/**
 * Configuración global de la planta — Ecolácteos Huata.
 * El administrador puede modificar estos valores desde la app.
 */
object ConfiguracionPlanta {
    const val NOMBRE = "Ecolácteos Huata"
    const val SLOGAN = "Disfrute lo Natural"

    // Precio por litro configurable por el administrador
    var precioPorLitro: Double = 1.70

    // Rango de litros válidos por entrega
    const val LITROS_MINIMOS = 0.0
    const val LITROS_MAXIMOS = 5000.0

    /** Calcula el total a pagar dado los litros entregados */
    fun calcularTotal(litros: Double): Double =
        litros * precioPorLitro

    /** Texto formateado del precio */
    val precioPorLitroFormateado: String
        get() = "S/ $precioPorLitro"
}
