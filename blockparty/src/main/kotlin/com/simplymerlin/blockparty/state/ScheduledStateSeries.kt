package com.simplymerlin.blockparty.state

import net.minestom.server.MinecraftServer
import net.minestom.server.timer.Task
import net.minestom.server.timer.TaskSchedule
import net.minikloon.fsmgasm.StateSeries

class ScheduledStateSeries : StateSeries() {

    lateinit var task: Task

    override fun onStart() {
        super.onStart()
        task = MinecraftServer.getSchedulerManager().scheduleTask({
            update()
        }, TaskSchedule.immediate(), TaskSchedule.tick(1))
    }

    override fun onEnd() {
        super.onEnd()
        task.cancel()
    }

}