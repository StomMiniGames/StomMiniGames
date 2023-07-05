package com.simplymerlin.minigameserver.core.phase

import com.simplymerlin.minigameserver.core.Minigame
import com.simplymerlin.minigameserver.core.state.GameState
import net.minestom.server.MinecraftServer

class EndPhase(val minigame: Minigame) : GameState() {

    override var time = 10

    override fun onStart() {
        minigame.logger.info("Minigame ${minigame.name} has ended.")
    }

    override fun onUpdate() {

    }

    override fun onEnd() {
        minigame.running = false
        minigame.clean()
        MinecraftServer.getConnectionManager().onlinePlayers.forEach {
            it.inventory.clear()
        }
        minigame.server.teleportAllToHub()
        minigame.instance.chunks.forEach {
            minigame.instance.unloadChunk(it)
        }
    }

}