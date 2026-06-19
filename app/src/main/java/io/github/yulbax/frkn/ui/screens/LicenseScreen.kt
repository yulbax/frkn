package io.github.yulbax.frkn.ui.screens

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import io.github.yulbax.frkn.R

@Composable
fun License() {
    val context = LocalContext.current
    val gpl = remember { readRaw(context, R.raw.gpl_3_0) }
    val ofl = remember { readRaw(context, R.raw.google_sans_ofl) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = stringResource(R.string.license_intro),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LicenseSection(
            title = stringResource(R.string.license_app_title),
            text = gpl
        )

        LicenseSection(
            title = stringResource(R.string.license_font_title),
            notice = stringResource(R.string.license_font_notice),
            text = ofl
        )
    }
}

@Composable
private fun LicenseSection(title: String, text: String, notice: String? = null) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp)
    )
    if (notice != null) {
        Text(
            text = notice,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )
    }
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        SelectionContainer(modifier = Modifier.padding(12.dp)) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun readRaw(context: android.content.Context, @RawRes id: Int): String =
    runCatching {
        context.resources.openRawResource(id).bufferedReader().use { it.readText() }
    }.getOrDefault("")
