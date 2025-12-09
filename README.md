# BabyBot

**BabyBot** es una aplicación móvil nativa para Android desarrollada con Kotlin y Jetpack Compose. Su propósito es brindar acompañamiento a padres primerizos mediante un asistente inteligente y una comunidad segura. El sistema integra un chatbot con base de conocimiento verificada y un foro supervisado para resolver dudas sobre el cuidado infantil (0-3 años).

## 📋 Información del Proyecto

* **Estado:** En desarrollo (Prototipo Académico)
* **Versión actual:** 1.0.0-alpha
* **Licencia:** Uso académico exclusivo

## 👥 Equipo de Desarrollo

* José Francisco Sánchez Neri
* Kevin Yael Gómez Cruz
* Andrés Zavala Pérez

## 🛠 Stack Tecnológico

El proyecto ha sido construido utilizando estándares modernos de desarrollo Android (2025):

| Categoría | Tecnología | Versión / Detalle                  |
| :--- | :--- |:-----------------------------------|
| **Lenguaje** | Kotlin | 2.0.21 (K2 Compiler)               |
| **UI Framework** | Jetpack Compose | Material Design 3 (BOM 2024.09.00) |
| **IDE** | Android Studio | Otter 2 (2025.2.2.7)               |
| **Target SDK** | Android SDK | API 36 (Min SDK 26 - Oreo)         |
| **Backend** | Firebase | Serverless Architecture            |
| **Diseño** | Figma | Prototipado UI/UX                  |
| **Gestión** | Jira / Git | Scrum & Gitflow                    |

### Servicios de Firebase Implementados
* **Authentication:** Gestión de sesiones segura.
* **Realtime Database:** Sincronización de foros y chats en tiempo real.
* **Cloud Firestore:** Base de conocimiento estructurada para el chatbot.
* **Cloud Messaging (FCM):** Notificaciones push.
* **Analytics:** Recopilación de métricas uso y comportamiento del usuario.

## ⚙️ Configuración del Entorno Local

Para ejecutar este proyecto en tu máquina local, asegúrate de cumplir con los siguientes requisitos previos.

### Prerrequisitos
* **Android Studio Ladybug** o superior.
* **JDK 17** (o la versión embebida en Android Studio).
* Dispositivo virtual o físico con **Android 8.0 (API 26)** mínimo.

### Pasos de Instalación

1.  **Clonar el repositorio:**
    ```bash
    git clone https://github.com/Danthalian98/BabyBot-App.git
    cd BabyBot-App
    ```

2.  **Abrir el proyecto:**
    * Inicia Android Studio.
    * Selecciona `File` > `Open` y busca la carpeta clonada.
    * Espera a que Gradle finalice la sincronización de dependencias.

3.  **Configurar Credenciales (Importante):**
    El archivo de configuración de Firebase (`google-services.json`) no se incluye en el repositorio por razones de seguridad.
    * **Si eres colaborador:** Solicita el archivo `google-services.json` directamente a los desarrolladores principales y colócalo en la carpeta `app/`.
    * **Si es una instalación nueva:** Crea tu propio proyecto en Firebase Console, registra el paquete `com.proyecto.babybot` y descarga tu propio archivo de configuración.

4.  **Ejecutar:**
    * Conecta tu dispositivo o inicia el emulador.
    * Presiona `Run` (Shift+F10).

## 📂 Estructura del Proyecto

* `/app`: Contiene el código fuente (Kotlin), recursos (XML, imágenes) y configuraciones de construcción del módulo principal.
* `/gradle`: Archivos del "Gradle Wrapper" para asegurar la consistencia del entorno de construcción.
* `/docs`: Directorio destinado a la documentación técnica y manuales de usuario.
* `/diagrams`: Almacenamiento de diagramas UML, flujos de navegación y esquemas de base de datos.
* `README.md`: Archivo de entrada con la información general del proyecto.
* `.gitignore`: Archivo de configuración que excluye temporales y credenciales sensibles.
* `google-services.json` (Excluido): Archivo de configuración de Firebase, mantenido únicamente de forma local por seguridad.

## 📄 Licencia y Uso
Este software fue desarrollado como parte de un proyecto universitario. Su distribución, modificación o uso comercial no está autorizado sin el consentimiento de los autores.

##