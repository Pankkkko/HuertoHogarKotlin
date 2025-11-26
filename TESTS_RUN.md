Instrucciones para ejecutar tests unitarios (Windows - cmd.exe)

1) Abrir una terminal "cmd.exe".
2) Ir a la raíz del proyecto (ejemplo):
   cd C:\Users\busto\StudioProjects\HuertoHogarKotlin
3) Ejecutar el script creado:
   run_tests.bat

El script llamará a `gradlew.bat :app:testDebugUnitTest` y volcará la salida en `test-output.txt`.
Si ocurre un problema con la terminal de la IDE ("Could not create classic terminal window"), abre una terminal independiente (cmd.exe) y ejecuta manualmente el comando:

    .\gradlew.bat :app:testDebugUnitTest --console=plain --no-daemon --stacktrace

Si obtienes errores o tests fallidos, copia aquí la sección de la salida de Gradle (preferiblemente `test-output.txt`) y la revisaré para corregir los tests o el código.

