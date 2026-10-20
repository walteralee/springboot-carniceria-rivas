@echo off
rem ================================================
rem Carniceria Rivas - arranque desde el codigo en Windows
rem Para usarla sin instalar nada esta el ZIP de la Release (ver README)
rem ================================================

setlocal EnableExtensions

cd /d "%~dp0"

title Carniceria Rivas

set "PUERTO=8080"

echo ================================================
echo CARNICERIA RIVAS
echo ================================================

rem ================================================
rem REQUISITOS
rem ================================================

where java >nul 2>nul
if errorlevel 1 (
    set "MENSAJE=No se encuentra Java. Instala Java 21 o superior desde https://adoptium.net"
    goto error
)

java -version 2>&1 | findstr /r /c:"version \"2[1-9]" /c:"version \"[3-9][0-9]" >nul
if errorlevel 1 (
    set "MENSAJE=Hace falta Java 21 o superior. Descargalo de https://adoptium.net"
    goto error
)

rem ================================================
rem PUERTO
rem ================================================

netstat -ano | findstr /r /c:":%PUERTO% .*LISTENING" >nul
if not errorlevel 1 (
    set "MENSAJE=El puerto %PUERTO% esta ocupado. Puede que la aplicacion ya este abierta: entra en http://localhost:%PUERTO%"
    goto error
)

rem ================================================
rem SERVIDOR
rem ================================================

echo.
echo Compilando y arrancando (la primera vez descarga dependencias y tarda un poco)...
echo Se abrira el navegador en http://localhost:%PUERTO%
echo Para detener la aplicacion cierra esta ventana o pulsa Ctrl+C.
echo.

rem La propia aplicacion abre el navegador cuando esta lista
set "CARNICERIA_ABRIR_NAVEGADOR=true"
call "%~dp0mvnw.cmd" -q spring-boot:run
if errorlevel 1 (
    set "MENSAJE=La aplicacion se ha detenido con un error. Revisa los mensajes de arriba"
    goto error
)
exit /b 0

:error
echo.
echo [ERROR] %MENSAJE%
echo.
pause
exit /b 1
