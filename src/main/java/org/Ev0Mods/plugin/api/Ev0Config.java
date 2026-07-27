/*
 * Decompiled with CFR 0.152.
 */
package org.Ev0Mods.plugin.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Properties;
import java.util.UUID;

import com.hypixel.hytale.logger.HytaleLogger;

public class Ev0Config {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final String CONFIG_FILE_NAME = "Ev0Lib_config.properties";
    private static int tierMultiplier = 4;
    private static boolean fluidTransferEnabled = true;
    private static boolean debugMode = false;
    private static boolean telemetryOptIn = true;
    private static String telemetryUrl = "https://ev0media.com";
    private static String telemetrySecret = "";
    private static String serverId = "";
    private static Path configFilePath = null;
    private static boolean initialized = false;

    public static void initialize(String configDirPath) {
        if (initialized) {
            Ev0Log.warn(LOGGER, "Ev0Config already initialized!");
            return;
        }
        configFilePath = Paths.get(configDirPath, CONFIG_FILE_NAME);
        Ev0Config.loadConfig();
        initialized = true;
        if (serverId.isBlank()) {
            try {
                String host = InetAddress.getLocalHost().getHostName();
                serverId = host.replaceAll("[^a-zA-Z0-9_-]", "-") + "-"
                        + UUID.randomUUID().toString().substring(0, 8);
            } catch (Throwable t) {
                serverId = "server-" + UUID.randomUUID().toString().substring(0, 8);
            }
            Ev0Config.save();
            Ev0Log.info(LOGGER, "Auto-generated serverId: " + serverId);
        }
        Ev0Log.info(LOGGER, "Ev0Config initialized from: " + String.valueOf(configFilePath.toAbsolutePath()));
    }

    private static void loadConfig() {
        block9: {
            Properties props = new Properties();
            try {
                if (Files.exists(configFilePath, new LinkOption[0])) {
                    try (InputStream input = Files.newInputStream(configFilePath, new OpenOption[0]);){
                        props.load(input);
                        Ev0Config.loadValuesFromProperties(props);
                        Ev0Log.info(LOGGER, "Loaded config from: " + String.valueOf(configFilePath));
                        break block9;
                    }
                }
                Ev0Config.saveDefaultConfig(props);
                Ev0Log.info(LOGGER, "Created default config at: " + String.valueOf(configFilePath));
            }
            catch (Exception e) {
                Ev0Log.warn(LOGGER, "Error loading config: " + e.getMessage());
            }
        }
    }

    private static void loadValuesFromProperties(Properties props) {
        String debug;
        String fluidTransfer;
        String tierMult = props.getProperty("tierMultiplier");
        if (tierMult != null) {
            try {
                tierMultiplier = Integer.parseInt(tierMult.trim());
            }
            catch (NumberFormatException e) {
                Ev0Log.warn(LOGGER, "Invalid tierMultiplier value, using default: " + tierMult);
            }
        }
        if ((fluidTransfer = props.getProperty("fluidTransferEnabled")) != null) {
            fluidTransferEnabled = Boolean.parseBoolean(fluidTransfer.trim());
        }
        if ((debug = props.getProperty("debugMode")) != null) {
            debugMode = Boolean.parseBoolean(debug.trim());
        }
        String optIn = props.getProperty("telemetryOptIn");
        if (optIn != null) telemetryOptIn = Boolean.parseBoolean(optIn.trim());
        String url = props.getProperty("telemetryUrl");
        if (url != null) telemetryUrl = url.trim();
        String secret = props.getProperty("telemetrySecret");
        if (secret != null) telemetrySecret = secret.trim();
        String sid = props.getProperty("serverId");
        if (sid != null) serverId = sid.trim();
    }

    private static void saveDefaultConfig(Properties props) {
        props.setProperty("tierMultiplier", String.valueOf(tierMultiplier));
        props.setProperty("fluidTransferEnabled", String.valueOf(fluidTransferEnabled));
        props.setProperty("debugMode", String.valueOf(debugMode));
        props.setProperty("telemetryOptIn", String.valueOf(telemetryOptIn));
        props.setProperty("telemetryUrl", telemetryUrl);
        props.setProperty("telemetrySecret", telemetrySecret);
        props.setProperty("serverId", serverId);
        try {
            if (configFilePath.getParent() != null) {
                Files.createDirectories(configFilePath.getParent(), new FileAttribute[0]);
            }
            try (OutputStream output = Files.newOutputStream(configFilePath, new OpenOption[0]);){
                props.store(output, "Ev0Lib Configuration File\n# Edit these values to enable/disable features\n# tierMultiplier: How many items per tier to transfer per tick (default: 4)\n# fluidTransferEnabled: Enable/disable fluid import/export via hoppers (default: true)\n# debugMode: Enable debug logging (default: false)\n# enableLogging: Master switch for all Ev0Lib logging (default: false)\n# telemetryUrl: Backend URL for error/heartbeat/report telemetry (default: ev0smods site)\n# telemetrySecret: Shared secret sent as X-Telemetry-Secret header (leave blank if not set)\n# serverId: Optional identifier for this server instance shown in reports");
            }
        }
        catch (IOException e) {
            Ev0Log.warn(LOGGER, "Error saving default config: " + e.getMessage());
        }
    }

    public static void reload() {
        if (!initialized) {
            Ev0Log.warn(LOGGER, "Ev0Config not initialized, cannot reload!");
            return;
        }
        Ev0Config.loadConfig();
        Ev0Log.info(LOGGER, "Ev0Config reloaded");
    }

    public static void save() {
        if (!initialized) {
            Ev0Log.warn(LOGGER, "Ev0Config not initialized, cannot save!");
            return;
        }
        Properties props = new Properties();
        props.setProperty("tierMultiplier", String.valueOf(tierMultiplier));
        props.setProperty("fluidTransferEnabled", String.valueOf(fluidTransferEnabled));
        props.setProperty("debugMode", String.valueOf(debugMode));
        props.setProperty("telemetryOptIn", String.valueOf(telemetryOptIn));
        props.setProperty("telemetryUrl", telemetryUrl);
        props.setProperty("telemetrySecret", telemetrySecret);
        props.setProperty("serverId", serverId);
        try {
            try (OutputStream output = Files.newOutputStream(configFilePath, new OpenOption[0]);){
                props.store(output, "Ev0Lib Configuration File\n# Edit these values to enable/disable features\n# tierMultiplier: How many items per tier to transfer per tick (default: 4)\n# fluidTransferEnabled: Enable/disable fluid import/export via hoppers (default: true)\n# debugMode: Enable debug logging (default: false)\n# enableLogging: Master switch for all Ev0Lib logging (default: false)\n# telemetryOptIn: Master switch for telemetry (default: true). Set to false to disable all telemetry.\n# telemetryUrl: Backend URL for error/heartbeat/report telemetry (default: ev0smods site)\n# telemetrySecret: Shared secret sent as X-Telemetry-Secret header (leave blank if not set)\n# serverId: Optional identifier for this server instance shown in reports");
            }
            Ev0Log.info(LOGGER, "Ev0Config saved");
        }
        catch (IOException e) {
            Ev0Log.warn(LOGGER, "Error saving config: " + e.getMessage());
        }
    }

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
        return debugMode;
    }

    public static String getTelemetryUrl() {
        return telemetryUrl;
    }

    public static String getTelemetrySecret() {
        return telemetrySecret;
    }

    public static String getServerId() {
        return serverId;
    }

    public static void setTierMultiplier(int value) {
        tierMultiplier = value;
        if (initialized) {
            Ev0Config.save();
        }
    }

    public static void setFluidTransferEnabled(boolean enabled) {
        fluidTransferEnabled = enabled;
        if (initialized) {
            Ev0Config.save();
        }
    }

    public static void setDebugMode(boolean enabled) {
        debugMode = enabled;
        if (initialized) {
            Ev0Config.save();
        }
    }

    public static void setLoggingEnabled(boolean enabled) {
        Ev0Config.setDebugMode(enabled);
    }

    public static boolean isTelemetryOptIn() {
        return telemetryOptIn;
    }

    public static void setTelemetryOptIn(boolean enabled) {
        telemetryOptIn = enabled;
        if (initialized) {
            Ev0Config.save();
        }
    }
}

