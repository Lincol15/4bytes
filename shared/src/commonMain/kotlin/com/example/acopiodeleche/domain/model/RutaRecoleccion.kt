package com.example.acopiodeleche.domain.model

/**
 * Ruta de recolección asignada a un acopiador, con sus paradas ordenadas.
 * Se obtiene del backend (collection_routes / route_stops).
 */
data class RutaRecoleccion(
    val id: String,
    val codigo: String,
    val nombre: String,
    val descripcion: String?,
    val dia: String?,
    val horaInicio: String?,
    val vehiculo: String?,
    val estado: String,             // planeada, en_curso, completada, cancelada
    val paradas: List<ParadaRuta>
) {
    val paradasVisitadas: Int
        get() = paradas.count { it.estado == "visitado" }
}

data class ParadaRuta(
    val id: String,
    val orden: Int,
    val productor: Productor?,
    val litrosEstimados: Double,
    val instrucciones: String?,
    val estado: String              // pendiente, en_camino, visitado, ausente, rechazado
) {
    val estadoTexto: String
        get() = ESTADOS_PARADA[estado] ?: estado

    companion object {
        val ESTADOS_PARADA = linkedMapOf(
            "pendiente" to "Pendiente",
            "en_camino" to "En camino",
            "visitado" to "Visitado",
            "ausente" to "Ausente",
            "rechazado" to "Rechazado"
        )
    }
}
