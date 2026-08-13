# 🥛 4bytes

## Sistema de Gestión para el Acopio de Leche

**Proyecto grupal del curso de Ingeniería de Software 1**

---

## 📋 Descripción

**4bytes** es un proyecto de software desarrollado por estudiantes del curso de **Ingeniería de Software 1**, cuyo objetivo es diseñar y desarrollar un sistema para apoyar la gestión del **acopio de leche en una zona rural**.

El sistema busca facilitar el registro y control de la información relacionada con los productores de leche, las entregas realizadas, la cantidad de leche recolectada, el control de calidad y los pagos correspondientes.

Actualmente, parte de la información de los procesos de acopio puede ser registrada manualmente, lo que puede generar errores, pérdida de información y dificultades para obtener reportes.

Por ello, el proyecto propone una solución informática que permita organizar y centralizar esta información, facilitando la gestión del centro de acopio.

---

## 🎯 Objetivo del proyecto

### Objetivo general

Desarrollar un sistema de gestión que permita registrar, controlar y administrar eficientemente el proceso de acopio de leche de los productores de una zona rural.

### Objetivos específicos

* Registrar y administrar la información de los productores.
* Registrar las entregas de leche realizadas por cada productor.
* Controlar la cantidad de litros recolectados.
* Registrar información relacionada con la calidad de la leche.
* Calcular los montos correspondientes a los productores.
* Mantener un historial de las entregas realizadas.
* Generar reportes sobre la producción y el acopio.
* Facilitar la consulta de información para los responsables del centro de acopio.

---

## 👥 Integrantes

* **LINCOL WERNER YUJRA CALLA**
* **GUIDO DANIEL MAYTA SUAÑA**
* **RONALD CONDORI OLAZABAL**
* **DILWERT JHONATAN AGUILAR PAJA**

---

## 🏗️ Funcionalidades previstas

El sistema contempla las siguientes funcionalidades:

### 👨‍🌾 Gestión de productores

* Registrar productores.
* Modificar información de productores.
* Consultar productores.
* Buscar productores.
* Gestionar información de contacto.

### 🥛 Gestión del acopio

* Registrar la recepción de leche.
* Registrar fecha y hora de entrega.
* Registrar cantidad de litros.
* Asociar cada entrega con un productor.
* Consultar el historial de entregas.

### 🧪 Control de calidad

* Registrar datos de calidad de la leche.
* Registrar observaciones.
* Clasificar el estado o calidad de la leche.
* Consultar los controles realizados.

### 💰 Gestión de pagos

* Registrar el precio por litro.
* Calcular el monto correspondiente a cada entrega.
* Consultar los montos acumulados por productor.
* Registrar el estado de los pagos.

### 📊 Reportes

* Reporte de producción diaria.
* Reporte de producción semanal.
* Reporte de producción mensual.
* Reporte por productor.
* Cantidad total de litros recolectados.
* Montos a pagar a los productores.

---

## 🛠️ Tecnologías

El proyecto utiliza las siguientes tecnologías:

* **Java** — Lenguaje principal de programación.
* **MySQL** — Sistema gestor de base de datos.
* **Git** — Sistema de control de versiones.
* **GitHub** — Plataforma para alojar y gestionar el repositorio.

### Tecnologías adicionales

> Esta sección se actualizará conforme avance el desarrollo del proyecto.

---

## 🗂️ Estructura del proyecto

La estructura del proyecto será organizada de acuerdo con las necesidades del sistema.

```text
4bytes/
│
├── src/
│   └── ...
│
├── database/
│   └── ...
│
├── docs/
│   └── ...
│
├── README.md
└── ...
```

### Descripción de carpetas

| Carpeta     | Descripción                               |
| ----------- | ----------------------------------------- |
| `src/`      | Código fuente del sistema                 |
| `database/` | Scripts y archivos relacionados con MySQL |
| `docs/`     | Documentación del proyecto                |
| `README.md` | Información general del proyecto          |

> La estructura puede modificarse durante el desarrollo.

---

## ⚙️ Requisitos

Para ejecutar el proyecto se necesitará tener instalado:

* Java JDK
* MySQL Server
* Git
* Un IDE compatible con Java, como IntelliJ IDEA, Eclipse o NetBeans.

Las versiones específicas se definirán durante el desarrollo del proyecto.

---

## 🚀 Cómo ejecutar el proyecto

### 1. Clonar el repositorio

```bash
git clone URL_DEL_REPOSITORIO
```

### 2. Ingresar al proyecto

```bash
cd 4bytes
```

### 3. Configurar la base de datos

Crear la base de datos en MySQL y ejecutar los scripts correspondientes.

```sql
CREATE DATABASE acopio_leche;
```

> El nombre y estructura definitiva de la base de datos se definirán durante el desarrollo.

### 4. Configurar la conexión

Configurar en el proyecto los datos correspondientes a:

* Servidor MySQL
* Puerto
* Usuario
* Contraseña
* Nombre de la base de datos

### 5. Ejecutar el proyecto

Abrir el proyecto utilizando el IDE seleccionado y ejecutar la clase principal de la aplicación.

---

## 🔀 Control de versiones

El proyecto utiliza **Git y GitHub** para gestionar el código fuente y el trabajo colaborativo.

Se recomienda utilizar ramas para trabajar en nuevas funcionalidades:

```text
main
│
├── develop
│
├── feature/productores
├── feature/acopio
├── feature/calidad
└── feature/pagos
```

### Ejemplo de creación de una rama

```bash
git checkout -b feature/productores
```

### Guardar cambios

```bash
git add .
git commit -m "Agregar registro de productores"
```

### Subir cambios

```bash
git push origin feature/productores
```

---

## 👨‍💻 Metodología de trabajo

El proyecto será desarrollado mediante un proceso colaborativo, aplicando conceptos de **Ingeniería de Software**, tales como:

* Análisis de requisitos.
* Diseño del sistema.
* Diseño de base de datos.
* Desarrollo.
* Pruebas.
* Control de versiones.
* Documentación.
* Mantenimiento.

La metodología de desarrollo podrá definirse y documentarse conforme avance el proyecto.

---

## 📌 Estado del proyecto

**Estado actual:** 🟡 En desarrollo

### Progreso

* [x] Creación del repositorio.
* [x] Definición del equipo.
* [x] Definición inicial del proyecto.
* [ ] Análisis de requisitos.
* [ ] Diseño del sistema.
* [ ] Diseño de la base de datos.
* [ ] Desarrollo del sistema.
* [ ] Implementación de funcionalidades.
* [ ] Pruebas.
* [ ] Documentación final.
* [ ] Presentación del proyecto.

---

## 📄 Documentación

La documentación del proyecto incluirá:

* Requerimientos funcionales.
* Requerimientos no funcionales.
* Historias de usuario.
* Casos de uso.
* Diagramas UML.
* Modelo entidad-relación.
* Diseño de la base de datos.
* Arquitectura del sistema.
* Plan de pruebas.
* Manual de usuario.

Los documentos serán agregados progresivamente al directorio `docs/`.

---

## 🤝 Trabajo colaborativo

Cada integrante participará en las diferentes etapas del proyecto, utilizando GitHub para compartir y controlar los cambios realizados en el código.

Se utilizarán **commits, ramas y pull requests** para facilitar la colaboración y mantener organizado el proyecto.

---

## 📜 Licencia

Este proyecto ha sido desarrollado con fines **académicos** para el curso de Ingeniería de Software 1.

---

## 📞 Contacto

**Equipo:** 4bytes
**Curso:** Ingeniería de Software 1
**Proyecto:** Sistema de Gestión para el Acopio de Leche

---

## ⭐ Nota

Este proyecto se encuentra en desarrollo. La información, funcionalidades, tecnologías y estructura pueden cambiar conforme avance el análisis y desarrollo del sistema.
