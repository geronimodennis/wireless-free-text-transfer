# Product Backlog & User Stories

## Epic A: Core Transfer

### Story A1 (Desktop write)
As a desktop user, I want to convert text in a textarea into a QR code so that Android can scan it.

Acceptance Criteria:

- Given text exists in textarea, when user taps "Generate QR", then QR is shown.
- Empty text shows validation message and no QR output.

### Story A2 (Desktop read)
As a desktop user, I want to scan a QR code with webcam so decoded text appears in the textarea.

Acceptance Criteria:

- Camera scanner can start and stop.
- On successful decode, textarea is replaced with decoded text.
- If decode fails, app does not crash.

### Story A3 (Android write)
As an Android user, I want to convert text in textarea into a QR code so desktop can scan it.

Acceptance Criteria:

- Tapping "Write QR" renders QR image from textarea text.
- Empty text shows user feedback.

### Story A4 (Android read)
As an Android user, I want to scan QR from camera so decoded text appears in textarea.

Acceptance Criteria:

- App requests camera permission if missing.
- On successful decode, textarea updates with payload.
- Scanner can be closed manually.

## Epic B: UX and Safety

### Story B1 (Clipboard helper)
As a user, I want to copy transferred text quickly.

Acceptance Criteria:

- Copy action stores current textarea content into clipboard.

### Story B2 (Error handling)
As a user, I want useful messages when scan/generation fails.

Acceptance Criteria:

- Camera failure, decode failure, and validation errors are surfaced.

## Epic C: Documentation & Operations

### Story C1 (Agile docs)
As a project owner, I want sprint planning, stories, technical design, and logs in `docs/`.

Acceptance Criteria:

- Required documents exist and are understandable by contributors.

## Prioritization (MoSCoW)

- Must: A1, A2, A3, A4, C1.
- Should: B1, B2.
- Could: export/import history, dark mode, multi-QR chunking for long text.
- Won't (MVP): automatic nearby discovery/network transfer.
