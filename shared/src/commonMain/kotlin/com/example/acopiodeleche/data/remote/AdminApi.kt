package com.example.acopiodeleche.data.remote

import com.example.acopiodeleche.data.remote.dto.ActualizarAsignacionRequestDto
import com.example.acopiodeleche.data.remote.dto.AsignacionDto
import com.example.acopiodeleche.data.remote.dto.CrearUsuarioRequestDto
import com.example.acopiodeleche.data.remote.dto.DataDto
import com.example.acopiodeleche.data.remote.dto.EstadoUsuarioRequestDto
import com.example.acopiodeleche.data.remote.dto.GrupoAjustesDto
import com.example.acopiodeleche.data.remote.dto.GuardarGrupoRequestDto
import com.example.acopiodeleche.data.remote.dto.NotificacionDto
import com.example.acopiodeleche.data.remote.dto.PaginaDto
import com.example.acopiodeleche.data.remote.dto.PublicarAvisoRequestDto
import com.example.acopiodeleche.data.remote.dto.RecoleccionDto
import com.example.acopiodeleche.data.remote.dto.ResumenPanelDto
import com.example.acopiodeleche.data.remote.dto.UsuarioDto
import com.example.acopiodeleche.data.remote.dto.VehiculoRequestDto
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/** Endpoints de gestión (administrador / gerente). */
class AdminApi(private val cliente: ApiClient) {

    suspend fun resumen(): ApiResult<ResumenPanelDto> =
        cliente.ejecutar<DataDto<ResumenPanelDto>> { get("dashboard") }.map { it.data }

    suspend fun usuarios(pagina: Int = 1, rol: String? = null): ApiResult<PaginaDto<UsuarioDto>> =
        cliente.ejecutar {
            get("users") {
                parameter("page", pagina)
                parameter("per_page", 100)
                rol?.let { parameter("role", it) }
            }
        }

    suspend fun crearUsuario(peticion: CrearUsuarioRequestDto): ApiResult<UsuarioDto> =
        cliente.ejecutar<DataDto<UsuarioDto>> {
            post("users") {
                contentType(ContentType.Application.Json)
                setBody(peticion)
            }
        }.map { it.data }

    suspend fun cambiarEstadoUsuario(id: Int, activo: Boolean): ApiResult<UsuarioDto> =
        cliente.ejecutar<DataDto<UsuarioDto>> {
            patch("users/$id/status") {
                contentType(ContentType.Application.Json)
                setBody(EstadoUsuarioRequestDto(activo))
            }
        }.map { it.data }

    suspend fun asignacion(idAcopiador: Int): ApiResult<AsignacionDto> =
        cliente.ejecutar<DataDto<AsignacionDto>> { get("collectors/$idAcopiador/assignment") }.map { it.data }

    suspend fun guardarAsignacion(idAcopiador: Int, peticion: ActualizarAsignacionRequestDto): ApiResult<AsignacionDto> =
        cliente.ejecutar<DataDto<AsignacionDto>> {
            put("collectors/$idAcopiador/assignment") {
                contentType(ContentType.Application.Json)
                setBody(peticion)
            }
        }.map { it.data }

    suspend fun guardarVehiculo(idAcopiador: Int, vehiculo: String?): ApiResult<AsignacionDto> =
        cliente.ejecutar<DataDto<AsignacionDto>> {
            patch("collectors/$idAcopiador/vehicle") {
                contentType(ContentType.Application.Json)
                setBody(VehiculoRequestDto(vehiculo))
            }
        }.map { it.data }

    suspend fun entregasDeAcopiador(idAcopiador: Int, desde: String, pagina: Int = 1): ApiResult<PaginaDto<RecoleccionDto>> =
        cliente.ejecutar {
            get("deliveries") {
                parameter("collector_id", idAcopiador)
                parameter("from", desde)
                parameter("page", pagina)
                parameter("per_page", 100)
            }
        }

    suspend fun avisosPublicados(): ApiResult<PaginaDto<NotificacionDto>> =
        cliente.ejecutar { get("notifications") { parameter("scope", "all") } }

    suspend fun publicarAviso(peticion: PublicarAvisoRequestDto): ApiResult<NotificacionDto> =
        cliente.ejecutar<DataDto<NotificacionDto>> {
            post("notifications") {
                contentType(ContentType.Application.Json)
                setBody(peticion)
            }
        }.map { it.data }

    suspend fun configuracion(): ApiResult<Map<String, GrupoAjustesDto>> =
        cliente.ejecutar<DataDto<Map<String, GrupoAjustesDto>>> { get("config") }.map { it.data }

    suspend fun guardarGrupo(grupo: String, valores: Map<String, String>): ApiResult<Map<String, GrupoAjustesDto>> =
        cliente.ejecutar<DataDto<Map<String, GrupoAjustesDto>>> {
            put("config/$grupo") {
                contentType(ContentType.Application.Json)
                setBody(GuardarGrupoRequestDto(valores))
            }
        }.map { it.data }
}
