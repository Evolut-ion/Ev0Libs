/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.math.vector.Vector3d
 *  com.hypixel.hytale.math.vector.Vector3f
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
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
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
import com.hypixel.hytale.server.core.modules.entity.DespawnComponent;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.Intangible;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.state.TickableBlockState;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockPatternRule;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.FillerBlockUtil;
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
import java.util.UUID;
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
import org.Ev0Mods.plugin.api.component.FluidComponent;
import org.Ev0Mods.plugin.api.interactions.HopperInteraction;
import org.Ev0Mods.plugin.api.system.WirelessHopperPlaceSystem;
import org.Ev0Mods.plugin.api.system.WirelessRegistry;
import org.Ev0Mods.plugin.api.ui.HopperUIPage;
import org.Ev0Mods.plugin.api.util.ItemUtilsExtended;
import org.Ev0Mods.plugin.api.util.WirelessHelpers;
import org.Ev0Mods.plugin.api.util.WorldHelper;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3ic;
import voidbond.arcio.ArcioPlugin;
import voidbond.arcio.components.ArcioMechanismComponent;
import voidbond.arcio.components.BlockUUIDComponent;

public class HopperComponent
implements Component<ChunkStore>,
TickableBlockState {
    private static final boolean PERF_DEBUG = false;
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
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
    private int exportFaceCursor = 0;
    private List<Ref<EntityStore>> nearbyBuffer = new ArrayList<Ref<EntityStore>>();
    private static final ConcurrentHashMap<Class<?>, Method> ITEM_KEY_METHOD_CACHE = new ConcurrentHashMap();
    private static final ConcurrentHashMap<Class<?>, Method> GET_ITEM_CONTAINER_METHOD_CACHE = new ConcurrentHashMap();
    private static final ConcurrentHashMap<Class<?>, Method> GET_CONTAINER_FROM_ITEM_CONTAINER_METHOD_CACHE = new ConcurrentHashMap();
    private static final ConcurrentHashMap<String, Method> REFLECTION_METHOD_CACHE = new ConcurrentHashMap();
    static final String PROCESSING_BENCH_CLASS = "com.hypixel.hytale.builtin.crafting.component.ProcessingBenchBlock";
    private static final ConcurrentHashMap<Class<?>, Method> BENCH_INPUT_METHOD_CACHE = new ConcurrentHashMap();
    private static final ConcurrentHashMap<Class<?>, Method> BENCH_FUEL_METHOD_CACHE = new ConcurrentHashMap();
    private static final ConcurrentHashMap<Class<?>, Method> BENCH_OUTPUT_METHOD_CACHE = new ConcurrentHashMap();
    private static volatile Object BENCH_COMPONENT_TYPE = null;
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
    private transient ItemContainerBlockState itemContainerBlock;
    private transient ItemContainer itemContainer;
    private Vector3i cachedPosition = new Vector3i(0, 0, 0);
    public static final BuilderCodec<HopperComponent> CODEC;
    private static volatile List<Object> KNOWN_CONTAINER_COMP_TYPES;
    public static final boolean ARCIO_PRESENT;
    public static final boolean SIMPLE_DRAWERS_PRESENT;
    private boolean arcioInitialized = false;
    private String arcioMode = "IgnoreSignal";
    private boolean wirelessRegistered = false;
    private final List<String> whitelist = Collections.synchronizedList(new ArrayList());
    private final List<String> blacklist = Collections.synchronizedList(new ArrayList());
    private volatile String filterMode = "Off";
    private final Map<PlayerRef, String> typedBuffer = new ConcurrentHashMap<PlayerRef, String>();
    private String hopperType = "Normal";
    private String wirelessName = "";
    private int wirelessTargetX = Integer.MIN_VALUE;
    private int wirelessTargetY = Integer.MIN_VALUE;
    private int wirelessTargetZ = Integer.MIN_VALUE;
    String facadeBlockId = "";
    private int facadeConnectionMask = 0;
    private int facadeRotation = 0;
    private int facadeRotationX = 0;
    private int facadeRotationZ = 0;
    private transient Ref<EntityStore> facadeEntityRef = null;
    @Nonnull
    private static final Set<Dependency<ChunkStore>> DEPENDENCIES;
    private ComponentType<EntityStore, FluidComponent> fluidComponent;

    private static void perfInfo(String msg) {
    }

    public ItemContainer getItemContainer() {
        return this.itemContainer != null ? this.itemContainer : null;
    }

    public void setItemContainer(ItemContainer ic) {
        this.itemContainer = ic;
    }

    private ConnectedBlockPatternRule.AdjacentSide opposite(ConnectedBlockPatternRule.AdjacentSide side) {
        return switch (side) {
            case ConnectedBlockPatternRule.AdjacentSide.Up -> ConnectedBlockPatternRule.AdjacentSide.Down;
            case ConnectedBlockPatternRule.AdjacentSide.Down -> ConnectedBlockPatternRule.AdjacentSide.Up;
            case ConnectedBlockPatternRule.AdjacentSide.North -> ConnectedBlockPatternRule.AdjacentSide.South;
            case ConnectedBlockPatternRule.AdjacentSide.South -> ConnectedBlockPatternRule.AdjacentSide.North;
            case ConnectedBlockPatternRule.AdjacentSide.East -> ConnectedBlockPatternRule.AdjacentSide.West;
            case ConnectedBlockPatternRule.AdjacentSide.West -> ConnectedBlockPatternRule.AdjacentSide.East;
            default -> side;
        };
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

    private static List<Object> getKnownContainerComponentTypes() {
        if (KNOWN_CONTAINER_COMP_TYPES != null) {
            return KNOWN_CONTAINER_COMP_TYPES;
        }
        ArrayList<Object> types = new ArrayList<Object>();
        try {
            Class<?> cls = Class.forName("com.Ev0sMods.Ev0sWoodCutter.blockstates.FertilizerState");
            Field f = cls.getField("COMPONENT_TYPE");
            Object ct = f.get(null);
            if (ct != null) {
                types.add(ct);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        KNOWN_CONTAINER_COMP_TYPES = types;
        return types;
    }

    private ItemContainer getContainerViaECS(Vector3i pos) {
        if (this.w == null || pos == null) {
            return null;
        }
        Vector3i p = this.resolveNeighborForTransfer(pos);
        if (p == null) {
            return null;
        }
        try {
            long chunkIdx = ChunkUtil.indexChunkFromBlock(p.x, p.z);
            if (this.w.getChunkIfInMemory(chunkIdx) == null) return null;
            Store<ChunkStore> cs = this.w.getChunkStore().getStore();
            Ref<ChunkStore> chunkRef = this.w.getChunkStore().getChunkReference(chunkIdx);
            if (chunkRef == null) {
                return null;
            }
            BlockComponentChunk bcc = cs.getComponent(chunkRef, BlockComponentChunk.getComponentType());
            if (bcc == null) {
                return null;
            }
            Ref<ChunkStore> blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn(p.x, p.y, p.z));
            if (blockRef == null) {
                return null;
            }
            for (Object compType : HopperComponent.getKnownContainerComponentTypes()) {
                try {
                    Object containerObj;
                    Object comp = cs.getComponent(blockRef, (ComponentType)compType);
                    if (comp == null || !((containerObj = this.getItemContainerFromState(comp)) instanceof ItemContainer)) continue;
                    ItemContainer ic = (ItemContainer)containerObj;
                    return ic;
                }
                catch (Throwable throwable) {
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    private Vector3i resolveMasterPos(Vector3i pos) {
        try {
            if (this.w == null) {
                return pos;
            }
            WorldChunk wc = this.w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
            if (wc == null) {
                return pos;
            }
            Object blockChunk = null;
            for (String name : new String[]{"getBlockChunk", "blockChunk", "getBlocks", "blocks"}) {
                try {
                    Method m = wc.getClass().getMethod(name, new Class[0]);
                    blockChunk = m.invoke((Object)wc, new Object[0]);
                    if (blockChunk == null) continue;
                    break;
                }
                catch (Throwable m) {
                    // empty catch block
                }
            }
            if (blockChunk == null) {
                return pos;
            }
            Object section = null;
            for (String name : new String[]{"getSectionAtBlockY", "getSection", "sectionAtBlockY"}) {
                try {
                    Method m = blockChunk.getClass().getMethod(name, Integer.TYPE);
                    section = m.invoke(blockChunk, pos.y);
                    if (section == null) continue;
                    break;
                }
                catch (Throwable m) {
                    // empty catch block
                }
            }
            if (!(section instanceof BlockSection)) {
                return pos;
            }
            BlockSection bs = (BlockSection)section;
            int lx = (pos.x % 32 + 32) % 32;
            int ly = (pos.y % 32 + 32) % 32;
            int lz = (pos.z % 32 + 32) % 32;
            int fillerVal = bs.getFiller(lx, ly, lz);
            if (fillerVal == 0) {
                return pos;
            }
            return new Vector3i(pos.x - FillerBlockUtil.unpackX(fillerVal), pos.y - FillerBlockUtil.unpackY(fillerVal), pos.z - FillerBlockUtil.unpackZ(fillerVal));
        }
        catch (Throwable throwable) {
            return pos;
        }
    }

    private Vector3i resolveNeighborForTransfer(Vector3i facePos) {
        if (facePos == null) {
            return null;
        }
        Vector3i p = this.resolveMasterPos(facePos);
        if (this.w == null) {
            return p;
        }
        try {
            if (this.w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(p.x, p.z)) == null) return p;
            BlockPosition bp = this.w.getBaseBlock(new BlockPosition(p.x, p.y, p.z));
            if (bp != null) {
                return new Vector3i(bp.x, bp.y, bp.z);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return p;
    }

    private static int containerSlotQuantity(ItemContainer c, short slot) {
        if (c == null) {
            return 0;
        }
        try {
            ItemStack s = c.getItemStack(slot);
            return s == null ? 0 : s.getQuantity();
        }
        catch (Throwable ignored) {
            return 0;
        }
    }

    private Object getBenchBlockAtPos(Vector3i pos) {
        if (this.w == null || pos == null) {
            return null;
        }
        pos = this.resolveMasterPos(pos);
        try {
            if (BENCH_COMPONENT_TYPE == null) {
                try {
                    Class<?> cls = Class.forName(PROCESSING_BENCH_CLASS);
                    BENCH_COMPONENT_TYPE = cls.getMethod("getComponentType", new Class[0]).invoke(null, new Object[0]);
                }
                catch (Throwable ignored) {
                    return null;
                }
            }
            if (BENCH_COMPONENT_TYPE == null) {
                return null;
            }
            long chunkIdx = ChunkUtil.indexChunkFromBlock(pos.x, pos.z);
            if (this.w.getChunkIfInMemory(chunkIdx) == null) return null;
            Store<ChunkStore> cs = this.w.getChunkStore().getStore();
            Ref<ChunkStore> chunkRef = this.w.getChunkStore().getChunkReference(chunkIdx);
            if (chunkRef == null) {
                return null;
            }
            BlockComponentChunk bcc = cs.getComponent(chunkRef, BlockComponentChunk.getComponentType());
            if (bcc == null) {
                return null;
            }
            Ref<ChunkStore> blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn(pos.x, pos.y, pos.z));
            if (blockRef == null) {
                return null;
            }
            return cs.getComponent(blockRef, (ComponentType)BENCH_COMPONENT_TYPE);
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    private Vector3i correctBenchProbePos(Vector3i probe, ConnectedBlockPatternRule.AdjacentSide side, int rotationIndex) {
        if (probe == null) {
            return null;
        }
        try {
            Vector3i master = this.resolveMasterPos(probe);
            return master != null ? master : probe;
        }
        catch (Throwable ignored) {
            return probe;
        }
    }

    private ItemContainer benchInputContainer(Object bench) {
        return HopperComponent.benchNamedContainer(bench, "getInputContainer", BENCH_INPUT_METHOD_CACHE);
    }

    private ItemContainer benchFuelContainer(Object bench) {
        return HopperComponent.benchNamedContainer(bench, "getFuelContainer", BENCH_FUEL_METHOD_CACHE);
    }

    private ItemContainer benchOutputContainer(Object bench) {
        return HopperComponent.benchNamedContainer(bench, "getOutputContainer", BENCH_OUTPUT_METHOD_CACHE);
    }

    private static ItemContainer benchNamedContainer(Object bench, String methodName, ConcurrentHashMap<Class<?>, Method> cache) {
        if (bench == null) {
            return null;
        }
        Class<?> cls = bench.getClass();
        Method m = cache.computeIfAbsent(cls, k -> {
            try {
                return k.getMethod(methodName, new Class[0]);
            }
            catch (Throwable ignored) {
                return null;
            }
        });
        if (m == null) {
            return null;
        }
        try {
            Object r = m.invoke(bench, new Object[0]);
            if (r instanceof ItemContainer) {
                ItemContainer ic = (ItemContainer)r;
                return ic;
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
                return this.w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(this.cachedPosition.x, this.cachedPosition.z));
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
        // Normal hoppers have absolute faces from the asset pack data class — no rotation needed.
        // Wireless hoppers also use ri=0 since their faces are set directly on placement.
        return 0;
    }

    public BlockType getBlockType() {
        return BlockType.EMPTY;
    }

    private ConnectedBlockPatternRule.AdjacentSide detectAdjacentTransferFace() {
        if (this.cachedPosition == null || this.w == null) {
            return null;
        }
        ConnectedBlockPatternRule.AdjacentSide found = null;
        try {
            for (ConnectedBlockPatternRule.AdjacentSide side : ConnectedBlockPatternRule.AdjacentSide.values()) {
                try {
                    boolean hasTarget;
                    Vector3i rel = new Vector3i(side.relativePosition);
                    Vector3i facePos = new Vector3i(this.cachedPosition.x + ((Vector3i)((Object)rel)).x, this.cachedPosition.y + ((Vector3i)((Object)rel)).y, this.cachedPosition.z + ((Vector3i)((Object)rel)).z);
                    Vector3i targetPos = this.correctBenchProbePos(this.resolveNeighborForTransfer(facePos), side, 0);
                    if (targetPos == null) continue;
                    boolean bl = hasTarget = this.getBenchBlockAtPos(targetPos) != null || this.getContainerViaECS(targetPos) != null;
                    if (!hasTarget) {
                        WorldChunk targetChunk = this.w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(targetPos.x, targetPos.z));
                        Object state = targetChunk == null ? null : EngineCompat.getState(targetChunk, targetPos.x, targetPos.y, targetPos.z);
                        boolean bl2 = hasTarget = state != null && (state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState") || state.getClass().getSimpleName().contains("ItemContainer") || this.getItemContainerFromState(state) != null);
                    }
                    if (!hasTarget) continue;
                    if (found != null) {
                        return null;
                    }
                    found = side;
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return found;
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

    public String getHopperType() {
        return this.hopperType;
    }

    public void setHopperType(String t) {
        if (t == null) {
            return;
        }
        this.hopperType = t;
        if (this.data != null) {
            this.data.hopperType = t;
        }
    }

    public String getWirelessName() {
        return this.wirelessName;
    }

    public void setWirelessName(String n) {
        this.wirelessName = n == null ? "" : n;
        this.wirelessRegistered = false;
        if (this.data != null) {
            this.data.wirelessName = this.wirelessName;
        }
    }

    public boolean hasWirelessTarget() {
        return this.wirelessTargetY != Integer.MIN_VALUE;
    }

    public Vector3i getWirelessTarget() {
        return this.wirelessTargetY == Integer.MIN_VALUE ? null : new Vector3i(this.wirelessTargetX, this.wirelessTargetY, this.wirelessTargetZ);
    }

    public void setWirelessTarget(Vector3i pos) {
        if (pos == null) {
            this.clearWirelessTarget();
            return;
        }
        this.wirelessTargetX = pos.x;
        this.wirelessTargetY = pos.y;
        this.wirelessTargetZ = pos.z;
        if (this.data != null) {
            this.data.setWirelessTarget(pos);
        }
    }

    public void clearWirelessTarget() {
        this.wirelessTargetX = Integer.MIN_VALUE;
        this.wirelessTargetY = Integer.MIN_VALUE;
        this.wirelessTargetZ = Integer.MIN_VALUE;
        if (this.data != null) {
            this.data.setWirelessTarget(null);
        }
    }

    public String getFacadeBlockId() {
        return this.facadeBlockId;
    }

    public void setFacadeBlockId(String id) {
        this.facadeBlockId = id == null ? "" : id;
        this.facadeConnectionMask = 0;
        this.facadeRotation = 0;
        this.facadeRotationX = 0;
        this.facadeRotationZ = 0;
    }

    public boolean hasFacade() {
        return !this.facadeBlockId.isEmpty();
    }

    public int getFacadeConnectionMask() {
        return this.facadeConnectionMask;
    }

    public void setFacadeConnectionMask(int mask) {
        this.facadeConnectionMask = mask;
    }

    public int getFacadeRotation() {
        return this.facadeRotation;
    }

    public void setFacadeRotation(int r) {
        this.facadeRotation = Math.floorMod(r, 4);
    }

    public int getFacadeRotationX() {
        return this.facadeRotationX;
    }

    public void setFacadeRotationX(int r) {
        this.facadeRotationX = Math.floorMod(r, 4);
    }

    public int getFacadeRotationZ() {
        return this.facadeRotationZ;
    }

    public void setFacadeRotationZ(int r) {
        this.facadeRotationZ = Math.floorMod(r, 4);
    }

    public void applyFacadeVisual(Vector3i pos, Store<EntityStore> entityStore) {
        Store<EntityStore> store;
        Store<EntityStore> store2 = store = entityStore != null ? entityStore : this.es;
        if (!this.hasFacade() || store == null || pos == null) {
            return;
        }
        if (!EngineCompat.isValidBlockKey(this.facadeBlockId)) {
            this.facadeBlockId = "";
            return;
        }
        this.despawnFacadeEntity(store);
        try {
            TimeResource timeRes = store.getResource(TimeResource.getResourceType());
            Vector3d center = new Vector3d((double)pos.x + 0.5, (double)pos.y, (double)pos.z + 0.5);
            Holder holder = BlockEntity.assembleDefaultBlockEntity((TimeResource)timeRes, (String)this.facadeBlockId, (Vector3d)center);
            if (holder == null) {
                Ev0Log.warn(LOGGER, "[Facade] assembleDefaultBlockEntity returned null for '" + this.facadeBlockId + "'");
                return;
            }
            try {
                holder.tryRemoveComponent(DespawnComponent.getComponentType());
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                holder.tryRemoveComponent(BoundingBox.getComponentType());
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                holder.ensureAndGetComponent(Intangible.getComponentType());
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (this.facadeRotation != 0 || this.facadeRotationX != 0 || this.facadeRotationZ != 0) {
                try {
                    float pitch = (float)this.facadeRotationX * 1.5707964f;
                    float yaw = (float)this.facadeRotation * 1.5707964f;
                    float roll = (float)this.facadeRotationZ * 1.5707964f;
                    Rotation3f rot = new Rotation3f(pitch, yaw, roll);
                    TransformComponent tc = (TransformComponent) holder.ensureAndGetComponent(TransformComponent.getComponentType());
                    if (tc != null) {
                        tc.setRotation(rot);
                    }
                    holder.addComponent(HeadRotation.getComponentType(), new HeadRotation(rot));
                    Ev0Log.info(LOGGER, "[Facade] rotation set (rad): pitch=" + pitch + " yaw=" + yaw + " roll=" + roll);
                }
                catch (Throwable ig) {
                    Ev0Log.warn(LOGGER, "[Facade] rotation set failed: " + String.valueOf(ig));
                }
            }
            this.facadeEntityRef = store.addEntity(holder, AddReason.SPAWN);
            Ev0Log.info(LOGGER, "[Facade] spawned BlockEntity '" + this.facadeBlockId + "' at " + String.valueOf(pos));
        }
        catch (Throwable t) {
            Ev0Log.warn(LOGGER, "[Facade] BlockEntity spawn failed at " + String.valueOf(pos) + ": " + String.valueOf(t));
        }
    }

    public void clearFacadeVisual(Vector3i pos, Store<EntityStore> entityStore) {
        Store<EntityStore> store = entityStore != null ? entityStore : this.es;
        this.despawnFacadeEntity(store);
    }

    private void despawnFacadeEntity(Store<EntityStore> store) {
        if (this.facadeEntityRef == null) {
            return;
        }
        try {
            if (store != null) {
                store.removeEntity(this.facadeEntityRef, RemoveReason.REMOVE);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        this.facadeEntityRef = null;
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

    public boolean isSingletonMode() {
        return "Singleton".equalsIgnoreCase(this.filterMode);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean isItemAllowedByFilter(String blockKey) {
        if (this.filterMode == null || this.filterMode.equalsIgnoreCase("Off") || this.filterMode.equalsIgnoreCase("Singleton")) {
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
            Class<?> cls = stack.getClass();
            Method m = ITEM_KEY_METHOD_CACHE.get(cls);
            if (m == null && !ITEM_KEY_METHOD_CACHE.containsKey(cls)) {
                Method found = null;
                for (String name : new String[]{"getItemId", "getItemKey", "getId", "getKey", "getName"}) {
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
        if (other.data != null) {
            HopperProcessor.Data d = new HopperProcessor.Data();
            d.tier = other.data.tier;
            d.force = other.data.force;
            d.players = other.data.players;
            d.entities = other.data.entities;
            d.items = other.data.items;
            d.height = other.data.height;
            d.output = other.data.output;
            d.exportFaces = other.data.exportFaces != null ? (ConnectedBlockPatternRule.AdjacentSide[])other.data.exportFaces.clone() : new ConnectedBlockPatternRule.AdjacentSide[]{};
            d.importFaces = other.data.importFaces != null ? (ConnectedBlockPatternRule.AdjacentSide[])other.data.importFaces.clone() : new ConnectedBlockPatternRule.AdjacentSide[]{};
            d.substitutions = other.data.substitutions;
            d.exportOnce = other.data.exportOnce;
            d.duration = other.data.duration;
            d.hopperType = other.data.hopperType;
            d.wirelessName = other.data.wirelessName;
            d.wirelessTargetX = other.data.wirelessTargetX;
            d.wirelessTargetY = other.data.wirelessTargetY;
            d.wirelessTargetZ = other.data.wirelessTargetZ;
            this.data = d;
        } else {
            this.data = null;
        }
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
        this.exportFaceCursor = other.exportFaceCursor;
        this.nearbyBuffer = new ArrayList<Ref<EntityStore>>(other.nearbyBuffer);
        this.cachedPosition = other.cachedPosition;
        this.arcioInitialized = other.arcioInitialized;
        this.arcioMode = other.arcioMode;
        this.whitelist.addAll(other.whitelist);
        this.blacklist.addAll(other.blacklist);
        this.filterMode = other.filterMode;
        this.typedBuffer.putAll(other.typedBuffer);
        this.hopperType = other.hopperType;
        this.wirelessName = other.wirelessName;
        this.wirelessTargetX = other.wirelessTargetX;
        this.wirelessTargetY = other.wirelessTargetY;
        this.wirelessTargetZ = other.wirelessTargetZ;
    }

    public void setOwnerId(Player ownerId) {
        this.ownerId = ownerId;
    }

    public Player getOwnerId() {
        return this.ownerId;
    }

    @Nullable
    public static String wirelessOwnerKey(@Nullable PlayerRef ref) {
        if (ref == null) {
            return null;
        }
        try {
            for (String m : new String[]{"getUuid", "getUUID", "getPlayerUuid", "getId"}) {
                try {
                    Method mm = ref.getClass().getMethod(m, new Class[0]);
                    Object v = mm.invoke((Object)ref, new Object[0]);
                    if (v == null) continue;
                    return v instanceof UUID ? ((UUID)v).toString() : String.valueOf(v);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            return ref.toString();
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    @Nullable
    private String resolveWirelessOwnerIdentity() {
        String fromRf = HopperComponent.wirelessOwnerKey(this.rf);
        if (fromRf != null && !fromRf.isBlank()) {
            return fromRf;
        }
        if (this.ownerId != null) {
            try {
                for (String m : new String[]{"getUuid", "getUUID"}) {
                    try {
                        Method mm = this.ownerId.getClass().getMethod(m, new Class[0]);
                        Object v = mm.invoke((Object)this.ownerId, new Object[0]);
                        if (v == null) continue;
                        return v instanceof UUID ? ((UUID)v).toString() : String.valueOf(v);
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
        return null;
    }

    public void onOpen(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl World world, @NonNullDecl Store<EntityStore> store) {
        if (ARCIO_PRESENT && !this.arcioInitialized) {
            this.ensureArcioComponents(world, null);
        }
        this.rf = store.getComponent(ref, PlayerRef.getComponentType());
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

    @Override
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
        block13: {
            this.clearFacadeVisual(this.cachedPosition, this.es);
            try {
                String wType;
                String string = wType = this.data != null && this.data.hopperType != null && !"Normal".equals(this.data.hopperType) ? this.data.hopperType : this.hopperType;
                if (!this.wirelessRegistered && !"WirelessExport".equalsIgnoreCase(wType) && !"WirelessImport".equalsIgnoreCase(wType)) break block13;
                try {
                    if (this.hasWirelessTarget() && this.w != null) {
                        Vector3i partner = this.getWirelessTarget();
                        WirelessHelpers.clearWirelessTargetOnly(this.w, partner);
                    }
                }
                catch (Throwable partner) {
                    // empty catch block
                }
                if (this.w != null) {
                    WirelessRegistry.unregister(this.w, this.cachedPosition);
                } else {
                    WirelessRegistry.unregister(this.cachedPosition);
                }
            }
            catch (Throwable wType) {
                // empty catch block
            }
        }
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
        block129: {
            boolean isWireless;
            boolean doImport;
            block127: {
                Vector3i probed2;
                this.lastEngineTick = System.currentTimeMillis();
                REGISTERED_COMPONENTS.put(this, Boolean.TRUE);
                if (this.data == null) {
                    this.data = new HopperProcessor.Data();
                }
                if ((this.data.hopperType == null || "Normal".equals(this.data.hopperType)) && this.hopperType != null && !"Normal".equals(this.hopperType)) {
                    this.data.hopperType = this.hopperType;
                } else if ((this.hopperType == null || "Normal".equals(this.hopperType)) && this.data.hopperType != null && !"Normal".equals(this.data.hopperType)) {
                    this.hopperType = this.data.hopperType;
                }
                Ref<ChunkStore> myRef = archeChunk.getReferenceTo(index);
                try {
                    this.w = store.getExternalData().getWorld();
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
                        this.resolvePosition(store, myRef);
                    }
                }
                catch (Throwable probed2_caught) {
                    // empty catch block
                }
                try {
                    Ev0Log.info(HytaleLogger.getLogger(), "[Ev0Lib] HopperComponent.tick invoked for index=" + index + " pos=" + String.valueOf(this.cachedPosition));
                }
                catch (Throwable probed3) {
                    // empty catch block
                }
                if (this.cachedPosition != null && (this.cachedPosition.x != 0 || this.cachedPosition.y != 0 || this.cachedPosition.z != 0) && this.w != null) {
                    String _wt0;
                    if ((this.data.hopperType == null || "Normal".equals(this.data.hopperType)) && (this.hopperType == null || "Normal".equals(this.hopperType))) {
                        try {
                            WorldChunk myChunk = this.w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(this.cachedPosition.x, this.cachedPosition.z));
                            Object state = null;
                            if (myChunk != null) {
                                state = EngineCompat.getState(myChunk, this.cachedPosition.x, this.cachedPosition.y, this.cachedPosition.z);
                            }
                            String detected = null;
                            if (state != null) {
                                String cn = state.getClass().getSimpleName();
                                if (cn.toLowerCase().contains("wireless_export") || cn.toLowerCase().contains("wirelessexport")) {
                                    detected = "WirelessExport";
                                } else if (cn.toLowerCase().contains("wireless_import") || cn.toLowerCase().contains("wirelessimport")) {
                                    detected = "WirelessImport";
                                }
                            }
                            if (detected != null) {
                                this.data.hopperType = detected;
                                this.hopperType = detected;
                            }
                            Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] type detection: hopperType=" + this.hopperType + " data.hopperType=" + (this.data != null ? this.data.hopperType : "null") + " stateClass=" + (state != null ? state.getClass().getSimpleName() : "null") + " pos=" + String.valueOf(this.cachedPosition));
                        }
                        catch (Throwable t) {
                            try {
                                Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] type detection exception: " + String.valueOf(t));
                            }
                            catch (Throwable state) {
                                // empty catch block
                            }
                        }
                    }
                    String string = _wt0 = this.data.hopperType != null && !"Normal".equals(this.data.hopperType) ? this.data.hopperType : this.hopperType;
                    if ("WirelessExport".equalsIgnoreCase(_wt0) || "WirelessImport".equalsIgnoreCase(_wt0)) {
                        boolean isWirelessExport = "WirelessExport".equalsIgnoreCase(_wt0);
                        boolean isWirelessImport = "WirelessImport".equalsIgnoreCase(_wt0);
                        boolean needsImportFace = isWirelessExport && (this.data.importFaces == null || this.data.importFaces.length == 0);
                        boolean needsExportFace = isWirelessImport && (this.data.exportFaces == null || this.data.exportFaces.length == 0);
                        boolean facesLikelySouthDefaultImport = isWirelessExport && this.data.importFaces != null && this.data.importFaces.length == 1 && this.data.importFaces[0] == ConnectedBlockPatternRule.AdjacentSide.South;
                        boolean facesLikelySouthDefaultExport = isWirelessImport && this.data.exportFaces != null && this.data.exportFaces.length == 1 && this.data.exportFaces[0] == ConnectedBlockPatternRule.AdjacentSide.South;
                        boolean pendingPlacementSelf = false;
                        int pendingPlacementNear = 0;
                        try {
                            pendingPlacementSelf = WirelessHopperPlaceSystem.PENDING_TARGET_BLOCKS.containsKey(this.cachedPosition);
                            for (ConnectedBlockPatternRule.AdjacentSide as : ConnectedBlockPatternRule.AdjacentSide.values()) {
                                Vector3i adj = new Vector3i(this.cachedPosition.x + ((Vector3i)((Object)as.relativePosition)).x, this.cachedPosition.y + ((Vector3i)((Object)as.relativePosition)).y, this.cachedPosition.z + ((Vector3i)((Object)as.relativePosition)).z);
                                if (!WirelessHopperPlaceSystem.PENDING_TARGET_BLOCKS.containsKey(adj)) continue;
                                pendingPlacementNear = 1;
                                break;
                            }
                        }
                        catch (Throwable throwable) {
                            // empty catch block
                        }
                        if (pendingPlacementSelf || pendingPlacementNear != 0) {
                            try {
                                ConnectedBlockPatternRule.AdjacentSide face = null;
                                for (ConnectedBlockPatternRule.AdjacentSide as : ConnectedBlockPatternRule.AdjacentSide.values()) {
                                    try {
                                        Vector3i adj = new Vector3i(this.cachedPosition.x + ((Vector3i)((Object)as.relativePosition)).x, this.cachedPosition.y + ((Vector3i)((Object)as.relativePosition)).y, this.cachedPosition.z + ((Vector3i)((Object)as.relativePosition)).z);
                                        Long placedAt = WirelessHopperPlaceSystem.PENDING_TARGET_BLOCKS.remove(adj);
                                        WirelessHopperPlaceSystem.PENDING_PLACEMENT_FACES.remove(adj);
                                        if (placedAt == null) continue;
                                        face = as;
                                        break;
                                    }
                                    catch (Throwable adj) {
                                        // empty catch block
                                    }
                                }
                                if (face == null) {
                                    WirelessHopperPlaceSystem.PENDING_TARGET_BLOCKS.remove(this.cachedPosition);
                                    WirelessHopperPlaceSystem.PENDING_PLACEMENT_FACES.remove(this.cachedPosition);
                                    face = this.detectAdjacentTransferFace();
                                }
                                if (face != null) {
                                    if (isWirelessImport && (needsExportFace || facesLikelySouthDefaultExport || pendingPlacementSelf || pendingPlacementNear != 0)) {
                                        this.data.exportFaces = new ConnectedBlockPatternRule.AdjacentSide[]{face};
                                    }
                                    if (isWirelessExport && (needsImportFace || facesLikelySouthDefaultImport || pendingPlacementSelf || pendingPlacementNear != 0)) {
                                        this.data.importFaces = new ConnectedBlockPatternRule.AdjacentSide[]{face};
                                    }
                                    try {
                                        Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] face configured: hopperSide=" + String.valueOf(face) + " hopperAt=" + String.valueOf(this.cachedPosition) + " for " + _wt0);
                                    }
                                    catch (Throwable throwable) {}
                                }
                            }
                            catch (Throwable face) {
                                // empty catch block
                            }
                        }
                    }
                }
                try {
                    block126: {
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
                            if (icbObj == null || !icbClass.isInstance(icbObj)) break block126;
                            this.itemContainerBlock = (ItemContainerBlockState)icbObj;
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
                    if (this.itemContainer != null) break block127;
                    probed2 = this.probeAndGetBlockPosition();
                    if ((probed2 == null || probed2.x == 0 && probed2.y == 0 && probed2.z == 0) && this.cachedPosition.x == 0 && this.cachedPosition.y == 0 && this.cachedPosition.z == 0) {
                        this.resolvePosition(store, myRef);
                    }
                    WorldChunk myChunk = null;
                    try {
                        if (this.w != null) {
                            myChunk = this.w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(this.cachedPosition.x, this.cachedPosition.z));
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
                            break block127;
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
                    this.itemContainer = new SimpleItemContainer((short)1);
                    try {
                        Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] created fallback SimpleItemContainer for hopper at pos=" + String.valueOf(this.cachedPosition));
                    }
                    catch (Throwable probed3) {}
                }
                catch (Throwable ignored) {
                    this.itemContainer = null;
                }
            }
            try {
                Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] itemContainerBlock=" + (this.itemContainerBlock != null) + " itemContainerPresent=" + (this.getItemContainer() != null));
            }
            catch (Throwable ignored) {
                // empty catch block
            }
            if (!this.pendingFluidRemovals.isEmpty()) {
                for (long[] coords : this.pendingFluidRemovals) {
                    try {
                        WorldChunk fc = this.w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock((int)coords[0], (int)coords[2]));
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
            if (!this.wirelessRegistered && this.w != null) {
                String wName;
                String wType = this.data != null && this.data.hopperType != null && !"Normal".equals(this.data.hopperType) ? this.data.hopperType : this.hopperType;
                String string = wName = this.data != null && this.data.wirelessName != null && !this.data.wirelessName.isBlank() ? this.data.wirelessName : this.wirelessName;
                if (("WirelessExport".equalsIgnoreCase(wType) || "WirelessImport".equalsIgnoreCase(wType)) && wName != null && !wName.isBlank()) {
                    try {
                        String ownerKey = this.resolveWirelessOwnerIdentity();
                        WirelessRegistry.register(this.w, this.cachedPosition, wName, wType, ownerKey);
                    }
                    catch (Throwable ownerKey) {
                        // empty catch block
                    }
                    this.wirelessRegistered = true;
                }
            }
            // If already registered but target not set, the partner chunk may have been unloaded
            // when attemptAutoLink ran. Retry the link whenever the partner chunk becomes available.
            // Throttled to every 20 ticks to avoid per-tick ECS/chunk lookups.
            if (this.wirelessRegistered && !this.hasWirelessTarget() && this.w != null && this.tickCounter % 20 == 0) {
                try {
                    String _wtRelink = this.data != null && this.data.hopperType != null && !"Normal".equals(this.data.hopperType) ? this.data.hopperType : this.hopperType;
                    if ("WirelessExport".equalsIgnoreCase(_wtRelink) || "WirelessImport".equalsIgnoreCase(_wtRelink)) {
                        String wNameRelink = this.data != null && this.data.wirelessName != null && !this.data.wirelessName.isBlank() ? this.data.wirelessName : this.wirelessName;
                        if (wNameRelink != null && !wNameRelink.isBlank()) {
                            WirelessRegistry.attemptRelinkIfChunkLoaded(this.w, this.cachedPosition, wNameRelink);
                        }
                    }
                }
                catch (Throwable ignored) {
                    // empty catch block
                }
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
            if (this.tickCounter == 1 && this.hasFacade() && !EngineCompat.isValidBlockKey(this.facadeBlockId)) {
                this.facadeBlockId = "";
            }
            int phase = this.tickCounter % 90;
            boolean doExport = phase == 0;
            boolean bl2 = doImport = phase == 45;
            if (this.tickCounter % 90 == 0) {
                block128: {
                    if (this.hasFacade() && this.w != null) {
                        try {
                            boolean refLost;
                            boolean bl3 = refLost = this.facadeEntityRef == null || !this.facadeEntityRef.isValid();
                            if (refLost) {
                                this.applyFacadeVisual(this.cachedPosition, this.es);
                            }
                        }
                        catch (Throwable ignored) {
                            if (this.facadeEntityRef != null && this.facadeEntityRef.isValid()) break block128;
                            this.applyFacadeVisual(this.cachedPosition, this.es);
                        }
                    }
                }
                try {
                    List rawPlayers = SpatialResource.getThreadLocalReferenceList();
                    Vector3d center = new Vector3d((double)this.cachedPosition.x, (double)this.cachedPosition.y, (double)this.cachedPosition.z);
                    this.es.getResource(EntityModule.get().getPlayerSpatialResourceType()).getSpatialStructure().collectCylinder(center, 4.0, Math.max(1.0f, this.data.height), rawPlayers);
                    this.playersNearbyCached = !rawPlayers.isEmpty();
                    rawPlayers.clear();
                }
                catch (Exception rawPlayers) {
                    // empty catch block
                }
            }
            try {
                Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] tickCounter=" + this.tickCounter + " phase=" + phase + " doExport=" + doExport + " doImport=" + doImport + " slot0=" + (this.getItemContainer() == null ? "null" : String.valueOf(this.getItemContainer().getItemStack((short)0))));
            }
            catch (Throwable rawPlayers) {
                // empty catch block
            }
            String _wt = this.data != null && this.data.hopperType != null && !"Normal".equals(this.data.hopperType) ? this.data.hopperType : this.hopperType;
            boolean bl4 = isWireless = "WirelessExport".equalsIgnoreCase(_wt) || "WirelessImport".equalsIgnoreCase(_wt);
            if (doExport) {
                if (this.getItemContainer() != null && this.getItemContainer().getItemStack((short)0) != null && (this.playersNearbyCached || isWireless)) {
                    this.nearbyBuffer = HopperComponent.getAllEntitiesInBox(this, this.cachedPosition, this.data.height, this.es, this.data.players, this.data.entities, this.data.items);
                } else {
                    this.nearbyBuffer.clear();
                }
                this.runExportPhase(this.cachedPosition, this.es);
            }
            if (doImport && !"WirelessImport".equalsIgnoreCase(_wt)) {
                ItemContainer impIc = this.getItemContainer();
                boolean hopperHasSpace = impIc == null || impIc.getItemStack((short)0) == null;
                if (hopperHasSpace && (this.playersNearbyCached || isWireless)) {
                    this.nearbyBuffer = HopperComponent.getAllEntitiesInBox(this, this.cachedPosition, this.data.height, this.es, this.data.players, this.data.entities, this.data.items);
                } else {
                    this.nearbyBuffer.clear();
                }
                for (ConnectedBlockPatternRule.AdjacentSide side : this.data.importFaces) {
                    Vector3i _ri = new Vector3i(WorldHelper.rotate((ConnectedBlockPatternRule.AdjacentSide)side, (int)this.getRotationIndex()).relativePosition);
                    Vector3i importFace = new Vector3i(this.cachedPosition.x + ((Vector3i)((Object)_ri)).x, this.cachedPosition.y + ((Vector3i)((Object)_ri)).y, this.cachedPosition.z + ((Vector3i)((Object)_ri)).z);
                    Vector3i importPos = this.resolveNeighborForTransfer(importFace);
                    importPos = this.correctBenchProbePos(importPos, side, this.getRotationIndex());
                    WorldChunk chunk = this.w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(importPos.x, importPos.z));
                    if (chunk == null) continue;
                    int targetFluidId = EngineCompat.getFluidId(chunk, importPos.x, importPos.y, importPos.z);
                    Object state = EngineCompat.getState(chunk, importPos.x, importPos.y, importPos.z);
                    boolean hasContainer = state != null && (state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState") || state.getClass().getSimpleName().contains("ItemContainer") || this.getItemContainerFromState(state) != null) || state == null && this.getContainerViaECS(importPos) != null;
                    ItemContainer currentIc = this.getItemContainer();
                    ItemStack currentItem = currentIc != null ? currentIc.getItemStack((short)0) : null;
                    if (Ev0Config.isFluidTransferEnabled() && targetFluidId != 0 && currentItem == null && !hasContainer) {
                        // Resolve the fluid by its persistent string id, not the numeric
                        // asset-map index which shifts between versions/mod sets.
                        String fluidKey = EngineCompat.getFluidKey(chunk, importPos.x, importPos.y, importPos.z);
                        String bucketKey = EngineCompat.filledBucketForFluid(fluidKey);
                        ItemStack bucketStack = bucketKey == null ? null : new ItemStack(bucketKey, 1, null);
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
                    HopperComponent.perfInfo("[Hopper][Import] side=" + String.valueOf((Object)side) + " importPos=" + String.valueOf(importPos) + " state=" + (state == null ? "null" : state.getClass().getSimpleName()) + " hasContainer=" + hasContainer);
                    if (this.tryImportFromContainer(chunk, importPos, this.es, side)) {
                        HopperComponent.perfInfo("[Hopper][Import] tryImportFromContainer SUCCESS side=" + String.valueOf((Object)side));
                        break;
                    }
                    HopperComponent.perfInfo("[Hopper][Import] tryImportFromContainer failed, hasContainer=" + hasContainer + " -> will tryPickup=" + !hasContainer);
                    if (hasContainer || !this.tryPickupItemEntities(importFace, this.es)) continue;
                    HopperComponent.perfInfo("[Hopper][Import] tryPickupItemEntities SUCCESS at " + String.valueOf(importFace));
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
                    catch (Exception pendingPlacementSelf) {}
                }
            }
            try {
                if (this.es == null || this.visualSpawnTimes.isEmpty()) break block129;
                Instant now = this.es.getResource(WorldTimeResource.getResourceType()).getGameTime();
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
        int n;
        try {
            Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] runExportPhase invoked; param pos=" + String.valueOf(pos) + " cachedPos=" + String.valueOf(this.getBlockPosition()) + " rotationIndex=" + this.getRotationIndex() + " slot0=" + (this.getItemContainer() == null ? "null" : String.valueOf(this.getItemContainer().getItemStack((short)0))));
            Ev0Log.info(LOGGER, "runExportPhase: nearbyBuffer.size=" + (this.nearbyBuffer == null ? "null" : String.valueOf(this.nearbyBuffer.size())) + " exporters=" + (entities != null));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
        ItemContainer currentIc = this.getItemContainer();
        if (currentIc == null) {
            return;
        }
        int cap = currentIc.getCapacity();
        int foundSlot = -1;
        ItemStack currentItemFast = null;
        String fastKey = null;
        for (int slotIdx = 0; slotIdx < cap; slotIdx++) {
            ItemStack slotStack = currentIc.getItemStack((short)slotIdx);
            if (slotStack == null) continue;
            String key = null;
            try {
                key = slotStack.getBlockKey();
            }
            catch (Throwable throwable) {
            }
            if (key == null) {
                key = this.resolveItemStackKey(slotStack);
            }
            if (this.isItemAllowedByFilter(key)) {
                foundSlot = slotIdx;
                currentItemFast = slotStack;
                fastKey = key;
                break;
            }
        }
        if (currentItemFast == null || foundSlot < 0) {
            return;
        }
        if (foundSlot != 0) {
            try {
                ItemStack slot0Stack = currentIc.getItemStack((short)0);
                int foundQty = currentItemFast.getQuantity();
                currentIc.removeItemStackFromSlot((short)foundSlot, foundQty);
                if (slot0Stack != null) {
                    int slot0Qty = slot0Stack.getQuantity();
                    currentIc.removeItemStackFromSlot((short)0, slot0Qty);
                    currentIc.addItemStackToSlot((short)foundSlot, slot0Stack);
                }
                currentIc.addItemStackToSlot((short)0, currentItemFast);
            } catch (Throwable throwable) {
                return;
            }
        }
        String wType = this.data != null && this.data.hopperType != null && !"Normal".equals(this.data.hopperType) ? this.data.hopperType : this.hopperType;
        try {
            Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag][WirelessExport] runExportPhase wType=" + wType + " hasTarget=" + this.hasWirelessTarget() + " target=" + String.valueOf(this.getWirelessTarget()) + " slot0=" + String.valueOf(currentItemFast));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        if ("WirelessExport".equalsIgnoreCase(wType) && this.hasWirelessTarget() && this.w != null) {
            block61: {
                Vector3i target = this.getWirelessTarget();
                try {
                    int amount;
                    boolean targetAllows;
                    String stackKey;
                    ItemStack stack;
                    Ev0Lib lib;
                    Ref<ChunkStore> blockRef;
                    BlockComponentChunk bcc;
                    long targetChunkIdx = ChunkUtil.indexChunkFromBlock(target.x, target.z);
                    if (this.w.getChunkIfInMemory(targetChunkIdx) == null) break block61;
                    Store<ChunkStore> cs = this.w.getChunkStore().getStore();
                    Ref<ChunkStore> colRef = this.w.getChunkStore().getChunkReference(targetChunkIdx);
                    if (colRef == null || (bcc = cs.getComponent(colRef, BlockComponentChunk.getComponentType())) == null || (blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn(target.x, target.y, target.z))) == null || (lib = Ev0Lib.getInstance()) == null || lib.getHopperComponentType() == null) break block61;
                    HopperComponent targetComp = cs.getComponent(blockRef, lib.getHopperComponentType());
                    try {
                        Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag][WirelessExport] targetComp=" + (targetComp != null));
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    ItemContainer targetIC = null;
                    HopperProcessor targetProc = null;
                    if (targetComp != null) {
                        targetIC = targetComp.getItemContainer();
                    } else {
                        WorldChunk targetChunk = this.w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(target.x, target.z));
                        if (targetChunk != null) {
                            Object rawIC;
                            Object state = EngineCompat.getState(targetChunk, target.x, target.y, target.z);
                            if (state instanceof HopperProcessor) {
                                HopperProcessor hp;
                                targetProc = hp = (HopperProcessor)state;
                                targetIC = hp.getItemContainer();
                            } else if (state != null && (rawIC = this.getItemContainerFromState(state)) instanceof ItemContainer) {
                                ItemContainer ic;
                                targetIC = ic = (ItemContainer)rawIC;
                            }
                        }
                    }
                    if (targetIC == null || (stack = this.getItemContainer().getItemStack((short)0)) == null || !this.isItemAllowedByFilter(stackKey = this.resolveItemStackKey(stack))) break block61;
                    // Plain containers (chest, etc.) have no filter — treat as accepting anything.
                    targetAllows = targetComp != null ? targetComp.isItemAllowedByFilter(stackKey) : (targetProc != null ? targetProc.isItemAllowedByFilter(stackKey) : true);
                    if (!targetAllows || (amount = (int)Math.min(this.data != null ? this.data.tier * (float)Ev0Config.getTierMultiplier() : 1.0f, (float)stack.getQuantity())) <= 0) break block61;
                    for (int slot = 0; slot < targetIC.getCapacity(); ++slot) {
                        try {
                            short tSlot = (short)slot;
                            int qtyBefore = HopperComponent.containerSlotQuantity(targetIC, tSlot);
                            ItemStackSlotTransaction tx = targetIC.addItemStackToSlot(tSlot, stack.withQuantity(amount));
                            try {
                                Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag][WirelessExport] tx slot=" + slot + " amt=" + amount + " succeeded=" + (tx != null && tx.succeeded()));
                            }
                            catch (Throwable throwable) {
                                // empty catch block
                            }
                            if (tx == null || !tx.succeeded()) continue;
                            int actuallyAdded = HopperComponent.containerSlotQuantity(targetIC, tSlot) - qtyBefore;
                            if (actuallyAdded <= 0) {
                                continue;
                            }
                            try {
                                this.getItemContainer().removeItemStackFromSlot((short)0, actuallyAdded);
                            } catch (Throwable th) {
                                try { targetIC.removeItemStackFromSlot(tSlot, actuallyAdded); } catch (Throwable ignored) {}
                                continue;
                            }
                            if (targetComp != null) {
                                try {
                                    this.putHopperComponent(cs, blockRef, targetComp);
                                }
                                catch (Throwable throwable) {
                                    // empty catch block
                                }
                            }
                            return;
                        }
                        catch (Throwable tSlot) {
                            // empty catch block
                        }
                    }
                }
                catch (Throwable cs) {
                    // empty catch block
                }
            }
            return;
        }
        HopperProcessor.Data dataLocal = this.data;
        ConnectedBlockPatternRule.AdjacentSide[] exportFaces = dataLocal != null ? dataLocal.exportFaces : null;
        int n2 = n = exportFaces == null ? 0 : exportFaces.length;
        if (n <= 0) {
            return;
        }
        int rrStart = Math.floorMod(this.exportFaceCursor, n);
        for (int i = 0; i < n; ++i) {
            String itemKey;
            ConnectedBlockPatternRule.AdjacentSide side = exportFaces[(rrStart + i) % n];
            boolean exportedThisFace = false;
            Vector3i hopperPos = this.getBlockPosition();
            ConnectedBlockPatternRule.AdjacentSide rotated = WorldHelper.rotate(side, this.getRotationIndex());
            Vector3i rel = new Vector3i(rotated.relativePosition);
            Vector3i exportFace = new Vector3i(hopperPos.x + ((Vector3i)((Object)rel)).x, hopperPos.y + ((Vector3i)((Object)rel)).y, hopperPos.z + ((Vector3i)((Object)rel)).z);
            Vector3i exportPos = this.resolveNeighborForTransfer(exportFace);
            exportPos = this.correctBenchProbePos(exportPos, side, this.getRotationIndex());
            try {
                Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] export probe side=" + String.valueOf((Object)side) + " rotated=" + String.valueOf((Object)rotated) + " rel=" + String.valueOf(rel) + " hopperPos=" + String.valueOf(hopperPos) + " exportFace=" + String.valueOf(exportFace) + " exportPos=" + String.valueOf(exportPos) + " rotationIndex=" + this.getRotationIndex());
            }
            catch (Throwable amount) {
                // empty catch block
            }
            WorldChunk chunk = this.w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(exportPos.x, exportPos.z));
            if (chunk == null) continue;
            Object state = EngineCompat.getState(chunk, exportPos.x, exportPos.y, exportPos.z);
            int targetFluidId = EngineCompat.getFluidId(chunk, exportPos.x, exportPos.y, exportPos.z);
            boolean hasContainer = state != null && (state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState") || state.getClass().getSimpleName().contains("ItemContainer") || this.getItemContainerFromState(state) != null) || state == null && this.getContainerViaECS(exportPos) != null;
            try {
                Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] export side=" + String.valueOf((Object)side) + " exportFace=" + String.valueOf(exportFace) + " exportPos=" + String.valueOf(exportPos) + " state=" + (state == null ? "null" : state.getClass().getSimpleName()) + " hasContainer=" + hasContainer);
            }
            catch (Throwable tx) {
                // empty catch block
            }
            try {
                Ev0Log.info(LOGGER, "export probe: side=" + String.valueOf((Object)side) + " exportPos=" + String.valueOf(exportPos) + " stateClass=" + (state == null ? "null" : state.getClass().getName()) + " hasContainer=" + hasContainer);
            }
            catch (Throwable tx) {
                // empty catch block
            }
            ItemStack currentItem = this.getItemContainer().getItemStack((short)0);
            // Filled buckets are kept as-is: the hopper no longer empties a filled bucket
            // into an adjacent fluid (which left a plain Container_Bucket behind).
            boolean transferred = !exportedThisFace && this.tryTransferToOrFromContainer(state, exportPos, side, entities, true);
            exportedThisFace = exportedThisFace || transferred;
            try {
                Ev0Log.info(LOGGER, "tryTransferToOrFromContainer returned=" + transferred + " for exportPos=" + String.valueOf(exportPos));
            }
            catch (Throwable fluidToPlace) {
                // empty catch block
            }
            if (!(exportedThisFace || transferred || currentItem == null || hasContainer || targetFluidId != 0)) {
                WorldChunk faceChunk;
                String blockKey = null;
                try {
                    blockKey = currentItem.getBlockKey();
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                if (blockKey == null) {
                    blockKey = this.resolveItemStackKey(currentItem);
                }
                if (this.isItemAllowedByFilter(blockKey) && (faceChunk = this.w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(exportFace.x, exportFace.z))) != null && EngineCompat.getBlockType(faceChunk, exportFace.x, exportFace.y, exportFace.z) == null) {
                    EngineCompat.setBlock(faceChunk, exportFace.x, exportFace.y, exportFace.z, blockKey);
                    if (EngineCompat.getBlockType(faceChunk, exportFace.x, exportFace.y, exportFace.z) != null) {
                        try {
                            this.getItemContainer().removeItemStackFromSlot((short)0, 1);
                        } catch (Throwable th) {
                            EngineCompat.setBlock(faceChunk, exportFace.x, exportFace.y, exportFace.z, BlockType.EMPTY);
                            continue;
                        }
                        exportedThisFace = true;
                    }
                }
            }
            if (!exportedThisFace) continue;
            this.exportFaceCursor = Math.floorMod(rrStart + i + 1, n);
            if (this.data.exportOnce) break;
        }
        } catch (Throwable t) { }
    }

    private boolean tryTransferToOrFromContainer(Object state, Vector3i pos, ConnectedBlockPatternRule.AdjacentSide side, Store<EntityStore> entities, boolean exportPhase) {
        ItemContainer sourceContainer;
        block168: {
            boolean isProcessingBench;
            block167: {
                block164: {
                    if (state == null) {
                        Object benchAtPos = this.getBenchBlockAtPos(pos);
                        if (benchAtPos != null) {
                            return this.tryTransferToOrFromContainer(benchAtPos, pos, side, entities, exportPhase);
                        }
                        try {
                            Ev0Log.info(LOGGER, "tryTransferToOrFromContainer: state==null for pos=" + String.valueOf(pos) + " exportPhase=" + exportPhase);
                        }
                        catch (Throwable throwable) {
                            // empty catch block
                        }
                        try {
                            Ref<ChunkStore> blockRef;
                            BlockComponentChunk bcc;
                            if (this.w == null) break block164;
                            if (this.w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(pos.x, pos.z)) == null) break block164;
                            Store<ChunkStore> cs = this.w.getChunkStore().getStore();
                            Ref<ChunkStore> chunkRef = this.w.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
                            if (chunkRef == null || (bcc = cs.getComponent(chunkRef, BlockComponentChunk.getComponentType())) == null || (blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn(pos.x, pos.y, pos.z))) == null) break block164;
                            try {
                                Method getComp;
                                block166: {
                                    block165: {
                                        getComp = cs.getClass().getMethod("getComponent", Ref.class, Class.forName("com.hypixel.hytale.component.ComponentType"));
                                        HopperComponent adjacentHopper = null;
                                        try {
                                            Ev0Lib lib = Ev0Lib.getInstance();
                                            if (lib != null && lib.getHopperComponentType() != null) {
                                                adjacentHopper = cs.getComponent(blockRef, lib.getHopperComponentType());
                                            }
                                        }
                                        catch (Throwable lib) {
                                            // empty catch block
                                        }
                                        if (exportPhase && adjacentHopper != null) {
                                            try {
                                                ItemContainer target2 = adjacentHopper.getItemContainer();
                                                ItemStack hopperStack2 = this.getItemContainer().getItemStack((short)0);
                                                if (target2 == null || hopperStack2 == null) break block165;
                                                for (int slot = 0; slot < target2.getCapacity(); ++slot) {
                                                    try {
                                                        int transferAmount;
                                                        if (!this.isItemAllowedByFilter(this.resolveItemStackKey(hopperStack2)) || (transferAmount = (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)hopperStack2.getQuantity())) <= 0) continue;
                                                        short tSlot = (short)slot;
                                                        int qtyBefore = HopperComponent.containerSlotQuantity(target2, tSlot);
                                                        ItemStackSlotTransaction t2 = target2.addItemStackToSlot(tSlot, hopperStack2.withQuantity(transferAmount));
                                                        try {
                                                            Ev0Log.info(LOGGER, "direct-hopper export attempt slot=" + slot + " transferAmount=" + transferAmount + " tx=" + (t2 == null ? "null" : String.valueOf(t2.succeeded())));
                                                        }
                                                        catch (Throwable throwable) {
                                                            // empty catch block
                                                        }
                                                        if (t2 == null || !t2.succeeded()) continue;
                                                        int actuallyAdded2 = HopperComponent.containerSlotQuantity(target2, tSlot) - qtyBefore;
                                                        if (actuallyAdded2 <= 0) continue;
                                                        try {
                                                            this.spawnVisualFor(hopperStack2.withQuantity(actuallyAdded2), true, pos, side, entities);
                                                        }
                                                        catch (Throwable throwable) {
                                                            // empty catch block
                                                        }
                                                        try {
                                                            this.getItemContainer().removeItemStackFromSlot((short)0, actuallyAdded2);
                                                        } catch (Throwable th) {
                                                            try { target2.removeItemStackFromSlot(tSlot, actuallyAdded2); } catch (Throwable ignored) {}
                                                            continue;
                                                        }
                                                        try {
                                                            this.putHopperComponent(cs, blockRef, adjacentHopper);
                                                        }
                                                        catch (Throwable throwable) {
                                                            // empty catch block
                                                        }
                                                        try {
                                                            Ref<ChunkStore> myBlockRef;
                                                            BlockComponentChunk bcc2;
                                                            Ref<ChunkStore> colRef2 = this.w.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(this.cachedPosition.x, this.cachedPosition.z));
                                                            if (colRef2 != null && (bcc2 = cs.getComponent(colRef2, BlockComponentChunk.getComponentType())) != null && (myBlockRef = bcc2.getEntityReference(ChunkUtil.indexBlockInColumn(this.cachedPosition.x, this.cachedPosition.y, this.cachedPosition.z))) != null) {
                                                                this.putHopperComponent(cs, myBlockRef, this);
                                                            }
                                                        }
                                                        catch (Throwable colRef2) {
                                                            // empty catch block
                                                        }
                                                        return true;
                                                    }
                                                    catch (Throwable transferAmount) {
                                                        // empty catch block
                                                    }
                                                }
                                            }
                                            catch (Throwable target2) {
                                                // empty catch block
                                            }
                                        }
                                    }
                                    boolean targetHasHopperComponent = false;
                                    try {
                                        Ev0Lib lib = Ev0Lib.getInstance();
                                        ComponentType<ChunkStore, HopperComponent> hopperCompTypeProbe = lib != null ? lib.getHopperComponentType() : null;
                                        Object hopperCompProbe = hopperCompTypeProbe != null ? getComp.invoke(cs, blockRef, hopperCompTypeProbe) : null;
                                        targetHasHopperComponent = hopperCompProbe instanceof HopperComponent;
                                    }
                                    catch (Throwable lib) {
                                        // empty catch block
                                    }
                                    Class<?> icbCls = Class.forName("com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock");
                                    Method getCompType = icbCls.getMethod("getComponentType", new Class[0]);
                                    Object compType = getCompType.invoke(null, new Object[0]);
                                    Object icbObj = null;
                                    try {
                                        icbObj = getComp.invoke(cs, blockRef, compType);
                                    }
                                    catch (Throwable ignored) {
                                        icbObj = null;
                                    }
                                    if (!targetHasHopperComponent && icbObj != null && icbCls.isInstance(icbObj) && this.getContainerViaECS(pos) == null) {
                                        try {
                                            Method getIC = icbCls.getMethod("getItemContainer", new Class[0]);
                                            Object cont = getIC.invoke(icbObj, new Object[0]);
                                            if (!(cont instanceof ItemContainer)) break block166;
                                            ItemContainer target = (ItemContainer)cont;
                                            ItemStack hopperStack = this.getItemContainer().getItemStack((short)0);
                                            try {
                                                Ev0Log.info(LOGGER, "component-first target capacity=" + target.getCapacity() + " hopperStack=" + String.valueOf(hopperStack));
                                            }
                                            catch (Throwable myBlockRef) {
                                                // empty catch block
                                            }
                                            if (hopperStack == null) {
                                                return false;
                                            }
                                            for (int slot = 0; slot < target.getCapacity(); ++slot) {
                                                try {
                                                    if (!this.isItemAllowedByFilter(this.resolveItemStackKey(hopperStack))) {
                                                        return false;
                                                    }
                                                    int transferAmount = (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)hopperStack.getQuantity());
                                                    if (transferAmount <= 0) continue;
                                                    short tSlot = (short)slot;
                                                    int qtyBefore = HopperComponent.containerSlotQuantity(target, tSlot);
                                                    ItemStackSlotTransaction t = target.addItemStackToSlot(tSlot, hopperStack.withQuantity(transferAmount));
                                                    try {
                                                        Ev0Log.info(LOGGER, "component-first transfer attempt slot=" + slot + " transferAmount=" + transferAmount + " tx=" + (t == null ? "null" : String.valueOf(t.succeeded())));
                                                    }
                                                    catch (Throwable throwable) {
                                                        // empty catch block
                                                    }
                                                    if (t == null || !t.succeeded()) continue;
                                                    int actuallyAdded = HopperComponent.containerSlotQuantity(target, tSlot) - qtyBefore;
                                                    if (actuallyAdded <= 0) continue;
                                                    try {
                                                        this.spawnVisualFor(hopperStack.withQuantity(actuallyAdded), true, pos, side, entities);
                                                    }
                                                    catch (Throwable throwable) {
                                                        // empty catch block
                                                    }
                                                    try {
                                                        this.getItemContainer().removeItemStackFromSlot((short)0, actuallyAdded);
                                                    } catch (Throwable th) {
                                                        try { target.removeItemStackFromSlot(tSlot, actuallyAdded); } catch (Throwable ignored) {}
                                                        continue;
                                                    }
                                                    return true;
                                                }
                                                catch (Throwable transferAmount) {
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
                                        hopperCompObj = getComp.invoke(cs, blockRef, hopperCompType);
                                    }
                                    catch (Throwable ignored) {
                                        hopperCompObj = null;
                                    }
                                    if (hopperCompObj == null || !hopperCompCls.isInstance(hopperCompObj)) break block164;
                                    try {
                                        Method getIC2 = hopperCompCls.getMethod("getItemContainer", new Class[0]);
                                        Object cont2 = getIC2.invoke(hopperCompObj, new Object[0]);
                                        if (!(cont2 instanceof ItemContainer)) break block164;
                                        ItemContainer target22 = (ItemContainer)cont2;
                                        ItemStack hopperStack2 = this.getItemContainer().getItemStack((short)0);
                                        if (hopperStack2 == null) {
                                            return false;
                                        }
                                        for (int slot = 0; slot < target22.getCapacity(); ++slot) {
                                            try {
                                                if (!this.isItemAllowedByFilter(this.resolveItemStackKey(hopperStack2))) {
                                                    return false;
                                                }
                                                int transferAmount = (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)hopperStack2.getQuantity());
                                                if (transferAmount <= 0) continue;
                                                short tSlot = (short)slot;
                                                int qtyBefore = HopperComponent.containerSlotQuantity(target22, tSlot);
                                                ItemStackSlotTransaction t2 = target22.addItemStackToSlot(tSlot, hopperStack2.withQuantity(transferAmount));
                                                try {
                                                    Ev0Log.info(LOGGER, "chained-hopper export attempt slot=" + slot + " transferAmount=" + transferAmount + " tx=" + (t2 == null ? "null" : String.valueOf(t2.succeeded())));
                                                }
                                                catch (Throwable throwable) {
                                                    // empty catch block
                                                }
                                                if (t2 == null || !t2.succeeded()) continue;
                                                int actuallyAdded22 = HopperComponent.containerSlotQuantity(target22, tSlot) - qtyBefore;
                                                if (actuallyAdded22 <= 0) continue;
                                                try {
                                                    this.spawnVisualFor(hopperStack2.withQuantity(actuallyAdded22), true, pos, side, entities);
                                                }
                                                catch (Throwable throwable) {
                                                    // empty catch block
                                                }
                                                try {
                                                    this.getItemContainer().removeItemStackFromSlot((short)0, actuallyAdded22);
                                                } catch (Throwable th) {
                                                    try { target22.removeItemStackFromSlot(tSlot, actuallyAdded22); } catch (Throwable ignored) {}
                                                    continue;
                                                }
                                                try {
                                                    this.putHopperComponent(cs, blockRef, (HopperComponent)hopperCompObj);
                                                }
                                                catch (Throwable tt) {
                                                    Ev0Log.warn(LOGGER, "putHopperComponent failed: " + (tt == null ? "null" : tt.getMessage()));
                                                }
                                                try {
                                                    if (this.w != null) {
                                                        Ref<ChunkStore> myBlockRef;
                                                        BlockComponentChunk bcc2;
                                                        Store<ChunkStore> cs2 = this.w.getChunkStore().getStore();
                                                        Ref<ChunkStore> colRef2 = this.w.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(this.cachedPosition.x, this.cachedPosition.z));
                                                        if (colRef2 != null && (bcc2 = cs2.getComponent(colRef2, BlockComponentChunk.getComponentType())) != null && (myBlockRef = bcc2.getEntityReference(ChunkUtil.indexBlockInColumn(this.cachedPosition.x, this.cachedPosition.y, this.cachedPosition.z))) != null) {
                                                            try {
                                                                this.putHopperComponent(cs2, myBlockRef, this);
                                                            }
                                                            catch (Throwable tt) {
                                                                Ev0Log.warn(LOGGER, "putHopperComponent (self) failed: " + (tt == null ? "null" : tt.getMessage()));
                                                            }
                                                        }
                                                    }
                                                }
                                                catch (Throwable throwable) {
                                                    // empty catch block
                                                }
                                                return true;
                                            }
                                            catch (Throwable transferAmount) {
                                                // empty catch block
                                            }
                                        }
                                    }
                                    catch (Throwable getIC2) {
                                    }
                                }
                                catch (Throwable hopperCompCls) {
                                }
                            }
                            catch (Throwable getComp) {}
                        }
                        catch (Throwable cs) {
                            // empty catch block
                        }
                    }
                }
                if (exportPhase && this.w != null && this.w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(pos.x, pos.z)) != null) {
                    try {
                        Method getIC2;
                        Object cont2;
                        Ref<ChunkStore> blockRef;
                        BlockComponentChunk bcc;
                        Store<ChunkStore> cs = this.w.getChunkStore().getStore();
                        Ref<ChunkStore> colRef = this.w.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
                        if (colRef == null || (bcc = cs.getComponent(colRef, BlockComponentChunk.getComponentType())) == null || (blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn(pos.x, pos.y, pos.z))) == null) break block167;
                        Method getComp = cs.getClass().getMethod("getComponent", Ref.class, Class.forName("com.hypixel.hytale.component.ComponentType"));
                        Class<?> hopperCompCls = Class.forName("org.Ev0Mods.plugin.api.component.HopperComponent");
                        Method getCompTypeH = hopperCompCls.getMethod("getComponentType", new Class[0]);
                        Object hopperCompType = getCompTypeH.invoke(null, new Object[0]);
                        Object hopperCompObj = null;
                        try {
                            hopperCompObj = getComp.invoke(cs, blockRef, hopperCompType);
                        }
                        catch (Throwable ignored) {
                            hopperCompObj = null;
                        }
                        if (hopperCompObj == null || !hopperCompCls.isInstance(hopperCompObj) || !((cont2 = (getIC2 = hopperCompCls.getMethod("getItemContainer", new Class[0])).invoke(hopperCompObj, new Object[0])) instanceof ItemContainer)) break block167;
                        ItemContainer target2 = (ItemContainer)cont2;
                        ItemStack hopperStack2 = this.getItemContainer().getItemStack((short)0);
                        if (hopperStack2 == null) {
                            return false;
                        }
                        for (int slot = 0; slot < target2.getCapacity(); ++slot) {
                            try {
                                int transferAmount;
                                if (!this.isItemAllowedByFilter(this.resolveItemStackKey(hopperStack2)) || (transferAmount = (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)hopperStack2.getQuantity())) <= 0) continue;
                                short tSlot = (short)slot;
                                int qtyBefore = HopperComponent.containerSlotQuantity(target2, tSlot);
                                ItemStackSlotTransaction t2 = target2.addItemStackToSlot(tSlot, hopperStack2.withQuantity(transferAmount));
                                if (t2 == null || !t2.succeeded()) continue;
                                int actuallyAdded2b = HopperComponent.containerSlotQuantity(target2, tSlot) - qtyBefore;
                                if (actuallyAdded2b <= 0) continue;
                                try {
                                    this.spawnVisualFor(hopperStack2.withQuantity(actuallyAdded2b), true, pos, side, entities);
                                }
                                catch (Throwable target22) {
                                    // empty catch block
                                }
                                try {
                                    this.getItemContainer().removeItemStackFromSlot((short)0, actuallyAdded2b);
                                } catch (Throwable th) {
                                    try { target2.removeItemStackFromSlot(tSlot, actuallyAdded2b); } catch (Throwable ignored) {}
                                    continue;
                                }
                                try {
                                    this.putHopperComponent(cs, blockRef, (HopperComponent)hopperCompObj);
                                }
                                catch (Throwable tt) {
                                    Ev0Log.warn(LOGGER, "putHopperComponent failed: " + (tt == null ? "null" : tt.getMessage()));
                                }
                                try {
                                    if (this.w != null) {
                                        Ref<ChunkStore> myBlockRef;
                                        BlockComponentChunk bcc2;
                                        Store<ChunkStore> cs2 = this.w.getChunkStore().getStore();
                                        Ref<ChunkStore> colRef2 = this.w.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(this.cachedPosition.x, this.cachedPosition.z));
                                        if (colRef2 != null && (bcc2 = cs2.getComponent(colRef2, BlockComponentChunk.getComponentType())) != null && (myBlockRef = bcc2.getEntityReference(ChunkUtil.indexBlockInColumn(this.cachedPosition.x, this.cachedPosition.y, this.cachedPosition.z))) != null) {
                                            try {
                                                this.putHopperComponent(cs2, myBlockRef, this);
                                            }
                                            catch (Throwable tt) {
                                                Ev0Log.warn(LOGGER, "putHopperComponent (self) failed: " + (tt == null ? "null" : tt.getMessage()));
                                            }
                                        }
                                    }
                                }
                                catch (Throwable throwable) {
                                    // empty catch block
                                }
                                return true;
                            }
                            catch (Throwable throwable) {
                                // empty catch block
                            }
                        }
                    }
                    catch (Throwable cs) {
                        // empty catch block
                    }
                }
            }
            if (state == null) {
                ItemStack have;
                ItemContainer ecsContainer = this.getContainerViaECS(pos);
                if (ecsContainer == null) {
                    return false;
                }
                if (!exportPhase) {
                    for (int slot = 0; slot < ecsContainer.getCapacity(); ++slot) {
                        ItemStackSlotTransaction t;
                        ItemStack toAdd;
                        int transferAmount;
                        ItemStack stack = ecsContainer.getItemStack((short)slot);
                        if (stack == null) continue;
                        String probeKey = null;
                        try {
                            probeKey = stack.getBlockKey();
                        }
                        catch (Throwable getComp) {
                            // empty catch block
                        }
                        if (probeKey == null) {
                            probeKey = this.resolveItemStackKey(stack);
                        }
                        if (!this.isItemAllowedByFilter(probeKey)) continue;
                        int srcAvailable = stack.getQuantity();
                        if (this.isSingletonMode() && srcAvailable <= 1) continue;
                        int n = transferAmount = this.isSingletonMode() && (float)srcAvailable < this.data.tier * (float)Ev0Config.getTierMultiplier() ? srcAvailable - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)srcAvailable);
                        if (transferAmount <= 0 || (toAdd = stack.withQuantity(transferAmount)) == null || !(t = this.getItemContainer().addItemStackToSlot((short)0, toAdd)).succeeded()) continue;
                        ecsContainer.removeItemStackFromSlot((short)slot, transferAmount);
                        return true;
                    }
                }
                if (exportPhase && (have = this.getItemContainer().getItemStack((short)0)) != null && have.getQuantity() > 0) {
                    int haveQty = have.getQuantity();
                    if (this.isSingletonMode() && haveQty <= 1) {
                        return false;
                    }
                    int transferAmount = this.isSingletonMode() && (float)haveQty < this.data.tier * (float)Ev0Config.getTierMultiplier() ? haveQty - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)haveQty);
                    ItemStack safeStack = have.withQuantity(transferAmount);
                    for (int slot = 0; slot < ecsContainer.getCapacity(); ++slot) {
                        short tSlot = (short)slot;
                        int qtyBefore = HopperComponent.containerSlotQuantity(ecsContainer, tSlot);
                        ItemStackSlotTransaction t = ecsContainer.addItemStackToSlot(tSlot, safeStack);
                        if (t == null || !t.succeeded()) continue;
                        int actuallyAddedECS = HopperComponent.containerSlotQuantity(ecsContainer, tSlot) - qtyBefore;
                        if (actuallyAddedECS <= 0) continue;
                        this.spawnVisualFor(safeStack.withQuantity(actuallyAddedECS), exportPhase, pos, side, entities);
                        try {
                            this.getItemContainer().removeItemStackFromSlot((short)0, actuallyAddedECS);
                        } catch (Throwable th) {
                            try { ecsContainer.removeItemStackFromSlot(tSlot, actuallyAddedECS); } catch (Throwable ignored) {}
                            continue;
                        }
                        return true;
                    }
                }
                return false;
            }
            Object bench = state != null && state.getClass().getName().equals(PROCESSING_BENCH_CLASS) ? state : this.getBenchBlockAtPos(pos);
            boolean bl = isProcessingBench = bench != null;
            if (!isProcessingBench && !state.getClass().getSimpleName().contains("ItemContainer") && this.getItemContainerFromState(state) == null) {
                return false;
            }
            if (isProcessingBench) {
                if (!exportPhase) {
                    ItemContainer output = this.benchOutputContainer(bench);
                    if (output == null) {
                        return false;
                    }
                    for (int slot = 0; slot < output.getCapacity(); ++slot) {
                        ItemStackSlotTransaction t2;
                        ItemStack fallbackStack;
                        Method ok;
                        Object r;
                        Method mm2;
                        int transferAmount2;
                        ItemStack stack = output.getItemStack((short)slot);
                        if (stack == null) continue;
                        String probeKeyPb = null;
                        try {
                            probeKeyPb = stack.getBlockKey();
                        }
                        catch (Throwable tSlot) {
                            // empty catch block
                        }
                        if (probeKeyPb == null) {
                            probeKeyPb = this.resolveItemStackKey(stack);
                        }
                        if (!this.isItemAllowedByFilter(probeKeyPb)) continue;
                        int pbAvailable = stack.getQuantity();
                        if (this.isSingletonMode() && pbAvailable <= 1) continue;
                        int n = transferAmount2 = this.isSingletonMode() && (float)pbAvailable < this.data.tier * (float)Ev0Config.getTierMultiplier() ? pbAvailable - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)pbAvailable);
                        if (transferAmount2 <= 0) continue;
                        boolean moved = false;
                        try {
                            mm2 = output.getClass().getMethod("moveItemStackFromSlot", Short.TYPE, Integer.TYPE, ItemContainer.class);
                            r = mm2.invoke((Object)output, (short)slot, transferAmount2, this.getItemContainer());
                            if (r != null) {
                                ok = r.getClass().getMethod("succeeded", new Class[0]);
                                moved = Boolean.TRUE.equals(ok.invoke(r, new Object[0]));
                            }
                        }
                            catch (Throwable mm3) {
                                // empty catch block
                            }
                            if (!moved) {
                                try {
                                    mm2 = output.getClass().getMethod("moveItemStackFromSlot", Short.TYPE, ItemContainer.class);
                                    r = mm2.invoke((Object)output, (short)slot, this.getItemContainer());
                                    if (r != null) {
                                        ok = r.getClass().getMethod("succeeded", new Class[0]);
                                        moved = Boolean.TRUE.equals(ok.invoke(r, new Object[0]));
                                    }
                                }
                                catch (Throwable mm4) {
                                    // empty catch block
                                }
                            }
                            if (!moved && (fallbackStack = stack.withQuantity(transferAmount2)) != null && (t2 = this.getItemContainer().addItemStackToSlot((short)0, fallbackStack)).succeeded()) {
                                output.removeItemStackFromSlot((short)slot, transferAmount2);
                                moved = true;
                            }
                            if (!moved) continue;
                            ItemStack visualStack = stack.withQuantity(transferAmount2);
                            if (visualStack != null) {
                                try {
                                    this.spawnVisualFor(visualStack, false, pos, side, entities);
                                }
                                catch (Throwable t3) {
                                // empty catch block
                            }
                        }
                        return true;
                    }
                } else {
                    ItemStack have = this.getItemContainer().getItemStack((short)0);
                    if (have != null && have.getQuantity() > 0) {
                        ItemContainer input;
                        ItemStackSlotTransaction t;
                        ItemContainer fuelContainer;
                        int transferAmount;
                        int haveQty = have.getQuantity();
                        if (this.isSingletonMode() && haveQty <= 1) {
                            return false;
                        }
                        int n = transferAmount = this.isSingletonMode() && (float)haveQty < this.data.tier * (float)Ev0Config.getTierMultiplier() ? haveQty - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)haveQty);
                        if (transferAmount <= 0) {
                            return false;
                        }
                        ItemStack safeStack = have.withQuantity(transferAmount);
                        String haveKey = this.resolveItemStackKey(have);
                        if (!this.isItemAllowedByFilter(haveKey)) {
                            return false;
                        }
                        boolean isFuel = false;
                        try {
                            isFuel = have.getItem().getFuelQuality() > 0.0;
                        }
                        catch (Throwable moved) {
                            // empty catch block
                        }
                        if (isFuel && (fuelContainer = this.benchFuelContainer(bench)) != null) {
                            for (int slot = 0; slot < fuelContainer.getCapacity(); ++slot) {
                                short tSlot = (short)slot;
                                int qtyBefore = HopperComponent.containerSlotQuantity(fuelContainer, tSlot);
                                t = fuelContainer.addItemStackToSlot(tSlot, safeStack);
                                if (t == null || !t.succeeded()) continue;
                                int actuallyAddedFuel = HopperComponent.containerSlotQuantity(fuelContainer, tSlot) - qtyBefore;
                                if (actuallyAddedFuel <= 0) continue;
                                try {
                                    this.spawnVisualFor(safeStack.withQuantity(actuallyAddedFuel), true, pos, side, entities);
                                }
                                catch (Throwable throwable) {
                                    // empty catch block
                                }
                                try {
                                    this.getItemContainer().removeItemStackFromSlot((short)0, actuallyAddedFuel);
                                } catch (Throwable th) {
                                    try { fuelContainer.removeItemStackFromSlot(tSlot, actuallyAddedFuel); } catch (Throwable ignored) {}
                                    continue;
                                }
                                return true;
                            }
                        }
                        if ((input = this.benchInputContainer(bench)) != null) {
                            for (int slot = 0; slot < input.getCapacity(); ++slot) {
                                short tSlot = (short)slot;
                                int qtyBefore = HopperComponent.containerSlotQuantity(input, tSlot);
                                t = input.addItemStackToSlot(tSlot, safeStack);
                                if (t == null || !t.succeeded()) continue;
                                int actuallyAddedInput = HopperComponent.containerSlotQuantity(input, tSlot) - qtyBefore;
                                if (actuallyAddedInput <= 0) continue;
                                try {
                                    this.spawnVisualFor(safeStack.withQuantity(actuallyAddedInput), true, pos, side, entities);
                                }
                                catch (Throwable throwable) {
                                    // empty catch block
                                }
                                try {
                                    this.getItemContainer().removeItemStackFromSlot((short)0, actuallyAddedInput);
                                } catch (Throwable th) {
                                    try { input.removeItemStackFromSlot(tSlot, actuallyAddedInput); } catch (Throwable ignored) {}
                                    continue;
                                }
                                return true;
                            }
                        }
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
            sourceContainer = (ItemContainer)containerStateObj;
            if (exportPhase) {
                try {
                    ItemStack hopperStack = this.getItemContainer().getItemStack((short)0);
                    if (hopperStack == null) break block168;
                    int hopperQty = hopperStack.getQuantity();
                    if (this.isSingletonMode() && hopperQty <= 1) {
                        return false;
                    }
                    int[] hspExport = getHopperInputSlots(state);
                    for (int slot = 0; slot < sourceContainer.getCapacity(); ++slot) {
                        try {
                            int transferAmount3;
                            if (hspExport != null && !isInputSlot(hspExport, slot)) continue;
                            if (!this.isItemAllowedByFilter(this.resolveItemStackKey(hopperStack))) break;
                            int n = transferAmount3 = this.isSingletonMode() && (float)hopperQty < this.data.tier * (float)Ev0Config.getTierMultiplier() ? hopperQty - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)hopperQty);
                            if (transferAmount3 <= 0) break;
                            ItemStack safeStack = hopperStack.withQuantity(transferAmount3);
                            short tSlot = (short)slot;
                            int qtyBefore = HopperComponent.containerSlotQuantity(sourceContainer, tSlot);
                            ItemStackSlotTransaction t = sourceContainer.addItemStackToSlot(tSlot, safeStack);
                            try {
                                Ev0Log.info(LOGGER, "fallback container transfer attempt slot=" + slot + " transferAmount=" + transferAmount3 + " tx=" + (t == null ? "null" : String.valueOf(t.succeeded())));
                            }
                            catch (Throwable throwable) {
                                // empty catch block
                            }
                            if (t == null || !t.succeeded()) continue;
                            int actuallyAdded3 = HopperComponent.containerSlotQuantity(sourceContainer, tSlot) - qtyBefore;
                            if (actuallyAdded3 <= 0) continue;
                            try {
                                this.spawnVisualFor(safeStack.withQuantity(actuallyAdded3), true, pos, side, entities);
                            }
                            catch (Throwable throwable) {
                                // empty catch block
                            }
                            try {
                                this.getItemContainer().removeItemStackFromSlot((short)0, actuallyAdded3);
                            } catch (Throwable th) {
                                try { sourceContainer.removeItemStackFromSlot(tSlot, actuallyAdded3); } catch (Throwable ignored) {}
                                continue;
                            }
                            return true;
                        }
                        catch (Throwable transferAmount3) {
                            // empty catch block
                        }
                    }
                }
                catch (Throwable hopperStack) {
                    // empty catch block
                }
            }
        }
        int[] hspImport = getHopperInputSlots(state);
        for (int slot = 0; slot < sourceContainer.getCapacity(); ++slot) {
            int transferAmount;
            if (isInputSlot(hspImport, slot)) continue;
            ItemStack stack = sourceContainer.getItemStack((short)slot);
            if (stack == null) continue;
            String probeKey2 = null;
            try {
                probeKey2 = stack.getBlockKey();
            }
            catch (Throwable transferAmount3) {
                // empty catch block
            }
            if (probeKey2 == null) {
                probeKey2 = this.resolveItemStackKey(stack);
            }
            if (!this.isItemAllowedByFilter(probeKey2)) continue;
            int srcAvailable = stack.getQuantity();
            if (this.isSingletonMode() && srcAvailable <= 1) continue;
            int n = transferAmount = this.isSingletonMode() && (float)srcAvailable < this.data.tier * (float)Ev0Config.getTierMultiplier() ? srcAvailable - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)srcAvailable);
            if (transferAmount <= 0) continue;
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
        block168: {
            block165: {
                ItemStack destStack = this.getItemContainer().getItemStack((short)0);
                try {
                    Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] tryImportFromContainer pos=" + String.valueOf(pos) + " destStack=" + (destStack == null ? "null" : String.valueOf(destStack)));
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                if (destStack != null) {
                    return false;
                }
                try {
                    String preMsg = "[HopperDiag] tryImportFromContainer START pos=" + String.valueOf(pos) + " chunkIndex=" + ChunkUtil.indexChunkFromBlock(pos.x, pos.z) + " chunkPresent=" + (chunk != null);
                    try {
                        Ev0Log.warn(HytaleLogger.getLogger(), preMsg);
                    }
                    catch (Throwable throwable) {}
                }
                catch (Throwable preMsg) {
                    // empty catch block
                }
                state = EngineCompat.getState(chunk, pos.x, pos.y, pos.z);
                try {
                    Ev0Log.warn(HytaleLogger.getLogger(), "[HopperDiag] tryImportFromContainer: pos=" + String.valueOf(pos) + " stateClass=" + (state == null ? "null" : state.getClass().getName()));
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                Object benchObj = this.getBenchBlockAtPos(pos);
                if (benchObj != null) {
                    ItemContainer output = this.benchOutputContainer(benchObj);
                    try {
                        Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] bench import: benchObj found, output=" + (String)(output == null ? "null" : "cap=" + output.getCapacity()));
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    if (output != null) {
                        for (int slot = 0; slot < output.getCapacity(); ++slot) {
                            ItemStackSlotTransaction t22;
                            ItemStack fallback2;
                            Method ok;
                            Object r;
                            Method mm2;
                            int transferAmount;
                            ItemStack stack2 = output.getItemStack((short)slot);
                            if (stack2 == null) continue;
                            String bKey = null;
                            try {
                                bKey = stack2.getBlockKey();
                            }
                            catch (Throwable throwable) {
                                // empty catch block
                            }
                            if (bKey == null) {
                                bKey = this.resolveItemStackKey(stack2);
                            }
                            if (!this.isItemAllowedByFilter(bKey)) continue;
                            int pbAvail = stack2.getQuantity();
                            if (this.isSingletonMode() && pbAvail <= 1) continue;
                            int n = transferAmount = this.isSingletonMode() && (float)pbAvail < this.data.tier * (float)Ev0Config.getTierMultiplier() ? pbAvail - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)pbAvail);
                            if (transferAmount <= 0) continue;
                            boolean moved = false;
                            try {
                                mm2 = output.getClass().getMethod("moveItemStackFromSlot", Short.TYPE, Integer.TYPE, ItemContainer.class);
                                r = mm2.invoke((Object)output, (short)slot, transferAmount, this.getItemContainer());
                                if (r != null) {
                                    ok = r.getClass().getMethod("succeeded", new Class[0]);
                                    moved = Boolean.TRUE.equals(ok.invoke(r, new Object[0]));
                                }
                            }
                            catch (Throwable mm3) {
                                // empty catch block
                            }
                            if (!moved) {
                                try {
                                    mm2 = output.getClass().getMethod("moveItemStackFromSlot", Short.TYPE, ItemContainer.class);
                                    r = mm2.invoke((Object)output, (short)slot, this.getItemContainer());
                                    if (r != null) {
                                        ok = r.getClass().getMethod("succeeded", new Class[0]);
                                        moved = Boolean.TRUE.equals(ok.invoke(r, new Object[0]));
                                    }
                                }
                                catch (Throwable mm4) {
                                    // empty catch block
                                }
                            }
                            if (!moved && (fallback2 = stack2.withQuantity(transferAmount)) != null) {
                                int qb22 = HopperComponent.containerSlotQuantity(this.getItemContainer(), (short)0);
                                t22 = this.getItemContainer().addItemStackToSlot((short)0, fallback2);
                                if (t22 != null && t22.succeeded()) {
                                    int added22 = HopperComponent.containerSlotQuantity(this.getItemContainer(), (short)0) - qb22;
                                    if (added22 > 0) {
                                        try {
                                            output.removeItemStackFromSlot((short)slot, added22);
                                        }
                                        catch (Throwable throwable) {
                                            this.getItemContainer().removeItemStackFromSlot((short)0, added22);
                                            continue;
                                        }
                                        moved = true;
                                    }
                                }
                            }
                            try {
                                Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] bench import slot=" + slot + " transferAmount=" + transferAmount + " moved=" + moved);
                            }
                            catch (Throwable fallback3) {
                                // empty catch block
                            }
                            if (!moved) continue;
                            ItemStack visualStack = stack2.withQuantity(transferAmount);
                            if (visualStack != null) {
                                try {
                                    this.spawnVisualFor(visualStack, false, pos, side, entities);
                                }
                                catch (Throwable t23) {
                                    // empty catch block
                                }
                            }
                            return true;
                        }
                    }
                    return false;
                }
                if (state == null) {
                    Object blockType = null;
                    try {
                        blockType = EngineCompat.getBlockType(chunk, pos.x, pos.y, pos.z);
                        Ev0Log.warn(HytaleLogger.getLogger(), "[HopperDiag] EngineCompat.getBlockType for pos=" + String.valueOf(pos) + " -> " + (blockType == null ? "null" : blockType.toString()));
                    }
                    catch (Throwable output) {
                        // empty catch block
                    }
                    try {
                        Ev0Log.warn(HytaleLogger.getLogger(), "[HopperDiag] state==null at pos=" + String.valueOf(pos) + "; blockTypeClass=" + (blockType == null ? "null" : blockType.getClass().getName()));
                    }
                    catch (Throwable output) {
                        // empty catch block
                    }
                    try {
                        if (this.w != null && this.w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(pos.x, pos.z)) != null) {
                            Store<ChunkStore> cs = this.w.getChunkStore().getStore();
                            Ref<ChunkStore> colRef = this.w.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
                            if (colRef == null) {
                                Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] chunk reference is null for chunkIndex=" + ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
                                break block165;
                            }
                            BlockComponentChunk bcc = cs.getComponent(colRef, BlockComponentChunk.getComponentType());
                            Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] BlockComponentChunk present=" + (bcc != null));
                            if (bcc == null) break block165;
                            Ref<ChunkStore> blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn(pos.x, pos.y, pos.z));
                            String brMsg = "[HopperDiag] blockRef for pos=" + String.valueOf(pos) + " -> " + (String)(blockRef == null ? "null" : "index=" + blockRef.getIndex());
                            Ev0Log.warn(HytaleLogger.getLogger(), brMsg);
                            try {
                                if (blockRef == null || this.w == null) break block165;
                                try {
                                    Method getComp;
                                    block167: {
                                        block166: {
                                            try {
                                                HopperComponent sourceHopper;
                                                Ev0Lib lib = Ev0Lib.getInstance();
                                                HopperComponent hopperComponent = sourceHopper = lib != null && lib.getHopperComponentType() != null ? cs.getComponent(blockRef, lib.getHopperComponentType()) : null;
                                                if (sourceHopper == null || sourceHopper.getItemContainer() == null) break block166;
                                                ItemContainer source = sourceHopper.getItemContainer();
                                                for (int slot = 0; slot < source.getCapacity(); ++slot) {
                                                    try {
                                                        int transferAmount;
                                                        ItemStack stack3 = source.getItemStack((short)slot);
                                                        if (stack3 == null) continue;
                                                        String blockKey2 = null;
                                                        try {
                                                            blockKey2 = stack3.getBlockKey();
                                                        }
                                                        catch (Throwable throwable) {
                                                            // empty catch block
                                                        }
                                                        if (blockKey2 == null) {
                                                            blockKey2 = this.resolveItemStackKey(stack3);
                                                        }
                                                        if (!this.isItemAllowedByFilter(blockKey2) || this.isSingletonMode() && stack3.getQuantity() <= 1) continue;
                                                        int n = transferAmount = this.isSingletonMode() && (float)stack3.getQuantity() < this.data.tier * (float)Ev0Config.getTierMultiplier() ? stack3.getQuantity() - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)stack3.getQuantity());
                                                        if (transferAmount <= 0) continue;
                                                        ItemStack safeStack = stack3.withQuantity(transferAmount);
                                                        int qb3 = HopperComponent.containerSlotQuantity(this.getItemContainer(), (short)0);
                                                        ItemStackSlotTransaction t3 = this.getItemContainer().addItemStackToSlot((short)0, safeStack);
                                                        try {
                                                            Ev0Log.info(LOGGER, "direct-hopper import attempt slot=" + slot + " transferAmount=" + transferAmount + " tx=" + (t3 == null ? "null" : String.valueOf(t3.succeeded())));
                                                        }
                                                        catch (Throwable throwable) {
                                                            // empty catch block
                                                        }
                                                        if (t3 == null || !t3.succeeded()) continue;
                                                        int added3 = HopperComponent.containerSlotQuantity(this.getItemContainer(), (short)0) - qb3;
                                                        if (added3 <= 0) continue;
                                                        try {
                                                            source.removeItemStackFromSlot((short)slot, added3);
                                                        }
                                                        catch (Throwable throwable) {
                                                            // empty catch block
                                                        }
                                                        try {
                                                            this.spawnVisualFor(safeStack, false, pos, side, entities);
                                                        }
                                                        catch (Throwable throwable) {
                                                            // empty catch block
                                                        }
                                                        try {
                                                            this.putHopperComponent(cs, blockRef, sourceHopper);
                                                        }
                                                        catch (Throwable throwable) {
                                                            // empty catch block
                                                        }
                                                        try {
                                                            Ref<ChunkStore> myBlockRef;
                                                            BlockComponentChunk bcc2;
                                                            Ref<ChunkStore> colRef2 = this.w.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(this.cachedPosition.x, this.cachedPosition.z));
                                                            if (colRef2 != null && (bcc2 = cs.getComponent(colRef2, BlockComponentChunk.getComponentType())) != null && (myBlockRef = bcc2.getEntityReference(ChunkUtil.indexBlockInColumn(this.cachedPosition.x, this.cachedPosition.y, this.cachedPosition.z))) != null) {
                                                                this.putHopperComponent(cs, myBlockRef, this);
                                                            }
                                                        }
                                                        catch (Throwable colRef2) {
                                                            // empty catch block
                                                        }
                                                        return true;
                                                    }
                                                    catch (Throwable stack3) {
                                                        // empty catch block
                                                    }
                                                }
                                            }
                                            catch (Throwable lib) {
                                                // empty catch block
                                            }
                                        }
                                        getComp = cs.getClass().getMethod("getComponent", Ref.class, Class.forName("com.hypixel.hytale.component.ComponentType"));
                                        Class<?> icbCls = Class.forName("com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock");
                                        Method getCompType = icbCls.getMethod("getComponentType", new Class[0]);
                                        Object compType = getCompType.invoke(null, new Object[0]);
                                        Object icbObj = null;
                                        try {
                                            icbObj = getComp.invoke(cs, blockRef, compType);
                                        }
                                        catch (Throwable ignored) {
                                            icbObj = null;
                                        }
                                        if (icbObj != null && icbCls.isInstance(icbObj) && this.getContainerViaECS(pos) == null) {
                                            try {
                                                Method getIC = icbCls.getMethod("getItemContainer", new Class[0]);
                                                Object cont = getIC.invoke(icbObj, new Object[0]);
                                                if (!(cont instanceof ItemContainer)) break block167;
                                                ItemContainer compContainer = (ItemContainer)cont;
                                                Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] component-first container capacity=" + compContainer.getCapacity());
                                                for (int slot = 0; slot < compContainer.getCapacity(); ++slot) {
                                                    try {
                                                        int transferAmount;
                                                        ItemStack stack4 = compContainer.getItemStack((short)slot);
                                                        if (stack4 == null) continue;
                                                        String blockKey3 = null;
                                                        try {
                                                            blockKey3 = stack4.getBlockKey();
                                                        }
                                                        catch (Throwable myBlockRef) {
                                                            // empty catch block
                                                        }
                                                        if (blockKey3 == null) {
                                                            blockKey3 = this.resolveItemStackKey(stack4);
                                                        }
                                                        if (!this.isItemAllowedByFilter(blockKey3) || this.isSingletonMode() && stack4.getQuantity() <= 1) continue;
                                                        int n = transferAmount = this.isSingletonMode() && (float)stack4.getQuantity() < this.data.tier * (float)Ev0Config.getTierMultiplier() ? stack4.getQuantity() - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)stack4.getQuantity());
                                                        if (transferAmount <= 0) continue;
                                                        int qb4 = HopperComponent.containerSlotQuantity(this.getItemContainer(), (short)0);
                                                        ItemStackSlotTransaction t4 = this.getItemContainer().addItemStackToSlot((short)0, stack4.withQuantity(transferAmount));
                                                        try {
                                                            Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] componentContainer transferAttempt slot=" + slot + " amt=" + transferAmount + " tx=" + (t4 == null ? "null" : String.valueOf(t4.succeeded())));
                                                        }
                                                        catch (Throwable throwable) {
                                                            // empty catch block
                                                        }
                                                        if (t4 == null || !t4.succeeded()) continue;
                                                        int added4 = HopperComponent.containerSlotQuantity(this.getItemContainer(), (short)0) - qb4;
                                                        if (added4 <= 0) continue;
                                                        try {
                                                            compContainer.removeItemStackFromSlot((short)slot, added4);
                                                        }
                                                        catch (Throwable throwable) {
                                                            // empty catch block
                                                        }
                                                        try {
                                                            if (this.w != null) {
                                                                Ref<ChunkStore> myBlockRef;
                                                                BlockComponentChunk bcc2;
                                                                Store<ChunkStore> cs2 = this.w.getChunkStore().getStore();
                                                                Ref<ChunkStore> colRef2 = this.w.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(this.cachedPosition.x, this.cachedPosition.z));
                                                                if (colRef2 != null && (bcc2 = cs2.getComponent(colRef2, BlockComponentChunk.getComponentType())) != null && (myBlockRef = bcc2.getEntityReference(ChunkUtil.indexBlockInColumn(this.cachedPosition.x, this.cachedPosition.y, this.cachedPosition.z))) != null) {
                                                                    this.putHopperComponent(cs2, myBlockRef, this);
                                                                }
                                                            }
                                                        }
                                                        catch (Throwable cs2) {
                                                            // empty catch block
                                                        }
                                                        return true;
                                                    }
                                                    catch (Throwable stack4) {
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
                                            hopperCompObj = getComp.invoke(cs, blockRef, hopperCompType);
                                        }
                                        catch (Throwable ignored) {
                                            hopperCompObj = null;
                                        }
                                        if (hopperCompObj == null || !hopperCompCls.isInstance(hopperCompObj)) break block165;
                                        try {
                                            Method getIC = hopperCompCls.getMethod("getItemContainer", new Class[0]);
                                            Object cont = getIC.invoke(hopperCompObj, new Object[0]);
                                            if (!(cont instanceof ItemContainer)) break block165;
                                            ItemContainer source = (ItemContainer)cont;
                                            for (int slot = 0; slot < source.getCapacity(); ++slot) {
                                                try {
                                                    int transferAmount;
                                                    ItemStack stack5 = source.getItemStack((short)slot);
                                                    if (stack5 == null) continue;
                                                    String blockKey4 = null;
                                                    try {
                                                        blockKey4 = stack5.getBlockKey();
                                                    }
                                                    catch (Throwable bcc2) {
                                                        // empty catch block
                                                    }
                                                    if (blockKey4 == null) {
                                                        blockKey4 = this.resolveItemStackKey(stack5);
                                                    }
                                                    if (!this.isItemAllowedByFilter(blockKey4) || this.isSingletonMode() && stack5.getQuantity() <= 1) continue;
                                                    int n = transferAmount = this.isSingletonMode() && (float)stack5.getQuantity() < this.data.tier * (float)Ev0Config.getTierMultiplier() ? stack5.getQuantity() - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)stack5.getQuantity());
                                                    if (transferAmount <= 0) continue;
                                                    ItemStack safeStack = stack5.withQuantity(transferAmount);
                                                    ItemStackSlotTransaction t5 = this.getItemContainer().addItemStackToSlot((short)0, safeStack);
                                                    if (t5 == null || !t5.succeeded()) continue;
                                                    try {
                                                        source.removeItemStackFromSlot((short)slot, transferAmount);
                                                    }
                                                    catch (Throwable throwable) {
                                                        // empty catch block
                                                    }
                                                    try {
                                                        this.spawnVisualFor(safeStack, false, pos, side, entities);
                                                    }
                                                    catch (Throwable throwable) {
                                                        // empty catch block
                                                    }
                                                    boolean putSucceeded = false;
                                                    try {
                                                        this.putHopperComponent(cs, blockRef, (HopperComponent)hopperCompObj);
                                                        putSucceeded = true;
                                                    }
                                                    catch (Throwable tt) {
                                                        Ev0Log.warn(LOGGER, "putHopperComponent failed: " + (tt == null ? "null" : tt.getMessage()));
                                                    }
                                                    try {
                                                        Ev0Log.info(LOGGER, "import-from-chained-hopper succeeded slot=" + slot + " putSucceeded=" + putSucceeded);
                                                    }
                                                    catch (Throwable tt) {
                                                        // empty catch block
                                                    }
                                                    try {
                                                        if (this.w != null) {
                                                            Ref<ChunkStore> myBlockRef;
                                                            BlockComponentChunk bcc2;
                                                            Store<ChunkStore> cs2 = this.w.getChunkStore().getStore();
                                                            Ref<ChunkStore> colRef2 = this.w.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(this.cachedPosition.x, this.cachedPosition.z));
                                                            if (colRef2 != null && (bcc2 = cs2.getComponent(colRef2, BlockComponentChunk.getComponentType())) != null && (myBlockRef = bcc2.getEntityReference(ChunkUtil.indexBlockInColumn(this.cachedPosition.x, this.cachedPosition.y, this.cachedPosition.z))) != null) {
                                                                try {
                                                                    this.putHopperComponent(cs2, myBlockRef, this);
                                                                }
                                                                catch (Throwable tt) {
                                                                    Ev0Log.warn(LOGGER, "putHopperComponent (self) failed: " + (tt == null ? "null" : tt.getMessage()));
                                                                }
                                                            }
                                                        }
                                                    }
                                                    catch (Throwable throwable) {
                                                        // empty catch block
                                                    }
                                                    return true;
                                                }
                                                catch (Throwable throwable) {
                                                    // empty catch block
                                                }
                                            }
                                            break block165;
                                        }
                                        catch (Throwable throwable) {
                                        }
                                    }
                                    catch (Throwable hopperCompCls) {}
                                    break block165;
                                }
                                catch (Throwable getComp) {
                                }
                            }
                            catch (Throwable getComp) {}
                            break block165;
                        }
                        Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] world reference (w) is null while inspecting pos=" + String.valueOf(pos));
                    }
                    catch (Throwable t6) {
                        try {
                            Ev0Log.warn(HytaleLogger.getLogger(), "[HopperDiag] exception while inspecting chunk/block refs: " + t6.getMessage());
                        }
                        catch (Throwable colRef) {
                            // empty catch block
                        }
                    }
                }
            }
            try {
                if (state == null || state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState")) break block168;
                ItemContainer direct = null;
                try {
                    if (state instanceof ItemContainer) {
                        direct = (ItemContainer)state;
                        Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] state is ItemContainer directly capacity=" + direct.getCapacity());
                    } else {
                        Object contObj = this.getItemContainerFromState(state);
                        direct = contObj instanceof ItemContainer ? (ItemContainer)contObj : this.getContainerFromItemContainerObject(contObj, 0);
                        if (direct != null) {
                            Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] extracted ItemContainer from state via reflection capacity=" + direct.getCapacity());
                        }
                    }
                }
                catch (Throwable ignored) {
                    direct = null;
                }
                if (direct == null) break block168;
                try {
                    Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] handling direct/extracted container for pos=" + String.valueOf(pos) + " capacity=" + direct.getCapacity());
                }
                catch (Throwable ignored) {
                    // empty catch block
                }
                int capd = direct.getCapacity();
                int[] hspDirect = getHopperInputSlots(state);
                for (int slot = 0; slot < capd; ++slot) {
                    try {
                        int transferAmount;
                        if (isInputSlot(hspDirect, slot)) continue;
                        stack = direct.getItemStack((short)slot);
                        if (stack == null) continue;
                        blockKey = null;
                        try {
                            blockKey = stack.getBlockKey();
                        }
                        catch (Throwable brMsg) {
                            // empty catch block
                        }
                        if (blockKey == null) {
                            blockKey = this.resolveItemStackKey(stack);
                        }
                        if (!this.isItemAllowedByFilter(blockKey) || this.isSingletonMode() && stack.getQuantity() <= 1) continue;
                        int n = transferAmount = this.isSingletonMode() && (float)stack.getQuantity() < this.data.tier * (float)Ev0Config.getTierMultiplier() ? stack.getQuantity() - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)stack.getQuantity());
                        if (transferAmount <= 0) continue;
                        int qtyBefore0 = HopperComponent.containerSlotQuantity(this.getItemContainer(), (short)0);
                        t = this.getItemContainer().addItemStackToSlot((short)0, stack.withQuantity(transferAmount));
                        try {
                            Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] directContainer transferAttempt slot=" + slot + " amt=" + transferAmount + " tx=" + (t == null ? "null" : String.valueOf(t.succeeded())));
                        }
                        catch (Throwable icbCls) {
                            // empty catch block
                        }
                        if (t == null || !t.succeeded()) continue;
                        int actuallyAdded = HopperComponent.containerSlotQuantity(this.getItemContainer(), (short)0) - qtyBefore0;
                        if (actuallyAdded <= 0) continue;
                        try {
                            direct.removeItemStackFromSlot((short)slot, actuallyAdded);
                        }
                        catch (Throwable icbCls) {
                            // empty catch block
                        }
                        try {
                            this.spawnVisualFor(stack.withQuantity(transferAmount), false, pos, side, entities);
                        }
                        catch (Throwable icbCls) {
                            // empty catch block
                        }
                        return true;
                    }
                    catch (Throwable stack6) {
                        // empty catch block
                    }
                }
            }
            catch (Throwable direct) {
                // empty catch block
            }
        }
        if (state != null && state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState")) {
            ItemContainer output = this.getContainerFromItemContainerObject(this.getItemContainerFromState(state), 2);
            if (output == null) {
                try {
                    Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] processingBench: output container null");
                }
                catch (Throwable capd) {
                    // empty catch block
                }
                return false;
            }
            try {
                Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] processingBench: output capacity=" + output.getCapacity());
            }
            catch (Throwable capd) {
                // empty catch block
            }
            int outCap = output.getCapacity();
            for (int slot = 0; slot < outCap; ++slot) {
                Ref<EntityStore> esx;
                stack = output.getItemStack((short)slot);
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
                if (!this.isItemAllowedByFilter(blockKey) || this.isSingletonMode() && stack.getQuantity() <= 1) continue;
                int transferAmount = this.isSingletonMode() && (float)stack.getQuantity() < this.data.tier * (float)Ev0Config.getTierMultiplier() ? stack.getQuantity() - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)stack.getQuantity());
                int n = transferAmount;
                if (transferAmount <= 0) continue;
                t = this.getItemContainer().addItemStackToSlot((short)0, stack.withQuantity(transferAmount));
                try {
                    Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] processingBench transferAttempt slot=" + slot + " amt=" + transferAmount + " tx=" + (t == null ? "null" : String.valueOf(t.succeeded())));
                }
                catch (Throwable icbCls) {
                    // empty catch block
                }
                if (t == null || !t.succeeded()) continue;
                Vector3i relRot = new Vector3i(WorldHelper.rotate((ConnectedBlockPatternRule.AdjacentSide)side, (int)this.getRotationIndex()).relativePosition);
                Vector3d velRot = new Vector3d((double)((Vector3i)((Object)relRot)).x * 0.35, 0.25, (double)((Vector3i)((Object)relRot)).z * 0.35);
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
            ItemContainer ecsContainer = this.getContainerViaECS(pos);
            if (ecsContainer != null) {
                for (int slot = 0; slot < ecsContainer.getCapacity(); ++slot) {
                    try {
                        stack = ecsContainer.getItemStack((short)slot);
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
                        if (!this.isItemAllowedByFilter(blockKey) || this.isSingletonMode() && stack.getQuantity() <= 1) continue;
                        int transferAmount = this.isSingletonMode() && (float)stack.getQuantity() < this.data.tier * (float)Ev0Config.getTierMultiplier() ? stack.getQuantity() - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)stack.getQuantity());
                        int n = transferAmount;
                        if (transferAmount <= 0 || (t = this.getItemContainer().addItemStackToSlot((short)0, stack.withQuantity(transferAmount))) == null || !t.succeeded()) continue;
                        try {
                            ecsContainer.removeItemStackFromSlot((short)slot, transferAmount);
                        }
                        catch (Throwable throwable) {
                            this.getItemContainer().removeItemStackFromSlot((short)0, transferAmount);
                            continue;
                        }
                        return true;
                    }
                    catch (Throwable stack7) {
                        // empty catch block
                    }
                }
            }
            return false;
        }
        try {
            Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] generic container capacity=" + container.getCapacity());
        }
        catch (Throwable ecsContainer) {
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
                if (!this.isItemAllowedByFilter(blockKey) || this.isSingletonMode() && stack.getQuantity() <= 1) continue;
                int transferAmount = this.isSingletonMode() && (float)stack.getQuantity() < this.data.tier * (float)Ev0Config.getTierMultiplier() ? stack.getQuantity() - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)stack.getQuantity());
                int n = transferAmount;
                if (transferAmount <= 0) continue;
                t = this.getItemContainer().addItemStackToSlot((short)0, stack.withQuantity(transferAmount));
                try {
                    Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] genericContainer transferAttempt slot=" + slot + " amt=" + transferAmount + " tx=" + (t == null ? "null" : String.valueOf(t.succeeded())));
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                if (t == null || !t.succeeded()) continue;
                try {
                    container.removeItemStackFromSlot((short)slot, transferAmount);
                }
                catch (Throwable throwable) {
                    this.getItemContainer().removeItemStackFromSlot((short)0, transferAmount);
                    continue;
                }
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
        entities.getResource(EntityModule.get().getItemSpatialResourceType()).getSpatialStructure().collectBox(boxMin, boxMax, rawResults);
        HopperComponent.perfInfo("[Hopper][Pickup] collectBox rawResults.size()=" + rawResults.size());
        try {
            Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] tryPickupItemEntities rawResults=" + rawResults.size());
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
        for (Ref ref : (java.util.List<Ref>) itemRefs) {
            int transferAmount;
            ItemStack stack;
            ItemComponent ic;
            if (ref == null || !ref.isValid() || this.l.contains(ref)) continue;
            if (entities.getComponent(ref, Intangible.getComponentType()) != null) {
                // empty if block
            }
            if ((ic = (ItemComponent) entities.getComponent(ref, ItemComponent.getComponentType())) == null || !ic.canPickUp() || (stack = ic.getItemStack()) == null || stack.isEmpty()) continue;
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
                Ev0Log.info(HytaleLogger.getLogger(), "[HopperDiag] pickup attempt ref=" + String.valueOf(ref) + " amt=" + transferAmount + " tx=" + (t == null ? "null" : String.valueOf(t.succeeded())));
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (t == null || !t.succeeded()) continue;
            int remaining = stack.getQuantity() - transferAmount;
            if (remaining <= 0) {
                entities.removeEntity(ref, RemoveReason.REMOVE);
            } else {
                TransformComponent tc = (TransformComponent) entities.getComponent(ref, TransformComponent.getComponentType());
                Vector3d dropPos = tc != null ? new Vector3d(tc.getPosition()) : new Vector3d((double)importPos.x + 0.5, (double)importPos.y + 0.5, (double)importPos.z + 0.5);
                entities.removeEntity(ref, RemoveReason.REMOVE);
                Holder newHolder = ItemComponent.generateItemDrop(entities, (ItemStack)stack.withQuantity(remaining), dropPos, Rotation3f.ZERO, (float)0.0f, (float)-1.0f, (float)0.0f);
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
            Store<ChunkStore> cs = world.getChunkStore().getStore();
            Ref<ChunkStore> chunkRef = world.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(bx, bz));
            if (chunkRef == null) {
                return;
            }
            BlockComponentChunk bcc = cs.getComponent(chunkRef, BlockComponentChunk.getComponentType());
            if (bcc == null) {
                return;
            }
            Ref<ChunkStore> blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn(bx, by, bz));
            if (blockRef == null) {
                return;
            }
            BlockUUIDComponent uuid = cs.getComponent(blockRef, BlockUUIDComponent.getComponentType());
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
            if ((mech = cs.getComponent(blockRef, ArcioMechanismComponent.getComponentType())) == null) {
                mech = new ArcioMechanismComponent("Hopper", 0, 1);
                if (commandBuffer != null) {
                    commandBuffer.putComponent(blockRef, ArcioMechanismComponent.getComponentType(), mech);
                } else {
                    cs.putComponent(blockRef, ArcioMechanismComponent.getComponentType(), mech);
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
            ArcioMechanismComponent mc;
            Ref<ChunkStore> blockRef;
            BlockComponentChunk bcc;
            Vector3i p = this.getBlockPosition();
            int bx = p.x;
            int by = p.y;
            int bz = p.z;
            Store<ChunkStore> cs = world.getChunkStore().getStore();
            Ref<ChunkStore> chunkRef = world.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(bx, bz));
            if (chunkRef != null && (bcc = cs.getComponent(chunkRef, BlockComponentChunk.getComponentType())) != null && (blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn(bx, by, bz))) != null && (mc = cs.getComponent(blockRef, ArcioMechanismComponent.getComponentType())) != null && mc.getStrongestInputSignal(world) > 0) {
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
            Store<ChunkStore> cs = world.getChunkStore().getStore();
            Vector3i p = this.getBlockPosition();
            int bx = p.x;
            int by = p.y;
            int bz = p.z;
            for (int[] off : offsets = new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}}) {
                ArcioMechanismComponent mc;
                Ref<ChunkStore> blockRef;
                BlockComponentChunk bcc;
                int nx = bx + off[0];
                int ny = by + off[1];
                int nz = bz + off[2];
                long adjChunkIdx = ChunkUtil.indexChunkFromBlock(nx, nz);
                if (world.getChunkIfInMemory(adjChunkIdx) == null) continue;
                Ref<ChunkStore> chunkRef = world.getChunkStore().getChunkReference(adjChunkIdx);
                if (chunkRef == null || (bcc = cs.getComponent(chunkRef, BlockComponentChunk.getComponentType())) == null || (blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn(nx, ny, nz))) == null || (mc = cs.getComponent(blockRef, ArcioMechanismComponent.getComponentType())) == null || mc.getStrongestInputSignal(world) <= 0) continue;
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
            components.getResource(EntityModule.get().getPlayerSpatialResourceType()).getSpatialStructure().collectCylinder(center, 4.0, queryHeight, results);
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
            components.getResource(EntityModule.get().getEntitySpatialResourceType()).getSpatialStructure().collectBox(min, max, results);
        }
        if (items) {
            components.getResource(EntityModule.get().getItemSpatialResourceType()).getSpatialStructure().collectCylinder(center, 2.0, Math.max(0.5, queryHeight), results);
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
            ComponentType<ChunkStore, HopperComponent> compType = Ev0Lib.getInstance().getHopperComponentType();
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
        block28: {
            try {
                Ev0Lib lib = Ev0Lib.getInstance();
                if (lib == null) {
                    return;
                }
                ComponentType<ChunkStore, HopperComponent> compType = lib.getHopperComponentType();
                if (compType == null) {
                    return;
                }
                Method put = null;
                for (Method mm : store.getClass().getMethods()) {
                    if (!mm.getName().equals("putComponent") || mm.getParameterCount() != 3) continue;
                    put = mm;
                    break;
                }
                if (put != null) {
                    put.invoke(store, ref, compType, comp);
                    return;
                }
                Method ensure = null;
                for (Method mm : store.getClass().getMethods()) {
                    if (!mm.getName().equals("ensureAndGetComponent") || mm.getParameterCount() != 2) continue;
                    ensure = mm;
                    break;
                }
                if (ensure == null) break block28;
                Object existing = ensure.invoke(store, ref, compType);
                if (existing == null) {
                    return;
                }
                try {
                    Field dataField = existing.getClass().getField("data");
                    dataField.set(existing, comp.data);
                }
                catch (Throwable dataField) {
                    // empty catch block
                }
                try {
                    Field whitelistField = existing.getClass().getField("whitelist");
                    whitelistField.set(existing, comp.whitelist);
                }
                catch (Throwable whitelistField) {
                    // empty catch block
                }
                try {
                    Field blacklistField = existing.getClass().getField("blacklist");
                    blacklistField.set(existing, comp.blacklist);
                }
                catch (Throwable blacklistField) {
                    // empty catch block
                }
                try {
                    Field filterModeField = existing.getClass().getField("filterMode");
                    filterModeField.set(existing, comp.filterMode);
                }
                catch (Throwable filterModeField) {
                    // empty catch block
                }
                try {
                    Field arcioModeField = existing.getClass().getField("arcioMode");
                    arcioModeField.set(existing, comp.arcioMode);
                }
                catch (Throwable arcioModeField) {
                    // empty catch block
                }
                try {
                    Field hopperTypeField = existing.getClass().getField("hopperType");
                    hopperTypeField.set(existing, comp.hopperType);
                }
                catch (Throwable hopperTypeField) {
                    // empty catch block
                }
                try {
                    Field wirelessNameField = existing.getClass().getField("wirelessName");
                    wirelessNameField.set(existing, comp.wirelessName);
                }
                catch (Throwable wirelessNameField) {
                    // empty catch block
                }
                try {
                    Field wirelessTargetXField = existing.getClass().getField("wirelessTargetX");
                    wirelessTargetXField.setInt(existing, comp.wirelessTargetX);
                }
                catch (Throwable wirelessTargetXField) {
                    // empty catch block
                }
                try {
                    Field wirelessTargetYField = existing.getClass().getField("wirelessTargetY");
                    wirelessTargetYField.setInt(existing, comp.wirelessTargetY);
                }
                catch (Throwable wirelessTargetYField) {
                    // empty catch block
                }
                try {
                    Field wirelessTargetZField = existing.getClass().getField("wirelessTargetZ");
                    wirelessTargetZField.setInt(existing, comp.wirelessTargetZ);
                }
                catch (Throwable throwable) {}
            }
            catch (Throwable t) {
                Ev0Log.warn(LOGGER, "putHopperComponent failed: " + (t == null ? "null" : t.getMessage()));
            }
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
            ChunkStore cs = store.getExternalData();
            LongSet chunkIndexes = cs.getChunkIndexes();
            if (chunkIndexes == null || chunkIndexes.isEmpty()) {
                return;
            }
            LongIterator longIterator = chunkIndexes.iterator();
            while (longIterator.hasNext()) {
                BlockComponentChunk bcc;
                long chunkIdx = (Long)longIterator.next();
                Ref<ChunkStore> colRef = cs.getChunkReference(chunkIdx);
                if (colRef == null || (bcc = store.getComponent(colRef, BlockComponentChunk.getComponentType())) == null) continue;
                for (Map.Entry entry : bcc.getEntityReferences().entrySet()) {
                    Ref blockRef = (Ref)entry.getValue();
                    if (blockRef == null || blockRef.getIndex() != myIdx) continue;
                    int blockIndex = (Integer)entry.getKey();
                    int lx = ChunkUtil.xFromBlockInColumn(blockIndex);
                    int wy = ChunkUtil.yFromBlockInColumn(blockIndex);
                    int lz = ChunkUtil.zFromBlockInColumn(blockIndex);
                    int wx = ChunkUtil.worldCoordFromLocalCoord(ChunkUtil.xOfChunkIndex(chunkIdx), lx);
                    int wz = ChunkUtil.worldCoordFromLocalCoord(ChunkUtil.zOfChunkIndex(chunkIdx), lz);
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
                Instant now = this.es.getResource(WorldTimeResource.getResourceType()).getGameTime();
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

    private static String oppositeSide(String side) {
        return switch (side) {
            case "East" -> "West";
            case "West" -> "East";
            case "North" -> "South";
            case "South" -> "North";
            case "Up" -> "Down";
            case "Down" -> "Up";
            default -> side;
        };
    }

    private void spawnVisualFor(ItemStack safeStack, boolean exportPhase, Vector3i pos, ConnectedBlockPatternRule.AdjacentSide side, Store<EntityStore> entities) {
        if (safeStack == null || safeStack.isEmpty()) {
            return;
        }
        if (this.nearbyBuffer.isEmpty()) {
            return;
        }
        Vector3i origin = exportPhase ? this.getBlockPosition() : pos;
        String throwDir = exportPhase ? HopperComponent.oppositeSide(side.toString()) : side.toString();
        for (Ref<EntityStore> targetRef : this.nearbyBuffer) {
            Ref<EntityStore> rs = ItemUtilsExtended.throwItem(this.getBlockType().getId(), throwDir, new Vector3d((double)origin.x, (double)origin.y, (double)origin.z), targetRef, entities, safeStack, Vector3d.ZERO, 0.0f);
            if (rs == null) continue;
            this.l.add(rs);
            try {
                this.visualMap.put(rs, safeStack);
                Instant now = this.es.getResource(WorldTimeResource.getResourceType()).getGameTime();
                this.visualSpawnTimes.put(rs, now);
            }
            catch (Exception exception) {}
        }
    }

    public ComponentType<EntityStore, FluidComponent> getFluidComponent() {
        return this.fluidComponent;
    }

    public void setFluidComponent(ComponentType<EntityStore, FluidComponent> fluidComponent) {
        this.fluidComponent = fluidComponent;
    }

    public void registerCodecs() {
        Ev0Lib.getInstance().getCodecRegistry(Interaction.CODEC).register("HopperInteraction", HopperInteraction.class, HopperInteraction.CODEC);
    }

    public void registerArcIOMechanism() {
        try {
            Class.forName("voidbond.arcio.ArcioPlugin");
            Class.forName("org.Ev0Mods.plugin.api.block.state.HopperArcioRegistration").getMethod("register", new Class[0]).invoke(null, new Object[0]);
            Ev0Log.info(LOGGER, "[HopperComponent] Registered ArcIO mechanism: Hopper");
        }
        catch (ClassNotFoundException ignored) {
            Ev0Log.info(LOGGER, "[HopperComponent] ArcIO not found - skipping mechanism registration");
        }
        catch (Exception e) {
            Ev0Log.warn(LOGGER, "[HopperComponent] Failed to register ArcIO mechanism: " + e.getMessage());
        }
    }

    public void logConfigValues() {
        Ev0Log.info(LOGGER, "Config values: tierMultiplier=" + Ev0Config.getTierMultiplier() + ", fluidTransferEnabled=" + Ev0Config.isFluidTransferEnabled());
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
        CODEC = BuilderCodec.builder(HopperComponent.class, HopperComponent::new)
            .append(new KeyedCodec<HopperProcessor.Data>("Data", HopperProcessor.Data.CODEC, true), (c, v) -> { ((HopperComponent)c).data = (HopperProcessor.Data)v; }, c -> ((HopperComponent)c).data).add()
            .append(new KeyedCodec<Rangef>("Duration", ProtocolCodecs.RANGEF, true), (c, v) -> { ((HopperComponent)c).duration = (Rangef)v; }, c -> ((HopperComponent)c).duration).add()
            .append(new KeyedCodec<Float>("Tier", Codec.FLOAT, true), (c, v) -> { ((HopperComponent)c).tier = ((Float)v).floatValue(); }, c -> Float.valueOf(((HopperComponent)c).tier)).add()
            .append(new KeyedCodec("Substitutions", Codec.STRING_ARRAY, true), (c, v) -> { ((HopperComponent)c).substitutions = (String[])v; }, c -> ((HopperComponent)c).substitutions).add()
            .append(new KeyedCodec<String>("FilterMode", Codec.STRING, true), (c, v) -> { ((HopperComponent)c).filterMode = v == null ? "Off" : (String)v; }, c -> ((HopperComponent)c).filterMode).add()
            .append(new KeyedCodec("Whitelist", Codec.STRING_ARRAY, true), (c, v) -> {
                if (v == null) { ((HopperComponent)c).whitelist.clear(); }
                else { ((HopperComponent)c).whitelist.clear(); ((HopperComponent)c).whitelist.addAll(Arrays.asList((String[])v)); }
            }, c -> ((HopperComponent)c).whitelist.toArray(new String[0])).add()
            .append(new KeyedCodec("Blacklist", Codec.STRING_ARRAY, true), (c, v) -> {
                if (v == null) { ((HopperComponent)c).blacklist.clear(); }
                else { ((HopperComponent)c).blacklist.clear(); ((HopperComponent)c).blacklist.addAll(Arrays.asList((String[])v)); }
            }, c -> ((HopperComponent)c).blacklist.toArray(new String[0])).add()
            .append(new KeyedCodec<String>("ArcioMode", Codec.STRING, true), (c, v) -> { ((HopperComponent)c).arcioMode = v == null ? "IgnoreSignal" : (String)v; }, c -> ((HopperComponent)c).arcioMode).add()
            .append(new KeyedCodec<String>("HopperType", Codec.STRING, true), (c, v) -> { ((HopperComponent)c).hopperType = v == null ? "Normal" : (String)v; }, c -> ((HopperComponent)c).hopperType).add()
            .append(new KeyedCodec<String>("WirelessName", Codec.STRING, true), (c, v) -> { ((HopperComponent)c).wirelessName = v == null ? "" : (String)v; }, c -> ((HopperComponent)c).wirelessName).add()
            .append(new KeyedCodec<Float>("WirelessTargetX", Codec.FLOAT, true), (c, v) -> { ((HopperComponent)c).wirelessTargetX = v == null ? Integer.MIN_VALUE : ((Float)v).intValue(); }, c -> ((HopperComponent)c).wirelessTargetX == Integer.MIN_VALUE ? null : Float.valueOf(((HopperComponent)c).wirelessTargetX)).add()
            .append(new KeyedCodec<Float>("WirelessTargetY", Codec.FLOAT, true), (c, v) -> { ((HopperComponent)c).wirelessTargetY = v == null ? Integer.MIN_VALUE : ((Float)v).intValue(); }, c -> ((HopperComponent)c).wirelessTargetY == Integer.MIN_VALUE ? null : Float.valueOf(((HopperComponent)c).wirelessTargetY)).add()
            .append(new KeyedCodec<Float>("WirelessTargetZ", Codec.FLOAT, true), (c, v) -> { ((HopperComponent)c).wirelessTargetZ = v == null ? Integer.MIN_VALUE : ((Float)v).intValue(); }, c -> ((HopperComponent)c).wirelessTargetZ == Integer.MIN_VALUE ? null : Float.valueOf(((HopperComponent)c).wirelessTargetZ)).add()
            .append(new KeyedCodec<String>("FacadeBlockId", Codec.STRING, true), (c, v) -> { ((HopperComponent)c).facadeBlockId = v == null ? "" : (String)v; }, c -> ((HopperComponent)c).facadeBlockId.isEmpty() ? null : ((HopperComponent)c).facadeBlockId).add()
            .append(new KeyedCodec<Float>("FacadeConnectionMask", Codec.FLOAT, true), (c, v) -> { ((HopperComponent)c).facadeConnectionMask = v == null ? 0 : ((Float)v).intValue(); }, c -> ((HopperComponent)c).facadeConnectionMask == 0 ? null : Float.valueOf(((HopperComponent)c).facadeConnectionMask)).add()
            .append(new KeyedCodec<Float>("FacadeRotation", Codec.FLOAT, true), (c, v) -> { ((HopperComponent)c).facadeRotation = v == null ? 0 : Math.floorMod(((Float)v).intValue(), 4); }, c -> ((HopperComponent)c).facadeRotation == 0 ? null : Float.valueOf(((HopperComponent)c).facadeRotation)).add()
            .append(new KeyedCodec<Float>("FacadeRotationX", Codec.FLOAT, true), (c, v) -> { ((HopperComponent)c).facadeRotationX = v == null ? 0 : Math.floorMod(((Float)v).intValue(), 4); }, c -> ((HopperComponent)c).facadeRotationX == 0 ? null : Float.valueOf(((HopperComponent)c).facadeRotationX)).add()
            .append(new KeyedCodec<Float>("FacadeRotationZ", Codec.FLOAT, true), (c, v) -> { ((HopperComponent)c).facadeRotationZ = v == null ? 0 : Math.floorMod(((Float)v).intValue(), 4); }, c -> ((HopperComponent)c).facadeRotationZ == 0 ? null : Float.valueOf(((HopperComponent)c).facadeRotationZ)).add()
            .build();
        KNOWN_CONTAINER_COMP_TYPES = null;
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

    /**
     * Reflectively calls {@code getHopperProtectedInputSlots()} on {@code state} if the method
     * exists (i.e. the state implements HopperSlotPolicy). Returns null if unavailable, meaning
     * no slot restriction applies. Keeps Ev0Lib free of any PhosphorTech dependency.
     */
    private static int[] getHopperInputSlots(Object state) {
        if (state == null) return null;
        try {
            java.lang.reflect.Method m = state.getClass().getMethod("getHopperProtectedInputSlots");
            Object result = m.invoke(state);
            if (result instanceof int[]) return (int[]) result;
        } catch (Throwable ignored) {}
        return null;
    }

    /** Returns true if {@code slot} is listed in {@code inputSlots}. */
    private static boolean isInputSlot(int[] inputSlots, int slot) {
        if (inputSlots == null) return false;
        for (int s : inputSlots) {
            if (s == slot) return true;
        }
        return false;
    }
}

