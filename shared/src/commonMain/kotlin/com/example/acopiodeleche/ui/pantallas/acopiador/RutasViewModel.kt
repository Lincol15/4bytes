package com.example.acopiodeleche.ui.pantallas.acopiador

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acopiodeleche.data.remote.ApiResult
import com.example.acopiodeleche.data.repository.RutaRepository
import com.example.acopiodeleche.domain.model.ParadaRuta
import com.example.acopiodeleche.domain.model.RutaRecoleccion
import com.example.acopiodeleche.ui.estado.EstadoUi
import com.example.acopiodeleche.ui.estado.aEstadoUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RutasViewModel(
    private val repositorio: RutaRepository
) : ViewModel() {

    private val _rutas = MutableStateFlow<EstadoUi<List<RutaRecoleccion>>>(EstadoUi.Cargando)
    val rutas: StateFlow<EstadoUi<List<RutaRecoleccion>>> = _rutas.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        _rutas.value = EstadoUi.Cargando
        viewModelScope.launch { _rutas.value = repositorio.listar().aEstadoUi() }
    }

    fun actualizarParada(parada: ParadaRuta, estado: String) {
        viewModelScope.launch {
            when (val resultado = repositorio.actualizarParada(parada, estado)) {
                is ApiResult.Exito -> {
                    val actualizada = resultado.datos
                    val rutas = (_rutas.value as? EstadoUi.Exito)?.datos.orEmpty().map { ruta ->
                        ruta.copy(paradas = ruta.paradas.map { if (it.id == actualizada.id) actualizada else it })
                    }
                    _rutas.value = EstadoUi.Exito(rutas)
                    _error.value = null
                }
                is ApiResult.Error -> _error.value = resultado.mensaje
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}
