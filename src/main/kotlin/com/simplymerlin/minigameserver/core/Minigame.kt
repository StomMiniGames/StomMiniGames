package com.simplymerlin.minigameserver.core

import com.simplymerlin.minigameserver.Server
import net.kyori.adventure.text.Component
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.item.Material

abstract class Minigame(val instance: InstanceContainer, val server: Server) {

    abstract val name: String

    abstract val displayName: Component

    abstract val displayDescription: List<Component>

    abstract val icon: Material

    var running = false
    open fun start() {
        if (running)
            return
        running = true
    }

    open fun clean() {
        if (running)
            return
    }

}