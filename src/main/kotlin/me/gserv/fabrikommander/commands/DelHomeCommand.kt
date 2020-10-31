package me.gserv.fabrikommander.commands

import com.mojang.brigadier.arguments.StringArgumentType
import me.gserv.fabrikommander.data.PlayerDataManager
import me.gserv.fabrikommander.utils.*
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText

class DelHomeCommand(val dispatcher: Dispatcher) {
    fun register() {
        dispatcher.register(
            CommandManager.literal("delhome")
                    // Below left as an example for when we have admin commands
//                .then(
//                    CommandManager.argument("player", EntityArgumentType.player())
//                        .requires { it.hasPermissionLevel(2) }
//                        .suggests { context, builder ->
//                            context.source.minecraftServer.playerNames.forEach(builder::suggest)
//
//                            builder.buildFuture()
//                        }
//                        .then(
//                            CommandManager.argument("name", StringArgumentType.word())
//                                .executes {
//                                    delHomeCommand(
//                                        it,
//                                        StringArgumentType.getString(it, "name"),
//                                        EntityArgumentType.getPlayer(it, "player")
//                                    )
//                                }
//                                .suggests { context, builder ->
//                                    PlayerDataManager.getHomes(EntityArgumentType.getPlayer(context, "player").uuid)
//                                        ?.forEach {
//                                            builder.suggest(it.name)
//                                        }
//
//                                    builder.buildFuture()
//                                }
//                        )
//                )
                .then(
                    CommandManager.argument("name", StringArgumentType.word())
                        .executes {
                            delHomeCommand(
                                it,
                                StringArgumentType.getString(it, "name"),
                                it.source.player
                            )
                        }
                        .suggests { context, builder ->
                            PlayerDataManager.getHomes(context.source.player.uuid)?.forEach {
                                builder.suggest(it.name)
                            }

                            builder.buildFuture()
                        }
                )
        )
    }

    fun delHomeCommand(context: Context, name: String, player: ServerPlayerEntity): Int {
        val home = PlayerDataManager.getHome(player.uuid, name)

        if (home == null) {
            context.source.sendFeedback(
                red("Unknown home: ") + aqua(name),
                false
            )
        } else {
            PlayerDataManager.deleteHome(player.uuid, name)

            context.source.sendFeedback(
                green("Home deleted: ") + aqua(name),
                true
            )
        }

        return 1
    }
}
