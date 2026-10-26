package com.example.acopiodeleche.ui.pantallas.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acopiodeleche.data.remote.ApiResult
import com.example.acopiodeleche.data.repository.CalidadRepository
import com.example.acopiodeleche.data.repository.ProductorRepository
import com.example.acopiodeleche.data.repository.SancionRepository
import com.example.acopiodeleche.domain.model.ControlCalidad
import com.example.acopiodeleche.domain.model.Productor
import com.example.acopiodeleche.domain.model.ResultadoCalidad
import com.example.acopiodeleche.domain.model.Sancion
import com.example.acopiodeleche.ui.estado.EstadoEnvio
import com.example.acopiodeleche.ui.estado.EstadoUi
import com.example.acopiodeleche.ui.estado.aEstadoUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Calidad vista por el administrador: evaluar análisis y emitir sanciones (datos de Laravel). */
class CalidadAdminViewModel(
    private val calidadRepository: CalidadRepository,
    private val sancionRepository: SancionRepository,
    private val productorRepository: ProductorRepository
) : ViewModel() {

    private val _analisis = MutableStateFlow<EstadoUi<List<ControlCalidad>>>(EstadoUi.Cargando)
    val analisis: StateFlow<EstadoUi<List<ControlCalidad>>> = _analisis.asStateFlow()

    private val _sanciones = MutableStateFlow<EstadoUi<List<Sancion>>>(EstadoUi.Cargando)
    val sanciones: StateFlow<EstadoUi<List<Sancion>>> = _sanciones.asStateFlow()

    private val _productores = MutableStateFlow<EstadoUi<List<Productor>>>(EstadoUi.Cargando)
    val productores: StateFlow<EstadoUi<List<Productor>>> = _productores.asStateFlow()

    private val _envio = MutableStateFlow<EstadoEnvio>(EstadoEnvio.Inactivo)
    val envio: StateFlow<EstadoEnvio> = _envio.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        _analisis.value = EstadoUi.Cargando
        _sanciones.value = EstadoUi.Cargando
        _productores.value = EstadoUi.Cargando
        viewModelScope.launch { _analisis.value = calidadRepository.listar().aEstadoUi() }
        viewModelScope.launch { _sanciones.value = sancionRepository.listar().aEstadoUi() }
        viewModelScope.launch { _productores.value = productorRepository.listar().aEstadoUi() }
    }

    fun evaluar(analisis: ControlCalidad, resultado: ResultadoCalidad, motivoRechazo: String?) =
        enviar(null) {
            calidadRepository.evaluarAnalisis(analisis, resultado, motivoRechazo)
                .alExito { actualizado -> _analisis.agregarOReemplazar(actualizado) { it.id } }
        }

    fun registrarSancion(sancion: Sancion) = enviar("Sanción registrada.") {
        sancionRepository.registrar(sancion)
            .alExito { nueva -> _sanciones.value = EstadoUi.Exito(listOf(nueva) + _sanciones.value.lista()) }
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
