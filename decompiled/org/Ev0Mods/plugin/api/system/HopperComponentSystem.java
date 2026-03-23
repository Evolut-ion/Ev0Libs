/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.builtin.blocktick.system.ChunkBlockTickSystem$Ticking
 *  com.hypixel.hytale.builtin.fluid.FluidSystems$Ticking
 *  com.hypixel.hytale.component.ArchetypeChunk
 *  com.hypixel.hytale.component.CommandBuffer
 *  com.hypixel.hytale.component.ComponentType
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.component.dependency.Dependency
 *  com.hypixel.hytale.component.dependency.Order
 *  com.hypixel.hytale.component.dependency.SystemDependency
 *  com.hypixel.hytale.component.query.Query
 *  com.hypixel.hytale.component.system.tick.EntityTickingSystem
 *  com.hypixel.hytale.logger.HytaleLogger
 *  com.hypixel.hytale.server.core.universe.world.storage.ChunkStore
 *  javax.annotation.Nonnull
 */
package org.Ev0Mods.plugin.api.system;

import com.hypixel.hytale.builtin.blocktick.system.ChunkBlockTickSystem;
import com.hypixel.hytale.builtin.fluid.FluidSystems;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import java.util.Set;
import javax.annotation.Nonnull;
import org.Ev0Mods.plugin.api.Ev0Log;
import org.Ev0Mods.plugin.api.component.HopperComponent;

public class HopperComponentSystem
extends EntityTickingSystem<ChunkStore> {
    @Nonnull
    private final Query<ChunkStore> query;
    @Nonnull
    private static final Set<Dependency<ChunkStore>> DEPENDENCIES = Set.of(new SystemDependency(Order.AFTER, FluidSystems.Ticking.class), new SystemDependency(Order.BEFORE, ChunkBlockTickSystem.Ticking.class));
    private final ComponentType<ChunkStore, HopperComponent> hopperComponentType;

    public HopperComponentSystem(ComponentType<ChunkStore, HopperComponent> hopperComponentType) {
        this.hopperComponentType = hopperComponentType;
        this.query = hopperComponentType;
    }

    public void tick(float dt, int index, @Nonnull ArchetypeChunk<ChunkStore> archetypeChunk, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
        block5: {
            if (this.hopperComponentType == null) {
                return;
            }
            try {
                HopperComponent hc = (HopperComponent)archetypeChunk.getComponent(index, this.hopperComponentType);
                if (hc == null) break block5;
                try {
                    Ev0Log.info(HytaleLogger.getLogger(), "[Ev0Lib] HopperComponentSystem.tick -> invoking HopperComponent.tick for index=" + index);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                hc.tick(dt, index, archetypeChunk, store, commandBuffer);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    @Nonnull
    public Query<ChunkStore> getQuery() {
        return this.query;
    }

    @Nonnull
    public Set<Dependency<ChunkStore>> getDependencies() {
        return DEPENDENCIES;
    }
}

