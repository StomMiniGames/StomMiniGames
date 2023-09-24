package com.simplymerlin.minigameserver.minigame.bomberman.phase

import com.simplymerlin.minigameserver.core.state.GameState
import com.simplymerlin.minigameserver.minigame.bomberman.BomberManGame
import io.github.bloepiloepi.pvp.PvpExtension
import io.github.bloepiloepi.pvp.config.ExplosionConfig
import io.github.bloepiloepi.pvp.config.PvPConfig
import io.github.bloepiloepi.pvp.events.ExplosionEvent
import io.github.bloepiloepi.pvp.explosion.ExplosionListener.primeTnt
import io.github.bloepiloepi.pvp.explosion.PvpExplosionSupplier
import io.github.bloepiloepi.pvp.explosion.TntEntity
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title
import net.minestom.server.MinecraftServer
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.GameMode
import net.minestom.server.event.EventListener
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.event.player.PlayerDeathEvent
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.instance.Explosion
import net.minestom.server.instance.ExplosionSupplier
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class GamePhase(private val game: BomberManGame): GameState() {

	override var time: Int = 60 * 10

	override fun onStart() {
		node.addChild(
			PvPConfig.emptyBuilder()
				.explosion(ExplosionConfig.DEFAULT)
				.build()
				.createNode()
		)
		game.instance.explosionSupplier = PvpExplosionSupplier.INSTANCE
//		MinecraftServer.getGlobalEventHandler().addChild(PvpExtension.events())

		node.addListener(EventListener.builder(PlayerBlockPlaceEvent::class.java)
			.filter { it.block == Block.TNT }
			.handler {
				it.isCancelled = true
				primeTnt(game.instance, it.blockPosition, it.player)


				val pos = it.blockPosition

//				game.instance.explode(pos.x().toFloat(), pos.y().toFloat(), pos.z().toFloat(), 4f)
			}
			.build())

		node.addListener(PlayerDeathEvent::class.java) {
			game.alivePlayers.remove(it.player)
			it.player.showTitle(
				Title.title(
					Component.text("You Died!", NamedTextColor.RED, TextDecoration.BOLD),
					Component.text("${game.alivePlayers.size}/${game.instance.players.size} remain.", NamedTextColor.GRAY)
				))
			it.player.teleport(Pos(0.0, 70.0, 0.0))

			Audiences.all().sendMessage(Component.text("${it.player.username} has died!"))

			it.player.gameMode = GameMode.SPECTATOR
			if (game.alivePlayers.size == 0) {
				end()
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
			it.inventory.addItemStack(ItemStack.of(Material.TNT))
		}
		game.instance.sendMessage(Component.text("The game has started", NamedTextColor.GREEN))
	}

	override fun onUpdate() {
	}

	override fun onEnd() {
	}
}