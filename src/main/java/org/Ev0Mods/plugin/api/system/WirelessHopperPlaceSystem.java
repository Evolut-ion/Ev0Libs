/*
 * Decompiled with CFR 0.152.
 */
package org.Ev0Mods.plugin.api.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockPatternRule;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.concurrent.ConcurrentHashMap;
import org.Ev0Mods.plugin.api.compat.HytaleCompat;

public class WirelessHopperPlaceSystem
extends EntityEventSystem<EntityStore, PlaceBlockEvent> {
    public static final ConcurrentHashMap<Vector3i, ConnectedBlockPatternRule.AdjacentSide> PENDING_PLACEMENT_FACES = new ConcurrentHashMap();
    public static final ConcurrentHashMap<Vector3i, Long> PENDING_TARGET_BLOCKS = new ConcurrentHashMap();
    private static final long EXPIRY_MS = 5000L;

    public WirelessHopperPlaceSystem() {
        super(PlaceBlockEvent.class);
    }

    @Override
    public void handle(int index, ArchetypeChunk<EntityStore> chunk, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer, PlaceBlockEvent event) {
        Object rawTarget;
        try {
            rawTarget = event.getClass().getMethod("getTargetBlock", new Class[0]).invoke((Object)event, new Object[0]);
        }
        catch (ReflectiveOperationException e2) {
            return;
        }
        Vector3i key = new Vector3i(HytaleCompat.intCoord(rawTarget, "x"), HytaleCompat.intCoord(rawTarget, "y"), HytaleCompat.intCoord(rawTarget, "z"));
        long now = System.currentTimeMillis();
        PENDING_TARGET_BLOCKS.put(key, now);
        PENDING_PLACEMENT_FACES.put(key, ConnectedBlockPatternRule.AdjacentSide.Up);
        PENDING_TARGET_BLOCKS.entrySet().removeIf(e -> now - (Long)e.getValue() > 5000L);
        PENDING_PLACEMENT_FACES.keySet().removeIf(k -> !PENDING_TARGET_BLOCKS.containsKey(k));
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }
}

