# Agile Plan (Scrum-Style)

## Cadence

- Sprint length: 1 week.
- Ceremonies:
  - Sprint planning (30-45 min).
  - Daily async standup in project notes.
  - Sprint review/demo (20 min).
  - Retrospective (20 min).

## Roles

- Product Owner: Defines priority and acceptance criteria.
- Developer: Implements desktop + Android features.
- QA (shared responsibility): Validates on target devices.

## Definition of Ready

A story is ready when:

- User value is clear.
- Acceptance criteria are testable.
- Dependencies and assumptions are noted.

## Definition of Done

A story is done when:

- Feature implemented on target platform.
- Manual test steps pass.
- Documentation updated under `docs/`.
- Known limitations are logged in risk/decision docs.

## Sprint Roadmap

- Sprint 1: Foundation + QR write/read MVP on desktop and Android.
- Sprint 2: Stability improvements (error handling, UX polish, performance checks).
- Sprint 3: Packaging/release readiness and pilot validation.
