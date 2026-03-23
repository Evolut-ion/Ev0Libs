package org.Ev0Mods.plugin.api.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.Ev0Mods.plugin.api.component.HopperComponent;
import org.Ev0Mods.plugin.Ev0Lib;
import org.Ev0Mods.plugin.api.Ev0Log;
import com.hypixel.hytale.logger.HytaleLogger;

import javax.annotation.Nonnull;
import java.util.Set;

public class HopperComponentSystem extends EntityTickingSystem<ChunkStore> {
    // Instance-level query: matches block entities that have HopperComponent attached.
    // ComponentType<ECS,T> implements Query<ECS>, so hopperComponentType is a valid Query
    // that selects any archetype containing HopperComponent.
    @Nonnull
    private final Query<ChunkStore> query;
    @Nonnull
    private static final Set<Dependency<ChunkStore>> DEPENDENCIES = Set.of(
            new SystemDependency<>(Order.AFTER, com.hypixel.hytale.builtin.fluid.FluidSystems.Ticking.class),
            new SystemDependency<>(Order.BEFORE, com.hypixel.hytale.builtin.blocktick.system.ChunkBlockTickSystem.Ticking.class)
    );

    private final com.hypixel.hytale.component.ComponentType<ChunkStore, HopperComponent> hopperComponentType;

    public HopperComponentSystem(com.hypixel.hytale.component.ComponentType<ChunkStore, HopperComponent> hopperComponentType) {
        this.hopperComponentType = hopperComponentType;
        // Use hopperComponentType directly as the query — it matches all entities whose
        // archetype includes HopperComponent (i.e., placed hopper block entities).
        this.query = hopperComponentType;
    }

    @Override
    public void tick(float dt, int index, @Nonnull ArchetypeChunk<ChunkStore> archetypeChunk, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
        if (hopperComponentType == null) return;
        try {
            HopperComponent hc = archetypeChunk.getComponent(index, hopperComponentType);
            if (hc != null) {
                try { Ev0Log.info(HytaleLogger.getLogger(), "[Ev0Lib] HopperComponentSystem.tick -> invoking HopperComponent.tick for index=" + index); } catch (Throwable ignored) {}
                hc.tick(dt, index, archetypeChunk, store, commandBuffer);
            }
        } catch (Throwable ignored) {}
    }

    @Nonnull
    @Override
    public Query<ChunkStore> getQuery() {
        return query;
    }

    @Nonnull
    @Override
    public Set<Dependency<ChunkStore>> getDependencies() {
        return DEPENDENCIES;
    }
}
