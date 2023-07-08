package com.simplymerlin.minigameserver.minigame.oitc

import com.simplymerlin.minigameserver.Server
import com.simplymerlin.minigameserver.core.Minigame
import com.simplymerlin.minigameserver.core.phase.EndPhase
import com.simplymerlin.minigameserver.core.state.ScheduledStateSeries
import com.simplymerlin.minigameserver.minigame.oitc.phase.GamePhase
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
    override val name
        get() = "One In The Chamber"
    override val displayName = Component.text("One In The Chamber", NamedTextColor.RED)
    override val displayDescription = listOf(Component.text("Beat your friends in some classic OITC!"))
    override val icon: Material = Material.BOW

    val playerData: MutableMap<Player, OITCPlayerData> = mutableMapOf()

    val players: Set<Player>
        get() = playerData.keys

    // TEMP: Playing field
    val playingField = buildList<Point> {
        for (x in -32..32) {
            for (z in -32..32) {
                this.add(Pos(x.toDouble(), 64.0, z.toDouble()))
            }
        }
    }

    private var state = ScheduledStateSeries()

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
        MinecraftServer.getConnectionManager().onlinePlayers.forEach {
            playerData[it] = OITCPlayerData(player = it)
            playingField.random().add(0.0, 1.0, 0.0).let {point ->
                it.setInstance(instance, (point as Pos).withLookAt(Pos(0.0, 64.0, 0.0)))
            }
        }
        if (state.started) {
            state.end()
            state = ScheduledStateSeries()
        }
        addStates()
        state.start()
    }

    override fun clean() {
        super.clean()
        playerData.clear()
    }

    private fun addStates() {
        state.add(GamePhase(this))
        state.add(EndPhase(this))
    }

    data class OITCPlayerData(val player: Player, var score: Int = 0) {
        fun addScore() {
            score++
        }
    }

}