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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.acopiodeleche.domain.model.Usuario
import com.example.acopiodeleche.domain.service.AuthService

private val VerdeHuata   = Color(0xFF2E7D32)
private val AzulHuata    = Color(0xFF1565C0)
private val AmarilloHuata = Color(0xFFF9A825)

@Composable
fun LoginScreen(
    onLoginExitoso: (Usuario) -> Unit,
    modifier: Modifier = Modifier
) {
    val authService = remember { AuthService(DatosMock.usuarios) }

    var correo            by remember { mutableStateOf("") }
    var contrasena        by remember { mutableStateOf("") }
    var mostrarContrasena by remember { mutableStateOf(false) }
    var errorMensaje      by remember { mutableStateOf("") }
    var cargando          by remember { mutableStateOf(false) }

    val formularioValido = correo.isNotBlank() && contrasena.isNotBlank()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(VerdeHuata, AzulHuata)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Logo
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(Color.White, RoundedCornerShape(44.dp)),
                contentAlignment = Alignment.Center
            ) { Text("🐄", fontSize = 44.sp) }

            Text(
                text = "ECOLÁCTEOS HUATA",
                style = MaterialTheme.typography.headlineSmall,
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

            Spacer(Modifier.height(8.dp))

            // Tarjeta de login
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp),
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

                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it; errorMensaje = "" },
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = errorMensaje.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VerdeHuata,
                            focusedLabelColor  = VerdeHuata
                        )
                    )

                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = { contrasena = it; errorMensaje = "" },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = if (mostrarContrasena)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { mostrarContrasena = !mostrarContrasena }) {
                                Text(if (mostrarContrasena) "👁" else "🔒", fontSize = 18.sp)
                            }
                        },
                        isError = errorMensaje.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VerdeHuata,
                            focusedLabelColor  = VerdeHuata
                        )
                    )

                    if (errorMensaje.isNotEmpty()) {
                        Text(
                            text = errorMensaje,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        onClick = {
                            cargando = true
                            errorMensaje = ""
                            val usuario = authService.login(correo.trim(), contrasena)
                            cargando = false
                            if (usuario != null) onLoginExitoso(usuario)
                            else errorMensaje = "Correo o contraseña incorrectos."
                        },
                        enabled = formularioValido && !cargando,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeHuata)
                    ) {
                        if (cargando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Ingresar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Sistema de Gestión · Ecolácteos Huata",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}
