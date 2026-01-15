package me.daoge.allaynpc.manager;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.daoge.allaynpc.config.NPCConfig;
import me.daoge.allaynpc.npc.NPC;
import org.allaymc.api.entity.Entity;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NPC Manager
 * Responsible for NPC lifecycle management, config loading and storage
 *
 * @author daoge_cmd
 */
@Slf4j
public class NPCManager {

    /**
     * NPC directory path
     */
    private final Path npcsDirectory;

    /**
     * NPC config cache (NPC name -> NPC config)
     */
    @Getter
    private final Map<String, NPCConfig> npcConfigs = new ConcurrentHashMap<>();

    /**
     * Spawned NPCs (NPC name -> NPC instance)
     */
    private final Map<String, NPC> spawnedNPCs = new ConcurrentHashMap<>();

    /**
     * Player click cooldown records (playerUUID_npcName -> last click time)
     */
    private final Map<String, Long> clickCooldowns = new ConcurrentHashMap<>();

    /**
     * YAML parser
     */
    private final Yaml yaml;

    /**
     * Create NPC manager
     *
     * @param npcsDirectory NPC directory path
     */
    public NPCManager(Path npcsDirectory) {
        this.npcsDirectory = npcsDirectory;

        // Configure YAML output format
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        this.yaml = new Yaml(options);
    }

    /**
     * Load all NPC configs
     */
    public void loadAllNPCConfigs() {
        npcConfigs.clear();

        if (!Files.exists(npcsDirectory)) {
            log.warn("NPCs directory does not exist: {}", npcsDirectory);
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(npcsDirectory, "*.yml")) {
            for (Path path : stream) {
                String fileName = path.getFileName().toString();
                String npcName = fileName.replace(".yml", "");
                loadNPCConfig(path, npcName);
            }
        } catch (IOException e) {
            log.error("Failed to load NPC configs from directory: {}", npcsDirectory, e);
        }

        log.info("Loaded {} NPC configs", npcConfigs.size());
    }

    /**
     * Load a single NPC config from file
     *
     * @param path    config file path
     * @param npcName NPC name
     */
    private void loadNPCConfig(Path path, String npcName) {
        try (InputStream inputStream = Files.newInputStream(path)) {
            Map<String, Object> data = yaml.load(inputStream);
            if (data == null) {
                log.warn("Empty NPC config: {}", npcName);
                return;
            }

            NPCConfig config = parseNPCConfig(npcName, data);
            npcConfigs.put(npcName, config);
            log.debug("Loaded NPC config: {}", npcName);

        } catch (IOException e) {
            log.error("Failed to load NPC config: {}", npcName, e);
        } catch (Exception e) {
            log.error("Failed to parse NPC config: {}", npcName, e);
        }
    }

    /**
     * Parse NPC config
     *
     * @param npcName NPC name
     * @param data    config data
     * @return NPC config object
     */
    @SuppressWarnings("unchecked")
    private NPCConfig parseNPCConfig(String npcName, Map<String, Object> data) {
        NPCConfig.NPCConfigBuilder builder = NPCConfig.builder()
                .name(npcName)
                .displayName(getString(data, "display_name", "NPC"))
                .alwaysShowName(getBoolean(data, "always_show_name", true))
                .skin(getString(data, "skin", ""))
                .heldItem(getString(data, "held_item", ""))
                .lookAtPlayer(getBoolean(data, "look_at_player", true))
                .clickCooldown(getInt(data, "click_cooldown", 20));

        // Parse position
        Object positionObj = data.get("position");
        if (positionObj instanceof Map<?, ?> positionData) {
            builder.position(parsePositionConfig((Map<String, Object>) positionData));
        }

        // Parse armor
        Object armorObj = data.get("armor");
        if (armorObj instanceof Map<?, ?> armorData) {
            builder.armor(parseArmorConfig((Map<String, Object>) armorData));
        }

        // Parse emote
        Object emoteObj = data.get("emote");
        if (emoteObj instanceof Map<?, ?> emoteData) {
            builder.emote(parseEmoteConfig((Map<String, Object>) emoteData));
        }

        // Parse action list
        Object actionsObj = data.get("actions");
        if (actionsObj instanceof List<?> actionsList) {
            List<NPCConfig.ActionConfig> actions = new ArrayList<>();
            for (Object actionObj : actionsList) {
                if (actionObj instanceof Map<?, ?> actionData) {
                    NPCConfig.ActionConfig action = parseActionConfig((Map<String, Object>) actionData);
                    if (action != null) {
                        actions.add(action);
                    }
                }
            }
            builder.actions(actions);
        }

        return builder.build();
    }

    /**
     * Parse position config
     */
    private NPCConfig.PositionConfig parsePositionConfig(Map<String, Object> data) {
        return NPCConfig.PositionConfig.builder()
                .world(getString(data, "world", "world"))
                .x(getDouble(data, "x", 0))
                .y(getDouble(data, "y", 0))
                .z(getDouble(data, "z", 0))
                .yaw(getFloat(data, "yaw", 0))
                .pitch(getFloat(data, "pitch", 0))
                .build();
    }

    /**
     * Parse armor config
     */
    private NPCConfig.ArmorConfig parseArmorConfig(Map<String, Object> data) {
        return NPCConfig.ArmorConfig.builder()
                .helmet(getString(data, "helmet", ""))
                .chestplate(getString(data, "chestplate", ""))
                .leggings(getString(data, "leggings", ""))
                .boots(getString(data, "boots", ""))
                .build();
    }

    /**
     * Parse emote config
     */
    private NPCConfig.EmoteConfig parseEmoteConfig(Map<String, Object> data) {
        return NPCConfig.EmoteConfig.builder()
                .id(getString(data, "id", ""))
                .interval(getInt(data, "interval", 100))
                .build();
    }

    /**
     * Parse action config
     */
    @Nullable
    private NPCConfig.ActionConfig parseActionConfig(Map<String, Object> data) {
        String typeStr = getString(data, "type", "");
        NPCConfig.ActionConfig.ActionType type;

        try {
            type = NPCConfig.ActionConfig.ActionType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Unknown action type: {}", typeStr);
            return null;
        }

        return NPCConfig.ActionConfig.builder()
                .type(type)
                .value(getString(data, "value", ""))
                .asPlayer(getBoolean(data, "as_player", false))
                .build();
    }

    /**
     * Save NPC config to file
     *
     * @param config NPC config
     */
    public void saveNPCConfig(NPCConfig config) {
        Path configFile = npcsDirectory.resolve(config.getName() + ".yml");

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("display_name", config.getDisplayName());
        data.put("always_show_name", config.isAlwaysShowName());
        data.put("skin", config.getSkin());

        // Position
        if (config.getPosition() != null) {
            Map<String, Object> positionData = new LinkedHashMap<>();
            positionData.put("world", config.getPosition().getWorld());
            positionData.put("x", config.getPosition().getX());
            positionData.put("y", config.getPosition().getY());
            positionData.put("z", config.getPosition().getZ());
            positionData.put("yaw", config.getPosition().getYaw());
            positionData.put("pitch", config.getPosition().getPitch());
            data.put("position", positionData);
        }

        data.put("held_item", config.getHeldItem());

        // Armor
        if (config.getArmor() != null && config.getArmor().hasAnyArmor()) {
            Map<String, Object> armorData = new LinkedHashMap<>();
            armorData.put("helmet", config.getArmor().getHelmet());
            armorData.put("chestplate", config.getArmor().getChestplate());
            armorData.put("leggings", config.getArmor().getLeggings());
            armorData.put("boots", config.getArmor().getBoots());
            data.put("armor", armorData);
        }

        data.put("look_at_player", config.isLookAtPlayer());

        // Emote
        if (config.getEmote() != null && config.getEmote().isEnabled()) {
            Map<String, Object> emoteData = new LinkedHashMap<>();
            emoteData.put("id", config.getEmote().getId());
            emoteData.put("interval", config.getEmote().getInterval());
            data.put("emote", emoteData);
        }

        data.put("click_cooldown", config.getClickCooldown());

        // Actions
        if (config.getActions() != null && !config.getActions().isEmpty()) {
            List<Map<String, Object>> actionsList = new ArrayList<>();
            for (NPCConfig.ActionConfig action : config.getActions()) {
                Map<String, Object> actionData = new LinkedHashMap<>();
                actionData.put("type", action.getType().name().toLowerCase());
                actionData.put("value", action.getValue());
                if (action.getType() == NPCConfig.ActionConfig.ActionType.COMMAND) {
                    actionData.put("as_player", action.isAsPlayer());
                }
                actionsList.add(actionData);
            }
            data.put("actions", actionsList);
        }

        try (OutputStream outputStream = Files.newOutputStream(configFile);
             OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {
            yaml.dump(data, writer);
            log.debug("Saved NPC config: {}", config.getName());
        } catch (IOException e) {
            log.error("Failed to save NPC config: {}", config.getName(), e);
        }
    }

    /**
     * Delete NPC config file
     *
     * @param npcName NPC name
     * @return whether deletion was successful
     */
    public boolean deleteNPCConfig(String npcName) {
        Path configFile = npcsDirectory.resolve(npcName + ".yml");
        try {
            if (Files.exists(configFile)) {
                Files.delete(configFile);
                npcConfigs.remove(npcName);
                return true;
            }
        } catch (IOException e) {
            log.error("Failed to delete NPC config: {}", npcName, e);
        }
        return false;
    }

    // Helper methods
    private String getString(Map<String, Object> data, String key, String defaultValue) {
        Object value = data.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    private boolean getBoolean(Map<String, Object> data, String key, boolean defaultValue) {
        Object value = data.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }

    private int getInt(Map<String, Object> data, String key, int defaultValue) {
        Object value = data.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }

    private double getDouble(Map<String, Object> data, String key, double defaultValue) {
        Object value = data.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }

    private float getFloat(Map<String, Object> data, String key, float defaultValue) {
        Object value = data.get(key);
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        return defaultValue;
    }

    /**
     * Spawn all NPCs
     */
    public void spawnAllNPCs() {
        for (NPCConfig config : npcConfigs.values()) {
            spawnNPC(config.getName());
        }
    }

    /**
     * Spawn specified NPC
     *
     * @param npcName NPC name
     * @return whether spawn was successful
     */
    public boolean spawnNPC(String npcName) {
        NPCConfig config = npcConfigs.get(npcName);
        if (config == null) {
            log.warn("NPC config not found: {}", npcName);
            return false;
        }

        // If already spawned, remove first
        if (spawnedNPCs.containsKey(npcName)) {
            removeNPC(npcName);
        }

        NPC npc = new NPC(config);
        if (npc.spawn()) {
            spawnedNPCs.put(npcName, npc);
            return true;
        }

        return false;
    }

    /**
     * Remove specified NPC
     *
     * @param npcName NPC name
     */
    public void removeNPC(String npcName) {
        NPC npc = spawnedNPCs.remove(npcName);
        if (npc != null) {
            npc.remove();
        }
    }

    /**
     * Remove all NPCs
     */
    public void removeAllNPCs() {
        for (NPC npc : spawnedNPCs.values()) {
            npc.remove();
        }
        spawnedNPCs.clear();
    }

    /**
     * Get NPC instance by name
     *
     * @param npcName NPC name
     * @return NPC instance, null if not exists
     */
    @Nullable
    public NPC getNPC(String npcName) {
        return spawnedNPCs.get(npcName);
    }

    /**
     * Get NPC instance by entity
     *
     * @param entity entity
     * @return NPC instance, null if not exists
     */
    @Nullable
    public NPC getNPCByEntity(Entity entity) {
        for (NPC npc : spawnedNPCs.values()) {
            if (npc.getEntity() == entity) {
                return npc;
            }
        }
        return null;
    }

    /**
     * Get NPC config
     *
     * @param npcName NPC name
     * @return NPC config, null if not exists
     */
    @Nullable
    public NPCConfig getNPCConfig(String npcName) {
        return npcConfigs.get(npcName);
    }

    /**
     * Check if NPC exists
     *
     * @param npcName NPC name
     * @return whether exists
     */
    public boolean hasNPC(String npcName) {
        return npcConfigs.containsKey(npcName);
    }

    /**
     * Get all NPC names
     *
     * @return NPC name set
     */
    public Set<String> getNPCNames() {
        return npcConfigs.keySet();
    }

    /**
     * Get all spawned NPCs
     *
     * @return NPC instance collection
     */
    public Collection<NPC> getSpawnedNPCs() {
        return spawnedNPCs.values();
    }

    /**
     * Get NPC config count
     *
     * @return NPC config count
     */
    public int getNPCConfigCount() {
        return npcConfigs.size();
    }

    /**
     * Get spawned NPC count
     *
     * @return spawned NPC count
     */
    public int getSpawnedNPCCount() {
        return spawnedNPCs.size();
    }

    /**
     * Register NPC config
     *
     * @param config NPC config
     */
    public void registerNPCConfig(NPCConfig config) {
        npcConfigs.put(config.getName(), config);
    }

    /**
     * Check player click cooldown
     *
     * @param player  player
     * @param npcName NPC name
     * @return whether on cooldown
     */
    public boolean isOnCooldown(EntityPlayer player, String npcName) {
        // Use UUID instead of RuntimeId for persistent tracking across reconnects
        String key = player.getUniqueId() + "_" + npcName;
        Long lastClick = clickCooldowns.get(key);

        if (lastClick == null) {
            return false;
        }

        NPCConfig config = npcConfigs.get(npcName);
        if (config == null) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        int cooldownMs = config.getClickCooldown() * 50; // tick to milliseconds

        return currentTime - lastClick < cooldownMs;
    }

    /**
     * Record player click time
     *
     * @param player  player
     * @param npcName NPC name
     */
    public void recordClick(EntityPlayer player, String npcName) {
        // Use UUID instead of RuntimeId for persistent tracking across reconnects
        String key = player.getUniqueId() + "_" + npcName;
        clickCooldowns.put(key, System.currentTimeMillis());
    }

    /**
     * Clear expired cooldown records to prevent memory leak
     * Call this periodically
     */
    public void cleanupCooldowns() {
        long currentTime = System.currentTimeMillis();
        // Remove entries older than 1 minute (max reasonable cooldown)
        clickCooldowns.entrySet().removeIf(entry ->
            currentTime - entry.getValue() > 60000);
    }

    /**
     * Handle world load event
     * Spawn all NPCs in the loaded world
     *
     * @param worldName world name
     */
    public void onWorldLoad(String worldName) {
        for (NPCConfig config : npcConfigs.values()) {
            if (config.getPosition() == null) continue;

            if (config.getPosition().getWorld().equals(worldName)) {
                if (!spawnedNPCs.containsKey(config.getName())) {
                    spawnNPC(config.getName());
                }
            }
        }
        log.debug("Spawned NPCs for world: {}", worldName);
    }

    /**
     * Handle world unload event
     * Remove all NPCs in the unloading world
     *
     * @param worldName world name
     */
    public void onWorldUnload(String worldName) {
        List<String> toRemove = new ArrayList<>();

        for (Map.Entry<String, NPC> entry : spawnedNPCs.entrySet()) {
            if (entry.getValue().getWorldName().equals(worldName)) {
                toRemove.add(entry.getKey());
            }
        }

        for (String npcName : toRemove) {
            removeNPC(npcName);
        }
        log.debug("Removed {} NPCs for unloading world: {}", toRemove.size(), worldName);
    }
}
