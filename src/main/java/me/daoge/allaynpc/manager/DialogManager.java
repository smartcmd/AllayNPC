package me.daoge.allaynpc.manager;

import lombok.extern.slf4j.Slf4j;
import me.daoge.allaynpc.config.DialogConfig;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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
    private final Map<String, DialogConfig> dialogs = new HashMap<>();

    /**
     * YAML parser
     */
    private final Yaml yaml = new Yaml();

    /**
     * Create dialog manager
     *
     * @param dialogsDirectory dialogs directory path
     */
    public DialogManager(Path dialogsDirectory) {
        this.dialogsDirectory = dialogsDirectory;
    }

    /**
     * Load all dialog configs
     */
    public void loadAllDialogs() {
        dialogs.clear();

        if (!Files.exists(dialogsDirectory)) {
            log.warn("Dialogs directory does not exist: {}", dialogsDirectory);
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dialogsDirectory, "*.yml")) {
            for (Path path : stream) {
                String fileName = path.getFileName().toString();
                String dialogName = fileName.replace(".yml", "");
                loadDialog(path, dialogName);
            }
        } catch (IOException e) {
            log.error("Failed to load dialogs from directory: {}", dialogsDirectory, e);
        }

        log.info("Loaded {} dialogs", dialogs.size());
    }

    /**
     * Load a single dialog config from file
     *
     * @param path       config file path
     * @param dialogName dialog name
     */
    private void loadDialog(Path path, String dialogName) {
        try (InputStream inputStream = Files.newInputStream(path)) {
            Map<String, Object> data = yaml.load(inputStream);
            if (data == null) {
                log.warn("Empty dialog config: {}", dialogName);
                return;
            }

            DialogConfig config = parseDialogConfig(dialogName, data);
            dialogs.put(dialogName, config);
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

        // Parse command list
        List<String> commands = new ArrayList<>();
        Object commandsObj = data.get("commands");
        if (commandsObj instanceof List<?> commandsList) {
            for (Object cmd : commandsList) {
                if (cmd != null) {
                    commands.add(cmd.toString());
                }
            }
        } else if (commandsObj instanceof String cmdStr) {
            commands.add(cmdStr);
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
}
