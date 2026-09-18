# Grand Horizon RP — Client (native)

The supplied Android client (`launcher.apk` from the purchased package) is a
**compiled, signed binary**. It is preserved untouched in
`backup/original/launcher.apk` (SHA-256 in `backup/original/checksums.txt`).

## What's inside (from the read-only audit)

- **7 DEX files** (`classes.dex`…`classes7.dex`, ~85 MB total) — multi-dex.
- **Native libs** in 2 ABIs: `arm64-v8a`, `armeabi-v7a` (no x86 → no Intel emulators).
- `libblackrussia-client.so` — the main client native lib.
- `libupdate-manager.so` — content download/update system.
- `libsigner.so` — **anti-tamper / integrity verification** (do NOT disable).
- `libbass.so` — BASS audio.
- `libcrypto.so` + `libssl.so` — OpenSSL.
- `libz-ng.so` — zlib-ng (compression).
- `libcrashlytics*.so` — Firebase Crashlytics (4 files).
- Bundled SDKs: Google Play Billing (IAP), Firebase, Adjust (marketing), Helpshift.

## Build paths leaked in the native lib

`/Users/evgeny/Projects/BLACKRUSSIA/Game/BRClient_Native/vendor/opus/opus-1.4/...`
confirms the supplied binary is the Black Russia client, bundles the Opus codec
(BSD-licensed → attribution required).

## Why the launcher here does NOT repackage this APK

1. Repackaging requires decompiling (apktool/jadx) → not available here + edges
   into reverse-engineering a third-party compiled binary.
2. `libsigner.so` would flag a modified package (per project Rule 7: report, do
   not circumvent).
3. The custom launcher in `launcher/android/` is a clean rebuild that does NOT
   depend on patching this APK.

## Integration path (operator decision)

To make PLAY actually launch the game, the operator has two honest options:

**Option A — host the supplied APK as-is** and have the launcher open it:
- The launcher can offer the supplied `launcher.apk` as a download (hosted on
  the same CDN as the content manifest).
- The user installs it; our launcher can fire an `Intent` to launch it by
  package name (once the package name is confirmed — needs aapt/apktool to read
  the binary `AndroidManifest.xml`).

**Option B — obtain the actual Android SOURCE** from the original supplier
(the `build.gradle` + `src/` + `AndroidManifest.xml` XML tree that produced
`launcher.apk`). Place it in the repo, then `android-build.yml` builds a GHRP-
branded APK from source. This is the clean path; decompiling is not.

Both options preserve `libsigner.so` (anti-tamper) and all third-party notices.
