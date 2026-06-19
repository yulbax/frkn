package io.github.yulbax.frkn.vpn

import android.annotation.SuppressLint
import android.net.LocalServerSocket
import android.net.LocalSocket
import android.util.Log
import java.io.FileDescriptor
import kotlin.concurrent.thread

class SocketProtector(
    val name: String,
    private val protect: (Int) -> Boolean
) {
    private var serverSocket: LocalServerSocket? = null
    private var worker: Thread? = null
    @Volatile private var running = false

    fun start() {
        if (running) return
        running = true
        serverSocket = LocalServerSocket(name)
        worker = thread(name = "frkn-protect") {
            val server = serverSocket ?: return@thread
            while (running) {
                val socket = try {
                    server.accept()
                } catch (_: Exception) {
                    break
                }
                handle(socket)
            }
        }
    }

    private fun handle(socket: LocalSocket) {
        try {
            socket.inputStream.read()
            val fds = socket.ancillaryFileDescriptors
            val ok = if (!fds.isNullOrEmpty()) {
                val intFd = fdToInt(fds[0])
                val result = intFd >= 0 && protect(intFd)
                fds.forEach { runCatching { closeFd(it) } }
                result
            } else {
                Log.w(TAG, "no ancillary fds received")
                false
            }
            socket.outputStream.write(if (ok) 0 else 1)
            socket.outputStream.flush()
        } catch (e: Exception) {
            Log.w(TAG, "protect handling failed", e)
        } finally {
            runCatching { socket.close() }
        }
    }

    fun stop() {
        running = false
        runCatching { serverSocket?.close() }
        runCatching { worker?.join(1000) }
        serverSocket = null
        worker = null
    }

    @SuppressLint("DiscouragedPrivateApi")
    private fun fdToInt(fd: FileDescriptor): Int = runCatching {
        val field = FileDescriptor::class.java.getDeclaredField("descriptor")
        field.isAccessible = true
        field.getInt(fd)
    }.getOrDefault(-1)

    private fun closeFd(fd: FileDescriptor) {
        android.system.Os.close(fd)
    }

    companion object {
        private const val TAG = "SocketProtector"
    }
}
