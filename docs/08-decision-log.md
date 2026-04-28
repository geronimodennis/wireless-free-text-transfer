# Decision Log

## 2026-04-28 - ADR-001: Platform strategy

Decision:

- Use Python desktop app + native Android app.

Reason:

- Meets strict offline runtime requirement with camera support and no local web server.

Tradeoff:

- Two codebases instead of one web codebase.

## 2026-04-28 - ADR-002: Android scanner stack

Decision:

- Use CameraX + ML Kit Barcode Scanning.

Reason:

- Stable camera pipeline and reliable QR decoding.

Tradeoff:

- Adds ML Kit dependency download at build time.

## 2026-04-28 - ADR-003: Desktop scanner stack

Decision:

- Use OpenCV `QRCodeDetector` for image and webcam decoding.

Reason:

- Avoid extra native dependencies from alternate decoders.

Tradeoff:

- Detection quality depends on camera frame quality and lighting.

## 2026-04-28 - ADR-004: Desktop one-click packaging

Decision:

- Use PyInstaller-based scripts for one-click desktop builds.

Reason:

- Provides simple local packaging for non-technical users on macOS and Windows.

Tradeoff:

- Build step may download packaging dependencies the first time.
