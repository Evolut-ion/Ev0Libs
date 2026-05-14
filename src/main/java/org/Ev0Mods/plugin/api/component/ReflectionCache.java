/*
 * Decompiled with CFR 0.152.
 */
package org.Ev0Mods.plugin.api.component;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionCache {
    private static final Map<Object, ConcurrentHashMap<String, Object>> syntheticStore = Collections.synchronizedMap(new WeakHashMap());

    public static Object getFieldValue(Class<?> clazz, Object target, String fieldName) {
        try {
            Field f = clazz.getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(target);
        }
        catch (IllegalAccessException | NoSuchFieldException e) {
            ConcurrentHashMap<String, Object> m = syntheticStore.get(target);
            if (m == null) {
                return null;
            }
            return m.get(fieldName);
        }
    }

    public static void setField(Class<?> clazz, Object target, String fieldName, Object value) {
        try {
            Field f = clazz.getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        }
        catch (IllegalAccessException | NoSuchFieldException e) {
            ConcurrentHashMap m = syntheticStore.computeIfAbsent(target, k -> new ConcurrentHashMap());
            if (value == null) {
                m.remove(fieldName);
            }
            m.put(fieldName, value);
        }
    }
}

