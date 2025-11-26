@echo off
REM Script para ejecutar tests unitarios del módulo app en Windows (cmd.exe)
REM Ubícalo en la raíz del proyecto y ejecútalo desde cmd.exe

cd /d "%~dp0"

echo Comprobando Java/JDK...
REM Si JAVA_HOME no está definido, comprobamos si existe java en PATH
if not defined JAVA_HOME (
    where java >nul 2>&1
    if %ERRORLEVEL% neq 0 (
        echo ERROR: JAVA_HOME no está definido y no se encuentra 'java' en el PATH.
        echo.
        echo Solucion:
        echo 1) Instala un JDK (por ejemplo Temurin/Adoptium, OpenJDK) si no lo tienes.
        echo 2) Define la variable de entorno JAVA_HOME apuntando a la carpeta del JDK, por ejemplo:
        echo    setx JAVA_HOME "C:\\Program Files\\Java\\jdk-17.0.6"
        echo    (luego cierra y abre la terminal)
        echo 3) Asegúrate que %%JAVA_HOME%%\bin esté en el PATH o añade temporalmente:
        echo    set "PATH=%%JAVA_HOME%%\bin;%%PATH%%"
        echo.
        echo También puedes probar temporalmente en esta sesión (solo hasta cerrar la ventana):
        echo    set "JAVA_HOME=C:\\ruta\\a\\tu\\jdk"
        echo    set "PATH=%%JAVA_HOME%%\bin;%%PATH%%"
        echo.
        echo Después de eso, vuelve a ejecutar: run_tests.bat
        exit /b 1
    ) else (
        echo java encontrada en PATH.
    )
) else (
    if not exist "%JAVA_HOME%\bin\java.exe" (
        echo ERROR: JAVA_HOME está definido pero no contiene \bin\java.exe.
        echo Verifica que %%JAVA_HOME%% apunta a la carpeta raíz del JDK.
        exit /b 1
    ) else (
        echo JAVA_HOME detectado: %JAVA_HOME%
    )
)

echo Ejecutando tests unitarios (app:testDebugUnitTest)...
.\gradlew.bat :app:testDebugUnitTest --console=plain --no-daemon --stacktrace > test-output.txt 2>&1

echo ----- SALIDA DE LOS TESTS (HEAD) -----
if exist test-output.txt (
    powershell -NoProfile -Command "Get-Content -Path 'test-output.txt' -TotalCount 200"
) else (
    echo Archivo test-output.txt no encontrado.
)

findstr /C:"BUILD SUCCESSFUL" test-output.txt >nul 2>&1
if %ERRORLEVEL%==0 (
    echo.
    echo Tests completados: BUILD SUCCESSFUL
    exit /b 0
) else (
    echo.
    echo Tests completados: FALLÓ (revisa test-output.txt)
    echo Puedes abrir test-output.txt y pegar aquí las secciones con FAIL o excepciones.
    exit /b 1
)
