package me.daoge.allaynpc.manager;

import lombok.extern.slf4j.Slf4j;
import me.daoge.allaynpc.config.DialogConfig;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import org.yaml.snakeyaml.DumperOptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dialog Manager
 * Responsible for loading, caching and managing dialog configs
 *
 * @author daoge_cmd
 */
@Slf4j
public class DialogManager {

    /**
     * Dialogs directory path
     */
    private final Path dialogsDirectory;

    /**
     * Dialog cache (dialog name -> dialog config)
     */
    private volatile Map<String, DialogConfig> dialogs = new ConcurrentHashMap<>();

    /**
     * Create dialog manager
     *
     * @param dialogsDirectory dialogs directory path
     */
    public DialogManager(Path dialogsDirectory) {
        this.dialogsDirectory = dialogsDirectory;
    }

    /**
     * Load all dialog configs using atomic replacement pattern.
     */
    public void loadAllDialogs() {
        // Create new map for atomic replacement
        Map<String, DialogConfig> newDialogs = new ConcurrentHashMap<>();

        if (!Files.exists(dialogsDirectory)) {
            log.warn("Dialogs directory does not exist: {}", dialogsDirectory);
            // Atomically replace with empty map
            this.dialogs = newDialogs;
            return;
        }

        // Create new Yaml instance for thread safety (Yaml is not thread-safe)
        Yaml yaml = new Yaml();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dialogsDirectory, "*.yml")) {
            for (Path path : stream) {
                String fileName = path.getFileName().toString();
                String dialogName = fileName.replace(".yml", "");
                loadDialog(path, dialogName, yaml, newDialogs);
            }
        } catch (IOException e) {
            log.error("Failed to load dialogs from directory: {}", dialogsDirectory, e);
        }

        // Atomic replacement - other threads will see either old or new map, never empty
        this.dialogs = newDialogs;
        log.info("Loaded {} dialogs", newDialogs.size());
    }

    /**
     * Load a single dialog config from file into target map
     *
     * @param path       config file path
     * @param dialogName dialog name
     * @param yaml       YAML parser instance
     * @param targetMap  target map to put dialog into
     */
    private void loadDialog(Path path, String dialogName, Yaml yaml, Map<String, DialogConfig> targetMap) {
        try (InputStream inputStream = Files.newInputStream(path)) {
            Map<String, Object> data = yaml.load(inputStream);
            if (data == null) {
                log.warn("Empty dialog config: {}", dialogName);
                return;
            }

            DialogConfig config = parseDialogConfig(dialogName, data);
            targetMap.put(dialogName, config);
            log.debug("Loaded dialog: {}", dialogName);

        } catch (IOException e) {
            log.error("Failed to load dialog: {}", dialogName, e);
        } catch (Exception e) {
            log.error("Failed to parse dialog config: {}", dialogName, e);
        }
    }

    /**
     * Parse dialog config
     *
     * @param dialogName dialog name
     * @param data       config data
     * @return dialog config object
     */
    @SuppressWarnings("unchecked")
    private DialogConfig parseDialogConfig(String dialogName, Map<String, Object> data) {
        DialogConfig.DialogConfigBuilder builder = DialogConfig.builder()
                .name(dialogName)
                .title(getString(data, "title", ""))
                .body(getString(data, "body", ""));

        // Parse button list
        List<DialogConfig.ButtonConfig> buttons = new ArrayList<>();
        Object buttonsObj = data.get("buttons");
        if (buttonsObj instanceof List<?> buttonsList) {
            for (Object buttonObj : buttonsList) {
                if (buttonObj instanceof Map<?, ?> buttonData) {
                    DialogConfig.ButtonConfig buttonConfig = parseButtonConfig((Map<String, Object>) buttonData);
                    buttons.add(buttonConfig);
                }
            }
        }
        builder.buttons(buttons);

        return builder.build();
    }

    /**
     * Parse button config
     *
     * @param data button config data
     * @return button config object
     */
    @SuppressWarnings("unchecked")
    private DialogConfig.ButtonConfig parseButtonConfig(Map<String, Object> data) {
        DialogConfig.ButtonConfig.ButtonConfigBuilder builder = DialogConfig.ButtonConfig.builder()
                .text(getString(data, "text", ""))
                .message(getString(data, "message", ""))
                .asPlayer(getBoolean(data, "as_player", false));

        // Parse command list (supports both List and single String)
        List<String> commands = new ArrayList<>();
        Object commandsObj = data.get("commands");
        if (commandsObj instanceof List<?> commandsList) {
            for (Object cmd : commandsList) {
                if (cmd != null) {
                    commands.add(cmd.toString());
                }
            }
        } else if (commandsObj instanceof String cmdStr) {
            // Support single command as string
            commands.add(cmdStr);
        } else if (commandsObj != null) {
            log.warn("Invalid commands format, expected List or String but got: {}", commandsObj.getClass().getSimpleName());
        }
        builder.commands(commands);

        return builder.build();
    }

    /**
     * Get string value from config data
     */
    private String getString(Map<String, Object> data, String key, String defaultValue) {
        Object value = data.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    /**
     * Get boolean value from config data
     */
    private boolean getBoolean(Map<String, Object> data, String key, boolean defaultValue) {
        Object value = data.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }

    /**
     * Get dialog config by name
     *
     * @param name dialog name
     * @return dialog config, null if not exists
     */
    @Nullable
    public DialogConfig getDialog(String name) {
        return dialogs.get(name);
    }

    /**
     * Check if dialog exists
     *
     * @param name dialog name
     * @return whether exists
     */
    public boolean hasDialog(String name) {
        return dialogs.containsKey(name);
    }

    /**
     * Get all dialog names
     *
     * @return dialog name set
     */
    public Set<String> getDialogNames() {
        return dialogs.keySet();
    }

    /**
     * Get all dialog configs
     *
     * @return dialog config collection
     */
    public Collection<DialogConfig> getAllDialogs() {
        return dialogs.values();
    }

    /**
     * Get dialog count
     *
     * @return dialog count
     */
    public int getDialogCount() {
        return dialogs.size();
    }

    /**
     * Register dialog config
     *
     * @param name   dialog name
     * @param config dialog config
     */
    public void registerDialog(String name, DialogConfig config) {
        dialogs.put(name, config);
    }

    /**
     * Remove dialog config
     *
     * @param name dialog name
     * @return removed dialog config, null if not exists
     */
    @Nullable
    public DialogConfig removeDialog(String name) {
        return dialogs.remove(name);
    }

    /**
     * Save dialog config to file
     *
     * @param config dialog config to save
     * @return true if saved successfully
     */
    public boolean saveDialogConfig(DialogConfig config) {
        if (config == null || config.getName() == null || config.getName().isEmpty()) {
            log.error("Cannot save dialog config: invalid config or name");
            return false;
        }

        // Ensure dialogs directory exists
        try {
            if (!Files.exists(dialogsDirectory)) {
                Files.createDirectories(dialogsDirectory);
            }
        } catch (IOException e) {
            log.error("Failed to create dialogs directory: {}", dialogsDirectory, e);
            return false;
        }

        Path filePath = dialogsDirectory.resolve(config.getName() + ".yml");

        // Build YAML data
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("title", config.getTitle());
        data.put("body", config.getBody());

        List<Map<String, Object>> buttonsList = new ArrayList<>();
        for (DialogConfig.ButtonConfig button : config.getButtons()) {
            Map<String, Object> buttonData = new LinkedHashMap<>();
            buttonData.put("text", button.getText());
            if (!button.getCommands().isEmpty()) {
                buttonData.put("commands", button.getCommands());
            }
            if (!button.getMessage().isEmpty()) {
                buttonData.put("message", button.getMessage());
            }
            if (button.isAsPlayer()) {
                buttonData.put("as_player", true);
            }
            buttonsList.add(buttonData);
        }
        data.put("buttons", buttonsList);

        // Configure YAML dumper for readable output
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setIndent(2);
        Yaml yaml = new Yaml(options);

        try (Writer writer = Files.newBufferedWriter(filePath)) {
            yaml.dump(data, writer);
            log.info("Saved dialog config: {}", config.getName());
            return true;
        } catch (IOException e) {
            log.error("Failed to save dialog config: {}", config.getName(), e);
            return false;
        }
    }

    /**
     * Delete dialog config file
     *
     * @param name dialog name
     * @return true if deleted successfully
     */
    public boolean deleteDialogFile(String name) {
        Path filePath = dialogsDirectory.resolve(name + ".yml");
        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Deleted dialog file: {}", name);
                return true;
            } else {
                log.warn("Dialog file does not exist: {}", name);
                return false;
            }
        } catch (IOException e) {
            log.error("Failed to delete dialog file: {}", name, e);
            return false;
        }
    }

    /**
     * Get dialogs directory path
     *
     * @return dialogs directory path
     */
    public Path getDialogsDirectory() {
        return dialogsDirectory;
    }
}
