package com.simplymerlin.minigameserver.minigame.maptest

import com.simplymerlin.minigameserver.Server
import com.simplymerlin.minigameserver.core.Minigame
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.item.Material

class MapTestGame(instance: InstanceContainer, server: Server): Minigame(instance, server) {

	override val name: String = "maptest"
	override val displayName: Component = Component.text("TEST MAP!!")
	override val displayDescription: List<Component> = listOf(Component.text("An example of map loading"))
	override val icon: Material = Material.FILLED_MAP

	override fun start() {
		super.start()
		MinecraftServer.getConnectionManager().onlinePlayers.forEach {
			it.setInstance(instance, Pos(0.0, 44.0, 0.0))
		}
	}

	override fun getEarlyName(): String {
		return name
	}
}