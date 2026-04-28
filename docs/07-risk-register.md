# Risk Register

## R1: Dense QR for long text

- Impact: High
- Probability: Medium
- Mitigation: introduce multi-QR chunking in Sprint 2.

## R2: Camera permission denial (Android)

- Impact: Medium
- Probability: Medium
- Mitigation: clear permission prompt and fallback messaging.

## R3: Webcam access issues (Desktop)

- Impact: Medium
- Probability: Low-Medium
- Mitigation: keep file-based decode fallback and improve diagnostics.

## R4: Dependency sync issues on clean Android environment

- Impact: Medium
- Probability: Medium
- Mitigation: document toolchain versions and setup steps.
