package me.gserv.fabrikommander.commands

import me.gserv.fabrikommander.data.PlayerDataManager
import me.gserv.fabrikommander.utils.*
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import org.apache.logging.log4j.core.jmx.Server

class HomesCommand(val dispatcher: Dispatcher) {
    fun register() {
        dispatcher.register(
            CommandManager.literal("homes")
                .executes { homesCommand(it, it.source.player) }
                .then(
                    CommandManager.argument("player", EntityArgumentType.player())
                        .requires { it.hasPermissionLevel(2) }
                        .executes { homesCommand(it, EntityArgumentType.getPlayer(it, "player")) }
                        .suggests { context, builder ->
                            context.source.minecraftServer.playerNames.forEach(builder::suggest)

                            builder.buildFuture()
                        }
                )
        )
    }

    fun homesCommand(context: Context, player: ServerPlayerEntity): Int {
        val homes = PlayerDataManager.getHomes(player.uuid)

        if (homes == null || homes.isEmpty()) {
            context.source.sendFeedback(
                red("No homes found."),
                false
            )
        } else {
            var text = green("Homes: ")

            for (element in homes.withIndex()) {
                val world = player.server.getWorld(RegistryKey.of(Registry.DIMENSION, element.value.world))

                if (world == null) {
                    text += hover(
                        red(element.value.name),

                        HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            red("This home is in a world ") +
                                    yellow("(") +
                                    aqua(identifierToWorldName(element.value.world)) +
                                    yellow(") ") +
                                    red("that no longer exists.")
                        )
                    )
                } else {
                    text += click(
                        if (element.value.world == player.serverWorld.registryKey.value) {
                            hover(
                                aqua(element.value.name),
                                HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT,
                                    white("This home is in: ") +
                                            aqua(identifierToWorldName(element.value.world)) +
                                            white(".\n") +
                                            yellow("Click to teleport!")
                                )
                            )
                        } else {
                            hover(
                                darkAqua(element.value.name),
                                HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT,
                                    white("This home is in: ") +
                                            darkAqua(identifierToWorldName(element.value.world)) +
                                            white(".\n") +
                                            yellow("Click to teleport!")
                                )
                            )
                        },

                        if (context.source.entity is ServerPlayerEntity && player.uuid == context.source.player.uuid) {
                            ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home ${element.value.name}")
                        } else {
                            ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "Not implemented for other players yet.")
                        }
                    )
                }

                if (element.index < homes.size - 1) text += yellow(", ")
            }

            context.source.sendFeedback(text, false)
        }

        return 1
    }
}
