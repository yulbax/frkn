package io.github.yulbax.frkn.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import io.github.yulbax.frkn.R
import io.github.yulbax.frkn.data.ConnectionType

@Composable
fun ConnectionTypeIcon(type: ConnectionType, modifier: Modifier = Modifier) {
    when (type) {
        ConnectionType.DIRECT ->
            Icon(Icons.Filled.ArrowUpward, contentDescription = type.label, modifier = modifier)
        ConnectionType.VPN ->
            Icon(Icons.Filled.VpnKey, contentDescription = type.label, modifier = modifier)
        ConnectionType.BYEDPI ->
            Icon(painterResource(R.drawable.ic_byedpi), contentDescription = type.label, modifier = modifier)
    }
}
