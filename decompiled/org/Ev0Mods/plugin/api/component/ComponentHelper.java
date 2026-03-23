/*
 * Decompiled with CFR 0.152.
 */
package org.Ev0Mods.plugin.api.component;

import java.util.concurrent.ConcurrentHashMap;
import org.Ev0Mods.plugin.api.component.ComponentMap;

public class ComponentHelper {
    private static final ConcurrentHashMap<String, ConcurrentHashMap<String, ComponentMap>> store = new ConcurrentHashMap();

    public static ComponentMap getOrCreateComponentForStateClass(Class<?> stateClass, String key) {
        String cls = stateClass.getName();
        return store.computeIfAbsent(cls, c -> new ConcurrentHashMap()).computeIfAbsent(key, k -> new ComponentMap());
    }

    public static ComponentMap getComponentForStateClass(Class<?> stateClass, String key) {
        String cls = stateClass.getName();
        ConcurrentHashMap<String, ComponentMap> m = store.get(cls);
        if (m == null) {
            return null;
        }
        return m.get(key);
    }

    public static ComponentMap getOrCreateForBlockState(Object state, String key) {
        if (state == null) {
            return ComponentHelper.getOrCreateComponentForStateClass(Object.class, key);
        }
        return ComponentHelper.getOrCreateComponentForStateClass(state.getClass(), key);
    }

    public static ComponentMap getForBlockState(Object state, String key) {
        if (state == null) {
            return null;
        }
        return ComponentHelper.getComponentForStateClass(state.getClass(), key);
    }

    public static Object get(Object state, String componentKey, String field) {
        ComponentMap cm = ComponentHelper.getForBlockState(state, componentKey);
        if (cm == null) {
            return null;
        }
        return cm.get(field);
    }

    public static <T> T getAs(Object state, String componentKey, String field, Class<T> cls) {
        ComponentMap cm = ComponentHelper.getForBlockState(state, componentKey);
        if (cm == null) {
            return null;
        }
        return cm.getAs(field, cls);
    }

    public static void put(Object state, String componentKey, String field, Object value) {
        ComponentMap cm = ComponentHelper.getOrCreateForBlockState(state, componentKey);
        cm.put(field, value);
    }
}

