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
 *  com.hypixel.hytale.component.ComponentRegistryProxy
 *  com.hypixel.hytale.component.ComponentType
 *  com.hypixel.hytale.component.Holder
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.RemoveReason
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.component.dependency.Dependency
 *  com.hypixel.hytale.component.dependency.Order
 *  com.hypixel.hytale.component.dependency.SystemDependency
 *  com.hypixel.hytale.component.query.Query
 *  com.hypixel.hytale.component.spatial.SpatialResource
 *  com.hypixel.hytale.logger.HytaleLogger
 *  com.hypixel.hytale.logger.HytaleLogger$Api
 *  com.hypixel.hytale.math.util.ChunkUtil
 *  com.hypixel.hytale.math.vector.Vector3d
 *  com.hypixel.hytale.math.vector.Vector3f
 *  com.hypixel.hytale.math.vector.Vector3i
 *  com.hypixel.hytale.protocol.BlockPosition
 *  com.hypixel.hytale.protocol.Rangef
 *  com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType
 *  com.hypixel.hytale.server.core.asset.type.blocktype.config.StateData
 *  com.hypixel.hytale.server.core.asset.type.fluid.Fluid
 *  com.hypixel.hytale.server.core.codec.ProtocolCodecs
 *  com.hypixel.hytale.server.core.entity.entities.BlockEntity
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.inventory.ItemStack
 *  com.hypixel.hytale.server.core.inventory.container.ItemContainer
 *  com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer
 *  com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction
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
 *  com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection
 *  com.hypixel.hytale.server.core.universe.world.chunk.section.FluidSection
 *  com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockPatternRule$AdjacentSide
 *  com.hypixel.hytale.server.core.universe.world.storage.ChunkStore
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.crepe.inventory.IDrawerContainer
 *  org.checkerframework.checker.nullness.compatqual.NonNullDecl
 *  voidbond.arcio.ArcioPlugin
 *  voidbond.arcio.components.ArcioMechanismComponent
 *  voidbond.arcio.components.BlockUUIDComponent
 */
package org.Ev0Mods.plugin.api.block.state;

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
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.Rangef;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.StateData;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.entity.entities.BlockEntity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
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
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.FluidSection;
import com.hypixel.hytale.server.core.universe.world.chunk.state.TickableBlockState;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockPatternRule;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerBlockState;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
import net.crepe.inventory.IDrawerContainer;
import org.Ev0Mods.plugin.Ev0Lib;
import org.Ev0Mods.plugin.api.Ev0Config;
import org.Ev0Mods.plugin.api.Ev0Log;
import org.Ev0Mods.plugin.api.codec.Codecs;
import org.Ev0Mods.plugin.api.codec.IdOutput;
import org.Ev0Mods.plugin.api.codec.ItemHandler;
import org.Ev0Mods.plugin.api.component.EngineCompat;
import org.Ev0Mods.plugin.api.component.HopperComponent;
import org.Ev0Mods.plugin.api.ui.HopperUIPage;
import org.Ev0Mods.plugin.api.util.ItemUtilsExtended;
import org.Ev0Mods.plugin.api.util.WorldHelper;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import voidbond.arcio.ArcioPlugin;
import voidbond.arcio.components.ArcioMechanismComponent;
import voidbond.arcio.components.BlockUUIDComponent;

public class HopperProcessor
implements TickableBlockState,
ItemContainerBlockState {
    private static final boolean PERF_DEBUG = false;
    public int fluid_id = 0;
    public Rangef duration = new Rangef(0.0f, 10.0f);
    public float tier;
    public static final BuilderCodec<HopperProcessor> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(HopperProcessor.class, HopperProcessor::new).append(new KeyedCodec("StartTime", (Codec)Codec.INSTANT, true), (i, v) -> {
        i.startTime = v;
    }, i -> i.startTime).add()).append(new KeyedCodec("Tier", (Codec)Codec.FLOAT, true), (i, v) -> {
        i.tier = v.floatValue();
    }, i -> Float.valueOf(i.tier)).add()).append(new KeyedCodec("Substitutions", (Codec)Codec.STRING_ARRAY, true), (i, v) -> {
        i.substitutions = v;
    }, i -> i.substitutions).add()).append(new KeyedCodec("Timer", (Codec)Codec.DOUBLE, true), (i, v) -> {
        i.timer = v;
    }, i -> i.timer).add()).append(new KeyedCodec("FilterMode", (Codec)Codec.STRING, true), (i, v) -> {
        i.filterMode = v == null ? "Off" : v;
    }, i -> i.filterMode).add()).append(new KeyedCodec("Whitelist", (Codec)Codec.STRING_ARRAY, true), (i, v) -> {
        if (v == null) {
            i.whitelist.clear();
        } else {
            i.whitelist.clear();
            i.whitelist.addAll(Arrays.asList(v));
        }
    }, i -> i.whitelist.toArray(new String[0])).add()).append(new KeyedCodec("Blacklist", (Codec)Codec.STRING_ARRAY, true), (i, v) -> {
        if (v == null) {
            i.blacklist.clear();
        } else {
            i.blacklist.clear();
            i.blacklist.addAll(Arrays.asList(v));
        }
    }, i -> i.blacklist.toArray(new String[0])).add()).append(new KeyedCodec("ArcioMode", (Codec)Codec.STRING, true), (i, v) -> {
        i.arcioMode = v == null ? "IgnoreSignal" : v;
    }, i -> i.arcioMode).add()).build();
    protected Instant startTime;
    private double timerV = 0.0;
    private double timer = 0.0;
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
    public Map<Ref<EntityStore>, ItemStack> visualMap = new HashMap<Ref<EntityStore>, ItemStack>();
    public Map<Ref<EntityStore>, Instant> visualSpawnTimes = new HashMap<Ref<EntityStore>, Instant>();
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
    private static final ConcurrentHashMap<HopperProcessor, Boolean> REGISTERED_PROCESSORS = new ConcurrentHashMap();
    private ItemContainer itemContainer;
    public static final boolean ARCIO_PRESENT;
    public static final boolean SIMPLE_DRAWERS_PRESENT;
    private boolean arcioInitialized = false;
    private String arcioMode = "IgnoreSignal";
    private final List<String> whitelist = Collections.synchronizedList(new ArrayList());
    private final List<String> blacklist = Collections.synchronizedList(new ArrayList());
    private volatile String filterMode = "Off";
    private final Map<PlayerRef, String> typedBuffer = new ConcurrentHashMap<PlayerRef, String>();
    @Nonnull
    private static final Query<ChunkStore> QUERY;
    @Nonnull
    private static final Set<Dependency<ChunkStore>> DEPENDENCIES;

    public HopperProcessor() {
        this.ic = new Ref[0];
    }

    private static void perfInfo(String msg) {
    }

    public ItemContainer getItemContainer() {
        return this.itemContainer;
    }

    public void setItemContainer(ItemContainer c) {
        this.itemContainer = c;
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
        block5: {
            try {
                Class<?> sc = this.getClass().getSuperclass();
                if (sc == null) break block5;
                for (String name : new String[]{"getBlockPosition", "getPosition", "getPos", "position"}) {
                    try {
                        Object r;
                        Method m = sc.getMethod(name, new Class[0]);
                        if (m == null || !((r = m.invoke((Object)this, new Object[0])) instanceof Vector3i)) continue;
                        return (Vector3i)r;
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
        return new Vector3i(0, 0, 0);
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

    public boolean initialize(BlockType blockType) {
        StateData stateData;
        boolean superInit = true;
        try {
            Object r;
            Method m = this.getClass().getSuperclass().getMethod("initialize", BlockType.class);
            if (m != null && (r = m.invoke((Object)this, blockType)) instanceof Boolean) {
                superInit = (Boolean)r;
            }
        }
        catch (Throwable m) {
            // empty catch block
        }
        if (superInit && blockType != null && (stateData = blockType.getState()) instanceof Data) {
            Data data;
            this.data = data = (Data)stateData;
            this.setItemContainer((ItemContainer)new SimpleItemContainer(1));
            try {
                REGISTERED_PROCESSORS.put(this, Boolean.TRUE);
                this.lastEngineTick = System.currentTimeMillis();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            return true;
        }
        return false;
    }

    public boolean canOpen(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl ComponentAccessor<EntityStore> componentAccessor) {
        try {
            Object r;
            Method m = this.getClass().getSuperclass().getMethod("canOpen", Ref.class, ComponentAccessor.class);
            if (m != null && (r = m.invoke((Object)this, ref, componentAccessor)) instanceof Boolean) {
                return (Boolean)r;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return true;
    }

    public void onDestroy() {
        for (int b = 0; b < this.l.size() - 1; ++b) {
            this.itemContainer.dropAllItemStacks();
            if (this.l.isEmpty() || this.l.size() <= b) continue;
            Ref<EntityStore> esx = this.l.get(0);
            this.l.remove(0);
            try {
                this.visualMap.remove(esx);
                this.visualSpawnTimes.remove(esx);
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (!esx.isValid()) continue;
            this.es.removeEntity(esx, RemoveReason.REMOVE);
        }
        try {
            Method m = this.getClass().getSuperclass().getMethod("onDestroy", new Class[0]);
            if (m != null) {
                m.invoke((Object)this, new Object[0]);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static boolean isProcessingBench(@Nullable BlockType bt) {
        return bt != null && bt.getState() != null;
    }

    @Override
    public void tick(float dt, int index, ArchetypeChunk<ChunkStore> archeChunk, Store<ChunkStore> store, CommandBuffer<ChunkStore> commandBuffer) {
        Store entities;
        block51: {
            boolean doImport;
            try {
                ((HytaleLogger.Api)HytaleLogger.getLogger().atWarning()).log("[Ev0Lib][DIAG] HopperProcessor.tick invoked for instance=" + String.valueOf(this) + " index=" + index);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            this.lastEngineTick = System.currentTimeMillis();
            REGISTERED_PROCESSORS.put(this, Boolean.TRUE);
            try {
                Object existing;
                Object ref = this.getRefFromArchetype(archeChunk, index);
                if (ref != null && (existing = this.getHopperComponent(store, ref)) == null && this.data != null) {
                    HopperComponent hc = new HopperComponent();
                    hc.data = this.data;
                    this.putHopperComponent(store, ref, hc);
                    this.data = null;
                }
            }
            catch (Throwable ref) {
                // empty catch block
            }
            this.w = ((ChunkStore)store.getExternalData()).getWorld();
            this.es = entities = this.w.getEntityStore().getStore();
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
            Vector3i pos = this.getBlockPosition();
            this.nearbyBuffer.clear();
            ++this.tickCounter;
            int phase = this.tickCounter % 180;
            boolean doExport = phase == 0;
            boolean bl2 = doImport = phase == 90;
            if (this.tickCounter % 180 == 0) {
                try {
                    List rawPlayers = SpatialResource.getThreadLocalReferenceList();
                    Vector3d center = new Vector3d((double)pos.x, (double)pos.y, (double)pos.z);
                    ((SpatialResource)entities.getResource(EntityModule.get().getPlayerSpatialResourceType())).getSpatialStructure().collectCylinder(center, 4.0, (double)Math.max(1.0f, this.data.height), rawPlayers);
                    this.playersNearbyCached = !rawPlayers.isEmpty();
                    rawPlayers.clear();
                }
                catch (Exception rawPlayers) {
                    // empty catch block
                }
            }
            if (doExport) {
                ItemStack have = this.getItemContainer().getItemStack((short)0);
                if (have != null && this.playersNearbyCached) {
                    this.nearbyBuffer = HopperProcessor.getAllEntitiesInBox(this, pos, this.data.height, (ComponentAccessor<EntityStore>)entities, this.data.players, this.data.entities, this.data.items);
                } else {
                    this.nearbyBuffer.clear();
                }
                this.runExportPhase(pos, (Store<EntityStore>)entities);
            }
            if (doImport) {
                boolean hopperHasSpace;
                ItemStack have2 = this.getItemContainer().getItemStack((short)0);
                boolean bl3 = hopperHasSpace = have2 == null || have2.getQuantity() < 100;
                if (hopperHasSpace && this.playersNearbyCached) {
                    this.nearbyBuffer = HopperProcessor.getAllEntitiesInBox(this, pos, this.data.height, (ComponentAccessor<EntityStore>)entities, this.data.players, this.data.entities, this.data.items);
                } else {
                    this.nearbyBuffer.clear();
                }
                for (ConnectedBlockPatternRule.AdjacentSide side : this.data.importFaces) {
                    Vector3i importPos = new Vector3i(pos.x, pos.y, pos.z).add(WorldHelper.rotate((ConnectedBlockPatternRule.AdjacentSide)side, (int)this.getRotationIndex()).relativePosition);
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
                            this.itemContainer.addItemStackToSlot((short)0, bucketStack);
                            this.pendingFluidRemovals.add(new long[]{importPos.x, importPos.y, importPos.z});
                            continue;
                        }
                    }
                    HopperProcessor.perfInfo("[Hopper][Import] side=" + String.valueOf(side) + " importPos=" + String.valueOf(importPos) + " state=" + (state == null ? "null" : state.getClass().getSimpleName()) + " hasContainer=" + hasContainer);
                    if (this.tryImportFromContainer(chunk, importPos, (Store<EntityStore>)entities, side)) {
                        HopperProcessor.perfInfo("[Hopper][Import] tryImportFromContainer SUCCESS side=" + String.valueOf(side));
                        break;
                    }
                    HopperProcessor.perfInfo("[Hopper][Import] tryImportFromContainer failed, hasContainer=" + hasContainer + " -> will tryPickup=" + !hasContainer);
                    if (hasContainer || !this.tryPickupItemEntities(importPos, (Store<EntityStore>)entities)) continue;
                    HopperProcessor.perfInfo("[Hopper][Import] tryPickupItemEntities SUCCESS at " + String.valueOf(importPos));
                    this.runExportPhase(pos, (Store<EntityStore>)entities);
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
                    catch (Exception exception) {}
                }
            }
            try {
                if (this.es == null || this.visualSpawnTimes.isEmpty()) break block51;
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
            catch (Exception exception) {
                // empty catch block
            }
        }
        this.es = entities;
    }

    private boolean tryTransferToOrFromContainer(Object state, Vector3i pos, ConnectedBlockPatternRule.AdjacentSide side, Store<EntityStore> entities, boolean exportPhase) {
        ItemStack have;
        ItemContainer container;
        Object bench;
        if (state == null || (state == null || !state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState")) && !state.getClass().getSimpleName().contains("ItemContainer")) {
            return false;
        }
        boolean isProcessingBench = state != null && state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState");
        Object object = bench = isProcessingBench ? state : null;
        if (isProcessingBench) {
            ItemContainer output = this.getContainerFromItemContainerObject(this.getItemContainerFromState(bench), 2);
            if (!exportPhase) {
                for (int slot = 0; slot < output.getCapacity(); ++slot) {
                    int transferAmount;
                    ItemStack stack = output.getItemStack((short)slot);
                    if (stack == null) continue;
                    String probeKeyPb = null;
                    try {
                        probeKeyPb = stack.getBlockKey();
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    if (probeKeyPb == null) {
                        probeKeyPb = this.resolveItemStackKey(stack);
                    }
                    if (!this.isItemAllowedByFilter(probeKeyPb) || (transferAmount = (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)stack.getQuantity())) <= 0) continue;
                    ItemStack safeStack = stack.withQuantity(transferAmount);
                    ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short)0, safeStack);
                    if (!t.succeeded()) continue;
                    output.removeItemStackFromSlot((short)slot, transferAmount);
                    return true;
                }
            } else {
                ItemStack have2 = this.getItemContainer().getItemStack((short)0);
                if (have2 != null && have2.getQuantity() > 0) {
                    int transferAmount = (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)have2.getQuantity());
                    ItemStack safeStack = have2.withQuantity(transferAmount);
                    for (int c = 0; c <= 1; ++c) {
                        ItemContainer input = this.getContainerFromItemContainerObject(this.getItemContainerFromState(bench), c);
                        for (int slot = 0; slot < input.getCapacity(); ++slot) {
                            ItemStackSlotTransaction t2 = input.addItemStackToSlot((short)slot, safeStack);
                            if (!t2.succeeded()) continue;
                            this.spawnVisualFor(safeStack, exportPhase, pos, side, entities);
                            this.getItemContainer().removeItemStackFromSlot((short)0, transferAmount);
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        Object containerObj = this.getItemContainerFromState(state);
        ItemContainer itemContainer = container = containerObj instanceof ItemContainer ? (ItemContainer)containerObj : null;
        if (SIMPLE_DRAWERS_PRESENT && container instanceof IDrawerContainer) {
            IDrawerContainer drawerContainer = (IDrawerContainer)container;
            if (!exportPhase) {
                for (short slot = 0; slot < drawerContainer.getSlotCount(); slot = (short)(slot + 1)) {
                    ItemStackSlotTransaction t;
                    int transferAmount;
                    ItemStack slotItem = drawerContainer.getSlotItem(slot);
                    int slotQty = drawerContainer.getSlotQuantity(slot);
                    if (slotItem == null || slotQty <= 0) continue;
                    String probeKey = null;
                    try {
                        probeKey = slotItem.getBlockKey();
                    }
                    catch (Throwable t2) {
                        // empty catch block
                    }
                    if (probeKey == null) {
                        probeKey = this.resolveItemStackKey(slotItem);
                    }
                    if (!this.isItemAllowedByFilter(probeKey) || (transferAmount = (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)slotQty)) <= 0 || !(t = this.getItemContainer().addItemStackToSlot((short)0, slotItem.withQuantity(transferAmount))).succeeded()) continue;
                    short fSlot = slot;
                    int fNewQty = slotQty - transferAmount;
                    ItemStack fSlotItem = slotItem;
                    drawerContainer.writeAction(() -> {
                        drawerContainer.setSlot(fSlot, fSlotItem.withQuantity(fNewQty));
                        return null;
                    });
                    return true;
                }
                return false;
            }
            ItemStack have3 = this.getItemContainer().getItemStack((short)0);
            if (have3 != null && have3.getQuantity() > 0) {
                int room;
                int actualTransfer;
                int slotCap;
                int slotQty;
                ItemStack slotItem;
                short slot;
                int transferAmount = (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)have3.getQuantity());
                short matchedSlot = -1;
                int matchedQty = 0;
                int matchedCap = 0;
                for (slot = 0; slot < drawerContainer.getSlotCount(); slot = (short)(slot + 1)) {
                    slotItem = drawerContainer.getSlotItem(slot);
                    if (ItemStack.isEmpty((ItemStack)slotItem) || !slotItem.isStackableWith(have3)) continue;
                    slotQty = drawerContainer.getSlotQuantity(slot);
                    slotCap = drawerContainer.getSlotStackCapacity(slot);
                    if (slotCap - slotQty <= 0 || drawerContainer.testCantAddToSlot(slot, have3, slotItem)) continue;
                    matchedSlot = slot;
                    matchedQty = slotQty;
                    matchedCap = slotCap;
                    break;
                }
                if (matchedSlot == -1) {
                    for (slot = 0; slot < drawerContainer.getSlotCount(); slot = (short)(slot + 1)) {
                        slotItem = drawerContainer.getSlotItem(slot);
                        if (!ItemStack.isEmpty((ItemStack)slotItem)) continue;
                        slotQty = drawerContainer.getSlotQuantity(slot);
                        slotCap = drawerContainer.getSlotStackCapacity(slot);
                        if (slotCap - slotQty <= 0) continue;
                        matchedSlot = slot;
                        matchedQty = slotQty;
                        matchedCap = slotCap;
                        break;
                    }
                }
                if (matchedSlot != -1 && (actualTransfer = Math.min(transferAmount, room = matchedCap - matchedQty)) > 0) {
                    short fSlot = matchedSlot;
                    int fNewQty = matchedQty + actualTransfer;
                    int fActual = actualTransfer;
                    ItemStack fHave = have3;
                    drawerContainer.writeAction(() -> {
                        drawerContainer.setSlot(fSlot, fHave.withQuantity(fNewQty));
                        return null;
                    });
                    this.spawnVisualFor(have3.withQuantity(fActual), exportPhase, pos, side, entities);
                    this.getItemContainer().removeItemStackFromSlot((short)0, fActual);
                    return true;
                }
            }
            return false;
        }
        if (!exportPhase) {
            for (int slot = 0; slot < container.getCapacity(); ++slot) {
                int transferAmount;
                ItemStack stack = container.getItemStack((short)slot);
                if (stack == null) continue;
                String probeKey = null;
                try {
                    probeKey = stack.getBlockKey();
                }
                catch (Throwable matchedSlot) {
                    // empty catch block
                }
                if (probeKey == null) {
                    probeKey = this.resolveItemStackKey(stack);
                }
                if (!this.isItemAllowedByFilter(probeKey) || (transferAmount = (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)stack.getQuantity())) <= 0) continue;
                ItemStack safeStack = stack.withQuantity(transferAmount);
                ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short)0, safeStack);
                if (!t.succeeded()) continue;
                container.removeItemStackFromSlot((short)slot, transferAmount);
                return true;
            }
        }
        if (exportPhase && (have = this.getItemContainer().getItemStack((short)0)) != null && have.getQuantity() > 0) {
            int transferAmount = (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)have.getQuantity());
            ItemStack safeStack = have.withQuantity(transferAmount);
            for (int slot = 0; slot < container.getCapacity(); ++slot) {
                ItemStackSlotTransaction t = container.addItemStackToSlot((short)slot, safeStack);
                if (!t.succeeded()) continue;
                this.spawnVisualFor(safeStack, exportPhase, pos, side, entities);
                this.getItemContainer().removeItemStackFromSlot((short)0, transferAmount);
                return true;
            }
        }
        return false;
    }

    private void spawnVisualFor(ItemStack safeStack, boolean exportPhase, Vector3i pos, ConnectedBlockPatternRule.AdjacentSide side, Store<EntityStore> entities) {
        List<Ref<EntityStore>> nearby;
        Vector3d velocity;
        if (safeStack == null || safeStack.isEmpty()) {
            return;
        }
        Vector3i rel = WorldHelper.rotate((ConnectedBlockPatternRule.AdjacentSide)side, (int)this.getRotationIndex()).relativePosition;
        Vector3i hopperBlock = this.getBlockPosition();
        Vector3d hopperCenter = new Vector3d((double)hopperBlock.x + 0.5 + 1.0, (double)hopperBlock.y + 0.5, (double)hopperBlock.z + 0.5);
        Vector3d sourceCenter = new Vector3d((double)pos.x + 0.5 + 1.0, (double)pos.y + 0.5, (double)pos.z + 0.5);
        Vector3d spawnPos = exportPhase ? hopperCenter : sourceCenter;
        Vector3d vector3d = velocity = exportPhase ? new Vector3d((double)(-rel.x) * 0.35, 0.25, (double)(-rel.z) * 0.35) : new Vector3d((double)rel.x * 0.35, 0.25, (double)rel.z * 0.35);
        if (exportPhase && !(nearby = this.nearbyBuffer).isEmpty()) {
            boolean anySpawned = false;
            for (Ref<EntityStore> targetRef : nearby) {
                Ref<EntityStore> rs;
                if ((rs = ItemUtilsExtended.throwItem(this.getBlockType().getId(), switch (side.toString()) {
                    case "East" -> "West";
                    case "West" -> "East";
                    case "North" -> "South";
                    case "South" -> "North";
                    case "Up" -> "Down";
                    case "Down" -> "Up";
                    default -> side.toString();
                }, new Vector3d((double)hopperBlock.x, (double)hopperBlock.y, (double)hopperBlock.z), targetRef, entities, safeStack, Vector3d.ZERO, 0.0f)) == null) continue;
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
        if (this.nearbyBuffer.isEmpty()) {
            return;
        }
        Holder itemEntityHolder = ItemComponent.generateItemDrop(entities, (ItemStack)safeStack, (Vector3d)new Vector3d(spawnPos.x, spawnPos.y, spawnPos.z), (Vector3f)Vector3f.ZERO, (float)0.0f, (float)-1.0f, (float)0.0f);
        if (itemEntityHolder == null) {
            return;
        }
        ItemComponent itemComponent = (ItemComponent)itemEntityHolder.getComponent(ItemComponent.getComponentType());
        if (itemComponent != null) {
            itemComponent.setPickupDelay(1.0E8f);
            itemComponent.setRemovedByPlayerPickup(false);
            itemComponent.computeDynamicLight();
        }
        try {
            ((PhysicsValues)itemEntityHolder.ensureAndGetComponent(PhysicsValues.getComponentType())).replaceValues(new PhysicsValues(0.0, 0.0, true));
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

    private void runExportPhase(Vector3i pos, Store<EntityStore> entities) {
        ItemStack currentItemFast = this.getItemContainer().getItemStack((short)0);
        if (currentItemFast == null) {
            return;
        }
        for (ConnectedBlockPatternRule.AdjacentSide side : this.data.exportFaces) {
            boolean transferred;
            String itemKey;
            Vector3i exportPos = new Vector3i(pos.x, pos.y, pos.z).add(WorldHelper.rotate((ConnectedBlockPatternRule.AdjacentSide)side, (int)this.getRotationIndex()).relativePosition);
            WorldChunk chunk = this.w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock((int)exportPos.x, (int)exportPos.z));
            if (chunk == null) continue;
            Object state = EngineCompat.getState(chunk, exportPos.x, exportPos.y, exportPos.z);
            int targetFluidId = EngineCompat.getFluidId(chunk, exportPos.x, exportPos.y, exportPos.z);
            boolean hasContainer = state != null && (state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState") || state.getClass().getSimpleName().contains("ItemContainer"));
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
                    this.itemContainer.removeItemStackFromSlot((short)0, 1);
                    this.itemContainer.addItemStackToSlot((short)0, new ItemStack("Container_Bucket", 1, null));
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

    private boolean handleExport(World world, Store<EntityStore> entities) {
        ItemStack source = this.getItemContainer().getItemStack((short)0);
        if (source == null) {
            return false;
        }
        int transferAmount = (int)Math.min(this.data.tier * 2.0f, (float)source.getQuantity());
        if (transferAmount <= 0) {
            return false;
        }
        Vector3i basePos = this.getBlockPosition();
        BlockPosition pos = world.getBaseBlock(new BlockPosition(basePos.x, basePos.y, basePos.z));
        for (ConnectedBlockPatternRule.AdjacentSide side : this.data.exportFaces) {
            Vector3i targetPos = new Vector3i(pos.x, pos.y, pos.z).add(WorldHelper.rotate((ConnectedBlockPatternRule.AdjacentSide)side, (int)this.getRotationIndex()).relativePosition);
            WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock((int)targetPos.x, (int)targetPos.z));
            if (chunk == null) continue;
            if (this.tryExportToContainer(chunk, targetPos, source, transferAmount)) {
                return true;
            }
            if (!this.tryExportToWorld(chunk, targetPos, source, transferAmount)) continue;
            return true;
        }
        return false;
    }

    private boolean tryExportToContainer(WorldChunk chunk, Vector3i pos, ItemStack source, int amount) {
        Object state = EngineCompat.getState(chunk, pos.x, pos.y, pos.z);
        if (state != null && state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState")) {
            Object bench = state;
            ItemStack sourcex = this.getItemContainer().getItemStack((short)0);
            if (sourcex == null) {
                return false;
            }
            int transferAmount = (int)Math.min(this.data.tier * 2.0f, (float)sourcex.getQuantity());
            if (transferAmount <= 0) {
                return false;
            }
            ItemStack safeStack = sourcex.withQuantity(transferAmount);
            if (!this.isItemAllowedByFilter(safeStack.getBlockKey())) {
                return false;
            }
            for (int c = 0; c <= 1; ++c) {
                ItemContainer input = this.getContainerFromItemContainerObject(this.getItemContainerFromState(bench), c);
                if (input == null) continue;
                for (int slot = 0; slot < input.getCapacity(); ++slot) {
                    ItemStackSlotTransaction t = input.addItemStackToSlot((short)slot, safeStack);
                    if (!t.succeeded()) continue;
                    this.getItemContainer().removeItemStackFromSlot((short)0, transferAmount);
                    return true;
                }
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
        ItemContainer target = (ItemContainer)containerStateObj;
        for (int slot = 0; slot < target.getCapacity(); ++slot) {
            if (!this.isItemAllowedByFilter(source.getBlockKey())) {
                return false;
            }
            ItemStackSlotTransaction t = target.addItemStackToSlot((short)slot, source.withQuantity(amount));
            if (!t.succeeded()) continue;
            this.getItemContainer().removeItemStackFromSlot((short)0, amount);
            return true;
        }
        return false;
    }

    private boolean tryExportToWorld(WorldChunk chunk, Vector3i pos, ItemStack source, int amount) {
        int fluidId;
        if (!this.isItemAllowedByFilter(source.getBlockKey())) {
            return false;
        }
        if (EngineCompat.getBlockType(chunk, pos.x, pos.y, pos.z) == null && (fluidId = EngineCompat.getFluidId(chunk, pos.x, pos.y, pos.z)) != 0) {
            block16: {
                EngineCompat.setBlock(chunk, pos.x, pos.y, pos.z, source.getBlockKey());
                this.getItemContainer().removeItemStackFromSlot((short)0, amount);
                try {
                    if (this.l == null || this.l.isEmpty() || this.es == null) break block16;
                    Iterator<Ref<EntityStore>> it = this.l.iterator();
                    while (it.hasNext()) {
                        Ref<EntityStore> ref = it.next();
                        try {
                            if (ref != null && ref.isValid()) {
                                TransformComponent tc = (TransformComponent)this.es.getComponent(ref, TransformComponent.getComponentType());
                                if (tc == null) continue;
                                Vector3d p = tc.getPosition();
                                if (!(Math.abs(p.x - ((double)pos.x + 0.5)) < 0.6) || !(Math.abs(p.y - ((double)pos.y + 0.5)) < 0.6) || !(Math.abs(p.z - ((double)pos.z + 0.5)) < 0.6)) continue;
                                it.remove();
                                try {
                                    this.visualMap.remove(ref);
                                }
                                catch (Exception exception) {
                                    // empty catch block
                                }
                                try {
                                    this.visualSpawnTimes.remove(ref);
                                }
                                catch (Exception exception) {
                                    // empty catch block
                                }
                                try {
                                    this.es.removeEntity(ref, RemoveReason.REMOVE);
                                }
                                catch (Exception exception) {}
                                continue;
                            }
                            it.remove();
                            try {
                                this.visualMap.remove(ref);
                                this.visualSpawnTimes.remove(ref);
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
            return true;
        }
        return false;
    }

    private boolean handleImport(World world, Store<EntityStore> entities) {
        Vector3i basePos = this.getBlockPosition();
        BlockPosition pos = world.getBaseBlock(new BlockPosition(basePos.x, basePos.y, basePos.z));
        for (ConnectedBlockPatternRule.AdjacentSide side : this.data.importFaces) {
            Vector3i targetPos = new Vector3i(pos.x, pos.y, pos.z).add(WorldHelper.rotate((ConnectedBlockPatternRule.AdjacentSide)side, (int)this.getRotationIndex()).relativePosition);
            WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock((int)targetPos.x, (int)targetPos.z));
            if (chunk == null || !this.tryImportFromContainer(chunk, targetPos, entities, side)) continue;
            return true;
        }
        return false;
    }

    /*
     * WARNING - void declaration
     */
    private boolean tryImportFromContainer(WorldChunk chunk, Vector3i pos, Store<EntityStore> entities, ConnectedBlockPatternRule.AdjacentSide side) {
        ItemStack destStack = this.getItemContainer().getItemStack((short)0);
        if (destStack != null && destStack.getQuantity() >= 100) {
            return false;
        }
        Object state = EngineCompat.getState(chunk, pos.x, pos.y, pos.z);
        if (state != null && state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState")) {
            ItemContainer output = this.getContainerFromItemContainerObject(this.getItemContainerFromState(state), 2);
            if (output == null) {
                return false;
            }
            int outCap = output.getCapacity();
            for (int slot = 0; slot < outCap; ++slot) {
                Ref<EntityStore> esx;
                ItemStackSlotTransaction t;
                int transferAmount;
                ItemStack stack = output.getItemStack((short)slot);
                if (stack == null) continue;
                String blockKey = null;
                try {
                    blockKey = stack.getBlockKey();
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                if (blockKey == null) {
                    blockKey = this.resolveItemStackKey(stack);
                }
                if (!this.isItemAllowedByFilter(blockKey) || (transferAmount = (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)stack.getQuantity())) <= 0 || !(t = this.getItemContainer().addItemStackToSlot((short)0, stack.withQuantity(transferAmount))).succeeded()) continue;
                Vector3i relRot = WorldHelper.rotate((ConnectedBlockPatternRule.AdjacentSide)side, (int)this.getRotationIndex()).relativePosition;
                Vector3d velRot = new Vector3d((double)relRot.x * 0.35, 0.25, (double)relRot.z * 0.35);
                Vector3i hopperBlock = this.getBlockPosition();
                Vector3d vector3d = new Vector3d((double)hopperBlock.x + 0.5, (double)hopperBlock.y + 0.5, (double)hopperBlock.z + 0.5);
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
        if (state instanceof HopperProcessor) {
            HopperProcessor otherHopper = (HopperProcessor)state;
            int otherCap = otherHopper.getItemContainer().getCapacity();
            for (int n = 0; n < otherCap; ++n) {
                ItemStack taken;
                ItemStackSlotTransaction t;
                ItemStack otherStack = otherHopper.getItemContainer().getItemStack((short)n);
                if (otherStack == null) continue;
                String otherKey = null;
                try {
                    otherKey = otherStack.getBlockKey();
                }
                catch (Throwable transferAmount) {
                    // empty catch block
                }
                if (otherKey == null) {
                    otherKey = this.resolveItemStackKey(otherStack);
                }
                if (!this.isItemAllowedByFilter(otherKey) || (transferAmount = (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)otherStack.getQuantity())) <= 0 || !(t = this.getItemContainer().addItemStackToSlot((short)0, otherStack.withQuantity(transferAmount))).succeeded()) continue;
                if (!this.nearbyBuffer.isEmpty() && (taken = this.getItemContainer().getItemStack((short)0)) != null && !taken.isEmpty()) {
                    for (Ref<EntityStore> targetRef : this.nearbyBuffer) {
                        Ref<EntityStore> ref = ItemUtilsExtended.throwItem(this.getBlockType().getId(), side.toString(), new Vector3d((double)pos.x, (double)pos.y, (double)pos.z), targetRef, entities, taken, Vector3d.ZERO, 0.0f);
                        if (ref == null) continue;
                        this.l.add(ref);
                        try {
                            this.visualMap.put(ref, taken);
                            Instant now = this.es != null ? ((WorldTimeResource)this.es.getResource(WorldTimeResource.getResourceType())).getGameTime() : Instant.now();
                            this.visualSpawnTimes.put(ref, now);
                        }
                        catch (Exception now) {}
                    }
                }
                otherHopper.getItemContainer().removeItemStackFromSlot((short)n, transferAmount);
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
        ItemContainer sourceContainer = (ItemContainer)containerStateObj;
        if (SIMPLE_DRAWERS_PRESENT && sourceContainer instanceof IDrawerContainer) {
            IDrawerContainer drawerContainer = (IDrawerContainer)sourceContainer;
            for (short slot = 0; slot < drawerContainer.getSlotCount(); slot = (short)(slot + 1)) {
                int transferAmount;
                ItemStack slotItem = drawerContainer.getSlotItem(slot);
                int slotQty = drawerContainer.getSlotQuantity(slot);
                if (slotItem == null || slotQty <= 0) continue;
                String probeKey2 = null;
                try {
                    probeKey2 = slotItem.getBlockKey();
                }
                catch (Throwable taken) {
                    // empty catch block
                }
                if (probeKey2 == null) {
                    probeKey2 = this.resolveItemStackKey(slotItem);
                }
                if (!this.isItemAllowedByFilter(probeKey2) || (transferAmount = (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)slotQty)) <= 0) continue;
                ItemStack safeStack = slotItem.withQuantity(transferAmount);
                ItemStackSlotTransaction t2 = this.getItemContainer().addItemStackToSlot((short)0, safeStack);
                if (!t2.succeeded()) continue;
                if (!this.nearbyBuffer.isEmpty()) {
                    void var19_86;
                    String now = side.toString();
                    int n = -1;
                    switch (now.hashCode()) {
                        case 2152477: {
                            if (!now.equals("East")) break;
                            boolean bl = false;
                            break;
                        }
                        case 2692559: {
                            if (!now.equals("West")) break;
                            boolean bl = true;
                            break;
                        }
                        case 75454693: {
                            if (!now.equals("North")) break;
                            int n2 = 2;
                            break;
                        }
                        case 80075181: {
                            if (!now.equals("South")) break;
                            int n3 = 3;
                            break;
                        }
                        case 2747: {
                            if (!now.equals("Up")) break;
                            int n4 = 4;
                            break;
                        }
                        case 2136258: {
                            if (!now.equals("Down")) break;
                            int n5 = 5;
                        }
                    }
                    switch (var19_86) {
                        case 0: {
                            String string = "West";
                            break;
                        }
                        case 1: {
                            String string = "East";
                            break;
                        }
                        case 2: {
                            String string = "South";
                            break;
                        }
                        case 3: {
                            String string = "North";
                            break;
                        }
                        case 4: {
                            String string = "Down";
                            break;
                        }
                        case 5: {
                            String exception = "Up";
                            break;
                        }
                        default: {
                            String string = side.toString();
                        }
                    }
                    for (Ref ref : this.nearbyBuffer) {
                        void var17_59;
                        Ref<EntityStore> rs = ItemUtilsExtended.throwItem(this.getBlockType().getId(), (String)var17_59, new Vector3d((double)pos.x, (double)pos.y, (double)pos.z), (Ref<EntityStore>)ref, entities, safeStack, Vector3d.ZERO, 0.0f);
                        if (rs == null) continue;
                        this.l.add(rs);
                        try {
                            this.visualMap.put(rs, safeStack);
                        }
                        catch (Exception exception) {}
                    }
                }
                short s = slot;
                int fNewQty = slotQty - transferAmount;
                ItemStack itemStack = slotItem;
                drawerContainer.writeAction(() -> {
                    drawerContainer.setSlot(fSlot, fSlotItem.withQuantity(fNewQty));
                    return null;
                });
                return true;
            }
            return false;
        }
        for (int slot = 0; slot < sourceContainer.getCapacity(); ++slot) {
            int transferAmount;
            ItemStack stack = sourceContainer.getItemStack((short)slot);
            if (stack == null) continue;
            String probeKey2 = null;
            try {
                probeKey2 = stack.getBlockKey();
            }
            catch (Throwable slotQty) {
                // empty catch block
            }
            if (probeKey2 == null) {
                probeKey2 = this.resolveItemStackKey(stack);
            }
            if (!this.isItemAllowedByFilter(probeKey2) || (transferAmount = (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)stack.getQuantity())) <= 0) continue;
            ItemStack safeStack = stack.withQuantity(transferAmount);
            ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short)0, safeStack);
            if (!t.succeeded()) continue;
            if (!this.nearbyBuffer.isEmpty()) {
                Ref<EntityStore> esx;
                void var17_68;
                String t2 = side.toString();
                int n = -1;
                switch (t2.hashCode()) {
                    case 2152477: {
                        if (!t2.equals("East")) break;
                        boolean bl = false;
                        break;
                    }
                    case 2692559: {
                        if (!t2.equals("West")) break;
                        boolean bl = true;
                        break;
                    }
                    case 75454693: {
                        if (!t2.equals("North")) break;
                        int n6 = 2;
                        break;
                    }
                    case 80075181: {
                        if (!t2.equals("South")) break;
                        int n7 = 3;
                        break;
                    }
                    case 2747: {
                        if (!t2.equals("Up")) break;
                        int n8 = 4;
                        break;
                    }
                    case 2136258: {
                        if (!t2.equals("Down")) break;
                        int n9 = 5;
                    }
                }
                String oppSide = switch (var17_68) {
                    case 0 -> "West";
                    case 1 -> "East";
                    case 2 -> "South";
                    case 3 -> "North";
                    case 4 -> "Down";
                    case 5 -> "Up";
                    default -> side.toString();
                };
                for (Ref ref : this.nearbyBuffer) {
                    Ref<EntityStore> rs = ItemUtilsExtended.throwItem(this.getBlockType().getId(), oppSide, new Vector3d((double)pos.x, (double)pos.y, (double)pos.z), (Ref<EntityStore>)ref, entities, safeStack, Vector3d.ZERO, 0.0f);
                    if (rs == null) continue;
                    this.l.add(rs);
                    try {
                        this.visualMap.put(rs, safeStack);
                    }
                    catch (Exception exception) {}
                }
                if (this.drop && !this.l.isEmpty() && (esx = this.l.getFirst()) != null && esx.isValid()) {
                    this.l.removeFirst();
                    try {
                        this.visualMap.remove(esx);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    entities.removeEntity(esx, RemoveReason.REMOVE);
                }
            }
            sourceContainer.removeItemStackFromSlot((short)slot, transferAmount);
            return true;
        }
        return false;
    }

    private boolean tryPickupItemEntities(Vector3i importPos, Store<EntityStore> entities) {
        HopperProcessor.perfInfo("[Hopper][Pickup] tryPickupItemEntities called at " + String.valueOf(importPos));
        List rawResults = SpatialResource.getThreadLocalReferenceList();
        Vector3d boxMin = new Vector3d((double)importPos.x, (double)importPos.y, (double)importPos.z);
        Vector3d boxMax = new Vector3d((double)importPos.x + 1.0, (double)importPos.y + 1.0, (double)importPos.z + 1.0);
        HopperProcessor.perfInfo("[Hopper][Pickup] collectBox min=" + String.valueOf(boxMin) + " max=" + String.valueOf(boxMax));
        ((SpatialResource)entities.getResource(EntityModule.get().getItemSpatialResourceType())).getSpatialStructure().collectBox(boxMin, boxMax, rawResults);
        HopperProcessor.perfInfo("[Hopper][Pickup] collectBox rawResults.size()=" + rawResults.size());
        if (rawResults.isEmpty()) {
            HopperProcessor.perfInfo("[Hopper][Pickup] no items found in box, returning false");
            return false;
        }
        ArrayList itemRefs = new ArrayList(rawResults);
        int hopperQty = this.getItemContainer().getItemStack((short)0) == null ? 0 : this.getItemContainer().getItemStack((short)0).getQuantity();
        HopperProcessor.perfInfo("[Hopper][Pickup] hopperQty=" + hopperQty + " itemRefs.size()=" + itemRefs.size());
        if (hopperQty >= 100) {
            HopperProcessor.perfInfo("[Hopper][Pickup] hopper full (qty=" + hopperQty + "), returning false");
            return false;
        }
        for (Ref ref : itemRefs) {
            ItemComponent ic;
            if (ref == null || !ref.isValid()) {
                HopperProcessor.perfInfo("[Hopper][Pickup] ref null or invalid, skipping");
                continue;
            }
            if (this.l.contains(ref)) {
                HopperProcessor.perfInfo("[Hopper][Pickup] ref=" + String.valueOf(ref) + " is own visual, skipping");
                continue;
            }
            if (entities.getComponent(ref, Intangible.getComponentType()) != null) {
                HopperProcessor.perfInfo("[Hopper][Pickup] ref=" + String.valueOf(ref) + " has Intangible (logging only, not skipping)");
            }
            if ((ic = (ItemComponent)entities.getComponent(ref, ItemComponent.getComponentType())) == null) {
                HopperProcessor.perfInfo("[Hopper][Pickup] ref=" + String.valueOf(ref) + " has no ItemComponent, skipping");
                continue;
            }
            if (!ic.canPickUp()) {
                HopperProcessor.perfInfo("[Hopper][Pickup] ref=" + String.valueOf(ref) + " canPickUp()=false (drop delay active), skipping");
                continue;
            }
            ItemStack stack = ic.getItemStack();
            if (stack == null || stack.isEmpty()) {
                HopperProcessor.perfInfo("[Hopper][Pickup] ref=" + String.valueOf(ref) + " stack is null or empty, skipping");
                continue;
            }
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
            HopperProcessor.perfInfo("[Hopper][Pickup] ref=" + String.valueOf(ref) + " itemKey=" + itemKey + " qty=" + stack.getQuantity());
            if (!this.isItemAllowedByFilter(itemKey)) {
                HopperProcessor.perfInfo("[Hopper][Pickup] ref=" + String.valueOf(ref) + " BLOCKED by filter (mode=" + this.filterMode + ")");
                continue;
            }
            int transferAmount = (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)Math.min(stack.getQuantity(), 100 - hopperQty));
            HopperProcessor.perfInfo("[Hopper][Pickup] transferAmount=" + transferAmount + " for " + itemKey);
            if (transferAmount <= 0) {
                HopperProcessor.perfInfo("[Hopper][Pickup] transferAmount<=0, skipping");
                continue;
            }
            ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short)0, stack.withQuantity(transferAmount));
            HopperProcessor.perfInfo("[Hopper][Pickup] addItemStackToSlot succeeded=" + t.succeeded() + " item=" + itemKey + " amount=" + transferAmount);
            if (!t.succeeded()) continue;
            int remaining = stack.getQuantity() - transferAmount;
            HopperProcessor.perfInfo("[Hopper][Pickup] SUCCESS item=" + itemKey + " transferred=" + transferAmount + " remaining=" + remaining);
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
        HopperProcessor.perfInfo("[Hopper][Pickup] no items collected at " + String.valueOf(importPos) + ", returning false");
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
    public static List<Ref<EntityStore>> getAllEntitiesInBox(HopperProcessor hp, Vector3i pos, float height, @Nonnull ComponentAccessor<EntityStore> components, boolean players, boolean entities, boolean items) {
        List results = SpatialResource.getThreadLocalReferenceList();
        Vector3d center = new Vector3d((double)pos.x, (double)pos.y, (double)pos.z);
        double queryHeight = Math.max(1.0f, height);
        if (entities) {
            // empty if block
        }
        if (players) {
            ((SpatialResource)components.getResource(EntityModule.get().getPlayerSpatialResourceType())).getSpatialStructure().collectCylinder(center, 4.0, queryHeight, results);
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

    public static List<Ref<EntityStore>> getAllItemsInBox(HopperProcessor hp, Vector3i pos, float height, @Nonnull ComponentAccessor<EntityStore> components, boolean players, boolean entities, boolean items) {
        List results = SpatialResource.getThreadLocalReferenceList();
        Vector3d center = new Vector3d((double)pos.x, (double)pos.y, (double)pos.z);
        double queryHeight = Math.max(0.5f, height);
        if (entities) {
            Vector3d min = new Vector3d((double)pos.x - 0.5, (double)pos.y - 0.5, (double)pos.z - 0.5);
            Vector3d max = new Vector3d((double)pos.x + 0.5, (double)pos.y + 0.5, (double)pos.z + 0.5);
            ((SpatialResource)components.getResource(EntityModule.get().getEntitySpatialResourceType())).getSpatialStructure().collectBox(min, max, results);
        }
        if (players) {
            // empty if block
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

    public static ComponentType<EntityStore, BlockEntity> getComponentType() {
        ComponentRegistryProxy entityStoreRegistry = EntityModule.get().getEntityStoreRegistry();
        return EntityModule.get().getBlockEntityComponentType();
    }

    @Override
    @Nullable
    public WorldChunk getChunk() {
        try {
            Vector3i p = this.getPosition();
            if (p == null || this.w == null) {
                return null;
            }
            return this.w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock((int)p.x, (int)p.z));
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    @Override
    public Vector3i getPosition() {
        try {
            return this.getBlockPosition();
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    @Override
    public void invalidate() {
        this.invalidatedFlag = true;
        try {
            REGISTERED_PROCESSORS.remove(this);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            this.lastEngineTick = 0L;
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

    static {
        FALLBACK_SCHEDULER.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            for (HopperProcessor hp : REGISTERED_PROCESSORS.keySet()) {
                try {
                    long last = hp.lastEngineTick;
                    if (hp.invalidatedFlag) {
                        REGISTERED_PROCESSORS.remove(hp);
                        continue;
                    }
                    if (now - last <= 2000L) continue;
                    try {
                        hp.fallbackHeartbeat();
                    }
                    catch (Throwable throwable) {
                    }
                }
                catch (Throwable throwable) {}
            }
        }, 2L, 2L, TimeUnit.SECONDS);
        boolean found = false;
        try {
            Class.forName("voidbond.arcio.components.ArcioMechanismComponent");
            found = true;
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        ARCIO_PRESENT = found;
        found = false;
        try {
            Class.forName("net.crepe.inventory.IDrawerContainer");
            found = true;
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        SIMPLE_DRAWERS_PRESENT = found;
        QUERY = Query.and((Query[])new Query[]{FluidSection.getComponentType(), ChunkSection.getComponentType()});
        DEPENDENCIES = Set.of(new SystemDependency(Order.AFTER, FluidSystems.Ticking.class), new SystemDependency(Order.BEFORE, ChunkBlockTickSystem.Ticking.class));
    }

    public static class Data
    extends StateData {
        @Nonnull
        public static final BuilderCodec<Data> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Data.class, Data::new).append(new KeyedCodec("Force", (Codec)Codec.FLOAT), (o, v) -> {
            o.force = v.floatValue();
        }, o -> Float.valueOf(o.force)).documentation("How much force to push the mob with.").add()).append(new KeyedCodec("Height", (Codec)Codec.FLOAT), (o, v) -> {
            o.height = v.floatValue();
        }, o -> Float.valueOf(o.height)).documentation("How high should the conveyor search?").add()).append(new KeyedCodec("Tier", (Codec)Codec.FLOAT), (o, v) -> {
            o.tier = v.floatValue();
        }, o -> Float.valueOf(o.tier)).documentation("How high should the conveyor search?").add()).append(new KeyedCodec("Players", (Codec)Codec.BOOLEAN), (o, v) -> {
            o.players = v;
        }, o -> o.players).documentation("Should players be affected?").add()).append(new KeyedCodec("Items", (Codec)Codec.BOOLEAN), (o, v) -> {
            o.items = v;
        }, o -> o.items).documentation("Should items be affected?").add()).append(new KeyedCodec("Entities", (Codec)Codec.BOOLEAN), (o, v) -> {
            o.entities = v;
        }, o -> o.entities).documentation("Should entities be affected?").add()).appendInherited(new KeyedCodec("Output", ItemHandler.CODEC), (i, v) -> {
            i.output = v;
        }, i -> i.output, (o, p) -> {
            o.output = p.output;
        }).documentation("Provides the items to be inserted into the container.").add()).appendInherited(new KeyedCodec("ExportFaces", Codecs.SIDE_ARRAY), (i, v) -> {
            i.exportFaces = v;
        }, i -> i.exportFaces, (o, p) -> {
            o.exportFaces = p.exportFaces;
        }).documentation("The adjacent faces to attempt exporting into.").add()).appendInherited(new KeyedCodec("ExportOnce", (Codec)Codec.BOOLEAN), (i, v) -> {
            i.exportOnce = v;
        }, i -> i.exportOnce, (o, p) -> {
            o.exportOnce = p.exportOnce;
        }).documentation("Should the generator only export items to the first valid side that accepts items?").add()).append(new KeyedCodec("Substitutions", (Codec)Codec.STRING_ARRAY, true), (i, v) -> {
            i.substitutions = v;
        }, i -> i.substitutions).add()).appendInherited(new KeyedCodec("Cooldown", (Codec)ProtocolCodecs.RANGEF), (i, v) -> {
            i.duration = v;
        }, i -> i.duration, (o, p) -> {
            o.duration = p.duration;
        }).documentation("A range that determines the cooldown before the next item is generated.").add()).appendInherited(new KeyedCodec("ImportFaces", Codecs.SIDE_ARRAY), (i, v) -> {
            i.importFaces = v;
        }, i -> i.importFaces, (o, p) -> {
            o.importFaces = p.importFaces;
        }).documentation("A range that determines the cooldown before the next item is generated.").add()).build();
        public float tier = 1.0f;
        public float force = 1.0f;
        public boolean players = true;
        public boolean entities = true;
        public boolean items = true;
        public float height = 0.99f;
        public ItemHandler output = new IdOutput();
        public ConnectedBlockPatternRule.AdjacentSide[] exportFaces = new ConnectedBlockPatternRule.AdjacentSide[0];
        public ConnectedBlockPatternRule.AdjacentSide[] importFaces = new ConnectedBlockPatternRule.AdjacentSide[0];
        public String[] substitutions;
        public boolean exportOnce = true;
        public Rangef duration;
    }
}

