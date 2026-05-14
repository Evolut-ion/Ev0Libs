/*
 * Decompiled with CFR 0.152.
 */
package org.Ev0Mods.plugin.api.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.Ev0Mods.plugin.api.system.WirelessRegistry;

public class WirelessRegistryStartupSystem
extends EntityTickingSystem<ChunkStore> {
    private static final Set<World> PROCESSED = Collections.newSetFromMap(new ConcurrentHashMap());

    @Override
    public void tick(float dt, int index, ArchetypeChunk<ChunkStore> archetypeChunk, Store<ChunkStore> store, CommandBuffer<ChunkStore> commandBuffer) {
        try {
            World world = store.getExternalData().getWorld();
            if (world == null) {
                return;
            }
            if (PROCESSED.contains(world)) {
                return;
            }
            PROCESSED.add(world);
            try {
                WirelessRegistry.pruneForWorld(world);
            }
            catch (Throwable throwable) {}
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    @Override
    public Query<ChunkStore> getQuery() {
        return Query.any();
    }
}

