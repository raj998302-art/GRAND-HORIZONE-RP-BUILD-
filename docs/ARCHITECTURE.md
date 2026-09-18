# Grand Horizon RP — Architecture

```
                 GRAND HORIZON RP
                         │
          ┌──────────────┴──────────────┐
          │                             │
      Android Launcher              RP Server
     (launcher/android)        (server/, lemehost)
          │                             │
          │                         142.132.203.47
          │                         Port 14448 (SA-MP)
          │                         Port 14571 (query = port+123)
          │
    1. Fetch manifest.json  ◄── deployment/update-manifest/
    2. Compare local SHA-256
    3. Download missing files
    4. Verify SHA-256 per file
    5. Install into app-specific storage
          │
    6. Launch native client (documented dependency — see CLIENT.md)
          │
          └──────────►  SA-MP server connection
```

## Components

| Path | Component | Owner | Status |
|---|---|---|---|
| `launcher/android/` | Custom Android launcher (Gradle + Kotlin) | Grand Horizon RP (this repo) | ✅ Source written |
| `server/` | SA-MP gamemode (`laird.pwn/.amx`), plugins, scriptfiles, server.cfg | Supplied (Black Russia package), rebranded | ✅ Extracted + hostname/SERVER_NAME rebranded |
| `client/` | Native client integration (`libblackrussia-client.so` + content pipeline) | Supplied binary — documented dependency | 📄 Reference only |
| `game-data/` | 4 GB client content packs (`.bpc`/`.tmb`) — `files.zip` | Supplied, proprietary format | 📄 Referenced via manifest; too large for git history |
| `deployment/update-manifest/` | Manifest schema + generator | Grand Horizon RP | ✅ Schema + generator + example |
| `.github/workflows/` | CI: android-build, server-validation | Grand Horizon RP | ✅ Written (run on GitHub) |

## Data flow

1. **Launcher** reads `assets/config/launcher_config.json` for `server_host`, `server_port`, `manifest_url`.
2. **Home** screen queries SA-MP (`SampQuery`, UDP port+123) → online/players.
3. **Update** screen fetches `manifest.json`, compares each `sha256` vs local copy in app-specific external storage.
4. Missing/mismatched files are downloaded via OkHttp, verified SHA-256, moved into place.
5. **PLAY** launches the native client (dependency on `libblackrussia-client.so` + content pipeline — see `docs/CLIENT.md`).

## Security

- Database credentials are SERVER-SIDE only (`server/`, lemehost). Never embedded in the APK.
- The launcher only knows the public SA-MP endpoint + a CDN manifest URL.
- Signing secrets live in GitHub Actions Secrets (`KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`). Never committed.
- The previously-pasted GitHub token is compromised; it is not used. See `docs/BLOCKERS.md`.
