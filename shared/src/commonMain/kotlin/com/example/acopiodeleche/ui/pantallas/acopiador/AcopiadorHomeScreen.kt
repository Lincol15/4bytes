package com.example.acopiodeleche.ui.pantallas.acopiador

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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.acopiodeleche.domain.model.DatosMock
import com.example.acopiodeleche.domain.model.RegistroAcopio
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.ui.pantallas.acopio.AcopioScreen
import com.example.acopiodeleche.ui.pantallas.productores.ProductoresScreen

private val VerdeHuata = Color(0xFF2E7D32)
private val AzulHuata  = Color(0xFF1565C0)

@Composable
fun AcopiadorHomeScreen(
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val usuario = SesionActual.usuario
    var pantallaActual by remember { mutableStateOf(0) }
    // 0=inicio, 1=registrar, 2=mis productores, 3=historial jornada, 4=mapa

    when (pantallaActual) {
        1 -> AcopioScreen(onVolver = { pantallaActual = 0 })
        2 -> MisProductoresScreen(onVolver = { pantallaActual = 0 })
        3 -> HistorialJornadaScreen(onVolver = { pantallaActual = 0 })
        4 -> MapaAcopiadorScreen(onVolver = { pantallaActual = 0 })
        else -> {
            // Registros SOLO de este acopiador
            val misRegistros = DatosMock.registrosAcopio.filter { r ->
                r.acopiador == usuario?.nombreCompleto
            }
            val litrosTotales = misRegistros.sumOf { it.litros }

            Column(modifier = modifier.fillMaxSize()) {

                // ── Encabezado ───────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VerdeHuata)
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            "Hola, ${usuario?.nombres ?: "Acopiador"} 👋",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "📍 ${usuario?.comunidad ?: "Sin comunidad"} · 🚛 ${usuario?.vehiculo ?: "Sin vehículo"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                    Text(
                        "Salir",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clickable { onCerrarSesion() }
                            .padding(4.dp),
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                // ── Resumen jornada ──────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TarjetaResumen(
                        icono    = "🥛",
                        valor    = "${misRegistros.size}",
                        etiqueta = "Entregas hoy",
                        color    = VerdeHuata,
                        modifier = Modifier.weight(1f)
                    )
                    TarjetaResumen(
                        icono    = "📦",
                        valor    = "$litrosTotales L",
                        etiqueta = "Litros hoy",
                        color    = AzulHuata,
                        modifier = Modifier.weight(1f)
                    )
                }

                HorizontalDivider()

                LazyColumn(
                    contentPadding     = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ── Menú de acciones ─────────────────────────────────
                    item {
                        Text(
                            "Acciones",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    item {
                        MenuItemAcopiador(
                            icono       = "➕",
                            titulo      = "Registrar acopio",
                            descripcion = "Nueva entrega de leche",
                            color       = VerdeHuata
                        ) { pantallaActual = 1 }
                    }
                    item {
                        MenuItemAcopiador(
                            icono       = "👥",
                            titulo      = "Mis productores",
                            descripcion = "Ver lista de mis productores asignados",
                            color       = AzulHuata
                        ) { pantallaActual = 2 }
                    }
                    item {
                        MenuItemAcopiador(
                            icono       = "📋",
                            titulo      = "Historial de jornada",
                            descripcion = "Ver mis registros de hoy",
                            color       = Color(0xFF6A1B9A)
                        ) { pantallaActual = 3 }
                    }
                    item {
                        MenuItemAcopiador(
                            icono       = "🗺️",
                            titulo      = "Mapa de ruta",
                            descripcion = "Ver ruta y puntos de recolección",
                            color       = Color(0xFF00838F)
                        ) { pantallaActual = 4 }
                    }

                    // ── Últimas 3 entregas ───────────────────────────────
                    item {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Últimas entregas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (misRegistros.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🥛", fontSize = 36.sp)
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        "Sin entregas registradas aún",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        "Toca \"Registrar acopio\" para empezar",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(misRegistros.takeLast(3).reversed()) { registro ->
                            TarjetaRegistroMini(registro)
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PANTALLA: Mis productores asignados
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun MisProductoresScreen(onVolver: () -> Unit) {
    val usuario     = SesionActual.usuario
    val asignacion  = DatosMock.asignaciones.find { it.idAcopiador == usuario?.id }
    val misProductores = if (asignacion != null) {
        DatosMock.productores.filter { p ->
            asignacion.idsProductores.contains(p.idProductor)
        }
    } else {
        DatosMock.productores.filter { p ->
            p.comunidad == usuario?.comunidad
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Encabezado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AzulHuata)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onVolver) {
                    Text("←", color = Color.White, fontSize = 20.sp)
                }
                Spacer(Modifier.width(4.dp))
                Column {
                    Text(
                        "Mis productores",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${misProductores.size} asignados · ${usuario?.comunidad ?: ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        if (misProductores.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("👥", fontSize = 48.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("No tienes productores asignados", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Contacta al administrador para configurar tu zona",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding     = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(misProductores) { p ->
                    // Cuántos litros registró este productor hoy con este acopiador
                    val litrosProductor = DatosMock.registrosAcopio
                        .filter { r ->
                            r.idProductor == p.idProductor &&
                            r.acopiador == SesionActual.usuario?.nombreCompleto
                        }
                        .sumOf { it.litros }

                    Card(
                        modifier  = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(VerdeHuata.copy(0.12f), RoundedCornerShape(50)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🌾", fontSize = 22.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    p.nombreCompleto,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "${p.comunidad}  •  ${p.telefono}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                if (litrosProductor > 0) {
                                    Text(
                                        "$litrosProductor L",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = VerdeHuata
                                    )
                                    Text(
                                        "hoy",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    Text(
                                        "Sin entrega",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PANTALLA: Historial de jornada
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun HistorialJornadaScreen(onVolver: () -> Unit) {
    val usuario   = SesionActual.usuario
    val registros = DatosMock.registrosAcopio.filter { r ->
        r.acopiador == usuario?.nombreCompleto
    }
    val litrosTotales = registros.sumOf { it.litros }
    val recibidos     = registros.count { it.recibido }

    Column(modifier = Modifier.fillMaxSize()) {
        // Encabezado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF6A1B9A))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onVolver) {
                    Text("←", color = Color.White, fontSize = 20.sp)
                }
                Spacer(Modifier.width(4.dp))
                Column {
                    Text(
                        "Historial de jornada",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${registros.size} entregas · $litrosTotales L total",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        if (registros.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📋", fontSize = 48.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "No hay registros en tu jornada",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Ve a \"Registrar acopio\" para comenzar",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            return
        }

        LazyColumn(
            contentPadding     = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Resumen
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TarjetaResumen("🥛", "${registros.size}", "Entregas", Color(0xFF6A1B9A), Modifier.weight(1f))
                    TarjetaResumen("📦", "$litrosTotales L", "Litros", VerdeHuata, Modifier.weight(1f))
                    TarjetaResumen("✅", "$recibidos", "Recibidos", AzulHuata, Modifier.weight(1f))
                }
            }

            item {
                HorizontalDivider()
                Spacer(Modifier.height(4.dp))
                Text(
                    "Detalle de entregas",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            items(registros.reversed()) { registro ->
                TarjetaRegistroDetalle(registro)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTES COMPARTIDOS
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TarjetaRegistroMini(registro: RegistroAcopio) {
    val productor = DatosMock.productores.find { it.idProductor == registro.idProductor }
    Card(
        modifier  = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🥛", fontSize = 22.sp)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    productor?.nombreCompleto ?: "Productor",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${registro.fecha} ${registro.hora} · ${registro.zona}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                registro.litrosFormateados,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = VerdeHuata
            )
        }
    }
}

@Composable
private fun TarjetaRegistroDetalle(registro: RegistroAcopio) {
    val productor = DatosMock.productores.find { it.idProductor == registro.idProductor }
    val colorEstado = if (registro.recibido) VerdeHuata else Color(0xFFE65100)

    Card(
        modifier  = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🥛", fontSize = 22.sp)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            productor?.nombreCompleto ?: "Productor",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${registro.fecha} · ${registro.hora}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        registro.litrosFormateados,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = VerdeHuata
                    )
                    Text(
                        if (registro.recibido) "✅ Recibido" else "⏳ En tránsito",
                        style = MaterialTheme.typography.labelSmall,
                        color = colorEstado
                    )
                }
            }
            if (!registro.observacion.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "📝 ${registro.observacion}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun TarjetaResumen(
    icono: String,
    valor: String,
    etiqueta: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors   = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape    = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icono, fontSize = 24.sp)
            Spacer(Modifier.height(2.dp))
            Text(valor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
            Text(etiqueta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MenuItemAcopiador(
    icono: String,
    titulo: String,
    descripcion: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp),
        shape     = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) { Text(icono, fontSize = 24.sp) }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(descripcion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("›", fontSize = 24.sp, color = color)
        }
    }
}
