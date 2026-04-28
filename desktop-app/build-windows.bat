@echo off
setlocal ENABLEDELAYEDEXPANSION

set "SCRIPT_DIR=%~dp0"
cd /d "%SCRIPT_DIR%"

set "PYTHON_BIN=python"
set "APP_NAME=WirelessFreeTextTransfer"
set "ENTRY_FILE=app.py"

echo [1/4] Installing runtime dependencies...
%PYTHON_BIN% -m pip install -r requirements.txt
if errorlevel 1 goto :fail

echo [2/4] Installing PyInstaller...
%PYTHON_BIN% -m pip install pyinstaller
if errorlevel 1 goto :fail

echo [3/4] Building Windows executable...
%PYTHON_BIN% -m PyInstaller --noconfirm --clean --windowed --onefile --name %APP_NAME% %ENTRY_FILE%
if errorlevel 1 goto :fail

echo [4/4] Build complete.
echo Executable: %SCRIPT_DIR%dist\%APP_NAME%.exe
exit /b 0

:fail
echo Build failed.
exit /b 1
