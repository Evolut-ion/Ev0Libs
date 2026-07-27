package org.Ev0Mods.plugin.api.telemetry;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.Ev0Mods.plugin.api.Ev0Config;
import org.Ev0Mods.plugin.api.Ev0Log;

import com.hypixel.hytale.logger.HytaleLogger;

public final class Ev0Telemetry {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static final String MOD_SLUG = "ev0lib";
    private static final long HEARTBEAT_INTERVAL_SECONDS = 300;

    private static volatile boolean initialized = false;
    private static HttpClient httpClient;
    private static ScheduledExecutorService scheduler;
    private static String pluginVersion = "unknown";
    private static final AtomicInteger playerCount = new AtomicInteger(0);
    private static volatile String serverType = "server";

    private Ev0Telemetry() {}

    public static synchronized void initialize(String version) {
        if (initialized) return;
        pluginVersion = version != null ? version : "unknown";
        httpClient = HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(10))
                .build();
        installUncaughtExceptionHandler();
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Ev0Telemetry-Heartbeat");
            t.setDaemon(true);
            return t;
        });
        scheduler.scheduleAtFixedRate(Ev0Telemetry::sendHeartbeat,
                15, HEARTBEAT_INTERVAL_SECONDS, TimeUnit.SECONDS);
        initialized = true;
        Ev0Log.info(LOGGER, "Ev0Telemetry initialized (version=" + pluginVersion + ")");
    }

    public static void shutdown() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
        if (!isConfigured() || httpClient == null) return;
        // Send a final heartbeat with player_count=0 so the website clears immediately.
        try {
            String serverId = Ev0Config.getServerId();
            String body = "{\"mod_slug\":\"" + MOD_SLUG + "\","
                    + "\"plugin_version\":\"" + esc(pluginVersion) + "\","
                    + "\"server_id\":\"" + esc(serverId) + "\","
                    + "\"player_count\":0,"
                    + "\"server_type\":\"" + esc(serverType) + "\"}";
            String base = Ev0Config.getTelemetryUrl().stripTrailing();
            if (base.endsWith("/")) base = base.substring(0, base.length() - 1);
            String secret = Ev0Config.getTelemetrySecret();
            HttpRequest.Builder req = HttpRequest.newBuilder()
                    .uri(URI.create(base + "/api/telemetry/heartbeat"))
                    .header("Content-Type", "application/json")
                    .timeout(java.time.Duration.ofSeconds(5))
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));
            if (!secret.isBlank()) req.header("X-Telemetry-Secret", secret);
            httpClient.send(req.build(), HttpResponse.BodyHandlers.discarding());
        } catch (Throwable ignored) {}
    }

    public static void notifyPlayerJoined() {
        playerCount.incrementAndGet();
    }

    public static void notifyPlayerLeft() {
        playerCount.updateAndGet(n -> Math.max(0, n - 1));
    }

    public static void setServerType(String type) {
        if ("singleplayer".equals(type) || "server".equals(type)) serverType = type;
    }

    // ── public send methods ────────────────────────────────────────────────

    public static void sendError(Throwable t) {
        if (!isConfigured()) return;
        String message = t.getClass().getName() + ": " + t.getMessage();
        String stack = stackTraceToString(t);
        postAsync("/api/telemetry/event", buildEventJson("error", message, stack));
    }

    public static void sendCrash(Throwable t) {
        if (!isConfigured()) return;
        String message = t.getClass().getName() + ": " + t.getMessage();
        String stack = stackTraceToString(t);
        postAsync("/api/telemetry/event", buildEventJson("crash", message, stack));
    }

    /**
     * Report a caught / handled error as a full GitHub-backed report.
     * Unlike {@link #sendError(Throwable)} which creates an orphan event, this creates
     * a proper report with a GitHub issue so it can be tracked, resolved, and deleted
     * from the admin panel.
     *
     * @param t       the exception that was caught
     * @param context a short label describing where the error was caught
     *                (e.g. "HopperInteraction.onUse"), included in the title
     */
    public static void sendHandledError(Throwable t, String context) {
        if (!isConfigured()) return;
        String title = (context != null && !context.isBlank()
                ? "[" + context + "] "
                : "")
                + t.getClass().getSimpleName() + ": " + t.getMessage();
        String details = stackTraceToString(t);
        String playerName = "server";  // system-reported, not player-initiated
        postAsync("/api/telemetry/report", buildReportJson(playerName, "bug", title, details));
    }

    public static void sendReport(String playerName, String type, String title, String details) {
        if (!isConfigured()) return;
        postAsync("/api/telemetry/report", buildReportJson(playerName, type, title, details));
    }

    private static void sendHeartbeat() {
        if (!isConfigured()) return;
        String serverId = Ev0Config.getServerId();
        String body = "{\"mod_slug\":\"" + MOD_SLUG + "\","
                + "\"plugin_version\":\"" + esc(pluginVersion) + "\","
                + "\"server_id\":\"" + esc(serverId) + "\","
                + "\"player_count\":" + playerCount.get() + ","
                + "\"server_type\":\"" + esc(serverType) + "\"}";
        postAsync("/api/telemetry/heartbeat", body);
    }

    // ── internals ─────────────────────────────────────────────────────────

    private static boolean isConfigured() {
        return initialized && Ev0Config.isTelemetryOptIn() && !Ev0Config.getTelemetryUrl().isBlank();
    }

    private static String buildEventJson(String type, String message, String stack) {
        return "{\"mod_slug\":\"" + MOD_SLUG + "\","
                + "\"plugin_version\":\"" + esc(pluginVersion) + "\","
                + "\"event_type\":\"" + esc(type) + "\","
                + "\"message\":\"" + esc(message) + "\","
                + "\"stack_trace\":\"" + esc(stack) + "\","
                + "\"server_id\":\"" + esc(Ev0Config.getServerId()) + "\"}";
    }

    private static String buildReportJson(String playerName, String type, String title, String details) {
        return "{\"mod_slug\":\"" + MOD_SLUG + "\","
                + "\"plugin_version\":\"" + esc(pluginVersion) + "\","
                + "\"player_name\":\"" + esc(playerName) + "\","
                + "\"type\":\"" + esc(type) + "\","
                + "\"title\":\"" + esc(title) + "\","
                + "\"details\":\"" + esc(details) + "\","
                + "\"server_id\":\"" + esc(Ev0Config.getServerId()) + "\"}";
    }

    private static void postAsync(String path, String json) {
        String base = Ev0Config.getTelemetryUrl().stripTrailing();
        if (base.endsWith("/")) base = base.substring(0, base.length() - 1);
        String url = base + path;
        String secret = Ev0Config.getTelemetrySecret();
        try {
            HttpRequest.Builder req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8));
            if (!secret.isBlank()) req.header("X-Telemetry-Secret", secret);
            httpClient.sendAsync(req.build(), HttpResponse.BodyHandlers.discarding())
                    .exceptionally(ex -> {
                        Ev0Log.warn(LOGGER, "Telemetry POST failed: " + ex.getMessage());
                        return null;
                    });
        } catch (Throwable ex) {
            Ev0Log.warn(LOGGER, "Telemetry POST setup failed: " + ex.getMessage());
        }
    }

    /**
     * Returns true if the throwable's stack trace contains any Ev0Lib class.
     * This filters out Hytale-internal crashes that aren't related to this plugin,
     * preventing them from polluting the issue tracker.
     */
    private static boolean involvesEv0Lib(Throwable t) {
        if (t == null) return false;
        for (StackTraceElement el : t.getStackTrace()) {
            String cn = el.getClassName();
            if (cn != null && cn.startsWith("org.Ev0Mods.")) return true;
        }
        Throwable cause = t.getCause();
        if (cause != null && cause != t) return involvesEv0Lib(cause);
        return false;
    }

    private static void installUncaughtExceptionHandler() {
        Thread.UncaughtExceptionHandler prev = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            try {
                // Only report crashes that originate from Ev0Lib code to avoid
                // filing issues for Hytale-internal bugs (e.g. Store is currently
                // processing during world shutdown, file-lock conflicts, etc.).
                if (involvesEv0Lib(throwable)) {
                    sendCrash(throwable);
                }
            } catch (Throwable ignored) {}
            if (prev != null) prev.uncaughtException(thread, throwable);
        });
    }

    private static String stackTraceToString(Throwable t) {
        if (t == null) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(t.toString()).append("\n");
        for (StackTraceElement el : t.getStackTrace()) {
            sb.append("\tat ").append(el).append("\n");
            if (sb.length() > 8000) { sb.append("\t..."); break; }
        }
        Throwable cause = t.getCause();
        if (cause != null && cause != t) {
            sb.append("Caused by: ").append(cause).append("\n");
            for (StackTraceElement el : cause.getStackTrace()) {
                sb.append("\tat ").append(el).append("\n");
                if (sb.length() > 12000) { sb.append("\t..."); break; }
            }
        }
        return sb.toString();
    }

    /** JSON-safe escape: handles the characters that break JSON string values. */
    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
