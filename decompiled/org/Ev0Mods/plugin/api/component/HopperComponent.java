/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.builtin.blocktick.system.ChunkBlockTickSystem$Ticking
 *  com.hypixel.hytale.builtin.fluid.FluidSystems$Ticking
 *  com.hypixel.hytale.codec.Codec
 *  com.hypixel.hytale.codec.KeyedCodec
 *  com.hypixel.hytale.codec.builder.BuilderCodec
 *  com.hypixel.hytale.codec.builder.BuilderCodec$Builder
 *  com.hypixel.hytale.component.AddReason
 *  com.hypixel.hytale.component.ArchetypeChunk
 *  com.hypixel.hytale.component.CommandBuffer
 *  com.hypixel.hytale.component.Component
 *  com.hypixel.hytale.component.ComponentAccessor
 *  com.hypixel.hytale.component.ComponentType
 *  com.hypixel.hytale.component.Holder
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.RemoveReason
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.component.dependency.Dependency
 *  com.hypixel.hytale.component.dependency.Order
 *  com.hypixel.hytale.component.dependency.SystemDependency
 *  com.hypixel.hytale.component.spatial.SpatialResource
 *  com.hypixel.hytale.logger.HytaleLogger
 *  com.hypixel.hytale.logger.HytaleLogger$Api
 *  com.hypixel.hytale.math.util.ChunkUtil
 *  com.hypixel.hytale.math.vector.Vector3d
 *  com.hypixel.hytale.math.vector.Vector3f
 *  com.hypixel.hytale.math.vector.Vector3i
 *  com.hypixel.hytale.protocol.Rangef
 *  com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType
 *  com.hypixel.hytale.server.core.asset.type.fluid.Fluid
 *  com.hypixel.hytale.server.core.codec.ProtocolCodecs
 *  com.hypixel.hytale.server.core.entity.entities.BlockEntity
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.inventory.ItemStack
 *  com.hypixel.hytale.server.core.inventory.container.ItemContainer
 *  com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer
 *  com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction
 *  com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock
 *  com.hypixel.hytale.server.core.modules.entity.EntityModule
 *  com.hypixel.hytale.server.core.modules.entity.component.BoundingBox
 *  com.hypixel.hytale.server.core.modules.entity.component.Intangible
 *  com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
 *  com.hypixel.hytale.server.core.modules.entity.item.ItemComponent
 *  com.hypixel.hytale.server.core.modules.physics.component.PhysicsValues
 *  com.hypixel.hytale.server.core.modules.physics.component.Velocity
 *  com.hypixel.hytale.server.core.modules.time.WorldTimeResource
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  com.hypixel.hytale.server.core.universe.world.World
 *  com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk
 *  com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk
 *  com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockPatternRule$AdjacentSide
 *  com.hypixel.hytale.server.core.universe.world.storage.ChunkStore
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.checkerframework.checker.nullness.compatqual.NonNullDecl
 *  voidbond.arcio.ArcioPlugin
 *  voidbond.arcio.components.ArcioMechanismComponent
 *  voidbond.arcio.components.BlockUUIDComponent
 */
package org.Ev0Mods.plugin.api.component;

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
import com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.Intangible;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.modules.physics.component.PhysicsValues;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.state.TickableBlockState;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockPatternRule;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.lang.reflect.Field;
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
import org.Ev0Mods.plugin.api.component.EngineCompat;
import org.Ev0Mods.plugin.api.ui.HopperUIPage;
import org.Ev0Mods.plugin.api.util.ItemUtilsExtended;
import org.Ev0Mods.plugin.api.util.WorldHelper;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import voidbond.arcio.ArcioPlugin;
import voidbond.arcio.components.ArcioMechanismComponent;
import voidbond.arcio.components.BlockUUIDComponent;

public class HopperComponent
implements Component<ChunkStore>,
TickableBlockState {
    private static final boolean PERF_DEBUG = false;
    public int fluid_id = 0;
    public Rangef duration = new Rangef(0.0f, 10.0f);
    public float tier;
    public HopperProcessor.Data data;
    protected Instant startTime;
    private double timerV = 0.0;
    private double timer = 0.0;
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
    public Deque<Ref<EntityStore>> l = new ArrayDeque<Ref<EntityStore>>();
    public Map<Ref<EntityStore>, ItemStack> visualMap = new ConcurrentHashMap<Ref<EntityStore>, ItemStack>();
    public Map<Ref<EntityStore>, Instant> visualSpawnTimes = new ConcurrentHashMap<Ref<EntityStore>, Instant>();
    private Fluid f;
    private int tickCounter = 0;
    private List<Ref<EntityStore>> nearbyBuffer = new ArrayList<Ref<EntityStore>>();
    private static final ConcurrentHashMap<Class<?>, Method> ITEM_KEY_METHOD_CACHE = new ConcurrentHashMap();
    private static final ConcurrentHashMap<Class<?>, Method> GET_ITEM_CONTAINER_METHOD_CACHE = new ConcurrentHashMap();
    private static final ConcurrentHashMap<Class<?>, Method> GET_CONTAINER_FROM_ITEM_CONTAINER_METHOD_CACHE = new ConcurrentHashMap();
    private static final ConcurrentHashMap<String, Method> REFLECTION_METHOD_CACHE = new ConcurrentHashMap();
    private boolean playersNearbyCached = false;
    private final List<long[]> pendingFluidRemovals = new ArrayList<long[]>();
    private volatile long lastEngineTick = System.currentTimeMillis();
    private volatile boolean invalidatedFlag = false;
    private static final ScheduledExecutorService FALLBACK_SCHEDULER = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "ev0-hopper-fallback");
        t.setDaemon(true);
        return t;
    });
    private static final ConcurrentHashMap<HopperComponent, Boolean> REGISTERED_COMPONENTS = new ConcurrentHashMap();
    private transient ItemContainerBlock itemContainerBlock;
    private transient ItemContainer itemContainer;
    private Vector3i cachedPosition = new Vector3i(0, 0, 0);
    public static final BuilderCodec<HopperComponent> CODEC;
    public static final boolean ARCIO_PRESENT;
    public static final boolean SIMPLE_DRAWERS_PRESENT;
    private boolean arcioInitialized = false;
    private String arcioMode = "IgnoreSignal";
    private final List<String> whitelist = Collections.synchronizedList(new ArrayList());
    private final List<String> blacklist = Collections.synchronizedList(new ArrayList());
    private volatile String filterMode = "Off";
    private final Map<PlayerRef, String> typedBuffer = new ConcurrentHashMap<PlayerRef, String>();
    @Nonnull
    private static final Set<Dependency<ChunkStore>> DEPENDENCIES;

    private static void perfInfo(String msg) {
    }

    public ItemContainer getItemContainer() {
        return this.itemContainer != null ? this.itemContainer : null;
    }

    private Object getItemContainerFromState(Object stateObj) {
        if (stateObj == null) {
            return null;
        }
        Class<?> cls = stateObj.getClass();
        if (GET_ITEM_CONTAINER_METHOD_CACHE.containsKey(cls)) {
            Method cached = GET_ITEM_CONTAINER_METHOD_CACHE.get(cls);
            if (cached == null) {
                return null;
            }
            try {
                return cached.invoke(stateObj, new Object[0]);
            }
            catch (Throwable ignored) {
                return null;
            }
        }
        Method found = null;
        try {
            found = cls.getMethod("getItemContainer", new Class[0]);
        }
        catch (Throwable ignored) {
            // empty catch block
        }
        if (found == null) {
            try {
                found = cls.getMethod("itemContainer", new Class[0]);
            }
            catch (Throwable ignored) {
                // empty catch block
            }
        }
        GET_ITEM_CONTAINER_METHOD_CACHE.put(cls, found);
        if (found == null) {
            return null;
        }
        try {
            return found.invoke(stateObj, new Object[0]);
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private ItemContainer getContainerFromItemContainerObject(Object itemContainerObj, int idx) {
        Object r;
        if (itemContainerObj == null) {
            return null;
        }
        if (itemContainerObj instanceof ItemContainer) {
            return (ItemContainer)itemContainerObj;
        }
        Class<?> cls = itemContainerObj.getClass();
        if (GET_CONTAINER_FROM_ITEM_CONTAINER_METHOD_CACHE.containsKey(cls)) {
            Method cached = GET_CONTAINER_FROM_ITEM_CONTAINER_METHOD_CACHE.get(cls);
            if (cached == null) {
                return null;
            }
            try {
                r = cached.invoke(itemContainerObj, idx);
                if (r instanceof ItemContainer) {
                    return (ItemContainer)r;
                }
            }
            catch (Throwable ignored) {
                return null;
            }
        }
        Method found = null;
        try {
            found = cls.getMethod("getContainer", Integer.TYPE);
        }
        catch (Throwable ignored) {
            // empty catch block
        }
        if (found == null) {
            try {
                found = cls.getMethod("container", Integer.TYPE);
            }
            catch (Throwable ignored) {
                // empty catch block
            }
        }
        GET_CONTAINER_FROM_ITEM_CONTAINER_METHOD_CACHE.put(cls, found);
        if (found == null) {
            return null;
        }
        try {
            r = found.invoke(itemContainerObj, idx);
            if (r instanceof ItemContainer) {
                return (ItemContainer)r;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    public Vector3i getBlockPosition() {
        return this.cachedPosition;
    }

    public Vector3i probeAndGetBlockPosition() {
        block5: {
            try {
                Class<?> sc = this.getClass().getSuperclass();
                if (sc == null) break block5;
                for (String name : new String[]{"getBlockPosition", "getPosition", "getPos", "position"}) {
                    try {
                        Object r;
                        Method m = sc.getMethod(name, new Class[0]);
                        if (m == null || !((r = m.invoke((Object)this, new Object[0])) instanceof Vector3i)) continue;
                        this.cachedPosition = (Vector3i)r;
                        return this.cachedPosition;
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return this.cachedPosition;
    }

    @Override
    public Vector3i getPosition() {
        return this.cachedPosition;
    }

    @Override
    @Nullable
    public WorldChunk getChunk() {
        try {
            if (this.w != null) {
                return this.w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock((int)this.cachedPosition.x, (int)this.cachedPosition.z));
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    @Override
    public void invalidate() {
        this.is_valid = false;
        this.invalidatedFlag = true;
    }

    public int getRotationIndex() {
        return 0;
    }

    public BlockType getBlockType() {
        return BlockType.EMPTY;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<String> getWhitelist() {
        List<String> list = this.whitelist;
        synchronized (list) {
            return new ArrayList<String>(this.whitelist);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<String> getBlacklist() {
        List<String> list = this.blacklist;
        synchronized (list) {
            return new ArrayList<String>(this.blacklist);
        }
    }

    public String getFilterMode() {
        return this.filterMode;
    }

    public void addToWhitelist(String id) {
        if (id != null) {
            this.whitelist.add(id);
        }
    }

    public void addToBlacklist(String id) {
        if (id != null) {
            this.blacklist.add(id);
        }
    }

    public void setFilterMode(String mode) {
        if (mode != null) {
            this.filterMode = mode;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String removeLastFromWhitelist() {
        List<String> list = this.whitelist;
        synchronized (list) {
            if (this.whitelist.isEmpty()) {
                return null;
            }
            return this.whitelist.remove(this.whitelist.size() - 1);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String removeLastFromBlacklist() {
        List<String> list = this.blacklist;
        synchronized (list) {
            if (this.blacklist.isEmpty()) {
                return null;
            }
            return this.blacklist.remove(this.blacklist.size() - 1);
        }
    }

    public void clearWhitelist() {
        this.whitelist.clear();
    }

    public void clearBlacklist() {
        this.blacklist.clear();
    }

    public String getArcioMode() {
        return this.arcioMode;
    }

    public void setArcioMode(String mode) {
        if (mode != null) {
            this.arcioMode = mode;
        }
    }

    public void setTypedBuffer(PlayerRef p, String v) {
        if (p == null) {
            return;
        }
        if (v == null) {
            this.typedBuffer.remove(p);
        } else {
            this.typedBuffer.put(p, v);
        }
    }

    public String getTypedBuffer(PlayerRef p) {
        if (p == null) {
            return null;
        }
        return this.typedBuffer.get(p);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean isItemAllowedByFilter(String blockKey) {
        if (this.filterMode == null || this.filterMode.equalsIgnoreCase("Off")) {
            return true;
        }
        if (this.filterMode.equalsIgnoreCase("Whitelist")) {
            List<String> list = this.whitelist;
            synchronized (list) {
                if (this.whitelist == null || this.whitelist.isEmpty()) {
                    return false;
                }
                if (blockKey == null) {
                    return false;
                }
                for (String s : this.whitelist) {
                    if (s == null || !s.equalsIgnoreCase(blockKey)) continue;
                    return true;
                }
                return false;
            }
        }
        if (this.filterMode.equalsIgnoreCase("Blacklist")) {
            List<String> list = this.blacklist;
            synchronized (list) {
                if (this.blacklist == null || this.blacklist.isEmpty()) {
                    return true;
                }
                if (blockKey == null) {
                    return true;
                }
                for (String s : this.blacklist) {
                    if (s == null || !s.equalsIgnoreCase(blockKey)) continue;
                    return false;
                }
                return true;
            }
        }
        return true;
    }

    public String resolveItemStackKey(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        try {
            Object probe = null;
            try {
                probe = stack.getBlockKey();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (probe == null) {
                Class<?> cls = stack.getClass();
                Method m = ITEM_KEY_METHOD_CACHE.get(cls);
                if (m == null && !ITEM_KEY_METHOD_CACHE.containsKey(cls)) {
                    String[] candidates;
                    Method found = null;
                    for (String name : candidates = new String[]{"getItemId", "getItemKey", "getId", "getKey", "getName", "getBlockKey"}) {
                        try {
                            found = cls.getMethod(name, new Class[0]);
                            if (found == null) continue;
                            break;
                        }
                        catch (Throwable throwable) {
                            // empty catch block
                        }
                    }
                    ITEM_KEY_METHOD_CACHE.put(cls, found);
                    m = found;
                }
                if (m != null) {
                    try {
                        Object v = m.invoke((Object)stack, new Object[0]);
                        if (v != null) {
                            probe = v;
                        }
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                }
            }
            if (probe == null) {
                probe = stack.toString();
            }
            return String.valueOf(probe);
        }
        catch (Throwable t) {
            try {
                return String.valueOf(stack.toString());
            }
            catch (Throwable ignored) {
                return null;
            }
        }
    }

    public HopperComponent() {
        this.ic = new Ref[0];
    }

    public HopperComponent(HopperComponent other) {
        this.ic = new Ref[0];
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
        this.l = new ArrayDeque<Ref<EntityStore>>(other.l);
        this.visualMap = new ConcurrentHashMap<Ref<EntityStore>, ItemStack>(other.visualMap);
        this.visualSpawnTimes = new ConcurrentHashMap<Ref<EntityStore>, Instant>(other.visualSpawnTimes);
        this.f = other.f;
        this.tickCounter = other.tickCounter;
        this.nearbyBuffer = new ArrayList<Ref<EntityStore>>(other.nearbyBuffer);
        this.cachedPosition = other.cachedPosition;
        this.arcioInitialized = other.arcioInitialized;
        this.arcioMode = other.arcioMode;
        this.whitelist.addAll(other.whitelist);
        this.blacklist.addAll(other.blacklist);
        this.filterMode = other.filterMode;
        this.typedBuffer.putAll(other.typedBuffer);
    }

    public void setOwnerId(Player ownerId) {
        this.ownerId = ownerId;
    }

    public Player getOwnerId() {
        return this.ownerId;
    }

    public void onOpen(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl World world, @NonNullDecl Store<EntityStore> store) {
        if (ARCIO_PRESENT && !this.arcioInitialized) {
            this.ensureArcioComponents(world, null);
        }
        this.rf = (PlayerRef)store.getComponent(ref, PlayerRef.getComponentType());
        try {
            if (this.rf == null) {
                return;
            }
            Vector3i pos = this.getBlockPosition();
            HopperUIPage.open(this.rf, store, pos, null);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    @Nullable
    public Component<ChunkStore> clone() {
        return new HopperComponent(this);
    }

    public boolean canOpen(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl ComponentAccessor<EntityStore> componentAccessor) {
        try {
            return true;
        }
        catch (Throwable ignored) {
            return true;
        }
    }

    public void onDestroy() {
        for (int b = 0; b < this.l.size() - 1; ++b) {
            try {
                ItemContainer ic = this.getItemContainer();
                if (ic != null) {
                    ic.dropAllItemStacks();
                }
            }
            catch (Throwable ic) {
                // empty catch block
            }
            if (this.l.isEmpty() || this.l.size() <= b) continue;
            Ref<EntityStore> esx = this.l.removeFirst();
            try {
                this.visualMap.remove(esx);
                this.visualSpawnTimes.remove(esx);
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (esx == null || !esx.isValid()) continue;
            try {
                this.es.removeEntity(esx, RemoveReason.REMOVE);
                continue;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    @Override
    public void tick(float dt, int index, ArchetypeChunk<ChunkStore> archeChunk, Store<ChunkStore> store, CommandBuffer<ChunkStore> commandBuffer) {
        block88: {
            boolean doImport;
            block87: {
                Vector3i probed2;
                this.lastEngineTick = System.currentTimeMillis();
                REGISTERED_COMPONENTS.put(this, Boolean.TRUE);
                if (this.data == null) {
                    this.data = new HopperProcessor.Data();
                }
                Ref myRef = archeChunk.getReferenceTo(index);
                try {
                    this.w = ((ChunkStore)store.getExternalData()).getWorld();
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                try {
                    this.es = this.w != null ? this.w.getEntityStore().getStore() : null;
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                try {
                    probed2 = this.probeAndGetBlockPosition();
                    if (probed2 == null || probed2.x == 0 && probed2.y == 0 && probed2.z == 0) {
                        this.resolvePosition(store, (Ref<ChunkStore>)myRef);
                    }
                }
                catch (Throwable probed2) {
                    // empty catch block
                }
                try {
                    Ev0Log.info(HytaleLogger.getLogger(), "[Ev0Lib] HopperComponent.tick invoked for index=" + index + " pos=" + String.valueOf(this.cachedPosition));
                }
                catch (Throwable probed2) {
                    // empty catch block
                }
                try {
                    block86: {
                        try {
                            Method getComponentMethod = store.getClass().getMethod("getComponent", Ref.class, Class.forName("com.hypixel.hytale.component.ComponentType"));
                            Class<?> icbClass = Class.forName("com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock");
                            Method icbGetComponentType = icbClass.getMethod("getComponentType", new Class[0]);
                            Object icbComponentType = icbGetComponentType.invoke(null, new Object[0]);
                            Object icbObj = null;
                            try {
                                icbObj = getComponentMethod.invoke(store, myRef, icbComponentType);
                            }
                            catch (Throwable ignored) {
                                icbObj = null;
                            }
                            if (icbObj == null || !icbClass.isInstance(icbObj)) break block86;
                            this.itemContainerBlock = (ItemContainerBlock)icbObj;
                            try {
                                Method getIC = this.itemContainerBlock.getClass().getMethod("getItemContainer", new Class[0]);
                                Object cont = getIC.invoke((Object)this.itemContainerBlock, new Object[0]);
                                if (cont instanceof ItemContainer) {
                                    this.itemContainer = (ItemContainer)cont;
                                }
                            }
                            catch (Throwable ignored) {
                                this.itemContainer = null;
                            }
                        }
                        catch (Throwable ignored) {
                            this.itemContainerBlock = null;
                        }
                    }
                    if (this.itemContainer != null) break block87;
                    probed2 = this.probeAndGetBlockPosition();
                    if ((probed2 == null || probed2.x == 0 && probed2.y == 0 && probed2.z == 0) && this.cachedPosition.x == 0 && this.cachedPosition.y == 0 && this.cachedPosition.z == 0) {
                        this.resolvePosition(store, (Ref<ChunkStore>)myRef);
                    }
                    WorldChunk myChunk = null;
                    try {
                        if (this.w != null) {
                            myChunk = this.w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock((int)this.cachedPosition.x, (int)this.cachedPosition.z));
                        }
                    }
                    catch (Throwable icbGetComponentType) {
                        // empty catch block
                    }
                    Object state = null;
                    try {
                        if (myChunk != null) {
                            state = EngineCompat.getState(myChunk, this.cachedPosition.x, this.cachedPosition.y, this.cachedPosition.z);
                        }
                    }
                    catch (Throwable icbComponentType) {
                        // empty catch block
                    }
                    try {
                        Object contObj = this.getItemContainerFromState(state);
                        if (contObj instanceof ItemContainer) {
                            this.itemContainer = (ItemContainer)contObj;
                            break block87;
                        }
                        this.itemContainer = this.getContainerFromItemContainerObject(contObj, 0);
                    }
                    catch (Throwable ignored) {
                        this.itemContainer = null;
                    }
                }
                catch (Throwable probed3) {
                    // empty catch block
                }
            }
            if (this.itemContainer == null) {
                try {
                    this.itemContainer = new SimpleItemContainer(1);
                    try {
                        ((HytaleLogger.Api)HytaleLogger.getLogger().atInfo()).log("[HopperDiag] created fallback SimpleItemContainer for hopper at pos=" + String.valueOf(this.cachedPosition));
                    }
                    catch (Throwable probed3) {}
                }
                catch (Throwable ignored) {
                    this.itemContainer = null;
                }
            }
            try {
                ((HytaleLogger.Api)HytaleLogger.getLogger().atInfo()).log("[HopperDiag] itemContainerBlock=" + (this.itemContainerBlock != null) + " itemContainerPresent=" + (this.getItemContainer() != null));
            }
            catch (Throwable ignored) {
                // empty catch block
            }
            if (!this.pendingFluidRemovals.isEmpty()) {
                for (long[] coords : this.pendingFluidRemovals) {
                    try {
                        WorldChunk fc = this.w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock((int)((int)coords[0]), (int)((int)coords[2])));
                        if (fc == null) continue;
                        fc.setBlock((int)coords[0], (int)coords[1], (int)coords[2], BlockType.EMPTY);
                    }
                    catch (Exception fc) {}
                }
                this.pendingFluidRemovals.clear();
            }
            if (ARCIO_PRESENT && !this.arcioInitialized) {
                this.ensureArcioComponents(this.w, commandBuffer);
            }
            if (ARCIO_PRESENT && "EnableWhenSignal".equals(this.arcioMode) && !this.isArcioActive(this.w)) {
                return;
            }
            this.timerV += 1.0;
            boolean bl = this.drop = this.timerV >= (double)this.duration.max;
            if (this.drop) {
                this.timerV = 0.0;
            }
            ++this.tickCounter;
            int phase = this.tickCounter % 180;
            boolean doExport = phase == 0;
            boolean bl2 = doImport = phase == 90;
            if (this.tickCounter % 180 == 0) {
                try {
                    List rawPlayers = SpatialResource.getThreadLocalReferenceList();
                    Vector3d center = new Vector3d((double)this.cachedPosition.x, (double)this.cachedPosition.y, (double)this.cachedPosition.z);
                    ((SpatialResource)this.es.getResource(EntityModule.get().getPlayerSpatialResourceType())).getSpatialStructure().collectCylinder(center, 4.0, (double)Math.max(1.0f, this.data.height), rawPlayers);
                    this.playersNearbyCached = !rawPlayers.isEmpty();
                    rawPlayers.clear();
                }
                catch (Exception rawPlayers) {
                    // empty catch block
                }
            }
            try {
                ((HytaleLogger.Api)HytaleLogger.getLogger().atInfo()).log("[HopperDiag] tickCounter=" + this.tickCounter + " phase=" + phase + " doExport=" + doExport + " doImport=" + doImport + " slot0=" + (this.getItemContainer() == null ? "null" : String.valueOf(this.getItemContainer().getItemStack((short)0))));
            }
            catch (Throwable rawPlayers) {
                // empty catch block
            }
            if (doExport) {
                if (this.getItemContainer() != null && this.getItemContainer().getItemStack((short)0) != null && this.playersNearbyCached) {
                    this.nearbyBuffer = HopperComponent.getAllEntitiesInBox(this, this.cachedPosition, this.data.height, this.es, this.data.players, this.data.entities, this.data.items);
                } else {
                    this.nearbyBuffer.clear();
                }
                this.runExportPhase(this.cachedPosition, this.es);
            }
            if (doImport) {
                boolean hopperHasSpace;
                ItemStack have2 = this.getItemContainer().getItemStack((short)0);
                boolean bl3 = hopperHasSpace = have2 == null || have2.getQuantity() < 100;
                if (hopperHasSpace && this.playersNearbyCached) {
                    this.nearbyBuffer = HopperComponent.getAllEntitiesInBox(this, this.cachedPosition, this.data.height, this.es, this.data.players, this.data.entities, this.data.items);
                } else {
                    this.nearbyBuffer.clear();
                }
                for (ConnectedBlockPatternRule.AdjacentSide side : this.data.importFaces) {
                    Vector3i importPos = new Vector3i(this.cachedPosition.x, this.cachedPosition.y, this.cachedPosition.z).add(WorldHelper.rotate((ConnectedBlockPatternRule.AdjacentSide)side, (int)this.getRotationIndex()).relativePosition);
                    WorldChunk chunk = this.w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock((int)importPos.x, (int)importPos.z));
                    if (chunk == null) continue;
                    int targetFluidId = EngineCompat.getFluidId(chunk, importPos.x, importPos.y, importPos.z);
                    Object state = EngineCompat.getState(chunk, importPos.x, importPos.y, importPos.z);
                    boolean hasContainer = state != null && (state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState") || state.getClass().getSimpleName().contains("ItemContainer"));
                    ItemStack currentItem = this.getItemContainer().getItemStack((short)0);
                    if (Ev0Config.isFluidTransferEnabled() && targetFluidId != 0 && currentItem == null && !hasContainer) {
                        ItemStack bucketStack = null;
                        switch (targetFluidId) {
                            case 2: {
                                bucketStack = new ItemStack("*Container_Bucket_State_Filled_Red_Slime", 1, null);
                                break;
                            }
                            case 3: {
                                bucketStack = new ItemStack("*Container_Bucket_State_Filled_Tar", 1, null);
                                break;
                            }
                            case 4: {
                                bucketStack = new ItemStack("*Container_Bucket_State_Filled_Poison", 1, null);
                                break;
                            }
                            case 5: {
                                bucketStack = new ItemStack("*Container_Bucket_State_Filled_Green_Slime", 1, null);
                                break;
                            }
                            case 6: {
                                bucketStack = new ItemStack("*Container_Bucket_State_Filled_Lava", 1, null);
                                break;
                            }
                            case 7: {
                                bucketStack = new ItemStack("*Container_Bucket_State_Filled_Water", 1, null);
                                break;
                            }
                            default: {
                                bucketStack = null;
                            }
                        }
                        if (bucketStack != null) {
                            this.getItemContainer().addItemStackToSlot((short)0, bucketStack);
                            this.pendingFluidRemovals.add(new long[]{importPos.x, importPos.y, importPos.z});
                            continue;
                        }
                    }
                    try {
                        Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] import from pos=" + String.valueOf(importPos) + " state=" + (state == null ? "null" : state.getClass().getSimpleName()) + " hasContainer=" + hasContainer);
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    HopperComponent.perfInfo("[Hopper][Import] side=" + String.valueOf(side) + " importPos=" + String.valueOf(importPos) + " state=" + (state == null ? "null" : state.getClass().getSimpleName()) + " hasContainer=" + hasContainer);
                    if (this.tryImportFromContainer(chunk, importPos, this.es, side)) {
                        HopperComponent.perfInfo("[Hopper][Import] tryImportFromContainer SUCCESS side=" + String.valueOf(side));
                        break;
                    }
                    HopperComponent.perfInfo("[Hopper][Import] tryImportFromContainer failed, hasContainer=" + hasContainer + " -> will tryPickup=" + !hasContainer);
                    if (hasContainer || !this.tryPickupItemEntities(importPos, this.es)) continue;
                    HopperComponent.perfInfo("[Hopper][Import] tryPickupItemEntities SUCCESS at " + String.valueOf(importPos));
                    this.runExportPhase(this.cachedPosition, this.es);
                    break;
                }
            }
            if (!this.l.isEmpty()) {
                Iterator<Ref<EntityStore>> it = this.l.iterator();
                while (it.hasNext()) {
                    Ref<EntityStore> esx = it.next();
                    if (esx != null && esx.isValid()) continue;
                    it.remove();
                    try {
                        this.visualMap.remove(esx);
                        this.visualSpawnTimes.remove(esx);
                    }
                    catch (Exception ignored) {}
                }
            }
            try {
                if (this.es == null || this.visualSpawnTimes.isEmpty()) break block88;
                Instant now = ((WorldTimeResource)this.es.getResource(WorldTimeResource.getResourceType())).getGameTime();
                Iterator<Map.Entry<Ref<EntityStore>, Instant>> it2 = this.visualSpawnTimes.entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry<Ref<EntityStore>, Instant> e = it2.next();
                    Ref<EntityStore> entryRef = e.getKey();
                    Instant spawnTime = e.getValue();
                    try {
                        if (entryRef == null || !entryRef.isValid()) {
                            it2.remove();
                            try {
                                this.visualMap.remove(entryRef);
                            }
                            catch (Exception exception) {}
                            continue;
                        }
                        if (!now.isAfter(spawnTime.plusSeconds(5L))) continue;
                        it2.remove();
                        try {
                            this.visualMap.remove(entryRef);
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                        try {
                            this.l.remove(entryRef);
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                        try {
                            this.es.removeEntity(entryRef, RemoveReason.REMOVE);
                        }
                        catch (Exception exception) {
                        }
                    }
                    catch (Exception exception) {}
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private void runExportPhase(Vector3i pos, Store<EntityStore> entities) {
        try {
            ((HytaleLogger.Api)HytaleLogger.getLogger().atInfo()).log("[HopperDiag] runExportPhase invoked; param pos=" + String.valueOf(pos) + " cachedPos=" + String.valueOf(this.getBlockPosition()) + " rotationIndex=" + this.getRotationIndex() + " slot0=" + (this.getItemContainer() == null ? "null" : String.valueOf(this.getItemContainer().getItemStack((short)0))));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        ItemStack currentItemFast = this.getItemContainer().getItemStack((short)0);
        if (currentItemFast == null) {
            return;
        }
        for (ConnectedBlockPatternRule.AdjacentSide side : this.data.exportFaces) {
            boolean transferred;
            String itemKey;
            Vector3i hopperPos = this.getBlockPosition();
            ConnectedBlockPatternRule.AdjacentSide rotated = WorldHelper.rotate(side, this.getRotationIndex());
            Vector3i rel = rotated.relativePosition;
            Vector3i exportPos = new Vector3i(hopperPos.x, hopperPos.y, hopperPos.z).add(rel);
            try {
                ((HytaleLogger.Api)HytaleLogger.getLogger().atInfo()).log("[HopperDiag] export probe side=" + String.valueOf(side) + " rotated=" + String.valueOf(rotated) + " rel=" + String.valueOf(rel) + " hopperPos=" + String.valueOf(hopperPos) + " exportPos=" + String.valueOf(exportPos) + " rotationIndex=" + this.getRotationIndex());
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            WorldChunk chunk = this.w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock((int)exportPos.x, (int)exportPos.z));
            if (chunk == null) continue;
            Object state = EngineCompat.getState(chunk, exportPos.x, exportPos.y, exportPos.z);
            int targetFluidId = EngineCompat.getFluidId(chunk, exportPos.x, exportPos.y, exportPos.z);
            boolean hasContainer = state != null && (state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState") || state.getClass().getSimpleName().contains("ItemContainer"));
            try {
                ((HytaleLogger.Api)HytaleLogger.getLogger().atInfo()).log("[HopperDiag] export side=" + String.valueOf(side) + " exportPos=" + String.valueOf(exportPos) + " state=" + (state == null ? "null" : state.getClass().getSimpleName()) + " hasContainer=" + hasContainer);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            ItemStack currentItem = this.getItemContainer().getItemStack((short)0);
            if (Ev0Config.isFluidTransferEnabled() && currentItem != null && !hasContainer && targetFluidId != 0 && (itemKey = currentItem.getBlockKey()) != null && itemKey.contains("Bucket") && !itemKey.contains("Empty")) {
                int fluidToPlace = 0;
                if (itemKey.contains("Water")) {
                    fluidToPlace = 7;
                } else if (itemKey.contains("Lava")) {
                    fluidToPlace = 6;
                } else if (itemKey.contains("Green_Slime")) {
                    fluidToPlace = 5;
                } else if (itemKey.contains("Poison")) {
                    fluidToPlace = 4;
                } else if (itemKey.contains("Tar")) {
                    fluidToPlace = 3;
                } else if (itemKey.contains("Red_Slime")) {
                    fluidToPlace = 2;
                }
                if (fluidToPlace != 0) {
                    this.getItemContainer().removeItemStackFromSlot((short)0, 1);
                    this.getItemContainer().addItemStackToSlot((short)0, new ItemStack("Container_Bucket", 1, null));
                    continue;
                }
            }
            if (!(transferred = this.tryTransferToOrFromContainer(state, exportPos, side, entities, true)) && currentItem != null && !hasContainer && targetFluidId == 0 && EngineCompat.getBlockType(chunk, exportPos.x, exportPos.y, exportPos.z) == null) {
                EngineCompat.setBlock(chunk, exportPos.x, exportPos.y, exportPos.z, currentItem.getBlockKey());
                this.getItemContainer().removeItemStackFromSlot((short)0, 1);
            }
            if (this.data.exportOnce && transferred) break;
        }
    }

    private boolean tryTransferToOrFromContainer(Object state, Vector3i pos, ConnectedBlockPatternRule.AdjacentSide side, Store<EntityStore> entities, boolean exportPhase) {
        ItemContainer sourceContainer;
        block55: {
            ItemStackSlotTransaction t;
            ItemStack safeStack;
            Object bench;
            block54: {
                if (state == null) {
                    try {
                        Ref blockRef;
                        BlockComponentChunk bcc;
                        if (this.w == null) break block54;
                        Store cs = this.w.getChunkStore().getStore();
                        Ref chunkRef = this.w.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock((int)pos.x, (int)pos.z));
                        if (chunkRef == null || (bcc = (BlockComponentChunk)cs.getComponent(chunkRef, BlockComponentChunk.getComponentType())) == null || (blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn((int)pos.x, (int)pos.y, (int)pos.z))) == null) break block54;
                        try {
                            Method getComp = cs.getClass().getMethod("getComponent", Ref.class, Class.forName("com.hypixel.hytale.component.ComponentType"));
                            Class<?> icbCls = Class.forName("com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock");
                            Method getCompType = icbCls.getMethod("getComponentType", new Class[0]);
                            Object compType = getCompType.invoke(null, new Object[0]);
                            Object icbObj = null;
                            try {
                                icbObj = getComp.invoke((Object)cs, blockRef, compType);
                            }
                            catch (Throwable ignored) {
                                icbObj = null;
                            }
                            if (icbObj == null || !icbCls.isInstance(icbObj)) break block54;
                            try {
                                Method getIC = icbCls.getMethod("getItemContainer", new Class[0]);
                                Object cont = getIC.invoke(icbObj, new Object[0]);
                                if (!(cont instanceof ItemContainer)) break block54;
                                ItemContainer target = (ItemContainer)cont;
                                ItemStack hopperStack = this.getItemContainer().getItemStack((short)0);
                                if (hopperStack == null) {
                                    return false;
                                }
                                for (int slot = 0; slot < target.getCapacity(); ++slot) {
                                    ItemStackSlotTransaction t2;
                                    if (!this.isItemAllowedByFilter(hopperStack.getBlockKey())) {
                                        return false;
                                    }
                                    int transferAmount = (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)hopperStack.getQuantity());
                                    if (transferAmount <= 0 || (t2 = target.addItemStackToSlot((short)slot, hopperStack.withQuantity(transferAmount))) == null || !t2.succeeded()) continue;
                                    try {
                                        this.spawnVisualFor(hopperStack.withQuantity(transferAmount), true, pos, side, entities);
                                    }
                                    catch (Throwable throwable) {
                                        // empty catch block
                                    }
                                    this.getItemContainer().removeItemStackFromSlot((short)0, transferAmount);
                                    return true;
                                }
                            }
                            catch (Throwable getIC) {
                            }
                        }
                        catch (Throwable getComp) {}
                    }
                    catch (Throwable cs) {
                        // empty catch block
                    }
                }
            }
            if (state == null) {
                return false;
            }
            if (!(state != null && state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState") || state.getClass().getSimpleName().contains("ItemContainer"))) {
                return false;
            }
            boolean isProcessingBench = state != null && state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState");
            Object object = bench = isProcessingBench ? state : null;
            if (isProcessingBench) {
                ItemContainer output = this.getContainerFromItemContainerObject(this.getItemContainerFromState(bench), 2);
                if (!exportPhase) {
                    for (int slot = 0; slot < output.getCapacity(); ++slot) {
                        ItemStackSlotTransaction t3;
                        int transferAmount;
                        ItemStack stack = output.getItemStack((short)slot);
                        if (stack == null) continue;
                        String probeKeyPb = null;
                        try {
                            probeKeyPb = stack.getBlockKey();
                        }
                        catch (Throwable getCompType) {
                            // empty catch block
                        }
                        if (probeKeyPb == null) {
                            probeKeyPb = this.resolveItemStackKey(stack);
                        }
                        if (!this.isItemAllowedByFilter(probeKeyPb) || (transferAmount = (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)stack.getQuantity())) <= 0 || !(t3 = this.getItemContainer().addItemStackToSlot((short)0, stack.withQuantity(transferAmount))).succeeded()) continue;
                        this.getItemContainer().removeItemStackFromSlot((short)0, transferAmount);
                        try {
                            this.spawnVisualFor(stack.withQuantity(transferAmount), false, pos, side, entities);
                        }
                        catch (Throwable icbObj) {
                            // empty catch block
                        }
                        return true;
                    }
                }
                Object containerStateObj = null;
                try {
                    Method m = state.getClass().getMethod("getItemContainer", new Class[0]);
                    containerStateObj = m.invoke(state, new Object[0]);
                }
                catch (Throwable m) {
                    // empty catch block
                }
                if (containerStateObj == null) {
                    return false;
                }
                ItemContainer target = (ItemContainer)containerStateObj;
                for (int slot = 0; slot < target.getCapacity(); ++slot) {
                    if (!this.isItemAllowedByFilter(this.getItemContainer().getItemStack((short)0) == null ? null : this.getItemContainer().getItemStack((short)0).getBlockKey())) {
                        return false;
                    }
                    int transferAmount = (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)this.getItemContainer().getItemStack((short)0).getQuantity());
                    safeStack = this.getItemContainer().getItemStack((short)0).withQuantity(transferAmount);
                    t = target.addItemStackToSlot((short)slot, safeStack);
                    if (!t.succeeded()) continue;
                    try {
                        this.spawnVisualFor(safeStack, true, pos, side, entities);
                    }
                    catch (Throwable getIC) {
                        // empty catch block
                    }
                    this.getItemContainer().removeItemStackFromSlot((short)0, transferAmount);
                    return true;
                }
                return false;
            }
            Object containerStateObj = null;
            try {
                Method m = state.getClass().getMethod("getItemContainer", new Class[0]);
                containerStateObj = m.invoke(state, new Object[0]);
            }
            catch (Throwable m) {
                // empty catch block
            }
            if (containerStateObj == null) {
                return false;
            }
            sourceContainer = (ItemContainer)containerStateObj;
            if (exportPhase) {
                try {
                    ItemStack hopperStack = this.getItemContainer().getItemStack((short)0);
                    if (hopperStack == null) break block55;
                    for (int slot = 0; slot < sourceContainer.getCapacity(); ++slot) {
                        try {
                            int transferAmount;
                            if (!this.isItemAllowedByFilter(hopperStack.getBlockKey()) || (transferAmount = (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)hopperStack.getQuantity())) <= 0) break;
                            safeStack = hopperStack.withQuantity(transferAmount);
                            t = sourceContainer.addItemStackToSlot((short)slot, safeStack);
                            if (t == null || !t.succeeded()) continue;
                            boolean verified = false;
                            try {
                                ItemStack after = sourceContainer.getItemStack((short)slot);
                                if (after != null && after.getQuantity() >= transferAmount) {
                                    verified = true;
                                }
                            }
                            catch (Throwable throwable) {
                                // empty catch block
                            }
                            if (!verified) {
                                try {
                                    sourceContainer.removeItemStackFromSlot((short)slot, transferAmount);
                                }
                                catch (Throwable throwable) {}
                                continue;
                            }
                            try {
                                this.spawnVisualFor(safeStack, true, pos, side, entities);
                            }
                            catch (Throwable throwable) {
                                // empty catch block
                            }
                            this.getItemContainer().removeItemStackFromSlot((short)0, transferAmount);
                            return true;
                        }
                        catch (Throwable transferAmount) {
                            // empty catch block
                        }
                    }
                }
                catch (Throwable hopperStack) {
                    // empty catch block
                }
            }
        }
        for (int slot = 0; slot < sourceContainer.getCapacity(); ++slot) {
            int transferAmount;
            ItemStack stack = sourceContainer.getItemStack((short)slot);
            if (stack == null) continue;
            String probeKey2 = null;
            try {
                probeKey2 = stack.getBlockKey();
            }
            catch (Throwable safeStack) {
                // empty catch block
            }
            if (probeKey2 == null) {
                probeKey2 = this.resolveItemStackKey(stack);
            }
            if (!this.isItemAllowedByFilter(probeKey2) || (transferAmount = (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)stack.getQuantity())) <= 0) continue;
            ItemStack safeStack = stack.withQuantity(transferAmount);
            ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short)0, safeStack);
            if (!t.succeeded()) continue;
            sourceContainer.removeItemStackFromSlot((short)slot, transferAmount);
            try {
                this.spawnVisualFor(safeStack, exportPhase, pos, side, entities);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            return true;
        }
        return false;
    }

    private boolean tryImportFromContainer(WorldChunk chunk, Vector3i pos, Store<EntityStore> entities, ConnectedBlockPatternRule.AdjacentSide side) {
        ItemStackSlotTransaction t;
        String blockKey;
        ItemStack stack;
        Object state;
        block92: {
            ItemStack destStack = this.getItemContainer().getItemStack((short)0);
            try {
                ((HytaleLogger.Api)HytaleLogger.getLogger().atInfo()).log("[HopperDiag] tryImportFromContainer pos=" + String.valueOf(pos) + " destStack=" + (destStack == null ? "null" : String.valueOf(destStack)));
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (destStack != null && destStack.getQuantity() >= 100) {
                return false;
            }
            try {
                String preMsg = "[HopperDiag] tryImportFromContainer START pos=" + String.valueOf(pos) + " chunkIndex=" + ChunkUtil.indexChunkFromBlock((int)pos.x, (int)pos.z) + " chunkPresent=" + (chunk != null);
                try {
                    ((HytaleLogger.Api)HytaleLogger.getLogger().atWarning()).log(preMsg);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                try {
                    System.out.println(preMsg);
                }
                catch (Throwable throwable) {}
            }
            catch (Throwable preMsg) {
                // empty catch block
            }
            state = EngineCompat.getState(chunk, pos.x, pos.y, pos.z);
            try {
                ((HytaleLogger.Api)HytaleLogger.getLogger().atWarning()).log("[HopperDiag] tryImportFromContainer: pos=" + String.valueOf(pos) + " stateClass=" + (state == null ? "null" : state.getClass().getName()));
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                System.out.println("[HopperDiag] tryImportFromContainer: pos=" + String.valueOf(pos) + " stateClass=" + (state == null ? "null" : state.getClass().getName()));
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (state == null) {
                try {
                    Object blockType = EngineCompat.getBlockType(chunk, pos.x, pos.y, pos.z);
                    ((HytaleLogger.Api)HytaleLogger.getLogger().atWarning()).log("[HopperDiag] EngineCompat.getBlockType for pos=" + String.valueOf(pos) + " -> " + (blockType == null ? "null" : blockType.toString()));
                    try {
                        System.out.println("[HopperDiag] EngineCompat.getBlockType for pos=" + String.valueOf(pos) + " -> " + (blockType == null ? "null" : blockType.toString()));
                    }
                    catch (Throwable throwable) {}
                }
                catch (Throwable blockType) {
                    // empty catch block
                }
                try {
                    if (this.w != null) {
                        Store cs = this.w.getChunkStore().getStore();
                        Ref colRef = this.w.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock((int)pos.x, (int)pos.z));
                        if (colRef == null) {
                            ((HytaleLogger.Api)HytaleLogger.getLogger().atInfo()).log("[HopperDiag] chunk reference is null for chunkIndex=" + ChunkUtil.indexChunkFromBlock((int)pos.x, (int)pos.z));
                            break block92;
                        }
                        BlockComponentChunk bcc = (BlockComponentChunk)cs.getComponent(colRef, BlockComponentChunk.getComponentType());
                        ((HytaleLogger.Api)HytaleLogger.getLogger().atInfo()).log("[HopperDiag] BlockComponentChunk present=" + (bcc != null));
                        if (bcc == null) break block92;
                        Ref blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn((int)pos.x, (int)pos.y, (int)pos.z));
                        String brMsg = "[HopperDiag] blockRef for pos=" + String.valueOf(pos) + " -> " + (String)(blockRef == null ? "null" : "index=" + blockRef.getIndex());
                        ((HytaleLogger.Api)HytaleLogger.getLogger().atWarning()).log(brMsg);
                        try {
                            System.out.println(brMsg);
                        }
                        catch (Throwable throwable) {
                            // empty catch block
                        }
                        try {
                            if (blockRef == null || this.w == null) break block92;
                            try {
                                Method getComp;
                                block93: {
                                    getComp = cs.getClass().getMethod("getComponent", Ref.class, Class.forName("com.hypixel.hytale.component.ComponentType"));
                                    Class<?> icbCls = Class.forName("com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock");
                                    Method getCompType = icbCls.getMethod("getComponentType", new Class[0]);
                                    Object compType = getCompType.invoke(null, new Object[0]);
                                    Object icbObj = null;
                                    try {
                                        icbObj = getComp.invoke((Object)cs, blockRef, compType);
                                    }
                                    catch (Throwable ignored) {
                                        icbObj = null;
                                    }
                                    if (icbObj != null && icbCls.isInstance(icbObj)) {
                                        try {
                                            Method getIC = icbCls.getMethod("getItemContainer", new Class[0]);
                                            Object cont = getIC.invoke(icbObj, new Object[0]);
                                            if (!(cont instanceof ItemContainer)) break block93;
                                            ItemContainer compContainer = (ItemContainer)cont;
                                            ((HytaleLogger.Api)HytaleLogger.getLogger().atInfo()).log("[HopperDiag] component-first container capacity=" + compContainer.getCapacity());
                                            for (int slot = 0; slot < compContainer.getCapacity(); ++slot) {
                                                try {
                                                    int transferAmount;
                                                    ItemStack stack2 = compContainer.getItemStack((short)slot);
                                                    if (stack2 == null) continue;
                                                    String blockKey2 = null;
                                                    try {
                                                        blockKey2 = stack2.getBlockKey();
                                                    }
                                                    catch (Throwable throwable) {
                                                        // empty catch block
                                                    }
                                                    if (blockKey2 == null) {
                                                        blockKey2 = this.resolveItemStackKey(stack2);
                                                    }
                                                    if (!this.isItemAllowedByFilter(blockKey2) || (transferAmount = (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)stack2.getQuantity())) <= 0) continue;
                                                    ItemStackSlotTransaction t2 = this.getItemContainer().addItemStackToSlot((short)0, stack2.withQuantity(transferAmount));
                                                    try {
                                                        ((HytaleLogger.Api)HytaleLogger.getLogger().atInfo()).log("[HopperDiag] componentContainer transferAttempt slot=" + slot + " amt=" + transferAmount + " tx=" + (t2 == null ? "null" : String.valueOf(t2.succeeded())));
                                                    }
                                                    catch (Throwable throwable) {
                                                        // empty catch block
                                                    }
                                                    if (t2 == null || !t2.succeeded()) continue;
                                                    compContainer.removeItemStackFromSlot((short)slot, transferAmount);
                                                    return true;
                                                }
                                                catch (Throwable stack2) {
                                                    // empty catch block
                                                }
                                            }
                                        }
                                        catch (Throwable getIC) {
                                            // empty catch block
                                        }
                                    }
                                }
                                try {
                                    Class<?> hopperCompCls = Class.forName("org.Ev0Mods.plugin.api.component.HopperComponent");
                                    Method getCompTypeH = hopperCompCls.getMethod("getComponentType", new Class[0]);
                                    Object hopperCompType = getCompTypeH.invoke(null, new Object[0]);
                                    Object hopperCompObj = null;
                                    try {
                                        hopperCompObj = getComp.invoke((Object)cs, blockRef, hopperCompType);
                                    }
                                    catch (Throwable ignored) {
                                        hopperCompObj = null;
                                    }
                                    if (hopperCompObj == null || !hopperCompCls.isInstance(hopperCompObj)) break block92;
                                    try {
                                        Method getIC = hopperCompCls.getMethod("getItemContainer", new Class[0]);
                                        Object cont = getIC.invoke(hopperCompObj, new Object[0]);
                                        if (!(cont instanceof ItemContainer)) break block92;
                                        ItemContainer target = (ItemContainer)cont;
                                        ((HytaleLogger.Api)HytaleLogger.getLogger().atInfo()).log("[HopperDiag] component-first chained hopper target capacity=" + target.getCapacity());
                                        ItemStack hopperStack = this.getItemContainer().getItemStack((short)0);
                                        if (hopperStack == null) {
                                            return false;
                                        }
                                        for (int slot = 0; slot < target.getCapacity(); ++slot) {
                                            try {
                                                ItemStackSlotTransaction t3;
                                                if (!this.isItemAllowedByFilter(hopperStack.getBlockKey())) {
                                                    return false;
                                                }
                                                int transferAmount = (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)hopperStack.getQuantity());
                                                if (transferAmount <= 0 || (t3 = target.addItemStackToSlot((short)slot, hopperStack.withQuantity(transferAmount))) == null || !t3.succeeded()) continue;
                                                boolean verified = false;
                                                try {
                                                    ItemStack after = target.getItemStack((short)slot);
                                                    if (after != null && after.getQuantity() >= transferAmount) {
                                                        verified = true;
                                                    }
                                                }
                                                catch (Throwable throwable) {
                                                    // empty catch block
                                                }
                                                if (!verified) {
                                                    try {
                                                        target.removeItemStackFromSlot((short)slot, transferAmount);
                                                    }
                                                    catch (Throwable throwable) {}
                                                    continue;
                                                }
                                                try {
                                                    this.spawnVisualFor(hopperStack.withQuantity(transferAmount), true, pos, side, entities);
                                                }
                                                catch (Throwable throwable) {
                                                    // empty catch block
                                                }
                                                this.getItemContainer().removeItemStackFromSlot((short)0, transferAmount);
                                                return true;
                                            }
                                            catch (Throwable throwable) {
                                                // empty catch block
                                            }
                                        }
                                        break block92;
                                    }
                                    catch (Throwable throwable) {
                                    }
                                }
                                catch (Throwable hopperCompCls) {}
                                break block92;
                            }
                            catch (Throwable getComp) {
                            }
                        }
                        catch (Throwable getComp) {}
                        break block92;
                    }
                    ((HytaleLogger.Api)HytaleLogger.getLogger().atInfo()).log("[HopperDiag] world reference (w) is null while inspecting pos=" + String.valueOf(pos));
                }
                catch (Throwable t4) {
                    try {
                        ((HytaleLogger.Api)HytaleLogger.getLogger().atWarning()).log("[HopperDiag] exception while inspecting chunk/block refs: " + t4.getMessage());
                    }
                    catch (Throwable colRef) {
                        // empty catch block
                    }
                }
            }
        }
        if (state != null && state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState")) {
            ItemContainer output = this.getContainerFromItemContainerObject(this.getItemContainerFromState(state), 2);
            if (output == null) {
                try {
                    ((HytaleLogger.Api)HytaleLogger.getLogger().atInfo()).log("[HopperDiag] processingBench: output container null");
                }
                catch (Throwable colRef) {
                    // empty catch block
                }
                return false;
            }
            try {
                ((HytaleLogger.Api)HytaleLogger.getLogger().atInfo()).log("[HopperDiag] processingBench: output capacity=" + output.getCapacity());
            }
            catch (Throwable colRef) {
                // empty catch block
            }
            int outCap = output.getCapacity();
            for (int slot = 0; slot < outCap; ++slot) {
                Ref<EntityStore> esx;
                int transferAmount;
                stack = output.getItemStack((short)slot);
                if (stack == null) continue;
                blockKey = null;
                try {
                    blockKey = stack.getBlockKey();
                }
                catch (Throwable getComp) {
                    // empty catch block
                }
                if (blockKey == null) {
                    blockKey = this.resolveItemStackKey(stack);
                }
                if (!this.isItemAllowedByFilter(blockKey) || (transferAmount = (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)stack.getQuantity())) <= 0) continue;
                t = this.getItemContainer().addItemStackToSlot((short)0, stack.withQuantity(transferAmount));
                try {
                    ((HytaleLogger.Api)HytaleLogger.getLogger().atInfo()).log("[HopperDiag] processingBench transferAttempt slot=" + slot + " amt=" + transferAmount + " tx=" + (t == null ? "null" : String.valueOf(t.succeeded())));
                }
                catch (Throwable getCompType) {
                    // empty catch block
                }
                if (t == null || !t.succeeded()) continue;
                Vector3i relRot = WorldHelper.rotate((ConnectedBlockPatternRule.AdjacentSide)side, (int)this.getRotationIndex()).relativePosition;
                Vector3d velRot = new Vector3d((double)relRot.x * 0.35, 0.25, (double)relRot.z * 0.35);
                Vector3i hopperBlock = this.getBlockPosition();
                Vector3d hopperCenter = new Vector3d((double)hopperBlock.x + 0.5, (double)hopperBlock.y + 0.5, (double)hopperBlock.z + 0.5);
                if (this.drop && !this.l.isEmpty() && this.l.getFirst() != null && (esx = this.l.getFirst()).isValid()) {
                    this.l.removeFirst();
                    try {
                        this.visualMap.remove(esx);
                        this.visualSpawnTimes.remove(esx);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    entities.removeEntity(esx, RemoveReason.REMOVE);
                }
                output.removeItemStackFromSlot((short)slot, transferAmount);
                return true;
            }
            return false;
        }
        ItemContainer container = null;
        try {
            Object contObj = this.getItemContainerFromState(state);
            container = contObj instanceof ItemContainer ? (ItemContainer)contObj : this.getContainerFromItemContainerObject(contObj, 0);
        }
        catch (Throwable contObj) {
            // empty catch block
        }
        if (container == null) {
            try {
                ((HytaleLogger.Api)HytaleLogger.getLogger().atInfo()).log("[HopperDiag] generic container null for state=" + (state == null ? "null" : state.getClass().getName()));
            }
            catch (Throwable contObj) {
                // empty catch block
            }
            return false;
        }
        try {
            ((HytaleLogger.Api)HytaleLogger.getLogger().atInfo()).log("[HopperDiag] generic container capacity=" + container.getCapacity());
        }
        catch (Throwable contObj) {
            // empty catch block
        }
        int cap = container.getCapacity();
        for (int slot = 0; slot < cap; ++slot) {
            try {
                stack = container.getItemStack((short)slot);
                if (stack == null) continue;
                blockKey = null;
                try {
                    blockKey = stack.getBlockKey();
                }
                catch (Throwable transferAmount) {
                    // empty catch block
                }
                if (blockKey == null) {
                    blockKey = this.resolveItemStackKey(stack);
                }
                if (!this.isItemAllowedByFilter(blockKey) || (transferAmount = (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)stack.getQuantity())) <= 0) continue;
                t = this.getItemContainer().addItemStackToSlot((short)0, stack.withQuantity(transferAmount));
                try {
                    ((HytaleLogger.Api)HytaleLogger.getLogger().atInfo()).log("[HopperDiag] genericContainer transferAttempt slot=" + slot + " amt=" + transferAmount + " tx=" + (t == null ? "null" : String.valueOf(t.succeeded())));
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                if (t == null || !t.succeeded()) continue;
                container.removeItemStackFromSlot((short)slot, transferAmount);
                return true;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return false;
    }

    private boolean tryPickupItemEntities(Vector3i importPos, Store<EntityStore> entities) {
        int hopperQty;
        HopperComponent.perfInfo("[Hopper][Pickup] tryPickupItemEntities called at " + String.valueOf(importPos));
        List rawResults = SpatialResource.getThreadLocalReferenceList();
        Vector3d boxMin = new Vector3d((double)importPos.x, (double)importPos.y, (double)importPos.z);
        Vector3d boxMax = new Vector3d((double)importPos.x + 1.0, (double)importPos.y + 1.0, (double)importPos.z + 1.0);
        HopperComponent.perfInfo("[Hopper][Pickup] collectBox min=" + String.valueOf(boxMin) + " max=" + String.valueOf(boxMax));
        ((SpatialResource)entities.getResource(EntityModule.get().getItemSpatialResourceType())).getSpatialStructure().collectBox(boxMin, boxMax, rawResults);
        HopperComponent.perfInfo("[Hopper][Pickup] collectBox rawResults.size()=" + rawResults.size());
        try {
            ((HytaleLogger.Api)HytaleLogger.getLogger().atInfo()).log("[HopperDiag] tryPickupItemEntities rawResults=" + rawResults.size());
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        if (rawResults.isEmpty()) {
            HopperComponent.perfInfo("[Hopper][Pickup] no items found in box, returning false");
            return false;
        }
        ArrayList itemRefs = new ArrayList(rawResults);
        int n = hopperQty = this.getItemContainer().getItemStack((short)0) == null ? 0 : this.getItemContainer().getItemStack((short)0).getQuantity();
        if (hopperQty >= 100) {
            return false;
        }
        for (Ref ref : itemRefs) {
            int transferAmount;
            ItemStack stack;
            ItemComponent ic;
            if (ref == null || !ref.isValid() || this.l.contains(ref)) continue;
            if (entities.getComponent(ref, Intangible.getComponentType()) != null) {
                // empty if block
            }
            if ((ic = (ItemComponent)entities.getComponent(ref, ItemComponent.getComponentType())) == null || !ic.canPickUp() || (stack = ic.getItemStack()) == null || stack.isEmpty()) continue;
            String itemKey = null;
            try {
                itemKey = stack.getBlockKey();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (itemKey == null) {
                itemKey = this.resolveItemStackKey(stack);
            }
            if (!this.isItemAllowedByFilter(itemKey) || (transferAmount = (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)Math.min(stack.getQuantity(), 100 - hopperQty))) <= 0) continue;
            ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short)0, stack.withQuantity(transferAmount));
            try {
                ((HytaleLogger.Api)HytaleLogger.getLogger().atInfo()).log("[HopperDiag] pickup attempt ref=" + String.valueOf(ref) + " amt=" + transferAmount + " tx=" + (t == null ? "null" : String.valueOf(t.succeeded())));
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (t == null || !t.succeeded()) continue;
            int remaining = stack.getQuantity() - transferAmount;
            if (remaining <= 0) {
                entities.removeEntity(ref, RemoveReason.REMOVE);
            } else {
                TransformComponent tc = (TransformComponent)entities.getComponent(ref, TransformComponent.getComponentType());
                Vector3d dropPos = tc != null ? tc.getPosition().clone() : new Vector3d((double)importPos.x + 0.5, (double)importPos.y + 0.5, (double)importPos.z + 0.5);
                entities.removeEntity(ref, RemoveReason.REMOVE);
                Holder newHolder = ItemComponent.generateItemDrop(entities, (ItemStack)stack.withQuantity(remaining), (Vector3d)dropPos, (Vector3f)Vector3f.ZERO, (float)0.0f, (float)-1.0f, (float)0.0f);
                if (newHolder != null) {
                    entities.addEntity(newHolder, AddReason.SPAWN);
                }
            }
            return true;
        }
        return false;
    }

    private void ensureArcioComponents(World world, @Nullable CommandBuffer<ChunkStore> commandBuffer) {
        if (this.arcioInitialized) {
            return;
        }
        try {
            ArcioMechanismComponent mech;
            Vector3i p = this.getBlockPosition();
            int bx = p.x;
            int by = p.y;
            int bz = p.z;
            Store cs = world.getChunkStore().getStore();
            Ref chunkRef = world.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock((int)bx, (int)bz));
            if (chunkRef == null) {
                return;
            }
            BlockComponentChunk bcc = (BlockComponentChunk)cs.getComponent(chunkRef, BlockComponentChunk.getComponentType());
            if (bcc == null) {
                return;
            }
            Ref blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn((int)bx, (int)by, (int)bz));
            if (blockRef == null) {
                return;
            }
            BlockUUIDComponent uuid = (BlockUUIDComponent)cs.getComponent(blockRef, BlockUUIDComponent.getComponentType());
            if (uuid == null) {
                uuid = BlockUUIDComponent.randomUUID();
                uuid.setPosition(new Vector3i(bx, by, bz));
                if (commandBuffer != null) {
                    commandBuffer.putComponent(blockRef, BlockUUIDComponent.getComponentType(), (Component)uuid);
                } else {
                    cs.putComponent(blockRef, BlockUUIDComponent.getComponentType(), (Component)uuid);
                }
                ArcioPlugin.get().putUUID(uuid.getUuid(), blockRef);
            }
            if ((mech = (ArcioMechanismComponent)cs.getComponent(blockRef, ArcioMechanismComponent.getComponentType())) == null) {
                mech = new ArcioMechanismComponent("Hopper", 0, 1);
                if (commandBuffer != null) {
                    commandBuffer.putComponent(blockRef, ArcioMechanismComponent.getComponentType(), (Component)mech);
                } else {
                    cs.putComponent(blockRef, ArcioMechanismComponent.getComponentType(), (Component)mech);
                }
            }
            this.arcioInitialized = true;
        }
        catch (Exception e) {
            Ev0Log.warn(HytaleLogger.getLogger(), "[Hopper] Failed to ensure ArcIO components: " + e.getMessage());
        }
    }

    private boolean isArcioActive(World world) {
        try {
            ArcioMechanismComponent mech;
            Ref blockRef;
            BlockComponentChunk bcc;
            Vector3i p = this.getBlockPosition();
            int bx = p.x;
            int by = p.y;
            int bz = p.z;
            Store cs = world.getChunkStore().getStore();
            Ref chunkRef = world.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock((int)bx, (int)bz));
            if (chunkRef != null && (bcc = (BlockComponentChunk)cs.getComponent(chunkRef, BlockComponentChunk.getComponentType())) != null && (blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn((int)bx, (int)by, (int)bz))) != null && (mech = (ArcioMechanismComponent)cs.getComponent(blockRef, ArcioMechanismComponent.getComponentType())) != null && mech.getStrongestInputSignal(world) > 0) {
                return true;
            }
        }
        catch (Exception e) {
            Ev0Log.warn(HytaleLogger.getLogger(), "[Hopper] ArcIO signal check failed: " + e.getMessage());
        }
        return this.hasAdjacentActiveArcioMechanism(world);
    }

    private boolean hasAdjacentActiveArcioMechanism(World world) {
        try {
            int[][] offsets;
            Store cs = world.getChunkStore().getStore();
            Vector3i p = this.getBlockPosition();
            int bx = p.x;
            int by = p.y;
            int bz = p.z;
            for (int[] off : offsets = new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}}) {
                ArcioMechanismComponent mc;
                Ref blockRef;
                BlockComponentChunk bcc;
                int nx = bx + off[0];
                int ny = by + off[1];
                int nz = bz + off[2];
                Ref chunkRef = world.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock((int)nx, (int)nz));
                if (chunkRef == null || (bcc = (BlockComponentChunk)cs.getComponent(chunkRef, BlockComponentChunk.getComponentType())) == null || (blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn((int)nx, (int)ny, (int)nz))) == null || (mc = (ArcioMechanismComponent)cs.getComponent(blockRef, ArcioMechanismComponent.getComponentType())) == null || mc.getStrongestInputSignal(world) <= 0) continue;
                return true;
            }
        }
        catch (Exception e) {
            Ev0Log.warn(HytaleLogger.getLogger(), "[Hopper] ArcIO adjacent check failed: " + e.getMessage());
        }
        return false;
    }

    protected void reset(Instant currentTime) {
        this.startTime = currentTime;
    }

    @Nonnull
    public static List<Ref<EntityStore>> getAllEntitiesInBox(HopperComponent hp, Vector3i pos, float height, @Nonnull ComponentAccessor<EntityStore> components, boolean players, boolean entities, boolean items) {
        List results = SpatialResource.getThreadLocalReferenceList();
        Vector3d center = new Vector3d((double)pos.x, (double)pos.y, (double)pos.z);
        double queryHeight = Math.max(1.0f, height);
        if (players) {
            ((SpatialResource)components.getResource(EntityModule.get().getPlayerSpatialResourceType())).getSpatialStructure().collectCylinder(center, 4.0, queryHeight, results);
        }
        if (entities) {
            // empty if block
        }
        if (items) {
            // empty if block
        }
        if (hp != null && hp.nearbyBuffer != null) {
            hp.nearbyBuffer.clear();
            hp.nearbyBuffer.addAll(results);
            return hp.nearbyBuffer;
        }
        return new ArrayList<Ref<EntityStore>>(results);
    }

    public static List<Ref<EntityStore>> getAllItemsInBox(HopperComponent hp, Vector3i pos, float height, @Nonnull ComponentAccessor<EntityStore> components, boolean players, boolean entities, boolean items) {
        List results = SpatialResource.getThreadLocalReferenceList();
        Vector3d center = new Vector3d((double)pos.x, (double)pos.y, (double)pos.z);
        double queryHeight = Math.max(0.5f, height);
        if (entities) {
            Vector3d min = new Vector3d((double)pos.x - 0.5, (double)pos.y - 0.5, (double)pos.z - 0.5);
            Vector3d max = new Vector3d((double)pos.x + 0.5, (double)pos.y + 0.5, (double)pos.z + 0.5);
            ((SpatialResource)components.getResource(EntityModule.get().getEntitySpatialResourceType())).getSpatialStructure().collectBox(min, max, results);
        }
        if (items) {
            ((SpatialResource)components.getResource(EntityModule.get().getItemSpatialResourceType())).getSpatialStructure().collectCylinder(center, 2.0, Math.max(0.5, queryHeight), results);
        }
        if (hp != null && hp.nearbyBuffer != null) {
            hp.nearbyBuffer.clear();
            hp.nearbyBuffer.addAll(results);
            return hp.nearbyBuffer;
        }
        return new ArrayList<Ref<EntityStore>>(results);
    }

    private Object getRefFromArchetype(ArchetypeChunk<?> archeChunk, int index) {
        try {
            String key = "ArchetypeChunk.getRef";
            Method m = REFLECTION_METHOD_CACHE.get(key);
            if (m == null) {
                Class<?> ac = archeChunk.getClass();
                for (String name : new String[]{"getReferenceTo", "getRef", "getRefAt", "referenceTo", "getReference"}) {
                    try {
                        m = ac.getMethod(name, Integer.TYPE);
                        break;
                    }
                    catch (NoSuchMethodException noSuchMethodException) {
                    }
                }
                if (m != null) {
                    REFLECTION_METHOD_CACHE.put(key, m);
                }
            }
            if (m != null) {
                return m.invoke(archeChunk, index);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    private HopperComponent getHopperComponent(Store<ChunkStore> store, Object ref) {
        try {
            Ev0Lib lib = Ev0Lib.getInstance();
            if (lib == null) {
                return null;
            }
            ComponentType<ChunkStore, HopperComponent> compType = lib.getHopperComponentType();
            if (compType == null) {
                return null;
            }
            Method getter = null;
            for (Method mm : store.getClass().getMethods()) {
                if (!mm.getName().equals("getComponent") || mm.getParameterCount() != 2) continue;
                getter = mm;
                break;
            }
            if (getter == null) {
                return null;
            }
            Object comp = getter.invoke(store, ref, compType);
            if (comp instanceof HopperComponent) {
                return (HopperComponent)comp;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    private void putHopperComponent(Store<ChunkStore> store, Object ref, HopperComponent comp) {
        try {
            Method put = null;
            for (Method method : store.getClass().getMethods()) {
                if (!method.getName().equals("putComponent") || method.getParameterCount() != 2) continue;
                put = method;
                break;
            }
            if (put != null) {
                put.invoke(store, ref, comp);
                return;
            }
            Ev0Lib lib = Ev0Lib.getInstance();
            if (lib == null) {
                return;
            }
            ComponentType<ChunkStore, HopperComponent> compType = lib.getHopperComponentType();
            if (compType == null) {
                return;
            }
            Method ensure = null;
            for (Method mm : store.getClass().getMethods()) {
                if (!mm.getName().equals("ensureAndGetComponent") || mm.getParameterCount() != 2) continue;
                ensure = mm;
                break;
            }
            if (ensure != null) {
                Object object = ensure.invoke(store, ref, compType);
                if (object == null) {
                    return;
                }
                try {
                    Field f = object.getClass().getField("data");
                    f.set(object, (Object)comp.data);
                }
                catch (Throwable throwable) {}
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private WorldChunk getChunkFromStoreRef(Store<ChunkStore> store, Object ref) {
        try {
            if (ref instanceof Ref) {
                Ref r = (Ref)ref;
                return null;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    private Vector3i extractPositionFromStoreRef(Store<ChunkStore> store, Object ref) {
        try {
            return this.cachedPosition;
        }
        catch (Throwable ignored) {
            return this.cachedPosition;
        }
    }

    private void resolvePosition(Store<ChunkStore> store, Ref<ChunkStore> myRef) {
        try {
            int myIdx = myRef.getIndex();
            ChunkStore cs = (ChunkStore)store.getExternalData();
            LongSet chunkIndexes = cs.getChunkIndexes();
            if (chunkIndexes == null || chunkIndexes.isEmpty()) {
                return;
            }
            LongIterator longIterator = chunkIndexes.iterator();
            while (longIterator.hasNext()) {
                BlockComponentChunk bcc;
                long chunkIdx = (Long)longIterator.next();
                Ref colRef = cs.getChunkReference(chunkIdx);
                if (colRef == null || (bcc = (BlockComponentChunk)store.getComponent(colRef, BlockComponentChunk.getComponentType())) == null) continue;
                for (Map.Entry entry : bcc.getEntityReferences().entrySet()) {
                    Ref blockRef = (Ref)entry.getValue();
                    if (blockRef == null || blockRef.getIndex() != myIdx) continue;
                    int blockIndex = (Integer)entry.getKey();
                    int lx = ChunkUtil.xFromBlockInColumn((int)blockIndex);
                    int wy = ChunkUtil.yFromBlockInColumn((int)blockIndex);
                    int lz = ChunkUtil.zFromBlockInColumn((int)blockIndex);
                    int wx = ChunkUtil.worldCoordFromLocalCoord((int)ChunkUtil.xOfChunkIndex((long)chunkIdx), (int)lx);
                    int wz = ChunkUtil.worldCoordFromLocalCoord((int)ChunkUtil.zOfChunkIndex((long)chunkIdx), (int)lz);
                    this.cachedPosition = new Vector3i(wx, wy, wz);
                    return;
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private void fallbackHeartbeat() {
        block14: {
            try {
                if (this.es == null || this.visualSpawnTimes.isEmpty()) break block14;
                Instant now = ((WorldTimeResource)this.es.getResource(WorldTimeResource.getResourceType())).getGameTime();
                Iterator<Map.Entry<Ref<EntityStore>, Instant>> it2 = this.visualSpawnTimes.entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry<Ref<EntityStore>, Instant> e = it2.next();
                    Ref<EntityStore> ref = e.getKey();
                    Instant spawnTime = e.getValue();
                    try {
                        if (ref == null || !ref.isValid()) {
                            it2.remove();
                            try {
                                this.visualMap.remove(ref);
                            }
                            catch (Exception exception) {}
                            continue;
                        }
                        if (!now.isAfter(spawnTime.plusSeconds(5L))) continue;
                        it2.remove();
                        try {
                            this.visualMap.remove(ref);
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                        try {
                            this.l.remove(ref);
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                        try {
                            this.es.removeEntity(ref, RemoveReason.REMOVE);
                        }
                        catch (Exception exception) {
                        }
                    }
                    catch (Exception exception) {}
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    private void spawnVisualFor(ItemStack safeStack, boolean exportPhase, Vector3i pos, ConnectedBlockPatternRule.AdjacentSide side, Store<EntityStore> entities) {
        Holder itemEntityHolder;
        List<Ref<EntityStore>> nearby;
        Vector3d velocity;
        try {
            ((HytaleLogger.Api)HytaleLogger.getLogger().atInfo()).log("[Visual] spawnVisualFor called exportPhase=" + exportPhase + " nearbySize=" + this.nearbyBuffer.size() + " entities=" + (entities != null) + " stack=" + String.valueOf(safeStack));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        if (safeStack == null || safeStack.isEmpty()) {
            return;
        }
        Vector3i rel = WorldHelper.rotate((ConnectedBlockPatternRule.AdjacentSide)side, (int)this.getRotationIndex()).relativePosition;
        Vector3i hopperBlock = this.getBlockPosition();
        Vector3d hopperCenter = new Vector3d((double)hopperBlock.x + 0.5, (double)hopperBlock.y + 0.5, (double)hopperBlock.z + 0.5);
        Vector3d sourceCenter = new Vector3d((double)pos.x + 0.5, (double)pos.y + 0.5, (double)pos.z + 0.5);
        Vector3d spawnPos = exportPhase ? hopperCenter : sourceCenter;
        Vector3d vector3d = velocity = exportPhase ? new Vector3d((double)(-rel.x) * 0.35, 0.25, (double)(-rel.z) * 0.35) : new Vector3d((double)rel.x * 0.35, 0.25, (double)rel.z * 0.35);
        if (exportPhase && !(nearby = this.nearbyBuffer).isEmpty()) {
            boolean anySpawned = false;
            for (Ref<EntityStore> targetRef : nearby) {
                Ref<EntityStore> rs;
                String oppSide = switch (side.toString()) {
                    case "East" -> "West";
                    case "West" -> "East";
                    case "North" -> "South";
                    case "South" -> "North";
                    case "Up" -> "Down";
                    case "Down" -> "Up";
                    default -> side.toString();
                };
                try {
                    rs = ItemUtilsExtended.throwItem(this.getBlockType().getId(), oppSide, new Vector3d((double)hopperBlock.x, (double)hopperBlock.y, (double)hopperBlock.z), targetRef, entities, safeStack, Vector3d.ZERO, 0.0f);
                }
                catch (Exception e) {
                    rs = null;
                }
                if (rs == null) continue;
                this.l.add(rs);
                try {
                    this.visualMap.put(rs, safeStack);
                    Instant now = entities != null ? ((WorldTimeResource)entities.getResource(WorldTimeResource.getResourceType())).getGameTime() : Instant.now();
                    this.visualSpawnTimes.put(rs, now);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                anySpawned = true;
            }
            if (anySpawned) {
                return;
            }
        }
        if ((itemEntityHolder = ItemComponent.generateItemDrop(entities, (ItemStack)safeStack, (Vector3d)new Vector3d(spawnPos.x, spawnPos.y, spawnPos.z), (Vector3f)Vector3f.ZERO, (float)0.0f, (float)-1.0f, (float)0.0f)) == null) {
            return;
        }
        ItemComponent itemComponent = (ItemComponent)itemEntityHolder.getComponent(ItemComponent.getComponentType());
        if (itemComponent != null) {
            itemComponent.setPickupDelay(1.0E8f);
            itemComponent.setRemovedByPlayerPickup(false);
            itemComponent.computeDynamicLight();
        }
        try {
            itemEntityHolder.removeComponent(PhysicsValues.getComponentType());
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(velocity.x, velocity.y, velocity.z);
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            itemEntityHolder.tryRemoveComponent(BoundingBox.getComponentType());
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            itemEntityHolder.ensureAndGetComponent(Intangible.getComponentType());
        }
        catch (Exception exception) {
            // empty catch block
        }
        Ref spawned = entities.addEntity(itemEntityHolder, AddReason.SPAWN);
        try {
            ((HytaleLogger.Api)HytaleLogger.getLogger().atInfo()).log("[Visual] addEntity returned=" + String.valueOf(spawned) + " spawnPos=" + String.valueOf(spawnPos));
        }
        catch (Throwable targetRef) {
            // empty catch block
        }
        if (spawned != null) {
            TransformComponent tc = (TransformComponent)entities.getComponent(spawned, TransformComponent.getComponentType());
            if (tc != null) {
                tc.setPosition(new Vector3d(spawnPos.x, spawnPos.y, spawnPos.z));
            }
            this.l.add((Ref<EntityStore>)spawned);
            try {
                this.visualMap.put((Ref<EntityStore>)spawned, safeStack);
                Instant now = entities != null ? ((WorldTimeResource)entities.getResource(WorldTimeResource.getResourceType())).getGameTime() : Instant.now();
                this.visualSpawnTimes.put((Ref<EntityStore>)spawned, now);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    static {
        FALLBACK_SCHEDULER.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            for (HopperComponent hc : REGISTERED_COMPONENTS.keySet()) {
                try {
                    long last = hc.lastEngineTick;
                    if (hc.invalidatedFlag) {
                        REGISTERED_COMPONENTS.remove(hc);
                        continue;
                    }
                    if (now - last <= 2000L) continue;
                    try {
                        hc.fallbackHeartbeat();
                    }
                    catch (Throwable throwable) {
                    }
                }
                catch (Throwable throwable) {}
            }
        }, 2L, 2L, TimeUnit.SECONDS);
        CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(HopperComponent.class, HopperComponent::new).append(new KeyedCodec("Data", HopperProcessor.Data.CODEC, true), (c, v) -> {
            c.data = v;
        }, c -> c.data).add()).append(new KeyedCodec("Duration", (Codec)ProtocolCodecs.RANGEF, true), (c, v) -> {
            c.duration = v;
        }, c -> c.duration).add()).append(new KeyedCodec("Tier", (Codec)Codec.FLOAT, true), (c, v) -> {
            c.tier = v.floatValue();
        }, c -> Float.valueOf(c.tier)).add()).append(new KeyedCodec("Substitutions", (Codec)Codec.STRING_ARRAY, true), (c, v) -> {
            c.substitutions = v;
        }, c -> c.substitutions).add()).append(new KeyedCodec("FilterMode", (Codec)Codec.STRING, true), (c, v) -> {
            c.filterMode = v == null ? "Off" : v;
        }, c -> c.filterMode).add()).append(new KeyedCodec("Whitelist", (Codec)Codec.STRING_ARRAY, true), (c, v) -> {
            if (v == null) {
                c.whitelist.clear();
            } else {
                c.whitelist.clear();
                c.whitelist.addAll(Arrays.asList(v));
            }
        }, c -> c.whitelist.toArray(new String[0])).add()).append(new KeyedCodec("Blacklist", (Codec)Codec.STRING_ARRAY, true), (c, v) -> {
            if (v == null) {
                c.blacklist.clear();
            } else {
                c.blacklist.clear();
                c.blacklist.addAll(Arrays.asList(v));
            }
        }, c -> c.blacklist.toArray(new String[0])).add()).append(new KeyedCodec("ArcioMode", (Codec)Codec.STRING, true), (c, v) -> {
            c.arcioMode = v == null ? "IgnoreSignal" : v;
        }, c -> c.arcioMode).add()).build();
        boolean found = false;
        try {
            Class.forName("voidbond.arcio.components.ArcioMechanismComponent");
            found = true;
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        ARCIO_PRESENT = found;
        boolean found2 = false;
        try {
            Class.forName("net.crepe.inventory.IDrawerContainer");
            found2 = true;
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        SIMPLE_DRAWERS_PRESENT = found2;
        DEPENDENCIES = Set.of(new SystemDependency(Order.AFTER, FluidSystems.Ticking.class), new SystemDependency(Order.BEFORE, ChunkBlockTickSystem.Ticking.class));
    }
}

