package com.simplymerlin.minigameserver

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.simplymerlin.minigameserver.command.SetGameCommand
import com.simplymerlin.minigameserver.command.StartCommand
import com.simplymerlin.minigameserver.command.StuckCommand
import com.simplymerlin.minigameserver.core.LoggerManager.debug
import com.simplymerlin.minigameserver.core.Minigame
import com.simplymerlin.minigameserver.minigame.blockparty.BlockPartyGame
import com.simplymerlin.minigameserver.minigame.maptest.MapTestGame
import com.simplymerlin.minigameserver.minigame.oitc.OneInTheChamberGame
import io.github.bloepiloepi.pvp.PvpExtension
import net.hollowcube.polar.PolarLoader
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import net.minestom.server.MinecraftServer
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.event.EventListener
import net.minestom.server.event.player.*
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.extras.MojangAuth
import net.minestom.server.instance.block.Block
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag
import net.minestom.server.timer.ExecutionType
import org.slf4j.LoggerFactory

class Server {

    companion object {
        val HUB_SPAWN = Pos(-264.5, -29.0, 108.5)
    }

    private val logger = ComponentLogger.logger(this::class.java)

    private val minecraftServer = MinecraftServer.init()
    private val instanceManager = MinecraftServer.getInstanceManager()
    private val globalEventHandler = MinecraftServer.getGlobalEventHandler()

    val hub = instanceManager.createInstanceContainer(FullBrightDimension.dimension)

    val games = listOf(
        BlockPartyGame(instanceManager.createInstanceContainer(FullBrightDimension.dimension), this),
        OneInTheChamberGame(instanceManager.createInstanceContainer(FullBrightDimension.dimension), this),
        MapTestGame(instanceManager.createInstanceContainer(FullBrightDimension.dimension), this),
    )

    var currentGame: Minigame = games[0]
        set(value) {
            field = value
            bossBar.name(
                text().append(
                    text("Current minigame: ", TextColor.color(0xFFFF00)),
                    field.displayName
                ).build()
            )
            logger.info("${field.name} has been selected.")
        }

    val bossBar = BossBar.bossBar(
        text().append(
            text("Current minigame: ", TextColor.color(0xFFFF00)),
            currentGame.displayName
        ).build(),
        1F,
        BossBar.Color.WHITE,
        BossBar.Overlay.PROGRESS
    )

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
        MinecraftServer.getCommandManager().register(StuckCommand(this))

        logger.info("Setting up hub.")
        hub.chunkLoader = PolarLoader(this::class.java.getResourceAsStream("/worlds/hub.polar")!!)

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
            response.description = text("So many minigames!", NamedTextColor.AQUA)
        }
        globalEventHandler.addListener(PlayerLoginEvent::class.java) {
            val player = it.player
            player.gameMode = GameMode.ADVENTURE
            it.setSpawningInstance(hub)

            player.respawnPoint = HUB_SPAWN

            if (MinecraftServer.getConnectionManager().onlinePlayers.size == 1) {
                player.setTag(Tag.Boolean("leader"), true)
            }
        }

        globalEventHandler.addListener(PlayerLoginEvent::class.java) {
            val player = it.player
            Audiences.all().sendMessage(
                text().append(
                    text("+", TextColor.color(0x00FF00)),
                    Component.space(),
                    player.name.color(NamedTextColor.GRAY)
                )
            )
        }
        globalEventHandler.addListener(PlayerDisconnectEvent::class.java) {
            val player = it.player
            Audiences.all().sendMessage(
                text().append(
                    text("-", TextColor.color(0xFF0000)),
                    Component.space(),
                    player.name.color(NamedTextColor.GRAY)
                )
            )
        }

        globalEventHandler.addListener(PlayerChatEvent::class.java) { event ->
            event.setChatFormat {
                text().append(
                    it.player.displayName ?: it.player.name,
                    text(": "),
                    text(it.message)
                ).build()
            }
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
            EventListener.builder(PlayerUseItemEvent::class.java)
                .filter {
                    !it.player.itemInMainHand.isAir
                }
                .filter {
                    (it.player.itemInMainHand.getTag(Tag.String("handler_id")) ?: "null") == "select_game"
                }
                .handler {
                    openGameSelectionMenu(it.player)
                }.build()
        )

        //Bossbar
        globalEventHandler.addListener(PlayerSpawnEvent::class.java) {
            if (it.spawnInstance == hub) {
                it.player.showBossBar(bossBar)
            } else {
                it.player.hideBossBar(bossBar)
            }
        }

        globalEventHandler.addListener(
            EventListener.builder(PlayerMoveEvent::class.java)
                .filter {
                    it.player.instance == hub
                }
                .filter {
                    it.player.openInventory == null
                }
                .handler {
                    MinecraftServer.getSchedulerManager().buildTask {
                        if(hub.getBlock(it.player.position).id() == Block.NETHER_PORTAL.id()) {
                            it.player.velocity = it.player.position.direction().mul(-20.0)
                            Thread.sleep(500) // TODO: Little bit hacky
                            openGameSelectionMenu(it.player)
                        }
                    }.executionType(ExecutionType.ASYNC).schedule()
                }
                .build()
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
                    it.player.inventory.setItemStack(
                        0, ItemStack.of(Material.COMPASS)
                            .withDisplayName(text("Game selector").decoration(TextDecoration.ITALIC, false))
                            .withTag(Tag.String("handler_id"), "select_game")
                    )

                    it.player.inventory.setItemStack(
                        8, ItemStack.of(Material.GREEN_STAINED_GLASS_PANE)
                            .withDisplayName(
                                text("Start", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)
                            )
                            .withTag(Tag.String("handler_id"), "start_game")
                    )

                    openGameSelectionMenu(it.player)
                }.build()
        )
    }

    fun teleportAllToHub() {
        MinecraftServer.getConnectionManager().onlinePlayers.forEach {
            if (it.instance != hub) {
                it.setInstance(hub, HUB_SPAWN)
            }
            it.gameMode = GameMode.ADVENTURE
        }
    }

    private fun openGameSelectionMenu(player: Player) {
        val inventory = Inventory(InventoryType.CHEST_6_ROW, "Pick a game")
        games.forEachIndexed { i, game ->
            val displayName =
                game.displayName.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            val lore = game.displayDescription.map { component ->
                component.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .colorIfAbsent(NamedTextColor.GRAY)
            }
            inventory.setItemStack(
                i,
                ItemStack.of(game.icon).withDisplayName(displayName).withLore(lore)
            )
            inventory.addInventoryCondition { _, slot, _, inventoryConditionResult ->
                if (slot != i) return@addInventoryCondition
                currentGame = game
                player.closeInventory()
                player.sendMessage(
                    text().append(
                        game.displayName,
                        text(
                            " has been selected!",
                            NamedTextColor.GREEN
                        )
                    )
                )
                player.playSound(
                    Sound.sound(
                        Key.key("entity.experience_orb.pickup"),
                        Sound.Source.NEUTRAL,
                        1f,
                        1f
                    )
                )
                inventoryConditionResult.isCancel = false
            }
        }
        player.openInventory(inventory)
    }

}