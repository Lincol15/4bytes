package com.example.acopiodeleche.ui.pantallas.acopio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acopiodeleche.data.remote.ApiResult
import com.example.acopiodeleche.data.repository.ProductorRepository
import com.example.acopiodeleche.data.repository.RecoleccionRepository
import com.example.acopiodeleche.data.sync.Sincronizador
import com.example.acopiodeleche.domain.model.Productor
import com.example.acopiodeleche.domain.model.RegistroAcopio
import com.example.acopiodeleche.ui.estado.EstadoEnvio
import com.example.acopiodeleche.ui.estado.EstadoUi
import com.example.acopiodeleche.ui.estado.aEstadoUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Estado del acopiador: sus recolecciones (las del servidor, o su copia local sin conexión, más
 * las guardadas en el teléfono que aún no se envían) y sus productores asignados.
 */
class RecoleccionViewModel(
    private val recoleccionRepository: RecoleccionRepository,
    private val productorRepository: ProductorRepository,
    private val sincronizador: Sincronizador
) : ViewModel() {

    private val _delServidor = MutableStateFlow<EstadoUi<List<RegistroAcopio>>>(EstadoUi.Cargando)

    /** Registros pendientes primero, luego los del servidor. */
    val registros: StateFlow<EstadoUi<List<RegistroAcopio>>> =
        combine(_delServidor, recoleccionRepository.pendientes()) { servidor, locales ->
            when (servidor) {
                is EstadoUi.Exito -> {
                    // Si un pendiente ya llegó al servidor (y la lista lo trae), no se muestra dos veces.
                    val enServidor = servidor.datos.mapNotNull { it.clientUuid }.toSet()
                    EstadoUi.Exito(locales.filter { it.clientUuid !in enServidor } + servidor.datos)
                }
                // Sin conexión ni copia local: al menos se ven los registros del teléfono.
                is EstadoUi.Error -> if (locales.isNotEmpty()) EstadoUi.Exito(locales) else servidor
                EstadoUi.Cargando -> servidor
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, EstadoUi.Cargando)

    private val _productores = MutableStateFlow<EstadoUi<List<Productor>>>(EstadoUi.Cargando)
    val productores: StateFlow<EstadoUi<List<Productor>>> = _productores.asStateFlow()

    private val _envio = MutableStateFlow<EstadoEnvio>(EstadoEnvio.Inactivo)
    val envio: StateFlow<EstadoEnvio> = _envio.asStateFlow()

    init {
        cargar()
        // Al subirse registros pendientes, la lista del servidor ya los incluye.
        viewModelScope.launch { sincronizador.cambios.collect { cargarRegistros() } }
    }

    fun cargar() {
        _productores.value = EstadoUi.Cargando
        cargarRegistros()
        viewModelScope.launch { _productores.value = productorRepository.listar().aEstadoUi() }
    }

    private fun cargarRegistros() {
        if (_delServidor.value !is EstadoUi.Exito) _delServidor.value = EstadoUi.Cargando
        viewModelScope.launch { _delServidor.value = recoleccionRepository.listar().aEstadoUi() }
    }

    /** Guarda la recolección en el teléfono y la envía; sin conexión queda pendiente. */
    fun registrar(registro: RegistroAcopio) {
        if (_envio.value == EstadoEnvio.Enviando) return
        _envio.value = EstadoEnvio.Enviando
        val productor = (_productores.value as? EstadoUi.Exito)?.datos?.firstOrNull { it.idProductor == registro.idProductor }
        viewModelScope.launch {
            val resultado = recoleccionRepository.registrar(
                idProductor = registro.idProductor,
                litros = registro.litros,
                observacion = registro.observacion,
                nombreProductor = productor?.nombreCompleto,
                zona = productor?.comunidad
            )
            _envio.value = when (resultado) {
                is ApiResult.Exito -> {
                    val guardado = resultado.datos
                    if (guardado.esLocal) {
                        EstadoEnvio.Exito(
                            "Sin conexión: la recolección de ${guardado.litrosFormateados} se guardó en el teléfono " +
                                "y se enviará sola cuando haya internet."
                        )
                    } else {
                        val actuales = (_delServidor.value as? EstadoUi.Exito)?.datos.orEmpty()
                        _delServidor.value = EstadoUi.Exito(listOf(guardado) + actuales)
                        EstadoEnvio.Exito("Recolección de ${guardado.litrosFormateados} registrada correctamente.")
                    }
                }
                is ApiResult.Error -> EstadoEnvio.Error(resultado.mensaje)
            }
        }
    }

    /** Vuelve a enviar una recolección que el servidor rechazó. */
    fun reintentar(registro: RegistroAcopio) {
        recoleccionRepository.reintentar(registro)
        viewModelScope.launch { sincronizador.sincronizar() }
    }

    /** Elimina del teléfono una recolección que no se pudo sincronizar. */
    fun descartar(registro: RegistroAcopio) {
        recoleccionRepository.descartar(registro)
    }

    fun limpiarEnvio() {
        _envio.value = EstadoEnvio.Inactivo
    }
}
