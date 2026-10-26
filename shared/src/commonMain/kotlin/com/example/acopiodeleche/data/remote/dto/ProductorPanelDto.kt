package com.example.acopiodeleche.data.remote.dto

import com.example.acopiodeleche.domain.model.ControlCalidad
import com.example.acopiodeleche.domain.model.EstadoPago
import com.example.acopiodeleche.domain.model.EstadoQueja
import com.example.acopiodeleche.domain.model.Notificacion
import com.example.acopiodeleche.domain.model.OrigenDatos
import com.example.acopiodeleche.domain.model.Pago
import com.example.acopiodeleche.domain.model.PagoAcopiador
import com.example.acopiodeleche.domain.model.VistaPreviaPago
import com.example.acopiodeleche.domain.model.Queja
import com.example.acopiodeleche.domain.model.ResultadoCalidad
import com.example.acopiodeleche.domain.model.TipoNotificacion
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonPrimitive

/** "2026-10-12T09:03:53-05:00" -> "09:03". */
internal fun horaDesdeIso(fechaHora: String?): String =
    fechaHora?.takeIf { it.length >= 16 }?.substring(11, 16).orEmpty()

internal fun fechaDesdeIso(fechaHora: String?): String =
    fechaHora?.let { fechaApiAPantalla(it) }.orEmpty()

// ── Calidad (quality_reports) ───────────────────────────────────────────────

@Serializable
data class AnalisisCalidadDto(
    val id: Int,
    @SerialName("sample_code") val codigoMuestra: String? = null,
    @SerialName("milk_delivery_id") val idEntrega: Int? = null,
    @SerialName("producer_id") val idProductor: Int? = null,
    @SerialName("producer_name") val nombreProductor: String? = null,
    @SerialName("analyst_id") val idAnalista: Int? = null,
    @SerialName("origen_datos") val origenDatos: String? = null,
    val temperatura: Double? = null,
    @SerialName("grasa_pct") val grasa: Double? = null,
    @SerialName("proteina_pct") val proteina: Double? = null,
    @SerialName("lactosa_pct") val lactosa: Double? = null,
    @SerialName("solidos_no_grasos_pct") val sng: Double? = null,
    @SerialName("total_solidos_pct") val totalSolidos: Double? = null,
    @SerialName("agua_aniadida_pct") val aguaAnadida: Double? = null,
    val ph: Double? = null,
    @SerialName("punto_congelacion") val puntoCongelacion: Double? = null,
    val densidad: Double? = null,
    val observations: String? = null,
    val result: String = "pendiente",
    @SerialName("analyzed_at") val analizadoEn: String? = null
)

/** Sin "result": Laravel evalúa los parámetros con su regla de negocio (QualityReportService::autoEvaluate). */
@Serializable
data class RegistrarAnalisisRequestDto(
    @SerialName("producer_id") val idProductor: Int? = null,
    @SerialName("milk_delivery_id") val idEntrega: Int? = null,
    @SerialName("origen_datos") val origenDatos: String,
    val temperatura: Double? = null,
    @SerialName("grasa_pct") val grasa: Double? = null,
    @SerialName("proteina_pct") val proteina: Double? = null,
    @SerialName("lactosa_pct") val lactosa: Double? = null,
    @SerialName("solidos_no_grasos_pct") val sng: Double? = null,
    @SerialName("total_solidos_pct") val totalSolidos: Double? = null,
    @SerialName("agua_aniadida_pct") val aguaAnadida: Double? = null,
    val ph: Double? = null,
    @SerialName("punto_congelacion") val puntoCongelacion: Double? = null,
    val densidad: Double? = null,
    val observations: String? = null
)

fun resultadoDesdeApi(result: String): ResultadoCalidad = when (result) {
    "aprobado", "aceptable" -> ResultadoCalidad.APTO
    "rechazado" -> ResultadoCalidad.NO_APTO
    "observado" -> ResultadoCalidad.OBSERVADO
    else -> ResultadoCalidad.PENDIENTE
}

fun OrigenDatos.aApi(): String = when (this) {
    OrigenDatos.MANUAL -> "manual"
    OrigenDatos.OCR -> "ocr"
    OrigenDatos.OCR_CORREGIDO -> "ocr_corregido"
}

fun AnalisisCalidadDto.toDomain(): ControlCalidad = ControlCalidad(
    id = id.toString(),
    codigoAnalisis = codigoMuestra ?: "#$id",
    idProductor = idProductor?.toString().orEmpty(),
    idRegistroAcopio = idEntrega?.toString(),
    fecha = fechaDesdeIso(analizadoEn),
    hora = horaDesdeIso(analizadoEn),
    temperatura = temperatura,
    grasa = grasa,
    sng = sng,
    densidad = densidad,
    proteina = proteina,
    lactosa = lactosa,
    totalSolidos = totalSolidos,
    aguaAnadida = aguaAnadida,
    puntoCongel = puntoCongelacion,
    ph = ph,
    resultado = resultadoDesdeApi(result),
    observacion = observations,
    origenDatos = when (origenDatos) {
        "ocr" -> OrigenDatos.OCR
        "ocr_corregido" -> OrigenDatos.OCR_CORREGIDO
        else -> OrigenDatos.MANUAL
    },
    idUsuarioAnalisis = idAnalista?.toString().orEmpty(),
    nombreProductor = nombreProductor
)

// ── Pagos (payments) ────────────────────────────────────────────────────────

@Serializable
data class PagoDto(
    val id: Int,
    @SerialName("producer_id") val idProductor: Int,
    @SerialName("producer_name") val nombreProductor: String? = null,
    @SerialName("period_code") val codigoPeriodo: String,
    @SerialName("period_start") val inicio: String,
    @SerialName("period_end") val fin: String,
    @SerialName("payment_date") val fechaPago: String? = null,
    @SerialName("total_liters") val litros: Double,
    @SerialName("price_per_liter") val precioPorLitro: Double,
    @SerialName("detalle_diario") val detalleDiario: JsonElement? = null,
    val deductions: Double = 0.0,
    @SerialName("llevado_a_planta") val llevadoAPlanta: Double = 0.0,
    @SerialName("total_amount") val total: Double,
    val status: String
)

fun PagoDto.toDomain(): Pago = Pago(
    id = id.toString(),
    idProductor = idProductor.toString(),
    codigoPago = codigoPeriodo,
    periodoDesde = fechaApiAPantalla(inicio),
    periodoHasta = fechaApiAPantalla(fin),
    precioPorLitro = precioPorLitro,
    litrosTotales = litros,
    detalleDiario = (detalleDiario as? JsonObject).orEmpty()
        .mapNotNull { (dia, valor) -> runCatching { valor.jsonPrimitive.doubleOrNull }.getOrNull()?.let { dia to it } }
        .toMap(),
    descuento = deductions,
    llevadoAPlanta = llevadoAPlanta,
    estado = when (status) {
        "pagado" -> EstadoPago.PAGADO
        "parcial" -> EstadoPago.PARCIAL
        else -> EstadoPago.PENDIENTE
    },
    fechaPago = fechaPago?.let(::fechaApiAPantalla),
    totalServidor = total,
    nombreProductor = nombreProductor
)

fun estadoPagoDesdeApi(status: String): EstadoPago = when (status) {
    "pagado" -> EstadoPago.PAGADO
    "parcial" -> EstadoPago.PARCIAL
    else -> EstadoPago.PENDIENTE
}

@Serializable
data class VistaPreviaPagoDto(
    @SerialName("producer_id") val idProductor: Int,
    @SerialName("producer_name") val nombreProductor: String? = null,
    val comunidad: String? = null,
    @SerialName("can_generate") val puedeGenerar: Boolean,
    @SerialName("total_liters") val litros: Double = 0.0,
    @SerialName("price_per_liter") val precioPorLitro: Double = 0.0,
    @SerialName("total_amount") val total: Double = 0.0,
    @SerialName("already_paid_period") val yaGenerado: Boolean = false
)

fun VistaPreviaPagoDto.toDomain() = VistaPreviaPago(
    idProductor.toString(), nombreProductor.orEmpty(), comunidad.orEmpty(), litros, precioPorLitro, total, puedeGenerar, yaGenerado
)

@Serializable
data class GenerarPagosRequestDto(
    @SerialName("period_start") val inicio: String,
    @SerialName("period_end") val fin: String,
    @SerialName("producer_ids") val idsProductores: List<Int>
)

@Serializable
data class GenerarPlanillaRequestDto(val month: String)

@Serializable
data class ResultadoGeneracionDto(val generated: Int = 0, @SerialName("skipped_without_salary") val sinSueldo: Int = 0)

@Serializable
data class MarcarPagadoRequestDto(@SerialName("payment_method") val metodo: String = "efectivo")

@Serializable
data class PagoAcopiadorDto(
    val id: Int,
    @SerialName("collector_name") val nombreAcopiador: String? = null,
    @SerialName("period_code") val periodo: String,
    @SerialName("base_salary") val sueldoBase: Double,
    @SerialName("total_amount") val total: Double,
    @SerialName("liters_collected") val litros: Double = 0.0,
    @SerialName("deliveries_count") val entregas: Int = 0,
    val status: String,
    @SerialName("payment_date") val fechaPago: String? = null
)

fun PagoAcopiadorDto.toDomain() = PagoAcopiador(
    id.toString(), nombreAcopiador.orEmpty(), periodo, sueldoBase, total, litros, entregas,
    estadoPagoDesdeApi(status), fechaPago?.let(::fechaApiAPantalla)
)

// ── Avisos (notifications) ──────────────────────────────────────────────────

@Serializable
data class NotificacionDto(
    val id: Int,
    val title: String,
    val message: String,
    val priority: String = "normal",
    @SerialName("published_at") val publicadoEn: String? = null,
    val read: Boolean = false
)

fun NotificacionDto.toDomain(): Notificacion = Notificacion(
    id = id.toString(),
    titulo = title,
    mensaje = message,
    tipo = TipoNotificacion.AVISO_GENERAL,
    fecha = fechaDesdeIso(publicadoEn),
    hora = horaDesdeIso(publicadoEn),
    leida = read
)

// ── Quejas (complaints) ─────────────────────────────────────────────────────

@Serializable
data class QuejaDto(
    val id: Int,
    @SerialName("ticket_number") val ticket: String,
    @SerialName("producer_id") val idProductor: Int? = null,
    @SerialName("producer_name") val nombreProductor: String? = null,
    val subject: String,
    val description: String,
    val status: String,
    @SerialName("staff_response") val respuesta: String? = null,
    @SerialName("created_at") val creadoEn: String? = null,
    @SerialName("responded_at") val respondidoEn: String? = null
)

@Serializable
data class RegistrarQuejaRequestDto(
    val category: String = "otro",
    val priority: String = "normal",
    val subject: String,
    val description: String
)

@Serializable
data class ResponderQuejaRequestDto(
    val status: String,
    @SerialName("staff_response") val respuesta: String? = null
)

fun EstadoQueja.aApi(): String = when (this) {
    EstadoQueja.PENDIENTE -> "abierto"
    EstadoQueja.EN_REVISION -> "en_revision"
    EstadoQueja.RESPONDIDA -> "respondido"
    EstadoQueja.CERRADA -> "cerrado"
}

fun QuejaDto.toDomain(): Queja = Queja(
    id = id.toString(),
    idProductor = idProductor?.toString().orEmpty(),
    titulo = subject,
    descripcion = description,
    fecha = fechaDesdeIso(creadoEn),
    hora = horaDesdeIso(creadoEn),
    estado = when (status) {
        "en_revision" -> EstadoQueja.EN_REVISION
        "respondido" -> EstadoQueja.RESPONDIDA
        "cerrado", "rechazado" -> EstadoQueja.CERRADA
        else -> EstadoQueja.PENDIENTE
    },
    respuesta = respuesta,
    fechaRespuesta = respondidoEn?.let(::fechaDesdeIso),
    nombreProductor = nombreProductor
)
