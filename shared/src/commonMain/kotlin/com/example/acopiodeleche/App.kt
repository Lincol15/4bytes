package com.example.acopiodeleche

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.acopiodeleche.domain.model.Rol
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.domain.model.Usuario
import com.example.acopiodeleche.ui.auth.LoginScreen
import com.example.acopiodeleche.ui.pantallas.acopiador.AcopiadorHomeScreen
import com.example.acopiodeleche.ui.pantallas.admin.AdminHomeScreen
import com.example.acopiodeleche.ui.pantallas.calidad.CalidadHomeScreen
import com.example.acopiodeleche.ui.pantallas.gerente.GerenteHomeScreen
import com.example.acopiodeleche.ui.pantallas.planta.TrabajadorPlantaScreen
import com.example.acopiodeleche.ui.pantallas.productor.ProductorHomeScreen

@Composable
fun App() {
    MaterialTheme {
        var sesion by remember { mutableStateOf<Usuario?>(null) }

        if (sesion == null) {
            LoginScreen(onLoginExitoso = { usuario -> sesion = usuario })
        } else {
            val cerrarSesion: () -> Unit = {
                SesionActual.cerrar()
                sesion = null
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
