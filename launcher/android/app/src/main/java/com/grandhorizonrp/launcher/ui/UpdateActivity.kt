package com.grandhorizonrp.launcher.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.grandhorizonrp.launcher.LauncherApp
import com.grandhorizonrp.launcher.R
import com.grandhorizonrp.launcher.net.ContentDownloader
import com.grandhorizonrp.launcher.net.ManifestDownloader
import com.grandhorizonrp.launcher.util.FileUtil
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Update / download screen.
 *
 * Flow: fetch manifest -> compare local files -> download missing/updated ->
 * SHA-256 verify -> install. Shows per-file progress and clear error states.
 */
class UpdateActivity : AppCompatActivity() {

    private lateinit var phaseText: TextView
    private lateinit var detailText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var actionBtn: Button
    private val downloader = ContentDownloader()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        phaseText = findViewById(R.id.phaseText)
        detailText = findViewById(R.id.detailText)
        progressBar = findViewById(R.id.progressBar)
        actionBtn = findViewById(R.id.actionBtn)
        actionBtn.setOnClickListener { runUpdate() }
        runUpdate()
    }

    private fun runUpdate() {
        val app = application as LauncherApp
        val cfg = app.serverConfig
        progressBar.progress = 0
        actionBtn.visibility = View.GONE
        lifecycleScope.launch {
            try {
                phaseText.text = getString(R.string.phase_fetch_manifest)
                val manifest = ManifestDownloader().fetch(cfg.manifestUrl)
                detailText.text = getString(R.string.detail_files_count, manifest.files.size)
                val destDir = FileUtil.clientDataDir(this@UpdateActivity)

                downloader.download(manifest.files, destDir).collect { p ->
                    when (p) {
                        is ContentDownloader.Progress.Start -> {
                            phaseText.text = getString(R.string.phase_downloading)
                            detailText.text = getString(
                                R.string.detail_file_of, p.index, p.total, p.file.path
                            )
                            progressBar.max = 100
                        }
                        is ContentDownloader.Progress.Bytes -> {
                            val pct = if (p.total > 0) (p.downloaded * 100 / p.total).toInt() else 0
                            progressBar.progress = pct
                        }
                        is ContentDownloader.Progress.Verifying -> {
                            phaseText.text = getString(R.string.phase_verifying)
                            detailText.text = p.file.path
                        }
                        is ContentDownloader.Progress.Done -> {
                            progressBar.progress = 100
                        }
                        is ContentDownloader.Progress.Failed -> {
                            phaseText.text = getString(R.string.phase_failed)
                            detailText.text = getString(R.string.detail_failed, p.file.path, p.reason)
                            actionBtn.text = getString(R.string.action_retry)
                            actionBtn.visibility = View.VISIBLE
                        }
                        is ContentDownloader.Progress.AllComplete -> {
                            phaseText.text = getString(R.string.phase_ready)
                            detailText.text = getString(R.string.detail_complete, p.ok, p.failed)
                            progressBar.progress = 100
                            if (p.failed == 0) actionBtn.text = getString(R.string.action_play)
                            else actionBtn.text = getString(R.string.action_retry)
                            actionBtn.visibility = View.VISIBLE
                        }
                    }
                }
            } catch (e: Exception) {
                phaseText.text = getString(R.string.phase_error)
                detailText.text = e.message ?: getString(R.string.err_generic)
                actionBtn.text = getString(R.string.action_retry)
                actionBtn.visibility = View.VISIBLE
            }
        }
    }
}
