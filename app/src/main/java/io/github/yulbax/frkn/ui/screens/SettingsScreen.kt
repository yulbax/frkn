package io.github.yulbax.frkn.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings as AndroidSettings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.yulbax.frkn.R
import io.github.yulbax.frkn.ui.components.ClickableRow
import io.github.yulbax.frkn.ui.components.CommittedTextField
import io.github.yulbax.frkn.ui.components.DropdownSetting
import io.github.yulbax.frkn.ui.components.GroupCard
import io.github.yulbax.frkn.ui.components.SwitchRow
import io.github.yulbax.frkn.ui.components.transparentFieldColors
import io.github.yulbax.frkn.ui.viewmodel.SettingsViewModel
import io.github.yulbax.frkn.util.Diagnostics
import org.koin.androidx.compose.koinViewModel

@Composable
fun Settings(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val showSystemApps by viewModel.showSystemApps.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        GroupCard(
            title = stringResource(R.string.language),
            items = listOf { LanguagePicker() }
        )

        GroupCard(
            title = stringResource(R.string.applications),
            items = listOf {
                SwitchRow(stringResource(R.string.show_system_apps), showSystemApps) {
                    viewModel.toggleShowSystemApps()
                }
            }
        )

        ByeDpiSection(viewModel)
        AutostartSection(viewModel)
        NetworkSection(viewModel)
        BackupSection(viewModel)
    }
}

private val LANGUAGE_TAGS = listOf("en", "ru", "zh", "fa")
private val LANGUAGE_NAMES = mapOf(
    "en" to "English",
    "ru" to "Русский",
    "zh" to "中文",
    "fa" to "فارسی"
)

@Composable
private fun LanguagePicker() {
    val systemLabel = stringResource(R.string.language_system)
    val labels = remember(systemLabel) {
        listOf(systemLabel) + LANGUAGE_TAGS.map { LANGUAGE_NAMES.getValue(it) }
    }
    val currentTag = AppCompatDelegate.getApplicationLocales()[0]?.language ?: ""
    val selected = LANGUAGE_NAMES[currentTag] ?: systemLabel

    DropdownSetting(
        label = stringResource(R.string.language),
        options = labels,
        selected = selected,
        onSelect = { label ->
            val tag = LANGUAGE_NAMES.entries.firstOrNull { it.value == label }?.key
            AppCompatDelegate.setApplicationLocales(
                if (tag == null) LocaleListCompat.getEmptyLocaleList()
                else LocaleListCompat.forLanguageTags(tag)
            )
        }
    )
}

@Composable
private fun ByeDpiSection(viewModel: SettingsViewModel) {
    val savedByeDpiArgs by viewModel.byeDpiArgs.collectAsState()
    var text by remember { mutableStateOf(savedByeDpiArgs) }
    var focused by remember { mutableStateOf(false) }
    LaunchedEffect(savedByeDpiArgs) { if (!focused) text = savedByeDpiArgs }

    GroupCard(
        title = stringResource(R.string.byedpi_cli_args),
        action = {
            Text(
                text = stringResource(R.string.reset_to_default),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    text = ""
                    viewModel.setByeDpiArgs("")
                }
            )
        },
        items = listOf {
            TextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                placeholder = {
                    Text(viewModel.byeDpiArgsDefault, fontFamily = FontFamily.Monospace)
                },
                label = { Text(stringResource(R.string.flags_label)) },
                colors = transparentFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { st ->
                        if (focused && !st.isFocused) viewModel.setByeDpiArgs(text.trim())
                        focused = st.isFocused
                    }
            )
        }
    )
}

@Composable
private fun BackupSection(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val exportAppConfigLabel = stringResource(R.string.export_app_config)
    val importFailedFormat = stringResource(R.string.import_failed)
    val importDoneFormat = stringResource(R.string.import_done)

    fun share(uri: Uri, mimeType: String, chooserTitle: String) {
        runCatching {
            context.startActivity(
                Intent.createChooser(Diagnostics.shareIntent(uri, mimeType), chooserTitle)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.importAppConfig(uri) { result ->
                val message = when {
                    result.error != null -> importFailedFormat.format(result.error)
                    else -> importDoneFormat.format(result.applied, result.skipped)
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    GroupCard(
        title = stringResource(R.string.backup_title),
        items = listOf(
            {
                ClickableRow(
                    label = stringResource(R.string.export_app_config),
                    trailingIcon = Icons.Filled.Upload
                ) {
                    viewModel.exportAppConfig { uri ->
                        share(uri, "application/json", exportAppConfigLabel)
                    }
                }
            },
            {
                ClickableRow(
                    label = stringResource(R.string.import_app_config),
                    trailingIcon = Icons.Filled.Download
                ) {
                    importLauncher.launch(arrayOf("application/json", "text/plain", "*/*"))
                }
            }
        )
    )
}

@Composable
private fun AutostartSection(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val autoConnect by viewModel.autoConnect.collectAsState()

    GroupCard(
        title = stringResource(R.string.autostart_title),
        items = listOf(
            {
                SwitchRow(stringResource(R.string.auto_connect), autoConnect) {
                    viewModel.setAutoConnect(it)
                }
            },
            {
                ClickableRow(label = stringResource(R.string.always_on_vpn)) {
                    runCatching {
                        context.startActivity(
                            Intent(AndroidSettings.ACTION_VPN_SETTINGS)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                }
            }
        )
    )
}

@Composable
private fun NetworkSection(viewModel: SettingsViewModel) {
    val tunStack by viewModel.tunStack.collectAsState()
    val mtu by viewModel.mtu.collectAsState()
    val ipv6Mode by viewModel.ipv6Mode.collectAsState()
    val dnsRemote by viewModel.dnsRemote.collectAsState()
    val dnsDirect by viewModel.dnsDirect.collectAsState()
    val sniff by viewModel.sniff.collectAsState()
    val bypassLan by viewModel.bypassLan.collectAsState()

    GroupCard(
        title = stringResource(R.string.network_title),
        items = listOf(
            {
                DropdownSetting(
                    label = stringResource(R.string.tun_stack),
                    options = SettingsViewModel.TUN_STACKS,
                    selected = tunStack,
                    onSelect = viewModel::setTunStack
                )
            },
            {
                CommittedTextField(
                    label = stringResource(R.string.mtu),
                    saved = mtu.toString(),
                    keyboardType = KeyboardType.Number,
                    onCommit = { it.trim().toIntOrNull()?.coerceIn(1280, 9000)?.let(viewModel::setMtu) }
                )
            },
            {
                DropdownSetting(
                    label = stringResource(R.string.ipv6),
                    options = SettingsViewModel.IPV6_MODES,
                    selected = ipv6Mode,
                    onSelect = viewModel::setIpv6Mode
                )
            },
            {
                CommittedTextField(
                    label = stringResource(R.string.remote_dns),
                    saved = dnsRemote,
                    onCommit = { if (it.isNotBlank()) viewModel.setDnsRemote(it) }
                )
            },
            {
                CommittedTextField(
                    label = stringResource(R.string.direct_dns),
                    saved = dnsDirect,
                    onCommit = { if (it.isNotBlank()) viewModel.setDnsDirect(it) }
                )
            },
            {
                SwitchRow(stringResource(R.string.sniff_destination), sniff) { viewModel.setSniff(it) }
            },
            {
                SwitchRow(stringResource(R.string.bypass_lan), bypassLan) { viewModel.setBypassLan(it) }
            }
        )
    )
}
