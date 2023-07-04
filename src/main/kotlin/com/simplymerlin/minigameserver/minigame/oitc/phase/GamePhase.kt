package com.simplymerlin.minigameserver.minigame.oitc.phase

import com.simplymerlin.minigameserver.core.state.GameState
import com.simplymerlin.minigameserver.minigame.oitc.OneInTheChamberGame
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerDeathEvent
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

class GamePhase(val game: OneInTheChamberGame) : GameState() {

    override var time = 60*10

    private val bow: ItemStack = ItemStack.of(Material.BOW)
    private val sword: ItemStack = ItemStack.of(Material.STONE_SWORD)
    private val arrow: ItemStack = ItemStack.of(Material.ARROW)

    override fun onStart() {
        game.players.forEach {
            spawn(it)
        }
    }

    override fun onUpdate() {
        TODO("Not yet implemented")
    }

    override fun onEnd() {
        TODO("Not yet implemented")
    }

    private fun spawn(player: Player) {
        game.playingField.random().add(0.0, 1.0, 0.0).let {point ->
            player.teleport((point as Pos).withLookAt(Pos(0.0, 64.0, 0.0)))
        }
        player.inventory.clear()
        player.inventory.setItemStack(36, bow)
        player.inventory.setItemStack(37, sword)
        player.inventory.setItemStack(44, arrow)
    }

    private fun onKill(killer: Player, target: Player) {
        spawn(target)
        if (killer.inventory.itemStacks.any { it.material() == Material.ARROW }) {
            killer.inventory.addItemStack(arrow)
        } else {
            killer.inventory.setItemStack(44, arrow)
        }
    }

}