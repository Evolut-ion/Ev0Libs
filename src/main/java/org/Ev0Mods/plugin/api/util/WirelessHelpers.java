/*
 * Decompiled with CFR 0.152.
 */
package org.Ev0Mods.plugin.api.util;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockPatternRule;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.Ev0Mods.plugin.Ev0Lib;
import org.Ev0Mods.plugin.api.block.state.HopperProcessor;
import org.Ev0Mods.plugin.api.component.EngineCompat;
import org.Ev0Mods.plugin.api.component.HopperComponent;

public final class WirelessHelpers {
    private WirelessHelpers() {
    }

    private static HopperComponent resolveHopperComponent(World world, Vector3i pos) {
        try {
            HopperComponent comp;
            ComponentType<ChunkStore, HopperComponent> compType;
            Ev0Lib lib;
            Ref<ChunkStore> blockRef;
            BlockComponentChunk bcc;
            Store<ChunkStore> cs = world.getChunkStore().getStore();
            Ref<ChunkStore> chunkRef = world.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
            if (chunkRef != null && (bcc = cs.getComponent(chunkRef, BlockComponentChunk.getComponentType())) != null && (blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn(pos.x, pos.y, pos.z))) != null && (lib = Ev0Lib.getInstance()) != null && (compType = lib.getHopperComponentType()) != null && (comp = cs.getComponent(blockRef, compType)) instanceof HopperComponent) {
                HopperComponent hc = comp;
                return hc;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    public static String getHopperType(World world, Vector3i pos) {
        HopperComponent hc = WirelessHelpers.resolveHopperComponent(world, pos);
        if (hc != null) {
            String t = hc.getHopperType();
            if (t != null && !"Normal".equals(t)) {
                return t;
            }
            if (hc.data != null && hc.data.hopperType != null && !"Normal".equals(hc.data.hopperType)) {
                return hc.data.hopperType;
            }
            return "Normal";
        }
        try {
            WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
            if (chunk == null) {
                return null;
            }
            Object state = EngineCompat.getState(chunk, pos.x, pos.y, pos.z);
            if (state instanceof HopperProcessor) {
                HopperProcessor hp = (HopperProcessor)state;
                if (hp.data != null) {
                    return hp.data.hopperType;
                }
            }
            if (state instanceof HopperComponent) {
                HopperComponent legacyHc = (HopperComponent)state;
                if (legacyHc.data != null && legacyHc.data.hopperType != null) {
                    return legacyHc.data.hopperType;
                }
                return legacyHc.getHopperType();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    public static String getWirelessName(World world, Vector3i pos) {
        HopperComponent hc = WirelessHelpers.resolveHopperComponent(world, pos);
        if (hc != null) {
            return hc.getWirelessName();
        }
        try {
            WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
            if (chunk == null) {
                return null;
            }
            Object state = EngineCompat.getState(chunk, pos.x, pos.y, pos.z);
            if (state instanceof HopperProcessor) {
                HopperProcessor hp = (HopperProcessor)state;
                if (hp.data != null) {
                    return hp.data.wirelessName;
                }
            }
            if (state instanceof HopperComponent) {
                HopperComponent legacyHc = (HopperComponent)state;
                return legacyHc.getWirelessName();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static void setWirelessName(World world, Vector3i pos, String name) {
        HopperComponent hc = WirelessHelpers.resolveHopperComponent(world, pos);
        if (hc != null) {
            hc.setWirelessName(name == null ? "" : name);
            return;
        }
        try {
            WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
            if (chunk == null) {
                return;
            }
            Object state = EngineCompat.getState(chunk, pos.x, pos.y, pos.z);
            if (state instanceof HopperProcessor) {
                HopperProcessor hp = (HopperProcessor)state;
                if (hp.data != null) {
                    hp.data.wirelessName = name == null ? "" : name;
                    return;
                }
            }
            if (!(state instanceof HopperComponent)) return;
            HopperComponent legacyHc = (HopperComponent)state;
            legacyHc.setWirelessName(name == null ? "" : name);
            return;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static void setWirelessTarget(World world, Vector3i hopperPos, Vector3i targetPos) {
        HopperComponent hc = WirelessHelpers.resolveHopperComponent(world, hopperPos);
        if (hc != null) {
            hc.setWirelessTarget(targetPos);
            return;
        }
        try {
            WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(hopperPos.x, hopperPos.z));
            if (chunk == null) {
                return;
            }
            Object state = EngineCompat.getState(chunk, hopperPos.x, hopperPos.y, hopperPos.z);
            if (state instanceof HopperProcessor) {
                HopperProcessor hp = (HopperProcessor)state;
                if (hp.data != null) {
                    hp.data.setWirelessTarget(targetPos);
                    return;
                }
            }
            if (!(state instanceof HopperComponent)) return;
            HopperComponent legacyHc = (HopperComponent)state;
            legacyHc.setWirelessTarget(targetPos);
            return;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static void setImportFace(World world, Vector3i pos, ConnectedBlockPatternRule.AdjacentSide side) {
        ConnectedBlockPatternRule.AdjacentSide[] faces = new ConnectedBlockPatternRule.AdjacentSide[]{side};
        HopperComponent hc = WirelessHelpers.resolveHopperComponent(world, pos);
        if (hc != null) {
            if (hc.data == null) return;
            hc.data.importFaces = faces;
            return;
        }
        try {
            WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
            if (chunk == null) {
                return;
            }
            Object state = EngineCompat.getState(chunk, pos.x, pos.y, pos.z);
            if (state instanceof HopperProcessor) {
                HopperProcessor hp = (HopperProcessor)state;
                if (hp.data != null) {
                    hp.data.importFaces = faces;
                    return;
                }
            }
            if (!(state instanceof HopperComponent)) return;
            HopperComponent legacyHc = (HopperComponent)state;
            if (legacyHc.data == null) return;
            legacyHc.data.importFaces = faces;
            return;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static void setExportFace(World world, Vector3i pos, ConnectedBlockPatternRule.AdjacentSide side) {
        ConnectedBlockPatternRule.AdjacentSide[] faces = new ConnectedBlockPatternRule.AdjacentSide[]{side};
        HopperComponent hc = WirelessHelpers.resolveHopperComponent(world, pos);
        if (hc != null) {
            if (hc.data == null) return;
            hc.data.exportFaces = faces;
            return;
        }
        try {
            WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
            if (chunk == null) {
                return;
            }
            Object state = EngineCompat.getState(chunk, pos.x, pos.y, pos.z);
            if (state instanceof HopperProcessor) {
                HopperProcessor hp = (HopperProcessor)state;
                if (hp.data != null) {
                    hp.data.exportFaces = faces;
                    return;
                }
            }
            if (!(state instanceof HopperComponent)) return;
            HopperComponent legacyHc = (HopperComponent)state;
            if (legacyHc.data == null) return;
            legacyHc.data.exportFaces = faces;
            return;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public static void linkPair(World world, Vector3i exportPos, Vector3i importPos, String name) {
        try {
            WirelessHelpers.setWirelessTarget(world, exportPos, importPos);
            WirelessHelpers.setWirelessTarget(world, importPos, exportPos);
            if (name != null && !name.isBlank()) {
                WirelessHelpers.setWirelessName(world, exportPos, name);
                WirelessHelpers.setWirelessName(world, importPos, name);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static void clearWirelessTarget(World world, Vector3i pos) {
        HopperComponent hc = WirelessHelpers.resolveHopperComponent(world, pos);
        if (hc != null) {
            hc.clearWirelessTarget();
            hc.setWirelessName("");
            return;
        }
        try {
            WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
            if (chunk == null) {
                return;
            }
            Object state = EngineCompat.getState(chunk, pos.x, pos.y, pos.z);
            if (state instanceof HopperProcessor) {
                HopperProcessor hp = (HopperProcessor)state;
                if (hp.data != null) {
                    hp.data.wirelessTargetY = Integer.MIN_VALUE;
                    hp.data.wirelessName = "";
                    return;
                }
            }
            if (!(state instanceof HopperComponent)) return;
            HopperComponent legacyHc = (HopperComponent)state;
            legacyHc.clearWirelessTarget();
            legacyHc.setWirelessName("");
            return;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

