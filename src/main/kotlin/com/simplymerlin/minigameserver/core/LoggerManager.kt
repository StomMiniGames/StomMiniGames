package com.simplymerlin.minigameserver.core

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

object LoggerManager {

	fun logger(klass: KClass<out Any>): ComponentLogger {
		return ComponentLogger.logger(klass.java)
	}

	fun ComponentLogger.debug(msg: Any) {
		debug(msg.toString())
	}
}