# PhotoTagger

A small-but-complete Android app that covers multiple bases/layers of SW development **end-to-end**: Compose UI, authentication, photo import, **cloud tagging via Firebase Function + LLM**, storage/retrieval, and **CI/CD**.

## Scope
- Import photos with **Android Photo Picker**.
- **Sign in** with email/password (or guest).
- Upload selected photos (or thumbnails) to **Firebase Storage**.
- Call **Cloud Function** to tag: Function uses an external **LLM/Vision** API.
- Persist metadata + tags in **Firestore**; show **Library**, **Search**, **Details**.
- **CI/CD**: GitHub Actions build, lint, tests, signed AAB artifact on tag.


## Architecture
- **App (Android):** Kotlin, Compose, Navigation, Hilt, WorkManager (enqueue upload -> tagging flow), Coil.
- **Backend (Firebase):** Auth, Storage, **Cloud Functions** (HTTP callable for `annotatePhoto`), Firestore.
- **Secrets:** LLM API key kept server-side (function env vars).

### Sequence
