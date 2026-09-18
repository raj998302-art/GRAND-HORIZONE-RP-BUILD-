# Grand Horizon RP — Branding Audit Map

Complete map of old-brand strings found across the supplied project, with
status: CHANGED (working copy) vs LEFT (with reason).

Originals preserved in `backup/original/`. Edits applied only to `working/`.

---

## SERVER-SIDE (`work.zip` → `gamemodes/laird.pwn`, `server.cfg`)

### ✅ CHANGED (working copy)

| File | Line | Old | New |
|---|---|---|---|
| `laird.pwn` | 591 | `#define SERVER_NAME "ELITE MOBILE RP"` | `#define SERVER_NAME "GRAND HORIZON RP"` |
| `laird.pwn` | 65020 | `…BLACK RUSSIA \| Admin Rules` | `…GRAND HORIZON RP \| Admin Rules` |
| `laird.pwn` | 66254 | `…Enjoy playing on BLACK RUSSIA` | `…Enjoy playing on GRAND HORIZON RP` |
| `server.cfg` | 4 | `hostname ELITE MOBILE RP \| Hindi Server` | `hostname Grand Horizon RP` |

**Cascade effect of the SERVER_NAME macro change** (auto-applied, no per-line edit needed):
- `laird.pwn:8097` `SendRconCommand("hostname "SERVER_NAME"")` → runtime hostname
- `laird.pwn:8098` `SendRconCommand("weburl www."SERVER_SITE"")` → weburl (uses SERVER_SITE, not SERVER_NAME)
- ~20 dialog headers `"{FF5252}"SERVER_NAME"{ffffff} | Work day"` (lines ~10701–10902) → now "GRAND HORIZON RP | Work day"

### ⏸️ LEFT (needs operator decision)

| File | Line(s) | String | Why left |
|---|---|---|---|
| `laird.pwn` | 592 | `#define SERVER_SITE "lemehost.com"` | Domain — replace with operator's real domain |
| `laird.pwn` | 47538, 47561 | `…rules apply to all players of RUSSIA-MOBILE.RU` | External domain reference — needs operator's new domain |
| `Inventory.inc` | 1398 | `"BlackRussia Cap"` | **In-game item display name** (maps to item ID). Content, not server branding. |
| `ipacket.inc` | 4619, 4655, 4656, 5313, 5399 | `"BlackRussia …"`, `"Банная шапка BLACK RUSSIA"` | **In-game item names** (caps/masks/hats) mapped to numeric IDs (921, 978, 983, 2003, 9973). Display-text change is safe, but it's a content/scope decision. |

---

## CLIENT-SIDE (`launcher.apk`) — NOT MODIFIED

The APK's user-visible branding strings live in `resources.arsc` (binary) and
the 7 `classes*.dex` files (binary). Strings confirmed present:

- `"BLACK RUSSIA"` (multiple languages — incl. Portuguese `"Download de arquivo do BlackRussia"`)
- `"Black Russia"`, `"BlackRussia"`
- Launcher UI keys: `launcher_dialog_not_enough_space`,
  `launcher_do_you_want_to_download`, `launcher_update_launcher_message`,
  `launcher_error_connect_with_timer`

**Why NOT changed:** modifying these requires decompiling the APK (apktool/jadx
→ smali/Java), editing, repackaging, and re-signing. This environment has NO
apktool/jadx/zipalign/apksigner, AND the APK contains `libsigner.so` — an
anti-tamper / integrity-verification native library that would flag a modified
package. Per project Rule 7 ("Do NOT disable security/integrity mechanisms"),
this is REPORTED as a blocker, not circumvented.

Decompiling+rebranding a compiled third-party launcher also has IP/circumvention
concerns that this assistant will not facilitate.

---

## CONTENT (`files.zip`) — NOT MODIFIED

No branding strings in `files.zip` (it is proprietary `.bpc`/`.tmb` binary
packs read by `libblackrussia-client.so`). Content categories:
`mesh/` (47), `textures/` (95), `audio/` (7), `resources/` (15), `jsons/` (6),
root `.bpc` (`launcher.bpc`, `gui.bpc`, `common.bpc`).

No modification attempted — proprietary format, no source.
