package me.daoge.allaynpc.action;

import lombok.extern.slf4j.Slf4j;
import me.daoge.allaynpc.npc.NPC;
import me.daoge.allaynpc.util.PlaceholderUtil;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.registry.Registries;
import org.allaymc.api.server.Server;

/**
 * Command action
 * Executes a command when NPC is clicked
 *
 * @author daoge_cmd
 */
@Slf4j
public class CommandAction implements NPCAction {

    /**
     * Command to execute
     */
    private final String command;

    /**
     * Whether to execute as player
     */
    private final boolean asPlayer;

    /**
     * Create command action
     *
     * @param command  command to execute
     * @param asPlayer whether to execute as player
     */
    public CommandAction(String command, boolean asPlayer) {
        this.command = command;
        this.asPlayer = asPlayer;
    }

    @Override
    public void execute(EntityPlayer player, NPC npc) {
        // Replace placeholders
        String parsedCommand = PlaceholderUtil.parse(player, command);

        // Remove leading slash if present
        if (parsedCommand.startsWith("/")) {
            parsedCommand = parsedCommand.substring(1);
        }

        try {
            if (asPlayer) {
                // Execute as player
                Registries.COMMANDS.execute(player, parsedCommand);
            } else {
                // Execute as console
                Registries.COMMANDS.execute(Server.getInstance(), parsedCommand);
            }
        } catch (Exception e) {
            log.error("Failed to execute command: {}", parsedCommand, e);
        }
    }
}
