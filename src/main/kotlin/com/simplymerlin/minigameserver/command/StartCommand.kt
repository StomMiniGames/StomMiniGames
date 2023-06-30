package com.simplymerlin.minigameserver.command

import com.simplymerlin.minigameserver.Server
import net.minestom.server.command.builder.Command

class StartCommand(
    val server: Server
) : Command("start") {

    init {
        setDefaultExecutor { sender, context ->
            server.start()
        }
    }

}