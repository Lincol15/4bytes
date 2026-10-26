package com.example.acopiodeleche.data.repository

import com.example.acopiodeleche.data.remote.AdminApi
import com.example.acopiodeleche.data.remote.ApiResult
import com.example.acopiodeleche.data.remote.dto.ActualizarAsignacionRequestDto
import com.example.acopiodeleche.data.remote.dto.CrearUsuarioRequestDto
import com.example.acopiodeleche.data.remote.dto.PublicarAvisoRequestDto
import com.example.acopiodeleche.data.remote.dto.aApi
import com.example.acopiodeleche.data.remote.dto.rolDesdeApi
import com.example.acopiodeleche.data.remote.dto.toDomain
import com.example.acopiodeleche.data.remote.map
import com.example.acopiodeleche.domain.model.AjustePlanta
import com.example.acopiodeleche.domain.model.AsignacionAcopiador
import com.example.acopiodeleche.domain.model.ConfiguracionPlanta
import com.example.acopiodeleche.domain.model.Notificacion
import com.example.acopiodeleche.domain.model.RegistroAcopio
import com.example.acopiodeleche.domain.model.ResumenPanel
import com.example.acopiodeleche.domain.model.Usuario

class AdminRepository(private val api: AdminApi) {

    suspend fun resumen(): ApiResult<ResumenPanel> = api.resumen().map { it.toDomain() }

    /** Usuarios del sistema; se omiten los roles que la app no maneja. */
    suspend fun usuarios(): ApiResult<List<Usuario>> =
        obtenerTodasLasPaginas { pagina -> api.usuarios(pagina) }
            .map { lista -> lista.mapNotNull { dto -> rolDesdeApi(dto.role)?.let { dto.toDomain(it) } } }

    suspend fun acopiadores(): ApiResult<List<Usuario>> =
        obtenerTodasLasPaginas { pagina -> api.usuarios(pagina, rol = "acopiador") }
            .map { lista -> lista.mapNotNull { dto -> rolDesdeApi(dto.role)?.let { dto.toDomain(it) } } }

    /** La contraseña viaja solo en esta petición; Laravel la guarda cifrada. */
    suspend fun crearUsuario(usuario: Usuario): ApiResult<Usuario> =
        api.crearUsuario(
            CrearUsuarioRequestDto(
                name = usuario.nombres,
                lastname = usuario.apellidos,
                dni = usuario.dni,
                email = usuario.correo,
                phone = usuario.telefono.ifBlank { null },
                role = usuario.rol.aApi(),
                comunidad = usuario.comunidad,
                vehiculo = usuario.vehiculo,
                password = usuario.contrasena,
                passwordConfirmation = usuario.contrasena
            )
        ).map { it.toDomain(usuario.rol) }

    suspend fun cambiarEstadoUsuario(usuario: Usuario, activo: Boolean): ApiResult<Usuario> {
        val id = idNumerico(usuario.id) ?: return ERROR_ID_INVALIDO
        return api.cambiarEstadoUsuario(id, activo).map { it.toDomain(usuario.rol) }
    }

    suspend fun asignacion(acopiador: Usuario): ApiResult<AsignacionAcopiador> {
        val id = idNumerico(acopiador.id) ?: return ERROR_ID_INVALIDO
        return api.asignacion(id).map { it.toDomain() }
    }

    /**
     * Guarda la lista de productores. Si todos son de una misma comunidad se envía también la comunidad,
     * y Laravel decide si basta con la asignación por comunidad.
     */
    suspend fun guardarAsignacion(acopiador: Usuario, idsProductores: List<String>, comunidad: String?): ApiResult<AsignacionAcopiador> {
        val id = idNumerico(acopiador.id) ?: return ERROR_ID_INVALIDO
        return api.guardarAsignacion(
            id,
            ActualizarAsignacionRequestDto(comunidad, idsProductores.mapNotNull(::idNumerico))
        ).map { it.toDomain() }
    }

    suspend fun guardarVehiculo(acopiador: Usuario, vehiculo: String): ApiResult<Unit> {
        val id = idNumerico(acopiador.id) ?: return ERROR_ID_INVALIDO
        return api.guardarVehiculo(id, vehiculo.ifBlank { null }).map { }
    }

    suspend fun entregasDeAcopiador(acopiador: Usuario, desde: String): ApiResult<List<RegistroAcopio>> {
        val id = idNumerico(acopiador.id) ?: return ERROR_ID_INVALIDO
        return obtenerTodasLasPaginas { pagina -> api.entregasDeAcopiador(id, desde, pagina) }
            .map { lista -> lista.map { it.toDomain() } }
    }

    suspend fun avisosPublicados(): ApiResult<List<Notificacion>> =
        api.avisosPublicados().map { pagina -> pagina.data.map { it.toDomain() } }

    /** [publicadoEn] en formato "yyyy-MM-ddTHH:mm" (o null para publicar ya). */
    suspend fun publicarAviso(titulo: String, mensaje: String, destinatarios: String, publicadoEn: String?): ApiResult<Notificacion> =
        api.publicarAviso(PublicarAvisoRequestDto(destinatarios, titulo.trim(), mensaje.trim(), publicadoEn = publicadoEn))
            .map { it.toDomain() }

    suspend fun configuracion(): ApiResult<List<AjustePlanta>> =
        api.configuracion().map { it.toDomain() }.also { sincronizarPrecio(it) }

    /**
     * Guarda los [cambios] (clave -> valor) de un grupo. Laravel exige el grupo completo,
     * así que se envían también los valores vigentes del resto de ajustes.
     */
    suspend fun guardarAjustes(grupo: String, vigentes: List<AjustePlanta>, cambios: Map<String, String>): ApiResult<List<AjustePlanta>> {
        val valores = vigentes.filter { it.grupo == grupo }.associate { it.clave to (cambios[it.clave] ?: it.valor) }
        return api.guardarGrupo(grupo, valores).map { it.toDomain() }.also { sincronizarPrecio(it) }
    }

    /** Mantiene el precio mostrado en otras pantallas igual al del backend. */
    private fun sincronizarPrecio(resultado: ApiResult<List<AjustePlanta>>) {
        (resultado as? ApiResult.Exito)?.datos
            ?.firstOrNull { it.clave == "precio_litro_leche" }?.valor?.toDoubleOrNull()
            ?.let { ConfiguracionPlanta.precioPorLitro = it }
    }
}
