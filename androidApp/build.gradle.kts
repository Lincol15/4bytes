import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}
dependencies {
    implementation(project(":shared"))

    implementation(libs.androidx.activity.compose)

    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
}

// URL base de la API Laravel. Se define en local.properties (no versionado) para no fijarla en el código:
//   api.baseUrl.debug=http://192.168.1.50:8000/api/v1/   (celular físico en la misma red)
//   api.baseUrl.release=https://tu-dominio/api/v1/
// Por defecto, debug apunta al emulador de Android (10.0.2.2 = localhost del equipo).
val localProperties = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}
fun apiBaseUrl(key: String, default: String): String =
    "\"" + (localProperties.getProperty(key) ?: default) + "\""

android {
    namespace = "com.example.acopiodeleche"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.acopiodeleche"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        debug {
            buildConfigField("String", "API_BASE_URL", apiBaseUrl("api.baseUrl.debug", "http://10.0.2.2:8000/api/v1/"))
        }
        release {
            buildConfigField("String", "API_BASE_URL", apiBaseUrl("api.baseUrl.release", ""))
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}