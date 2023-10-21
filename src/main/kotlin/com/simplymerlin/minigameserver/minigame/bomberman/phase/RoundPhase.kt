package com.simplymerlin.minigameserver.minigame.bomberman.phase

import com.simplymerlin.fsmchamp.State
import com.simplymerlin.fsmchamp.StateSeries
import com.simplymerlin.minigameserver.core.state.GameState
import com.simplymerlin.minigameserver.minigame.bomberman.BomberManGame
import io.github.bloepiloepi.pvp.config.ExplosionConfig
import io.github.bloepiloepi.pvp.config.PvPConfig
import io.github.bloepiloepi.pvp.explosion.ExplosionListener
import io.github.bloepiloepi.pvp.explosion.PvpExplosionSupplier
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title
import net.minestom.server.MinecraftServer
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.event.EventListener
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.event.player.PlayerDeathEvent
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.instance.batch.AbsoluteBlockBatch
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import kotlin.random.Random

class RoundPhase(val game: BomberManGame) : StateSeries() {

	companion object {
		// TODO: Make this an editable game option
		const val RESPAWN = true
	}

	private val node = EventNode.all(javaClass.name)


	init {
		addAll(listOf(
			SetupPhase(this),
			GamePhase(this),
			EndRoundPhase(this)
		))
	}

	override fun onStart() {
		super.onStart()
		game.logger.debug("Round phase started")
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
		MinecraftServer.getGlobalEventHandler().addChild(node)
	}

	override fun onUpdate() {
		super.onUpdate()
		if (game.alivePlayers.size == 0) {
			end()
		}
	}

	override fun onEnd() {
		if (game.alivePlayers.size != 0) {
			game.addRound()
		}
		super.onEnd()
		MinecraftServer.getGlobalEventHandler().removeChild(node)
	}



	class SetupPhase(round: RoundPhase) : GameState() {

		override var time: Int = 5
		val game = round.game

		override fun onStart() {
			game.logger.debug("Setup phase")
			val batch = AbsoluteBlockBatch()

			game.playingField.forEach { point ->
				game.instance.loadChunk(point)
				batch.setBlock(point, Block.BEDROCK)
				val point = point.add(0.0, 1.0, 0.0)

//			if(point.x() == 0.0 || point.z() == 0.0) {
//				repeat(5) {
//					batch.setBlock(point.add(0.0, it.toDouble(), 0.0), Block.BEDROCK)
//				}
//				return@forEach
//			}

				if(point.x() % 2 == 0.0 && point.z() % 2 == 0.0) {
					repeat(5) {
						batch.setBlock(point.add(0.0, it.toDouble(), 0.0), Block.OBSIDIAN)
					}
					return@forEach
				}

				repeat(Random.nextInt(3, 5)) {
					batch.setBlock(point.add(0.0, it.toDouble(), 0.0), game.fillerBlocks.random())
				}
			}
			batch.apply(game.instance) {}

			val teleportPoints = game.playingField
				.map { it.add(0.0, 1.0, 0.0) }
				.filter { it.x() % 2 != 0.0 && it.z() % 2 != 0.0 }

			game.alivePlayers.addAll(MinecraftServer.getConnectionManager().onlinePlayers)
			game.alivePlayers.forEach {
				it.gameMode = GameMode.ADVENTURE
				teleportPoints.random().let { point ->
					val spawnBatch = AbsoluteBlockBatch()
					for (x in -3..1) {
						for (z in -3..1) {
							repeat(3) { y ->
								val block = Pos(point.add(x.toDouble(), y.toDouble(), z.toDouble()))
								if(game.instance.getBlock(block) == Block.OBSIDIAN) return@repeat
								spawnBatch.setBlock(block, Block.AIR)
							}
						}
					}
					spawnBatch.apply(game.instance) {}
					if(it.instance != game.instance) it.setInstance(game.instance, (point as Pos).withLookAt(Pos(0.0, 64.0, 0.0)))
					it.teleport((point as Pos).withLookAt(Pos(0.0, 64.0, 0.0)))
				}
			}
		}

		override fun onEnd() {
			Audiences.all().sendMessage(Component.text("Starting soon..."))
		}

		override fun onUpdate() {
		}
	}

	class GamePhase(round: RoundPhase): GameState() {

		override var time: Int = 60 * 10
		val game = round.game

		override fun onStart() {
			game.logger.debug("gaming")
			node.addChild(
				PvPConfig.emptyBuilder()
					.explosion(ExplosionConfig.DEFAULT)
					.build()
					.createNode()
			)
			game.instance.explosionSupplier = PvpExplosionSupplier.INSTANCE

			node.addListener(EventListener.builder(PlayerBlockPlaceEvent::class.java)
				.filter { it.block == Block.TNT }
				.handler {
					it.isCancelled = true
					ExplosionListener.primeTnt(game.instance, it.blockPosition, it.player)
				}
				.build())

			game.alivePlayers.forEach {
				it.gameMode = GameMode.SURVIVAL
				it.inventory.addItemStack(ItemStack.of(Material.TNT))
			}
			game.instance.sendMessage(Component.text("The game has started", NamedTextColor.GREEN))
		}

		override fun onUpdate() {
			if(game.alivePlayers.size == 1) {
				end()
			}
		}

		override fun onEnd() {
		}
	}

	class EndRoundPhase(round: RoundPhase) : State() {
		override var time: Int = 5
		val game = round.game

		override fun onStart() {
			game.logger.debug("ending")
			game.alivePlayers.forEach {
				it.gameMode = GameMode.SPECTATOR
				it.inventory.clear()
				it.heal()
			}
		}

		override fun onUpdate() {
		}
		override fun onEnd() {
		}
	}
}