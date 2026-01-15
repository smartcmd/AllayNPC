package me.daoge.allaynpc.util;

import lombok.experimental.UtilityClass;
import org.allaymc.api.command.CommandSender;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.message.I18n;
import org.allaymc.api.message.LangCode;
import org.allaymc.api.player.Player;

/**
 * I18n utility for player-specific translations
 * Uses player's language instead of server's default language
 *
 * @author daoge_cmd
 */
@UtilityClass
public class I18nUtil {

    /**
     * Get the language code for a player
     *
     * @param player the player
     * @return the player's language code, or server default if not available
     */
    public static LangCode getLangCode(EntityPlayer player) {
        if (player == null) {
            return I18n.get().getDefaultLangCode();
        }

        if (player.isActualPlayer()) {
            Player actualPlayer = player.getController();
            if (actualPlayer != null) {
                return actualPlayer.getLoginData().getLangCode();
            }
        }

        return I18n.get().getDefaultLangCode();
    }

    /**
     * Get the language code for a command sender
     *
     * @param sender the command sender
     * @return the sender's language code, or server default if not a player
     */
    public static LangCode getLangCode(CommandSender sender) {
        if (sender instanceof Player player) {
            return player.getLoginData().getLangCode();
        }
        return I18n.get().getDefaultLangCode();
    }

    /**
     * Translate a message using the player's language
     *
     * @param player the player
     * @param key    the translation key
     * @param args   the arguments
     * @return the translated message
     */
    public static String tr(EntityPlayer player, String key, Object... args) {
        return I18n.get().tr(getLangCode(player), key, args);
    }

    /**
     * Translate a message using the command sender's language
     *
     * @param sender the command sender
     * @param key    the translation key
     * @param args   the arguments
     * @return the translated message
     */
    public static String tr(CommandSender sender, String key, Object... args) {
        return I18n.get().tr(getLangCode(sender), key, args);
    }

    /**
     * Translate a message using the Player's language
     *
     * @param player the player
     * @param key    the translation key
     * @param args   the arguments
     * @return the translated message
     */
    public static String tr(Player player, String key, Object... args) {
        if (player == null) {
            return I18n.get().tr(key, args);
        }
        return I18n.get().tr(player.getLoginData().getLangCode(), key, args);
    }
}
