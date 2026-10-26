package com.example.acopiodeleche.ui.tema

import acopiodeleche.shared.generated.resources.Res
import acopiodeleche.shared.generated.resources.inter_bold
import acopiodeleche.shared.generated.resources.inter_medium
import acopiodeleche.shared.generated.resources.inter_regular
import acopiodeleche.shared.generated.resources.inter_semibold
import acopiodeleche.shared.generated.resources.poppins_bold
import acopiodeleche.shared.generated.resources.poppins_extrabold
import acopiodeleche.shared.generated.resources.poppins_semibold
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font

/**
 * Paleta de Ecolácteos Huata: los mismos valores que las variables CSS de la web
 * (web_acopio/resources/css/app.css), para que la app y el panel web se vean iguales.
 */
object HuataColores {
    val Verde = Color(0xFF075B3A)          // --primary
    val VerdeOscuro = Color(0xFF054529)    // --primary-dark
    val VerdeClaro = Color(0xFF0BAA72)     // --primary-light
    val Dorado = Color(0xFFF2C94C)         // --accent
    val DoradoOscuro = Color(0xFFD6A927)   // --accent-dark
    val Rojo = Color(0xFFC62828)           // --danger
    val RojoOscuro = Color(0xFFB71C1C)
    val Azul = Color(0xFF1565C0)           // --info
    val AzulOscuro = Color(0xFF0D47A1)
    val Morado = Color(0xFF6A1B9A)         // --purple
    val Naranja = Color(0xFFEF6C00)        // --orange
    val NaranjaOscuro = Color(0xFFE65100)
    val Cian = Color(0xFF0891B2)
    val Fondo = Color(0xFFF6FBF8)          // --bg
    val Tarjeta = Color.White              // --card
    val Texto = Color(0xFF24332D)          // --text
    val TextoSuave = Color(0xFF5C6E64)     // --text-light
    val Borde = Color(0xFFE3EEE8)          // --border

    // Menú lateral (sidebar)
    val SidebarArriba = Color(0xFF06492F)
    val SidebarAbajo = Color(0xFF043521)

    /** Degradado de los botones principales y del menú lateral, como en la web. */
    val DegradadoPrimario = Brush.linearGradient(listOf(Verde, VerdeOscuro))
    val DegradadoSidebar = Brush.verticalGradient(listOf(SidebarArriba, SidebarAbajo))
    val DegradadoTitulo = Brush.linearGradient(listOf(VerdeOscuro, VerdeClaro))
    val DegradadoAvatar = Brush.linearGradient(listOf(Dorado, DoradoOscuro))
}

/**
 * Tonos de las tarjetas, insignias y avisos (clases .green, .amber, .blue… de la web):
 * [fondo] claro, [texto] oscuro y [acento] para el círculo decorativo.
 */
enum class Tono(val fondo: Color, val texto: Color, val acento: Color) {
    VERDE(Color(0xFFD1FAE5), HuataColores.VerdeOscuro, HuataColores.Verde),
    DORADO(Color(0xFFFEF3C7), Color(0xFF8A6A0E), HuataColores.Dorado),
    AZUL(Color(0xFFDBEAFE), HuataColores.AzulOscuro, HuataColores.Azul),
    MORADO(Color(0xFFEDE9FE), Color(0xFF4A148C), HuataColores.Morado),
    ROJO(Color(0xFFFEE2E2), HuataColores.RojoOscuro, HuataColores.Rojo),
    NARANJA(Color(0xFFFED7AA), HuataColores.NaranjaOscuro, HuataColores.Naranja),
    CIAN(Color(0xFFCFFAFE), Color(0xFF155E75), Color(0xFF06B6D4)),
    GRIS(Color(0xFFF1F5F9), Color(0xFF475569), HuataColores.TextoSuave)
}

private val esquemaHuata = lightColorScheme(
    primary = HuataColores.Verde,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = HuataColores.VerdeOscuro,
    secondary = HuataColores.DoradoOscuro,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF5C4708),
    tertiary = HuataColores.Azul,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFDBEAFE),
    onTertiaryContainer = HuataColores.AzulOscuro,
    error = HuataColores.Rojo,
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = HuataColores.RojoOscuro,
    background = HuataColores.Fondo,
    onBackground = HuataColores.Texto,
    surface = HuataColores.Tarjeta,
    onSurface = HuataColores.Texto,
    surfaceVariant = Color(0xFFEFF6F2),
    onSurfaceVariant = HuataColores.TextoSuave,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainer = Color.White,
    surfaceContainerHigh = Color(0xFFF6FBF8),
    // Las Card de Material usan este color: blancas, como las tarjetas de la web
    surfaceContainerHighest = Color.White,
    outline = Color(0xFFC9DCD2),
    outlineVariant = HuataColores.Borde
)

/** Poppins para títulos y cifras; Inter para el texto (igual que la web). */
@Composable
private fun tipografiaHuata(): Typography {
    val poppins = FontFamily(
        Font(Res.font.poppins_semibold, FontWeight.SemiBold),
        Font(Res.font.poppins_bold, FontWeight.Bold),
        Font(Res.font.poppins_extrabold, FontWeight.ExtraBold)
    )
    val inter = FontFamily(
        Font(Res.font.inter_regular, FontWeight.Normal),
        Font(Res.font.inter_medium, FontWeight.Medium),
        Font(Res.font.inter_semibold, FontWeight.SemiBold),
        Font(Res.font.inter_bold, FontWeight.Bold)
    )
    val base = Typography()
    fun TextStyle.titulo(peso: FontWeight = FontWeight.Bold) = copy(fontFamily = poppins, fontWeight = peso, letterSpacing = (-0.3).sp)
    fun TextStyle.texto() = copy(fontFamily = inter)
    return Typography(
        displayLarge = base.displayLarge.titulo(FontWeight.ExtraBold),
        displayMedium = base.displayMedium.titulo(FontWeight.ExtraBold),
        displaySmall = base.displaySmall.titulo(FontWeight.ExtraBold),
        headlineLarge = base.headlineLarge.titulo(FontWeight.ExtraBold),
        headlineMedium = base.headlineMedium.titulo(FontWeight.ExtraBold),
        headlineSmall = base.headlineSmall.titulo(),
        titleLarge = base.titleLarge.titulo(),
        titleMedium = base.titleMedium.titulo(),
        titleSmall = base.titleSmall.titulo(FontWeight.SemiBold),
        bodyLarge = base.bodyLarge.texto(),
        bodyMedium = base.bodyMedium.texto(),
        bodySmall = base.bodySmall.texto(),
        labelLarge = base.labelLarge.copy(fontFamily = inter, fontWeight = FontWeight.SemiBold),
        labelMedium = base.labelMedium.copy(fontFamily = inter, fontWeight = FontWeight.SemiBold),
        labelSmall = base.labelSmall.copy(fontFamily = inter, fontWeight = FontWeight.SemiBold)
    )
}

/** Radios de la web: 8 (botón chico), 10 (botón/campo), 14, 18 (tarjetas y paneles). */
private val formasHuata = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun TemaHuata(contenido: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = esquemaHuata,
        typography = tipografiaHuata(),
        shapes = formasHuata,
        content = contenido
    )
}
