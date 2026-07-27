package org.Ev0Mods.plugin.api.telemetry;

import au.ellie.hyui.builders.PageBuilder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.Ev0Mods.plugin.api.Ev0Log;
import org.Ev0Mods.plugin.api.component.HopperComponent;

public final class TelemetryReportPage {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private TelemetryReportPage() {}

    public static void open(PlayerRef playerRef, Store<EntityStore> store, String defaultType) {
        try {
            openInner(playerRef, store, defaultType != null ? defaultType : "bug");
        } catch (Throwable t) {
            Ev0Log.warn(LOGGER, "TelemetryReportPage: open failed: " + t.getMessage());
        }
    }

    private static void openInner(PlayerRef playerRef, Store<EntityStore> store, String selectedType) {
        String html = buildHtml(selectedType);
        PageBuilder builder = ((PageBuilder) PageBuilder.pageForPlayer(playerRef)
                .fromHtml(html))
                .withLifetime(CustomPageLifetime.CanDismissOrCloseThroughInteraction);

        builder.addEventListener("selectBug", CustomUIEventBindingType.Activating, (ign, ctx) ->
                openInner(playerRef, store, "bug"));

        builder.addEventListener("selectSuggestion", CustomUIEventBindingType.Activating, (ign, ctx) ->
                openInner(playerRef, store, "suggestion"));

        builder.addEventListener("submitReport", CustomUIEventBindingType.Activating, (ign, ctx) -> {
            String title = ctx.getValue("reportTitle", String.class).orElse("").trim();
            String details = ctx.getValue("reportDetails", String.class).orElse("").trim();
            if (title.length() < 3) return;
            if (details.length() < 5) return;
            String playerName = resolvePlayerName(playerRef);
            try {
                Ev0Telemetry.sendReport(playerName, selectedType, title, details);
            } catch (Throwable t) {
                Ev0Log.warn(LOGGER, "TelemetryReportPage: sendReport failed: " + t.getMessage());
            }
            openConfirmation(playerRef, store);
        });

        builder.open(store);
    }

    private static void openConfirmation(PlayerRef playerRef, Store<EntityStore> store) {
        try {
            PageBuilder builder = ((PageBuilder) PageBuilder.pageForPlayer(playerRef)
                    .fromHtml(buildConfirmationHtml()))
                    .withLifetime(CustomPageLifetime.CanDismissOrCloseThroughInteraction);
            builder.addEventListener("closeConfirm", CustomUIEventBindingType.Activating,
                    (ign, ctx) -> { /* page dismisses itself */ });
            builder.open(store);
        } catch (Throwable ignored) {}
    }

    private static String buildStyles() {
        return "<style>\n"
                + "    .section-title {\n"
                + "        font-weight: bold;\n"
                + "        color: #bdcbd3;\n"
                + "        font-size: 16;\n"
                + "        padding-top: 12;\n"
                + "        padding-bottom: 4;\n"
                + "    }\n"
                + "    .info-label {\n"
                + "        padding-top: 4;\n"
                + "        padding-bottom: 4;\n"
                + "        color: #a0b8c8;\n"
                + "        font-size: 14;\n"
                + "    }\n"
                + "    .separator {\n"
                + "        layout-mode: Full;\n"
                + "        anchor-height: 2;\n"
                + "        background-color: #ffffff(0.15);\n"
                + "        margin-top: 8;\n"
                + "        margin-bottom: 8;\n"
                + "    }\n"
                + "    .btn-row {\n"
                + "        layout-mode: Left;\n"
                + "        padding-top: 6;\n"
                + "        padding-bottom: 6;\n"
                + "        spacing: 8;\n"
                + "    }\n"
                + "    .input-field {\n"
                + "        padding-top: 8;\n"
                + "        padding-bottom: 8;\n"
                + "    }\n"
                + "</style>\n";
    }

    private static String buildContainer(String title, String innerHtml) {
        return "<div class=\"page-overlay\">\n"
                + "    <div class=\"decorated-container\" data-hyui-title=\"" + esc(title) + "\" style=\"anchor-width: 520; anchor-height: 600;\">\n"
                + "        <div class=\"container-contents\" style=\"layout-mode: Top; padding: 16 28;\">\n"
                + innerHtml
                + "        </div>\n"
                + "    </div>\n"
                + "</div>\n";
    }

    private static String buildConfirmationHtml() {
        return buildStyles()
                + buildContainer("Report Submitted",
                "            <p class=\"section-title\" style=\"color: #22c55e;\">Report Submitted!</p>\n"
                        + "            <p class=\"info-label\">Thank you! The Ev0 team has received your report.</p>\n"
                        + "            <div class=\"separator\"></div>\n"
                        + "            <div class=\"btn-row\">\n"
                        + "                <button id=\"closeConfirm\" class=\"primary-button\" style=\"padding: 8 24;\">Close</button>\n"
                        + "            </div>\n");
    }

    private static String buildHtml(String selectedType) {
        boolean isBug = "bug".equals(selectedType);
        String bugActive = isBug ? "primary-button" : "secondary-button";
        String sugActive = !isBug ? "primary-button" : "secondary-button";
        String typeLabel = isBug ? "Bug Report" : "Suggestion";
        String placeholder = isBug
                ? "Describe what happened, steps to reproduce, and expected vs actual behaviour..."
                : "Describe your suggestion and how it would improve the mod...";

        String inner = "            <p class=\"section-title\">Submit a " + typeLabel + "</p>\n"
                + "            <p class=\"info-label\" style=\"margin-bottom: 12;\">Reports go directly to the Ev0 development team.</p>\n"
                + "            <div class=\"separator\"></div>\n"
                + "            <p class=\"section-title\">Type</p>\n"
                + "            <div class=\"btn-row\" style=\"margin-bottom: 12;\">\n"
                + "                <button id=\"selectBug\" class=\"" + bugActive + "\" style=\"padding: 6 18;\">Bug</button>\n"
                + "                <button id=\"selectSuggestion\" class=\"" + sugActive + "\" style=\"padding: 6 18;\">Suggestion</button>\n"
                + "            </div>\n"
                + "            <p class=\"section-title\">Title</p>\n"
                + "            <div class=\"input-field\" style=\"margin-bottom: 10;\">\n"
                + "                <input type=\"text\" id=\"reportTitle\" placeholder=\"Short description (3-140 chars)\" "
                + "style=\"width: 100%; padding: 8 12; font-size: 14;\" />\n"
                + "            </div>\n"
                + "            <p class=\"section-title\">Details</p>\n"
                + "            <div class=\"input-field\" style=\"margin-bottom: 16;\">\n"
                + "                <input type=\"text\" id=\"reportDetails\" placeholder=\"" + esc(placeholder) + "\" "
                + "style=\"width: 100%; padding: 8 12; font-size: 13;\" />\n"
                + "            </div>\n"
                + "            <div class=\"separator\"></div>\n"
                + "            <div class=\"btn-row\">\n"
                + "                <button id=\"submitReport\" class=\"primary-button\" style=\"padding: 8 24;\">Submit</button>\n"
                + "            </div>\n";

        return buildStyles() + buildContainer("Submit " + typeLabel, inner);
    }

    private static String resolvePlayerName(PlayerRef playerRef) {
        try {
            String name = HopperComponent.wirelessOwnerKey(playerRef);
            if (name != null && !name.isBlank()) return name;
        } catch (Throwable ignored) {}
        try {
            return playerRef != null ? playerRef.toString() : "unknown";
        } catch (Throwable ignored) {
            return "unknown";
        }
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
