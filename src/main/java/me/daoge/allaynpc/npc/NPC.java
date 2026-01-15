package me.daoge.allaynpc.npc;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.daoge.allaynpc.AllayNPC;
import me.daoge.allaynpc.config.NPCConfig;
import org.allaymc.api.container.ContainerTypes;
import org.allaymc.api.entity.EntityInitInfo;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.entity.type.EntityTypes;
import org.allaymc.api.player.Player;
import org.allaymc.api.item.ItemStack;
import org.allaymc.api.math.location.Location3d;
import org.allaymc.api.math.location.Location3dc;
import org.allaymc.api.player.GameMode;
import org.allaymc.api.player.Skin;
import org.allaymc.api.registry.Registries;
import org.allaymc.api.server.Server;
import org.allaymc.api.utils.TextFormat;
import org.allaymc.api.utils.identifier.Identifier;
import org.allaymc.api.world.Dimension;
import org.allaymc.api.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.UUID;

/**
 * NPC entity wrapper class
 * Wraps EntityPlayer entity and provides NPC-specific functionality
 *
 * @author daoge_cmd
 */
@Slf4j
@Getter
public class NPC {

    /**
     * NPC configuration
     */
    private final NPCConfig config;

    /**
     * EntityPlayer entity
     */
    private EntityPlayer entity;

    /**
     * Last emote play time (tick)
     */
    private long lastEmoteTick = 0;

    /**
     * Create NPC
     *
     * @param config NPC configuration
     */
    public NPC(NPCConfig config) {
        this.config = config;
    }

    /**
     * Spawn NPC into world
     *
     * @return whether spawn was successful
     */
    public boolean spawn() {
        if (isSpawned()) {
            log.warn("NPC {} is already spawned", config.getName());
            return false;
        }

        // Get world and dimension
        NPCConfig.PositionConfig pos = config.getPosition();
        if (pos == null) {
            log.error("NPC {} has no position config", config.getName());
            return false;
        }

        World world = Server.getInstance().getWorldPool().getWorld(pos.getWorld());
        if (world == null) {
            log.error("World {} not found for NPC {}", pos.getWorld(), config.getName());
            return false;
        }

        // Use getOverWorld() to get overworld dimension
        Dimension dimension = world.getOverWorld();
        if (dimension == null) {
            log.error("Dimension not found for NPC {}", config.getName());
            return false;
        }

        try {
            // Create EntityPlayer entity
            EntityInitInfo initInfo = EntityInitInfo.builder()
                    .dimension(dimension)
                    .pos(pos.getX(), pos.getY(), pos.getZ())
                    .rot(pos.getYaw(), pos.getPitch())
                    .build();

            entity = EntityTypes.PLAYER.createEntity(initInfo);

            // Set display name (use colorize to replace color codes)
            String displayName = TextFormat.colorize(config.getDisplayName());
            entity.setDisplayName(displayName);

            // Set name tag
            entity.setNameTag(displayName);
            entity.setNameTagAlwaysShow(config.isAlwaysShowName());

            // Set NPC as immobile (can look around but cannot move)
            entity.setImmobile(true);

            // Set gamemode to survival so EntityDamageEvent can be triggered
            entity.setGameMode(GameMode.SURVIVAL);

            // Apply skin BEFORE spawning
            applySkin();

            // Apply held item
            applyHeldItem();

            // Apply armor
            applyArmor();

            // Add entity to world (engine handles spawning to players automatically)
            dimension.getEntityManager().addEntity(entity, () -> {
                log.debug("NPC {} spawned at {} in world {}", config.getName(), pos.toVector3d(), pos.getWorld());
            });

            return true;

        } catch (Exception e) {
            log.error("Failed to spawn NPC {}", config.getName(), e);
            return false;
        }
    }

    /**
     * Remove NPC
     */
    public void remove() {
        if (entity == null) {
            return;
        }

        try {
            // Engine handles despawning from players automatically
            Dimension dimension = entity.getDimension();
            if (dimension != null) {
                dimension.getEntityManager().removeEntity(entity);
            }
        } catch (Exception e) {
            log.error("Failed to remove NPC {}", config.getName(), e);
        }

        entity = null;
    }

    /**
     * Apply skin to NPC
     */
    private void applySkin() {
        if (entity == null) return;

        String skinName = config.getSkin();
        Skin skin;

        if (skinName != null && !skinName.isEmpty()) {
            skin = AllayNPC.getInstance().getSkinManager().getSkin(skinName);
        } else {
            skin = AllayNPC.getInstance().getSkinManager().getDefaultSkin();
        }

        if (skin != null) {
            entity.setSkin(skin);
        }
    }

    /**
     * Apply held item to NPC
     */
    private void applyHeldItem() {
        if (entity == null) return;

        String heldItemId = config.getHeldItem();
        if (heldItemId == null || heldItemId.isEmpty()) {
            return;
        }

        try {
            ItemStack itemStack = createItemStack(heldItemId);
            if (itemStack != null) {
                entity.setItemInHand(itemStack);
            }
        } catch (Exception e) {
            log.warn("Failed to set held item for NPC {}: {}", config.getName(), heldItemId, e);
        }
    }

    /**
     * Apply armor to NPC
     */
    private void applyArmor() {
        if (entity == null) return;

        NPCConfig.ArmorConfig armor = config.getArmor();
        if (armor == null || !armor.hasAnyArmor()) {
            return;
        }

        try {
            var armorContainer = entity.getContainer(ContainerTypes.ARMOR);

            // Set helmet
            if (!armor.getHelmet().isEmpty()) {
                ItemStack helmet = createItemStack(armor.getHelmet());
                if (helmet != null) {
                    armorContainer.setHelmet(helmet);
                }
            }

            // Set chestplate
            if (!armor.getChestplate().isEmpty()) {
                ItemStack chestplate = createItemStack(armor.getChestplate());
                if (chestplate != null) {
                    armorContainer.setChestplate(chestplate);
                }
            }

            // Set leggings
            if (!armor.getLeggings().isEmpty()) {
                ItemStack leggings = createItemStack(armor.getLeggings());
                if (leggings != null) {
                    armorContainer.setLeggings(leggings);
                }
            }

            // Set boots
            if (!armor.getBoots().isEmpty()) {
                ItemStack boots = createItemStack(armor.getBoots());
                if (boots != null) {
                    armorContainer.setBoots(boots);
                }
            }

        } catch (Exception e) {
            log.warn("Failed to set armor for NPC {}", config.getName(), e);
        }
    }

    /**
     * Create ItemStack from item ID
     *
     * @param itemId item ID
     * @return ItemStack object, null if creation failed
     */
    @Nullable
    private ItemStack createItemStack(String itemId) {
        try {
            Identifier identifier = new Identifier(itemId);
            var itemType = Registries.ITEMS.get(identifier);
            if (itemType != null) {
                return itemType.createItemStack(1);
            }
        } catch (Exception e) {
            log.warn("Invalid item ID: {}", itemId);
        }
        return null;
    }

    /**
     * Play emote action
     */
    public void playEmote() {
        if (!isSpawned()) return;

        NPCConfig.EmoteConfig emoteConfig = config.getEmote();
        if (emoteConfig == null || !emoteConfig.isEnabled()) {
            return;
        }

        try {
            UUID emoteUuid = UUID.fromString(emoteConfig.getId());
            // Send emote to viewers only
            entity.forEachViewers(viewer -> viewer.viewPlayerEmote(entity, emoteUuid));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid emote UUID for NPC {}: {}", config.getName(), emoteConfig.getId());
        }
    }

    /**
     * Check if emote should be played
     *
     * @param currentTick current tick
     * @return whether should play
     */
    public boolean shouldPlayEmote(long currentTick) {
        NPCConfig.EmoteConfig emoteConfig = config.getEmote();
        if (emoteConfig == null || !emoteConfig.isEnabled()) {
            return false;
        }

        if (currentTick - lastEmoteTick >= emoteConfig.getInterval()) {
            lastEmoteTick = currentTick;
            return true;
        }

        return false;
    }

    /**
     * Make NPC look at all viewers individually.
     * Each player will see the NPC looking directly at them using WorldViewer.viewEntityLocation().
     */
    public void lookAtNearestPlayer() {
        if (!isSpawned() || !config.isLookAtPlayer()) return;

        Location3dc npcLoc = entity.getLocation();

        for (var viewer : entity.getViewers()) {
            // viewer is WorldViewer (Player), not EntityPlayer
            // Need to get the controlled EntityPlayer from Player
            if (!(viewer instanceof Player player)) continue;

            EntityPlayer playerEntity = player.getControlledEntity();
            if (playerEntity == null) continue;

            try {
                Location3dc playerLoc = playerEntity.getLocation();

                // Calculate direction vector (target at player's eye level)
                double dx = playerLoc.x() - npcLoc.x();
                double dy = (playerLoc.y() + 1.62) - (npcLoc.y() + 1.62); // Eye level
                double dz = playerLoc.z() - npcLoc.z();

                // Calculate horizontal distance
                double horizontalDist = Math.sqrt(dx * dx + dz * dz);

                // Calculate yaw (horizontal rotation)
                // In Minecraft, yaw 0 is south (+Z), and increases counterclockwise
                double yaw = Math.toDegrees(Math.atan2(-dx, dz));

                // Calculate pitch (vertical rotation)
                double pitch = -Math.toDegrees(Math.atan2(dy, horizontalDist));

                // Clamp pitch to prevent extreme angles
                pitch = Math.max(-89, Math.min(89, pitch));

                // Create location with updated rotation for this specific viewer
                Location3d viewLocation = new Location3d(
                        npcLoc.x(), npcLoc.y(), npcLoc.z(),
                        pitch, yaw,
                        npcLoc.dimension()
                );

                // Send individualized location to this viewer only
                // The lastSentLocation is passed as a new object to avoid modifying the actual entity state
                player.viewEntityLocation(entity, new Location3d(npcLoc), viewLocation, false);

            } catch (Exception e) {
                log.warn("Failed to update look direction for viewer {} on NPC {}", player.getOriginName(), config.getName(), e);
            }
        }
    }

    /**
     * Check if NPC is spawned
     *
     * @return true if spawned
     */
    public boolean isSpawned() {
        return entity != null && entity.isSpawned();
    }

    /**
     * Get NPC name
     */
    public String getName() {
        return config.getName();
    }

    /**
     * Get NPC display name
     */
    public String getDisplayName() {
        return config.getDisplayName();
    }

    /**
     * Get NPC world name
     */
    public String getWorldName() {
        return config.getPosition() != null ? config.getPosition().getWorld() : "";
    }

    /**
     * Get NPC position
     */
    @Nullable
    public Vector3d getPosition() {
        return config.getPosition() != null ? config.getPosition().toVector3d() : null;
    }

    /**
     * Get NPC chunk X coordinate
     */
    public int getChunkX() {
        return config.getPosition() != null ? config.getPosition().getChunkX() : 0;
    }

    /**
     * Get NPC chunk Z coordinate
     */
    public int getChunkZ() {
        return config.getPosition() != null ? config.getPosition().getChunkZ() : 0;
    }
}
