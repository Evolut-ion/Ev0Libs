/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.ArchetypeChunk
 *  com.hypixel.hytale.component.CommandBuffer
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.math.vector.Vector3i
 *  com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk
 *  com.hypixel.hytale.server.core.universe.world.storage.ChunkStore
 *  javax.annotation.Nullable
 */
package com.hypixel.hytale.server.core.universe.world.chunk.state;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import javax.annotation.Nullable;

public interface TickableBlockState {
    public void tick(float var1, int var2, ArchetypeChunk<ChunkStore> var3, Store<ChunkStore> var4, CommandBuffer<ChunkStore> var5);

    @Nullable
    public WorldChunk getChunk();

    public Vector3i getPosition();

    public void invalidate();
}

