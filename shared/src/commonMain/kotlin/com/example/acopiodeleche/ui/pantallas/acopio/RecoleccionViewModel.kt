package com.example.acopiodeleche.ui.pantallas.acopio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acopiodeleche.data.AppContainer
import com.example.acopiodeleche.data.remote.ApiResult
import com.example.acopiodeleche.data.repository.ProductorRepository
import com.example.acopiodeleche.data.repository.RecoleccionRepository
import com.example.acopiodeleche.domain.model.Productor
import com.example.acopiodeleche.domain.model.RegistroAcopio
import com.example.acopiodeleche.ui.estado.EstadoEnvio
import com.example.acopiodeleche.ui.estado.EstadoUi
import com.example.acopiodeleche.ui.estado.aEstadoUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado del acopiador: sus recolecciones y sus productores asignados, ambos leídos
 * desde Laravel, y el envío de nuevas recolecciones.
 */
class RecoleccionViewModel(
    private val recoleccionRepository: RecoleccionRepository = AppContainer.recoleccionRepository,
    private val productorRepository: ProductorRepository = AppContainer.productorRepository
) : ViewModel() {

    private val _registros = MutableStateFlow<EstadoUi<List<RegistroAcopio>>>(EstadoUi.Cargando)
    val registros: StateFlow<EstadoUi<List<RegistroAcopio>>> = _registros.asStateFlow()

    private val _productores = MutableStateFlow<EstadoUi<List<Productor>>>(EstadoUi.Cargando)
    val productores: StateFlow<EstadoUi<List<Productor>>> = _productores.asStateFlow()

    private val _envio = MutableStateFlow<EstadoEnvio>(EstadoEnvio.Inactivo)
    val envio: StateFlow<EstadoEnvio> = _envio.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        _registros.value = EstadoUi.Cargando
        _productores.value = EstadoUi.Cargando
        viewModelScope.launch { _registros.value = recoleccionRepository.listar().aEstadoUi() }
        viewModelScope.launch { _productores.value = productorRepository.listar().aEstadoUi() }
    }

    /** Envía la recolección capturada en el formulario; el backend la valida y la guarda. */
    fun registrar(registro: RegistroAcopio) {
        if (_envio.value == EstadoEnvio.Enviando) return
        _envio.value = EstadoEnvio.Enviando
        viewModelScope.launch {
            val resultado = recoleccionRepository.registrar(
                idProductor = registro.idProductor,
                litros = registro.litros,
                observacion = registro.observacion
            )
            _envio.value = when (resultado) {
                is ApiResult.Exito -> {
                    val guardado = resultado.datos
                    val actuales = (_registros.value as? EstadoUi.Exito)?.datos.orEmpty()
                    _registros.value = EstadoUi.Exito(listOf(guardado) + actuales)
                    EstadoEnvio.Exito("Recolección de ${guardado.litrosFormateados} registrada correctamente.")
                }
                is ApiResult.Error -> EstadoEnvio.Error(resultado.mensaje)
            }
        }
    }

    fun limpiarEnvio() {
        _envio.value = EstadoEnvio.Inactivo
    }
}
