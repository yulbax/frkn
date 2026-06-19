package io.github.yulbax.frkn.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import io.github.yulbax.frkn.R
import io.github.yulbax.frkn.util.Diagnostics
import io.github.yulbax.frkn.util.FrknLog
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun Logs() {
    val context = LocalContext.current
    val frknLog = koinInject<FrknLog>()
    val scope = rememberCoroutineScope()
    val shareLabel = stringResource(R.string.export_logs)

    var reloadKey by remember { mutableIntStateOf(0) }
    var text by remember { mutableStateOf("") }
    LaunchedEffect(reloadKey) {
        text = Diagnostics.collectDiagnostics(context, frknLog)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { reloadKey++ }) {
                Icon(Icons.Filled.Refresh, contentDescription = null)
                Text(
                    stringResource(R.string.logs_refresh),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            FilledTonalButton(
                onClick = {
                    scope.launch {
                        val uri = Diagnostics.exportLogs(context, frknLog)
                        runCatching {
                            context.startActivity(
                                Intent.createChooser(
                                    Diagnostics.shareIntent(uri, "text/plain"),
                                    shareLabel
                                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        }
                    }
                }
            ) {
                Icon(Icons.Filled.Share, contentDescription = null)
                Text(stringResource(R.string.share), modifier = Modifier.padding(start = 8.dp))
            }
        }

        Surface(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            SelectionContainer(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp)
            ) {
                Text(
                    text = text.ifBlank { stringResource(R.string.logs_empty) },
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
