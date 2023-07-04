package com.simplymerlin.minigameserver.minigame.oitc

import com.simplymerlin.minigameserver.Server
import com.simplymerlin.minigameserver.core.Minigame
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.batch.AbsoluteBlockBatch
import net.minestom.server.instance.block.Block
import net.minestom.server.item.Material

class OneInTheChamberGame(instance: InstanceContainer, server: Server) : Minigame(instance, server) {
    override val name = "One In The Chamber"
    override val displayName = Component.text("One In The Chamber", NamedTextColor.RED, TextDecoration.BOLD)
    override val displayDescription = listOf(Component.text("Beat your friends in some classic OITC!", NamedTextColor.RED))
    override val icon: Material = Material.BOW

    val players: MutableList<Player> = mutableListOf()

    // TEMP: Playing field
    val playingField = buildList<Point> {
        for (x in -32..32) {
            for (z in -32..32) {
                this.add(Pos(x.toDouble(), 64.0, z.toDouble()))
            }
        }
    }

    init {
        val batch = AbsoluteBlockBatch()
        playingField.forEach {
            instance.loadChunk(it)
            batch.setBlock(it, Block.STONE)
        }
        batch.apply(instance) {}
    }

    override fun start() {
        super.start()
        players.addAll(MinecraftServer.getConnectionManager().onlinePlayers)
        players.forEach {
            playingField.random().add(0.0, 1.0, 0.0).let {point ->
                it.setInstance(instance, (point as Pos).withLookAt(Pos(0.0, 64.0, 0.0)))
            }
        }
    }

    override fun clean() {
        super.clean()
        players.clear()
    }

}