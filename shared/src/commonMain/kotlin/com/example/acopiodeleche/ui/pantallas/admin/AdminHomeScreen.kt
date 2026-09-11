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
import com.example.acopiodeleche.domain.model.DatosMock
import com.example.acopiodeleche.domain.model.Rol
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.domain.model.Usuario
import com.example.acopiodeleche.ui.components.FilaFechaHora
import kotlin.random.Random
import kotlinx.coroutines.launch

private val VerdeHuata = Color(0xFF2E7D32)
private val RojoAdmin  = Color(0xFFC62828)

// ── Ítems del menú lateral ────────────────────────────────────────────────

private data class ItemMenu(val icono: String, val titulo: String, val indice: Int)

private val MENU_ITEMS = listOf(
    ItemMenu("🏠", "Inicio",        0),
    ItemMenu("👥", "Usuarios",      1),
    ItemMenu("🚜", "Acopiadores",   2),
    ItemMenu("🌾", "Productores",   3),
    ItemMenu("💳", "Pagos",         4),
    ItemMenu("🔬", "Calidad",       5),
    ItemMenu("📣", "Quejas",        6),
    ItemMenu("📢", "Avisos",        7),
    ItemMenu("⚙️", "Configuración", 8)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val usuario     = SesionActual.usuario
    var seccionActual by remember { mutableStateOf(0) }
    val drawerState  = rememberDrawerState(DrawerValue.Closed)
    val scope        = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(260.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                // Cabecera del drawer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RojoAdmin)
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .background(Color.White.copy(0.2f), RoundedCornerShape(50)),
                            contentAlignment = Alignment.Center
                        ) { Text("👤", fontSize = 26.sp) }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            usuario?.nombreCompleto ?: "Administrador",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Ecolácteos Huata",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(0.75f)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Ítems de navegación
                MENU_ITEMS.forEach { item ->
                    val seleccionado = seccionActual == item.indice
                    NavigationDrawerItem(
                        icon  = { Text(item.icono, fontSize = 18.sp) },
                        label = {
                            Text(
                                item.titulo,
                                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selected = seleccionado,
                        onClick  = {
                            seccionActual = item.indice
                            scope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = RojoAdmin.copy(0.12f),
                            selectedTextColor      = RojoAdmin,
                            selectedIconColor      = RojoAdmin
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                    )
                }

                Spacer(Modifier.weight(1f))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
                // Botón cerrar sesión en el drawer
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
        // ── Contenido principal ───────────────────────────────────────────
        Column(modifier = Modifier.fillMaxSize()) {

            // TopBar con botón hamburguesa
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RojoAdmin)
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

                // Título de la sección actual
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 56.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val item = MENU_ITEMS.find { it.indice == seccionActual }
                    Text(
                        "${item?.icono ?: ""} ${item?.titulo ?: "Admin"}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Ecolácteos Huata",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(0.75f)
                    )
                }
            }

            // Contenido según sección
            Box(modifier = Modifier.fillMaxSize()) {
                when (seccionActual) {
                    0 -> DashboardAdmin()
                    1 -> UsuariosTab()
                    2 -> AcopiadoresTab()
                    3 -> com.example.acopiodeleche.ui.pantallas.productores.ProductoresScreen()
                    4 -> com.example.acopiodeleche.ui.pantallas.pagos.PagosAdminScreen(
                            onVolver = { seccionActual = 0 }
                         )
                    5 -> com.example.acopiodeleche.ui.pantallas.admin.CalidadAdminScreen(
                            onVolver = { seccionActual = 0 }
                         )
                    6 -> QuejasAdminTab()
                    7 -> AvisosTab()
                    8 -> ConfiguracionTab()
                }
            }
        }
    }
}

@Composable
private fun DashboardAdmin() {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Resumen del sistema", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaDashboard("👥", "${DatosMock.usuarios.size}", "Usuarios", VerdeHuata, Modifier.weight(1f))
                TarjetaDashboard("🌾", "${DatosMock.productores.count { it.estado }}", "Productores\nactivos", Color(0xFF1565C0), Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaDashboard("🥛", "${DatosMock.registrosAcopio.size}", "Acopios\nregistrados", Color(0xFF6A1B9A), Modifier.weight(1f))
                TarjetaDashboard("💰", "S/ 1.70", "Precio\nactual/L", Color(0xFFE65100), Modifier.weight(1f))
            }
        }
        item {
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))
            Text("Acceso rápido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        item {
            Text(
                text = "🔑 Sistema: 4bytes — Ecolácteos Huata\n📱 Versión: 1.0.0\n🗄️ Base de datos: SQLite (en desarrollo)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TarjetaDashboard(icono: String, valor: String, etiqueta: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icono, fontSize = 28.sp)
            Text(valor, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
            Text(etiqueta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UsuariosTab() {
    var todosUsuarios by remember { mutableStateOf(DatosMock.usuarios.toList()) }
    var busqueda by remember { mutableStateOf("") }
    var filtroRol by remember { mutableStateOf<Rol?>(null) }
    var mostrarFormulario by remember { mutableStateOf(false) }

    if (mostrarFormulario) {
        FormularioUsuario(
            alGuardar = { nuevo ->
                DatosMock.usuarios.add(nuevo)
                todosUsuarios = DatosMock.usuarios.toList()
                mostrarFormulario = false
            },
            alCancelar = { mostrarFormulario = false }
        )
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
                    onClick = { mostrarFormulario = true },
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
                        onDesactivar = {
                            val idx = DatosMock.usuarios.indexOfFirst { it.id == u.id }
                            if (idx != -1) {
                                DatosMock.usuarios[idx] = u.copy(estado = !u.estado)
                                todosUsuarios = DatosMock.usuarios.toList()
                            }
                        }
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
    alCancelar: () -> Unit
) {
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var rolSeleccionado by remember { mutableStateOf(Rol.PRODUCTOR) }
    var dropRolExpanded by remember { mutableStateOf(false) }

    val formularioValido = nombres.isNotBlank() && apellidos.isNotBlank()
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
                label = { Text("DNI") }, singleLine = true, modifier = Modifier.fillMaxWidth()
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
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        alGuardar(
                            Usuario(
                                id = "u-${Random.nextInt(1000, 9999)}",
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
                    enabled = formularioValido,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = RojoAdmin)
                ) { Text("Guardar") }
            }
        }
    }
}

// ── TAB ACOPIADORES ───────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AcopiadoresTab() {
    val acopiadores = DatosMock.usuarios.filter { it.rol == Rol.ACOPIADOR }
    var acopiadorDetalle by remember { mutableStateOf<Usuario?>(null) }

    if (acopiadorDetalle != null) {
        DetalleAcopiadorScreen(
            acopiador = acopiadorDetalle!!,
            onVolver  = { acopiadorDetalle = null }
        )
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
            val registros = DatosMock.registrosAcopio.filter { r ->
                r.acopiador == acopiador.nombreCompleto
            }
            val litrosHoy   = registros.sumOf { it.litros }
            val asignacion  = DatosMock.asignaciones.find { it.idAcopiador == acopiador.id }
            val cantProductores = asignacion?.idsProductores?.size ?: 0

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
                        ResumenChip("📋", "${registros.size}", "Entregas", Color(0xFF1565C0))
                        ResumenChip("👥", "$cantProductores", "Productores", Color(0xFF6A1B9A))
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
    var tabActual by remember { mutableStateOf(0) }
    val tabs = listOf("Registros del día", "Mis productores", "Configurar asignación")

    Column(modifier = Modifier.fillMaxSize()) {
        // Encabezado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerdeHuata)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onVolver) {
                    Text("←", color = Color.White, fontSize = 20.sp)
                }
                Spacer(Modifier.width(4.dp))
                Column {
                    Text(
                        acopiador.nombreCompleto,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "📍 ${acopiador.comunidad ?: "-"} · 🚛 ${acopiador.vehiculo ?: "-"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        TabRow(selectedTabIndex = tabActual) {
            tabs.forEachIndexed { i, t ->
                Tab(selected = tabActual == i, onClick = { tabActual = i }, text = {
                    Text(t, style = MaterialTheme.typography.labelSmall)
                })
            }
        }

        when (tabActual) {
            0 -> RegistrosDiaAcopiador(acopiador)
            1 -> ProductoresAcopiador(acopiador)
            2 -> ConfigurarAsignacion(acopiador)
        }
    }
}

@Composable
private fun RegistrosDiaAcopiador(acopiador: Usuario) {
    val registros = DatosMock.registrosAcopio.filter { r ->
        r.acopiador == acopiador.nombreCompleto
    }
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
                    ResumenChip("📋", "${registros.size}", "Entregas", Color(0xFF1565C0))
                    ResumenChip("✅", "${registros.count { it.recibido }}", "Recibidos", Color(0xFF2E7D32))
                }
            }
        }
        items(registros.reversed()) { r ->
            val productor = DatosMock.productores.find { it.idProductor == r.idProductor }
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Row(
                    modifier = Modifier.padding(14.dp),
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
private fun ProductoresAcopiador(acopiador: Usuario) {
    val asignacion = DatosMock.asignaciones.find { it.idAcopiador == acopiador.id }
    val misProductores = if (asignacion != null) {
        DatosMock.productores.filter { p -> asignacion.idsProductores.contains(p.idProductor) }
    } else {
        DatosMock.productores.filter { p -> p.comunidad == acopiador.comunidad }
    }

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
            val litros = DatosMock.registrosAcopio
                .filter { r -> r.idProductor == p.idProductor && r.acopiador == acopiador.nombreCompleto }
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
private fun ConfigurarAsignacion(acopiador: Usuario) {
    // Estado mutable de los IDs asignados para este acopiador
    var idsAsignados by remember {
        val existente = DatosMock.asignaciones.find { it.idAcopiador == acopiador.id }
        mutableStateOf(existente?.idsProductores?.toMutableList() ?: mutableListOf())
    }
    var comunidadFiltro by remember { mutableStateOf(acopiador.comunidad ?: "") }
    var guardado by remember { mutableStateOf(false) }

    val comunidades = DatosMock.productores.map { it.comunidad }.distinct().sorted()
    val productoresFiltrados = DatosMock.productores.filter { p ->
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
                        guardado = false
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
                onClick = {
                    val idx = DatosMock.asignaciones.indexOfFirst { it.idAcopiador == acopiador.id }
                    val nueva = com.example.acopiodeleche.domain.model.AsignacionAcopiador(
                        idAcopiador     = acopiador.id,
                        idsProductores  = idsAsignados.toList()
                    )
                    if (idx == -1) DatosMock.asignaciones.add(nueva)
                    else DatosMock.asignaciones[idx] = nueva
                    guardado = true
                },
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

// ── TAB QUEJAS DEL PRODUCTOR ──────────────────────────────────────────────
@Composable
private fun QuejasAdminTab() {
    var quejas by remember { mutableStateOf(DatosMock.quejas.toList()) }
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
                    color = if (pendientes > 0) Color(0xFFE65100) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        HorizontalDivider()

        if (quejas.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay quejas registradas", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(quejas) { queja: com.example.acopiodeleche.domain.model.Queja ->
                    val productor = DatosMock.productores.find { it.idProductor == queja.idProductor }
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
                                        "${productor?.nombreCompleto ?: "Productor"} · ${queja.fecha}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(
                                            when (queja.estado) {
                                                com.example.acopiodeleche.domain.model.EstadoQueja.PENDIENTE -> Color(0xFFE65100).copy(alpha = 0.15f)
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
                                            val idx = DatosMock.quejas.indexOfFirst { it.id == queja.id }
                                            if (idx != -1) {
                                                DatosMock.quejas[idx] = queja.copy(
                                                    respuesta = respuestaTexto,
                                                    estado = com.example.acopiodeleche.domain.model.EstadoQueja.RESPONDIDA,
                                                    fechaRespuesta = "Hoy"
                                                )
                                                quejas = DatosMock.quejas.toList()
                                            }
                                            respondiendo = false
                                        },
                                        enabled = respuestaTexto.isNotBlank(),
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
private fun AvisosTab() {
    var avisos by remember { mutableStateOf(DatosMock.notificaciones.toList()) }
    var mostrarFormulario by remember { mutableStateOf(false) }

    if (mostrarFormulario) {
        FormularioAviso(
            alGuardar = { nuevo ->
                DatosMock.notificaciones.add(nuevo)
                avisos = DatosMock.notificaciones.toList()
                mostrarFormulario = false
            },
            alCancelar = { mostrarFormulario = false }
        )
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
                    onClick = { mostrarFormulario = true },
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
                                Text("${aviso.fecha} ${aviso.hora} · ${aviso.tipo.etiqueta}",
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
    alGuardar: (com.example.acopiodeleche.domain.model.Notificacion) -> Unit,
    alCancelar: () -> Unit
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
                    alGuardar(
                        com.example.acopiodeleche.domain.model.Notificacion(
                            id = "n-${kotlin.random.Random.nextInt(1000, 9999)}",
                            titulo = titulo.trim(),
                            mensaje = mensaje.trim(),
                            tipo = tipoSeleccionado,
                            fecha = fecha.trim(),
                            hora = hora.trim(),
                            leida = false
                        )
                    )
                },
                enabled = formularioValido,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RojoAdmin)
            ) { Text("Enviar aviso a todos los productores", fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun ConfiguracionTab() {
    var precioTexto by remember { mutableStateOf(ConfiguracionPlanta.precioPorLitro.toString()) }
    var guardado by remember { mutableStateOf(false) }

    val precioValido = precioTexto.toDoubleOrNull()?.let { it > 0 } ?: false

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Configuración de la planta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        item {
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("💰 Precio por litro de leche", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = precioTexto,
                        onValueChange = { precioTexto = it; guardado = false },
                        label = { Text("Precio (S/)") },
                        prefix = { Text("S/ ") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = !precioValido && precioTexto.isNotBlank(),
                        supportingText = { if (!precioValido && precioTexto.isNotBlank()) Text("Ingresa un número mayor a 0") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            val nuevo = precioTexto.toDoubleOrNull()
                            if (nuevo != null && nuevo > 0) {
                                ConfiguracionPlanta.precioPorLitro = nuevo
                                guardado = true
                            }
                        },
                        enabled = precioValido,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeHuata)
                    ) { Text("Actualizar precio") }

                    if (guardado) {
                        Text(
                            text = "✅ Precio actualizado a S/ ${ConfiguracionPlanta.precioPorLitro} por litro",
                            style = MaterialTheme.typography.bodySmall,
                            color = VerdeHuata
                        )
                    }
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("ℹ️ Información del sistema", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Planta: ${ConfiguracionPlanta.NOMBRE}", style = MaterialTheme.typography.bodySmall)
                    Text("Slogan: ${ConfiguracionPlanta.SLOGAN}", style = MaterialTheme.typography.bodySmall)
                    Text("Precio vigente: S/ ${ConfiguracionPlanta.precioPorLitro}/L", style = MaterialTheme.typography.bodySmall)
                    Text("Versión: 1.0.0", style = MaterialTheme.typography.bodySmall)
                    Text("BD: SQLite / SQLDelight (en desarrollo)", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
