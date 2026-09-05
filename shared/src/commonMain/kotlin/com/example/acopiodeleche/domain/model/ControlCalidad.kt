package com.example.acopiodeleche.domain.model

/**
 * Entidad ControlCalidad — registra el análisis del Analizador LACTOMAT.
 *
 * Parámetros del ticket real de la planta:
 * Grasa: 16.3%, SNG: 7.18%, Densidad: 18.2, Proteína: 2.7%,
 * Lactosa: 3.9%, Sales: 0.4%, Total sólidos: 23.4%,
 * Agua añadida: 3.5%, Punto cong: -0.501°C, pH: 11.5
 */
data class ControlCalidad(
    val id: String,
    val codigoAnalisis: String,         // ej: "LAC-2026-001"
    val idProductor: String,
    val idRegistroAcopio: String? = null,
    val fecha: String,                  // "dd/MM/yyyy"
    val hora: String,                   // "HH:mm"
    val temperatura: Double? = null,    // Temp. 14.8 C
    val grasa: Double? = null,          // 16.3%
    val sng: Double? = null,            // 7.18%
    val densidad: Double? = null,       // 18.2
    val proteina: Double? = null,       // 2.7%
    val lactosa: Double? = null,        // 3.9%
    val sales: Double? = null,          // 0.4%
    val totalSolidos: Double? = null,   // 23.4%
    val aguaAnadida: Double? = null,    // 3.5%
    val puntoCongel: Double? = null,    // -0.501 C
    val ph: Double? = null,             // 11.5
    val resultado: ResultadoCalidad = ResultadoCalidad.PENDIENTE,
    val observacion: String? = null,
    val imagenTicket: String? = null,   // ruta de la imagen guardada
    val origenDatos: OrigenDatos = OrigenDatos.MANUAL,
    val idUsuarioAnalisis: String = ""  // quién realizó el análisis
)

enum class ResultadoCalidad(val etiqueta: String, val color: String) {
    APTO("Apto", "verde"),
    NO_APTO("No apto", "rojo"),
    OBSERVADO("Observado — requiere revisión", "amarillo"),
    PENDIENTE("Pendiente de evaluación", "gris")
}

enum class OrigenDatos(val etiqueta: String) {
    MANUAL("Ingreso manual"),
    OCR("Lectura automática (OCR)"),
    OCR_CORREGIDO("OCR con corrección manual")
}
