/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.AddReason
 *  com.hypixel.hytale.component.CommandBuffer
 *  com.hypixel.hytale.component.ComponentAccessor
 *  com.hypixel.hytale.component.Holder
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.math.vector.Vector3d
 *  com.hypixel.hytale.math.vector.Vector3f
 *  com.hypixel.hytale.protocol.Rotation
 *  com.hypixel.hytale.server.core.inventory.ItemStack
 *  com.hypixel.hytale.server.core.modules.entity.item.ItemComponent
 *  com.hypixel.hytale.server.core.universe.world.World
 *  com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockPatternRule$AdjacentSide
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nonnull
 */
package org.Ev0Mods.plugin.api.util;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.Rotation;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockPatternRule;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.List;
import javax.annotation.Nonnull;

public class WorldHelper {
    public static ConnectedBlockPatternRule.AdjacentSide rotate(ConnectedBlockPatternRule.AdjacentSide side, int rotation) {
        return switch (side) {
            default -> throw new MatchException(null, null);
            case ConnectedBlockPatternRule.AdjacentSide.North, ConnectedBlockPatternRule.AdjacentSide.East, ConnectedBlockPatternRule.AdjacentSide.South, ConnectedBlockPatternRule.AdjacentSide.West -> WorldHelper.side(Math.floorMod(side.ordinal() - 2 - rotation, 4) + 2);
            case ConnectedBlockPatternRule.AdjacentSide.Up, ConnectedBlockPatternRule.AdjacentSide.Down -> side;
        };
    }

    public static ConnectedBlockPatternRule.AdjacentSide rotate(ConnectedBlockPatternRule.AdjacentSide side, Rotation rotation) {
        return WorldHelper.rotate(side, rotation.ordinal());
    }

    public static ConnectedBlockPatternRule.AdjacentSide side(int ordinal) {
        if (ordinal < 0 || ordinal > 5) {
            throw new IllegalArgumentException("AdjacentSide ordinal must be between 0 and 5. Received '" + ordinal + "'!");
        }
        return ConnectedBlockPatternRule.AdjacentSide.values()[ordinal];
    }

    public static Rotation rotation(int ordinal) {
        if (ordinal < 0 || ordinal > 3) {
            throw new IllegalArgumentException("Rotation ordinal must be between 0 and 3. Received '" + ordinal + "'!");
        }
        return Rotation.values()[ordinal];
    }

    public static void dropItems(World world, List<ItemStack> drops, Vector3d position) {
        Store store = world.getEntityStore().getStore();
        if (store != null && !drops.isEmpty()) {
            Holder[] dropHolder = ItemComponent.generateItemDrops((ComponentAccessor)store, drops, (Vector3d)position.clone(), (Vector3f)Vector3f.ZERO.clone());
            store.addEntities(dropHolder, AddReason.SPAWN);
        }
    }

    public static void dropItems(List<ItemStack> drops, Vector3d position, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        if (!drops.isEmpty()) {
            Holder[] dropHolder = ItemComponent.generateItemDrops(store, drops, (Vector3d)position.clone(), (Vector3f)Vector3f.ZERO.clone());
            commandBuffer.addEntities(dropHolder, AddReason.SPAWN);
        }
    }
}

