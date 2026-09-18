# Grand Horizon RP — Asset Dependency Map

Traces: asset → config/reference → screen/function → launcher behavior.
Built from inspecting `files.zip`'s nested zips + `.bpc` (which are themselves
ZIPs) + the XAML UI definitions inside `launcher.bpc`.

---

## 1. Splash screen

```
bg_start_screen.png  (files/resources/images/backgrounds/bg_start_screen.zip)
        │
        ▼
activity_splash.xml :: ImageView src=@drawable/ghr_start_bg
        │
        ├── (overlay) bg_vignette.xml  ◄── reproduces LauncherScreen.xaml "Fade" gradient
        │
        ├── launcher.mp4  (files/resources/video/launcher.zip)
        │       │
        │       ▼  VideoView (SplashActivity.copyAssetToCache → setVideoURI)
        │       │    on completion → HomeActivity
        │       │    tap-to-skip    → HomeActivity
        │       │
        │       └── referenced in original by: LauncherScreen.xaml
        │           <MediaElement Source="bhgpackage://resources/video/launcher_video.zip?videoPath=launcher.mp4">
        │
        └── GHRP logo (ic_launcher_foreground.xml — horizon vector)
                + splash_fade_in.xml  ◄── reproduces SplashScreen.xaml PresentAnimation
                                         (alpha 0→1.0, 1500ms, startOffset 800ms)
```

Original splash used a Black Russia **bear** vector (`SplashScreen.xaml`
"Logo bear", Canvas+Path) + "BLACK"/"RUSSIA" TextBlocks. Bear = BR branding
→ replaced with the GHRP horizon vector. Animation timing preserved.

---

## 2. Home / launcher screen

```
bg.png  (files/launcher.bpc :: Images/bg.png)
        │
        ▼
activity_home.xml :: ImageView src=@drawable/ghr_bg  (scaleType=centerCrop)
        │
        ├── (overlay) bg_vignette.xml  ◄── reproduces LauncherScreen.xaml "Fade"
        │       original: <Rectangle x:Name="Fade"> with LinearGradientBrush
        │       (top #54000000 → mid #00000000 @0.58 → bottom #19000000)
        │       original behavior: collapses when ProgressPercent == 100
        │
        └── content (ScrollView):
                ├── GHRP logo + title + server endpoint
                ├── live SA-MP status (SampQuery → UDP port+123 "i")
                └── buttons: PLAY / NEWS / SERVER STATUS / UPDATE / SETTINGS / ABOUT
```

Original `LauncherScreen.xaml` also bound a `ProgressBar` to
`ProgressPercent` (with a gradient foreground). Our home shows server status;
the progress UI lives in `UpdateActivity` (next).

---

## 3. Update / download screen

```
manifest_url  (launcher_config.json)
        │
        ▼
ManifestDownloader.fetch()  →  UpdateManifest  (data/UpdateManifest.kt)
        │
        ▼  for each ManifestFile:
ContentDownloader.download()  (Flow<Progress>)
        ├── OkHttp GET url
        ├── stream to <client-data>/.part
        ├── SHA-256 verify  (HashUtil)  ◄── manifest.sha256
        └── rename .part → final path in FileUtil.clientDataDir()
                │
                ▼
        activity_update.xml :: phaseText + detailText + progressBar + actionBtn
```

This is NEW plumbing (no equivalent XAML asset was bundled for the download
flow — the original `LauncherScreen.xaml` only showed a `ProgressBar` bound to
`ProgressPercent`; the actual download logic lives in the compiled APK's dex
+ native `libupdate-manager.so`, which is not source-reusable here).

---

## 4. Native client integration (documented dependency)

```
libblackrussia-client.so  (launcher.apk :: lib/{arm64-v8a,armeabi-v7a}/)
        │
        ▼  reads
common.bpc, gui.bpc, mesh/*.bpc, textures/*.bpc, audio/*.bpc  (files.zip)
        │
        ▼  renders
the actual game (RenderQueue / TextureDatabase / Raster — GTA SA-style engine)
```

The custom launcher does NOT replace this pipeline. `PLAY` is a documented
integration point — see `docs/CLIENT.md` Option A (host supplied APK, launch
by package) / Option B (obtain real Android source). The `.bpc`/`.tmb`
content is consumed by the supplied native lib, not by our launcher.

---

## 5. Config references (from client-jsons.zip)

| Config | Purpose | Launcher-relevant? |
|---|---|---|
| `cinematic.json` | Cutscene sequence: `waitTimeDuration`, `skipButtonTime`, `videos[]` (`videoName`, `time`, `skipFlag`) | Indirect — defines in-game cinematics, not launcher. Confirms a skip-button pattern we mirror in the splash (tap-to-skip). |
| `buttons-config.json` | `isShowSimButton`, `isShowTanpinButton` feature flags | No — in-game button visibility. |
| `containers.json` | NPC info / dialog / teleport coords / dialog buttons + sounds | No — in-game NPC dialogs. |
| `banners-hub.json` (133 KB) | Banner hub definitions | Possibly — could feed a NEWS screen; not yet wired. |
| `cases.json`, `craft.json`, `donate-items.json`, `inventory.json`, … | Game content data | No — game content. |

---

## 6. What is NOT reused and why

| Asset | Reason |
|---|---|
| Bear-logo vector (`SplashScreen.xaml`) | Black Russia branding → replaced by GHRP horizon vector. |
| `common.bpc` | Proprietary encrypted pack; only the native client reads it. Not a launcher asset. |
| `gui.bpc` JSON tree | In-game GUI menus (AdminTools, BattleForContainers, …), not launcher UI. |
| `launcher.apk` dex/smali | Compiled bytecode; decompiling/repackaging declined (anti-tamper `libsigner.so` + IP). |
| In-game cinematic videos (`Cin_FTUE-*`, etc.) | In-game cutscenes, not launcher. Live in the package; played by the native client. |
