package com.example.acopiodeleche.ui.pantallas.productor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acopiodeleche.domain.ApiResult
import com.example.acopiodeleche.data.repository.CalidadRepository
import com.example.acopiodeleche.data.repository.NotificacionRepository
import com.example.acopiodeleche.data.repository.PagoRepository
import com.example.acopiodeleche.domain.model.ControlCalidad
import com.example.acopiodeleche.domain.model.Notificacion
import com.example.acopiodeleche.domain.model.Pago
import com.example.acopiodeleche.domain.model.RegistroAcopio
import com.example.acopiodeleche.ui.estado.EstadoUi
import com.example.acopiodeleche.ui.estado.aEstadoUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.acopiodeleche.domain.usecase.ObtenerRecolecciones

/** Panel del productor: Laravel devuelve solo sus entregas, pagos y análisis, y los avisos dirigidos a él. */
class ProductorViewModel(
    private val obtenerRecolecciones: ObtenerRecolecciones,
    private val pagoRepository: PagoRepository,
    private val calidadRepository: CalidadRepository,
    private val notificacionRepository: NotificacionRepository
) : ViewModel() {

    private val _entregas = MutableStateFlow<EstadoUi<List<RegistroAcopio>>>(EstadoUi.Cargando)
    val entregas: StateFlow<EstadoUi<List<RegistroAcopio>>> = _entregas.asStateFlow()

    private val _pagos = MutableStateFlow<EstadoUi<List<Pago>>>(EstadoUi.Cargando)
    val pagos: StateFlow<EstadoUi<List<Pago>>> = _pagos.asStateFlow()

    private val _analisis = MutableStateFlow<EstadoUi<List<ControlCalidad>>>(EstadoUi.Cargando)
    val analisis: StateFlow<EstadoUi<List<ControlCalidad>>> = _analisis.asStateFlow()

    private val _notificaciones = MutableStateFlow<EstadoUi<List<Notificacion>>>(EstadoUi.Cargando)
    val notificaciones: StateFlow<EstadoUi<List<Notificacion>>> = _notificaciones.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        _entregas.value = EstadoUi.Cargando
        _pagos.value = EstadoUi.Cargando
        _analisis.value = EstadoUi.Cargando
        _notificaciones.value = EstadoUi.Cargando
        viewModelScope.launch { _entregas.value = obtenerRecolecciones().aEstadoUi() }
        viewModelScope.launch { _pagos.value = pagoRepository.listar().aEstadoUi() }
        viewModelScope.launch { _analisis.value = calidadRepository.listar().aEstadoUi() }
        viewModelScope.launch { _notificaciones.value = notificacionRepository.listar().aEstadoUi() }
    }

    fun marcarLeida(notificacion: Notificacion) {
        if (notificacion.leida) return
        viewModelScope.launch {
            val resultado = notificacionRepository.marcarLeida(notificacion)
            if (resultado is ApiResult.Exito) {
                val actuales = (_notificaciones.value as? EstadoUi.Exito)?.datos.orEmpty()
                _notificaciones.value = EstadoUi.Exito(
                    actuales.map { if (it.id == notificacion.id) it.copy(leida = true) else it }
                )
            }
        }
    }
}
