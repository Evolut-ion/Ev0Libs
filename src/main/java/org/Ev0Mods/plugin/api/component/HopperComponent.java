package org.Ev0Mods.plugin.api.component;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.Ev0Mods.plugin.Ev0Lib;
import org.Ev0Mods.plugin.api.Ev0Config;
import org.Ev0Mods.plugin.api.Ev0Log;
import org.Ev0Mods.plugin.api.block.state.HopperProcessor;
import org.Ev0Mods.plugin.api.interactions.HopperInteraction;
import org.Ev0Mods.plugin.api.util.ItemUtilsExtended;
import org.Ev0Mods.plugin.api.util.WorldHelper;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import com.hypixel.hytale.builtin.blocktick.system.ChunkBlockTickSystem;
import com.hypixel.hytale.builtin.fluid.FluidSystems;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.Rangef;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.entity.entities.BlockEntity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlockState;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.Intangible;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.physics.component.PhysicsValues;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.state.TickableBlockState;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockPatternRule.AdjacentSide;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import voidbond.arcio.components.ArcioMechanismComponent;
import voidbond.arcio.components.BlockUUIDComponent;

@SuppressWarnings("removal")

public class HopperComponent implements Component<ChunkStore>, TickableBlockState {
    // Ported from HopperProcessor: fields, helpers and full tick logic
    private static final boolean PERF_DEBUG = false;
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static void perfInfo(String msg) { if (PERF_DEBUG) Ev0Log.info(LOGGER, msg); }

    public int fluid_id = 0;
    public Rangef duration = new Rangef(0, 10);
    public float tier;
    public org.Ev0Mods.plugin.api.block.state.HopperProcessor.Data data;
    protected Instant startTime;
    private double timerV = 0;
    private double timer = 0;
    protected short outputSlot = 0;
    private String[] substitutions;
    public World w;
    boolean is_valid = true;
    public String sideVar;
    private Player ownerId;
    BlockEntity be;
    private PlayerRef rf;
    boolean drop = false;
    public ComponentAccessor<EntityStore> ca;
    public Ref<EntityStore>[] ic;
    public Store<EntityStore> es;
    public Deque<Ref<EntityStore>> l = new ArrayDeque<>();
    // Map to keep a reference from spawned visual entity -> the ItemStack it represents
    public Map<Ref<EntityStore>, ItemStack> visualMap = new ConcurrentHashMap<>();
    // Map to keep spawn time for each visual so we can explicitly remove them
    public Map<Ref<EntityStore>, Instant> visualSpawnTimes = new ConcurrentHashMap<>();
    private Fluid f;
    private int tickCounter = 0;
    // Snapshotted once per tick so all visual-spawn calls share the same stable player list
    private List<Ref<EntityStore>> nearbyBuffer = new ArrayList<>();
    private static final ConcurrentHashMap<Class<?>, Method> ITEM_KEY_METHOD_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class<?>, Method> GET_ITEM_CONTAINER_METHOD_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class<?>, Method> GET_CONTAINER_FROM_ITEM_CONTAINER_METHOD_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Method> REFLECTION_METHOD_CACHE = new ConcurrentHashMap<>();
    private boolean playersNearbyCached = false;
    private final List<long[]> pendingFluidRemovals = new ArrayList<>();

    // Engine tick tracking for fallback
    private volatile long lastEngineTick = System.currentTimeMillis();
    private volatile boolean invalidatedFlag = false;

    private static final ScheduledExecutorService FALLBACK_SCHEDULER = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "ev0-hopper-fallback");
        t.setDaemon(true);
        return t;
    });

    private static final ConcurrentHashMap<HopperComponent, Boolean> REGISTERED_COMPONENTS = new ConcurrentHashMap<>();

    static {
        FALLBACK_SCHEDULER.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            for (HopperComponent hc : REGISTERED_COMPONENTS.keySet()) {
                try {
                    long last = hc.lastEngineTick;
                    if (hc.invalidatedFlag) { REGISTERED_COMPONENTS.remove(hc); continue; }
                    if (now - last > 2000) {
                        try { hc.fallbackHeartbeat(); } catch (Throwable ignored) {}
                    }
                } catch (Throwable ignored) {}
            }
        }, 2, 2, TimeUnit.SECONDS);
    }
    

    // Item container reference (may be provided either directly or via ItemContainerBlock component)
    private transient ItemContainerBlockState itemContainerBlock;
    private transient ItemContainer itemContainer;

    public ItemContainer getItemContainer() { return itemContainer != null ? itemContainer : null; }

    // Position cache for methods that need a quick position
    private Vector3i cachedPosition = new Vector3i(0,0,0);

        public static final BuilderCodec<HopperComponent> CODEC = BuilderCodec.builder(HopperComponent.class, HopperComponent::new)
            .append(new KeyedCodec<>("Data", org.Ev0Mods.plugin.api.block.state.HopperProcessor.Data.CODEC, true), (c, v) -> c.data = v, c -> c.data).add()
            .append(new KeyedCodec<>("Duration", ProtocolCodecs.RANGEF, true), (c, v) -> c.duration = v, c -> c.duration).add()
            .append(new KeyedCodec<>("Tier", Codec.FLOAT, true), (c, v) -> c.tier = v, c -> c.tier).add()
            .append(new KeyedCodec<>("Substitutions", Codec.STRING_ARRAY, true), (c, v) -> c.substitutions = v, c -> c.substitutions).add()
            .append(new KeyedCodec<>("FilterMode", Codec.STRING, true), (c, v) -> c.filterMode = v == null ? "Off" : v, c -> c.filterMode).add()
            .append(new KeyedCodec<>("Whitelist", Codec.STRING_ARRAY, true), (c, v) -> { if (v == null) c.whitelist.clear(); else { c.whitelist.clear(); c.whitelist.addAll(Arrays.asList(v)); } }, c -> c.whitelist.toArray(new String[0])).add()
            .append(new KeyedCodec<>("Blacklist", Codec.STRING_ARRAY, true), (c, v) -> { if (v == null) c.blacklist.clear(); else { c.blacklist.clear(); c.blacklist.addAll(Arrays.asList(v)); } }, c -> c.blacklist.toArray(new String[0])).add()
            .append(new KeyedCodec<>("ArcioMode", Codec.STRING, true), (c, v) -> c.arcioMode = v == null ? "IgnoreSignal" : v, c -> c.arcioMode).add()
            .build();

    // Reflection helpers ported from HopperProcessor
    private Object getItemContainerFromState(Object stateObj) {
        if (stateObj == null) return null;
        Class<?> cls = stateObj.getClass();
        if (GET_ITEM_CONTAINER_METHOD_CACHE.containsKey(cls)) {
            Method cached = GET_ITEM_CONTAINER_METHOD_CACHE.get(cls);
            if (cached == null) return null;
            try { return cached.invoke(stateObj); } catch (Throwable ignored) { return null; }
        }

        Method found = null;
        try { found = cls.getMethod("getItemContainer"); } catch (Throwable ignored) {}
        if (found == null) {
            try { found = cls.getMethod("itemContainer"); } catch (Throwable ignored) {}
        }
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
        try { Object r = found.invoke(itemContainerObj, idx); if (r instanceof ItemContainer) return (ItemContainer) r; } catch (Throwable ignored) {}
        return null;
    }

    public Vector3i getBlockPosition() { return cachedPosition; }

    // Prefer engine-provided position accessors if available (some engine versions
    // supply a superclass method for block position). If we can read a position
    // from the superclass, update the cache and return it. Otherwise return the
    // existing cached position (may be 0,0,0 until resolved by resolvePosition()).
    public Vector3i probeAndGetBlockPosition() {
        try {
            Class<?> sc = this.getClass().getSuperclass();
            if (sc != null) {
                for (String name : new String[]{"getBlockPosition", "getPosition", "getPos", "position"}) {
                    try {
                        java.lang.reflect.Method m = sc.getMethod(name);
                        if (m != null) {
                            Object r = m.invoke(this);
                            if (r instanceof Vector3i) {
                                cachedPosition = (Vector3i) r;
                                return cachedPosition;
                            }
                        }
                    } catch (Throwable ignored) {}
                }
            }
        } catch (Throwable ignored) {}
        return cachedPosition;
    }

    @Override
    public Vector3i getPosition() { return cachedPosition; }

    @Override
    @Nullable
    public WorldChunk getChunk() {
        try { if (w != null) return w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(cachedPosition.x, cachedPosition.z)); } catch (Throwable ignored) {}
        return null;
    }

    @Override
    public void invalidate() { is_valid = false; invalidatedFlag = true; }

    public int getRotationIndex() { return 0; }

    public BlockType getBlockType() { return BlockType.EMPTY; }

    public static final boolean ARCIO_PRESENT;
    static { boolean found = false; try { Class.forName("voidbond.arcio.components.ArcioMechanismComponent"); found = true; } catch (ClassNotFoundException ignored) {} ARCIO_PRESENT = found; }

    public static final boolean SIMPLE_DRAWERS_PRESENT;
    static { boolean found2 = false; try { Class.forName("net.crepe.inventory.IDrawerContainer"); found2 = true; } catch (ClassNotFoundException ignored) {} SIMPLE_DRAWERS_PRESENT = found2; }

    private boolean arcioInitialized = false;
    private String arcioMode = "IgnoreSignal";

    private final List<String> whitelist = Collections.synchronizedList(new ArrayList<>());
    private final List<String> blacklist = Collections.synchronizedList(new ArrayList<>());
    private volatile String filterMode = "Off";
    private final Map<PlayerRef, String> typedBuffer = new ConcurrentHashMap<>();

    public List<String> getWhitelist() { synchronized (whitelist) { return new ArrayList<>(whitelist); } }
    public List<String> getBlacklist() { synchronized (blacklist) { return new ArrayList<>(blacklist); } }
    public String getFilterMode() { return filterMode; }
    public void addToWhitelist(String id) { if (id != null) whitelist.add(id); }
    public void addToBlacklist(String id) { if (id != null) blacklist.add(id); }
    public void setFilterMode(String mode) { if (mode != null) filterMode = mode; }
    public String removeLastFromWhitelist() { synchronized (whitelist) { if (whitelist.isEmpty()) return null; return whitelist.remove(whitelist.size() - 1); } }
    public String removeLastFromBlacklist() { synchronized (blacklist) { if (blacklist.isEmpty()) return null; return blacklist.remove(blacklist.size() - 1); } }
    public void clearWhitelist() { whitelist.clear(); }
    public void clearBlacklist() { blacklist.clear(); }

    public String getArcioMode() { return arcioMode; }
    public void setArcioMode(String mode) { if (mode != null) arcioMode = mode; }

    public void setTypedBuffer(PlayerRef p, String v) { if (p == null) return; if (v == null) typedBuffer.remove(p); else typedBuffer.put(p, v); }
    public String getTypedBuffer(PlayerRef p) { if (p == null) return null; return typedBuffer.get(p); }

    public boolean isSingletonMode() {
        return "Singleton".equalsIgnoreCase(filterMode);
    }

    private boolean isItemAllowedByFilter(String blockKey) {
        if (filterMode == null || filterMode.equalsIgnoreCase("Off") || filterMode.equalsIgnoreCase("Singleton")) return true;
        if (filterMode.equalsIgnoreCase("Whitelist")) {
            synchronized (whitelist) { if (whitelist == null || whitelist.isEmpty()) return false; if (blockKey == null) return false; for (String s : whitelist) if (s != null && s.equalsIgnoreCase(blockKey)) return true; return false; }
        }
        if (filterMode.equalsIgnoreCase("Blacklist")) { synchronized (blacklist) { if (blacklist == null || blacklist.isEmpty()) return true; if (blockKey == null) return true; for (String s : blacklist) if (s != null && s.equalsIgnoreCase(blockKey)) return false; return true; } }
        return true;
    }

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
                    for (String name : candidates) { try { found = cls.getMethod(name); if (found != null) break; } catch (Throwable ignored) {} }
                    ITEM_KEY_METHOD_CACHE.put(cls, found);
                    m = found;
                }
                if (m != null) {
                    try { Object v = m.invoke(stack); if (v != null) probe = v; } catch (Throwable ignored) {}
                }
            }
            if (probe == null) probe = stack.toString();
            return String.valueOf(probe);
        } catch (Throwable t) { try { return String.valueOf(stack.toString()); } catch (Throwable ignored) { return null; } }
    }

    { ic = new Ref[0]; }

    public HopperComponent() {}

    public HopperComponent(HopperComponent other) {
        this.fluid_id = other.fluid_id;
        this.duration = other.duration;
        this.tier = other.tier;
        this.data = other.data;
        this.startTime = other.startTime;
        this.timerV = other.timerV;
        this.timer = other.timer;
        this.outputSlot = other.outputSlot;
        this.substitutions = other.substitutions;
        this.w = other.w;
        this.is_valid = other.is_valid;
        this.sideVar = other.sideVar;
        this.ownerId = other.ownerId;
        this.be = other.be;
        this.rf = other.rf;
        this.drop = other.drop;
        this.ca = other.ca;
        this.ic = other.ic;
        this.es = other.es;
        this.l = new ArrayDeque<>(other.l);
        this.visualMap = new ConcurrentHashMap<>(other.visualMap);
        this.visualSpawnTimes = new ConcurrentHashMap<>(other.visualSpawnTimes);
        this.f = other.f;
        this.tickCounter = other.tickCounter;
        this.nearbyBuffer = new ArrayList<>(other.nearbyBuffer);
        this.cachedPosition = other.cachedPosition;
        this.arcioInitialized = other.arcioInitialized;
        this.arcioMode = other.arcioMode;
        this.whitelist.addAll(other.whitelist);
        this.blacklist.addAll(other.blacklist);
        this.filterMode = other.filterMode;
        this.typedBuffer.putAll(other.typedBuffer);
    }

    public void setOwnerId(Player ownerId) { this.ownerId = ownerId; }
    public Player getOwnerId() { return this.ownerId; }

    public void onOpen(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl World world, @NonNullDecl Store<EntityStore> store) {
        if (ARCIO_PRESENT && !arcioInitialized) { ensureArcioComponents(world, null); }
        rf = store.getComponent(ref, PlayerRef.getComponentType());
        try { if (rf == null) return; Vector3i pos = this.getBlockPosition(); org.Ev0Mods.plugin.api.ui.HopperUIPage.open(rf, store, pos, null); } catch (Throwable t) {}
    }

    @Nullable
    @Override
    public Component<ChunkStore> clone() {
        return new HopperComponent(this);
    }

    public boolean canOpen(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl ComponentAccessor<EntityStore> componentAccessor) {
        try { return true; } catch (Throwable ignored) { return true; }
    }

    public void onDestroy() {
        for (int b = 0; b < l.size() - 1; b++) {
            try { ItemContainer ic = getItemContainer(); if (ic != null) ic.dropAllItemStacks(); } catch (Throwable ignored) {}
            if (!l.isEmpty()) {
                if (l.size() > b) {
                    Ref<EntityStore> esx = l.removeFirst();
                    try { visualMap.remove(esx); visualSpawnTimes.remove(esx); } catch (Exception ignored) {}
                    if (esx != null && esx.isValid()) {
                        try { this.es.removeEntity(esx, RemoveReason.REMOVE); } catch (Throwable ignored) {}
                    }
                }
            }
        }
    }

    @Nonnull
    private static final Set<Dependency<ChunkStore>> DEPENDENCIES = Set.of(
            new SystemDependency<>(Order.AFTER, FluidSystems.Ticking.class), new SystemDependency<>(Order.BEFORE, ChunkBlockTickSystem.Ticking.class)
    );

    // Core tick logic port
    public void tick(float dt, int index, ArchetypeChunk<ChunkStore> archeChunk, Store<ChunkStore> store, CommandBuffer<ChunkStore> commandBuffer) {
        // mark engine tick
        lastEngineTick = System.currentTimeMillis();
        REGISTERED_COMPONENTS.put(this, Boolean.TRUE);

        // Ensure data and itemContainer are never null — CODEC marks them optional,
        // so a freshly-placed block entity may not have them serialized yet.
        if (data == null) data = new HopperProcessor.Data();

        // Resolve world position on the first tick (cachedPosition starts at 0,0,0)
        Ref<ChunkStore> myRef = archeChunk.getReferenceTo(index);

        // set world/store references used by other helpers
        try { this.w = store.getExternalData().getWorld(); } catch (Throwable ignored) {}
        try { this.es = this.w != null ? this.w.getEntityStore().getStore() : null; } catch (Throwable ignored) {}

        // Try to read engine-provided position accessors first, then fall back to scanning
        try {
            Vector3i probed = probeAndGetBlockPosition();
            if (probed == null || (probed.x == 0 && probed.y == 0 && probed.z == 0)) {
                resolvePosition(store, myRef);
            }
        } catch (Throwable ignored) {}

        // Diagnostic log to detect whether this tick method is invoked (after resolving position)
        try { Ev0Log.info(HytaleLogger.getLogger(), "[Ev0Lib] HopperComponent.tick invoked for index=" + index + " pos=" + cachedPosition); } catch (Throwable ignored) {}

        // Fetch the inventory: prefer ItemContainer (direct) but fall back to ItemContainerBlock component.
        // Use reflection for component lookup to avoid generic type inference issues across server versions.
        try {
            // Attempt component-first lookup via BlockComponentChunk -> blockRef -> ItemContainerBlock
            try {
                Method getComponentMethod = store.getClass().getMethod("getComponent", Ref.class, Class.forName("com.hypixel.hytale.component.ComponentType"));
                Class<?> icbClass = Class.forName("com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock");
                Method icbGetComponentType = icbClass.getMethod("getComponentType");
                Object icbComponentType = icbGetComponentType.invoke(null);
                Object icbObj = null;
                try { icbObj = getComponentMethod.invoke(store, myRef, icbComponentType); } catch (Throwable ignored) { icbObj = null; }
                if (icbObj != null && icbClass.isInstance(icbObj)) {
                    itemContainerBlock = (ItemContainerBlockState) icbObj;
                    try {
                        Method getIC = itemContainerBlock.getClass().getMethod("getItemContainer");
                        Object cont = getIC.invoke(itemContainerBlock);
                        if (cont instanceof ItemContainer) itemContainer = (ItemContainer) cont;
                    } catch (Throwable ignored) { itemContainer = null; }
                }
            } catch (Throwable ignored) { itemContainerBlock = null; }

                // If we still don't have an ItemContainer, fall back to legacy state-based lookup
                if (itemContainer == null) {
                    // Try to probe engine-provided position accessors first, then resolve as fallback
                    Vector3i probed = probeAndGetBlockPosition();
                    if (probed == null || (probed.x == 0 && probed.y == 0 && probed.z == 0)) {
                        if (cachedPosition.x == 0 && cachedPosition.y == 0 && cachedPosition.z == 0) resolvePosition(store, myRef);
                    }
                WorldChunk myChunk = null; try { if (w != null) myChunk = w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(cachedPosition.x, cachedPosition.z)); } catch (Throwable ignored) {}
                Object state = null; try { if (myChunk != null) state = EngineCompat.getState(myChunk, cachedPosition.x, cachedPosition.y, cachedPosition.z); } catch (Throwable ignored) {}
                try {
                    Object contObj = getItemContainerFromState(state);
                    if (contObj instanceof ItemContainer) itemContainer = (ItemContainer) contObj; else itemContainer = getContainerFromItemContainerObject(contObj, 0);
                } catch (Throwable ignored) { itemContainer = null; }
            }
        } catch (Throwable ignored) {}

        // Ensure we have a usable ItemContainer instance (create fallback if none found)
        if (itemContainer == null) {
            try {
                itemContainer = new SimpleItemContainer((short)1);
                try { HytaleLogger.getLogger().atInfo().log("[HopperDiag] created fallback SimpleItemContainer for hopper at pos=" + cachedPosition); } catch (Throwable ignored) {}
            } catch (Throwable ignored) { itemContainer = null; }
        }

        try { HytaleLogger.getLogger().atInfo().log("[HopperDiag] itemContainerBlock=" + (itemContainerBlock != null) + " itemContainerPresent=" + (getItemContainer() != null)); } catch (Throwable ignored) {}

        // Apply deferred fluid removals
        if (!pendingFluidRemovals.isEmpty()) {
            for (long[] coords : pendingFluidRemovals) {
                try {
                    WorldChunk fc = w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock((int) coords[0], (int) coords[2]));
                    if (fc != null) fc.setBlock((int) coords[0], (int) coords[1], (int) coords[2], BlockType.EMPTY);
                } catch (Exception ignored) {}
            }
            pendingFluidRemovals.clear();
        }

        if (ARCIO_PRESENT && !arcioInitialized) ensureArcioComponents(w, commandBuffer);

        if (ARCIO_PRESENT && "EnableWhenSignal".equals(arcioMode)) { if (!isArcioActive(w)) return; }

        this.timerV += 1.0; drop = this.timerV >= duration.max; if (drop) this.timerV = 0;

        // Update nearby buffer once in a while
        tickCounter++;
        int phase = tickCounter % 180;
        boolean doExport = phase == 0;
        boolean doImport = phase == 90;

        if (tickCounter % 180 == 0) {
            try {
                final java.util.List rawPlayers = (java.util.List) SpatialResource.getThreadLocalReferenceList();
                final Vector3d center = new Vector3d(cachedPosition.x, cachedPosition.y, cachedPosition.z);
                ((ComponentAccessor<EntityStore>) es).getResource(EntityModule.get().getPlayerSpatialResourceType()).getSpatialStructure().collectCylinder(center, 4, Math.max(1.0f, data.height), rawPlayers);
                playersNearbyCached = !rawPlayers.isEmpty(); rawPlayers.clear();
            } catch (Exception ignored) {}
        }
        try { HytaleLogger.getLogger().atInfo().log("[HopperDiag] tickCounter=" + tickCounter + " phase=" + phase + " doExport=" + doExport + " doImport=" + doImport + " slot0=" + (this.getItemContainer() == null ? "null" : String.valueOf(this.getItemContainer().getItemStack((short)0)))); } catch (Throwable ignored) {}

        if (doExport) {
            if (this.getItemContainer() != null && this.getItemContainer().getItemStack((short)0) != null && playersNearbyCached) {
                nearbyBuffer = getAllEntitiesInBox(this, cachedPosition, data.height, (ComponentAccessor<EntityStore>) es, data.players, data.entities, data.items);
            } else nearbyBuffer.clear();
            runExportPhase(cachedPosition, es);
        }
        if (doImport) {
            ItemStack have2 = this.getItemContainer().getItemStack((short)0);
            boolean hopperHasSpace = (have2 == null) || (have2.getQuantity() < 100);
            if (hopperHasSpace && playersNearbyCached) nearbyBuffer = getAllEntitiesInBox(this, cachedPosition, data.height, (ComponentAccessor<EntityStore>) es, data.players, data.entities, data.items); else nearbyBuffer.clear();
            for (AdjacentSide side : this.data.importFaces) {
            final Vector3i importPos = new Vector3i(cachedPosition.x, cachedPosition.y, cachedPosition.z).add(WorldHelper.rotate(side, this.getRotationIndex()).relativePosition);
            final WorldChunk chunk = w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(importPos.x, importPos.z)); if (chunk == null) continue;
            int targetFluidId = EngineCompat.getFluidId(chunk, importPos.x, importPos.y, importPos.z);
            Object state = EngineCompat.getState(chunk, importPos.x, importPos.y, importPos.z);
            boolean hasContainer = (state != null && (state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState") || state.getClass().getSimpleName().contains("ItemContainer")));
            ItemStack currentItem = this.getItemContainer().getItemStack((short)0);
            if (Ev0Config.isFluidTransferEnabled() && targetFluidId != 0 && currentItem == null && !hasContainer) {
                ItemStack bucketStack = null; switch (targetFluidId) { case 2 -> bucketStack = new ItemStack("*Container_Bucket_State_Filled_Red_Slime", 1, null); case 3 -> bucketStack = new ItemStack("*Container_Bucket_State_Filled_Tar", 1, null); case 4 -> bucketStack = new ItemStack("*Container_Bucket_State_Filled_Poison", 1, null); case 5 -> bucketStack = new ItemStack("*Container_Bucket_State_Filled_Green_Slime", 1, null); case 6 -> bucketStack = new ItemStack("*Container_Bucket_State_Filled_Lava", 1, null); case 7 -> bucketStack = new ItemStack("*Container_Bucket_State_Filled_Water", 1, null); default -> bucketStack = null; }
                if (bucketStack != null) { this.getItemContainer().addItemStackToSlot((short)0, bucketStack); pendingFluidRemovals.add(new long[]{importPos.x, importPos.y, importPos.z}); continue; }
            }
            try { Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] import from pos=" + importPos + " state=" + (state == null ? "null" : state.getClass().getSimpleName()) + " hasContainer=" + hasContainer); } catch (Throwable ignored) {}
            perfInfo("[Hopper][Import] side=" + side + " importPos=" + importPos + " state=" + (state == null ? "null" : state.getClass().getSimpleName()) + " hasContainer=" + hasContainer);
            if (tryImportFromContainer(chunk, importPos, es, side)) { perfInfo("[Hopper][Import] tryImportFromContainer SUCCESS side=" + side); break; }
            perfInfo("[Hopper][Import] tryImportFromContainer failed, hasContainer=" + hasContainer + " -> will tryPickup=" + !hasContainer);
            if (!hasContainer && tryPickupItemEntities(importPos, es)) { perfInfo("[Hopper][Import] tryPickupItemEntities SUCCESS at " + importPos); runExportPhase(cachedPosition, es); break; }
        } }

        // cleanup visuals
        if (!l.isEmpty()) { Iterator<Ref<EntityStore>> it = l.iterator(); while (it.hasNext()) { Ref<EntityStore> esx = it.next(); if (esx == null || !esx.isValid()) { it.remove(); try { visualMap.remove(esx); visualSpawnTimes.remove(esx);} catch (Exception ignored) {} } } }

        // expire visuals after 5 seconds
        try { if (this.es != null && !visualSpawnTimes.isEmpty()) { Instant now = this.es.getResource(WorldTimeResource.getResourceType()).getGameTime(); Iterator<Map.Entry<Ref<EntityStore>, Instant>> it2 = visualSpawnTimes.entrySet().iterator(); while (it2.hasNext()) { Map.Entry<Ref<EntityStore>, Instant> e = it2.next(); Ref<EntityStore> entryRef = e.getKey(); Instant spawnTime = e.getValue(); try { if (entryRef == null || !entryRef.isValid()) { it2.remove(); try { visualMap.remove(entryRef); } catch (Exception ignored) {} continue; } if (now.isAfter(spawnTime.plusSeconds(5))) { it2.remove(); try { visualMap.remove(entryRef); } catch (Exception ignored) {} try { l.remove(entryRef); } catch (Exception ignored) {} try { this.es.removeEntity(entryRef, RemoveReason.REMOVE); } catch (Exception ignored) {} } } catch (Exception ignored) {} } } } catch (Exception ignored) {}
    }

    // runExportPhase, tryImportFromContainer, tryTransferToOrFromContainer, tryPickupItemEntities and other helpers copied/adapted from HopperProcessor
    private void runExportPhase(Vector3i pos, Store<EntityStore> entities) {
        try {
            HytaleLogger.getLogger().atInfo().log("[HopperDiag] runExportPhase invoked; param pos=" + pos + " cachedPos=" + this.getBlockPosition() + " rotationIndex=" + this.getRotationIndex() + " slot0=" + (this.getItemContainer() == null ? "null" : String.valueOf(this.getItemContainer().getItemStack((short)0))));
            Ev0Log.info(LOGGER, "runExportPhase: nearbyBuffer.size=" + (nearbyBuffer == null ? "null" : String.valueOf(nearbyBuffer.size())) + " exporters=" + (entities != null));
        } catch (Throwable ignored) {}
        ItemStack currentItemFast = this.getItemContainer().getItemStack((short) 0);
        if (currentItemFast == null) return;
        for (AdjacentSide side : this.data.exportFaces) {
            Vector3i hopperPos = this.getBlockPosition();
            AdjacentSide rotated = WorldHelper.rotate(side, this.getRotationIndex());
            Vector3i rel = rotated.relativePosition;
            final Vector3i exportPos = new Vector3i(hopperPos.x, hopperPos.y, hopperPos.z).add(rel);
            try { HytaleLogger.getLogger().atInfo().log("[HopperDiag] export probe side=" + side + " rotated=" + rotated + " rel=" + rel + " hopperPos=" + hopperPos + " exportPos=" + exportPos + " rotationIndex=" + this.getRotationIndex()); } catch (Throwable ignored) {}
            final WorldChunk chunk = w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(exportPos.x, exportPos.z)); if (chunk == null) continue;
            Object state = EngineCompat.getState(chunk, exportPos.x, exportPos.y, exportPos.z);
            int targetFluidId = EngineCompat.getFluidId(chunk, exportPos.x, exportPos.y, exportPos.z);
            boolean hasContainer = (state != null && (state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState") || state.getClass().getSimpleName().contains("ItemContainer")));
            try { HytaleLogger.getLogger().atInfo().log("[HopperDiag] export side=" + side + " exportPos=" + exportPos + " state=" + (state == null ? "null" : state.getClass().getSimpleName()) + " hasContainer=" + hasContainer); } catch (Throwable ignored) {}
            try { Ev0Log.info(LOGGER, "export probe: side=" + side + " exportPos=" + exportPos + " stateClass=" + (state == null ? "null" : state.getClass().getName()) + " hasContainer=" + hasContainer); } catch (Throwable ignored) {}
            ItemStack currentItem = this.getItemContainer().getItemStack((short) 0);
            if (Ev0Config.isFluidTransferEnabled() && currentItem != null && !hasContainer && targetFluidId != 0) {
                String itemKey = currentItem.getBlockKey(); if (itemKey != null && itemKey.contains("Bucket") && !itemKey.contains("Empty")) {
                    int fluidToPlace = 0; if (itemKey.contains("Water")) fluidToPlace = 7; else if (itemKey.contains("Lava")) fluidToPlace = 6; else if (itemKey.contains("Green_Slime")) fluidToPlace = 5; else if (itemKey.contains("Poison")) fluidToPlace = 4; else if (itemKey.contains("Tar")) fluidToPlace = 3; else if (itemKey.contains("Red_Slime")) fluidToPlace = 2;
                    if (fluidToPlace != 0) { this.getItemContainer().removeItemStackFromSlot((short)0, 1); this.getItemContainer().addItemStackToSlot((short)0, new ItemStack("Container_Bucket",1,null)); continue; }
                }
            }
            boolean transferred = tryTransferToOrFromContainer(state, exportPos, side, entities, true);
            try { Ev0Log.info(LOGGER, "tryTransferToOrFromContainer returned=" + transferred + " for exportPos=" + exportPos); } catch (Throwable ignored) {}
            if (!transferred && currentItem != null && !hasContainer && targetFluidId == 0) {
                if (EngineCompat.getBlockType(chunk, exportPos.x, exportPos.y, exportPos.z) == null) {
                    EngineCompat.setBlock(chunk, exportPos.x, exportPos.y, exportPos.z, currentItem.getBlockKey());
                    this.getItemContainer().removeItemStackFromSlot((short)0, 1);
                }
            }
            if (this.data.exportOnce && transferred) break;
        }
    }

    private boolean tryTransferToOrFromContainer(Object state, Vector3i pos, AdjacentSide side, Store<EntityStore> entities, boolean exportPhase) {
        // If state is null, attempt component-first lookup for a block-backed ItemContainer
        if (state == null) {
            try { Ev0Log.info(LOGGER, "tryTransferToOrFromContainer: state==null for pos=" + pos + " exportPhase=" + exportPhase); } catch (Throwable ignored) {}
            try {
                if (w != null) {
                    Store<ChunkStore> cs = w.getChunkStore().getStore();
                    Ref<ChunkStore> chunkRef = w.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
                    if (chunkRef != null) {
                        BlockComponentChunk bcc = cs.getComponent(chunkRef, BlockComponentChunk.getComponentType());
                        if (bcc != null) {
                            Ref<ChunkStore> blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn(pos.x, pos.y, pos.z));
                            if (blockRef != null) {
                                try {
                                    Method getComp = cs.getClass().getMethod("getComponent", Ref.class, Class.forName("com.hypixel.hytale.component.ComponentType"));
                                    HopperComponent adjacentHopper = null;
                                    try {
                                        Ev0Lib lib = Ev0Lib.getInstance();
                                        if (lib != null && lib.getHopperComponentType() != null) {
                                            adjacentHopper = cs.getComponent(blockRef, lib.getHopperComponentType());
                                        }
                                    } catch (Throwable ignored) {}
                                    if (exportPhase && adjacentHopper != null) {
                                        try {
                                            ItemContainer target2 = adjacentHopper.getItemContainer();
                                            ItemStack hopperStack2 = this.getItemContainer().getItemStack((short) 0);
                                            if (target2 != null && hopperStack2 != null) {
                                                for (int slot = 0; slot < target2.getCapacity(); slot++) {
                                                    try {
                                                        if (!isItemAllowedByFilter(hopperStack2.getBlockKey())) continue;
                                                        int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), hopperStack2.getQuantity());
                                                        if (transferAmount <= 0) continue;
                                                        ItemStackSlotTransaction t2 = target2.addItemStackToSlot((short) slot, hopperStack2.withQuantity(transferAmount));
                                                        try { Ev0Log.info(LOGGER, "direct-hopper export attempt slot=" + slot + " transferAmount=" + transferAmount + " tx=" + (t2 == null ? "null" : String.valueOf(t2.succeeded()))); } catch (Throwable ignored) {}
                                                        if (t2 != null && t2.succeeded()) {
                                                            try { spawnVisualFor(hopperStack2.withQuantity(transferAmount), true, pos, side, entities); } catch (Throwable ignored) {}
                                                            this.getItemContainer().removeItemStackFromSlot((short) 0, transferAmount);
                                                            try { putHopperComponent(cs, blockRef, adjacentHopper); } catch (Throwable ignored) {}
                                                            try {
                                                                Ref<ChunkStore> colRef2 = w.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(cachedPosition.x, cachedPosition.z));
                                                                if (colRef2 != null) {
                                                                    BlockComponentChunk bcc2 = cs.getComponent(colRef2, BlockComponentChunk.getComponentType());
                                                                    if (bcc2 != null) {
                                                                        Ref<ChunkStore> myBlockRef = bcc2.getEntityReference(ChunkUtil.indexBlockInColumn(cachedPosition.x, cachedPosition.y, cachedPosition.z));
                                                                        if (myBlockRef != null) putHopperComponent(cs, myBlockRef, this);
                                                                    }
                                                                }
                                                            } catch (Throwable ignored) {}
                                                            return true;
                                                        }
                                                    } catch (Throwable ignored) {}
                                                }
                                            }
                                        } catch (Throwable ignored) {}
                                    }
                                    boolean targetHasHopperComponent = false;
                                    try {
                                        Ev0Lib lib = Ev0Lib.getInstance();
                                        Object hopperCompTypeProbe = lib != null ? lib.getHopperComponentType() : null;
                                        Object hopperCompProbe = (hopperCompTypeProbe != null) ? getComp.invoke(cs, blockRef, hopperCompTypeProbe) : null;
                                        targetHasHopperComponent = hopperCompProbe instanceof HopperComponent;
                                    } catch (Throwable ignored) {}
                                    Class<?> icbCls = Class.forName("com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock");
                                    Method getCompType = icbCls.getMethod("getComponentType");
                                    Object compType = getCompType.invoke(null);
                                    Object icbObj = null;
                                    try { icbObj = getComp.invoke(cs, blockRef, compType); } catch (Throwable ignored) { icbObj = null; }
                                    if (!targetHasHopperComponent && icbObj != null && icbCls.isInstance(icbObj)) {
                                        try {
                                            Method getIC = icbCls.getMethod("getItemContainer");
                                            Object cont = getIC.invoke(icbObj);
                                            if (cont instanceof ItemContainer) {
                                                ItemContainer target = (ItemContainer) cont;
                                                // Push from hopper -> target (component-first)
                                                ItemStack hopperStack = this.getItemContainer().getItemStack((short)0);
                                                try { Ev0Log.info(LOGGER, "component-first target capacity=" + target.getCapacity() + " hopperStack=" + hopperStack); } catch (Throwable ignored) {}
                                                if (hopperStack == null) return false;
                                                for (int slot = 0; slot < target.getCapacity(); slot++) {
                                                    try {
                                                        if (!isItemAllowedByFilter(hopperStack.getBlockKey())) return false;
                                                        int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), hopperStack.getQuantity());
                                                        if (transferAmount <= 0) continue;
                                                        ItemStackSlotTransaction t = target.addItemStackToSlot((short) slot, hopperStack.withQuantity(transferAmount));
                                                        try { Ev0Log.info(LOGGER, "component-first transfer attempt slot=" + slot + " transferAmount=" + transferAmount + " tx=" + (t == null ? "null" : String.valueOf(t.succeeded()))); } catch (Throwable ignored) {}
                                                        if (t != null && t.succeeded()) {
                                                            boolean verified = false;
                                                            try { ItemStack after = target.getItemStack((short) slot); if (after != null && after.getQuantity() >= transferAmount) verified = true; } catch (Throwable ignored) {}
                                                            if (!verified) {
                                                                try { target.removeItemStackFromSlot((short) slot, transferAmount); } catch (Throwable ignored) {}
                                                                continue;
                                                            }
                                                            try { spawnVisualFor(hopperStack.withQuantity(transferAmount), true, pos, side, entities); } catch (Throwable ignored) {}
                                                            this.getItemContainer().removeItemStackFromSlot((short)0, transferAmount);
                                                            return true;
                                                        }
                                                    } catch (Throwable ignored) {}
                                                }
                                            }
                                        } catch (Throwable ignored) {}
                                    }

                                    // If the adjacent block exposes a HopperComponent, attempt to push into its ItemContainer
                                    try {
                                        Class<?> hopperCompCls = Class.forName("org.Ev0Mods.plugin.api.component.HopperComponent");
                                        Method getCompTypeH = hopperCompCls.getMethod("getComponentType");
                                        Object hopperCompType = getCompTypeH.invoke(null);
                                        Object hopperCompObj = null;
                                        try { hopperCompObj = getComp.invoke(cs, blockRef, hopperCompType); } catch (Throwable ignored) { hopperCompObj = null; }
                                        if (hopperCompObj != null && hopperCompCls.isInstance(hopperCompObj)) {
                                            try {
                                                Method getIC2 = hopperCompCls.getMethod("getItemContainer");
                                                Object cont2 = getIC2.invoke(hopperCompObj);
                                                if (cont2 instanceof ItemContainer) {
                                                    ItemContainer target2 = (ItemContainer) cont2;
                                                    ItemStack hopperStack2 = this.getItemContainer().getItemStack((short)0);
                                                    if (hopperStack2 == null) return false;
                                                    for (int slot = 0; slot < target2.getCapacity(); slot++) {
                                                        try {
                                                            if (!isItemAllowedByFilter(hopperStack2.getBlockKey())) return false;
                                                            int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), hopperStack2.getQuantity());
                                                            if (transferAmount <= 0) continue;
                                                            ItemStackSlotTransaction t2 = target2.addItemStackToSlot((short) slot, hopperStack2.withQuantity(transferAmount));
                                                            try { Ev0Log.info(LOGGER, "chained-hopper export attempt slot=" + slot + " transferAmount=" + transferAmount + " tx=" + (t2 == null ? "null" : String.valueOf(t2.succeeded()))); } catch (Throwable ignored) {}
                                                            if (t2 != null && t2.succeeded()) {
                                                                boolean verified2 = false;
                                                                try { ItemStack after2 = target2.getItemStack((short) slot); if (after2 != null && after2.getQuantity() >= transferAmount) verified2 = true; } catch (Throwable ignored) {}
                                                                if (!verified2) { try { target2.removeItemStackFromSlot((short) slot, transferAmount); } catch (Throwable ignored) {} continue; }
                                                                try { spawnVisualFor(hopperStack2.withQuantity(transferAmount), true, pos, side, entities); } catch (Throwable ignored) {}
                                                                this.getItemContainer().removeItemStackFromSlot((short)0, transferAmount);
                                                                try { putHopperComponent(cs, blockRef, (HopperComponent) hopperCompObj); } catch (Throwable tt) { Ev0Log.warn(LOGGER, "putHopperComponent failed: " + (tt == null ? "null" : tt.getMessage())); }
                                                                // Also persist this hopper so clients see the updated slot/state
                                                                try {
                                                                    if (w != null) {
                                                                        Store<ChunkStore> cs2 = w.getChunkStore().getStore();
                                                                        Ref<ChunkStore> colRef2 = w.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(cachedPosition.x, cachedPosition.z));
                                                                        if (colRef2 != null) {
                                                                            BlockComponentChunk bcc2 = cs2.getComponent(colRef2, BlockComponentChunk.getComponentType());
                                                                            if (bcc2 != null) {
                                                                                Ref<ChunkStore> myBlockRef = bcc2.getEntityReference(ChunkUtil.indexBlockInColumn(cachedPosition.x, cachedPosition.y, cachedPosition.z));
                                                                                if (myBlockRef != null) {
                                                                                    try { putHopperComponent(cs2, myBlockRef, this); } catch (Throwable tt) { Ev0Log.warn(LOGGER, "putHopperComponent (self) failed: " + (tt == null ? "null" : tt.getMessage())); }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                } catch (Throwable ignored) {}
                                                                return true;
                                                            }
                                                        } catch (Throwable ignored) {}
                                                    }
                                                }
                                            } catch (Throwable ignored) {}
                                        }
                                    } catch (Throwable ignored) {}

                                } catch (Throwable ignored) {}
                            }
                        }
                    }
                }
            } catch (Throwable ignored) {}
        }
        // Hopper-to-hopper export: the adjacent hopper may not present an ItemContainer-like
        // state anymore (prerelease changes), but it should still have a HopperComponent
        // attached that we can transfer into.
        if (exportPhase && w != null) {
            try {
                Store<ChunkStore> cs = w.getChunkStore().getStore();
                Ref<ChunkStore> colRef = w.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
                if (colRef != null) {
                    BlockComponentChunk bcc = cs.getComponent(colRef, BlockComponentChunk.getComponentType());
                    if (bcc != null) {
                        Ref<ChunkStore> blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn(pos.x, pos.y, pos.z));
                        if (blockRef != null) {
                            Method getComp = cs.getClass().getMethod("getComponent", Ref.class, Class.forName("com.hypixel.hytale.component.ComponentType"));
                            Class<?> hopperCompCls = Class.forName("org.Ev0Mods.plugin.api.component.HopperComponent");
                            Method getCompTypeH = hopperCompCls.getMethod("getComponentType");
                            Object hopperCompType = getCompTypeH.invoke(null);

                            Object hopperCompObj = null;
                            try { hopperCompObj = getComp.invoke(cs, blockRef, hopperCompType); } catch (Throwable ignored) { hopperCompObj = null; }
                            if (hopperCompObj != null && hopperCompCls.isInstance(hopperCompObj)) {
                                Method getIC2 = hopperCompCls.getMethod("getItemContainer");
                                Object cont2 = getIC2.invoke(hopperCompObj);
                                if (cont2 instanceof ItemContainer) {
                                    ItemContainer target2 = (ItemContainer) cont2;
                                    ItemStack hopperStack2 = this.getItemContainer().getItemStack((short) 0);
                                    if (hopperStack2 == null) return false;

                                    for (int slot = 0; slot < target2.getCapacity(); slot++) {
                                        try {
                                            if (!isItemAllowedByFilter(hopperStack2.getBlockKey())) continue;
                                            int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), hopperStack2.getQuantity());
                                            if (transferAmount <= 0) continue;

                                            ItemStackSlotTransaction t2 = target2.addItemStackToSlot((short) slot, hopperStack2.withQuantity(transferAmount));
                                            if (t2 != null && t2.succeeded()) {
                                                boolean verified2 = false;
                                                try {
                                                    ItemStack after2 = target2.getItemStack((short) slot);
                                                    if (after2 != null && after2.getQuantity() >= transferAmount) verified2 = true;
                                                } catch (Throwable ignored) {}
                                                if (!verified2) {
                                                    try { target2.removeItemStackFromSlot((short) slot, transferAmount); } catch (Throwable ignored) {}
                                                    continue;
                                                }

                                                try { spawnVisualFor(hopperStack2.withQuantity(transferAmount), true, pos, side, entities); } catch (Throwable ignored) {}
                                                this.getItemContainer().removeItemStackFromSlot((short) 0, transferAmount);
                                                try { putHopperComponent(cs, blockRef, (HopperComponent) hopperCompObj); } catch (Throwable tt) {
                                                    Ev0Log.warn(LOGGER, "putHopperComponent failed: " + (tt == null ? "null" : tt.getMessage()));
                                                }

                                                // Persist this hopper so clients see the updated slot/state
                                                try {
                                                    if (w != null) {
                                                        Store<ChunkStore> cs2 = w.getChunkStore().getStore();
                                                        Ref<ChunkStore> colRef2 = w.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(cachedPosition.x, cachedPosition.z));
                                                        if (colRef2 != null) {
                                                            BlockComponentChunk bcc2 = cs2.getComponent(colRef2, BlockComponentChunk.getComponentType());
                                                            if (bcc2 != null) {
                                                                Ref<ChunkStore> myBlockRef = bcc2.getEntityReference(ChunkUtil.indexBlockInColumn(cachedPosition.x, cachedPosition.y, cachedPosition.z));
                                                                if (myBlockRef != null) {
                                                                    try { putHopperComponent(cs2, myBlockRef, this); } catch (Throwable tt) {
                                                                        Ev0Log.warn(LOGGER, "putHopperComponent (self) failed: " + (tt == null ? "null" : tt.getMessage()));
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                } catch (Throwable ignored) {}

                                                return true;
                                            }
                                        } catch (Throwable ignored) {}
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Throwable ignored) {}
        }

        if (state == null) return false;
        if ((!(state != null && state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState")) && !state.getClass().getSimpleName().contains("ItemContainer"))) return false;
        boolean isProcessingBench = (state != null && state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState"));
        Object bench = isProcessingBench ? state : null;
        if (isProcessingBench) {
            ItemContainer output = getContainerFromItemContainerObject(getItemContainerFromState(bench), 2);
            if (!exportPhase) {
                for (int slot = 0; slot < output.getCapacity(); slot++) {
                    ItemStack stack = output.getItemStack((short) slot);
                    if (stack == null) continue;
                    String probeKeyPb = null; try { probeKeyPb = stack.getBlockKey(); } catch (Throwable ignored) {}
                    if (probeKeyPb == null) probeKeyPb = resolveItemStackKey(stack);
                    if (!isItemAllowedByFilter(probeKeyPb)) continue;
                    int pbAvailable = stack.getQuantity();
                    if (isSingletonMode() && pbAvailable <= 1) continue;
                    int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), isSingletonMode() ? pbAvailable - 1 : pbAvailable); if (transferAmount <= 0) continue;
                    ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short)0, stack.withQuantity(transferAmount));
                    if (t.succeeded()) {
                        this.getItemContainer().removeItemStackFromSlot((short)0, transferAmount);
                        try { spawnVisualFor(stack.withQuantity(transferAmount), false, pos, side, entities); } catch (Throwable ignored) {}
                        return true;
                    }
                }
            }
            Object containerStateObj = null; try { java.lang.reflect.Method m = state.getClass().getMethod("getItemContainer"); containerStateObj = m.invoke(state);} catch (Throwable ignored) {}
            if (containerStateObj == null) return false; ItemContainer target = (ItemContainer) containerStateObj;
            for (int slot = 0; slot < target.getCapacity(); slot++) {
                if (!isItemAllowedByFilter(this.getItemContainer().getItemStack((short)0) == null ? null : this.getItemContainer().getItemStack((short)0).getBlockKey())) return false;
                int transferAmount = (int)Math.min(data.tier * Ev0Config.getTierMultiplier(), this.getItemContainer().getItemStack((short)0).getQuantity());
                ItemStack safeStack = this.getItemContainer().getItemStack((short)0).withQuantity(transferAmount);
                ItemStackSlotTransaction t = target.addItemStackToSlot((short) slot, safeStack);
                if (t.succeeded()) {
                    try { spawnVisualFor(safeStack, true, pos, side, entities); } catch (Throwable ignored) {}
                    this.getItemContainer().removeItemStackFromSlot((short)0, transferAmount);
                    return true;
                }
            }
            return false;
        }
        // fallback: container object direct
        Object containerStateObj = null; try { java.lang.reflect.Method m = state.getClass().getMethod("getItemContainer"); containerStateObj = m.invoke(state);} catch (Throwable ignored) {}
        if (containerStateObj == null) return false; ItemContainer sourceContainer = (ItemContainer) containerStateObj;
        // If exporting, attempt to push into the target block's container (supports chained hoppers)
        if (exportPhase) {
            try {
                ItemStack hopperStack = this.getItemContainer().getItemStack((short)0);
                if (hopperStack != null) {
                    for (int slot = 0; slot < sourceContainer.getCapacity(); slot++) {
                        try {
                            if (!isItemAllowedByFilter(hopperStack.getBlockKey())) break;
                            int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), hopperStack.getQuantity());
                            if (transferAmount <= 0) break;
                            ItemStack safeStack = hopperStack.withQuantity(transferAmount);
                            ItemStackSlotTransaction t = sourceContainer.addItemStackToSlot((short) slot, safeStack);
                            try { Ev0Log.info(LOGGER, "fallback container transfer attempt slot=" + slot + " transferAmount=" + transferAmount + " tx=" + (t == null ? "null" : String.valueOf(t.succeeded()))); } catch (Throwable ignored) {}
                            if (t != null && t.succeeded()) {
                                boolean verified = false;
                                try {
                                    ItemStack after = sourceContainer.getItemStack((short) slot);
                                    if (after != null && after.getQuantity() >= transferAmount) verified = true;
                                } catch (Throwable ignored) {}
                                if (!verified) {
                                    try { sourceContainer.removeItemStackFromSlot((short) slot, transferAmount); } catch (Throwable ignored) {}
                                    continue;
                                }
                                try { spawnVisualFor(safeStack, true, pos, side, entities); } catch (Throwable ignored) {}
                                this.getItemContainer().removeItemStackFromSlot((short)0, transferAmount);
                                return true;
                            }
                        } catch (Throwable ignored) {}
                    }
                }
            } catch (Throwable ignored) {}
        }
        for (int slot = 0; slot < sourceContainer.getCapacity(); slot++) {
            ItemStack stack = sourceContainer.getItemStack((short) slot);
            if (stack == null) continue;
            String probeKey2 = null; try { probeKey2 = stack.getBlockKey(); } catch (Throwable ignored) {}
            if (probeKey2 == null) probeKey2 = resolveItemStackKey(stack);
            if (!isItemAllowedByFilter(probeKey2)) continue;
            int srcAvailable = stack.getQuantity();
            if (isSingletonMode() && srcAvailable <= 1) continue;
            int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), isSingletonMode() ? srcAvailable - 1 : srcAvailable); if (transferAmount <= 0) continue;
            ItemStack safeStack = stack.withQuantity(transferAmount);
            ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short)0, safeStack);
            if (t.succeeded()) {
                sourceContainer.removeItemStackFromSlot((short) slot, transferAmount);
                try { spawnVisualFor(safeStack, exportPhase, pos, side, entities); } catch (Throwable ignored) {}
                return true;
            }
        }
        return false;
    }

    private boolean tryImportFromContainer(WorldChunk chunk,
                                           Vector3i pos,
                                           Store<EntityStore> entities,
                                           AdjacentSide side) {
        // Fast path: if this hopper is already full, skip scanning containers entirely.
        ItemStack destStack = this.getItemContainer().getItemStack((short) 0);
        try { HytaleLogger.getLogger().atInfo().log("[HopperDiag] tryImportFromContainer pos=" + pos + " destStack=" + (destStack == null ? "null" : String.valueOf(destStack))); } catch (Throwable ignored) {}
        if (destStack != null && destStack.getQuantity() >= 100) return false;

        try {
            String preMsg = "[HopperDiag] tryImportFromContainer START pos=" + pos + " chunkIndex=" + ChunkUtil.indexChunkFromBlock(pos.x, pos.z) + " chunkPresent=" + (chunk != null);
            try { HytaleLogger.getLogger().atWarning().log(preMsg); } catch (Throwable ignored) {}
            try { System.out.println(preMsg); } catch (Throwable ignored) {}
        } catch (Throwable ignored) {}
        Object state = org.Ev0Mods.plugin.api.component.EngineCompat.getState(chunk, pos.x, pos.y, pos.z);
        try { HytaleLogger.getLogger().atWarning().log("[HopperDiag] tryImportFromContainer: pos=" + pos + " stateClass=" + (state == null ? "null" : state.getClass().getName())); } catch (Throwable ignored) {}
        try { System.out.println("[HopperDiag] tryImportFromContainer: pos=" + pos + " stateClass=" + (state == null ? "null" : state.getClass().getName())); } catch (Throwable ignored) {}

        if (state == null) {
            Object blockType = null;
            try {
                blockType = org.Ev0Mods.plugin.api.component.EngineCompat.getBlockType(chunk, pos.x, pos.y, pos.z);
                HytaleLogger.getLogger().atWarning().log("[HopperDiag] EngineCompat.getBlockType for pos=" + pos + " -> " + (blockType == null ? "null" : blockType.toString()));
                try { System.out.println("[HopperDiag] EngineCompat.getBlockType for pos=" + pos + " -> " + (blockType == null ? "null" : blockType.toString())); } catch (Throwable ignored) {}
            } catch (Throwable ignored) {}
            try { HytaleLogger.getLogger().atWarning().log("[HopperDiag] state==null at pos=" + pos + "; blockTypeClass=" + (blockType == null ? "null" : blockType.getClass().getName())); } catch (Throwable ignored) {}
            try {
                if (w != null) {
                    Store<ChunkStore> cs = w.getChunkStore().getStore();
                    Ref<ChunkStore> colRef = w.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
                    if (colRef == null) {
                        HytaleLogger.getLogger().atInfo().log("[HopperDiag] chunk reference is null for chunkIndex=" + ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
                    } else {
                        BlockComponentChunk bcc = cs.getComponent(colRef, BlockComponentChunk.getComponentType());
                        HytaleLogger.getLogger().atInfo().log("[HopperDiag] BlockComponentChunk present=" + (bcc != null));
                        if (bcc != null) {
                            Ref<ChunkStore> blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn(pos.x, pos.y, pos.z));
                            String brMsg = "[HopperDiag] blockRef for pos=" + pos + " -> " + (blockRef == null ? "null" : "index=" + blockRef.getIndex());
                            HytaleLogger.getLogger().atWarning().log(brMsg);
                            try { System.out.println(brMsg); } catch (Throwable ignored) {}
                            // Component-first: if blockRef exists, try to read an ItemContainerBlock component and extract its ItemContainer
                            try {
                                if (blockRef != null && w != null) {
                                    try {
                                        // Prefer direct hopper-to-hopper import when adjacent block has HopperComponent.
                                        try {
                                            Ev0Lib lib = Ev0Lib.getInstance();
                                            HopperComponent sourceHopper = (lib != null && lib.getHopperComponentType() != null)
                                                    ? cs.getComponent(blockRef, lib.getHopperComponentType())
                                                    : null;
                                            if (sourceHopper != null && sourceHopper.getItemContainer() != null) {
                                                ItemContainer source = sourceHopper.getItemContainer();
                                                for (int slot = 0; slot < source.getCapacity(); slot++) {
                                                    try {
                                                        ItemStack stack = source.getItemStack((short) slot);
                                                        if (stack == null) continue;
                                                        String blockKey = null; try { blockKey = stack.getBlockKey(); } catch (Throwable ignored) {}
                                                        if (blockKey == null) blockKey = resolveItemStackKey(stack);
                                                        if (!isItemAllowedByFilter(blockKey)) continue;
                                                        int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), stack.getQuantity());
                                                        if (transferAmount <= 0) continue;
                                                        ItemStack safeStack = stack.withQuantity(transferAmount);
                                                        ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short) 0, safeStack);
                                                        try { Ev0Log.info(LOGGER, "direct-hopper import attempt slot=" + slot + " transferAmount=" + transferAmount + " tx=" + (t == null ? "null" : String.valueOf(t.succeeded()))); } catch (Throwable ignored) {}
                                                        if (t != null && t.succeeded()) {
                                                            try { source.removeItemStackFromSlot((short) slot, transferAmount); } catch (Throwable ignored) {}
                                                            try { spawnVisualFor(safeStack, false, pos, side, entities); } catch (Throwable ignored) {}
                                                            try { putHopperComponent(cs, blockRef, sourceHopper); } catch (Throwable ignored) {}
                                                            try {
                                                                Ref<ChunkStore> colRef2 = w.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(cachedPosition.x, cachedPosition.z));
                                                                if (colRef2 != null) {
                                                                    BlockComponentChunk bcc2 = cs.getComponent(colRef2, BlockComponentChunk.getComponentType());
                                                                    if (bcc2 != null) {
                                                                        Ref<ChunkStore> myBlockRef = bcc2.getEntityReference(ChunkUtil.indexBlockInColumn(cachedPosition.x, cachedPosition.y, cachedPosition.z));
                                                                        if (myBlockRef != null) putHopperComponent(cs, myBlockRef, this);
                                                                    }
                                                                }
                                                            } catch (Throwable ignored) {}
                                                            return true;
                                                        }
                                                    } catch (Throwable ignored) {}
                                                }
                                            }
                                        } catch (Throwable ignored) {}

                                        Method getComp = cs.getClass().getMethod("getComponent", Ref.class, Class.forName("com.hypixel.hytale.component.ComponentType"));
                                        Class<?> icbCls = Class.forName("com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock");
                                        Method getCompType = icbCls.getMethod("getComponentType");
                                        Object compType = getCompType.invoke(null);
                                        Object icbObj = null;
                                        try { icbObj = getComp.invoke(cs, blockRef, compType); } catch (Throwable ignored) { icbObj = null; }
                                        if (icbObj != null && icbCls.isInstance(icbObj)) {
                                            try {
                                                Method getIC = icbCls.getMethod("getItemContainer");
                                                Object cont = getIC.invoke(icbObj);
                                                if (cont instanceof ItemContainer) {
                                                    ItemContainer compContainer = (ItemContainer) cont;
                                                    HytaleLogger.getLogger().atInfo().log("[HopperDiag] component-first container capacity=" + compContainer.getCapacity());
                                                    for (int slot = 0; slot < compContainer.getCapacity(); slot++) {
                                                        try {
                                                            ItemStack stack = compContainer.getItemStack((short) slot);
                                                            if (stack == null) continue;
                                                            String blockKey = null; try { blockKey = stack.getBlockKey(); } catch (Throwable ignored) {}
                                                            if (blockKey == null) blockKey = resolveItemStackKey(stack);
                                                            if (!isItemAllowedByFilter(blockKey)) continue;
                                                            int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), stack.getQuantity());
                                                            if (transferAmount <= 0) continue;
                                                            ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short)0, stack.withQuantity(transferAmount));
                                                            try { HytaleLogger.getLogger().atInfo().log("[HopperDiag] componentContainer transferAttempt slot=" + slot + " amt=" + transferAmount + " tx=" + (t == null ? "null" : String.valueOf(t.succeeded()))); } catch (Throwable ignored) {}
                                                            if (t != null && t.succeeded()) {
                                                                try { compContainer.removeItemStackFromSlot((short) slot, transferAmount); } catch (Throwable ignored) {}

                                                                // Persist this (destination) hopper so the updated slot/state becomes visible
                                                                // to the rest of the engine/UI in prerelease builds.
                                                                try {
                                                                    if (w != null) {
                                                                        Store<ChunkStore> cs2 = w.getChunkStore().getStore();
                                                                        Ref<ChunkStore> colRef2 = w.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(cachedPosition.x, cachedPosition.z));
                                                                        if (colRef2 != null) {
                                                                            BlockComponentChunk bcc2 = cs2.getComponent(colRef2, BlockComponentChunk.getComponentType());
                                                                            if (bcc2 != null) {
                                                                                Ref<ChunkStore> myBlockRef = bcc2.getEntityReference(ChunkUtil.indexBlockInColumn(cachedPosition.x, cachedPosition.y, cachedPosition.z));
                                                                                if (myBlockRef != null) putHopperComponent(cs2, myBlockRef, this);
                                                                            }
                                                                        }
                                                                    }
                                                                } catch (Throwable ignored2) {}

                                                                return true;
                                                            }
                                                        } catch (Throwable ignored) {}
                                                    }
                                                }
                                            } catch (Throwable ignored) {}
                                        }
                                        // If the adjacent block has a HopperComponent, import (pull) from its ItemContainer
                                        // rather than writing directly into its internal container. This ensures the
                                        // receiving hopper's state is updated correctly via its own component lifecycle.
                                        try {
                                            Class<?> hopperCompCls = Class.forName("org.Ev0Mods.plugin.api.component.HopperComponent");
                                            Method getCompTypeH = hopperCompCls.getMethod("getComponentType");
                                            Object hopperCompType = getCompTypeH.invoke(null);
                                            Object hopperCompObj = null;
                                            try { hopperCompObj = getComp.invoke(cs, blockRef, hopperCompType); } catch (Throwable ignored) { hopperCompObj = null; }
                                            if (hopperCompObj != null && hopperCompCls.isInstance(hopperCompObj)) {
                                                try {
                                                    Method getIC = hopperCompCls.getMethod("getItemContainer");
                                                    Object cont = getIC.invoke(hopperCompObj);
                                                    if (cont instanceof ItemContainer) {
                                                        ItemContainer source = (ItemContainer) cont;
                                                        for (int slot = 0; slot < source.getCapacity(); slot++) {
                                                            try {
                                                                ItemStack stack = source.getItemStack((short) slot);
                                                                if (stack == null) continue;
                                                                String blockKey = null; try { blockKey = stack.getBlockKey(); } catch (Throwable ignored) {}
                                                                if (blockKey == null) blockKey = resolveItemStackKey(stack);
                                                                if (!isItemAllowedByFilter(blockKey)) continue;
                                                                int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), stack.getQuantity());
                                                                if (transferAmount <= 0) continue;
                                                                ItemStack safeStack = stack.withQuantity(transferAmount);
                                                                ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short)0, safeStack);
                                                                if (t != null && t.succeeded()) {
                                                                    try { source.removeItemStackFromSlot((short) slot, transferAmount); } catch (Throwable ignored) {}
                                                                    try { spawnVisualFor(safeStack, false, pos, side, entities); } catch (Throwable ignored) {}
                                                                    boolean putSucceeded = false;
                                                                    try {
                                                                        putHopperComponent(cs, blockRef, (HopperComponent) hopperCompObj);
                                                                        putSucceeded = true;
                                                                    } catch (Throwable tt) { Ev0Log.warn(LOGGER, "putHopperComponent failed: " + (tt == null ? "null" : tt.getMessage())); }
                                                                    try { Ev0Log.info(LOGGER, "import-from-chained-hopper succeeded slot=" + slot + " putSucceeded=" + putSucceeded); } catch (Throwable ignored) {}
                                                                    // Persist this (destination) hopper so clients receive updated UI/state
                                                                    try {
                                                                        if (w != null) {
                                                                            Store<ChunkStore> cs2 = w.getChunkStore().getStore();
                                                                            Ref<ChunkStore> colRef2 = w.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(cachedPosition.x, cachedPosition.z));
                                                                            if (colRef2 != null) {
                                                                                BlockComponentChunk bcc2 = cs2.getComponent(colRef2, BlockComponentChunk.getComponentType());
                                                                                if (bcc2 != null) {
                                                                                    Ref<ChunkStore> myBlockRef = bcc2.getEntityReference(ChunkUtil.indexBlockInColumn(cachedPosition.x, cachedPosition.y, cachedPosition.z));
                                                                                    if (myBlockRef != null) {
                                                                                        try { putHopperComponent(cs2, myBlockRef, this); } catch (Throwable tt) { Ev0Log.warn(LOGGER, "putHopperComponent (self) failed: " + (tt == null ? "null" : tt.getMessage())); }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    } catch (Throwable ignored) {}
                                                                    return true;
                                                                }
                                                            } catch (Throwable ignored) {}
                                                        }
                                                    }
                                                } catch (Throwable ignored) {}
                                            }
                                        } catch (Throwable ignored) {}
                                    } catch (Throwable ignored) {}
                                }
                            } catch (Throwable ignored) {}
                            
                        }
                    }
                } else {
                    HytaleLogger.getLogger().atInfo().log("[HopperDiag] world reference (w) is null while inspecting pos=" + pos);
                }
            } catch (Throwable t) { try { HytaleLogger.getLogger().atWarning().log("[HopperDiag] exception while inspecting chunk/block refs: " + t.getMessage()); } catch (Throwable ignored) {} }
        }

        /* ---------------------------------
       Special handling: Processing Bench
       --------------------------------- */

        // Some engine versions may expose an ItemContainer directly from the block state
        // or via an ItemContainer-like state object. Try both direct ItemContainer and
        // reflected extraction early to handle these variants before falling back to
        // the existing processing-bench and generic paths.
        try {
            if (state != null && !state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState")) {
                ItemContainer direct = null;
                try {
                    if (state instanceof ItemContainer) {
                        direct = (ItemContainer) state;
                        HytaleLogger.getLogger().atInfo().log("[HopperDiag] state is ItemContainer directly capacity=" + direct.getCapacity());
                    } else {
                        Object contObj = getItemContainerFromState(state);
                        if (contObj instanceof ItemContainer) direct = (ItemContainer) contObj;
                        else direct = getContainerFromItemContainerObject(contObj, 0);
                        if (direct != null) HytaleLogger.getLogger().atInfo().log("[HopperDiag] extracted ItemContainer from state via reflection capacity=" + direct.getCapacity());
                    }
                } catch (Throwable ignored) { direct = null; }

                if (direct != null) {
                    try { HytaleLogger.getLogger().atInfo().log("[HopperDiag] handling direct/extracted container for pos=" + pos + " capacity=" + direct.getCapacity()); } catch (Throwable ignored) {}
                    int capd = direct.getCapacity();
                    for (int slot = 0; slot < capd; slot++) {
                        try {
                            ItemStack stack = direct.getItemStack((short) slot);
                            if (stack == null) continue;
                            String blockKey = null; try { blockKey = stack.getBlockKey(); } catch (Throwable ignored) {}
                            if (blockKey == null) blockKey = resolveItemStackKey(stack);
                            if (!isItemAllowedByFilter(blockKey)) continue;
                            int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), stack.getQuantity());
                            if (transferAmount <= 0) continue;
                            ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short)0, stack.withQuantity(transferAmount));
                            try { HytaleLogger.getLogger().atInfo().log("[HopperDiag] directContainer transferAttempt slot=" + slot + " amt=" + transferAmount + " tx=" + (t == null ? "null" : String.valueOf(t.succeeded()))); } catch (Throwable ignored) {}
                            if (t != null && t.succeeded()) {
                                try { direct.removeItemStackFromSlot((short) slot, transferAmount); } catch (Throwable ignored) {}
                                try { spawnVisualFor(stack.withQuantity(transferAmount), false, pos, side, entities); } catch (Throwable ignored) {}
                                return true;
                            }
                        } catch (Throwable ignored) {}
                    }
                }
            }
        } catch (Throwable ignored) {}
        if (state != null && state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState")) {

            // Only import from output container (2)
            ItemContainer output = getContainerFromItemContainerObject(getItemContainerFromState(state), 2);
            if (output == null) {
                try { HytaleLogger.getLogger().atInfo().log("[HopperDiag] processingBench: output container null"); } catch (Throwable ignored) {}
                return false;
            }
            try { HytaleLogger.getLogger().atInfo().log("[HopperDiag] processingBench: output capacity=" + output.getCapacity()); } catch (Throwable ignored) {}

            int outCap = output.getCapacity();
            for (int slot = 0; slot < outCap; slot++) {
            ItemStack stack = output.getItemStack((short) slot);
            if (stack == null) continue;
            // Apply filter: prefer fast path via getBlockKey() then fallback to resolve
            String blockKey = null;
            try { blockKey = stack.getBlockKey(); } catch (Throwable ignored) {}
            if (blockKey == null) blockKey = resolveItemStackKey(stack);
            if (!isItemAllowedByFilter(blockKey)) continue;

                int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), stack.getQuantity());
                if (transferAmount <= 0) continue;

                ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short) 0, stack.withQuantity(transferAmount));
                try { HytaleLogger.getLogger().atInfo().log("[HopperDiag] processingBench transferAttempt slot=" + slot + " amt=" + transferAmount + " tx=" + (t == null ? "null" : String.valueOf(t.succeeded()))); } catch (Throwable ignored) {}

                if (t != null && t.succeeded()) {

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
                        return true;
                }
            }

            return false;
        }

        // Generic container import
        ItemContainer container = null;
        try { Object contObj = getItemContainerFromState(state); if (contObj instanceof ItemContainer) container = (ItemContainer) contObj; else container = getContainerFromItemContainerObject(contObj, 0); } catch (Throwable ignored) {}
        if (container == null) { try { HytaleLogger.getLogger().atInfo().log("[HopperDiag] generic container null for state=" + (state == null ? "null" : state.getClass().getName())); } catch (Throwable ignored) {} return false; }
        try { HytaleLogger.getLogger().atInfo().log("[HopperDiag] generic container capacity=" + container.getCapacity()); } catch (Throwable ignored) {}
        int cap = container.getCapacity();
        for (int slot = 0; slot < cap; slot++) {
            try {
                ItemStack stack = container.getItemStack((short) slot);
                if (stack == null) continue;
                String blockKey = null; try { blockKey = stack.getBlockKey(); } catch (Throwable ignored) {}
                if (blockKey == null) blockKey = resolveItemStackKey(stack);
                if (!isItemAllowedByFilter(blockKey)) continue;
                int transferAmount = (int) Math.min(data.tier * Ev0Config.getTierMultiplier(), stack.getQuantity());
                if (transferAmount <= 0) continue;
                ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short)0, stack.withQuantity(transferAmount));
                try { HytaleLogger.getLogger().atInfo().log("[HopperDiag] genericContainer transferAttempt slot=" + slot + " amt=" + transferAmount + " tx=" + (t == null ? "null" : String.valueOf(t.succeeded()))); } catch (Throwable ignored) {}
                if (t != null && t.succeeded()) {
                    container.removeItemStackFromSlot((short) slot, transferAmount);
                    return true;
                }
            } catch (Throwable ignored) { }
        }
        return false;
    }

    private boolean tryPickupItemEntities(Vector3i importPos, Store<EntityStore> entities) {
        perfInfo("[Hopper][Pickup] tryPickupItemEntities called at " + importPos);
        final java.util.List rawResults = (java.util.List) SpatialResource.getThreadLocalReferenceList();
        final Vector3d boxMin = new Vector3d(importPos.x, importPos.y, importPos.z);
        final Vector3d boxMax = new Vector3d(importPos.x + 1.0, importPos.y + 1.0, importPos.z + 1.0);
        perfInfo("[Hopper][Pickup] collectBox min=" + boxMin + " max=" + boxMax);
        ((ComponentAccessor<EntityStore>) entities).getResource(EntityModule.get().getItemSpatialResourceType()).getSpatialStructure().collectBox(boxMin, boxMax, rawResults);
        perfInfo("[Hopper][Pickup] collectBox rawResults.size()=" + rawResults.size());
        try { HytaleLogger.getLogger().atInfo().log("[HopperDiag] tryPickupItemEntities rawResults=" + rawResults.size()); } catch (Throwable ignored) {}
        if (rawResults.isEmpty()) { perfInfo("[Hopper][Pickup] no items found in box, returning false"); return false; }
        List<Ref<EntityStore>> itemRefs = new ArrayList<>(rawResults);
        int hopperQty = this.getItemContainer().getItemStack((short)0) == null ? 0 : this.getItemContainer().getItemStack((short)0).getQuantity();
        if (hopperQty >= 100) return false;
        for (Ref<EntityStore> ref : itemRefs) {
            if (ref == null || !ref.isValid()) continue;
            if (l.contains(ref)) continue;
            if (entities.getComponent(ref, Intangible.getComponentType()) != null) {}
            ItemComponent ic = (ItemComponent) entities.getComponent(ref, ItemComponent.getComponentType()); if (ic == null) continue;
            if (!ic.canPickUp()) continue;
            ItemStack stack = ic.getItemStack(); if (stack == null || stack.isEmpty()) continue;
            String itemKey = null; try { itemKey = stack.getBlockKey(); } catch (Throwable ignored) {} if (itemKey == null) itemKey = resolveItemStackKey(stack);
            if (!isItemAllowedByFilter(itemKey)) continue;
            int transferAmount = (int)Math.min(data.tier * Ev0Config.getTierMultiplier(), Math.min(stack.getQuantity(), 100 - hopperQty)); if (transferAmount <= 0) continue;
            ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short)0, stack.withQuantity(transferAmount));
            try { HytaleLogger.getLogger().atInfo().log("[HopperDiag] pickup attempt ref=" + ref + " amt=" + transferAmount + " tx=" + (t == null ? "null" : String.valueOf(t.succeeded()))); } catch (Throwable ignored) {}
            if (t != null && t.succeeded()) {
                int remaining = stack.getQuantity() - transferAmount;
                if (remaining <= 0) { entities.removeEntity(ref, RemoveReason.REMOVE); }
                else {
                    TransformComponent tc = entities.getComponent(ref, TransformComponent.getComponentType()); Vector3d dropPos = tc != null ? tc.getPosition().clone() : new Vector3d(importPos.x + 0.5, importPos.y + 0.5, importPos.z + 0.5);
                    entities.removeEntity(ref, RemoveReason.REMOVE);
                    Holder<EntityStore> newHolder = ItemComponent.generateItemDrop((ComponentAccessor<EntityStore>) entities, stack.withQuantity(remaining), dropPos, Vector3f.ZERO, 0, -1, 0);
                    if (newHolder != null) entities.addEntity(newHolder, AddReason.SPAWN);
                }
                return true;
            }
        }
        return false;
    }

    private void ensureArcioComponents(World world, @Nullable CommandBuffer<ChunkStore> commandBuffer) {
        if (arcioInitialized) return;
        try {
            Vector3i p = getBlockPosition(); int bx = p.x, by = p.y, bz = p.z;
            Store<ChunkStore> cs = world.getChunkStore().getStore(); Ref<ChunkStore> chunkRef = world.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(bx, bz)); if (chunkRef == null) return;
            BlockComponentChunk bcc = (BlockComponentChunk) cs.getComponent(chunkRef, BlockComponentChunk.getComponentType()); if (bcc == null) return;
            Ref<ChunkStore> blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn(bx, by, bz)); if (blockRef == null) return;
            BlockUUIDComponent uuid;
            uuid = (BlockUUIDComponent) cs.getComponent(blockRef, BlockUUIDComponent.getComponentType());
if (uuid == null) { uuid = BlockUUIDComponent.randomUUID(); uuid.setPosition(new Vector3i(bx, by, bz)); if (commandBuffer != null) commandBuffer.putComponent(blockRef, BlockUUIDComponent.getComponentType(), uuid); else cs.putComponent(blockRef, BlockUUIDComponent.getComponentType(), uuid); voidbond.arcio.ArcioPlugin.get().putUUID(uuid.getUuid(), blockRef); }
            ArcioMechanismComponent mech = (ArcioMechanismComponent) cs.getComponent(blockRef, ArcioMechanismComponent.getComponentType()); if (mech == null) { mech = new ArcioMechanismComponent("Hopper", 0, 1); if (commandBuffer != null) commandBuffer.putComponent(blockRef, ArcioMechanismComponent.getComponentType(), mech); else cs.putComponent(blockRef, ArcioMechanismComponent.getComponentType(), mech); }
            arcioInitialized = true;
        } catch (Exception e) { Ev0Log.warn(HytaleLogger.getLogger(), "[Hopper] Failed to ensure ArcIO components: " + e.getMessage()); }
    }

    private boolean isArcioActive(World world) {
        try { Vector3i p = getBlockPosition(); int bx = p.x, by = p.y, bz = p.z; Store<ChunkStore> cs = world.getChunkStore().getStore(); Ref<ChunkStore> chunkRef = world.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(bx, bz)); if (chunkRef != null) { BlockComponentChunk bcc = (BlockComponentChunk) cs.getComponent(chunkRef, BlockComponentChunk.getComponentType()); if (bcc != null) { Ref<ChunkStore> blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn(bx, by, bz)); if (blockRef != null) { ArcioMechanismComponent mc = (ArcioMechanismComponent) cs.getComponent(blockRef, ArcioMechanismComponent.getComponentType()); if (mc != null && mc.getStrongestInputSignal(world) > 0) return true; } } } } catch (Exception e) { Ev0Log.warn(HytaleLogger.getLogger(), "[Hopper] ArcIO signal check failed: " + e.getMessage()); } return hasAdjacentActiveArcioMechanism(world);
    }

    private boolean hasAdjacentActiveArcioMechanism(World world) {
        try { Store<ChunkStore> cs = world.getChunkStore().getStore(); Vector3i p = getBlockPosition(); int bx = p.x, by = p.y, bz = p.z; int[][] offsets = {{1,0,0},{-1,0,0},{0,1,0},{0,-1,0},{0,0,1},{0,0,-1}}; for (int[] off : offsets) { int nx = bx + off[0], ny = by + off[1], nz = bz + off[2]; Ref<ChunkStore> chunkRef = world.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(nx, nz)); if (chunkRef == null) continue; BlockComponentChunk bcc = (BlockComponentChunk) cs.getComponent(chunkRef, BlockComponentChunk.getComponentType()); if (bcc == null) continue; Ref<ChunkStore> blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn(nx, ny, nz)); if (blockRef == null) continue; ArcioMechanismComponent mc = (ArcioMechanismComponent) cs.getComponent(blockRef, ArcioMechanismComponent.getComponentType()); if (mc != null && mc.getStrongestInputSignal(world) > 0) return true; } } catch (Exception e) { Ev0Log.warn(HytaleLogger.getLogger(), "[Hopper] ArcIO adjacent check failed: " + e.getMessage()); } return false; }

    protected void reset(Instant currentTime) { startTime = currentTime; }

    @Nonnull
    public static List<Ref<EntityStore>> getAllEntitiesInBox(HopperComponent hp, Vector3i pos, float height, @Nonnull ComponentAccessor<EntityStore> components, boolean players, boolean entities, boolean items) {
        final java.util.List results = (java.util.List) SpatialResource.getThreadLocalReferenceList(); final Vector3d center = new Vector3d(pos.x, pos.y, pos.z); final double queryHeight = Math.max(1.0f, height);
        if (players) components.getResource(EntityModule.get().getPlayerSpatialResourceType()).getSpatialStructure().collectCylinder(center, 4, queryHeight, results);
        if (entities) {}
        if (items) {}
        if (hp != null && hp.nearbyBuffer != null) { hp.nearbyBuffer.clear(); hp.nearbyBuffer.addAll(results); return hp.nearbyBuffer; }
        return new ArrayList<>(results);
    }

    public static List<Ref<EntityStore>> getAllItemsInBox(HopperComponent hp, Vector3i pos, float height, @Nonnull ComponentAccessor<EntityStore> components, boolean players, boolean entities, boolean items) {
        final java.util.List results = (java.util.List) SpatialResource.getThreadLocalReferenceList(); final Vector3d center = new Vector3d(pos.x, pos.y, pos.z); final double queryHeight = Math.max(0.5f, height);
        if (entities) { final Vector3d min = new Vector3d(pos.x - 0.5, pos.y - 0.5, pos.z - 0.5); final Vector3d max = new Vector3d(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5); components.getResource(EntityModule.get().getEntitySpatialResourceType()).getSpatialStructure().collectBox(min, max, results); }
        if (items) components.getResource(EntityModule.get().getItemSpatialResourceType()).getSpatialStructure().collectCylinder(center, 2, Math.max(0.5, queryHeight), results);
        if (hp != null && hp.nearbyBuffer != null) { hp.nearbyBuffer.clear(); hp.nearbyBuffer.addAll(results); return hp.nearbyBuffer; }
        return new ArrayList<>(results);
    }

    // Reflection helper moved from HopperProcessor
    private Object getRefFromArchetype(ArchetypeChunk<?> archeChunk, int index) {
        try {
            String key = "ArchetypeChunk.getRef"; Method m = REFLECTION_METHOD_CACHE.get(key);
            if (m == null) {
                Class<?> ac = archeChunk.getClass();
                for (String name : new String[]{"getReferenceTo", "getRef", "getRefAt", "referenceTo", "getReference"}) { try { m = ac.getMethod(name, int.class); break; } catch (NoSuchMethodException ignored) {} }
                if (m != null) REFLECTION_METHOD_CACHE.put(key, m);
            }
            if (m != null) return m.invoke(archeChunk, index);
        } catch (Throwable ignored) {}
        return null;
    }

    private HopperComponent getHopperComponent(Store<ChunkStore> store, Object ref) {
        try {
            Ev0Lib lib = Ev0Lib.getInstance(); if (lib == null) return null; Object compType = Ev0Lib.getInstance().getHopperComponentType(); if (compType == null) return null; Method getter = null; for (Method mm : store.getClass().getMethods()) { if (!mm.getName().equals("getComponent")) continue; if (mm.getParameterCount() == 2) { getter = mm; break; } } if (getter == null) return null; Object comp = getter.invoke(store, ref, compType); if (comp instanceof HopperComponent) return (HopperComponent) comp; } catch (Throwable ignored) {} return null;
    }

    private void putHopperComponent(Store<ChunkStore> store, Object ref, HopperComponent comp) {
        try {
            Method put = null;
            for (Method mm : store.getClass().getMethods()) {
                if (!mm.getName().equals("putComponent")) continue;
                if (mm.getParameterCount() == 2) {
                    put = mm;
                    break;
                }
            }
            if (put != null) {
                put.invoke(store, ref, comp);
                return;
            }
            HopperComponent compType = getHopperComponentType();
            Method ensure = null;
            for (Method mm : store.getClass().getMethods()) {
                if (mm.getName().equals("ensureAndGetComponent") && mm.getParameterCount() == 2) {
                    ensure = mm;
                    break;
                }
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

    private HopperComponent getHopperComponentType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHopperComponentType'");
    }

    // Minimal chunk/ref helpers
    private WorldChunk getChunkFromStoreRef(Store<ChunkStore> store, Object ref) { try { if (ref instanceof Ref) { Ref r = (Ref) ref; // best-effort
                return null; } } catch (Throwable ignored) {} return null; }

    private Vector3i extractPositionFromStoreRef(Store<ChunkStore> store, Object ref) { try { // unable to reliably compute here in a generic way; return cachedPosition
            return cachedPosition; } catch (Throwable ignored) { return cachedPosition; } }

    /**
     * Scans the chunk store to find which column's BlockComponentChunk maps to this
     * block entity, then decodes the world position from the block index + chunk coords.
     * Called once on first tick when cachedPosition is still (0,0,0).
     */
    @SuppressWarnings("unchecked")
    private void resolvePosition(Store<ChunkStore> store, Ref<ChunkStore> myRef) {
        try {
            int myIdx = myRef.getIndex();
            ChunkStore cs = store.getExternalData();
            it.unimi.dsi.fastutil.longs.LongSet chunkIndexes = cs.getChunkIndexes();
            if (chunkIndexes == null || chunkIndexes.isEmpty()) return;
            for (long chunkIdx : chunkIndexes) {
                Ref<ChunkStore> colRef = cs.getChunkReference(chunkIdx);
                if (colRef == null) continue;
                BlockComponentChunk bcc = store.getComponent(colRef, BlockComponentChunk.getComponentType());
                if (bcc == null) continue;
                for (Map.Entry<Integer, Ref<ChunkStore>> entry
                        : bcc.getEntityReferences().entrySet()) {
                    Ref<ChunkStore> blockRef = entry.getValue();
                    if (blockRef != null && blockRef.getIndex() == myIdx) {
                        int blockIndex = entry.getKey();
                        int lx = ChunkUtil.xFromBlockInColumn(blockIndex);
                        int wy = ChunkUtil.yFromBlockInColumn(blockIndex);
                        int lz = ChunkUtil.zFromBlockInColumn(blockIndex);
                        int wx = ChunkUtil.worldCoordFromLocalCoord(ChunkUtil.xOfChunkIndex(chunkIdx), lx);
                        int wz = ChunkUtil.worldCoordFromLocalCoord(ChunkUtil.zOfChunkIndex(chunkIdx), lz);
                        cachedPosition = new Vector3i(wx, wy, wz);
                        return;
                    }
                }
            }
        } catch (Throwable ignored) {}
    }

    private void fallbackHeartbeat() {
        try {
            if (this.es != null && !visualSpawnTimes.isEmpty()) {
                Instant now = this.es.getResource(WorldTimeResource.getResourceType()).getGameTime(); Iterator<Map.Entry<Ref<EntityStore>, Instant>> it2 = visualSpawnTimes.entrySet().iterator(); while (it2.hasNext()) { Map.Entry<Ref<EntityStore>, Instant> e = it2.next(); Ref<EntityStore> ref = e.getKey(); Instant spawnTime = e.getValue(); try { if (ref == null || !ref.isValid()) { it2.remove(); try { visualMap.remove(ref); } catch (Exception ignored) {} continue; } if (now.isAfter(spawnTime.plusSeconds(5))) { it2.remove(); try { visualMap.remove(ref); } catch (Exception ignored) {} try { l.remove(ref); } catch (Exception ignored) {} try { this.es.removeEntity(ref, RemoveReason.REMOVE); } catch (Exception ignored) {} } } catch (Exception ignored) {} }
            }
        } catch (Throwable ignored) {}
    }

    // Direct port of HopperProcessor.spawnVisualFor (last committed version)
    private void spawnVisualFor(ItemStack safeStack, boolean exportPhase, Vector3i pos, AdjacentSide side, Store<EntityStore> entities) {
        try { HytaleLogger.getLogger().atInfo().log("[Visual] spawnVisualFor called exportPhase=" + exportPhase + " nearbySize=" + nearbyBuffer.size() + " entities=" + (entities != null) + " stack=" + safeStack); } catch (Throwable ignored) {}
        if (safeStack == null || safeStack.isEmpty()) return;
        Vector3i rel = WorldHelper.rotate(side, this.getRotationIndex()).relativePosition;
        Vector3i hopperBlock = this.getBlockPosition();
        Vector3d hopperCenter = new Vector3d(hopperBlock.x + 0.5, hopperBlock.y + 0.5, hopperBlock.z + 0.5);
        Vector3d sourceCenter = new Vector3d(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
        Vector3d spawnPos = exportPhase ? hopperCenter : sourceCenter;
        Vector3d velocity = exportPhase ? new Vector3d(-rel.x * 0.35, 0.25, -rel.z * 0.35) : new Vector3d(rel.x * 0.35, 0.25, rel.z * 0.35);

        if (exportPhase) {
            List<Ref<EntityStore>> nearby = nearbyBuffer;
            if (!nearby.isEmpty()) {
                boolean anySpawned = false;
                for (Ref<EntityStore> targetRef : nearby) {
                    String oppSide;
                    switch (side.toString()) {
                        case "East"  -> oppSide = "West";
                        case "West"  -> oppSide = "East";
                        case "North" -> oppSide = "South";
                        case "South" -> oppSide = "North";
                        case "Up"    -> oppSide = "Down";
                        case "Down"  -> oppSide = "Up";
                        default      -> oppSide = side.toString();
                    }
                    Ref<EntityStore> rs;
                    try {
                        rs = ItemUtilsExtended.throwItem(this.getBlockType().getId(), oppSide, new Vector3d(hopperBlock.x, hopperBlock.y, hopperBlock.z), targetRef, (ComponentAccessor<EntityStore>) entities, safeStack, Vector3d.ZERO, 0f);
                    } catch (Exception e) { rs = null; }
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

        Holder<EntityStore> itemEntityHolder = ItemComponent.generateItemDrop((ComponentAccessor<EntityStore>) entities, safeStack, new Vector3d(spawnPos.x, spawnPos.y, spawnPos.z), Vector3f.ZERO, 0, -1, 0);
        if (itemEntityHolder == null) return;

        ItemComponent itemComponent = (ItemComponent) itemEntityHolder.getComponent(ItemComponent.getComponentType());
        if (itemComponent != null) {
            itemComponent.setPickupDelay(100000000);
            itemComponent.setRemovedByPlayerPickup(false);
            itemComponent.computeDynamicLight();
        }

        try { itemEntityHolder.removeComponent(PhysicsValues.getComponentType()); } catch (Exception ignored) {}
        try {
            ((Velocity) itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(velocity.x, velocity.y, velocity.z);
        } catch (Exception ignored) {}
        try { itemEntityHolder.tryRemoveComponent(BoundingBox.getComponentType()); } catch (Exception ignored) {}
        try { itemEntityHolder.ensureAndGetComponent(Intangible.getComponentType()); } catch (Exception ignored) {}

        Ref<EntityStore> spawned = entities.addEntity(itemEntityHolder, AddReason.SPAWN);
        try { HytaleLogger.getLogger().atInfo().log("[Visual] addEntity returned=" + spawned + " spawnPos=" + spawnPos); } catch (Throwable ignored) {}
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

    // Restore FluidComponent integration
    private ComponentType<EntityStore, FluidComponent> fluidComponent;

    public ComponentType<EntityStore, FluidComponent> getFluidComponent() {
        return fluidComponent;
    }

    public void setFluidComponent(ComponentType<EntityStore, FluidComponent> fluidComponent) {
        this.fluidComponent = fluidComponent;
    }

    // Restore codec registration
    public void registerCodecs() {
        Ev0Lib.getInstance().getCodecRegistry(Interaction.CODEC).register("HopperInteraction", HopperInteraction.class, HopperInteraction.CODEC);
    }

    // Restore ArcIO mechanism registration
    public void registerArcIOMechanism() {
        try {
            Class.forName("voidbond.arcio.ArcioPlugin");
            Class.forName("org.Ev0Mods.plugin.api.block.state.HopperArcioRegistration")
                    .getMethod("register")
                    .invoke(null);
            Ev0Log.info(LOGGER, "[HopperComponent] Registered ArcIO mechanism: Hopper");
        } catch (ClassNotFoundException ignored) {
            Ev0Log.info(LOGGER, "[HopperComponent] ArcIO not found - skipping mechanism registration");
        } catch (Exception e) {
            Ev0Log.warn(LOGGER, "[HopperComponent] Failed to register ArcIO mechanism: " + e.getMessage());
        }
    }

    // Integrate configuration
    public void logConfigValues() {
        Ev0Log.info(LOGGER, "Config values: tierMultiplier=" + Ev0Config.getTierMultiplier() + ", fluidTransferEnabled=" + Ev0Config.isFluidTransferEnabled());
    }
}
