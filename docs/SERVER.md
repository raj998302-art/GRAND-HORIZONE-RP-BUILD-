# Grand Horizon RP — Server

SA-MP server gamemode, extracted from the supplied `work.zip` into `server/`.

## Layout

```
server/
├── gamemodes/
│   ├── laird.pwn          ← PAWN source (GHRP-branded)
│   ├── laird.amx          ← compiled bytecode (RECOMPILE needed — see below)
│   ├── gui/               ← GUI modules (cases, family, inventory, plates, regauth, reward, tuning)
│   └── modules/core/      ← admin, vehicle modules
├── include/               ← PAWN includes (a_mysql, Pawn.CMD, Pawn.RakNet, streamer, sscanf2, …)
├── pawno/                 ← Pawno IDE config/sound
├── plugins/               ← .so (Linux, 20) + .dll (Windows, 11): mysql_static, pawnraknet, pawncmd, profiler, sscanf, streamer, sampvoice, tutor, pawn_json
├── scriptfiles/           ← vehicles.json, whitelist.ini
├── reytize_fixed.sql      ← MySQL schema import
└── server.cfg             ← SA-MP config (GHRP-branded hostname)
```

## Server endpoint

- IP / host: `142.132.203.47`
- Port: `14448` (SA-MP)  →  query port `14571` (port + 123)
- Hostname (server.cfg): `Grand Horizon RP` (rebranded from `ELITE MOBILE RP | Hindi Server`)
- Gamemode: `laird` (`gamemode0 laird 1`)
- Language: Hindi
- maxplayers: 50

## Branding applied (server-side)

| File | Line | Old | New |
|---|---|---|---|
| `gamemodes/laird.pwn` | 591 | `#define SERVER_NAME "ELITE MOBILE RP"` | `#define SERVER_NAME "GRAND HORIZON RP"` |
| `gamemodes/laird.pwn` | 65020 | `…BLACK RUSSIA \| Admin Rules` | `…GRAND HORIZON RP \| Admin Rules` |
| `gamemodes/laird.pwn` | 66254 | `…Enjoy playing on BLACK RUSSIA` | `…Enjoy playing on GRAND HORIZON RP` |
| `server.cfg` | 4 | `hostname ELITE MOBILE RP \| Hindi Server` | `hostname Grand Horizon RP` |

The `SERVER_NAME` macro cascades to ~20 dialog headers and the runtime hostname
(via `SendRconCommand("hostname "SERVER_NAME"")` at laird.pwn:8097).

## ⚠️ Recompile required (BLOCKED here)

`laird.pwn` source edits take effect **only after recompilation** to
`laird.amx` with the SA-MP PAWN compiler (`pawncc`). No `pawncc` is available
in this sandbox. The operator must recompile on the lemehost server (it has the
compiler) or locally, then restart the SA-MP server.

## NOT changed (documented)

- `SERVER_SITE` (`lemehost.com`) — replace with operator's real domain.
- `RUSSIA-MOBILE.RU` references (laird.pwn:47538, 47561) — external domain.
- Item names containing "BlackRussia" (`Inventory.inc`, `ipacket.inc`) — in-game
  content names mapped to numeric IDs; left as a content/scope decision.

## Database (server-side secret — NOT in this repo)

The MySQL credentials belong on the lemehost server only. The SA-MP
`mysql_static` plugin connects from the server process; the Android client never
sees them. Rotate the previously-shared password (it was exposed in chat).
