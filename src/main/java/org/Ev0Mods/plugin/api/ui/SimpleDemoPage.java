package org.Ev0Mods.plugin.api.ui;

import au.ellie.hyui.builders.LabelBuilder;
import au.ellie.hyui.builders.PageBuilder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.Ev0Mods.plugin.api.Ev0Log;

/**
 * A minimal reference example showing how to open a server-side UI page
 * using HyUI's PageBuilder with inline HTML.
 *
 * <p>Pattern: {@code PageBuilder.pageForPlayer(player).fromHtml(html).addEventListener(...).open(store)}</p>
 */
public final class SimpleDemoPage {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private SimpleDemoPage() {}

    public static void open(PlayerRef playerRef, Store<EntityStore> store) {
        try {
            openInner(playerRef, store);
        } catch (Throwable t) {
            Ev0Log.warn(LOGGER, "SimpleDemoPage: open failed: " + t.getMessage());
        }
    }

    private static void openInner(PlayerRef playerRef, Store<EntityStore> store) {
        String html = buildHtml();

        PageBuilder builder = PageBuilder.pageForPlayer(playerRef)
                .fromHtml(html)
                .withLifetime(CustomPageLifetime.CanDismissOrCloseThroughInteraction);

        // Greeting button - reads name from text input and updates the greeting label
        builder.addEventListener("greetBtn", CustomUIEventBindingType.Activating, (ign, ctx) -> {
            String name = ctx.getValue("nameInput", String.class).orElse("Player").trim();
            final String greetName = name.isBlank() ? "Player" : name;
            ctx.getById("greetingLabel", LabelBuilder.class)
                    .ifPresent(lb -> lb.withText("Hello, " + greetName + "!"));
            ctx.updatePage(true);
        });

        // Reset button - clears the greeting back to default
        builder.addEventListener("resetBtn", CustomUIEventBindingType.Activating, (ign, ctx) -> {
            ctx.getById("greetingLabel", LabelBuilder.class)
                    .ifPresent(lb -> lb.withText("Enter your name above and click Greet!"));
            ctx.updatePage(true);
        });

        // Close button - dismisses the page
        builder.addEventListener("closeBtn", CustomUIEventBindingType.Activating, (ign, ctx) -> {
            // Page auto-dismisses
        });

        builder.open(store);
    }

    private static String buildHtml() {
        return "<style>\n"
                + "    .title {\n"
                + "        font-weight: bold;\n"
                + "        color: #ffffff;\n"
                + "        font-size: 20;\n"
                + "        padding-top: 8;\n"
                + "        padding-bottom: 8;\n"
                + "    }\n"
                + "    .section {\n"
                + "        padding-top: 4;\n"
                + "        padding-bottom: 4;\n"
                + "        color: #bdcbd3;\n"
                + "        font-size: 16;\n"
                + "    }\n"
                + "    .info-label {\n"
                + "        padding-top: 6;\n"
                + "        padding-bottom: 6;\n"
                + "        color: #a0b8c8;\n"
                + "        font-size: 14;\n"
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
                + "    .separator {\n"
                + "        layout-mode: Full;\n"
                + "        anchor-height: 2;\n"
                + "        background-color: #ffffff(0.15);\n"
                + "        margin-top: 8;\n"
                + "        margin-bottom: 8;\n"
                + "    }\n"
                + "</style>\n"
                + "<div class=\"page-overlay\">\n"
                + "    <div class=\"decorated-container\" data-hyui-title=\"Demo UI\" style=\"anchor-width: 440; anchor-height: 360;\">\n"
                + "        <div class=\"container-contents\" style=\"layout-mode: Top; padding: 16 24;\">\n"
                + "            <p class=\"title\">Demo UI Page</p>\n"
                + "            <p class=\"info-label\">This is a simple reference UI built with HyUI inline HTML.</p>\n"
                + "            <div class=\"separator\"></div>\n"
                + "            <p class=\"section\">Greeting</p>\n"
                + "            <div class=\"input-field\">\n"
                + "                <input type=\"text\" id=\"nameInput\" value=\"\" placeholder=\"Enter your name\""
                + " style=\"width: 100%; padding: 8 12; font-size: 14;\" />\n"
                + "            </div>\n"
                + "            <div class=\"btn-row\">\n"
                + "                <button id=\"greetBtn\" class=\"primary-button\" style=\"padding: 8 20;\">Greet</button>\n"
                + "                <button id=\"resetBtn\" class=\"secondary-button\" style=\"padding: 8 20;\">Reset</button>\n"
                + "            </div>\n"
                + "            <p id=\"greetingLabel\" class=\"info-label\">Enter your name above and click Greet!</p>\n"
                + "            <div class=\"separator\"></div>\n"
                + "            <div class=\"btn-row\">\n"
                + "                <button id=\"closeBtn\" class=\"tertiary-button\" style=\"padding: 6 16;\">Close</button>\n"
                + "            </div>\n"
                + "        </div>\n"
                + "    </div>\n"
                + "</div>\n";
    }
}