package io.github.yulbax.frkn.ui.screens.frkn

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.yulbax.frkn.R
import io.github.yulbax.frkn.data.profile.ProfileEntity
import io.github.yulbax.frkn.ui.components.AppDialog
import io.github.yulbax.frkn.ui.components.GroupCard
import io.github.yulbax.frkn.ui.components.transparentFieldColors

@Composable
internal fun EditServerDialog(
    profile: ProfileEntity,
    onDismiss: () -> Unit,
    onSave: (name: String, link: String) -> Unit
) {
    var name by remember { mutableStateOf(profile.name) }
    var link by remember { mutableStateOf(profile.link) }

    AppDialog(
        title = stringResource(R.string.edit_server),
        onDismiss = onDismiss,
        buttons = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
            TextButton(onClick = { onSave(name, link) }) { Text(stringResource(R.string.save)) }
        }
    ) {
        GroupCard(
            itemColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            items = listOf(
                {
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(stringResource(R.string.name_label)) },
                        singleLine = true,
                        colors = transparentFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                {
                    TextField(
                        value = link,
                        onValueChange = { link = it },
                        label = { Text(stringResource(R.string.share_link_label)) },
                        colors = transparentFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        )
    }
}

@Composable
internal fun AddServerDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    AppDialog(
        title = stringResource(R.string.add_server),
        onDismiss = onDismiss,
        buttons = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
            TextButton(
                onClick = { if (text.isNotBlank()) onAdd(text) },
                enabled = text.isNotBlank()
            ) { Text(stringResource(R.string.add)) }
        }
    ) {
        Text(
            text = stringResource(R.string.add_server_instructions),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        GroupCard(
            itemColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            items = listOf {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(stringResource(R.string.link_or_subscription_url)) },
                    colors = transparentFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }
}
