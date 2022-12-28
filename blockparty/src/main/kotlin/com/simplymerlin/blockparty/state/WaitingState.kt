package com.simplymerlin.blockparty.state

import com.simplymerlin.blockparty.BlockPartyGame
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.instance.block.Block

class WaitingState(private val game: BlockPartyGame) : GameState() {

    override var time = 0

    override fun onStart() {
        game.playingField.forEach {
            game.instance.setBlock(it, Block.GLOWSTONE)
        }
        game.alivePlayers.addAll(game.instance.players)
        node.addListener(PlayerLoginEvent::class.java) { event ->
            game.alivePlayers.add(event.player)
        }
        game.instance.players.forEach{
            it.gameMode = GameMode.CREATIVE
            game.playingField.random().add(0.0, 1.0, 0.0).let {point ->
                it.teleport((point as Pos).withLookAt(Pos(0.0, 64.0, 0.0)))
            }
        }
    }

    override fun onUpdate() {

    }

    override fun onEnd() {

    }

    override fun isReadyToEnd(): Boolean {
        return game.instance.players.size > 0
    }

}