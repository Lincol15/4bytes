package com.example.acopiodeleche.ui.pantallas.acopiador

/**
 * Punto de ruta del acopiador.
 * Puede ser el origen (planta), destino (productor) o parada.
 */
data class PuntoRuta(
    val id: String,
    val nombre: String,
    val tipo: TipoPunto,
    val latitud: Double,
    val longitud: Double
)

enum class TipoPunto(val etiqueta: String, val icono: String) {
    ORIGEN("Punto de inicio", "🏭"),
    DESTINO("Punto final", "🏁"),
    PRODUCTOR("Productor", "🌾"),
    PARADA("Parada", "📍")
}

/**
 * Ruta de acopio con todos los puntos ordenados que el acopiador debe visitar.
 */
data class RutaAcopio(
    val nombre: String,
    val descripcion: String,
    val puntos: List<PuntoRuta>
)
