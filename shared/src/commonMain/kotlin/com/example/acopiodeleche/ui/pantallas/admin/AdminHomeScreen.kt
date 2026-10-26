package com.example.acopiodeleche.ui.pantallas.admin

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acopiodeleche.domain.model.ConfiguracionPlanta
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.LaunchedEffect
import com.example.acopiodeleche.domain.model.AjustePlanta
import com.example.acopiodeleche.ui.estado.CargaOError
import com.example.acopiodeleche.ui.estado.EstadoEnvio
import com.example.acopiodeleche.ui.estado.EstadoUi
import com.example.acopiodeleche.ui.estado.datosOVacio
import com.example.acopiodeleche.domain.model.Rol
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.domain.model.Usuario
import com.example.acopiodeleche.ui.components.FilaFechaHora
import kotlinx.coroutines.launch
import com.example.acopiodeleche.ui.tema.AvatarIniciales
import com.example.acopiodeleche.ui.tema.EsqueletoRol
import com.example.acopiodeleche.ui.tema.formatoLitros
import com.example.acopiodeleche.ui.tema.tarjetaHuata
import com.example.acopiodeleche.ui.tema.HuataColores
import com.example.acopiodeleche.ui.tema.Insignia
import com.example.acopiodeleche.ui.tema.OpcionMenu
import com.example.acopiodeleche.ui.tema.PanelHuata
import com.example.acopiodeleche.ui.tema.TarjetaEstadistica
import com.example.acopiodeleche.ui.tema.Tono
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private val VerdeHuata = HuataColores.Verde
private val RojoAdmin  = HuataColores.Verde   // acento del admin: el verde de la web

// ── Menú lateral: mismas secciones que el panel web del administrador ─────

private val MENU_ADMIN = listOf(
    OpcionMenu("🏠", "Dashboard"),
    OpcionMenu("👥", "Usuarios", "Gestión"),
    OpcionMenu("🚜", "Acopiadores", "Gestión"),
    OpcionMenu("🌾", "Productores", "Gestión"),
    OpcionMenu("💳", "Pagos", "Gestión"),
    OpcionMenu("🔬", "Calidad y sanciones", "Calidad"),
    OpcionMenu("📣", "Quejas", "Sistema"),
    OpcionMenu("📢", "Avisos", "Sistema"),
    OpcionMenu("💰", "Actualizar precio", "Sistema"),
    OpcionMenu("⚙️", "Configuración", "Sistema")
)

@Composable
fun AdminHomeScreen(
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    var seccionActual by remember { mutableStateOf(0) }
    val panel: AdminViewModel = koinViewModel()
    val resumen by panel.resumen.collectAsStateWithLifecycle()
    val quejasAbiertas = (resumen as? EstadoUi.Exito)?.datos?.quejasAbiertas ?: 0
    val opciones = MENU_ADMIN.map { if (it.titulo == "Quejas") it.copy(insignia = quejasAbiertas) else it }

    EsqueletoRol(
        opciones = opciones,
        seleccion = seccionActual,
        onSeleccionar = { seccionActual = it },
        onCerrarSesion = onCerrarSesion,
        subtitulo = if (seccionActual == 0) "Resumen general del sistema" else null,
        modifier = modifier
    ) {
        when (seccionActual) {
            0 -> DashboardAdmin()
            1 -> UsuariosTab()
            2 -> AcopiadoresTab()
            3 -> com.example.acopiodeleche.ui.pantallas.productores.ProductoresScreen()
            4 -> com.example.acopiodeleche.ui.pantallas.pagos.PagosAdminScreen(onVolver = { seccionActual = 0 })
            5 -> com.example.acopiodeleche.ui.pantallas.admin.CalidadAdminScreen(onVolver = { seccionActual = 0 })
            6 -> QuejasAdminTab()
            7 -> AvisosTab()
            8 -> ActualizarPrecioTab()
            9 -> ConfiguracionSistemaTab()
        }
    }
}

@Composable
private fun DashboardAdmin(viewModel: AdminViewModel = koinViewModel()) {
    val estado by viewModel.resumen.collectAsStateWithLifecycle()
    val resumen = (estado as? EstadoUi.Exito)?.datos
    if (resumen == null) {
        CargaOError(estado, onReintentar = viewModel::cargar)
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            FilaEstadisticas(
                { TarjetaEstadistica("Litros hoy", formatoLitros(resumen.litrosHoy), "🥛", Tono.VERDE, it) },
                { TarjetaEstadistica("Acopios de hoy", "${resumen.entregasHoy}", "🚚", Tono.AZUL, it) }
            )
        }
        item {
            FilaEstadisticas(
                { TarjetaEstadistica("Productores activos", "${resumen.productoresActivos}", "🌾", Tono.DORADO, it) },
                { TarjetaEstadistica("En tránsito hoy", "${resumen.entregasEnTransitoHoy}", "⏳", Tono.NARANJA, it) }
            )
        }
        item {
            FilaEstadisticas(
                { TarjetaEstadistica("Usuarios", "${resumen.usuarios}", "👥", Tono.MORADO, it) },
                { TarjetaEstadistica("Precio por litro", "S/ ${resumen.precioLitro}", "💰", Tono.VERDE, it) }
            )
        }
        item {
            FilaEstadisticas(
                { TarjetaEstadistica("Litros del mes", formatoLitros(resumen.litrosMes), "📦", Tono.CIAN, it) },
                { TarjetaEstadistica("Quejas abiertas", "${resumen.quejasAbiertas}", "📣", if (resumen.quejasAbiertas > 0) Tono.ROJO else Tono.GRIS, it) }
            )
        }
        item {
            PanelHuata(titulo = "Sistema", icono = "🗄️") {
                Text(
                    "Los datos se comparten con el panel web de Ecolácteos Huata.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Insignia("App 1.0.0", Tono.VERDE)
                    Insignia("${resumen.lotesActivos} lotes activos", Tono.AZUL)
                }
            }
        }
    }
}

/** Dos tarjetas de estadística lado a lado con el mismo ancho. */
@Composable
internal fun FilaEstadisticas(
    izquierda: @Composable (Modifier) -> Unit,
    derecha: @Composable (Modifier) -> Unit
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        izquierda(Modifier.weight(1f))
        derecha(Modifier.weight(1f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UsuariosTab(viewModel: AdminViewModel = koinViewModel()) {
    // Usuarios del sistema leídos desde Laravel (mismas cuentas que la web)
    val estadoUsuarios by viewModel.usuarios.collectAsStateWithLifecycle()
    val envio by viewModel.envio.collectAsStateWithLifecycle()
    val todosUsuarios = estadoUsuarios.datosOVacio()
    var busqueda by remember { mutableStateOf("") }
    var filtroRol by remember { mutableStateOf<Rol?>(null) }
    var mostrarFormulario by remember { mutableStateOf(false) }

    LaunchedEffect(envio) {
        if (envio is EstadoEnvio.Exito) mostrarFormulario = false
    }

    if (mostrarFormulario) {
        FormularioUsuario(
            alGuardar = { nuevo -> viewModel.crearUsuario(nuevo) },
            alCancelar = { viewModel.limpiarEnvio(); mostrarFormulario = false },
            enviando = envio == EstadoEnvio.Enviando,
            mensajeError = (envio as? EstadoEnvio.Error)?.mensaje
        )
        return
    }
    if (estadoUsuarios !is EstadoUi.Exito) {
        CargaOError(estadoUsuarios, onReintentar = viewModel::cargar)
        return
    }

    // Filtrado combinado: texto + rol
    val usuariosFiltrados = remember(busqueda, filtroRol, todosUsuarios) {
        todosUsuarios.filter { u ->
            val coincideTexto = busqueda.isBlank() ||
                u.nombreCompleto.contains(busqueda, ignoreCase = true) ||
                u.correo.contains(busqueda, ignoreCase = true) ||
                u.dni.contains(busqueda) ||
                u.telefono.contains(busqueda)
            val coincideRol = filtroRol == null || u.rol == filtroRol
            coincideTexto && coincideRol
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Barra de búsqueda + botón nuevo
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${usuariosFiltrados.size} de ${todosUsuarios.size} usuarios",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = { viewModel.limpiarEnvio(); mostrarFormulario = true },
                    colors = ButtonDefaults.buttonColors(containerColor = RojoAdmin)
                ) { Text("+ Nuevo") }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                label = { Text("🔍 Buscar usuario") },
                placeholder = { Text("Nombre, correo, DNI, teléfono...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (busqueda.isNotBlank()) {
                        TextButton(onClick = { busqueda = "" }) { Text("✕") }
                    }
                }
            )
            Spacer(modifier = Modifier.height(6.dp))
            // Filtros rápidos por rol
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    FiltroChip(
                        label = "Todos",
                        seleccionado = filtroRol == null,
                        onClick = { filtroRol = null }
                    )
                }
                items(Rol.entries.toList()) { rol ->
                    FiltroChip(
                        label = rol.etiqueta,
                        seleccionado = filtroRol == rol,
                        onClick = { filtroRol = if (filtroRol == rol) null else rol }
                    )
                }
            }
        }

        (envio as? EstadoEnvio.Error)?.let {
            Text(it.mensaje, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp))
        }
        HorizontalDivider()

        if (usuariosFiltrados.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("👤", fontSize = 40.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        if (busqueda.isNotBlank()) "Sin resultados para \"$busqueda\""
                        else "No hay usuarios registrados",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(usuariosFiltrados, key = { it.id }) { u ->
                    TarjetaUsuario(
                        usuario = u,
                        onDesactivar = { viewModel.cambiarEstadoUsuario(u) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FiltroChip(label: String, seleccionado: Boolean, onClick: () -> Unit) {
    val bg = if (seleccionado) RojoAdmin else RojoAdmin.copy(alpha = 0.1f)
    val txt = if (seleccionado) Color.White else RojoAdmin
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = txt, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TarjetaUsuario(usuario: Usuario, onDesactivar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(usuario.nombreCompleto, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(usuario.correo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = usuario.rol.etiqueta,
                        style = MaterialTheme.typography.labelSmall,
                        color = RojoAdmin
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (usuario.estado) "Activo" else "Inactivo",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (usuario.estado) VerdeHuata else Color.Gray
                    )
                    Switch(
                        checked = usuario.estado,
                        onCheckedChange = { onDesactivar() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioUsuario(
    alGuardar: (Usuario) -> Unit,
    alCancelar: () -> Unit,
    enviando: Boolean = false,
    mensajeError: String? = null
) {
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var rolSeleccionado by remember { mutableStateOf(Rol.PRODUCTOR) }
    var dropRolExpanded by remember { mutableStateOf(false) }

    // Laravel exige DNI único, correo único y contraseña de al menos 6 caracteres
    val formularioValido = nombres.isNotBlank() && apellidos.isNotBlank() && dni.isNotBlank()
        && correo.isNotBlank() && contrasena.length >= 6

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = alCancelar) { Text("← Volver") }
                Text("Nuevo usuario", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }
        item {
            OutlinedTextField(
                value = nombres, onValueChange = { nombres = it },
                label = { Text("Nombres *") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = apellidos, onValueChange = { apellidos = it },
                label = { Text("Apellidos *") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = dni, onValueChange = { if (it.length <= 8) dni = it.filter { c -> c.isDigit() } },
                label = { Text("DNI *") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = telefono, onValueChange = { telefono = it },
                label = { Text("Teléfono") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = correo, onValueChange = { correo = it },
                label = { Text("Correo *") }, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = contrasena, onValueChange = { contrasena = it },
                label = { Text("Contraseña * (mín. 6 caracteres)") }, singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            ExposedDropdownMenuBox(expanded = dropRolExpanded, onExpandedChange = { dropRolExpanded = it }) {
                OutlinedTextField(
                    value = rolSeleccionado.etiqueta,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Rol *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dropRolExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(expanded = dropRolExpanded, onDismissRequest = { dropRolExpanded = false }) {
                    Rol.entries.forEach { rol ->
                        DropdownMenuItem(
                            text = { Text(rol.etiqueta) },
                            onClick = { rolSeleccionado = rol; dropRolExpanded = false }
                        )
                    }
                }
            }
        }
        if (mensajeError != null) {
            item { Text(mensajeError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        alGuardar(
                            Usuario(
                                id = "",   // lo asigna el backend
                                nombres = nombres.trim(),
                                apellidos = apellidos.trim(),
                                dni = dni.trim(),
                                telefono = telefono.trim(),
                                correo = correo.trim(),
                                contrasena = contrasena,
                                rol = rolSeleccionado
                            )
                        )
                    },
                    enabled = formularioValido && !enviando,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = RojoAdmin)
                ) { Text(if (enviando) "Guardando..." else "Guardar") }
            }
        }
    }
}

// ── TAB ACOPIADORES ───────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AcopiadoresTab(viewModel: AdminViewModel = koinViewModel()) {
    val estadoUsuarios by viewModel.usuarios.collectAsStateWithLifecycle()
    val estadoEntregas by viewModel.entregasHoy.collectAsStateWithLifecycle()
    val acopiadores = estadoUsuarios.datosOVacio().filter { it.rol == Rol.ACOPIADOR }
    val entregasHoy = estadoEntregas.datosOVacio()
    var acopiadorDetalle by remember { mutableStateOf<Usuario?>(null) }

    if (acopiadorDetalle != null) {
        DetalleAcopiadorScreen(
            acopiador = acopiadorDetalle!!,
            onVolver  = { acopiadorDetalle = null }
        )
        return
    }
    if (estadoUsuarios !is EstadoUi.Exito) {
        CargaOError(estadoUsuarios, onReintentar = viewModel::cargar)
        return
    }

    LazyColumn(
        contentPadding     = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "Panel de acopiadores",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "${acopiadores.size} acopiadores activos",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(acopiadores) { acopiador ->
            val registros = entregasHoy.filter { r -> r.idAcopiador == acopiador.id }
            val litrosHoy   = registros.sumOf { it.litros }

            Card(
                modifier  = Modifier.fillMaxWidth().clickable { acopiadorDetalle = acopiador },
                elevation = CardDefaults.cardElevation(3.dp),
                shape     = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(VerdeHuata.copy(0.12f), RoundedCornerShape(50)),
                                contentAlignment = Alignment.Center
                            ) { Text("👤", fontSize = 22.sp) }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    acopiador.nombreCompleto,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "📍 ${acopiador.comunidad ?: "Sin comunidad"} · 🚛 ${acopiador.vehiculo ?: "Sin vehículo"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Text("›", fontSize = 22.sp, color = VerdeHuata)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ResumenChip("🥛", "$litrosHoy L", "Litros hoy", VerdeHuata)
                        ResumenChip("📋", "${registros.size}", "Entregas", HuataColores.Azul)
                        ResumenChip("✅", "${registros.count { it.recibido }}", "Recibidos", HuataColores.Morado)
                    }
                }
            }
        }
    }
}

@Composable
private fun ResumenChip(icono: String, valor: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icono, fontSize = 18.sp)
        Text(valor, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DETALLE DE ACOPIADOR — litros del día + asignación de productores
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetalleAcopiadorScreen(acopiador: Usuario, onVolver: () -> Unit) {
    val viewModel: AcopiadorAdminViewModel = koinViewModel(key = "acopiador-${acopiador.id}") { parametersOf(acopiador) }
    val envio by viewModel.envio.collectAsStateWithLifecycle()
    var tabActual by remember { mutableStateOf(0) }
    val tabs = listOf("Registros del día", "Mis productores", "Configurar asignación", "Configurar vehículo")

    Column(modifier = Modifier.fillMaxSize()) {
        // Encabezado del acopiador (tarjeta blanca como en la web)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .tarjetaHuata(RoundedCornerShape(16.dp))
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onVolver) { Text("←", fontSize = 20.sp) }
            AvatarIniciales(acopiador.nombres, acopiador.apellidos)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(acopiador.nombreCompleto, style = MaterialTheme.typography.titleSmall)
                Text(
                    "📍 ${acopiador.comunidad ?: "-"} · 🚛 ${acopiador.vehiculo ?: "-"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        TabRow(selectedTabIndex = tabActual) {
            tabs.forEachIndexed { i, t ->
                Tab(selected = tabActual == i, onClick = { tabActual = i }, text = {
                    Text(t, style = MaterialTheme.typography.labelSmall)
                })
            }
        }

        (envio as? EstadoEnvio.Error)?.let {
            Text(it.mensaje, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
        }
        when (tabActual) {
            0 -> RegistrosDiaAcopiador(viewModel)
            1 -> ProductoresAcopiador(viewModel)
            2 -> ConfigurarAsignacion(acopiador, viewModel)
            3 -> ConfigurarVehiculo(acopiador, viewModel)
        }
    }
}

@Composable
private fun RegistrosDiaAcopiador(viewModel: AcopiadorAdminViewModel) {
    // Entregas de hoy de este acopiador, desde Laravel
    val estado by viewModel.entregasHoy.collectAsStateWithLifecycle()
    if (estado !is EstadoUi.Exito) {
        CargaOError(estado, onReintentar = viewModel::cargar)
        return
    }
    val registros = estado.datosOVacio()
    val litrosTotales = registros.sumOf { it.litros }

    if (registros.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🥛", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text("Sin registros hoy", style = MaterialTheme.typography.titleMedium)
            }
        }
        return
    }

    LazyColumn(
        contentPadding     = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(containerColor = VerdeHuata.copy(0.08f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ResumenChip("🥛", "$litrosTotales L", "Total litros", VerdeHuata)
                    ResumenChip("📋", "${registros.size}", "Entregas", HuataColores.Azul)
                    ResumenChip("✅", "${registros.count { it.recibido }}", "Recibidos", HuataColores.Verde)
                }
            }
        }
        items(registros) { r ->
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🥛", fontSize = 22.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            r.nombreProductor ?: "Productor",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${r.fecha} · ${r.hora} · ${r.zona}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (!r.observacion.isNullOrBlank()) {
                            Text(
                                "📝 ${r.observacion}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            r.litrosFormateados,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = VerdeHuata
                        )
                        Text(
                            if (r.recibido) "✅" else "⏳",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductoresAcopiador(viewModel: AcopiadorAdminViewModel) {
    // Laravel resuelve la asignación (individual o por comunidad)
    val estadoAsignacion by viewModel.asignacion.collectAsStateWithLifecycle()
    val estadoProductores by viewModel.productores.collectAsStateWithLifecycle()
    val estadoEntregas by viewModel.entregasHoy.collectAsStateWithLifecycle()
    val asignacion = (estadoAsignacion as? EstadoUi.Exito)?.datos
    if (asignacion == null || estadoProductores !is EstadoUi.Exito) {
        CargaOError(if (asignacion == null) estadoAsignacion else estadoProductores, onReintentar = viewModel::cargar)
        return
    }
    val misProductores = estadoProductores.datosOVacio().filter { p -> asignacion.idsProductores.contains(p.idProductor) }
    val entregasHoy = estadoEntregas.datosOVacio()

    if (misProductores.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("👥", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text("Sin productores asignados", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Ve a la pestaña \"Configurar asignación\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    LazyColumn(
        contentPadding     = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                "${misProductores.size} productores asignados",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
        items(misProductores) { p ->
            val litros = entregasHoy
                .filter { r -> r.idProductor == p.idProductor }
                .sumOf { it.litros }

            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🌾", fontSize = 20.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(p.nombreCompleto, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("${p.comunidad} · DNI: ${p.dni}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (litros > 0) {
                        Text(
                            "$litros L",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = VerdeHuata
                        )
                    } else {
                        Text("Sin entrega", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfigurarAsignacion(acopiador: Usuario, viewModel: AcopiadorAdminViewModel) {
    val estadoAsignacion by viewModel.asignacion.collectAsStateWithLifecycle()
    val estadoProductores by viewModel.productores.collectAsStateWithLifecycle()
    val envio by viewModel.envio.collectAsStateWithLifecycle()
    val asignacion = (estadoAsignacion as? EstadoUi.Exito)?.datos
    if (asignacion == null || estadoProductores !is EstadoUi.Exito) {
        CargaOError(if (asignacion == null) estadoAsignacion else estadoProductores, onReintentar = viewModel::cargar)
        return
    }
    val todosProductores = estadoProductores.datosOVacio()

    // Selección inicial = asignación vigente en Laravel
    var idsAsignados by remember(asignacion) { mutableStateOf(asignacion.idsProductores.toMutableList()) }
    var comunidadFiltro by remember { mutableStateOf(acopiador.comunidad ?: "") }
    val guardado = envio is EstadoEnvio.Exito

    val comunidades = todosProductores.map { it.comunidad }.filter { it.isNotBlank() }.distinct().sorted()
    val productoresFiltrados = todosProductores.filter { p ->
        p.estado && (comunidadFiltro.isBlank() || p.comunidad == comunidadFiltro)
    }

    LazyColumn(
        contentPadding     = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                "Asignar productores a ${acopiador.nombres}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Selecciona los productores que este acopiador debe atender.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Filtro por comunidad
        item {
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    FiltroChip("Todas", comunidadFiltro.isBlank()) { comunidadFiltro = "" }
                }
                items(comunidades) { com ->
                    FiltroChip(com, comunidadFiltro == com) { comunidadFiltro = if (comunidadFiltro == com) "" else com }
                }
            }
        }

        item {
            Text(
                "${idsAsignados.size} productores seleccionados",
                style = MaterialTheme.typography.labelMedium,
                color = VerdeHuata,
                fontWeight = FontWeight.Bold
            )
        }

        items(productoresFiltrados) { p ->
            val seleccionado = idsAsignados.contains(p.idProductor)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        idsAsignados = if (seleccionado) {
                            idsAsignados.filter { it != p.idProductor }.toMutableList()
                        } else {
                            (idsAsignados + p.idProductor).toMutableList()
                        }
                        viewModel.limpiarEnvio()
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (seleccionado) VerdeHuata.copy(0.1f)
                                     else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(if (seleccionado) 3.dp else 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (seleccionado) "✅" else "⬜", fontSize = 20.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(p.nombreCompleto, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("${p.comunidad} · DNI: ${p.dni}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(4.dp))
            Button(
                // Laravel guarda la asignación (y la comunidad si todos son de una misma)
                onClick = { viewModel.guardarAsignacion(idsAsignados.toList()) },
                enabled = envio != EstadoEnvio.Enviando,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = VerdeHuata)
            ) {
                Text(
                    if (guardado) "✅ Asignación guardada" else "Guardar asignación",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ── CONFIGURAR VEHÍCULO ───────────────────────────────────────────────────
@Composable
private fun ConfigurarVehiculo(acopiador: Usuario, viewModel: AcopiadorAdminViewModel) {
    val envio by viewModel.envio.collectAsStateWithLifecycle()
    // Vehículos disponibles
    val vehiculosDisponibles = listOf(
        "Furgoneta 01",
        "Furgoneta 02",
        "Furgoneta 03",
        "Motocarga 01"
    )
    
    var vehiculoSeleccionado by remember { mutableStateOf(acopiador.vehiculo ?: "") }
    val guardado = envio is EstadoEnvio.Exito

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "Asignar vehículo a ${acopiador.nombres}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Selecciona el vehículo que este acopiador utilizará para la recolección de leche.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Vehículo actual
        if (vehiculoSeleccionado.isNotBlank()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = VerdeHuata.copy(0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(VerdeHuata.copy(0.2f), RoundedCornerShape(50)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🚛", fontSize = 24.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Vehículo actual",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                vehiculoSeleccionado,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = VerdeHuata
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(4.dp))
            Text(
                "Vehículos disponibles",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Selecciona uno de los vehículos de la flota",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Lista de vehículos
        items(vehiculosDisponibles) { vehiculo ->
            val seleccionado = vehiculoSeleccionado == vehiculo
            val icono = if (vehiculo.contains("Motocarga")) "🏍️" else "🚚"
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        vehiculoSeleccionado = vehiculo
                        viewModel.limpiarEnvio()
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (seleccionado) VerdeHuata.copy(0.12f)
                                     else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(if (seleccionado) 3.dp else 1.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (seleccionado) VerdeHuata.copy(0.2f) 
                                else MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(50)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(icono, fontSize = 24.sp)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            vehiculo,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                            color = if (seleccionado) VerdeHuata else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (seleccionado) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(VerdeHuata, RoundedCornerShape(50)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✓", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { viewModel.guardarVehiculo(vehiculoSeleccionado) },
                enabled = vehiculoSeleccionado.isNotBlank() && envio != EstadoEnvio.Enviando,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeHuata)
            ) {
                Text(
                    if (guardado) "✅ Vehículo asignado" else "Guardar asignación",
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        if (guardado) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = VerdeHuata.copy(0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("✅", fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "El vehículo se ha asignado correctamente al acopiador.",
                            style = MaterialTheme.typography.bodySmall,
                            color = VerdeHuata,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// ── TAB QUEJAS DEL PRODUCTOR ──────────────────────────────────────────────
@Composable
private fun QuejasAdminTab(
    viewModel: com.example.acopiodeleche.ui.pantallas.productor.QuejasViewModel = koinViewModel()
) {
    // Quejas enviadas por los productores (desde la app o la web), leídas de Laravel
    val estadoQuejas by viewModel.quejas.collectAsStateWithLifecycle()
    val envio by viewModel.envio.collectAsStateWithLifecycle()
    val quejas = (estadoQuejas as? com.example.acopiodeleche.ui.estado.EstadoUi.Exito)?.datos.orEmpty()
    val pendientes = quejas.count {
        it.estado == com.example.acopiodeleche.domain.model.EstadoQueja.PENDIENTE
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Quejas y reclamos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "$pendientes pendientes de respuesta",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (pendientes > 0) HuataColores.NaranjaOscuro else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        (envio as? com.example.acopiodeleche.ui.estado.EstadoEnvio.Error)?.let {
            Text(it.mensaje, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp))
        }
        HorizontalDivider()

        if (estadoQuejas !is com.example.acopiodeleche.ui.estado.EstadoUi.Exito) {
            com.example.acopiodeleche.ui.estado.CargaOError(estadoQuejas, onReintentar = viewModel::cargar)
        } else if (quejas.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay quejas registradas", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(quejas) { queja: com.example.acopiodeleche.domain.model.Queja ->
                    var respuestaTexto by remember { mutableStateOf(queja.respuesta ?: "") }
                    var respondiendo by remember { mutableStateOf(false) }

                    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(queja.titulo, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text(
                                        "${queja.nombreProductor ?: "Productor"} · ${queja.fecha}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(
                                            when (queja.estado) {
                                                com.example.acopiodeleche.domain.model.EstadoQueja.PENDIENTE -> HuataColores.NaranjaOscuro.copy(alpha = 0.15f)
                                                com.example.acopiodeleche.domain.model.EstadoQueja.RESPONDIDA -> VerdeHuata.copy(alpha = 0.15f)
                                                else -> Color.Gray.copy(alpha = 0.15f)
                                            },
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        "${queja.estado.icono} ${queja.estado.etiqueta}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(queja.descripcion, style = MaterialTheme.typography.bodySmall)

                            if (!queja.respuesta.isNullOrBlank() && !respondiendo) {
                                Spacer(Modifier.height(6.dp))
                                Text("Tu respuesta: ${queja.respuesta}", style = MaterialTheme.typography.bodySmall, color = VerdeHuata)
                            }

                            if (respondiendo) {
                                Spacer(Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = respuestaTexto,
                                    onValueChange = { respuestaTexto = it },
                                    label = { Text("Respuesta al productor") },
                                    minLines = 2,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(onClick = { respondiendo = false }) { Text("Cancelar") }
                                    Button(
                                        onClick = {
                                            viewModel.responder(
                                                queja,
                                                com.example.acopiodeleche.domain.model.EstadoQueja.RESPONDIDA,
                                                respuestaTexto
                                            )
                                            respondiendo = false
                                        },
                                        enabled = respuestaTexto.isNotBlank() &&
                                            envio != com.example.acopiodeleche.ui.estado.EstadoEnvio.Enviando,
                                        colors = ButtonDefaults.buttonColors(containerColor = VerdeHuata)
                                    ) { Text("Enviar respuesta") }
                                }
                            } else if (queja.estado == com.example.acopiodeleche.domain.model.EstadoQueja.PENDIENTE) {
                                Spacer(Modifier.height(4.dp))
                                Button(
                                    onClick = { respondiendo = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = RojoAdmin)
                                ) { Text("Responder queja") }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── TAB AVISOS / EVENTOS ─────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AvisosTab(viewModel: AdminViewModel = koinViewModel()) {
    // Avisos publicados en Laravel (los ven los productores en la app y en la web)
    val estadoAvisos by viewModel.avisos.collectAsStateWithLifecycle()
    val envio by viewModel.envio.collectAsStateWithLifecycle()
    val avisos = estadoAvisos.datosOVacio()
    var mostrarFormulario by remember { mutableStateOf(false) }

    LaunchedEffect(envio) {
        if (envio is EstadoEnvio.Exito) mostrarFormulario = false
    }

    if (mostrarFormulario) {
        FormularioAviso(
            alGuardar = { titulo, mensaje, publicadoEn -> viewModel.publicarAviso(titulo, mensaje, publicadoEn) },
            alCancelar = { viewModel.limpiarEnvio(); mostrarFormulario = false },
            enviando = envio == EstadoEnvio.Enviando,
            mensajeError = (envio as? EstadoEnvio.Error)?.mensaje
        )
    } else if (estadoAvisos !is EstadoUi.Exito) {
        CargaOError(estadoAvisos, onReintentar = viewModel::cargar)
    } else {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${avisos.size} avisos enviados",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = { viewModel.limpiarEnvio(); mostrarFormulario = true },
                    colors = ButtonDefaults.buttonColors(containerColor = RojoAdmin)
                ) { Text("+ Nuevo aviso") }
            }
            HorizontalDivider()
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(avisos) { aviso ->
                    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                            Text(aviso.tipo.icono, fontSize = 28.sp)
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(aviso.titulo, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(aviso.mensaje, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${aviso.fecha} ${aviso.hora}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioAviso(
    alGuardar: (titulo: String, mensaje: String, publicadoEn: String?) -> Unit,
    alCancelar: () -> Unit,
    enviando: Boolean = false,
    mensajeError: String? = null
) {
    var titulo by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var tipoSeleccionado by remember { mutableStateOf(com.example.acopiodeleche.domain.model.TipoNotificacion.AVISO_GENERAL) }
    var dropTipoExpanded by remember { mutableStateOf(false) }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }

    val formularioValido = titulo.isNotBlank() && mensaje.isNotBlank() && fecha.isNotBlank() && hora.isNotBlank()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = alCancelar) { Text("← Volver") }
                Text("Nuevo aviso / evento", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }
        item {
            OutlinedTextField(
                value = titulo, onValueChange = { titulo = it },
                label = { Text("Título *") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = mensaje, onValueChange = { mensaje = it },
                label = { Text("Mensaje *") }, minLines = 3, modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            ExposedDropdownMenuBox(expanded = dropTipoExpanded, onExpandedChange = { dropTipoExpanded = it }) {
                OutlinedTextField(
                    value = "${tipoSeleccionado.icono} ${tipoSeleccionado.etiqueta}",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dropTipoExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(expanded = dropTipoExpanded, onDismissRequest = { dropTipoExpanded = false }) {
                    com.example.acopiodeleche.domain.model.TipoNotificacion.entries.forEach { tipo ->
                        DropdownMenuItem(
                            text = { Text("${tipo.icono} ${tipo.etiqueta}") },
                            onClick = { tipoSeleccionado = tipo; dropTipoExpanded = false }
                        )
                    }
                }
            }
        }
        item {
            FilaFechaHora(
                fecha = fecha,
                hora = hora,
                onFechaChange = { fecha = it },
                onHoraChange  = { hora = it },
                colorPrimario = RojoAdmin
            )
        }
        item {
            Button(
                onClick = {
                    // El backend no tiene "tipo": se antepone su icono al título.
                    alGuardar("${tipoSeleccionado.icono} ${titulo.trim()}", mensaje.trim(), fechaHoraIso(fecha, hora))
                },
                enabled = formularioValido && !enviando,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RojoAdmin)
            ) { Text(if (enviando) "Publicando..." else "Enviar aviso a todos los productores", fontWeight = FontWeight.Bold) }
        }
        if (mensajeError != null) {
            item { Text(mensajeError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
        }
    }
}

@Composable
private fun ActualizarPrecioTab(viewModel: AdminViewModel = koinViewModel()) {
    val estadoAjustes by viewModel.ajustes.collectAsStateWithLifecycle()
    val envio by viewModel.envio.collectAsStateWithLifecycle()
    val ajustes = estadoAjustes.datosOVacio()
    if (estadoAjustes !is EstadoUi.Exito) {
        CargaOError(estadoAjustes, onReintentar = viewModel::cargar)
        return
    }
    // Precio vigente guardado en Laravel (plant_configs.precio_litro_leche)
    val precioActual = ajustes.firstOrNull { it.clave == "precio_litro_leche" }?.valor.orEmpty()
    var precioTexto  by remember(precioActual) { mutableStateOf(precioActual) }
    val guardado     = envio is EstadoEnvio.Exito
    val precioValido = precioTexto.toDoubleOrNull()?.let { it > 0 } ?: false

    LazyColumn(
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("💰 Actualizar precio por litro", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Este precio se aplica a los pagos de todos los productores", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(containerColor = VerdeHuata.copy(0.1f)),
                shape    = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier              = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Precio vigente actual", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("Ecolácteos Huata · temporada actual", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(
                        "S/ $precioActual / L",
                        style      = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color      = VerdeHuata
                    )
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Nuevo precio por litro", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value          = precioTexto,
                        onValueChange  = { precioTexto = it; viewModel.limpiarEnvio() },
                        label          = { Text("Precio (S/)") },
                        prefix         = { Text("S/ ") },
                        suffix         = { Text("por litro") },
                        singleLine     = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError        = !precioValido && precioTexto.isNotBlank(),
                        supportingText = { if (!precioValido && precioTexto.isNotBlank()) Text("Ingresa un número mayor a 0") },
                        modifier       = Modifier.fillMaxWidth()
                    )
                    if (guardado) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors   = CardDefaults.cardColors(containerColor = VerdeHuata.copy(0.12f)),
                            shape    = RoundedCornerShape(8.dp)
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("✅", fontSize = 18.sp); Spacer(Modifier.width(8.dp))
                                Text("Precio actualizado a S/ $precioActual/L", style = MaterialTheme.typography.bodySmall, color = VerdeHuata, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    (envio as? EstadoEnvio.Error)?.let {
                        Text(it.mensaje, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                    Button(
                        onClick = { viewModel.guardarAjustes("acopio", mapOf("precio_litro_leche" to precioTexto.trim())) },
                        enabled  = precioValido && precioTexto.trim() != precioActual && envio != EstadoEnvio.Enviando,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = RojoAdmin)
                    ) { Text("Actualizar precio", fontWeight = FontWeight.Bold) }
                }
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                shape    = RoundedCornerShape(10.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                    Text("⚠️", fontSize = 18.sp); Spacer(Modifier.width(8.dp))
                    Text(
                        "Al actualizar el precio, las entregas y pagos nuevos se calcularán con este valor. Los ya registrados no se modifican.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF5D4037)
                    )
                }
            }
        }
    }
}

/**
 * Configuración de la planta guardada en Laravel (plant_configs). Solo se muestran los ajustes
 * que existen en el backend; se editan por grupo, igual que en el panel web.
 */
@Composable
private fun ConfiguracionSistemaTab(viewModel: AdminViewModel = koinViewModel()) {
    val estadoAjustes by viewModel.ajustes.collectAsStateWithLifecycle()
    val envio by viewModel.envio.collectAsStateWithLifecycle()
    if (estadoAjustes !is EstadoUi.Exito) {
        CargaOError(estadoAjustes, onReintentar = viewModel::cargar)
        return
    }
    val ajustes = estadoAjustes.datosOVacio()
    fun valor(clave: String) = ajustes.firstOrNull { it.clave == clave }?.valor.orEmpty()

    var nombre    by remember(ajustes) { mutableStateOf(valor("nombre_planta")) }
    var ruc       by remember(ajustes) { mutableStateOf(valor("ruc_planta")) }
    var direccion by remember(ajustes) { mutableStateOf(valor("direccion_planta")) }
    var telefono  by remember(ajustes) { mutableStateOf(valor("telefono_planta")) }
    var correo    by remember(ajustes) { mutableStateOf(valor("email_planta")) }
    var litrosMax by remember(ajustes) { mutableStateOf(valor("litros_maximos_entrega")) }
    var diaPago   by remember(ajustes) { mutableStateOf(valor("dias_pago_semanal")) }
    val diasPago  = ajustes.firstOrNull { it.clave == "dias_pago_semanal" }?.opciones.orEmpty()
    val guardado  = envio is EstadoEnvio.Exito

    LazyColumn(
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("⚙️ Configuración del sistema", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Valores compartidos con el panel web de Ecolácteos Huata", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // ── Información de la empresa ──────────────────────────────────────
        item { SeccionConfig("🏢 Información de la empresa") }
        item {
            OutlinedTextField(value = nombre, onValueChange = { nombre = it; viewModel.limpiarEnvio() }, label = { Text("Nombre de la empresa") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        item {
            OutlinedTextField(value = ruc, onValueChange = { if (it.length <= 11) ruc = it.filter { c -> c.isDigit() }; viewModel.limpiarEnvio() }, label = { Text("RUC") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        }
        item {
            OutlinedTextField(value = direccion, onValueChange = { direccion = it; viewModel.limpiarEnvio() }, label = { Text("Dirección") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = telefono, onValueChange = { telefono = it; viewModel.limpiarEnvio() }, label = { Text("Teléfono") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.weight(1f))
                OutlinedTextField(value = correo, onValueChange = { correo = it; viewModel.limpiarEnvio() }, label = { Text("Correo") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.weight(1f))
            }
        }
        item {
            Button(
                onClick = {
                    viewModel.guardarAjustes("empresa", mapOf(
                        "nombre_planta" to nombre.trim(), "ruc_planta" to ruc.trim(), "direccion_planta" to direccion.trim(),
                        "telefono_planta" to telefono.trim(), "email_planta" to correo.trim()
                    ))
                },
                enabled  = envio != EstadoEnvio.Enviando,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = RojoAdmin)
            ) { Text("Guardar datos de la empresa", fontWeight = FontWeight.Bold) }
        }

        // ── Límites operativos ─────────────────────────────────────────────
        item { SeccionConfig("📊 Acopio y pagos") }
        item {
            OutlinedTextField(
                value = litrosMax, onValueChange = { litrosMax = it; viewModel.limpiarEnvio() },
                label = { Text("Litros máximos por entrega") }, suffix = { Text("L") }, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Text("Día de pago a productores", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(6.dp))
            androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(diasPago) { dia -> FiltroChip(dia, diaPago == dia) { diaPago = dia; viewModel.limpiarEnvio() } }
            }
        }
        item {
            Button(
                onClick = { viewModel.guardarAjustes("acopio", mapOf("litros_maximos_entrega" to litrosMax.trim(), "dias_pago_semanal" to diaPago)) },
                enabled  = litrosMax.toDoubleOrNull() != null && envio != EstadoEnvio.Enviando,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = RojoAdmin)
            ) { Text("Guardar acopio y pagos", fontWeight = FontWeight.Bold) }
        }

        // ── Info del sistema ───────────────────────────────────────────────
        item { SeccionConfig("ℹ️ Información del sistema") }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(containerColor = RojoAdmin.copy(0.06f)),
                shape    = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    InfoRow("📱 Versión de la app",  ConfiguracionPlanta.VERSION_APP)
                    InfoRow("🗄️ Datos",              "Servidor Laravel (compartido con la web)")
                    InfoRow("👨‍💻 Desarrollado por",   ConfiguracionPlanta.DESARROLLADOR)
                    InfoRow("🔑 Sistema",             "4bytes — Gestión Ecolácteos")
                }
            }
        }

        if (guardado || envio is EstadoEnvio.Error) {
            item {
                Text(
                    (envio as? EstadoEnvio.Exito)?.mensaje ?: (envio as? EstadoEnvio.Error)?.mensaje.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (guardado) VerdeHuata else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

/** "12/10/2026" + "08:30" -> "2026-10-12T08:30" (formato que acepta Laravel). */
private fun fechaHoraIso(fecha: String, hora: String): String? {
    val partes = fecha.trim().split("/")
    if (partes.size != 3) return null
    return "${partes[2]}-${partes[1].padStart(2, '0')}-${partes[0].padStart(2, '0')}T${hora.trim().ifBlank { "00:00" }}"
}

@Composable
private fun SeccionConfig(titulo: String) {
    Column {
        HorizontalDivider()
        Spacer(Modifier.height(4.dp))
        Text(titulo, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = RojoAdmin)
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun InfoRow(label: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}
