package com.simplymerlin.minigameserver

import com.simplymerlin.blockparty.BlockPartyGame
import com.simplymerlin.core.Minigame
import com.simplymerlin.core.Server
import com.simplymerlin.minigameserver.command.SetGameCommand
import com.simplymerlin.minigameserver.command.StartCommand
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.extras.MojangAuth
import net.minestom.server.instance.block.Block

class ServerImpl : Server() {

    private val minecraftServer  = MinecraftServer.init()
    private val instanceManager = MinecraftServer.getInstanceManager()
    private val globalEventHandler = MinecraftServer.getGlobalEventHandler()

    private val hub = instanceManager.createInstanceContainer(FullBrightDimension.dimension)

    val games = listOf<Minigame>(
        BlockPartyGame(instanceManager.createInstanceContainer(FullBrightDimension.dimension), this)
    )

    var currentGame: Minigame = games[0]

    init {
        MojangAuth.init()
        initialiseEvents()

        MinecraftServer.getCommandManager().register(StartCommand(this))
        MinecraftServer.getCommandManager().register(SetGameCommand(this))

        hub.setBlock(0, 64, 0, Block.STONE)

        minecraftServer.start("0.0.0.0", 25565)
    }

    fun start() {
        currentGame.start()
    }

    private fun initialiseEvents() {
        globalEventHandler.addListener(PlayerLoginEvent::class.java) { event ->
            val player = event.player
            player.gameMode = GameMode.ADVENTURE
            event.setSpawningInstance(hub)

            player.respawnPoint = Pos(0.0, 65.0, 0.0)
        }

        globalEventHandler.addListener(PlayerLoginEvent::class.java) {
            val player = it.player
            Audiences.all().sendMessage(
                Component.text()
                    .append(Component.text("+", TextColor.color(0x00FF00)))
                    .append(Component.space())
                    .append(player.name.color(NamedTextColor.GRAY))
            )
        }
        globalEventHandler.addListener(PlayerDisconnectEvent::class.java) {
            val player = it.player
            Audiences.all().sendMessage(
                Component.text()
                    .append(Component.text("-", TextColor.color(0xFF0000)))
                    .append(Component.space())
                    .append(player.name.color(NamedTextColor.GRAY))
            )
        }
    }

    override fun teleportAllToHub() {
        MinecraftServer.getConnectionManager().onlinePlayers.forEach {
            if (it.instance != hub) {
                it.setInstance(hub, Pos(0.0, 65.0, 0.0))
            }
            it.gameMode = GameMode.ADVENTURE
        }
    }

}