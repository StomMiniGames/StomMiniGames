package com.simplymerlin.minigameserver.core.world

import java.io.File
import java.util.jar.JarFile

object ResourceNavigator {

	/**
	 * Returns a list of files in the resources folder
	 * @param path The path in the resources folder, e.g. worlds/spleef
	 * (Note: no slash at beginning of end)
	 * @return a list of the files using the java.io.File class
	 */
	fun listResourceFilesOf(path: String): List<File> {
		val classLoader = Thread.currentThread().contextClassLoader
		val fileList = mutableListOf<File>()

		classLoader.getResources(path).toList().forEach { url ->
			when (url.protocol) {
				"file" -> {
					val directory = File(url.toURI())
					if (directory.isDirectory) {
						directory.listFiles()?.forEach {
							fileList.add(it)
						}
					}
				}
				"jar" -> {
					val jarPath = url.path.substringAfter("file:")
						.substringBeforeLast(".jar")
						.plus(".jar")

					val jarFile = JarFile(jarPath)
					val entries = jarFile.entries()

					while (entries.hasMoreElements()) {
						val entry = entries.nextElement()
						val entryPath = entry.name

						if (entryPath.startsWith(path) && !entry.isDirectory) {
							val fileName = File(entryPath).name
							fileList.add(File(fileName))
						}
					}
					jarFile.close()
				}
			}
		}
		return fileList
	}

}