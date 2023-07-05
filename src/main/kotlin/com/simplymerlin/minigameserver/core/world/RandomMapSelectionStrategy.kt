package com.simplymerlin.minigameserver.core.world

import java.io.File

class RandomMapSelectionStrategy : MapSelectionStrategy {
	override fun selectMapFile(list: List<File>): File?  = list.randomOrNull()
}