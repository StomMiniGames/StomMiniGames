package com.simplymerlin.blockparty.state

import com.simplymerlin.blockparty.BlockPartyGame
import java.time.Duration

class WaitingState(private val game: BlockPartyGame) : GameState() {

    override val duration: Duration = Duration.ofSeconds(0)

    override fun onStart() {
        println("Start")
    }

    override fun onUpdate() {

    }

    override fun onEnd() {
        println("End")
    }

    override fun isReadyToEnd(): Boolean {
        return game.instance.players.size > 0
    }

}