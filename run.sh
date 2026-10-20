#!/usr/bin/env bash
# ================================================
# Carniceria Rivas - arranque desde el codigo en Linux y macOS
# Tambien se puede usar con Docker: docker compose up -d (ver README)
# ================================================

set -euo pipefail

cd "$(dirname "$0")"

PUERTO=8080

error() {
  echo
  echo "[ERROR] $*" >&2
  exit 1
}

echo "================================================"
echo "CARNICERIA RIVAS"
echo "================================================"

# ================================================
# REQUISITOS
# ================================================

command -v java >/dev/null 2>&1 ||
  error "No se encuentra Java. Instala Java 21 o superior desde https://adoptium.net (o usa Docker)"

VERSION_JAVA="$(java -version 2>&1 | awk -F'"' '/version/ { split($2, v, "."); print v[1]; exit }')"
[ "${VERSION_JAVA:-0}" -ge 21 ] 2>/dev/null ||
  error "Hace falta Java 21 o superior (tienes la ${VERSION_JAVA:-desconocida}). Descargalo de https://adoptium.net"

# ================================================
# PUERTO
# ================================================

if (exec 3<>"/dev/tcp/127.0.0.1/$PUERTO") 2>/dev/null; then
  error "El puerto $PUERTO esta ocupado. Puede que la aplicacion ya este abierta: entra en http://localhost:$PUERTO"
fi

# ================================================
# SERVIDOR
# ================================================

echo
echo "Compilando y arrancando (la primera vez descarga dependencias y tarda un poco)..."
echo "Se abrira el navegador en http://localhost:$PUERTO"
echo "Pulsa Ctrl+C para detener la aplicacion."
echo

# La propia aplicacion abre el navegador cuando esta lista
export CARNICERIA_ABRIR_NAVEGADOR=true
./mvnw -q spring-boot:run
