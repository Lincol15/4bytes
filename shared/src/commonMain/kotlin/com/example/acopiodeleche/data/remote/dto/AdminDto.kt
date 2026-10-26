package com.example.acopiodeleche.data.remote.dto

import com.example.acopiodeleche.domain.model.AjustePlanta
import com.example.acopiodeleche.domain.model.AsignacionAcopiador
import com.example.acopiodeleche.domain.model.ResumenPanel
import com.example.acopiodeleche.domain.model.Rol
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResumenPanelDto(
    @SerialName("users_count") val usuarios: Double = 0.0,
    @SerialName("producers_active") val productoresActivos: Double = 0.0,
    @SerialName("liters_today") val litrosHoy: Double = 0.0,
    @SerialName("liters_month") val litrosMes: Double = 0.0,
    @SerialName("deliveries_count") val entregasHoy: Double = 0.0,
    @SerialName("deliveries_received") val recibidasHoy: Double = 0.0,
    @SerialName("deliveries_in_transit") val enTransitoHoy: Double = 0.0,
    @SerialName("batches_active") val lotesActivos: Double = 0.0,
    @SerialName("sales_today") val ventasHoy: Double = 0.0,
    @SerialName("sales_month") val ventasMes: Double = 0.0,
    @SerialName("complaints_open") val quejasAbiertas: Double = 0.0,
    @SerialName("milk_price") val precioLitro: Double = 0.0
)

fun ResumenPanelDto.toDomain() = ResumenPanel(
    usuarios = usuarios.toInt(),
    productoresActivos = productoresActivos.toInt(),
    litrosHoy = litrosHoy,
    litrosMes = litrosMes,
    entregasHoy = entregasHoy.toInt(),
    entregasRecibidasHoy = recibidasHoy.toInt(),
    entregasEnTransitoHoy = enTransitoHoy.toInt(),
    lotesActivos = lotesActivos.toInt(),
    ventasHoy = ventasHoy,
    ventasMes = ventasMes,
    quejasAbiertas = quejasAbiertas.toInt(),
    precioLitro = precioLitro
)

/** Datos de StoreUserRequest de Laravel. */
@Serializable
data class CrearUsuarioRequestDto(
    val name: String,
    val lastname: String,
    val dni: String,
    val email: String,
    val phone: String? = null,
    val role: String,
    val comunidad: String? = null,
    val vehiculo: String? = null,
    val password: String,
    @SerialName("password_confirmation") val passwordConfirmation: String
)

@Serializable
data class EstadoUsuarioRequestDto(val active: Boolean)

fun Rol.aApi(): String = when (this) {
    Rol.ADMINISTRADOR -> "admin"
    Rol.GERENTE -> "gerente"
    Rol.ACOPIADOR -> "acopiador"
    Rol.PRODUCTOR -> "productor"
    Rol.CONTROL_CALIDAD -> "control_calidad"
    Rol.TRABAJADOR_PLANTA -> "trabajador_planta"
}

@Serializable
data class AsignacionDto(
    @SerialName("collector_id") val idAcopiador: Int,
    val comunidad: String? = null,
    val vehiculo: String? = null,
    val individual: Boolean = false,
    @SerialName("producer_ids") val idsProductores: List<Int> = emptyList()
)

fun AsignacionDto.toDomain() = AsignacionAcopiador(
    idAcopiador = idAcopiador.toString(),
    idsProductores = idsProductores.map { it.toString() }
)

@Serializable
data class ActualizarAsignacionRequestDto(
    val comunidad: String? = null,
    @SerialName("producer_ids") val idsProductores: List<Int>
)

@Serializable
data class VehiculoRequestDto(val vehiculo: String?)

@Serializable
data class PublicarAvisoRequestDto(
    val target: String,
    val title: String,
    val message: String,
    val priority: String = "normal",
    @SerialName("published_at") val publicadoEn: String? = null
)

// ── Configuración (plant_configs) ───────────────────────────────────────────

@Serializable
data class AjusteDto(
    val label: String,
    val type: String,
    val options: List<String>? = null,
    val value: String? = null
)

@Serializable
data class GrupoAjustesDto(
    val title: String,
    val settings: Map<String, AjusteDto>
)

@Serializable
data class GuardarGrupoRequestDto(val settings: Map<String, String>)

fun Map<String, GrupoAjustesDto>.toDomain(): List<AjustePlanta> =
    flatMap { (grupo, datos) ->
        datos.settings.map { (clave, ajuste) ->
            AjustePlanta(grupo, clave, ajuste.label, ajuste.type, ajuste.options.orEmpty(), ajuste.value.orEmpty())
        }
    }
