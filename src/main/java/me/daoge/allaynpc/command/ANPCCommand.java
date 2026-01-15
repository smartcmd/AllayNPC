package me.daoge.allaynpc.command;

import me.daoge.allaynpc.AllayNPC;
import me.daoge.allaynpc.config.NPCConfig;
import me.daoge.allaynpc.form.NPCFormHandler;
import me.daoge.allaynpc.i18n.I18nKeys;
import me.daoge.allaynpc.manager.NPCManager;
import me.daoge.allaynpc.npc.NPC;
import me.daoge.allaynpc.util.I18nUtil;
import org.allaymc.api.command.Command;
import org.allaymc.api.command.CommandResult;
import org.allaymc.api.command.SenderType;
import org.allaymc.api.command.tree.CommandContext;
import org.allaymc.api.command.tree.CommandTree;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.utils.TextFormat;

import java.util.Set;

/**
 * AllayNPC main command
 * Provides NPC creation, management, and deletion functionality
 *
 * @author daoge_cmd
 */
public class ANPCCommand extends Command {

    public ANPCCommand() {
        super("anpc", I18nKeys.COMMAND_DESCRIPTION, "allaynpc.command");
        aliases.add("npc");
    }

    @Override
    public void prepareCommandTree(CommandTree tree) {
        tree.getRoot()
                // /anpc create <name> - Create NPC
                .key("create")
                    .str("name")
                    .exec((ctx, player) -> {
                        String name = ctx.getResult(1);
                        return handleCreate(ctx, player, name);
                    }, SenderType.PLAYER)
                .root()

                // /anpc edit <name> - Edit NPC
                .key("edit")
                    .str("name")
                    .exec((ctx, player) -> {
                        String name = ctx.getResult(1);
                        return handleEdit(ctx, player, name);
                    }, SenderType.PLAYER)
                .root()

                // /anpc delete <name> - Delete NPC
                .key("delete")
                    .str("name")
                    .exec(ctx -> {
                        String name = ctx.getResult(1);
                        return handleDelete(ctx, name);
                    })
                .root()

                // /anpc list - List all NPCs
                .key("list")
                    .exec(this::handleList)
                .root()

                // /anpc tp <name> - Teleport to NPC
                .key("tp")
                    .str("name")
                    .exec((ctx, player) -> {
                        String name = ctx.getResult(1);
                        return handleTeleport(ctx, player, name);
                    }, SenderType.PLAYER)
                .root()

                // /anpc reload - Reload configuration
                .key("reload")
                    .exec(this::handleReload)
                .root()

                // /anpc skins - List all skins
                .key("skins")
                    .exec(this::handleSkins)
                .root()

                // /anpc spawn <name> - Spawn specified NPC
                .key("spawn")
                    .str("name")
                    .exec(ctx -> {
                        String name = ctx.getResult(1);
                        return handleSpawn(ctx, name);
                    })
                .root()

                // /anpc remove <name> - Remove NPC entity (without deleting config)
                .key("remove")
                    .str("name")
                    .exec(ctx -> {
                        String name = ctx.getResult(1);
                        return handleRemove(ctx, name);
                    })
                .root()

                // /anpc help - Show help
                .key("help")
                    .exec(this::handleHelp);
    }

    /**
     * Handle create command
     */
    private CommandResult handleCreate(CommandContext ctx, EntityPlayer player, String name) {
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();

        // Check if NPC already exists
        if (npcManager.hasNPC(name)) {
            player.sendMessage(TextFormat.RED + I18nUtil.tr(player, I18nKeys.COMMAND_NPC_EXISTS, name));
            return ctx.fail();
        }

        // Open creation form
        NPCFormHandler.openCreateForm(player, name);
        return ctx.success();
    }

    /**
     * Handle edit command
     */
    private CommandResult handleEdit(CommandContext ctx, EntityPlayer player, String name) {
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();

        // Check if NPC exists
        if (!npcManager.hasNPC(name)) {
            player.sendMessage(TextFormat.RED + I18nUtil.tr(player, I18nKeys.COMMAND_NPC_NOTFOUND, name));
            return ctx.fail();
        }

        // Open edit form
        NPCFormHandler.openEditForm(player, name);
        return ctx.success();
    }

    /**
     * Handle delete command
     */
    private CommandResult handleDelete(CommandContext ctx, String name) {
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();
        var sender = ctx.getSender();

        // Check if NPC exists
        if (!npcManager.hasNPC(name)) {
            sender.sendMessage(TextFormat.RED + I18nUtil.tr(sender, I18nKeys.COMMAND_NPC_NOTFOUND, name));
            return ctx.fail();
        }

        // Remove NPC entity
        npcManager.removeNPC(name);

        // Delete config file
        if (npcManager.deleteNPCConfig(name)) {
            sender.sendMessage(TextFormat.GREEN + I18nUtil.tr(sender, I18nKeys.COMMAND_NPC_DELETED, name));
            return ctx.success();
        } else {
            sender.sendMessage(TextFormat.RED + I18nUtil.tr(sender, I18nKeys.COMMAND_NPC_DELETE_FAILED));
            return ctx.fail();
        }
    }

    /**
     * Handle list command
     */
    private CommandResult handleList(CommandContext ctx) {
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();
        var sender = ctx.getSender();
        Set<String> npcNames = npcManager.getNPCNames();

        if (npcNames.isEmpty()) {
            sender.sendMessage(TextFormat.YELLOW + I18nUtil.tr(sender, I18nKeys.COMMAND_LIST_EMPTY));
            return ctx.success();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(TextFormat.GREEN).append(I18nUtil.tr(sender, I18nKeys.COMMAND_LIST_HEADER, npcNames.size())).append("\n");

        for (String name : npcNames) {
            NPCConfig config = npcManager.getNPCConfig(name);
            NPC npc = npcManager.getNPC(name);
            boolean spawned = npc != null && npc.isSpawned();

            sb.append(TextFormat.GRAY).append("- ").append(TextFormat.WHITE).append(name);
            if (config != null && config.getPosition() != null) {
                sb.append(TextFormat.GRAY).append(" @ ").append(config.getPosition().getWorld());
            }
            sb.append(spawned ? TextFormat.GREEN + " " + I18nUtil.tr(sender, I18nKeys.COMMAND_LIST_SPAWNED) : TextFormat.RED + " " + I18nUtil.tr(sender, I18nKeys.COMMAND_LIST_NOTSPAWNED));
            sb.append("\n");
        }

        sender.sendMessage(sb.toString());
        return ctx.success();
    }

    /**
     * Handle teleport command
     */
    private CommandResult handleTeleport(CommandContext ctx, EntityPlayer player, String name) {
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();
        NPCConfig config = npcManager.getNPCConfig(name);

        if (config == null || config.getPosition() == null) {
            player.sendMessage(TextFormat.RED + I18nUtil.tr(player, I18nKeys.COMMAND_NPC_NOPOSITION, name));
            return ctx.fail();
        }

        var pos = config.getPosition();

        // Get NPC's world and dimension
        var world = org.allaymc.api.server.Server.getInstance().getWorldPool().getWorld(pos.getWorld());
        if (world == null) {
            player.sendMessage(TextFormat.RED + I18nUtil.tr(player, I18nKeys.COMMAND_WORLD_NOTFOUND, pos.getWorld()));
            return ctx.fail();
        }

        var dimension = world.getOverWorld();
        if (dimension == null) {
            player.sendMessage(TextFormat.RED + I18nUtil.tr(player, I18nKeys.COMMAND_DIMENSION_NOTFOUND));
            return ctx.fail();
        }

        var targetLoc = new org.allaymc.api.math.location.Location3d(
                pos.getX(), pos.getY(), pos.getZ(),
                pos.getPitch(), pos.getYaw(),
                dimension
        );
        player.teleport(targetLoc);
        player.sendMessage(TextFormat.GREEN + I18nUtil.tr(player, I18nKeys.COMMAND_TELEPORTED, name));
        return ctx.success();
    }

    /**
     * Handle reload command
     */
    private CommandResult handleReload(CommandContext ctx) {
        var sender = ctx.getSender();
        AllayNPC.getInstance().reload();
        sender.sendMessage(TextFormat.GREEN + I18nUtil.tr(sender, I18nKeys.COMMAND_RELOADED));
        return ctx.success();
    }

    /**
     * Handle skins list command
     */
    private CommandResult handleSkins(CommandContext ctx) {
        var skinManager = AllayNPC.getInstance().getSkinManager();
        var sender = ctx.getSender();
        Set<String> skinNames = skinManager.getSkinNames();

        if (skinNames.isEmpty()) {
            sender.sendMessage(TextFormat.YELLOW + I18nUtil.tr(sender, I18nKeys.COMMAND_SKINS_EMPTY));
            return ctx.success();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(TextFormat.GREEN).append(I18nUtil.tr(sender, I18nKeys.COMMAND_SKINS_HEADER, skinNames.size())).append("\n");

        for (String name : skinNames) {
            sb.append(TextFormat.GRAY).append("- ").append(TextFormat.WHITE).append(name).append("\n");
        }

        sender.sendMessage(sb.toString());
        return ctx.success();
    }

    /**
     * Handle spawn command
     */
    private CommandResult handleSpawn(CommandContext ctx, String name) {
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();
        var sender = ctx.getSender();

        if (!npcManager.hasNPC(name)) {
            sender.sendMessage(TextFormat.RED + I18nUtil.tr(sender, I18nKeys.COMMAND_NPC_NOTFOUND, name));
            return ctx.fail();
        }

        if (npcManager.spawnNPC(name)) {
            sender.sendMessage(TextFormat.GREEN + I18nUtil.tr(sender, I18nKeys.COMMAND_NPC_SPAWNED, name));
            return ctx.success();
        } else {
            sender.sendMessage(TextFormat.RED + I18nUtil.tr(sender, I18nKeys.COMMAND_NPC_SPAWN_FAILED, name));
            return ctx.fail();
        }
    }

    /**
     * Handle remove command
     */
    private CommandResult handleRemove(CommandContext ctx, String name) {
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();
        var sender = ctx.getSender();

        if (!npcManager.hasNPC(name)) {
            sender.sendMessage(TextFormat.RED + I18nUtil.tr(sender, I18nKeys.COMMAND_NPC_NOTFOUND, name));
            return ctx.fail();
        }

        npcManager.removeNPC(name);
        sender.sendMessage(TextFormat.GREEN + I18nUtil.tr(sender, I18nKeys.COMMAND_NPC_REMOVED, name));
        return ctx.success();
    }

    /**
     * Handle help command
     */
    private CommandResult handleHelp(CommandContext ctx) {
        var sender = ctx.getSender();
        StringBuilder sb = new StringBuilder();
        sb.append(TextFormat.GREEN).append(I18nUtil.tr(sender, I18nKeys.COMMAND_HELP_TITLE)).append("\n");
        sb.append(TextFormat.YELLOW).append(I18nUtil.tr(sender, I18nKeys.COMMAND_HELP_CREATE)).append("\n");
        sb.append(TextFormat.YELLOW).append(I18nUtil.tr(sender, I18nKeys.COMMAND_HELP_EDIT)).append("\n");
        sb.append(TextFormat.YELLOW).append(I18nUtil.tr(sender, I18nKeys.COMMAND_HELP_DELETE)).append("\n");
        sb.append(TextFormat.YELLOW).append(I18nUtil.tr(sender, I18nKeys.COMMAND_HELP_LIST)).append("\n");
        sb.append(TextFormat.YELLOW).append(I18nUtil.tr(sender, I18nKeys.COMMAND_HELP_TP)).append("\n");
        sb.append(TextFormat.YELLOW).append(I18nUtil.tr(sender, I18nKeys.COMMAND_HELP_SPAWN)).append("\n");
        sb.append(TextFormat.YELLOW).append(I18nUtil.tr(sender, I18nKeys.COMMAND_HELP_REMOVE)).append("\n");
        sb.append(TextFormat.YELLOW).append(I18nUtil.tr(sender, I18nKeys.COMMAND_HELP_SKINS)).append("\n");
        sb.append(TextFormat.YELLOW).append(I18nUtil.tr(sender, I18nKeys.COMMAND_HELP_RELOAD)).append("\n");

        sender.sendMessage(sb.toString());
        return ctx.success();
    }
}
