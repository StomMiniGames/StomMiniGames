package com.simplymerlin.blockparty.state

import com.simplymerlin.blockparty.BlockPartyGame
import com.simplymerlin.fsmchamp.State
import net.kyori.adventure.text.Component
import net.minestom.server.adventure.audience.Audiences

class EndState(private val game: BlockPartyGame) : State() {

    override var time = 10

    override fun onStart() {
        Audiences.all().sendMessage(Component.text("Game has ended!"))
    }

    override fun onUpdate() {

    }

    override fun onEnd() {
        game.alivePlayers.clear()
        game.restart()
    }

}