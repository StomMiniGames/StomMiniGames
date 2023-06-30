package com.simplymerlin.minigameserver.core.state

import com.simplymerlin.fsmchamp.State
import net.minestom.server.MinecraftServer
import net.minestom.server.event.EventNode
import net.minestom.server.timer.Task

abstract class GameState : State() {

    val tasks: MutableList<Task> = mutableListOf()
    val node = EventNode.all(javaClass.name)

    override fun start() {
        super.start()
        MinecraftServer.getGlobalEventHandler().addChild(node)
    }

    override fun end() {
        super.end()
        tasks.forEach { it.cancel() }
        tasks.clear()
        MinecraftServer.getGlobalEventHandler().removeChild(node)
    }

    fun registerTask(task: Task) {
        tasks.add(task)
    }

}