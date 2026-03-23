/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.Component
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nullable
 */
package org.Ev0Mods.plugin.api.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nullable;

public class FluidComponent
implements Component<EntityStore> {
    private float tickInterval;
    private int remainingTicks;
    private float elapsedTime;

    public FluidComponent() {
        this(5.0f, 1.0f, 10);
    }

    public FluidComponent(float damagePerTick, float tickInterval, int totalTicks) {
        this.tickInterval = tickInterval;
        this.remainingTicks = totalTicks;
        this.elapsedTime = 0.0f;
    }

    public FluidComponent(FluidComponent other) {
        this.tickInterval = other.tickInterval;
        this.remainingTicks = other.remainingTicks;
        this.elapsedTime = other.elapsedTime;
    }

    @Nullable
    public Component<EntityStore> clone() {
        return new FluidComponent(this).clone();
    }

    public float getTickInterval() {
        return this.tickInterval;
    }

    public int getRemainingTicks() {
        return this.remainingTicks;
    }

    public float getElapsedTime() {
        return this.elapsedTime;
    }

    public void addElapsedTime(float dt) {
        this.elapsedTime += dt;
    }

    public void resetElapsedTime() {
        this.elapsedTime = 0.0f;
    }

    public void decrementRemainingTicks() {
        --this.remainingTicks;
    }

    public boolean isExpired() {
        return this.remainingTicks <= 0;
    }
}

