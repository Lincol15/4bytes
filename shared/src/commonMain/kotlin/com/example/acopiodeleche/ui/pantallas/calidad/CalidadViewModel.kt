package com.example.acopiodeleche.ui.pantallas.calidad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acopiodeleche.data.remote.ApiResult
import com.example.acopiodeleche.data.repository.CalidadRepository
import com.example.acopiodeleche.data.repository.ProductorRepository
import com.example.acopiodeleche.domain.model.ControlCalidad
import com.example.acopiodeleche.domain.model.Productor
import com.example.acopiodeleche.ui.estado.EstadoEnvio
import com.example.acopiodeleche.ui.estado.EstadoUi
import com.example.acopiodeleche.ui.estado.aEstadoUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CalidadViewModel(
    private val calidadRepository: CalidadRepository,
    private val productorRepository: ProductorRepository
) : ViewModel() {

    private val _analisis = MutableStateFlow<EstadoUi<List<ControlCalidad>>>(EstadoUi.Cargando)
    val analisis: StateFlow<EstadoUi<List<ControlCalidad>>> = _analisis.asStateFlow()

    private val _productores = MutableStateFlow<EstadoUi<List<Productor>>>(EstadoUi.Cargando)
    val productores: StateFlow<EstadoUi<List<Productor>>> = _productores.asStateFlow()

    private val _envio = MutableStateFlow<EstadoEnvio>(EstadoEnvio.Inactivo)
    val envio: StateFlow<EstadoEnvio> = _envio.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        _analisis.value = EstadoUi.Cargando
        _productores.value = EstadoUi.Cargando
        viewModelScope.launch { _analisis.value = calidadRepository.listar().aEstadoUi() }
        viewModelScope.launch { _productores.value = productorRepository.listar().aEstadoUi() }
    }

    /** Envía el análisis; Laravel valida rangos, decide el resultado y actualiza la entrega si está ligada. */
    fun registrar(analisis: ControlCalidad) {
        if (_envio.value == EstadoEnvio.Enviando) return
        _envio.value = EstadoEnvio.Enviando
        viewModelScope.launch {
            _envio.value = when (val resultado = calidadRepository.registrar(analisis)) {
                is ApiResult.Exito -> {
                    val guardado = resultado.datos
                    val actuales = (_analisis.value as? EstadoUi.Exito)?.datos.orEmpty()
                    _analisis.value = EstadoUi.Exito(listOf(guardado) + actuales)
                    EstadoEnvio.Exito("Análisis registrado: ${guardado.resultado.etiqueta}.")
                }
                is ApiResult.Error -> EstadoEnvio.Error(resultado.mensaje)
            }
        }
    }

    fun limpiarEnvio() {
        _envio.value = EstadoEnvio.Inactivo
    }
}
