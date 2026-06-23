/*
 * Decompiled with CFR 0.152.
 */
package org.Ev0Mods.plugin.api.system;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.Ev0Mods.plugin.api.Ev0Log;
import org.Ev0Mods.plugin.api.util.WirelessHelpers;
import org.yaml.snakeyaml.Yaml;

public class WirelessRegistry {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    // Outer key: worldKey(world), inner key: channelName
    private static final ConcurrentHashMap<String, ConcurrentHashMap<String, Entry>> WORLD_ENTRIES = new ConcurrentHashMap<>();
    private static Path DATA_FILE = null;

    // Derives a stable string ID for a world using reflection so we don't depend on a specific API.
    static String worldKey(World world) {
        if (world == null) return "default";
        for (String m : new String[]{"getId", "getUUID", "getUuid", "getIdentifier", "getName", "getDimensionKey"}) {
            try {
                Method mm = world.getClass().getMethod(m);
                Object v = mm.invoke(world);
                if (v != null) return v.toString();
            } catch (Throwable ignored) {}
        }
        return "w" + Integer.toHexString(System.identityHashCode(world));
    }

    private static ConcurrentHashMap<String, Entry> entriesFor(World world) {
        return WORLD_ENTRIES.computeIfAbsent(worldKey(world), k -> new ConcurrentHashMap<>());
    }

    public static synchronized void initialize(Path dataDir) {
        try {
            if (dataDir == null) {
                return;
            }
            Files.createDirectories(dataDir, new FileAttribute[0]);
            DATA_FILE = dataDir.resolve("wireless_registry.yaml");
            WirelessRegistry.loadFromDisk();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    WirelessRegistry.save();
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }));
            Ev0Log.info(LOGGER, "WirelessRegistry initialized (dataFile=" + String.valueOf(DATA_FILE.toAbsolutePath()) + ")");
        }
        catch (Throwable t) {
            Ev0Log.warn(LOGGER, "WirelessRegistry.initialize failed: " + t.getMessage());
        }
    }

    public static synchronized void register(World world, Vector3i pos, String name, String hopperType, String userName) {
        if (name == null) {
            return;
        }
        String key = name.trim();
        if (key.isEmpty()) {
            return;
        }
        Entry e = entriesFor(world).computeIfAbsent(key, Entry::new);
        if (e.owner == null && userName != null && !userName.isBlank()) {
            e.owner = userName;
        }
        if ("WirelessExport".equalsIgnoreCase(hopperType)) {
            e.exporters.add(pos);
        } else {
            e.importers.add(pos);
        }
        WirelessRegistry.attemptAutoLink(world, key);
        WirelessRegistry.save();
    }

    // --- isAccessibleBy ---

    public static boolean isAccessibleBy(World world, String channelName, String playerName, String providedPasscode) {
        if (channelName == null) return false;
        Entry e = entriesFor(world).get(channelName.trim());
        if (e == null) return true;
        if (e.owner == null) return true;
        if (playerName != null && playerName.equals(e.owner)) return true;
        return e.passcode != null && !e.passcode.isBlank() && e.passcode.equals(providedPasscode);
    }

    // Backward-compat overload: searches all worlds; grants access if found accessible in any.
    public static boolean isAccessibleBy(String channelName, String playerName, String providedPasscode) {
        if (channelName == null) return false;
        String key = channelName.trim();
        for (ConcurrentHashMap<String, Entry> entries : WORLD_ENTRIES.values()) {
            Entry e = entries.get(key);
            if (e == null) continue;
            if (e.owner == null) return true;
            if (playerName != null && playerName.equals(e.owner)) return true;
            if (e.passcode != null && !e.passcode.isBlank() && e.passcode.equals(providedPasscode)) return true;
        }
        return true;
    }

    // --- setPasscode ---

    public static synchronized boolean setPasscode(World world, String channelName, String requestingPlayer, String newPasscode) {
        if (channelName == null) return false;
        Entry e = entriesFor(world).get(channelName.trim());
        if (e == null) return false;
        if (e.owner != null && !e.owner.equals(requestingPlayer)) return false;
        e.passcode = newPasscode == null || newPasscode.isBlank() ? null : newPasscode.trim();
        WirelessRegistry.save();
        return true;
    }

    // Backward-compat overload: searches all worlds.
    public static synchronized boolean setPasscode(String channelName, String requestingPlayer, String newPasscode) {
        if (channelName == null) return false;
        String key = channelName.trim();
        for (ConcurrentHashMap<String, Entry> entries : WORLD_ENTRIES.values()) {
            Entry e = entries.get(key);
            if (e == null) continue;
            if (e.owner != null && !e.owner.equals(requestingPlayer)) return false;
            e.passcode = newPasscode == null || newPasscode.isBlank() ? null : newPasscode.trim();
            WirelessRegistry.save();
            return true;
        }
        return false;
    }

    // --- getOwner / getPasscode ---

    public static String getOwner(World world, String channelName) {
        if (channelName == null) return null;
        Entry e = entriesFor(world).get(channelName.trim());
        return e == null ? null : e.owner;
    }

    public static String getOwner(String channelName) {
        if (channelName == null) return null;
        String key = channelName.trim();
        for (ConcurrentHashMap<String, Entry> entries : WORLD_ENTRIES.values()) {
            Entry e = entries.get(key);
            if (e != null && e.owner != null) return e.owner;
        }
        return null;
    }

    public static String getPasscode(World world, String channelName) {
        if (channelName == null) return null;
        Entry e = entriesFor(world).get(channelName.trim());
        return e == null ? null : e.passcode;
    }

    public static String getPasscode(String channelName) {
        if (channelName == null) return null;
        String key = channelName.trim();
        for (ConcurrentHashMap<String, Entry> entries : WORLD_ENTRIES.values()) {
            Entry e = entries.get(key);
            if (e != null && e.passcode != null) return e.passcode;
        }
        return null;
    }

    // --- unregister ---

    public static synchronized void unregister(World world, Vector3i pos) {
        if (pos == null) return;
        String wk = worldKey(world);
        ConcurrentHashMap<String, Entry> entries = WORLD_ENTRIES.get(wk);
        if (entries == null) return;
        for (Map.Entry<String, Entry> en : entries.entrySet()) {
            Entry e = en.getValue();
            e.exporters.remove(pos);
            e.importers.remove(pos);
        }
        entries.entrySet().removeIf(en -> en.getValue().exporters.isEmpty() && en.getValue().importers.isEmpty());
        if (entries.isEmpty()) WORLD_ENTRIES.remove(wk);
        WirelessRegistry.save();
    }

    // Backward-compat overload: removes the position from all worlds.
    public static synchronized void unregister(Vector3i pos) {
        if (pos == null) return;
        for (Map.Entry<String, ConcurrentHashMap<String, Entry>> worldEntry : WORLD_ENTRIES.entrySet()) {
            ConcurrentHashMap<String, Entry> entries = worldEntry.getValue();
            for (Map.Entry<String, Entry> en : entries.entrySet()) {
                Entry e = en.getValue();
                e.exporters.remove(pos);
                e.importers.remove(pos);
            }
            entries.entrySet().removeIf(en -> en.getValue().exporters.isEmpty() && en.getValue().importers.isEmpty());
        }
        WORLD_ENTRIES.entrySet().removeIf(en -> en.getValue().isEmpty());
        WirelessRegistry.save();
    }

    public static void pruneHopperLink(World world, Vector3i hopperPos) {
        WirelessRegistry.unregister(world, hopperPos);
    }

    /**
     * Called from a hopper's tick when it is already registered but has no wirelessTarget.
     * If the partner chunk is now loaded, re-runs linkPair so both hoppers get their targets set.
     * No-ops cheaply if the partner chunk is still unloaded.
     */
    public static void attemptRelinkIfChunkLoaded(World world, Vector3i myPos, String channelName) {
        if (world == null || myPos == null || channelName == null) return;
        try {
            Entry e = entriesFor(world).get(channelName.trim());
            if (e == null || e.exporters.isEmpty() || e.importers.isEmpty()) return;
            boolean iAmExporter = e.exporters.contains(myPos);
            boolean iAmImporter = e.importers.contains(myPos);
            if (!iAmExporter && !iAmImporter) return;
            Vector3i partnerPos = iAmExporter ? e.importers.iterator().next()
                                              : e.exporters.iterator().next();
            if (partnerPos == null) return;
            if (world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(partnerPos.x, partnerPos.z)) == null) return;
            Vector3i exportPos = iAmExporter ? myPos : partnerPos;
            Vector3i importPos = iAmExporter ? partnerPos : myPos;
            WirelessHelpers.linkPair(world, exportPos, importPos, channelName.trim());
        } catch (Throwable ignored) {}
    }

    // --- getTypeForPos ---

    public static String getTypeForPos(World world, Vector3i pos) {
        if (pos == null) return null;
        for (Entry e : entriesFor(world).values()) {
            if (e.exporters.contains(pos)) return "WirelessExport";
            if (e.importers.contains(pos)) return "WirelessImport";
        }
        return null;
    }

    // Backward-compat overload: scans all worlds.
    public static String getTypeForPos(Vector3i pos) {
        if (pos == null) return null;
        for (ConcurrentHashMap<String, Entry> entries : WORLD_ENTRIES.values()) {
            for (Entry e : entries.values()) {
                if (e.exporters.contains(pos)) return "WirelessExport";
                if (e.importers.contains(pos)) return "WirelessImport";
            }
        }
        return null;
    }

    // --- list queries (world-scoped and global fallbacks) ---

    public static List<LinkItem> getAllLinkItems(World world) {
        ArrayList<LinkItem> out = new ArrayList<LinkItem>();
        for (Entry e : entriesFor(world).values()) {
            for (Vector3i p : e.exporters) out.add(new LinkItem(e.name, "Export", p));
            for (Vector3i p : e.importers) out.add(new LinkItem(e.name, "Import", p));
        }
        return out;
    }

    public static List<LinkItem> getAllLinkItems() {
        ArrayList<LinkItem> out = new ArrayList<LinkItem>();
        for (ConcurrentHashMap<String, Entry> entries : WORLD_ENTRIES.values()) {
            for (Entry e : entries.values()) {
                for (Vector3i p : e.exporters) out.add(new LinkItem(e.name, "Export", p));
                for (Vector3i p : e.importers) out.add(new LinkItem(e.name, "Import", p));
            }
        }
        return out;
    }

    public static List<ActivePair> getActivePairs(World world) {
        ArrayList<ActivePair> out = new ArrayList<ActivePair>();
        for (Entry e : entriesFor(world).values()) {
            if (e.exporters.isEmpty() || e.importers.isEmpty()) continue;
            for (Vector3i exp : e.exporters) {
                for (Vector3i imp : e.importers) {
                    out.add(new ActivePair(e.name, exp, imp));
                }
            }
        }
        return out;
    }

    public static List<ActivePair> getActivePairs() {
        ArrayList<ActivePair> out = new ArrayList<ActivePair>();
        for (ConcurrentHashMap<String, Entry> entries : WORLD_ENTRIES.values()) {
            for (Entry e : entries.values()) {
                if (e.exporters.isEmpty() || e.importers.isEmpty()) continue;
                for (Vector3i exp : e.exporters) {
                    for (Vector3i imp : e.importers) {
                        out.add(new ActivePair(e.name, exp, imp));
                    }
                }
            }
        }
        return out;
    }

    public static List<LinkItem> getPendingItems(World world) {
        ArrayList<LinkItem> out = new ArrayList<LinkItem>();
        for (Entry e : entriesFor(world).values()) {
            if (!e.exporters.isEmpty() && !e.importers.isEmpty()) continue;
            for (Vector3i p : e.exporters) out.add(new LinkItem(e.name, "Export", p));
            for (Vector3i p : e.importers) out.add(new LinkItem(e.name, "Import", p));
        }
        return out;
    }

    public static List<LinkItem> getPendingItems() {
        ArrayList<LinkItem> out = new ArrayList<LinkItem>();
        for (ConcurrentHashMap<String, Entry> entries : WORLD_ENTRIES.values()) {
            for (Entry e : entries.values()) {
                if (!e.exporters.isEmpty() && !e.importers.isEmpty()) continue;
                for (Vector3i p : e.exporters) out.add(new LinkItem(e.name, "Export", p));
                for (Vector3i p : e.importers) out.add(new LinkItem(e.name, "Import", p));
            }
        }
        return out;
    }

    private static void attemptAutoLink(World world, String name) {
        try {
            Entry e = entriesFor(world).get(name);
            if (e == null) return;
            if (e.exporters.isEmpty() || e.importers.isEmpty()) return;
            Vector3i exportPos = e.exporters.iterator().next();
            Vector3i importPos = e.importers.iterator().next();
            if (exportPos == null || importPos == null) return;
            WirelessHelpers.linkPair(world, exportPos, importPos, name);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public static void linkTo(World world, Vector3i hopperPos, Vector3i targetPos) {
        if (world == null || hopperPos == null || targetPos == null) return;
        try {
            String myType = WirelessHelpers.getHopperType(world, hopperPos);
            String theirType = WirelessHelpers.getHopperType(world, targetPos);
            if (myType == null || theirType == null) {
                WirelessHelpers.setWirelessTarget(world, hopperPos, targetPos);
                return;
            }
            if ("WirelessExport".equalsIgnoreCase(myType) && "WirelessImport".equalsIgnoreCase(theirType)) {
                WirelessHelpers.linkPair(world, hopperPos, targetPos, null);
            } else if ("WirelessImport".equalsIgnoreCase(myType) && "WirelessExport".equalsIgnoreCase(theirType)) {
                WirelessHelpers.linkPair(world, targetPos, hopperPos, null);
            } else {
                WirelessHelpers.setWirelessTarget(world, hopperPos, targetPos);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static synchronized void save() {
        if (DATA_FILE == null) return;
        try {
            // Format: worldKey -> (channelName -> {exporters, importers, owner, passcode})
            LinkedHashMap<String, Object> root = new LinkedHashMap<>();
            for (Map.Entry<String, ConcurrentHashMap<String, Entry>> worldEntry : WORLD_ENTRIES.entrySet()) {
                LinkedHashMap<String, Object> worldNode = new LinkedHashMap<>();
                for (Map.Entry<String, Entry> en : worldEntry.getValue().entrySet()) {
                    Entry e = en.getValue();
                    LinkedHashMap<String, Object> node = new LinkedHashMap<>();
                    ArrayList<Object> exps = new ArrayList<>();
                    for (Vector3i p : e.exporters) {
                        HashMap<String, Integer> mp = new HashMap<>();
                        mp.put("x", p.x); mp.put("y", p.y); mp.put("z", p.z);
                        exps.add(mp);
                    }
                    ArrayList<Object> imps = new ArrayList<>();
                    for (Vector3i p : e.importers) {
                        HashMap<String, Integer> mp = new HashMap<>();
                        mp.put("x", p.x); mp.put("y", p.y); mp.put("z", p.z);
                        imps.add(mp);
                    }
                    node.put("exporters", exps);
                    node.put("importers", imps);
                    if (e.owner != null) node.put("owner", e.owner);
                    if (e.passcode != null) node.put("passcode", e.passcode);
                    worldNode.put(en.getKey(), node);
                }
                root.put(worldEntry.getKey(), worldNode);
            }
            Yaml yaml = new Yaml();
            try (BufferedWriter w = Files.newBufferedWriter(DATA_FILE, StandardCharsets.UTF_8, new OpenOption[0])) {
                yaml.dump(root, w);
            }
        }
        catch (Throwable t) {
            Ev0Log.warn(LOGGER, "WirelessRegistry.save failed: " + t.getMessage());
        }
    }

    private static synchronized void loadFromDisk() {
        if (DATA_FILE == null) return;
        try {
            if (!Files.exists(DATA_FILE, new LinkOption[0])) return;
            Yaml yaml = new Yaml();
            try (InputStream in = Files.newInputStream(DATA_FILE, new OpenOption[0])) {
                Object obj = yaml.load(in);
                if (!(obj instanceof Map)) return;
                Map<?, ?> root = (Map<?, ?>) obj;
                if (root.isEmpty()) return;

                // Detect format: old format has channel entries directly at root level (their values
                // contain "exporters"/"importers"). New format has worldKey -> channelMap nesting.
                boolean isOldFormat = false;
                for (Object val : root.values()) {
                    if (val instanceof Map) {
                        Map<?, ?> inner = (Map<?, ?>) val;
                        if (inner.containsKey("exporters") || inner.containsKey("importers")) {
                            isOldFormat = true;
                        }
                    }
                    break;
                }

                if (isOldFormat) {
                    // Migrate: treat everything as belonging to the "default" world.
                    ConcurrentHashMap<String, Entry> entries = WORLD_ENTRIES.computeIfAbsent("default", k -> new ConcurrentHashMap<>());
                    WirelessRegistry.parseChannelMap(root, entries);
                    Ev0Log.info(LOGGER, "WirelessRegistry: migrated legacy (single-world) save to 'default' world slot");
                } else {
                    // New format: root keys are worldKeys.
                    for (Map.Entry<?, ?> worldEntry : root.entrySet()) {
                        String wk = String.valueOf(worldEntry.getKey());
                        Object channelMapObj = worldEntry.getValue();
                        if (!(channelMapObj instanceof Map)) continue;
                        ConcurrentHashMap<String, Entry> entries = WORLD_ENTRIES.computeIfAbsent(wk, k -> new ConcurrentHashMap<>());
                        WirelessRegistry.parseChannelMap((Map<?, ?>) channelMapObj, entries);
                    }
                }
            }
        }
        catch (Throwable t) {
            Ev0Log.warn(LOGGER, "WirelessRegistry.loadFromDisk failed: " + t.getMessage());
        }
    }

    private static void parseChannelMap(Map<?, ?> channelMap, ConcurrentHashMap<String, Entry> entries) {
        for (Map.Entry<?, ?> en : channelMap.entrySet()) {
            Object val = en.getValue();
            if (!(val instanceof Map)) continue;
            Map<?, ?> node = (Map<?, ?>) val;
            String name = String.valueOf(en.getKey());
            Entry entry = entries.computeIfAbsent(name, Entry::new);
            Object exps = node.get("exporters");
            if (exps instanceof Iterable) {
                for (Object o : (Iterable<?>) exps) {
                    if (!(o instanceof Map)) continue;
                    Map<?, ?> mp = (Map<?, ?>) o;
                    Number x = (Number) mp.get("x");
                    Number y = (Number) mp.get("y");
                    Number z = (Number) mp.get("z");
                    if (x == null || y == null || z == null) continue;
                    entry.exporters.add(new Vector3i(x.intValue(), y.intValue(), z.intValue()));
                }
            }
            Object imps = node.get("importers");
            if (imps instanceof Iterable) {
                for (Object o : (Iterable<?>) imps) {
                    if (!(o instanceof Map)) continue;
                    Map<?, ?> mp = (Map<?, ?>) o;
                    Number x = (Number) mp.get("x");
                    Number y = (Number) mp.get("y");
                    Number z = (Number) mp.get("z");
                    if (x == null || y == null || z == null) continue;
                    entry.importers.add(new Vector3i(x.intValue(), y.intValue(), z.intValue()));
                }
            }
            Object ownerVal = node.get("owner");
            if (ownerVal instanceof String && !((String) ownerVal).isBlank()) {
                entry.owner = (String) ownerVal;
            }
            Object passcodeVal = node.get("passcode");
            if (passcodeVal instanceof String && !((String) passcodeVal).isBlank()) {
                entry.passcode = (String) passcodeVal;
            }
        }
    }

    public static synchronized void pruneForWorld(World world) {
        if (world == null) return;
        try {
            ConcurrentHashMap<String, Entry> entries = WORLD_ENTRIES.get(worldKey(world));
            if (entries == null) return;
            ArrayList<Vector3i> toRemove = new ArrayList<>();
            for (Map.Entry<String, Entry> en : entries.entrySet()) {
                Entry e = en.getValue();
                for (Vector3i p : e.exporters) {
                    try {
                        WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(p.x, p.z));
                        if (chunk != null && WirelessHelpers.getHopperType(world, p) == null) {
                            toRemove.add(p);
                        }
                    } catch (Throwable ignored) {}
                }
                for (Vector3i p : e.importers) {
                    try {
                        WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(p.x, p.z));
                        if (chunk != null && WirelessHelpers.getHopperType(world, p) == null) {
                            toRemove.add(p);
                        }
                    } catch (Throwable ignored) {}
                }
            }
            if (!toRemove.isEmpty()) {
                for (Vector3i pos : toRemove) {
                    for (Map.Entry<String, Entry> en : entries.entrySet()) {
                        Entry e = en.getValue();
                        e.exporters.remove(pos);
                        e.importers.remove(pos);
                    }
                }
                entries.entrySet().removeIf(en -> en.getValue().exporters.isEmpty() && en.getValue().importers.isEmpty());
                if (entries.isEmpty()) WORLD_ENTRIES.remove(worldKey(world));
                WirelessRegistry.save();
                Ev0Log.info(LOGGER, "WirelessRegistry.pruneForWorld removed " + toRemove.size() + " stale entries");
            }
        }
        catch (Throwable t) {
            Ev0Log.warn(LOGGER, "WirelessRegistry.pruneForWorld failed: " + t.getMessage());
        }
    }

    private static final class Entry {
        final String name;
        final Set<Vector3i> exporters = Collections.newSetFromMap(new ConcurrentHashMap<>());
        final Set<Vector3i> importers = Collections.newSetFromMap(new ConcurrentHashMap<>());
        volatile String owner;
        volatile String passcode;

        Entry(String n) {
            this.name = n;
        }
    }

    public static final class LinkItem {
        public final String name;
        public final String type;
        public final Vector3i pos;

        public LinkItem(String name, String type, Vector3i pos) {
            this.name = name;
            this.type = type;
            this.pos = pos;
        }
    }

    public static final class ActivePair {
        public final String name;
        public final Vector3i exportPos;
        public final Vector3i importPos;

        public ActivePair(String name, Vector3i exportPos, Vector3i importPos) {
            this.name = name;
            this.exportPos = exportPos;
            this.importPos = importPos;
        }
    }
}
