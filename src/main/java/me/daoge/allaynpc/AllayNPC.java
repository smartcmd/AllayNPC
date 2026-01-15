package me.daoge.allaynpc;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.daoge.allaynpc.command.ANPCCommand;
import me.daoge.allaynpc.listener.NPCEventListener;
import me.daoge.allaynpc.manager.DialogManager;
import me.daoge.allaynpc.manager.NPCManager;
import me.daoge.allaynpc.manager.SkinManager;
import me.daoge.allaynpc.npc.NPC;
import org.allaymc.api.plugin.Plugin;
import org.allaymc.api.registry.Registries;
import org.allaymc.api.server.Server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * AllayNPC - Main plugin class
 * Provides complete NPC creation, management and interaction functionality for AllayMC
 *
 * @author daoge_cmd
 */
@Slf4j
public class AllayNPC extends Plugin {

    /**
     * NPC tick update interval in ticks (1 ticks = 50ms)
     */
    private static final int NPC_UPDATE_INTERVAL = 1;

    @Getter
    private static AllayNPC instance;

    @Getter
    private SkinManager skinManager;

    @Getter
    private DialogManager dialogManager;

    @Getter
    private NPCManager npcManager;

    @Getter
    private NPCEventListener eventListener;

    /**
     * Current server tick counter
     */
    private long currentTick = 0;

    @Override
    public void onLoad() {
        instance = this;
        log.info("AllayNPC is loading...");

        // Create data directory structure
        createDataDirectories();
    }

    @Override
    public void onEnable() {
        log.info("AllayNPC is enabling...");

        // Initialize managers
        initManagers();

        // Register commands
        registerCommands();

        // Register event listeners
        registerEventListeners();

        // Start NPC update task
        startNPCUpdateTask();

        log.info("AllayNPC has been enabled successfully!");
    }

    @Override
    public void onDisable() {
        log.info("AllayNPC is disabling...");

        // Remove all NPCs
        if (npcManager != null) {
            npcManager.removeAllNPCs();
        }

        log.info("AllayNPC has been disabled!");
    }

    /**
     * Create plugin data directory structure
     */
    private void createDataDirectories() {
        Path dataFolder = getPluginContainer().dataFolder();

        try {
            // Create skins directory
            Path skinsDir = dataFolder.resolve("skins");
            if (!Files.exists(skinsDir)) {
                Files.createDirectories(skinsDir);
                log.info("Created skins directory");
            }

            // Create dialogs directory
            Path dialogsDir = dataFolder.resolve("dialogs");
            if (!Files.exists(dialogsDir)) {
                Files.createDirectories(dialogsDir);
                log.info("Created dialogs directory");
            }

            // Create npcs directory
            Path npcsDir = dataFolder.resolve("npcs");
            if (!Files.exists(npcsDir)) {
                Files.createDirectories(npcsDir);
                log.info("Created npcs directory");
            }

            // Create lang directory
            Path langDir = dataFolder.resolve("lang");
            if (!Files.exists(langDir)) {
                Files.createDirectories(langDir);
                log.info("Created lang directory");
            }

        } catch (IOException e) {
            log.error("Failed to create data directories", e);
        }
    }

    /**
     * Initialize managers
     */
    private void initManagers() {
        Path dataFolder = getPluginContainer().dataFolder();

        // Initialize skin manager
        skinManager = new SkinManager(dataFolder.resolve("skins"));
        skinManager.loadAllSkins();
        log.info("Loaded {} skins", skinManager.getSkinCount());

        // Initialize dialog manager
        dialogManager = new DialogManager(dataFolder.resolve("dialogs"));
        dialogManager.loadAllDialogs();
        log.info("Loaded {} dialogs", dialogManager.getDialogCount());

        // Initialize NPC manager
        npcManager = new NPCManager(dataFolder.resolve("npcs"));
        npcManager.loadAllNPCConfigs();
        log.info("Loaded {} NPC configs", npcManager.getNPCConfigCount());
    }

    /**
     * Register commands
     */
    private void registerCommands() {
        Registries.COMMANDS.register(new ANPCCommand());
        log.info("Registered /anpc command");
    }

    /**
     * Register event listeners
     */
    private void registerEventListeners() {
        eventListener = new NPCEventListener();
        Server.getInstance().getEventBus().registerListener(eventListener);
        log.info("Registered event listeners");
    }

    /**
     * Start NPC update task for look-at-player and emotes
     */
    private void startNPCUpdateTask() {
        Server.getInstance().getScheduler().scheduleRepeating(this, () -> {
            currentTick += NPC_UPDATE_INTERVAL;
            updateNPCs();
        }, NPC_UPDATE_INTERVAL);

        log.info("Started NPC update task (interval: {} ticks)", NPC_UPDATE_INTERVAL);
    }

    /**
     * Update all NPCs (look-at-player and emotes)
     */
    private void updateNPCs() {
        for (NPC npc : npcManager.getSpawnedNPCs()) {
            try {
                // Update look-at-player
                npc.lookAtNearestPlayer();

                // Check and play emotes
                if (npc.shouldPlayEmote(currentTick)) {
                    npc.playEmote();
                }
            } catch (Exception e) {
                log.warn("Error updating NPC {}: {}", npc.getName(), e.getMessage());
            }
        }

        // Cleanup expired cooldowns every minute (1200 ticks)
        if (currentTick % 1200 == 0) {
            npcManager.cleanupCooldowns();
        }
    }

    /**
     * Reload plugin configuration
     */
    public void reload() {
        log.info("Reloading AllayNPC...");

        // Remove all NPCs
        npcManager.removeAllNPCs();

        // Reload skins
        skinManager.loadAllSkins();
        log.info("Reloaded {} skins", skinManager.getSkinCount());

        // Reload dialogs
        dialogManager.loadAllDialogs();
        log.info("Reloaded {} dialogs", dialogManager.getDialogCount());

        // Reload NPC configs
        npcManager.loadAllNPCConfigs();
        log.info("Reloaded {} NPC configs", npcManager.getNPCConfigCount());

        // Respawn all NPCs
        npcManager.spawnAllNPCs();

        log.info("AllayNPC reloaded successfully!");
    }
}
