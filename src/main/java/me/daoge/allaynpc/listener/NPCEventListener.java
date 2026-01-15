package me.daoge.allaynpc.listener;

import lombok.extern.slf4j.Slf4j;
import me.daoge.allaynpc.AllayNPC;
import me.daoge.allaynpc.action.CommandAction;
import me.daoge.allaynpc.action.DialogAction;
import me.daoge.allaynpc.action.MessageAction;
import me.daoge.allaynpc.action.NPCAction;
import me.daoge.allaynpc.config.NPCConfig;
import me.daoge.allaynpc.manager.NPCManager;
import me.daoge.allaynpc.npc.NPC;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.eventbus.EventHandler;
import org.allaymc.api.eventbus.event.entity.EntityDamageEvent;
import org.allaymc.api.eventbus.event.player.PlayerInteractEntityEvent;
import org.allaymc.api.eventbus.event.world.ChunkLoadEvent;

/**
 * NPC event listener
 * Handles NPC-related events
 *
 * @author daoge_cmd
 */
@Slf4j
public class NPCEventListener {

    /**
     * Handle player interact entity event (right-click)
     *
     * @param event event object
     */
    @EventHandler
    private void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        EntityPlayer player = event.getPlayer();
        var entity = event.getEntity();

        // Get NPC manager
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();

        // Check if the clicked entity is an NPC
        NPC npc = npcManager.getNPCByEntity(entity);
        if (npc == null) {
            return;
        }

        // Cancel default interaction
        event.setCancelled(true);

        // Handle NPC click
        handleNPCClick(player, npc);
    }

    /**
     * Handle entity damage event (left-click attack)
     *
     * @param event event object
     */
    @EventHandler
    private void onEntityDamage(EntityDamageEvent event) {
        var entity = event.getEntity();
        var damageContainer = event.getDamageContainer();

        // Check if the attacker is a player
        if (!(damageContainer.getAttacker() instanceof EntityPlayer player)) {
            return;
        }

        // Get NPC manager
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();

        // Check if the damaged entity is an NPC
        NPC npc = npcManager.getNPCByEntity(entity);
        if (npc == null) {
            return;
        }

        // Cancel damage to NPC
        event.setCancelled(true);

        // Handle NPC click
        handleNPCClick(player, npc);
    }

    /**
     * Handle NPC click (both left and right click)
     *
     * @param player player who clicked
     * @param npc    NPC that was clicked
     */
    private void handleNPCClick(EntityPlayer player, NPC npc) {
        NPCManager npcManager = AllayNPC.getInstance().getNpcManager();

        // Check click cooldown
        if (npcManager.isOnCooldown(player, npc.getName())) {
            return;
        }

        // Record click time
        npcManager.recordClick(player, npc.getName());

        // Execute NPC actions
        executeNPCActions(player, npc);
    }

    /**
     * Execute all actions of the NPC
     *
     * @param player player who clicked
     * @param npc    NPC that was clicked
     */
    private void executeNPCActions(EntityPlayer player, NPC npc) {
        NPCConfig config = npc.getConfig();
        if (config.getActions() == null || config.getActions().isEmpty()) {
            return;
        }

        for (NPCConfig.ActionConfig actionConfig : config.getActions()) {
            NPCAction action = createAction(actionConfig);
            if (action != null) {
                try {
                    action.execute(player, npc);
                } catch (Exception e) {
                    log.error("Failed to execute NPC action: {}", actionConfig.getType(), e);
                }
            }
        }
    }

    /**
     * Create action instance from config
     *
     * @param config action config
     * @return action instance
     */
    private NPCAction createAction(NPCConfig.ActionConfig config) {
        if (config == null || config.getType() == null) {
            return null;
        }

        return switch (config.getType()) {
            case COMMAND -> new CommandAction(config.getValue(), config.isAsPlayer());
            case DIALOG -> new DialogAction(config.getValue());
            case MESSAGE -> new MessageAction(config.getValue());
        };
    }

    /**
     * Handle chunk load event
     *
     * @param event event object
     */
    @EventHandler
    private void onChunkLoad(ChunkLoadEvent event) {
        var dimension = event.getDimension();
        var chunk = event.getChunk();

        String worldName = dimension.getWorld().getName();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        // Notify NPC manager that chunk has been loaded
        AllayNPC.getInstance().getNpcManager().onChunkLoad(worldName, chunkX, chunkZ);
    }
}
