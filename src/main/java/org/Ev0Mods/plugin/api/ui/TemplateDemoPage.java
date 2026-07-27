package org.Ev0Mods.plugin.api.ui;

import au.ellie.hyui.builders.LabelBuilder;
import au.ellie.hyui.builders.PageBuilder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import org.Ev0Mods.plugin.api.Ev0Log;

/**
 * Demonstrates loading a UI template from resources, populating it with variables,
 * then opening it as a server-side UI page.
 *
 * <p>This example also shows how to maintain per-player state (a counter) across page
 * interactions without re-building the entire template.</p>
 */
public final class TemplateDemoPage {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final String TEMPLATE_PATH = "Common/UI/Templates/counter_template.html";

    /** Per-player counter state. */
    private static final ConcurrentHashMap<PlayerRef, Integer> COUNTERS = new ConcurrentHashMap<>();

    private TemplateDemoPage() {}

    public static void open(PlayerRef playerRef, Store<EntityStore> store) {
        try {
            openInner(playerRef, store);
        } catch (Throwable t) {
            Ev0Log.warn(LOGGER, "TemplateDemoPage: open failed: " + t.getMessage());
        }
    }

    private static void openInner(PlayerRef playerRef, Store<EntityStore> store) {
        int counter = COUNTERS.getOrDefault(playerRef, 0);

        // 1. Load the raw template HTML from resources using the context class loader
        String rawTemplate;
        try (InputStream in = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(TEMPLATE_PATH)) {
            if (in == null) {
                Ev0Log.warn(LOGGER, "Template not found: " + TEMPLATE_PATH);
                return;
            }
            rawTemplate = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            Ev0Log.warn(LOGGER, "Failed to load template: " + e.getMessage());
            return;
        }

        // 2. Substitute template variables
        String html = rawTemplate
                .replace("{{title}}", "Template Demo")
                .replace("{{description}}", "Built from a resource template with per-player state.")
                .replace("{{counter}}", String.valueOf(counter));

        // 3. Build the page
        PageBuilder builder = PageBuilder.pageForPlayer(playerRef)
                .fromHtml(html)
                .withLifetime(CustomPageLifetime.CanDismissOrCloseThroughInteraction);

        // 4. Wire up event listeners
        builder.addEventListener("incrementBtn", CustomUIEventBindingType.Activating, (ign, ctx) -> {
            final int newVal = COUNTERS.merge(playerRef, 1, Integer::sum);
            ctx.getById("counterLabel", LabelBuilder.class)
                    .ifPresent(lb -> lb.withText(String.valueOf(newVal)));
            ctx.updatePage(true);
        });

        builder.addEventListener("decrementBtn", CustomUIEventBindingType.Activating, (ign, ctx) -> {
            final int newVal = COUNTERS.merge(playerRef, -1, (old, delta) -> Math.max(0, old + delta));
            ctx.getById("counterLabel", LabelBuilder.class)
                    .ifPresent(lb -> lb.withText(String.valueOf(newVal)));
            ctx.updatePage(true);
        });

        builder.addEventListener("closeBtn", CustomUIEventBindingType.Activating, (ign, ctx) -> {
            COUNTERS.remove(playerRef);
            // Page auto-dismisses
        });

        // 5. Open the page for the player
        builder.open(store);
    }

    /** Clean up any state for a disconnected player. */
    public static void cleanup(PlayerRef playerRef) {
        COUNTERS.remove(playerRef);
    }
}