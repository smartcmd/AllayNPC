package me.daoge.allaynpc.action;

import me.daoge.allaynpc.npc.NPC;
import me.daoge.allaynpc.util.PlaceholderUtil;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.utils.TextFormat;

/**
 * Message action
 * Sends a message to the player when NPC is clicked
 *
 * @author daoge_cmd
 */
public class MessageAction implements NPCAction {

    /**
     * Message to send
     */
    private final String message;

    /**
     * Create message action
     *
     * @param message message to send
     */
    public MessageAction(String message) {
        this.message = message;
    }

    @Override
    public void execute(EntityPlayer player, NPC npc) {
        // Replace placeholders
        String parsedMessage = PlaceholderUtil.parse(player, message);

        // Process color codes
        String formattedMessage = TextFormat.colorize(parsedMessage);

        // Send message to player
        player.sendMessage(formattedMessage);
    }
}
