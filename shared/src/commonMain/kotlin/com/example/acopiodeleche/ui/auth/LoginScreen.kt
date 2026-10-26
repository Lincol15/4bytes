package com.example.acopiodeleche.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.acopiodeleche.domain.model.Usuario
import com.example.acopiodeleche.ui.tema.HuataColores
import com.example.acopiodeleche.ui.tema.LogoHuata
import com.example.acopiodeleche.ui.tema.MarcaHuata
import com.example.acopiodeleche.ui.tema.Tono
import org.koin.compose.viewmodel.koinViewModel

/** Fondo del panel de marca del login web: verde muy oscuro que se aclara hacia abajo. */
private val DegradadoLogin = Brush.linearGradient(
    listOf(Color(0xFF043521), HuataColores.Verde, Color(0xFF0B8F60)),
    start = Offset(0f, 0f),
    end = Offset(600f, 2000f)
)

@Composable
fun LoginScreen(
    onLoginExitoso: (Usuario) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel()
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    var correo            by remember { mutableStateOf("") }
    var contrasena        by remember { mutableStateOf("") }
    var mostrarContrasena by remember { mutableStateOf(false) }
    val errorMensaje      = estado.error.orEmpty()
    val cargando          = estado.cargando

    // Credenciales validadas por Laravel: se entra con el usuario devuelto por la API.
    LaunchedEffect(estado.usuario) {
        estado.usuario?.let(onLoginExitoso)
    }

    val formularioValido = correo.isNotBlank() && contrasena.isNotBlank()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DegradadoLogin)
            .drawBehind {
                // Brillo dorado de la esquina, como en el panel de marca de la web
                drawCircle(
                    Brush.radialGradient(
                        listOf(HuataColores.Dorado.copy(alpha = 0.22f), Color.Transparent),
                        center = Offset(size.width, 0f), radius = size.width * 0.9f
                    ),
                    radius = size.width * 0.9f, center = Offset(size.width, 0f)
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 22.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Marca
            Row(verticalAlignment = Alignment.CenterVertically) {
                LogoHuata(tamano = 56.dp, forma = RoundedCornerShape(50), modifier = Modifier.shadow(10.dp, RoundedCornerShape(50)))
                Spacer(Modifier.width(12.dp))
                Column {
                    MarcaHuata(estilo = MaterialTheme.typography.titleLarge.copy(fontSize = 21.sp), colorBase = Color.White)
                    Text("Sistema de Gestión y Acopio", color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(
                buildAnnotatedString {
                    append("Del productor a tu mesa, ")
                    withStyle(SpanStyle(color = HuataColores.Dorado)) { append("con calidad garantizada.") }
                },
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 28.sp, lineHeight = 32.sp, letterSpacing = (-0.8).sp),
                color = Color.White
            )
            Text(
                "Registra el acopio de leche, el control de calidad, la producción y las ventas desde un solo lugar, incluso sin internet.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.82f)
            )

            Spacer(Modifier.height(6.dp))

            // Tarjeta de login (panel del formulario de la web)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(22.dp))
                    .background(Color.White, RoundedCornerShape(22.dp))
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column {
                    Text("Iniciar sesión", style = MaterialTheme.typography.headlineSmall, color = HuataColores.Texto)
                    Text(
                        "Ingresa con tu correo electrónico o DNI para acceder a tu cuenta.",
                        style = MaterialTheme.typography.bodySmall,
                        color = HuataColores.TextoSuave,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                val coloresCampo = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HuataColores.Verde,
                    focusedLabelColor = HuataColores.Verde,
                    unfocusedBorderColor = HuataColores.Borde,
                    cursorColor = HuataColores.Verde
                )

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it; viewModel.limpiarError() },
                    label = { Text("Correo electrónico o DNI") },
                    leadingIcon = { Text("👤", fontSize = 16.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = errorMensaje.isNotEmpty(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = coloresCampo
                )

                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it; viewModel.limpiarError() },
                    label = { Text("Contraseña") },
                    leadingIcon = { Text("🔒", fontSize = 16.sp) },
                    singleLine = true,
                    visualTransformation = if (mostrarContrasena)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { mostrarContrasena = !mostrarContrasena }) {
                            Text(if (mostrarContrasena) "🙈" else "👁", fontSize = 18.sp)
                        }
                    },
                    isError = errorMensaje.isNotEmpty(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = coloresCampo
                )

                if (errorMensaje.isNotEmpty()) {
                    Text(
                        text = errorMensaje,
                        color = Tono.ROJO.texto,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Tono.ROJO.fondo, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }

                // Botón con el degradado verde de .btn-primary
                Button(
                    onClick = { viewModel.iniciarSesion(correo, contrasena) },
                    enabled = formularioValido && !cargando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .background(
                            if (formularioValido && !cargando) HuataColores.DegradadoPrimario
                            else Brush.linearGradient(listOf(HuataColores.Borde, HuataColores.Borde)),
                            RoundedCornerShape(12.dp)
                        ),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = HuataColores.TextoSuave
                    )
                ) {
                    if (cargando) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = HuataColores.Verde, strokeWidth = 2.dp)
                    } else {
                        Text("Ingresar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text(
                text = "© Ecolácteos Huata · Puno",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
        }
    }
}
