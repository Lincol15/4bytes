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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.acopiodeleche.domain.model.RegistroAcopio
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.ui.estado.CargaOError
import com.example.acopiodeleche.ui.estado.EstadoUi
import com.example.acopiodeleche.ui.estado.datosOVacio
import com.example.acopiodeleche.ui.estado.fechaHoyPantalla
import com.example.acopiodeleche.ui.pantallas.acopio.AcopioScreen
import com.example.acopiodeleche.ui.pantallas.acopio.RecoleccionViewModel
import com.example.acopiodeleche.ui.tema.EsqueletoRol
import com.example.acopiodeleche.ui.tema.EstadoVacio
import com.example.acopiodeleche.ui.tema.HuataColores
import com.example.acopiodeleche.ui.tema.Insignia
import com.example.acopiodeleche.ui.tema.OpcionMenu
import com.example.acopiodeleche.ui.tema.PanelHuata
import com.example.acopiodeleche.ui.tema.TarjetaEstadistica
import com.example.acopiodeleche.ui.tema.TarjetaIndicador
import com.example.acopiodeleche.ui.tema.Tono
import com.example.acopiodeleche.ui.tema.tarjetaHuata
import org.koin.compose.viewmodel.koinViewModel

private val VerdeHuata = HuataColores.Verde
private val AzulHuata  = HuataColores.Azul

private val MENU_ACOPIADOR = listOf(
    OpcionMenu("🏠", "Inicio"),
    OpcionMenu("➕", "Registrar acopio", "Acopio"),
    OpcionMenu("👥", "Mis productores", "Acopio"),
    OpcionMenu("📋", "Historial de jornada", "Acopio"),
    OpcionMenu("🗺️", "Mis rutas", "Acopio")
)

@Composable
fun AcopiadorHomeScreen(
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val usuario = SesionActual.usuario
    var pantallaActual by remember { mutableStateOf(0) }
    // 0=inicio, 1=registrar, 2=mis productores, 3=historial jornada, 4=mis rutas

    // Recolecciones y productores del acopiador, leídos desde Laravel (compartidos por las subpantallas)
    val recoleccionViewModel: RecoleccionViewModel = koinViewModel()

    EsqueletoRol(
        opciones = MENU_ACOPIADOR,
        seleccion = pantallaActual,
        onSeleccionar = { pantallaActual = it },
        onCerrarSesion = onCerrarSesion,
        subtitulo = "📍 ${usuario?.comunidad ?: "Sin comunidad"} · 🚛 ${usuario?.vehiculo ?: "Sin vehículo"}",
        modifier = modifier
    ) {
        when (pantallaActual) {
            1 -> AcopioScreen(viewModel = recoleccionViewModel)
            2 -> MisProductoresScreen(onVolver = { pantallaActual = 0 }, viewModel = recoleccionViewModel)
            3 -> HistorialJornadaScreen(onVolver = { pantallaActual = 0 }, viewModel = recoleccionViewModel)
            4 -> RutasAcopiadorScreen(onVolver = { pantallaActual = 0 })
            else -> InicioAcopiador(recoleccionViewModel, onIr = { pantallaActual = it })
        }
    }
}

@Composable
private fun InicioAcopiador(viewModel: RecoleccionViewModel, onIr: (Int) -> Unit) {
    val estadoRegistros by viewModel.registros.collectAsStateWithLifecycle()
    // El backend ya devuelve SOLO las recolecciones de este acopiador; aquí se toman las de hoy
    val hoy = fechaHoyPantalla()
    val misRegistros = estadoRegistros.datosOVacio().filter { it.fecha == hoy }
    val litrosTotales = misRegistros.sumOf { it.litros }

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaEstadistica("Entregas hoy", "${misRegistros.size}", "🥛", Tono.VERDE, Modifier.weight(1f))
                TarjetaEstadistica("Litros hoy", "$litrosTotales L", "📦", Tono.AZUL, Modifier.weight(1f))
            }
        }
        item {
            Text("Acciones", style = MaterialTheme.typography.titleMedium)
        }
        item { MenuItemAcopiador("➕", "Registrar acopio", "Nueva entrega de leche", Tono.VERDE) { onIr(1) } }
        item { MenuItemAcopiador("👥", "Mis productores", "Productores asignados y su entrega de hoy", Tono.AZUL) { onIr(2) } }
        item { MenuItemAcopiador("📋", "Historial de jornada", "Todos mis registros de hoy", Tono.MORADO) { onIr(3) } }
        item { MenuItemAcopiador("🗺️", "Mis rutas", "Rutas asignadas y estado de cada parada", Tono.NARANJA) { onIr(4) } }
        item {
            PanelHuata(titulo = "Últimas entregas", icono = "🕒") {
                when {
                    estadoRegistros !is EstadoUi.Exito -> CargaOError(
                        estadoRegistros,
                        onReintentar = viewModel::cargar,
                        modifier = Modifier.height(140.dp)
                    )
                    misRegistros.isEmpty() -> EstadoVacio(
                        "🥛",
                        "Sin entregas registradas aún",
                        "Toca \"Registrar acopio\" para empezar"
                    )
                    else -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        misRegistros.take(3).forEach { TarjetaRegistroMini(it) }
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
fun MisProductoresScreen(onVolver: () -> Unit, viewModel: RecoleccionViewModel) {
    val usuario     = SesionActual.usuario
    // Laravel resuelve la asignación (individual o por comunidad) del acopiador
    val estadoProductores by viewModel.productores.collectAsStateWithLifecycle()
    val estadoRegistros by viewModel.registros.collectAsStateWithLifecycle()
    val misProductores = estadoProductores.datosOVacio()
    val hoy = fechaHoyPantalla()
    val registrosHoy = estadoRegistros.datosOVacio().filter { it.fecha == hoy }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "${misProductores.size} asignados · ${usuario?.comunidad ?: ""}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
        )

        if (estadoProductores !is EstadoUi.Exito) {
            CargaOError(estadoProductores, onReintentar = viewModel::cargar)
        } else if (misProductores.isEmpty()) {
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
                    val litrosProductor = registrosHoy
                        .filter { r -> r.idProductor == p.idProductor }
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
fun HistorialJornadaScreen(onVolver: () -> Unit, viewModel: RecoleccionViewModel) {
    val estadoRegistros by viewModel.registros.collectAsStateWithLifecycle()
    val hoy = fechaHoyPantalla()
    val registros = estadoRegistros.datosOVacio().filter { it.fecha == hoy }
    val litrosTotales = registros.sumOf { it.litros }
    val recibidos     = registros.count { it.recibido }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "${registros.size} entregas · $litrosTotales L en total",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
        )

        if (estadoRegistros !is EstadoUi.Exito) {
            CargaOError(estadoRegistros, onReintentar = viewModel::cargar)
            return
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
                    TarjetaResumen("🥛", "${registros.size}", "Entregas", HuataColores.Morado, Modifier.weight(1f))
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

            items(registros) { registro ->
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
    Box(Modifier.fillMaxWidth().background(HuataColores.Fondo, RoundedCornerShape(12.dp))) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🥛", fontSize = 22.sp)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    registro.nombreProductor ?: "Productor",
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
    Box(Modifier.fillMaxWidth().tarjetaHuata(RoundedCornerShape(14.dp))) {
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
                            registro.nombreProductor ?: "Productor",
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
                    Spacer(Modifier.height(4.dp))
                    Insignia(
                        if (registro.esLocal) "Sin enviar" else if (registro.recibido) "Recibido" else "En tránsito",
                        if (registro.esLocal) Tono.DORADO else if (registro.recibido) Tono.VERDE else Tono.NARANJA
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
) = TarjetaIndicador(icono, valor, etiqueta, color, modifier)

@Composable
private fun MenuItemAcopiador(
    icono: String,
    titulo: String,
    descripcion: String,
    tono: Tono,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .tarjetaHuata(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(tono.fondo, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) { Text(icono, fontSize = 22.sp) }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.titleSmall)
            Text(descripcion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text("›", fontSize = 24.sp, color = tono.texto)
    }
}
