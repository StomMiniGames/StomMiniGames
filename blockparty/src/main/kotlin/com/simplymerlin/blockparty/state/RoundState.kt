package com.simplymerlin.blockparty.state

import com.simplymerlin.blockparty.BlockPartyGame
import com.simplymerlin.blockparty.util.FloorBlock
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack
import net.minikloon.fsmgasm.State
import net.minikloon.fsmgasm.StateSeries
import java.time.Duration

class RoundState(val game: BlockPartyGame) : StateSeries() {
    
    val chosenBlock: FloorBlock = FloorBlock.values().random()
    
    val playingField = game.playingField
    val instance = game.instance
    
    init {
        addAll(listOf(
            GenerateState(this),
            ChosenState(this),
            EndState(this),
        ))
    }
    
    class GenerateState(private val round: RoundState) : GameState() {
        override val duration: Duration = Duration.ofSeconds(10)

        override fun onStart() {
            round.playingField.forEach {
                round.instance.setBlock(it, FloorBlock.values().random().block)
            }
            Audiences.all().playSound(Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.MASTER, 1f, 1f))
        }

        override fun onUpdate() {

        }

        override fun onEnd() {
            
        }
    }

    class ChosenState(private val round: RoundState) : GameState() {
        override val duration: Duration = Duration.ofSeconds(5)

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

        }

        override fun onSecond() {
            Audiences.all().sendMessage(Component.text("${remainingDuration.toSeconds() + 1} - $remainingDuration"))
            Audiences.all().playSound(Sound.sound(Key.key("ui.button.click"), Sound.Source.MASTER, 1f, 2f))
        }

        override fun onEnd() {
            round.instance.players.forEach {
                it.inventory.clear()
            }
        }
    }

    class EndState(private val round: RoundState) : State() {
        override val duration: Duration = Duration.ofSeconds(5)

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
            round.game.addRound()
        }
    }

}