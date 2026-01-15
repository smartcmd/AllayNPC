package me.daoge.allaynpc.action;

import lombok.extern.slf4j.Slf4j;
import me.daoge.allaynpc.AllayNPC;
import me.daoge.allaynpc.config.DialogConfig;
import me.daoge.allaynpc.npc.NPC;
import me.daoge.allaynpc.util.PlaceholderUtil;
import org.allaymc.api.dialog.Dialog;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.player.Player;
import org.allaymc.api.registry.Registries;
import org.allaymc.api.server.Server;
import org.allaymc.api.utils.TextFormat;

/**
 * Dialog action
 * Shows a dialog to the player when NPC is clicked
 *
 * @author daoge_cmd
 */
@Slf4j
public class DialogAction implements NPCAction {

    /**
     * Dialog name
     */
    private final String dialogName;

    /**
     * Create dialog action
     *
     * @param dialogName dialog name
     */
    public DialogAction(String dialogName) {
        this.dialogName = dialogName;
    }

    @Override
    public void execute(EntityPlayer player, NPC npc) {
        // Get dialog config
        DialogConfig config = AllayNPC.getInstance().getDialogManager().getDialog(dialogName);
        if (config == null) {
            log.warn("Dialog not found: {}", dialogName);
            return;
        }

        // Check if player is actual player
        if (!player.isActualPlayer()) {
            log.warn("Cannot show dialog to non-actual player");
            return;
        }

        Player actualPlayer = player.getController();
        if (actualPlayer == null) {
            return;
        }

        // Create dialog
        Dialog dialog = Dialog.create()
                .title(TextFormat.colorize(PlaceholderUtil.parse(player, config.getTitle())))
                .body(TextFormat.colorize(PlaceholderUtil.parse(player, config.getBody())));

        // Add buttons
        for (DialogConfig.ButtonConfig buttonConfig : config.getButtons()) {
            String buttonText = TextFormat.colorize(PlaceholderUtil.parse(player, buttonConfig.getText()));

            dialog.button(buttonText, button -> {
                // Execute button commands
                for (String command : buttonConfig.getCommands()) {
                    String parsedCommand = PlaceholderUtil.parse(player, command);
                    if (parsedCommand.startsWith("/")) {
                        parsedCommand = parsedCommand.substring(1);
                    }

                    try {
                        if (buttonConfig.isAsPlayer()) {
                            Registries.COMMANDS.execute(player, parsedCommand);
                        } else {
                            Registries.COMMANDS.execute(Server.getInstance(), parsedCommand);
                        }
                    } catch (Exception e) {
                        log.error("Failed to execute button command: {}", parsedCommand, e);
                    }
                }

                // Send button message
                if (!buttonConfig.getMessage().isEmpty()) {
                    String message = TextFormat.colorize(PlaceholderUtil.parse(player, buttonConfig.getMessage()));
                    player.sendMessage(message);
                }
            });
        }

        // Show dialog
        dialog.sendTo(actualPlayer, npc.getEntity());
    }
}
