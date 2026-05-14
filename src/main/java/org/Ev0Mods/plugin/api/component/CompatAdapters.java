/*
 * Decompiled with CFR 0.152.
 */
package org.Ev0Mods.plugin.api.component;

import org.Ev0Mods.plugin.api.component.ComponentHelper;
import org.Ev0Mods.plugin.api.component.ReflectionCache;

public class CompatAdapters {
    public static Object readStateFieldOrComponent(Object stateInstance, Object stateDataInstance, String stateField, String componentKey, String componentField) {
        Object v = ComponentHelper.get(stateInstance, componentKey, componentField);
        if (v != null) {
            return v;
        }
        if (stateDataInstance != null) {
            return ReflectionCache.getFieldValue(stateDataInstance.getClass(), stateDataInstance, stateField);
        }
        return null;
    }

    public static void writeStateFieldAndComponent(Object stateInstance, Object stateDataInstance, String stateField, String componentKey, String componentField, Object value) {
        if (stateInstance != null) {
            ComponentHelper.put(stateInstance, componentKey, componentField, value);
        }
        if (stateDataInstance != null) {
            ReflectionCache.setField(stateDataInstance.getClass(), stateDataInstance, stateField, value);
        }
    }
}

