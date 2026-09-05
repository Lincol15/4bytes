package com.example.acopiodeleche.ui.pantallas.admin

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acopiodeleche.domain.model.ConfiguracionPlanta
import com.example.acopiodeleche.domain.model.DatosMock
import com.example.acopiodeleche.domain.model.Rol
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.domain.model.Usuario
import kotlin.random.Random

private val VerdeHuata = Color(0xFF2E7D32)
private val RojoAdmin = Color(0xFFC62828)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val usuario = SesionActual.usuario
    var tabActual by remember { mutableStateOf(0) }
    val tabs = listOf("Dashboard", "Usuarios", "Productores", "Config")

    Column(modifier = modifier.fillMaxSize()) {

        // Encabezado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(RojoAdmin)
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Administración",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ecolácteos Huata · ${usuario?.nombreCompleto ?: "Admin"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            Text(
                text = "Salir",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clickable { onCerrarSesion() }
                    .padding(4.dp),
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.labelMedium
            )
        }

        TabRow(selectedTabIndex = tabActual) {
            tabs.forEachIndexed { i, titulo ->
                Tab(selected = tabActual == i, onClick = { tabActual = i }, text = { Text(titulo) })
            }
        }

        when (tabActual) {
            0 -> DashboardAdmin()
            1 -> UsuariosTab()
            2 -> com.example.acopiodeleche.ui.pantallas.productores.ProductoresScreen()
            3 -> ConfiguracionTab()
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
    var usuarios by remember { mutableStateOf(DatosMock.usuarios.toList()) }
    var mostrarFormulario by remember { mutableStateOf(false) }

    if (mostrarFormulario) {
        FormularioUsuario(
            alGuardar = { nuevo ->
                DatosMock.usuarios.add(nuevo)
                usuarios = DatosMock.usuarios.toList()
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
                    "${usuarios.size} usuarios registrados",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = { mostrarFormulario = true },
                    colors = ButtonDefaults.buttonColors(containerColor = RojoAdmin)
                ) { Text("+ Nuevo usuario") }
            }
            HorizontalDivider()
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(usuarios) { u ->
                    TarjetaUsuario(
                        usuario = u,
                        onDesactivar = {
                            val idx = DatosMock.usuarios.indexOfFirst { it.id == u.id }
                            if (idx != -1) {
                                DatosMock.usuarios[idx] = u.copy(estado = !u.estado)
                                usuarios = DatosMock.usuarios.toList()
                            }
                        }
                    )
                }
            }
        }
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
