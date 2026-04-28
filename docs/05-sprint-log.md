# Sprint Log

## Sprint 1 (2026-04-28 to 2026-05-04)

Goal:

- Deliver offline MVP for desktop and Android with QR read/write in both apps.

Completed:

- Created desktop app scaffold and offline QR flow.
- Implemented desktop QR encode/decode from text, image, and webcam.
- Created Android native project scaffold.
- Implemented Android QR write and camera-based read flow.
- Added Agile planning and technical documentation under `docs/`.
- Added one-click packaging scripts for desktop (`build-mac.sh`, `build-windows.bat`).
- Verified macOS build output at `desktop-app/dist/WirelessFreeTextTransfer.app`.

In Progress:

- Validate Android build and camera behavior on physical devices.

Blocked/Risks:

- Android build requires Gradle dependency download on first sync.
- Desktop camera compatibility may vary by webcam drivers.

Retro Notes:

- What went well: architecture chosen early to satisfy offline runtime.
- What to improve: add automated checks and UI tests in next sprint.
