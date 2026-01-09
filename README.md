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

### Proposed Activity Diagram
<img width="2055" height="562" alt="Class Diagram(2)" src="https://github.com/user-attachments/assets/ae9c0791-25d2-4433-a110-eea7ba65e128" />

### First Good Design
#### Welcome Screen
<img width="264" height="447" alt="welcomescreen" src="https://github.com/user-attachments/assets/bd9f0797-103d-433b-8b21-d3ebe38b1c6c" />

#### Home Screen 1
<img width="264" height="447" alt="homescreen1" src="https://github.com/user-attachments/assets/2ab95203-44d0-4f63-9cb8-817f2eb115a7" />


#### Home Screen 2
<img width="290" height="447" alt="homescreen2" src="https://github.com/user-attachments/assets/858bb7a3-0d5d-479c-bb1b-68d708b06954" />



