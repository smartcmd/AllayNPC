package me.daoge.allaynpc.config;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialog configuration class
 * Stores dialog configuration information
 *
 * @author daoge_cmd
 */
@Data
@Builder
public class DialogConfig {

    /**
     * Dialog name (config file name)
     */
    private String name;

    /**
     * Dialog title
     */
    @Builder.Default
    private String title = "";

    /**
     * Dialog body content
     */
    @Builder.Default
    private String body = "";

    /**
     * Dialog button list
     */
    @Builder.Default
    private List<ButtonConfig> buttons = new ArrayList<>();

    /**
     * Button configuration class
     */
    @Data
    @Builder
    public static class ButtonConfig {

        /**
         * Button text
         */
        @Builder.Default
        private String text = "";

        /**
         * Commands to execute when button is clicked
         */
        @Builder.Default
        private List<String> commands = new ArrayList<>();

        /**
         * Message to send to player when button is clicked
         */
        @Builder.Default
        private String message = "";

        /**
         * Whether to execute commands as player
         */
        @Builder.Default
        private boolean asPlayer = false;
    }
}
