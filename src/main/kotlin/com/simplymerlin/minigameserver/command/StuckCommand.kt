package com.simplymerlin.minigameserver.command

import com.simplymerlin.minigameserver.Server
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.builder.Command
import net.minestom.server.entity.Player

class StuckCommand(val server: Server) : Command("stuck") {

	init {
		setDefaultExecutor { sender, _ ->
			if(sender is Player) {
				if(sender.instance == server.hub) {
					sender.teleport(Server.HUB_SPAWN)
					sender.sendMessage(Component.text("You have been teleported back to spawn", NamedTextColor.GREEN))
				}else {
					sender.sendMessage(Component.text("You must be in the hub", NamedTextColor.RED))
				}
			}else {
				sender.sendMessage(Component.text("This command is only for players", NamedTextColor.RED))
			}
		}
	}
}