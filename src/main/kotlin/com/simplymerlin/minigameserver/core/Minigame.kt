package com.simplymerlin.minigameserver.core

import com.simplymerlin.minigameserver.Server
import com.simplymerlin.minigameserver.core.world.MapSelectionStrategy
import com.simplymerlin.minigameserver.core.world.RandomMapSelectionStrategy
import com.simplymerlin.minigameserver.core.world.ResourceNavigator
import net.hollowcube.polar.PolarLoader
import net.kyori.adventure.text.Component
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.item.Material

abstract class Minigame(val instance: InstanceContainer, val server: Server) {

    abstract val name: String
    abstract val displayName: Component
    abstract val displayDescription: List<Component>
    abstract val icon: Material

    internal open val mapSelectionStrategy: MapSelectionStrategy = RandomMapSelectionStrategy()

    var running = false
    open fun start() {
        if (running)
            return
        val mapFiles = ResourceNavigator.listResourceFilesOf("worlds/$name")
            .filter { it.path.endsWith(".polar") }
        val mapFile = mapSelectionStrategy.selectMapFile(mapFiles)
        if(mapFile != null) {
            val loader = PolarLoader(mapFile.toPath())
            instance.chunkLoader = loader
        }
        running = true
    }

    open fun clean() {
        if (running)
            return
    }

}