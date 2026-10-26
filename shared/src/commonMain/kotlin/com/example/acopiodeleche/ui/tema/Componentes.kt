package com.example.acopiodeleche.ui.tema

import acopiodeleche.shared.generated.resources.Res
import acopiodeleche.shared.generated.resources.logo_ecolacteos
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acopiodeleche.domain.model.Rol
import com.example.acopiodeleche.domain.model.SesionActual
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

// ─────────────────────────────────────────────────────────────────────────────
// Menú lateral y estructura de cada rol (copia del layout de la web)
// ─────────────────────────────────────────────────────────────────────────────

/** Opción del menú lateral. [seccion] agrupa opciones bajo un título ("Gestión", "Calidad"…). */
data class OpcionMenu(
    val icono: String,
    val titulo: String,
    val seccion: String = "Principal",
    val insignia: Int = 0
)

/** Nombre del rol como lo muestra la web. */
val Rol.etiqueta: String
    get() = when (this) {
        Rol.ADMINISTRADOR -> "Administrador"
        Rol.GERENTE -> "Gerente"
        Rol.ACOPIADOR -> "Acopiador"
        Rol.PRODUCTOR -> "Productor"
        Rol.CONTROL_CALIDAD -> "Control de calidad"
        Rol.TRABAJADOR_PLANTA -> "Trabajador de planta"
    }

/**
 * Estructura común de todas las pantallas principales: menú lateral verde (como el sidebar de la web),
 * barra superior con el botón de menú y el título de la sección, y el contenido sobre el fondo claro.
 */
@Composable
fun EsqueletoRol(
    opciones: List<OpcionMenu>,
    seleccion: Int,
    onSeleccionar: (Int) -> Unit,
    onCerrarSesion: () -> Unit,
    subtitulo: String? = null,
    modifier: Modifier = Modifier,
    contenido: @Composable BoxScope.() -> Unit
) {
    val estadoMenu = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var confirmarSalida by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = estadoMenu,
        modifier = modifier,
        scrimColor = Color(0x80041E14),
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(288.dp),
                drawerContainerColor = Color.Transparent,
                drawerShape = RoundedCornerShape(topEnd = 0.dp, bottomEnd = 0.dp),
                // El verde llega hasta la barra de estado; el contenido se separa con statusBarsPadding
                windowInsets = WindowInsets(0, 0, 0, 0)
            ) {
                MenuLateral(
                    opciones = opciones,
                    seleccion = seleccion,
                    onSeleccionar = { indice ->
                        onSeleccionar(indice)
                        scope.launch { estadoMenu.close() }
                    },
                    onCerrarSesion = {
                        scope.launch { estadoMenu.close() }
                        confirmarSalida = true
                    }
                )
            }
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(HuataColores.Fondo)
        ) {
            BarraSuperior(
                titulo = opciones.getOrNull(seleccion)?.titulo ?: "",
                subtitulo = subtitulo,
                onAbrirMenu = { scope.launch { estadoMenu.open() } }
            )
            Box(Modifier.fillMaxSize(), content = contenido)
        }
    }

    if (confirmarSalida) {
        AlertDialog(
            onDismissRequest = { confirmarSalida = false },
            icon = { Text("🚪", fontSize = 28.sp) },
            title = { Text("¿Cerrar sesión?") },
            text = { Text("Tendrá que volver a ingresar con su correo o DNI.") },
            confirmButton = {
                Button(
                    onClick = { confirmarSalida = false; onCerrarSesion() },
                    colors = ButtonDefaults.buttonColors(containerColor = HuataColores.Rojo)
                ) { Text("Cerrar sesión") }
            },
            dismissButton = { TextButton(onClick = { confirmarSalida = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun MenuLateral(
    opciones: List<OpcionMenu>,
    seleccion: Int,
    onSeleccionar: (Int) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val usuario = SesionActual.usuario
    Column(
        Modifier
            .fillMaxHeight()
            .background(HuataColores.DegradadoSidebar)
            .drawBehind {
                // Brillos verde y dorado de las esquinas, como en la web
                drawCircle(
                    Brush.radialGradient(
                        listOf(HuataColores.VerdeClaro.copy(alpha = 0.28f), Color.Transparent),
                        center = Offset.Zero, radius = size.width * 0.9f
                    ),
                    radius = size.width * 0.9f, center = Offset.Zero
                )
                drawCircle(
                    Brush.radialGradient(
                        listOf(HuataColores.Dorado.copy(alpha = 0.10f), Color.Transparent),
                        center = Offset(size.width, size.height), radius = size.width * 0.8f
                    ),
                    radius = size.width * 0.8f, center = Offset(size.width, size.height)
                )
            }
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Logo y marca
        Row(
            Modifier.padding(start = 18.dp, end = 18.dp, top = 22.dp, bottom = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LogoHuata(
                tamano = 44.dp,
                modifier = Modifier
                    .shadow(8.dp, RoundedCornerShape(14.dp))
                    .border(2.dp, HuataColores.Dorado.copy(alpha = 0.55f), RoundedCornerShape(14.dp))
            )
            Spacer(Modifier.width(12.dp))
            Column {
                MarcaHuata(estilo = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp), colorBase = Color.White)
                Text("Sistema de Gestión", color = Color.White.copy(alpha = 0.55f), fontSize = 11.sp)
            }
        }

        // Opciones agrupadas por sección
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp)
        ) {
            var seccionAnterior: String? = null
            opciones.forEachIndexed { indice, opcion ->
                if (opcion.seccion != seccionAnterior) {
                    seccionAnterior = opcion.seccion
                    Text(
                        opcion.seccion.uppercase(),
                        color = Color.White.copy(alpha = 0.42f),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.4.sp,
                        modifier = Modifier.padding(start = 12.dp, top = 18.dp, bottom = 8.dp)
                    )
                }
                ItemMenuLateral(opcion, activo = indice == seleccion) { onSeleccionar(indice) }
            }
        }

        // Usuario y cerrar sesión
        Column(
            Modifier
                .fillMaxWidth()
                .drawBehind { drawLine(Color.White.copy(alpha = 0.08f), Offset.Zero, Offset(size.width, 0f), 1.dp.toPx()) }
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.07f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarIniciales(usuario?.nombres, usuario?.apellidos)
                Spacer(Modifier.width(11.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        usuario?.nombreCompleto ?: "",
                        color = Color.White, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold,
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Text(usuario?.rol?.etiqueta ?: "", color = Color.White.copy(alpha = 0.55f), fontSize = 11.5.sp)
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(HuataColores.Rojo.copy(alpha = 0.12f))
                    .border(1.dp, Color(0x38FCA5A5), RoundedCornerShape(12.dp))
                    .clickable(onClick = onCerrarSesion),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Cerrar sesión", color = Color(0xFFFECACA), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ItemMenuLateral(opcion: OpcionMenu, activo: Boolean, onClick: () -> Unit) {
    val forma = RoundedCornerShape(12.dp)
    Box(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .clip(forma)
                .then(
                    if (activo) Modifier
                        .background(Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.16f), Color.White.copy(alpha = 0.06f))))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), forma)
                    else Modifier
                )
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(opcion.icono, fontSize = 17.sp, modifier = Modifier.width(26.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                opcion.titulo,
                color = if (activo) Color.White else Color.White.copy(alpha = 0.72f),
                fontSize = 14.sp,
                fontWeight = if (activo) FontWeight.SemiBold else FontWeight.Medium,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (opcion.insignia > 0) {
                Box(
                    Modifier
                        .background(HuataColores.Dorado, RoundedCornerShape(50))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text("${opcion.insignia}", color = HuataColores.SidebarAbajo, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
        // Barra dorada del ítem activo
        if (activo) {
            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-14).dp)
                    .width(4.dp)
                    .height(22.dp)
                    .background(HuataColores.Dorado, RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
            )
        }
    }
}

@Composable
private fun BarraSuperior(titulo: String, subtitulo: String?, onAbrirMenu: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(HuataColores.Fondo)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón de menú: blanco con borde, como .menu-toggle de la web
        Box(
            Modifier
                .size(42.dp)
                .shadow(3.dp, RoundedCornerShape(12.dp))
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(1.dp, HuataColores.Borde, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onAbrirMenu),
            contentAlignment = Alignment.Center
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(3) {
                    Box(Modifier.width(18.dp).height(2.dp).background(HuataColores.Verde, RoundedCornerShape(1.dp)))
                }
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            TituloDegradado(titulo, MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp, fontWeight = FontWeight.ExtraBold))
            if (subtitulo != null) {
                Text(subtitulo, style = MaterialTheme.typography.bodySmall, color = HuataColores.TextoSuave, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Piezas de marca
// ─────────────────────────────────────────────────────────────────────────────

/** Logo de Ecolácteos (el mismo de la web) sobre fondo blanco redondeado. */
@Composable
fun LogoHuata(tamano: Dp, modifier: Modifier = Modifier, forma: Shape = RoundedCornerShape(tamano * 0.32f)) {
    Image(
        painter = painterResource(Res.drawable.logo_ecolacteos),
        contentDescription = "Ecolácteos Huata",
        // Fit con margen: el logo entero (vaca y texto) cabe también en la versión circular
        contentScale = ContentScale.Fit,
        modifier = modifier
            .size(tamano)
            .clip(forma)
            .background(Color.White)
            .padding(tamano * 0.06f)
    )
}

/** Litros para tarjetas: sin decimales y con separador de miles a partir de 1 000 ("18 183 L"). */
fun formatoLitros(litros: Double): String {
    if (litros < 1000) return "${(litros * 10).toLong() / 10.0} L".replace(".0 L", " L")
    val entero = kotlin.math.round(litros).toLong().toString()
    return entero.reversed().chunked(3).joinToString(" ").reversed() + " L"
}

/** "Ecolácteos Huata" con "Huata" en dorado. */
@Composable
fun MarcaHuata(estilo: TextStyle, colorBase: Color, modifier: Modifier = Modifier) {
    Text(
        buildAnnotatedString {
            append("Ecolácteos")
            withStyle(SpanStyle(color = HuataColores.Dorado)) { append(" Huata") }
        },
        style = estilo,
        color = colorBase,
        modifier = modifier
    )
}

/** Título con el degradado verde de los encabezados de la web. */
@Composable
fun TituloDegradado(texto: String, estilo: TextStyle = MaterialTheme.typography.titleLarge, modifier: Modifier = Modifier) {
    Text(texto, style = estilo.copy(brush = HuataColores.DegradadoTitulo), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = modifier)
}

@Composable
fun AvatarIniciales(nombres: String?, apellidos: String?, tamano: Dp = 38.dp) {
    val iniciales = listOfNotNull(nombres?.firstOrNull(), apellidos?.firstOrNull()).joinToString("").uppercase()
    Box(
        Modifier
            .size(tamano)
            .background(HuataColores.DegradadoAvatar, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(iniciales.ifBlank { "?" }, color = HuataColores.SidebarAbajo, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tarjetas, paneles e insignias
// ─────────────────────────────────────────────────────────────────────────────

private val FormaTarjeta = RoundedCornerShape(18.dp)

/** Fondo blanco, radio 18 y sombra suave de las tarjetas de la web. */
fun Modifier.tarjetaHuata(forma: Shape = FormaTarjeta): Modifier =
    this
        .shadow(4.dp, forma, ambientColor = Color(0x1A000000), spotColor = Color(0x1A000000))
        .background(Color.White, forma)
        .clip(forma)

/** Tarjeta de estadística (.stat-card): etiqueta, cifra grande, ícono y círculo decorativo del tono. */
@Composable
fun TarjetaEstadistica(
    etiqueta: String,
    valor: String,
    icono: String,
    tono: Tono,
    modifier: Modifier = Modifier
) = TarjetaEstadisticaBase(etiqueta, valor, icono, tono.texto, tono.fondo, tono.acento, modifier)

/** Igual que la anterior para pantallas que ya trabajan con un color propio. */
@Composable
fun TarjetaEstadistica(
    etiqueta: String,
    valor: String,
    icono: String,
    color: Color,
    modifier: Modifier = Modifier
) = TarjetaEstadisticaBase(etiqueta, valor, icono, color, color.copy(alpha = 0.14f), color, modifier)

@Composable
private fun TarjetaEstadisticaBase(
    etiqueta: String,
    valor: String,
    icono: String,
    colorValor: Color,
    fondoIcono: Color,
    acento: Color,
    modifier: Modifier
) {
    Box(
        modifier
            .tarjetaHuata()
            .drawBehind {
                drawCircle(
                    color = acento.copy(alpha = 0.12f),
                    radius = 50.dp.toPx(),
                    center = Offset(size.width - 15.dp.toPx(), 15.dp.toPx())
                )
            }
            .padding(16.dp)
    ) {
        Column(Modifier.padding(end = 44.dp)) {
            Text(
                etiqueta.uppercase(),
                fontSize = 11.sp, letterSpacing = 0.5.sp, fontWeight = FontWeight.Medium,
                color = HuataColores.TextoSuave, maxLines = 2, lineHeight = 14.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                valor,
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.8).sp),
                color = colorValor, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .size(40.dp)
                .background(fondoIcono, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) { Text(icono, fontSize = 18.sp) }
    }
}

/** Indicador compacto y centrado para filas de tres (misma tarjeta blanca y colores de la web). */
@Composable
fun TarjetaIndicador(icono: String, valor: String, etiqueta: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier
            .tarjetaHuata(RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.size(34.dp).background(color.copy(alpha = 0.14f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) { Text(icono, fontSize = 16.sp) }
        Spacer(Modifier.height(6.dp))
        Text(
            valor,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = color, maxLines = 1, overflow = TextOverflow.Ellipsis
        )
        Text(
            etiqueta,
            fontSize = 11.sp, color = HuataColores.TextoSuave, maxLines = 2,
            lineHeight = 13.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

/** Panel (.panel) con encabezado opcional separado por una línea. */
@Composable
fun PanelHuata(
    modifier: Modifier = Modifier,
    titulo: String? = null,
    icono: String? = null,
    acciones: @Composable RowScope.() -> Unit = {},
    contenido: @Composable ColumnScope.() -> Unit
) {
    Column(modifier.fillMaxWidth().tarjetaHuata()) {
        if (titulo != null) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icono != null) {
                    Text(icono, fontSize = 17.sp)
                    Spacer(Modifier.width(10.dp))
                }
                Text(titulo, style = MaterialTheme.typography.titleSmall.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold), modifier = Modifier.weight(1f))
                acciones()
            }
            HorizontalDivider(color = HuataColores.Borde)
        }
        Column(Modifier.padding(18.dp), content = contenido)
    }
}

/** Insignia redondeada (.badge) del tono indicado. */
@Composable
fun Insignia(texto: String, tono: Tono, modifier: Modifier = Modifier) {
    Text(
        texto,
        color = tono.texto,
        fontSize = 11.5.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        modifier = modifier
            .background(tono.fondo, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

/** Estado vacío (.empty): ícono grande atenuado, título y explicación. */
@Composable
fun EstadoVacio(icono: String, titulo: String, mensaje: String? = null, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxWidth().padding(vertical = 48.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icono, fontSize = 52.sp, modifier = Modifier.padding(bottom = 12.dp), color = Color.Black.copy(alpha = 0.4f))
        Text(titulo, style = MaterialTheme.typography.titleSmall.copy(fontSize = 17.sp), color = HuataColores.Texto)
        if (mensaje != null) {
            Text(mensaje, style = MaterialTheme.typography.bodySmall, color = HuataColores.TextoSuave, modifier = Modifier.padding(top = 6.dp))
        }
    }
}

/** Punto de color con texto (.status-pill). */
@Composable
fun EstadoPunto(texto: String, tono: Tono) {
    Row(
        Modifier.background(tono.fondo, CircleShape).padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(7.dp).background(tono.texto, CircleShape))
        Spacer(Modifier.width(6.dp))
        Text(texto, color = tono.texto, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
