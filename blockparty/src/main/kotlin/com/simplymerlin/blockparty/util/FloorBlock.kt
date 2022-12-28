package com.simplymerlin.blockparty.util

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.minestom.server.instance.block.Block
import net.minestom.server.item.Material

enum class FloorBlock(val material: Material, val color: TextColor, val displayName: String,
                      val block: Block = material.block()) {

    ORANGE(Material.ORANGE_CONCRETE, NamedTextColor.GOLD, "Orange"),
    MAGENTA(Material.MAGENTA_CONCRETE, NamedTextColor.LIGHT_PURPLE, "Magenta"),
    LIGHT_BLUE(Material.LIGHT_BLUE_CONCRETE, NamedTextColor.BLUE, "Light Blue"),
    YELLOW(Material.YELLOW_CONCRETE, NamedTextColor.YELLOW, "Yellow"),
    LIME(Material.LIME_CONCRETE, NamedTextColor.GREEN, "Lime"),
    PINK(Material.PINK_CONCRETE, NamedTextColor.LIGHT_PURPLE, "Pink"),
    GRAY(Material.GRAY_CONCRETE, NamedTextColor.DARK_GRAY, "Gray"),
    LIGHT_GRAY(Material.LIGHT_GRAY_CONCRETE, NamedTextColor.GRAY, "Light Gray"),
    CYAN(Material.CYAN_CONCRETE, NamedTextColor.DARK_AQUA, "Cyan"),
    PURPLE(Material.PURPLE_CONCRETE, NamedTextColor.DARK_PURPLE, "Purple"),
    BLUE(Material.BLUE_CONCRETE, NamedTextColor.BLUE, "Blue"),
    BROWN(Material.BROWN_CONCRETE, TextColor.color(0x964B00), "Brown"),
    GREEN(Material.GREEN_CONCRETE, NamedTextColor.DARK_GREEN, "Green"),

}