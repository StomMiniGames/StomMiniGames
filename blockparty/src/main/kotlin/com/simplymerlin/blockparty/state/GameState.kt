package com.simplymerlin.blockparty.state

import net.minestom.server.MinecraftServer
import net.minestom.server.timer.Task
import net.minestom.server.timer.TaskSchedule
import net.minikloon.fsmgasm.State

abstract class GameState : State() {

    val tasks: MutableList<Task> = mutableListOf()

    override fun start() {
        super.start()
        registerTask(MinecraftServer.getSchedulerManager().scheduleTask({
            onSecond()
        }, TaskSchedule.immediate(), TaskSchedule.seconds(1)))
    }

    protected open fun onSecond() {

    }

    override fun end() {
        super.end()
        tasks.forEach { it.cancel() }
        tasks.clear()
    }

    fun registerTask(task: Task) {
        tasks.add(task)
    }

}