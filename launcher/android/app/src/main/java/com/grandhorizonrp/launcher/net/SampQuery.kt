package com.grandhorizonrp.launcher.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Minimal SA-MP server query client.
 *
 * The SA-MP query port is the configured server port + 123. The launcher
 * sends the "i" (info) opcode and parses the response to determine online
 * status, hostname, gamemode and player count.
 *
 * Reference: SA-MP query protocol. Only the info ("i") packet is implemented
 * because that is all the launcher needs for a status indicator.
 */
class SampQuery(private val host: String, private val port: Int) {

    sealed class Result {
        data class Online(
            val hostname: String,
            val gamemode: String,
            val mapName: String,
            val players: Int,
            val maxPlayers: Int
        ) : Result()
        object Offline : Result()
    }

    suspend fun queryInfo(timeoutMs: Int = 3000): Result = withContext(Dispatchers.IO) {
        val queryPort = port + 123
        DatagramSocket().use { socket ->
            socket.soTimeout = timeoutMs
            val addr = InetAddress.getByName(host)

            // Request packet: "SAMP" + ip(4 bytes) + port(2 LE) + 'i'
            val ipParts = host.split(".").map { it.toInt().toByte() }
            val req = ByteBuffer.allocate(15).order(ByteOrder.LITTLE_ENDIAN)
            req.put("SAMP".toByteArray(Charsets.US_ASCII))
            if (ipParts.size == 4) req.put(ipParts.toByteArray()) else req.put(ByteArray(4))
            req.putShort(port.toShort())
            req.put('i'.code.toByte())
            socket.send(DatagramPacket(req.array(), req.array().size, addr, queryPort))

            val buf = ByteArray(1024)
            val resp = DatagramPacket(buf, buf.size)
            socket.receive(resp)
            parseInfo(resp.data, resp.length)
        }
    }

    private fun parseInfo(data: ByteArray, len: Int): Result {
        return try {
            val bb = ByteBuffer.wrap(data, 0, len).order(ByteOrder.LITTLE_ENDIAN)
            // Skip 11-byte header (SAMP + ip + port + 'i')
            bb.position(11)
            bb.get() // password flag
            bb.getShort() // players
            bb.getShort() // max players
            val hostLen = bb.getInt().toInt()
            val hostname = String(data, bb.position(), hostLen, Charsets.UTF_8)
            bb.position(bb.position() + hostLen)
            val gmLen = bb.getInt().toInt()
            val gamemode = String(data, bb.position(), gmLen, Charsets.UTF_8)
            bb.position(bb.position() + gmLen)
            val mapLen = bb.getInt().toInt()
            val mapName = String(data, bb.position(), mapLen, Charsets.UTF_8)

            val players = bb.getShort(11 + 1).toInt() and 0xFFFF
            val maxPlayers = bb.getShort(11 + 1 + 2).toInt() and 0xFFFF
            Result.Online(hostname, gamemode, mapName, players, maxPlayers)
        } catch (e: Exception) {
            Result.Offline
        }
    }
}
