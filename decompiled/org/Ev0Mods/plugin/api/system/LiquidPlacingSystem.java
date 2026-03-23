/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.builtin.blocktick.system.ChunkBlockTickSystem$Ticking
 *  com.hypixel.hytale.builtin.fluid.FluidSystems$Ticking
 *  com.hypixel.hytale.component.ArchetypeChunk
 *  com.hypixel.hytale.component.CommandBuffer
 *  com.hypixel.hytale.component.ComponentType
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.component.dependency.Dependency
 *  com.hypixel.hytale.component.dependency.Order
 *  com.hypixel.hytale.component.dependency.SystemDependency
 *  com.hypixel.hytale.component.query.Query
 *  com.hypixel.hytale.component.system.tick.EntityTickingSystem
 *  com.hypixel.hytale.math.util.ChunkUtil
 *  com.hypixel.hytale.math.vector.Vector2i
 *  com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy
 *  com.hypixel.hytale.server.core.asset.type.fluid.Fluid
 *  com.hypixel.hytale.server.core.asset.type.fluid.FluidTicker$CachedAccessor
 *  com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk
 *  com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection
 *  com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection
 *  com.hypixel.hytale.server.core.universe.world.chunk.section.FluidSection
 *  com.hypixel.hytale.server.core.universe.world.storage.ChunkStore
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nonnull
 */
package org.Ev0Mods.plugin.api.system;

import com.hypixel.hytale.builtin.blocktick.system.ChunkBlockTickSystem;
import com.hypixel.hytale.builtin.fluid.FluidSystems;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.asset.type.fluid.FluidTicker;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.FluidSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Set;
import javax.annotation.Nonnull;
import org.Ev0Mods.plugin.api.component.FluidComponent;

public class LiquidPlacingSystem
extends EntityTickingSystem<ChunkStore> {
    private ComponentType<EntityStore, FluidComponent> fluidComponentComponentType;
    @Nonnull
    private static final Query<ChunkStore> QUERY = Query.and((Query[])new Query[]{FluidSection.getComponentType(), ChunkSection.getComponentType()});
    @Nonnull
    private static final Set<Dependency<ChunkStore>> DEPENDENCIES = Set.of(new SystemDependency(Order.AFTER, FluidSystems.Ticking.class), new SystemDependency(Order.BEFORE, ChunkBlockTickSystem.Ticking.class));

    public LiquidPlacingSystem() {
        this.fluidComponentComponentType = this.fluidComponentComponentType = null;
    }

    public LiquidPlacingSystem(ComponentType<EntityStore, FluidComponent> fluidComponent) {
        this.fluidComponentComponentType = null;
        this.fluidComponentComponentType = fluidComponent;
    }

    public void tick(float dt, int index, @Nonnull ArchetypeChunk<ChunkStore> archetypeChunk, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
        ChunkSection chunkSectionComponent = (ChunkSection)archetypeChunk.getComponent(index, ChunkSection.getComponentType());
        assert (chunkSectionComponent != null);
        FluidSection fluidSectionComponent = (FluidSection)archetypeChunk.getComponent(index, FluidSection.getComponentType());
        assert (fluidSectionComponent != null);
        Ref chunkRef = chunkSectionComponent.getChunkColumnReference();
        BlockChunk blockChunkComponent = (BlockChunk)commandBuffer.getComponent(chunkRef, BlockChunk.getComponentType());
        assert (blockChunkComponent != null);
        BlockSection blockSection = blockChunkComponent.getSectionAtIndex(fluidSectionComponent.getY());
        if (blockSection != null && blockSection.getTickingBlocksCountCopy() != 0) {
            FluidTicker.CachedAccessor accessor = FluidTicker.CachedAccessor.of(commandBuffer, (FluidSection)fluidSectionComponent, (BlockSection)blockSection, (int)5);
            blockSection.forEachTicking((Object)accessor, commandBuffer, fluidSectionComponent.getY(), (accessor1, commandBuffer1, x, y, z, block) -> {
                FluidSection fluidSection = accessor1.selfFluidSection;
                BlockSection blockSectionInner = accessor1.selfBlockSection;
                int fluidId = fluidSection.getFluidId(x, y, z);
                if (fluidId == 0) {
                    return BlockTickStrategy.IGNORED;
                }
                Fluid fluid = (Fluid)Fluid.getAssetMap().getAsset(fluidId);
                if (fluid != null && fluid.getId().equals("Water_Source")) {
                    int worldX = fluidSection.getX() << 5 | x;
                    int worldZ = fluidSection.getZ() << 5 | z;
                    for (Vector2i offset : new Vector2i[]{new Vector2i(-1, 0), new Vector2i(1, 0), new Vector2i(0, -1), new Vector2i(0, 1)}) {
                        BlockSection targetBlockSection;
                        int blockX = offset.x + worldX;
                        int blockZ = offset.y + worldZ;
                        boolean isDifferentSection = !ChunkUtil.isSameChunkSection((int)worldX, (int)y, (int)worldZ, (int)blockX, (int)y, (int)blockZ);
                        FluidSection targetFluidSection = isDifferentSection ? accessor.getFluidSectionByBlock(blockX, y, blockZ) : fluidSection;
                        BlockSection blockSection = targetBlockSection = isDifferentSection ? accessor.getBlockSectionByBlock(blockX, y, blockZ) : blockSectionInner;
                        if (targetBlockSection == null) {
                            return BlockTickStrategy.WAIT_FOR_ADJACENT_CHUNK_LOAD;
                        }
                        int fluidIdTarget = targetFluidSection.getFluidId(blockX, y, blockZ);
                        Fluid fluidTarget = (Fluid)Fluid.getAssetMap().getAsset(fluidIdTarget);
                        if (fluidTarget != null && !fluidTarget.getId().equals("Empty") && !fluidTarget.getId().equals("Water")) continue;
                        int waterSourceCount = 0;
                        for (Vector2i offsetInner : new Vector2i[]{new Vector2i(-1, 0), new Vector2i(1, 0), new Vector2i(0, -1), new Vector2i(0, 1)}) {
                            BlockSection checkBlockSection;
                            int checkBlockX = offsetInner.x + blockX;
                            int checkBlockZ = offsetInner.y + blockZ;
                            boolean isDifferentSectionInner = !ChunkUtil.isSameChunkSection((int)worldX, (int)y, (int)worldZ, (int)checkBlockX, (int)y, (int)checkBlockZ);
                            FluidSection checkFluidSection = isDifferentSectionInner ? accessor.getFluidSectionByBlock(checkBlockX, y, checkBlockZ) : fluidSection;
                            BlockSection blockSection2 = checkBlockSection = isDifferentSectionInner ? accessor.getBlockSectionByBlock(checkBlockX, y, checkBlockZ) : blockSectionInner;
                            if (checkFluidSection == null) {
                                return BlockTickStrategy.WAIT_FOR_ADJACENT_CHUNK_LOAD;
                            }
                            int fluidIdCheck = checkFluidSection.getFluidId(checkBlockX, y, checkBlockZ);
                            Fluid fluidCheck = (Fluid)Fluid.getAssetMap().getAsset(fluidIdCheck);
                            if (fluidCheck == null || !fluidCheck.getId().equals("Water_Source")) continue;
                            ++waterSourceCount;
                        }
                    }
                    return BlockTickStrategy.SLEEP;
                }
                return BlockTickStrategy.IGNORED;
            });
        }
    }

    @Nonnull
    public Query<ChunkStore> getQuery() {
        return QUERY;
    }

    @Nonnull
    public Set<Dependency<ChunkStore>> getDependencies() {
        return DEPENDENCIES;
    }
}

