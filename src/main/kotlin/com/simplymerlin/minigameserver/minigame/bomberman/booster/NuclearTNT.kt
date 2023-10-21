package com.simplymerlin.minigameserver.minigame.bomberman.booster

import com.simplymerlin.minigameserver.minigame.bomberman.BomberManGame
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag

class NuclearTNT : Booster {
	override val id: String = "nuclear_tnt"

	override val item: ItemStack = ItemStack
		.of(Material.TNT)
		.withDisplayName(Component.text("Nuclear TNT", NamedTextColor.RED))
		.withLore {
			listOf(Component.text("Create a REALLY big explosion!", NamedTextColor.GRAY).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
		}
		.withTag(Tag.Integer("explosion_size"), 20)

	override fun applyTo(player: Player, game: BomberManGame) {
		player.sendMessage(Component.text("Watch out!", NamedTextColor.GREEN))
	}
}