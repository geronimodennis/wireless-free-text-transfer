# Desktop App (Mac/Windows)

## Features

- Convert text into QR code.
- Scan QR code from webcam into textarea.
- Decode QR code from an image file.
- Save generated QR as PNG.
- Works fully offline at runtime.

## Run (development)

```bash
pip install -r requirements.txt
python app.py
```

## One-Click Build

### macOS

Run:

```bash
./build-mac.sh
```

Output:

- `dist/WirelessFreeTextTransfer.app`

### Windows

Run:

```bat
build-windows.bat
```

Output:

- `dist\\WirelessFreeTextTransfer.exe`

## Notes

- Webcam scan opens an OpenCV preview window.
- Press `Q` or `Esc` in scanner window to cancel scan.
- Build scripts may download packaging dependencies the first time.
