package me.daoge.allaynpc.command;

import me.daoge.allaynpc.AllayNPC;
import me.daoge.allaynpc.config.NPCConfig;
import me.daoge.allaynpc.form.NPCFormHandler;
import me.daoge.allaynpc.manager.NPCManager;
import me.daoge.allaynpc.npc.NPC;
import org.allaymc.api.command.Command;
import org.allaymc.api.command.CommandResult;
import org.allaymc.api.command.SenderType;
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
        super("anpc", "AllayNPC management command", "allaynpc.command");
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
                        return handleCreate(player, name);
                    }, SenderType.PLAYER)
                .root()

                // /anpc edit <name> - Edit NPC
                .key("edit")
                    .str("name")
                    .exec((ctx, player) -> {
                        String name = ctx.getResult(1);
                        return handleEdit(player, name);
                    }, SenderType.PLAYER)
                .root()

                // /anpc delete <name> - Delete NPC
                .key("delete")
                    .str("name")
                    .exec(ctx -> {
                        String name = ctx.getResult(1);
                        return handleDelete(ctx.getSender(), name);
                    })
                .root()

                // /anpc list - List all NPCs
                .key("list")
                    .exec(ctx -> handleList(ctx.getSender()))
                .root()

                // /anpc tp <name> - Teleport to NPC
                .key("tp")
                    .str("name")
                    .exec((ctx, player) -> {
                        String name = ctx.getResult(1);
                        return handleTeleport(player, name);
                    }, SenderType.PLAYER)
                .root()

                // /anpc reload - Reload configuration
                .key("reload")
                    .exec(ctx -> handleReload(ctx.getSender()))
                .root()

                // /anpc skins - List all skins
                .key("skins")
                    .exec(ctx -> handleSkins(ctx.getSender()))
                .root()

                // /anpc spawn <name> - Spawn specified NPC
                .key("spawn")
                    .str("name")
                    .exec(ctx -> {
                        String name = ctx.getResult(1);
                        return handleSpawn(ctx.getSender(), name);
                    })
                .root()

                // /anpc remove <name> - Remove NPC entity (without deleting config)
                .key("remove")
                    .str("name")
                    .exec(ctx -> {
                        String name = ctx.getResult(1);
                        return handleRemove(ctx.getSender(), name);
                    })
                .root()

                // /anpc help - Show help
                .key("help")
                    .exec(ctx -> handleHelp(ctx.getSender()));
    }

    /**
     * Handle create command
     */
    private CommandResult handleCreate(EntityPlayer player, String name) {
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();

        // Check if NPC already exists
        if (npcManager.hasNPC(name)) {
            player.sendMessage(TextFormat.RED + "NPC '" + name + "' already exists!");
            return CommandResult.fail();
        }

        // Open creation form
        NPCFormHandler.openCreateForm(player, name);
        return CommandResult.success(null);
    }

    /**
     * Handle edit command
     */
    private CommandResult handleEdit(EntityPlayer player, String name) {
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();

        // Check if NPC exists
        if (!npcManager.hasNPC(name)) {
            player.sendMessage(TextFormat.RED + "NPC '" + name + "' not found!");
            return CommandResult.fail();
        }

        // Open edit form
        NPCFormHandler.openEditForm(player, name);
        return CommandResult.success(null);
    }

    /**
     * Handle delete command
     */
    private CommandResult handleDelete(org.allaymc.api.command.CommandSender sender, String name) {
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();

        // Check if NPC exists
        if (!npcManager.hasNPC(name)) {
            sender.sendMessage(TextFormat.RED + "NPC '" + name + "' not found!");
            return CommandResult.fail();
        }

        // Remove NPC entity
        npcManager.removeNPC(name);

        // Delete config file
        if (npcManager.deleteNPCConfig(name)) {
            sender.sendMessage(TextFormat.GREEN + "NPC '" + name + "' has been deleted!");
            return CommandResult.success(null);
        } else {
            sender.sendMessage(TextFormat.RED + "Failed to delete NPC config file!");
            return CommandResult.fail();
        }
    }

    /**
     * Handle list command
     */
    private CommandResult handleList(org.allaymc.api.command.CommandSender sender) {
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();
        Set<String> npcNames = npcManager.getNPCNames();

        if (npcNames.isEmpty()) {
            sender.sendMessage(TextFormat.YELLOW + "No NPCs found.");
            return CommandResult.success(null);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(TextFormat.GREEN).append("NPCs (").append(npcNames.size()).append("):\n");

        for (String name : npcNames) {
            NPCConfig config = npcManager.getNPCConfig(name);
            NPC npc = npcManager.getNPC(name);
            boolean spawned = npc != null && npc.isSpawned();

            sb.append(TextFormat.GRAY).append("- ").append(TextFormat.WHITE).append(name);
            if (config != null && config.getPosition() != null) {
                sb.append(TextFormat.GRAY).append(" @ ").append(config.getPosition().getWorld());
            }
            sb.append(spawned ? TextFormat.GREEN + " [Spawned]" : TextFormat.RED + " [Not Spawned]");
            sb.append("\n");
        }

        sender.sendMessage(sb.toString());
        return CommandResult.success(null);
    }

    /**
     * Handle teleport command
     */
    private CommandResult handleTeleport(EntityPlayer player, String name) {
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();
        NPCConfig config = npcManager.getNPCConfig(name);

        if (config == null || config.getPosition() == null) {
            player.sendMessage(TextFormat.RED + "NPC '" + name + "' not found or has no position!");
            return CommandResult.fail();
        }

        var pos = config.getPosition();
        var targetLoc = new org.allaymc.api.math.location.Location3d(
                pos.getX(), pos.getY(), pos.getZ(),
                pos.getPitch(), pos.getYaw(),
                player.getLocation().dimension()
        );
        player.teleport(targetLoc);
        player.sendMessage(TextFormat.GREEN + "Teleported to NPC '" + name + "'!");
        return CommandResult.success(null);
    }

    /**
     * Handle reload command
     */
    private CommandResult handleReload(org.allaymc.api.command.CommandSender sender) {
        AllayNPC.getInstance().reload();
        sender.sendMessage(TextFormat.GREEN + "AllayNPC reloaded!");
        return CommandResult.success(null);
    }

    /**
     * Handle skins list command
     */
    private CommandResult handleSkins(org.allaymc.api.command.CommandSender sender) {
        var skinManager = AllayNPC.getInstance().getSkinManager();
        Set<String> skinNames = skinManager.getSkinNames();

        if (skinNames.isEmpty()) {
            sender.sendMessage(TextFormat.YELLOW + "No skins found.");
            return CommandResult.success(null);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(TextFormat.GREEN).append("Available Skins (").append(skinNames.size()).append("):\n");

        for (String name : skinNames) {
            sb.append(TextFormat.GRAY).append("- ").append(TextFormat.WHITE).append(name).append("\n");
        }

        sender.sendMessage(sb.toString());
        return CommandResult.success(null);
    }

    /**
     * Handle spawn command
     */
    private CommandResult handleSpawn(org.allaymc.api.command.CommandSender sender, String name) {
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();

        if (!npcManager.hasNPC(name)) {
            sender.sendMessage(TextFormat.RED + "NPC '" + name + "' not found!");
            return CommandResult.fail();
        }

        if (npcManager.spawnNPC(name)) {
            sender.sendMessage(TextFormat.GREEN + "NPC '" + name + "' has been spawned!");
            return CommandResult.success(null);
        } else {
            sender.sendMessage(TextFormat.RED + "Failed to spawn NPC '" + name + "'!");
            return CommandResult.fail();
        }
    }

    /**
     * Handle remove command
     */
    private CommandResult handleRemove(org.allaymc.api.command.CommandSender sender, String name) {
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();

        if (!npcManager.hasNPC(name)) {
            sender.sendMessage(TextFormat.RED + "NPC '" + name + "' not found!");
            return CommandResult.fail();
        }

        npcManager.removeNPC(name);
        sender.sendMessage(TextFormat.GREEN + "NPC '" + name + "' has been removed!");
        return CommandResult.success(null);
    }

    /**
     * Handle help command
     */
    private CommandResult handleHelp(org.allaymc.api.command.CommandSender sender) {
        StringBuilder sb = new StringBuilder();
        sb.append(TextFormat.GREEN).append("=== AllayNPC Commands ===\n");
        sb.append(TextFormat.YELLOW).append("/anpc create <name>").append(TextFormat.WHITE).append(" - Create a new NPC\n");
        sb.append(TextFormat.YELLOW).append("/anpc edit <name>").append(TextFormat.WHITE).append(" - Edit an existing NPC\n");
        sb.append(TextFormat.YELLOW).append("/anpc delete <name>").append(TextFormat.WHITE).append(" - Delete an NPC\n");
        sb.append(TextFormat.YELLOW).append("/anpc list").append(TextFormat.WHITE).append(" - List all NPCs\n");
        sb.append(TextFormat.YELLOW).append("/anpc tp <name>").append(TextFormat.WHITE).append(" - Teleport to an NPC\n");
        sb.append(TextFormat.YELLOW).append("/anpc spawn <name>").append(TextFormat.WHITE).append(" - Spawn an NPC\n");
        sb.append(TextFormat.YELLOW).append("/anpc remove <name>").append(TextFormat.WHITE).append(" - Remove an NPC entity\n");
        sb.append(TextFormat.YELLOW).append("/anpc skins").append(TextFormat.WHITE).append(" - List available skins\n");
        sb.append(TextFormat.YELLOW).append("/anpc reload").append(TextFormat.WHITE).append(" - Reload configuration\n");

        sender.sendMessage(sb.toString());
        return CommandResult.success(null);
    }
}
