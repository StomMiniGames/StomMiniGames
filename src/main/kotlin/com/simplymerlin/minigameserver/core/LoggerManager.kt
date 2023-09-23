package com.simplymerlin.minigameserver.core

import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import kotlin.reflect.KClass

object LoggerManager {

	fun logger(klass: KClass<out Any>): ComponentLogger {
		return ComponentLogger.logger(klass.java)
	}

	fun ComponentLogger.debug(msg: Any) {
		debug(msg.toString())
	}
}