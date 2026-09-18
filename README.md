# Grand Horizon RP

Integrated project workspace: a clean, buildable Android launcher + branded
SA-MP server config + client content manifest system for Grand Horizon RP.

Originals are preserved untouched in `backup/original/` (with SHA-256). All
edits live in `server/`, `launcher/android/`, `deployment/`, `docs/`.

## Structure

```
grand-horizon-rp/
├── launcher/android/         # clean Android launcher (Gradle + Kotlin) — BUILDABLE
├── client/                    # native client integration (documented dep)
├── game-data/                 # 4GB content manifest reference (CDN-hosted)
├── server/                    # SA-MP gamemode (laird), plugins, scriptfiles, server.cfg — GHRP-branded
├── deployment/
│   ├── update-manifest/       # schema + generator (SHA-256 per file)
│   ├── versioning/
│   └── download-server/
├── backup/original/           # untouched supplied files + checksums
├── docs/                      # ARCHITECTURE, CLIENT, LAUNCHER, SERVER, ASSETS, BUILD, BRANDING, BLOCKERS
├── .github/workflows/         # android-build.yml + server-validation.yml
├── .env.example
├── .gitignore
├── CHANGELOG.md
└── README.md (this file)
```

## Server

- IP / port: `142.132.203.47:14448`
- Gamemode: `laird` (branded `Grand Horizon RP`)
- See `docs/SERVER.md`

## Build the launcher APK

GitHub Actions (no local Android toolchain needed):
1. Push this repo to a **private** GitHub repo.
2. Add signing secrets if you want a signed release
   (`KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`).
3. On push, `.github/workflows/android-build.yml` runs `gradle assembleDebug`
   on a GitHub runner (JDK + Android SDK pre-installed).
4. Download the `grand-horizon-rp-launcher-apk` artifact from the Actions tab.

Local build (needs Android SDK + JDK 17):
```
cd launcher/android
gradle assembleDebug
```

## Status (honest)

| Area | Status |
|---|---|
| Launcher source written | ✅ |
| Launcher build verified on CI | ❌ pending operator push (token not used) |
| Server branding applied (source) | ✅ |
| Server recompiled (.amx) | ❌ pending pawncc on lemehost |
| Runtime test on Android | ❌ no device here |
| Native client integration | 📄 documented dependency — operator decision (see docs/CLIENT.md) |

See `docs/BLOCKERS.md` for the honest blocker list.

## Security

- The GitHub token pasted in chat earlier is compromised — NOT used. Operator
  pushes from their own machine.
- DB credentials are server-side only; never in the APK.
- Signing secrets live only in GitHub Actions Secrets.
