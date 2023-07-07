package com.simplymerlin.minigameserver.minigame.spleef.state

import com.simplymerlin.minigameserver.core.state.GameState
import com.simplymerlin.minigameserver.minigame.spleef.SpleefGame
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.event.entity.projectile.ProjectileCollideWithBlockEvent
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.event.player.PlayerStartDiggingEvent
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

class GameState(private val game: SpleefGame) : GameState() {

	override var time: Int = 600


	override fun onStart() {
		node.addListener(PlayerStartDiggingEvent::class.java) {
			if(it.entity.itemInMainHand.material() != Material.STONE_SHOVEL) return@addListener
			it.isCancelled = true
			it.instance.setBlock(it.blockPosition, Block.AIR)
			it.entity.inventory.addItemStack(ItemStack.of(Material.SNOWBALL))
		}
		node.addListener(ProjectileCollideWithBlockEvent::class.java) {
			it.instance.setBlock(it.collisionPosition, Block.AIR)
			it.instance.playSound(
				Sound.sound(Key.key("block.bubble_column.bubble_pop"), Sound.Source.BLOCK, 1f, 1f),
				it.collisionPosition
			)
			// TODO: particles?
		}
		node.addListener(PlayerMoveEvent::class.java) { event ->
			if (event.newPosition.y < 20) {
				val player = event.player
				game.alivePlayers.remove(player)
				player.showTitle(
					Title.title(
					Component.text("You Died!", NamedTextColor.RED, TextDecoration.BOLD),
					Component.text("${game.alivePlayers.size}/${game.instance.players.size} remain.", NamedTextColor.GRAY)
				))
				player.teleport(Pos(0.0, 70.0, 0.0))

				Audiences.all().sendMessage(Component.text("${player.username} has died!"))

				player.gameMode = GameMode.SPECTATOR
				if (game.alivePlayers.size == 0) {
					end()
				}
			}
		}
		node.addListener(PlayerDisconnectEvent::class.java) {
			game.alivePlayers.remove(it.player)
			if (game.alivePlayers.size == 0) {
				end()
			}
		}
		game.alivePlayers.forEach {
			it.gameMode = GameMode.SURVIVAL
		}
		game.instance.sendMessage(Component.text("The game has started", NamedTextColor.GREEN))
	}

	override fun onUpdate() {
	}

	override fun onEnd() {
		game.alivePlayers.forEach {
			it.gameMode = GameMode.ADVENTURE
		}
	}
}