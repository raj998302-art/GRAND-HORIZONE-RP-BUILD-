package com.grandhorizonrp.launcher.data

import com.google.gson.annotations.SerializedName

/**
 * Central server connection configuration for Grand Horizon RP.
 *
 * Loaded once at app start from `assets/config/launcher_config.json`.
 * Do NOT hardcode host/port in activities — read from [com.grandhorizonrp.launcher.LauncherApp].
 *
 * Database credentials are SERVER-SIDE secrets and must NEVER appear here.
 * The launcher only needs the public SA-MP endpoint + the content manifest URL.
 */
data class ServerConfig(
    @SerializedName("project") val project: String = "Grand Horizon RP",
    @SerializedName("server_host") val serverHost: String = "142.132.203.47",
    @SerializedName("server_port") val serverPort: Int = 14448,
    @SerializedName("manifest_url") val manifestUrl: String = "https://cdn.grandhorizonrp.example/manifest.json",
    @SerializedName("launcher_version") val launcherVersion: String = "1.0.0",
    @SerializedName("website_url") val websiteUrl: String = "https://grandhorizonrp.example",
    @SerializedName("discord_url") val discordUrl: String = ""
) {
    companion object {
        val DEFAULT = ServerConfig()
    }
}
