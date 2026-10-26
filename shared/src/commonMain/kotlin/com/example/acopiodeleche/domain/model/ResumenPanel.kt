package com.example.acopiodeleche.domain.model

/** Indicadores del panel de gestión, calculados por el backend (mismos que la web). */
data class ResumenPanel(
    val usuarios: Int,
    val productoresActivos: Int,
    val litrosHoy: Double,
    val litrosMes: Double,
    val entregasHoy: Int,
    val entregasRecibidasHoy: Int,
    val entregasEnTransitoHoy: Int,
    val lotesActivos: Int,
    val ventasHoy: Double,
    val ventasMes: Double,
    val quejasAbiertas: Int,
    val precioLitro: Double
)

/** Ajuste editable de la planta (tabla plant_configs). */
data class AjustePlanta(
    val grupo: String,
    val clave: String,
    val etiqueta: String,
    val tipo: String,              // string, text, number, select
    val opciones: List<String>,
    val valor: String
)
