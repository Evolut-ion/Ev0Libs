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
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import voidbond.arcio.ArcioPlugin;
import voidbond.arcio.components.ArcioMechanismComponent;
import voidbond.arcio.components.BlockUUIDComponent;
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
import org.Ev0Mods.plugin.api.Ev0Config;
import au.ellie.hyui.builders.PageBuilder;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.logging.Level;

import static com.hypixel.hytale.builtin.fluid.FluidSystems.*;
import static org.bouncycastle.asn1.x500.style.BCStyle.T;


@SuppressWarnings("removal")

public class HopperProcessor extends ItemContainerState implements TickableBlockState, ItemContainerBlockState {
    public int fluid_id = 0;
    public Rangef duration = new Rangef(0, 10);
    public float tier;
    public static final BuilderCodec<HopperProcessor> CODEC = BuilderCodec.builder(HopperProcessor.class, HopperProcessor::new, BlockState.BASE_CODEC)
            .append(new KeyedCodec<>("StartTime", Codec.INSTANT, true), (i, v) -> i.startTime = v, i -> i.startTime).add()
            .append(new KeyedCodec<>("Tier", Codec.FLOAT, true), (i, v) -> i.tier = v, i -> i.tier).add()
            .append(new KeyedCodec<>("Substitutions", Codec.STRING_ARRAY, true), (i, v) -> i.substitutions = v, i -> i.substitutions).add()
            .append(new KeyedCodec<>("Timer", Codec.DOUBLE, true), (i, v) -> i.timer = v, i -> i.timer).add()
            // Persist filter mode and lists so UI state survives reloads
            .append(new KeyedCodec<>("FilterMode", Codec.STRING, true), (i, v) -> i.filterMode = v == null ? "Off" : v, i -> i.filterMode).add()
            .append(new KeyedCodec<>("Whitelist", Codec.STRING_ARRAY, true), (i, v) -> {
                if (v == null) i.whitelist.clear(); else {
                    i.whitelist.clear();
                    i.whitelist.addAll(Arrays.asList(v));
                }
            }, i -> i.whitelist.toArray(new String[0])).add()
            .append(new KeyedCodec<>("Blacklist", Codec.STRING_ARRAY, true), (i, v) -> {
                if (v == null) i.blacklist.clear(); else {
                    i.blacklist.clear();
                    i.blacklist.addAll(Arrays.asList(v));
                }
            }, i -> i.blacklist.toArray(new String[0])).add()
            .append(new KeyedCodec<>("ArcioMode", Codec.STRING, true), (i, v) -> i.arcioMode = v == null ? "IgnoreSignal" : v, i -> i.arcioMode).add()
            .build();
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
    // Snapshotted once per tick so all visual-spawn calls share the same stable player list,
    // avoiding the thread-local spatial list being cleared mid-tick by throwItem/addEntity internals.
    private List<Ref<EntityStore>> nearbyBuffer = new ArrayList<>();
    // Fluid blocks to remove on the next tick (deferred to avoid mutating the archetype chunk mid-tick)
    private final List<long[]> pendingFluidRemovals = new ArrayList<>();

    // ── ArcIO integration ─────────────────────────────────────────────────────
    /** True when ArcIO is present on the server classpath (detected once per class load). */
    public static final boolean ARCIO_PRESENT;
    static {
        boolean found = false;
        try { Class.forName("voidbond.arcio.components.ArcioMechanismComponent"); found = true; }
        catch (ClassNotFoundException ignored) {}
        ARCIO_PRESENT = found;
    }
    /** Whether ArcIO mechanism & UUID components have been attached to this block entity. */
    private boolean arcioInitialized = false;
    /** "IgnoreSignal" (default – always run) or "EnableWhenSignal" (only run while ArcIO signal is active). */
    private String arcioMode = "IgnoreSignal";

    // Runtime filter state for this hopper (not persisted yet)
    private final List<String> whitelist = new ArrayList<>();
    private final List<String> blacklist = new ArrayList<>();
    private String filterMode = "Off";
    // Temporary per-player typed buffer (so TextField changes can be captured even if Add button doesn't send value)
    private final Map<com.hypixel.hytale.server.core.universe.PlayerRef, String> typedBuffer = new HashMap<>();

    public synchronized List<String> getWhitelist() { return new ArrayList<>(whitelist); }
    public synchronized List<String> getBlacklist() { return new ArrayList<>(blacklist); }
    public synchronized String getFilterMode() { return filterMode; }
    public synchronized void addToWhitelist(String id) { if (id != null) whitelist.add(id); }
    public synchronized void addToBlacklist(String id) { if (id != null) blacklist.add(id); }
    public synchronized void setFilterMode(String mode) { if (mode != null) filterMode = mode; }
    public synchronized String removeLastFromWhitelist() {
        if (whitelist.isEmpty()) return null;
        return whitelist.remove(whitelist.size() - 1);
    }
    public synchronized String removeLastFromBlacklist() {
        if (blacklist.isEmpty()) return null;
        return blacklist.remove(blacklist.size() - 1);
    }
    public synchronized void clearWhitelist() { whitelist.clear(); }
    public synchronized void clearBlacklist() { blacklist.clear(); }

    public synchronized String getArcioMode() { return arcioMode; }
    public synchronized void setArcioMode(String mode) { if (mode != null) arcioMode = mode; }

    public synchronized void setTypedBuffer(com.hypixel.hytale.server.core.universe.PlayerRef p, String v) {
        if (p == null) return;
        if (v == null) typedBuffer.remove(p);
        else typedBuffer.put(p, v);
    }

    public synchronized String getTypedBuffer(com.hypixel.hytale.server.core.universe.PlayerRef p) {
        if (p == null) return null;
        return typedBuffer.get(p);
    }

    // Returns true when the given block key is allowed to be imported according to filter state
    private synchronized boolean isItemAllowedByFilter(String blockKey) {
        // If mode is not set or Off, allow everything
        if (filterMode == null || filterMode.equalsIgnoreCase("Off")) return true;

        // Whitelist mode: if whitelist is empty/null, block everything. Only allow exact matches.
        if (filterMode.equalsIgnoreCase("Whitelist")) {
            if (whitelist == null || whitelist.isEmpty()) return false;
            if (blockKey == null) return false;
            for (String s : whitelist) {
                if (s != null && s.equalsIgnoreCase(blockKey)) return true;
            }
            return false;
        }

        // Blacklist mode: if blacklist is empty/null, allow everything. Block only exact matches.
        if (filterMode.equalsIgnoreCase("Blacklist")) {
            if (blacklist == null || blacklist.isEmpty()) return true;
            if (blockKey == null) return true;
            for (String s : blacklist) {
                if (s != null && s.equalsIgnoreCase(blockKey)) return false;
            }
            return true;
        }

        // Default allow
        return true;
    }

    // Try to resolve a reliable id/key string from an ItemStack using known accessors, fallback to toString()
    public synchronized String resolveItemStackKey(ItemStack stack) {
        if (stack == null) return null;
        try {
            Object probe = null;
            try { probe = stack.getBlockKey(); } catch (Throwable ignored) {}
            if (probe == null) {
                String[] candidates = new String[]{"getItemId","getItemKey","getId","getKey","getName","getBlockKey"};
                for (String m : candidates) {
                    try {
                        java.lang.reflect.Method mm = stack.getClass().getMethod(m);
                        Object v = mm.invoke(stack);
                        if (v != null) { probe = v; break; }
                    } catch (Throwable ignored) {}
                }
            }
            if (probe == null) probe = stack.toString();
            return String.valueOf(probe);
        } catch (Throwable t) {
            try { return String.valueOf(stack.toString()); } catch (Throwable ignored) { return null; }
        }
    }
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
        // Attach ArcIO components directly (null commandBuffer = direct store write, safe here
        // because onOpen is not called from within the tick-loop archetype iteration).
        if (ARCIO_PRESENT && !arcioInitialized) {
            ensureArcioComponents(world, null);
        }
        // Use the PageManager / InteractiveCustomUIPage approach to open the custom page for this hopper
        rf = store.getComponent(ref, PlayerRef.getComponentType());
        //HytaleLogger.getLogger().atInfo().log("HopperProcessor.onOpen called, playerRef=" + rf + " ref=" + ref);
        try {
            if (rf == null) {
                //HytaleLogger.getLogger().at(Level.WARNING).log("PlayerRef is null in onOpen; cannot open UI");
                return;
            }
            Vector3i pos = this.getBlockPosition();
            //HytaleLogger.getLogger().atInfo().log("HopperProcessor.onOpen: opening page for ref=" + ref + " playerRef=" + rf + " pos=" + pos);
            org.Ev0Mods.plugin.api.ui.HopperUIPage.open(rf, store, pos, null);
            //HytaleLogger.getLogger().atInfo().log("Opened HopperUIPage via HyUI PageBuilder for ref=" + ref);
        } catch (Throwable t) {
            //HytaleLogger.getLogger().at(Level.WARNING).log("Failed to open HopperUIPage: " + t.getMessage());
        }
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
        // Allow the native container UI to open — don't block it here.
        try {
            return super.canOpen(ref, componentAccessor);
        } catch (Throwable t) {
            //HytaleLogger.getLogger().at(Level.WARNING).log("HopperProcessor.canOpen encountered error: " + t.getMessage());
            return true;
        }
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

        // Apply any deferred fluid-block removals from the previous tick
        if (!pendingFluidRemovals.isEmpty()) {
            for (long[] coords : pendingFluidRemovals) {
                try {
                    WorldChunk fc = w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock((int) coords[0], (int) coords[2]));
                    if (fc != null) fc.setBlock((int) coords[0], (int) coords[1], (int) coords[2], BlockType.EMPTY);
                } catch (Exception ignored) {}
            }
            pendingFluidRemovals.clear();
        }

        // Ensure ArcIO components are registered. commandBuffer.putComponent() defers the
        // archetype migration to after the iteration loop completes, avoiding the crash.
        if (ARCIO_PRESENT && !arcioInitialized) {
            ensureArcioComponents(w, commandBuffer);
        }

        // ArcIO gate: if ArcIO is installed and the user chose "EnableWhenSignal", skip this
        // tick entirely when there is no active ArcIO signal connected to the block.
        if (ARCIO_PRESENT && "EnableWhenSignal".equals(arcioMode)) {
            if (!isArcioActive(w)) return;
        }

        // Timer logic
        this.timerV += 1.0;
        drop = this.timerV >= duration.max;
        if (drop) this.timerV = 0;

        // if (rf != null) {
        //     HudBuilder.hudForPlayer(rf).fromHtml("<div>Welcome!</div>")
        //             .show(rf, entities);
        // }

        final Vector3i pos = this.getBlockPosition();

        // Snapshot nearby players once per tick into an instance field so every spawnVisual call
        // within this tick reads from a stable ArrayList rather than re-querying the shared
        // thread-local ObjectList (which getThreadLocalReferenceList() clears on every call).
        nearbyBuffer = getAllEntitiesInBox(this, pos, data.height, (ComponentAccessor<EntityStore>) entities, data.players, data.entities, data.items);

        // Tick-based transfer control: increment counter and only act every 30 ticks.
        tickCounter++;
        int phase = tickCounter % 60; // 0..59
        boolean doExport = phase == 0;   // export on ticks 0,60,120...
        boolean doImport = phase == 30;  // import on ticks 30,90,150...


        if (doExport) {
            runExportPhase(pos, entities);
        }

        if (doImport) {
            // Import phase: try to pull from configured import faces AND collect fluids from world
            for (AdjacentSide side : this.data.importFaces) {
                final Vector3i importPos = new Vector3i(pos.x, pos.y, pos.z)
                        .add(WorldHelper.rotate(side, this.getRotationIndex()).relativePosition);
                final WorldChunk chunk = w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(importPos.x, importPos.z));
                if (chunk == null) continue;

                // IMPORT FLUID: If there's fluid at target and hopper is empty, create a bucket (collect fluid)
                // Only run if fluid transfer is enabled in config
                int targetFluidId = chunk.getFluidId(importPos.x, importPos.y, importPos.z);
                Object state = chunk.getState(importPos.x, importPos.y, importPos.z);
                boolean hasContainer = (state instanceof ItemContainerState || state instanceof ProcessingBenchState);
                ItemStack currentItem = this.getItemContainer().getItemStack((short) 0);
                
                if (Ev0Config.isFluidTransferEnabled() && targetFluidId != 0 && currentItem == null && !hasContainer) {
                    ItemStack bucketStack = null;
                    switch (targetFluidId) {
                        case 2 -> bucketStack = new ItemStack("*Container_Bucket_State_Filled_Red_Slime", 1, null);
                        case 3 -> bucketStack = new ItemStack("*Container_Bucket_State_Filled_Tar", 1, null);
                        case 4 -> bucketStack = new ItemStack("*Container_Bucket_State_Filled_Poison", 1, null);
                        case 5 -> bucketStack = new ItemStack("*Container_Bucket_State_Filled_Green_Slime", 1, null);
                        case 6 -> bucketStack = new ItemStack("*Container_Bucket_State_Filled_Lava", 1, null);
                        case 7 -> bucketStack = new ItemStack("*Container_Bucket_State_Filled_Water", 1, null);
                        default -> bucketStack = null;
                    }

                    if (bucketStack != null) {
                        this.itemContainer.addItemStackToSlot((short) 0, bucketStack);
                        // Defer setBlock to next tick — calling it during the archetype iteration
                        // can shrink the chunk and cause IndexOutOfBoundsException in the engine.
                        pendingFluidRemovals.add(new long[]{importPos.x, importPos.y, importPos.z});
                        continue;
                    }
                }

                HytaleLogger.getLogger().atInfo().log("[Hopper][Import] side=" + side + " importPos=" + importPos + " state=" + (state == null ? "null" : state.getClass().getSimpleName()) + " hasContainer=" + hasContainer);

                if (tryImportFromContainer(chunk, importPos, entities, side)) {
                    HytaleLogger.getLogger().atInfo().log("[Hopper][Import] tryImportFromContainer SUCCESS side=" + side);
                    break; // one successful import stops further faces this phase
                }

                HytaleLogger.getLogger().atInfo().log("[Hopper][Import] tryImportFromContainer failed, hasContainer=" + hasContainer + " -> will tryPickup=" + !hasContainer);
                // No matching block state at the import face — behave like a hopper:
                // pick up item entities sitting in the 1x1x1 space where the input block would be.
                if (!hasContainer && tryPickupItemEntities(importPos, entities)) {
                    HytaleLogger.getLogger().atInfo().log("[Hopper][Import] tryPickupItemEntities SUCCESS at " + importPos);
                    runExportPhase(pos, entities);
                    break;
                }
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
        //HytaleLogger.getLogger().atInfo().log("tryTransferToOrFromContainer: side=" + side + " exportPhase=" + exportPhase + " state=" + (state == null ? "null" : state.getClass().getSimpleName()));
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
            //HytaleLogger.getLogger().atInfo().log("spawnVisual: spawnPos=" + spawnPos + " item=" + safeStack);
            // Prefer helper behavior when we have nearby targets, but only on export
            if (exportPhase) {
                List<Ref<EntityStore>> nearby = nearbyBuffer;
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
                        //HytaleLogger.getLogger().atInfo().log("spawnVisual: helper returned=" + rs + " target=" + targetRef + " oppSide=" + oppSide + " safeStack=" + safeStack);
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

            // Only spawn visual if there are nearby targets in range
            if (nearbyBuffer.isEmpty()) {
                return null; // Skip visual spawn entirely when no players/entities are nearby
            }

            // Fallback: direct spawn if no targets
            Holder<EntityStore> itemEntityHolder = ItemComponent.generateItemDrop((ComponentAccessor<EntityStore>) entities, safeStack, new Vector3d(spawnPos.x, spawnPos.y, spawnPos.z), Vector3f.ZERO, 0, -1, 0);
            if (itemEntityHolder == null) {
                //HytaleLogger.getLogger().atInfo().log("spawnVisual: generateItemDrop returned null for " + safeStack + " at " + spawnPos);
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
            //HytaleLogger.getLogger().atInfo().log("spawnVisual: addEntity returned=" + spawned + " for item=" + safeStack);
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
                        // Apply filter: skip items not allowed by filter
                        String probeKeyPb = resolveItemStackKey(stack);
                        if (!isItemAllowedByFilter(probeKeyPb)) {
                            continue;
                        }
        int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), Math.min(stack.getQuantity(), MAX_STACK - hopperQty));
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
                    int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), have.getQuantity());
                    ItemStack safeStack = have.withQuantity(transferAmount);
                    for (int c = 0; c <= 1; c++) {
                        ItemContainer input = bench.getItemContainer().getContainer(c);
                        for (int slot = 0; slot < input.getCapacity(); slot++) {
                            ItemStackSlotTransaction t = input.addItemStackToSlot((short) slot, safeStack);
                            if (t.succeeded()) {
                                //HytaleLogger.getLogger().atInfo().log("spawnVisual: caller=ProcessingBenchExport side=" + side + " pos=" + pos + " safeStack=" + safeStack);
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
                // Apply filter: skip items not allowed by filter
                String probeKey = resolveItemStackKey(stack);
                if (!isItemAllowedByFilter(probeKey)) {
                    continue;
                }
                int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), Math.min(stack.getQuantity(), MAX_STACK - hopperQuantity));
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
            int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), have.getQuantity());
            ItemStack safeStack = have.withQuantity(transferAmount);
            for (int slot = 0; slot < container.getCapacity(); slot++) {
                ItemStackSlotTransaction t = container.addItemStackToSlot((short) slot, safeStack);
                if (t.succeeded()) {
                    //HytaleLogger.getLogger().atInfo().log("spawnVisual: caller=NormalExport side=" + side + " pos=" + pos + " safeStack=" + safeStack);
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

    /** Runs the full export cycle (same logic as the doExport tick phase). */
    private void runExportPhase(Vector3i pos, Store<EntityStore> entities) {
        for (AdjacentSide side : this.data.exportFaces) {
            final Vector3i exportPos = new Vector3i(pos.x, pos.y, pos.z)
                    .add(WorldHelper.rotate(side, this.getRotationIndex()).relativePosition);
            final WorldChunk chunk = w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(exportPos.x, exportPos.z));
            if (chunk == null) continue;

            Object state = chunk.getState(exportPos.x, exportPos.y, exportPos.z);
            int targetFluidId = chunk.getFluidId(exportPos.x, exportPos.y, exportPos.z);
            boolean hasContainer = (state instanceof ItemContainerState || state instanceof ProcessingBenchState);
            ItemStack currentItem = this.getItemContainer().getItemStack((short) 0);

            if (Ev0Config.isFluidTransferEnabled() && currentItem != null && !hasContainer && targetFluidId != 0) {
                String itemKey = currentItem.getBlockKey();
                if (itemKey != null && itemKey.contains("Bucket") && !itemKey.contains("Empty")) {
                    int fluidToPlace = 0;
                    if (itemKey.contains("Water")) fluidToPlace = 7;
                    else if (itemKey.contains("Lava")) fluidToPlace = 6;
                    else if (itemKey.contains("Green_Slime")) fluidToPlace = 5;
                    else if (itemKey.contains("Poison")) fluidToPlace = 4;
                    else if (itemKey.contains("Tar")) fluidToPlace = 3;
                    else if (itemKey.contains("Red_Slime")) fluidToPlace = 2;
                    if (fluidToPlace != 0) {
                        this.itemContainer.removeItemStackFromSlot((short) 0, 1);
                        this.itemContainer.addItemStackToSlot((short) 0, new ItemStack("Container_Bucket", 1, null));
                        continue;
                    }
                }
            }

            boolean transferred = tryTransferToOrFromContainer(state, exportPos, side, entities, true);

            if (!transferred && currentItem != null && !hasContainer && targetFluidId == 0) {
                if (chunk.getBlockType(exportPos.x, exportPos.y, exportPos.z) == null) {
                    chunk.setBlock(exportPos.x, exportPos.y, exportPos.z, currentItem.getBlockKey());
                    this.getItemContainer().removeItemStackFromSlot((short) 0, 1);
                }
            }

            if (this.data.exportOnce && transferred) break;
        }
    }

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

            // Apply filter: do not export items that are blocked by filter
            if (!isItemAllowedByFilter(safeStack.getBlockKey())) {
                //HytaleLogger.getLogger().atInfo().log("Hopper filter: blocking export to processing bench item=" + safeStack.getBlockKey() + " mode=" + getFilterMode());
                return false;
            }

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

            // Apply filter: do not export items that are blocked by filter
            if (!isItemAllowedByFilter(source.getBlockKey())) {
                //HytaleLogger.getLogger().atInfo().log("Hopper filter: blocking export to container item=" + source.getBlockKey() + " mode=" + getFilterMode());
                return false;
            }

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

        // Apply filter: do not export items that are blocked by filter
        if (!isItemAllowedByFilter(source.getBlockKey())) {
            //HytaleLogger.getLogger().atInfo().log("Hopper filter: blocking export to world item=" + source.getBlockKey() + " mode=" + getFilterMode());
            return false;
        }

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
                // Apply filter: skip items not allowed by filter
                if (!isItemAllowedByFilter(stack.getBlockKey())) {
                    continue;
                }

                int transferAmount =
                        (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), stack.getQuantity());
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

        // Special-case: importing from another HopperProcessor
        if (state instanceof HopperProcessor otherHopper) {
            final int MAX_STACK = 100;
            // Use same tier-based transfer as other containers
            int hopperQty = this.getItemContainer().getItemStack((short) 0) == null ? 0 : this.getItemContainer().getItemStack((short) 0).getQuantity();
            int maxTransfer = MAX_STACK - hopperQty;
            
            for (int n = 0; n < otherHopper.getItemContainer().getCapacity(); n++) {
                ItemStack otherStack = otherHopper.getItemContainer().getItemStack((short) n);
                if (otherStack == null) continue;

                // Apply filter: skip items not allowed
                String otherKey = resolveItemStackKey(otherStack);
                if (!isItemAllowedByFilter(otherKey)) {
                    continue;
                }

                // Calculate transfer amount based on tier multiplier
                int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), Math.min(otherStack.getQuantity(), maxTransfer));
                if (transferAmount <= 0) continue;

                // Try to take items from the other hopper into this hopper
                ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short) 0, otherStack.withQuantity(transferAmount));
                if (t.succeeded()) {
                    // Spawn hopper-to-hopper visual using the tick-buffered player list.
                    if (!nearbyBuffer.isEmpty()) {
                        ItemStack taken = this.getItemContainer().getItemStack((short) 0);
                        if (taken != null && !taken.isEmpty()) {
                            // Pass `side` directly — throwItem's velocity table already moves the
                            // entity in the correct direction (from the other hopper toward this one).
                            for (Ref<EntityStore> targetRef : nearbyBuffer) {
                                Ref<EntityStore> rs = ItemUtilsExtended.throwItem(
                                        this.getBlockType().getId(), side.toString(),
                                        new Vector3d(pos.x, pos.y, pos.z),
                                        targetRef, (ComponentAccessor<EntityStore>) entities,
                                        taken, Vector3d.ZERO, 0f);
                                if (rs != null) {
                                    l.add(rs);
                                    try {
                                        visualMap.put(rs, taken);
                                        Instant now = this.es != null ? this.es.getResource(WorldTimeResource.getResourceType()).getGameTime() : Instant.now();
                                        visualSpawnTimes.put(rs, now);
                                    } catch (Exception ignored) {}
                                }
                            }
                        }
                    }
                    otherHopper.getItemContainer().removeItemStackFromSlot((short) n, transferAmount);
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

            // Apply filter: skip items not allowed by filter
            String probeKey2 = resolveItemStackKey(stack);
            if (!isItemAllowedByFilter(probeKey2)) {
                continue;
            }

            int transferAmount =
                (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), stack.getQuantity());
            if (transferAmount <= 0) continue;

            ItemStack safeStack = stack.withQuantity(transferAmount);

            ItemStackSlotTransaction t =
                this.getItemContainer().addItemStackToSlot(
                    (short) 0,
                    safeStack
                );

            if (t.succeeded()) {

            // Spawn visual if nearby players are present (uses tick-buffered list)
            if (!nearbyBuffer.isEmpty()) {
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
                for (Ref<EntityStore> target2 : nearbyBuffer) {
                    Ref<EntityStore> rs = ItemUtilsExtended.throwItem(this.getBlockType().getId(), oppSide, new Vector3d(pos.x, pos.y, pos.z), target2, (ComponentAccessor<EntityStore>) entities, safeStack, Vector3d.ZERO, 0f);
                    if (rs != null) { l.add(rs); try { visualMap.put(rs, safeStack); } catch (Exception ignored) {} }
                }
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


    /**
     * Scans the 1x1x1 space at {@code importPos} for dropped item entities and
     * pulls them into this hopper's inventory, just like a vanilla hopper does
     * when there is no input block present at that face.
     */
    private boolean tryPickupItemEntities(Vector3i importPos, Store<EntityStore> entities) {
        HytaleLogger.getLogger().atInfo().log("[Hopper][Pickup] tryPickupItemEntities called at " + importPos);
        final ObjectList<Ref<EntityStore>> rawResults = SpatialResource.getThreadLocalReferenceList();
        // Dropped items are tracked under the item spatial resource (getItemSpatialResourceType),
        // not the generic entity resource. This matches how Spellbook's ConveyorState locates items.
        final Vector3d boxMin = new Vector3d(importPos.x, importPos.y, importPos.z);
        final Vector3d boxMax = new Vector3d(importPos.x + 1.0, importPos.y + 1.0, importPos.z + 1.0);
        HytaleLogger.getLogger().atInfo().log("[Hopper][Pickup] collectBox min=" + boxMin + " max=" + boxMax);
        ((ComponentAccessor<EntityStore>) entities)
                .getResource(EntityModule.get().getItemSpatialResourceType())
                .getSpatialStructure()
                .collectBox(boxMin, boxMax, rawResults);
        HytaleLogger.getLogger().atInfo().log("[Hopper][Pickup] collectBox rawResults.size()=" + rawResults.size());
        if (rawResults.isEmpty()) {
            HytaleLogger.getLogger().atInfo().log("[Hopper][Pickup] no items found in box, returning false");
            return false;
        }

        // Copy immediately — subsequent calls may reuse the same thread-local list.
        List<Ref<EntityStore>> itemRefs = new ArrayList<>(rawResults);

        int hopperQty = this.getItemContainer().getItemStack((short) 0) == null ? 0
                : this.getItemContainer().getItemStack((short) 0).getQuantity();
        HytaleLogger.getLogger().atInfo().log("[Hopper][Pickup] hopperQty=" + hopperQty + " itemRefs.size()=" + itemRefs.size());
        if (hopperQty >= 100) {
            HytaleLogger.getLogger().atInfo().log("[Hopper][Pickup] hopper full (qty=" + hopperQty + "), returning false");
            return false;
        }

        for (Ref<EntityStore> ref : itemRefs) {
            if (ref == null || !ref.isValid()) {
                HytaleLogger.getLogger().atInfo().log("[Hopper][Pickup] ref null or invalid, skipping");
                continue;
            }
            // Skip our own in-transit visual entities.
            if (l.contains(ref)) {
                HytaleLogger.getLogger().atInfo().log("[Hopper][Pickup] ref=" + ref + " is own visual, skipping");
                continue;
            }
            // Note: NOT skipping Intangible entities — real dropped items may carry that component.
            if (entities.getComponent(ref, Intangible.getComponentType()) != null) {
                HytaleLogger.getLogger().atInfo().log("[Hopper][Pickup] ref=" + ref + " has Intangible (logging only, not skipping)");
            }

            ItemComponent ic = (ItemComponent) entities.getComponent(ref, ItemComponent.getComponentType());
            if (ic == null) {
                HytaleLogger.getLogger().atInfo().log("[Hopper][Pickup] ref=" + ref + " has no ItemComponent, skipping");
                continue;
            }

            // Skip items still in their drop delay (not yet collectible).
            if (!ic.canPickUp()) {
                HytaleLogger.getLogger().atInfo().log("[Hopper][Pickup] ref=" + ref + " canPickUp()=false (drop delay active), skipping");
                continue;
            }

            ItemStack stack = ic.getItemStack();
            if (stack == null || stack.isEmpty()) {
                HytaleLogger.getLogger().atInfo().log("[Hopper][Pickup] ref=" + ref + " stack is null or empty, skipping");
                continue;
            }

            String itemKey = resolveItemStackKey(stack);
            HytaleLogger.getLogger().atInfo().log("[Hopper][Pickup] ref=" + ref + " itemKey=" + itemKey + " qty=" + stack.getQuantity());

            // Apply filter.
            if (!isItemAllowedByFilter(itemKey)) {
                HytaleLogger.getLogger().atInfo().log("[Hopper][Pickup] ref=" + ref + " BLOCKED by filter (mode=" + filterMode + ")");
                continue;
            }

            int transferAmount = (int) Math.min(
                    data.tier * Ev0Config.getTierMultiplier(),
                    Math.min(stack.getQuantity(), 100 - hopperQty)
            );
            HytaleLogger.getLogger().atInfo().log("[Hopper][Pickup] transferAmount=" + transferAmount + " for " + itemKey);
            if (transferAmount <= 0) {
                HytaleLogger.getLogger().atInfo().log("[Hopper][Pickup] transferAmount<=0, skipping");
                continue;
            }

            ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot(
                    (short) 0, stack.withQuantity(transferAmount));

            HytaleLogger.getLogger().atInfo().log("[Hopper][Pickup] addItemStackToSlot succeeded=" + t.succeeded() + " item=" + itemKey + " amount=" + transferAmount);
            if (t.succeeded()) {
                int remaining = stack.getQuantity() - transferAmount;
                HytaleLogger.getLogger().atInfo().log("[Hopper][Pickup] SUCCESS item=" + itemKey + " transferred=" + transferAmount + " remaining=" + remaining);
                if (remaining <= 0) {
                    entities.removeEntity(ref, RemoveReason.REMOVE);
                } else {
                    // Capture position before the entity is removed, then re-drop the remainder.
                    TransformComponent tc = entities.getComponent(ref, TransformComponent.getComponentType());
                    Vector3d dropPos = tc != null ? tc.getPosition().clone()
                            : new Vector3d(importPos.x + 0.5, importPos.y + 0.5, importPos.z + 0.5);
                    entities.removeEntity(ref, RemoveReason.REMOVE);
                    Holder<EntityStore> newHolder = ItemComponent.generateItemDrop(
                            (ComponentAccessor<EntityStore>) entities,
                            stack.withQuantity(remaining),
                            dropPos, Vector3f.ZERO, 0, -1, 0
                    );
                    if (newHolder != null) {
                        entities.addEntity(newHolder, AddReason.SPAWN);
                    }
                }
                return true;
            }
        }
        HytaleLogger.getLogger().atInfo().log("[Hopper][Pickup] no items collected at " + importPos + ", returning false");
        return false;
    }

    // ── ArcIO helpers ─────────────────────────────────────────────────────────

    /**
     * Registers ArcIO's {@code BlockUUIDComponent} and {@code ArcioMechanismComponent}
     * on this block entity so ArcIO can track signal state for it.
     * Only runs once; subsequent calls are no-ops.
     */
    /**
     * Ensures that the ArcIO UUID and mechanism components are attached to this block entity.
     *
     * @param commandBuffer When called from inside the tick-loop pass the tick's CommandBuffer so
     *                      that putComponent() is deferred and does not migrate the archetype while
     *                      the LegacyTickingBlockStateSystem is iterating it. Pass {@code null} when
     *                      calling from outside the tick-loop (e.g. onOpen) to write directly.
     */
    private void ensureArcioComponents(World world, @Nullable CommandBuffer<ChunkStore> commandBuffer) {
        if (arcioInitialized) return;
        try {
            Vector3i p = getBlockPosition();
            int bx = p.x, by = p.y, bz = p.z;
            Store<ChunkStore> cs = world.getChunkStore().getStore();
            Ref<ChunkStore> chunkRef = world.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(bx, bz));
            if (chunkRef == null) return;
            BlockComponentChunk bcc = (BlockComponentChunk) cs.getComponent(chunkRef, BlockComponentChunk.getComponentType());
            if (bcc == null) return;
            Ref<ChunkStore> blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn(bx, by, bz));
            if (blockRef == null) return;
            BlockUUIDComponent uuid = (BlockUUIDComponent) cs.getComponent(blockRef, BlockUUIDComponent.getComponentType());
            if (uuid == null) {
                uuid = BlockUUIDComponent.randomUUID();
                uuid.setPosition(new Vector3i(bx, by, bz));
                if (commandBuffer != null) {
                    commandBuffer.putComponent(blockRef, BlockUUIDComponent.getComponentType(), uuid);
                } else {
                    cs.putComponent(blockRef, BlockUUIDComponent.getComponentType(), uuid);
                }
                ArcioPlugin.get().putUUID(uuid.getUuid(), blockRef);
            }
            ArcioMechanismComponent mech = (ArcioMechanismComponent) cs.getComponent(blockRef, ArcioMechanismComponent.getComponentType());
            if (mech == null) {
                mech = new ArcioMechanismComponent("Hopper", 0, 1);
                if (commandBuffer != null) {
                    commandBuffer.putComponent(blockRef, ArcioMechanismComponent.getComponentType(), mech);
                } else {
                    cs.putComponent(blockRef, ArcioMechanismComponent.getComponentType(), mech);
                }
            }
            arcioInitialized = true;
        } catch (Exception e) {
            HytaleLogger.getLogger().atWarning().log("[Hopper] Failed to ensure ArcIO components: " + e.getMessage());
        }
    }

    /**
     * Returns {@code true} when the ArcIO signal on this block's own mechanism
     * meets the required threshold, or when an adjacent block with an active
     * ArcIO mechanism is found.
     */
    private boolean isArcioActive(World world) {
        try {
            Vector3i p = getBlockPosition();
            int bx = p.x, by = p.y, bz = p.z;
            Store<ChunkStore> cs = world.getChunkStore().getStore();
            Ref<ChunkStore> chunkRef = world.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(bx, bz));
            if (chunkRef != null) {
                BlockComponentChunk bcc = (BlockComponentChunk) cs.getComponent(chunkRef, BlockComponentChunk.getComponentType());
                if (bcc != null) {
                    Ref<ChunkStore> blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn(bx, by, bz));
                    if (blockRef != null) {
                        ArcioMechanismComponent mech = (ArcioMechanismComponent) cs.getComponent(blockRef, ArcioMechanismComponent.getComponentType());
                        if (mech != null && mech.getStrongestInputSignal(world) > 0) return true;
                    }
                }
            }
        } catch (Exception e) {
            HytaleLogger.getLogger().atWarning().log("[Hopper] ArcIO signal check failed: " + e.getMessage());
        }
        return hasAdjacentActiveArcioMechanism(world);
    }

    /** Checks the six orthogonal neighbors for an active ArcIO mechanism. */
    private boolean hasAdjacentActiveArcioMechanism(World world) {
        try {
            Store<ChunkStore> cs = world.getChunkStore().getStore();
            Vector3i p = getBlockPosition();
            int bx = p.x, by = p.y, bz = p.z;
            int[][] offsets = {{1,0,0},{-1,0,0},{0,1,0},{0,-1,0},{0,0,1},{0,0,-1}};
            for (int[] off : offsets) {
                int nx = bx + off[0], ny = by + off[1], nz = bz + off[2];
                Ref<ChunkStore> chunkRef = world.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(nx, nz));
                if (chunkRef == null) continue;
                BlockComponentChunk bcc = (BlockComponentChunk) cs.getComponent(chunkRef, BlockComponentChunk.getComponentType());
                if (bcc == null) continue;
                Ref<ChunkStore> blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn(nx, ny, nz));
                if (blockRef == null) continue;
                ArcioMechanismComponent mc = (ArcioMechanismComponent) cs.getComponent(blockRef, ArcioMechanismComponent.getComponentType());
                if (mc != null && mc.getStrongestInputSignal(world) > 0) return true;
            }
        } catch (Exception e) {
            HytaleLogger.getLogger().atWarning().log("[Hopper] ArcIO adjacent check failed: " + e.getMessage());
        }
        return false;
    }

    protected void reset(Instant currentTime) {
        startTime = currentTime;
    }
    @Nonnull
    public static List<Ref<EntityStore>> getAllEntitiesInBox(HopperProcessor hp, Vector3i pos, float height, @Nonnull ComponentAccessor<EntityStore> components,  boolean players, boolean entities, boolean items) {
        final ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
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
        // Return a copy so callers iterating the result are not affected if the
        // thread-local list is reused by a nested spatial query (e.g. inside throwItem).
        return new ArrayList<>(results);
    }
    public static List<Ref<EntityStore>> getAllItemsInBox(HopperProcessor hp, Vector3i pos, float height, @Nonnull ComponentAccessor<EntityStore> components,  boolean players, boolean entities, boolean items) {
        final ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
        final Vector3d min = new Vector3d(pos.x-.5, pos.y-.5 , pos.z-.5);
        final Vector3d max = new Vector3d(pos.x+.5, pos.y+.5, pos.z+.5);
        if (entities) {
            components.getResource(EntityModule.get().getEntitySpatialResourceType()).getSpatialStructure().collectBox(min, max, results);
        }
        if (players) {
            //components.getResource(EntityModule.get().getPlayerSpatialResourceType()).getSpatialStructure().collectCylinder(new Vector3d(pos.x,pos.y,pos.z), 4, 8, results );
        }
        if (items) {
            
            components.getResource(EntityModule.get().getItemSpatialResourceType()).getSpatialStructure().collectCylinder(new Vector3d(pos.x,pos.y,pos.z), 8,6,results );
        }
        hp.ca = components;
        return new ArrayList<>(results);
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

