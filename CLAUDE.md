# CLAUDE.md — OffVault (Secure Offline Vault, Android)

> Planning & requirements specification for an AI-assisted build.
> This file describes **what** to build and the **constraints**. It does not contain implementation code.
> Items marked **[DECISION NEEDED]** require the project owner to confirm before/during the build.

---

## 1. Project Overview

**OffVault** is a **fully offline, Android-only** mobile application that securely stores a
user's sensitive personal and financial information **locally on the device**. The app is
protected by **biometric (fingerprint) authentication** and, in its final version, supports
**encrypted backup/restore** via an exportable JSON file that the user can keep in their own
personal cloud.

**Current focus (build priority)**
- **Phase 1 is the immediate goal:** correctly **entering, storing, and displaying** the
  information exactly as specified (sections, lists, add/edit/delete, debit/credit label, masked fields).
- **Encryption / decryption and the JSON export/import are deferred to the final version (Phase 2)**
  and are not the current priority.

**Primary goals**
- Keep all sensitive data on-device; never transmit it anywhere.
- Make it fast and easy to store, view, and manage secret records.
- (Final version) Let the user create an encrypted backup they fully control, and restore from it.

**Who it's for:** a single user storing their own secrets on their own phone.

---

## 2. Core Principles (non-negotiable)

1. **Offline-first / zero network.** The app must function with no internet. It should
   not request the `INTERNET` permission at all, so it is technically incapable of
   sending data off-device. This is a hard guarantee, not just a setting.
2. **Security & privacy by design.** All sensitive data is encrypted at rest. Nothing
   sensitive is logged, cached in plaintext, or exposed to other apps.
3. **Local data ownership.** The user owns their data and their backups. No accounts,
   no servers, no telemetry, no analytics.
4. **Fail safe.** If authentication or decryption fails, the app reveals nothing.

---

## 3. Platform & Technical Constraints

| Constraint | Value |
|---|---|
| Platform | Android only |
| Internet | **None** — no `INTERNET` permission in the manifest |
| Data location | 100% local (on-device storage) |
| Accounts / cloud | None built in (user handles their own cloud for backups) |
| Minimum Android version | **[DECISION NEEDED]** — suggested: Android 9 (API 28) or 10 (API 29) |
| Framework | **Native Android (Kotlin)** ✓ |
| Local DB | Encrypted (e.g. encrypted database or encrypted key–value store) |

---

## 4. Functional Requirements

### 4.1 App Structure & Sections (Home screen)
After the user authenticates, they land on the **Home screen**, which contains four sections:

1. **Cards** — a **single list of all cards** (debit and credit together; no separate sub-screens).
    - When adding/editing a card, the user **selects the card type** (Debit or Credit).
    - In the card list, each card shows a **small label** (Debit / Credit) based on that selection.
2. **Documents** — document-related information (e.g. PAN, Aadhaar, passport, driving licence, insurance, etc.).
3. **Login Details** — saved **username + password** credentials.
4. **Others** — anything that doesn't fit the above (e.g. bank account details, misc secrets, notes).

A **search icon / search bar is present on the Home screen** (available immediately after
unlocking) for **global search across all sections** — see §4.3.

> **DECIDED:** Records are **text fields only** for v1 — no image/scan attachments
> (deferred; see §12 Out of Scope).

### 4.2 Per-Section Behavior (list + add)
- Opening a section shows a **list of all saved entries** of that type.
- Each list has an **Add (+) button** to create a new entry of that type.
- Entries can be **viewed, edited, and deleted**.
- This same **list + Add** pattern applies to every section (Cards, Documents, Login Details, Others).
- Search/filter **within a section** is supported, in addition to the **global search** (§4.3).

### 4.3 Global Search (Phase 1)
- A **search icon / bar on the Home screen** (shown right after unlock) lets the user search
  **across every section at once** — Cards, Documents, Login Details, and Others — not just one section.
- The search matches against **all relevant text fields** of every record, including:
    - card label/nickname, cardholder name, issuing bank
    - document type/name and document-related info
    - login label (site/app name), username
    - any **notes / info** field on any record
    - titles and values under Others (e.g. bank name)
- **Matching records from all sections are displayed together** in the results, and each
  result shows **which section/type it came from**.
- *Example:* searching **"Axis Bank"** returns the matching card(s) **and** the matching
  login/password entry **and** any Document or Other record that mentions Axis Bank.
- Search is **case-insensitive** and matches **partial text**.
- Sensitive values (PIN/CVV, password) are **not searched on** and stay **masked** in results.

### 4.4 Sensitive Field Masking & Reveal (Phase 2)
- Certain fields are **sensitive** and **hidden by default** (shown as dots/asterisks):
    - **Card PIN / CVV** (in Cards)
    - **Password** (in Login Details)
- Each hidden field has an **eye (reveal) button**. Tapping it requires the user to
  **re-authenticate with fingerprint** before the value is shown.
- **Phase:** the fingerprint-gated reveal is planned for the **final version (Phase 2)**.
  In Phase 1 these fields are still masked by default with a simple show/hide toggle.

### 4.5 Authentication (Phase 1)
- On opening the app, the user must authenticate **before any data is shown**, using
  **fingerprint/biometric** or the **device PIN / pattern / password**.
- The **device PIN / pattern / password** also serves as the fallback when biometrics
  fail or are unavailable (device credential, handled by the OS).
- **Auto-lock:** the app re-locks when sent to the background or after a short
  inactivity timeout (suggested default: lock immediately on background + configurable timeout).

### 4.6 Encryption (Phase 2)
- The user sets a personal **encryption passphrase/key**.
- All stored data is protected using strong, modern encryption (see §6).
- This passphrase is the basis for the encrypted backup (export/import).
  **[DECISION NEEDED]** — Is the backup passphrase **separate** from the app-unlock
  (biometric/PIN), or the same? Default assumption: **separate passphrase** used only
  for export/import.

### 4.7 Export / Backup (Phase 2)
- The user can **export all stored data into a single JSON file**.
- The exported file is **encrypted** using the user's personal passphrase, so the JSON
  contents are unreadable without it.
- The user saves this file wherever they like (their own personal cloud, local storage, etc.).
- The app itself does **not** upload anything — export produces a file the user moves manually.

### 4.8 Import / Restore (Phase 2)
- The user can **select a previously exported JSON file** and import it.
- The user enters their **decryption passphrase**; on success, the data is restored
  into the app.
- **[DECISION NEEDED]** On import, should data **replace** existing data, **merge**
  with it, or let the user **choose**? Default assumption: user chooses (replace vs merge).
- Wrong passphrase or corrupted/tampered file → import fails cleanly with no partial data.

---

## 5. Data Model (draft fields)

> Field lists are a starting point — confirm/adjust per record type.

**Card** (single list) — entity `Card`
- Card label/nickname, Cardholder name *(optional)*, Card number, Expiry (MM/YY),
  **PIN / CVV** *(sensitive — masked; fingerprint-gated reveal in Phase 2)*,
  **Card type** (Debit / Credit — selected on entry, shown as a small label in the list),
  Issuing bank *(optional)*, Notes

**Document** — entity `Document`
- Document type (PAN, Aadhaar, passport, licence, insurance, …), Document number,
  Issue date *(optional)*, Expiry date *(optional)*, Issued by *(optional)*, Notes

**Login Detail** — entity `LoginDetail`
- Label (site/app name), Username, **Password** *(sensitive — masked; fingerprint-gated reveal in Phase 2)*,
  URL *(optional)*, Notes

**Other** — entity `Other`
- Title, Value/details, Notes
- *Example use — bank account:* Account holder, Account number, IFSC, Bank, Branch, Account type

**Common to all records**
- Title/label, Created date, Last modified date

---

## 6. Security Requirements

- **Encryption at rest:** all sensitive data encrypted on device using a modern
  authenticated cipher (suggested: **AES-256-GCM**).
- **Key handling:**
    - App-unlock keys protected by the **Android Keystore** / hardware-backed keystore where available.
    - Backup passphrase converted to a key using a **strong key-derivation function**
      (suggested: **Argon2id**, or PBKDF2 with a high iteration count) with a unique salt.
- **Backup file integrity:** exported JSON must be **authenticated** so tampering is
  detected on import (the GCM auth tag / an integrity check covers this).
- **No plaintext leakage:** no sensitive data in logs, crash reports, or temp files.
- **Screenshot/recording protection:** mark sensitive screens as secure
  (block screenshots and screen recording, hide content in the app switcher).
- **Clipboard hygiene:** if "copy to clipboard" is offered, clear it automatically
  after a short delay.
- **Sensitive-field reveal:** PIN/CVV and passwords stay masked by default; revealing a
  value requires a fresh biometric check (Phase 2).
- **Tamper/auth-failure behavior:** never reveal data on failed auth or failed decryption.
- **[DECISION NEEDED]** Lockout policy after repeated failed unlock attempts (e.g.
  temporary lockout, or optional wipe-after-N-failures).

---

## 7. Non-Functional Requirements

- **Usability:** simple, clean UI; quick to add and find a record.
- **Performance:** instant unlock and search even with many records.
- **Reliability:** export/import must be robust; never silently lose or corrupt data.
- **Privacy:** no analytics, no ads, no third-party trackers, no network calls.
- **Maintainability:** clear separation between data, encryption, and UI layers.
- **[DECISION NEEDED]** Localization / languages (default: English only for v1).
- **[DECISION NEEDED]** Light/dark theme support (default: follow system theme).

---

## 8. Key User Flows

1. **First launch / setup:** set up unlock method (biometric + fallback); later, set backup passphrase.
2. **Unlock:** open app → fingerprint (or fallback) → vault shown.
3. **Add record:** choose type → fill fields → save (encrypted).
4. **View/edit/delete record:** with sensitive fields masked by default and reveal-on-tap.
5. **Global search:** tap the search icon → type a name (card, bank, document, username, notes…) → matching records from **all sections** are shown together.
6. **Export backup:** authenticate → enter backup passphrase → generate encrypted JSON → save/share file.
7. **Import backup:** pick file → enter passphrase → choose replace/merge → restore.
8. **Auto-lock:** app backgrounded/timed out → re-locks.
9. **Reveal a secret (Phase 2):** tap the eye on a masked PIN/CVV or password → fingerprint check → value shown.

---

## 9. Phased Roadmap

**Phase 1 — Core local vault (current priority)**
- All four sections with correct **entry, storage, and display** of data
  (Cards with debit/credit label, Documents, Login Details, Others).
- Per-section **list + Add (+)** and **view / edit / delete**.
- **Global search** from the Home screen across all sections (by name, bank, document, username, notes).
- Fingerprint/biometric unlock + device-credential fallback.
- Sensitive fields (PIN/CVV, password) **masked by default** with a simple show/hide toggle.
- Auto-lock and screenshot protection.
- *Recommended:* encryption at rest from the start (good practice for stored secrets).

**Phase 2 — Encryption / decryption & backup (final version, later)**
- User-set encryption passphrase.
- Export all data to an **encrypted JSON file**.
- Import + **decrypt** to restore data.
- Fingerprint-gated reveal of masked PIN/CVV and passwords.

> Priority: **Phase 1 (correct data entry + display) is the immediate focus.** The
> passphrase-based **encryption/decryption and JSON export/import are Phase 2** and come later.

---

## 10. Development Workflow & Engineering Standards

The AI building this app must follow a **clean, structured, professional** process.

**Architecture & code**
- Use a **clean, layered architecture** with clear separation of concerns
  (suggested: **MVVM** — UI / ViewModel / Repository / Data layers).
- Use idiomatic, modern **Kotlin** with standard Android Jetpack components
  (suggested: Room for the encrypted local database, ViewModel, etc.).
- Define clear, strongly-typed **entities/models** for each record type:
  `Card`, `Document`, `LoginDetail`, `Other` (plus supporting models).
- Code must be **clean, readable, consistently styled, and well-organised** — meaningful
  names, no dead code, comments where they add value.
- Always **consider the existing codebase** and keep it consistent; build feature on
  feature without breaking what already works.

**Git workflow (required)**
- Use a **feature-branch workflow**: one branch per feature
  (e.g. `feature/card-storage`, `feature/biometric-auth`, `feature/export-import`).
- Implement and verify a feature on its branch, then **merge it into `main`**.
- Keep **`main` always buildable** and in a working state.
- Write **clear, descriptive commit messages**; keep commits focused.
- Build features in the **phased order** (Phase 1 first, then Phase 2).

---

## 11. Open Questions / Decisions Needed (summary)

1. **Minimum Android version?** (suggested: API 28 or 29)
2. **Backup passphrase:** separate from app-unlock, or the same? (default: separate)
3. **Import behavior:** replace, merge, or user-choice? (default: user-choice)
4. **Failed-attempt lockout / optional wipe-after-N-failures policy?**
5. **Localization & theming defaults?** (default: English only, follow system theme)

*Resolved:* Framework = **Native Android (Kotlin)** · Attachments = **text-only (v1)** · Fallback unlock = **device credential (PIN/pattern/password)**.

---

## 12. Out of Scope (v1)

- Any cloud sync, server, or online account.
- Multi-user / shared vaults.
- Cross-platform (iOS, web, desktop).
- Auto-upload of backups (user moves the exported file manually).
- Password autofill / browser integration.
- Image/scan attachments for records (deferred to a later version).

---

*Prepared as a planning document. Confirm the **[DECISION NEEDED]** items and this can be handed to the build phase.*


## Build Setup

- **Language**: Kotlin 2.0.21
- **UI**: Jetpack Compose (BOM 2024.09.00)
- **AGP**: 8.10.0 (Android Studio compatibility ceiling)
- **Gradle**: 8.11.1
- **compileSdk**: 35 | **minSdk**: 26 | **targetSdk**: 35

## Key Constraints

Android Studio supports AGP up to 8.10.0 — do not upgrade AGP beyond this without first upgrading Android Studio.

Library versions must stay compatible with compileSdk 35:
- `core-ktx`: 1.15.0
- `lifecycle-runtime-ktx`: 2.8.7
- `activity-compose`: 1.10.0

## Common Commands

```bash
./gradlew assembleDebug        # build debug APK
./gradlew test                 # run unit tests
./gradlew connectedAndroidTest # run instrumented tests
```

## Project Structure

```
app/src/main/java/com/aks/offvault/
├── MainActivity.kt
└── ui/theme/
    ├── Color.kt
    ├── Theme.kt
    └── Type.kt
```