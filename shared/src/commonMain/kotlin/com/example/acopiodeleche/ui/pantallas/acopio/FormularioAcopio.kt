package com.example.acopiodeleche.ui.pantallas.acopio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acopiodeleche.domain.model.DatosMock
import com.example.acopiodeleche.domain.model.Productor
import com.example.acopiodeleche.domain.model.RegistroAcopio
import com.example.acopiodeleche.domain.model.SesionActual
import com.example.acopiodeleche.ui.components.FechaData
import com.example.acopiodeleche.ui.components.HoraData
import kotlin.random.Random

private val VerdeHuata = Color(0xFF2E7D32)

@Composable
fun FormularioAcopio(
    alGuardar: (RegistroAcopio) -> Unit,
    alCancelar: () -> Unit,
    modifier: Modifier = Modifier
) {
    // ── Datos automáticos desde la sesión ────────────────────────────────
    val usuarioActual = SesionActual.usuario
    val nombreAcopiador = usuarioActual?.nombreCompleto ?: "Acopiador"
    val vehiculoAcopiador = usuarioActual?.vehiculo ?: "Sin vehículo asignado"
    val comunidadAcopiador = usuarioActual?.comunidad

    // Productores asignados a este acopiador (desde asignaciones del admin)
    val asignacion = DatosMock.asignaciones
        .find { it.idAcopiador == usuarioActual?.id }
    val productoresAsignados: List<Productor> = if (asignacion != null) {
        DatosMock.productores.filter { p ->
            p.estado && asignacion.idsProductores.contains(p.idProductor)
        }
    } else {
        // fallback: todos de su comunidad
        DatosMock.productores.filter { p ->
            p.estado && (comunidadAcopiador == null || p.comunidad == comunidadAcopiador)
        }
    }

    // ── Estado del formulario ────────────────────────────────────────────
    var busqueda                by remember { mutableStateOf("") }
    var productorSeleccionado   by remember { mutableStateOf<Productor?>(null) }
    var litrosTexto             by remember { mutableStateOf("") }
    var observacion             by remember { mutableStateOf("") }

    // Filtro de búsqueda
    val productoresFiltrados = remember(busqueda, productoresAsignados) {
        if (busqueda.isBlank()) productoresAsignados
        else productoresAsignados.filter { p ->
            p.nombreCompleto.contains(busqueda, ignoreCase = true) ||
            p.dni.contains(busqueda) ||
            p.comunidad.contains(busqueda, ignoreCase = true)
        }
    }

    val litros         = litrosTexto.toDoubleOrNull()
    val litrosInvalido = litrosTexto.isNotBlank() && (litros == null || litros <= 0)
    val formularioValido = productorSeleccionado != null
            && litros != null && litros > 0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        // ── Encabezado con gradiente ─────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(VerdeHuata, VerdeHuata.copy(alpha = 0.85f))
                    )
                )
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Column {
                Text(
                    "📝 Registrar Acopio",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Completa la información del acopio de leche",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Info automática del acopiador ────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = VerdeHuata.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(VerdeHuata.copy(0.15f), RoundedCornerShape(50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 24.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            nombreAcopiador,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = VerdeHuata
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "🚛 $vehiculoAcopiador",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "📍 ${comunidadAcopiador ?: "Sin comunidad"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .background(VerdeHuata.copy(0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "Auto",
                            style = MaterialTheme.typography.labelMedium,
                            color = VerdeHuata,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // ── Buscar productor ─────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "1",
                            modifier = Modifier
                                .size(28.dp)
                                .background(VerdeHuata, RoundedCornerShape(50))
                                .padding(4.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Seleccionar Productor",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = busqueda,
                        onValueChange = {
                            busqueda = it
                        },
                        label = { Text("Buscar productor") },
                        placeholder = { Text("Nombre, DNI o comunidad...") },
                        leadingIcon = { Text("🔍", fontSize = 20.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Productor seleccionado — chip mejorado
                    if (productorSeleccionado != null) {
                        Spacer(Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = VerdeHuata.copy(0.12f)),
                            shape = RoundedCornerShape(10.dp),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(VerdeHuata.copy(0.2f), RoundedCornerShape(50)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("✅", fontSize = 20.sp)
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        productorSeleccionado!!.nombreCompleto,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = VerdeHuata
                                    )
                                    Text(
                                        "${productorSeleccionado!!.comunidad}  •  DNI: ${productorSeleccionado!!.dni}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(Color.Red.copy(0.1f), RoundedCornerShape(50))
                                        .clickable { productorSeleccionado = null; busqueda = "" },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "✕",
                                        color = Color.Red,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Lista de productores filtrados (solo si no hay uno seleccionado)
            if (productorSeleccionado == null) {
                if (productoresFiltrados.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🔍", fontSize = 40.sp)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    if (productoresAsignados.isEmpty())
                                        "No tienes productores asignados"
                                    else "Sin resultados para \"$busqueda\"",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                if (productoresAsignados.isEmpty()) {
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "Contacta al administrador",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "${productoresFiltrados.size} productor${if(productoresFiltrados.size != 1) "es" else ""} disponible${if(productoresFiltrados.size != 1) "s" else ""}",
                            style = MaterialTheme.typography.labelLarge,
                            color = VerdeHuata,
                            fontWeight = FontWeight.Bold
                        )
                        productoresFiltrados.forEach { p ->
                            TarjetaProductorSeleccionable(
                                productor = p,
                                onSeleccionar = {
                                    productorSeleccionado = p
                                    busqueda = ""
                                }
                            )
                        }
                    }
                }
            }

            // ── Zona automática (sin selector) ───────────────────────────────────
            if (productorSeleccionado != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = VerdeHuata.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(VerdeHuata.copy(0.15f), RoundedCornerShape(50)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📍", fontSize = 22.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Zona / Comunidad",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                productorSeleccionado!!.comunidad,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = VerdeHuata
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(VerdeHuata.copy(0.15f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                "Auto",
                                style = MaterialTheme.typography.labelMedium,
                                color = VerdeHuata,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // ── Datos del acopio ─────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "2",
                            modifier = Modifier
                                .size(28.dp)
                                .background(VerdeHuata, RoundedCornerShape(50))
                                .padding(4.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Información del Acopio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Litros
                    OutlinedTextField(
                        value = litrosTexto,
                        onValueChange = { litrosTexto = it },
                        label = { Text("Cantidad en Litros *") },
                        leadingIcon = { Text("🥛", fontSize = 20.sp) },
                        suffix = { 
                            Text(
                                "L", 
                                fontWeight = FontWeight.Bold,
                                color = VerdeHuata
                            ) 
                        },
                        isError = litrosInvalido,
                        supportingText = { 
                            if (litrosInvalido) {
                                Text("⚠️ Ingresa un número mayor a 0", color = Color.Red)
                            } else {
                                Text("Ingresa la cantidad de leche acopiada")
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    // Observación
                    OutlinedTextField(
                        value = observacion,
                        onValueChange = { observacion = it },
                        label = { Text("Observaciones (opcional)") },
                        leadingIcon = { Text("📝", fontSize = 20.sp) },
                        placeholder = { Text("Calidad, temperatura, incidencias...") },
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            // ── Botones ──────────────────────────────────────────────────────
            Spacer(Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = alCancelar,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) { 
                    Text(
                        "Cancelar",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    ) 
                }

                Button(
                    onClick = {
                        val ahora = FechaData.hoy().formateada()
                        val horaAhora = HoraData.ahora().formateada()
                        alGuardar(
                            RegistroAcopio(
                                id          = "a-${Random.nextInt(1000, 9999)}",
                                idProductor = productorSeleccionado!!.idProductor,
                                acopiador   = nombreAcopiador,
                                vehiculo    = vehiculoAcopiador,
                                zona        = productorSeleccionado!!.comunidad, // Zona automática del productor
                                fecha       = ahora,
                                hora        = horaAhora,
                                litros      = litros ?: 0.0,
                                observacion = observacion.ifBlank { null },
                                recibido    = false
                            )
                        )
                    },
                    enabled = formularioValido,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeHuata),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 3.dp,
                        pressedElevation = 6.dp,
                        disabledElevation = 0.dp
                    )
                ) { 
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "✓",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Guardar Acopio",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Tarjeta de productor seleccionable ───────────────────────────────────────

@Composable
private fun TarjetaProductorSeleccionable(
    productor: Productor,
    onSeleccionar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSeleccionar() },
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(VerdeHuata.copy(0.12f), RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Text("🌾", fontSize = 22.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    productor.nombreCompleto,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        productor.comunidad,
                        style = MaterialTheme.typography.bodySmall,
                        color = VerdeHuata,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "  •  ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "DNI: ${productor.dni}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(VerdeHuata.copy(0.1f), RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Text("›", fontSize = 24.sp, color = VerdeHuata, fontWeight = FontWeight.Bold)
            }
        }
    }
}
