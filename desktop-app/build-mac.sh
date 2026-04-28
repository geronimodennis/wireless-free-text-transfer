#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

PYTHON_BIN="${PYTHON_BIN:-python3}"
APP_NAME="WirelessFreeTextTransfer"
ENTRY_FILE="app.py"

if ! command -v "$PYTHON_BIN" >/dev/null 2>&1; then
  echo "Error: $PYTHON_BIN not found."
  exit 1
fi

echo "[1/4] Installing runtime dependencies..."
"$PYTHON_BIN" -m pip install -r requirements.txt

echo "[2/4] Installing PyInstaller..."
"$PYTHON_BIN" -m pip install pyinstaller

echo "[3/4] Building macOS app bundle..."
"$PYTHON_BIN" -m PyInstaller \
  --noconfirm \
  --clean \
  --windowed \
  --name "$APP_NAME" \
  "$ENTRY_FILE"

echo "[4/4] Build complete."
echo "App bundle: $SCRIPT_DIR/dist/$APP_NAME.app"
