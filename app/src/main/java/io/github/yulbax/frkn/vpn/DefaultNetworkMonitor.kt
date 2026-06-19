package io.github.yulbax.frkn.vpn

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import io.github.yulbax.frkn.vpn.core.DefaultInterfaceListener
import java.net.NetworkInterface

class DefaultNetworkMonitor(context: Context) {

    private val connectivity: ConnectivityManager =
        context.getSystemService(ConnectivityManager::class.java)
    private var listener: DefaultInterfaceListener? = null

    @Volatile
    private var underlyingNetwork: Network? = null
    private var registered = false

    private val handlerThread = HandlerThread("FrknNetMonitor").apply { start() }
    private val handler = Handler(handlerThread.looper)

    private val request = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
        .build()

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            underlyingNetwork = network
            pushUpdate()
        }

        override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
            underlyingNetwork = network
            pushUpdate()
        }

        override fun onLost(network: Network) {
            if (network == underlyingNetwork) underlyingNetwork = null
            pushUpdate()
        }
    }

    fun currentNetwork(): Network? = underlyingNetwork

    fun start() {
        if (registered) return
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
                connectivity.registerBestMatchingNetworkCallback(request, callback, handler)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ->
                connectivity.requestNetwork(request, callback, handler)
            else ->
                connectivity.registerDefaultNetworkCallback(callback)
        }
        registered = true
        underlyingNetwork = connectivity.activeNetwork
    }

    fun stop() {
        if (registered) {
            runCatching { connectivity.unregisterNetworkCallback(callback) }
            registered = false
        }
        underlyingNetwork = null
    }

    fun setListener(newListener: DefaultInterfaceListener?) {
        listener = newListener
        if (newListener != null) pushUpdate()
    }

    private fun pushUpdate() {
        val l = listener ?: return
        val network = underlyingNetwork
        if (network == null) {
            l.onDefaultInterfaceChanged("", -1)
            return
        }

        repeat(10) {
            val name = connectivity.getLinkProperties(network)?.interfaceName
            if (name == null) {
                Thread.sleep(100)
                return@repeat
            }
            val index = runCatching { NetworkInterface.getByName(name)?.index ?: -1 }.getOrDefault(-1)
            if (index <= 0) {
                Thread.sleep(100)
                return@repeat
            }
            l.onDefaultInterfaceChanged(name, index)
            return
        }
        l.onDefaultInterfaceChanged("", -1)
    }
}
