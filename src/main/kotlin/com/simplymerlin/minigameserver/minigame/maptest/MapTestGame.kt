package com.simplymerlin.minigameserver.minigame.maptest

import com.simplymerlin.minigameserver.Server
import com.simplymerlin.minigameserver.core.Minigame
import com.simplymerlin.minigameserver.core.phase.EndPhase
import com.simplymerlin.minigameserver.core.state.ScheduledStateSeries
import com.simplymerlin.minigameserver.minigame.maptest.state.WaitingState
import net.kyori.adventure.text.Component
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.item.Material

class MapTestGame(instance: InstanceContainer, server: Server): Minigame(instance, server) {

	override val name
		get() = "maptest"

	override val displayName: Component = Component.text("TEST MAP!!")
	override val displayDescription: List<Component> = listOf(Component.text("An example of map loading"))
	override val icon: Material = Material.FILLED_MAP

	private var state = ScheduledStateSeries()


	override fun start() {
		super.start()
		if(state.started) {
			state.end()
			state = ScheduledStateSeries()
		}

		state.add(WaitingState(this))
		state.add(EndPhase(this))

		state.start()
	}
}