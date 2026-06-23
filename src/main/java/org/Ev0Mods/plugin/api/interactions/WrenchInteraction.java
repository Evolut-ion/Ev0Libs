/*
 * Decompiled with CFR 0.152.
 */
package org.Ev0Mods.plugin.api.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.joml.Vector3i;
import org.Ev0Mods.plugin.api.Ev0Log;
import org.Ev0Mods.plugin.api.component.EngineCompat;
import org.Ev0Mods.plugin.api.util.WirelessHelpers;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class WrenchInteraction
extends SimpleBlockInteraction {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public static final BuilderCodec<WrenchInteraction> CODEC = BuilderCodec.builder(WrenchInteraction.class, WrenchInteraction::new, SimpleBlockInteraction.CODEC).build();

    protected void interactWithBlock(@NonNullDecl World world, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl Vector3i pos, @NonNullDecl CooldownHandler cooldownHandler) {
        Ev0Log.info(LOGGER, "[Wrench] interactWithBlock called at pos=" + pos + " interactionType=" + interactionType);

        WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
        if (chunk == null) {
            Ev0Log.warn(LOGGER, "[Wrench] chunk not in memory at pos=" + pos);
            return;
        }

        String[] substitutions = WirelessHelpers.getSubstitutions(world, pos.x, pos.y, pos.z);
        Ev0Log.info(LOGGER, "[Wrench] substitutions=" + (substitutions == null ? "null" : java.util.Arrays.toString(substitutions)));
        if (substitutions == null || substitutions.length == 0) {
            Ev0Log.warn(LOGGER, "[Wrench] no substitutions found at pos=" + pos);
            return;
        }

        String currentId = EngineCompat.getBlockId(chunk, pos.x, pos.y, pos.z);
        Ev0Log.info(LOGGER, "[Wrench] currentId=" + currentId);

        int currentIdx = substitutions.length - 1;
        if (currentId != null) {
            for (int i = 0; i < substitutions.length; i++) {
                if (substitutions[i] != null && currentId.equalsIgnoreCase(substitutions[i])) {
                    currentIdx = i;
                    break;
                }
            }
        }

        int nextIdx = (currentIdx + 1) % substitutions.length;
        String nextKey = substitutions[nextIdx];
        Ev0Log.info(LOGGER, "[Wrench] currentIdx=" + currentIdx + " nextIdx=" + nextIdx + " nextKey=" + nextKey);

        if (nextKey == null || nextKey.isEmpty()) {
            Ev0Log.warn(LOGGER, "[Wrench] nextKey is null or empty");
            return;
        }

        boolean placed = false;
        Throwable placeError = null;
        try {
            placed = EngineCompat.setBlock(chunk, pos.x, pos.y, pos.z, nextKey);
        } catch (Throwable t) {
            placeError = t;
        }
        Ev0Log.info(LOGGER, "[Wrench] setBlock key=" + nextKey + " pos=" + pos + " result=" + placed + (placeError != null ? " error=" + placeError : ""));
    }

    protected void simulateInteractWithBlock(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl World world, @NonNullDecl Vector3i vector3i) {
    }
}
