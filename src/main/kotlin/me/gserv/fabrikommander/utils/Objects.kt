package me.gserv.fabrikommander.utils

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.Identifier

fun identifierToWorldName(id: Identifier) = when (id.toString()) {
    "minecraft:overworld" -> "Overworld"
    "minecraft:the_nether" -> "The Nether"
    "minecraft:the_end" -> "The End"

    else -> {
        val mod = FabricLoader.getInstance().getModContainer(id.namespace)

        if (mod.isEmpty) {
            id.toString()
        } else {
            val modName = mod.get().metadata.name

            "A \"$modName\" World"
        }
    }
}
