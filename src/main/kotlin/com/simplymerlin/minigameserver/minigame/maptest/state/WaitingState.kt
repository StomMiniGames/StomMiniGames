package com.simplymerlin.minigameserver.minigame.maptest.state

import com.simplymerlin.minigameserver.core.state.GameState
import com.simplymerlin.minigameserver.minigame.maptest.MapTestGame
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos

class WaitingState(private val game: MapTestGame) : GameState() {

	override var time: Int = 3

	override fun onStart() {
		MinecraftServer.getConnectionManager().onlinePlayers.forEach {
			it.setInstance(game.instance, Pos(0.0, 44.0, 0.0))
		}
	}

	override fun onUpdate() {
	}

	override fun onEnd() {
	}
}