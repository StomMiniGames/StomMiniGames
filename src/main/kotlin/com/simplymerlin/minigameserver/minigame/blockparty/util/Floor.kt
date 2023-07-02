package com.simplymerlin.minigameserver.minigame.blockparty.util

import net.minestom.server.coordinate.Point
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.batch.AbsoluteBlockBatch

enum class Floor {

    RANDOM {
        override fun applyFloor(floor: List<Point>, instance: InstanceContainer) {
            val batch = AbsoluteBlockBatch()
            floor.forEach {
                batch.setBlock(it, FloorBlock.values().random().block)
            }
            batch.apply(instance) {
                println("Batch place complete!")
            }
        }
    },
    DIAGONAL {
        override fun applyFloor(floor: List<Point>, instance: InstanceContainer) {
            val batch = AbsoluteBlockBatch()
            val blocks = FloorBlock.values().apply {
                shuffle()
            }
            floor.forEach {
                batch.setBlock(it, blocks[(it.blockX() - it.blockZ()).mod(blocks.size)].block)
            }
            batch.apply(instance) {
                println("Batch place complete!")
            }
        }
    },
    BLOCKS {
        override fun applyFloor(floor: List<Point>, instance: InstanceContainer) {
            val batch = AbsoluteBlockBatch()
            val blocks = FloorBlock.values().apply {
                shuffle()
            }
            floor.forEach {
                batch.setBlock(it, blocks[( (it.blockX() * it.blockZ()) / 2).mod(blocks.size)].block)
            }
            batch.apply(instance) {
                println("Batch place complete!")
            }
        }
    };

    abstract fun applyFloor(floor: List<Point>, instance: InstanceContainer)

}