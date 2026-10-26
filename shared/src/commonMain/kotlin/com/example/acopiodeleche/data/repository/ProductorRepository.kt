package com.example.acopiodeleche.data.repository

import com.example.acopiodeleche.data.remote.ApiResult
import com.example.acopiodeleche.data.remote.ProductorApi
import com.example.acopiodeleche.data.remote.dto.RegistrarProductorRequestDto
import com.example.acopiodeleche.data.remote.dto.toDomain
import com.example.acopiodeleche.data.remote.map
import com.example.acopiodeleche.domain.model.Productor

class ProductorRepository(private val api: ProductorApi) {

    suspend fun listar(): ApiResult<List<Productor>> =
        obtenerTodasLasPaginas { pagina -> api.listar(pagina) }
            .map { lista -> lista.map { it.toDomain() } }

    /** Laravel crea la cuenta del productor (correo + contraseña) y su ficha en una transacción. */
    suspend fun registrar(productor: Productor, correo: String, contrasena: String): ApiResult<Productor> =
        api.registrar(
            RegistrarProductorRequestDto(
                name = productor.nombres,
                lastname = productor.apellidos,
                dni = productor.dni,
                email = correo.trim(),
                phone = productor.telefono,
                address = productor.direccion.ifBlank { null },
                comunidad = productor.comunidad,
                password = contrasena,
                passwordConfirmation = contrasena
            )
        ).map { it.toDomain() }

    suspend fun cambiarEstado(productor: Productor, activo: Boolean): ApiResult<Productor> {
        val id = idNumerico(productor.idProductor) ?: return ERROR_ID_INVALIDO
        return api.cambiarEstado(id, activo).map { it.toDomain() }
    }
}
