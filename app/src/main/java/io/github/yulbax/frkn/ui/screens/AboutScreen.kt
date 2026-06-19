package io.github.yulbax.frkn.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.yulbax.frkn.R
import io.github.yulbax.frkn.engine.BYEDPI_VERSION
import io.github.yulbax.frkn.ui.components.GroupCard
import io.github.yulbax.frkn.ui.components.GroupInfoRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import libbox.Libbox

@Composable
fun About() {
    val context = LocalContext.current
    val unknown = stringResource(R.string.about_unknown)

    val appVersion = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: unknown
    }
    val singboxVersion by produceState(initialValue = "…") {
        value = withContext(Dispatchers.IO) {
            runCatching { Libbox.version() }.getOrDefault(unknown)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        GroupCard(
            title = stringResource(R.string.about_versions),
            items = listOf(
                { GroupInfoRow(stringResource(R.string.about_app), appVersion) },
                { GroupInfoRow(stringResource(R.string.about_singbox), singboxVersion) },
                { GroupInfoRow(stringResource(R.string.about_byedpi), BYEDPI_VERSION) }
            )
        )

        GroupCard(
            title = stringResource(R.string.about_legal),
            items = listOf(
                {
                    GroupInfoRow(
                        stringResource(R.string.about_license_row),
                        stringResource(R.string.about_license_value)
                    )
                },
                {
                    GroupInfoRow(
                        stringResource(R.string.about_font_row),
                        stringResource(R.string.about_font_value)
                    )
                }
            )
        )
    }
}
