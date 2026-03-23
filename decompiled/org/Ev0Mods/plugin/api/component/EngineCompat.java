/*
 * Decompiled with CFR 0.152.
 */
package org.Ev0Mods.plugin.api.component;

import java.lang.reflect.Method;

public class EngineCompat {
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

