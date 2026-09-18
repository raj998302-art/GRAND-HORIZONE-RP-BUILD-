package com.grandhorizonrp.launcher.data

import com.google.gson.annotations.SerializedName

/**
 * Update manifest schema for Grand Horizon RP client data delivery.
 *
 * The launcher fetches this JSON, compares each file's sha256 against the
 * locally-installed copy, and downloads only missing/updated files.
 *
 * Schema matches `deployment/update-manifest/manifest.schema.json`.
 */
data class UpdateManifest(
    @SerializedName("project") val project: String,
    @SerializedName("clientVersion") val clientVersion: String,
    @SerializedName("minimumLauncherVersion") val minimumLauncherVersion: String,
    @SerializedName("files") val files: List<ManifestFile>
)

data class ManifestFile(
    @SerializedName("path") val path: String,        // relative path under client data dir
    @SerializedName("size") val size: Long,          // bytes
    @SerializedName("sha256") val sha256: String,    // expected checksum
    @SerializedName("version") val version: String,  // content version tag
    @SerializedName("required") val required: Boolean,
    @SerializedName("url") val url: String           // where to download this file
)
