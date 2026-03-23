package com.hypixel.hytale.server.core.universe.world.chunk.state;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.math.vector.Vector3i;

import javax.annotation.Nullable;

/** Minimal local stub to allow compiling against engine-less classpath.
 *  At runtime the engine's interface will be present; this stub matches
 *  the methods used by the plugin.
 */
public interface TickableBlockState {
    void tick(float dt, int index, ArchetypeChunk<ChunkStore> archeChunk,
              Store<ChunkStore> store, CommandBuffer<ChunkStore> commandBuffer);

    @Nullable
    WorldChunk getChunk();

    Vector3i getPosition();

    void invalidate();
}
