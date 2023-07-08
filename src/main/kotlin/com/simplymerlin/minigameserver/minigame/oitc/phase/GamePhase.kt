package com.simplymerlin.minigameserver.minigame.oitc.phase

import com.simplymerlin.minigameserver.core.cancel
import com.simplymerlin.minigameserver.core.state.GameState
import com.simplymerlin.minigameserver.minigame.oitc.OneInTheChamberGame
import io.github.bloepiloepi.pvp.PvpExtension
import io.github.bloepiloepi.pvp.damage.CustomDamageType
import io.github.bloepiloepi.pvp.events.FinalDamageEvent
import io.github.bloepiloepi.pvp.events.PickupEntityEvent
import io.github.bloepiloepi.pvp.events.ProjectileHitEvent.ProjectileBlockHitEvent
import io.github.bloepiloepi.pvp.projectile.Arrow
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.title.Title
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.event.item.ItemDropEvent
import net.minestom.server.event.player.PlayerDeathEvent
import net.minestom.server.event.player.PlayerRespawnEvent
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.scoreboard.Sidebar
import kotlin.math.min

class GamePhase(private val game: OneInTheChamberGame) : GameState() {

    override var time = 60*10

    private val bow: ItemStack = ItemStack.of(Material.BOW)
    private val sword: ItemStack = ItemStack.of(Material.STONE_SWORD)
    private val arrow: ItemStack = ItemStack.of(Material.ARROW)

    private val sidebar: Sidebar = Sidebar(game.displayName)

    override fun onStart() {
        game.players.forEach {
            spawn(it)
            sidebar.addViewer(it)
        }

        createLines()

        node.addChild(PvpExtension.events())

        PickupEntityEvent::class.cancel(node)
        ItemDropEvent::class.cancel(node)

        node.addListener(PlayerRespawnEvent::class.java) {
            spawn(it.player, false)
        }

        node.addListener(PlayerDeathEvent::class.java) {
            it.chatMessage = null
            it.player.respawnPoint = randomRespawnLocation()
        }

        node.addListener(ProjectileBlockHitEvent::class.java) {
            it.entity.remove()
        }

        node.addListener(FinalDamageEvent::class.java) {
            val entity = it.entity
            if (entity !is Player)
                return@addListener
            if (it.damageType.directEntity is Arrow) {
                it.damage = 1000F
            }
            if (it.doesKillEntity()) {
                sendDeathMessage(entity, it.damageType.entity as? Player, it.damageType)
                val killer = it.damageType.entity
                if (killer is Player && killer != entity) {
                    onKill(killer)
                }
            }
        }
    }

    override fun onUpdate() {

    }

    override fun onEnd() {
        val winner = game.playerData.values.maxBy { it.score }.player
        game.instance.showTitle(Title.title(winner.name.color(NamedTextColor.GREEN), Component.text("Has won.", NamedTextColor.GRAY)))
        game.players.forEach {
            sidebar.removeViewer(it)
        }
    }

    private fun sendDeathMessage(victim: Player, killer: Player?, damageType: CustomDamageType) {
        val skull = Component.text("☠", NamedTextColor.RED)

        val miniMessage = if (killer == null) {
            when {
                damageType.isOutOfWorld -> "<victim> fell out of the world."
                damageType.isProjectile -> "<victim> got shot."
                else -> "<victim> died."
            }
        } else {
            if (victim == killer) {
                when {
                    damageType.isOutOfWorld -> "<victim> jumped into the void."
                    damageType.isProjectile -> "<victim> shot themselves."
                    else -> "<victim> killed themselves."
                }
            } else {
                when {
                    damageType.isOutOfWorld -> "<victim> got thrown in the void by <killer>."
                    damageType.isProjectile -> "<victim> got shot by <killer>."
                    else -> "<victim> got killed by <killer>."
                }
            }
        }

        val message = MiniMessage.miniMessage().deserialize(miniMessage,
            Placeholder.component("victim", victim.name.color(NamedTextColor.GOLD)),
            Placeholder.component("killer", killer?.name?.color(NamedTextColor.GOLD) ?: Component.empty()),
            ).color(NamedTextColor.GRAY)

        val component = Component.text().append(
            skull,
            Component.space(),
            message
        ).build()

        game.instance.sendMessage(component)
    }

    private fun spawn(player: Player, teleport: Boolean = true) {
        if (teleport) {
            player.teleport(randomRespawnLocation())
        }
        player.inventory.clear()
        player.inventory.setItemStack(0, sword)
        player.inventory.setItemStack(1, bow)
        player.inventory.setItemStack(8, arrow)
    }

    private fun randomRespawnLocation(): Pos {
        return (game.playingField.random().add(0.0, 1.0, 0.0) as Pos).withLookAt(Pos(0.0, 64.0, 0.0))
    }

    private fun onKill(killer: Player) {
        if (killer.inventory.itemStacks.any { it.material() == Material.ARROW }) {
            killer.inventory.addItemStack(arrow)
        } else {
            killer.inventory.setItemStack(8, arrow)
        }
        killer.sendActionBar(Component.text("+1", NamedTextColor.GOLD))
        game.playerData[killer]?.addScore()
        updateLines()
        if (game.playerData[killer]?.score == 20) {
            end()
        }
    }

    private fun createLines() {
        val length = min(game.players.size, 14) + 2
        for (i in 1..length) {
            sidebar.createLine(Sidebar.ScoreboardLine((i - 1).toString(), Component.empty(), length - i))
        }
        updateLines()
    }

    private fun updateLines() {
        var i = 1
        game.playerData.values.sortedByDescending { it.score }.take(14).forEach {
            val text = Component.text().append(
                it.player.name.color(NamedTextColor.GOLD),
                Component.text(": ", NamedTextColor.GRAY),
                Component.text(it.score, NamedTextColor.GOLD)
            ).build()
            sidebar.updateLineContent(i.toString(), text)
            i++
        }
    }

}