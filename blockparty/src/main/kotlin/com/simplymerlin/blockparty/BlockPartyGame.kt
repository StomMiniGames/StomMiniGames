package com.simplymerlin.blockparty

import com.simplymerlin.blockparty.state.EndState
import com.simplymerlin.blockparty.state.RoundState
import com.simplymerlin.blockparty.state.ScheduledStateSeries
import com.simplymerlin.blockparty.state.WaitingState
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.block.Block

class BlockPartyGame(val instance: InstanceContainer) {

    private val globalEventHandler = MinecraftServer.getGlobalEventHandler()

    val playingField = buildList<Point> {
        for (x in -16..16) {
            for (z in -16..16) {
                this.add(Pos(x.toDouble(), 64.0, z.toDouble()))
            }
        }
    }

    private val state = ScheduledStateSeries()

    init {
        playingField.forEach {
            instance.setBlock(it, Block.GLOWSTONE)
        }

        globalEventHandler.addListener(PlayerLoginEvent::class.java) { event ->
            val player = event.player
            player.gameMode = GameMode.CREATIVE
            event.setSpawningInstance(instance)

            playingField.random().add(0.0, 1.0, 0.0).let {
                player.respawnPoint = (it as Pos).add(0.0, player.eyeHeight, 0.0)
                    .withLookAt(Pos(0.0, 64.0, 0.0))
            }
        }
        state.add(WaitingState(this))
        state.add(RoundState(this))
        state.add(EndState(this))
        state.start()
    }

    fun addRound() {
        state.addNext(RoundState(this))
    }


}