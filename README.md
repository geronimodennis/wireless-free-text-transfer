# Wireless Free Text Transfer

Offline QR text transfer between desktop (Mac/Windows) and Android.

## What this repo includes

- `desktop-app/`: Python desktop app (Tkinter) that can:
  - Encode textarea text into a QR code.
  - Scan QR code via webcam and push decoded text into textarea.
  - Decode QR from an image file.
- `android-app/`: Native Android app (Kotlin + Compose) that can:
  - Encode textarea text into a QR code.
  - Scan QR code via camera and push decoded text into textarea.
- `docs/`: Agile planning, stories, design, sprint logs, risk and test documents.

## Why this architecture

A native Android app + local desktop app best satisfies your requirement of **no active web server and no internet at runtime**.

- Desktop app runs fully local as a Python process (or packaged executable).
- Android app runs fully local as an APK.
- Data exchange happens by scanning each other's QR codes, with no network.

## Quick Start

### Desktop app (dev run)

1. Install Python 3.10+.
2. Install dependencies:

```bash
cd desktop-app
pip install -r requirements.txt
```

3. Run:

```bash
python app.py
```

### Desktop one-click build

```bash
cd desktop-app
./build-mac.sh      # macOS output: dist/WirelessFreeTextTransfer.app
# or on Windows:
# build-windows.bat # output: dist\\WirelessFreeTextTransfer.exe
```

### Android app

1. Open `android-app/` in Android Studio (latest stable).
2. Let Gradle sync and download dependencies.
3. Run on a physical Android device (camera required for scanning).

## Documentation index

See [`docs/README.md`](docs/README.md).
