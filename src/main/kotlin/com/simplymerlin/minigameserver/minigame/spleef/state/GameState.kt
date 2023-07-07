package com.simplymerlin.minigameserver.minigame.spleef.state

import com.simplymerlin.minigameserver.core.ConstantValues.SNOWBALL_VELOCITY
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
import net.minestom.server.entity.EntityProjectile
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.GameMode
import net.minestom.server.event.EventListener
import net.minestom.server.event.entity.projectile.ProjectileCollideWithBlockEvent
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.event.player.PlayerStartDiggingEvent
import net.minestom.server.event.player.PlayerUseItemEvent
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
		node.addListener(
			EventListener.builder(PlayerUseItemEvent::class.java)
				.filter {
					it.itemStack.material() == Material.SNOWBALL
				}
				.handler {
					game.logger.debug("Projectile!")
					val projectile = EntityProjectile(it.player, EntityType.SNOWBALL)
					projectile.instance = game.instance
					projectile.velocity = it.player.position.direction().normalize().mul(SNOWBALL_VELOCITY)

					projectile.setInstance(game.instance, it.player.position.add(0.0, it.player.eyeHeight, 0.0))
				}
				.build()
		)
		node.addListener(ProjectileCollideWithBlockEvent::class.java) {
			if(it.entity.entityType != EntityType.SNOWBALL) return@addListener
			it.instance.setBlock(it.collisionPosition, Block.AIR)
			it.instance.playSound(
				Sound.sound(Key.key("block.bubble_column.bubble_pop"), Sound.Source.BLOCK, 1f, 1f),
				it.collisionPosition
			)
			it.entity.remove()
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