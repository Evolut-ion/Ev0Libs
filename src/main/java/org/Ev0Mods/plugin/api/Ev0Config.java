package org.Ev0Mods.plugin.api;

import com.hypixel.hytale.logger.HytaleLogger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Global configuration for Ev0Lib mod
 * Values are loaded from config.properties in the server config directory
 * Server managers can edit this file to enable/disable features
 */
public class Ev0Config {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final String CONFIG_FILE_NAME = "Ev0Lib_config.properties";

    // Default values
    private static int tierMultiplier = 4; // Default value - transfers tier * 4 items per tick
    private static boolean fluidTransferEnabled = true; // Enable/disable fluid import/export
    private static boolean debugMode = false; // Enable debug logging
    private static boolean loggingEnabled = false; // Master logging switch (default: false)

    // Config file path (set by plugin)
    private static Path configFilePath = null;
    private static boolean initialized = false;

    public Ev0Config() {}

    /**
     * Initialize the config system - called by the plugin on startup
     * @param configDirPath Path to the server's config directory
     */
    public static void initialize(String configDirPath) {
        if (initialized) {
            Ev0Log.warn(LOGGER, "Ev0Config already initialized!");
            return;
        }

        configFilePath = Paths.get(configDirPath, CONFIG_FILE_NAME);
        loadConfig();
        initialized = true;
        Ev0Log.info(LOGGER, "Ev0Config initialized from: " + configFilePath.toAbsolutePath());
    }

    /**
     * Load config from file, or create default if not exists
     */
    private static void loadConfig() {
        Properties props = new Properties();

        try {
            if (Files.exists(configFilePath)) {
                // Load existing config
                try (InputStream input = Files.newInputStream(configFilePath)) {
                    props.load(input);
                    loadValuesFromProperties(props);
                    Ev0Log.info(LOGGER, "Loaded config from: " + configFilePath);
                }
            } else {
                // Create default config file
                saveDefaultConfig(props);
                Ev0Log.info(LOGGER, "Created default config at: " + configFilePath);
            }
        } catch (Exception e) {
            Ev0Log.warn(LOGGER, "Error loading config: " + e.getMessage());
            // Use defaults on error
        }
    }

    /**
     * Load values from Properties
     */
    private static void loadValuesFromProperties(Properties props) {
        // Tier Multiplier
        String tierMult = props.getProperty("tierMultiplier");
        if (tierMult != null) {
            try {
                tierMultiplier = Integer.parseInt(tierMult.trim());
            } catch (NumberFormatException e) {
                Ev0Log.warn(LOGGER, "Invalid tierMultiplier value, using default: " + tierMult);
            }
        }

        // Fluid Transfer
        String fluidTransfer = props.getProperty("fluidTransferEnabled");
        if (fluidTransfer != null) {
            fluidTransferEnabled = Boolean.parseBoolean(fluidTransfer.trim());
        }

        // Debug Mode
        String debug = props.getProperty("debugMode");
        if (debug != null) {
            debugMode = Boolean.parseBoolean(debug.trim());
        }

        // Master Logging
        String logging = props.getProperty("enableLogging");
        if (logging != null) {
            loggingEnabled = Boolean.parseBoolean(logging.trim());
        }
    }

    /**
     * Save the default config file
     */
    private static void saveDefaultConfig(Properties props) {
        props.setProperty("tierMultiplier", String.valueOf(tierMultiplier));
        props.setProperty("fluidTransferEnabled", String.valueOf(fluidTransferEnabled));
        props.setProperty("debugMode", String.valueOf(debugMode));
        props.setProperty("enableLogging", String.valueOf(loggingEnabled));

        try {
            // Ensure parent directory exists
            if (configFilePath.getParent() != null) {
                Files.createDirectories(configFilePath.getParent());
            }
            try (OutputStream output = Files.newOutputStream(configFilePath)) {
                props.store(output, "Ev0Lib Configuration File\n" +
                    "# Edit these values to enable/disable features\n" +
                    "# tierMultiplier: How many items per tier to transfer per tick (default: 4)\n" +
                    "# fluidTransferEnabled: Enable/disable fluid import/export via hoppers (default: true)\n" +
                    "# debugMode: Enable debug logging (default: false)\n" +
                    "# enableLogging: Master switch for all Ev0Lib logging (default: false)");
            }
        } catch (IOException e) {
            Ev0Log.warn(LOGGER, "Error saving default config: " + e.getMessage());
        }
    }

    /**
     * Reload config from file (can be called at runtime)
     */
    public static void reload() {
        if (!initialized) {
            Ev0Log.warn(LOGGER, "Ev0Config not initialized, cannot reload!");
            return;
        }

        loadConfig();
        Ev0Log.info(LOGGER, "Ev0Config reloaded");
    }

    /**
     * Save current values to config file
     */
    public static void save() {
        if (!initialized) {
            Ev0Log.warn(LOGGER, "Ev0Config not initialized, cannot save!");
            return;
        }

        Properties props = new Properties();
        props.setProperty("tierMultiplier", String.valueOf(tierMultiplier));
        props.setProperty("fluidTransferEnabled", String.valueOf(fluidTransferEnabled));
        props.setProperty("debugMode", String.valueOf(debugMode));
        props.setProperty("enableLogging", String.valueOf(loggingEnabled));

        try {
            try (OutputStream output = Files.newOutputStream(configFilePath)) {
                props.store(output, "Ev0Lib Configuration File\n" +
                    "# Edit these values to enable/disable features\n" +
                    "# tierMultiplier: How many items per tier to transfer per tick (default: 4)\n" +
                    "# fluidTransferEnabled: Enable/disable fluid import/export via hoppers (default: true)\n" +
                    "# debugMode: Enable debug logging (default: false)\n" +
                    "# enableLogging: Master switch for all Ev0Lib logging (default: false)");
            }
            Ev0Log.info(LOGGER, "Ev0Config saved");
        } catch (IOException e) {
            Ev0Log.warn(LOGGER, "Error saving config: " + e.getMessage());
        }
    }

    // Getters

    public static int getTierMultiplier() {
        return tierMultiplier;
    }

    public static boolean isFluidTransferEnabled() {
        return fluidTransferEnabled;
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    // Setters with save

    public static void setTierMultiplier(int value) {
        tierMultiplier = value;
        if (initialized) save();
    }

    public static void setFluidTransferEnabled(boolean enabled) {
        fluidTransferEnabled = enabled;
        if (initialized) save();
    }

    public static void setDebugMode(boolean enabled) {
        debugMode = enabled;
        if (initialized) save();
    }

    public static void setLoggingEnabled(boolean enabled) {
        loggingEnabled = enabled;
        if (initialized) save();
    }
}
