package me.daoge.allaynpc;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.daoge.allaynpc.command.ANPCCommand;
import me.daoge.allaynpc.i18n.I18nKeys;
import me.daoge.allaynpc.listener.NPCEventListener;
import me.daoge.allaynpc.manager.DialogManager;
import me.daoge.allaynpc.manager.NPCManager;
import me.daoge.allaynpc.manager.SkinManager;
import me.daoge.allaynpc.npc.NPC;
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

        // Start NPC update task
        startNPCUpdateTask();

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

            // Create lang directory
            Path langDir = dataFolder.resolve("lang");
            if (!Files.exists(langDir)) {
                Files.createDirectories(langDir);
                log.info(I18n.get().tr(I18nKeys.DIRECTORY_LANG_CREATED));
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
     * Start NPC update task for look-at-player and emotes
     */
    private void startNPCUpdateTask() {
        Server.getInstance().getScheduler().scheduleRepeating(this, () -> {
            currentTick += NPC_UPDATE_INTERVAL;
            updateNPCs();
        }, NPC_UPDATE_INTERVAL);

        log.info(I18n.get().tr(I18nKeys.TASK_STARTED, NPC_UPDATE_INTERVAL));
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
                log.warn(I18n.get().tr(I18nKeys.NPC_UPDATE_ERROR, npc.getName(), e.getMessage()));
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
        log.info(I18n.get().tr(I18nKeys.PLUGIN_RELOADING));

        // Remove all NPCs
        npcManager.removeAllNPCs();

        // Reload skins
        skinManager.loadAllSkins();
        log.info(I18n.get().tr(I18nKeys.MANAGER_SKINS_RELOADED, skinManager.getSkinCount()));

        // Reload dialogs
        dialogManager.loadAllDialogs();
        log.info(I18n.get().tr(I18nKeys.MANAGER_DIALOGS_RELOADED, dialogManager.getDialogCount()));

        // Reload NPC configs
        npcManager.loadAllNPCConfigs();
        log.info(I18n.get().tr(I18nKeys.MANAGER_NPCS_RELOADED, npcManager.getNPCConfigCount()));

        // Respawn all NPCs
        npcManager.spawnAllNPCs();

        log.info(I18n.get().tr(I18nKeys.PLUGIN_RELOADED));
    }
}
