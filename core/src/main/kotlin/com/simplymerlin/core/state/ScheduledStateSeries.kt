package com.simplymerlin.core.state

import com.simplymerlin.fsmchamp.StateSeries
import net.minestom.server.MinecraftServer
import net.minestom.server.timer.Task
import net.minestom.server.timer.TaskSchedule

class ScheduledStateSeries : StateSeries() {

    lateinit var task: Task

    override fun onStart() {
        super.onStart()
        task = MinecraftServer.getSchedulerManager().scheduleTask({
            update()
        }, TaskSchedule.immediate(), TaskSchedule.tick(20))
    }

    override fun onEnd() {
        super.onEnd()
        task.cancel()
    }

}