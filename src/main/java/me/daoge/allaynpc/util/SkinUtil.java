package me.daoge.allaynpc.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.allaymc.api.player.Skin;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

/**
 * Skin utility class
 *
 * @author daoge_cmd
 */
@Slf4j
@UtilityClass
public class SkinUtil {

    @Language("json")
    public static final String STEVE_GEOMETRY = """
            {"format_version":"1.12.0","minecraft:geometry":[{"bones":[{"name":"body","parent":"waist","pivot":[0.0,24.0,0.0]},{"name":"waist","pivot":[0.0,12.0,0.0]},{"cubes":[{"origin":[-5.0,8.0,3.0],"size":[10,16,1],"uv":[0,0]}],"name":"cape","parent":"body","pivot":[0.0,24.0,3.0],"rotation":[0.0,180.0,0.0]}],"description":{"identifier":"geometry.cape","texture_height":32,"texture_width":64}},{"bones":[{"name":"root","pivot":[0.0,0.0,0.0]},{"cubes":[{"origin":[-4.0,12.0,-2.0],"size":[8,12,4],"uv":[16,16]}],"name":"body","parent":"waist","pivot":[0.0,24.0,0.0]},{"name":"waist","parent":"root","pivot":[0.0,12.0,0.0]},{"cubes":[{"origin":[-4.0,24.0,-4.0],"size":[8,8,8],"uv":[0,0]}],"name":"head","parent":"body","pivot":[0.0,24.0,0.0]},{"name":"cape","parent":"body","pivot":[0.0,24,3.0]},{"cubes":[{"inflate":0.50,"origin":[-4.0,24.0,-4.0],"size":[8,8,8],"uv":[32,0]}],"name":"hat","parent":"head","pivot":[0.0,24.0,0.0]},{"cubes":[{"origin":[4.0,12.0,-2.0],"size":[4,12,4],"uv":[32,48]}],"name":"leftArm","parent":"body","pivot":[5.0,22.0,0.0]},{"cubes":[{"inflate":0.250,"origin":[4.0,12.0,-2.0],"size":[4,12,4],"uv":[48,48]}],"name":"leftSleeve","parent":"leftArm","pivot":[5.0,22.0,0.0]},{"name":"leftItem","parent":"leftArm","pivot":[6.0,15.0,1.0]},{"cubes":[{"origin":[-8.0,12.0,-2.0],"size":[4,12,4],"uv":[40,16]}],"name":"rightArm","parent":"body","pivot":[-5.0,22.0,0.0]},{"cubes":[{"inflate":0.250,"origin":[-8.0,12.0,-2.0],"size":[4,12,4],"uv":[40,32]}],"name":"rightSleeve","parent":"rightArm","pivot":[-5.0,22.0,0.0]},{"locators":{"lead_hold":[-6,15,1]},"name":"rightItem","parent":"rightArm","pivot":[-6,15,1]},{"cubes":[{"origin":[-0.10,0.0,-2.0],"size":[4,12,4],"uv":[16,48]}],"name":"leftLeg","parent":"root","pivot":[1.90,12.0,0.0]},{"cubes":[{"inflate":0.250,"origin":[-0.10,0.0,-2.0],"size":[4,12,4],"uv":[0,48]}],"name":"leftPants","parent":"leftLeg","pivot":[1.90,12.0,0.0]},{"cubes":[{"origin":[-3.90,0.0,-2.0],"size":[4,12,4],"uv":[0,16]}],"name":"rightLeg","parent":"root","pivot":[-1.90,12.0,0.0]},{"cubes":[{"inflate":0.250,"origin":[-3.90,0.0,-2.0],"size":[4,12,4],"uv":[0,32]}],"name":"rightPants","parent":"rightLeg","pivot":[-1.90,12.0,0.0]},{"cubes":[{"inflate":0.250,"origin":[-4.0,12.0,-2.0],"size":[8,12,4],"uv":[16,32]}],"name":"jacket","parent":"body","pivot":[0.0,24.0,0.0]}],"description":{"identifier":"geometry.humanoid.custom","texture_height":64,"texture_width":64,"visible_bounds_height":2,"visible_bounds_offset":[0,1,0],"visible_bounds_width":1}},{"bones":[{"name":"root","pivot":[0.0,0.0,0.0]},{"name":"waist","parent":"root","pivot":[0.0,12.0,0.0]},{"cubes":[{"origin":[-4.0,12.0,-2.0],"size":[8,12,4],"uv":[16,16]}],"name":"body","parent":"waist","pivot":[0.0,24.0,0.0]},{"cubes":[{"origin":[-4.0,24.0,-4.0],"size":[8,8,8],"uv":[0,0]}],"name":"head","parent":"body","pivot":[0.0,24.0,0.0]},{"cubes":[{"inflate":0.50,"origin":[-4.0,24.0,-4.0],"size":[8,8,8],"uv":[32,0]}],"name":"hat","parent":"head","pivot":[0.0,24.0,0.0]},{"cubes":[{"origin":[-3.90,0.0,-2.0],"size":[4,12,4],"uv":[0,16]}],"name":"rightLeg","parent":"root","pivot":[-1.90,12.0,0.0]},{"cubes":[{"inflate":0.250,"origin":[-3.90,0.0,-2.0],"size":[4,12,4],"uv":[0,32]}],"name":"rightPants","parent":"rightLeg","pivot":[-1.90,12.0,0.0]},{"cubes":[{"origin":[-0.10,0.0,-2.0],"size":[4,12,4],"uv":[16,48]}],"name":"leftLeg","parent":"root","pivot":[1.90,12.0,0.0]},{"cubes":[{"inflate":0.250,"origin":[-0.10,0.0,-2.0],"size":[4,12,4],"uv":[0,48]}],"name":"leftPants","parent":"leftLeg","pivot":[1.90,12.0,0.0]},{"cubes":[{"origin":[4.0,11.50,-2.0],"size":[3,12,4],"uv":[32,48]}],"name":"leftArm","parent":"body","pivot":[5.0,21.50,0.0]},{"cubes":[{"inflate":0.250,"origin":[4.0,11.50,-2.0],"size":[3,12,4],"uv":[48,48]}],"name":"leftSleeve","parent":"leftArm","pivot":[5.0,21.50,0.0]},{"name":"leftItem","parent":"leftArm","pivot":[6,14.50,1]},{"cubes":[{"origin":[-7.0,11.50,-2.0],"size":[3,12,4],"uv":[40,16]}],"name":"rightArm","parent":"body","pivot":[-5.0,21.50,0.0]},{"cubes":[{"inflate":0.250,"origin":[-7.0,11.50,-2.0],"size":[3,12,4],"uv":[40,32]}],"name":"rightSleeve","parent":"rightArm","pivot":[-5.0,21.50,0.0]},{"locators":{"lead_hold":[-6,14.50,1]},"name":"rightItem","parent":"rightArm","pivot":[-6,14.50,1]},{"cubes":[{"inflate":0.250,"origin":[-4.0,12.0,-2.0],"size":[8,12,4],"uv":[16,32]}],"name":"jacket","parent":"body","pivot":[0.0,24.0,0.0]},{"name":"cape","parent":"body","pivot":[0.0,24,-3.0]}],"description":{"identifier":"geometry.humanoid.customSlim","texture_height":64,"texture_width":64,"visible_bounds_height":2,"visible_bounds_offset":[0,1,0],"visible_bounds_width":1}}]}""";

    private static final String GEOMETRY_CUSTOM = "{\"geometry\":{\"default\":\"geometry.humanoid.custom\"}}";
    private static final String GEOMETRY_CUSTOM_SLIM = "{\"geometry\":{\"default\":\"geometry.humanoid.customSlim\"}}";
    private static final String DEFAULT_SKIN_COLOR = "#0";

    /**
     * Load skin from folder
     */
    @Nullable
    public static Skin loadSkinFromFolder(Path skinFolder, String skinName) {
        Path skinPng = skinFolder.resolve("skin.png");
        Path skinSlimPng = skinFolder.resolve("skin_slim.png");
        Path skinJson = skinFolder.resolve("skin.json");

        boolean isSlim;
        Path skinImagePath;

        if (Files.exists(skinSlimPng)) {
            skinImagePath = skinSlimPng;
            isSlim = true;
        } else if (Files.exists(skinPng)) {
            skinImagePath = skinPng;
            isSlim = false;
        } else {
            log.warn("Skin image not found in folder: {}", skinFolder);
            return null;
        }

        try {
            BufferedImage image = ImageIO.read(skinImagePath.toFile());
            if (image == null) {
                log.warn("Failed to read skin image: {}", skinImagePath);
                return null;
            }

            byte[] skinData = imageToRGBA(image);
            Skin.ImageData imageData = new Skin.ImageData(image.getWidth(), image.getHeight(), skinData);

            // Load 4D geometry if exists
            GeometryInfo geometry = loadGeometry(skinJson, skinData, skinName);

            return buildSkin(
                    geometry != null ? geometry.skinId : skinName,
                    geometry != null ? geometry.resourcePatch : getResourcePatch(isSlim),
                    imageData,
                    geometry != null ? geometry.geometryData : STEVE_GEOMETRY,
                    geometry != null ? geometry.engineVersion : "0.0.0",
                    isSlim
            );
        } catch (IOException e) {
            log.error("Failed to load skin from folder: {}", skinFolder, e);
            return null;
        }
    }

    /**
     * Load skin from single PNG file
     */
    @Nullable
    public static Skin loadSkinFromFile(Path skinFile, String skinName) {
        try {
            BufferedImage image = ImageIO.read(skinFile.toFile());
            if (image == null) {
                log.warn("Failed to read skin image: {}", skinFile);
                return null;
            }

            String fileName = skinFile.getFileName().toString();
            boolean isSlim = fileName.contains("_slim") || skinName.contains("_slim");
            String baseSkinName = skinName.replace("_slim", "");

            byte[] skinData = imageToRGBA(image);
            Skin.ImageData imageData = new Skin.ImageData(image.getWidth(), image.getHeight(), skinData);

            // Check for 4D skin JSON file alongside the PNG
            Path skinJson = skinFile.getParent().resolve(baseSkinName + ".json");
            GeometryInfo geometry = loadGeometry(skinJson, skinData, baseSkinName);

            return buildSkin(
                    geometry != null ? geometry.skinId : baseSkinName,
                    geometry != null ? geometry.resourcePatch : getResourcePatch(isSlim),
                    imageData,
                    geometry != null ? geometry.geometryData : STEVE_GEOMETRY,
                    geometry != null ? geometry.engineVersion : "0.0.0",
                    isSlim
            );
        } catch (IOException e) {
            log.error("Failed to load skin from file: {}", skinFile, e);
            return null;
        }
    }

    /**
     * Convert BufferedImage to RGBA byte array
     */
    public static byte[] imageToRGBA(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        byte[] data = new byte[width * height * 4];

        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y), true);
                data[index++] = (byte) color.getRed();
                data[index++] = (byte) color.getGreen();
                data[index++] = (byte) color.getBlue();
                data[index++] = (byte) color.getAlpha();
            }
        }
        image.flush();
        return data;
    }

    /**
     * Create default Steve skin
     */
    public static Skin createDefaultSkin() {
        int width = 64;
        int height = 64;
        byte[] skinData = new byte[width * height * 4];

        for (int i = 0; i < skinData.length; i += 4) {
            skinData[i] = (byte) 0x8B;
            skinData[i + 1] = (byte) 0x73;
            skinData[i + 2] = (byte) 0x62;
            skinData[i + 3] = (byte) 0xFF;
        }

        return buildSkin(
                UUID.randomUUID().toString(),
                GEOMETRY_CUSTOM,
                new Skin.ImageData(width, height, skinData),
                STEVE_GEOMETRY,
                "0.0.0",
                false
        );
    }

    private static String getResourcePatch(boolean isSlim) {
        return isSlim ? GEOMETRY_CUSTOM_SLIM : GEOMETRY_CUSTOM;
    }

    @Nullable
    private static GeometryInfo loadGeometry(Path skinJson, byte[] skinData, String skinName) {
        if (!Files.exists(skinJson)) {
            return null;
        }

        try {
            String customGeometry = Files.readString(skinJson);
            String formatVersion = parseFormatVersion(customGeometry);
            String geometryName = parseGeometryName(customGeometry, formatVersion);

            if (geometryName == null) {
                return null;
            }

            String resourcePatch = "{\"geometry\":{\"default\":\"" + geometryName + "\"}}";
            String skinId = generateSkinId(skinData, resourcePatch, skinName);

            log.debug("Loaded 4D skin geometry for {}: {} (version: {})", skinName, geometryName, formatVersion);
            return new GeometryInfo(skinId, resourcePatch, customGeometry, formatVersion);
        } catch (Exception e) {
            log.error("Failed to load skin.json for {}: {}", skinName, e.getMessage());
            return null;
        }
    }

    @Nullable
    private static String parseGeometryName(String geometryData, String formatVersion) {
        try {
            JsonObject json = JsonParser.parseString(geometryData).getAsJsonObject();

            return switch (formatVersion) {
                case "1.12.0", "1.16.0" -> parseModernGeometryName(json);
                default -> parseLegacyGeometryName(json);
            };
        } catch (Exception e) {
            log.error("Failed to parse geometry name", e);
            return null;
        }
    }

    @Nullable
    private static String parseModernGeometryName(JsonObject json) {
        if (!json.has("minecraft:geometry")) {
            return null;
        }

        var geometryArray = json.getAsJsonArray("minecraft:geometry");
        if (geometryArray.isEmpty()) {
            return null;
        }

        var firstGeometry = geometryArray.get(0).getAsJsonObject();
        if (!firstGeometry.has("description")) {
            return null;
        }

        var description = firstGeometry.getAsJsonObject("description");
        if (!description.has("identifier")) {
            return null;
        }

        return description.get("identifier").getAsString();
    }

    @Nullable
    private static String parseLegacyGeometryName(JsonObject json) {
        for (String key : json.keySet()) {
            if (key.startsWith("geometry")) {
                return key;
            }
        }
        return null;
    }

    private static String parseFormatVersion(String geometryData) {
        try {
            JsonObject json = JsonParser.parseString(geometryData).getAsJsonObject();
            if (json.has("format_version")) {
                return json.get("format_version").getAsString();
            }
        } catch (Exception ignored) {
        }
        // Default to legacy format (same as RsNPC)
        return "1.10.0";
    }

    private static String generateSkinId(byte[] skinData, String resourcePatch, String name) {
        byte[] patchBytes = resourcePatch.getBytes(StandardCharsets.UTF_8);
        byte[] combined = new byte[skinData.length + patchBytes.length];
        System.arraycopy(skinData, 0, combined, 0, skinData.length);
        System.arraycopy(patchBytes, 0, combined, skinData.length, patchBytes.length);
        return UUID.nameUUIDFromBytes(combined) + "." + name;
    }

    @Nullable
    private static Skin buildSkin(String skinId, String resourcePatch, Skin.ImageData imageData,
                                  String geometryData, String engineVersion, boolean isSlim) {
        Skin skin = Skin.builder()
                .skinId(skinId)
                .skinResourcePatch(resourcePatch)
                .skinData(imageData)
                .animations(List.of())
                .capeData(Skin.ImageData.EMPTY)
                .skinGeometry(geometryData)
                .geometryDataEngineVersion(engineVersion)
                .capeId("")
                .skinColor(DEFAULT_SKIN_COLOR)
                .armSize(isSlim ? Skin.ARM_SIZE_SLIM : Skin.ARM_SIZE_WIDE)
                .personaPieces(List.of())
                .pieceTintColors(List.of())
                .build();

        if (!skin.isValid()) {
            log.error("Invalid skin: {} (dimensions: {}x{})", skinId, imageData.width(), imageData.height());
            return null;
        }

        return skin;
    }

    private record GeometryInfo(String skinId, String resourcePatch, String geometryData, String engineVersion) {}
}
