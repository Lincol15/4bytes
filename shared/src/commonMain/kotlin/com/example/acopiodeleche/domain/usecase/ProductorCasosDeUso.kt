package com.example.acopiodeleche.domain.usecase

import com.example.acopiodeleche.domain.ApiResult
import com.example.acopiodeleche.domain.model.Productor
import com.example.acopiodeleche.domain.repository.ProductorRepositorio

/**
 * Casos de uso del productor (entidad principal del CRUD). Validan las reglas del negocio
 * antes de llamar al repositorio: un dato inválido no llega a la red.
 */
class ObtenerProductores(private val repositorio: ProductorRepositorio) {
    suspend operator fun invoke(): ApiResult<List<Productor>> =
        when (val resultado = repositorio.listar()) {
            is ApiResult.Exito -> ApiResult.Exito(resultado.datos.sortedBy { it.nombreCompleto.lowercase() })
            is ApiResult.Error -> resultado
        }
}

class RegistrarProductor(private val repositorio: ProductorRepositorio) {
    suspend operator fun invoke(productor: Productor, correo: String, contrasena: String): ApiResult<Productor> {
        validarProductor(productor)?.let { return it }
        validarCuenta(correo, contrasena)?.let { return it }
        return repositorio.registrar(productor, correo.trim(), contrasena)
    }
}

class ActualizarProductor(private val repositorio: ProductorRepositorio) {
    suspend operator fun invoke(productor: Productor): ApiResult<Productor> {
        if (productor.idProductor.isBlank()) return ApiResult.ErrorValidacion("El productor aún no está registrado.")
        validarProductor(productor)?.let { return it }
        return repositorio.actualizar(productor)
    }
}

class EliminarProductor(private val repositorio: ProductorRepositorio) {
    suspend operator fun invoke(productor: Productor): ApiResult<Unit> = repositorio.eliminar(productor)
}

/** Activa al productor inactivo y viceversa. */
class CambiarEstadoProductor(private val repositorio: ProductorRepositorio) {
    suspend operator fun invoke(productor: Productor): ApiResult<Productor> =
        repositorio.cambiarEstado(productor, activo = !productor.estado)
}

/** Mismas reglas que Laravel (StoreProducerRequest / UpdateProducerApiRequest). */
internal fun validarProductor(productor: Productor): ApiResult.ErrorValidacion? = when {
    productor.nombres.isBlank() -> ApiResult.ErrorValidacion("Ingrese los nombres del productor.")
    productor.apellidos.isBlank() -> ApiResult.ErrorValidacion("Ingrese los apellidos del productor.")
    !productor.dni.trim().matches(Regex("\\d{8}")) -> ApiResult.ErrorValidacion("El DNI debe tener 8 dígitos.")
    !productor.telefono.trim().matches(Regex("\\d{9}")) -> ApiResult.ErrorValidacion("El teléfono debe tener 9 dígitos.")
    productor.comunidad.isBlank() -> ApiResult.ErrorValidacion("Ingrese la comunidad del productor.")
    else -> null
}

internal fun validarCuenta(correo: String, contrasena: String): ApiResult.ErrorValidacion? = when {
    !correo.trim().matches(Regex("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) -> ApiResult.ErrorValidacion("Ingrese un correo válido.")
    contrasena.length < 6 -> ApiResult.ErrorValidacion("La contraseña debe tener al menos 6 caracteres.")
    else -> null
}
