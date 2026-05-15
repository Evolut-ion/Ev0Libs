/*
 * Decompiled with CFR 0.152.
 */
package org.Ev0Mods.plugin.api.component;

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
            Method m = EngineCompat.findMethod(chunk.getClass(), "getBlockType", "blockTypeAt", "getType");
            if (m != null) {
                return m.invoke(chunk, x, y, z);
            }
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

    public static void setBlock(Object chunk, int x, int y, int z, Object blockKeyOrType) {
        if (chunk == null) {
            return;
        }
        try {
            Method m = EngineCompat.findMethod(chunk.getClass(), "setBlock", "placeBlockAt", "setBlockAt");
            if (m != null) {
                m.invoke(chunk, x, y, z, blockKeyOrType);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

