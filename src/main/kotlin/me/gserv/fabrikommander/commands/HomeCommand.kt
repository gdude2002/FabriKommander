package me.gserv.fabrikommander.commands

import com.mojang.brigadier.arguments.StringArgumentType
import me.gserv.fabrikommander.data.PlayerDataManager
import me.gserv.fabrikommander.utils.*
import net.minecraft.server.command.CommandManager
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey

class HomeCommand(val dispatcher: Dispatcher) {
    fun register() {
        dispatcher.register(
            CommandManager.literal("home")
                .executes { homeCommand(it) }
                .then(
                    CommandManager.argument("name", StringArgumentType.word())
                        .executes { homeCommand(it, StringArgumentType.getString(it, "name")) }
                        .suggests { context, builder ->
                            PlayerDataManager.getHomes(context.source.player.uuid)?.forEach {
                                builder.suggest(it.name)
                            }

                            builder.buildFuture()
                        }
                )
        )
    }

    fun homeCommand(context: Context, name: String = "home"): Int {
        val player = context.source.player
        val home = PlayerDataManager.getHome(player.uuid, name)

        if (home == null) {
            context.source.sendFeedback(
                red("Unknown home: ") + aqua(name),
                false
            )
        } else {
            val world = player.server.getWorld(RegistryKey.of(Registry.DIMENSION, home.world))

            if (world == null) {
                context.source.sendFeedback(
                    red("Home ") +
                            aqua(name) +
                            red(" is in a world ") +
                            yellow("(") +
                            aqua(identifierToWorldName(home.world)) +
                            yellow(") ") +
                            red("that no longer exists."),
                    false
                )
            } else {
                player.teleport(world, home.x, home.y, home.z, home.yaw, home.pitch)

                context.source.sendFeedback(
                    green("Teleported to home: ") + aqua(name),
                    true
                )
            }
        }

        return 1
    }
}
