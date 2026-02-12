package org.Ev0Mods.plugin.api.block.state;


import au.ellie.hyui.builders.HudBuilder;
import au.ellie.hyui.builders.HyUIAnchor;
import au.ellie.hyui.builders.LabelBuilder;
import com.hypixel.hytale.builtin.blocktick.system.ChunkBlockTickSystem;
import com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState;
import com.hypixel.hytale.builtin.fluid.FluidPlugin;
import com.hypixel.hytale.builtin.fluid.FluidState;
import com.hypixel.hytale.builtin.fluid.FluidSystems;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.common.thread.ticking.Tickable;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.HashUtil;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.ChangeVelocityType;
import com.hypixel.hytale.protocol.ItemResourceType;
import com.hypixel.hytale.protocol.Rangef;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.StateData;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.asset.type.fluid.FluidTicker;
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
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.ChunkColumn;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.FluidSection;
import com.hypixel.hytale.server.core.universe.world.chunk.state.TickableBlockState;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockPatternRule.AdjacentSide;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerBlockState;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.FillerBlockUtil;
import com.hypixel.hytale.server.worldgen.util.BlockFluidEntry;
import com.nimbusds.jose.util.Container;
//import dev.dukedarius.HytaleIndustries.BlockStates.ItemPipeBlockState;
//import dev.dukedarius.HytaleIndustries.HytaleIndustriesPlugin;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.Ev0Mods.plugin.Ev0Lib;
import org.Ev0Mods.plugin.api.codec.Codecs;
import org.Ev0Mods.plugin.api.codec.IdOutput;
import org.Ev0Mods.plugin.api.codec.ItemHandler;
import org.Ev0Mods.plugin.api.component.FluidComponent;
import org.Ev0Mods.plugin.api.system.LiquidPlacingSystem;
import org.Ev0Mods.plugin.api.util.EntityHelper;
import org.Ev0Mods.plugin.api.util.ItemUtilsExtended;
import org.Ev0Mods.plugin.api.util.WorldHelper;
import au.ellie.hyui.builders.PageBuilder;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import static com.hypixel.hytale.builtin.fluid.FluidSystems.*;
import static org.bouncycastle.asn1.x500.style.BCStyle.T;


@SuppressWarnings("removal")

public class HopperProcessor extends ItemContainerState implements TickableBlockState, ItemContainerBlockState {
    public int fluid_id = 0;
    public Rangef duration = new Rangef(0, 10);
    public float tier;
    public static final BuilderCodec<HopperProcessor> CODEC = BuilderCodec.builder(HopperProcessor.class, HopperProcessor::new, BlockState.BASE_CODEC).append(new KeyedCodec<>("StartTime", Codec.INSTANT, true), (i, v) -> i.startTime = v, i -> i.startTime).add()
            .append(new KeyedCodec<>("Tier", Codec.FLOAT, true), (i, v) -> i.tier = v, i -> i.tier).add()
            .append(new KeyedCodec<>("Substitutions", Codec.STRING_ARRAY, true), (i, v) -> i.substitutions = v, i -> i.substitutions).add().append(new KeyedCodec<>("Timer", Codec.DOUBLE, true), (i, v) -> i.timer = v, i -> i.timer).add().build();
    protected Instant startTime;
    private double timerV = 0;
    private double timer = 0;
    protected short outputSlot = 0;
    private String[] substitutions;
    public Data data;
    private World w;
    boolean is_valid = true;
    public String sideVar;
    private Player ownerId;
    BlockEntity be;
    private PlayerRef rf;
    boolean drop = false;
    public ComponentAccessor<EntityStore> ca;
    public Ref<EntityStore>[] ic;
    public Store<EntityStore> es;
    public List<Ref<EntityStore>> l = new ArrayList<Ref<EntityStore>>();
    // Map to keep a reference from spawned visual entity -> the ItemStack it represents
    public Map<Ref<EntityStore>, ItemStack> visualMap = new HashMap<>();
    // Map to keep spawn time for each visual so we can explicitly remove them
    public Map<Ref<EntityStore>, Instant> visualSpawnTimes = new HashMap<>();
    private Fluid f;
    private int tickCounter = 0;

    {
        ic = new Ref[0];
    }

    public void setOwnerId(Player ownerId) {
        this.ownerId = ownerId;
    }

    public Player getOwnerId() {
        return this.ownerId;
    }

    @Override
    public void onOpen(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl World world, @NonNullDecl Store<EntityStore> store) {
        super.onOpen(ref, world, store);
        rf = store.getComponent(ref, PlayerRef.getComponentType());
    }

    @Override
    public boolean initialize(BlockType blockType) {
        if (super.initialize(blockType) && blockType.getState() instanceof Data data) {
            this.data = data;
            //SpatialResource<Ref<EntityStore>, EntityStore> playerSpatialResource = (SpatialResource) .getResource(EntityModule.get().getPlayerSpatialResourceType());
            //if()
            setItemContainer(new SimpleItemContainer((short) 1));
            return true;
        }

        return false;

    }

    @Override
    public boolean canOpen(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl ComponentAccessor<EntityStore> componentAccessor) {
        return super.canOpen(ref, componentAccessor);
    }

    @Override
    public void onDestroy() {

        for (int b = 0; b < l.size() - 1; b++) {
            itemContainer.dropAllItemStacks();
            if (!l.isEmpty()) {
                    if (l.size() > b) {
                    Ref<EntityStore> esx = l.get(0);
                    l.remove(0);
                    try { visualMap.remove(esx); visualSpawnTimes.remove(esx); } catch (Exception ignored) {}
                    if (esx.isValid()) {
                        this.es.removeEntity(esx, RemoveReason.REMOVE);
                    }
                }
            }

        }

        super.onDestroy();


    }

    @Nonnull
    private static final Query<ChunkStore> QUERY = Query.and(FluidSection.getComponentType(), ChunkSection.getComponentType());
    @Nonnull
    private static final Set<Dependency<ChunkStore>> DEPENDENCIES = Set.of(
            new SystemDependency<>(Order.AFTER, FluidSystems.Ticking.class), new SystemDependency<>(Order.BEFORE, ChunkBlockTickSystem.Ticking.class)
    );

    private static boolean isProcessingBench(@Nullable BlockType bt) {
        return bt != null
                && bt.getState() != null;
    }


    @Override
    public void tick(float dt, int index, ArchetypeChunk<ChunkStore> archeChunk,
                     Store<ChunkStore> store, CommandBuffer<ChunkStore> commandBuffer) {

        w = store.getExternalData().getWorld();
        final Store<EntityStore> entities = w.getEntityStore().getStore();
        this.es = entities;

        // Timer logic
        this.timerV += 1.0;
        drop = this.timerV >= duration.max;
        if (drop) this.timerV = 0;

        // if (rf != null) {
        //     HudBuilder.hudForPlayer(rf).fromHtml("<div>Welcome!</div>")
        //             .show(rf, entities);
        // }

        final Vector3i pos = this.getBlockPosition();

        // Tick-based transfer control: increment counter and only act every 30 ticks.
        tickCounter++;
        int phase = tickCounter % 60; // 0..59
        boolean doExport = phase == 0;   // export on ticks 0,60,120...
        boolean doImport = phase == 30;  // import on ticks 30,90,150...

        if (doExport) {
            // Export phase: try to transfer out and do fluid/block fallback
            for (AdjacentSide side : this.data.exportFaces) {
                final Vector3i exportPos = new Vector3i(pos.x, pos.y, pos.z)
                        .add(WorldHelper.rotate(side, this.getRotationIndex()).relativePosition);
                final WorldChunk chunk = w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(exportPos.x, exportPos.z));
                if (chunk == null) continue;

                Object state = chunk.getState(exportPos.x, exportPos.y, exportPos.z);

                boolean transferred = tryTransferToOrFromContainer(state, exportPos, side, entities, true);

                // Fluid / block fallback
                if (!transferred && !this.itemContainer.isEmpty()) {
                    ItemStack stack = this.getItemContainer().getItemStack((short) 0);
                    if (stack != null && !(state instanceof ItemContainerState || state instanceof ProcessingBenchState)) {
                        int fluidId = chunk.getFluidId(exportPos.x, exportPos.y, exportPos.z);
                        ItemStack fluidStack = null;
                        switch (fluidId) {
                            case 2 -> fluidStack = new ItemStack("*Container_Bucket_State_Filled_Red_Slime", 1, null);
                            case 3 -> fluidStack = new ItemStack("*Container_Bucket_State_Filled_Tar", 1, null);
                            case 4 -> fluidStack = new ItemStack("*Container_Bucket_State_Filled_Poison", 1, null);
                            case 5 -> fluidStack = new ItemStack("*Container_Bucket_State_Filled_Green_Slime", 1, null);
                            case 6 -> fluidStack = new ItemStack("*Container_Bucket_State_Filled_Lava", 1, null);
                            case 7 -> fluidStack = new ItemStack("*Container_Bucket_State_Filled_Water", 1, null);
                            default -> fluidStack = null;
                        }

                        if (fluidStack != null && this.itemContainer.canAddItemStack(fluidStack)) {
                            this.itemContainer.addItemStackToSlot((short) 0, fluidStack);
                            chunk.setBlock(exportPos.x, exportPos.y, exportPos.z, BlockType.EMPTY);
                        } else {
                            chunk.setBlock(exportPos.x, exportPos.y, exportPos.z, stack.getBlockKey());
                            this.getItemContainer().removeItemStackFromSlot((short) 0, 1);
                        }
                    }
                }

                if (this.data.exportOnce && transferred) break;
            }
        }

        if (doImport) {
            // Import phase: try to pull from configured import faces
            for (AdjacentSide side : this.data.importFaces) {
                final Vector3i importPos = new Vector3i(pos.x, pos.y, pos.z)
                        .add(WorldHelper.rotate(side, this.getRotationIndex()).relativePosition);
                final WorldChunk chunk = w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(importPos.x, importPos.z));
                if (chunk == null) continue;

                if (tryImportFromContainer(chunk, importPos, entities, side))
                    break; // one successful import stops further faces this phase
            }
        }

        // Cleanup thrown entities: remove invalid refs and explicitly expire visuals after 5 seconds
        if (!l.isEmpty()) {
            Iterator<Ref<EntityStore>> it = l.iterator();
            while (it.hasNext()) {
                Ref<EntityStore> esx = it.next();
                if (esx == null || !esx.isValid()) {
                    it.remove();
                    try { visualMap.remove(esx); visualSpawnTimes.remove(esx); } catch (Exception ignored) {}
                }
            }
        }

        // Explicitly remove visual entities after 5 seconds instead of relying on DespawnComponent
        try {
            if (this.es != null && !visualSpawnTimes.isEmpty()) {
                Instant now = this.es.getResource(WorldTimeResource.getResourceType()).getGameTime();
                Iterator<Map.Entry<Ref<EntityStore>, Instant>> it2 = visualSpawnTimes.entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry<Ref<EntityStore>, Instant> e = it2.next();
                    Ref<EntityStore> ref = e.getKey();
                    Instant spawnTime = e.getValue();
                    try {
                        if (ref == null || !ref.isValid()) {
                            it2.remove();
                            try { visualMap.remove(ref); } catch (Exception ignored) {}
                            continue;
                        }
                        if (now.isAfter(spawnTime.plusSeconds(5))) {
                            it2.remove();
                            try { visualMap.remove(ref); } catch (Exception ignored) {}
                            try { l.remove(ref); } catch (Exception ignored) {}
                            try { this.es.removeEntity(ref, RemoveReason.REMOVE); } catch (Exception ignored) {}
                        }
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception ignored) {}

        this.es = entities;
    }

    /**
     * Handles both export (to inputs) and import (from outputs) in one function.
     */
    private boolean tryTransferToOrFromContainer(Object state, Vector3i pos, AdjacentSide side,
                                                 Store<EntityStore> entities, boolean exportPhase) {
        HytaleLogger.getLogger().atInfo().log("tryTransferToOrFromContainer: side=" + side + " exportPhase=" + exportPhase + " state=" + (state == null ? "null" : state.getClass().getSimpleName()));
        if (!(state instanceof ItemContainerState || state instanceof ProcessingBenchState))
            return false;

        boolean isProcessingBench = state instanceof ProcessingBenchState;
        ProcessingBenchState bench = isProcessingBench ? (ProcessingBenchState) state : null;

        final int MAX_STACK = 100;

            // Helper to spawn visual thrown items: compute velocity from side and always spawn an entity
        Function<ItemStack, Void> spawnVisual = (safeStack) -> {
                Vector3i rel = WorldHelper.rotate(side, this.getRotationIndex()).relativePosition;
                // compute spawn origin and velocity depending on whether we're exporting or importing
                Vector3i hopperBlock = this.getBlockPosition();
                Vector3d hopperCenter = new Vector3d(hopperBlock.x + 0.5, hopperBlock.y + 0.5, hopperBlock.z + 0.5);
                Vector3d sourceCenter = new Vector3d(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
                // spawn at the source when importing, at hopper when exporting
                Vector3d spawnPos = exportPhase ? hopperCenter : sourceCenter;
                // velocity: export sends items away from hopper, import should move toward hopper (inverse direction)
                // Invert both X and Z components (multiply by -1) to correct direction mapping
                Vector3d velocity = exportPhase ? new Vector3d(-rel.x * 0.35, 0.25, -rel.z * 0.35) : new Vector3d(rel.x * 0.35, 0.25, rel.z * 0.35);

                if (safeStack == null || safeStack.isEmpty()) return null;
            HytaleLogger.getLogger().atInfo().log("spawnVisual: spawnPos=" + spawnPos + " item=" + safeStack);
            // Prefer helper behavior when we have nearby targets, but only on export
            if (exportPhase) {
                List<Ref<EntityStore>> nearby = getAllEntitiesInBox(this, hopperBlock, data.height, (ComponentAccessor<EntityStore>) entities, data.players, data.entities, data.items);
                if (!nearby.isEmpty()) {
                    boolean anySpawned = false;
                    for (Ref<EntityStore> targetRef : nearby) {
                        // spawn helper visuals from the hopper center so visuals appear inside the block
                        String oppSide;
                        switch (side.toString()) {
                            case "East" -> oppSide = "West";
                            case "West" -> oppSide = "East";
                            case "North" -> oppSide = "South";
                            case "South" -> oppSide = "North";
                            case "Up" -> oppSide = "Down";
                            case "Down" -> oppSide = "Up";
                            default -> oppSide = side.toString();
                        }
                        Ref<EntityStore> rs = ItemUtilsExtended.throwItem(this.getBlockType().getId(), oppSide, new Vector3d(hopperBlock.x, hopperBlock.y, hopperBlock.z), targetRef, (ComponentAccessor<EntityStore>) entities, safeStack, Vector3d.ZERO, 0f);
                        HytaleLogger.getLogger().atInfo().log("spawnVisual: helper returned=" + rs + " target=" + targetRef + " oppSide=" + oppSide + " safeStack=" + safeStack);
                        if (rs != null) {
                            l.add(rs);
                            try {
                                visualMap.put(rs, safeStack);
                                Instant now = this.es != null ? this.es.getResource(WorldTimeResource.getResourceType()).getGameTime() : Instant.now();
                                visualSpawnTimes.put(rs, now);
                            } catch (Exception ignored) {}
                            anySpawned = true;
                        }
                    }
                    // only skip the fallback spawn if at least one helper spawn succeeded
                    if (anySpawned) return null;
                }
            }

            // Fallback: direct spawn if no targets
            Holder<EntityStore> itemEntityHolder = ItemComponent.generateItemDrop((ComponentAccessor<EntityStore>) entities, safeStack, new Vector3d(spawnPos.x, spawnPos.y, spawnPos.z), Vector3f.ZERO, 0, -1, 0);
            if (itemEntityHolder == null) {
                HytaleLogger.getLogger().atInfo().log("spawnVisual: generateItemDrop returned null for " + safeStack + " at " + spawnPos);
                return null;
            }

            ItemComponent itemComponent = (ItemComponent) itemEntityHolder.getComponent(ItemComponent.getComponentType());
            if (itemComponent != null) {
                itemComponent.setPickupDelay(100000000);
                itemComponent.setRemovedByPlayerPickup(false);
                itemComponent.computeDynamicLight();
            }

            try {
                ((PhysicsValues) itemEntityHolder.ensureAndGetComponent(PhysicsValues.getComponentType())).replaceValues(new PhysicsValues(0,0,true));
                ((Velocity) itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(velocity.x, velocity.y, velocity.z);
            } catch (Exception ignored) {}
            try { itemEntityHolder.tryRemoveComponent(BoundingBox.getComponentType()); } catch (Exception ignored) {}
            try { itemEntityHolder.ensureAndGetComponent(Intangible.getComponentType()); } catch (Exception ignored) {}

            Ref<EntityStore> spawned = entities.addEntity(itemEntityHolder, AddReason.SPAWN);
            HytaleLogger.getLogger().atInfo().log("spawnVisual: addEntity returned=" + spawned + " for item=" + safeStack);
            if (spawned != null) {
                TransformComponent tc = entities.getComponent(spawned, TransformComponent.getComponentType());
                if (tc != null) tc.setPosition(new Vector3d(spawnPos.x, spawnPos.y, spawnPos.z));
                l.add(spawned);
                try {
                    visualMap.put(spawned, safeStack);
                    Instant now = this.es != null ? this.es.getResource(WorldTimeResource.getResourceType()).getGameTime() : Instant.now();
                    visualSpawnTimes.put(spawned, now);
                } catch (Exception ignored) {}
            }

            return null;
        };

        // ProcessingBench handling: respect phase
        if (isProcessingBench) {
            ItemContainer output = bench.getItemContainer().getContainer(2);
            if (!exportPhase) {
                // Import phase: pull from output into hopper if hopper has room
                ItemStack hopperStack = this.getItemContainer().getItemStack((short) 0);
                int hopperQty = hopperStack == null ? 0 : hopperStack.getQuantity();
                if (hopperQty < MAX_STACK) {
                    for (int slot = 0; slot < output.getCapacity(); slot++) {
                        ItemStack stack = output.getItemStack((short) slot);
                        if (stack == null) continue;
                        int transferAmount = (int) Math.min(data.tier * 2, Math.min(stack.getQuantity(), MAX_STACK - hopperQty));
                        if (transferAmount <= 0) continue;
                        ItemStack safeStack = stack.withQuantity(transferAmount);
                        ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short) 0, safeStack);
                        if (t.succeeded()) {
                            output.removeItemStackFromSlot((short) slot, transferAmount);
                            return true;
                        }
                    }
                }
            } else {
                // Export phase: push from hopper into input containers 0 and 1
                ItemStack have = this.getItemContainer().getItemStack((short) 0);
                if (have != null && have.getQuantity() > 0) {
                    int transferAmount = (int) Math.min(data.tier * 2, have.getQuantity());
                    ItemStack safeStack = have.withQuantity(transferAmount);
                    for (int c = 0; c <= 1; c++) {
                        ItemContainer input = bench.getItemContainer().getContainer(c);
                        for (int slot = 0; slot < input.getCapacity(); slot++) {
                            ItemStackSlotTransaction t = input.addItemStackToSlot((short) slot, safeStack);
                            if (t.succeeded()) {
                                HytaleLogger.getLogger().atInfo().log("spawnVisual: caller=ProcessingBenchExport side=" + side + " pos=" + pos + " safeStack=" + safeStack);
                                spawnVisual.apply(safeStack);
                                this.getItemContainer().removeItemStackFromSlot((short) 0, transferAmount);
                                return true;
                            }
                        }
                    }
                }
            }

            return false;
        }

        // Normal container handling: try import first (if hopper has room), then export
        ItemContainer container = ((ItemContainerState) state).getItemContainer();
        ItemStack hopper = this.getItemContainer().getItemStack((short) 0);
        int hopperQuantity = hopper == null ? 0 : hopper.getQuantity();

        // Import (only during import phase)
        if (!exportPhase && hopperQuantity < MAX_STACK) {
            for (int slot = 0; slot < container.getCapacity(); slot++) {
                ItemStack stack = container.getItemStack((short) slot);
                if (stack == null) continue;
                int transferAmount = (int) Math.min(data.tier * 2, Math.min(stack.getQuantity(), MAX_STACK - hopperQuantity));
                if (transferAmount <= 0) continue;
                ItemStack safeStack = stack.withQuantity(transferAmount);
                ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short) 0, safeStack);
                if (t.succeeded()) {
                    container.removeItemStackFromSlot((short) slot, transferAmount);
                    return true;
                }
            }
        }

        // Export (only during export phase)
        if (exportPhase) {
            ItemStack have = this.getItemContainer().getItemStack((short) 0);
            if (have != null && have.getQuantity() > 0) {
            int transferAmount = (int) Math.min(data.tier * 2, have.getQuantity());
            ItemStack safeStack = have.withQuantity(transferAmount);
            for (int slot = 0; slot < container.getCapacity(); slot++) {
                ItemStackSlotTransaction t = container.addItemStackToSlot((short) slot, safeStack);
                if (t.succeeded()) {
                    HytaleLogger.getLogger().atInfo().log("spawnVisual: caller=NormalExport side=" + side + " pos=" + pos + " safeStack=" + safeStack);
                    spawnVisual.apply(safeStack);
                    this.getItemContainer().removeItemStackFromSlot((short) 0, transferAmount);
                    return true;
                }
            }
        }
        }
        return false;
    }


    /**
     * Handles both export (to inputs) and import (from outputs) in one function.
     */



/* ============================================================
   EXPORT
   ============================================================ */

    private boolean handleExport(World world, Store<EntityStore> entities) {

        ItemStack source = this.getItemContainer().getItemStack((short) 0);
        if (source == null) return false;

        int transferAmount = (int) Math.min(data.tier * 2, source.getQuantity());
        if (transferAmount <= 0) return false;

        Vector3i basePos = this.getBlockPosition();
        BlockPosition pos = world.getBaseBlock(
                new BlockPosition(basePos.x, basePos.y, basePos.z)
        );

        for (AdjacentSide side : data.exportFaces) {

            Vector3i targetPos = new Vector3i(pos.x, pos.y, pos.z)
                    .add(WorldHelper.rotate(side, this.getRotationIndex()).relativePosition);

            WorldChunk chunk = world.getChunkIfInMemory(
                    ChunkUtil.indexChunkFromBlock(targetPos.x, targetPos.z)
            );

            if (chunk == null) continue;

            if (tryExportToContainer(chunk, targetPos, source, transferAmount))
                return true;

            if (tryExportToWorld(chunk, targetPos, source, transferAmount))
                return true;
        }

        return false;
    }


    private boolean tryExportToContainer(WorldChunk chunk,
                                         Vector3i pos,
                                         ItemStack source,
                                         int amount) {

        Object state = chunk.getState(pos.x, pos.y, pos.z);
        if (state instanceof ProcessingBenchState bench) {

            ItemStack sourcex = this.getItemContainer().getItemStack((short) 0);
            if (sourcex == null) return false;

            int transferAmount = (int) Math.min(data.tier * 2, sourcex.getQuantity());
            if (transferAmount <= 0) return false;

            ItemStack safeStack = sourcex.withQuantity(transferAmount);

            // Only input containers (0 and 1)
            for (int c = 0; c <= 1; c++) {

                ItemContainer input =
                        bench.getItemContainer().getContainer(c);

                for (int slot = 0; slot < input.getCapacity(); slot++) {

                    ItemStackSlotTransaction t =
                            input.addItemStackToSlot((short) slot, safeStack);

                    if (t.succeeded()) {

                        this.getItemContainer()
                                .removeItemStackFromSlot((short) 0, transferAmount);

                        return true; // one batch per tick
                    }
                }
            }

            return false;
        }


        if (!(state instanceof ItemContainerState containerState))
            return false;

        ItemContainer target = containerState.getItemContainer();

        for (int slot = 0; slot < target.getCapacity(); slot++) {

            ItemStackSlotTransaction t =
                    target.addItemStackToSlot((short) slot,
                            source.withQuantity(amount));

            if (t.succeeded()) {
                this.getItemContainer()
                        .removeItemStackFromSlot((short) 0, amount);
                return true;
            }
        }

        return false;
    }


    private boolean tryExportToWorld(WorldChunk chunk,
                                     Vector3i pos,
                                     ItemStack source,
                                     int amount) {

        // If block is empty, place block item
        if (chunk.getBlockType(pos.x, pos.y, pos.z) == null) {
            // Only place a block when the target position currently contains a fluid (liquid).
            int fluidId = chunk.getFluidId(pos.x, pos.y, pos.z);
            if (fluidId != 0) {
                chunk.setBlock(pos.x, pos.y, pos.z,
                        source.getBlockKey());

                this.getItemContainer()
                        .removeItemStackFromSlot((short) 0, amount);

                // Remove any visual entities we spawned that are at this position
                try {
                    if (this.l != null && !this.l.isEmpty() && this.es != null) {
                        Iterator<Ref<EntityStore>> it = this.l.iterator();
                        while (it.hasNext()) {
                            Ref<EntityStore> ref = it.next();
                            try {
                                if (ref != null && ref.isValid()) {
                                    TransformComponent tc = this.es.getComponent(ref, TransformComponent.getComponentType());
                                    if (tc != null) {
                                        Vector3d p = tc.getPosition();
                                        // if the spawned entity is within the block center (±0.6) remove it
                                        if (Math.abs(p.x - (pos.x + 0.5)) < 0.6 && Math.abs(p.y - (pos.y + 0.5)) < 0.6 && Math.abs(p.z - (pos.z + 0.5)) < 0.6) {
                                                                    it.remove();
                                                                    try { visualMap.remove(ref); } catch (Exception ignored) {}
                                                                    try { visualSpawnTimes.remove(ref); } catch (Exception ignored) {}
                                                                    try { this.es.removeEntity(ref, RemoveReason.REMOVE); } catch (Exception ignored) {}
                                        }
                                    }
                                } else {
                                    it.remove();
                                    try { visualMap.remove(ref); visualSpawnTimes.remove(ref); } catch (Exception ignored) {}
                                }
                            } catch (Exception ignored) {}
                        }
                    }
                } catch (Exception ignored) {}

                return true;
            }
        }

        return false;
    }


/* ============================================================
   IMPORT
   ============================================================ */

    private boolean handleImport(World world, Store<EntityStore> entities) {

        Vector3i basePos = this.getBlockPosition();
        BlockPosition pos = world.getBaseBlock(
                new BlockPosition(basePos.x, basePos.y, basePos.z)
        );

        for (AdjacentSide side : data.importFaces) {

            Vector3i targetPos = new Vector3i(pos.x, pos.y, pos.z)
                    .add(WorldHelper.rotate(side, this.getRotationIndex()).relativePosition);

            WorldChunk chunk = world.getChunkIfInMemory(
                    ChunkUtil.indexChunkFromBlock(targetPos.x, targetPos.z)
            );

            if (chunk == null) continue;

            if (tryImportFromContainer(chunk, targetPos, entities, side))
                return true;
        }

        return false;
    }


    private boolean tryImportFromContainer(WorldChunk chunk,
                                           Vector3i pos,
                                           Store<EntityStore> entities,
                                           AdjacentSide side) {

        Object state = chunk.getState(pos.x, pos.y, pos.z);

    /* ---------------------------------
       Special handling: Processing Bench
       --------------------------------- */
        if (state instanceof ProcessingBenchState bench) {

            // Only import from output container (2)
            ItemContainer output =
                    bench.getItemContainer().getContainer(2);

            for (int slot = 0; slot < output.getCapacity(); slot++) {

                ItemStack stack = output.getItemStack((short) slot);
                if (stack == null) continue;

                int transferAmount =
                        (int) Math.min(data.tier * 2, stack.getQuantity());
                if (transferAmount <= 0) continue;

                ItemStack safeStack =
                        stack.withQuantity(transferAmount);

                ItemStackSlotTransaction t =
                        this.getItemContainer().addItemStackToSlot(
                                (short) 0,
                                safeStack
                        );

                if (t.succeeded()) {

                    // Visual: throw the consumed item towards nearby players/entities/items
                    Vector3i relRot = WorldHelper.rotate(side, this.getRotationIndex()).relativePosition;
                    Vector3d velRot = new Vector3d(relRot.x * 0.35, 0.25, relRot.z * 0.35);
                            Vector3i hopperBlock = this.getBlockPosition();
                            Vector3d hopperCenter = new Vector3d(hopperBlock.x + 0.5, hopperBlock.y + 0.5, hopperBlock.z + 0.5);
                            // No visual spawn on import; visuals only appear on export.

                    // Optionally remove the visual entity if drop flag is set
                    if (drop) {
                        if (!l.isEmpty()) {
                            if (l.getFirst() != null) {
                                Ref<EntityStore> esx = l.getFirst();
                                if (esx.isValid()) {
                                    l.removeFirst();
                                    try { visualMap.remove(esx); visualSpawnTimes.remove(esx); } catch (Exception ignored) {}
                                    entities.removeEntity(esx, RemoveReason.REMOVE);
                                }
                            }
                        }
                    }

                    output.removeItemStackFromSlot(
                            (short) slot,
                            transferAmount
                    );

                    return true; // one batch per tick
                }
            }

            return false;
        }

        // Special-case: importing from another HopperProcessor (pull 1 item and show visual)
        if (state instanceof HopperProcessor otherHopper) {
            for (int n = 0; n < otherHopper.getItemContainer().getCapacity(); n++) {
                ItemStack otherStack = otherHopper.getItemContainer().getItemStack((short) n);
                if (otherStack == null) continue;

                // Try to take a single item from the other hopper into this hopper
                ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short) 0, otherStack.withQuantity(1));
                if (t.succeeded()) {
                    // Spawn visual for the taken item
                    ItemStack taken = this.getItemContainer().getItemStack((short) 0);
                    if (taken != null && !taken.isEmpty()) {
                        List<Ref<EntityStore>> nearby = getAllEntitiesInBox(this, this.getBlockPosition(), data.height, (ComponentAccessor<EntityStore>) entities, data.players, data.entities, data.items);
                        Ref<EntityStore> targetRef = nearby.isEmpty() ? null : nearby.get(0);
                        // origin should be the other hopper's center; direction should be toward THIS hopper (opposite side)
                        Vector3d otherCenter = new Vector3d(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
                        String oppSide;
                        switch (side.toString()) {
                            case "East" -> oppSide = "West";
                            case "West" -> oppSide = "East";
                            case "North" -> oppSide = "South";
                            case "South" -> oppSide = "North";
                            case "Up" -> oppSide = "Down";
                            case "Down" -> oppSide = "Up";
                            default -> oppSide = side.toString();
                        }
                        if (targetRef != null) {
                            // spawn directly at other hopper center toward the target entity
                            TransformComponent tc = entities.getComponent(targetRef, TransformComponent.getComponentType());
                            Vector3d targetPosVec = tc != null ? tc.getPosition().clone() : new Vector3d(this.getBlockPosition().x + 0.5, this.getBlockPosition().y + 0.5, this.getBlockPosition().z + 0.5);
                            double dx = targetPosVec.x - otherCenter.x;
                            double dy = targetPosVec.y - otherCenter.y;
                            double dz = targetPosVec.z - otherCenter.z;
                            double len = Math.sqrt(dx*dx + dy*dy + dz*dz);
                            double speed = 0.35;
                            Vector3d vel;
                            if (len > 1e-6) {
                                vel = new Vector3d(dx / len * speed, dy / len * speed, dz / len * speed);
                            } else {
                                vel = new Vector3d(0, 0.25, 0);
                            }

                            // No visual spawn on import from another hopper; visuals only appear on export.
                        } else {
                            // spawn direct item entity at hopper position with directional velocity
                            Vector3i rel = WorldHelper.rotate(side, this.getRotationIndex()).relativePosition;
                            // velocity should go from source (other hopper) TO this hopper, so invert rel
                            Vector3d velocity = new Vector3d(-rel.x * 0.35, 0.25, -rel.z * 0.35);
                            Vector3d spawnPos = new Vector3d(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
                            Holder<EntityStore> itemEntityHolder = ItemComponent.generateItemDrop((ComponentAccessor<EntityStore>) entities, taken, new Vector3d(spawnPos.x, spawnPos.y, spawnPos.z), Vector3f.ZERO, 0, -1, 0);
                            if (itemEntityHolder != null) {
                                ItemComponent itemComponent = (ItemComponent) itemEntityHolder.getComponent(ItemComponent.getComponentType());
                                if (itemComponent != null) {
                                    itemComponent.setPickupDelay(100000000);
                                    itemComponent.setRemovedByPlayerPickup(false);
                                    itemComponent.computeDynamicLight();
                                }
                                try {
                                    ((PhysicsValues) itemEntityHolder.ensureAndGetComponent(PhysicsValues.getComponentType())).replaceValues(new PhysicsValues(0,0,true));
                                    ((Velocity) itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(velocity.x, velocity.y, velocity.z);
                                } catch (Exception ignored) {}
                                try { itemEntityHolder.tryRemoveComponent(BoundingBox.getComponentType()); } catch (Exception ignored) {}
                                try { itemEntityHolder.ensureAndGetComponent(Intangible.getComponentType()); } catch (Exception ignored) {}

                                Ref<EntityStore> spawned = entities.addEntity(itemEntityHolder, AddReason.SPAWN);
                                if (spawned != null) {
                                    TransformComponent tcSpawned2 = entities.getComponent(spawned, TransformComponent.getComponentType());
                                    if (tcSpawned2 != null) tcSpawned2.setPosition(new Vector3d(spawnPos.x, spawnPos.y, spawnPos.z));
                                    l.add(spawned);
                                    try {
                                        visualMap.put(spawned, taken);
                                        Instant now = this.es != null ? this.es.getResource(WorldTimeResource.getResourceType()).getGameTime() : Instant.now();
                                        visualSpawnTimes.put(spawned, now);
                                    } catch (Exception ignored) {}
                                }
                            }
                        }
                    }

                    otherHopper.getItemContainer().removeItemStackFromSlot((short) n, 1);
                    return true;
                }
            }

            return false;
        }

    /* ---------------------------------
       Normal container handling
       --------------------------------- */
        if (!(state instanceof ItemContainerState containerState))
            return false;

        ItemContainer sourceContainer =
            containerState.getItemContainer();

        for (int slot = 0; slot < sourceContainer.getCapacity(); slot++) {

            ItemStack stack =
                sourceContainer.getItemStack((short) slot);
            if (stack == null) continue;

            int transferAmount =
                (int) Math.min(data.tier * 2, stack.getQuantity());
            if (transferAmount <= 0) continue;

            ItemStack safeStack = stack.withQuantity(transferAmount);

            ItemStackSlotTransaction t =
                this.getItemContainer().addItemStackToSlot(
                    (short) 0,
                    safeStack
                );

            if (t.succeeded()) {

            // Visual: throw the item from the source towards nearby targets
            Vector3i relRot2 = WorldHelper.rotate(side, this.getRotationIndex()).relativePosition;
            Vector3d velRot2 = new Vector3d(relRot2.x * 0.35, 0.25, relRot2.z * 0.35);
            // For visuals originating from the source container, spawn at the source center
            Vector3d sourceCenter = new Vector3d(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
            String oppSide;
            switch (side.toString()) {
                case "East" -> oppSide = "West";
                case "West" -> oppSide = "East";
                case "North" -> oppSide = "South";
                case "South" -> oppSide = "North";
                case "Up" -> oppSide = "Down";
                case "Down" -> oppSide = "Up";
                default -> oppSide = side.toString();
            }

            for (Ref<EntityStore> target2 : getAllEntitiesInBox(this, pos, data.height,
                    (ComponentAccessor<EntityStore>) entities, data.players, data.entities, data.items)) {
                    HytaleLogger.getLogger().atInfo().log("spawnVisual: caller=SourceContainerExport side=" + oppSide + " sourceCenter=" + sourceCenter + " target=" + target2 + " safeStack=" + safeStack);
                    Ref<EntityStore> rs = ItemUtilsExtended.throwItem(this.getBlockType().getId(), oppSide, new Vector3d(pos.x, pos.y, pos.z), target2, (ComponentAccessor<EntityStore>) entities, safeStack, Vector3d.ZERO, 0f);
                    HytaleLogger.getLogger().atInfo().log("spawnVisual: helper returned=" + rs + " target=" + target2 + " oppSide=" + oppSide + " safeStack=" + safeStack);
                if (rs != null) { l.add(rs); try { visualMap.put(rs, safeStack); } catch (Exception ignored) {} }
            }

            // Optionally remove the visual entity if drop flag is set
            if (drop) {
                if (!l.isEmpty()) {
                Ref<EntityStore> esx = l.getFirst();
                if (esx != null && esx.isValid()) {
                    l.removeFirst();
                    try { visualMap.remove(esx); } catch (Exception ignored) {}
                    entities.removeEntity(esx, RemoveReason.REMOVE);
                }
                }
            }

            sourceContainer.removeItemStackFromSlot(
                (short) slot,
                transferAmount
            );

            return true;
            }
        }

        return false;
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
    public static List<Ref<EntityStore>> getAllItemsInBox(HopperProcessor hp, Vector3i pos, float height, @Nonnull ComponentAccessor<EntityStore> components,  boolean players, boolean entities, boolean items) {
        final ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
        final ObjectList<Ref<Store>> results2 = SpatialResource.getThreadLocalReferenceList();
        final Vector3d min = new Vector3d(pos.x-.5, pos.y-.5 , pos.z-.5);
        final Vector3d max = new Vector3d(pos.x+.5, pos.y+.5, pos.z+.5);
        if (entities) {
            //components.getResource(EntityModule.get().getEntitySpatialResourceType()).getSpatialStructure()
        }
        if (players) {
            //components.getResource(EntityModule.get().getPlayerSpatialResourceType()).getSpatialStructure().collectCylinder(new Vector3d(pos.x,pos.y,pos.z), 4, 8, results );
        }
        if (items) {
            components.getResource(EntityModule.get().getItemSpatialResourceType()).getSpatialStructure().collectCylinder(new Vector3d(pos.x,pos.y,pos.z), 8,6,results );
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
                .append(new KeyedCodec<>("Tier", Codec.FLOAT), (o, v) -> o.tier = v, o -> o.tier)
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
                .append(new KeyedCodec<>("Substitutions", Codec.STRING_ARRAY, true), (i,v) ->i.substitutions = v, i -> i.substitutions)
                .add()


                .appendInherited(new KeyedCodec<>("Cooldown", ProtocolCodecs.RANGEF), (i, v) -> i.duration = v, i -> i.duration, (o, p) -> o.duration = p.duration)
                .documentation("A range that determines the cooldown before the next item is generated.")
                .add()
                .appendInherited(new KeyedCodec<>("ImportFaces", Codecs.SIDE_ARRAY), (i, v) -> i.importFaces = v, i -> i.importFaces, (o, p) -> o.importFaces = p.importFaces)
                .documentation("A range that determines the cooldown before the next item is generated.")
                .add()
                .build();
        public float tier = 1;
        private float force = 1f;
        private boolean players = true;
        private boolean entities = true;
        private boolean items = true;
        private float height = 0.99f;
        private ItemHandler output = new IdOutput();
        private AdjacentSide[] exportFaces = new AdjacentSide[0];
        private AdjacentSide[] importFaces = new AdjacentSide[0];
        public String[] substitutions;
        private boolean exportOnce = true;
        protected Rangef duration;
    }
}

