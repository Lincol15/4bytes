package com.example.acopiodeleche.ui.pantallas.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acopiodeleche.data.AppContainer
import com.example.acopiodeleche.data.remote.ApiResult
import com.example.acopiodeleche.data.repository.AdminRepository
import com.example.acopiodeleche.data.repository.ProductorRepository
import com.example.acopiodeleche.data.repository.RecoleccionRepository
import com.example.acopiodeleche.domain.model.AjustePlanta
import com.example.acopiodeleche.domain.model.AsignacionAcopiador
import com.example.acopiodeleche.domain.model.Notificacion
import com.example.acopiodeleche.domain.model.Productor
import com.example.acopiodeleche.domain.model.RegistroAcopio
import com.example.acopiodeleche.domain.model.ResumenPanel
import com.example.acopiodeleche.domain.model.Usuario
import com.example.acopiodeleche.ui.estado.EstadoEnvio
import com.example.acopiodeleche.ui.estado.EstadoUi
import com.example.acopiodeleche.ui.estado.aEstadoUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/** Panel del administrador: resumen, usuarios, avisos y ajustes de planta leídos de Laravel. */
class AdminViewModel(
    private val repositorio: AdminRepository = AppContainer.adminRepository,
    private val recoleccionRepository: RecoleccionRepository = AppContainer.recoleccionRepository
) : ViewModel() {

    private val _resumen = MutableStateFlow<EstadoUi<ResumenPanel>>(EstadoUi.Cargando)
    val resumen: StateFlow<EstadoUi<ResumenPanel>> = _resumen.asStateFlow()

    private val _usuarios = MutableStateFlow<EstadoUi<List<Usuario>>>(EstadoUi.Cargando)
    val usuarios: StateFlow<EstadoUi<List<Usuario>>> = _usuarios.asStateFlow()

    private val _avisos = MutableStateFlow<EstadoUi<List<Notificacion>>>(EstadoUi.Cargando)
    val avisos: StateFlow<EstadoUi<List<Notificacion>>> = _avisos.asStateFlow()

    private val _ajustes = MutableStateFlow<EstadoUi<List<AjustePlanta>>>(EstadoUi.Cargando)
    val ajustes: StateFlow<EstadoUi<List<AjustePlanta>>> = _ajustes.asStateFlow()

    /** Entregas de hoy de todos los acopiadores (para el panel de acopiadores). */
    private val _entregasHoy = MutableStateFlow<EstadoUi<List<RegistroAcopio>>>(EstadoUi.Cargando)
    val entregasHoy: StateFlow<EstadoUi<List<RegistroAcopio>>> = _entregasHoy.asStateFlow()

    private val _envio = MutableStateFlow<EstadoEnvio>(EstadoEnvio.Inactivo)
    val envio: StateFlow<EstadoEnvio> = _envio.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        val hoy = kotlinx.datetime.Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
        viewModelScope.launch { _entregasHoy.value = recoleccionRepository.listar(desde = hoy).aEstadoUi() }
        _resumen.value = EstadoUi.Cargando
        _usuarios.value = EstadoUi.Cargando
        _avisos.value = EstadoUi.Cargando
        _ajustes.value = EstadoUi.Cargando
        viewModelScope.launch { _resumen.value = repositorio.resumen().aEstadoUi() }
        viewModelScope.launch { _usuarios.value = repositorio.usuarios().aEstadoUi() }
        viewModelScope.launch { _avisos.value = repositorio.avisosPublicados().aEstadoUi() }
        viewModelScope.launch { _ajustes.value = repositorio.configuracion().aEstadoUi() }
    }

    fun crearUsuario(usuario: Usuario) = enviar("Usuario ${usuario.nombreCompleto} creado.") {
        repositorio.crearUsuario(usuario).alExito { creado -> _usuarios.agregarOReemplazar(creado) { it.id } }
    }

    fun cambiarEstadoUsuario(usuario: Usuario) = enviar(null) {
        repositorio.cambiarEstadoUsuario(usuario, !usuario.estado)
            .alExito { actualizado -> _usuarios.agregarOReemplazar(actualizado) { it.id } }
    }

    fun publicarAviso(titulo: String, mensaje: String, publicadoEn: String?) = enviar("Aviso publicado para los productores.") {
        repositorio.publicarAviso(titulo, mensaje, "productores", publicadoEn)
            .alExito { aviso -> _avisos.value = EstadoUi.Exito(listOf(aviso) + _avisos.value.lista()) }
    }

    fun guardarAjustes(grupo: String, cambios: Map<String, String>) = enviar("Configuración guardada.") {
        repositorio.guardarAjustes(grupo, _ajustes.value.lista(), cambios)
            .alExito { _ajustes.value = EstadoUi.Exito(it) }
    }

    fun limpiarEnvio() {
        _envio.value = EstadoEnvio.Inactivo
    }

    private fun <T> enviar(mensajeExito: String?, accion: suspend () -> ApiResult<T>) {
        if (_envio.value == EstadoEnvio.Enviando) return
        _envio.value = EstadoEnvio.Enviando
        viewModelScope.launch {
            _envio.value = when (val resultado = accion()) {
                is ApiResult.Exito -> mensajeExito?.let { EstadoEnvio.Exito(it) } ?: EstadoEnvio.Inactivo
                is ApiResult.Error -> EstadoEnvio.Error(resultado.mensaje)
            }
        }
    }
}

/** Detalle de un acopiador para el administrador: entregas de hoy, asignación y vehículo. */
class AcopiadorAdminViewModel(
    private val acopiador: Usuario,
    private val repositorio: AdminRepository = AppContainer.adminRepository,
    private val productorRepository: ProductorRepository = AppContainer.productorRepository
) : ViewModel() {

    private val _entregasHoy = MutableStateFlow<EstadoUi<List<RegistroAcopio>>>(EstadoUi.Cargando)
    val entregasHoy: StateFlow<EstadoUi<List<RegistroAcopio>>> = _entregasHoy.asStateFlow()

    private val _asignacion = MutableStateFlow<EstadoUi<AsignacionAcopiador>>(EstadoUi.Cargando)
    val asignacion: StateFlow<EstadoUi<AsignacionAcopiador>> = _asignacion.asStateFlow()

    private val _productores = MutableStateFlow<EstadoUi<List<Productor>>>(EstadoUi.Cargando)
    val productores: StateFlow<EstadoUi<List<Productor>>> = _productores.asStateFlow()

    private val _envio = MutableStateFlow<EstadoEnvio>(EstadoEnvio.Inactivo)
    val envio: StateFlow<EstadoEnvio> = _envio.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        val hoy = kotlinx.datetime.Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
        viewModelScope.launch { _entregasHoy.value = repositorio.entregasDeAcopiador(acopiador, hoy).aEstadoUi() }
        viewModelScope.launch { _asignacion.value = repositorio.asignacion(acopiador).aEstadoUi() }
        viewModelScope.launch { _productores.value = productorRepository.listar().aEstadoUi() }
    }

    fun guardarAsignacion(idsProductores: List<String>) {
        val comunidades = _productores.value.lista()
            .filter { it.idProductor in idsProductores }.map { it.comunidad }.distinct()
        // Una sola comunidad: Laravel puede guardarla como asignación por comunidad.
        val comunidad = comunidades.singleOrNull() ?: acopiador.comunidad
        enviar("Asignación guardada.") {
            repositorio.guardarAsignacion(acopiador, idsProductores, comunidad)
                .alExito { _asignacion.value = EstadoUi.Exito(it) }
        }
    }

    fun guardarVehiculo(vehiculo: String) = enviar("Vehículo asignado.") {
        repositorio.guardarVehiculo(acopiador, vehiculo)
    }

    fun limpiarEnvio() {
        _envio.value = EstadoEnvio.Inactivo
    }

    private fun <T> enviar(mensajeExito: String, accion: suspend () -> ApiResult<T>) {
        if (_envio.value == EstadoEnvio.Enviando) return
        _envio.value = EstadoEnvio.Enviando
        viewModelScope.launch {
            _envio.value = when (val resultado = accion()) {
                is ApiResult.Exito -> EstadoEnvio.Exito(mensajeExito)
                is ApiResult.Error -> EstadoEnvio.Error(resultado.mensaje)
            }
        }
    }
}

internal inline fun <T> ApiResult<T>.alExito(accion: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Exito) accion(datos)
    return this
}

internal fun <T> EstadoUi<List<T>>.lista(): List<T> = (this as? EstadoUi.Exito)?.datos.orEmpty()

internal fun <T> MutableStateFlow<EstadoUi<List<T>>>.agregarOReemplazar(elemento: T, clave: (T) -> String) {
    val actuales = value.lista()
    value = EstadoUi.Exito(
        if (actuales.any { clave(it) == clave(elemento) }) actuales.map { if (clave(it) == clave(elemento)) elemento else it }
        else actuales + elemento
    )
}
