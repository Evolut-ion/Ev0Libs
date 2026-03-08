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

    // ─── Tab navigation state (per player, non-persistent) ────────────────────
    private static final java.util.concurrent.ConcurrentHashMap<PlayerRef, String> ACTIVE_TAB =
            new java.util.concurrent.ConcurrentHashMap<>();
    private static final String[] TAB_ORDER = {"Status", "Filter", "Signals"};

    private HopperUIPage() {} // utility class – all access through open()

    // ─────────────────────────────────────────────────────────────────────────
    // Public entry point
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Opens the hopper filter UI for the given player at the given block position.
     */
    public static void open(PlayerRef playerRef, Store<EntityStore> store, Vector3i pos, String heldItemId) {
        String activeTab = ACTIVE_TAB.getOrDefault(playerRef, "Status");
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
        boolean showArcio = HopperProcessor.ARCIO_PRESENT;
        String arcioMode = hp != null ? hp.getArcioMode() : "IgnoreSignal";
        String html = buildHtml(mode, hopperSlot, wlText, blText, heldDisplay, showArcio, arcioMode, activeTab);

        PageBuilder builder = PageBuilder.pageForPlayer(playerRef)
                .fromHtml(html)
                .withLifetime(CustomPageLifetime.CanDismissOrCloseThroughInteraction);

        // ── Tab navigation (always present) ───────────────────────────────
        final String currentTab = activeTab;
        builder.addEventListener("prevTab", CustomUIEventBindingType.Activating, (ign, ctx) -> {
            int idx = java.util.Arrays.asList(TAB_ORDER).indexOf(currentTab);
            ACTIVE_TAB.put(playerRef, TAB_ORDER[(idx - 1 + TAB_ORDER.length) % TAB_ORDER.length]);
            HopperUIPage.open(playerRef, store, pos, heldItemId);
        });
        builder.addEventListener("nextTab", CustomUIEventBindingType.Activating, (ign, ctx) -> {
            int idx = java.util.Arrays.asList(TAB_ORDER).indexOf(currentTab);
            ACTIVE_TAB.put(playerRef, TAB_ORDER[(idx + 1) % TAB_ORDER.length]);
            HopperUIPage.open(playerRef, store, pos, heldItemId);
        });

        // ── Filter tab listeners (only when Filter tab is active) ──────────
        if ("Filter".equals(activeTab)) {
            if (heldDisplay != null) {
                final String heldId = heldDisplay;
                builder.addEventListener("addHeldWl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                    HopperProcessor hopper = lookupHopper(store, pos);
                    if (hopper == null) return;
                    for (String e : hopper.getWhitelist())
                        if (e != null && e.equalsIgnoreCase(heldId)) return;
                    hopper.addToWhitelist(heldId);
                    refreshLabels(ctx, store, pos);
                });
                builder.addEventListener("addHeldBl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                    HopperProcessor hopper = lookupHopper(store, pos);
                    if (hopper == null) return;
                    for (String e : hopper.getBlacklist())
                        if (e != null && e.equalsIgnoreCase(heldId)) return;
                    hopper.addToBlacklist(heldId);
                    refreshLabels(ctx, store, pos);
                });
            }
            builder.addEventListener("addWl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                ctx.getValue("itemInput", String.class).ifPresent(text -> {
                    if (text.isBlank()) return;
                    HopperProcessor hopper = lookupHopper(store, pos);
                    if (hopper == null) return;
                    String id = text.trim();
                    for (String e : hopper.getWhitelist())
                        if (e != null && e.equalsIgnoreCase(id)) return;
                    hopper.addToWhitelist(id);
                    refreshLabels(ctx, store, pos);
                });
            });
            builder.addEventListener("addBl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                ctx.getValue("itemInput", String.class).ifPresent(text -> {
                    if (text.isBlank()) return;
                    HopperProcessor hopper = lookupHopper(store, pos);
                    if (hopper == null) return;
                    String id = text.trim();
                    for (String e : hopper.getBlacklist())
                        if (e != null && e.equalsIgnoreCase(id)) return;
                    hopper.addToBlacklist(id);
                    refreshLabels(ctx, store, pos);
                });
            });
            builder.addEventListener("removeWl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                HopperProcessor hopper = lookupHopper(store, pos);
                if (hopper == null) return;
                hopper.removeLastFromWhitelist();
                refreshLabels(ctx, store, pos);
            });
            builder.addEventListener("removeBl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                HopperProcessor hopper = lookupHopper(store, pos);
                if (hopper == null) return;
                hopper.removeLastFromBlacklist();
                refreshLabels(ctx, store, pos);
            });
            builder.addEventListener("clearWl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                HopperProcessor hopper = lookupHopper(store, pos);
                if (hopper == null) return;
                hopper.clearWhitelist();
                refreshLabels(ctx, store, pos);
            });
            builder.addEventListener("clearBl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                HopperProcessor hopper = lookupHopper(store, pos);
                if (hopper == null) return;
                hopper.clearBlacklist();
                refreshLabels(ctx, store, pos);
            });
            builder.addEventListener("modeOff", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                HopperProcessor hopper = lookupHopper(store, pos);
                if (hopper == null) return;
                hopper.setFilterMode("Off");
                refreshLabels(ctx, store, pos);
            });
            builder.addEventListener("modeWl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                HopperProcessor hopper = lookupHopper(store, pos);
                if (hopper == null) return;
                hopper.setFilterMode("Whitelist");
                refreshLabels(ctx, store, pos);
            });
            builder.addEventListener("modeBl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                HopperProcessor hopper = lookupHopper(store, pos);
                if (hopper == null) return;
                hopper.setFilterMode("Blacklist");
                refreshLabels(ctx, store, pos);
            });
        }

        // ── Signals tab listeners (only when Signals tab is active) ────────
        if ("Signals".equals(activeTab) && HopperProcessor.ARCIO_PRESENT) {
            builder.addEventListener("arcioIgnoreSignal", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                HopperProcessor hopper = lookupHopper(store, pos);
                if (hopper == null) return;
                hopper.setArcioMode("IgnoreSignal");
                refreshLabels(ctx, store, pos);
            });
            builder.addEventListener("arcioEnableSignal", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                HopperProcessor hopper = lookupHopper(store, pos);
                if (hopper == null) return;
                hopper.setArcioMode("EnableWhenSignal");
                refreshLabels(ctx, store, pos);
            });
        }

        builder.open(store);
        //LOGGER.atInfo().log("HopperUI: opened page for pos=" + pos + " player=" + playerRef);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HYUIML layout
    // ─────────────────────────────────────────────────────────────────────────

    private static String buildHtml(String mode, String hopperSlot, String wlText, String blText, String heldItem,
                                    boolean showArcio, String arcioMode, String activeTab) {
        String styles = """
                <style>
                    .section-title {
                        font-weight: bold;
                        color: #bdcbd3;
                        font-size: 16;
                        padding-top: 12;
                        padding-bottom: 4;
                    }
                    .info-label {
                        padding-top: 4;
                        padding-bottom: 4;
                        color: #a0b8c8;
                        font-size: 14;
                    }
                    .separator {
                        layout-mode: Full;
                        anchor-height: 2;
                        background-color: #ffffff(0.15);
                        margin-top: 8;
                        margin-bottom: 8;
                    }
                    .btn-row {
                        layout-mode: Left;
                        padding-top: 6;
                        padding-bottom: 6;
                        spacing: 8;
                    }
                    .input-field {
                        padding-top: 8;
                        padding-bottom: 8;
                    }
                    .tab-nav {
                        layout-mode: Center;
                        padding-top: 6;
                        padding-bottom: 2;
                        spacing: 16;
                    }
                    .tab-label {
                        font-weight: bold;
                        font-size: 18;
                        color: #ffffff;
                        min-width: 120;
                        text-align: center;
                    }
                    .tab-arrow {
                        anchor-width: 36;
                        anchor-height: 36;
                        font-size: 18;
                        padding: 4 10;
                    }
                </style>
                """;

        String tabNav = """
                            <div class="tab-nav">
                                <button id="prevTab" class="tab-arrow">&lt;</button>
                                <p class="tab-label">%s</p>
                                <button id="nextTab" class="tab-arrow">&gt;</button>
                            </div>
                            <div class="separator"></div>
                """.formatted(activeTab.toUpperCase());

        String content = switch (activeTab) {
            case "Filter"  -> buildFilterTab(wlText, blText, heldItem);
            case "Signals" -> buildSignalsTab(showArcio, arcioMode);
            default        -> buildStatusTab(mode, hopperSlot);
        };

        return styles + """
                <div class="page-overlay">
                    <div class="decorated-container" data-hyui-title="Hopper Filter" style="anchor-width: 640; anchor-height: 840;">
                        <div class="container-contents" style="layout-mode: Top; padding: 16 28;">
                """ + tabNav + content + """
                        </div>
                    </div>
                </div>
                """;
    }

    private static String buildStatusTab(String mode, String hopperSlot) {
        return """
                            <p class="section-title">Status</p>
                            <p id="modeLabel" class="info-label">Mode: %s</p>
                            <p id="hopperSlotLabel" class="info-label">Hopper Slot: %s</p>
                """.formatted(mode, hopperSlot);
    }

    private static String buildFilterTab(String wlText, String blText, String heldItem) {
        String heldSection = "";
        if (heldItem != null) {
            heldSection = """
                            <div class="separator"></div>
                            <p class="section-title">Held Item</p>
                            <p id="heldLabel" class="info-label">In Hand: %s</p>
                            <div class="btn-row">
                                <button id="addHeldWl" class="small-secondary-button" style="padding: 4 12;">+ Whitelist</button>
                                <button id="addHeldBl" class="small-secondary-button" style="padding: 4 12;">+ Blacklist</button>
                            </div>
                    """.formatted(heldItem);
        }
        return """
                            <p class="section-title">Filter Lists</p>
                            <p id="wlLabel" class="info-label">Whitelist: %s</p>
                            <p id="blLabel" class="info-label">Blacklist: %s</p>
                            %s
                            <div class="separator"></div>
                            <p class="section-title">Item Entry</p>
                            <div class="input-field">
                                <input type="text" id="itemInput" value="" placeholder="Item ID (e.g. Wood_Ash_Trunk)" style="width: 100%%; padding: 8 12; font-size: 14;" />
                            </div>
                            <div class="btn-row">
                                <button id="addWl" class="secondary-button" style="padding: 6 16;">+ Whitelist</button>
                                <button id="addBl" class="secondary-button" style="padding: 6 16;">+ Blacklist</button>
                            </div>
                            <div class="btn-row">
                                <button id="removeWl" class="tertiary-button" style="padding: 4 12;">- Remove Last</button>
                                <button id="removeBl" class="tertiary-button" style="padding: 4 12;">- Remove Last</button>
                            </div>
                            <div class="btn-row">
                                <button id="clearWl" class="tertiary-button" style="padding: 4 12;">Clear Whitelist</button>
                                <button id="clearBl" class="tertiary-button" style="padding: 4 12;">Clear Blacklist</button>
                            </div>
                            <div class="separator"></div>
                            <p class="section-title">Filter Mode</p>
                            <div class="btn-row">
                                <button id="modeOff" class="primary-button" style="padding: 8 20;">Off</button>
                                <button id="modeWl" class="primary-button" style="padding: 8 20;">Whitelist</button>
                                <button id="modeBl" class="primary-button" style="padding: 8 20;">Blacklist</button>
                            </div>
                """.formatted(wlText, blText, heldSection);
    }

    private static String buildSignalsTab(boolean showArcio, String arcioMode) {
        if (!showArcio) {
            return """
                            <p class="section-title">ArcIO Signals</p>
                            <p class="info-label">ArcIO is not installed on this server.</p>
                    """;
        }
        String modeDisplay = "EnableWhenSignal".equals(arcioMode) ? "Enable When Signal" : "Ignore Signal";
        return """
                            <p class="section-title">ArcIO Signal Control</p>
                            <p id="arcioModeLabel" class="info-label">ArcIO Mode: %s</p>
                            <div class="btn-row">
                                <button id="arcioIgnoreSignal" class="primary-button" style="padding: 8 20;">Ignore Signal</button>
                                <button id="arcioEnableSignal" class="secondary-button" style="padding: 8 20;">Enable When Signal</button>
                            </div>
                """.formatted(modeDisplay);
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

        // Update ArcIO mode label when ArcIO is installed
        if (HopperProcessor.ARCIO_PRESENT) {
            String modeDisplay = "EnableWhenSignal".equals(hp.getArcioMode()) ? "Enable When Signal" : "Ignore Signal";
            ctx.getById("arcioModeLabel", LabelBuilder.class)
                    .ifPresent(lb -> lb.withText("ArcIO Mode: " + modeDisplay));
        }

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
            //LOGGER.atWarning().log("HopperUI: failed to lookup hopper at " + pos + ": " + t.getMessage());
            return null;
        }
    }
}
