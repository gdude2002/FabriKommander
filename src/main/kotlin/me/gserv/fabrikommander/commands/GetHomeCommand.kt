package me.gserv.fabrikommander.commands

import com.mojang.brigadier.arguments.StringArgumentType
import me.gserv.fabrikommander.data.PlayerDataManager
import me.gserv.fabrikommander.utils.*
import net.minecraft.server.command.CommandManager

class GetHomeCommand(val dispatcher: Dispatcher) {
    fun register() {
        dispatcher.register(
            CommandManager.literal("gethome")
                .executes { getHome(it) }
                .then(
                    CommandManager.argument("name", StringArgumentType.word())
                        .executes { getHome(it, StringArgumentType.getString(it, "name")) }
                        .suggests { context, builder ->
                            PlayerDataManager.getHomes(context.source.player.uuid)?.forEach {
                                builder.suggest(it.name)
                            }

                            builder.buildFuture()
                        }
                )
        )
    }

    fun getHome(context: Context, name: String = "home"): Int {
        val player = context.source.player
        val home = PlayerDataManager.getHome(player.uuid, name)

        if (home == null) {
            context.source.sendFeedback(
                red("Unknown home: ") + aqua(name),
                false
            )
        } else {
            context.source.sendFeedback(
                yellow("Home ") + aqua(name) + yellow(": [") +
                        green("World: ") + aqua(home.world.toString()) + yellow(", ") +
                        green("X: ") + aqua(home.x.toString()) + yellow(", ") +
                        green("Y: ") + aqua(home.y.toString()) + yellow(", ") +
                        green("Z: ") + aqua(home.z.toString()) + yellow(", ") +
                        green("Pitch: ") + aqua(home.pitch.toString()) + yellow(", ") +
                        green("Yaw: ") + aqua(home.yaw.toString()) + yellow("]"),
                false
            )
        }

        return 1
    }
}
