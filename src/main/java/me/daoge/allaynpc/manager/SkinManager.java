package me.daoge.allaynpc.manager;

import lombok.extern.slf4j.Slf4j;
import me.daoge.allaynpc.util.SkinUtil;
import org.allaymc.api.player.Skin;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Skin Manager
 * Responsible for loading, caching and managing NPC skins
 *
 * @author daoge_cmd
 */
@Slf4j
public class SkinManager {

    /**
     * Skins directory path
     */
    private final Path skinsDirectory;

    /**
     * Skin cache (skin name -> skin object)
     */
    private final Map<String, Skin> skins = new HashMap<>();

    /**
     * Default skin
     */
    private Skin defaultSkin;

    /**
     * Create skin manager
     *
     * @param skinsDirectory skins directory path
     */
    public SkinManager(Path skinsDirectory) {
        this.skinsDirectory = skinsDirectory;
        this.defaultSkin = SkinUtil.createDefaultSkin();
    }

    /**
     * Load all skins
     */
    public void loadAllSkins() {
        skins.clear();

        if (!Files.exists(skinsDirectory)) {
            log.warn("Skins directory does not exist: {}", skinsDirectory);
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(skinsDirectory)) {
            for (Path path : stream) {
                String fileName = path.getFileName().toString();

                if (Files.isDirectory(path)) {
                    // Load skin from folder
                    loadSkinFromFolder(path, fileName);
                } else if (fileName.endsWith(".png")) {
                    // Load skin from single PNG file
                    String skinName = fileName.replace(".png", "").replace("_slim", "");
                    loadSkinFromFile(path, skinName);
                }
            }
        } catch (IOException e) {
            log.error("Failed to load skins from directory: {}", skinsDirectory, e);
        }

        log.info("Loaded {} skins", skins.size());
    }

    /**
     * Load skin from folder
     *
     * @param folder   skin folder
     * @param skinName skin name
     */
    private void loadSkinFromFolder(Path folder, String skinName) {
        Skin skin = SkinUtil.loadSkinFromFolder(folder, skinName);
        if (skin != null) {
            skins.put(skinName, skin);
            log.debug("Loaded skin from folder: {}", skinName);
        } else {
            log.warn("Failed to load skin from folder: {}", skinName);
        }
    }

    /**
     * Load skin from file
     *
     * @param file     skin file
     * @param skinName skin name
     */
    private void loadSkinFromFile(Path file, String skinName) {
        Skin skin = SkinUtil.loadSkinFromFile(file, skinName);
        if (skin != null) {
            skins.put(skinName, skin);
            log.debug("Loaded skin from file: {}", skinName);
        } else {
            log.warn("Failed to load skin from file: {}", skinName);
        }
    }

    /**
     * Get skin by name
     *
     * @param name skin name
     * @return skin object, returns default skin if not exists
     */
    public Skin getSkin(String name) {
        return skins.getOrDefault(name, defaultSkin);
    }

    /**
     * Get skin by name, returns null if not exists
     *
     * @param name skin name
     * @return skin object, null if not exists
     */
    @Nullable
    public Skin getSkinOrNull(String name) {
        return skins.get(name);
    }

    /**
     * Check if skin exists
     *
     * @param name skin name
     * @return whether exists
     */
    public boolean hasSkin(String name) {
        return skins.containsKey(name);
    }

    /**
     * Get all skin names
     *
     * @return skin name set
     */
    public Set<String> getSkinNames() {
        return skins.keySet();
    }

    /**
     * Get all skins
     *
     * @return skin collection
     */
    public Collection<Skin> getAllSkins() {
        return skins.values();
    }

    /**
     * Get skin count
     *
     * @return skin count
     */
    public int getSkinCount() {
        return skins.size();
    }

    /**
     * Get default skin
     *
     * @return default skin object
     */
    public Skin getDefaultSkin() {
        return defaultSkin;
    }

    /**
     * Set default skin
     *
     * @param defaultSkin default skin object
     */
    public void setDefaultSkin(Skin defaultSkin) {
        this.defaultSkin = defaultSkin;
    }

    /**
     * Register skin
     *
     * @param name skin name
     * @param skin skin object
     */
    public void registerSkin(String name, Skin skin) {
        skins.put(name, skin);
    }

    /**
     * Remove skin
     *
     * @param name skin name
     * @return removed skin object, null if not exists
     */
    @Nullable
    public Skin removeSkin(String name) {
        return skins.remove(name);
    }
}
