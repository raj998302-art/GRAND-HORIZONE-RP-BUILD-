package com.grandhorizonrp.launcher.util

import android.content.Context
import android.os.Environment
import java.io.File

/**
 * Resolves the client data directory in a storage-scoped location compatible
 * with Android 10+ scoped storage. Game content packs are stored here after
 * download + verification.
 */
object FileUtil {

    /**
     * Client data root. Uses app-specific external storage (no MANAGE_EXTERNAL_STORAGE
     * permission needed, survives app updates, cleared on uninstall).
     */
    fun clientDataDir(context: Context): File {
        val base = context.getExternalFilesDir(null)
            ?: File(context.filesDir, "external").apply { mkdirs() }
        return File(base, "grand-horizon-rp/client-data").apply { mkdirs() }
    }

    fun freeBytes(): Long {
        val stat = android.os.StatFs(Environment.getDataDirectory().path)
        return stat.availableBytes
    }

    fun ensureParent(file: File): File {
        file.parentFile?.mkdirs()
        return file
    }
}
