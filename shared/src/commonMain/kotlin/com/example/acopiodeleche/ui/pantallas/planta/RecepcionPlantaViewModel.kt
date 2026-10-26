package com.example.acopiodeleche.ui.pantallas.planta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acopiodeleche.domain.ApiResult
import com.example.acopiodeleche.domain.model.RegistroAcopio
import com.example.acopiodeleche.ui.estado.EstadoEnvio
import com.example.acopiodeleche.ui.estado.EstadoUi
import com.example.acopiodeleche.ui.estado.aEstadoUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.acopiodeleche.domain.repository.RecoleccionRepositorio

/** Entregas "en tránsito" y registro de su recepción en planta. */
class RecepcionPlantaViewModel(
    private val repositorio: RecoleccionRepositorio
) : ViewModel() {

    private val _pendientes = MutableStateFlow<EstadoUi<List<RegistroAcopio>>>(EstadoUi.Cargando)
    val pendientes: StateFlow<EstadoUi<List<RegistroAcopio>>> = _pendientes.asStateFlow()

    private val _envio = MutableStateFlow<EstadoEnvio>(EstadoEnvio.Inactivo)
    val envio: StateFlow<EstadoEnvio> = _envio.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        _pendientes.value = EstadoUi.Cargando
        viewModelScope.launch { _pendientes.value = repositorio.listar(recibido = false).aEstadoUi() }
    }

    fun registrarRecepcion(registro: RegistroAcopio) {
        if (_envio.value == EstadoEnvio.Enviando) return
        _envio.value = EstadoEnvio.Enviando
        viewModelScope.launch {
            _envio.value = when (val resultado = repositorio.registrarRecepcion(registro)) {
                is ApiResult.Exito -> {
                    val actuales = (_pendientes.value as? EstadoUi.Exito)?.datos.orEmpty()
                    _pendientes.value = EstadoUi.Exito(actuales.filterNot { it.id == registro.id })
                    EstadoEnvio.Exito("Recepción de ${resultado.datos.litrosFormateados} registrada.")
                }
                is ApiResult.Error -> EstadoEnvio.Error(resultado.mensaje)
            }
        }
    }

    fun limpiarEnvio() {
        _envio.value = EstadoEnvio.Inactivo
    }
}
