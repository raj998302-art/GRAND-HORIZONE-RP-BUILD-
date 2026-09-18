package com.grandhorizonrp.launcher.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.grandhorizonrp.launcher.R
import java.io.File
import java.io.FileOutputStream

/**
 * Splash screen — reproduces the supplied project's splash experience:
 *  • background = supplied bg_start_screen.png (reused from files.zip)
 *  • gradient vignette overlay (reproduces LauncherScreen.xaml "Fade")
 *  • intro video = supplied launcher.mp4 (reused from files.zip, 1280×720 H.264, 48.2s)
 *  • Grand Horizon RP logo + title fade-in (reproduces SplashScreen.xaml PresentAnimation)
 *  • tap-to-skip → Home
 *  • on video completion → Home
 *
 * The original Black Russia bear-logo vector is NOT used (it is Black Russia
 * branding); the GHRP horizon vector logo replaces it.
 */
class SplashActivity : AppCompatActivity() {

    private var routed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val overlay = findViewById<View>(R.id.splashRoot)
        overlay.startAnimation(AnimationUtils.loadAnimation(this, R.anim.splash_fade_in))

        val video = findViewById<VideoView>(R.id.introVideo)
        try {
            val cached = copyAssetToCache("media/launcher.mp4", "launcher.mp4")
            video.setVideoURI(Uri.fromFile(cached))
            video.setOnCompletionListener { routeToHome() }
            video.setOnErrorListener { _, _, _ ->
                overlay.postDelayed({ routeToHome() }, 3500)
                true
            }
            video.start()
        } catch (e: Exception) {
            overlay.postDelayed({ routeToHome() }, 3500)
        }

        overlay.setOnClickListener { routeToHome() }
    }

    private fun copyAssetToCache(assetPath: String, destName: String): File {
        val out = File(cacheDir, destName)
        if (!out.exists() || out.length() == 0L) {
            assets.open(assetPath).use { input ->
                FileOutputStream(out).use { fos -> input.copyTo(fos) }
            }
        }
        return out
    }

    private fun routeToHome() {
        if (routed) return
        routed = true
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
