package me.daoge.allaynpc.action;

import me.daoge.allaynpc.npc.NPC;
import org.allaymc.api.entity.interfaces.EntityPlayer;

/**
 * NPC action interface
 * Defines actions that can be executed when NPC is clicked
 *
 * @author daoge_cmd
 */
public interface NPCAction {

    /**
     * Execute the action
     *
     * @param player player who clicked the NPC
     * @param npc    NPC that was clicked
     */
    void execute(EntityPlayer player, NPC npc);
}
