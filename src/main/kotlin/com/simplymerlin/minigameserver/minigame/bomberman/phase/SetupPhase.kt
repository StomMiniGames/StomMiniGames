package com.simplymerlin.minigameserver.minigame.bomberman.phase

import com.simplymerlin.minigameserver.core.state.GameState
import com.simplymerlin.minigameserver.minigame.bomberman.BomberManGame
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.instance.batch.AbsoluteBlockBatch
import net.minestom.server.instance.block.Block
import kotlin.random.Random

class SetupPhase(private val game: BomberManGame) : GameState() {

	override var time: Int = 5

	override fun onStart() {
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

			if(point.x() % 4 == 0.0 && point.z() % 4 == 0.0) {
				repeat(5) {
					batch.setBlock(point.add(0.0, it.toDouble(), 0.0), Block.OBSIDIAN)
				}
				return@forEach
			}

			repeat(Random.nextInt(1, 3)) {
				batch.setBlock(point.add(0.0, it.toDouble(), 0.0), game.fillerBlocks.random())
			}
		}
		batch.apply(game.instance) {}

		val teleportPoints = game.playingField
			.map { it.add(0.0, 3.0, 0.0) }
			.filter { it.x() != 0.0 || it.z() != 0.0 }
			.filter { it.x() % 2 != 0.0 && it.y() % 2 != 0.0 }

		game.alivePlayers.addAll(MinecraftServer.getConnectionManager().onlinePlayers)
		MinecraftServer.getConnectionManager().onlinePlayers.forEach{
			it.gameMode = GameMode.ADVENTURE
			teleportPoints.random().let { point ->
				it.setInstance(game.instance, (point as Pos).withLookAt(Pos(0.0, 64.0, 0.0)))
			}
		}
	}

	override fun onEnd() {
		Audiences.all().sendMessage(Component.text("Starting soon..."))
	}

	override fun onUpdate() {

	}
}