# Proyecto Huerto Hogar en KOTLIN por Germán Bonhomme y Tomás Bustos


# Tecnologías y metodologías usadas en la app

Este documento describe las tecnologías, librerías, herramientas y metodologías usadas (o recomendadas) para la construcción de la aplicación Android incluida en este repositorio.

## Resumen rápido
- Lenguaje: Kotlin
- Plataforma: Android (compileSdk 34, targetSdk 34, minSdk 24)
- UI: Jetpack Compose + Material3
- Arquitectura: Jetpack (ViewModel, LiveData/State), enfoque MVVM (asumido)
- Persistencia local: Room, DataStore (preferido para preferencias)
- Red / API: Retrofit + OkHttp + converter-gson
- Serialización: Gson
- Carga de imágenes: Coil (Compose)
- Concurrencia: Kotlin Coroutines
- Build: Gradle (Kotlin DSL), Gradle Wrapper
- Testing: JUnit, MockK, Espresso, Compose UI testing, kotlinx-coroutines-test
- Otros: KAPT para generación de código (Room), Proguard (rules incluidas)

> Nota: Muchas versiones se especifican en `app/build.gradle.kts` y en el catálogo de dependencias (`gradle/libs.versions.toml`). Donde se listan versiones aquí, están extraídas del `build.gradle.kts` del módulo `app`.

---

## Detalle de tecnologías (extraído de `app/build.gradle.kts`)
- Android SDK
    - compileSdk = 34
    - targetSdk = 34
    - minSdk = 24
- Java/Kotlin
    - jvmTarget / compatibilidad con Java 11
- Plugins
    - Kotlin Android
    - Kotlin Kapt (annotation processing)
    - Jetpack Compose plugin
- AndroidX / Jetpack
    - androidx.core:core-ktx (1.12.0)
    - androidx.lifecycle:lifecycle-runtime-ktx (2.7.0)
    - androidx.activity:activity-compose (1.8.2)
    - lifecycle-viewmodel-compose (2.7.0)
    - navigation-compose (2.7.6)
    - Compose UI (BOM usado: androidx.compose:compose-bom:2023.08.00)
    - material3
- Persistencia y preferencias
    - Room (room-runtime, room-ktx, room-compiler) — 2.6.1
    - androidx.datastore:datastore-preferences: 1.1.1
- Networking
    - OkHttp 4.11.0 (incluye logging-interceptor)
    - Retrofit 2.9.0 con converter-gson 2.9.0
    - com.google.code.gson:gson:2.10.1
- Imágenes
    - io.coil-kt:coil-compose:2.4.0
- Concurrencia / utilidades
    - org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3
- Tests
    - junit (version declarada en el catálogo)
    - androidx.test.ext:junit (androidTest)
    - androidx.test.espresso:espresso-core (androidTest)
    - androidx.compose.ui:ui-test-junit4 (androidTest)
    - io.mockk:mockk:1.13.5
    - org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3

## Metodologías y patrones de diseño observados / recomendados
- Arquitectura: MVVM
    - Hay dependencias de `lifecycle-viewmodel-compose` y `lifecycle-runtime-ktx`, lo que sugiere el uso de ViewModels para separar UI y lógica.
- Persistencia y acceso a datos
    - Uso de Room para bases de datos locales y DataStore para preferencias.
    - Recomendado: implementar un patrón Repository que abstraiga las fuentes de datos (Room, Retrofit, DataStore).
- Networking
    - Retrofit con OkHttp y logging-interceptor para llamadas a APIs REST. Se usa Gson como conversor.
- UI
    - Declarativa con Jetpack Compose y Material3. Navigation via `navigation-compose`.
- Concurrency
    - Kotlin Coroutines para operaciones asíncronas (I/O, acceso a base de datos y red).
- Testing
    - Tests unitarios con JUnit + MockK. Pruebas para coroutines con kotlinx-coroutines-test.
    - Tests instrumentados y de UI con Espresso y Compose UI testing.

## Flujo de desarrollo y buenas prácticas (recomendadas para este repo)
- Mantener la separación de responsabilidades: UI (Compose) <-> ViewModel <-> Repository <-> DataSource (Room/Retrofit)
- Hacer inyección de dependencias (DI) para facilitar testing y modularidad. El proyecto no define explícitamente una librería DI (Hilt/DI), considerar añadir Hilt o Koin si el proyecto crece.
- Manejar errores de red y estados de carga en ViewModel (sealed classes / Result wrappers).
- Mapear modelos de red a modelos de dominio/local para evitar acoplamientos con librerías externas (p. ej. Retrofit/Gson).
- Agregar pruebas unitarias para ViewModels y repositorios; pruebas instrumentadas para flujos críticos.

## Archivos y áreas clave del proyecto
- `app/` — módulo principal Android (código fuente, build.gradle.kts, proguard-rules.pro)
- `app/src/main/java` y `app/src/main/kotlin` — código fuente de la app (UI, ViewModels, repositorios, models)
- `app/src/test` — tests unitarios
- `app/src/androidTest` — tests instrumentados y de UI
- `app/build.gradle.kts` — dependencias y configuraciones (usar como fuente principal de versiones)

## Notas de build y empaquetado
- El proyecto incluye Gradle Wrapper; usar el wrapper para consistencia de entorno.
- Se generan artefactos release en `app/release/` (apk, aab) según la estructura de build existente.
- Proguard / R8: existe `proguard-rules.pro` y la configuración de `release` en `build.gradle.kts`.


