package com.grandhorizonrp.launcher.net

import com.grandhorizonrp.launcher.data.ManifestFile
import com.grandhorizonrp.launcher.util.HashUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

/**
 * Downloads client content files referenced by the manifest and verifies each
 * against its declared SHA-256 before moving it into place. Files that fail
 * verification are deleted and reported as corrupt.
 */
class ContentDownloader {

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .build()

    sealed class Progress {
        data class Start(val file: ManifestFile, val index: Int, val total: Int) : Progress()
        data class Bytes(val downloaded: Long, val total: Long) : Progress()
        data class Verifying(val file: ManifestFile) : Progress()
        data class Done(val file: ManifestFile) : Progress()
        data class Failed(val file: ManifestFile, val reason: String) : Progress()
        data class AllComplete(val ok: Int, val failed: Int) : Progress()
    }

    fun download(files: List<ManifestFile>, destDir: File): Flow<Progress> = flow {
        var ok = 0; var failed = 0
        files.forEachIndexed { i, f ->
            emit(Progress.Start(f, i + 1, files.size))
            val dest = File(destDir, f.path).apply { parentFile?.mkdirs() }
            try {
                val tmp = File(dest.absolutePath + ".part")
                val req = Request.Builder().url(f.url).build()
                client.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) throw RuntimeException("HTTP ${resp.code}")
                    val body = resp.body ?: throw RuntimeException("No body")
                    val total = body.contentLength()
                    var read = 0L
                    body.byteStream().use { input ->
                        FileOutputStream(tmp).use { out ->
                            val buf = ByteArray(64 * 1024)
                            while (true) {
                                val n = input.read(buf)
                                if (n <= 0) break
                                out.write(buf, 0, n)
                                read += n
                                emit(Progress.Bytes(read, if (total > 0) total else f.size))
                            }
                        }
                    }
                }
                emit(Progress.Verifying(f))
                val actual = tmp.inputStream().use { HashUtil.sha256(it) }
                if (!actual.equals(f.sha256, ignoreCase = true)) {
                    tmp.delete()
                    throw RuntimeException("SHA-256 mismatch: expected ${f.sha256}, got $actual")
                }
                if (dest.exists()) dest.delete()
                if (!tmp.renameTo(dest)) throw RuntimeException("Could not move file into place")
                emit(Progress.Done(f)); ok++
            } catch (e: Exception) {
                emit(Progress.Failed(f, e.message ?: "unknown error")); failed++
            }
        }
        emit(Progress.AllComplete(ok, failed))
    }.flowOn(Dispatchers.IO)
}
