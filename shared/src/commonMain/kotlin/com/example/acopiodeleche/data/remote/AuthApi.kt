package com.example.acopiodeleche.data.remote

import com.example.acopiodeleche.data.remote.dto.DataDto
import com.example.acopiodeleche.data.remote.dto.LoginRequestDto
import com.example.acopiodeleche.data.remote.dto.LoginResponseDto
import com.example.acopiodeleche.data.remote.dto.UsuarioDto
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

/** Endpoints de autenticación /api/v1/auth (Laravel Sanctum). */
class AuthApi(private val cliente: ApiClient) {

    suspend fun login(peticion: LoginRequestDto): ApiResult<LoginResponseDto> =
        cliente.ejecutar<DataDto<LoginResponseDto>> {
            post("auth/login") {
                contentType(ContentType.Application.Json)
                setBody(peticion)
            }
        }.map { it.data }

    suspend fun usuarioActual(): ApiResult<UsuarioDto> =
        cliente.ejecutar<DataDto<UsuarioDto>> { get("auth/me") }.map { it.data }

    /** Revoca el [token] indicado (la sesión local ya puede estar cerrada). */
    suspend fun logout(token: String): ApiResult<Unit> =
        cliente.ejecutarSinCuerpo {
            post("auth/logout") { header(HttpHeaders.Authorization, "Bearer $token") }
        }
}
