package com.simplymerlin.minigameserver.core

import com.simplymerlin.minigameserver.Server
import net.minestom.server.instance.InstanceContainer

abstract class Minigame(val instance: InstanceContainer, val server: Server) {

    abstract val name: String

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