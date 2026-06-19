package io.github.yulbax.frkn.ui.screens

import android.content.Context
import android.graphics.Canvas
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedToggleButton
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import io.github.yulbax.frkn.R
import io.github.yulbax.frkn.data.ConnectionType
import io.github.yulbax.frkn.ui.components.ConnectionTypeIcon
import io.github.yulbax.frkn.ui.viewmodel.AppInfo
import io.github.yulbax.frkn.ui.viewmodel.AppsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel

private val TYPE_ORDER = listOf(ConnectionType.DIRECT, ConnectionType.BYEDPI, ConnectionType.VPN)
private val SELECTOR_HEIGHT = 48.dp
private val APP_SELECTOR_HEIGHT = 36.dp
private val APP_SELECTOR_WIDTH = 150.dp
private val SELECTOR_ICON_WIDTH = 48.dp

@Composable
fun Apps(
    query: String,
    viewModel: AppsViewModel = koinViewModel()
) {
    val apps by viewModel.apps.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    when {
        isLoading -> LoadingContent()
        error != null -> ErrorContent(error = error ?: "", onRetry = viewModel::retry)
        else -> {
            val filtered = remember(apps, query) {
                if (query.isBlank()) apps
                else apps.filter { app ->
                    app.name.contains(query, ignoreCase = true) ||
                            app.packageName.contains(query, ignoreCase = true)
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    val snackbarPattern = stringResource(R.string.set_apps_snackbar)
                    val undoLabel = stringResource(R.string.undo)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.apply_to_all),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        GlobalTypeConnectedGroup(
                            modifier = Modifier.weight(1f),
                            onSelect = { type ->
                                val snackMsg = snackbarPattern.format(filtered.size, type.label)
                                viewModel.setAllConnectionTypes(filtered, type)
                                scope.launch {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    val result = snackbarHostState.showSnackbar(
                                        message = snackMsg,
                                        actionLabel = undoLabel,
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.restoreConnectionTypes(filtered)
                                    }
                                }
                            }
                        )
                    }
                    Box(modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()) {
                        when {
                            query.isNotBlank() && filtered.isEmpty() -> NoResultsContent(query = query)
                            filtered.isEmpty() -> EmptyContent()
                            else -> AppsList(
                                apps = filtered,
                                onConnectionTypeChange = viewModel::setConnectionType
                            )
                        }
                    }
                }
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        actionColor = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun GlobalTypeConnectedGroup(
    onSelect: (ConnectionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(SELECTOR_HEIGHT),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        TYPE_ORDER.forEachIndexed { index, type ->
            ElevatedToggleButton(
                checked = false,
                onCheckedChange = { onSelect(type) },
                shapes = shapes(index),
                colors = ToggleButtonDefaults.toggleButtonColors(
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface
                ),
                contentPadding = PaddingValues(horizontal = 4.dp),
                modifier = Modifier
                    .weight(1f)
            ) {
                ConnectionTypeIcon(type, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    text = type.label,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AppRowConnectedGroup(
    selected: ConnectionType?,
    onSelect: (ConnectionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(APP_SELECTOR_HEIGHT)
            .width(APP_SELECTOR_WIDTH),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        TYPE_ORDER.forEachIndexed { index, type ->
            val isSelected = type == selected

            ToggleButton(
                checked = isSelected,
                onCheckedChange = { if (it) onSelect(type) },
                shapes = shapes(index),
                modifier = Modifier
                    .then(if (isSelected) Modifier.weight(1f) else Modifier.width(SELECTOR_ICON_WIDTH))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    ConnectionTypeIcon(type, Modifier.size(16.dp))
                }
            }
        }
    }
}


@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.loading_apps),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.error_loading_apps),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Button(onClick = onRetry) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}

@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.no_apps_found),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun NoResultsContent(query: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.no_apps_matching, query),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AppsList(
    apps: List<AppInfo>,
    onConnectionTypeChange: (String, String, Boolean, ConnectionType) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(
            count = apps.size,
            key = { apps[it].packageName },
            contentType = { "app_row" }
        ) { index ->
            val app = apps[index]
            AppRow(
                app = app,
                onConnectionTypeChange = { type ->
                    onConnectionTypeChange(app.packageName, app.name, app.isSystemApp, type)
                }
            )
            if (index < apps.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
private fun AppRow(
    app: AppInfo,
    onConnectionTypeChange: (ConnectionType) -> Unit,
    modifier: Modifier = Modifier
) {
    val imageBitmap = rememberAppIcon(app.packageName)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
        ) {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = app.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = app.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = app.packageName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        AppRowConnectedGroup(
            selected = app.connectionType,
            onSelect = onConnectionTypeChange
        )
    }
}

@Composable
private fun rememberAppIcon(packageName: String): ImageBitmap? {
    val context = LocalContext.current
    return produceState(initialValue = AppIconCache.get(packageName), key1 = packageName) {
        if (value == null) {
            value = withContext(Dispatchers.IO) { AppIconCache.load(context, packageName) }
        }
    }.value
}

private object AppIconCache {
    private const val SIZE_PX = 128
    private const val BYTES_PER_ICON = SIZE_PX * SIZE_PX * 4

    private val cache = object : LruCache<String, ImageBitmap>(
        (Runtime.getRuntime().maxMemory() / 8).toInt().coerceAtLeast(4 * 1024 * 1024)
    ) {
        override fun sizeOf(key: String, value: ImageBitmap): Int = BYTES_PER_ICON
    }

    fun get(packageName: String): ImageBitmap? = cache.get(packageName)

    fun load(context: Context, packageName: String): ImageBitmap? {
        cache.get(packageName)?.let { return it }
        return runCatching {
            val drawable = context.packageManager.getApplicationIcon(packageName)
            val bitmap = createBitmap(SIZE_PX, SIZE_PX)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, SIZE_PX, SIZE_PX)
            drawable.draw(canvas)
            bitmap.asImageBitmap()
        }.getOrNull()?.also { cache.put(packageName, it) }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun shapes(index: Int): ToggleButtonShapes = when (index) {
    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
    TYPE_ORDER.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
}