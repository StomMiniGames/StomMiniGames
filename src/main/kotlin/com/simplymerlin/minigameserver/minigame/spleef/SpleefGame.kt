package com.simplymerlin.minigameserver.minigame.spleef

import com.simplymerlin.minigameserver.Server
import com.simplymerlin.minigameserver.core.Minigame
import com.simplymerlin.minigameserver.core.phase.EndPhase
import com.simplymerlin.minigameserver.core.state.ScheduledStateSeries
import com.simplymerlin.minigameserver.minigame.spleef.state.GameState
import com.simplymerlin.minigameserver.minigame.spleef.state.SetupState
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.item.Material

class SpleefGame(instance: InstanceContainer, server: Server) : Minigame(instance, server) {

	override val name: String
		get() = "Spleef"

	override val displayName: Component
		get() = Component.text("Spleef", NamedTextColor.GRAY)
	override val displayDescription: List<Component> = listOf(
		Component.text("Destroy the blocks below your components", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
	)
	override val icon: Material
		get() = Material.SNOWBALL

	val playingField = buildList<net.minestom.server.coordinate.Point> {
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
		if(state.started) {
			state.end()
			state = ScheduledStateSeries()
		}
		addStates()
		state.start()
	}

	override fun clean() {
		super.clean()
	}

	private fun addStates() {
		state.add(SetupState(this))
		state.add(GameState(this))
		state.add(EndPhase(this))
	}
}