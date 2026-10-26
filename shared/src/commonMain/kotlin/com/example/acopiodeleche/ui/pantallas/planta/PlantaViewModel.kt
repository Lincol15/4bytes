package com.example.acopiodeleche.ui.pantallas.planta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acopiodeleche.data.remote.ApiResult
import com.example.acopiodeleche.data.repository.PlantaRepository
import com.example.acopiodeleche.domain.model.InsumoPlanta
import com.example.acopiodeleche.domain.model.LoteProduccion
import com.example.acopiodeleche.domain.model.ProductoPlanta
import com.example.acopiodeleche.domain.model.Receta
import com.example.acopiodeleche.domain.model.RecetaDetalle
import com.example.acopiodeleche.domain.model.VentaSalida
import com.example.acopiodeleche.ui.estado.EstadoEnvio
import com.example.acopiodeleche.ui.estado.EstadoUi
import com.example.acopiodeleche.ui.estado.aEstadoUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Planta con datos de Laravel: catálogo, recetas, lotes (con stock según inventario) y ventas.
 * El cálculo de leche, insumos, stock e IGV lo hace el backend.
 */
class PlantaViewModel(
    private val repositorio: PlantaRepository
) : ViewModel() {

    private val _productos = MutableStateFlow<EstadoUi<List<ProductoPlanta>>>(EstadoUi.Cargando)
    val productos: StateFlow<EstadoUi<List<ProductoPlanta>>> = _productos.asStateFlow()

    private val _insumos = MutableStateFlow<EstadoUi<List<InsumoPlanta>>>(EstadoUi.Cargando)
    val insumos: StateFlow<EstadoUi<List<InsumoPlanta>>> = _insumos.asStateFlow()

    private val _recetas = MutableStateFlow<EstadoUi<List<Pair<Receta, List<RecetaDetalle>>>>>(EstadoUi.Cargando)
    val recetas: StateFlow<EstadoUi<List<Pair<Receta, List<RecetaDetalle>>>>> = _recetas.asStateFlow()

    private val _lotes = MutableStateFlow<EstadoUi<List<LoteProduccion>>>(EstadoUi.Cargando)
    val lotes: StateFlow<EstadoUi<List<LoteProduccion>>> = _lotes.asStateFlow()

    private val _ventas = MutableStateFlow<EstadoUi<List<VentaSalida>>>(EstadoUi.Cargando)
    val ventas: StateFlow<EstadoUi<List<VentaSalida>>> = _ventas.asStateFlow()

    private val _envio = MutableStateFlow<EstadoEnvio>(EstadoEnvio.Inactivo)
    val envio: StateFlow<EstadoEnvio> = _envio.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch { _productos.value = repositorio.productos().aEstadoUi() }
        viewModelScope.launch { _insumos.value = repositorio.insumos().aEstadoUi() }
        viewModelScope.launch { _recetas.value = repositorio.recetas().aEstadoUi() }
        viewModelScope.launch { _lotes.value = repositorio.lotes().aEstadoUi() }
        viewModelScope.launch { _ventas.value = repositorio.ventas().aEstadoUi() }
    }

    fun registrarReceta(nombre: String, idProducto: String, descripcion: String?, insumos: Map<String, Double>) =
        enviar("Receta registrada.") { repositorio.registrarReceta(nombre, idProducto, descripcion, insumos) }

    /** [cantidadesPorReceta]: id de receta -> unidades a producir. */
    fun registrarProduccion(fecha: String, cantidadesPorReceta: Map<String, Double>, observacion: String?) {
        val recetas = (_recetas.value as? EstadoUi.Exito)?.datos.orEmpty().map { it.first }
        val porProducto = cantidadesPorReceta
            .mapNotNull { (idReceta, cant) -> recetas.firstOrNull { it.id == idReceta }?.idProducto?.let { it to cant } }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, cantidades) -> cantidades.sum() }
        enviar("Producción registrada. Lotes creados en el inventario.") {
            repositorio.registrarProduccion(fecha, porProducto, observacion)
        }
    }

    fun registrarVenta(lote: LoteProduccion, cliente: String, cantidad: Double, precio: Double, fecha: String, observacion: String?) =
        enviar("Venta registrada (el total incluye IGV en la factura).") {
            repositorio.registrarVenta(lote, cliente, cantidad, precio, fecha, observacion)
        }

    fun limpiarEnvio() {
        _envio.value = EstadoEnvio.Inactivo
    }

    /** Tras registrar algo se recargan los datos: el backend recalcula stock y lotes. */
    private fun <T> enviar(mensajeExito: String, accion: suspend () -> ApiResult<T>) {
        if (_envio.value == EstadoEnvio.Enviando) return
        _envio.value = EstadoEnvio.Enviando
        viewModelScope.launch {
            _envio.value = when (val resultado = accion()) {
                is ApiResult.Exito -> { cargar(); EstadoEnvio.Exito(mensajeExito) }
                is ApiResult.Error -> EstadoEnvio.Error(resultado.mensaje)
            }
        }
    }
}
