
# Comparativa de Frameworks Multiplataforma

**Proyecto:** LactoRuta  
**Equipo:** LactoRuta Team — Semana 1  

## Cuadro Comparativo

| Criterio | Kotlin Multiplatform (KMP) | Flutter | React Native |
| :--- | :--- | :--- | :--- |
| **Lenguaje y aprendizaje** | Usa **Kotlin**. Curva suave para desarrolladores Android; requiere aprender patrones `expect`/`actual` *(JetBrains, 2025)*. | Usa **Dart**. Curva inicial más alta al aprender lenguaje y árbol de *widgets* al mismo tiempo *(Google, 2025)*. | Usa **JavaScript / TypeScript**. Curva muy rápida para desarrolladores con experiencia web *(Meta, 2025)*. |
| **Estrategia de UI** | **Compose Multiplatform**. Permite compartir solo la lógica o también la interfaz (estable en iOS y Desktop) *(JetBrains, 2025)*. | **Skia / Impeller**. Dibuja cada píxel en pantalla con paridad visual 1:1 sin depender de elementos nativos *(Google, 2025)*. | **Fabric**. Traduce componentes a elementos nativos reales de cada sistema operativo *(Meta, 2025)*. |
| **Código compartido** | **Flexible (0% al 100%)**. Desde solo lógica de negocio hasta UI completa, manteniendo acceso nativo directo *(JetBrains, 2025)*. | **Casi 100%**. Comparte lógica y UI en su totalidad. El acceso nativo requiere desarrollo de *plugins* *(Google, 2025)*. | **Alto (~80%-90%)**. Comparte lógica y UI declarativa; sensores o APIs avanzadas requieren módulos nativos *(Meta, 2025)*. |
| **Rendimiento** | **Nativo / Binario**. Compila a bytecode (JVM) y binario nativo (Kotlin/Native) con rendimiento nativo *(JetBrains, 2025)*. | **Alto**. Compila a binario ARM nativo con motor gráfico propio, excelente para animaciones complejas *(Google, 2025)*. | **Casi Nativo**. La Nueva Arquitectura (JSI/TurboModules) permite llamadas síncronas directas al sistema *(Meta, 2025)*. |
| **Ecosistema y madurez** | **Estable (2023)**. Respaldo oficial de Google para lógica; ecosistema en crecimiento activo *(JetBrains, 2025)*. | **Muy Maduro**. Amplia variedad de paquetes en `pub.dev` y gran comunidad global *(Google, 2025)*. | **Muy Maduro**. El ecosistema más grande gracias a la comunidad de React/JS y herramientas como Expo *(Meta, 2025)*. |
| **Respaldo y empresas** | **JetBrains & Google**. Usado por Netflix, Cash App, McDonald's y VMware *(JetBrains, 2025)*. | **Google**. Usado por Google Pay, BMW, Nubank y Alibaba *(Google, 2025)*. | **Meta**. Usado por Instagram, Facebook, Shopify y Discord *(Meta, 2025)*. |
| **Entorno en Windows** | **Soporte Android & Desktop**. Compilación nativa desde Windows; iOS requiere macOS *(JetBrains, 2025)*. | **Soporte Android**. Desarrollo y pruebas en Windows; iOS requiere macOS *(Google, 2025)*. | **Soporte Android**. Desarrollo total en Windows; iOS requiere macOS o la nube *(Meta, 2025)*. |

## Conclusión del Equipo

Los tres frameworks ofrecen soluciones sólidas pero atienden enfoques distintos:

* **Flutter:** Ideal cuando el objetivo principal es garantizar una interfaz idéntica en todas las plataformas con animaciones complejas.
* **React Native:** Conviene a equipos con experiencia previa sólida en el ecosistema web con JavaScript o TypeScript.
* **Kotlin Multiplatform (KMP):** Fue la elección para **LactoRuta** al permitir compartir la lógica de negocio e interfaz mediante Compose Multiplatform sin perder el acceso directo a las capacidades del sistema. Su integración nativa con Kotlin brinda un entorno óptimo para la arquitectura *offline-first*.

---

## Referencias

* **American Psychological Association.** (2020). *Publication manual of the American Psychological Association* (7.ª ed.). https://doi.org/10.1037/0000165-000
* **Google.** (2025). *Flutter documentation*. https://docs.flutter.dev/
* **JetBrains.** (2025). *Kotlin Multiplatform documentation*. https://kotlinlang.org/docs/multiplatform.html
* **Meta.** (2025). *React Native documentation*. https://reactnative.dev/docs/getting-started
