package com.simplymerlin.minigameserver

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.simplymerlin.minigameserver.command.SetGameCommand
import com.simplymerlin.minigameserver.command.StartCommand
import com.simplymerlin.minigameserver.core.Minigame
import com.simplymerlin.minigameserver.minigame.blockparty.BlockPartyGame
import com.simplymerlin.minigameserver.minigame.oitc.OneInTheChamberGame
import io.github.bloepiloepi.pvp.PvpExtension
import com.simplymerlin.minigameserver.minigame.maptest.MapTestGame
import com.simplymerlin.minigameserver.minigame.spleef.SpleefGame
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import net.minestom.server.MinecraftServer
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.event.EventListener
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.event.player.PlayerUseItemEvent
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.extras.MojangAuth
import net.minestom.server.instance.block.Block
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag
import org.slf4j.LoggerFactory

class Server {

    private val logger = ComponentLogger.logger(this::class.java)

    private val minecraftServer  = MinecraftServer.init()
    private val instanceManager = MinecraftServer.getInstanceManager()
    private val globalEventHandler = MinecraftServer.getGlobalEventHandler()

    private val hub = instanceManager.createInstanceContainer(FullBrightDimension.dimension)

    val games = listOf(
        BlockPartyGame(instanceManager.createInstanceContainer(FullBrightDimension.dimension), this),
        SpleefGame(instanceManager.createInstanceContainer(FullBrightDimension.dimension), this),
        OneInTheChamberGame(instanceManager.createInstanceContainer(FullBrightDimension.dimension), this),
        MapTestGame(instanceManager.createInstanceContainer(FullBrightDimension.dimension), this),
    )

    var currentGame: Minigame = games[0]
        set(value) {
            field = value
            logger.info("${field.name} has been selected.")
        }

    init {
        val level = Level.valueOf(System.getenv("LOG_LEVEL") ?: "INFO")
        val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
        rootLogger.level = level

        logger.info("Stomminigames is starting up...")
        logger.info("Stomminigames is running on ${MinecraftServer.VERSION_NAME} (${MinecraftServer.PROTOCOL_VERSION}).")
        logger.info("${games.size} minigames have been found.")
        val startTime = System.currentTimeMillis()
        MojangAuth.init()
        PvpExtension.init()
        initialiseEvents()

        logger.info("Registering commands.")
        MinecraftServer.getCommandManager().register(StartCommand(this))
        MinecraftServer.getCommandManager().register(SetGameCommand(this))

        logger.info("Setting up hub.")
        hub.setBlock(0, 64, 0, Block.STONE)

        minecraftServer.start("0.0.0.0", 25565)
        logger.info("Startup complete in ${System.currentTimeMillis() - startTime}ms")
    }

    fun start() {
        currentGame.start()
    }

    private fun initialiseEvents() {
        logger.info("Registering events.")
        globalEventHandler.addListener(ServerListPingEvent::class.java) {
            val response = it.responseData
            response.description = Component.text("So many minigames!", NamedTextColor.AQUA)
        }
        globalEventHandler.addListener(PlayerLoginEvent::class.java) { event ->
            val player = event.player
            player.gameMode = GameMode.ADVENTURE
            event.setSpawningInstance(hub)

            player.respawnPoint = Pos(0.0, 65.0, 0.0)

            if(MinecraftServer.getConnectionManager().onlinePlayers.size == 1) {
                player.setTag(Tag.Boolean("leader"), true)
            }
        }

        globalEventHandler.addListener(PlayerLoginEvent::class.java) {
            val player = it.player
            Audiences.all().sendMessage(
                Component.text()
                    .append(Component.text("+", TextColor.color(0x00FF00)))
                    .append(Component.space())
                    .append(player.name.color(NamedTextColor.GRAY))
            )
        }
        globalEventHandler.addListener(PlayerDisconnectEvent::class.java) {
            val player = it.player
            Audiences.all().sendMessage(
                Component.text()
                    .append(Component.text("-", TextColor.color(0xFF0000)))
                    .append(Component.space())
                    .append(player.name.color(NamedTextColor.GRAY))
            )
        }

        globalEventHandler.addListener(
            EventListener.builder(PlayerUseItemEvent::class.java)
                .filter {
                    !it.player.itemInMainHand.isAir
                }
                .filter {
                    (it.player.itemInMainHand.getTag(Tag.String("handler_id")) ?: "null") == "start_game"
                }
                .handler {
                    currentGame.start()
                }.build()
        )

        globalEventHandler.addListener(
            EventListener.builder(PlayerSpawnEvent::class.java)
                .filter {
                    it.player.getTag(Tag.Boolean("leader")) ?: false
                }
                .filter {
                    it.spawnInstance == hub
                }
                .handler {
                    it.player.inventory.setItemStack(0, ItemStack.of(Material.GREEN_STAINED_GLASS_PANE)
                        .withDisplayName(Component.text("Start", NamedTextColor.GREEN))
                        .withTag(Tag.String("handler_id"), "start_game"))

                    val inventory = Inventory(InventoryType.CHEST_6_ROW, "Pick a game")
                    games.forEachIndexed { i, game ->
                        val displayName = game.displayName.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                        val lore = game.displayDescription.map { component ->
                            component.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE).colorIfAbsent(NamedTextColor.GRAY)
                        }
                        inventory.setItemStack(
                            i,
                            ItemStack.of(game.icon).withDisplayName(displayName).withLore(lore)
                        )
                        inventory.addInventoryCondition { _, slot, _, inventoryConditionResult ->
                            if (slot != i) return@addInventoryCondition
                            currentGame = game
                            it.player.closeInventory()
                            it.player.sendMessage(game.displayName.append(Component.text(" has been selected", NamedTextColor.GREEN)))
                            it.player.playSound(Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.NEUTRAL, 1f, 1f))
                            inventoryConditionResult.isCancel = false
                        }
                    }
                it.player.openInventory(inventory)
            }.build())
    }

    fun teleportAllToHub() {
        MinecraftServer.getConnectionManager().onlinePlayers.forEach {
            if (it.instance != hub) {
                it.setInstance(hub, Pos(0.0, 65.0, 0.0))
            }
            it.gameMode = GameMode.ADVENTURE
        }
    }

}