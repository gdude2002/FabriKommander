package me.gserv.fabrikommander.commands

import com.mojang.brigadier.arguments.StringArgumentType
import me.gserv.fabrikommander.data.PlayerDataManager
import me.gserv.fabrikommander.data.spec.Home
import me.gserv.fabrikommander.utils.*
import net.minecraft.server.command.CommandManager
import net.minecraft.text.LiteralText

class SetHomeCommand(val dispatcher: Dispatcher) {
    fun register() {
        dispatcher.register(
            CommandManager.literal("sethome")
                .executes { setHomeCommand(it) }
                .then(
                    CommandManager.argument("name", StringArgumentType.word())
                        .executes { setHomeCommand(it, StringArgumentType.getString(it, "name")) }
                )
        )
    }

    fun setHomeCommand(context: Context, name: String = "home"): Int {
        val player = context.source.player

        val home = Home(
            name = name,
            world = player.world.registryKey.value,

            x = player.x,
            y = player.y,
            z = player.z,

            pitch = player.pitch,
            yaw = player.yaw
        )

        PlayerDataManager.setHome(player.uuid, home)

        context.source.sendFeedback(
            green("Home created: ") + aqua(name),
            true
        )

        return 1
    }
}
