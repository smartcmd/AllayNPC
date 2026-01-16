package me.daoge.allaynpc.manager;

import lombok.extern.slf4j.Slf4j;
import me.daoge.allaynpc.util.SkinUtil;
import org.allaymc.api.player.Skin;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cape Manager
 * Responsible for loading, caching and managing NPC capes
 *
 * @author daoge_cmd
 */
@Slf4j
public class CapeManager {

    /**
     * Capes directory path
     */
    private final Path capesDirectory;

    /**
     * Cape cache (cape name -> cape ImageData)
     */
    private volatile Map<String, Skin.ImageData> capes = new ConcurrentHashMap<>();

    /**
     * Create cape manager
     *
     * @param capesDirectory capes directory path
     */
    public CapeManager(Path capesDirectory) {
        this.capesDirectory = capesDirectory;
    }

    /**
     * Load all capes using atomic replacement pattern.
     */
    public void loadAllCapes() {
        // Create new map for atomic replacement
        Map<String, Skin.ImageData> newCapes = new ConcurrentHashMap<>();

        if (!Files.exists(capesDirectory)) {
            log.warn("Capes directory does not exist: {}", capesDirectory);
            // Atomically replace with empty map
            this.capes = newCapes;
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(capesDirectory, "*.png")) {
            for (Path path : stream) {
                String fileName = path.getFileName().toString();
                String capeName = fileName.replace(".png", "");
                loadCape(path, capeName, newCapes);
            }
        } catch (IOException e) {
            log.error("Failed to load capes from directory: {}", capesDirectory, e);
        }

        // Atomic replacement - other threads will see either old or new map, never empty
        this.capes = newCapes;
        log.info("Loaded {} capes", newCapes.size());
    }

    /**
     * Load a single cape from file into target map
     *
     * @param file      cape file path
     * @param capeName  cape name
     * @param targetMap target map to put cape into
     */
    private void loadCape(Path file, String capeName, Map<String, Skin.ImageData> targetMap) {
        try {
            BufferedImage image = ImageIO.read(file.toFile());
            if (image == null) {
                log.warn("Failed to read cape image: {}", file);
                return;
            }

            // Convert image to RGBA byte array (reuse SkinUtil's method)
            byte[] capeData = SkinUtil.imageToRGBA(image);

            // Validate cape data size
            int expectedSize = image.getWidth() * image.getHeight() * 4;
            if (capeData.length != expectedSize) {
                log.warn("Invalid cape data size for {}: expected {}, got {}", capeName, expectedSize, capeData.length);
                return;
            }

            // Create ImageData
            Skin.ImageData imageData = new Skin.ImageData(image.getWidth(), image.getHeight(), capeData);

            targetMap.put(capeName, imageData);
            log.debug("Loaded cape: {} ({}x{})", capeName, image.getWidth(), image.getHeight());

        } catch (IOException e) {
            log.error("Failed to load cape from file: {}", file, e);
        }
    }

    /**
     * Get cape by name
     *
     * @param name cape name
     * @return cape ImageData, null if not exists
     */
    @Nullable
    public Skin.ImageData getCape(String name) {
        return capes.get(name);
    }

    /**
     * Check if cape exists
     *
     * @param name cape name
     * @return whether exists
     */
    public boolean hasCape(String name) {
        return capes.containsKey(name);
    }

    /**
     * Get all cape names
     *
     * @return cape name set
     */
    public Set<String> getCapeNames() {
        return capes.keySet();
    }

    /**
     * Get all capes
     *
     * @return cape collection
     */
    public Collection<Skin.ImageData> getAllCapes() {
        return capes.values();
    }

    /**
     * Get cape count
     *
     * @return cape count
     */
    public int getCapeCount() {
        return capes.size();
    }
}
