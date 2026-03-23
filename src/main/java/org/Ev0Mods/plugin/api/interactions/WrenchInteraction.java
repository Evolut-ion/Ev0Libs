//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.Ev0Mods.plugin.api.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.protocol.PlaceBlockInteraction;

import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
// BlockState moved/removed in prerelease; not required here (use EngineCompat)
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.Ev0Mods.plugin.api.block.state.HopperProcessor;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Random;

@SuppressWarnings("removal")
public class WrenchInteraction extends SimpleBlockInteraction {
    public static final BuilderCodec<WrenchInteraction> CODEC;
    public Random r = new Random();

    protected void interactWithBlock(@NonNullDecl World world, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl Vector3i vector3i, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> playerEnt = interactionContext.getOwningEntity();
        playerEnt.getStore().getComponent(playerEnt, PlayerRef.getComponentType());

        BlockPosition contextTargetBlock = interactionContext.getTargetBlock();
        assert interactionContext.getHeldItem() != null;


        assert contextTargetBlock != null;

        WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(contextTargetBlock.x, contextTargetBlock.z));

        assert chunk != null;
        Object var11 = org.Ev0Mods.plugin.api.component.EngineCompat.getState(chunk, contextTargetBlock.x, contextTargetBlock.y, contextTargetBlock.z);
        if (var11 instanceof HopperProcessor) {
            HopperProcessor c = (HopperProcessor)var11;
            HopperProcessor cx = (HopperProcessor)org.Ev0Mods.plugin.api.component.EngineCompat.getState(chunk, contextTargetBlock.x, contextTargetBlock.y, contextTargetBlock.z);
            // Prefer new ComponentMap storage, fall back to legacy StateData field
            String[] substitutions = null;
            try {
                Object o = org.Ev0Mods.plugin.api.component.CompatAdapters.readStateFieldOrComponent(c, c.data, "substitutions", "ev0s:hopper", "substitutions");
                if (o instanceof String[]) substitutions = (String[]) o;
            } catch (Throwable ignored) {
            }
            if (substitutions == null || substitutions.length == 0) return;
            for (int v = 0; v < substitutions.length; ++v) {
                ItemStack is = new ItemStack(substitutions[this.r.nextInt(substitutions.length)]);
                Item i = is.getItem();
                String blockKey = is.getBlockKey();
                if (i.getBlockId() != null) {
                    org.Ev0Mods.plugin.api.component.EngineCompat.setBlock(chunk, contextTargetBlock.x, contextTargetBlock.y, contextTargetBlock.z, blockKey);
                }
            }
        }



    }

    protected void simulateInteractWithBlock(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl World world, @NonNullDecl Vector3i vector3i) {
    }

    static {
        CODEC = BuilderCodec.builder(WrenchInteraction.class, WrenchInteraction::new, SimpleBlockInteraction.CODEC).build();
    }
}