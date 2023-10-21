package com.simplymerlin.minigameserver.minigame.bomberman

import com.simplymerlin.minigameserver.Server
import com.simplymerlin.minigameserver.core.Minigame
import com.simplymerlin.minigameserver.core.phase.EndPhase
import com.simplymerlin.minigameserver.core.state.ScheduledStateSeries
import com.simplymerlin.minigameserver.minigame.bomberman.booster.Booster
import com.simplymerlin.minigameserver.minigame.bomberman.booster.InstantHealBooster
import com.simplymerlin.minigameserver.minigame.bomberman.booster.NuclearTNT
import com.simplymerlin.minigameserver.minigame.bomberman.phase.RoundPhase
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.block.Block
import net.minestom.server.item.Material

class BomberManGame(instance: InstanceContainer, server: Server): Minigame(instance, server) {

	override val name: String
		get() = "Bomber"

	override val displayName: Component
		get() = Component.text("Bomber", NamedTextColor.GOLD)
	override val displayDescription: List<Component> = listOf(
		Component.text("Blow up your friends and try to survive yourself", NamedTextColor.GRAY)
	)
	override val icon: Material
		get() = Material.TNT

	val playingField = buildList<net.minestom.server.coordinate.Point> {
		for (x in -16..16) {
			for (z in -16..16) {
				this.add(Pos(x.toDouble(), 64.0, z.toDouble()))
			}
		}
	}

	val fillerBlocks: Set<Block> = setOf(
		Block.OAK_PLANKS,
		Block.NETHERRACK,
		Block.BRICKS,
		Block.END_STONE_BRICKS,
		Block.STONE,
		Block.TERRACOTTA,
		Block.BONE_BLOCK,
		Block.MELON,
		Block.SLIME_BLOCK
	)

	val boosters: Map<String, Booster> = setOf(
		InstantHealBooster(),
		NuclearTNT()
	).associateBy { it.id }

	val alivePlayers: MutableList<Player> = mutableListOf()

	private var state = ScheduledStateSeries()

	override fun start() {
		super.start()
		if(state.started) {
			state.end()
			state = ScheduledStateSeries()
		}
		addStates()
		state.start()
	}

	override fun clean() {
		super.clean()
	}

	private fun addStates() {
		state.add(RoundPhase(this))
		state.add(EndPhase(this))
	}

	fun addRound() {
		state.addNext(RoundPhase(this))
	}

}