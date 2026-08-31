# 4bytes — Sistema de Gestión para el Acopio de Leche

> Aplicación multiplataforma desarrollada para digitalizar y controlar el proceso de acopio de leche, desde la recolección realizada a los productores hasta la recepción de la leche en la planta.

---

## Descripción del proyecto

**4bytes** es un sistema de gestión para centros o plantas de acopio de leche que busca reemplazar el registro manual realizado durante las actividades de recolección y recepción.

Actualmente, los acopiadores recorren diferentes comunidades utilizando vehículos como furgones o motocargas para recoger la leche de los productores. La información de cada entrega puede registrarse manualmente en papel, lo que puede generar pérdida de información, errores de registro y dificultades para realizar un seguimiento de las cantidades recolectadas.

El sistema permitirá centralizar esta información y facilitar el control de:

* Productores.
* Entregas de leche.
* Acopiadores.
* Vehículos.
* Rutas de recolección.
* Recepción en planta.
* Control de calidad.
* Incidencias.
* Producción.
* Pagos.
* Clientes y ventas.
* Notificaciones y eventos.

El proyecto se desarrollará progresivamente, incorporando los diferentes módulos durante las siguientes etapas del desarrollo.

---

#  Objetivo general

Desarrollar una aplicación multiplataforma que permita **digitalizar, organizar y controlar el proceso de acopio de leche**, facilitando el registro de las entregas realizadas por los productores y el seguimiento de la leche desde su recolección hasta su recepción en la planta.

## Objetivos específicos

* Digitalizar el registro de productores.
* Registrar las cantidades de leche recolectadas.
* Asociar cada recolección con un productor, acopiador y vehículo.
* Mantener un historial de las entregas realizadas.
* Facilitar el control de la recepción de leche en planta.
* Registrar información relacionada con la calidad de la leche.
* Reducir errores ocasionados por registros manuales.
* Centralizar la información del proceso de acopio.
* Facilitar posteriormente el cálculo y control de pagos.
* Permitir la generación de información para el control y seguimiento del negocio.

---

# Problema que resuelve

El proceso tradicional de acopio presenta diferentes dificultades debido al uso de registros manuales y a la dispersión de la información.

Entre los principales problemas se encuentran:

* Registro de litros de leche en papel.
* Posibilidad de pérdida o deterioro de registros.
* Errores al registrar las cantidades recolectadas.
* Dificultad para consultar el historial de un productor.
* Dificultad para conocer cuánto se recolectó durante una jornada.
* Poco control sobre los vehículos y rutas de acopio.
* Dificultad para comparar la leche recolectada con la recibida en planta.
* Falta de trazabilidad ante incidencias.
* Dificultad para comunicar avisos a los usuarios.

**4bytes** busca solucionar estos problemas mediante la digitalización y centralización de la información.

---

#  Usuarios del sistema

El sistema contempla diferentes tipos de usuarios de acuerdo con las responsabilidades dentro del proceso.

| Usuario                        | Responsabilidades                                                               |
| ------------------------------ | ------------------------------------------------------------------------------- |
| 👨‍💼 **Administrador**        | Gestionar usuarios, roles, permisos y configuraciones del sistema.              |
| 👔 **Gerente**                 | Supervisar el proceso de acopio, producción, pagos, ventas y reportes.          |
| 🚚 **Acopiador**               | Realizar las rutas de recolección y registrar las entregas de leche.            |
| 🧑‍🔧 **Trabajador de planta** | Registrar la recepción de leche y apoyar en el control de calidad y producción. |
| 👨‍🌾 **Productor**            | Entregar leche y consultar sus entregas, historial y posteriormente sus pagos.  |
| 🏪 **Cliente / Distribuidor**  | Consultar y adquirir productos elaborados por la planta.                        |

> Los roles se irán implementando progresivamente durante el desarrollo del proyecto.

---

#  Flujo general del sistema

El proceso principal que busca representar la aplicación es:

```text
👨‍🌾 PRODUCTOR
      │
      │ Entrega leche
      ▼
🚚 ACOPIADOR
      │
      │ Registra litros
      │ Vehículo
      │ Fecha / hora
      ▼
📍 RUTA DE ACOPIO
      │
      ▼
🏭 PLANTA
      │
      ├── Recepción
      │
      ├── Control de calidad
      │
      └── Registro de incidencias
              │
              ▼
        🧀 PRODUCCIÓN
        ├── Queso
        ├── Yogur
        └── Otros productos
              │
              ▼
       🏪 CLIENTE / DISTRIBUIDOR
              │
              ▼
         CONSUMIDOR
```

---

# 🧩 Servicios / módulos del sistema

El sistema está planteado alrededor de los siguientes módulos:

### 1.  Gestión de productores

Permitirá:

* Registrar productores.
* Listar productores.
* Editar productores.
* Eliminar o desactivar productores.
* Consultar información del productor.
* Mantener su historial de entregas.

### 2.  Gestión de acopio

Permitirá:

* Registrar entregas de leche.
* Registrar cantidad de litros.
* Asociar la entrega con un productor.
* Asociar la entrega con un vehículo.
* Registrar acopiador.
* Registrar fecha y hora.
* Registrar observaciones.
* Consultar el historial de acopio.

### 3.  Gestión de rutas

Permitirá posteriormente:

* Registrar zonas.
* Registrar rutas.
* Asignar vehículos.
* Asignar acopiadores.
* Consultar las rutas de recolección.

### 4.  Recepción en planta

Permitirá:

* Registrar la llegada de la leche.
* Registrar la cantidad recibida.
* Asociar la recepción con una jornada de acopio.
* Comparar cantidades recolectadas y recibidas.

### 5.  Control de calidad

Permitirá registrar información relacionada con la calidad de la leche y detectar posibles incidencias.

### 6.  Producción

Permitirá posteriormente controlar la transformación de la leche en:

* Queso.
* Yogur.
* Otros productos derivados.

### 7.  Pagos

Permitirá posteriormente:

* Calcular el monto correspondiente al productor.
* Consultar pagos.
* Mantener historial de pagos.

### 8.  Clientes y ventas

Permitirá gestionar posteriormente:

* Clientes.
* Distribuidores.
* Productos.
* Pedidos.
* Ventas.

### 9.  Notificaciones y eventos

Permitirá enviar posteriormente:

* Avisos importantes.
* Eventos de la planta.
* Alertas relacionadas con incidencias.
* Comunicaciones dirigidas a productores o clientes.

### 10.  Reportes

Permitirá consultar información como:

* Litros recolectados.
* Litros recibidos.
* Entregas por productor.
* Producción.
* Pagos.
* Ventas.
* Incidencias.

---

#  Funcionalidades

##  Implementadas actualmente

Como parte de la **Sesión 3 de Desarrollo de Aplicaciones Móviles**, actualmente se encuentran implementados:

### Productores

* Registro de productores.
* Listado de productores.
* Tarjetas de información.
* Validación de formularios.
* Estado activo/inactivo.
* Lista mediante `LazyColumn`.

### Acopio

* Registro de entregas de leche.
* Selección de productor.
* Selección de vehículo.
* Registro de litros.
* Historial de acopio.
* Formulario para registrar una entrega.

### Compose Multiplatform

También se han implementado y verificado conceptos de:

* `@Composable`.
* `remember`.
* `mutableStateOf`.
* Elevación de estado.
* `LazyColumn`.
* `Modifier`.
* `padding`.
* `background`.
* Formularios.
* Validación reactiva.

### Verificación

* Aplicación ejecutándose en Android.
* 52 tests ejecutados.
* 0 tests fallidos.
* Rama `s3-primeras-pantallas` subida a GitHub.

---

# 🔜 Funcionalidades previstas

Las siguientes funcionalidades serán desarrolladas progresivamente:

* [ ] Gestión completa de rutas.
* [ ] Gestión de vehículos.
* [ ] Recepción de leche en planta.
* [ ] Control de calidad.
* [ ] Registro de incidencias.
* [ ] Control de producción.
* [ ] Gestión de queso y yogur.
* [ ] Cálculo y registro de pagos.
* [ ] Gestión de clientes y distribuidores.
* [ ] Gestión de ventas.
* [ ] Notificaciones.
* [ ] Eventos.
* [ ] Reportes y estadísticas.
* [ ] Inicio de sesión y control de acceso.
* [ ] Trabajo sin conexión.
* [ ] Sincronización cuando vuelva la conexión.
* [ ] Uso de ubicación del dispositivo para apoyar las rutas de recolección.

---

# Entidad principal

## Productor

La entidad principal del CRUD es **Productor**, debido a que representa a las personas que entregan leche al centro de acopio.

### Atributos

```text
Productor
├── idProductor
├── nombres
├── apellidos
├── dni
├── telefono
├── direccion
├── comunidad
└── estado
```

El productor posteriormente estará relacionado con los registros de acopio y las entregas de leche.

---

# Registro de acopio

Cada registro de acopio representa una entrega de leche realizada durante una jornada de recolección.

Conceptualmente contiene:

```text
RegistroAcopio
├── id
├── productor
├── acopiador
├── vehiculo
├── zona
├── fecha
├── hora
├── litros
├── observacion
└── estado
```

El registro permitirá mantener la trazabilidad de la leche desde el productor hasta la planta.

---

#  Capacidad nativa

## Ubicación

Una de las capacidades nativas previstas es el uso de la **ubicación del dispositivo**.

Esta funcionalidad permitirá posteriormente:

* Registrar la ubicación de una recolección.
* Identificar los puntos donde se recoge leche.
* Apoyar el seguimiento de las rutas.
* Facilitar el control de las actividades realizadas en campo.

La funcionalidad de ubicación será implementada en una etapa posterior.

---

#  Arquitectura del proyecto

El proyecto utiliza **Kotlin Multiplatform** y **Compose Multiplatform**, buscando compartir la mayor cantidad posible de código entre las plataformas.

Actualmente el código compartido se encuentra principalmente en `commonMain`.

La estructura se organiza separando el dominio de la interfaz:

```text
shared/
└── src/
    ├── commonMain/
    │   └── kotlin/
    │       └── .../
    │           ├── domain/
    │           │   └── model/
    │           │       ├── Productor.kt
    │           │       ├── RegistroLeche.kt
    │           │       ├── RegistroAcopio.kt
    │           │       └── Categoria.kt
    │           │
    │           ├── service/
    │           │   ├── ProductorService.kt
    │           │   └── AcopioService.kt
    │           │
    │           └── ui/
    │               └── pantallas/
    │                   ├── CatalogoPantalla.kt
    │                   ├── productores/
    │                   │   ├── ProductoresScreen.kt
    │                   │   ├── TarjetaProductor.kt
    │                   │   └── FormularioProductor.kt
    │                   │
    │                   └── acopio/
    │                       ├── AcopioScreen.kt
    │                       ├── TarjetaAcopio.kt
    │                       └── FormularioAcopio.kt
    │
    ├── androidMain/
    ├── iosMain/
    └── jvmMain/
```

---

#  Tecnologías utilizadas

* **Kotlin**
* **Kotlin Multiplatform**
* **Compose Multiplatform**
* **Jetpack Compose**
* **Android**
* **Desktop / JVM**
* **Git**
* **GitHub**

### Targets

* Android
* Desktop

> iOS está preparado dentro del proyecto, pero requiere macOS para realizar la compilación y ejecución.

---

#  Plataformas

La aplicación busca compartir la lógica y la interfaz desarrollada en `commonMain`.

```text
              commonMain
                  │
        ┌─────────┴─────────┐
        ▼                   ▼
     Android              Desktop
        │                   │
     📱 App              💻 App
```

---

#  Control de versiones

El proyecto utiliza Git y GitHub para controlar las versiones del desarrollo.

Las funcionalidades se desarrollan mediante ramas asociadas a las diferentes sesiones o actividades.

Ejemplo:

```text
main
 │
 └── s3-primeras-pantallas
        │
        ├── Productores
        ├── Acopio
        └── Compose / UI
```

---

#  Bitácora de desarrollo

### Semana 1

* Configuración del entorno.
* Creación del proyecto.
* Configuración de Git y GitHub.
* Definición inicial del proyecto.

### Semana 2

* Desarrollo del modelo de dominio.
* Creación de entidades.
* Implementación de servicios.
* Pruebas del dominio.

### Semana 3

* Creación de `ui/pantallas`.
* Implementación de composables.
* Uso de `remember` y `mutableStateOf`.
* Implementación de modifiers.
* Implementación de `ProductoresScreen`.
* Implementación de `TarjetaProductor`.
* Implementación de `FormularioProductor`.
* Implementación de `AcopioScreen`.
* Implementación de `FormularioAcopio`.
* Uso de `LazyColumn`.
* Validación reactiva.
* Elevación de estado.
* Verificación en Android.
* 52 tests con 0 fallos.

---

# Estructura de módulos

* [`/androidApp`](./androidApp) — aplicación y entrada para Android.
* [`/shared/src/commonMain`](./shared/src/commonMain/kotlin) — código compartido entre plataformas.
* [`/shared/src/androidMain`](./shared/src/androidMain/kotlin) — código específico para Android.
* [`/shared/src/iosMain`](./shared/src/iosMain/kotlin) — código específico para iOS.
* [`/shared/src/jvmMain`](./shared/src/jvmMain/kotlin) — código JVM / Desktop.

---

# ▶️ Comandos útiles

### Compilar Android

```bash
./gradlew :androidApp:assembleDebug
```

### Ejecutar Desktop

```bash
./gradlew :shared:jvmRun
```

### Ejecutar pruebas

```bash
./gradlew :shared:testAndroidHostTest
```

```bash
./gradlew :shared:iosSimulatorArm64Test
```

---

#  Equipo 4bytes

| Integrante                       | Código    | Rol                |
| -------------------------------- | --------- | ------------------ |
| **LINCOL WERNER YUJRA CALLA**    | 202411737 | Coordinación       |
| **GUIDO DANIEL MAYTA SUAÑA**     | 202413212 | QA y documentación |
| **RONALD CONDORI OLAZABAL**      | 202410814 | Lógica y datos     |
| **DILWER JHONATAN AGUILAR PAJA** | 202414021 | UI                 |

---

#  Estado actual

**Proyecto en desarrollo **

Actualmente el sistema cuenta con los módulos iniciales de:

*  **Productores**
*  **Acopio de leche**

Estos módulos constituyen la base para continuar posteriormente con la recepción en planta, control de calidad, producción, pagos, clientes, ventas, notificaciones y reportes.

---

##  Visión del proyecto

4bytes busca convertirse en una herramienta que permita controlar digitalmente todo el proceso:

**Productor → Acopio → Transporte → Recepción → Calidad → Producción → Venta**

centralizando la información y proporcionando trazabilidad sobre las operaciones realizadas por la planta de leche.

---
