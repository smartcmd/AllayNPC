package me.daoge.allaynpc.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.papi.PlaceholderAPI;

/**
 * Placeholder utility class
 * Provides PAPI placeholder replacement functionality
 *
 * @author daoge_cmd
 */
@Slf4j
@UtilityClass
public class PlaceholderUtil {

    /**
     * Parse placeholders in text
     *
     * @param player player (provides placeholder context)
     * @param text   text to parse
     * @return parsed text
     */
    public static String parse(EntityPlayer player, String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        try {
            // Try to use PlaceholderAPI
            PlaceholderAPI papi = PlaceholderAPI.getAPI();
            if (papi != null) {
                return papi.setPlaceholders(player, text);
            }
        } catch (NoClassDefFoundError | NullPointerException e) {
            // PlaceholderAPI not loaded, use built-in replacement
            log.debug("PlaceholderAPI not available, using built-in placeholders");
        }

        // Use built-in placeholder replacement
        return parseBuiltIn(player, text);
    }

    /**
     * Use built-in placeholder replacement
     *
     * @param player player
     * @param text   text
     * @return replaced text
     */
    private static String parseBuiltIn(EntityPlayer player, String text) {
        if (player == null) {
            return text;
        }

        // Replace basic placeholders
        text = text.replace("{player_name}", player.getDisplayName());
        text = text.replace("{player_display_name}", player.getDisplayName());

        // Location placeholders
        var location = player.getLocation();
        text = text.replace("{x}", String.valueOf((int) Math.floor(location.x())));
        text = text.replace("{y}", String.valueOf((int) Math.floor(location.y())));
        text = text.replace("{z}", String.valueOf((int) Math.floor(location.z())));

        // Health and hunger
        text = text.replace("{health}", String.valueOf((int) player.getHealth()));
        text = text.replace("{max_health}", String.valueOf((int) player.getMaxHealth()));
        text = text.replace("{food_level}", String.valueOf(player.getFoodLevel()));

        // Experience level
        text = text.replace("{exp_level}", String.valueOf(player.getExperienceLevel()));

        // Game mode
        text = text.replace("{game_mode}", player.getGameMode().name().toLowerCase());

        return text;
    }

    /**
     * Check if text contains placeholders
     *
     * @param text text to check
     * @return whether contains placeholders
     */
    public static boolean containsPlaceholders(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        try {
            PlaceholderAPI papi = PlaceholderAPI.getAPI();
            if (papi != null) {
                return papi.containsPlaceholders(text);
            }
        } catch (NoClassDefFoundError | NullPointerException e) {
            // PlaceholderAPI not loaded
        }

        // Check for built-in placeholders
        return text.contains("{") && text.contains("}");
    }
}
