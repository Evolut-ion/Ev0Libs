/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.ComponentAccessor
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 */
package org.Ev0Mods.plugin.api.util;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class EntityHelper {
    public static boolean isCrouching(ComponentAccessor<EntityStore> components, Ref<EntityStore> target) {
        MovementStatesComponent movement = (MovementStatesComponent)components.getComponent(target, MovementStatesComponent.getComponentType());
        return movement != null && movement.getMovementStates().crouching;
    }
}

