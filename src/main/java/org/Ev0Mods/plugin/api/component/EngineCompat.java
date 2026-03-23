package org.Ev0Mods.plugin.api.component;

import java.lang.reflect.Method;

public class EngineCompat {
    private static Method findMethod(Class<?> cls, String... names) {
        for (String name : names) {
            try {
                for (Method m : cls.getMethods()) {
                    if (m.getName().equals(name)) return m;
                }
            } catch (Throwable ignored) {}
        }
        return null;
    }

    public static Object getState(Object chunk, int x, int y, int z) {
        if (chunk == null) return null;
        try {
            Method m = findMethod(chunk.getClass(), "getState", "getBlockState", "stateAt");
            if (m != null) return m.invoke(chunk, x, y, z);
        } catch (Throwable ignored) {}
        return null;
    }

    public static Object getBlockType(Object chunk, int x, int y, int z) {
        if (chunk == null) return null;
        try {
            Method m = findMethod(chunk.getClass(), "getBlockType", "blockTypeAt", "getType");
            if (m != null) return m.invoke(chunk, x, y, z);
        } catch (Throwable ignored) {}
        return null;
    }

    public static int getFluidId(Object chunk, int x, int y, int z) {
        if (chunk == null) return 0;
        try {
            Method m = findMethod(chunk.getClass(), "getFluidId", "fluidIdAt");
            if (m != null) {
                Object r = m.invoke(chunk, x, y, z);
                if (r instanceof Number) return ((Number) r).intValue();
            }
        } catch (Throwable ignored) {}
        return 0;
    }

    public static void setBlock(Object chunk, int x, int y, int z, Object blockKeyOrType) {
        if (chunk == null) return;
        try {
            Method m = findMethod(chunk.getClass(), "setBlock", "placeBlockAt", "setBlockAt");
            if (m != null) m.invoke(chunk, x, y, z, blockKeyOrType);
        } catch (Throwable ignored) {}
    }
}
