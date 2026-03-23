/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.codec.builder.BuilderCodec
 *  com.hypixel.hytale.component.CommandBuffer
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.math.util.ChunkUtil
 *  com.hypixel.hytale.math.vector.Vector3i
 *  com.hypixel.hytale.protocol.BlockPosition
 *  com.hypixel.hytale.protocol.InteractionType
 *  com.hypixel.hytale.server.core.asset.type.item.config.Item
 *  com.hypixel.hytale.server.core.entity.InteractionContext
 *  com.hypixel.hytale.server.core.inventory.ItemStack
 *  com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler
 *  com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  com.hypixel.hytale.server.core.universe.world.World
 *  com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  org.checkerframework.checker.nullness.compatqual.NonNullDecl
 *  org.checkerframework.checker.nullness.compatqual.NullableDecl
 */
package org.Ev0Mods.plugin.api.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Random;
import org.Ev0Mods.plugin.api.block.state.HopperProcessor;
import org.Ev0Mods.plugin.api.component.CompatAdapters;
import org.Ev0Mods.plugin.api.component.EngineCompat;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class WrenchInteraction
extends SimpleBlockInteraction {
    public static final BuilderCodec<WrenchInteraction> CODEC = BuilderCodec.builder(WrenchInteraction.class, WrenchInteraction::new, (BuilderCodec)SimpleBlockInteraction.CODEC).build();
    public Random r = new Random();

    protected void interactWithBlock(@NonNullDecl World world, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl Vector3i vector3i, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref playerEnt = interactionContext.getOwningEntity();
        playerEnt.getStore().getComponent(playerEnt, PlayerRef.getComponentType());
        BlockPosition contextTargetBlock = interactionContext.getTargetBlock();
        assert (interactionContext.getHeldItem() != null);
        assert (contextTargetBlock != null);
        WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock((int)contextTargetBlock.x, (int)contextTargetBlock.z));
        assert (chunk != null);
        Object var11 = EngineCompat.getState(chunk, contextTargetBlock.x, contextTargetBlock.y, contextTargetBlock.z);
        if (var11 instanceof HopperProcessor) {
            HopperProcessor c = (HopperProcessor)var11;
            HopperProcessor cx = (HopperProcessor)EngineCompat.getState(chunk, contextTargetBlock.x, contextTargetBlock.y, contextTargetBlock.z);
            String[] substitutions = null;
            try {
                Object o = CompatAdapters.readStateFieldOrComponent(c, (Object)c.data, "substitutions", "ev0s:hopper", "substitutions");
                if (o instanceof String[]) {
                    substitutions = (String[])o;
                }
            }
            catch (Throwable o) {
                // empty catch block
            }
            if (substitutions == null || substitutions.length == 0) {
                return;
            }
            for (int v = 0; v < substitutions.length; ++v) {
                ItemStack is = new ItemStack(substitutions[this.r.nextInt(substitutions.length)]);
                Item i = is.getItem();
                String blockKey = is.getBlockKey();
                if (i.getBlockId() == null) continue;
                EngineCompat.setBlock(chunk, contextTargetBlock.x, contextTargetBlock.y, contextTargetBlock.z, blockKey);
            }
        }
    }

    protected void simulateInteractWithBlock(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl World world, @NonNullDecl Vector3i vector3i) {
    }
}

