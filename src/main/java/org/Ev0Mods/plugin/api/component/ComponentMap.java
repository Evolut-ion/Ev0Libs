/*
 * Decompiled with CFR 0.152.
 */
package org.Ev0Mods.plugin.api.component;

import java.util.concurrent.ConcurrentHashMap;

public class ComponentMap {
    private final ConcurrentHashMap<String, Object> map = new ConcurrentHashMap();

    public Object get(String key) {
        return this.map.get(key);
    }

    public <T> T getAs(String key, Class<T> cls) {
        Object v = this.map.get(key);
        if (v == null) {
            return null;
        }
        if (cls.isInstance(v)) {
            return cls.cast(v);
        }
        return null;
    }

    public void put(String key, Object value) {
        if (value == null) {
            this.map.remove(key);
        } else {
            this.map.put(key, value);
        }
    }

    public boolean contains(String key) {
        return this.map.containsKey(key);
    }
}

