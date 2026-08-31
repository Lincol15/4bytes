package com.example.acopiodeleche.ui.pantallas.productores

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.acopiodeleche.domain.model.Productor
import kotlin.random.Random

@Composable
fun FormularioProductor(
    alGuardar: (Productor) -> Unit,
    alCancelar: () -> Unit,
    modifier: Modifier = Modifier
) {
    // --- Estado por campo ---
    var nombres      by remember { mutableStateOf("") }
    var apellidos    by remember { mutableStateOf("") }
    var dni          by remember { mutableStateOf("") }
    var telefono     by remember { mutableStateOf("") }
    var direccion    by remember { mutableStateOf("") }
    var comunidad    by remember { mutableStateOf("") }
    var estado       by remember { mutableStateOf(true) }

    // --- Validaciones derivadas (val, no var) ---
    val nombresVacio   = nombres.isBlank()
    val apellidosVacio = apellidos.isBlank()
    val dniVacio       = dni.isBlank()
    val dniInvalido    = dni.isNotBlank() && dni.length != 8
    val comunidadVacia = comunidad.isBlank()
    val formularioValido = !nombresVacio && !apellidosVacio && !dniVacio
                          && !dniInvalido && !comunidadVacia

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Registrar productor",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = nombres,
            onValueChange = { nombres = it },
            label = { Text("Nombres *") },
            isError = nombresVacio && nombres.isEmpty().not(),
            supportingText = { if (nombresVacio && nombres.isEmpty().not()) Text("Campo obligatorio") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = apellidos,
            onValueChange = { apellidos = it },
            label = { Text("Apellidos *") },
            isError = apellidosVacio && apellidos.isEmpty().not(),
            supportingText = { if (apellidosVacio && apellidos.isEmpty().not()) Text("Campo obligatorio") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = dni,
            onValueChange = { if (it.length <= 8) dni = it.filter { c -> c.isDigit() } },
            label = { Text("DNI *") },
            isError = dniInvalido,
            supportingText = {
                when {
                    dniInvalido -> Text("El DNI debe tener 8 dígitos")
                    dniVacio && dni.isEmpty().not() -> Text("Campo obligatorio")
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = telefono,
            onValueChange = { if (it.length <= 9) telefono = it.filter { c -> c.isDigit() } },
            label = { Text("Teléfono") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = comunidad,
            onValueChange = { comunidad = it },
            label = { Text("Comunidad / Zona *") },
            isError = comunidadVacia && comunidad.isEmpty().not(),
            supportingText = { if (comunidadVacia && comunidad.isEmpty().not()) Text("Campo obligatorio") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Switch de estado
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Productor activo", modifier = Modifier.weight(1f))
            Switch(checked = estado, onCheckedChange = { estado = it })
        }

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
                        Productor(
                            idProductor = "p-${Random.nextInt(1000, 9999)}",
                            nombres     = nombres.trim(),
                            apellidos   = apellidos.trim(),
                            dni         = dni.trim(),
                            telefono    = telefono.trim(),
                            direccion   = direccion.trim(),
                            comunidad   = comunidad.trim(),
                            estado      = estado
                        )
                    )
                },
                enabled = formularioValido,
                modifier = Modifier.weight(1f)
            ) { Text("Guardar") }
        }
    }
}
