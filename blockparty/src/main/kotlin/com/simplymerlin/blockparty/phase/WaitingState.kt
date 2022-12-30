package com.simplymerlin.blockparty.phase

import com.simplymerlin.blockparty.BlockPartyGame
import com.simplymerlin.core.state.GameState
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.instance.block.Block

class WaitingState(private val game: BlockPartyGame) : GameState() {

    override var time = 5

    override fun onStart() {
        game.playingField.forEach {
            game.instance.setBlock(it, Block.GLOWSTONE)
        }
        game.alivePlayers.addAll(MinecraftServer.getConnectionManager().onlinePlayers)
        MinecraftServer.getConnectionManager().onlinePlayers.forEach{
            it.gameMode = GameMode.ADVENTURE
            game.playingField.random().add(0.0, 1.0, 0.0).let {point ->
                it.setInstance(game.instance, (point as Pos).withLookAt(Pos(0.0, 64.0, 0.0)))
            }
        }
        Audiences.all().sendMessage(Component.text("Starting soon..."))
    }

    override fun onUpdate() {

    }

    override fun onEnd() {

    }

}