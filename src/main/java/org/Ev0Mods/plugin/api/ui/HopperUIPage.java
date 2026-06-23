/*
 * Decompiled with CFR 0.152.
 */
package org.Ev0Mods.plugin.api.ui;

import au.ellie.hyui.builders.LabelBuilder;
import au.ellie.hyui.builders.PageBuilder;
import au.ellie.hyui.events.UIContext;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.Ev0Mods.plugin.Ev0Lib;
import org.Ev0Mods.plugin.api.Ev0Log;
import org.Ev0Mods.plugin.api.block.state.HopperProcessor;
import org.Ev0Mods.plugin.api.component.EngineCompat;
import org.Ev0Mods.plugin.api.component.HopperComponent;
import org.Ev0Mods.plugin.api.system.WirelessRegistry;
import org.Ev0Mods.plugin.api.util.FacadeHelper;
import org.Ev0Mods.plugin.api.util.ItemUtilsExtended;
import org.Ev0Mods.plugin.api.util.WirelessHelpers;

public final class HopperUIPage {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final ConcurrentHashMap<PlayerRef, String> ACTIVE_TAB = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<PlayerRef, Vector3i> LAST_POS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<PlayerRef, Integer> LINKS_PAGE = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<PlayerRef, Ref<EntityStore>> PLAYER_ENTITY_REFS = new ConcurrentHashMap<>();
    // Guards against a single Collect click paying out more than once. Repeated open()
    // calls stack pages that all bind the same "collectItem" id, so one click can dispatch
    // to several listeners; this de-dupes them within a short window per (player, pos).
    private static final ConcurrentHashMap<String, Long> COLLECT_GUARD = new ConcurrentHashMap<>();
    private static final long COLLECT_GUARD_MS = 400L;
    private static final String[] TAB_ORDER = new String[]{"Status", "Filter", "Facade", "Signals", "Links"};

    private HopperUIPage() {
    }

    private static Store<EntityStore> liveStore(PlayerRef playerRef, Store<EntityStore> fallback) {
        try {
            Ref<EntityStore> ref = PLAYER_ENTITY_REFS.get(playerRef);
            if (ref != null && ref.isValid()) {
                return ref.getStore();
            }
        } catch (Throwable ignored) {}
        return fallback;
    }

    private static String escapeHtml(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }

    public static void open(PlayerRef playerRef, Store<EntityStore> store, Vector3i pos, String heldItemId) {
        try {
            HopperUIPage.openInner(playerRef, store, pos, heldItemId);
        }
        catch (Throwable t) {
            Ev0Log.warn(HytaleLogger.forEnclosingClass(), "HopperUI: open failed: " + t.getMessage());
        }
    }

    private static void openInner(PlayerRef playerRef, Store<EntityStore> store, Vector3i pos, String heldItemId) {
        String hopperSlot = "(empty)";
        String blText = "(empty)";
        String wlText = "(empty)";
        String mode = "Off";
        boolean isWireless = false;
        String resolvedHopperType = "Normal";
        HopperComponent hp = null;
        String activeTab = "Status";
        try {
            String regType;
            String rt;
            activeTab = ACTIVE_TAB.getOrDefault(playerRef, "Status");
            Vector3i lastPos = LAST_POS.get(playerRef);
            if (lastPos == null || !lastPos.equals(pos)) {
                ACTIVE_TAB.remove(playerRef);
                LINKS_PAGE.remove(playerRef);
                activeTab = "Status";
            }
            LAST_POS.put(playerRef, pos);
            hp = HopperUIPage.lookupHopper(store, pos);
            resolvedHopperType = "Normal";
            boolean hopperTypeKnown = false;
            World _w = store.getExternalData().getWorld();
            if ((rt = WirelessHelpers.getHopperType(_w, pos)) != null) {
                resolvedHopperType = rt;
                hopperTypeKnown = true;
            }
            if ("Normal".equals(resolvedHopperType) && hp != null && hp.data != null && hp.data.hopperType != null && !"Normal".equals(hp.data.hopperType)) {
                resolvedHopperType = hp.data.hopperType;
                hopperTypeKnown = true;
            }
            if ("Normal".equals(resolvedHopperType) && !hopperTypeKnown && (regType = WirelessRegistry.getTypeForPos(pos)) != null) {
                resolvedHopperType = regType;
            }
            if (hp != null && "Normal".equals(hp.getHopperType()) && !"Normal".equals(resolvedHopperType)) {
                hp.setHopperType(resolvedHopperType);
            }
            isWireless = "WirelessExport".equals(resolvedHopperType) || "WirelessImport".equals(resolvedHopperType);
            if ("Links".equals(activeTab) && !isWireless) {
                activeTab = "Status";
                ACTIVE_TAB.put(playerRef, "Status");
            }
            boolean slotEmpty = true;
            try {
                ItemContainer _ic2 = hp != null ? hp.getItemContainer() : null;
                slotEmpty = _ic2 == null || _ic2.getItemStack((short)0) == null;
            } catch (Throwable ignored) {}
            // Default wireless hoppers to Links tab on first open, but only when the slot is empty
            // so a hopper holding an item lands on Status where the collect button lives.
            if (isWireless && "Status".equals(activeTab) && !ACTIVE_TAB.containsKey(playerRef) && slotEmpty) {
                activeTab = "Links";
                ACTIVE_TAB.put(playerRef, "Links");
            }
            mode = "Off";
            wlText = "(empty)";
            blText = "(empty)";
            hopperSlot = "(empty)";
            if (hp != null) {
                mode = hp.getFilterMode();
                List<String> wl = hp.getWhitelist();
                List<String> bl2 = hp.getBlacklist();
                wlText = wl.isEmpty() ? "(empty)" : String.join((CharSequence)", ", wl);
                blText = bl2.isEmpty() ? "(empty)" : String.join((CharSequence)", ", bl2);
                try {
                    ItemContainer _ic = hp.getItemContainer();
                    if (_ic == null) throw new RuntimeException("skipSlot");
                    ItemStack stack = _ic.getItemStack((short)0);
                    if (stack == null) throw new RuntimeException("skipSlot");
                    String key = hp.resolveItemStackKey(stack);
                    int qty = 0;
                    try {
                        qty = stack.getQuantity();
                    }
                    catch (RuntimeException | Error throwable) {
                        // empty catch block
                    }
                    hopperSlot = (key != null ? key : "(unknown)") + " x" + qty; // hopperSlot is String
                }
                catch (RuntimeException | Error stack) {
                    // empty catch block
                }
            }
        } catch (Throwable t) {
        }
        try {
        String heldDisplay = heldItemId != null && !heldItemId.isBlank() ? heldItemId : null;
        boolean showArcio = HopperProcessor.ARCIO_PRESENT;
        String arcioMode = hp != null && hp.getArcioMode() != null ? hp.getArcioMode() : "IgnoreSignal";
        String wirelessName = hp != null && hp.getWirelessName() != null ? hp.getWirelessName() : "";
        Vector3i wirelessTarget = hp != null ? hp.getWirelessTarget() : null;
        String linkTargetText = wirelessTarget != null ? wirelessTarget.x + ", " + wirelessTarget.y + ", " + wirelessTarget.z : "(none)";
        String resolvedWirelessOwner = HopperComponent.wirelessOwnerKey(playerRef);
        if (resolvedWirelessOwner == null || resolvedWirelessOwner.isBlank()) {
            try {
                resolvedWirelessOwner = playerRef != null ? playerRef.toString() : "";
            }
            catch (Throwable ignored2) {
                resolvedWirelessOwner = "";
            }
        }
        String playerName = resolvedWirelessOwner;
        String channelOwner = wirelessName.isBlank() ? null : WirelessRegistry.getOwner(wirelessName);
        String channelPasscode = wirelessName.isBlank() ? null : WirelessRegistry.getPasscode(wirelessName);
        boolean isChannelOwner = channelOwner == null || channelOwner.equals(playerName);
        ArrayList<WirelessRegistry.LinkItem> linkCandidates = new ArrayList<>();
        if (isWireless) {
            String wantType = "WirelessExport".equals(resolvedHopperType) ? "Import" : "Export";
            for (WirelessRegistry.LinkItem item : WirelessRegistry.getAllLinkItems()) {
                if (!item.type.equals(wantType) || item.pos.equals(pos)) continue;
                linkCandidates.add(item);
            }
        }
        String facadeBlockId = hp != null ? hp.getFacadeBlockId() : "";
        int facadeConnectionMask = hp != null ? hp.getFacadeConnectionMask() : 0;
        int facadeRotation = hp != null ? hp.getFacadeRotation() : 0;
        int facadeRotationX = hp != null ? hp.getFacadeRotationX() : 0;
        int facadeRotationZ = hp != null ? hp.getFacadeRotationZ() : 0;
        ItemContainer _hpIc = hp != null ? hp.getItemContainer() : null;
        boolean hasItem = _hpIc != null && _hpIc.getItemStack((short)0) != null;
        final int LINKS_PAGE_SIZE = 5;
        int linksPageNum = LINKS_PAGE.getOrDefault(playerRef, 0);
        int totalLinksPages = linkCandidates.isEmpty() ? 1 : (linkCandidates.size() + LINKS_PAGE_SIZE - 1) / LINKS_PAGE_SIZE;
        linksPageNum = Math.max(0, Math.min(linksPageNum, totalLinksPages - 1));
        int linksStart = linksPageNum * LINKS_PAGE_SIZE;
        List<WirelessRegistry.LinkItem> pagedLinkCandidates = new ArrayList<>(linkCandidates.subList(linksStart, Math.min(linksStart + LINKS_PAGE_SIZE, linkCandidates.size())));
        String html = HopperUIPage.buildHtml(mode, hopperSlot, wlText, blText, heldDisplay, showArcio, arcioMode, activeTab, wirelessName, linkTargetText, pagedLinkCandidates, facadeBlockId, facadeConnectionMask, facadeRotation, facadeRotationX, facadeRotationZ, channelOwner, channelPasscode, isChannelOwner, hasItem, linksPageNum, totalLinksPages);
        PageBuilder builder = ((PageBuilder)PageBuilder.pageForPlayer(playerRef).fromHtml(html)).withLifetime(CustomPageLifetime.CanDismissOrCloseThroughInteraction);
        String currentTab = activeTab;
        boolean isWirelessFinal = isWireless;
        builder.addEventListener("prevTab", CustomUIEventBindingType.Activating, (ign, ctx) -> {
            String next;
            int idx = Arrays.asList(TAB_ORDER).indexOf(currentTab);
            while ("Links".equals(next = TAB_ORDER[idx = (idx - 1 + TAB_ORDER.length) % TAB_ORDER.length]) && !isWirelessFinal) {
            }
            ACTIVE_TAB.put(playerRef, next);
            HopperUIPage.open(playerRef, store, pos, heldItemId);
        });
        builder.addEventListener("nextTab", CustomUIEventBindingType.Activating, (ign, ctx) -> {
            String next;
            int idx = Arrays.asList(TAB_ORDER).indexOf(currentTab);
            while ("Links".equals(next = TAB_ORDER[idx = (idx + 1) % TAB_ORDER.length]) && !isWirelessFinal) {
            }
            ACTIVE_TAB.put(playerRef, next);
            HopperUIPage.open(playerRef, store, pos, heldItemId);
        });
        if ("Filter".equals(activeTab)) {
            if (heldDisplay != null) {
                String heldIdWl = heldDisplay;
                builder.addEventListener("addHeldWl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                    Store<EntityStore> s = liveStore(playerRef, store);
                    HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                    if (hopper == null) return;
                    for (String e : hopper.getWhitelist()) {
                        if (e == null || !e.equalsIgnoreCase(heldIdWl)) continue;
                        return;
                    }
                    hopper.addToWhitelist(heldIdWl);
                    HopperUIPage.refreshLabels(ctx, s, pos);
                });
                String heldIdBl = heldDisplay;
                builder.addEventListener("addHeldBl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                    Store<EntityStore> s = liveStore(playerRef, store);
                    HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                    if (hopper == null) return;
                    for (String e : hopper.getBlacklist()) {
                        if (e == null || !e.equalsIgnoreCase(heldIdBl)) continue;
                        return;
                    }
                    hopper.addToBlacklist(heldIdBl);
                    HopperUIPage.refreshLabels(ctx, s, pos);
                });
            }
            builder.addEventListener("addWl", CustomUIEventBindingType.Activating, (ignored, ctx) -> ctx.getValue("itemInput", String.class).ifPresent(text -> {
                if (text.isBlank()) return;
                Store<EntityStore> s = liveStore(playerRef, store);
                HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                if (hopper == null) return;
                String id = text.trim();
                for (String e : hopper.getWhitelist()) {
                    if (e == null || !e.equalsIgnoreCase(id)) continue;
                    return;
                }
                hopper.addToWhitelist(id);
                HopperUIPage.refreshLabels(ctx, s, pos);
            }));
            builder.addEventListener("addBl", CustomUIEventBindingType.Activating, (ignored, ctx) -> ctx.getValue("itemInput", String.class).ifPresent(text -> {
                if (text.isBlank()) return;
                Store<EntityStore> s = liveStore(playerRef, store);
                HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                if (hopper == null) return;
                String id = text.trim();
                for (String e : hopper.getBlacklist()) {
                    if (e == null || !e.equalsIgnoreCase(id)) continue;
                    return;
                }
                hopper.addToBlacklist(id);
                HopperUIPage.refreshLabels(ctx, s, pos);
            }));
            builder.addEventListener("removeWl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                Store<EntityStore> s = liveStore(playerRef, store);
                HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                if (hopper == null) return;
                hopper.removeLastFromWhitelist();
                HopperUIPage.refreshLabels(ctx, s, pos);
            });
            builder.addEventListener("removeBl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                Store<EntityStore> s = liveStore(playerRef, store);
                HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                if (hopper == null) return;
                hopper.removeLastFromBlacklist();
                HopperUIPage.refreshLabels(ctx, s, pos);
            });
            builder.addEventListener("clearWl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                Store<EntityStore> s = liveStore(playerRef, store);
                HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                if (hopper == null) return;
                hopper.clearWhitelist();
                HopperUIPage.refreshLabels(ctx, s, pos);
            });
            builder.addEventListener("clearBl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                Store<EntityStore> s = liveStore(playerRef, store);
                HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                if (hopper == null) return;
                hopper.clearBlacklist();
                HopperUIPage.refreshLabels(ctx, s, pos);
            });
            builder.addEventListener("modeOff", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                Store<EntityStore> s = liveStore(playerRef, store);
                HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                if (hopper == null) return;
                hopper.setFilterMode("Off");
                HopperUIPage.refreshLabels(ctx, s, pos);
            });
            builder.addEventListener("modeWl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                Store<EntityStore> s = liveStore(playerRef, store);
                HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                if (hopper == null) return;
                hopper.setFilterMode("Whitelist");
                HopperUIPage.refreshLabels(ctx, s, pos);
            });
            builder.addEventListener("modeBl", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                Store<EntityStore> s = liveStore(playerRef, store);
                HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                if (hopper == null) return;
                hopper.setFilterMode("Blacklist");
                HopperUIPage.refreshLabels(ctx, s, pos);
            });
            builder.addEventListener("modeSingleton", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                Store<EntityStore> s = liveStore(playerRef, store);
                HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                if (hopper == null) return;
                hopper.setFilterMode("Singleton");
                HopperUIPage.refreshLabels(ctx, s, pos);
            });
        }
        if ("Facade".equals(activeTab)) {
            if (heldDisplay != null && EngineCompat.isValidBlockKey(heldDisplay)) {
                String heldIdFacade = heldDisplay;
                builder.addEventListener("setFacadeHeld", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                    Store<EntityStore> s = liveStore(playerRef, store);
                    HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                    if (hopper == null) return;
                    String normalized = FacadeHelper.normalizeBlockId(heldIdFacade);
                    hopper.setFacadeBlockId(normalized);
                    World _w = ((EntityStore)s.getExternalData()).getWorld();
                    hopper.setFacadeConnectionMask(FacadeHelper.computeConnectionMask(_w, pos, normalized));
                    hopper.applyFacadeVisual(pos, s);
                    HopperUIPage.open(playerRef, s, pos, heldItemId);
                });
            }
            builder.addEventListener("clearFacade", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                Store<EntityStore> s = liveStore(playerRef, store);
                HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                if (hopper == null) return;
                hopper.clearFacadeVisual(pos, s);
                hopper.setFacadeBlockId("");
                hopper.setFacadeConnectionMask(0);
                HopperUIPage.open(playerRef, s, pos, heldItemId);
            });
            if (!facadeBlockId.isEmpty()) {
                builder.addEventListener("facadeRotLeft", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                    Store<EntityStore> s = liveStore(playerRef, store);
                    HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                    if (hopper == null || !hopper.hasFacade()) return;
                    hopper.setFacadeRotation(hopper.getFacadeRotation() - 1);
                    hopper.applyFacadeVisual(pos, s);
                    HopperUIPage.refreshFacadeLabels(ctx, hopper);
                });
                builder.addEventListener("facadeRotRight", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                    Store<EntityStore> s = liveStore(playerRef, store);
                    HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                    if (hopper == null || !hopper.hasFacade()) return;
                    hopper.setFacadeRotation(hopper.getFacadeRotation() + 1);
                    hopper.applyFacadeVisual(pos, s);
                    HopperUIPage.refreshFacadeLabels(ctx, hopper);
                });
                builder.addEventListener("facadeRotXLeft", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                    Store<EntityStore> s = liveStore(playerRef, store);
                    HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                    if (hopper == null || !hopper.hasFacade()) return;
                    hopper.setFacadeRotationX(hopper.getFacadeRotationX() - 1);
                    hopper.applyFacadeVisual(pos, s);
                    HopperUIPage.refreshFacadeLabels(ctx, hopper);
                });
                builder.addEventListener("facadeRotXRight", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                    Store<EntityStore> s = liveStore(playerRef, store);
                    HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                    if (hopper == null || !hopper.hasFacade()) return;
                    hopper.setFacadeRotationX(hopper.getFacadeRotationX() + 1);
                    hopper.applyFacadeVisual(pos, s);
                    HopperUIPage.refreshFacadeLabels(ctx, hopper);
                });
                builder.addEventListener("facadeRotZLeft", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                    Store<EntityStore> s = liveStore(playerRef, store);
                    HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                    if (hopper == null || !hopper.hasFacade()) return;
                    hopper.setFacadeRotationZ(hopper.getFacadeRotationZ() - 1);
                    hopper.applyFacadeVisual(pos, s);
                    HopperUIPage.refreshFacadeLabels(ctx, hopper);
                });
                builder.addEventListener("facadeRotZRight", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                    Store<EntityStore> s = liveStore(playerRef, store);
                    HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                    if (hopper == null || !hopper.hasFacade()) return;
                    hopper.setFacadeRotationZ(hopper.getFacadeRotationZ() + 1);
                    hopper.applyFacadeVisual(pos, s);
                    HopperUIPage.refreshFacadeLabels(ctx, hopper);
                });
            }
        }
        if ("Links".equals(activeTab)) {
            final int finalLinksPage = linksPageNum;
            final int finalTotalLinksPages = totalLinksPages;
            int i = 0;
            while (i < pagedLinkCandidates.size()) {
                WirelessRegistry.LinkItem candidate = (WirelessRegistry.LinkItem)pagedLinkCandidates.get(i);
                int idx = i++;
                builder.addEventListener("linkWith_" + idx, CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                    Store<EntityStore> s = liveStore(playerRef, store);
                    String targetChannel = candidate.name;
                    String enteredPasscode = ctx.getValue("passcodeInput", String.class).orElse(null);
                    if (!WirelessRegistry.isAccessibleBy(targetChannel, playerName, enteredPasscode)) {
                        HopperUIPage.open(playerRef, s, pos, heldItemId);
                        return;
                    }
                    World world = ((EntityStore)s.getExternalData()).getWorld();
                    WirelessRegistry.linkTo(world, pos, candidate.pos);
                    LINKS_PAGE.remove(playerRef);
                    HopperUIPage.open(playerRef, s, pos, heldItemId);
                });
            }
            if (finalLinksPage > 0) {
                builder.addEventListener("prevLinksPage", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                    LINKS_PAGE.put(playerRef, finalLinksPage - 1);
                    HopperUIPage.open(playerRef, store, pos, heldItemId);
                });
            }
            if (finalLinksPage < finalTotalLinksPages - 1) {
                builder.addEventListener("nextLinksPage", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                    LINKS_PAGE.put(playerRef, finalLinksPage + 1);
                    HopperUIPage.open(playerRef, store, pos, heldItemId);
                });
            }
            if (isChannelOwner && !wirelessName.isBlank()) {
                builder.addEventListener("setPasscode", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                    Store<EntityStore> s = liveStore(playerRef, store);
                    HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                    if (hopper == null) return;
                    String channel = hopper.getWirelessName();
                    if (channel == null || channel.isBlank()) return;
                    String newPc = ctx.getValue("passcodeInput", String.class).orElse("").trim();
                    WirelessRegistry.setPasscode(channel, playerName, newPc.isBlank() ? null : newPc);
                    HopperUIPage.open(playerRef, s, pos, heldItemId);
                });
            }
            builder.addEventListener("setWirelessName", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                Store<EntityStore> s = liveStore(playerRef, store);
                HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                if (hopper == null) return;
                ctx.getValue("wirelessNameInput", String.class).ifPresent(name -> {
                    String trimmed = name.trim();
                    hopper.setWirelessName(trimmed);
                    World world = ((EntityStore)s.getExternalData()).getWorld();
                    if (!trimmed.isBlank()) {
                        String regType = hopper.data != null && hopper.data.hopperType != null && !"Normal".equals(hopper.data.hopperType) ? hopper.data.hopperType : hopper.getHopperType();
                        WirelessRegistry.register(world, pos, trimmed, regType, playerName);
                    }
                });
                HopperUIPage.open(playerRef, s, pos, heldItemId);
            });
            builder.addEventListener("linkUnlink", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                Store<EntityStore> s = liveStore(playerRef, store);
                World world = ((EntityStore)s.getExternalData()).getWorld();
                HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                if (hopper != null && hopper.hasWirelessTarget()) {
                    Vector3i oldTarget = hopper.getWirelessTarget();
                    WirelessHelpers.clearWirelessTarget(world, pos);
                    WirelessHelpers.clearWirelessTarget(world, oldTarget);
                } else {
                    WirelessHelpers.clearWirelessTarget(world, pos);
                }
                try {
                    WirelessRegistry.pruneForWorld(world);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                HopperUIPage.open(playerRef, s, pos, heldItemId);
            });
        }
        if (hasItem) {
            builder.addEventListener("collectItem", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                // De-dupe: one Collect click can dispatch to several stacked-page listeners.
                // Only the first within the guard window is allowed to pay out.
                String guardKey = String.valueOf(playerRef) + "@" + pos.x + "," + pos.y + "," + pos.z;
                long nowMs = System.currentTimeMillis();
                Long lastMs = COLLECT_GUARD.get(guardKey);
                if (lastMs != null && nowMs - lastMs < COLLECT_GUARD_MS) return;
                COLLECT_GUARD.put(guardKey, nowMs);
                Store<EntityStore> s = liveStore(playerRef, store);
                HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                if (hopper == null) return;
                ItemContainer ic = hopper.getItemContainer();
                if (ic == null) return;
                ItemStack stack = ic.getItemStack((short)0);
                if (stack == null) return;
                Ref<EntityStore> playerEntityRef = PLAYER_ENTITY_REFS.get(playerRef);
                if (playerEntityRef == null || !playerEntityRef.isValid()) return;
                try {
                    int beforeQty = stack.getQuantity();
                    if (beforeQty <= 0) return;
                    // Clear the hopper slot BEFORE handing items to the player, then give
                    // ONLY what was actually removed from the slot. If a concurrent/stacked
                    // listener already emptied it, the delta is 0 and nothing is paid out,
                    // so a single click can never dupe regardless of how many times it fires.
                    ic.removeItemStackFromSlot((short)0, beforeQty);
                    ItemStack afterStack = ic.getItemStack((short)0);
                    int afterQty = afterStack == null ? 0 : afterStack.getQuantity();
                    int removed = beforeQty - afterQty;
                    if (removed <= 0) return;
                    ItemStack toGive = java.util.Objects.requireNonNullElse(stack.withQuantity(removed), stack);
                    // When a pickup-XP mod (e.g. MMOSkillTree) is installed, withdraw the
                    // item silently so it isn't miscredited as a gather/pickup. Otherwise
                    // use the normal interactive pickup so other listeners still fire.
                    if (ItemUtilsExtended.PICKUP_XP_MOD_PRESENT) {
                        ItemUtilsExtended.giveItemSilently(playerEntityRef, toGive, null, s);
                    } else {
                        ItemUtilsExtended.interactivelyPickupItem(playerEntityRef, toGive, null, s);
                    }
                }
                catch (Throwable t) {
                    Ev0Log.warn(LOGGER, "collectItem failed: " + t.getMessage());
                }
                HopperUIPage.open(playerRef, s, pos, heldItemId);
            });
        }
        if ("Signals".equals(activeTab) && HopperComponent.ARCIO_PRESENT) {
            builder.addEventListener("arcioIgnoreSignal", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                Store<EntityStore> s = liveStore(playerRef, store);
                HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                if (hopper == null) return;
                hopper.setArcioMode("IgnoreSignal");
                HopperUIPage.refreshLabels(ctx, s, pos);
            });
            builder.addEventListener("arcioEnableSignal", CustomUIEventBindingType.Activating, (ignored, ctx) -> {
                Store<EntityStore> s = liveStore(playerRef, store);
                HopperComponent hopper = HopperUIPage.lookupHopper(s, pos);
                if (hopper == null) return;
                hopper.setArcioMode("EnableWhenSignal");
                HopperUIPage.refreshLabels(ctx, s, pos);
            });
        }
        builder.open(store);
        Ev0Log.info(LOGGER, "HopperUI: opened page for pos=" + String.valueOf(pos) + " player=" + String.valueOf(playerRef));
        Ev0Log.warn(LOGGER, "[Ev0Lib][DIAG] HopperUI opened for player=" + String.valueOf(playerRef) + " pos=" + String.valueOf(pos) + " hopperPresent=" + (hp != null));
        if (hp == null) {
            Ev0Log.warn(LOGGER, "HopperUI: no hopper found at pos=" + String.valueOf(pos) + " when opening UI");
            Ev0Log.warn(LOGGER, "[Ev0Lib][DIAG] HopperUI lookup returned null for pos=" + String.valueOf(pos));
        }
        } catch (Throwable t) { }
    }

    private static String buildHtml(String mode, String hopperSlot, String wlText, String blText, String heldItem, boolean showArcio, String arcioMode, String activeTab, String wirelessName, String linkTargetText, List<WirelessRegistry.LinkItem> linkCandidates, String facadeBlockId, int facadeConnectionMask, int facadeRotation, int facadeRotationX, int facadeRotationZ, String channelOwner, String channelPasscode, boolean isChannelOwner, boolean hasItem, int linksPage, int totalLinksPages) {
        String tabNav = "            <div class=\"tab-nav\">\n                <button id=\"prevTab\" class=\"tab-arrow\">&lt;</button>\n                <p class=\"tab-label\">%s</p>\n                <button id=\"nextTab\" class=\"tab-arrow\">&gt;</button>\n            </div>\n            <div class=\"separator\"></div>\n".formatted(HopperUIPage.escapeHtml(activeTab.toUpperCase()));
        String content = switch (activeTab) {
            case "Filter" -> HopperUIPage.buildFilterTab(wlText, blText, heldItem);
            case "Facade" -> HopperUIPage.buildFacadeTab(facadeBlockId, facadeConnectionMask, facadeRotation, facadeRotationX, facadeRotationZ, EngineCompat.isValidBlockKey(heldItem) ? heldItem : null);
            case "Signals" -> HopperUIPage.buildSignalsTab(showArcio, arcioMode);
            case "Links" -> HopperUIPage.buildLinksTab(wirelessName, linkTargetText, linkCandidates, channelOwner, channelPasscode, isChannelOwner, linksPage, totalLinksPages);
            default -> HopperUIPage.buildStatusTab(mode, hopperSlot, hasItem);
        };
        String styles = "<style>\n    .section-title {\n        font-weight: bold;\n        color: #bdcbd3;\n        font-size: 16;\n        padding-top: 12;\n        padding-bottom: 4;\n    }\n    .info-label {\n        padding-top: 4;\n        padding-bottom: 4;\n        color: #a0b8c8;\n        font-size: 14;\n    }\n    .separator {\n        layout-mode: Full;\n        anchor-height: 2;\n        background-color: #ffffff(0.15);\n        margin-top: 8;\n        margin-bottom: 8;\n    }\n    .btn-row {\n        layout-mode: Left;\n        padding-top: 6;\n        padding-bottom: 6;\n        spacing: 8;\n    }\n    .input-field {\n        padding-top: 8;\n        padding-bottom: 8;\n    }\n    .tab-nav {\n        layout-mode: Center;\n        padding-top: 6;\n        padding-bottom: 2;\n        spacing: 16;\n    }\n    .tab-label {\n        font-weight: bold;\n        font-size: 18;\n        color: #ffffff;\n        min-width: 120;\n        text-align: center;\n    }\n    .tab-arrow {\n        anchor-width: 36;\n        anchor-height: 36;\n        font-size: 18;\n        padding: 4 10;\n    }\n</style>\n";
        return styles + "<div class=\"page-overlay\">\n    <div class=\"decorated-container\" data-hyui-title=\"Hopper Filter\" style=\"anchor-width: 640; anchor-height: 840;\">\n        <div class=\"container-contents\" style=\"layout-mode: Top; padding: 16 28;\">\n" + tabNav + content + "        </div>\n    </div>\n</div>\n";
    }

    private static String buildStatusTab(String mode, String hopperSlot, boolean hasItem) {
        String collectSection = hasItem ? "            <div class=\"separator\"></div>\n            <p class=\"section-title\">Item</p>\n            <p id=\"hopperItemLabel\" class=\"info-label\">Slot: %s</p>\n            <div class=\"btn-row\">\n                <button id=\"collectItem\" class=\"primary-button\" style=\"padding: 8 20;\">Collect</button>\n            </div>\n".formatted(HopperUIPage.escapeHtml(hopperSlot)) : "            <div class=\"separator\"></div>\n            <p class=\"info-label\">Slot: (empty)</p>\n";
        return "            <p class=\"section-title\">Status</p>\n            <p id=\"modeLabel\" class=\"info-label\">Mode: %s</p>\n".formatted(HopperUIPage.escapeHtml(mode)) + collectSection;
    }

    private static String buildFilterTab(String wlText, String blText, String heldItem) {
        String heldSection = "";
        if (heldItem != null) {
            heldSection = "        <div class=\"separator\"></div>\n        <p class=\"section-title\">Held Item</p>\n        <p id=\"heldLabel\" class=\"info-label\">In Hand: %s</p>\n        <div class=\"btn-row\">\n            <button id=\"addHeldWl\" class=\"small-secondary-button\" style=\"padding: 4 12;\">+ Whitelist</button>\n            <button id=\"addHeldBl\" class=\"small-secondary-button\" style=\"padding: 4 12;\">+ Blacklist</button>\n        </div>\n".formatted(HopperUIPage.escapeHtml(heldItem));
        }
        return "            <p class=\"section-title\">Filter Lists</p>\n            <p id=\"wlLabel\" class=\"info-label\">Whitelist: %s</p>\n            <p id=\"blLabel\" class=\"info-label\">Blacklist: %s</p>\n            %s\n            <div class=\"separator\"></div>\n            <p class=\"section-title\">Item Entry</p>\n            <div class=\"input-field\">\n                <input type=\"text\" id=\"itemInput\" value=\"\" placeholder=\"Item ID (e.g. Wood_Ash_Trunk)\" style=\"width: 100%%; padding: 8 12; font-size: 14;\" />\n            </div>\n            <div class=\"btn-row\">\n                <button id=\"addWl\" class=\"secondary-button\" style=\"padding: 6 16;\">+ Whitelist</button>\n                <button id=\"addBl\" class=\"secondary-button\" style=\"padding: 6 16;\">+ Blacklist</button>\n            </div>\n            <div class=\"btn-row\">\n                <button id=\"removeWl\" class=\"tertiary-button\" style=\"padding: 4 12;\">- Remove Last</button>\n                <button id=\"removeBl\" class=\"tertiary-button\" style=\"padding: 4 12;\">- Remove Last</button>\n            </div>\n            <div class=\"btn-row\">\n                <button id=\"clearWl\" class=\"tertiary-button\" style=\"padding: 4 12;\">Clear Whitelist</button>\n                <button id=\"clearBl\" class=\"tertiary-button\" style=\"padding: 4 12;\">Clear Blacklist</button>\n            </div>\n            <div class=\"separator\"></div>\n            <p class=\"section-title\">Filter Mode</p>\n            <div class=\"btn-row\">\n                <button id=\"modeOff\" class=\"primary-button\" style=\"padding: 8 20;\">Off</button>\n                <button id=\"modeWl\" class=\"primary-button\" style=\"padding: 8 20;\">Whitelist</button>\n                <button id=\"modeBl\" class=\"primary-button\" style=\"padding: 8 20;\">Blacklist</button>\n                <button id=\"modeSingleton\" class=\"primary-button\" style=\"padding: 8 20;\">Singleton</button>\n            </div>\n".formatted(HopperUIPage.escapeHtml(wlText), HopperUIPage.escapeHtml(blText), heldSection);
    }

    private static String buildLinksTab(String wirelessName, String linkTargetText, List<WirelessRegistry.LinkItem> candidates, String channelOwner, String channelPasscode, boolean isChannelOwner, int currentPage, int totalPages) {
        if (wirelessName == null) wirelessName = "";
        if (linkTargetText == null) linkTargetText = "(none)";
        StringBuilder sb = new StringBuilder();
        String safeWirelessName = HopperUIPage.escapeHtml(wirelessName.isBlank() ? "(none)" : wirelessName);
        sb.append("<p class=\"section-title\">Wireless Link</p>\n<p class=\"info-label\">Name:").append(safeWirelessName).append("</p>\n");
        sb.append("                            <div class=\"input-field\">\n");
        sb.append("                                <input type=\"text\" id=\"wirelessNameInput\" value=\"").append(HopperUIPage.escapeHtml(wirelessName)).append("\" placeholder=\"Channel name (e.g. Farm1)\" style=\"width: 100%; padding: 8 12; font-size: 14;\" />\n");
        sb.append("                            </div>\n");
        sb.append("                            <div class=\"btn-row\">\n");
        sb.append("                                <button id=\"setWirelessName\" class=\"primary-button\" style=\"padding: 6 16;\">Set Name</button>\n");
        sb.append("                            </div>\n");
        sb.append("                            <div class=\"separator\"></div>\n");
        if (!wirelessName.isBlank()) {
            String ownerLabel = channelOwner != null ? channelOwner : "(unowned \u2014 anyone can link)";
            sb.append("                            <p class=\"info-label\">Owner: ").append(HopperUIPage.escapeHtml(ownerLabel)).append("</p>\n");
            if (isChannelOwner) {
                String pcDisplay = channelPasscode != null && !channelPasscode.isBlank() ? channelPasscode : "";
                sb.append("                            <p class=\"section-title\">Passcode</p>\n");
                sb.append("                            <p class=\"info-label\">Set a passcode to allow others to link to your channel. Leave blank for private (owner only).</p>\n");
                sb.append("                            <div class=\"input-field\">\n");
                sb.append("                                <input type=\"text\" id=\"passcodeInput\" value=\"").append(HopperUIPage.escapeHtml(pcDisplay)).append("\" placeholder=\"Passcode (leave blank = private)\" style=\"width: 100%; padding: 8 12; font-size: 14;\" />\n");
                sb.append("                            </div>\n");
                sb.append("                            <div class=\"btn-row\">\n");
                sb.append("                                <button id=\"setPasscode\" class=\"secondary-button\" style=\"padding: 6 16;\">Set Passcode</button>\n");
                sb.append("                            </div>\n");
            } else {
                boolean hasPasscode = channelPasscode != null && !channelPasscode.isBlank();
                sb.append("                            <p class=\"section-title\">Link Access</p>\n");
                if (hasPasscode) {
                    sb.append("                            <p class=\"info-label\">This channel requires a passcode to link.</p>\n");
                    sb.append("                            <div class=\"input-field\">\n");
                    sb.append("                                <input type=\"text\" id=\"passcodeInput\" value=\"\" placeholder=\"Enter passcode\" style=\"width: 100%; padding: 8 12; font-size: 14;\" />\n");
                    sb.append("                            </div>\n");
                } else {
                    sb.append("                            <p class=\"info-label\">This channel is private. Only the owner can link to it.</p>\n");
                }
            }
        }
        sb.append("                            <p class=\"info-label\">Linked To: ").append(HopperUIPage.escapeHtml(linkTargetText)).append("</p>\n");
        sb.append("                            <div class=\"separator\"></div>\n");
        sb.append("                            <p class=\"section-title\">Available Wireless Hoppers</p>\n");
        if (candidates.isEmpty()) {
            sb.append("                            <p class=\"info-label\">(No compatible wireless hoppers registered)</p>\n");
        } else {
            for (int i = 0; i < candidates.size(); ++i) {
                WirelessRegistry.LinkItem item = candidates.get(i);
                String label = (item.name != null && !item.name.isBlank() ? item.name : "Unnamed") + " [" + item.type + "] @ " + item.pos.x + ", " + item.pos.y + ", " + item.pos.z;
                sb.append("                            <div class=\"btn-row\">\n");
                sb.append("                                <p class=\"info-label\" style=\"flex-grow: 1;\">").append(HopperUIPage.escapeHtml(label)).append("</p>\n");
                sb.append("                                <button id=\"linkWith_").append(i).append("\" class=\"primary-button\" style=\"padding: 6 16;\">Link</button>\n");
                sb.append("                            </div>\n");
            }
        }
        if (totalPages > 1) {
            sb.append("                            <div class=\"btn-row\">\n");
            if (currentPage > 0) {
                sb.append("                                <button id=\"prevLinksPage\" class=\"secondary-button\" style=\"padding: 6 14;\">&lt; Prev</button>\n");
            }
            sb.append("                                <p class=\"info-label\" style=\"flex-grow: 1; text-align: center;\">Page ").append(currentPage + 1).append(" / ").append(totalPages).append("</p>\n");
            if (currentPage < totalPages - 1) {
                sb.append("                                <button id=\"nextLinksPage\" class=\"secondary-button\" style=\"padding: 6 14;\">Next &gt;</button>\n");
            }
            sb.append("                            </div>\n");
        }
        sb.append("            <div class=\"separator\"></div>\n            <div class=\"btn-row\">\n                <button id=\"linkUnlink\" class=\"tertiary-button\" style=\"padding: 8 20;\">Unlink</button>\n            </div>\n");
        return sb.toString();
    }

    private static String buildFacadeTab(String facadeBlockId, int facadeConnectionMask, int facadeRotation, int facadeRotationX, int facadeRotationZ, String heldItem) {
        boolean hasFacade = facadeBlockId != null && !facadeBlockId.isEmpty();
        String slotDisplay = hasFacade ? HopperUIPage.escapeHtml(facadeBlockId) : "(none)";
        String connDisplay = hasFacade ? "Connects: " + HopperUIPage.escapeHtml(FacadeHelper.maskToString(facadeConnectionMask)) : "";
        String rotationSection = "";
        if (hasFacade) {
            int rotatedMask = FacadeHelper.rotateMask(facadeConnectionMask, facadeRotation);
            String variantDisplay = HopperUIPage.escapeHtml(FacadeHelper.buildVariantKey(facadeBlockId, rotatedMask));
            rotationSection = "        <div class=\"separator\"></div>\n        <p class=\"section-title\">Rotation</p>\n        <p class=\"info-label\">Y (Yaw)</p>\n        <div class=\"btn-row\">\n            <button id=\"facadeRotLeft\"  class=\"secondary-button\" style=\"padding: 6 14;\">&lt; CCW</button>\n            <p id=\"facadeRotYLabel\" class=\"info-label\">%s deg</p>\n            <button id=\"facadeRotRight\" class=\"secondary-button\" style=\"padding: 6 14;\">CW &gt;</button>\n        </div>\n        <p class=\"info-label\">X (Pitch)</p>\n        <div class=\"btn-row\">\n            <button id=\"facadeRotXLeft\"  class=\"secondary-button\" style=\"padding: 6 14;\">&lt;</button>\n            <p id=\"facadeRotXLabel\" class=\"info-label\">%s deg</p>\n            <button id=\"facadeRotXRight\" class=\"secondary-button\" style=\"padding: 6 14;\">&gt;</button>\n        </div>\n        <p class=\"info-label\">Z (Roll)</p>\n        <div class=\"btn-row\">\n            <button id=\"facadeRotZLeft\"  class=\"secondary-button\" style=\"padding: 6 14;\">&lt;</button>\n            <p id=\"facadeRotZLabel\" class=\"info-label\">%s deg</p>\n            <button id=\"facadeRotZRight\" class=\"secondary-button\" style=\"padding: 6 14;\">&gt;</button>\n        </div>\n        <p id=\"facadeVariantLabel\" class=\"info-label\">Variant: %s</p>\n".formatted(facadeRotation * 90, facadeRotationX * 90, facadeRotationZ * 90, variantDisplay);
        }
        String heldSection = "";
        if (heldItem != null) {
            heldSection = "        <div class=\"separator\"></div>\n        <p class=\"section-title\">Held Item</p>\n        <p id=\"facadeHeldLabel\" class=\"info-label\">In Hand: %s</p>\n        <div class=\"btn-row\">\n            <button id=\"setFacadeHeld\" class=\"primary-button\" style=\"padding: 8 20;\">Set as Facade</button>\n        </div>\n".formatted(HopperUIPage.escapeHtml(heldItem));
        }
        return "            <p class=\"section-title\">Block Facade</p>\n            <p class=\"info-label\">The hopper will visually appear as the configured block.</p>\n            <div class=\"separator\"></div>\n            <p class=\"section-title\">Current Facade</p>\n            <p id=\"facadeSlotLabel\" class=\"info-label\">Block: %s</p>\n            <p id=\"facadeConnLabel\" class=\"info-label\">%s</p>\n            <div class=\"btn-row\">\n                <button id=\"clearFacade\" class=\"tertiary-button\" style=\"padding: 6 16;\">Clear Facade</button>\n            </div>\n            %s\n            %s\n".formatted(slotDisplay, connDisplay, rotationSection, heldSection);
    }

    private static String buildSignalsTab(boolean showArcio, String arcioMode) {
        if (!showArcio) {
            return "        <p class=\"section-title\">ArcIO Signals</p>\n        <p class=\"info-label\">ArcIO is not installed on this server.</p>\n";
        }
        String modeDisplay = "EnableWhenSignal".equals(arcioMode) ? "Enable When Signal" : "Ignore Signal";
        return "            <p class=\"section-title\">ArcIO Signal Control</p>\n            <p id=\"arcioModeLabel\" class=\"info-label\">ArcIO Mode: %s</p>\n            <div class=\"btn-row\">\n                <button id=\"arcioIgnoreSignal\" class=\"primary-button\" style=\"padding: 8 20;\">Ignore Signal</button>\n                <button id=\"arcioEnableSignal\" class=\"secondary-button\" style=\"padding: 8 20;\">Enable When Signal</button>\n            </div>\n".formatted(HopperUIPage.escapeHtml(modeDisplay));
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
            ItemContainer _ic = hp.getItemContainer();
            if (_ic == null) throw new RuntimeException("noContainer");
            ItemStack stack = _ic.getItemStack((short)0);
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
            ctx.getById("hopperItemLabel", LabelBuilder.class).ifPresent(arg_0 -> HopperUIPage.lambda$refreshLabels$3((String)slotText, arg_0));
        }
        catch (RuntimeException | Error stack) {
            // empty catch block
        }
        String _wn = hp.getWirelessName();
        ctx.getById("linkNameLabel", LabelBuilder.class).ifPresent(lb -> lb.withText("Name: " + (_wn == null || _wn.isBlank() ? "(none)" : _wn)));
        Vector3i wt = hp.getWirelessTarget();
        String wtText = wt != null ? wt.x + ", " + wt.y + ", " + wt.z : "(none)";
        ctx.getById("linkTargetLabel", LabelBuilder.class).ifPresent(lb -> lb.withText("Linked To: " + wtText));
        if (HopperProcessor.ARCIO_PRESENT) {
            String modeDisplay = "EnableWhenSignal".equals(hp.getArcioMode()) ? "Enable When Signal" : "Ignore Signal";
            ctx.getById("arcioModeLabel", LabelBuilder.class).ifPresent(lb -> lb.withText("ArcIO Mode: " + modeDisplay));
        }
        ctx.updatePage(true);
    }

    private static void refreshFacadeLabels(UIContext ctx, HopperComponent hopper) {
        int rotatedMask = FacadeHelper.rotateMask(hopper.getFacadeConnectionMask(), hopper.getFacadeRotation());
        String variantKey = FacadeHelper.buildVariantKey(hopper.getFacadeBlockId(), rotatedMask);
        ctx.getById("facadeRotYLabel", LabelBuilder.class).ifPresent(lb -> lb.withText(hopper.getFacadeRotation() * 90 + " deg"));
        ctx.getById("facadeRotXLabel", LabelBuilder.class).ifPresent(lb -> lb.withText(hopper.getFacadeRotationX() * 90 + " deg"));
        ctx.getById("facadeRotZLabel", LabelBuilder.class).ifPresent(lb -> lb.withText(hopper.getFacadeRotationZ() * 90 + " deg"));
        ctx.getById("facadeVariantLabel", LabelBuilder.class).ifPresent(lb -> lb.withText("Variant: " + variantKey));
        ctx.updatePage(true);
    }

    private static HopperComponent lookupHopper(Store<EntityStore> store, Vector3i pos) {
        try {
            World world = store.getExternalData().getWorld();
            WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
            if (chunk == null) {
                return null;
            }
            try {
                Store<ChunkStore> cs = world.getChunkStore().getStore();
                Ref<ChunkStore> chunkRef = world.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
                if (chunkRef != null) {
                    BlockComponentChunk bcc = cs.getComponent(chunkRef, BlockComponentChunk.getComponentType());
                    if (bcc != null) {
                        Ref<ChunkStore> blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn(pos.x, pos.y, pos.z));
                        Ev0Lib lib = Ev0Lib.getInstance();
                        if (blockRef != null && lib != null) {
                            ComponentType<ChunkStore, HopperComponent> compType = lib.getHopperComponentType();
                            if (compType != null) {
                                HopperComponent comp = cs.getComponent(blockRef, compType);
                                if (comp instanceof HopperComponent) {
                                    return comp;
                                }
                            }
                        }
                    }
                }
            }
            catch (Throwable cs) {
                // empty catch block
            }
            Object state = EngineCompat.getState(chunk, pos.x, pos.y, pos.z);
            if (state instanceof HopperProcessor hp) {
                HopperComponent hc = new HopperComponent();
                try {
                    hc.data = hp.data;
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                try {
                    hc.setFilterMode(hp.getFilterMode());
                    for (String s : hp.getWhitelist()) {
                        hc.addToWhitelist(s);
                    }
                    for (String s : hp.getBlacklist()) {
                        hc.addToBlacklist(s);
                    }
                    hc.setArcioMode(hp.getArcioMode());
                    if (hp.data != null) {
                        hc.setWirelessName(hp.data.wirelessName);
                        if (hp.data.wirelessTargetY != Integer.MIN_VALUE) {
                            hc.setWirelessTarget(new Vector3i(hp.data.wirelessTargetX, hp.data.wirelessTargetY, hp.data.wirelessTargetZ));
                        }
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                try {
                    hc.setItemContainer(hp.getItemContainer());
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                return hc;
            }
            return null;
        }
        catch (Throwable t) {
            Ev0Log.warn(LOGGER, "HopperUI: lookupHopper error: " + t.getMessage());
            return null;
        }
    }

    private static /* synthetic */ void lambda$refreshLabels$3(String slotText, LabelBuilder lb) {
        lb.withText("Slot: " + slotText);
    }
}

