package com.simplymerlin.minigameserver.core.world

import java.io.File

interface MapSelectionStrategy {

	fun selectMapFile(list: List<File>): File
}