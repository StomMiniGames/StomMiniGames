package com.simplymerlin.blockparty

import com.simplymerlin.blockparty.event.ConnectionEvents
import net.minestom.server.MinecraftServer
import net.minestom.server.extras.MojangAuth
import net.minestom.server.utils.NamespaceID
import net.minestom.server.world.DimensionType

class BlockParty {

    private val minecraftServer  = MinecraftServer.init()
    private val instanceManager = MinecraftServer.getInstanceManager()

    private val fullbrightDimension = DimensionType.builder(NamespaceID.from("minestom:full_bright"))
        .ambientLight(2.0f)
        .build()
        .also {
            MinecraftServer.getDimensionTypeManager().addDimension(it)
        }
    private val instance = instanceManager.createInstanceContainer(fullbrightDimension)

    init {
        MojangAuth.init()
        initialiseEvents()

        BlockPartyGame(instance)

        minecraftServer.start("0.0.0.0", 25565)
    }

    private fun initialiseEvents() {
        ConnectionEvents()
    }

}