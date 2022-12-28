package com.simplymerlin.blockparty.state

import com.simplymerlin.blockparty.BlockPartyGame
import net.minikloon.fsmgasm.State
import java.time.Duration

class EndState(private val game: BlockPartyGame) : State() {

    override val duration: Duration = Duration.ofSeconds(10)

    override fun onStart() {

    }

    override fun onUpdate() {

    }

    override fun onEnd() {

    }

}