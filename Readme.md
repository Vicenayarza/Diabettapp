# Diabettapp

Aplicación móvil Android desarrollada como Trabajo de Fin de Grado en Ingeniería Informática de Gestión y Sistemas de Información.

**Diabettapp** es una aplicación orientada al seguimiento y gestión de la diabetes, proporcionando herramientas tanto para pacientes como para profesionales sanitarios.

El proyecto integra una aplicación Android con servicios backend, almacenamiento de datos, comunicación mediante Bluetooth y servicios cloud.

---

## 📱 Descripción

Diabettapp busca facilitar el seguimiento de la información relacionada con la diabetes y mejorar la comunicación entre pacientes y profesionales sanitarios.

La aplicación dispone de diferentes funcionalidades según el tipo de usuario:

* 👤 Pacientes
* 👨‍⚕️ Profesionales sanitarios
* 📊 Registro y consulta de información
* 📅 Gestión de citas
* 💬 Comunicación entre usuarios
* 🚨 Gestión de alertas
* 🏃 Registro de actividad física
* 🍽️ Gestión de alimentación
* 📈 Visualización de información y estadísticas
* 🤖 Funcionalidades basadas en inteligencia artificial
* 🔵 Comunicación mediante Bluetooth

---

## ✨ Funcionalidades principales

### Paciente

* Registro e inicio de sesión.
* Gestión de información personal.
* Registro y consulta de datos relacionados con la diabetes.
* Seguimiento de alimentación.
* Registro de actividad física.
* Consulta de estadísticas.
* Gestión y solicitud de citas.
* Comunicación con profesionales sanitarios.
* Recepción de notificaciones y alertas.
* Interfaz adaptada para facilitar el seguimiento diario.

### Profesional sanitario

* Gestión de pacientes.
* Consulta de información de los pacientes.
* Gestión de citas.
* Comunicación mediante chat.
* Consulta de estadísticas.
* Visualización de evolución de los datos registrados.
* Gestión y seguimiento de alertas.

---

## 🩸 Gestión de glucosa

La aplicación permite trabajar con información relacionada con los niveles de glucosa y establecer alertas cuando los valores se encuentran fuera de determinados rangos.

Esto permite facilitar la identificación de situaciones que requieren atención y mejorar el seguimiento de la información registrada.

> La aplicación tiene finalidad académica y de demostración. No sustituye el criterio ni la atención de profesionales sanitarios.

---

## 🔵 Comunicación Bluetooth

Uno de los componentes del proyecto es la comunicación mediante **Bluetooth** con hardware externo.

La aplicación incorpora una implementación específica para gestionar esta comunicación y recibir información procedente del dispositivo.

Esta parte del proyecto permite integrar:

```text
Hardware
   │
   │ Bluetooth
   ▼
Diabettapp
   │
   ▼
Procesamiento de datos
   │
   ▼
Registro / visualización
```

---

## 🤖 Inteligencia artificial

El proyecto incorpora funcionalidades relacionadas con inteligencia artificial y procesamiento mediante servicios externos.

Entre ellas se encuentra un sistema de chatbot integrado dentro de la aplicación, utilizado como apoyo para la interacción con el usuario.

Las credenciales y claves privadas necesarias para estos servicios **no forman parte del repositorio público**.

---

## 🏗️ Arquitectura

El proyecto está compuesto por diferentes capas y servicios:

```text
┌──────────────────────────────┐
│       Android App            │
│       Java + XML             │
└──────────────┬───────────────┘
               │
        REST / Firebase
               │
       ┌───────┴────────┐
       │                │
       ▼                ▼
   Backend          Firebase
 Node + Express    Auth / FCM
       │
       ▼
    MySQL
       │
       ▼
   Datos de aplicación
```

---

## 🛠️ Tecnologías utilizadas

### Aplicación móvil

* Java
* Android
* XML
* Android SDK
* Gradle

### Backend

* Node.js
* Express
* API REST
* Volley

### Base de datos

* MySQL
* phpMyAdmin

### Servicios cloud

* Firebase Authentication
* Firebase Cloud Messaging

### Hardware

* Bluetooth
* Comunicación serie
* Dispositivo externo para adquisición de datos

### Otras tecnologías

* Inteligencia artificial
* Git
* GitHub

---

## 📂 Estructura del proyecto

```text
Diabettapp/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/example/tfg/
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   │
│   ├── build.gradle
│   └── proguard-rules.pro
│
├── gradle/
├── build.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
├── settings.gradle
└── .gitignore
```

---

## 🔐 Seguridad

El proyecto se ha preparado para su publicación como portfolio técnico.

No se incluyen en el repositorio público:

* API keys.
* Contraseñas.
* Credenciales de bases de datos.
* Ficheros de configuración sensibles.
* Claves privadas.

Las credenciales necesarias para ejecutar determinados servicios deben configurarse localmente.

---

## 🎓 Contexto académico

**Trabajo de Fin de Grado**

Grado en Ingeniería Informática de Gestión y Sistemas de Información
Universidad del País Vasco / Euskal Herriko Unibertsitatea (UPV/EHU)

El proyecto permitió trabajar de forma práctica con desarrollo móvil, servicios backend, bases de datos, comunicación con hardware y servicios cloud.

---

## 🚀 Objetivos técnicos

Los principales objetivos técnicos del proyecto fueron:

* Diseñar y desarrollar una aplicación Android funcional.
* Implementar diferentes perfiles de usuario.
* Diseñar una arquitectura cliente-servidor.
* Integrar una API REST.
* Gestionar información mediante una base de datos relacional.
* Integrar servicios Firebase.
* Implementar comunicación Bluetooth.
* Incorporar funcionalidades de inteligencia artificial.
* Desarrollar interfaces Android mediante XML.
* Gestionar un proyecto de software utilizando Git.

---

## 📌 Estado del proyecto

Proyecto académico finalizado y publicado como portfolio técnico.

El repositorio se mantiene como referencia del desarrollo realizado durante el Trabajo de Fin de Grado.

---

## 👨‍💻 Autor

**Vicente Ayarza**

Ingeniero Informático
Máster en Ciberseguridad

GitHub: [Vicenayarza](https://github.com/Vicenayarza)
