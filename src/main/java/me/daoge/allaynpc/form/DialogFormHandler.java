package me.daoge.allaynpc.form;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import me.daoge.allaynpc.AllayNPC;
import me.daoge.allaynpc.config.DialogConfig;
import me.daoge.allaynpc.i18n.I18nKeys;
import me.daoge.allaynpc.manager.DialogManager;
import me.daoge.allaynpc.util.I18nUtil;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.form.Forms;
import org.allaymc.api.form.type.CustomForm;
import org.allaymc.api.form.type.SimpleForm;
import org.allaymc.api.player.Player;
import org.allaymc.api.utils.TextFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Dialog form handler
 * Provides form UI for dialog creation and editing
 *
 * @author daoge_cmd
 */
@Slf4j
@UtilityClass
public class DialogFormHandler {

    /**
     * Open dialog list form (main menu)
     *
     * @param player player
     */
    public static void openDialogListForm(EntityPlayer player) {
        if (!player.isActualPlayer()) {
            return;
        }

        Player actualPlayer = player.getController();
        DialogManager dialogManager = AllayNPC.getInstance().getDialogManager();
        Set<String> dialogNames = dialogManager.getDialogNames();

        SimpleForm form = Forms.simple()
                .title(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_LIST_TITLE))
                .content(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_LIST_CONTENT, dialogNames.size()));

        // Add button for each existing dialog
        for (String dialogName : dialogNames) {
            form.button(dialogName).onClick(btn -> openDialogEditMenuForm(player, dialogName));
        }

        // Add new dialog button
        form.button(TextFormat.GREEN + I18nUtil.tr(player, I18nKeys.FORM_DIALOG_CREATE))
                .onClick(btn -> openCreateDialogForm(player));

        actualPlayer.viewForm(form);
    }

    /**
     * Open create dialog form
     *
     * @param player player
     */
    public static void openCreateDialogForm(EntityPlayer player) {
        if (!player.isActualPlayer()) {
            return;
        }

        Player actualPlayer = player.getController();

        CustomForm form = Forms.custom()
                .title(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_CREATE_TITLE))
                .input(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_NAME), I18nUtil.tr(player, I18nKeys.FORM_DIALOG_NAME_PLACEHOLDER), "")
                .input(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_TITLE_FIELD), I18nUtil.tr(player, I18nKeys.FORM_DIALOG_TITLE_PLACEHOLDER), "")
                .input(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BODY), I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BODY_PLACEHOLDER), "")
                .onResponse(responses -> {
                    String name = responses.get(0).trim();
                    String title = responses.get(1);
                    String body = responses.get(2);

                    if (name.isEmpty()) {
                        player.sendMessage(TextFormat.RED + I18nUtil.tr(player, I18nKeys.FORM_DIALOG_NAME_EMPTY));
                        return;
                    }

                    // Check if dialog already exists
                    DialogManager dialogManager = AllayNPC.getInstance().getDialogManager();
                    if (dialogManager.hasDialog(name)) {
                        player.sendMessage(TextFormat.RED + I18nUtil.tr(player, I18nKeys.FORM_DIALOG_EXISTS, name));
                        return;
                    }

                    // Create new dialog config
                    DialogConfig config = DialogConfig.builder()
                            .name(name)
                            .title(title)
                            .body(body)
                            .buttons(new ArrayList<>())
                            .build();

                    // Save and register
                    dialogManager.registerDialog(name, config);
                    if (dialogManager.saveDialogConfig(config)) {
                        player.sendMessage(TextFormat.GREEN + I18nUtil.tr(player, I18nKeys.FORM_DIALOG_CREATED, name));
                        openDialogEditMenuForm(player, name);
                    } else {
                        player.sendMessage(TextFormat.RED + I18nUtil.tr(player, I18nKeys.FORM_DIALOG_SAVE_FAILED));
                    }
                });

        actualPlayer.viewForm(form);
    }

    /**
     * Open dialog edit menu form
     *
     * @param player     player
     * @param dialogName dialog name
     */
    public static void openDialogEditMenuForm(EntityPlayer player, String dialogName) {
        if (!player.isActualPlayer()) {
            return;
        }

        Player actualPlayer = player.getController();
        DialogManager dialogManager = AllayNPC.getInstance().getDialogManager();
        DialogConfig config = dialogManager.getDialog(dialogName);

        if (config == null) {
            player.sendMessage(TextFormat.RED + I18nUtil.tr(player, I18nKeys.FORM_DIALOG_NOTFOUND, dialogName));
            return;
        }

        SimpleForm form = Forms.simple()
                .title(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_EDIT_TITLE, dialogName))
                .content(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_EDIT_CONTENT));

        form.button(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_EDIT_BASIC))
                .onClick(btn -> openEditBasicForm(player, dialogName));
        form.button(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_EDIT_BUTTONS))
                .onClick(btn -> openButtonsMenuForm(player, dialogName));
        form.button(TextFormat.RED + I18nUtil.tr(player, I18nKeys.FORM_DIALOG_DELETE))
                .onClick(btn -> openDeleteConfirmForm(player, dialogName));
        form.button(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BACK))
                .onClick(btn -> openDialogListForm(player));

        actualPlayer.viewForm(form);
    }

    /**
     * Open edit basic settings form
     *
     * @param player     player
     * @param dialogName dialog name
     */
    private static void openEditBasicForm(EntityPlayer player, String dialogName) {
        if (!player.isActualPlayer()) return;

        Player actualPlayer = player.getController();
        DialogManager dialogManager = AllayNPC.getInstance().getDialogManager();
        DialogConfig config = dialogManager.getDialog(dialogName);

        if (config == null) return;

        CustomForm form = Forms.custom()
                .title(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BASIC_TITLE, dialogName))
                .input(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_TITLE_FIELD), I18nUtil.tr(player, I18nKeys.FORM_DIALOG_TITLE_PLACEHOLDER), config.getTitle())
                .input(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BODY), I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BODY_PLACEHOLDER), config.getBody())
                .onResponse(responses -> {
                    String title = responses.get(0);
                    String body = responses.get(1);

                    // Update config using builder to create new instance
                    DialogConfig newConfig = DialogConfig.builder()
                            .name(dialogName)
                            .title(title)
                            .body(body)
                            .buttons(config.getButtons())
                            .build();

                    dialogManager.registerDialog(dialogName, newConfig);
                    if (dialogManager.saveDialogConfig(newConfig)) {
                        player.sendMessage(TextFormat.GREEN + I18nUtil.tr(player, I18nKeys.FORM_DIALOG_UPDATED));
                    } else {
                        player.sendMessage(TextFormat.RED + I18nUtil.tr(player, I18nKeys.FORM_DIALOG_SAVE_FAILED));
                    }

                    openDialogEditMenuForm(player, dialogName);
                });

        actualPlayer.viewForm(form);
    }

    /**
     * Open buttons menu form
     *
     * @param player     player
     * @param dialogName dialog name
     */
    private static void openButtonsMenuForm(EntityPlayer player, String dialogName) {
        if (!player.isActualPlayer()) return;

        Player actualPlayer = player.getController();
        DialogManager dialogManager = AllayNPC.getInstance().getDialogManager();
        DialogConfig config = dialogManager.getDialog(dialogName);

        if (config == null) return;

        List<DialogConfig.ButtonConfig> buttons = config.getButtons();

        SimpleForm form = Forms.simple()
                .title(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTONS_TITLE, dialogName))
                .content(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTONS_CONTENT, buttons.size()));

        // Add buttons for existing buttons
        for (int i = 0; i < buttons.size(); i++) {
            DialogConfig.ButtonConfig button = buttons.get(i);
            int index = i;
            form.button(String.format("[%d] %s", i + 1, truncate(button.getText(), 25)))
                    .onClick(btn -> openEditButtonForm(player, dialogName, index));
        }

        // Add new button button
        form.button(TextFormat.GREEN + I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTON_ADD))
                .onClick(btn -> openAddButtonForm(player, dialogName));

        // Back button
        form.button(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BACK))
                .onClick(btn -> openDialogEditMenuForm(player, dialogName));

        actualPlayer.viewForm(form);
    }

    /**
     * Open add button form
     *
     * @param player     player
     * @param dialogName dialog name
     */
    private static void openAddButtonForm(EntityPlayer player, String dialogName) {
        if (!player.isActualPlayer()) return;

        Player actualPlayer = player.getController();
        DialogManager dialogManager = AllayNPC.getInstance().getDialogManager();
        DialogConfig config = dialogManager.getDialog(dialogName);

        if (config == null) return;

        CustomForm form = Forms.custom()
                .title(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTON_ADD_TITLE))
                .input(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTON_TEXT), I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTON_TEXT_PLACEHOLDER), "")
                .input(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTON_COMMANDS), I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTON_COMMANDS_PLACEHOLDER), "")
                .input(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTON_MESSAGE), I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTON_MESSAGE_PLACEHOLDER), "")
                .toggle(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTON_ASPLAYER), false)
                .onResponse(responses -> {
                    String text = responses.get(0);
                    String commandsStr = responses.get(1);
                    String message = responses.get(2);
                    boolean asPlayer = parseBoolean(responses.get(3), false);

                    // Parse commands (newline separated)
                    List<String> commands = parseCommands(commandsStr);

                    DialogConfig.ButtonConfig buttonConfig = DialogConfig.ButtonConfig.builder()
                            .text(text)
                            .commands(commands)
                            .message(message)
                            .asPlayer(asPlayer)
                            .build();

                    // Add button to config
                    List<DialogConfig.ButtonConfig> newButtons = new ArrayList<>(config.getButtons());
                    newButtons.add(buttonConfig);

                    DialogConfig newConfig = DialogConfig.builder()
                            .name(dialogName)
                            .title(config.getTitle())
                            .body(config.getBody())
                            .buttons(newButtons)
                            .build();

                    dialogManager.registerDialog(dialogName, newConfig);
                    if (dialogManager.saveDialogConfig(newConfig)) {
                        player.sendMessage(TextFormat.GREEN + I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTON_ADDED));
                    } else {
                        player.sendMessage(TextFormat.RED + I18nUtil.tr(player, I18nKeys.FORM_DIALOG_SAVE_FAILED));
                    }

                    openButtonsMenuForm(player, dialogName);
                });

        actualPlayer.viewForm(form);
    }

    /**
     * Open edit button form
     *
     * @param player      player
     * @param dialogName  dialog name
     * @param buttonIndex button index
     */
    private static void openEditButtonForm(EntityPlayer player, String dialogName, int buttonIndex) {
        if (!player.isActualPlayer()) return;

        Player actualPlayer = player.getController();
        DialogManager dialogManager = AllayNPC.getInstance().getDialogManager();
        DialogConfig config = dialogManager.getDialog(dialogName);

        if (config == null || buttonIndex >= config.getButtons().size()) return;

        DialogConfig.ButtonConfig button = config.getButtons().get(buttonIndex);

        SimpleForm form = Forms.simple()
                .title(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTON_EDIT_TITLE, buttonIndex + 1))
                .content(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTON_CONTENT,
                        button.getText(),
                        String.join(", ", button.getCommands()),
                        button.getMessage(),
                        button.isAsPlayer()));

        form.button(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTON_EDIT))
                .onClick(btn -> openButtonEditDetailForm(player, dialogName, buttonIndex));
        form.button(TextFormat.RED + I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTON_DELETE))
                .onClick(btn -> {
                    List<DialogConfig.ButtonConfig> newButtons = new ArrayList<>(config.getButtons());
                    newButtons.remove(buttonIndex);

                    DialogConfig newConfig = DialogConfig.builder()
                            .name(dialogName)
                            .title(config.getTitle())
                            .body(config.getBody())
                            .buttons(newButtons)
                            .build();

                    dialogManager.registerDialog(dialogName, newConfig);
                    if (dialogManager.saveDialogConfig(newConfig)) {
                        player.sendMessage(TextFormat.GREEN + I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTON_DELETED));
                    } else {
                        player.sendMessage(TextFormat.RED + I18nUtil.tr(player, I18nKeys.FORM_DIALOG_SAVE_FAILED));
                    }

                    openButtonsMenuForm(player, dialogName);
                });
        form.button(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BACK))
                .onClick(btn -> openButtonsMenuForm(player, dialogName));

        actualPlayer.viewForm(form);
    }

    /**
     * Open button edit detail form
     *
     * @param player      player
     * @param dialogName  dialog name
     * @param buttonIndex button index
     */
    private static void openButtonEditDetailForm(EntityPlayer player, String dialogName, int buttonIndex) {
        if (!player.isActualPlayer()) return;

        Player actualPlayer = player.getController();
        DialogManager dialogManager = AllayNPC.getInstance().getDialogManager();
        DialogConfig config = dialogManager.getDialog(dialogName);

        if (config == null || buttonIndex >= config.getButtons().size()) return;

        DialogConfig.ButtonConfig button = config.getButtons().get(buttonIndex);

        CustomForm form = Forms.custom()
                .title(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTON_EDIT_TITLE, buttonIndex + 1))
                .input(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTON_TEXT), I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTON_TEXT_PLACEHOLDER), button.getText())
                .input(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTON_COMMANDS), I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTON_COMMANDS_PLACEHOLDER), String.join("\n", button.getCommands()))
                .input(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTON_MESSAGE), I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTON_MESSAGE_PLACEHOLDER), button.getMessage())
                .toggle(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTON_ASPLAYER), button.isAsPlayer())
                .onResponse(responses -> {
                    String text = responses.get(0);
                    String commandsStr = responses.get(1);
                    String message = responses.get(2);
                    boolean asPlayer = parseBoolean(responses.get(3), button.isAsPlayer());

                    List<String> commands = parseCommands(commandsStr);

                    DialogConfig.ButtonConfig newButton = DialogConfig.ButtonConfig.builder()
                            .text(text)
                            .commands(commands)
                            .message(message)
                            .asPlayer(asPlayer)
                            .build();

                    List<DialogConfig.ButtonConfig> newButtons = new ArrayList<>(config.getButtons());
                    newButtons.set(buttonIndex, newButton);

                    DialogConfig newConfig = DialogConfig.builder()
                            .name(dialogName)
                            .title(config.getTitle())
                            .body(config.getBody())
                            .buttons(newButtons)
                            .build();

                    dialogManager.registerDialog(dialogName, newConfig);
                    if (dialogManager.saveDialogConfig(newConfig)) {
                        player.sendMessage(TextFormat.GREEN + I18nUtil.tr(player, I18nKeys.FORM_DIALOG_BUTTON_UPDATED));
                    } else {
                        player.sendMessage(TextFormat.RED + I18nUtil.tr(player, I18nKeys.FORM_DIALOG_SAVE_FAILED));
                    }

                    openButtonsMenuForm(player, dialogName);
                });

        actualPlayer.viewForm(form);
    }

    /**
     * Open delete confirmation form
     *
     * @param player     player
     * @param dialogName dialog name
     */
    private static void openDeleteConfirmForm(EntityPlayer player, String dialogName) {
        if (!player.isActualPlayer()) return;

        Player actualPlayer = player.getController();

        SimpleForm form = Forms.simple()
                .title(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_DELETE_TITLE))
                .content(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_DELETE_CONFIRM, dialogName));

        form.button(TextFormat.RED + I18nUtil.tr(player, I18nKeys.FORM_DIALOG_DELETE_YES))
                .onClick(btn -> {
                    DialogManager dialogManager = AllayNPC.getInstance().getDialogManager();
                    dialogManager.removeDialog(dialogName);
                    if (dialogManager.deleteDialogFile(dialogName)) {
                        player.sendMessage(TextFormat.GREEN + I18nUtil.tr(player, I18nKeys.FORM_DIALOG_DELETED, dialogName));
                    } else {
                        player.sendMessage(TextFormat.RED + I18nUtil.tr(player, I18nKeys.FORM_DIALOG_DELETE_FAILED));
                    }
                    openDialogListForm(player);
                });

        form.button(I18nUtil.tr(player, I18nKeys.FORM_DIALOG_DELETE_NO))
                .onClick(btn -> openDialogEditMenuForm(player, dialogName));

        actualPlayer.viewForm(form);
    }

    /**
     * Parse commands from newline-separated string
     */
    private static List<String> parseCommands(String commandsStr) {
        List<String> commands = new ArrayList<>();
        if (commandsStr != null && !commandsStr.isEmpty()) {
            for (String cmd : commandsStr.split("\n")) {
                String trimmed = cmd.trim();
                if (!trimmed.isEmpty()) {
                    commands.add(trimmed);
                }
            }
        }
        return commands;
    }

    /**
     * Truncate string with ellipsis
     */
    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (maxLength <= 3) return str.length() <= maxLength ? str : str.substring(0, maxLength);
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }

    /**
     * Safely parse boolean from form response
     */
    private static boolean parseBoolean(Object value, boolean defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof Boolean b) return b;
        return "true".equalsIgnoreCase(value.toString());
    }
}
