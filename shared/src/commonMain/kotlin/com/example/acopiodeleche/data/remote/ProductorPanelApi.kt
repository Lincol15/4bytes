package com.example.acopiodeleche.data.remote

import com.example.acopiodeleche.data.remote.dto.AnalisisCalidadDto
import com.example.acopiodeleche.data.remote.dto.DataDto
import com.example.acopiodeleche.data.remote.dto.EvaluarAnalisisRequestDto
import com.example.acopiodeleche.data.remote.dto.RegistrarSancionRequestDto
import com.example.acopiodeleche.data.remote.dto.SancionDto
import com.example.acopiodeleche.data.remote.dto.NotificacionDto
import com.example.acopiodeleche.data.remote.dto.GenerarPagosRequestDto
import com.example.acopiodeleche.data.remote.dto.GenerarPlanillaRequestDto
import com.example.acopiodeleche.data.remote.dto.MarcarPagadoRequestDto
import com.example.acopiodeleche.data.remote.dto.PagoAcopiadorDto
import com.example.acopiodeleche.data.remote.dto.PagoDto
import com.example.acopiodeleche.data.remote.dto.ResultadoGeneracionDto
import com.example.acopiodeleche.data.remote.dto.VistaPreviaPagoDto
import com.example.acopiodeleche.data.remote.dto.PaginaDto
import com.example.acopiodeleche.data.remote.dto.QuejaDto
import com.example.acopiodeleche.data.remote.dto.RegistrarAnalisisRequestDto
import com.example.acopiodeleche.data.remote.dto.RegistrarQuejaRequestDto
import com.example.acopiodeleche.data.remote.dto.ResponderQuejaRequestDto
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import com.example.acopiodeleche.domain.ApiResult
import com.example.acopiodeleche.domain.map

/** Endpoints /api/v1/quality-reports. Laravel filtra por rol (el productor ve solo los suyos). */
class CalidadApi(private val cliente: ApiClient) {

    suspend fun listar(pagina: Int = 1): ApiResult<PaginaDto<AnalisisCalidadDto>> =
        cliente.ejecutar {
            get("quality-reports") {
                parameter("page", pagina)
                parameter("per_page", 100)
            }
        }

    suspend fun registrar(peticion: RegistrarAnalisisRequestDto): ApiResult<AnalisisCalidadDto> =
        cliente.ejecutar<DataDto<AnalisisCalidadDto>> {
            post("quality-reports") {
                contentType(ContentType.Application.Json)
                setBody(peticion)
            }
        }.map { it.data }

    suspend fun evaluar(id: Int, peticion: EvaluarAnalisisRequestDto): ApiResult<AnalisisCalidadDto> =
        cliente.ejecutar<DataDto<AnalisisCalidadDto>> {
            patch("quality-reports/$id") {
                contentType(ContentType.Application.Json)
                setBody(peticion)
            }
        }.map { it.data }
}

/** Endpoints /api/v1/sanctions. */
class SancionApi(private val cliente: ApiClient) {

    suspend fun listar(): ApiResult<PaginaDto<SancionDto>> = cliente.ejecutar { get("sanctions") }

    suspend fun registrar(peticion: RegistrarSancionRequestDto): ApiResult<SancionDto> =
        cliente.ejecutar<DataDto<SancionDto>> {
            post("sanctions") {
                contentType(ContentType.Application.Json)
                setBody(peticion)
            }
        }.map { it.data }
}

/** Endpoints /api/v1/payments y /api/v1/collector-payments. */
class PagoApi(private val cliente: ApiClient) {

    suspend fun vistaPrevia(inicio: String, fin: String): ApiResult<List<VistaPreviaPagoDto>> =
        cliente.ejecutar<DataDto<List<VistaPreviaPagoDto>>> {
            get("payments/preview") {
                parameter("period_start", inicio)
                parameter("period_end", fin)
            }
        }.map { it.data }

    suspend fun generar(peticion: GenerarPagosRequestDto): ApiResult<ResultadoGeneracionDto> =
        cliente.ejecutar<DataDto<ResultadoGeneracionDto>> {
            post("payments/generate") {
                contentType(ContentType.Application.Json)
                setBody(peticion)
            }
        }.map { it.data }

    suspend fun marcarPagado(id: Int): ApiResult<PagoDto> =
        cliente.ejecutar<DataDto<PagoDto>> {
            patch("payments/$id/mark-paid") {
                contentType(ContentType.Application.Json)
                setBody(MarcarPagadoRequestDto())
            }
        }.map { it.data }

    suspend fun pagosAcopiadores(mes: String): ApiResult<PaginaDto<PagoAcopiadorDto>> =
        cliente.ejecutar { get("collector-payments") { parameter("month", mes) } }

    suspend fun generarPlanilla(mes: String): ApiResult<ResultadoGeneracionDto> =
        cliente.ejecutar<DataDto<ResultadoGeneracionDto>> {
            post("collector-payments/generate") {
                contentType(ContentType.Application.Json)
                setBody(GenerarPlanillaRequestDto(mes))
            }
        }.map { it.data }

    suspend fun marcarPagadoAcopiador(id: Int): ApiResult<PagoAcopiadorDto> =
        cliente.ejecutar<DataDto<PagoAcopiadorDto>> {
            patch("collector-payments/$id/mark-paid") {
                contentType(ContentType.Application.Json)
                setBody(MarcarPagadoRequestDto())
            }
        }.map { it.data }

    suspend fun listar(pagina: Int = 1): ApiResult<PaginaDto<PagoDto>> =
        cliente.ejecutar {
            get("payments") {
                parameter("page", pagina)
                parameter("per_page", 100)
            }
        }
}

/** Endpoints /api/v1/notifications. */
class NotificacionApi(private val cliente: ApiClient) {

    suspend fun listar(pagina: Int = 1): ApiResult<PaginaDto<NotificacionDto>> =
        cliente.ejecutar { get("notifications") { parameter("page", pagina) } }

    suspend fun marcarLeida(id: Int): ApiResult<NotificacionDto> =
        cliente.ejecutar<DataDto<NotificacionDto>> { patch("notifications/$id/read") }.map { it.data }
}

/** Endpoints /api/v1/complaints. */
class QuejaApi(private val cliente: ApiClient) {

    suspend fun listar(pagina: Int = 1): ApiResult<PaginaDto<QuejaDto>> =
        cliente.ejecutar {
            get("complaints") {
                parameter("page", pagina)
                parameter("per_page", 100)
            }
        }

    suspend fun registrar(peticion: RegistrarQuejaRequestDto): ApiResult<QuejaDto> =
        cliente.ejecutar<DataDto<QuejaDto>> {
            post("complaints") {
                contentType(ContentType.Application.Json)
                setBody(peticion)
            }
        }.map { it.data }

    suspend fun responder(id: Int, peticion: ResponderQuejaRequestDto): ApiResult<QuejaDto> =
        cliente.ejecutar<DataDto<QuejaDto>> {
            patch("complaints/$id") {
                contentType(ContentType.Application.Json)
                setBody(peticion)
            }
        }.map { it.data }
}
