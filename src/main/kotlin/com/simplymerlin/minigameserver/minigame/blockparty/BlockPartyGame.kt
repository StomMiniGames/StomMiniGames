package com.simplymerlin.minigameserver.minigame.blockparty

import com.simplymerlin.minigameserver.minigame.blockparty.phase.RoundPhase
import com.simplymerlin.minigameserver.minigame.blockparty.phase.WaitingState
import com.simplymerlin.minigameserver.Server
import com.simplymerlin.minigameserver.core.Minigame
import com.simplymerlin.minigameserver.core.phase.EndPhase
import com.simplymerlin.minigameserver.core.state.ScheduledStateSeries
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.instance.InstanceContainer

class BlockPartyGame(instance: InstanceContainer, server: Server) : Minigame(instance, server) {

    override val name = "BlockParty"

    val playingField = buildList<Point> {
        for (x in -16..16) {
            for (z in -16..16) {
                this.add(Pos(x.toDouble(), 64.0, z.toDouble()))
            }
        }
    }

    val alivePlayers: MutableList<Player> = mutableListOf()

    private var state = ScheduledStateSeries()

    override fun start() {
        super.start()
        if (state.started) {
            state.end()
            state = ScheduledStateSeries()
        }
        addStates()
        state.start()
    }

    override fun clean() {
        super.clean()
        alivePlayers.clear()
    }

    private fun addStates() {
        state.add(WaitingState(this))
        state.add(RoundPhase(this))
        state.add(EndPhase(this))
    }

    fun addRound() {
        state.addNext(RoundPhase(this))
    }

}