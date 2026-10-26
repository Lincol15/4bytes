package com.example.acopiodeleche.ui.pantallas.productores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acopiodeleche.domain.ApiResult
import com.example.acopiodeleche.domain.usecase.ActualizarProductor
import com.example.acopiodeleche.domain.usecase.CambiarEstadoProductor
import com.example.acopiodeleche.domain.usecase.EliminarProductor
import com.example.acopiodeleche.domain.usecase.ObtenerProductores
import com.example.acopiodeleche.domain.usecase.RegistrarProductor
import com.example.acopiodeleche.domain.model.Productor
import com.example.acopiodeleche.ui.estado.EstadoEnvio
import com.example.acopiodeleche.ui.estado.EstadoUi
import com.example.acopiodeleche.ui.estado.aEstadoUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** CRUD de productores a través de los casos de uso del dominio (validan antes de llamar a la API). */
class ProductoresViewModel(
    private val obtenerProductores: ObtenerProductores,
    private val registrarProductor: RegistrarProductor,
    private val actualizarProductor: ActualizarProductor,
    private val eliminarProductor: EliminarProductor,
    private val cambiarEstadoProductor: CambiarEstadoProductor
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
        viewModelScope.launch { _productores.value = obtenerProductores().aEstadoUi() }
    }

    fun registrar(productor: Productor, correo: String, contrasena: String) {
        if (_envio.value == EstadoEnvio.Enviando) return
        _envio.value = EstadoEnvio.Enviando
        viewModelScope.launch {
            _envio.value = when (val resultado = registrarProductor(productor, correo, contrasena)) {
                is ApiResult.Exito -> {
                    reemplazarOAgregar(resultado.datos)
                    EstadoEnvio.Exito("Productor ${resultado.datos.nombreCompleto} registrado.")
                }
                is ApiResult.Error -> EstadoEnvio.Error(resultado.mensaje)
            }
        }
    }

    fun actualizar(productor: Productor) {
        if (_envio.value == EstadoEnvio.Enviando) return
        _envio.value = EstadoEnvio.Enviando
        viewModelScope.launch {
            _envio.value = when (val resultado = actualizarProductor(productor)) {
                is ApiResult.Exito -> {
                    reemplazarOAgregar(resultado.datos)
                    EstadoEnvio.Exito("Datos de ${resultado.datos.nombreCompleto} actualizados.")
                }
                is ApiResult.Error -> EstadoEnvio.Error(resultado.mensaje)
            }
        }
    }

    /** Laravel rechaza (422) eliminar productores con historial; el mensaje sugiere desactivarlo. */
    fun eliminar(productor: Productor) {
        viewModelScope.launch {
            _envio.value = when (val resultado = eliminarProductor(productor)) {
                is ApiResult.Exito -> {
                    val actuales = (_productores.value as? EstadoUi.Exito)?.datos.orEmpty()
                    _productores.value = EstadoUi.Exito(actuales.filterNot { it.idProductor == productor.idProductor })
                    EstadoEnvio.Exito("Productor ${productor.nombreCompleto} eliminado.")
                }
                is ApiResult.Error -> EstadoEnvio.Error(resultado.mensaje)
            }
        }
    }

    fun cambiarEstado(productor: Productor) {
        viewModelScope.launch {
            _envio.value = when (val resultado = cambiarEstadoProductor(productor)) {
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
