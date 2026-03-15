package org.Ev0Mods.plugin.api;

import com.hypixel.hytale.logger.HytaleLogger;

/**
 * Centralized logging wrapper that only forwards logs when enabled in Ev0Config.
 */
public final class Ev0Log {

    private Ev0Log() {}

    public static void info(HytaleLogger logger, String message) {
        if (!Ev0Config.isLoggingEnabled()) return;
        logger.atInfo().log(message);
    }

    public static void warn(HytaleLogger logger, String message) {
        if (!Ev0Config.isLoggingEnabled()) return;
        logger.atWarning().log(message);
    }

    public static void error(HytaleLogger logger, String message) {
        if (!Ev0Config.isLoggingEnabled()) return;
        logger.atWarning().log(message);
    }
}
