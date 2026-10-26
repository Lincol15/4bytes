package com.example.acopiodeleche.ui.pantallas.productor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acopiodeleche.domain.model.ControlCalidad
import com.example.acopiodeleche.domain.model.EstadoPago
import com.example.acopiodeleche.domain.model.ResultadoCalidad
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.ui.pantallas.productor.QuejasProductorScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.acopiodeleche.domain.model.Notificacion
import com.example.acopiodeleche.domain.model.Productor
import com.example.acopiodeleche.ui.estado.CargaOError
import com.example.acopiodeleche.ui.estado.EstadoUi
import com.example.acopiodeleche.ui.estado.datosOVacio
import kotlinx.coroutines.launch
import com.example.acopiodeleche.ui.tema.EsqueletoRol
import com.example.acopiodeleche.ui.tema.HuataColores
import com.example.acopiodeleche.ui.tema.OpcionMenu
import com.example.acopiodeleche.ui.tema.TarjetaIndicador
import com.example.acopiodeleche.ui.tema.tarjetaHuata
import org.koin.compose.viewmodel.koinViewModel

private val VerdeHuata   = HuataColores.Verde
private val VerdeOscuro  = HuataColores.VerdeOscuro
private val AzulHuata    = HuataColores.Azul
private val AmbarHuata   = HuataColores.DoradoOscuro


@Composable
fun ProductorHomeScreen(
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductorViewModel = koinViewModel()
) {
    val usuario     = SesionActual.usuario
    val idProductor = usuario?.idProductor ?: ""
    // Datos del perfil tomados de la sesión (Laravel los devuelve al iniciar sesión)
    val productor   = usuario?.let {
        Productor(idProductor, it.nombres, it.apellidos, it.dni, it.telefono, "", it.comunidad.orEmpty(), it.estado)
    }

    // Laravel ya filtra por el productor autenticado
    val estadoEntregas       by viewModel.entregas.collectAsStateWithLifecycle()
    val estadoPagos          by viewModel.pagos.collectAsStateWithLifecycle()
    val estadoAnalisis       by viewModel.analisis.collectAsStateWithLifecycle()
    val estadoNotificaciones by viewModel.notificaciones.collectAsStateWithLifecycle()
    val misEntregas        = estadoEntregas.datosOVacio()
    val misPagos           = estadoPagos.datosOVacio()
    val misNotificaciones  = estadoNotificaciones.datosOVacio()
    val misAnalisis        = estadoAnalisis.datosOVacio()
    val noLeidas           = misNotificaciones.count { !it.leida }
    val pagosPendientes    = misPagos.count { it.estado == EstadoPago.PENDIENTE }

    val menuItems = listOf(
        OpcionMenu("🏠", "Inicio"),
        OpcionMenu("🥛", "Mis entregas", "Mi cuenta"),
        OpcionMenu("💳", "Mis pagos", "Mi cuenta", insignia = pagosPendientes),
        OpcionMenu("🔬", "Calidad", "Mi cuenta"),
        OpcionMenu("📣", "Quejas", "Mi cuenta"),
        OpcionMenu("🔔", "Notificaciones", "Mi cuenta", insignia = noLeidas)
    )

    var seccionActual by remember { mutableStateOf(0) }

    EsqueletoRol(
        opciones = menuItems,
        seleccion = seccionActual,
        onSeleccionar = { seccionActual = it },
        onCerrarSesion = onCerrarSesion,
        subtitulo = if (seccionActual == 0) "Bienvenido, ${usuario?.nombres.orEmpty()}" else null,
        modifier = modifier
    ) {
        when (seccionActual) {
            0 -> ResumenProductor(
                productor        = productor,
                totalEntregas    = misEntregas.size,
                totalLitros      = misEntregas.sumOf { it.litros },
                pagosPendientes  = pagosPendientes,
                noLeidas         = noLeidas
            )
            1 -> if (estadoEntregas is EstadoUi.Exito) EntregasProductorTab(entregas = misEntregas)
                 else CargaOError(estadoEntregas, onReintentar = viewModel::cargar)
            2 -> if (estadoPagos is EstadoUi.Exito) PagosProductorTab(pagos = misPagos)
                 else CargaOError(estadoPagos, onReintentar = viewModel::cargar)
            3 -> if (estadoAnalisis is EstadoUi.Exito) CalidadProductorTab(analisis = misAnalisis)
                 else CargaOError(estadoAnalisis, onReintentar = viewModel::cargar)
            4 -> QuejasProductorScreen()
            5 -> if (estadoNotificaciones is EstadoUi.Exito) {
                     NotificacionesTab(notificaciones = misNotificaciones, onLeer = viewModel::marcarLeida)
                 } else {
                     CargaOError(estadoNotificaciones, onReintentar = viewModel::cargar)
                 }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// INICIO / RESUMEN
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ResumenProductor(
    productor: com.example.acopiodeleche.domain.model.Productor?,
    totalEntregas: Int,
    totalLitros: Double,
    pagosPendientes: Int,
    noLeidas: Int
) {
    LazyColumn(
        contentPadding     = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner de bienvenida (tarjeta blanca de la web)
        item {
            Box(Modifier.fillMaxWidth().tarjetaHuata()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(VerdeHuata.copy(0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            (productor?.nombres ?: "P").take(1).uppercase(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = VerdeHuata
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(
                            "Hola, ${productor?.nombres ?: "Productor"} 👋",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${productor?.comunidad ?: ""} · Ecolácteos Huata",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Tarjetas de resumen
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TarjetaKPI("🥛", "$totalEntregas", "Entregas", VerdeHuata, Modifier.weight(1f))
                TarjetaKPI("📦", "${totalLitros}L", "Litros", AzulHuata, Modifier.weight(1f))
                TarjetaKPI("⏳", "$pagosPendientes", "Pagos pend.", HuataColores.NaranjaOscuro, Modifier.weight(1f))
            }
        }

        // Precio vigente
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AmbarHuata.copy(0.12f))
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💰", fontSize = 28.sp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "Precio vigente",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "S/ 1.70 / litro",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF7B5E00)
                            )
                        }
                    }
                    Text(
                        "Vigente",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7B5E00),
                        modifier = Modifier
                            .background(AmbarHuata.copy(0.3f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Estimado semanal
        if (totalLitros > 0) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AzulHuata.copy(0.08f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "💳 Estimado a cobrar",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(6.dp))
                        val estimado = totalLitros * 1.70
                        val ent = estimado.toLong()
                        val dec = ((estimado - ent) * 100).toLong().toString().padStart(2, '0')
                        Text(
                            "S/ $ent.$dec",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = AzulHuata
                        )
                        Text(
                            "Basado en $totalLitros L × S/ 1.70",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaKPI(icono: String, valor: String, label: String, color: Color, modifier: Modifier) =
    TarjetaIndicador(icono, valor, label, color, modifier)

// ─────────────────────────────────────────────────────────────────────────────
// ENTREGAS
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EntregasProductorTab(entregas: List<com.example.acopiodeleche.domain.model.RegistroAcopio>) {
    if (entregas.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🥛", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text("No hay entregas registradas", style = MaterialTheme.typography.titleMedium)
            }
        }
        return
    }
    LazyColumn(
        contentPadding     = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TarjetaKPI("📋", "${entregas.size}", "Total entregas", VerdeHuata, Modifier.weight(1f))
                TarjetaKPI("📦", "${entregas.sumOf { it.litros }}L", "Litros totales", AzulHuata, Modifier.weight(1f))
            }
        }
        items(entregas) { entrega ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(VerdeHuata.copy(0.12f)),
                        contentAlignment = Alignment.Center
                    ) { Text("🥛", fontSize = 20.sp) }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "${entrega.fecha}  ·  ${entrega.hora}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Acopiador: ${entrega.acopiador}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (!entrega.observacion.isNullOrBlank()) {
                            Text(
                                "📝 ${entrega.observacion}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            entrega.litrosFormateados,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = VerdeHuata
                        )
                        val gananc = entrega.litros * 1.70
                        val ent = gananc.toLong()
                        val dec = ((gananc - ent) * 100).toLong().toString().padStart(2, '0')
                        Text(
                            "S/ $ent.$dec",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PAGOS
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PagosProductorTab(pagos: List<com.example.acopiodeleche.domain.model.Pago>) {
    if (pagos.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("💳", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text("No hay pagos registrados", style = MaterialTheme.typography.titleMedium)
            }
        }
        return
    }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(pagos) { pago ->
            val colorEstado = when (pago.estado) {
                EstadoPago.PAGADO    -> VerdeHuata
                EstadoPago.PENDIENTE -> HuataColores.NaranjaOscuro
                else                 -> Color.Gray
            }
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Código: ${pago.codigoPago}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("${pago.periodoDesde} — ${pago.periodoHasta}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Box(modifier = Modifier.background(colorEstado.copy(0.15f), RoundedCornerShape(8.dp)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                            Text(pago.estado.etiqueta, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = colorEstado)
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Litros:", style = MaterialTheme.typography.bodySmall)
                        Text(pago.litrosFormateados, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Precio/litro:", style = MaterialTheme.typography.bodySmall)
                        Text(pago.precioPorLitroFormateado, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("TOTAL:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold)
                        val t = pago.totalCalculado
                        val ent = t.toLong()
                        val dec = ((t - ent) * 100).toLong().toString().padStart(2, '0')
                        Text("S/ $ent.$dec", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = VerdeHuata)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CALIDAD
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CalidadProductorTab(analisis: List<ControlCalidad>) {
    if (analisis.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🔬", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text("Sin análisis de calidad", style = MaterialTheme.typography.titleMedium)
                Text("El personal registrará los análisis de tu leche", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(analisis) { cal ->
            val colorR = when (cal.resultado) {
                ResultadoCalidad.APTO      -> VerdeHuata
                ResultadoCalidad.NO_APTO   -> Color.Red
                ResultadoCalidad.OBSERVADO -> HuataColores.DoradoOscuro
                ResultadoCalidad.PENDIENTE -> Color.Gray
            }
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(cal.codigoAnalisis, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("${cal.fecha} · ${cal.hora}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Box(modifier = Modifier.background(colorR.copy(0.15f), RoundedCornerShape(8.dp)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                            Text(cal.resultado.etiqueta, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = colorR)
                        }
                    }
                    if (cal.grasa != null || cal.ph != null) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            if (cal.grasa != null) ParamChip("Grasa", "${cal.grasa}%")
                            if (cal.sng != null)   ParamChip("SNG",   "${cal.sng}%")
                            if (cal.ph != null)    ParamChip("pH",    "${cal.ph}")
                            if (cal.densidad != null) ParamChip("Densidad", "${cal.densidad}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ParamChip(label: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// NOTIFICACIONES
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun NotificacionesTab(notificaciones: List<Notificacion>, onLeer: (Notificacion) -> Unit) {
    if (notificaciones.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🔔", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text("Sin notificaciones", style = MaterialTheme.typography.titleMedium)
            }
        }
        return
    }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(notificaciones) { notif ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onLeer(notif) },
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(if (!notif.leida) 3.dp else 1.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (!notif.leida) VerdeHuata.copy(0.06f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(VerdeHuata.copy(0.12f)),
                        contentAlignment = Alignment.Center
                    ) { Text(notif.tipo.icono, fontSize = 22.sp) }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                notif.titulo,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (!notif.leida) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )
                            if (!notif.leida) {
                                Box(modifier = Modifier.size(8.dp).background(VerdeHuata, CircleShape))
                            }
                        }
                        Text(notif.mensaje, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${notif.fecha}  ${notif.hora}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
