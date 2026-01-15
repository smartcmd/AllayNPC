package me.daoge.allaynpc.util;

import lombok.experimental.UtilityClass;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.papi.PlaceholderAPI;

/**
 * Placeholder utility class
 * Provides PAPI placeholder replacement functionality
 *
 * @author daoge_cmd
 */
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
        return PlaceholderAPI.getAPI().setPlaceholders(player, text);
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
        return PlaceholderAPI.getAPI().containsPlaceholders(text);
    }
}
