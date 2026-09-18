package com.grandhorizonrp.launcher

import android.app.Application
import com.google.gson.Gson
import com.grandhorizonrp.launcher.data.ServerConfig
import java.io.InputStreamReader

/**
 * Grand Horizon RP launcher application.
 *
 * Loads the central server connection configuration from
 * `assets/config/launcher_config.json` so the server endpoint is NOT
 * hardcoded across the codebase. This is the single source of truth.
 */
class LauncherApp : Application() {

    lateinit var serverConfig: ServerConfig
        private set

    val gson: Gson = Gson()

    override fun onCreate() {
        super.onCreate()
        serverConfig = loadServerConfig()
    }

    private fun loadServerConfig(): ServerConfig {
        return try {
            assets.open("config/launcher_config.json").use { stream ->
                gson.fromJson(InputStreamReader(stream), ServerConfig::class.java)
            } ?: ServerConfig.DEFAULT
        } catch (e: Exception) {
            // Fall back to bundled defaults if config is missing/corrupt.
            ServerConfig.DEFAULT
        }
    }
}
