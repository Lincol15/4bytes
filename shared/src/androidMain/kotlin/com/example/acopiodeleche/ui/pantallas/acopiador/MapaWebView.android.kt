package com.example.acopiodeleche.ui.pantallas.acopiador

import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
actual fun MapaWebView(
    puntos: List<PuntoRuta>,
    modifier: Modifier
) {
    val html = generarHtmlMapa(puntos)

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    setSupportZoom(true)
                    builtInZoomControls = true
                    displayZoomControls = false
                    cacheMode = WebSettings.LOAD_DEFAULT
                }
                loadDataWithBaseURL(
                    "https://openstreetmap.org",
                    html,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(
                "https://openstreetmap.org",
                generarHtmlMapa(puntos),
                "text/html",
                "UTF-8",
                null
            )
        },
        modifier = modifier
    )
}
