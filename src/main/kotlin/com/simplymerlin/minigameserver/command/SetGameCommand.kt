package com.simplymerlin.minigameserver.command

import com.simplymerlin.minigameserver.Server
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType

class SetGameCommand(
    private val server: Server
) : Command("setgame") {

    init {
        setDefaultExecutor { sender, context ->
            sender.sendMessage("Usage /setgame <game>")
        }

        val gameArgument = ArgumentType.String("game")

        addSyntax({ sender, context ->
            val game = context.get(gameArgument)
            if (server.games.map { it.name.lowercase() }.contains(game.lowercase())) {
                server.games.forEach {
                    if (it.name.lowercase() == game.lowercase()) {
                        server.currentGame = it
                        sender.sendMessage("Chosen ${server.currentGame.name}!")
                        return@forEach
                    }
                }
            } else {
                sender.sendMessage("game $game not found!")
            }
        }, gameArgument)
    }

}