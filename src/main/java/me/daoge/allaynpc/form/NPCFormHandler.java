package me.daoge.allaynpc.form;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import me.daoge.allaynpc.AllayNPC;
import me.daoge.allaynpc.config.NPCConfig;
import me.daoge.allaynpc.manager.NPCManager;
import me.daoge.allaynpc.manager.SkinManager;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.form.Forms;
import org.allaymc.api.form.type.CustomForm;
import org.allaymc.api.form.type.SimpleForm;
import org.allaymc.api.player.Player;
import org.allaymc.api.utils.TextFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * NPC form handler
 * Provides form UI for NPC creation and editing
 *
 * @author daoge_cmd
 */
@Slf4j
@UtilityClass
public class NPCFormHandler {

    /**
     * Open NPC creation form
     *
     * @param player  player
     * @param npcName NPC name
     */
    public static void openCreateForm(EntityPlayer player, String npcName) {
        if (!player.isActualPlayer()) {
            return;
        }

        Player actualPlayer = player.getController();
        var location = player.getLocation();
        SkinManager skinManager = AllayNPC.getInstance().getSkinManager();

        // Get skin list
        List<String> skinList = new ArrayList<>();
        skinList.add(""); // Default option: use default skin
        skinList.addAll(skinManager.getSkinNames());

        CustomForm form = Forms.custom()
                .title("Create NPC: " + npcName)
                .input("Display Name", "Enter display name...", npcName)
                .toggle("Always Show Name", true)
                .dropdown("Skin", skinList, 0)
                .toggle("Look at Player", true)
                .input("Held Item ID", "e.g. minecraft:diamond_sword", "")
                .input("Click Cooldown (ticks)", "20", "20")
                .label("Position will be set to your current location")
                .onResponse(responses -> {
                    // Parse form response safely
                    String displayName = responses.get(0);
                    boolean alwaysShowName = parseBoolean(responses.get(1), true);
                    int skinIndex = parseInt(responses.get(2), 0);
                    String skinName = (skinIndex > 0 && skinIndex < skinList.size()) ? skinList.get(skinIndex) : "";
                    boolean lookAtPlayer = parseBoolean(responses.get(3), true);
                    String heldItem = responses.get(4);
                    int clickCooldown = parseInt(responses.get(5), 20);

                    // Create NPC config
                    NPCConfig config = NPCConfig.builder()
                            .name(npcName)
                            .displayName(displayName.isEmpty() ? npcName : displayName)
                            .alwaysShowName(alwaysShowName)
                            .skin(skinName)
                            .lookAtPlayer(lookAtPlayer)
                            .heldItem(heldItem)
                            .clickCooldown(clickCooldown)
                            .position(NPCConfig.PositionConfig.builder()
                                    .world(location.dimension().getWorld().getName())
                                    .x(location.x())
                                    .y(location.y())
                                    .z(location.z())
                                    .yaw((float) location.yaw())
                                    .pitch((float) location.pitch())
                                    .build())
                            .armor(NPCConfig.ArmorConfig.builder().build())
                            .emote(NPCConfig.EmoteConfig.builder().build())
                            .actions(new ArrayList<>())
                            .build();

                    // Save and spawn NPC
                    NPCManager npcManager = AllayNPC.getInstance().getNpcManager();
                    npcManager.registerNPCConfig(config);
                    npcManager.saveNPCConfig(config);

                    if (npcManager.spawnNPC(npcName)) {
                        player.sendMessage(TextFormat.GREEN + "NPC '" + npcName + "' created successfully!");
                    } else {
                        player.sendMessage(TextFormat.RED + "NPC created but failed to spawn. Check server logs.");
                    }
                });

        actualPlayer.viewForm(form);
    }

    /**
     * Open NPC edit form
     *
     * @param player  player
     * @param npcName NPC name
     */
    public static void openEditForm(EntityPlayer player, String npcName) {
        if (!player.isActualPlayer()) {
            return;
        }

        Player actualPlayer = player.getController();
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();
        NPCConfig config = npcManager.getNPCConfig(npcName);

        if (config == null) {
            player.sendMessage(TextFormat.RED + "NPC not found!");
            return;
        }

        // Show edit options menu
        SimpleForm menuForm = Forms.simple()
                .title("Edit NPC: " + npcName)
                .content("Select what you want to edit:");

        menuForm.button("Basic Settings").onClick(btn -> openBasicSettingsForm(player, npcName));
        menuForm.button("Position").onClick(btn -> openPositionForm(player, npcName));
        menuForm.button("Armor").onClick(btn -> openArmorForm(player, npcName));
        menuForm.button("Emote").onClick(btn -> openEmoteForm(player, npcName));
        menuForm.button("Actions").onClick(btn -> openActionsMenuForm(player, npcName));

        actualPlayer.viewForm(menuForm);
    }

    /**
     * Open basic settings form
     */
    private static void openBasicSettingsForm(EntityPlayer player, String npcName) {
        if (!player.isActualPlayer()) return;

        Player actualPlayer = player.getController();
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();
        NPCConfig config = npcManager.getNPCConfig(npcName);
        SkinManager skinManager = AllayNPC.getInstance().getSkinManager();

        if (config == null) return;

        // Get skin list
        List<String> skinList = new ArrayList<>();
        skinList.add(""); // Default option
        skinList.addAll(skinManager.getSkinNames());

        int currentSkinIndex = 0;
        if (!config.getSkin().isEmpty()) {
            int idx = skinList.indexOf(config.getSkin());
            if (idx >= 0) currentSkinIndex = idx;
        }

        CustomForm form = Forms.custom()
                .title("Edit Basic Settings: " + npcName)
                .input("Display Name", "Enter display name...", config.getDisplayName())
                .toggle("Always Show Name", config.isAlwaysShowName())
                .dropdown("Skin", skinList, currentSkinIndex)
                .toggle("Look at Player", config.isLookAtPlayer())
                .input("Held Item ID", "e.g. minecraft:diamond_sword", config.getHeldItem())
                .input("Click Cooldown (ticks)", "20", String.valueOf(config.getClickCooldown()))
                .onResponse(responses -> {
                    String displayName = responses.get(0);
                    boolean alwaysShowName = parseBoolean(responses.get(1), config.isAlwaysShowName());
                    int skinIndex = parseInt(responses.get(2), 0);
                    String skinName = (skinIndex > 0 && skinIndex < skinList.size()) ? skinList.get(skinIndex) : "";
                    boolean lookAtPlayer = parseBoolean(responses.get(3), config.isLookAtPlayer());
                    String heldItem = responses.get(4);
                    int clickCooldown = parseInt(responses.get(5), config.getClickCooldown());

                    // Update config
                    config.setDisplayName(displayName.isEmpty() ? npcName : displayName);
                    config.setAlwaysShowName(alwaysShowName);
                    config.setSkin(skinName);
                    config.setLookAtPlayer(lookAtPlayer);
                    config.setHeldItem(heldItem);
                    config.setClickCooldown(clickCooldown);

                    // Save config
                    npcManager.saveNPCConfig(config);

                    // Respawn NPC
                    npcManager.removeNPC(npcName);
                    npcManager.spawnNPC(npcName);

                    player.sendMessage(TextFormat.GREEN + "Basic settings updated!");
                });

        actualPlayer.viewForm(form);
    }

    /**
     * Open position settings form
     */
    private static void openPositionForm(EntityPlayer player, String npcName) {
        if (!player.isActualPlayer()) return;

        Player actualPlayer = player.getController();
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();
        NPCConfig config = npcManager.getNPCConfig(npcName);

        if (config == null || config.getPosition() == null) return;

        var pos = config.getPosition();

        SimpleForm form = Forms.simple()
                .title("Edit Position: " + npcName)
                .content(String.format("Current Position:\nWorld: %s\nX: %.2f, Y: %.2f, Z: %.2f\nYaw: %.2f, Pitch: %.2f",
                        pos.getWorld(), pos.getX(), pos.getY(), pos.getZ(), pos.getYaw(), pos.getPitch()));

        form.button("Set to My Position").onClick(btn -> {
            var loc = player.getLocation();
            pos.setWorld(loc.dimension().getWorld().getName());
            pos.setX(loc.x());
            pos.setY(loc.y());
            pos.setZ(loc.z());
            pos.setYaw((float) loc.yaw());
            pos.setPitch((float) loc.pitch());

            npcManager.saveNPCConfig(config);
            npcManager.removeNPC(npcName);
            npcManager.spawnNPC(npcName);

            player.sendMessage(TextFormat.GREEN + "Position updated to your location!");
        });

        form.button("Cancel").onClick(btn -> {});

        actualPlayer.viewForm(form);
    }

    /**
     * Open armor settings form
     */
    private static void openArmorForm(EntityPlayer player, String npcName) {
        if (!player.isActualPlayer()) return;

        Player actualPlayer = player.getController();
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();
        NPCConfig config = npcManager.getNPCConfig(npcName);

        if (config == null) return;

        var armor = config.getArmor();
        if (armor == null) {
            armor = NPCConfig.ArmorConfig.builder().build();
            config.setArmor(armor);
        }

        final var finalArmor = armor;

        CustomForm form = Forms.custom()
                .title("Edit Armor: " + npcName)
                .input("Helmet", "e.g. minecraft:diamond_helmet", armor.getHelmet())
                .input("Chestplate", "e.g. minecraft:diamond_chestplate", armor.getChestplate())
                .input("Leggings", "e.g. minecraft:diamond_leggings", armor.getLeggings())
                .input("Boots", "e.g. minecraft:diamond_boots", armor.getBoots())
                .onResponse(responses -> {
                    finalArmor.setHelmet(responses.get(0));
                    finalArmor.setChestplate(responses.get(1));
                    finalArmor.setLeggings(responses.get(2));
                    finalArmor.setBoots(responses.get(3));

                    npcManager.saveNPCConfig(config);
                    npcManager.removeNPC(npcName);
                    npcManager.spawnNPC(npcName);

                    player.sendMessage(TextFormat.GREEN + "Armor settings updated!");
                });

        actualPlayer.viewForm(form);
    }

    /**
     * Open emote settings form
     */
    private static void openEmoteForm(EntityPlayer player, String npcName) {
        if (!player.isActualPlayer()) return;

        Player actualPlayer = player.getController();
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();
        NPCConfig config = npcManager.getNPCConfig(npcName);

        if (config == null) return;

        var emote = config.getEmote();
        if (emote == null) {
            emote = NPCConfig.EmoteConfig.builder().build();
            config.setEmote(emote);
        }

        final var finalEmote = emote;

        CustomForm form = Forms.custom()
                .title("Edit Emote: " + npcName)
                .input("Emote UUID", "e.g. 4c8ae710-df2e-47cd-814d-cc7bf21a3d67", emote.getId())
                .input("Interval (ticks)", "100", String.valueOf(emote.getInterval()))
                .label("Leave UUID empty to disable emotes")
                .onResponse(responses -> {
                    finalEmote.setId(responses.get(0));
                    finalEmote.setInterval(parseInt(responses.get(1), finalEmote.getInterval()));

                    npcManager.saveNPCConfig(config);
                    player.sendMessage(TextFormat.GREEN + "Emote settings updated!");
                });

        actualPlayer.viewForm(form);
    }

    /**
     * Open actions menu form
     */
    private static void openActionsMenuForm(EntityPlayer player, String npcName) {
        if (!player.isActualPlayer()) return;

        Player actualPlayer = player.getController();
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();
        NPCConfig config = npcManager.getNPCConfig(npcName);

        if (config == null) return;

        List<NPCConfig.ActionConfig> actions = config.getActions();
        if (actions == null) {
            actions = new ArrayList<>();
            config.setActions(actions);
        }

        SimpleForm formBuilder = Forms.simple()
                .title("Edit Actions: " + npcName)
                .content("Current actions: " + actions.size());

        // Add buttons for existing actions
        for (int i = 0; i < actions.size(); i++) {
            var action = actions.get(i);
            int index = i;
            formBuilder.button(String.format("[%d] %s: %s", i + 1, action.getType(), truncate(action.getValue(), 20)))
                    .onClick(btn -> openEditActionForm(player, npcName, index));
        }

        // Add new action button
        formBuilder.button(TextFormat.GREEN + "+ Add Action").onClick(btn -> openAddActionForm(player, npcName));

        actualPlayer.viewForm(formBuilder);
    }

    /**
     * Open add action form
     */
    private static void openAddActionForm(EntityPlayer player, String npcName) {
        if (!player.isActualPlayer()) return;

        Player actualPlayer = player.getController();
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();
        NPCConfig config = npcManager.getNPCConfig(npcName);

        if (config == null) return;

        List<String> actionTypes = List.of("COMMAND", "MESSAGE", "DIALOG");

        CustomForm form = Forms.custom()
                .title("Add Action: " + npcName)
                .dropdown("Action Type", actionTypes, 0)
                .input("Value", "Command/Message/Dialog name", "")
                .toggle("Execute as Player (for commands)", false)
                .onResponse(responses -> {
                    int typeIndex = parseInt(responses.get(0), 0);
                    if (typeIndex < 0 || typeIndex >= actionTypes.size()) typeIndex = 0;
                    String value = responses.get(1);
                    boolean asPlayer = parseBoolean(responses.get(2), false);

                    NPCConfig.ActionConfig.ActionType type = NPCConfig.ActionConfig.ActionType.valueOf(actionTypes.get(typeIndex));
                    NPCConfig.ActionConfig action = NPCConfig.ActionConfig.builder()
                            .type(type)
                            .value(value)
                            .asPlayer(asPlayer)
                            .build();

                    if (config.getActions() == null) {
                        config.setActions(new ArrayList<>());
                    }
                    config.getActions().add(action);

                    npcManager.saveNPCConfig(config);
                    player.sendMessage(TextFormat.GREEN + "Action added!");

                    // Return to actions menu
                    openActionsMenuForm(player, npcName);
                });

        actualPlayer.viewForm(form);
    }

    /**
     * Open edit action form
     */
    private static void openEditActionForm(EntityPlayer player, String npcName, int actionIndex) {
        if (!player.isActualPlayer()) return;

        Player actualPlayer = player.getController();
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();
        NPCConfig config = npcManager.getNPCConfig(npcName);

        if (config == null || config.getActions() == null || actionIndex >= config.getActions().size()) return;

        NPCConfig.ActionConfig action = config.getActions().get(actionIndex);

        SimpleForm form = Forms.simple()
                .title("Edit Action #" + (actionIndex + 1))
                .content(String.format("Type: %s\nValue: %s\nAs Player: %s",
                        action.getType(), action.getValue(), action.isAsPlayer()));

        form.button("Edit").onClick(btn -> openActionEditDetailForm(player, npcName, actionIndex));

        form.button(TextFormat.RED + "Delete").onClick(btn -> {
            config.getActions().remove(actionIndex);
            npcManager.saveNPCConfig(config);
            player.sendMessage(TextFormat.GREEN + "Action deleted!");
            openActionsMenuForm(player, npcName);
        });

        form.button("Back").onClick(btn -> openActionsMenuForm(player, npcName));

        actualPlayer.viewForm(form);
    }

    /**
     * Open action edit detail form
     */
    private static void openActionEditDetailForm(EntityPlayer player, String npcName, int actionIndex) {
        if (!player.isActualPlayer()) return;

        Player actualPlayer = player.getController();
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();
        NPCConfig config = npcManager.getNPCConfig(npcName);

        if (config == null || config.getActions() == null || actionIndex >= config.getActions().size()) return;

        NPCConfig.ActionConfig action = config.getActions().get(actionIndex);
        List<String> actionTypes = List.of("COMMAND", "MESSAGE", "DIALOG");

        int currentTypeIndex = actionTypes.indexOf(action.getType().name());
        if (currentTypeIndex < 0) currentTypeIndex = 0;

        CustomForm form = Forms.custom()
                .title("Edit Action #" + (actionIndex + 1))
                .dropdown("Action Type", actionTypes, currentTypeIndex)
                .input("Value", "Command/Message/Dialog name", action.getValue())
                .toggle("Execute as Player (for commands)", action.isAsPlayer())
                .onResponse(responses -> {
                    int typeIndex = parseInt(responses.get(0), 0);
                    if (typeIndex < 0 || typeIndex >= actionTypes.size()) typeIndex = 0;
                    String value = responses.get(1);
                    boolean asPlayer = parseBoolean(responses.get(2), action.isAsPlayer());

                    action.setType(NPCConfig.ActionConfig.ActionType.valueOf(actionTypes.get(typeIndex)));
                    action.setValue(value);
                    action.setAsPlayer(asPlayer);

                    npcManager.saveNPCConfig(config);
                    player.sendMessage(TextFormat.GREEN + "Action updated!");
                    openActionsMenuForm(player, npcName);
                });

        actualPlayer.viewForm(form);
    }

    /**
     * Truncate string
     */
    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }

    /**
     * Safely parse boolean from form response
     * Handles both Boolean objects and "true"/"false" strings
     */
    private static boolean parseBoolean(Object value, boolean defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof Boolean b) return b;
        return "true".equalsIgnoreCase(value.toString());
    }

    /**
     * Safely parse integer from form response
     */
    private static int parseInt(Object value, int defaultValue) {
        if (value == null) return defaultValue;
        try {
            if (value instanceof Number n) return n.intValue();
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
