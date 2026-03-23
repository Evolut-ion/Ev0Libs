/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  au.ellie.hyui.builders.LabelBuilder
 *  au.ellie.hyui.builders.PageBuilder
 *  au.ellie.hyui.events.UIContext
 *  com.hypixel.hytale.component.Component
 *  com.hypixel.hytale.component.ComponentType
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.logger.HytaleLogger
 *  com.hypixel.hytale.logger.HytaleLogger$Api
 *  com.hypixel.hytale.math.util.ChunkUtil
 *  com.hypixel.hytale.math.vector.Vector3i
 *  com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime
 *  com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType
 *  com.hypixel.hytale.server.core.inventory.ItemStack
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  com.hypixel.hytale.server.core.universe.world.World
 *  com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk
 *  com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk
 *  com.hypixel.hytale.server.core.universe.world.storage.ChunkStore
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 */
package org.Ev0Mods.plugin.api.ui;

import au.ellie.hyui.builders.LabelBuilder;
import au.ellie.hyui.builders.PageBuilder;
import au.ellie.hyui.events.UIContext;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.Ev0Mods.plugin.Ev0Lib;
import org.Ev0Mods.plugin.api.Ev0Log;
import org.Ev0Mods.plugin.api.block.state.HopperProcessor;
import org.Ev0Mods.plugin.api.component.EngineCompat;
import org.Ev0Mods.plugin.api.component.HopperComponent;

public final class HopperUIPage {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final ConcurrentHashMap<PlayerRef, String> ACTIVE_TAB = new ConcurrentHashMap();
    private static final String[] TAB_ORDER = new String[]{"Status", "Filter", "Signals"};

    private HopperUIPage() {
    }

    public static void open(PlayerRef playerRef, Store<EntityStore> store, Vector3i pos, String heldItemId) {
        Object hopperSlot;
        String blText;
        String wlText;
        String mode;
        HopperComponent hp;
        String activeTab;
        block9: {
            activeTab = ACTIVE_TAB.getOrDefault(playerRef, "Status");
            hp = HopperUIPage.lookupHopper(store, pos);
            mode = "Off";
            wlText = "(empty)";
            blText = "(empty)";
            hopperSlot = "(empty)";
            if (hp != null) {
                mode = hp.getFilterMode();
                List<String> wl = hp.getWhitelist();
                List<String> bl = hp.getBlacklist();
                wlText = wl.isEmpty() ? "(empty)" : String.join((CharSequence)", ", wl);
                blText = bl.isEmpty() ? "(empty)" : String.join((CharSequence)", ", bl);
                try {
                    ItemStack stack = hp.getItemContainer().getItemStack((short)0);
                    if (stack == null) break block9;
                    String key = hp.resolveItemStackKey(stack);
                    int qty = 0;
                    try {
                        qty = stack.getQuantity();
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    hopperSlot = (key != null ? key : "(unknown)") + " x" + qty;
                }
                catch (Throwable stack) {
                    // empty catch block
                }
            }
        }
        String heldDisplay = heldItemId != null && !heldItemId.isBlank() ? heldItemId : null;
        boolean showArcio = HopperProcessor.ARCIO_PRESENT;
        String arcioMode = hp != null ? hp.getArcioMode() : "IgnoreSignal";
        String html = HopperUIPage.buildHtml(mode, (String)hopperSlot, wlText, blText, heldDisplay, showArcio, arcioMode, activeTab);
        PageBuilder builder = ((PageBuilder)PageBuilder.pageForPlayer((PlayerRef)playerRef).fromHtml(html)).withLifetime(CustomPageLifetime.CanDismissOrCloseThroughInteraction);
        String currentTab = activeTab;
        builder.addEventListener("prevTab", CustomUIEventBindingType.Activating, (ign, ctx) -> {
            int idx = Arrays.asList(TAB_ORDER).indexOf(currentTab);
            ACTIVE_TAB.put(playerRef, TAB_ORDER[(idx - 1 + TAB_ORDER.length) % TAB_ORDER.length]);
            HopperUIPage.open(playerRef, store, pos, heldItemId);
        });
        builder.addEventListener("nextTab", CustomUIEventBindingType.Activating, (ign, ctx) -> {
            int idx = Arrays.asList(TAB_ORDER).indexOf(currentTab);
            ACTIVE_TAB.put(playerRef, TAB_ORDER[(idx + 1) % TAB_ORDER.length]);
            HopperUIPage.open(playerRef, store, pos, heldItemId);
        });
        if ("Filter".equals(activeTab)) {
            if (heldDisplay != null) {
                String heldId = heldDisplay;
                builder.addEventListener("addHeldWl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                    HopperComponent hopper = HopperUIPage.lookupHopper(store, pos);
                    if (hopper == null) {
                        return;
                    }
                    for (String e : hopper.getWhitelist()) {
                        if (e == null || !e.equalsIgnoreCase(heldId)) continue;
                        return;
                    }
                    hopper.addToWhitelist(heldId);
                    HopperUIPage.refreshLabels(ctx, store, pos);
                });
                builder.addEventListener("addHeldBl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                    HopperComponent hopper = HopperUIPage.lookupHopper(store, pos);
                    if (hopper == null) {
                        return;
                    }
                    for (String e : hopper.getBlacklist()) {
                        if (e == null || !e.equalsIgnoreCase(heldId)) continue;
                        return;
                    }
                    hopper.addToBlacklist(heldId);
                    HopperUIPage.refreshLabels(ctx, store, pos);
                });
            }
            builder.addEventListener("addWl", CustomUIEventBindingType.Activating, (ignored, ctx) -> ctx.getValue("itemInput", String.class).ifPresent(text -> {
                if (text.isBlank()) {
                    return;
                }
                HopperComponent hopper = HopperUIPage.lookupHopper(store, pos);
                if (hopper == null) {
                    return;
                }
                String id = text.trim();
                for (String e : hopper.getWhitelist()) {
                    if (e == null || !e.equalsIgnoreCase(id)) continue;
                    return;
                }
                hopper.addToWhitelist(id);
                HopperUIPage.refreshLabels(ctx, store, pos);
            }));
            builder.addEventListener("addBl", CustomUIEventBindingType.Activating, (ignored, ctx) -> ctx.getValue("itemInput", String.class).ifPresent(text -> {
                if (text.isBlank()) {
                    return;
                }
                HopperComponent hopper = HopperUIPage.lookupHopper(store, pos);
                if (hopper == null) {
                    return;
                }
                String id = text.trim();
                for (String e : hopper.getBlacklist()) {
                    if (e == null || !e.equalsIgnoreCase(id)) continue;
                    return;
                }
                hopper.addToBlacklist(id);
                HopperUIPage.refreshLabels(ctx, store, pos);
            }));
            builder.addEventListener("removeWl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                HopperComponent hopper = HopperUIPage.lookupHopper(store, pos);
                if (hopper == null) {
                    return;
                }
                hopper.removeLastFromWhitelist();
                HopperUIPage.refreshLabels(ctx, store, pos);
            });
            builder.addEventListener("removeBl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                HopperComponent hopper = HopperUIPage.lookupHopper(store, pos);
                if (hopper == null) {
                    return;
                }
                hopper.removeLastFromBlacklist();
                HopperUIPage.refreshLabels(ctx, store, pos);
            });
            builder.addEventListener("clearWl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                HopperComponent hopper = HopperUIPage.lookupHopper(store, pos);
                if (hopper == null) {
                    return;
                }
                hopper.clearWhitelist();
                HopperUIPage.refreshLabels(ctx, store, pos);
            });
            builder.addEventListener("clearBl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                HopperComponent hopper = HopperUIPage.lookupHopper(store, pos);
                if (hopper == null) {
                    return;
                }
                hopper.clearBlacklist();
                HopperUIPage.refreshLabels(ctx, store, pos);
            });
            builder.addEventListener("modeOff", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                HopperComponent hopper = HopperUIPage.lookupHopper(store, pos);
                if (hopper == null) {
                    return;
                }
                hopper.setFilterMode("Off");
                HopperUIPage.refreshLabels(ctx, store, pos);
            });
            builder.addEventListener("modeWl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                HopperComponent hopper = HopperUIPage.lookupHopper(store, pos);
                if (hopper == null) {
                    return;
                }
                hopper.setFilterMode("Whitelist");
                HopperUIPage.refreshLabels(ctx, store, pos);
            });
            builder.addEventListener("modeBl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                HopperComponent hopper = HopperUIPage.lookupHopper(store, pos);
                if (hopper == null) {
                    return;
                }
                hopper.setFilterMode("Blacklist");
                HopperUIPage.refreshLabels(ctx, store, pos);
            });
        }
        if ("Signals".equals(activeTab) && HopperComponent.ARCIO_PRESENT) {
            builder.addEventListener("arcioIgnoreSignal", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                HopperComponent hopper = HopperUIPage.lookupHopper(store, pos);
                if (hopper == null) {
                    return;
                }
                hopper.setArcioMode("IgnoreSignal");
                HopperUIPage.refreshLabels(ctx, store, pos);
            });
            builder.addEventListener("arcioEnableSignal", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                HopperComponent hopper = HopperUIPage.lookupHopper(store, pos);
                if (hopper == null) {
                    return;
                }
                hopper.setArcioMode("EnableWhenSignal");
                HopperUIPage.refreshLabels(ctx, store, pos);
            });
        }
        builder.open(store);
        Ev0Log.info(LOGGER, "HopperUI: opened page for pos=" + String.valueOf(pos) + " player=" + String.valueOf(playerRef));
        ((HytaleLogger.Api)LOGGER.atWarning()).log("[Ev0Lib][DIAG] HopperUI opened for player=" + String.valueOf(playerRef) + " pos=" + String.valueOf(pos) + " hopperPresent=" + (hp != null));
        if (hp == null) {
            Ev0Log.warn(LOGGER, "HopperUI: no hopper found at pos=" + String.valueOf(pos) + " when opening UI");
            ((HytaleLogger.Api)LOGGER.atWarning()).log("[Ev0Lib][DIAG] HopperUI lookup returned null for pos=" + String.valueOf(pos));
        }
    }

    private static String buildHtml(String mode, String hopperSlot, String wlText, String blText, String heldItem, boolean showArcio, String arcioMode, String activeTab) {
        String styles = "<style>\n    .section-title {\n        font-weight: bold;\n        color: #bdcbd3;\n        font-size: 16;\n        padding-top: 12;\n        padding-bottom: 4;\n    }\n    .info-label {\n        padding-top: 4;\n        padding-bottom: 4;\n        color: #a0b8c8;\n        font-size: 14;\n    }\n    .separator {\n        layout-mode: Full;\n        anchor-height: 2;\n        background-color: #ffffff(0.15);\n        margin-top: 8;\n        margin-bottom: 8;\n    }\n    .btn-row {\n        layout-mode: Left;\n        padding-top: 6;\n        padding-bottom: 6;\n        spacing: 8;\n    }\n    .input-field {\n        padding-top: 8;\n        padding-bottom: 8;\n    }\n    .tab-nav {\n        layout-mode: Center;\n        padding-top: 6;\n        padding-bottom: 2;\n        spacing: 16;\n    }\n    .tab-label {\n        font-weight: bold;\n        font-size: 18;\n        color: #ffffff;\n        min-width: 120;\n        text-align: center;\n    }\n    .tab-arrow {\n        anchor-width: 36;\n        anchor-height: 36;\n        font-size: 18;\n        padding: 4 10;\n    }\n</style>\n";
        String tabNav = "            <div class=\"tab-nav\">\n                <button id=\"prevTab\" class=\"tab-arrow\">&lt;</button>\n                <p class=\"tab-label\">%s</p>\n                <button id=\"nextTab\" class=\"tab-arrow\">&gt;</button>\n            </div>\n            <div class=\"separator\"></div>\n".formatted(activeTab.toUpperCase());
        String content = switch (activeTab) {
            case "Filter" -> HopperUIPage.buildFilterTab(wlText, blText, heldItem);
            case "Signals" -> HopperUIPage.buildSignalsTab(showArcio, arcioMode);
            default -> HopperUIPage.buildStatusTab(mode, hopperSlot);
        };
        return styles + "<div class=\"page-overlay\">\n    <div class=\"decorated-container\" data-hyui-title=\"Hopper Filter\" style=\"anchor-width: 640; anchor-height: 840;\">\n        <div class=\"container-contents\" style=\"layout-mode: Top; padding: 16 28;\">\n" + tabNav + content + "        </div>\n    </div>\n</div>\n";
    }

    private static String buildStatusTab(String mode, String hopperSlot) {
        return "            <p class=\"section-title\">Status</p>\n            <p id=\"modeLabel\" class=\"info-label\">Mode: %s</p>\n            <p id=\"hopperSlotLabel\" class=\"info-label\">Hopper Slot: %s</p>\n".formatted(mode, hopperSlot);
    }

    private static String buildFilterTab(String wlText, String blText, String heldItem) {
        String heldSection = "";
        if (heldItem != null) {
            heldSection = "        <div class=\"separator\"></div>\n        <p class=\"section-title\">Held Item</p>\n        <p id=\"heldLabel\" class=\"info-label\">In Hand: %s</p>\n        <div class=\"btn-row\">\n            <button id=\"addHeldWl\" class=\"small-secondary-button\" style=\"padding: 4 12;\">+ Whitelist</button>\n            <button id=\"addHeldBl\" class=\"small-secondary-button\" style=\"padding: 4 12;\">+ Blacklist</button>\n        </div>\n".formatted(heldItem);
        }
        return "            <p class=\"section-title\">Filter Lists</p>\n            <p id=\"wlLabel\" class=\"info-label\">Whitelist: %s</p>\n            <p id=\"blLabel\" class=\"info-label\">Blacklist: %s</p>\n            %s\n            <div class=\"separator\"></div>\n            <p class=\"section-title\">Item Entry</p>\n            <div class=\"input-field\">\n                <input type=\"text\" id=\"itemInput\" value=\"\" placeholder=\"Item ID (e.g. Wood_Ash_Trunk)\" style=\"width: 100%%; padding: 8 12; font-size: 14;\" />\n            </div>\n            <div class=\"btn-row\">\n                <button id=\"addWl\" class=\"secondary-button\" style=\"padding: 6 16;\">+ Whitelist</button>\n                <button id=\"addBl\" class=\"secondary-button\" style=\"padding: 6 16;\">+ Blacklist</button>\n            </div>\n            <div class=\"btn-row\">\n                <button id=\"removeWl\" class=\"tertiary-button\" style=\"padding: 4 12;\">- Remove Last</button>\n                <button id=\"removeBl\" class=\"tertiary-button\" style=\"padding: 4 12;\">- Remove Last</button>\n            </div>\n            <div class=\"btn-row\">\n                <button id=\"clearWl\" class=\"tertiary-button\" style=\"padding: 4 12;\">Clear Whitelist</button>\n                <button id=\"clearBl\" class=\"tertiary-button\" style=\"padding: 4 12;\">Clear Blacklist</button>\n            </div>\n            <div class=\"separator\"></div>\n            <p class=\"section-title\">Filter Mode</p>\n            <div class=\"btn-row\">\n                <button id=\"modeOff\" class=\"primary-button\" style=\"padding: 8 20;\">Off</button>\n                <button id=\"modeWl\" class=\"primary-button\" style=\"padding: 8 20;\">Whitelist</button>\n                <button id=\"modeBl\" class=\"primary-button\" style=\"padding: 8 20;\">Blacklist</button>\n            </div>\n".formatted(wlText, blText, heldSection);
    }

    private static String buildSignalsTab(boolean showArcio, String arcioMode) {
        if (!showArcio) {
            return "        <p class=\"section-title\">ArcIO Signals</p>\n        <p class=\"info-label\">ArcIO is not installed on this server.</p>\n";
        }
        String modeDisplay = "EnableWhenSignal".equals(arcioMode) ? "Enable When Signal" : "Ignore Signal";
        return "            <p class=\"section-title\">ArcIO Signal Control</p>\n            <p id=\"arcioModeLabel\" class=\"info-label\">ArcIO Mode: %s</p>\n            <div class=\"btn-row\">\n                <button id=\"arcioIgnoreSignal\" class=\"primary-button\" style=\"padding: 8 20;\">Ignore Signal</button>\n                <button id=\"arcioEnableSignal\" class=\"secondary-button\" style=\"padding: 8 20;\">Enable When Signal</button>\n            </div>\n".formatted(modeDisplay);
    }

    private static void refreshLabels(UIContext ctx, Store<EntityStore> store, Vector3i pos) {
        HopperComponent hp = HopperUIPage.lookupHopper(store, pos);
        if (hp == null) {
            return;
        }
        ctx.getById("modeLabel", LabelBuilder.class).ifPresent(lb -> lb.withText("Mode: " + hp.getFilterMode()));
        List<String> wl = hp.getWhitelist();
        ctx.getById("wlLabel", LabelBuilder.class).ifPresent(lb -> lb.withText("Whitelist: " + (wl.isEmpty() ? "(empty)" : String.join((CharSequence)", ", wl))));
        List<String> bl = hp.getBlacklist();
        ctx.getById("blLabel", LabelBuilder.class).ifPresent(lb -> lb.withText("Blacklist: " + (bl.isEmpty() ? "(empty)" : String.join((CharSequence)", ", bl))));
        try {
            Object slotText;
            ItemStack stack = hp.getItemContainer().getItemStack((short)0);
            if (stack != null) {
                String key = hp.resolveItemStackKey(stack);
                int qty = 0;
                try {
                    qty = stack.getQuantity();
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                slotText = (key != null ? key : "(unknown)") + " x" + qty;
            } else {
                slotText = "(empty)";
            }
            ctx.getById("hopperSlotLabel", LabelBuilder.class).ifPresent(arg_0 -> HopperUIPage.lambda$refreshLabels$3((String)slotText, arg_0));
        }
        catch (Throwable stack) {
            // empty catch block
        }
        if (HopperProcessor.ARCIO_PRESENT) {
            String modeDisplay = "EnableWhenSignal".equals(hp.getArcioMode()) ? "Enable When Signal" : "Ignore Signal";
            ctx.getById("arcioModeLabel", LabelBuilder.class).ifPresent(lb -> lb.withText("ArcIO Mode: " + modeDisplay));
        }
        ctx.updatePage(true);
    }

    private static HopperComponent lookupHopper(Store<EntityStore> store, Vector3i pos) {
        try {
            World world = ((EntityStore)store.getExternalData()).getWorld();
            if (world == null) {
                return null;
            }
            WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock((int)pos.x, (int)pos.z));
            if (chunk == null) {
                ((HytaleLogger.Api)LOGGER.atWarning()).log("[Ev0Lib][DIAG] lookupHopper: chunk null at pos=" + String.valueOf(pos));
                return null;
            }
            try {
                Component comp;
                ComponentType<ChunkStore, HopperComponent> compType;
                Ev0Lib lib;
                Ref blockRef;
                BlockComponentChunk bcc;
                Store cs = world.getChunkStore().getStore();
                Ref chunkRef = world.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock((int)pos.x, (int)pos.z));
                if (chunkRef != null && (bcc = (BlockComponentChunk)cs.getComponent(chunkRef, BlockComponentChunk.getComponentType())) != null && (blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn((int)pos.x, (int)pos.y, (int)pos.z))) != null && (lib = Ev0Lib.getInstance()) != null && (compType = lib.getHopperComponentType()) != null && (comp = cs.getComponent(blockRef, compType)) instanceof HopperComponent) {
                    HopperComponent hc = (HopperComponent)comp;
                    ((HytaleLogger.Api)LOGGER.atWarning()).log("[Ev0Lib][DIAG] lookupHopper: found component HopperComponent at pos=" + String.valueOf(pos));
                    return hc;
                }
            }
            catch (Throwable cs) {
                // empty catch block
            }
            Object state = EngineCompat.getState(chunk, pos.x, pos.y, pos.z);
            Object blockType = EngineCompat.getBlockType(chunk, pos.x, pos.y, pos.z);
            ((HytaleLogger.Api)LOGGER.atWarning()).log("[Ev0Lib][DIAG] lookupHopper: blockType=" + (String)(blockType == null ? "null" : blockType.getClass().getName() + " -> " + blockType.toString()));
            if (state == null) {
                ((HytaleLogger.Api)LOGGER.atWarning()).log("[Ev0Lib][DIAG] lookupHopper: no state object at pos=" + String.valueOf(pos) + " (chunk exists)");
                return null;
            }
            ((HytaleLogger.Api)LOGGER.atWarning()).log("[Ev0Lib][DIAG] lookupHopper: found state class=" + state.getClass().getName() + " at pos=" + String.valueOf(pos));
            if (state instanceof HopperProcessor) {
                HopperProcessor hpState = (HopperProcessor)state;
                HopperComponent hc = new HopperComponent();
                try {
                    hc.data = hpState.data;
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                return hc;
            }
            return null;
        }
        catch (Throwable t) {
            Ev0Log.warn(LOGGER, "HopperUI: failed to lookup hopper at " + String.valueOf(pos) + ": " + t.getMessage());
            ((HytaleLogger.Api)LOGGER.atWarning()).log("[Ev0Lib][DIAG] HopperUI lookup failed for pos=" + String.valueOf(pos) + ": " + String.valueOf(t));
            return null;
        }
    }

    private static /* synthetic */ void lambda$refreshLabels$3(String slotText, LabelBuilder lb) {
        lb.withText("Hopper Slot: " + slotText);
    }
}

