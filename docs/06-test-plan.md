# Test Plan

## Scope

Validate core QR text transfer flows on desktop and Android for offline runtime.

## Test Matrix

### Desktop

- OS: macOS, Windows 10/11.
- Python: 3.10+
- Camera: built-in webcam and external webcam (if available).

### Android

- API level: 24+
- Devices: at least one physical device (camera required).

## Functional Test Cases

1. Desktop write QR
- Enter text.
- Click Generate QR.
- Verify QR appears.

2. Desktop read QR from image
- Select valid QR image.
- Verify textarea matches encoded text.

3. Desktop read QR from webcam
- Start camera scan.
- Show QR to webcam.
- Verify textarea updates.

4. Android write QR
- Enter text and tap Write QR.
- Verify QR image renders.

5. Android read QR
- Tap Read QR and grant permission.
- Scan QR from desktop.
- Verify textarea updates.

6. Bidirectional transfer
- Desktop -> Android and Android -> Desktop round trip.

## Negative Cases

- Empty textarea generate action.
- Denied camera permission on Android.
- Blurry/partial QR input.

## Exit Criteria (MVP)

- All core functional test cases pass.
- No critical crashes in basic flows.
- Documentation updated with known issues.
