package com.example.acopiodeleche

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.example.acopiodeleche.data.remote.EstadoConexion
import com.example.acopiodeleche.data.repository.AuthRepository
import com.example.acopiodeleche.data.repository.RecoleccionRepository
import com.example.acopiodeleche.data.sync.Sincronizador
import com.example.acopiodeleche.domain.model.EstadoSync
import com.example.acopiodeleche.domain.model.Rol
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.ui.auth.LoginScreen
import com.example.acopiodeleche.ui.estado.AvisoConexion
import com.example.acopiodeleche.ui.estado.textoAvisoConexion
import com.example.acopiodeleche.ui.pantallas.acopiador.AcopiadorHomeScreen
import com.example.acopiodeleche.ui.pantallas.admin.AdminHomeScreen
import com.example.acopiodeleche.ui.pantallas.calidad.CalidadHomeScreen
import com.example.acopiodeleche.ui.pantallas.gerente.GerenteHomeScreen
import com.example.acopiodeleche.ui.pantallas.planta.TrabajadorPlantaScreen
import com.example.acopiodeleche.ui.pantallas.productor.ProductorHomeScreen
import com.example.acopiodeleche.ui.tema.TemaHuata
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun App() {
    TemaHuata {
        // La sesión vive en SesionActual: la abre el login, la restaura la base local al iniciar
        // y la cierra el usuario o el servidor (refresh token vencido).
        val sesion by SesionActual.estado.collectAsState()
        val sesionExpirada by SesionActual.sesionExpirada.collectAsState()
        // Dependencias de Koin (módulos en di/Modulos.kt)
        val estadoConexion = koinInject<EstadoConexion>()
        val recoleccionRepository = koinInject<RecoleccionRepository>()
        val sincronizador = koinInject<Sincronizador>()
        val authRepository = koinInject<AuthRepository>()

        val servidorResponde by estadoConexion.enLinea.collectAsState()
        val hayRed by estadoConexion.hayRed.collectAsState()
        val idUsuario = sesion?.usuario?.id
        val locales by remember(idUsuario) { recoleccionRepository.pendientes() }
            .collectAsState(emptyList())
        val scope = rememberCoroutineScope()

        // Mientras haya sesión: sube lo pendiente y llena la base local, periódicamente y al volver la red.
        LaunchedEffect(idUsuario) {
            if (idUsuario != null) sincronizador.ejecutarMientrasHaySesion()
        }

        val aviso = textoAvisoConexion(
            enLinea = hayRed && servidorResponde,
            pendientes = locales.count { it.estadoSync == EstadoSync.PENDIENTE },
            rechazadas = locales.count { it.estadoSync == EstadoSync.ERROR },
            sesionExpirada = sesion == null && sesionExpirada
        )

        Column(Modifier.fillMaxSize()) {
            if (aviso != null) {
                AvisoConexion(aviso, esAlerta = sesionExpirada || locales.any { it.estadoSync == EstadoSync.ERROR })
            }
            // El aviso ya ocupa la barra de estado: las pantallas no vuelven a dejar ese espacio.
            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .then(if (aviso != null) Modifier.consumeWindowInsets(WindowInsets.statusBars) else Modifier)
            ) {
                AlcanceDeSesion(clave = idUsuario) {
                    val actual = sesion
                    if (actual == null) {
                        LoginScreen(onLoginExitoso = { /* AuthRepository ya abrió la sesión */ })
                    } else {
                        val cerrarSesion: () -> Unit = {
                            // Revoca los tokens en Laravel y limpia la sesión local.
                            scope.launch { authRepository.cerrarSesion() }
                        }
                        when (actual.usuario.rol) {
                            Rol.ADMINISTRADOR     -> AdminHomeScreen(onCerrarSesion = cerrarSesion)
                            Rol.GERENTE           -> GerenteHomeScreen(onCerrarSesion = cerrarSesion)
                            Rol.ACOPIADOR         -> AcopiadorHomeScreen(onCerrarSesion = cerrarSesion)
                            Rol.PRODUCTOR         -> ProductorHomeScreen(onCerrarSesion = cerrarSesion)
                            Rol.CONTROL_CALIDAD   -> CalidadHomeScreen(onCerrarSesion = cerrarSesion)
                            Rol.TRABAJADOR_PLANTA -> TrabajadorPlantaScreen(onCerrarSesion = cerrarSesion)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Da a cada sesión su propio ViewModelStore: al iniciar o cerrar sesión se descartan
 * los ViewModels anteriores, para no mostrar datos de otro usuario.
 */
@Composable
private fun AlcanceDeSesion(clave: String?, contenido: @Composable () -> Unit) {
    val propietario = remember(clave) {
        object : ViewModelStoreOwner {
            override val viewModelStore = ViewModelStore()
        }
    }
    DisposableEffect(propietario) {
        onDispose { propietario.viewModelStore.clear() }
    }
    CompositionLocalProvider(LocalViewModelStoreOwner provides propietario, content = contenido)
}
