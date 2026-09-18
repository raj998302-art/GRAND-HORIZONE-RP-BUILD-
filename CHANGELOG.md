# Grand Horizon RP — CHANGELOG

All notable changes. Originals preserved in `backup/original/` with SHA-256.

---

## [Unreleased] — full project integration

### Added — clean Android launcher (NEW source, not a repackage)
- `launcher/android/` — Gradle + Kotlin project, package `com.grandhorizonrp.launcher`
  - 5 activities: Splash, Home, Update, Settings, About
  - `SampQuery` — live SA-MP UDP info query (port+123)
  - `ManifestDownloader` + `ContentDownloader` — fetch manifest, download files
    with per-file SHA-256 verification (OkHttp + Gson + Coroutines Flow)
  - `HashUtil` (SHA-256), `FileUtil` (scoped-storage client-data dir)
  - `ServerConfig` loaded from `assets/config/launcher_config.json` (single
    source of truth — host/port/manifest_url NOT hardcoded in activities)
  - Dark RP theme, GHRP strings, horizon-motif vector icon
  - minSdk 24, target/compile 34, AGP 8.5.2, Kotlin 1.9.24, Gradle 8.8

### Added — update-manifest system
- `deployment/update-manifest/manifest.schema.json` — JSON schema
- `deployment/update-manifest/manifest.example.json` — sample
- `deployment/update-manifest/generate_manifest.py` — walks a content dir,
  computes size + SHA-256 per file, emits manifest.json (smoke-tested ✓)

### Added — CI
- `.github/workflows/android-build.yml` — JDK 17 + Android SDK on GitHub runner,
  `gradle assembleDebug/Release`, signing via Secrets, apksigner verify,
  APK artifact upload (30-day retention)
- `.github/workflows/server-validation.yml` — validates server.cfg branding,
  SERVER_NAME macro, plugin presence (no pawncc on runner)

### Added — docs
- `docs/ARCHITECTURE.md`, `docs/LAUNCHER.md`, `docs/CLIENT.md`,
  `docs/SERVER.md`, `docs/ASSETS.md`, `docs/BUILD.md`
- `docs/BRANDING.md`, `docs/BLOCKERS.md`

### Changed — server-side branding (`server/`, from `work.zip`)
| File | Line | Old | New |
|---|---|---|---|
| `gamemodes/laird.pwn` | 591 | `#define SERVER_NAME "ELITE MOBILE RP"` | `#define SERVER_NAME "GRAND HORIZON RP"` |
| `gamemodes/laird.pwn` | 65020 | `…BLACK RUSSIA \| Admin Rules` | `…GRAND HORIZON RP \| Admin Rules` |
| `gamemodes/laird.pwn` | 66254 | `…Enjoy playing on BLACK RUSSIA` | `…Enjoy playing on GRAND HORIZON RP` |
| `server.cfg` | 4 | `hostname ELITE MOBILE RP \| Hindi Server` | `hostname Grand Horizon RP` |

### NOT changed (documented in BRANDING.md)
- Item names containing "BlackRussia" (in-game content, mapped to numeric IDs).
- `SERVER_SITE` / `RUSSIA-MOBILE.RU` domain references (operator's real domain unknown).

### Required operator follow-up
1. Recompile `laird.pwn` → `laird.amx` on lemehost (pawncc), restart SA-MP server.
2. Push repo to private GitHub; observe Actions run for APK artifact.
3. Provide real website domain → update `SERVER_SITE` + `RUSSIA-MOBILE.RU`.
4. Host 4 GB content on CDN; run `generate_manifest.py` → upload manifest.json.
5. Replace vector logo with operator's real GHRP logo PNGs when supplied.

### Build verification (honest)
- Launcher source written + committed: ✅
- Build verified on GitHub Actions: ❌ pending operator push (token not used)
- APK artifact exists: ❌ pending Actions run
- `laird.amx` recompiled: ❌ no pawncc here
- Android runtime test: ❌ no device here
