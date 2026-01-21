package org.Ev0Mods.plugin.api.block.state;


import com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.HashUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.ChangeVelocityType;
import com.hypixel.hytale.protocol.Rangef;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.StateData;

import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.modules.collision.CollisionModule;
import com.hypixel.hytale.server.core.modules.collision.CollisionResult;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.state.TickableBlockState;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockPatternRule;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerBlockState;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.Ev0Mods.plugin.api.codec.Codecs;
import org.Ev0Mods.plugin.api.codec.IdOutput;
import org.Ev0Mods.plugin.api.codec.ItemHandler;
import org.Ev0Mods.plugin.api.util.EntityHelper;
import org.Ev0Mods.plugin.api.util.WorldHelper;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockPatternRule.AdjacentSide;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;


@SuppressWarnings("removal")


public class ConveyorProcessor extends ProcessingBenchState implements TickableBlockState, ItemContainerBlockState {
    public Rangef duration = new Rangef(0,10);
    public static final BuilderCodec<ConveyorProcessor> CODEC = BuilderCodec.builder(ConveyorProcessor.class, ConveyorProcessor::new, BlockState.BASE_CODEC).append(new KeyedCodec<>("StartTime", Codec.INSTANT, true), (i, v) -> i.startTime = v, i -> i.startTime).add()
            .append(new KeyedCodec<>("Timer", Codec.DOUBLE, true), (i, v) -> i.timer = v, i -> i.timer).add().build();
    protected Instant startTime;
    protected double timer = -1;
    protected short outputSlot = 0  ;
    protected Data data;
    boolean is_valid = true;

    @Override
    public boolean initialize(BlockType blockType) {
        if (super.initialize(blockType) && blockType.getState() instanceof Data data) {
            this.data = data;
            return true;
        }
        return false;
    }

    @Override
    public void tick(float dt, int index, ArchetypeChunk<ChunkStore> archeChunk, Store<ChunkStore> store, CommandBuffer<ChunkStore> commandBuffer) {
        final World world = store.getExternalData().getWorld();
        if (this.getItemContainer().getContainer(2).getItemStack((short)0) != null) {
            //LOGGER.atInfo().log(Objects.requireNonNull(this.getItemContainer().getContainer(2).getItemStack((short)0).toString()));
        }
        final Store<EntityStore> entities = world.getEntityStore().getStore();
        final Instant currentTime = world.getEntityStore().getStore().getResource(WorldTimeResource.getResourceType()).getGameTime();
        final Vector3d forceVec = new Vector3d((1.5f) * data.force, (1.5f) * data.force, (1.5f) * data.force);
        if (startTime == null || startTime.isAfter(currentTime) || timer <= 0) {
            this.reset(currentTime);
        }else if (timer > 0 && Duration.between(startTime, currentTime).getSeconds() >= timer) {

            }
            this.reset(currentTime);
        final Vector3i generatorPos = this.getBlockPosition();
        final BlockPosition pos = world.getBaseBlock(new BlockPosition(generatorPos.x, generatorPos.y, generatorPos.z));
        for (ConnectedBlockPatternRule.AdjacentSide side : this.data.exportFaces) {
            //LOGGER.atInfo().log(side.name());
            boolean exportedItems = false;
            final Vector3i exportPos = new Vector3i(pos.x, pos.y, pos.z).add(WorldHelper.rotate(side, this.getRotationIndex()).relativePosition);
            final WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(exportPos.x, exportPos.z));
            //LOGGER.atInfo().log(chunk.toString());
            if (chunk != null && chunk.getState(exportPos.x, exportPos.y, exportPos.z) instanceof ItemContainerState containerState) {
                //for (ItemStack stack : this.data.output.outputList()) {
                //LOGGER.atInfo().log("Chunk Not Null" + exportPos.x + ", " +exportPos.y + ", " + exportPos.z);
                //LOGGER.atInfo().log(this.toString());

                if(this.getItemContainer().getContainer(2).getItemStack((short) 0) != null ){
                    //LOGGER.atInfo().log(this.getItemContainer().getContainer(2).getItemStack((short) 0).toString());
                    final ItemStackSlotTransaction transaction = containerState.getItemContainer().addItemStackToSlot((short)0, Objects.requireNonNull(this.getItemContainer().getContainer(2).getItemStack((short) 0).withQuantity(1)));
                    this.getItemContainer().removeItemStack(Objects.requireNonNull(this.getItemContainer().getContainer(2).getItemStack((short) 0).withQuantity(1)));
                    //LOGGER.atInfo().log(transaction.toString());
                    final ItemStack remainder = transaction.getRemainder();
                    if (transaction.succeeded() && (remainder == null || remainder.isEmpty())) {
                        exportedItems = true;
                    }
                }


                setBlockInteractionState("default", Objects.requireNonNull(getBlockType()));
                //}
            }
            setBlockInteractionState("default", Objects.requireNonNull(getBlockType()));
            if (this.data.exportOnce && exportedItems) {
                break;
            }
            setBlockInteractionState("default", getBlockType());
        }
        for (ConnectedBlockPatternRule.AdjacentSide side : this.data.importFaces){
            final Vector3i exportPos = new Vector3i(pos.x, pos.y, pos.z).add(WorldHelper.rotate(side, this.getRotationIndex()).relativePosition);
            final WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(exportPos.x, exportPos.z));
            ItemStackSlotTransaction transaction = null;
            if (chunk != null && chunk.getState(exportPos.x, exportPos.y, exportPos.z) instanceof ItemContainerState containerState) {

            for(int i = 0; i<containerState.getItemContainer().getCapacity()-1; i++){
                            if(this.getItemContainer().getContainer(0).getItemStack((short)0) != null){
                                if(this.getItemContainer().getContainer(0).getItemStack((short)0).getQuantity() <99){
                                    if(containerState.getItemContainer().getItemStack((short) i) != null) {
                                        transaction = this.getItemContainer().getContainer(0).addItemStackToSlot((short) 0, Objects.requireNonNull(containerState.getItemContainer().getItemStack((short) i).withQuantity(1)));
                                        Objects.requireNonNull(containerState.getItemContainer().removeItemStackFromSlot((short) 0, 1));
                                    }
                                }
                            }else {
                                if (containerState.getItemContainer().getItemStack((short)0) != null){
                                    if (this.getItemContainer().canAddItemStack(Objects.requireNonNull(Objects.requireNonNull(containerState.getItemContainer().getItemStack((short) i)).withQuantity(1))))
                                    {

                                        transaction = this.getItemContainer().getContainer(0).addItemStackToSlot((short) 0, Objects.requireNonNull(containerState.getItemContainer().getItemStack((short) i).withQuantity(1)));
                                        Objects.requireNonNull(containerState.getItemContainer().removeItemStackFromSlot((short) 0, 1));
                                    }//if (containerState.getItemContainer().getItemStack((short)i) != null) {
                                    //    if (containerState.getItemContainer().getItemStack((short) i).isEmpty()) {
                                    //        for (int n = 0; n < this.getItemContainer().getCapacity() - 1; n++) {
//
                                    //            LOGGER.atInfo().log(transaction.toString());
                                    //        }
                                    //    }
                                    //}
                                }
                            }
                        }

            }
        }
        setBlockInteractionState("default", Objects.requireNonNull(getBlockType()));
        super.tick(dt, index, archeChunk, store, commandBuffer);
        for (Ref<EntityStore> target : getAllEntitiesInBox(this.getBlockPosition(), data.height, entities, data.players, data.entities, data.items)) {
            final Player player = entities.getComponent(target, Player.getComponentType());
            if (player != null) {
                if (!EntityHelper.isCrouching(entities, target)) {
                    final Velocity velocity = entities.getComponent(target, Velocity.getComponentType());
                    if (velocity != null) {
                        velocity.addInstruction(forceVec.clone().scale(12), null, ChangeVelocityType.Set);
                    }
                }
            }
            else {
                final TransformComponent targetPos = entities.getComponent(target, TransformComponent.getComponentType());
                if (targetPos != null) {
                    final CollisionResult collision = new CollisionResult();
                    final Box boundingBox = entities.getComponent(target, BoundingBox.getComponentType()).getBoundingBox();
                    CollisionModule.findCollisions(boundingBox, targetPos.getPosition().clone(), forceVec.clone(), collision, entities);
                    if (collision.getFirstBlockCollision() == null) {
                        targetPos.getPosition().assign(targetPos.getPosition().add(forceVec.clone()));
                    }
                }
            }
        }
    }

    protected void reset(Instant currentTime) {
        startTime = currentTime;
        setBlockInteractionState("default", getBlockType());
        timer = data.duration.min + (data.duration.max - data.duration.min) * HashUtil.random(startTime.getEpochSecond(), this.getBlockX(), this.getBlockY(), this.getBlockZ());
    }
    @Nonnull
    public static List<Ref<EntityStore>> getAllEntitiesInBox(Vector3i pos, float height, @Nonnull ComponentAccessor<EntityStore> components, boolean players, boolean entities, boolean items) {
        final ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
        final Vector3d min = new Vector3d(pos.x, pos.y, pos.z);
        final Vector3d max = new Vector3d(pos.x + 1, pos.y + height, pos.z + 1);
        if (entities) {
            components.getResource(EntityModule.get().getEntitySpatialResourceType()).getSpatialStructure().collectBox(min, max, results);
        }
        if (players) {
            components.getResource(EntityModule.get().getPlayerSpatialResourceType()).getSpatialStructure().collectBox(min, max, results);
        }
        if (items) {
            components.getResource(EntityModule.get().getItemSpatialResourceType()).getSpatialStructure().collectBox(min, max, results);
        }
        return results;
    }
    public static class Data extends StateData {

        @Nonnull
        public static final BuilderCodec<Data> CODEC = BuilderCodec.builder(Data.class, Data::new, StateData.DEFAULT_CODEC)
                .append(new KeyedCodec<>("Force", Codec.FLOAT), (o, v) -> o.force = v, o -> o.force)
                .documentation("How much force to push the mob with.")
                .add()
                .append(new KeyedCodec<>("Height", Codec.FLOAT), (o, v) -> o.height = v, o -> o.height)
                .documentation("How high should the conveyor search?")
                .add()
                .append(new KeyedCodec<>("Players", Codec.BOOLEAN), (o, v) -> o.players = v, o -> o.players)
                .documentation("Should players be affected?")
                .add()
                .append(new KeyedCodec<>("Items", Codec.BOOLEAN), (o, v) -> o.items = v, o -> o.items)
                .documentation("Should items be affected?")
                .add()
                .append(new KeyedCodec<>("Entities", Codec.BOOLEAN), (o, v) -> o.entities = v, o -> o.entities)
                .documentation("Should entities be affected?")
                .add()
                .appendInherited(new KeyedCodec<>("Output", ItemHandler.CODEC), (i, v) -> i.output = v, i -> i.output, (o, p) -> o.output = p.output)
                .documentation("Provides the items to be inserted into the container.")
                .add()

                .appendInherited(new KeyedCodec<>("ExportFaces", Codecs.SIDE_ARRAY), (i, v) -> i.exportFaces = v, i -> i.exportFaces, (o, p) -> o.exportFaces = p.exportFaces)
                .documentation("The adjacent faces to attempt exporting into.")
                .add()

                .appendInherited(new KeyedCodec<>("ExportOnce", Codec.BOOLEAN), (i, v) -> i.exportOnce = v, i -> i.exportOnce, (o, p) -> o.exportOnce = p.exportOnce)
                .documentation("Should the generator only export items to the first valid side that accepts items?")
                .add()

                .appendInherited(new KeyedCodec<>("Cooldown", ProtocolCodecs.RANGEF), (i, v) -> i.duration = v, i -> i.duration, (o, p) -> o.duration = p.duration)
                .documentation("A range that determines the cooldown before the next item is generated.")
                .add()
                .appendInherited(new KeyedCodec<>("ImportFaces", Codecs.SIDE_ARRAY), (i, v) -> i.importFaces = v, i -> i.importFaces, (o, p) -> o.importFaces = p.importFaces)
                .documentation("A range that determines the cooldown before the next item is generated.")
                .add()
                .build();

        private float force = 1f;
        private boolean players = true;
        private boolean entities = true;
        private boolean items = true;
        private float height = 0.99f;
        private ItemHandler output = new IdOutput();
        private AdjacentSide[] exportFaces = new AdjacentSide[0];
        private AdjacentSide[] importFaces = new AdjacentSide[0];
        private boolean exportOnce = true;
        protected Rangef duration;
    }
}

