package com.example.acopiodeleche.ui.pantallas.productores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acopiodeleche.data.AppContainer
import com.example.acopiodeleche.data.remote.ApiResult
import com.example.acopiodeleche.data.repository.ProductorRepository
import com.example.acopiodeleche.domain.model.Productor
import com.example.acopiodeleche.ui.estado.EstadoEnvio
import com.example.acopiodeleche.ui.estado.EstadoUi
import com.example.acopiodeleche.ui.estado.aEstadoUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductoresViewModel(
    private val repositorio: ProductorRepository = AppContainer.productorRepository
) : ViewModel() {

    private val _productores = MutableStateFlow<EstadoUi<List<Productor>>>(EstadoUi.Cargando)
    val productores: StateFlow<EstadoUi<List<Productor>>> = _productores.asStateFlow()

    private val _envio = MutableStateFlow<EstadoEnvio>(EstadoEnvio.Inactivo)
    val envio: StateFlow<EstadoEnvio> = _envio.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        _productores.value = EstadoUi.Cargando
        viewModelScope.launch { _productores.value = repositorio.listar().aEstadoUi() }
    }

    fun registrar(productor: Productor, correo: String, contrasena: String) {
        if (_envio.value == EstadoEnvio.Enviando) return
        _envio.value = EstadoEnvio.Enviando
        viewModelScope.launch {
            _envio.value = when (val resultado = repositorio.registrar(productor, correo, contrasena)) {
                is ApiResult.Exito -> {
                    reemplazarOAgregar(resultado.datos)
                    EstadoEnvio.Exito("Productor ${resultado.datos.nombreCompleto} registrado.")
                }
                is ApiResult.Error -> EstadoEnvio.Error(resultado.mensaje)
            }
        }
    }

    fun cambiarEstado(productor: Productor) {
        viewModelScope.launch {
            _envio.value = when (val resultado = repositorio.cambiarEstado(productor, !productor.estado)) {
                is ApiResult.Exito -> {
                    reemplazarOAgregar(resultado.datos)
                    EstadoEnvio.Inactivo
                }
                is ApiResult.Error -> EstadoEnvio.Error(resultado.mensaje)
            }
        }
    }

    fun limpiarEnvio() {
        _envio.value = EstadoEnvio.Inactivo
    }

    private fun reemplazarOAgregar(productor: Productor) {
        val actuales = (_productores.value as? EstadoUi.Exito)?.datos.orEmpty()
        val nuevos = if (actuales.any { it.idProductor == productor.idProductor }) {
            actuales.map { if (it.idProductor == productor.idProductor) productor else it }
        } else {
            actuales + productor
        }
        _productores.value = EstadoUi.Exito(nuevos)
    }
}
