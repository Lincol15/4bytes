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
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Registrar acopio", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        // ── Info automática del acopiador ────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = VerdeHuata.copy(alpha = 0.08f)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("👤", fontSize = 24.sp)
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        nombreAcopiador,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "🚛 $vehiculoAcopiador  •  📍 ${comunidadAcopiador ?: "Sin comunidad"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    "Auto",
                    style = MaterialTheme.typography.labelSmall,
                    color = VerdeHuata,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(VerdeHuata.copy(0.12f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // ── Buscar productor ─────────────────────────────────────────────
        Text(
            "Seleccionar productor",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = busqueda,
            onValueChange = {
                busqueda = it
                // Si borra la búsqueda y había uno seleccionado, lo mantiene
            },
            label = { Text("🔍 Buscar productor") },
            placeholder = { Text("Nombre, DNI o comunidad...") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Productor seleccionado — chip
        if (productorSeleccionado != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VerdeHuata.copy(0.1f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("✅", fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        productorSeleccionado!!.nombreCompleto,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = VerdeHuata
                    )
                    Text(
                        "${productorSeleccionado!!.comunidad}  •  DNI: ${productorSeleccionado!!.dni}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    "✕",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { productorSeleccionado = null; busqueda = "" }
                        .padding(4.dp)
                )
            }
        }

        // Lista de productores filtrados (solo si no hay uno seleccionado)
        if (productorSeleccionado == null) {
            if (productoresFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (productoresAsignados.isEmpty())
                            "No tienes productores asignados.\nContacta al administrador."
                        else "Sin resultados para \"$busqueda\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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

        HorizontalDivider()

        // ── Litros ───────────────────────────────────────────────────────
        OutlinedTextField(
            value = litrosTexto,
            onValueChange = { litrosTexto = it },
            label = { Text("Litros *") },
            suffix = { Text("L") },
            isError = litrosInvalido,
            supportingText = { if (litrosInvalido) Text("Ingresa un número mayor a 0") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // ── Observación ──────────────────────────────────────────────────
        OutlinedTextField(
            value = observacion,
            onValueChange = { observacion = it },
            label = { Text("Observación (opcional)") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(4.dp))

        // ── Botones ──────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = alCancelar,
                modifier = Modifier.weight(1f)
            ) { Text("Cancelar") }

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
                            zona        = productorSeleccionado!!.comunidad,
                            fecha       = ahora,
                            hora        = horaAhora,
                            litros      = litros ?: 0.0,
                            observacion = observacion.ifBlank { null },
                            recibido    = false
                        )
                    )
                },
                enabled = formularioValido,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeHuata)
            ) { Text("Guardar", fontWeight = FontWeight.Bold) }
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
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(VerdeHuata.copy(0.12f), RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Text("🌾", fontSize = 18.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    productor.nombreCompleto,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${productor.comunidad}  •  DNI: ${productor.dni}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text("›", fontSize = 20.sp, color = VerdeHuata)
        }
    }
}
