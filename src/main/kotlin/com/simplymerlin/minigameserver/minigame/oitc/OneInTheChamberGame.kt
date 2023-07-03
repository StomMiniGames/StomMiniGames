package com.simplymerlin.minigameserver.minigame.oitc

import com.simplymerlin.minigameserver.Server
import com.simplymerlin.minigameserver.core.Minigame
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.item.Material

class OneInTheChamberGame(instance: InstanceContainer, server: Server) : Minigame(instance, server) {
    override val name = "One In The Chamber"
    override val displayName = Component.text("One In The Chamber", NamedTextColor.RED, TextDecoration.BOLD)
    override val displayDescription = listOf(Component.text("Beat your friends in some classic OITC!", NamedTextColor.RED))
    override val icon: Material = Material.BOW



}