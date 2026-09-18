package com.grandhorizonrp.launcher.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grandhorizonrp.launcher.LauncherApp
import com.grandhorizonrp.launcher.R

/**
 * Settings screen — shows the bundled, read-only configuration (server endpoint,
 * launcher version, manifest URL). Real per-user settings can be layered on top
 * later via androidx.preference.
 */
class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val cfg = (application as LauncherApp).serverConfig
        findViewById<TextView>(R.id.tvServerHost).text = cfg.serverHost
        findViewById<TextView>(R.id.tvServerPort).text = cfg.serverPort.toString()
        findViewById<TextView>(R.id.tvManifestUrl).text = cfg.manifestUrl
        findViewById<TextView>(R.id.tvLauncherVersion).text = cfg.launcherVersion
        findViewById<TextView>(R.id.tvWebsite).text = cfg.websiteUrl
    }
}
