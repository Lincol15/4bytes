package com.example.acopiodeleche.domain.repository

import com.example.acopiodeleche.domain.ApiResult
import com.example.acopiodeleche.domain.model.Productor
import com.example.acopiodeleche.domain.model.RegistroAcopio
import kotlinx.coroutines.flow.Flow

/**
 * Contratos de la capa de dominio. La capa de datos los implementa (API de Laravel + SQLite);
 * los casos de uso y ViewModels dependen solo de estas interfaces.
 */
interface ProductorRepositorio {
    suspend fun listar(): ApiResult<List<Productor>>

    /** Crea la ficha del productor y su cuenta de acceso (correo + contraseña). */
    suspend fun registrar(productor: Productor, correo: String, contrasena: String): ApiResult<Productor>

    suspend fun actualizar(productor: Productor): ApiResult<Productor>

    /** Solo se permite si el productor no tiene historial (entregas, pagos o sanciones). */
    suspend fun eliminar(productor: Productor): ApiResult<Unit>

    suspend fun cambiarEstado(productor: Productor, activo: Boolean): ApiResult<Productor>
}

interface RecoleccionRepositorio {
    /** Recolecciones del servidor (o su copia local sin conexión), filtradas por rol en Laravel. */
    suspend fun listar(recibido: Boolean? = null, desde: String? = null): ApiResult<List<RegistroAcopio>>

    /** Recolecciones guardadas en el dispositivo que aún no llegan al servidor. */
    fun pendientes(): Flow<List<RegistroAcopio>>

    /**
     * Guarda la recolección en el dispositivo y la envía; sin conexión queda pendiente
     * (el resultado es el registro local con estado PENDIENTE).
     */
    suspend fun registrar(
        idProductor: String,
        litros: Double,
        observacion: String?,
        idParada: String? = null,
        nombreProductor: String? = null,
        zona: String? = null
    ): ApiResult<RegistroAcopio>

    /** Sube las pendientes; devuelve cuántas se enviaron. */
    suspend fun enviarPendientes(): Int

    fun reintentar(registro: RegistroAcopio)

    fun descartar(registro: RegistroAcopio)

    suspend fun registrarRecepcion(registro: RegistroAcopio): ApiResult<RegistroAcopio>
}
