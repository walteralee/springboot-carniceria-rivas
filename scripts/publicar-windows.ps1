# Genera la versión para Windows que no necesita instalar nada (lleva Java dentro):
# dist/CarniceriaRivas-Windows.zip con la carpeta CarniceriaRivas y su CarniceriaRivas.exe
# Para generarla hace falta un JDK 21 o superior (trae jlink y jpackage); para usarla, nada.
# Uso (desde la raíz del repositorio):  powershell -ExecutionPolicy Bypass -File scripts/publicar-windows.ps1

$ErrorActionPreference = "Stop"
$raiz = Split-Path -Parent $PSScriptRoot
$dist = Join-Path $raiz "dist"
$trabajo = Join-Path $dist "trabajo"
$aplicacion = Join-Path $dist "CarniceriaRivas"
$zip = Join-Path $dist "CarniceriaRivas-Windows.zip"

# ---------------------------------------------------------------------
# Herramientas del JDK (JAVA_HOME o el java que haya en el PATH)
# ---------------------------------------------------------------------
if ($env:JAVA_HOME) {
    $jdk = $env:JAVA_HOME
} else {
    $jdk = Split-Path -Parent (Split-Path -Parent (Get-Command java).Source)
}
$jlink = Join-Path $jdk "bin\jlink.exe"
$jpackage = Join-Path $jdk "bin\jpackage.exe"
if (-not (Test-Path $jpackage)) {
    throw "No se encuentra jpackage en $jdk. Instala un JDK 21 o superior y define JAVA_HOME"
}

foreach ($ruta in $trabajo, $aplicacion, $zip) {
    if (Test-Path $ruta) { Remove-Item $ruta -Recurse -Force }
}
New-Item -ItemType Directory -Force (Join-Path $trabajo "entrada") | Out-Null

# ---------------------------------------------------------------------
# 1) Compilar el .jar
# ---------------------------------------------------------------------
Push-Location $raiz
try {
    & .\mvnw.cmd -B -q -DskipTests package
    if ($LASTEXITCODE -ne 0) { throw "Error al compilar con Maven" }
} finally {
    Pop-Location
}
Copy-Item (Join-Path $raiz "target\carniceria-rivas.jar") (Join-Path $trabajo "entrada")

# ---------------------------------------------------------------------
# 2) Java reducido: solo los módulos que usan Spring Boot, Hibernate y SQLite (calculados con jdeps)
# ---------------------------------------------------------------------
$modulos = @(
    "java.base", "java.compiler", "java.desktop", "java.instrument", "java.logging", "java.management",
    "java.naming", "java.net.http", "java.prefs", "java.scripting", "java.security.jgss", "java.sql",
    "java.sql.rowset", "java.transaction.xa", "java.xml", "jdk.crypto.ec", "jdk.jfr", "jdk.management",
    "jdk.unsupported", "jdk.zipfs"
) -join ","

& $jlink --add-modules $modulos --strip-debug --no-header-files --no-man-pages --compress=zip-6 `
    --output (Join-Path $trabajo "java")
if ($LASTEXITCODE -ne 0) { throw "Error al crear el Java reducido con jlink" }

# ---------------------------------------------------------------------
# 3) Programa con su .exe. Al abrirlo enseña una ventana de consola (cerrarla apaga la aplicación)
#    y abre el navegador solo. Nada se escribe fuera de la carpeta del programa (la base de datos, SQLite
#    y Tomcat usan datos\, la carpeta temporal de Java es app\ y sin -XX:-UsePerfData Java dejaría un
#    archivo en la carpeta temporal de Windows): al borrar la carpeta no queda rastro
# ---------------------------------------------------------------------
& $jpackage --type app-image --name CarniceriaRivas `
    --input (Join-Path $trabajo "entrada") --main-jar carniceria-rivas.jar `
    --runtime-image (Join-Path $trabajo "java") `
    --dest $dist --win-console `
    --icon (Join-Path $PSScriptRoot "recursos\carniceria.ico") `
    --app-version 1.0.0 --vendor "Walter Alejandro Cutiño Ledo" --description "Carnicería Rivas" `
    --java-options "-Dcarniceria.abrir-navegador=true" `
    --java-options '-Djava.io.tmpdir=$APPDIR' `
    --java-options "-XX:-UsePerfData"
if ($LASTEXITCODE -ne 0) { throw "Error al crear el programa con jpackage" }

# ---------------------------------------------------------------------
# 4) Comprimir
# ---------------------------------------------------------------------
Compress-Archive -Path $aplicacion -DestinationPath $zip
Remove-Item $trabajo -Recurse -Force

Write-Host "Listo: $zip"
