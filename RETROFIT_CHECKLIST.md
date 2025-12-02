# Checklist resumido: Implementación Retrofit (MVVM)

Estado real tras revisar el proyecto (resumen rápido con ticks):

- [x] Dependencias en `app/build.gradle.kts` (Retrofit, converter-gson, okhttp, logging-interceptor, coroutines) — ya presentes
- [ ] Permiso `<uses-permission android:name="android.permission.INTERNET" />` en `AndroidManifest.xml` — falta
- [x] `RetrofitClient.kt` (singleton) creado — existe en `data/remote/RetrofitClient.kt`
- [x] DTOs en `data/remote/dto/` (`ProductoDto.kt`, `UsuarioDto.kt`) con `@SerializedName` y mappers — presentes
- [x] Interfaces API en `data/remote/api/` (`ProductoApi.kt`, `UsuarioApi.kt`) — presentes (firmas devuelven DTOs/Lists; no usan `Response<T>`)
- [x] Repositorios remotos y/o híbridos (`UsuarioRemoteRepository`, `ProductoRemoteRepository`, `UsuarioRepository`, `ProductoRepository`) — implementados
- [x] Inyección manual usando `ServiceLocator` / `RetrofitClient` en `MainActivity` / pantallas — implementado
- [x] ViewModel: llamadas `suspend` desde `viewModelScope` y exposición de estados (`ProductoViewModel`) — implementado
- [x] UI (Compose): `collectAsState()` usado y UI muestra indicador de carga — implementado (NO hay botón "Reintentar" en el flujo de productos)
- [ ] Tests unitarios para repository + viewmodel (mockear `apiService` o usar `MockWebServer`) — no encontrados en el proyecto
- [ ] (Opcional) `db.json` / JSON Server local — no encontrado
- [ ] `HttpLoggingInterceptor` limitado a debug builds — actualmente se añade siempre en `RetrofitClient` (debería condicionar por BuildConfig.DEBUG)
- [ ] Reglas ProGuard/R8 revisadas para Gson/reflect — no revisadas / pendientes

Notas rápidas y próximos pasos recomendados (para completar al 100%):
1. Añadir permiso INTERNET en `app/src/main/AndroidManifest.xml` (línea fuera del <application>).  
2. Decidir si quieres cambiar las firmas API a `Response<T>` para revisar códigos HTTP en repositorio; hoy las funciones devuelven objetos directamente y el repo maneja con try/catch y Result.
3. Habilitar `HttpLoggingInterceptor` solo en debug (usar `if (BuildConfig.DEBUG)`).
4. Añadir tests unitarios (mockk + coroutines-test o MockWebServer) para Repository y ViewModel.  
5. (Opcional) Añadir `db.json` y documentar `baseUrl` para pruebas locales; ServiceLocator ya permite pasar baseUrl personalizado.

Si quieres, puedo aplicar los cambios siguientes ahora: (elige uno o varios)
- A: Añadir `<uses-permission android:name="android.permission.INTERNET" />` al manifest.
- B: Ajustar `RetrofitClient` para activar `HttpLoggingInterceptor` solo en Debug.
- C: Cambiar firmas de APIs para devolver `Response<T>` (y adaptar `UsuarioRemoteRepository`/`ProductoRemoteRepository` en consecuencia).
- D: Crear tests básicos para Repository usando `Mockk` o `MockWebServer`.

Dime cuál acción(s) quieres que ejecute ahora y las aplicaré y validaré con una compilación rápida.

Fecha: 2025-12-02
Proyecto: HuertoHogarKotlin
