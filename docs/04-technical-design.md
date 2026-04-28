# Technical Design

## High-Level Architecture

- Desktop: Python + Tkinter + OpenCV + qrcode + Pillow.
- Android: Native Kotlin + Jetpack Compose + CameraX + ML Kit + ZXing.
- Data path: plaintext -> QR image -> camera decode -> plaintext.
- Network: none required at runtime.

## Why Native Android + Desktop Local App

This design satisfies the strict offline/runtime requirement better than browser-only flows that often require secure contexts (`https` or `localhost`) for camera APIs.

## Desktop Design

Main module: `desktop-app/app.py`

Key components:

- Text editor (Tkinter `Text`).
- QR encoder (`qrcode`) from textarea content.
- File decoder (`cv2.QRCodeDetector`) from selected image.
- Webcam decoder loop (`cv2.VideoCapture`) for live scanning.

Failure handling:

- No camera: show dialog and stay responsive.
- No QR in image/frame: no crash, show status.

## Android Design

Main module: `android-app/app/src/main/java/com/wirelessfreetexttransfer/MainActivity.kt`

Key components:

- Compose textarea + action buttons.
- QR generation using ZXing `MultiFormatWriter`.
- QR scanning using CameraX image analysis + ML Kit barcode scanner.
- Runtime camera permission handling with activity result API.

Failure handling:

- Permission denied: show toast guidance.
- Camera binding failure: close scanner with message.

## Security and Privacy

- No cloud transport.
- Payload remains local to device memory/UI.
- No automatic persistence of transferred text in MVP.

## Performance Notes

- QR generation is local and immediate for typical note sizes.
- Camera analysis uses latest-frame strategy to avoid backlog.

## Known Limits (MVP)

- Very long plaintext may produce dense QR codes that are hard to scan.
- No chunking/reassembly protocol yet for large payloads.
