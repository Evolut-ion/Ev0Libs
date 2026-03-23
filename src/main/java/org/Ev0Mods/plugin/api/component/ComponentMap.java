package org.Ev0Mods.plugin.api.component;

import java.util.concurrent.ConcurrentHashMap;

public class ComponentMap {
    private final ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();

    public Object get(String key) {
        return map.get(key);
    }

    public <T> T getAs(String key, Class<T> cls) {
        Object v = map.get(key);
        if (v == null) return null;
        if (cls.isInstance(v)) return cls.cast(v);
        return null;
    }

    public void put(String key, Object value) {
        if (value == null) map.remove(key); else map.put(key, value);
    }

    public boolean contains(String key) {
        return map.containsKey(key);
    }
}
