package com.example.acopiodeleche.data.remote.dto

import com.example.acopiodeleche.domain.model.ResultadoCalidad
import com.example.acopiodeleche.domain.model.Sancion
import com.example.acopiodeleche.domain.model.TipoSancion
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EvaluarAnalisisRequestDto(
    val result: String,
    @SerialName("rejection_reason") val motivoRechazo: String? = null
)

fun ResultadoCalidad.aApi(): String = when (this) {
    ResultadoCalidad.APTO -> "aprobado"
    ResultadoCalidad.NO_APTO -> "rechazado"
    ResultadoCalidad.OBSERVADO -> "observado"
    ResultadoCalidad.PENDIENTE -> "pendiente"
}

/** Motivos de rechazo que acepta Laravel (quality_reports.rejection_reason). */
val MOTIVOS_RECHAZO = linkedMapOf(
    "baja_grasa" to "Baja grasa",
    "baja_proteina" to "Baja proteína",
    "exceso_agua" to "Exceso de agua",
    "acidez_alta" to "Acidez alta",
    "ph_anormal" to "pH anormal",
    "congelacion_anormal" to "Congelación anormal",
    "contaminacion" to "Contaminación"
)

@Serializable
data class SancionDto(
    val id: Int,
    @SerialName("producer_id") val idProductor: Int,
    @SerialName("producer_name") val nombreProductor: String? = null,
    @SerialName("issued_by") val emitidaPor: Int? = null,
    val type: String,
    val motivo: String,
    val description: String? = null,
    val status: String,
    @SerialName("sanction_date") val fecha: String? = null
)

/**
 * Datos de StoreSanctionRequest. El backend clasifica por categoría (calidad, fraude…) y no por
 * gravedad: las sanciones de esta pantalla son de "calidad" y la gravedad va al inicio del motivo.
 */
@Serializable
data class RegistrarSancionRequestDto(
    @SerialName("producer_id") val idProductor: Int,
    val type: String = "calidad",
    val motivo: String,
    val description: String? = null,
    @SerialName("sanction_date") val fecha: String
)

private const val SEPARADOR_TIPO = ": "

fun motivoConTipo(tipo: TipoSancion, motivo: String): String = "${tipo.etiqueta}$SEPARADOR_TIPO${motivo.trim()}"

fun SancionDto.toDomain(): Sancion {
    val tipo = TipoSancion.entries.firstOrNull { motivo.startsWith(it.etiqueta + SEPARADOR_TIPO) }
    return Sancion(
        id = id.toString(),
        idProductor = idProductor.toString(),
        tipo = tipo ?: TipoSancion.ADVERTENCIA,
        motivo = tipo?.let { motivo.removePrefix(it.etiqueta + SEPARADOR_TIPO) } ?: motivo,
        descripcion = description.orEmpty(),
        fecha = fecha?.let(::fechaApiAPantalla).orEmpty(),
        idAdministrador = emitidaPor?.toString().orEmpty(),
        activa = status == "activa",
        nombreProductor = nombreProductor
    )
}

/** "12/10/2026" -> "2026-10-12". */
fun fechaPantallaAApi(fecha: String): String? {
    val partes = fecha.trim().split("/")
    return if (partes.size == 3) "${partes[2]}-${partes[1].padStart(2, '0')}-${partes[0].padStart(2, '0')}" else null
}
