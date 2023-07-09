package com.simplymerlin.minigameserver.core

import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.trait.CancellableEvent
import kotlin.reflect.KClass

fun <T: CancellableEvent> KClass<T>.cancel(node: EventNode<Event>) {
    node.addListener(this.java) {
        it.isCancelled = true
    }
}