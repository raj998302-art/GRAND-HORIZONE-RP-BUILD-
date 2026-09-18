package com.grandhorizonrp.launcher.net

import com.google.gson.Gson
import com.grandhorizonrp.launcher.data.UpdateManifest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * Fetches and parses the update manifest JSON from the configured CDN URL.
 */
class ManifestDownloader {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun fetch(manifestUrl: String): UpdateManifest = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(manifestUrl).build()
        client.newCall(request).execute().use { resp ->
            if (!resp.isSuccessful) {
                throw RuntimeException("Manifest HTTP ${resp.code}")
            }
            val body = resp.body?.string()
                ?: throw RuntimeException("Empty manifest response")
            Gson().fromJson(body, UpdateManifest::class.java)
                ?: throw RuntimeException("Malformed manifest JSON")
        }
    }
}
