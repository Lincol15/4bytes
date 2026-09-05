package com.example.acopiodeleche

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acopiodeleche.domain.model.Rol
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.domain.model.Usuario
import com.example.acopiodeleche.ui.auth.LoginScreen
import com.example.acopiodeleche.ui.pantallas.acopio.AcopioScreen
import com.example.acopiodeleche.ui.pantallas.acopiador.AcopiadorHomeScreen
import com.example.acopiodeleche.ui.pantallas.admin.AdminHomeScreen
import com.example.acopiodeleche.ui.pantallas.calidad.CalidadHomeScreen
import com.example.acopiodeleche.ui.pantallas.productor.ProductorHomeScreen
import com.example.acopiodeleche.ui.pantallas.productores.ProductoresScreen

@Composable
fun App() {
    MaterialTheme {
        // Estado de sesión — null = no logueado
        var sesion by remember { mutableStateOf<Usuario?>(null) }

        if (sesion == null) {
            // ── Pantalla de Login ──────────────────────────────────
            LoginScreen(
                onLoginExitoso = { usuario -> sesion = usuario }
            )
        } else {
            // ── Navegar según el rol ───────────────────────────────
            val cerrarSesion: () -> Unit = {
                SesionActual.cerrar()
                sesion = null
            }

            when (sesion!!.rol) {
                Rol.ADMINISTRADOR -> AdminHomeScreen(onCerrarSesion = cerrarSesion)
                Rol.GERENTE       -> GerenteHomeScreen(onCerrarSesion = cerrarSesion)
                Rol.ACOPIADOR     -> AcopiadorHomeScreen(onCerrarSesion = cerrarSesion)
                Rol.PRODUCTOR     -> ProductorHomeScreen(onCerrarSesion = cerrarSesion)
                Rol.CONTROL_CALIDAD -> CalidadHomeScreen(onCerrarSesion = cerrarSesion)
                Rol.TRABAJADOR_PLANTA -> TrabajadorHomeScreen(onCerrarSesion = cerrarSesion)
            }
        }
    }
}

// ── Pantalla Gerente (simplificada — se expande en Fase 10) ───────────────
private val VerdeHuata = Color(0xFF2E7D32)
private val AzulGerente = Color(0xFF1A237E)

@Composable
fun GerenteHomeScreen(
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val usuario = SesionActual.usuario
    var tabActual by remember { mutableStateOf(0) }
    val tabs = listOf("Dashboard", "Productores", "Acopio")

    Column(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AzulGerente)
                .padding(16.dp)
        ) {
            Column {
                Text("Dashboard Gerente", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                Text("Ecolácteos Huata · ${usuario?.nombreCompleto ?: "Gerente"}", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
            }
            Text(
                text = "Salir",
                modifier = Modifier.align(Alignment.TopEnd).clickable { onCerrarSesion() }.padding(4.dp),
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.labelMedium
            )
        }

        TabRow(selectedTabIndex = tabActual) {
            tabs.forEachIndexed { i, t ->
                Tab(selected = tabActual == i, onClick = { tabActual = i }, text = { Text(t) })
            }
        }

        when (tabActual) {
            0 -> DashboardGerente()
            1 -> ProductoresScreen(soloLectura = true)
            2 -> AcopioScreen()
        }
    }
}

@Composable
private fun DashboardGerente() {
    val registros = com.example.acopiodeleche.domain.model.DatosMock.registrosAcopio
    val productores = com.example.acopiodeleche.domain.model.DatosMock.productores

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Resumen del negocio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaGerente("🥛", "${registros.sumOf { it.litros }} L", "Total litros acopiados", AzulGerente, Modifier.weight(1f))
                TarjetaGerente("👥", "${productores.count { it.estado }}", "Productores activos", VerdeHuata, Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaGerente("📦", "${registros.size}", "Registros de acopio", Color(0xFF6A1B9A), Modifier.weight(1f))
                TarjetaGerente("💰", "S/ 1.70", "Precio actual/L", Color(0xFFE65100), Modifier.weight(1f))
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Módulos próximos", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    listOf("📋 Recepción en planta", "🔬 Control de calidad", "💰 Gestión de pagos",
                        "🏭 Producción (queso/yogur)", "📊 Reportes y estadísticas").forEach {
                        Text("• $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaGerente(icono: String, valor: String, etiqueta: String, color: Color, modifier: Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icono, fontSize = 26.sp)
            Text(valor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
            Text(etiqueta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// ── Pantalla Trabajador de Planta ─────────────────────────────────────────
@Composable
fun TrabajadorHomeScreen(
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val usuario = SesionActual.usuario
    Column(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF37474F))
                .padding(16.dp)
        ) {
            Column {
                Text("Trabajador de Planta", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                Text("Ecolácteos Huata · ${usuario?.nombreCompleto ?: ""}", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
            }
            Text(
                text = "Salir",
                modifier = Modifier.align(Alignment.TopEnd).clickable { onCerrarSesion() }.padding(4.dp),
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.labelMedium
            )
        }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🏭", fontSize = 64.sp)
                Spacer(Modifier.height(16.dp))
                Text("Módulo en desarrollo", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Recepción, Producción e Inventario\nestarán disponibles próximamente",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
