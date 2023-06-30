package com.simplymerlin.minigameserver.minigame.blockparty.event

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerLoginEvent

class ConnectionEvents() {

    private val eventHandler = MinecraftServer.getGlobalEventHandler()

    init {
        eventHandler.addListener(PlayerLoginEvent::class.java) {
            val player = it.player
            Audiences.all().sendMessage(
                Component.text()
                    .append(Component.text("+", TextColor.color(0x00FF00)))
                    .append(Component.space())
                    .append(player.name.color(NamedTextColor.GRAY))
            )
        }
        eventHandler.addListener(PlayerDisconnectEvent::class.java) {
            val player = it.player
            Audiences.all().sendMessage(
                Component.text()
                    .append(Component.text("-", TextColor.color(0xFF0000)))
                    .append(Component.space())
                    .append(player.name.color(NamedTextColor.GRAY))
            )
        }
    }
}