package org.Ev0Mods.plugin.api.block.state;


import au.ellie.hyui.builders.HudBuilder;
import au.ellie.hyui.builders.HyUIAnchor;
import au.ellie.hyui.builders.LabelBuilder;
import com.hypixel.hytale.builtin.blocktick.system.ChunkBlockTickSystem;
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
import org.Ev0Mods.plugin.api.Ev0Log;
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
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerBlockState;
// Engine types removed in prerelease: use runtime reflection/name-checks instead
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
import net.crepe.inventory.IDrawerContainer;
import org.Ev0Mods.plugin.api.system.LiquidPlacingSystem;
import org.Ev0Mods.plugin.api.util.EntityHelper;
import org.Ev0Mods.plugin.api.util.ItemUtilsExtended;
import org.Ev0Mods.plugin.api.util.WorldHelper;
import org.Ev0Mods.plugin.api.Ev0Config;
import org.Ev0Mods.plugin.api.component.HopperComponent;
import au.ellie.hyui.builders.PageBuilder;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.logging.Level;

import static com.hypixel.hytale.builtin.fluid.FluidSystems.*;
import static org.bouncycastle.asn1.x500.style.BCStyle.T;


@SuppressWarnings("removal")

public class HopperProcessor implements TickableBlockState, ItemContainerBlockState{
    // Performance tuning: toggle debug logging for hopper internals
    private static final boolean PERF_DEBUG = false;

    private static void perfInfo(String msg) {
        if (PERF_DEBUG) Ev0Log.info(HytaleLogger.getLogger(), msg);
    }
    public int fluid_id = 0;
    public Rangef duration = new Rangef(0, 10);
    public float tier;
    public static final BuilderCodec<HopperProcessor> CODEC = BuilderCodec.builder(HopperProcessor.class, HopperProcessor::new)
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
    private static final ConcurrentHashMap<Class<?>, Method> ITEM_KEY_METHOD_CACHE = new ConcurrentHashMap<>();
    // Cache for reflective accessors to avoid repeated lookups in hot path
    private static final ConcurrentHashMap<Class<?>, Method> GET_ITEM_CONTAINER_METHOD_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class<?>, Method> GET_CONTAINER_FROM_ITEM_CONTAINER_METHOD_CACHE = new ConcurrentHashMap<>();
    // Reflection caches for archetype/ref/component helpers
    private static final ConcurrentHashMap<String, Method> REFLECTION_METHOD_CACHE = new ConcurrentHashMap<>();
    // Cached answer to "are any players nearby?" updated every 60 ticks to avoid
    // running the player spatial query every tick for every hopper.
    private boolean playersNearbyCached = false;
    // Fluid blocks to remove on the next tick (deferred to avoid mutating the archetype chunk mid-tick)
    private final List<long[]> pendingFluidRemovals = new ArrayList<>();

    // Engine tick tracking for fallback
    private volatile long lastEngineTick = System.currentTimeMillis();
    private volatile boolean invalidatedFlag = false;

    private static final ScheduledExecutorService FALLBACK_SCHEDULER = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "ev0-hopper-fallback");
        t.setDaemon(true);
        return t;
    });

    private static final ConcurrentHashMap<HopperProcessor, Boolean> REGISTERED_PROCESSORS = new ConcurrentHashMap<>();

    static {
        // Run a light maintenance task every 2 seconds to handle processors that the engine didn't tick.
        FALLBACK_SCHEDULER.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            for (HopperProcessor hp : REGISTERED_PROCESSORS.keySet()) {
                try {
                    long last = hp.lastEngineTick;
                    if (hp.invalidatedFlag) { REGISTERED_PROCESSORS.remove(hp); continue; }
                    if (now - last > 2000) {
                        try { hp.fallbackHeartbeat(); } catch (Throwable ignored) {}
                    }
                } catch (Throwable ignored) {}
            }
        }, 2, 2, TimeUnit.SECONDS);
    }

    // Local container storage to avoid depending on engine base state classes
    private ItemContainer itemContainer;

    public ItemContainer getItemContainer() {
        return this.itemContainer;
    }

    public void setItemContainer(ItemContainer c) {
        this.itemContainer = c;
    }

    // Reflection helpers to avoid compile-time dependency on engine state classes
    private Object getItemContainerFromState(Object stateObj) {
        if (stateObj == null) return null;
        Class<?> cls = stateObj.getClass();
        if (GET_ITEM_CONTAINER_METHOD_CACHE.containsKey(cls)) {
            Method cached = GET_ITEM_CONTAINER_METHOD_CACHE.get(cls);
            if (cached == null) return null;
            try { return cached.invoke(stateObj); } catch (Throwable ignored) { return null; }
        }

        Method found = null;
        try {
            found = cls.getMethod("getItemContainer");
        } catch (Throwable ignored) {}
        if (found == null) {
            try { found = cls.getMethod("itemContainer"); } catch (Throwable ignored) {}
        }
        // Cache even null to avoid repeated lookups
        GET_ITEM_CONTAINER_METHOD_CACHE.put(cls, found);
        if (found == null) return null;
        try { return found.invoke(stateObj); } catch (Throwable ignored) { return null; }
    }

    private ItemContainer getContainerFromItemContainerObject(Object itemContainerObj, int idx) {
        if (itemContainerObj == null) return null;
        if (itemContainerObj instanceof ItemContainer) return (ItemContainer) itemContainerObj;
        Class<?> cls = itemContainerObj.getClass();
        if (GET_CONTAINER_FROM_ITEM_CONTAINER_METHOD_CACHE.containsKey(cls)) {
            Method cached = GET_CONTAINER_FROM_ITEM_CONTAINER_METHOD_CACHE.get(cls);
            if (cached == null) return null;
            try {
                Object r = cached.invoke(itemContainerObj, idx);
                if (r instanceof ItemContainer) return (ItemContainer) r;
            } catch (Throwable ignored) { return null; }
        }

        Method found = null;
        try { found = cls.getMethod("getContainer", int.class); } catch (Throwable ignored) {}
        if (found == null) {
            try { found = cls.getMethod("container", int.class); } catch (Throwable ignored) {}
        }
        GET_CONTAINER_FROM_ITEM_CONTAINER_METHOD_CACHE.put(cls, found);
        if (found == null) return null;
        try {
            Object r = found.invoke(itemContainerObj, idx);
            if (r instanceof ItemContainer) return (ItemContainer) r;
        } catch (Throwable ignored) {}
        return null;
    }

    // Engine-derived helpers (best-effort defaults when engine isn't wiring these in)
    public Vector3i getBlockPosition() {
        // Try to call a superclass-provided position accessor (engine may supply it)
        try {
            Class<?> sc = this.getClass().getSuperclass();
            if (sc != null) {
                for (String name : new String[]{"getBlockPosition", "getPosition", "getPos", "position"}) {
                    try {
                        java.lang.reflect.Method m = sc.getMethod(name);
                        if (m != null) {
                            Object r = m.invoke(this);
                            if (r instanceof Vector3i) return (Vector3i) r;
                        }
                    } catch (Throwable ignored) {}
                }
            }
        } catch (Throwable ignored) {}
        return new Vector3i(0,0,0);
    }

    public int getRotationIndex() {
        return 0;
    }

    public BlockType getBlockType() {
        return BlockType.EMPTY;
    }

    // ── ArcIO integration ─────────────────────────────────────────────────────
    /** True when ArcIO is present on the server classpath (detected once per class load). */
    public static final boolean ARCIO_PRESENT;
    static {
        boolean found = false;
        try { Class.forName("voidbond.arcio.components.ArcioMechanismComponent"); found = true; }
        catch (ClassNotFoundException ignored) {}
        ARCIO_PRESENT = found;
    }

    // ── Simple Drawers integration ────────────────────────────────────────────
    /** True when Simple Drawers is present on the server classpath (detected once per class load). */
    public static final boolean SIMPLE_DRAWERS_PRESENT;
    static {
        boolean found = false;
        try { Class.forName("net.crepe.inventory.IDrawerContainer"); found = true; }
        catch (ClassNotFoundException ignored) {}
        SIMPLE_DRAWERS_PRESENT = found;
    }
    /** Whether ArcIO mechanism & UUID components have been attached to this block entity. */
    private boolean arcioInitialized = false;
    /** "IgnoreSignal" (default – always run) or "EnableWhenSignal" (only run while ArcIO signal is active). */
    private String arcioMode = "IgnoreSignal";

    // Runtime filter state for this hopper (not persisted yet)
    private final List<String> whitelist = Collections.synchronizedList(new ArrayList<>());
    private final List<String> blacklist = Collections.synchronizedList(new ArrayList<>());
    private volatile String filterMode = "Off";
    // Temporary per-player typed buffer (so TextField changes can be captured even if Add button doesn't send value)
    private final Map<com.hypixel.hytale.server.core.universe.PlayerRef, String> typedBuffer = new ConcurrentHashMap<>();

    public List<String> getWhitelist() { synchronized (whitelist) { return new ArrayList<>(whitelist); } }
    public List<String> getBlacklist() { synchronized (blacklist) { return new ArrayList<>(blacklist); } }
    public String getFilterMode() { return filterMode; }
    public void addToWhitelist(String id) { if (id != null) whitelist.add(id); }
    public void addToBlacklist(String id) { if (id != null) blacklist.add(id); }
    public void setFilterMode(String mode) { if (mode != null) filterMode = mode; }
    public String removeLastFromWhitelist() {
        synchronized (whitelist) {
            if (whitelist.isEmpty()) return null;
            return whitelist.remove(whitelist.size() - 1);
        }
    }
    public String removeLastFromBlacklist() {
        synchronized (blacklist) {
            if (blacklist.isEmpty()) return null;
            return blacklist.remove(blacklist.size() - 1);
        }
    }
    public void clearWhitelist() { whitelist.clear(); }
    public void clearBlacklist() { blacklist.clear(); }

    public String getArcioMode() { return arcioMode; }
    public void setArcioMode(String mode) { if (mode != null) arcioMode = mode; }

    public void setTypedBuffer(com.hypixel.hytale.server.core.universe.PlayerRef p, String v) {
        if (p == null) return;
        if (v == null) typedBuffer.remove(p);
        else typedBuffer.put(p, v);
    }

    public String getTypedBuffer(com.hypixel.hytale.server.core.universe.PlayerRef p) {
        if (p == null) return null;
        return typedBuffer.get(p);
    }

    public boolean isSingletonMode() {
        return "Singleton".equalsIgnoreCase(filterMode);
    }

    // Returns true when the given block key is allowed to be imported according to filter state
    private boolean isItemAllowedByFilter(String blockKey) {
        // If mode is not set or Off or Singleton, allow everything
        if (filterMode == null || filterMode.equalsIgnoreCase("Off") || filterMode.equalsIgnoreCase("Singleton")) return true;

        // Whitelist mode: if whitelist is empty/null, block everything. Only allow exact matches.
        if (filterMode.equalsIgnoreCase("Whitelist")) {
            synchronized (whitelist) {
                if (whitelist == null || whitelist.isEmpty()) return false;
                if (blockKey == null) return false;
                for (String s : whitelist) {
                    if (s != null && s.equalsIgnoreCase(blockKey)) return true;
                }
                return false;
            }
        }

        // Blacklist mode: if blacklist is empty/null, allow everything. Block only exact matches.
        if (filterMode.equalsIgnoreCase("Blacklist")) {
            synchronized (blacklist) {
                if (blacklist == null || blacklist.isEmpty()) return true;
                if (blockKey == null) return true;
                for (String s : blacklist) {
                    if (s != null && s.equalsIgnoreCase(blockKey)) return false;
                }
                return true;
            }
        }

        // Default allow
        return true;
    }

    // Try to resolve a reliable id/key string from an ItemStack using known accessors, fallback to toString()
    public String resolveItemStackKey(ItemStack stack) {
        if (stack == null) return null;
        try {
            Object probe = null;
            try { probe = stack.getBlockKey(); } catch (Throwable ignored) {}
            if (probe == null) {
                Class<?> cls = stack.getClass();
                Method m = ITEM_KEY_METHOD_CACHE.get(cls);
                if (m == null && !ITEM_KEY_METHOD_CACHE.containsKey(cls)) {
                    Method found = null;
                    String[] candidates = new String[]{"getItemId","getItemKey","getId","getKey","getName","getBlockKey"};
                    for (String name : candidates) {
                        try {
                            found = cls.getMethod(name);
                            if (found != null) break;
                        } catch (Throwable ignored) {}
                    }
                    ITEM_KEY_METHOD_CACHE.put(cls, found); // cache even null to avoid repeated lookups
                    m = found;
                }
                if (m != null) {
                    try {
                        Object v = m.invoke(stack);
                        if (v != null) probe = v;
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

    public boolean initialize(BlockType blockType) {
        boolean superInit = true;
        try {
            java.lang.reflect.Method m = this.getClass().getSuperclass().getMethod("initialize", BlockType.class);
            if (m != null) {
                Object r = m.invoke(this, blockType);
                if (r instanceof Boolean) superInit = (Boolean) r;
            }
        } catch (Throwable ignored) {}

        if (superInit && blockType != null && blockType.getState() instanceof Data data) {
            this.data = data;
            setItemContainer(new SimpleItemContainer((short) 1));
            // Ensure fallback scheduler is aware of this processor even if the engine doesn't tick it yet
            try {
                REGISTERED_PROCESSORS.put(this, Boolean.TRUE);
                this.lastEngineTick = System.currentTimeMillis();
            } catch (Throwable ignored) {}
            return true;
        }

        return false;

    }

    public boolean canOpen(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl ComponentAccessor<EntityStore> componentAccessor) {
        // Allow the native container UI to open — don't block it here.
        try {
            java.lang.reflect.Method m = this.getClass().getSuperclass().getMethod("canOpen", Ref.class, ComponentAccessor.class);
            if (m != null) {
                Object r = m.invoke(this, ref, componentAccessor);
                if (r instanceof Boolean) return (Boolean) r;
            }
        } catch (Throwable ignored) {
            // fallback
        }
        return true;
    }

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

        try {
            java.lang.reflect.Method m = this.getClass().getSuperclass().getMethod("onDestroy");
            if (m != null) m.invoke(this);
        } catch (Throwable ignored) {}


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


    public void tick(float dt, int index, ArchetypeChunk<ChunkStore> archeChunk,
                     Store<ChunkStore> store, CommandBuffer<ChunkStore> commandBuffer) {

        // Diagnostic: log when engine invokes tick
        try { HytaleLogger.getLogger().atWarning().log("[Ev0Lib][DIAG] HopperProcessor.tick invoked for instance=" + this + " index=" + index); } catch (Throwable ignored) {}
        // mark that the engine invoked our tick so fallback knows it's running
        lastEngineTick = System.currentTimeMillis();
        REGISTERED_PROCESSORS.put(this, Boolean.TRUE);

        // Migrate StateData -> HopperComponent on first tick for this block when possible
        try {
            Object ref = getRefFromArchetype(archeChunk, index);
            if (ref != null) {
                HopperComponent existing = getHopperComponent(store, ref);
                if (existing == null) {
                    // create new component from legacy data if present
                    if (this.data != null) {
                        HopperComponent hc = new HopperComponent();
                        hc.data = this.data;
                        putHopperComponent(store, ref, hc);
                        // Clear local copy to prefer component-backed storage going forward
                        this.data = null;
                    }
                }
            }
        } catch (Throwable ignored) {}

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

        // Defer spatial queries until we know visuals may be spawned. This avoids
        // running an expensive spatial collect on every tick for every hopper.
        nearbyBuffer.clear();
        // Tick-based transfer control: increment counter and only act every 90 ticks (cycle length 180).
        // We multiply the previous 60-tick cycle by 3 to reduce heavy calls frequency.
        tickCounter++;
        int phase = tickCounter % 180; // 0..179
        boolean doExport = phase == 0;   // export on ticks 0,180,360...
        boolean doImport = phase == 90;  // import on ticks 90,270,450...

        // Update cached nearby-player state once every 180 ticks to reduce query cost.
        if (tickCounter % 180 == 0) {
            try {
                final java.util.List rawPlayers = (java.util.List) SpatialResource.getThreadLocalReferenceList();
                final Vector3d center = new Vector3d(pos.x, pos.y, pos.z);
                ((ComponentAccessor<EntityStore>) entities)
                        .getResource(EntityModule.get().getPlayerSpatialResourceType())
                        .getSpatialStructure()
                        .collectCylinder(center, 4, Math.max(1.0f, data.height), rawPlayers);
                playersNearbyCached = !rawPlayers.isEmpty();
                rawPlayers.clear();
            } catch (Exception ignored) {}
        }


        if (doExport) {
            // Only compute nearby entities if we have items to export *and* players are nearby
            ItemStack have = this.getItemContainer().getItemStack((short) 0);
            if (have != null && playersNearbyCached) {
                nearbyBuffer = getAllEntitiesInBox(this, pos, data.height, (ComponentAccessor<EntityStore>) entities, data.players, data.entities, data.items);
            } else {
                nearbyBuffer.clear();
            }
            runExportPhase(pos, entities);
        }

        if (doImport) {
            // Only compute nearby entities if hopper has room to import items AND players are nearby
            ItemStack have2 = this.getItemContainer().getItemStack((short) 0);
            boolean hopperHasSpace = (have2 == null) || (have2.getQuantity() < 100);
            if (hopperHasSpace && playersNearbyCached) {
                nearbyBuffer = getAllEntitiesInBox(this, pos, data.height, (ComponentAccessor<EntityStore>) entities, data.players, data.entities, data.items);
            } else {
                nearbyBuffer.clear();
            }
            // Import phase: try to pull from configured import faces AND collect fluids from world
            for (AdjacentSide side : this.data.importFaces) {
                final Vector3i importPos = new Vector3i(pos.x, pos.y, pos.z)
                        .add(WorldHelper.rotate(side, this.getRotationIndex()).relativePosition);
                final WorldChunk chunk = w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(importPos.x, importPos.z));
                if (chunk == null) continue;

                // IMPORT FLUID: If there's fluid at target and hopper is empty, create a bucket (collect fluid)
                // Only run if fluid transfer is enabled in config
                int targetFluidId = org.Ev0Mods.plugin.api.component.EngineCompat.getFluidId(chunk, importPos.x, importPos.y, importPos.z);
                Object state = org.Ev0Mods.plugin.api.component.EngineCompat.getState(chunk, importPos.x, importPos.y, importPos.z);
                boolean hasContainer = (state != null && (state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState") || state.getClass().getSimpleName().contains("ItemContainer")));
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

                perfInfo("[Hopper][Import] side=" + side + " importPos=" + importPos + " state=" + (state == null ? "null" : state.getClass().getSimpleName()) + " hasContainer=" + hasContainer);

                if (tryImportFromContainer(chunk, importPos, entities, side)) {
                    perfInfo("[Hopper][Import] tryImportFromContainer SUCCESS side=" + side);
                    break; // one successful import stops further faces this phase
                }

                perfInfo("[Hopper][Import] tryImportFromContainer failed, hasContainer=" + hasContainer + " -> will tryPickup=" + !hasContainer);
                // No matching block state at the import face — behave like a hopper:
                // pick up item entities sitting in the 1x1x1 space where the input block would be.
                if (!hasContainer && tryPickupItemEntities(importPos, entities)) {
                    perfInfo("[Hopper][Import] tryPickupItemEntities SUCCESS at " + importPos);
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
        if (state == null || (!(state != null && state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState")) && !state.getClass().getSimpleName().contains("ItemContainer")))
            return false;

        boolean isProcessingBench = (state != null && state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState"));
        Object bench = isProcessingBench ? state : null;

        // Visual spawn moved to helper method to avoid lambda/metafactory churn

        // ProcessingBench handling: respect phase
        if (isProcessingBench) {
            ItemContainer output = getContainerFromItemContainerObject(getItemContainerFromState(bench), 2);
            if (!exportPhase) {
                // Import phase: pull from output into hopper if the hopper slot can accept items.
                // Let addItemStackToSlot decide capacity — supports containers with custom stack limits.
                for (int slot = 0; slot < output.getCapacity(); slot++) {
                    ItemStack stack = output.getItemStack((short) slot);
                    if (stack == null) continue;
                    // Apply filter: skip items not allowed by filter (fast path via getBlockKey)
                    String probeKeyPb = null;
                    try { probeKeyPb = stack.getBlockKey(); } catch (Throwable ignored) {}
                    if (probeKeyPb == null) probeKeyPb = resolveItemStackKey(stack);
                    if (!isItemAllowedByFilter(probeKeyPb)) {
                        continue;
                    }
                    int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), stack.getQuantity());
                    if (transferAmount <= 0) continue;
                    ItemStack safeStack = stack.withQuantity(transferAmount);
                    ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short) 0, safeStack);
                    if (t.succeeded()) {
                        output.removeItemStackFromSlot((short) slot, transferAmount);
                        return true;
                    }
                }
            } else {
                // Export phase: push from hopper into input containers 0 and 1
                ItemStack have = this.getItemContainer().getItemStack((short) 0);
                if (have != null && have.getQuantity() > 0) {
                    int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), have.getQuantity());
                    ItemStack safeStack = have.withQuantity(transferAmount);
                    for (int c = 0; c <= 1; c++) {
                        ItemContainer input = getContainerFromItemContainerObject(getItemContainerFromState(bench), c);
                        for (int slot = 0; slot < input.getCapacity(); slot++) {
                            ItemStackSlotTransaction t = input.addItemStackToSlot((short) slot, safeStack);
                            if (t.succeeded()) {
                                //HytaleLogger.getLogger().atInfo().log("spawnVisual: caller=ProcessingBenchExport side=" + side + " pos=" + pos + " safeStack=" + safeStack);
                                spawnVisualFor(safeStack, exportPhase, pos, side, entities);
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
        // Capacity is determined by addItemStackToSlot itself — supports containers like Simple Drawers
        // that override their own max stack, rather than relying on a hardcoded limit.
        Object containerObj = getItemContainerFromState(state);
        ItemContainer container = containerObj instanceof ItemContainer ? (ItemContainer) containerObj : null;

        // Simple Drawers: use the IDrawerContainer API so transfer respects each slot's
        // actual capacity (which can be thousands on upgraded drawers/controllers).
        if (SIMPLE_DRAWERS_PRESENT && container instanceof IDrawerContainer drawerContainer) {
            if (!exportPhase) {
                // Import: pull from drawer slot into hopper using the drawer's slot API
                for (short slot = 0; slot < drawerContainer.getSlotCount(); slot++) {
                    ItemStack slotItem = drawerContainer.getSlotItem(slot);
                    int slotQty = drawerContainer.getSlotQuantity(slot);
                    if (slotItem == null || slotQty <= 0) continue;
                    String probeKey = null;
                    try { probeKey = slotItem.getBlockKey(); } catch (Throwable ignored) {}
                    if (probeKey == null) probeKey = resolveItemStackKey(slotItem);
                    if (!isItemAllowedByFilter(probeKey)) continue;
                    int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), slotQty);
                    if (transferAmount <= 0) continue;
                    ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short) 0, slotItem.withQuantity(transferAmount));
                    if (t.succeeded()) {
                        final short fSlot = slot;
                        final int fNewQty = slotQty - transferAmount;
                        final ItemStack fSlotItem = slotItem;
                        drawerContainer.writeAction(() -> {
                            drawerContainer.setSlot(fSlot, fSlotItem.withQuantity(fNewQty));
                            return null;
                        });
                        return true;
                    }
                }
                return false;
            } else {
                // Export: push from hopper into drawer using only IDrawerContainer interface methods.
                ItemStack have = this.getItemContainer().getItemStack((short) 0);
                if (have != null && have.getQuantity() > 0) {
                    int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), have.getQuantity());

                    // Pass 1: prefer an existing slot whose locked type matches the incoming item.
                    short matchedSlot = -1;
                    int matchedQty = 0;
                    int matchedCap = 0;
                    for (short slot = 0; slot < drawerContainer.getSlotCount(); slot++) {
                        ItemStack slotItem = drawerContainer.getSlotItem(slot);
                        if (ItemStack.isEmpty(slotItem)) continue; // uninitialized — skip in pass 1
                        // Type must match first (same check SD does internally)
                        if (!slotItem.isStackableWith(have)) continue;
                        int slotQty = drawerContainer.getSlotQuantity(slot);
                        int slotCap = drawerContainer.getSlotStackCapacity(slot);
                        if (slotCap - slotQty <= 0) continue;
                        if (drawerContainer.testCantAddToSlot(slot, have, slotItem)) continue;
                        matchedSlot = slot;
                        matchedQty = slotQty;
                        matchedCap = slotCap;
                        break;
                    }

                    // Pass 2: if nothing matched, fall back to the first truly empty slot.
                    if (matchedSlot == -1) {
                        for (short slot = 0; slot < drawerContainer.getSlotCount(); slot++) {
                            ItemStack slotItem = drawerContainer.getSlotItem(slot);
                            if (!ItemStack.isEmpty(slotItem)) continue; // already has a type lock — skip
                            int slotQty = drawerContainer.getSlotQuantity(slot);
                            int slotCap = drawerContainer.getSlotStackCapacity(slot);
                            if (slotCap - slotQty <= 0) continue;
                            matchedSlot = slot;
                            matchedQty = slotQty;
                            matchedCap = slotCap;
                            break;
                        }
                    }

                    if (matchedSlot != -1) {
                        int room = matchedCap - matchedQty;
                        int actualTransfer = Math.min(transferAmount, room);
                        if (actualTransfer > 0) {
                            final short fSlot = matchedSlot;
                            final int fNewQty = matchedQty + actualTransfer;
                            final int fActual = actualTransfer;
                            final ItemStack fHave = have;
                            drawerContainer.writeAction(() -> {
                                drawerContainer.setSlot(fSlot, fHave.withQuantity(fNewQty));
                                return null;
                            });
                            spawnVisualFor(have.withQuantity(fActual), exportPhase, pos, side, entities);
                            this.getItemContainer().removeItemStackFromSlot((short) 0, fActual);
                            return true;
                        }
                    }
                }
                return false;
            }
        }

        // Import (only during import phase)
        if (!exportPhase) {
            for (int slot = 0; slot < container.getCapacity(); slot++) {
                ItemStack stack = container.getItemStack((short) slot);
                if (stack == null) continue;
                // Apply filter: skip items not allowed by filter (fast path)
                String probeKey = null;
                try { probeKey = stack.getBlockKey(); } catch (Throwable ignored) {}
                if (probeKey == null) probeKey = resolveItemStackKey(stack);
                if (!isItemAllowedByFilter(probeKey)) {
                    continue;
                }
                int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), stack.getQuantity());
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
                    spawnVisualFor(safeStack, exportPhase, pos, side, entities);
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

    // Extracted visual spawn logic to a method to avoid per-call lambda allocation
    private void spawnVisualFor(ItemStack safeStack, boolean exportPhase, Vector3i pos, AdjacentSide side, Store<EntityStore> entities) {
        if (safeStack == null || safeStack.isEmpty()) return;
        Vector3i rel = WorldHelper.rotate(side, this.getRotationIndex()).relativePosition;
        Vector3i hopperBlock = this.getBlockPosition();
        Vector3d hopperCenter = new Vector3d(hopperBlock.x + 0.5 +1, hopperBlock.y + 0.5, hopperBlock.z + 0.5);
        Vector3d sourceCenter = new Vector3d(pos.x + 0.5 +1, pos.y + 0.5, pos.z + 0.5);
        Vector3d spawnPos = exportPhase ? hopperCenter : sourceCenter;
        Vector3d velocity = exportPhase ? new Vector3d(-rel.x * 0.35, 0.25, -rel.z * 0.35) : new Vector3d(rel.x * 0.35, 0.25, rel.z * 0.35);

        if (exportPhase) {
            List<Ref<EntityStore>> nearby = nearbyBuffer;
            if (!nearby.isEmpty()) {
                boolean anySpawned = false;
                for (Ref<EntityStore> targetRef : nearby) {
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
                    if (rs != null) {
                        l.add(rs);
                        try {
                            visualMap.put(rs, safeStack);
                            Instant now = entities != null ? entities.getResource(WorldTimeResource.getResourceType()).getGameTime() : Instant.now();
                            visualSpawnTimes.put(rs, now);
                        } catch (Exception ignored) {}
                        anySpawned = true;
                    }
                }
                if (anySpawned) return;
            }
        }

        if (nearbyBuffer.isEmpty()) return;

        Holder<EntityStore> itemEntityHolder = ItemComponent.generateItemDrop((ComponentAccessor<EntityStore>) entities, safeStack, new Vector3d(spawnPos.x, spawnPos.y, spawnPos.z), Vector3f.ZERO, 0, -1, 0);
        if (itemEntityHolder == null) return;

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
            TransformComponent tc = entities.getComponent(spawned, TransformComponent.getComponentType());
            if (tc != null) tc.setPosition(new Vector3d(spawnPos.x, spawnPos.y, spawnPos.z));
            l.add(spawned);
            try {
                visualMap.put(spawned, safeStack);
                Instant now = entities != null ? entities.getResource(WorldTimeResource.getResourceType()).getGameTime() : Instant.now();
                visualSpawnTimes.put(spawned, now);
            } catch (Exception ignored) {}
        }
    }



/* ============================================================
   EXPORT
   ============================================================ */

    /** Runs the full export cycle (same logic as the doExport tick phase). */
    private void runExportPhase(Vector3i pos, Store<EntityStore> entities) {
        // Fast-path: if hopper is empty, skip work entirely (most common case).
        ItemStack currentItemFast = this.getItemContainer().getItemStack((short) 0);
        if (currentItemFast == null) return;

        for (AdjacentSide side : this.data.exportFaces) {
            final Vector3i exportPos = new Vector3i(pos.x, pos.y, pos.z)
                    .add(WorldHelper.rotate(side, this.getRotationIndex()).relativePosition);
            final WorldChunk chunk = w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(exportPos.x, exportPos.z));
            if (chunk == null) continue;

            Object state = org.Ev0Mods.plugin.api.component.EngineCompat.getState(chunk, exportPos.x, exportPos.y, exportPos.z);
            int targetFluidId = org.Ev0Mods.plugin.api.component.EngineCompat.getFluidId(chunk, exportPos.x, exportPos.y, exportPos.z);
            boolean hasContainer = (state != null && (state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState") || state.getClass().getSimpleName().contains("ItemContainer")));
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
                if (org.Ev0Mods.plugin.api.component.EngineCompat.getBlockType(chunk, exportPos.x, exportPos.y, exportPos.z) == null) {
                    org.Ev0Mods.plugin.api.component.EngineCompat.setBlock(chunk, exportPos.x, exportPos.y, exportPos.z, currentItem.getBlockKey());
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

        Object state = org.Ev0Mods.plugin.api.component.EngineCompat.getState(chunk, pos.x, pos.y, pos.z);
        if (state != null && state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState")) {

            Object bench = state;

            ItemStack sourcex = this.getItemContainer().getItemStack((short) 0);
            if (sourcex == null) return false;

            int transferAmount = (int) Math.min(data.tier * 2, sourcex.getQuantity());
            if (transferAmount <= 0) return false;

            ItemStack safeStack = sourcex.withQuantity(transferAmount);

            // Apply filter: do not export items that are blocked by filter
            if (!isItemAllowedByFilter(safeStack.getBlockKey())) {
                return false;
            }

            // Only input containers (0 and 1)
            for (int c = 0; c <= 1; c++) {

                ItemContainer input = getContainerFromItemContainerObject(getItemContainerFromState(bench), c);
                if (input == null) continue;

                for (int slot = 0; slot < input.getCapacity(); slot++) {

                    ItemStackSlotTransaction t = input.addItemStackToSlot((short) slot, safeStack);

                    if (t.succeeded()) {

                        this.getItemContainer().removeItemStackFromSlot((short) 0, transferAmount);

                        return true; // one batch per tick
                    }
                }
            }

            return false;
        }


        Object containerStateObj = null;
        try {
            java.lang.reflect.Method m = state.getClass().getMethod("getItemContainer");
            containerStateObj = m.invoke(state);
        } catch (Throwable ignored) {}
        if (containerStateObj == null) return false;
        ItemContainer target = (ItemContainer) containerStateObj;

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
        if (org.Ev0Mods.plugin.api.component.EngineCompat.getBlockType(chunk, pos.x, pos.y, pos.z) == null) {
            // Only place a block when the target position currently contains a fluid (liquid).
            int fluidId = org.Ev0Mods.plugin.api.component.EngineCompat.getFluidId(chunk, pos.x, pos.y, pos.z);
            if (fluidId != 0) {
                org.Ev0Mods.plugin.api.component.EngineCompat.setBlock(chunk, pos.x, pos.y, pos.z,
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
        // Fast path: if this hopper is already full, skip scanning containers entirely.
        ItemStack destStack = this.getItemContainer().getItemStack((short) 0);
        if (destStack != null && destStack.getQuantity() >= 100) return false;

        Object state = org.Ev0Mods.plugin.api.component.EngineCompat.getState(chunk, pos.x, pos.y, pos.z);

    /* ---------------------------------
       Special handling: Processing Bench
       --------------------------------- */
        if (state != null && state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState")) {

            // Only import from output container (2)
            ItemContainer output = getContainerFromItemContainerObject(getItemContainerFromState(state), 2);
            if (output == null) return false;

            int outCap = output.getCapacity();
            for (int slot = 0; slot < outCap; slot++) {
            ItemStack stack = output.getItemStack((short) slot);
            if (stack == null) continue;
            // Apply filter: prefer fast path via getBlockKey() then fallback to resolve
            String blockKey = null;
            try { blockKey = stack.getBlockKey(); } catch (Throwable ignored) {}
            if (blockKey == null) blockKey = resolveItemStackKey(stack);
            if (!isItemAllowedByFilter(blockKey)) continue;

                int available = stack.getQuantity();
                // Singleton mode: leave at least 1 item in the source slot so auto-refill mods keep working
                if (isSingletonMode() && available <= 1) continue;
                int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), isSingletonMode() ? available - 1 : available);
                if (transferAmount <= 0) continue;

                ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short) 0, stack.withQuantity(transferAmount));

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

                        output.removeItemStackFromSlot((short) slot, transferAmount);

                    return true; // one batch per tick
                }
            }

            return false;
        }

        // Special-case: importing from another HopperProcessor
        if (state instanceof HopperProcessor otherHopper) {
            // Let addItemStackToSlot decide whether the hopper slot can accept — no hardcoded cap.
            int otherCap = otherHopper.getItemContainer().getCapacity();
            for (int n = 0; n < otherCap; n++) {
                ItemStack otherStack = otherHopper.getItemContainer().getItemStack((short) n);
                if (otherStack == null) continue;

                String otherKey = null;
                try { otherKey = otherStack.getBlockKey(); } catch (Throwable ignored) {}
                if (otherKey == null) otherKey = resolveItemStackKey(otherStack);
                if (!isItemAllowedByFilter(otherKey)) continue;

                int otherAvailable = otherStack.getQuantity();
                // Singleton mode: leave at least 1 item in the other hopper's slot
                if (isSingletonMode() && otherAvailable <= 1) continue;
                int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), isSingletonMode() ? otherAvailable - 1 : otherAvailable);
                if (transferAmount <= 0) continue;

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
        Object containerStateObj = null;
        try {
            java.lang.reflect.Method m = state.getClass().getMethod("getItemContainer");
            containerStateObj = m.invoke(state);
        } catch (Throwable ignored) {}
        if (containerStateObj == null) return false;
        ItemContainer sourceContainer = (ItemContainer) containerStateObj;

        // Simple Drawers: use the IDrawerContainer API so we read the actual slot quantity
        // (which can be thousands on upgraded drawers/controllers) and remove via setSlot.
        if (SIMPLE_DRAWERS_PRESENT && sourceContainer instanceof IDrawerContainer drawerContainer) {
            for (short slot = 0; slot < drawerContainer.getSlotCount(); slot++) {
                ItemStack slotItem = drawerContainer.getSlotItem(slot);
                int slotQty = drawerContainer.getSlotQuantity(slot);
                if (slotItem == null || slotQty <= 0) continue;
                String probeKey2 = null;
                try { probeKey2 = slotItem.getBlockKey(); } catch (Throwable ignored) {}
                if (probeKey2 == null) probeKey2 = resolveItemStackKey(slotItem);
                if (!isItemAllowedByFilter(probeKey2)) continue;
                // Singleton mode: leave at least 1 item in the drawer slot
                if (isSingletonMode() && slotQty <= 1) continue;
                int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), isSingletonMode() ? slotQty - 1 : slotQty);
                if (transferAmount <= 0) continue;
                ItemStack safeStack = slotItem.withQuantity(transferAmount);
                ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short) 0, safeStack);
                if (t.succeeded()) {
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
                    }
                    final short fSlot = slot;
                    final int fNewQty = slotQty - transferAmount;
                    final ItemStack fSlotItem = slotItem;
                    drawerContainer.writeAction(() -> {
                        drawerContainer.setSlot(fSlot, fSlotItem.withQuantity(fNewQty));
                        return null;
                    });
                    return true;
                }
            }
            return false;
        }

        for (int slot = 0; slot < sourceContainer.getCapacity(); slot++) {

            ItemStack stack =
                sourceContainer.getItemStack((short) slot);
            if (stack == null) continue;

            // Apply filter: skip items not allowed by filter (fast path)
            String probeKey2 = null;
            try { probeKey2 = stack.getBlockKey(); } catch (Throwable ignored) {}
            if (probeKey2 == null) probeKey2 = resolveItemStackKey(stack);
            if (!isItemAllowedByFilter(probeKey2)) {
                continue;
            }

            int srcAvailable = stack.getQuantity();
            // Singleton mode: leave at least 1 item in each source slot
            if (isSingletonMode() && srcAvailable <= 1) continue;
            int transferAmount =
                (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), isSingletonMode() ? srcAvailable - 1 : srcAvailable);
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
        perfInfo("[Hopper][Pickup] tryPickupItemEntities called at " + importPos);
        final java.util.List rawResults = (java.util.List) SpatialResource.getThreadLocalReferenceList();
        final Vector3d boxMin = new Vector3d(importPos.x, importPos.y, importPos.z);
        final Vector3d boxMax = new Vector3d(importPos.x + 1.0, importPos.y + 1.0, importPos.z + 1.0);
        perfInfo("[Hopper][Pickup] collectBox min=" + boxMin + " max=" + boxMax);
        ((ComponentAccessor<EntityStore>) entities)
            .getResource(EntityModule.get().getItemSpatialResourceType())
            .getSpatialStructure()
            .collectBox(boxMin, boxMax, rawResults);
        perfInfo("[Hopper][Pickup] collectBox rawResults.size()=" + rawResults.size());
        if (rawResults.isEmpty()) {
            perfInfo("[Hopper][Pickup] no items found in box, returning false");
            return false;
        }

        // Copy immediately — subsequent calls may reuse the same thread-local list.
        List<Ref<EntityStore>> itemRefs = new ArrayList<>(rawResults);

        int hopperQty = this.getItemContainer().getItemStack((short) 0) == null ? 0
                : this.getItemContainer().getItemStack((short) 0).getQuantity();
        perfInfo("[Hopper][Pickup] hopperQty=" + hopperQty + " itemRefs.size()=" + itemRefs.size());
        if (hopperQty >= 100) {
            perfInfo("[Hopper][Pickup] hopper full (qty=" + hopperQty + "), returning false");
            return false;
        }

        for (Ref<EntityStore> ref : itemRefs) {
            if (ref == null || !ref.isValid()) {
                perfInfo("[Hopper][Pickup] ref null or invalid, skipping");
                continue;
            }
            // Skip our own in-transit visual entities.
            if (l.contains(ref)) {
                perfInfo("[Hopper][Pickup] ref=" + ref + " is own visual, skipping");
                continue;
            }
            // Note: NOT skipping Intangible entities — real dropped items may carry that component.
            if (entities.getComponent(ref, Intangible.getComponentType()) != null) {
                perfInfo("[Hopper][Pickup] ref=" + ref + " has Intangible (logging only, not skipping)");
            }

            ItemComponent ic = (ItemComponent) entities.getComponent(ref, ItemComponent.getComponentType());
            if (ic == null) {
                perfInfo("[Hopper][Pickup] ref=" + ref + " has no ItemComponent, skipping");
                continue;
            }

            // Skip items still in their drop delay (not yet collectible).
            if (!ic.canPickUp()) {
                perfInfo("[Hopper][Pickup] ref=" + ref + " canPickUp()=false (drop delay active), skipping");
                continue;
            }

            ItemStack stack = ic.getItemStack();
            if (stack == null || stack.isEmpty()) {
                perfInfo("[Hopper][Pickup] ref=" + ref + " stack is null or empty, skipping");
                continue;
            }

            String itemKey = null;
            try { itemKey = stack.getBlockKey(); } catch (Throwable ignored) {}
            if (itemKey == null) itemKey = resolveItemStackKey(stack);
            perfInfo("[Hopper][Pickup] ref=" + ref + " itemKey=" + itemKey + " qty=" + stack.getQuantity());

            // Apply filter.
            if (!isItemAllowedByFilter(itemKey)) {
                perfInfo("[Hopper][Pickup] ref=" + ref + " BLOCKED by filter (mode=" + filterMode + ")");
                continue;
            }

            int transferAmount = (int) Math.min(
                    data.tier * Ev0Config.getTierMultiplier(),
                    Math.min(stack.getQuantity(), 100 - hopperQty)
            );
            perfInfo("[Hopper][Pickup] transferAmount=" + transferAmount + " for " + itemKey);
            if (transferAmount <= 0) {
                perfInfo("[Hopper][Pickup] transferAmount<=0, skipping");
                continue;
            }

            ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot(
                    (short) 0, stack.withQuantity(transferAmount));

            perfInfo("[Hopper][Pickup] addItemStackToSlot succeeded=" + t.succeeded() + " item=" + itemKey + " amount=" + transferAmount);
            if (t.succeeded()) {
                int remaining = stack.getQuantity() - transferAmount;
                perfInfo("[Hopper][Pickup] SUCCESS item=" + itemKey + " transferred=" + transferAmount + " remaining=" + remaining);
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
        perfInfo("[Hopper][Pickup] no items collected at " + importPos + ", returning false");
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
            Ev0Log.warn(HytaleLogger.getLogger(), "[Hopper] Failed to ensure ArcIO components: " + e.getMessage());
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
            Ev0Log.warn(HytaleLogger.getLogger(), "[Hopper] ArcIO signal check failed: " + e.getMessage());
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
            Ev0Log.warn(HytaleLogger.getLogger(), "[Hopper] ArcIO adjacent check failed: " + e.getMessage());
        }
        return false;
    }

    protected void reset(Instant currentTime) {
        startTime = currentTime;
    }
    @Nonnull
    public static List<Ref<EntityStore>> getAllEntitiesInBox(HopperProcessor hp, Vector3i pos, float height, @Nonnull ComponentAccessor<EntityStore> components,  boolean players, boolean entities, boolean items) {
        final java.util.List results = (java.util.List) SpatialResource.getThreadLocalReferenceList();
        final Vector3d center = new Vector3d(pos.x, pos.y, pos.z);

        // Use the provided height parameter (caller-controlled) rather than hardcoded values.
        final double queryHeight = Math.max(1.0f, height);

        if (entities) {
            // If you later want to query entities, enable and tune radius/height here.
            // components.getResource(EntityModule.get().getEntitySpatialResourceType()).getSpatialStructure().collectCylinder(center, 2, queryHeight, results);
        }
        if (players) {
            components.getResource(EntityModule.get().getPlayerSpatialResourceType()).getSpatialStructure().collectCylinder(center, 4, queryHeight, results);
        }
        if (items) {
            // items typically don't need as large a radius; tune if necessary
            // components.getResource(EntityModule.get().getItemSpatialResourceType()).getSpatialStructure().collectCylinder(center, 2, queryHeight, results);
        }

        // Prefer reusing the instance buffer when available to avoid allocating a new list each tick.
        if (hp != null && hp.nearbyBuffer != null) {
            hp.nearbyBuffer.clear();
            hp.nearbyBuffer.addAll(results);
            return hp.nearbyBuffer;
        }

        // Return a copy so callers iterating the result are not affected if the
        // thread-local list is reused by a nested spatial query (e.g. inside throwItem).
        return new ArrayList<>(results);
    }
    public static List<Ref<EntityStore>> getAllItemsInBox(HopperProcessor hp, Vector3i pos, float height, @Nonnull ComponentAccessor<EntityStore> components,  boolean players, boolean entities, boolean items) {
        final java.util.List results = (java.util.List) SpatialResource.getThreadLocalReferenceList();
        final Vector3d center = new Vector3d(pos.x, pos.y, pos.z);
        final double queryHeight = Math.max(0.5f, height);

        if (entities) {
            final Vector3d min = new Vector3d(pos.x - 0.5, pos.y - 0.5, pos.z - 0.5);
            final Vector3d max = new Vector3d(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
            components.getResource(EntityModule.get().getEntitySpatialResourceType()).getSpatialStructure().collectBox(min, max, results);
        }
        if (players) {
            // tuned out by default; enable/adjust if visuals should affect players
            // components.getResource(EntityModule.get().getPlayerSpatialResourceType()).getSpatialStructure().collectCylinder(center, 4, queryHeight, results );
        }
        if (items) {
            // Use a tighter radius/height for item queries to reduce returned set size
            components.getResource(EntityModule.get().getItemSpatialResourceType()).getSpatialStructure().collectCylinder(center, 2, Math.max(0.5, queryHeight), results );
        }

        if (hp != null && hp.nearbyBuffer != null) {
            hp.nearbyBuffer.clear();
            hp.nearbyBuffer.addAll(results);
            return hp.nearbyBuffer;
        }

        return new ArrayList<>(results);
    }

    public static ComponentType<EntityStore, BlockEntity> getComponentType() {
        ComponentRegistryProxy<EntityStore> entityStoreRegistry = EntityModule.get().getEntityStoreRegistry();
        return EntityModule.get().getBlockEntityComponentType();
    }

    public static class Data extends StateData {

        @Nonnull
        public static final BuilderCodec<Data> CODEC = BuilderCodec.builder(Data.class, Data::new)
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
        public float force = 1f;
        public boolean players = true;
        public boolean entities = true;
        public boolean items = true;
        public float height = 0.99f;
        public ItemHandler output = new IdOutput();
        public AdjacentSide[] exportFaces = new AdjacentSide[0];
        public AdjacentSide[] importFaces = new AdjacentSide[0];
        public String[] substitutions;
        public boolean exportOnce = true;
        public Rangef duration;
    }

    @Override
    @Nullable
    public WorldChunk getChunk() {
        try {
            Vector3i p = getPosition();
            if (p == null || w == null) return null;
            return w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(p.x, p.z));
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Override
    public Vector3i getPosition() {
        try { return this.getBlockPosition(); } catch (Throwable ignored) { return null; }
    }

    @Override
    public void invalidate() {
        this.invalidatedFlag = true;
        try { REGISTERED_PROCESSORS.remove(this); } catch (Throwable ignored) {}
        try { lastEngineTick = 0; } catch (Throwable ignored) {}
    }

    // Lightweight maintenance task invoked by fallback scheduler when engine ticks are absent.
    private void fallbackHeartbeat() {
        try {
            // clean up stale visual entities (same logic as in tick but safe-guarded)
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
        } catch (Throwable ignored) {}
    }

    // ------------------------- Reflection helpers -------------------------
    private Object getRefFromArchetype(ArchetypeChunk<?> archeChunk, int index) {
        try {
            String key = "ArchetypeChunk.getRef";
            Method m = REFLECTION_METHOD_CACHE.get(key);
            if (m == null) {
                Class<?> ac = archeChunk.getClass();
                for (String name : new String[]{"getReferenceTo", "getRef", "getRefAt", "referenceTo", "getReference"}) {
                    try { m = ac.getMethod(name, int.class); break; } catch (NoSuchMethodException ignored) {}
                }
                if (m != null) REFLECTION_METHOD_CACHE.put(key, m);
            }
            if (m != null) return m.invoke(archeChunk, index);
        } catch (Throwable ignored) {}
        return null;
    }

    private HopperComponent getHopperComponent(Store<ChunkStore> store, Object ref) {
        try {
            Ev0Lib lib = Ev0Lib.getInstance();
            if (lib == null) return null;
            Object compType = lib.getHopperComponentType();
            if (compType == null) return null;
            Method getter = null;
            for (Method mm : store.getClass().getMethods()) {
                if (!mm.getName().equals("getComponent")) continue;
                if (mm.getParameterCount() == 2) { getter = mm; break; }
            }
            if (getter == null) return null;
            Object comp = getter.invoke(store, ref, compType);
            if (comp instanceof HopperComponent) return (HopperComponent) comp;
        } catch (Throwable ignored) {}
        return null;
    }

    private void putHopperComponent(Store<ChunkStore> store, Object ref, HopperComponent comp) {
        try {
            Method put = null;
            for (Method mm : store.getClass().getMethods()) {
                if (!mm.getName().equals("putComponent")) continue;
                if (mm.getParameterCount() == 2) { put = mm; break; }
            }
            if (put != null) { put.invoke(store, ref, comp); return; }

            Ev0Lib lib = Ev0Lib.getInstance();
            if (lib == null) return;
            Object compType = lib.getHopperComponentType();
            if (compType == null) return;
            Method ensure = null;
            for (Method mm : store.getClass().getMethods()) {
                if (mm.getName().equals("ensureAndGetComponent") && mm.getParameterCount() == 2) { ensure = mm; break; }
            }
            if (ensure != null) {
                Object existing = ensure.invoke(store, ref, compType);
                if (existing == null) return;
                try {
                    java.lang.reflect.Field f = existing.getClass().getField("data");
                    f.set(existing, comp.data);
                } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {}
    }

}

