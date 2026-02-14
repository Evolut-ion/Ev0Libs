package org.Ev0Mods.plugin.api.ui;

import au.ellie.hyui.builders.LabelBuilder;
import au.ellie.hyui.builders.PageBuilder;
import au.ellie.hyui.events.UIContext;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.Ev0Mods.plugin.api.block.state.HopperProcessor;

import java.util.List;

/**
 * Hopper filter UI page built with HyUI's PageBuilder + HYUIML.
 * Uses HyUI's proven TextFieldBuilder value capture mechanism:
 * the {@code <input type="text">} element auto-registers ValueChanged bindings
 * so {@code ctx.getValue("itemInput", String.class)} retrieves the latest typed text.
 */
public final class HopperUIPage {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private HopperUIPage() {} // utility class – all access through open()

    // ─────────────────────────────────────────────────────────────────────────
    // Public entry point
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Opens the hopper filter UI for the given player at the given block position.
     */
    public static void open(PlayerRef playerRef, Store<EntityStore> store, Vector3i pos, String heldItemId) {
        // Resolve hopper state to populate initial label values
        HopperProcessor hp = lookupHopper(store, pos);

        String mode = "Off";
        String wlText = "(empty)";
        String blText = "(empty)";
        String hopperSlot = "(empty)";

        if (hp != null) {
            mode = hp.getFilterMode();
            List<String> wl = hp.getWhitelist();
            List<String> bl = hp.getBlacklist();
            wlText = wl.isEmpty() ? "(empty)" : String.join(", ", wl);
            blText = bl.isEmpty() ? "(empty)" : String.join(", ", bl);

            try {
                var stack = hp.getItemContainer().getItemStack((short) 0);
                if (stack != null) {
                    String key = hp.resolveItemStackKey(stack);
                    int qty = 0;
                    try { qty = stack.getQuantity(); } catch (Throwable ignored) {}
                    hopperSlot = (key != null ? key : "(unknown)") + " x" + qty;
                }
            } catch (Throwable ignored) {}
        }

        String heldDisplay = (heldItemId != null && !heldItemId.isBlank()) ? heldItemId : null;
        String html = buildHtml(mode, hopperSlot, wlText, blText, heldDisplay);

        PageBuilder builder = PageBuilder.pageForPlayer(playerRef)
                .fromHtml(html)
                .withLifetime(CustomPageLifetime.CanDismissOrCloseThroughInteraction);

        // ── Add held item to WL / BL ───────────────────────────────────────
        if (heldDisplay != null) {
            final String heldId = heldDisplay;
            builder.addEventListener("addHeldWl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                HopperProcessor hopper = lookupHopper(store, pos);
                if (hopper == null) return;
                for (String e : hopper.getWhitelist())
                    if (e != null && e.equalsIgnoreCase(heldId)) return;
                hopper.addToWhitelist(heldId);
                LOGGER.atInfo().log("HopperUI: added held item to whitelist: " + heldId);
                refreshLabels(ctx, store, pos);
            });
            builder.addEventListener("addHeldBl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                HopperProcessor hopper = lookupHopper(store, pos);
                if (hopper == null) return;
                for (String e : hopper.getBlacklist())
                    if (e != null && e.equalsIgnoreCase(heldId)) return;
                hopper.addToBlacklist(heldId);
                LOGGER.atInfo().log("HopperUI: added held item to blacklist: " + heldId);
                refreshLabels(ctx, store, pos);
            });
        }

        // ── Add to Whitelist ────────────────────────────────────────────────
        builder.addEventListener("addWl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
            ctx.getValue("itemInput", String.class).ifPresent(text -> {
                if (text.isBlank()) return;
                HopperProcessor hopper = lookupHopper(store, pos);
                if (hopper == null) return;
                String id = text.trim();
                // avoid duplicates (case-insensitive)
                for (String e : hopper.getWhitelist())
                    if (e != null && e.equalsIgnoreCase(id)) return;
                hopper.addToWhitelist(id);
                LOGGER.atInfo().log("HopperUI: added to whitelist: " + id);
                refreshLabels(ctx, store, pos);
            });
        });

        // ── Add to Blacklist ────────────────────────────────────────────────
        builder.addEventListener("addBl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
            ctx.getValue("itemInput", String.class).ifPresent(text -> {
                if (text.isBlank()) return;
                HopperProcessor hopper = lookupHopper(store, pos);
                if (hopper == null) return;
                String id = text.trim();
                for (String e : hopper.getBlacklist())
                    if (e != null && e.equalsIgnoreCase(id)) return;
                hopper.addToBlacklist(id);
                LOGGER.atInfo().log("HopperUI: added to blacklist: " + id);
                refreshLabels(ctx, store, pos);
            });
        });

        // ── Remove last from WL / BL ───────────────────────────────────────
        builder.addEventListener("removeWl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
            HopperProcessor hopper = lookupHopper(store, pos);
            if (hopper == null) return;
            String removed = hopper.removeLastFromWhitelist();
            LOGGER.atInfo().log("HopperUI: removed from whitelist: " + removed);
            refreshLabels(ctx, store, pos);
        });
        builder.addEventListener("removeBl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
            HopperProcessor hopper = lookupHopper(store, pos);
            if (hopper == null) return;
            String removed = hopper.removeLastFromBlacklist();
            LOGGER.atInfo().log("HopperUI: removed from blacklist: " + removed);
            refreshLabels(ctx, store, pos);
        });

        // ── Clear WL / BL ──────────────────────────────────────────────────
        builder.addEventListener("clearWl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
            HopperProcessor hopper = lookupHopper(store, pos);
            if (hopper == null) return;
            hopper.clearWhitelist();
            LOGGER.atInfo().log("HopperUI: cleared whitelist");
            refreshLabels(ctx, store, pos);
        });
        builder.addEventListener("clearBl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
            HopperProcessor hopper = lookupHopper(store, pos);
            if (hopper == null) return;
            hopper.clearBlacklist();
            LOGGER.atInfo().log("HopperUI: cleared blacklist");
            refreshLabels(ctx, store, pos);
        });

        // ── Mode buttons ───────────────────────────────────────────────────
        builder.addEventListener("modeOff", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
            HopperProcessor hopper = lookupHopper(store, pos);
            if (hopper == null) return;
            hopper.setFilterMode("Off");
            LOGGER.atInfo().log("HopperUI: set mode Off");
            refreshLabels(ctx, store, pos);
        });
        builder.addEventListener("modeWl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
            HopperProcessor hopper = lookupHopper(store, pos);
            if (hopper == null) return;
            hopper.setFilterMode("Whitelist");
            LOGGER.atInfo().log("HopperUI: set mode Whitelist");
            refreshLabels(ctx, store, pos);
        });
        builder.addEventListener("modeBl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
            HopperProcessor hopper = lookupHopper(store, pos);
            if (hopper == null) return;
            hopper.setFilterMode("Blacklist");
            LOGGER.atInfo().log("HopperUI: set mode Blacklist");
            refreshLabels(ctx, store, pos);
        });

        builder.open(store);
        LOGGER.atInfo().log("HopperUI: opened page for pos=" + pos + " player=" + playerRef);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HYUIML layout
    // ─────────────────────────────────────────────────────────────────────────

    private static String buildHtml(String mode, String hopperSlot, String wlText, String blText, String heldItem) {
        // Build the optional "Held Item" section
        String heldSection;
        if (heldItem != null) {
            heldSection = """

                            <div class="separator"></div>

                            <p class="section-title">Held Item</p>
                            <p id="heldLabel" class="info-label">In Hand: %s</p>
                            <div class="btn-row">
                                <button id="addHeldWl" class="small-secondary-button" data-hyui-tooltiptext="Add held item to whitelist" style="padding: 0 8;">+ WL (held)</button>
                                <button id="addHeldBl" class="small-secondary-button" data-hyui-tooltiptext="Add held item to blacklist" style="padding: 0 8;">+ BL (held)</button>
                            </div>
            """.formatted(heldItem);
        } else {
            heldSection = "";
        }

        return """
                <style>
                    .section-title {
                        font-weight: bold;
                        color: #bdcbd3;
                        font-size: 16;
                        padding-top: 6;
                        padding-bottom: 2;
                    }
                    .info-label {
                        padding-top: 2;
                        padding-bottom: 2;
                        color: #a0b8c8;
                    }
                    .separator {
                        layout-mode: Full;
                        anchor-height: 1;
                        background-color: #ffffff(0.15);
                    }
                    .btn-row {
                        layout-mode: Left;
                        padding-top: 4;
                        padding-bottom: 4;
                    }
                </style>
                <div class="page-overlay">
                    <div class="decorated-container" data-hyui-title="Hopper Filter" style="anchor-width: 560; anchor-height: 500;">
                        <div class="container-contents" style="layout-mode: Top; padding: 12 24;">

                            <p class="section-title">Status</p>
                            <p id="modeLabel" class="info-label">Mode: %s</p>
                            <p id="hopperSlotLabel" class="info-label">Hopper Slot: %s</p>

                            <div class="separator"></div>

                            <p class="section-title">Filter Lists</p>
                            <p id="wlLabel" class="info-label">Whitelist: %s</p>
                            <p id="blLabel" class="info-label">Blacklist: %s</p>
                            %s

                            <div class="separator"></div>

                            <p class="section-title">Item Entry</p>
                            <input type="text" id="itemInput" value="" placeholder="Item ID  (e.g. Wood_Ash_Trunk)" style="padding-top: 4; padding-bottom: 8;" />

                            <div class="btn-row">
                                <button id="addWl" class="small-secondary-button" data-hyui-tooltiptext="Add item to whitelist" style="padding: 0 8;">+ Whitelist</button>
                                <button id="addBl" class="small-secondary-button" data-hyui-tooltiptext="Add item to blacklist" style="padding: 0 8;">+ Blacklist</button>
                            </div>
                            <div class="btn-row">
                                <button id="removeWl" class="small-tertiary-button" data-hyui-tooltiptext="Remove last whitelist entry" style="padding: 0 6;">- Last WL</button>
                                <button id="removeBl" class="small-tertiary-button" data-hyui-tooltiptext="Remove last blacklist entry" style="padding: 0 6;">- Last BL</button>
                                <button id="clearWl" class="small-tertiary-button" data-hyui-tooltiptext="Clear entire whitelist" style="padding: 0 6;">Clear WL</button>
                                <button id="clearBl" class="small-tertiary-button" data-hyui-tooltiptext="Clear entire blacklist" style="padding: 0 6;">Clear BL</button>
                            </div>

                            <div class="separator"></div>

                            <p class="section-title">Filter Mode</p>
                            <div class="btn-row">
                                <button id="modeOff" class="secondary-button" data-hyui-tooltiptext="Disable filtering" style="padding: 0 10;">Off</button>
                                <button id="modeWl" class="secondary-button" data-hyui-tooltiptext="Only allow whitelisted items" style="padding: 0 10;">Whitelist</button>
                                <button id="modeBl" class="secondary-button" data-hyui-tooltiptext="Block blacklisted items" style="padding: 0 10;">Blacklist</button>
                            </div>

                        </div>
                    </div>
                </div>
                """.formatted(mode, hopperSlot, wlText, blText, heldSection);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Live label refresh (after any mutating action)
    // ─────────────────────────────────────────────────────────────────────────

    private static void refreshLabels(UIContext ctx, Store<EntityStore> store, Vector3i pos) {
        HopperProcessor hp = lookupHopper(store, pos);
        if (hp == null) return;

        ctx.getById("modeLabel", LabelBuilder.class)
                .ifPresent(lb -> lb.withText("Mode: " + hp.getFilterMode()));

        List<String> wl = hp.getWhitelist();
        ctx.getById("wlLabel", LabelBuilder.class)
                .ifPresent(lb -> lb.withText("Whitelist: " + (wl.isEmpty() ? "(empty)" : String.join(", ", wl))));

        List<String> bl = hp.getBlacklist();
        ctx.getById("blLabel", LabelBuilder.class)
                .ifPresent(lb -> lb.withText("Blacklist: " + (bl.isEmpty() ? "(empty)" : String.join(", ", bl))));

        // Update hopper slot display too
        try {
            var stack = hp.getItemContainer().getItemStack((short) 0);
            String slotText;
            if (stack != null) {
                String key = hp.resolveItemStackKey(stack);
                int qty = 0;
                try { qty = stack.getQuantity(); } catch (Throwable ignored) {}
                slotText = (key != null ? key : "(unknown)") + " x" + qty;
            } else {
                slotText = "(empty)";
            }
            ctx.getById("hopperSlotLabel", LabelBuilder.class)
                    .ifPresent(lb -> lb.withText("Hopper Slot: " + slotText));
        } catch (Throwable ignored) {}

        ctx.updatePage(true);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Hopper state lookup
    // ─────────────────────────────────────────────────────────────────────────

    private static HopperProcessor lookupHopper(Store<EntityStore> store, Vector3i pos) {
        try {
            World world = store.getExternalData().getWorld();
            if (world == null) return null;
            WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
            if (chunk == null) return null;
            Object state = chunk.getState(pos.x, pos.y, pos.z);
            return (state instanceof HopperProcessor hp) ? hp : null;
        } catch (Throwable t) {
            LOGGER.atWarning().log("HopperUI: failed to lookup hopper at " + pos + ": " + t.getMessage());
            return null;
        }
    }
}
