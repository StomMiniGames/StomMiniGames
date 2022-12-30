package com.simplymerlin.blockparty.util

import net.minestom.server.coordinate.Point
import net.minestom.server.instance.InstanceContainer

enum class Floor {

    RANDOM {
        override fun applyFloor(floor: List<Point>, instance: InstanceContainer) {
            floor.forEach {
                instance.setBlock(it, FloorBlock.values().random().block)
            }
        }
    },
    DIAGONAL {
        override fun applyFloor(floor: List<Point>, instance: InstanceContainer) {
            val blocks = FloorBlock.values().apply {
                shuffle()
            }
            floor.forEach {
                instance.setBlock(it, blocks[(it.blockX()-it.blockZ()).mod(blocks.size)].block)
            }
        }
    },
    BLOCKS {
        override fun applyFloor(floor: List<Point>, instance: InstanceContainer) {
            val blocks = FloorBlock.values().apply {
                shuffle()
            }
            floor.forEach {
                instance.setBlock(it, blocks[((it.blockX()*it.blockZ())/2).mod(blocks.size)].block)
            }
        }
    };

    abstract fun applyFloor(floor: List<Point>, instance: InstanceContainer)

}