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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.Ev0Mods.plugin.api.Ev0Log;
import org.Ev0Mods.plugin.api.util.WirelessHelpers;
import org.yaml.snakeyaml.Yaml;

public class WirelessRegistry {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final ConcurrentHashMap<String, Entry> ENTRIES = new ConcurrentHashMap();
    private static Path DATA_FILE = null;

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
        Entry e = ENTRIES.computeIfAbsent(key, Entry::new);
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

    public static boolean isAccessibleBy(String channelName, String playerName, String providedPasscode) {
        if (channelName == null) {
            return false;
        }
        Entry e = ENTRIES.get(channelName.trim());
        if (e == null) {
            return true;
        }
        if (e.owner == null) {
            return true;
        }
        if (playerName != null && playerName.equals(e.owner)) {
            return true;
        }
        return e.passcode != null && !e.passcode.isBlank() && e.passcode.equals(providedPasscode);
    }

    public static synchronized boolean setPasscode(String channelName, String requestingPlayer, String newPasscode) {
        if (channelName == null) {
            return false;
        }
        Entry e = ENTRIES.get(channelName.trim());
        if (e == null) {
            return false;
        }
        if (e.owner != null && !e.owner.equals(requestingPlayer)) {
            return false;
        }
        e.passcode = newPasscode == null || newPasscode.isBlank() ? null : newPasscode.trim();
        WirelessRegistry.save();
        return true;
    }

    public static String getOwner(String channelName) {
        if (channelName == null) {
            return null;
        }
        Entry e = ENTRIES.get(channelName.trim());
        return e == null ? null : e.owner;
    }

    public static String getPasscode(String channelName) {
        if (channelName == null) {
            return null;
        }
        Entry e = ENTRIES.get(channelName.trim());
        return e == null ? null : e.passcode;
    }

    public static synchronized void unregister(Vector3i pos) {
        if (pos == null) {
            return;
        }
        for (Map.Entry<String, Entry> en2 : ENTRIES.entrySet()) {
            Entry e = en2.getValue();
            e.exporters.remove(pos);
            e.importers.remove(pos);
        }
        ENTRIES.entrySet().removeIf(en -> ((Entry)en.getValue()).exporters.isEmpty() && ((Entry)en.getValue()).importers.isEmpty());
        WirelessRegistry.save();
    }

    public static void pruneHopperLink(World world, Vector3i hopperPos) {
        WirelessRegistry.unregister(hopperPos);
    }

    public static String getTypeForPos(Vector3i pos) {
        if (pos == null) {
            return null;
        }
        for (Entry e : ENTRIES.values()) {
            if (e.exporters.contains(pos)) {
                return "WirelessExport";
            }
            if (!e.importers.contains(pos)) continue;
            return "WirelessImport";
        }
        return null;
    }

    public static List<LinkItem> getAllLinkItems() {
        ArrayList<LinkItem> out = new ArrayList<LinkItem>();
        for (Entry e : ENTRIES.values()) {
            for (Vector3i p : e.exporters) {
                out.add(new LinkItem(e.name, "Export", p));
            }
            for (Vector3i p : e.importers) {
                out.add(new LinkItem(e.name, "Import", p));
            }
        }
        return out;
    }

    public static List<ActivePair> getActivePairs() {
        ArrayList<ActivePair> out = new ArrayList<ActivePair>();
        for (Entry e : ENTRIES.values()) {
            if (e.exporters.isEmpty() || e.importers.isEmpty()) continue;
            for (Vector3i exp : e.exporters) {
                for (Vector3i imp : e.importers) {
                    out.add(new ActivePair(e.name, exp, imp));
                }
            }
        }
        return out;
    }

    public static List<LinkItem> getPendingItems() {
        ArrayList<LinkItem> out = new ArrayList<LinkItem>();
        for (Entry e : ENTRIES.values()) {
            if (!e.exporters.isEmpty() && !e.importers.isEmpty()) continue;
            for (Vector3i p : e.exporters) {
                out.add(new LinkItem(e.name, "Export", p));
            }
            for (Vector3i p : e.importers) {
                out.add(new LinkItem(e.name, "Import", p));
            }
        }
        return out;
    }

    private static void attemptAutoLink(World world, String name) {
        try {
            Entry e = ENTRIES.get(name);
            if (e == null) {
                return;
            }
            if (e.exporters.isEmpty() || e.importers.isEmpty()) {
                return;
            }
            Vector3i exportPos = e.exporters.iterator().next();
            Vector3i importPos = e.importers.iterator().next();
            if (exportPos == null || importPos == null) {
                return;
            }
            WirelessHelpers.linkPair(world, exportPos, importPos, name);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public static void linkTo(World world, Vector3i hopperPos, Vector3i targetPos) {
        if (world == null || hopperPos == null || targetPos == null) {
            return;
        }
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
        if (DATA_FILE == null) {
            return;
        }
        try {
            LinkedHashMap root = new LinkedHashMap();
            for (Map.Entry<String, Entry> en : ENTRIES.entrySet()) {
                Entry e = en.getValue();
                LinkedHashMap<String, Object> node = new LinkedHashMap<String, Object>();
                ArrayList exps = new ArrayList();
                for (Vector3i p : e.exporters) {
                    HashMap<String, Integer> mp = new HashMap<String, Integer>();
                    mp.put("x", p.x);
                    mp.put("y", p.y);
                    mp.put("z", p.z);
                    exps.add(mp);
                }
                ArrayList imps = new ArrayList();
                for (Vector3i p : e.importers) {
                    HashMap<String, Integer> mp = new HashMap<String, Integer>();
                    mp.put("x", p.x);
                    mp.put("y", p.y);
                    mp.put("z", p.z);
                    imps.add(mp);
                }
                node.put("exporters", exps);
                node.put("importers", imps);
                if (e.owner != null) {
                    node.put("owner", e.owner);
                }
                if (e.passcode != null) {
                    node.put("passcode", e.passcode);
                }
                root.put(en.getKey(), node);
            }
            Yaml yaml = new Yaml();
            try (BufferedWriter w = Files.newBufferedWriter(DATA_FILE, StandardCharsets.UTF_8, new OpenOption[0]);){
                yaml.dump(root, w);
            }
        }
        catch (Throwable t) {
            Ev0Log.warn(LOGGER, "WirelessRegistry.save failed: " + t.getMessage());
        }
    }

    private static synchronized void loadFromDisk() {
        if (DATA_FILE == null) {
            return;
        }
        try {
            if (!Files.exists(DATA_FILE, new LinkOption[0])) {
                return;
            }
            Yaml yaml = new Yaml();
            try (InputStream in = Files.newInputStream(DATA_FILE, new OpenOption[0]);){
                Object obj = yaml.load(in);
                if (!(obj instanceof Map)) {
                    return;
                }
                Map root = (Map)obj;
                for (Object enObj : root.entrySet()) { Map.Entry en = (Map.Entry) enObj;
                    String s;
                    Object passcodeVal;
                    String s2;
                    Object ownerVal;
                    Object imps;
                    String name = String.valueOf(en.getKey());
                    Object val = en.getValue();
                    if (!(val instanceof Map)) continue;
                    Map node = (Map)val;
                    Entry entry = ENTRIES.computeIfAbsent(name, Entry::new);
                    Object exps = node.get("exporters");
                    if (exps instanceof Iterable) {
                        for (Object o : (Iterable)exps) {
                            if (!(o instanceof Map)) continue;
                            Map mp = (Map)o;
                            Number x = (Number)mp.get("x");
                            Number y = (Number)mp.get("y");
                            Number z = (Number)mp.get("z");
                            if (x == null || y == null || z == null) continue;
                            entry.exporters.add(new Vector3i(x.intValue(), y.intValue(), z.intValue()));
                        }
                    }
                    if ((imps = node.get("importers")) instanceof Iterable) {
                        Iterator o = ((Iterable)imps).iterator();
                        while (o.hasNext()) {
                            Object o2 = o.next();
                            if (!(o2 instanceof Map)) continue;
                            Map mp = (Map)o2;
                            Number x = (Number)mp.get("x");
                            Number y = (Number)mp.get("y");
                            Number z = (Number)mp.get("z");
                            if (x == null || y == null || z == null) continue;
                            entry.importers.add(new Vector3i(x.intValue(), y.intValue(), z.intValue()));
                        }
                    }
                    if ((ownerVal = node.get("owner")) instanceof String && !(s2 = (String)ownerVal).isBlank()) {
                        entry.owner = s2;
                    }
                    if (!((passcodeVal = node.get("passcode")) instanceof String) || (s = (String)passcodeVal).isBlank()) continue;
                    entry.passcode = s;
                }
            }
        }
        catch (Throwable t) {
            Ev0Log.warn(LOGGER, "WirelessRegistry.loadFromDisk failed: " + t.getMessage());
        }
    }

    public static synchronized void pruneForWorld(World world) {
        if (world == null) {
            return;
        }
        try {
            ArrayList<Vector3i> toRemove = new ArrayList<Vector3i>();
            for (Map.Entry<String, Entry> en2 : ENTRIES.entrySet()) {
                String type;
                WorldChunk chunk2;
                Entry e = en2.getValue();
                for (Vector3i p : e.exporters) {
                    try {
                        chunk2 = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(p.x, p.z));
                        if (chunk2 == null || (type = WirelessHelpers.getHopperType(world, p)) != null) continue;
                        toRemove.add(p);
                    }
                    catch (Throwable chunk3) {}
                }
                for (Vector3i p : e.importers) {
                    try {
                        chunk2 = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(p.x, p.z));
                        if (chunk2 == null || (type = WirelessHelpers.getHopperType(world, p)) != null) continue;
                        toRemove.add(p);
                    }
                    catch (Throwable throwable) {}
                }
            }
            if (!toRemove.isEmpty()) {
                for (Vector3i pos : toRemove) {
                    for (Map.Entry<String, Entry> en3 : ENTRIES.entrySet()) {
                        Entry e = en3.getValue();
                        e.exporters.remove(pos);
                        e.importers.remove(pos);
                    }
                }
                ENTRIES.entrySet().removeIf(en -> ((Entry)en.getValue()).exporters.isEmpty() && ((Entry)en.getValue()).importers.isEmpty());
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
        final Set<Vector3i> exporters = Collections.newSetFromMap(new ConcurrentHashMap());
        final Set<Vector3i> importers = Collections.newSetFromMap(new ConcurrentHashMap());
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

