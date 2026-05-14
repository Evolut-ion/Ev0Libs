/*
 * Decompiled with CFR 0.152.
 */
package org.Ev0Mods.plugin.api.compat;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import org.joml.Vector3d;
import org.joml.Vector3i;

public final class HytaleCompat {
    private HytaleCompat() {
    }

    public static Vector3i toBlockVector(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Vector3i) {
            Vector3i vector = (Vector3i)value;
            return new Vector3i(vector);
        }
        return new Vector3i(HytaleCompat.intCoord(value, "x"), HytaleCompat.intCoord(value, "y"), HytaleCompat.intCoord(value, "z"));
    }

    public static Vector3d toWorldVector(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Vector3d) {
            Vector3d vector = (Vector3d)value;
            return new Vector3d(vector);
        }
        return new Vector3d(HytaleCompat.doubleCoord(value, "x"), HytaleCompat.doubleCoord(value, "y"), HytaleCompat.doubleCoord(value, "z"));
    }

    public static int intCoord(Object value, String axis) {
        Object direct = HytaleCompat.invokeNoArgs(value, axis);
        if (direct instanceof Number) {
            Number number = (Number)direct;
            return number.intValue();
        }
        Object getter = HytaleCompat.invokeNoArgs(value, "get" + Character.toUpperCase(axis.charAt(0)) + axis.substring(1));
        if (getter instanceof Number) {
            Number number = (Number)getter;
            return number.intValue();
        }
        Object field = HytaleCompat.readField(value, axis);
        if (field instanceof Number) {
            Number number = (Number)field;
            return number.intValue();
        }
        throw new IllegalArgumentException("Unable to read integer coordinate '" + axis + "' from " + value.getClass().getName());
    }

    public static double doubleCoord(Object value, String axis) {
        Object direct = HytaleCompat.invokeNoArgs(value, axis);
        if (direct instanceof Number) {
            Number number = (Number)direct;
            return number.doubleValue();
        }
        Object getter = HytaleCompat.invokeNoArgs(value, "get" + Character.toUpperCase(axis.charAt(0)) + axis.substring(1));
        if (getter instanceof Number) {
            Number number = (Number)getter;
            return number.doubleValue();
        }
        Object field = HytaleCompat.readField(value, axis);
        if (field instanceof Number) {
            Number number = (Number)field;
            return number.doubleValue();
        }
        throw new IllegalArgumentException("Unable to read numeric coordinate '" + axis + "' from " + value.getClass().getName());
    }

    public static float yaw(Object rotation) {
        return HytaleCompat.angle(rotation, "yaw", "getYaw", "y");
    }

    public static float pitch(Object rotation) {
        return HytaleCompat.angle(rotation, "pitch", "getPitch", "x");
    }

    public static Vector3d directionFromRotation(Object rotation) {
        double yawRadians = Math.toRadians(HytaleCompat.yaw(rotation));
        double pitchRadians = Math.toRadians(HytaleCompat.pitch(rotation));
        double x = -Math.sin(yawRadians) * Math.cos(pitchRadians);
        double y = -Math.sin(pitchRadians);
        double z = Math.cos(yawRadians) * Math.cos(pitchRadians);
        return new Vector3d(x, y, z);
    }

    public static Holder<EntityStore> generateItemDrop(ComponentAccessor<EntityStore> accessor, ItemStack itemStack, Vector3d position, float velocityX, float velocityY, float velocityZ) {
        Method method = HytaleCompat.findMethod(ItemComponent.class, "generateItemDrop", 7);
        if (method == null) {
            throw new IllegalStateException("Unable to resolve ItemComponent.generateItemDrop");
        }
        Object adaptedPosition = HytaleCompat.adaptVector(position, method.getParameterTypes()[2]);
        Object adaptedRotation = HytaleCompat.zeroRotationFor(method.getParameterTypes()[3]);
        return (Holder)HytaleCompat.invokeStatic(method, accessor, itemStack, adaptedPosition, adaptedRotation, Float.valueOf(velocityX), Float.valueOf(velocityY), Float.valueOf(velocityZ));
    }

    public static Holder<EntityStore>[] generateItemDrops(ComponentAccessor<EntityStore> accessor, List<ItemStack> drops, Vector3d position) {
        Method method = HytaleCompat.findMethod(ItemComponent.class, "generateItemDrops", 4);
        if (method == null) {
            throw new IllegalStateException("Unable to resolve ItemComponent.generateItemDrops");
        }
        Object adaptedPosition = HytaleCompat.adaptVector(position, method.getParameterTypes()[2]);
        Object adaptedRotation = HytaleCompat.zeroRotationFor(method.getParameterTypes()[3]);
        return (Holder[])HytaleCompat.invokeStatic(method, accessor, drops, adaptedPosition, adaptedRotation);
    }

    public static Holder<EntityStore> generatePickedUpItem(ItemStack itemStack, Vector3d origin, ComponentAccessor<EntityStore> accessor, Ref<EntityStore> ref) {
        Method method = HytaleCompat.findItemStackMethod("generatePickedUpItem");
        if (method == null) {
            throw new IllegalStateException("Unable to resolve ItemComponent.generatePickedUpItem(ItemStack, ...)");
        }
        Object adaptedOrigin = HytaleCompat.adaptVector(origin, method.getParameterTypes()[1]);
        return (Holder)HytaleCompat.invokeStatic(method, itemStack, adaptedOrigin, accessor, ref);
    }

    public static void notifyPickupItem(Object playerComponent, Ref<EntityStore> ref, ItemStack itemStack, Vector3d origin, ComponentAccessor<EntityStore> accessor) {
        Method method = HytaleCompat.findMethod(playerComponent.getClass(), "notifyPickupItem", 4);
        if (method == null) {
            throw new IllegalStateException("Unable to resolve notifyPickupItem on " + playerComponent.getClass().getName());
        }
        Object adaptedOrigin = origin == null ? null : HytaleCompat.adaptVector(origin, method.getParameterTypes()[2]);
        HytaleCompat.invokeInstance(method, playerComponent, ref, itemStack, adaptedOrigin, accessor);
    }

    public static void setTransformPosition(TransformComponent transformComponent, Vector3d position) {
        Method method = HytaleCompat.findMethod(transformComponent.getClass(), "setPosition", 1);
        if (method == null) {
            throw new IllegalStateException("Unable to resolve TransformComponent.setPosition");
        }
        HytaleCompat.invokeInstance(method, transformComponent, HytaleCompat.adaptVector(position, method.getParameterTypes()[0]));
    }

    public static TransformComponent newTransformComponent(Vector3d position) {
        for (Constructor<?> constructor : TransformComponent.class.getConstructors()) {
            if (constructor.getParameterCount() != 2) continue;
            Object adaptedPosition = HytaleCompat.adaptVector(position, constructor.getParameterTypes()[0]);
            Object adaptedRotation = HytaleCompat.zeroRotationFor(constructor.getParameterTypes()[1]);
            if (adaptedPosition == null || adaptedRotation == null) continue;
            try {
                return (TransformComponent)constructor.newInstance(adaptedPosition, adaptedRotation);
            }
            catch (ReflectiveOperationException reflectiveOperationException) {
                // empty catch block
            }
        }
        throw new IllegalStateException("Unable to resolve TransformComponent(Vector3, Rotation) constructor");
    }

    public static void collectBox(Object spatialStructure, Vector3d min, Vector3d max, Collection<?> results) {
        Method method = HytaleCompat.findMethod(spatialStructure.getClass(), "collectBox", 3);
        if (method == null) {
            throw new IllegalStateException("Unable to resolve collectBox on " + spatialStructure.getClass().getName());
        }
        HytaleCompat.invokeInstance(method, spatialStructure, HytaleCompat.adaptVector(min, method.getParameterTypes()[0]), HytaleCompat.adaptVector(max, method.getParameterTypes()[1]), results);
    }

    public static void collectCylinder(Object spatialStructure, Vector3d center, double radius, double height, Collection<?> results) {
        Method method = HytaleCompat.findMethod(spatialStructure.getClass(), "collectCylinder", 4);
        if (method == null) {
            throw new IllegalStateException("Unable to resolve collectCylinder on " + spatialStructure.getClass().getName());
        }
        HytaleCompat.invokeInstance(method, spatialStructure, HytaleCompat.adaptVector(center, method.getParameterTypes()[0]), radius, height, results);
    }

    private static Method findItemStackMethod(String name) {
        for (Method method : ItemComponent.class.getMethods()) {
            if (!method.getName().equals(name) || method.getParameterCount() != 4 || !method.getParameterTypes()[0].isAssignableFrom(ItemStack.class)) continue;
            return method;
        }
        return null;
    }

    private static Method findMethod(Class<?> type, String name, int parameterCount) {
        for (Method method : type.getMethods()) {
            if (!method.getName().equals(name) || method.getParameterCount() != parameterCount) continue;
            return method;
        }
        return null;
    }

    private static Object adaptVector(Vector3d vector, Class<?> targetType) {
        if (targetType.isInstance(vector)) {
            return vector;
        }
        String targetName = targetType.getName();
        try {
            if ("org.joml.Vector3d".equals(targetName) || "org.joml.Vector3dc".equals(targetName)) {
                return new Vector3d(vector);
            }
            if ("com.hypixel.hytale.math.vector.Vector3d".equals(targetName)) {
                Constructor<?> constructor = targetType.getConstructor(Double.TYPE, Double.TYPE, Double.TYPE);
                return constructor.newInstance(vector.x, vector.y, vector.z);
            }
        }
        catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to adapt Vector3d to " + targetName, exception);
        }
        throw new IllegalArgumentException("Unsupported vector target type: " + targetName);
    }

    private static Object zeroRotationFor(Class<?> targetType) {
        try {
            Field zeroField = targetType.getField("ZERO");
            return zeroField.get(null);
        }
        catch (ReflectiveOperationException zeroField) {
            String targetName = targetType.getName();
            try {
                if ("com.hypixel.hytale.math.vector.Vector3f".equals(targetName)) {
                    Constructor<?> constructor = targetType.getConstructor(Float.TYPE, Float.TYPE, Float.TYPE);
                    return constructor.newInstance(Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(0.0f));
                }
                if ("com.hypixel.hytale.math.vector.Rotation3f".equals(targetName)) {
                    Constructor<?> constructor = targetType.getConstructor(Float.TYPE, Float.TYPE, Float.TYPE);
                    return constructor.newInstance(Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(0.0f));
                }
            }
            catch (ReflectiveOperationException exception) {
                throw new IllegalStateException("Unable to create zero rotation for " + targetName, exception);
            }
            throw new IllegalArgumentException("Unsupported rotation target type: " + targetName);
        }
    }

    private static <T> T invokeStatic(Method method, Object ... args) {
        try {
            return (T)method.invoke(null, args);
        }
        catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to invoke " + String.valueOf(method), exception);
        }
    }

    private static void invokeInstance(Method method, Object instance, Object ... args) {
        try {
            method.invoke(instance, args);
        }
        catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to invoke " + String.valueOf(method), exception);
        }
    }

    private static Object invokeNoArgs(Object instance, String methodName) {
        try {
            Method method = instance.getClass().getMethod(methodName, new Class[0]);
            return method.invoke(instance, new Object[0]);
        }
        catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static Object readField(Object instance, String fieldName) {
        try {
            Field field = instance.getClass().getField(fieldName);
            return field.get(instance);
        }
        catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static float angle(Object value, String primaryMethod, String secondaryMethod, String fallbackField) {
        Object direct = HytaleCompat.invokeNoArgs(value, primaryMethod);
        if (direct instanceof Number) {
            Number number = (Number)direct;
            return number.floatValue();
        }
        Object legacy = HytaleCompat.invokeNoArgs(value, secondaryMethod);
        if (legacy instanceof Number) {
            Number number = (Number)legacy;
            return number.floatValue();
        }
        Object field = HytaleCompat.readField(value, fallbackField);
        if (field instanceof Number) {
            Number number = (Number)field;
            return number.floatValue();
        }
        throw new IllegalArgumentException("Unable to read rotation angle from " + value.getClass().getName());
    }
}

