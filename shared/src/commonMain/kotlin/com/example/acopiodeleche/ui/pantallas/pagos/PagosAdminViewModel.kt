package com.example.acopiodeleche.ui.pantallas.pagos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acopiodeleche.data.AppContainer
import com.example.acopiodeleche.data.remote.ApiResult
import com.example.acopiodeleche.data.repository.PagoRepository
import com.example.acopiodeleche.domain.model.Pago
import com.example.acopiodeleche.domain.model.PagoAcopiador
import com.example.acopiodeleche.domain.model.VistaPreviaPago
import com.example.acopiodeleche.ui.estado.EstadoEnvio
import com.example.acopiodeleche.ui.estado.EstadoUi
import com.example.acopiodeleche.ui.estado.aEstadoUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

/**
 * Pagos para el administrador. Laravel calcula las liquidaciones (PaymentService) y la planilla de
 * acopiadores (CollectorPayrollService); la app solo las muestra y dispara las acciones.
 */
class PagosAdminViewModel(
    private val repositorio: PagoRepository = AppContainer.pagoRepository
) : ViewModel() {

    private val hoy = kotlin.time.Clock.System.todayIn(TimeZone.currentSystemDefault())

    /** Semana actual (lunes a domingo), en formato "yyyy-MM-dd". */
    val inicioSemana = hoy.minus(DatePeriod(days = hoy.dayOfWeek.isoDayNumber - 1)).toString()
    val finSemana = hoy.minus(DatePeriod(days = hoy.dayOfWeek.isoDayNumber - 1)).plus(DatePeriod(days = 6)).toString()
    val mesActual = hoy.toString().take(7)

    private val _vistaPrevia = MutableStateFlow<EstadoUi<List<VistaPreviaPago>>>(EstadoUi.Cargando)
    val vistaPrevia: StateFlow<EstadoUi<List<VistaPreviaPago>>> = _vistaPrevia.asStateFlow()

    private val _pagos = MutableStateFlow<EstadoUi<List<Pago>>>(EstadoUi.Cargando)
    val pagos: StateFlow<EstadoUi<List<Pago>>> = _pagos.asStateFlow()

    private val _pagosAcopiadores = MutableStateFlow<EstadoUi<List<PagoAcopiador>>>(EstadoUi.Cargando)
    val pagosAcopiadores: StateFlow<EstadoUi<List<PagoAcopiador>>> = _pagosAcopiadores.asStateFlow()

    private val _envio = MutableStateFlow<EstadoEnvio>(EstadoEnvio.Inactivo)
    val envio: StateFlow<EstadoEnvio> = _envio.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch { _vistaPrevia.value = repositorio.vistaPrevia(inicioSemana, finSemana).aEstadoUi() }
        viewModelScope.launch { _pagos.value = repositorio.listar().aEstadoUi() }
        viewModelScope.launch { _pagosAcopiadores.value = repositorio.pagosAcopiadores(mesActual).aEstadoUi() }
    }

    fun generarPago(vista: VistaPreviaPago) = enviar {
        when (val r = repositorio.generar(inicioSemana, finSemana, vista.idProductor)) {
            is ApiResult.Exito -> { cargar(); EstadoEnvio.Exito("Pago de ${vista.nombreProductor} generado.") }
            is ApiResult.Error -> EstadoEnvio.Error(r.mensaje)
        }
    }

    fun marcarPagado(pago: Pago) = enviar {
        when (val r = repositorio.marcarPagado(pago)) {
            is ApiResult.Exito -> {
                _pagos.value = EstadoUi.Exito(listaDe(_pagos.value).map { if (it.id == pago.id) r.datos else it })
                EstadoEnvio.Exito("Pago marcado como pagado.")
            }
            is ApiResult.Error -> EstadoEnvio.Error(r.mensaje)
        }
    }

    fun generarPlanilla() = enviar {
        when (val r = repositorio.generarPlanilla(mesActual)) {
            is ApiResult.Exito -> {
                cargar()
                val (generados, sinSueldo) = r.datos
                EstadoEnvio.Exito(
                    "Planilla del mes: $generados pago(s) generado(s)." +
                        if (sinSueldo > 0) " $sinSueldo acopiador(es) sin sueldo definido." else ""
                )
            }
            is ApiResult.Error -> EstadoEnvio.Error(r.mensaje)
        }
    }

    fun marcarPagadoAcopiador(pago: PagoAcopiador) = enviar {
        when (val r = repositorio.marcarPagadoAcopiador(pago)) {
            is ApiResult.Exito -> {
                _pagosAcopiadores.value = EstadoUi.Exito(listaDe(_pagosAcopiadores.value).map { if (it.id == pago.id) r.datos else it })
                EstadoEnvio.Exito("Sueldo de ${pago.nombreAcopiador} marcado como pagado.")
            }
            is ApiResult.Error -> EstadoEnvio.Error(r.mensaje)
        }
    }

    fun limpiarEnvio() {
        _envio.value = EstadoEnvio.Inactivo
    }

    private fun <T> listaDe(estado: EstadoUi<List<T>>): List<T> = (estado as? EstadoUi.Exito)?.datos.orEmpty()

    private fun enviar(accion: suspend () -> EstadoEnvio) {
        if (_envio.value == EstadoEnvio.Enviando) return
        _envio.value = EstadoEnvio.Enviando
        viewModelScope.launch { _envio.value = accion() }
    }
}
