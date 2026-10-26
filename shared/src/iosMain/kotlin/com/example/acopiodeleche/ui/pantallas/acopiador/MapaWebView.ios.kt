package com.example.acopiodeleche.ui.pantallas.acopiador

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectMake
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration

/**
 * Mapa en iOS: WKWebView nativo (WebKit) con el mismo HTML de Leaflet + OpenStreetMap
 * que genera [generarHtmlMapa] en el código común.
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapaWebView(
    puntos: List<PuntoRuta>,
    rutaActiva: RutaAcopio?,
    modifier: Modifier
) {
    val html = remember(puntos, rutaActiva) { generarHtmlMapa(puntos, rutaActiva) }
    UIKitView(
        factory = {
            WKWebView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0), configuration = WKWebViewConfiguration())
        },
        modifier = modifier,
        update = { webView -> webView.loadHTMLString(html, baseURL = null) }
    )
}
