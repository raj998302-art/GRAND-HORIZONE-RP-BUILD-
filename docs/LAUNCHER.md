# Grand Horizon RP — Launcher

A clean, buildable Android launcher written from scratch for Grand Horizon RP.
It is NOT a repackage of any supplied APK — it is new source that builds via
`gradle assembleDebug` on GitHub Actions.

## Project layout

```
launcher/android/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── gradle/wrapper/gradle-wrapper.properties
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── assets/config/launcher_config.json   ← central server config (single source of truth)
│       ├── java/com/grandhorizonrp/launcher/
│       │   ├── LauncherApp.kt                   ← loads ServerConfig from assets
│       │   ├── data/ServerConfig.kt              ← host/port/manifest_url (no secrets)
│       │   ├── data/UpdateManifest.kt            ← manifest JSON model
│       │   ├── net/SampQuery.kt                 ← SA-MP UDP "i" info query
│       │   ├── net/ManifestDownloader.kt         ← fetch + parse manifest (OkHttp + Gson)
│       │   ├── net/ContentDownloader.kt          ← download + SHA-256 verify (Flow)
│       │   ├── util/HashUtil.kt                  ← SHA-256
│       │   ├── util/FileUtil.kt                  ← scoped-storage client-data dir
│       │   └── ui/{Splash,Home,Update,Settings,About}Activity.kt
│       └── res/
│           ├── values/{strings,colors,themes,dimens}.xml
│           ├── layout/activity_{splash,home,update,settings,about}.xml
│           └── drawable/{ic_launcher_foreground,ic_launcher_background,bg_btn_primary}.xml
└── README.md (this file)
```

## Build

Local (requires Android SDK + JDK 17):
```
cd launcher/android
./gradlew assembleDebug      # → app/build/outputs/apk/debug/app-debug.apk
```

CI (no local Android toolchain needed) — see `.github/workflows/android-build.yml`.
On push, GitHub Actions runner builds the APK and uploads it as an artifact.

## Configuration

Edit `app/src/main/assets/config/launcher_config.json`:
```json
{
  "project": "Grand Horizon RP",
  "server_host": "142.132.203.47",
  "server_port": 14448,
  "manifest_url": "https://<your-cdn>/manifest.json",
  "launcher_version": "1.0.0",
  "website_url": "https://<your-site>",
  "discord_url": ""
}
```
This is the single source of truth for the server endpoint — no hardcoded host/port
in activities.

## Branding

- App name: `Grand Horizon RP` (`res/values/strings.xml → app_name`)
- Launcher title, splash, all UI strings: GHRP-branded.
- Icon: vector placeholder (`ic_launcher_foreground.xml` horizon motif). Replace
  with the operator's real logo PNGs in `mipmap-*` when supplied.

## Screens

| Screen | Activity | Purpose |
|---|---|---|
| Splash | `SplashActivity` | Logo + 1.2s, routes to Home |
| Home | `HomeActivity` | PLAY / NEWS / SERVER STATUS / UPDATE / SETTINGS / ABOUT + live SA-MP status |
| Update | `UpdateActivity` | manifest fetch → download → SHA-256 verify → install, with per-file progress + error states |
| Settings | `SettingsActivity` | read-only display of bundled config |
| About | `AboutActivity` | GHRP description + third-party credits/NOTICES (preserved) |

## Native client dependency

Launching the actual game requires the supplied native client
(`libblackrussia-client.so` + content pipeline). That integration is documented
in `docs/CLIENT.md` and is NOT part of this clean launcher source. The PLAY
button currently routes to the Update screen as a placeholder until the native
client integration is wired (operator decision).
