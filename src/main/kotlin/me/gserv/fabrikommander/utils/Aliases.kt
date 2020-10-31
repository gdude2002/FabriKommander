package me.gserv.fabrikommander.utils

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource

typealias Dispatcher = CommandDispatcher<ServerCommandSource>
typealias Context = CommandContext<ServerCommandSource>
