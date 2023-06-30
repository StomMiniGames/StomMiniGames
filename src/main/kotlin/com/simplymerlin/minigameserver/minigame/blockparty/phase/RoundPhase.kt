package com.simplymerlin.minigameserver.minigame.blockparty.phase

import com.simplymerlin.minigameserver.minigame.blockparty.BlockPartyGame
import com.simplymerlin.minigameserver.minigame.blockparty.util.Floor
import com.simplymerlin.minigameserver.minigame.blockparty.util.FloorBlock
import com.simplymerlin.fsmchamp.State
import com.simplymerlin.fsmchamp.StateSeries
import com.simplymerlin.minigameserver.core.state.GameState
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title
import net.minestom.server.MinecraftServer
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack

class RoundPhase(val game: BlockPartyGame) : StateSeries() {
    
    val chosenBlock: FloorBlock = FloorBlock.values().random()
    
    val playingField = game.playingField
    val instance = game.instance

    val node = EventNode.all(javaClass.name)
    
    init {
        addAll(listOf(
            GeneratePhase(this),
            ChosenPhase(this),
            EndPhase(this),
        ))
    }

    override fun onStart() {
        super.onStart()
        node.addListener(PlayerMoveEvent::class.java) { event ->
            if (event.newPosition.y < 20) {
                val player = event.player
                game.alivePlayers.remove(player)
                player.showTitle(Title.title(
                    Component.text("You Died!", NamedTextColor.RED, TextDecoration.BOLD),
                    Component.text("${game.alivePlayers.size}/${game.instance.players.size} remain.", NamedTextColor.GRAY)
                ))
                player.teleport(Pos(0.0, 70.0, 0.0))

                Audiences.all().sendMessage(Component.text("${player.name} has died!"))

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
    
    class GeneratePhase(private val round: RoundPhase) : GameState() {
        override var time = 10

        override fun onStart() {
            Floor.values().random().applyFloor(round.playingField, round.instance)
            Audiences.all().playSound(Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.MASTER, 1f, 1f))
        }

        override fun onUpdate() {
            println(time)
        }

        override fun onEnd() {
            
        }
    }

    class ChosenPhase(private val round: RoundPhase) : GameState() {
        override var time = 5

        override fun onStart() {
            val material = round.chosenBlock.material
            Audiences.all().sendMessage(Component.text("Chosen block is ${round.chosenBlock.displayName}! Run!"))
            round.instance.players.forEach {
                it.inventory.setItemStack(
                    4,
                    ItemStack.of(material).withDisplayName(Component.text(round.chosenBlock.displayName, round.chosenBlock.color, TextDecoration.BOLD))
                )
            }
        }

        override fun onUpdate() {
            Audiences.all().sendActionBar(Component.text("${time + 1}", NamedTextColor.GRAY))
            Audiences.all().playSound(Sound.sound(Key.key("ui.button.click"), Sound.Source.MASTER, 1f, 2f))
        }

        override fun onEnd() {
            round.instance.players.forEach {
                it.inventory.clear()
            }
        }
    }

    class EndPhase(private val round: RoundPhase) : State() {
        override var time = 5

        override fun onStart() {
            round.playingField.forEach {
                if (round.instance.getBlock(it) != round.chosenBlock.block) {
                    round.instance.setBlock(it, Block.AIR)
                }
            }
            Audiences.all().playSound(Sound.sound(Key.key("entity.ender_dragon.shoot"), Sound.Source.MASTER, 1f, 1f))
        }

        override fun onUpdate() {

        }

        override fun onEnd() {

        }
    }

}