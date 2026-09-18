# Grand Horizon RP — Launcher Asset Reuse Report

Inspection of `files.zip`'s full tree (not just game-data). Launcher/update/
loading assets were located, understood, and reused in the Android launcher.

`files.zip` is NOT only game data — it contains the launcher's visual system:
backgrounds, intro video, and the original XAML UI definitions.

---

## Summary table

| Asset | Source path in `files.zip` | Type | Size | Resolution / Duration | Purpose | Referenced by | Reused in launcher? |
|---|---|---|---|---|---|---|---|
| `launcher.mp4` | `resources/video/launcher.zip` (and duplicate `launcher_video.zip`) | MP4 (H.264 + AAC) | 43.9 MB | 1280×720, 48.2 s, 30 fps, 7.3 Mb/s | Launcher intro / background video | `LauncherScreen.xaml` commented `MediaElement` (`bhgpackage://resources/video/launcher_video.zip?videoPath=launcher.mp4`) | ✅ bundled at `app/src/main/assets/media/launcher.mp4`, played by `SplashActivity` `VideoView` |
| `bg.png` | `launcher.bpc` → `Images/bg.png` | PNG RGBA | 4.0 MB | 1920×1080 | Launcher (home) full-bleed background | `<Image Source="Images/bg.png" Stretch="UniformToFill">` in `LauncherScreen.xaml` | ✅ bundled at `res/drawable-nodpi/ghr_bg.png`, used in `activity_home.xml` |
| `bg_start_screen.png` | `resources/images/backgrounds/bg_start_screen.zip` | PNG RGBA | 3.3 MB | 1920×1080 | Splash / start-screen background | start screen image pack | ✅ bundled at `res/drawable-nodpi/ghr_start_bg.png`, used in `activity_splash.xml` |
| `LauncherScreen.xaml` | `launcher.bpc` | XAML (NoesisGUI/.NET) | 21 KB | 1920×1080 design | Original launcher home UI definition (bg image + gradient vignette + progress bar) | `LauncherVM` binding | 📐 used as DESIGN REFERENCE — reproduced as `activity_home.xml` (FrameLayout: bg image + vignette view + content) |
| `SplashScreen.xaml` | `launcher.bpc` | XAML | 35 KB | 1920×1080 design | Original splash UI (dark `#0D0D0D`, 6 s fade Storyboard, bear logo) | `SplashVM` binding | 📐 used as DESIGN REFERENCE — reproduced: `#0D0D0D` bg, `splash_fade_in.xml` (0→1.0 over 1.5 s after 0.8 s offset). Bear logo vector NOT reused (Black Russia branding); replaced by GHRP horizon vector. |
| `LauncherDialog.xaml` | `launcher.bpc` | XAML | 11 KB | — | Launcher dialog UI | — | 📄 preserved for reference; not yet wired (operator can adapt for dialogs) |
| `LauncherPolicy.xaml` | `launcher.bpc` | XAML | 2 KB | — | Launcher policy/ToS screen | — | 📄 preserved for reference |
| `client-jsons.zip` | `jsons/client-jsons.zip` (+`en/`, `pt-br/`) | ZIP of 40+ JSON | ~350 KB each | — | Client UI/data configs (cinematic, buttons, containers, banners, cases, etc.) | client runtime | 📄 configs documented; in-game content configs (cases/cars/craft/…) are NOT launcher UI. `cinematic.json` (intro cutscene list) documented in dependency map. |
| `gui.bpc` | root `gui.bpc` (ZIP) | ZIP of `.json/` tree | varies | — | In-game GUI menu definitions (ActiveTask/AdminTools/BattleForContainers/…) | client GUI runtime | 📄 NOT launcher UI — in-game menus. Preserved, documented. |
| `common.bpc` | root `common.bpc` | binary (not a ZIP) | large | — | Encrypted/proprietary pack | native client | ⛔ not inspectable without the native client; not a launcher asset |

---

## Videos found
1. `launcher.mp4` — 1280×720 H.264+AAC, 48.2 s. **Reused** as splash intro (tap-to-skip).
2. `cinematic.json` references additional in-game cinematic videos (`Cin_FTUE-2`, `cinematic_intro-S6_2026`, `cinematic-quest1_intro`, …) by name — these live elsewhere in the package and are in-game cutscenes, not launcher UI.

## Animations found
- `SplashScreen.xaml` → `PresentAnimation` Storyboard: `ContentRoot` opacity 0→0 (0–0.8 s) →1.0 (2.3 s) hold→1.0 (3.1 s) →0 (4.6 s); `WindowRoot` 1.0→0 (5.4–6.0 s).
  **Reproduced** as `res/anim/splash_fade_in.xml` (alpha 0→1.0, 1500 ms, startOffset 800 ms).

## Loading assets found
- `bg_start_screen.png` (start screen) — **reused** (splash background).
- `bg.png` (launcher background) — **reused** (home background).
- Gradient vignette `#54000000`→transparent→`#19000000` (`LauncherScreen.xaml` "Fade" rectangle) — **reproduced** as `res/drawable/bg_vignette.xml`.

## UI assets found
- Original launcher UI defined in XAML (`LauncherScreen.xaml`, `SplashScreen.xaml`, `LauncherDialog.xaml`, `LauncherPolicy.xaml`).
- `buttons-config.json` — feature flags (`isShowSimButton`, `isShowTanpinButton`) for in-game buttons (not launcher).
- `containers.json` — in-game NPC/dialog UI (not launcher).

## Fonts found
- `assets/Fonts/` inside `launcher.apk` (bundled APK fonts). Not extracted into this launcher yet; the launcher uses the system default + Geist (via the Next.js companion only). Operator can bundle the supplied Fonts into `res/font/` if exact typography is required.

## Icons found
- `launcher.apk` ships `mipmap`/`res` icons (binary, Black Russia branded). NOT reused — replaced by the GHRP horizon vector (`ic_launcher_foreground.xml`). Operator can swap in real GHRP logo PNGs.

## Launcher configs found
- `assets/config/launcher_config.json` (in the bundled APK) — likely the original launcher config. Not directly readable (binary APK). Our equivalent: `launcher/android/app/src/main/assets/config/launcher_config.json`.
- `cinematic.json` — cutscene sequence config.

## Launcher-related code/config found
- `launcher.bpc` (ZIP) contains the XAML UI + `bg.png` — this IS the launcher's resource pack.
- `gui.bpc` (ZIP) contains in-game GUI menu JSONs (not launcher).
- `LauncherVM` / `SplashVM` are .NET ViewModels referenced in XAML (the compiled bytecode is in `launcher.apk` dex; source not available).

---

## Reuse summary

| Status | Count | Assets |
|---|---|---|
| ✅ Reused (bundled + wired) | 3 | `launcher.mp4`, `bg.png`, `bg_start_screen.png` |
| 📐 Reused as design reference | 2 | `LauncherScreen.xaml`, `SplashScreen.xaml` (reproduced as Android layouts + anim + vignette) |
| 📄 Preserved/documented (not launcher UI) | 4 | `LauncherDialog.xaml`, `LauncherPolicy.xaml`, `client-jsons.zip`, `gui.bpc` |
| ⛔ Not inspectable (proprietary binary) | 1 | `common.bpc` |
| ❌ Not reused (BR branding, replaced) | 1 | bear-logo vector (replaced by GHRP horizon vector) |

## Existing visual system preserved: **YES**
- Dark `#0D0D0D` base color (matches `SplashScreen.xaml Background="#FF0D0D0D"`).
- Full-bleed background image + gradient vignette overlay (matches `LauncherScreen.xaml`).
- Splash fade-in animation timing (matches `PresentAnimation` Storyboard).
- Intro video playback (matches the commented `MediaElement` intent).

## Grand Horizon RP branding integrated: **YES**
- App name, launcher title, splash title, all button strings → "Grand Horizon RP".
- Server hostname + SERVER_NAME macro → "Grand Horizon RP".
- Old "BLACK RUSSIA" text in `SplashScreen.xaml` line 215/221 → NOT bundled (the XAML is reference-only; our splash uses the GHRP horizon vector + "Grand Horizon RP" string from `strings.xml`).

## Custom launcher build: source written ✅ — build not yet verified on CI (no token; operator pushes).
## APK: pending GitHub Actions run (operator push) — artifact `grand-horizon-rp-launcher-apk`.
