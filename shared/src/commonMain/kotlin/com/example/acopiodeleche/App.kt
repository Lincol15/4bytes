package com.example.acopiodeleche

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.example.acopiodeleche.data.AppContainer
import com.example.acopiodeleche.domain.model.Rol
import com.example.acopiodeleche.domain.model.Usuario
import com.example.acopiodeleche.ui.auth.LoginScreen
import com.example.acopiodeleche.ui.pantallas.acopiador.AcopiadorHomeScreen
import com.example.acopiodeleche.ui.pantallas.admin.AdminHomeScreen
import com.example.acopiodeleche.ui.pantallas.calidad.CalidadHomeScreen
import com.example.acopiodeleche.ui.pantallas.gerente.GerenteHomeScreen
import com.example.acopiodeleche.ui.pantallas.planta.TrabajadorPlantaScreen
import com.example.acopiodeleche.ui.pantallas.productor.ProductorHomeScreen
import kotlinx.coroutines.launch

@Composable
fun App() {
    MaterialTheme {
        var sesion by remember { mutableStateOf<Usuario?>(null) }
        val scope = rememberCoroutineScope()

        AlcanceDeSesion(clave = sesion?.id) {
            if (sesion == null) {
                LoginScreen(onLoginExitoso = { usuario -> sesion = usuario })
            } else {
                val cerrarSesion: () -> Unit = {
                    sesion = null
                    // Revoca el token en Laravel y limpia la sesión local.
                    scope.launch { AppContainer.authRepository.cerrarSesion() }
                }
                when (sesion!!.rol) {
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
