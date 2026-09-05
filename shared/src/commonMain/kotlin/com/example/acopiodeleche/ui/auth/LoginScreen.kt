package com.example.acopiodeleche.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acopiodeleche.domain.model.DatosMock
import com.example.acopiodeleche.domain.model.Rol
import com.example.acopiodeleche.domain.model.Usuario
import com.example.acopiodeleche.domain.service.AuthService

// Colores de marca Ecolácteos Huata
private val VerdeHuata = Color(0xFF2E7D32)
private val AzulHuata = Color(0xFF1565C0)
private val AmarilloHuata = Color(0xFFF9A825)
private val VerdeClaro = Color(0xFF66BB6A)

@Composable
fun LoginScreen(
    onLoginExitoso: (Usuario) -> Unit,
    modifier: Modifier = Modifier
) {
    val authService = remember {
        AuthService(DatosMock.usuarios).also { svc ->
            // Los usuarios ya están en DatosMock, no hace falta registrar
        }
    }

    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mostrarContrasena by remember { mutableStateOf(false) }
    var errorMensaje by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    val correoVacio = correo.isBlank()
    val contrasenaVacia = contrasena.isBlank()
    val formularioValido = !correoVacio && !contrasenaVacia

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(VerdeHuata, AzulHuata)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Logo / Encabezado ──────────────────────────────────
            Spacer(modifier = Modifier.height(40.dp))

            // Ícono de vaca (placeholder — luego agregas imagen real)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.White, shape = RoundedCornerShape(40.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🐄",
                    fontSize = 40.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "ECOLÁCTEOS HUATA",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Disfrute lo Natural",
                style = MaterialTheme.typography.bodyMedium,
                color = AmarilloHuata,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Tarjeta de login ───────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Iniciar sesión",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = VerdeHuata
                    )

                    // Campo correo
                    OutlinedTextField(
                        value = correo,
                        onValueChange = {
                            correo = it
                            errorMensaje = ""
                        },
                        label = { Text("Correo electrónico") },
                        placeholder = { Text("usuario@ecolacteos.com") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = errorMensaje.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VerdeHuata,
                            focusedLabelColor = VerdeHuata
                        )
                    )

                    // Campo contraseña
                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = {
                            contrasena = it
                            errorMensaje = ""
                        },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = if (mostrarContrasena)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { mostrarContrasena = !mostrarContrasena }) {
                                Text(
                                    text = if (mostrarContrasena) "👁" else "🔒",
                                    fontSize = 18.sp
                                )
                            }
                        },
                        isError = errorMensaje.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VerdeHuata,
                            focusedLabelColor = VerdeHuata
                        )
                    )

                    // Error
                    if (errorMensaje.isNotEmpty()) {
                        Text(
                            text = errorMensaje,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Botón login
                    Button(
                        onClick = {
                            cargando = true
                            errorMensaje = ""
                            val usuario = authService.login(correo.trim(), contrasena)
                            cargando = false
                            if (usuario != null) {
                                onLoginExitoso(usuario)
                            } else {
                                errorMensaje = "Correo o contraseña incorrectos. Verifica tus datos."
                            }
                        },
                        enabled = formularioValido && !cargando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerdeHuata
                        )
                    ) {
                        if (cargando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Ingresar",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Hint para pruebas
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.15f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Cuentas de prueba:",
                        style = MaterialTheme.typography.labelMedium,
                        color = AmarilloHuata,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    CuentaPrueba("admin@ecolacteos.com", "admin123", "Administrador")
                    CuentaPrueba("carlos@ecolacteos.com", "acopio123", "Acopiador")
                    CuentaPrueba("pedro@productor.com", "pedro123", "Productor")
                    CuentaPrueba("calidad@ecolacteos.com", "calidad123", "Calidad")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun CuentaPrueba(correo: String, pass: String, rol: String) {
    Text(
        text = "• $rol: $correo / $pass",
        style = MaterialTheme.typography.bodySmall,
        color = Color.White.copy(alpha = 0.85f)
    )
}
