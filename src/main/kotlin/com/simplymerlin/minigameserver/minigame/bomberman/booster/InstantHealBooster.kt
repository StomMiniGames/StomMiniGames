package com.simplymerlin.minigameserver.minigame.bomberman.booster

import com.simplymerlin.minigameserver.minigame.bomberman.BomberManGame
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

class InstantHealBooster : Booster {
	override val id: String = "instant_heal"

	override val item: ItemStack = ItemStack
		.of(Material.ENCHANTED_GOLDEN_APPLE)
		.withDisplayName(Component.text("Instant Heal", NamedTextColor.LIGHT_PURPLE))
		.withLore {
			listOf(
				Component.text("Right click to receive", NamedTextColor.GRAY).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE),
				Component.text("an instant heal!", NamedTextColor.GRAY).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE),
				)
		}

	override fun applyTo(player: Player, game: BomberManGame) {
		player.heal()
		player.playSound(Sound.sound(Key.key("entity.player.burp"), Sound.Source.PLAYER, 1f, 1f), player.position)
	}
}