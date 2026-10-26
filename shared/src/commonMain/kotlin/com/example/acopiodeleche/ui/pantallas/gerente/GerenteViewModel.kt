package com.example.acopiodeleche.ui.pantallas.gerente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acopiodeleche.data.AppContainer
import com.example.acopiodeleche.data.repository.AdminRepository
import com.example.acopiodeleche.data.repository.CalidadRepository
import com.example.acopiodeleche.data.repository.PagoRepository
import com.example.acopiodeleche.data.repository.RecoleccionRepository
import com.example.acopiodeleche.domain.model.ControlCalidad
import com.example.acopiodeleche.domain.model.Pago
import com.example.acopiodeleche.domain.model.RegistroAcopio
import com.example.acopiodeleche.domain.model.ResumenPanel
import com.example.acopiodeleche.ui.estado.EstadoUi
import com.example.acopiodeleche.ui.estado.aEstadoUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn

/** Vista gerencial de solo lectura con los datos de Laravel. */
class GerenteViewModel(
    private val adminRepository: AdminRepository = AppContainer.adminRepository,
    private val recoleccionRepository: RecoleccionRepository = AppContainer.recoleccionRepository,
    private val calidadRepository: CalidadRepository = AppContainer.calidadRepository,
    private val pagoRepository: PagoRepository = AppContainer.pagoRepository
) : ViewModel() {

    private val _resumen = MutableStateFlow<EstadoUi<ResumenPanel>>(EstadoUi.Cargando)
    val resumen: StateFlow<EstadoUi<ResumenPanel>> = _resumen.asStateFlow()

    /** Recolecciones de los últimos 30 días. */
    private val _registros = MutableStateFlow<EstadoUi<List<RegistroAcopio>>>(EstadoUi.Cargando)
    val registros: StateFlow<EstadoUi<List<RegistroAcopio>>> = _registros.asStateFlow()

    private val _analisis = MutableStateFlow<EstadoUi<List<ControlCalidad>>>(EstadoUi.Cargando)
    val analisis: StateFlow<EstadoUi<List<ControlCalidad>>> = _analisis.asStateFlow()

    private val _pagos = MutableStateFlow<EstadoUi<List<Pago>>>(EstadoUi.Cargando)
    val pagos: StateFlow<EstadoUi<List<Pago>>> = _pagos.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        val desde = kotlinx.datetime.Clock.System.todayIn(TimeZone.currentSystemDefault()).minus(DatePeriod(days = 30)).toString()
        viewModelScope.launch { _resumen.value = adminRepository.resumen().aEstadoUi() }
        viewModelScope.launch { _registros.value = recoleccionRepository.listar(desde = desde).aEstadoUi() }
        viewModelScope.launch { _analisis.value = calidadRepository.listar().aEstadoUi() }
        viewModelScope.launch { _pagos.value = pagoRepository.listar().aEstadoUi() }
    }
}
