package io.github.yulbax.frkn.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.yulbax.frkn.data.AppDao
import io.github.yulbax.frkn.data.SettingsDao
import io.github.yulbax.frkn.data.SettingsEntity
import io.github.yulbax.frkn.engine.ByeDpi
import io.github.yulbax.frkn.util.Diagnostics
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val application: Application,
    private val settingsDao: SettingsDao,
    private val appDao: AppDao
) : ViewModel() {

    private val settings: StateFlow<SettingsEntity> = settingsDao.observeSettings()
        .map { it ?: SettingsEntity() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsEntity())

    val showSystemApps: StateFlow<Boolean> = settings
        .map { it.showSystemApps }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val byeDpiArgs: StateFlow<String> = settings
        .map { it.byeDpiArgs }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val byeDpiArgsDefault: String = ByeDpi.DEFAULT_DESYNC_ARGS.joinToString(" ")

    private val defaults = SettingsEntity()

    val tunStack: StateFlow<String> = field { it.tunStack }
    val mtu: StateFlow<Int> = field { it.mtu }
    val ipv6Mode: StateFlow<String> = field { it.ipv6Mode }
    val dnsRemote: StateFlow<String> = field { it.dnsRemote }
    val dnsDirect: StateFlow<String> = field { it.dnsDirect }
    val sniff: StateFlow<Boolean> = field { it.sniff }
    val bypassLan: StateFlow<Boolean> = field { it.bypassLan }
    val autoConnect: StateFlow<Boolean> = field { it.autoConnect }

    fun toggleShowSystemApps() = update { it.copy(showSystemApps = !it.showSystemApps) }
    fun setByeDpiArgs(args: String) = update { it.copy(byeDpiArgs = args) }

    fun setTunStack(value: String) = update { it.copy(tunStack = value) }
    fun setMtu(value: Int) = update { it.copy(mtu = value) }
    fun setIpv6Mode(value: String) = update { it.copy(ipv6Mode = value) }
    fun setDnsRemote(value: String) = update { it.copy(dnsRemote = value.trim()) }
    fun setDnsDirect(value: String) = update { it.copy(dnsDirect = value.trim()) }
    fun setSniff(value: Boolean) = update { it.copy(sniff = value) }
    fun setBypassLan(value: Boolean) = update { it.copy(bypassLan = value) }
    fun setAutoConnect(value: Boolean) = update { it.copy(autoConnect = value) }
    fun exportAppConfig(onReady: (Uri) -> Unit) {
        viewModelScope.launch {
            runCatching { Diagnostics.exportAppConfig(application, appDao) }.onSuccess(onReady)
        }
    }

    fun importAppConfig(uri: Uri, onResult: (Diagnostics.ImportResult) -> Unit) {
        viewModelScope.launch {
            val result = Diagnostics.importAppConfig(application, appDao, uri)
            onResult(result)
        }
    }

    private fun <T> field(selector: (SettingsEntity) -> T): StateFlow<T> = settings
        .map(selector)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), selector(defaults))

    companion object {
        val TUN_STACKS = listOf("gvisor", "system", "mixed")
        val IPV6_MODES = listOf("disable", "enable", "prefer", "only")
    }

    private fun update(transform: (SettingsEntity) -> SettingsEntity) {
        viewModelScope.launch {
            val current = settingsDao.observeSettings().first() ?: SettingsEntity()
            settingsDao.upsertSettings(transform(current))
        }
    }
}
