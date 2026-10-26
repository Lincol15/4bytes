package com.example.acopiodeleche

import androidx.compose.ui.window.ComposeUIViewController
import com.example.acopiodeleche.data.remote.ApiConfig
import platform.Foundation.NSBundle
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    // URL del backend definida en Info.plist (clave API_BASE_URL); si falta, se usa la de desarrollo.
    (NSBundle.mainBundle.objectForInfoDictionaryKey("API_BASE_URL") as? String)
        ?.takeIf { it.isNotBlank() }
        ?.let { ApiConfig.baseUrl = it }

    return ComposeUIViewController { App() }
}
