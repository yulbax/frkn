package io.github.yulbax.frkn.vpn

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/** Operations sent to the engine consumer in [FrknVpnService]. */
sealed interface Command {
    data object Start : Command
    data object Reload : Command
    data object Recover : Command
    data object CheckByeDpi : Command
    data object Stop : Command
}

/**
 * Process-wide command bus from the UI (and the service's own watchers) to the running
 * [FrknVpnService], which is the single consumer. Producers just call the typed methods — no
 * Intents, no reference to the Service instance. The UNLIMITED channel buffers across the brief
 * gap while the service starts consuming; any command that arrives while it isn't running is
 * absorbed by the service's lifecycle guards.
 */
class VpnCommandBus {
    private val channel = Channel<Command>(Channel.UNLIMITED)
    val commands: Flow<Command> = channel.receiveAsFlow()

    fun start() { channel.trySend(Command.Start) }
    fun reload() { channel.trySend(Command.Reload) }
    fun recover() { channel.trySend(Command.Recover) }
    fun checkByeDpi() { channel.trySend(Command.CheckByeDpi) }
    fun stop() { channel.trySend(Command.Stop) }
}
