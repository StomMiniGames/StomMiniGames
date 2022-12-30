package com.simplymerlin.minigameserver

import net.minestom.server.MinecraftServer
import net.minestom.server.utils.NamespaceID
import net.minestom.server.world.DimensionType

object FullBrightDimension {

    val dimension = DimensionType.builder(NamespaceID.from("minestom:full_bright"))
        .ambientLight(2.0f)
        .build()
        .also {
            MinecraftServer.getDimensionTypeManager().addDimension(it)
        }

}