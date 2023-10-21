package com.simplymerlin.minigameserver.minigame.bomberman.booster

import com.simplymerlin.minigameserver.minigame.bomberman.BomberManGame
import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack

interface Booster {

	val id: String

	val item: ItemStack
	val name: Component
		get() = item.meta().displayName ?: Component.empty()

	fun applyTo(player: Player, game: BomberManGame)
}