package me.daoge.allaynpc.config;

import lombok.Builder;
import lombok.Data;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

/**
 * NPC configuration class
 * Stores all configuration information for an NPC
 *
 * @author daoge_cmd
 */
@Data
@Builder
public class NPCConfig {

    /**
     * NPC name (unique identifier, config file name)
     */
    private String name;

    /**
     * Display name (shown on nametag, supports color codes)
     */
    @Builder.Default
    private String displayName = "NPC";

    /**
     * Whether to always show the name tag
     */
    @Builder.Default
    private boolean alwaysShowName = true;

    /**
     * Skin name
     */
    @Builder.Default
    private String skin = "";

    /**
     * Cape name (cape ID, references a cape file in capes/ directory)
     */
    @Builder.Default
    private String cape = "";

    /**
     * Position information
     */
    private PositionConfig position;

    /**
     * Held item (item ID)
     */
    @Builder.Default
    private String heldItem = "";

    /**
     * Armor configuration
     */
    @Builder.Default
    private ArmorConfig armor = ArmorConfig.builder().build();

    /**
     * Whether to look at the nearest player
     */
    @Builder.Default
    private boolean lookAtPlayer = true;

    /**
     * NPC scale (1.0 = normal size)
     */
    @Builder.Default
    private double scale = 1.0;

    /**
     * Score tag (displayed below name tag, supports color codes and PAPI)
     */
    @Builder.Default
    private String scoreTag = "";

    /**
     * Emote action configuration
     */
    @Builder.Default
    private EmoteConfig emote = EmoteConfig.builder().build();

    /**
     * Click cooldown time (ticks)
     */
    @Builder.Default
    private int clickCooldown = 20;

    /**
     * List of click actions
     */
    @Builder.Default
    private List<ActionConfig> actions = new ArrayList<>();

    /**
     * Position configuration
     */
    @Data
    @Builder
    public static class PositionConfig {

        /**
         * World name
         */
        @Builder.Default
        private String world = "world";

        /**
         * X coordinate
         */
        @Builder.Default
        private double x = 0;

        /**
         * Y coordinate
         */
        @Builder.Default
        private double y = 0;

        /**
         * Z coordinate
         */
        @Builder.Default
        private double z = 0;

        /**
         * Horizontal rotation angle (yaw)
         */
        @Builder.Default
        private float yaw = 0;

        /**
         * Vertical rotation angle (pitch)
         */
        @Builder.Default
        private float pitch = 0;

        /**
         * Convert to Vector3d
         */
        public Vector3d toVector3d() {
            return new Vector3d(x, y, z);
        }

        /**
         * Get chunk X coordinate
         */
        public int getChunkX() {
            return (int) Math.floor(x) >> 4;
        }

        /**
         * Get chunk Z coordinate
         */
        public int getChunkZ() {
            return (int) Math.floor(z) >> 4;
        }
    }

    /**
     * Armor configuration
     */
    @Data
    @Builder
    public static class ArmorConfig {

        /**
         * Helmet item ID
         */
        @Builder.Default
        private String helmet = "";

        /**
         * Chestplate item ID
         */
        @Builder.Default
        private String chestplate = "";

        /**
         * Leggings item ID
         */
        @Builder.Default
        private String leggings = "";

        /**
         * Boots item ID
         */
        @Builder.Default
        private String boots = "";

        /**
         * Check if any armor is configured
         */
        public boolean hasAnyArmor() {
            return !helmet.isEmpty() || !chestplate.isEmpty() || !leggings.isEmpty() || !boots.isEmpty();
        }
    }

    /**
     * Emote action configuration
     */
    @Data
    @Builder
    public static class EmoteConfig {

        /**
         * Emote UUID
         */
        @Builder.Default
        private String id = "";

        /**
         * Play interval (ticks)
         */
        @Builder.Default
        private int interval = 100;

        /**
         * Whether enabled
         */
        public boolean isEnabled() {
            return !id.isEmpty() && interval > 0;
        }
    }

    /**
     * Action configuration
     */
    @Data
    @Builder
    public static class ActionConfig {

        /**
         * Action type
         */
        private ActionType type;

        /**
         * Action value (command/dialog name/message content)
         */
        @Builder.Default
        private String value = "";

        /**
         * Whether to execute as player (only valid for command type)
         */
        @Builder.Default
        private boolean asPlayer = false;

        /**
         * Action type enum
         */
        public enum ActionType {
            /**
             * Execute command
             */
            COMMAND,
            /**
             * Open dialog
             */
            DIALOG,
            /**
             * Send message
             */
            MESSAGE
        }
    }
}
