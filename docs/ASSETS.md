# Grand Horizon RP — Client Assets (game-data / files.zip)

The supplied 4 GB client content package (`files.zip`) is preserved untouched.
It is too large for normal git history — see the storage strategy below.

## Contents (audited — facts, not assumptions)

175 files, 4.1 GB uncompressed, all under `files/`:

| Subdir | Count | Format | Content |
|---|---|---|---|
| `textures/` | 95 | `.astc.bpc.tmb` | ASTC-compressed textures |
| `mesh/` | 47 | `.bpc` | 3D content packs (events, DLCs, seasons, casino, cyberfarm…) |
| `resources/` | 15 | mixed | images, **video** (`launcher.zip` 43 MB, `launcher_video.zip` 43 MB, `video.zip` 455 MB) |
| `audio/` | 7 | `.bpc` | `streams.bpc`, samples: GENERIC/STEPS/VEHICLE |
| `jsons/` | 6 | `.zip` | `client-jsons.zip` (default, en, pt-br) |
| root | 3 | `.bpc` | `launcher.bpc`, `gui.bpc`, `common.bpc` |

**Format:** proprietary `.bpc` / `.tmb` binary packs read by
`libblackrussia-client.so`. NOT raw `.txd`/`.dff`/`.img`. Do not assume these
are vanilla GTA San Andreas files — they are the purchased project's packaged
content.

## License / attribution

No `LICENSE`/`README`/`NOTICE` files exist inside `files.zip`. Attribution for
any bundled third-party codecs (Opus is bundled in the client `.so`) must be
added by the operator when redistributing — see `docs/LAUNCHER.md` credits.

## Storage strategy (do NOT commit 4 GB to git)

`game-data/` in this repo holds only:
- this README
- the manifest reference (`game-data/manifests/`)

The actual 4 GB content is hosted on an external CDN (e.g. Cloudflare R2, S3,
or a GitHub Release asset). The update manifest (`deployment/update-manifest/`)
points each file's `url` at the CDN. The launcher downloads + SHA-256-verifies
each file at runtime.

### To produce the manifest

On the operator's machine (where `files.zip` is extracted to `client-data/`):
```
cd deployment/update-manifest
python3 generate_manifest.py /path/to/client-data https://<your-cdn>/client 1.0.0 > manifest.json
# upload manifest.json to the manifest_url in launcher_config.json
```

## Checksums (integrity)

`backup/original/files.zip.REFERENCE.txt` records the SHA-256 of the original
archive. The manifest generator computes per-file SHA-256 for runtime
verification. Mismatches are deleted and reported as corrupt (see
`ContentDownloader`).
