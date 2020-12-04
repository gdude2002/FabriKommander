package me.gserv.fabrikommander.data

import com.charleskorn.kaml.Yaml
import me.gserv.fabrikommander.data.spec.Home
import me.gserv.fabrikommander.data.spec.Player
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.WorldSavePath
import org.apache.logging.log4j.LogManager
import java.nio.file.Path
import java.util.*
import kotlin.NoSuchElementException

object PlayerDataManager {
    private val logger = LogManager.getLogger(this::class.java)

    private var cache: MutableMap<UUID, Player> = mutableMapOf()

    private lateinit var dataDir: Path

    fun setup() {
        ServerLifecycleEvents.SERVER_STARTING.register {
            cache.clear()
            dataDir = it.getSavePath(WorldSavePath.ROOT).resolve("FabriKommander")

            dataDir.toFile().mkdir()

            logger.info("Data directory: $dataDir")
        }

        ServerLifecycleEvents.SERVER_STOPPING.register {
            shutdown()
        }
    }

    fun playerJoined(player: ServerPlayerEntity) {
        val uuid = player.uuid

        cache[uuid] = loadData(player)
    }

    fun playerLeft(player: ServerPlayerEntity) {
        val uuid = player.uuid
        val data = cache[uuid]

        if (data != null) {
            saveData(uuid)
            cache.remove(uuid)
        }
    }

    fun loadData(player: ServerPlayerEntity): Player {
        val uuid = player.uuid
        val playerFile = dataDir.resolve("$uuid.yaml").toFile()

        if (!playerFile.exists()) {
            playerFile.createNewFile()

            val data = Player(name = player.gameProfile.name)

            playerFile.writeText(
                Yaml.default.encodeToString(Player.serializer(), data)
            )

            return data
        }

        val string = playerFile.readText()
        return Yaml.default.decodeFromString(Player.serializer(), string)
    }

    fun saveData(uuid: UUID) {
        val playerFile = dataDir.resolve("$uuid.yaml").toFile()
        val data = cache[uuid]
            ?: throw NoSuchElementException("No cached data found for player: ($uuid)")

        if (!playerFile.exists()) {
            playerFile.createNewFile()
        }

        playerFile.writeText(
            Yaml.default.encodeToString(Player.serializer(), data)
        )
    }

    fun getHomes(uuid: UUID): List<Home>? {
        return cache[uuid]?.homes
    }

    fun getHome(uuid: UUID, name: String): Home? {
        return cache[uuid]?.homes
            ?.firstOrNull { it.name.equals(name, ignoreCase = true) }
    }

    fun setHome(uuid: UUID, home: Home): Boolean? {
        val existing = getHome(uuid, home.name)

        if (existing != null) {
            cache[uuid]?.homes?.remove(home)
        }

        val result = cache[uuid]?.homes?.add(home)

        saveData(uuid)

        return result
    }

    fun deleteHome(uuid: UUID, name: String): Boolean? {
        val result = cache[uuid]?.homes?.removeIf { it.name.equals(name, ignoreCase = true) }

        saveData(uuid)

        return result
    }

    fun shutdown() {
        for (uuid in cache.keys) {
            saveData(uuid)
        }
    }
}
