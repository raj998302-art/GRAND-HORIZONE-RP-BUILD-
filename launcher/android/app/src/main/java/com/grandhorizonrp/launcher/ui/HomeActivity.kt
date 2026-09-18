package com.grandhorizonrp.launcher.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.grandhorizonrp.launcher.LauncherApp
import com.grandhorizonrp.launcher.R
import com.grandhorizonrp.launcher.net.SampQuery
import kotlinx.coroutines.launch

/**
 * Home screen of the Grand Horizon RP launcher.
 *
 * Buttons: PLAY, NEWS/UPDATES, SERVER STATUS, DOWNLOAD/UPDATE, SETTINGS, ABOUT.
 * Server status is queried live via the SA-MP info opcode (port+123) and shown
 * next to the PLAY button.
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var playersText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val cfg = (application as LauncherApp).serverConfig
        findViewById<TextView>(R.id.serverName).text = cfg.project
        findViewById<TextView>(R.id.serverEndpoint).text = "${cfg.serverHost}:${cfg.serverPort}"
        statusText = findViewById(R.id.statusText)
        playersText = findViewById(R.id.playersText)

        findViewById<Button>(R.id.btnPlay).setOnClickListener {
            // Launching the actual game client is a documented dependency on
            // the supplied native client integration. See docs/CLIENT.md.
            startActivity(Intent(this, UpdateActivity::class.java))
        }
        findViewById<Button>(R.id.btnNews).setOnClickListener {
            val i = Intent(this, AboutActivity::class.java)
            i.putExtra("tab", "news"); startActivity(i)
        }
        findViewById<Button>(R.id.btnServerStatus).setOnClickListener { refreshStatus() }
        findViewById<Button>(R.id.btnUpdate).setOnClickListener {
            startActivity(Intent(this, UpdateActivity::class.java))
        }
        findViewById<Button>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        findViewById<Button>(R.id.btnAbout).setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

        refreshStatus()
    }

    private fun refreshStatus() {
        val cfg = (application as LauncherApp).serverConfig
        statusText.text = getString(R.string.status_checking)
        playersText.visibility = View.GONE
        lifecycleScope.launch {
            val result = SampQuery(cfg.serverHost, cfg.serverPort).queryInfo()
            when (result) {
                is SampQuery.Result.Online -> {
                    statusText.text = getString(R.string.status_online)
                    playersText.text = getString(R.string.players_format, result.players, result.maxPlayers)
                    playersText.visibility = View.VISIBLE
                }
                SampQuery.Result.Offline -> {
                    statusText.text = getString(R.string.status_offline)
                    playersText.visibility = View.GONE
                }
            }
        }
    }
}
