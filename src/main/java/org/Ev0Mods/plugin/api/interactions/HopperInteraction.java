//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.Ev0Mods.plugin.api.interactions;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.PlaceBlockInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.Ev0Mods.plugin.api.block.state.HopperProcessor;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Random;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
@SuppressWarnings("removal")
public class HopperInteraction extends PlaceBlockInteraction {
    public static final BuilderCodec<HopperInteraction> CODEC;
    public Random r = new Random();

    protected void interactWithBlock(@NonNullDecl World world, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl Vector3i vector3i, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> playerEnt = interactionContext.getOwningEntity();
        playerEnt.getStore().getComponent(playerEnt, PlayerRef.getComponentType());

        BlockPosition contextTargetBlock = interactionContext.getTargetBlock();
        assert interactionContext.getHeldItem() != null;



        assert contextTargetBlock != null;

        WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(contextTargetBlock.x, contextTargetBlock.z));

        assert chunk != null;



    }

    protected void simulateInteractWithBlock(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl World world, @NonNullDecl Vector3i vector3i) {
    }

    static {
        CODEC = BuilderCodec.builder(HopperInteraction.class, HopperInteraction::new).build();
    }
}