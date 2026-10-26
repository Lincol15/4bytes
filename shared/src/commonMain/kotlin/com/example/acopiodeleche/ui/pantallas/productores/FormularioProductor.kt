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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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

@Composable
fun FormularioProductor(
    alGuardar: (productor: Productor, correo: String, contrasena: String) -> Unit,
    alCancelar: () -> Unit,
    modifier: Modifier = Modifier,
    enviando: Boolean = false,
    mensajeError: String? = null,
    /** Si se indica, el formulario edita ese productor: no pide correo ni contraseña. */
    productorEditar: Productor? = null
) {
    val esEdicion = productorEditar != null

    // --- Estado por campo (precargado al editar) ---
    var nombres      by remember { mutableStateOf(productorEditar?.nombres.orEmpty()) }
    var apellidos    by remember { mutableStateOf(productorEditar?.apellidos.orEmpty()) }
    var dni          by remember { mutableStateOf(productorEditar?.dni.orEmpty()) }
    var telefono     by remember { mutableStateOf(productorEditar?.telefono.orEmpty()) }
    var direccion    by remember { mutableStateOf(productorEditar?.direccion.orEmpty()) }
    var comunidad    by remember { mutableStateOf(productorEditar?.comunidad.orEmpty()) }
    var correo       by remember { mutableStateOf("") }
    var contrasena   by remember { mutableStateOf("") }
    var estado       by remember { mutableStateOf(productorEditar?.estado ?: true) }

    // --- Validaciones derivadas (val, no var) ---
    val nombresVacio   = nombres.isBlank()
    val apellidosVacio = apellidos.isBlank()
    val dniVacio       = dni.isBlank()
    val dniInvalido    = dni.isNotBlank() && dni.length != 8
    val comunidadVacia = comunidad.isBlank()
    // Laravel crea la cuenta del productor: exige teléfono de 9 dígitos, correo y contraseña (mín. 6)
    val telefonoInvalido   = telefono.length != 9
    val correoInvalido     = correo.isNotBlank() && !correo.contains("@")
    val contrasenaInvalida = contrasena.isNotEmpty() && contrasena.length < 6
    // La cuenta (correo y contraseña) solo se crea al registrar
    val cuentaValida = esEdicion || (correo.isNotBlank() && !correoInvalido && contrasena.length >= 6)
    val formularioValido = !nombresVacio && !apellidosVacio && !dniVacio
                          && !dniInvalido && !comunidadVacia && !telefonoInvalido
                          && cuentaValida

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (esEdicion) "Editar productor" else "Registrar productor",
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
            label = { Text("Teléfono *") },
            isError = telefono.isNotEmpty() && telefonoInvalido,
            supportingText = { if (telefono.isNotEmpty() && telefonoInvalido) Text("El teléfono debe tener 9 dígitos") },
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

        if (!esEdicion) OutlinedTextField(
            value = correo,
            onValueChange = { correo = it.trim() },
            label = { Text("Correo (acceso del productor) *") },
            isError = correoInvalido,
            supportingText = { if (correoInvalido) Text("Correo inválido") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (!esEdicion) OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña inicial *") },
            isError = contrasenaInvalida,
            supportingText = { if (contrasenaInvalida) Text("Mínimo 6 caracteres") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (mensajeError != null) {
            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

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
                            idProductor = productorEditar?.idProductor.orEmpty(),   // al registrar lo asigna el backend
                            nombres     = nombres.trim(),
                            apellidos   = apellidos.trim(),
                            dni         = dni.trim(),
                            telefono    = telefono.trim(),
                            direccion   = direccion.trim(),
                            comunidad   = comunidad.trim(),
                            estado      = estado
                        ),
                        correo,
                        contrasena
                    )
                },
                enabled = formularioValido && !enviando,
                modifier = Modifier.weight(1f)
            ) { Text(if (enviando) "Guardando..." else "Guardar") }
        }
    }
}
