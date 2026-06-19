package io.github.yulbax.frkn.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apps")
data class App(
    @PrimaryKey val packageName: String,
    val name: String,
    val isSystemApp: Boolean,
    val connectionType: ConnectionType = ConnectionType.VPN
)

enum class ConnectionType(val label: String) {
    DIRECT("Direct"), BYEDPI("ByeDPI"), VPN("VPN")
}
