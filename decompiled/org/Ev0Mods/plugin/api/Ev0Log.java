/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.logger.HytaleLogger
 *  com.hypixel.hytale.logger.HytaleLogger$Api
 */
package org.Ev0Mods.plugin.api;

import com.hypixel.hytale.logger.HytaleLogger;
import org.Ev0Mods.plugin.api.Ev0Config;

public final class Ev0Log {
    private Ev0Log() {
    }

    public static void info(HytaleLogger logger, String message) {
        if (!Ev0Config.isLoggingEnabled()) {
            return;
        }
        ((HytaleLogger.Api)logger.atInfo()).log(message);
    }

    public static void warn(HytaleLogger logger, String message) {
        if (!Ev0Config.isLoggingEnabled()) {
            return;
        }
        ((HytaleLogger.Api)logger.atWarning()).log(message);
    }

    public static void error(HytaleLogger logger, String message) {
        if (!Ev0Config.isLoggingEnabled()) {
            return;
        }
        ((HytaleLogger.Api)logger.atWarning()).log(message);
    }
}

