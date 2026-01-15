package me.daoge.allaynpc.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.allaymc.api.player.Skin;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Skin utility class
 * Provides utility methods for skin loading and processing
 *
 * @author daoge_cmd
 */
@Slf4j
@UtilityClass
public class SkinUtil {

    /**
     * Default geometry data for Steve skin
     */
    public static final String STEVE_GEOMETRY = "{\"format_version\":\"1.12.0\",\"minecraft:geometry\":[{\"bones\":[{\"name\":\"body\",\"parent\":\"waist\",\"pivot\":[0.0,24.0,0.0]},{\"name\":\"waist\",\"pivot\":[0.0,12.0,0.0]},{\"cubes\":[{\"origin\":[-5.0,8.0,3.0],\"size\":[10,16,1],\"uv\":[0,0]}],\"name\":\"cape\",\"parent\":\"body\",\"pivot\":[0.0,24.0,3.0],\"rotation\":[0.0,180.0,0.0]}],\"description\":{\"identifier\":\"geometry.cape\",\"texture_height\":32,\"texture_width\":64}},{\"bones\":[{\"name\":\"root\",\"pivot\":[0.0,0.0,0.0]},{\"cubes\":[{\"origin\":[-4.0,12.0,-2.0],\"size\":[8,12,4],\"uv\":[16,16]}],\"name\":\"body\",\"parent\":\"waist\",\"pivot\":[0.0,24.0,0.0]},{\"name\":\"waist\",\"parent\":\"root\",\"pivot\":[0.0,12.0,0.0]},{\"cubes\":[{\"origin\":[-4.0,24.0,-4.0],\"size\":[8,8,8],\"uv\":[0,0]}],\"name\":\"head\",\"parent\":\"body\",\"pivot\":[0.0,24.0,0.0]},{\"name\":\"cape\",\"parent\":\"body\",\"pivot\":[0.0,24,3.0]},{\"cubes\":[{\"inflate\":0.50,\"origin\":[-4.0,24.0,-4.0],\"size\":[8,8,8],\"uv\":[32,0]}],\"name\":\"hat\",\"parent\":\"head\",\"pivot\":[0.0,24.0,0.0]},{\"cubes\":[{\"origin\":[4.0,12.0,-2.0],\"size\":[4,12,4],\"uv\":[32,48]}],\"name\":\"leftArm\",\"parent\":\"body\",\"pivot\":[5.0,22.0,0.0]},{\"cubes\":[{\"inflate\":0.250,\"origin\":[4.0,12.0,-2.0],\"size\":[4,12,4],\"uv\":[48,48]}],\"name\":\"leftSleeve\",\"parent\":\"leftArm\",\"pivot\":[5.0,22.0,0.0]},{\"name\":\"leftItem\",\"parent\":\"leftArm\",\"pivot\":[6.0,15.0,1.0]},{\"cubes\":[{\"origin\":[-8.0,12.0,-2.0],\"size\":[4,12,4],\"uv\":[40,16]}],\"name\":\"rightArm\",\"parent\":\"body\",\"pivot\":[-5.0,22.0,0.0]},{\"cubes\":[{\"inflate\":0.250,\"origin\":[-8.0,12.0,-2.0],\"size\":[4,12,4],\"uv\":[40,32]}],\"name\":\"rightSleeve\",\"parent\":\"rightArm\",\"pivot\":[-5.0,22.0,0.0]},{\"locators\":{\"lead_hold\":[-6,15,1]},\"name\":\"rightItem\",\"parent\":\"rightArm\",\"pivot\":[-6,15,1]},{\"cubes\":[{\"origin\":[-0.10,0.0,-2.0],\"size\":[4,12,4],\"uv\":[16,48]}],\"name\":\"leftLeg\",\"parent\":\"root\",\"pivot\":[1.90,12.0,0.0]},{\"cubes\":[{\"inflate\":0.250,\"origin\":[-0.10,0.0,-2.0],\"size\":[4,12,4],\"uv\":[0,48]}],\"name\":\"leftPants\",\"parent\":\"leftLeg\",\"pivot\":[1.90,12.0,0.0]},{\"cubes\":[{\"origin\":[-3.90,0.0,-2.0],\"size\":[4,12,4],\"uv\":[0,16]}],\"name\":\"rightLeg\",\"parent\":\"root\",\"pivot\":[-1.90,12.0,0.0]},{\"cubes\":[{\"inflate\":0.250,\"origin\":[-3.90,0.0,-2.0],\"size\":[4,12,4],\"uv\":[0,32]}],\"name\":\"rightPants\",\"parent\":\"rightLeg\",\"pivot\":[-1.90,12.0,0.0]},{\"cubes\":[{\"inflate\":0.250,\"origin\":[-4.0,12.0,-2.0],\"size\":[8,12,4],\"uv\":[16,32]}],\"name\":\"jacket\",\"parent\":\"body\",\"pivot\":[0.0,24.0,0.0]}],\"description\":{\"identifier\":\"geometry.humanoid.custom\",\"texture_height\":64,\"texture_width\":64,\"visible_bounds_height\":2,\"visible_bounds_offset\":[0,1,0],\"visible_bounds_width\":1}},{\"bones\":[{\"name\":\"root\",\"pivot\":[0.0,0.0,0.0]},{\"name\":\"waist\",\"parent\":\"root\",\"pivot\":[0.0,12.0,0.0]},{\"cubes\":[{\"origin\":[-4.0,12.0,-2.0],\"size\":[8,12,4],\"uv\":[16,16]}],\"name\":\"body\",\"parent\":\"waist\",\"pivot\":[0.0,24.0,0.0]},{\"cubes\":[{\"origin\":[-4.0,24.0,-4.0],\"size\":[8,8,8],\"uv\":[0,0]}],\"name\":\"head\",\"parent\":\"body\",\"pivot\":[0.0,24.0,0.0]},{\"cubes\":[{\"inflate\":0.50,\"origin\":[-4.0,24.0,-4.0],\"size\":[8,8,8],\"uv\":[32,0]}],\"name\":\"hat\",\"parent\":\"head\",\"pivot\":[0.0,24.0,0.0]},{\"cubes\":[{\"origin\":[-3.90,0.0,-2.0],\"size\":[4,12,4],\"uv\":[0,16]}],\"name\":\"rightLeg\",\"parent\":\"root\",\"pivot\":[-1.90,12.0,0.0]},{\"cubes\":[{\"inflate\":0.250,\"origin\":[-3.90,0.0,-2.0],\"size\":[4,12,4],\"uv\":[0,32]}],\"name\":\"rightPants\",\"parent\":\"rightLeg\",\"pivot\":[-1.90,12.0,0.0]},{\"cubes\":[{\"origin\":[-0.10,0.0,-2.0],\"size\":[4,12,4],\"uv\":[16,48]}],\"name\":\"leftLeg\",\"parent\":\"root\",\"pivot\":[1.90,12.0,0.0]},{\"cubes\":[{\"inflate\":0.250,\"origin\":[-0.10,0.0,-2.0],\"size\":[4,12,4],\"uv\":[0,48]}],\"name\":\"leftPants\",\"parent\":\"leftLeg\",\"pivot\":[1.90,12.0,0.0]},{\"cubes\":[{\"origin\":[4.0,11.50,-2.0],\"size\":[3,12,4],\"uv\":[32,48]}],\"name\":\"leftArm\",\"parent\":\"body\",\"pivot\":[5.0,21.50,0.0]},{\"cubes\":[{\"inflate\":0.250,\"origin\":[4.0,11.50,-2.0],\"size\":[3,12,4],\"uv\":[48,48]}],\"name\":\"leftSleeve\",\"parent\":\"leftArm\",\"pivot\":[5.0,21.50,0.0]},{\"name\":\"leftItem\",\"parent\":\"leftArm\",\"pivot\":[6,14.50,1]},{\"cubes\":[{\"origin\":[-7.0,11.50,-2.0],\"size\":[3,12,4],\"uv\":[40,16]}],\"name\":\"rightArm\",\"parent\":\"body\",\"pivot\":[-5.0,21.50,0.0]},{\"cubes\":[{\"inflate\":0.250,\"origin\":[-7.0,11.50,-2.0],\"size\":[3,12,4],\"uv\":[40,32]}],\"name\":\"rightSleeve\",\"parent\":\"rightArm\",\"pivot\":[-5.0,21.50,0.0]},{\"locators\":{\"lead_hold\":[-6,14.50,1]},\"name\":\"rightItem\",\"parent\":\"rightArm\",\"pivot\":[-6,14.50,1]},{\"cubes\":[{\"inflate\":0.250,\"origin\":[-4.0,12.0,-2.0],\"size\":[8,12,4],\"uv\":[16,32]}],\"name\":\"jacket\",\"parent\":\"body\",\"pivot\":[0.0,24.0,0.0]},{\"name\":\"cape\",\"parent\":\"body\",\"pivot\":[0.0,24,-3.0]}],\"description\":{\"identifier\":\"geometry.humanoid.customSlim\",\"texture_height\":64,\"texture_width\":64,\"visible_bounds_height\":2,\"visible_bounds_offset\":[0,1,0],\"visible_bounds_width\":1}}]}";

    /**
     * Wide arm resource patch
     */
    public static final String GEOMETRY_CUSTOM = "{\"geometry\":{\"default\":\"geometry.humanoid.custom\"}}";

    /**
     * Slim arm resource patch
     */
    public static final String GEOMETRY_CUSTOM_SLIM = "{\"geometry\":{\"default\":\"geometry.humanoid.customSlim\"}}";

    /**
     * Default skin color (matches player skin default)
     */
    public static final String DEFAULT_SKIN_COLOR = "#0";

    /**
     * Load skin from folder (following RsNPC's approach)
     *
     * @param skinFolder skin folder path
     * @param skinName   skin name
     * @return loaded skin object, null if failed
     */
    public static Skin loadSkinFromFolder(Path skinFolder, String skinName) {
        // Find skin image file (same as RsNPC: check skin_slim.png first, then skin.png)
        Path skinPng = skinFolder.resolve("skin.png");
        Path skinSlimPng = skinFolder.resolve("skin_slim.png");
        Path skinJson = skinFolder.resolve("skin.json");

        boolean isSlim = false;
        Path skinImagePath;

        if (Files.exists(skinSlimPng)) {
            skinImagePath = skinSlimPng;
            isSlim = true;
        } else if (Files.exists(skinPng)) {
            skinImagePath = skinPng;
        } else {
            log.warn("Skin image not found in folder: {}", skinFolder);
            return null;
        }

        try {
            // Read skin image (same as RsNPC: ImageIO.read)
            BufferedImage image = ImageIO.read(skinImagePath.toFile());
            if (image == null) {
                log.warn("Failed to read skin image: {}", skinImagePath);
                return null;
            }

            // Convert image to RGBA byte array
            byte[] skinData = imageToRGBA(image);

            // Validate skin data size (same check as RsNPC's SerializedImage.fromLegacy)
            int expectedSize = image.getWidth() * image.getHeight() * 4;
            if (skinData.length != expectedSize) {
                log.warn("Invalid skin data size for {}: expected {}, got {}", skinName, expectedSize, skinData.length);
                return null;
            }

            // Create ImageData
            Skin.ImageData imageData = new Skin.ImageData(image.getWidth(), image.getHeight(), skinData);

            // Use skin name as base for ID (similar to RsNPC)
            String skinId = skinName;
            String resourcePatch = isSlim ? GEOMETRY_CUSTOM_SLIM : GEOMETRY_CUSTOM;
            String geometryData = STEVE_GEOMETRY;
            String geometryEngineVersion = "1.12.0";

            // Check if custom geometry data exists (4D skin) - following RsNPC's approach
            if (Files.exists(skinJson)) {
                try {
                    String customGeometry = Files.readString(skinJson);
                    String formatVersion = parseFormatVersion(customGeometry);
                    geometryEngineVersion = formatVersion;

                    String geometryName = null;
                    // Handle different format versions (same as RsNPC)
                    switch (formatVersion) {
                        case "1.16.0":
                        case "1.12.0":
                            geometryName = parseGeometryName(customGeometry);
                            if (geometryName != null && !geometryName.equals("nullvalue")) {
                                // Generate skin ID for 4D skin (similar to RsNPC's generateSkinId)
                                skinId = skinName + "_" + UUID.randomUUID().toString().substring(0, 8);
                                resourcePatch = "{\"geometry\":{\"default\":\"" + geometryName + "\"}}";
                                geometryData = customGeometry;
                                log.debug("Loaded 4D skin geometry for {}: {}", skinName, geometryName);
                            } else {
                                log.warn("Failed to parse geometry name from skin.json for {}", skinName);
                            }
                            break;
                        default:
                            log.warn("Skin {} has format_version {}, attempting to load", skinName, formatVersion);
                            // Fall through to legacy format handling
                        case "1.10.0":
                        case "1.8.0":
                            geometryName = parseGeometryNameLegacy(customGeometry);
                            if (geometryName != null) {
                                skinId = skinName + "_" + UUID.randomUUID().toString().substring(0, 8);
                                resourcePatch = "{\"geometry\":{\"default\":\"" + geometryName + "\"}}";
                                geometryData = customGeometry;
                                log.debug("Loaded legacy 4D skin geometry for {}: {}", skinName, geometryName);
                            }
                            break;
                    }
                } catch (Exception e) {
                    log.error("Failed to load skin.json for {}: {}", skinName, e.getMessage());
                    // Continue with default geometry
                }
            }

            // Build Skin object with all required fields (matching player skin values)
            Skin skin = new Skin(
                    skinId,                                           // skinId
                    null,                                             // playFabId (null like player)
                    resourcePatch,                                    // skinResourcePatch
                    imageData,                                        // skinData
                    new ArrayList<>(),                                // animations
                    Skin.ImageData.EMPTY,                             // capeData
                    geometryData,                                     // skinGeometry
                    null,                                             // animationData (null like player)
                    "0.0.0",                                          // geometryDataEngineVersion (0.0.0 like player)
                    false,                                            // premiumSkin
                    false,                                            // personaSkin
                    false,                                            // personaCapeOnClassicSkin
                    false,                                            // primaryUser (false like player)
                    "",                                               // capeId
                    null,                                             // fullId (null like player)
                    DEFAULT_SKIN_COLOR,                               // skinColor
                    isSlim ? Skin.ARM_SIZE_SLIM : Skin.ARM_SIZE_WIDE, // armSize
                    new ArrayList<>(),                                // personaPieces
                    new ArrayList<>(),                                // pieceTintColors
                    false                                             // overrideAppearance (false like player)
            );

            // Validate skin (similar to RsNPC's isValid check)
            if (!skin.isValid()) {
                log.error("Invalid skin: {} (skinId={}, dimensions={}x{}, resourcePatch={})",
                        skinName, skin.skinId(),
                        skin.skinData().width(), skin.skinData().height(),
                        skin.skinResourcePatch());
                return null;
            }

            log.info("Successfully loaded skin: {} ({}x{}, slim={})",
                    skinName, image.getWidth(), image.getHeight(), isSlim);

            return skin;

        } catch (IOException e) {
            log.error("Failed to load skin from folder: {}", skinFolder, e);
            return null;
        }
    }

    /**
     * Load skin from single PNG file (following RsNPC's approach for single file skins)
     *
     * @param skinFile skin image file path
     * @param skinName skin name
     * @return loaded skin object, null if failed
     */
    public static Skin loadSkinFromFile(Path skinFile, String skinName) {
        try {
            BufferedImage image = ImageIO.read(skinFile.toFile());
            if (image == null) {
                log.warn("Failed to read skin image: {}", skinFile);
                return null;
            }

            // Check if skin is slim (same as RsNPC: check filename for _slim)
            String fileName = skinFile.getFileName().toString();
            boolean isSlim = fileName.contains("_slim") || skinName.contains("_slim");

            // Remove _slim suffix from skin name for ID
            String baseSkinName = skinName.replace("_slim", "");

            // Convert image to RGBA byte array
            byte[] skinData = imageToRGBA(image);

            // Validate skin data size
            int expectedSize = image.getWidth() * image.getHeight() * 4;
            if (skinData.length != expectedSize) {
                log.warn("Invalid skin data size for {}: expected {}, got {}", skinName, expectedSize, skinData.length);
                return null;
            }

            // Create ImageData
            Skin.ImageData imageData = new Skin.ImageData(image.getWidth(), image.getHeight(), skinData);

            // Use base skin name as ID (similar to RsNPC)
            String skinId = baseSkinName;

            // Check for 4D skin JSON file alongside the PNG
            Path skinJson = skinFile.getParent().resolve(baseSkinName + ".json");
            String geometryData = STEVE_GEOMETRY;
            String resourcePatch = isSlim ? GEOMETRY_CUSTOM_SLIM : GEOMETRY_CUSTOM;

            if (Files.exists(skinJson)) {
                try {
                    String customGeometry = Files.readString(skinJson);
                    String formatVersion = parseFormatVersion(customGeometry);

                    String geometryName = null;
                    switch (formatVersion) {
                        case "1.16.0":
                        case "1.12.0":
                            geometryName = parseGeometryName(customGeometry);
                            if (geometryName != null && !geometryName.equals("nullvalue")) {
                                skinId = baseSkinName + "_" + UUID.randomUUID().toString().substring(0, 8);
                                resourcePatch = "{\"geometry\":{\"default\":\"" + geometryName + "\"}}";
                                geometryData = customGeometry;
                            }
                            break;
                        default:
                        case "1.10.0":
                        case "1.8.0":
                            geometryName = parseGeometryNameLegacy(customGeometry);
                            if (geometryName != null) {
                                skinId = baseSkinName + "_" + UUID.randomUUID().toString().substring(0, 8);
                                resourcePatch = "{\"geometry\":{\"default\":\"" + geometryName + "\"}}";
                                geometryData = customGeometry;
                            }
                            break;
                    }
                } catch (Exception e) {
                    log.debug("No 4D skin data for {}", skinName);
                }
            }

            // Build Skin object (matching player skin values)
            Skin skin = new Skin(
                    skinId,                                           // skinId
                    null,                                             // playFabId (null like player)
                    resourcePatch,                                    // skinResourcePatch
                    imageData,                                        // skinData
                    new ArrayList<>(),                                // animations
                    Skin.ImageData.EMPTY,                             // capeData
                    geometryData,                                     // skinGeometry
                    null,                                             // animationData (null like player)
                    "0.0.0",                                          // geometryDataEngineVersion (0.0.0 like player)
                    false,                                            // premiumSkin
                    false,                                            // personaSkin
                    false,                                            // personaCapeOnClassicSkin
                    false,                                            // primaryUser (false like player)
                    "",                                               // capeId
                    null,                                             // fullId (null like player)
                    DEFAULT_SKIN_COLOR,                               // skinColor
                    isSlim ? Skin.ARM_SIZE_SLIM : Skin.ARM_SIZE_WIDE, // armSize
                    new ArrayList<>(),                                // personaPieces
                    new ArrayList<>(),                                // pieceTintColors
                    false                                             // overrideAppearance (false like player)
            );

            // Validate skin
            if (!skin.isValid()) {
                log.error("Invalid skin: {} (dimensions: {}x{})", skinName, image.getWidth(), image.getHeight());
                return null;
            }

            log.info("Successfully loaded skin file: {} ({}x{}, slim={})",
                    skinName, image.getWidth(), image.getHeight(), isSlim);
            return skin;

        } catch (IOException e) {
            log.error("Failed to load skin from file: {}", skinFile, e);
            return null;
        }
    }

    /**
     * Convert BufferedImage to RGBA byte array
     * Using exactly the same approach as Nukkit-MOT's parseBufferedImage()
     *
     * @param image image object
     * @return RGBA byte array
     */
    public static byte[] imageToRGBA(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        byte[] data = new byte[width * height * 4];

        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Use java.awt.Color with hasalpha=true, same as Nukkit-MOT
                Color color = new Color(image.getRGB(x, y), true);
                data[index++] = (byte) color.getRed();
                data[index++] = (byte) color.getGreen();
                data[index++] = (byte) color.getBlue();
                data[index++] = (byte) color.getAlpha();
            }
        }

        return data;
    }

    /**
     * Parse geometry name from geometry data JSON (for 1.12.0 and 1.16.0 format)
     * Following RsNPC's getGeometryName() approach
     *
     * @param geometryData geometry data JSON string
     * @return geometry name, "nullvalue" if format incompatible, null if parsing failed
     */
    public static String parseGeometryName(String geometryData) {
        try {
            JsonObject json = JsonParser.parseString(geometryData).getAsJsonObject();
            String formatVersion = json.has("format_version") ? json.get("format_version").getAsString() : "1.10.0";

            // Only handle 1.12.0 and 1.16.0 format here (same as RsNPC)
            if (!formatVersion.equals("1.12.0") && !formatVersion.equals("1.16.0")) {
                return "nullvalue";
            }

            // Read minecraft:geometry array
            if (json.has("minecraft:geometry")) {
                var geometryArray = json.getAsJsonArray("minecraft:geometry");
                if (!geometryArray.isEmpty()) {
                    // Get first geometry entry
                    var firstGeometry = geometryArray.get(0).getAsJsonObject();
                    // Get description.identifier
                    if (firstGeometry.has("description")) {
                        var description = firstGeometry.getAsJsonObject("description");
                        if (description.has("identifier")) {
                            return description.get("identifier").getAsString();
                        }
                    }
                }
            }
            return "geometry.unknown";
        } catch (Exception e) {
            log.error("Failed to parse geometry name", e);
        }
        return null;
    }

    /**
     * Parse geometry name from legacy format JSON (1.8.0, 1.10.0)
     * Following RsNPC's approach for legacy format
     *
     * @param geometryData geometry data JSON string
     * @return geometry name, null if parsing failed
     */
    public static String parseGeometryNameLegacy(String geometryData) {
        try {
            JsonObject json = JsonParser.parseString(geometryData).getAsJsonObject();
            // In legacy format, geometry is stored directly as keys like "geometry.xxx"
            for (String key : json.keySet()) {
                if (key.startsWith("geometry.")) {
                    return key;
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse legacy geometry name", e);
        }
        return null;
    }

    /**
     * Parse format version from geometry data JSON
     *
     * @param geometryData geometry data JSON string
     * @return format version string, defaults to "1.12.0"
     */
    public static String parseFormatVersion(String geometryData) {
        try {
            JsonObject json = JsonParser.parseString(geometryData).getAsJsonObject();
            if (json.has("format_version")) {
                return json.get("format_version").getAsString();
            }
        } catch (Exception e) {
            log.debug("Failed to parse format version, using default");
        }
        return "1.12.0";
    }

    /**
     * Create default Steve skin
     *
     * @return Steve skin object
     */
    public static Skin createDefaultSkin() {
        // Create a simple 64x64 skin with a basic color pattern
        int width = 64;
        int height = 64;
        byte[] skinData = new byte[width * height * 4];

        // Fill with a light gray color (Steve-like)
        for (int i = 0; i < skinData.length; i += 4) {
            skinData[i] = (byte) 0x8B;     // R (139)
            skinData[i + 1] = (byte) 0x73; // G (115)
            skinData[i + 2] = (byte) 0x62; // B (98)
            skinData[i + 3] = (byte) 0xFF; // A (255 = fully opaque)
        }

        String skinId = UUID.randomUUID().toString();

        return new Skin(
                skinId,                                  // skinId
                null,                                    // playFabId (null like player)
                GEOMETRY_CUSTOM,                         // skinResourcePatch
                new Skin.ImageData(width, height, skinData), // skinData
                new ArrayList<>(),                       // animations
                Skin.ImageData.EMPTY,                    // capeData
                STEVE_GEOMETRY,                          // skinGeometry
                null,                                    // animationData (null like player)
                "0.0.0",                                 // geometryDataEngineVersion (0.0.0 like player)
                false,                                   // premiumSkin
                false,                                   // personaSkin
                false,                                   // personaCapeOnClassicSkin
                false,                                   // primaryUser (false like player)
                "",                                      // capeId
                null,                                    // fullId (null like player)
                DEFAULT_SKIN_COLOR,                      // skinColor
                Skin.ARM_SIZE_WIDE,                      // armSize
                new ArrayList<>(),                       // personaPieces
                new ArrayList<>(),                       // pieceTintColors
                false                                    // overrideAppearance (false like player)
        );
    }
}
