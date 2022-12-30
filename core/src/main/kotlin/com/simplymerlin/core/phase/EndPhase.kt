package com.simplymerlin.core.phase

import com.simplymerlin.core.Minigame
import com.simplymerlin.core.state.GameState

class EndPhase(val minigame: Minigame) : GameState() {

    override var time = 10

    override fun onStart() {
        println("Minigame ${minigame.name} has ended.")
    }

    override fun onUpdate() {

    }

    override fun onEnd() {
        minigame.running = false
        minigame.clean()
        minigame.server.teleportAllToHub()
    }

}