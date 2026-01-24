package org.Ev0Mods.plugin.api.block.state;


import com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.common.thread.ticking.Tickable;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.HashUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.ChangeVelocityType;
import com.hypixel.hytale.protocol.ItemResourceType;
import com.hypixel.hytale.protocol.Rangef;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.StateData;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemDrop;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.ItemUtils;
import com.hypixel.hytale.server.core.entity.entities.BlockEntity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.Transaction;
import com.hypixel.hytale.server.core.modules.collision.CollisionModule;
import com.hypixel.hytale.server.core.modules.collision.CollisionResult;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.Intangible;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.modules.entity.item.PickupItemComponent;
import com.hypixel.hytale.server.core.modules.entity.item.PreventItemMerging;
import com.hypixel.hytale.server.core.modules.entity.system.ItemSpatialSystem;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.physics.component.PhysicsValues;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.state.TickableBlockState;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockPatternRule.AdjacentSide;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerBlockState;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.FillerBlockUtil;
import com.nimbusds.jose.util.Container;
//import dev.dukedarius.HytaleIndustries.BlockStates.ItemPipeBlockState;
//import dev.dukedarius.HytaleIndustries.HytaleIndustriesPlugin;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.Ev0Mods.plugin.Ev0Lib;
import org.Ev0Mods.plugin.api.codec.Codecs;
import org.Ev0Mods.plugin.api.codec.IdOutput;
import org.Ev0Mods.plugin.api.codec.ItemHandler;
import org.Ev0Mods.plugin.api.util.EntityHelper;
import org.Ev0Mods.plugin.api.util.ItemUtilsExtended;
import org.Ev0Mods.plugin.api.util.WorldHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import static org.bouncycastle.asn1.x500.style.BCStyle.T;


@SuppressWarnings("removal")

public class HopperProcessor extends ItemContainerState implements TickableBlockState, ItemContainerBlockState{

    public Rangef duration = new Rangef(0,10);
    public static final BuilderCodec<HopperProcessor> CODEC = BuilderCodec.builder(HopperProcessor.class, HopperProcessor::new, BlockState.BASE_CODEC).append(new KeyedCodec<>("StartTime", Codec.INSTANT, true), (i, v) -> i.startTime = v, i -> i.startTime).add()
            .append(new KeyedCodec<>("Timer", Codec.DOUBLE, true), (i, v) -> i.timer = v, i -> i.timer).add().build();
    protected Instant startTime;
    private double timerV = 0;
    private double timer = 0;
    protected short outputSlot = 0  ;
    protected Data data;
    boolean is_valid = true;
    public String sideVar;
    BlockEntity be;
    boolean drop = false;
    public ComponentAccessor<EntityStore> ca;
    public Ref<EntityStore>[] ic;
    public Store<EntityStore> es;
    public List<Ref<EntityStore>> l = new ArrayList<Ref<EntityStore>>();
    {
        ic = new Ref[0];
    }

    public boolean show(Entity e, World world, Vector3d v3d, Vector3f v3f, AddReason reason){
        world.addEntity(e, v3d, v3f, reason);
        return true;
    }
    @Override
    public boolean initialize(BlockType blockType) {
        if (super.initialize(blockType) && blockType.getState() instanceof Data data) {
            this.data = data;
            setItemContainer(new SimpleItemContainer((short)1));
            return true;
        }

        return false;

    }

    @Override
    public void onDestroy() {

        for(int b = 0; b<=l.size()-1;b++) {
            itemContainer.dropAllItemStacks();
            if(!l.isEmpty()){
                if(l.size() >b) {
                    Ref<EntityStore> esx = l.get(0);
                    l.remove(0);
                    if(esx.isValid()) {
                        this.es.removeEntity(esx, RemoveReason.REMOVE);
                    }
                }
            }

        }

        super.onDestroy();



    }
    private static boolean isProcessingBench(@Nullable BlockType bt){
        return bt != null
        && bt.getState()!= null;
    }
    @Override
    public void tick(float dt, int index, ArchetypeChunk<ChunkStore> archeChunk, Store<ChunkStore> store, CommandBuffer<ChunkStore> commandBuffer) {

        this.timerV = this.timerV +1.0;
        final World world = store.getExternalData().getWorld();

        if (this.getItemContainer().getItemStack((short)0) != null) {
            //LOGGER.atInfo().log(Objects.requireNonNull(this.getItemContainer().getContainer(2).getItemStack((short)0).toString()));
        }
        final Store<EntityStore> entities = world.getEntityStore().getStore();
        this.es = entities;
        final Ref<EntityStore> targetX;
        final Instant currentTime = world.getEntityStore().getStore().getResource(WorldTimeResource.getResourceType()).getGameTime();
        final Vector3d forceVec = new Vector3d((1.5f) * data.force, (1.5f) * data.force, (1.5f) * data.force);
        //HytaleLogger.getLogger().atInfo().log("timer: " + timerV);
        if (this.timerV >= duration.max) {
            //HytaleLogger.getLogger().atInfo().log("timer: " + timerV);
            this.timerV = 0;
            drop = true;
        }else if (this.timerV< duration.max) {
            drop = false;
        }
        //this.reset(currentTime);

        final Vector3i generatorPos = this.getBlockPosition();
        final BlockPosition pos = world.getBaseBlock(new BlockPosition(generatorPos.x, generatorPos.y, generatorPos.z));
        if(drop == true){
        for (AdjacentSide side : this.data.exportFaces) {
            //LOGGER.atInfo().log(side.name());
            boolean exportedItems = false;
            final Vector3i exportPos = new Vector3i(pos.x, pos.y, pos.z).add(WorldHelper.rotate(side, this.getRotationIndex()).relativePosition);
            final WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(exportPos.x, exportPos.z));
            //LOGGER.atInfo().log(chunk.toString());


            if (chunk != null && chunk.getState(exportPos.x, exportPos.y, exportPos.z) instanceof HopperProcessor containerState) {
                    for(int n = 0; n< containerState.getItemContainer().getCapacity(); n++) {
                        if(this.getItemContainer().getItemStack((short)0) != null && this.getItemContainer().getItemStack((short)0).getQuantity() <100) {
                            ItemStackSlotTransaction t = containerState.getItemContainer().addItemStackToSlot((short) n, this.getItemContainer().getItemStack((short) 0).withQuantity(1));
                            if (t.succeeded()) {
                                if(drop){
                                    if(!l.isEmpty()) {
                                        if(l.getFirst() != null){

                                            Ref<EntityStore> esx = l.getFirst();

                                            if(esx.isValid()) {
                                                l.removeFirst();
                                                entities.removeEntity(esx, RemoveReason.REMOVE);
                                            }
                                        }
                                    }
                                }
                                if (!this.getItemContainer().isEmpty()) {
                                    //int length = ic.length;
                                    //l.add( ItemUtilsExtended.throwItem(new Vector3d(pos.x, pos.y,pos.z), target2, (ComponentAccessor<EntityStore>) entities,this.getItemContainer().getItemStack((short) 0), Vector3d.UP, -.1f));
                                    //ItemUtilsExtended.throwItem(entities, this.getItemContainer().getItemStack((short) 0), Vector3d.UP, 0.2f, new Vector3d(pos.x,pos.y,pos.z));
                                    // HytaleLogger.getLogger().atInfo().log("Throwing" + pos.x + " " + pos.y + " " + pos.z + " ");
                                    this.getItemContainer().removeItemStackFromSlot((short) 0,1);
                                    break;
                                }
                            }



                                    if(!l.isEmpty()) {
                                        Ref<EntityStore> esx = l.getFirst();
                                        if(esx.isValid()) {
                                            l.removeFirst();

                                            entities.removeEntity(esx, RemoveReason.REMOVE);
                                        }
                                    }


                        }
                    }

            }



            if (chunk != null && chunk.getState(exportPos.x, exportPos.y, exportPos.z) instanceof ItemContainerBlockState containerState) {
                        for(int n = 0; n< containerState.getItemContainer().getCapacity(); n++) {
                            if(this.getItemContainer().getItemStack((short)0) != null) {
                                ItemStackSlotTransaction t = containerState.getItemContainer().addItemStackToSlot((short) n, this.getItemContainer().getItemStack((short) 0).withQuantity(1));
                                if (t.succeeded()) {
                                    if(drop){

                                    }

                                                //int length = ic.length;
                                                //l.add( ItemUtilsExtended.throwItem(new Vector3d(pos.x, pos.y,pos.z), target2, (ComponentAccessor<EntityStore>) entities,this.getItemContainer().getItemStack((short) 0), Vector3d.UP, -.1f));
                                                //ItemUtilsExtended.throwItem(entities, this.getItemContainer().getItemStack((short) 0), Vector3d.UP, 0.2f, new Vector3d(pos.x,pos.y,pos.z));
                                                // HytaleLogger.getLogger().atInfo().log("Throwing" + pos.x + " " + pos.y + " " + pos.z + " ");
                                    this.getItemContainer().removeItemStackFromSlot((short) 0, 1);
                                    break;
                                }

                            }
                            if(drop){
                                if(!l.isEmpty()) {
                                    if(l.getFirst() != null){

                                        Ref<EntityStore> esx = l.getFirst();

                                        if(esx.isValid()) {
                                            l.removeFirst();
                                            entities.removeEntity(esx, RemoveReason.REMOVE);
                                        }
                                    }
                                }
                            }
                        }
            }

            if (chunk != null && chunk.getState(exportPos.x, exportPos.y, exportPos.z) instanceof ProcessingBenchState containerState) {
                if(drop == true){
                    if (!this.itemContainer.isEmpty()) {
                        if (!containerState.getItemContainer().getContainer(2).isEmpty()) {
                            //HytaleLogger.getLogger().atInfo().log(containerState.getItemContainer().getContainer(2).getItemStack((short) 0).toString());
                        }

                        ItemStack source = this.getItemContainer().getItemStack((short) 0);
                        if (source == null || source.getQuantity() <= 0) {
                            return;
                        }

    // try container 0 first, then container 1
                        for (int c = 0; c <= 1; c++) {
                            ItemContainer targetContainer =
                                    containerState.getItemContainer().getContainer(c);

                            for (int n = 0; n < targetContainer.getCapacity(); n++) {
                                ItemStack target = targetContainer.getItemStack((short) n);

                                if (target == null || target.getQuantity() < 100) {
                                    ItemStackSlotTransaction t =

                                            targetContainer.addItemStackToSlot(
                                                    (short) n, source.withQuantity(1)
                                            );
                                    be = new BlockEntity(this.getItemContainer().getItemStack((short) 0).getItem().getBlockId());
                                    Vector3d v3d = new Vector3d(pos.x, pos.y, pos.z);
                                    Vector3f v3i = new Vector3f(pos.x, pos.y, pos.z);
                                    final ObjectList<Ref<EntityStore>> result = SpatialResource.getThreadLocalReferenceList();
                                    final ComponentType<EntityStore, BlockEntity> ct = getComponentType();



                                    if (t.succeeded()) {
                                        for (Ref<EntityStore> target2: getAllEntitiesInBox(this, this.getBlockPosition(), data.height, entities, data.players, data.entities, data.items))
                                        {
                                            if (!this.getItemContainer().isEmpty()) {
                                            //int length = ic.length;
                                            //l.add( ItemUtilsExtended.throwItem(new Vector3d(pos.x, pos.y,pos.z), target2, (ComponentAccessor<EntityStore>) entities,this.getItemContainer().getItemStack((short) 0), Vector3d.UP, -.1f));
                                            //ItemUtilsExtended.throwItem(entities, this.getItemContainer().getItemStack((short) 0), Vector3d.UP, 0.2f, new Vector3d(pos.x,pos.y,pos.z));
                                            // HytaleLogger.getLogger().atInfo().log("Throwing" + pos.x + " " + pos.y + " " + pos.z + " ");
                                            }
                                            this.getItemContainer().removeItemStackFromSlot((short) 0, 1);
                                            if(drop){
                                                if(!l.isEmpty()) {
                                                    if(l.getFirst() != null){

                                                        Ref<EntityStore> esx = l.getFirst();

                                                        if(esx.isValid()) {
                                                            l.removeFirst();
                                                            entities.removeEntity(esx, RemoveReason.REMOVE);
                                                        }
                                                    }
                                                }
                                            }
                                            return;
                                        }
                                         // move exactly one item total
                                    }

                                        if(drop){
                                            if(!l.isEmpty()) {
                                                if(l.getFirst() != null){

                                                    Ref<EntityStore> esx = l.getFirst();

                                                    if(esx.isValid()) {
                                                        l.removeFirst();
                                                        entities.removeEntity(esx, RemoveReason.REMOVE);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                        }

                        ItemStack source2 = this.getItemContainer().getItemStack((short) 0);
                        if (source2 == null || source2.getQuantity() <= 0) {
                            return;
                        }

    // iterate container 1 slots first, then container 0
                        for (int c = 1; c >= 0; c--) {
                            ItemContainer targetContainer =
                                    containerState.getItemContainer().getContainer(c);

                            for (int n = 0; n < targetContainer.getCapacity(); n++) {
                                ItemStack target = targetContainer.getItemStack((short) n);

                                if (target == null || target.getQuantity() < 100) {
                                    ItemStackSlotTransaction t =
                                            targetContainer.addItemStackToSlot(
                                                    (short) n, source2.withQuantity(1)
                                            );

                                    if (t.succeeded()) {
                                            if(!this.getItemContainer().isEmpty() &&this.getItemContainer().getItemStack((short) 0) !=null) {
                                                //ItemUtilsExtended.throwItem(entities, this.getItemContainer().getItemStack((short) 0), Vector3d.UP, .2f, new Vector3d(pos.x, pos.y, pos.z));
                                                //HytaleLogger.getLogger().atInfo().log("Throwing" + entities.toString());

                                            }

                                            if(drop){
                                                if(!l.isEmpty()){

                                                        Ref<EntityStore> esx = l.getFirst();

                                                        if(esx.isValid()) {
                                                            l.removeFirst();
                                                            entities.removeEntity(esx, RemoveReason.REMOVE);
                                                        }

                                                }
                                            }
                                        this.getItemContainer().removeItemStackFromSlot((short) 0, 1);
                                        }

                                        return; // move exactly one item
                                    }
                                    if(drop){
                                        if(!l.isEmpty()) {
                                            if(l.getFirst() != null){

                                                Ref<EntityStore> esx = l.getFirst();

                                                if(esx.isValid()) {
                                                    l.removeFirst();
                                                    entities.removeEntity(esx, RemoveReason.REMOVE);
                                                }
                                            }
                                        }

                                }
                            }


                    }
                }


                }

            }


            if (this.data.exportOnce && exportedItems) {
                break;
            }

        }
            if(!l.isEmpty()) {
                if(l.getFirst() != null){

                    Ref<EntityStore> esx = l.getFirst();

                    if(esx.isValid()) {
                        l.removeFirst();
                        es.removeEntity(esx, RemoveReason.REMOVE);
                    }
                }
            }


        for (AdjacentSide side : this.data.importFaces) {
            final Vector3i exportPos = new Vector3i(pos.x, pos.y, pos.z).add(WorldHelper.rotate(side, this.getRotationIndex()).relativePosition);
            final WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(exportPos.x, exportPos.z));
            final String sideVar = side.toString();
            ItemStackSlotTransaction transaction = null;
           // HytaleLogger.getLogger().atInfo().log(side.name().toString());
            if (chunk != null && chunk.getState(exportPos.x, exportPos.y, exportPos.z) instanceof ItemContainerState containerState) {
                for (int n = 0; n < containerState.getItemContainer().getCapacity() - 1; n++) {
                    if (containerState.getItemContainer().getItemStack((short) n) != null && containerState instanceof ItemContainerBlockState) {
                            ItemStackSlotTransaction t = this.itemContainer.addItemStackToSlot((short) 0, containerState.getItemContainer().getItemStack((short) n).withQuantity(1));
                            if (t.succeeded()){
                                for (Ref<EntityStore> target2 : getAllEntitiesInBox(this, this.getBlockPosition(), data.height, entities, data.players, data.entities, data.items)) {
                                        //int length = ic.length;
                                        if(!this.itemContainer.isEmpty()) {
                                            Ref<EntityStore> rs = ItemUtilsExtended.throwItem(this.getBlockType().getId(), sideVar, new Vector3d(pos.x, pos.y, pos.z), target2, (ComponentAccessor<EntityStore>) entities, this.getItemContainer().getItemStack((short) 0), Vector3d.ZERO, 0f);
                                            //HytaleLogger.getLogger().atInfo().log("Throwing");
                                            //entities.addComponent(target2, Velocity.getComponentType()).set(0,1,0);
                                            //entities.addComponent(target2,  PhysicsValues.getComponentType()).replaceValues(new PhysicsValues(0,0,true));
                                            l.add(rs);

                                            //ItemUtilsExtended.throwItem(entities, this.getItemContainer().getItemStack((short) 0), Vector3d.UP, 0.2f, new Vector3d(pos.x,pos.y,pos.z));
                                            //HytaleLogger.getLogger().atInfo().log("Throwing" + pos.x + " " + pos.y + " " + pos.z + " ");
                                        }

                                }

                                    containerState.getItemContainer().removeItemStackFromSlot((short) n, 1);

                            }
                    }
                }
            }

            if (chunk != null && chunk.getState(exportPos.x, exportPos.y, exportPos.z) instanceof HopperProcessor containerState) {
                for (int n = 0; n < containerState.getItemContainer().getCapacity(); n++) {
                    if (containerState.getItemContainer().getItemStack((short) 0) != null && containerState instanceof HopperProcessor) {
                        ItemStackSlotTransaction t = this.itemContainer.addItemStackToSlot((short) 0, containerState.getItemContainer().getItemStack((short) 0).withQuantity(1));
                        if (t.succeeded()){
                            for (Ref<EntityStore> target2 : getAllEntitiesInBox(this, this.getBlockPosition(), data.height, entities, data.players, data.entities, data.items)) {
                                //int length = ic.length;
                                if(!this.itemContainer.isEmpty()) {
                                    Ref<EntityStore> rs = ItemUtilsExtended.throwItem(this.getBlockType().getId(), sideVar, new Vector3d(pos.x, pos.y, pos.z), target2, (ComponentAccessor<EntityStore>) entities, this.getItemContainer().getItemStack((short) 0), Vector3d.ZERO, 0f);
                                    //HytaleLogger.getLogger().atInfo().log("Throwing");
                                    //entities.addComponent(target2, Velocity.getComponentType()).set(0,1,0);
                                    //entities.addComponent(target2,  PhysicsValues.getComponentType()).replaceValues(new PhysicsValues(0,0,true));
                                    l.add(rs);

                                    //ItemUtilsExtended.throwItem(entities, this.getItemContainer().getItemStack((short) 0), Vector3d.UP, 0.2f, new Vector3d(pos.x,pos.y,pos.z));
                                    //HytaleLogger.getLogger().atInfo().log("Throwing" + pos.x + " " + pos.y + " " + pos.z + " ");
                                }

                            }

                            containerState.getItemContainer().removeItemStackFromSlot((short) 0, 1);

                        }
                    }
                }
            }

            if (chunk != null && chunk.getState(exportPos.x, exportPos.y, exportPos.z) instanceof ProcessingBenchState containerState) {
                //HytaleLogger.getLogger().atInfo().log(side.name());
                if (!containerState.getItemContainer().isEmpty()) {
                    if (!containerState.getItemContainer().getContainer(2).isEmpty()) {
                        for (int i = 0; i < containerState.getItemContainer().getContainer(2).getCapacity() - 1; i++) {
                            if (containerState.getItemContainer().getContainer(2).getItemStack((short) i) != null) {
                                //HytaleLogger.getLogger().atInfo().log(containerState.getItemContainer().getContainer(2).getItemStack((short) i).toString());

                                ItemStackSlotTransaction t = this.itemContainer.addItemStackToSlot((short) 0, containerState.getItemContainer().getContainer(2).getItemStack((short) i).withQuantity(1));
                                if (t.succeeded()) {
                                    for (Ref<EntityStore> target2 : getAllEntitiesInBox(this, this.getBlockPosition(), data.height, entities, data.players, data.entities, data.items)) {
                                        if (!this.getItemContainer().isEmpty()) {
                                            //int length = ic.length;

                                            l.add(ItemUtilsExtended.throwItem(this.getBlockType().getId(), sideVar, new Vector3d(pos.x, pos.y, pos.z), target2, (ComponentAccessor<EntityStore>) entities, this.getItemContainer().getItemStack((short) 0), Vector3d.ZERO, 0));
                                            //ItemUtilsExtended.throwItem(entities, this.getItemContainer().getItemStack((short) 0), Vector3d.UP, 0.2f, new Vector3d(pos.x,pos.y,pos.z));
                                            // HytaleLogger.getLogger().atInfo().log("Throwing" + pos.x + " " + pos.y + " " + pos.z + " ");

                                        }

                                    }
                                    containerState.getItemContainer().getContainer(2).removeItemStackFromSlot((short) i, 1);
                                }


                            }
                        }
                    }

                }
            }
        }
        }

//        for (Ref<EntityStore> target : getAllEntitiesInBox(this, this.getBlockPosition(), data.height, entities, data.players, data.entities, data.items)) {
//            final Player player = entities.getComponent(target, Player.getComponentType());
//            if (player != null) {
//                if (!EntityHelper.isCrouching(entities, target)) {
//                    final Velocity velocity = entities.getComponent(target, Velocity.getComponentType());
//                    if (velocity != null) {
//                        velocity.addInstruction(forceVec.clone().scale(12), null, ChangeVelocityType.Set);
//                    }
//                }
//                this.ca = entities;
//                final Velocity velocity = entities.getComponent(target, Velocity.getComponentType());
//                if (velocity != null) {
//                    velocity.addInstruction(forceVec.clone().scale(12), null, ChangeVelocityType.Set);
//                }
//            }
//            else {
////                final TransformComponent targetPos = entities.getComponent(target, TransformComponent.getComponentType());
////                if (targetPos != null) {
////                    final CollisionResult collision = new CollisionResult();
////                    final Box boundingBox = entities.getComponent(target, BoundingBox.getComponentType()).getBoundingBox();
////                    CollisionModule.findCollisions(boundingBox, targetPos.getPosition().clone(), forceVec.clone(), collision, entities);
////                    if (collision.getFirstBlockCollision() == null) {
////                        targetPos.getPosition().assign(targetPos.getPosition().add(forceVec.clone()));
////                    }
////                }
//            }
//        }
        this.es = entities;
    }

    protected void reset(Instant currentTime) {
        startTime = currentTime;
    }
    @Nonnull
    public static List<Ref<EntityStore>> getAllEntitiesInBox(HopperProcessor hp, Vector3i pos, float height, @Nonnull ComponentAccessor<EntityStore> components,  boolean players, boolean entities, boolean items) {
        final ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
        final ObjectList<Ref<Store>> results2 = SpatialResource.getThreadLocalReferenceList();
        final Vector3d min = new Vector3d(pos.x-.1, pos.y , pos.z-.1);
        final Vector3d max = new Vector3d(pos.x+.1, pos.y, pos.z+.1);
        if (entities) {
            //components.getResource(EntityModule.get().getEntitySpatialResourceType()).getSpatialStructure().collectCylinder(new Vector3d(pos.x,pos.y,pos.z), 2,4,results );
        }
        if (players) {
            components.getResource(EntityModule.get().getPlayerSpatialResourceType()).getSpatialStructure().collectCylinder(new Vector3d(pos.x,pos.y,pos.z), 4, 8, results );
        }
        if (items) {
            //components.getResource(EntityModule.get().getItemSpatialResourceType()).getSpatialStructure().collectCylinder(new Vector3d(pos.x,pos.y,pos.z), 2,4,results );
        }
        hp.ca = components;
        return results;
    }

    public static ComponentType<EntityStore, BlockEntity> getComponentType() {
        ComponentRegistryProxy<EntityStore> entityStoreRegistry = EntityModule.get().getEntityStoreRegistry();
        return EntityModule.get().getBlockEntityComponentType();
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

