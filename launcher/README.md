# Grand Horizon RP Launcher (Android)

Clean, buildable Android launcher for Grand Horizon RP. Not a repackage of any
supplied APK — new Gradle + Kotlin source that builds via GitHub Actions.

- **Package:** `com.grandhorizonrp.launcher`
- **Min SDK:** 24 (Android 7.0) — **Target/Compile SDK:** 34
- **Version:** 1.0.0
- **Central config:** `app/src/main/assets/config/launcher_config.json`
- **Build:** `gradle assembleDebug` (locally) or GitHub Actions (`.github/workflows/android-build.yml`)

## Screens

Splash → Home (PLAY / NEWS / SERVER STATUS / UPDATE / SETTINGS / ABOUT) → Update
(manifest fetch → download → SHA-256 verify → install) with full error states.

## Live server status

`HomeActivity` queries the SA-MP server via UDP `SampQuery` (port + 123, "i"
opcode) and shows ONLINE/players or OFFLINE.

## Update system

`UpdateActivity` fetches the JSON manifest from the URL in `launcher_config.json`,
compares each file's declared SHA-256 vs the local copy in app-specific external
storage, downloads missing/updated files via OkHttp, verifies SHA-256, and
installs. Corrupt files are deleted and reported.

See `../docs/LAUNCHER.md` and `../docs/BUILD.md` for full details.
