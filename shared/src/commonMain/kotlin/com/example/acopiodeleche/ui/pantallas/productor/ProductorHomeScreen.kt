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
import com.example.acopiodeleche.domain.model.DatosMock
import com.example.acopiodeleche.domain.model.EstadoPago
import com.example.acopiodeleche.domain.model.ResultadoCalidad
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.ui.pantallas.productor.QuejasProductorScreen
import kotlinx.coroutines.launch

private val VerdeHuata   = Color(0xFF2E7D32)
private val VerdeOscuro  = Color(0xFF1B5E20)
private val AzulHuata    = Color(0xFF1565C0)
private val AmbarHuata   = Color(0xFFF9A825)

private data class ItemMenuProductor(val icono: String, val titulo: String, val indice: Int, val badge: Int = 0)

@Composable
fun ProductorHomeScreen(
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val usuario     = SesionActual.usuario
    val idProductor = usuario?.idProductor ?: ""
    val productor   = DatosMock.productores.find { it.idProductor == idProductor }

    val misEntregas        = DatosMock.registrosAcopio.filter { it.idProductor == idProductor }
    val misPagos           = DatosMock.pagos.filter { it.idProductor == idProductor }
    val misNotificaciones  = DatosMock.notificaciones
    val misAnalisis        = DatosMock.analisisCalidad.filter { it.idProductor == idProductor }
    val noLeidas           = misNotificaciones.count { !it.leida }
    val pagosPendientes    = misPagos.count { it.estado == EstadoPago.PENDIENTE }

    val menuItems = listOf(
        ItemMenuProductor("🏠", "Inicio",         0),
        ItemMenuProductor("🥛", "Mis entregas",   1, misEntregas.size),
        ItemMenuProductor("💳", "Mis pagos",      2, pagosPendientes),
        ItemMenuProductor("🔬", "Calidad",        3, misAnalisis.size),
        ItemMenuProductor("📣", "Quejas",         4),
        ItemMenuProductor("🔔", "Notificaciones", 5, noLeidas)
    )

    var seccionActual by remember { mutableStateOf(0) }
    val drawerState   = rememberDrawerState(DrawerValue.Closed)
    val scope         = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(270.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                // ── Cabecera del drawer ──────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(listOf(VerdeOscuro, VerdeHuata))
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        // Avatar con inicial
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                (productor?.nombres ?: usuario?.nombres ?: "P").take(1).uppercase(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            productor?.nombreCompleto ?: usuario?.nombreCompleto ?: "Productor",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "📍 ${productor?.comunidad ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(0.8f)
                        )
                        Text(
                            "DNI: ${productor?.dni ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(0.7f)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // ── Ítems de navegación ──────────────────────────────────
                menuItems.forEach { item ->
                    val seleccionado = seccionActual == item.indice
                    NavigationDrawerItem(
                        icon  = { Text(item.icono, fontSize = 18.sp) },
                        label = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    item.titulo,
                                    fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
                                )
                                if (item.badge > 0) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .background(
                                                if (item.indice == 5) Color.Red else VerdeHuata,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "${item.badge}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        },
                        selected = seleccionado,
                        onClick  = {
                            seccionActual = item.indice
                            scope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = VerdeHuata.copy(0.12f),
                            selectedTextColor      = VerdeHuata,
                            selectedIconColor      = VerdeHuata
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                    )
                }

                Spacer(Modifier.weight(1f))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
                NavigationDrawerItem(
                    icon  = { Text("🚪", fontSize = 18.sp) },
                    label = { Text("Cerrar sesión", color = Color(0xFFB71C1C)) },
                    selected = false,
                    onClick  = {
                        scope.launch { drawerState.close() }
                        onCerrarSesion()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
                Spacer(Modifier.height(12.dp))
            }
        },
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── TopBar ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(VerdeOscuro, VerdeHuata)))
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                // Botón hamburguesa
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(44.dp)
                        .clickable { scope.launch { drawerState.open() } }
                        .background(Color.White.copy(0.15f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        repeat(3) {
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(2.dp)
                                    .background(Color.White, RoundedCornerShape(1.dp))
                            )
                        }
                    }
                }

                // Título sección
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 56.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val item = menuItems.find { it.indice == seccionActual }
                    Text(
                        "${item?.icono ?: ""} ${item?.titulo ?: "Inicio"}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        productor?.nombreCompleto ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(0.75f)
                    )
                }

                // Badge notificaciones en el top
                if (noLeidas > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                            .size(28.dp)
                            .background(Color.Red, CircleShape)
                            .clickable {
                                seccionActual = 5
                                scope.launch { drawerState.close() }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "$noLeidas",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // ── Contenido ───────────────────────────────────────────────
            Box(modifier = Modifier.fillMaxSize()) {
                when (seccionActual) {
                    0 -> ResumenProductor(
                        productor        = productor,
                        totalEntregas    = misEntregas.size,
                        totalLitros      = misEntregas.sumOf { it.litros },
                        pagosPendientes  = pagosPendientes,
                        noLeidas         = noLeidas
                    )
                    1 -> EntregasProductorTab(entregas = misEntregas)
                    2 -> PagosProductorTab(pagos = misPagos)
                    3 -> CalidadProductorTab(analisis = misAnalisis)
                    4 -> QuejasProductorScreen()
                    5 -> NotificacionesTab(notificaciones = misNotificaciones)
                }
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
        // Banner de bienvenida
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = VerdeHuata.copy(0.08f))
            ) {
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
                TarjetaKPI("⏳", "$pagosPendientes", "Pagos pend.", Color(0xFFE65100), Modifier.weight(1f))
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
private fun TarjetaKPI(icono: String, valor: String, label: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(0.1f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icono, fontSize = 20.sp)
            Spacer(Modifier.height(2.dp))
            Text(valor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

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
        items(entregas.reversed()) { entrega ->
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
                EstadoPago.PENDIENTE -> Color(0xFFE65100)
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
        items(analisis.reversed()) { cal ->
            val colorR = when (cal.resultado) {
                ResultadoCalidad.APTO      -> VerdeHuata
                ResultadoCalidad.NO_APTO   -> Color.Red
                ResultadoCalidad.OBSERVADO -> Color(0xFFF9A825)
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
private fun NotificacionesTab(notificaciones: List<com.example.acopiodeleche.domain.model.Notificacion>) {
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
                modifier = Modifier.fillMaxWidth(),
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
