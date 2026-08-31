package com.example.acopiodeleche.ui.pantallas.acopio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.example.acopiodeleche.domain.model.DatosMock
import com.example.acopiodeleche.domain.model.Productor
import com.example.acopiodeleche.domain.model.RegistroAcopio
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioAcopio(
    alGuardar: (RegistroAcopio) -> Unit,
    alCancelar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val productores = DatosMock.productores.filter { it.estado }
    val vehiculos   = DatosMock.vehiculos

    // --- Estado por campo ---
    var productorSeleccionado  by remember { mutableStateOf<Productor?>(null) }
    var vehiculoSeleccionado   by remember { mutableStateOf("") }
    var acopiador              by remember { mutableStateOf("") }
    var zona                   by remember { mutableStateOf("") }
    var fecha                  by remember { mutableStateOf("") }
    var hora                   by remember { mutableStateOf("") }
    var litrosTexto            by remember { mutableStateOf("") }
    var observacion            by remember { mutableStateOf("") }

    // Dropdowns
    var dropProductorExpanded  by remember { mutableStateOf(false) }
    var dropVehiculoExpanded   by remember { mutableStateOf(false) }

    // --- Validaciones derivadas ---
    val litros         = litrosTexto.toDoubleOrNull()
    val litrosInvalido = litrosTexto.isNotBlank() && (litros == null || litros <= 0)
    val formularioValido = productorSeleccionado != null
            && vehiculoSeleccionado.isNotBlank()
            && acopiador.isNotBlank()
            && fecha.isNotBlank()
            && hora.isNotBlank()
            && litros != null && litros > 0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Registrar acopio", style = MaterialTheme.typography.headlineSmall)

        // 1. Selección de productor
        ExposedDropdownMenuBox(
            expanded = dropProductorExpanded,
            onExpandedChange = { dropProductorExpanded = it }
        ) {
            OutlinedTextField(
                value = productorSeleccionado?.nombreCompleto ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Productor *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dropProductorExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = dropProductorExpanded,
                onDismissRequest = { dropProductorExpanded = false }
            ) {
                productores.forEach { p ->
                    DropdownMenuItem(
                        text = { Text("${p.nombreCompleto} — ${p.comunidad}") },
                        onClick = {
                            productorSeleccionado = p
                            zona = p.comunidad
                            dropProductorExpanded = false
                        }
                    )
                }
            }
        }

        // 2. Selección de vehículo
        ExposedDropdownMenuBox(
            expanded = dropVehiculoExpanded,
            onExpandedChange = { dropVehiculoExpanded = it }
        ) {
            OutlinedTextField(
                value = vehiculoSeleccionado,
                onValueChange = {},
                readOnly = true,
                label = { Text("Vehículo *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dropVehiculoExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = dropVehiculoExpanded,
                onDismissRequest = { dropVehiculoExpanded = false }
            ) {
                vehiculos.forEach { v ->
                    DropdownMenuItem(
                        text = { Text(v) },
                        onClick = {
                            vehiculoSeleccionado = v
                            dropVehiculoExpanded = false
                        }
                    )
                }
            }
        }

        // 3. Acopiador
        OutlinedTextField(
            value = acopiador,
            onValueChange = { acopiador = it },
            label = { Text("Acopiador *") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // 4. Litros
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

        // 5. Fecha y hora en fila
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it },
                label = { Text("Fecha *") },
                placeholder = { Text("dd/MM/yyyy") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = hora,
                onValueChange = { hora = it },
                label = { Text("Hora *") },
                placeholder = { Text("HH:mm") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        // 6. Observación
        OutlinedTextField(
            value = observacion,
            onValueChange = { observacion = it },
            label = { Text("Observación") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Botones
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
                    alGuardar(
                        RegistroAcopio(
                            id           = "a-${Random.nextInt(1000, 9999)}",
                            idProductor  = productorSeleccionado!!.idProductor,
                            acopiador    = acopiador.trim(),
                            vehiculo     = vehiculoSeleccionado,
                            zona         = zona.ifBlank { productorSeleccionado!!.comunidad },
                            fecha        = fecha.trim(),
                            hora         = hora.trim(),
                            litros       = litros ?: 0.0,
                            observacion  = observacion.ifBlank { null },
                            recibido     = false
                        )
                    )
                },
                enabled = formularioValido,
                modifier = Modifier.weight(1f)
            ) { Text("Guardar") }
        }
    }
}
