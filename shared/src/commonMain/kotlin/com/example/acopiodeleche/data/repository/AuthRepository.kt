package com.example.acopiodeleche.data.repository

import com.example.acopiodeleche.domain.ApiResult
import com.example.acopiodeleche.data.remote.AuthApi
import com.example.acopiodeleche.data.remote.dto.LoginRequestDto
import com.example.acopiodeleche.data.remote.dto.aSesion
import com.example.acopiodeleche.data.remote.dto.rolDesdeApi
import com.example.acopiodeleche.data.remote.dto.toDomain
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.domain.model.Usuario
import kotlin.time.Clock

class AuthRepository(
    private val api: AuthApi,
    private val ahora: () -> Long = { Clock.System.now().toEpochMilliseconds() }
) {

    /** Valida las credenciales en Laravel y abre (y guarda) la sesión con el access y refresh token. */
    suspend fun iniciarSesion(correoODni: String, contrasena: String): ApiResult<Usuario> =
        when (val resultado = api.login(LoginRequestDto(correoODni.trim(), contrasena))) {
            is ApiResult.Error -> resultado
            is ApiResult.Exito -> {
                val respuesta = resultado.datos
                val rol = rolDesdeApi(respuesta.user.role)
                if (rol == null) {
                    ApiResult.SinPermiso("Su rol no tiene acceso a la aplicación móvil.")
                } else {
                    val usuario = respuesta.user.toDomain(rol)
                    SesionActual.abrir(respuesta.aSesion(usuario, ahora()))
                    ApiResult.Exito(usuario)
                }
            }
        }

    /** Limpia la sesión local de inmediato y luego revoca el token en el servidor (si hay conexión). */
    suspend fun cerrarSesion() {
        val token = SesionActual.sesion?.token
        SesionActual.cerrar()
        if (!token.isNullOrBlank()) {
            api.logout(token)
        }
    }
}
