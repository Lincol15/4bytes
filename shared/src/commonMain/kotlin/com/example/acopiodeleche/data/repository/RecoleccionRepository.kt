package com.example.acopiodeleche.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.acopiodeleche.domain.ApiResult
import com.example.acopiodeleche.data.remote.RecoleccionApi
import com.example.acopiodeleche.data.remote.dto.RegistrarRecoleccionRequestDto
import com.example.acopiodeleche.data.remote.dto.toDomain
import com.example.acopiodeleche.domain.map
import com.example.acopiodeleche.db.AcopioDatabase
import com.example.acopiodeleche.db.Recoleccion_pendiente
import com.example.acopiodeleche.domain.model.EstadoSync
import com.example.acopiodeleche.domain.model.RegistroAcopio
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.domain.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import com.example.acopiodeleche.domain.repository.RecoleccionRepositorio

/**
 * Recolecciones del acopiador, offline-first.
 *
 * Cada registro se guarda primero en SQLite (tabla recoleccion_pendiente) y luego se envía a
 * Laravel. Si no hay conexión queda pendiente y [enviarPendientes] lo sube cuando vuelve la red.
 * Sin [db] (tests de la capa remota) se envía directo, como antes.
 */
class RecoleccionRepository(
    private val api: RecoleccionApi,
    private val db: AcopioDatabase? = null,
    private val ahora: () -> Long = { Clock.System.now().toEpochMilliseconds() },
    private val nuevoUuid: () -> String = ::uuidAleatorio
) : RecoleccionRepositorio {
    /** Evita enviar el mismo registro dos veces a la vez (registro inmediato y sincronización). */
    private val mutexEnvio = Mutex()

    /** Recolecciones visibles para el usuario actual (Laravel aplica el filtro por rol). */
    override suspend fun listar(recibido: Boolean?, desde: String?): ApiResult<List<RegistroAcopio>> =
        obtenerTodasLasPaginas { pagina -> api.listar(pagina = pagina, desde = desde, recibido = recibido) }
            .map { lista -> lista.map { it.toDomain() } }

    /** Recolecciones del usuario actual que aún no están en el servidor (pendientes o rechazadas). */
    override fun pendientes(): Flow<List<RegistroAcopio>> {
        val db = db ?: return flowOf(emptyList())
        val usuario = SesionActual.usuario ?: return flowOf(emptyList())
        return db.recoleccionPendienteQueries.deUsuario(usuario.id)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { filas -> filas.map { it.toDomain(usuario) } }
    }

    /**
     * Registra la recolección. Fecha, hora, vehículo, zona y precio los fija Laravel; si se guarda
     * sin conexión, se envía después con la hora real de la recolección.
     *
     * @return el registro del servidor, o el registro local con [EstadoSync.PENDIENTE] si no hubo
     * conexión. Si el servidor rechaza los datos se devuelve el error y no se guarda nada.
     */
    override suspend fun registrar(
        idProductor: String,
        litros: Double,
        observacion: String?,
        idParada: String?,
        nombreProductor: String?,
        zona: String?
    ): ApiResult<RegistroAcopio> {
        val productor = idNumerico(idProductor) ?: return ERROR_ID_INVALIDO
        val db = db ?: return api.registrar(
            RegistrarRecoleccionRequestDto(
                idProductor = productor,
                liters = litros,
                observations = observacion?.ifBlank { null },
                idParada = idParada?.let(::idNumerico)
            )
        ).map { it.toDomain() }

        val usuario = SesionActual.usuario
            ?: return ApiResult.NoAutenticado("Inicie sesión para registrar recolecciones.")
        val uuid = nuevoUuid()
        db.recoleccionPendienteQueries.insertar(
            client_uuid = uuid,
            id_usuario = usuario.id,
            id_productor = idProductor,
            nombre_productor = nombreProductor,
            zona = zona,
            litros = litros,
            observacion = observacion?.ifBlank { null },
            id_parada = idParada,
            registrado_en = ahora()
        )
        val fila = db.recoleccionPendienteQueries.deUsuario(usuario.id).executeAsList().first { it.client_uuid == uuid }

        return mutexEnvio.withLock {
            when (val resultado = enviar(fila)) {
                is ApiResult.Exito -> {
                    db.recoleccionPendienteQueries.eliminar(uuid)
                    resultado
                }
                // Datos rechazados con conexión: el acopiador corrige el formulario, no queda pendiente.
                is ApiResult.ErrorValidacion, is ApiResult.SinPermiso -> {
                    db.recoleccionPendienteQueries.eliminar(uuid)
                    resultado
                }
                is ApiResult.ErrorServidor -> if (esErrorTemporal(resultado)) {
                    ApiResult.Exito(fila.toDomain(usuario))
                } else {
                    db.recoleccionPendienteQueries.eliminar(uuid)
                    resultado
                }
                is ApiResult.ErrorConexion, is ApiResult.NoAutenticado -> ApiResult.Exito(fila.toDomain(usuario))
            }
        }
    }

    /**
     * Sube al servidor las recolecciones pendientes del usuario, de la más antigua a la más nueva.
     * Se detiene al primer problema de conexión; las rechazadas quedan como [EstadoSync.ERROR].
     * @return cuántas se enviaron.
     */
    override suspend fun enviarPendientes(): Int {
        val db = db ?: return 0
        val usuario = SesionActual.usuario ?: return 0
        return mutexEnvio.withLock {
            var enviadas = 0
            for (fila in db.recoleccionPendienteQueries.porEnviar(usuario.id).executeAsList()) {
                when (val resultado = enviar(fila)) {
                    is ApiResult.Exito -> {
                        db.recoleccionPendienteQueries.eliminar(fila.client_uuid)
                        enviadas++
                    }
                    is ApiResult.ErrorConexion, is ApiResult.NoAutenticado -> break
                    is ApiResult.ErrorServidor -> if (esErrorTemporal(resultado)) {
                        db.recoleccionPendienteQueries.sumarIntento(fila.client_uuid)
                        break
                    } else {
                        db.recoleccionPendienteQueries.marcarError(resultado.mensaje, fila.client_uuid)
                    }
                    is ApiResult.ErrorValidacion, is ApiResult.SinPermiso ->
                        db.recoleccionPendienteQueries.marcarError(resultado.mensaje, fila.client_uuid)
                }
            }
            enviadas
        }
    }

    /** Vuelve a poner en cola una recolección rechazada (p. ej. después de que el admin corrigió la asignación). */
    override fun reintentar(registro: RegistroAcopio) {
        registro.clientUuid?.let { db?.recoleccionPendienteQueries?.reintentar(it) }
    }

    /** Descarta una recolección que solo existe en el dispositivo. */
    override fun descartar(registro: RegistroAcopio) {
        registro.clientUuid?.let { db?.recoleccionPendienteQueries?.eliminar(it) }
    }

    override suspend fun registrarRecepcion(registro: RegistroAcopio): ApiResult<RegistroAcopio> {
        val id = idNumerico(registro.id) ?: return ERROR_ID_INVALIDO
        return api.registrarRecepcion(id).map { it.toDomain() }
    }

    private suspend fun enviar(fila: Recoleccion_pendiente): ApiResult<RegistroAcopio> {
        val productor = idNumerico(fila.id_productor) ?: return ERROR_ID_INVALIDO
        return api.registrar(
            RegistrarRecoleccionRequestDto(
                idProductor = productor,
                liters = fila.litros,
                observations = fila.observacion,
                idParada = fila.id_parada?.let(::idNumerico),
                clientUuid = fila.client_uuid,
                recolectadoEn = Instant.fromEpochMilliseconds(fila.registrado_en).toString()
            )
        ).map { it.toDomain() }
    }

    /** 5xx, 429 o respuesta ilegible: el servidor podría aceptarla más tarde. */
    private fun esErrorTemporal(error: ApiResult.ErrorServidor): Boolean =
        error.codigo == 0 || error.codigo == 429 || error.codigo >= 500
}

private fun Recoleccion_pendiente.toDomain(usuario: Usuario): RegistroAcopio {
    val momento = Instant.fromEpochMilliseconds(registrado_en).toLocalDateTime(TimeZone.currentSystemDefault())
    return RegistroAcopio(
        id = client_uuid,
        idProductor = id_productor,
        acopiador = usuario.nombreCompleto,
        vehiculo = usuario.vehiculo.orEmpty(),
        zona = zona ?: usuario.comunidad.orEmpty(),
        fecha = "${momento.day.dosDigitos()}/${momento.month.ordinal.plus(1).dosDigitos()}/${momento.year}",
        hora = "${momento.hour.dosDigitos()}:${momento.minute.dosDigitos()}",
        litros = litros,
        observacion = observacion,
        recibido = false,
        nombreProductor = nombre_productor,
        estado = if (estado == EstadoSync.ERROR.name) "rechazado" else "pendiente",
        idAcopiador = usuario.id,
        estadoSync = if (estado == EstadoSync.ERROR.name) EstadoSync.ERROR else EstadoSync.PENDIENTE,
        errorSync = error,
        clientUuid = client_uuid
    )
}

private fun Int.dosDigitos(): String = toString().padStart(2, '0')

@OptIn(ExperimentalUuidApi::class)
private fun uuidAleatorio(): String = Uuid.random().toString()
