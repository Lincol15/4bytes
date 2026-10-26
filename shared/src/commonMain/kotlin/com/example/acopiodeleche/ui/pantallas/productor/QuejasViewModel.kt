package com.example.acopiodeleche.ui.pantallas.productor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acopiodeleche.data.AppContainer
import com.example.acopiodeleche.data.remote.ApiResult
import com.example.acopiodeleche.data.repository.QuejaRepository
import com.example.acopiodeleche.domain.model.EstadoQueja
import com.example.acopiodeleche.domain.model.Queja
import com.example.acopiodeleche.ui.estado.EstadoEnvio
import com.example.acopiodeleche.ui.estado.EstadoUi
import com.example.acopiodeleche.ui.estado.aEstadoUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Quejas: el productor ve y envía las suyas; el administrador ve todas y responde. */
class QuejasViewModel(
    private val repositorio: QuejaRepository = AppContainer.quejaRepository
) : ViewModel() {

    private val _quejas = MutableStateFlow<EstadoUi<List<Queja>>>(EstadoUi.Cargando)
    val quejas: StateFlow<EstadoUi<List<Queja>>> = _quejas.asStateFlow()

    private val _envio = MutableStateFlow<EstadoEnvio>(EstadoEnvio.Inactivo)
    val envio: StateFlow<EstadoEnvio> = _envio.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        _quejas.value = EstadoUi.Cargando
        viewModelScope.launch { _quejas.value = repositorio.listar().aEstadoUi() }
    }

    fun enviar(titulo: String, descripcion: String) {
        ejecutar("Queja enviada. El administrador la responderá.") { repositorio.registrar(titulo, descripcion) }
    }

    fun responder(queja: Queja, estado: EstadoQueja, respuesta: String?) {
        ejecutar("Queja actualizada.") { repositorio.responder(queja, estado, respuesta) }
    }

    fun limpiarEnvio() {
        _envio.value = EstadoEnvio.Inactivo
    }

    private fun ejecutar(mensajeExito: String, accion: suspend () -> ApiResult<Queja>) {
        if (_envio.value == EstadoEnvio.Enviando) return
        _envio.value = EstadoEnvio.Enviando
        viewModelScope.launch {
            _envio.value = when (val resultado = accion()) {
                is ApiResult.Exito -> {
                    val guardada = resultado.datos
                    val actuales = (_quejas.value as? EstadoUi.Exito)?.datos.orEmpty()
                    _quejas.value = EstadoUi.Exito(
                        if (actuales.any { it.id == guardada.id }) actuales.map { if (it.id == guardada.id) guardada else it }
                        else listOf(guardada) + actuales
                    )
                    EstadoEnvio.Exito(mensajeExito)
                }
                is ApiResult.Error -> EstadoEnvio.Error(resultado.mensaje)
            }
        }
    }
}
