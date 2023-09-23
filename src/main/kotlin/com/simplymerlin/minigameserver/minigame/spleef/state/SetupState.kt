package com.simplymerlin.minigameserver.minigame.spleef.state

import com.simplymerlin.minigameserver.core.state.GameState
import com.simplymerlin.minigameserver.minigame.spleef.SpleefGame
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.instance.batch.AbsoluteBlockBatch
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

class SetupState(private val game: SpleefGame) : GameState() {

	override var time = 5

	override fun onStart() {
		val batch = AbsoluteBlockBatch()
		repeat(4) { i ->
			game.logger.debug(game.playingField[0].add(0.0, i * 4.0, 0.0).toString())
			game.playingField.forEach {
				if(i == 0) game.instance.loadChunk(it)
				batch.setBlock(it.add(0.0, i * 4.0, 0.0), Block.SNOW_BLOCK)
			}
		}
		batch.apply(game.instance) {
			game.alivePlayers.addAll(MinecraftServer.getConnectionManager().onlinePlayers)
			val shovel = ItemStack.of(Material.STONE_SHOVEL).withDisplayName(Component.text("Ye Shovel", NamedTextColor.GREEN))
			MinecraftServer.getConnectionManager().onlinePlayers.forEach{
				it.gameMode = GameMode.ADVENTURE
				it.inventory.setItemStack(0, shovel)
				game.playingField.random().withY(77.0).let { point ->
					it.setInstance(game.instance, (point as Pos).withLookAt(Pos(0.0, 64.0, 0.0)))
				}
			}
			Audiences.all().sendMessage(Component.text("Starting soon..."))
		}
	}

	override fun onUpdate() {

	}

	override fun onEnd() {

	}

}