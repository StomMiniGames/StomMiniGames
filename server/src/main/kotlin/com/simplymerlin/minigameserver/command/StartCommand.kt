package com.simplymerlin.minigameserver.command

import com.simplymerlin.minigameserver.ServerImpl
import net.minestom.server.command.builder.Command

class StartCommand(
    val server: ServerImpl
) : Command("start") {

    init {
        setDefaultExecutor { sender, context ->
            server.start()
        }
    }

}