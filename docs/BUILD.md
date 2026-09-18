# Grand Horizon RP — Build

The Android launcher builds via GitHub Actions. No local Android SDK is
required — the workflow installs JDK + Android SDK on a GitHub runner.

## Workflow

`.github/workflows/android-build.yml`:

1. Checkout repo.
2. Set up JDK 17 (Temurin).
3. Set up Android SDK (platform 34, build-tools 34.0.0).
4. Cache Gradle.
5. `gradle assembleDebug` (debug) and `assembleRelease` (release).
6. Sign release via GitHub Secrets (`KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`,
   `KEY_ALIAS`, `KEY_PASSWORD`) — if set; otherwise unsigned.
7. Verify release signing with `apksigner verify --print-certs`.
8. Upload `grand-horizon-rp-apk` artifact (30-day retention).
9. On failure, upload Gradle reports as a separate artifact.

## Local build (optional)

Requires Android SDK + JDK 17:
```
cd launcher/android
gradle assembleDebug
# APK at app/build/outputs/apk/debug/app-debug.apk
```

## Release signing (CI secrets)

Generate a keystore locally (JDK needed):
```
keytool -genkey -v -keystore grand_horizon.jks \
  -keyalg RSA -keysize 2048 -validity 10000 -alias ghrp
base64 -w0 grand_horizon.jks > keystore.b64
```
Add 4 GitHub Actions Secrets:
- `KEYSTORE_BASE64` ← contents of `keystore.b64`
- `KEYSTORE_PASSWORD` ← keystore password
- `KEY_ALIAS` ← e.g. `ghrp`
- `KEY_PASSWORD` ← key password

Never commit `grand_horizon.jks` or these passwords. They live only in GitHub
Secrets.

## Build status (honest)

- Workflow file: ✅ written + committed.
- Build verified on GitHub Actions: ❌ NOT VERIFIED — the GitHub token pasted in
  chat is compromised and is not used. The operator must push the repo to their
  own private GitHub and observe the Actions run there.
- APK artifact exists: ❌ NOT YET — pending operator push + Actions run.
