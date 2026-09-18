package com.grandhorizonrp.launcher.util

import java.io.InputStream
import java.security.MessageDigest

object HashUtil {
    fun sha256(stream: InputStream): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val buf = ByteArray(64 * 1024)
        while (true) {
            val n = stream.read(buf)
            if (n <= 0) break
            digest.update(buf, 0, n)
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    fun sha256(text: String): String =
        sha256(text.byteInputStream(Charsets.UTF_8))
}
