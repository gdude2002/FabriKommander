package me.gserv.fabrikommander.commands

import me.gserv.fabrikommander.utils.Context
import me.gserv.fabrikommander.utils.Dispatcher
import me.gserv.fabrikommander.utils.green
import net.minecraft.server.command.CommandManager
import net.minecraft.text.LiteralText

class PingCommand(val dispatcher: Dispatcher) {
    fun register() {
        dispatcher.register(
            CommandManager.literal("ping")
                .executes { pingCommand(it) }
        )
    }

    fun pingCommand(context: Context): Int {
        context.source.sendFeedback(
            green("Pong!"),
            false
        )

        return 1
    }
}
