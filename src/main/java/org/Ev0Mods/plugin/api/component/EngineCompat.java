/*
 * Decompiled with CFR 0.152.
 */
package org.Ev0Mods.plugin.api.component;

import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import java.lang.reflect.Method;

public class EngineCompat {
    private static volatile boolean blockTypeReflectionDone = false;
    private static Method blockTypeGetAssetMap = null;
    private static Method assetMapGetIndexOrDefault = null;

    /**
     * Returns true if key is a registered block type.
     * Uses BlockType.getAssetMap().getIndexOrDefault(key, -1) >= 0.
     * Fails open (true) only if reflection itself cannot locate the methods.
     */
    public static boolean isValidBlockKey(String key) {
        if (key == null || key.isEmpty()) return false;
        if (!blockTypeReflectionDone) {
            synchronized (EngineCompat.class) {
                if (!blockTypeReflectionDone) {
                    try {
                        Class<?> btClass = Class.forName(
                            "com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType");
                        blockTypeGetAssetMap = btClass.getMethod("getAssetMap");
                        Object sampleMap = blockTypeGetAssetMap.invoke(null);
                        if (sampleMap != null) {
                            assetMapGetIndexOrDefault = sampleMap.getClass()
                                .getMethod("getIndexOrDefault", Object.class, int.class);
                        }
                    } catch (Throwable ignored) {}
                    blockTypeReflectionDone = true;
                }
            }
        }
        if (blockTypeGetAssetMap != null && assetMapGetIndexOrDefault != null) {
            try {
                Object map = blockTypeGetAssetMap.invoke(null);
                if (map == null) return true;
                Object idx = assetMapGetIndexOrDefault.invoke(map, key, -1);
                return idx instanceof Integer && (Integer) idx >= 0;
            } catch (Throwable ignored) {}
        }
        return true; // fail open only when reflection itself is unavailable
    }

    private static Method findMethod(Class<?> cls, String ... names) {
        for (String name : names) {
            try {
                for (Method m : cls.getMethods()) {
                    if (!m.getName().equals(name)) continue;
                    return m;
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return null;
    }

    public static Object getState(Object chunk, int x, int y, int z) {
        if (chunk == null) {
            return null;
        }
        try {
            Method m = EngineCompat.findMethod(chunk.getClass(), "getState", "getBlockState", "stateAt");
            if (m != null) {
                return m.invoke(chunk, x, y, z);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    public static Object getBlockType(Object chunk, int x, int y, int z) {
        if (chunk == null) {
            return null;
        }
        try {
            // Use the exact (int,int,int) overload from BlockAccessor
            Method m = chunk.getClass().getMethod("getBlockType", int.class, int.class, int.class);
            return m.invoke(chunk, x, y, z);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    public static int getFluidId(Object chunk, int x, int y, int z) {
        if (chunk == null) {
            return 0;
        }
        try {
            Object r;
            Method m = EngineCompat.findMethod(chunk.getClass(), "getFluidId", "fluidIdAt");
            if (m != null && (r = m.invoke(chunk, x, y, z)) instanceof Number) {
                return ((Number)r).intValue();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return 0;
    }

    /**
     * Resolves the persistent string identifier of the fluid at the given position
     * (e.g. "Water_Source", "Lava"), or null if there is no fluid. Prefer this over the
     * raw numeric fluid id: that id is an asset-map index that is NOT stable across game
     * versions or different sets of installed mods, so comparing it to hardcoded numbers
     * breaks as soon as another mod registers a fluid and shifts the indices.
     */
    public static String getFluidKey(Object chunk, int x, int y, int z) {
        int id = EngineCompat.getFluidId(chunk, x, y, z);
        if (id == 0) {
            return null;
        }
        try {
            Fluid f = Fluid.getAssetMap().getAsset(id);
            return f == null ? null : f.getId();
        }
        catch (Throwable t) {
            return null;
        }
    }

    /**
     * Maps a fluid's string identifier to the matching filled-bucket item key, or null if
     * the fluid has no bucket. Matches on the base fluid name so both the source and
     * flowing variants (e.g. "Lava" and "Lava_Source") resolve to the same bucket.
     */
    public static String filledBucketForFluid(String fluidKey) {
        if (fluidKey == null) {
            return null;
        }
        // Order matters: "Slime_Red" must be checked before "Slime".
        if (fluidKey.startsWith("Slime_Red")) return "*Container_Bucket_State_Filled_Red_Slime";
        if (fluidKey.startsWith("Slime")) return "*Container_Bucket_State_Filled_Green_Slime";
        if (fluidKey.startsWith("Tar")) return "*Container_Bucket_State_Filled_Tar";
        if (fluidKey.startsWith("Poison")) return "*Container_Bucket_State_Filled_Poison";
        if (fluidKey.startsWith("Lava")) return "*Container_Bucket_State_Filled_Lava";
        if (fluidKey.startsWith("Water")) return "*Container_Bucket_State_Filled_Water";
        return null;
    }

    // Calls setBlock(int, int, int, String) — the BlockAccessor overload that accepts a block key string.
    // Returns the boolean result from the engine (false = placement was rejected).
    public static boolean setBlock(Object chunk, int x, int y, int z, String blockKey) {
        if (chunk == null || blockKey == null) return false;
        try {
            Method m = chunk.getClass().getMethod("setBlock", int.class, int.class, int.class, String.class);
            Object result = m.invoke(chunk, x, y, z, blockKey);
            return Boolean.TRUE.equals(result);
        } catch (Throwable t) {
            return false;
        }
    }

    // Kept for call sites that pass a non-String (e.g. BlockType.EMPTY). Dispatches to the String
    // overload when given a String, otherwise falls back to name-search reflection.
    public static void setBlock(Object chunk, int x, int y, int z, Object blockKeyOrType) {
        if (chunk == null) return;
        if (blockKeyOrType instanceof String) {
            setBlock(chunk, x, y, z, (String) blockKeyOrType);
            return;
        }
        try {
            Method m = EngineCompat.findMethod(chunk.getClass(), "setBlock", "placeBlockAt", "setBlockAt");
            if (m != null) {
                m.invoke(chunk, x, y, z, blockKeyOrType);
            }
        } catch (Throwable ignored) {}
    }

    // Uses BlockAccessor.getBlockType(int,int,int).getId() — exact signatures, no name-only search.
    public static String getBlockId(Object chunk, int x, int y, int z) {
        if (chunk == null) return null;
        try {
            Method getBlockType = chunk.getClass().getMethod("getBlockType", int.class, int.class, int.class);
            Object bt = getBlockType.invoke(chunk, x, y, z);
            return getBlockTypeId(bt);
        } catch (Throwable ignored) {}
        return null;
    }

    // Uses BlockType.getId() — exact no-arg method, returns String.
    public static String getBlockTypeId(Object blockType) {
        if (blockType == null) return null;
        try {
            Method m = blockType.getClass().getMethod("getId");
            Object v = m.invoke(blockType);
            if (v instanceof String && !((String) v).isEmpty()) return (String) v;
        } catch (Throwable ignored) {}
        return null;
    }
}
