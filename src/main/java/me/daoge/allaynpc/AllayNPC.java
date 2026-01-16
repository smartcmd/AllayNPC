package me.daoge.allaynpc;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.daoge.allaynpc.command.ANPCCommand;
import me.daoge.allaynpc.i18n.I18nKeys;
import me.daoge.allaynpc.listener.NPCEventListener;
import me.daoge.allaynpc.manager.CapeManager;
import me.daoge.allaynpc.manager.DialogManager;
import me.daoge.allaynpc.manager.NPCManager;
import me.daoge.allaynpc.manager.SkinManager;
import org.allaymc.api.message.I18n;
import org.allaymc.api.plugin.Plugin;
import org.allaymc.api.registry.Registries;
import org.allaymc.api.server.Server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * AllayNPC - Main plugin class
 * Provides complete NPC creation, management and interaction functionality for AllayMC
 * <p>
 * Thread-safety: Each NPC schedules its own update task on its dimension's scheduler,
 * ensuring all NPC updates run on the same thread as world events. This avoids race
 * conditions between the server thread and world threads.
 *
 * @author daoge_cmd
 */
@Slf4j
public class AllayNPC extends Plugin {

    /**
     * Cooldown cleanup interval in ticks (1 minute = 1200 ticks)
     */
    private static final int COOLDOWN_CLEANUP_INTERVAL = 1200;

    @Getter
    private static AllayNPC instance;

    @Getter
    private SkinManager skinManager;

    @Getter
    private CapeManager capeManager;

    @Getter
    private DialogManager dialogManager;

    @Getter
    private NPCManager npcManager;

    @Getter
    private NPCEventListener eventListener;

    @Override
    public void onLoad() {
        instance = this;
        log.info(I18n.get().tr(I18nKeys.PLUGIN_LOADING));

        // Create data directory structure
        createDataDirectories();
    }

    @Override
    public void onEnable() {
        log.info(I18n.get().tr(I18nKeys.PLUGIN_ENABLING));

        // Initialize managers
        initManagers();

        // Register commands
        registerCommands();

        // Register event listeners
        registerEventListeners();

        // Start cooldown cleanup task
        startCooldownCleanupTask();

        // Spawn NPCs for already loaded worlds
        spawnNPCsForLoadedWorlds();

        log.info(I18n.get().tr(I18nKeys.PLUGIN_ENABLED));
    }

    @Override
    public void onDisable() {
        log.info(I18n.get().tr(I18nKeys.PLUGIN_DISABLING));

        // Remove all NPCs
        if (npcManager != null) {
            npcManager.removeAllNPCs();
        }

        log.info(I18n.get().tr(I18nKeys.PLUGIN_DISABLED));
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
                log.info(I18n.get().tr(I18nKeys.DIRECTORY_SKINS_CREATED));
            }

            // Create capes directory
            Path capesDir = dataFolder.resolve("capes");
            if (!Files.exists(capesDir)) {
                Files.createDirectories(capesDir);
                log.info(I18n.get().tr(I18nKeys.DIRECTORY_CAPES_CREATED));
            }

            // Create dialogs directory
            Path dialogsDir = dataFolder.resolve("dialogs");
            if (!Files.exists(dialogsDir)) {
                Files.createDirectories(dialogsDir);
                log.info(I18n.get().tr(I18nKeys.DIRECTORY_DIALOGS_CREATED));
            }

            // Create npcs directory
            Path npcsDir = dataFolder.resolve("npcs");
            if (!Files.exists(npcsDir)) {
                Files.createDirectories(npcsDir);
                log.info(I18n.get().tr(I18nKeys.DIRECTORY_NPCS_CREATED));
            }

        } catch (IOException e) {
            log.error(I18n.get().tr(I18nKeys.DIRECTORY_CREATE_FAILED), e);
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
        log.info(I18n.get().tr(I18nKeys.MANAGER_SKINS_LOADED, skinManager.getSkinCount()));

        // Initialize cape manager
        capeManager = new CapeManager(dataFolder.resolve("capes"));
        capeManager.loadAllCapes();
        log.info(I18n.get().tr(I18nKeys.MANAGER_CAPES_LOADED, capeManager.getCapeCount()));

        // Initialize dialog manager
        dialogManager = new DialogManager(dataFolder.resolve("dialogs"));
        dialogManager.loadAllDialogs();
        log.info(I18n.get().tr(I18nKeys.MANAGER_DIALOGS_LOADED, dialogManager.getDialogCount()));

        // Initialize NPC manager
        npcManager = new NPCManager(dataFolder.resolve("npcs"));
        npcManager.loadAllNPCConfigs();
        log.info(I18n.get().tr(I18nKeys.MANAGER_NPCS_LOADED, npcManager.getNPCConfigCount()));
    }

    /**
     * Register commands
     */
    private void registerCommands() {
        Registries.COMMANDS.register(new ANPCCommand());
        log.info(I18n.get().tr(I18nKeys.COMMAND_REGISTERED));
    }

    /**
     * Register event listeners
     */
    private void registerEventListeners() {
        eventListener = new NPCEventListener();
        Server.getInstance().getEventBus().registerListener(eventListener);
        log.info(I18n.get().tr(I18nKeys.EVENT_REGISTERED));
    }

    /**
     * Start cooldown cleanup task.
     * This task runs on the server scheduler to periodically clean up expired click cooldowns.
     * Note: NPC update tasks (look-at-player, emotes, etc.) are now scheduled per-NPC
     * on each NPC's dimension scheduler for thread safety.
     */
    private void startCooldownCleanupTask() {
        Server.getInstance().getScheduler().scheduleRepeating(this,
                () -> npcManager.cleanupCooldowns(),
                COOLDOWN_CLEANUP_INTERVAL);

        log.debug("Started cooldown cleanup task (interval: {} ticks)", COOLDOWN_CLEANUP_INTERVAL);
    }

    /**
     * Spawn NPCs for all already loaded worlds
     * This is called during plugin enable to handle worlds that were loaded before the plugin
     */
    private void spawnNPCsForLoadedWorlds() {
        for (var world : Server.getInstance().getWorldPool().getWorlds().values()) {
            npcManager.onWorldLoad(world.getName());
        }
    }

    /**
     * Reload plugin configuration
     */
    public void reload() {
        log.info(I18n.get().tr(I18nKeys.PLUGIN_RELOADING));

        // Remove all NPCs (this will cancel their dimension scheduler tasks)
        npcManager.removeAllNPCs();

        // Reload skins
        skinManager.loadAllSkins();
        log.info(I18n.get().tr(I18nKeys.MANAGER_SKINS_RELOADED, skinManager.getSkinCount()));

        // Reload capes
        capeManager.loadAllCapes();
        log.info(I18n.get().tr(I18nKeys.MANAGER_CAPES_RELOADED, capeManager.getCapeCount()));

        // Reload dialogs
        dialogManager.loadAllDialogs();
        log.info(I18n.get().tr(I18nKeys.MANAGER_DIALOGS_RELOADED, dialogManager.getDialogCount()));

        // Reload NPC configs
        npcManager.loadAllNPCConfigs();
        log.info(I18n.get().tr(I18nKeys.MANAGER_NPCS_RELOADED, npcManager.getNPCConfigCount()));

        // Respawn all NPCs (each NPC will start its own dimension scheduler task)
        npcManager.spawnAllNPCs();

        log.info(I18n.get().tr(I18nKeys.PLUGIN_RELOADED));
    }
}
