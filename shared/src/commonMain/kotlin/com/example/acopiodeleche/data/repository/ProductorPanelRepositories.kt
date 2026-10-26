package com.example.acopiodeleche.data.repository

import com.example.acopiodeleche.domain.ApiResult
import com.example.acopiodeleche.data.remote.CalidadApi
import com.example.acopiodeleche.data.remote.NotificacionApi
import com.example.acopiodeleche.data.remote.PagoApi
import com.example.acopiodeleche.data.remote.QuejaApi
import com.example.acopiodeleche.data.remote.SancionApi
import com.example.acopiodeleche.data.remote.dto.EvaluarAnalisisRequestDto
import com.example.acopiodeleche.data.remote.dto.GenerarPagosRequestDto
import com.example.acopiodeleche.data.remote.dto.RegistrarSancionRequestDto
import com.example.acopiodeleche.data.remote.dto.fechaPantallaAApi
import com.example.acopiodeleche.data.remote.dto.motivoConTipo
import com.example.acopiodeleche.domain.model.ResultadoCalidad
import com.example.acopiodeleche.domain.model.Sancion
import com.example.acopiodeleche.data.remote.dto.RegistrarAnalisisRequestDto
import com.example.acopiodeleche.data.remote.dto.RegistrarQuejaRequestDto
import com.example.acopiodeleche.data.remote.dto.ResponderQuejaRequestDto
import com.example.acopiodeleche.data.remote.dto.aApi
import com.example.acopiodeleche.data.remote.dto.toDomain
import com.example.acopiodeleche.domain.map
import com.example.acopiodeleche.domain.model.ControlCalidad
import com.example.acopiodeleche.domain.model.EstadoQueja
import com.example.acopiodeleche.domain.model.Notificacion
import com.example.acopiodeleche.domain.model.Pago
import com.example.acopiodeleche.domain.model.PagoAcopiador
import com.example.acopiodeleche.domain.model.VistaPreviaPago
import com.example.acopiodeleche.domain.model.Queja

class CalidadRepository(private val api: CalidadApi) {

    suspend fun evaluarAnalisis(analisis: ControlCalidad, resultado: ResultadoCalidad, motivoRechazo: String?): ApiResult<ControlCalidad> {
        val id = idNumerico(analisis.id) ?: return ERROR_ID_INVALIDO
        return api.evaluar(id, EvaluarAnalisisRequestDto(resultado.aApi(), motivoRechazo)).map { it.toDomain() }
    }

    suspend fun listar(): ApiResult<List<ControlCalidad>> =
        obtenerTodasLasPaginas { pagina -> api.listar(pagina) }.map { lista -> lista.map { it.toDomain() } }

    /** El resultado (apto / no apto / observado) lo decide el backend a partir de los parámetros. */
    suspend fun registrar(analisis: ControlCalidad): ApiResult<ControlCalidad> {
        val idProductor = idNumerico(analisis.idProductor)
        val idEntrega = analisis.idRegistroAcopio?.let(::idNumerico)
        if (idProductor == null && idEntrega == null) return ERROR_ID_INVALIDO
        return api.registrar(
            RegistrarAnalisisRequestDto(
                idProductor = idProductor,
                idEntrega = idEntrega,
                origenDatos = analisis.origenDatos.aApi(),
                temperatura = analisis.temperatura,
                grasa = analisis.grasa,
                proteina = analisis.proteina,
                lactosa = analisis.lactosa,
                sng = analisis.sng,
                totalSolidos = analisis.totalSolidos,
                aguaAnadida = analisis.aguaAnadida,
                ph = analisis.ph,
                puntoCongelacion = analisis.puntoCongel,
                densidad = analisis.densidad,
                observations = analisis.observacion?.ifBlank { null }
            )
        ).map { it.toDomain() }
    }
}

class SancionRepository(private val api: SancionApi) {

    suspend fun listar(): ApiResult<List<Sancion>> =
        api.listar().map { pagina -> pagina.data.map { it.toDomain() } }

    suspend fun registrar(sancion: Sancion): ApiResult<Sancion> {
        val idProductor = idNumerico(sancion.idProductor) ?: return ERROR_ID_INVALIDO
        val fecha = fechaPantallaAApi(sancion.fecha) ?: return ApiResult.ErrorValidacion("La fecha debe tener el formato dd/MM/yyyy.")
        return api.registrar(
            RegistrarSancionRequestDto(
                idProductor = idProductor,
                motivo = motivoConTipo(sancion.tipo, sancion.motivo),
                description = sancion.descripcion.ifBlank { null },
                fecha = fecha
            )
        ).map { it.toDomain() }
    }
}

class PagoRepository(private val api: PagoApi) {

    suspend fun listar(): ApiResult<List<Pago>> =
        obtenerTodasLasPaginas { pagina -> api.listar(pagina) }.map { lista -> lista.map { it.toDomain() } }

    /** Fechas "yyyy-MM-dd". Los montos (bonos, descuentos) los calcula PaymentService en Laravel. */
    suspend fun vistaPrevia(inicio: String, fin: String): ApiResult<List<VistaPreviaPago>> =
        api.vistaPrevia(inicio, fin).map { lista -> lista.map { it.toDomain() } }

    suspend fun generar(inicio: String, fin: String, idProductor: String): ApiResult<Int> {
        val id = idNumerico(idProductor) ?: return ERROR_ID_INVALIDO
        return api.generar(GenerarPagosRequestDto(inicio, fin, listOf(id))).map { it.generated }
    }

    suspend fun marcarPagado(pago: Pago): ApiResult<Pago> {
        val id = idNumerico(pago.id) ?: return ERROR_ID_INVALIDO
        return api.marcarPagado(id).map { it.toDomain().copy(nombreProductor = pago.nombreProductor) }
    }

    /** [mes] en formato "yyyy-MM". */
    suspend fun pagosAcopiadores(mes: String): ApiResult<List<PagoAcopiador>> =
        api.pagosAcopiadores(mes).map { pagina -> pagina.data.map { it.toDomain() } }

    suspend fun generarPlanilla(mes: String): ApiResult<Pair<Int, Int>> =
        api.generarPlanilla(mes).map { it.generated to it.sinSueldo }

    suspend fun marcarPagadoAcopiador(pago: PagoAcopiador): ApiResult<PagoAcopiador> {
        val id = idNumerico(pago.id) ?: return ERROR_ID_INVALIDO
        return api.marcarPagadoAcopiador(id).map { it.toDomain().copy(nombreAcopiador = pago.nombreAcopiador) }
    }
}

class NotificacionRepository(private val api: NotificacionApi) {

    suspend fun listar(): ApiResult<List<Notificacion>> =
        obtenerTodasLasPaginas { pagina -> api.listar(pagina) }.map { lista -> lista.map { it.toDomain() } }

    suspend fun marcarLeida(notificacion: Notificacion): ApiResult<Notificacion> {
        val id = idNumerico(notificacion.id) ?: return ERROR_ID_INVALIDO
        return api.marcarLeida(id).map { it.toDomain() }
    }
}

class QuejaRepository(private val api: QuejaApi) {

    suspend fun listar(): ApiResult<List<Queja>> =
        obtenerTodasLasPaginas { pagina -> api.listar(pagina) }.map { lista -> lista.map { it.toDomain() } }

    suspend fun registrar(titulo: String, descripcion: String): ApiResult<Queja> =
        api.registrar(RegistrarQuejaRequestDto(subject = titulo.trim(), description = descripcion.trim()))
            .map { it.toDomain() }

    suspend fun responder(queja: Queja, estado: EstadoQueja, respuesta: String?): ApiResult<Queja> {
        val id = idNumerico(queja.id) ?: return ERROR_ID_INVALIDO
        return api.responder(id, ResponderQuejaRequestDto(estado.aApi(), respuesta?.ifBlank { null }))
            .map { it.toDomain() }
    }
}
