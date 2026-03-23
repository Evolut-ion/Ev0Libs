package org.Ev0Mods.plugin.api.component;

public class CompatAdapters {
    public static Object readStateFieldOrComponent(Object stateInstance, Object stateDataInstance, String stateField, String componentKey, String componentField) {
        Object v = ComponentHelper.get(stateInstance, componentKey, componentField);
        if (v != null) return v;
        if (stateDataInstance != null) {
            return ReflectionCache.getFieldValue(stateDataInstance.getClass(), stateDataInstance, stateField);
        }
        return null;
    }

    public static void writeStateFieldAndComponent(Object stateInstance, Object stateDataInstance, String stateField, String componentKey, String componentField, Object value) {
        // write component map first (source of truth)
        if (stateInstance != null) ComponentHelper.put(stateInstance, componentKey, componentField, value);
        // attempt to set the legacy field; will fall back to synthetic store if field removed
        if (stateDataInstance != null) ReflectionCache.setField(stateDataInstance.getClass(), stateDataInstance, stateField, value);
    }
}
