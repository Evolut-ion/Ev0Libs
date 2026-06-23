/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.math.vector.Vector3d
 *  com.hypixel.hytale.math.vector.Vector3f
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
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.math.vector.Vector3d;
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

public class HopperProcessor
implements TickableBlockState,
ItemContainerBlockState {
    private static final boolean PERF_DEBUG = false;
    public int fluid_id = 0;
    public Rangef duration = new Rangef(0.0f, 10.0f);
    public float tier;
    public static final BuilderCodec<HopperProcessor> CODEC = BuilderCodec.builder(HopperProcessor.class, HopperProcessor::new).build();
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
    private int exportFaceCursor = 0;
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
    private static volatile List<Object> KNOWN_CONTAINER_COMP_TYPES;
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
        if (this.w == null) {
            return null;
        }
        try {
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
            for (Object compType : HopperProcessor.getKnownContainerComponentTypes()) {
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

    private ConnectedBlockPatternRule.AdjacentSide detectAdjacentTransferFace(Vector3i hopperPos) {
        if (hopperPos == null || this.w == null) {
            return null;
        }
        ConnectedBlockPatternRule.AdjacentSide found = null;
        try {
            for (ConnectedBlockPatternRule.AdjacentSide side : ConnectedBlockPatternRule.AdjacentSide.values()) {
                try {
                    boolean hasTarget;
                    Vector3i rel = new Vector3i(side.relativePosition);
                    Vector3i targetPos = new Vector3i(hopperPos.x + ((Vector3i)((Object)rel)).x, hopperPos.y + ((Vector3i)((Object)rel)).y, hopperPos.z + ((Vector3i)((Object)rel)).z);
                    boolean bl = hasTarget = this.getContainerViaECS(targetPos) != null;
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

    private void configureWirelessFacesFromPlacement(Vector3i hopperPos) {
        if (hopperPos == null || this.data == null || this.w == null) {
            return;
        }
        String wt = this.data.hopperType;
        boolean isWirelessExport = "WirelessExport".equalsIgnoreCase(wt);
        boolean isWirelessImport = "WirelessImport".equalsIgnoreCase(wt);
        if (!isWirelessExport && !isWirelessImport) {
            return;
        }
        boolean needsImportFace = isWirelessExport && (this.data.importFaces == null || this.data.importFaces.length == 0);
        boolean needsExportFace = isWirelessImport && (this.data.exportFaces == null || this.data.exportFaces.length == 0);
        boolean facesLikelySouthDefaultImport = isWirelessExport && this.data.importFaces != null && this.data.importFaces.length == 1 && this.data.importFaces[0] == ConnectedBlockPatternRule.AdjacentSide.South;
        boolean facesLikelySouthDefaultExport = isWirelessImport && this.data.exportFaces != null && this.data.exportFaces.length == 1 && this.data.exportFaces[0] == ConnectedBlockPatternRule.AdjacentSide.South;
        boolean pendingPlacementSelf = false;
        boolean pendingPlacementNear = false;
        try {
            pendingPlacementSelf = WirelessHopperPlaceSystem.PENDING_TARGET_BLOCKS.containsKey(hopperPos);
            for (ConnectedBlockPatternRule.AdjacentSide side : ConnectedBlockPatternRule.AdjacentSide.values()) {
                Vector3i adj = new Vector3i(hopperPos.x + ((Vector3i)((Object)side.relativePosition)).x, hopperPos.y + ((Vector3i)((Object)side.relativePosition)).y, hopperPos.z + ((Vector3i)((Object)side.relativePosition)).z);
                if (!WirelessHopperPlaceSystem.PENDING_TARGET_BLOCKS.containsKey(adj)) continue;
                pendingPlacementNear = true;
                break;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        // Only run face detection when a placement event is actually pending — avoids re-overwriting
        // the configured face on every subsequent tick via detectAdjacentTransferFace.
        if (!pendingPlacementSelf && !pendingPlacementNear) {
            return;
        }
        ConnectedBlockPatternRule.AdjacentSide face = null;
        try {
            for (ConnectedBlockPatternRule.AdjacentSide side : ConnectedBlockPatternRule.AdjacentSide.values()) {
                Vector3i adj = new Vector3i(hopperPos.x + ((Vector3i)((Object)side.relativePosition)).x, hopperPos.y + ((Vector3i)((Object)side.relativePosition)).y, hopperPos.z + ((Vector3i)((Object)side.relativePosition)).z);
                Long placedAt = WirelessHopperPlaceSystem.PENDING_TARGET_BLOCKS.remove(adj);
                WirelessHopperPlaceSystem.PENDING_PLACEMENT_FACES.remove(adj);
                if (placedAt == null) continue;
                face = side;
                break;
            }
            if (face == null) {
                WirelessHopperPlaceSystem.PENDING_TARGET_BLOCKS.remove(hopperPos);
                WirelessHopperPlaceSystem.PENDING_PLACEMENT_FACES.remove(hopperPos);
                face = this.detectAdjacentTransferFace(hopperPos);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        if (face == null) {
            return;
        }
        if (isWirelessImport && (needsExportFace || facesLikelySouthDefaultExport || pendingPlacementSelf || pendingPlacementNear)) {
            this.data.exportFaces = new ConnectedBlockPatternRule.AdjacentSide[]{face};
        }
        if (isWirelessExport && (needsImportFace || facesLikelySouthDefaultImport || pendingPlacementSelf || pendingPlacementNear)) {
            this.data.importFaces = new ConnectedBlockPatternRule.AdjacentSide[]{face};
        }
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

    public boolean isSingletonMode() {
        return "Singleton".equalsIgnoreCase(this.filterMode);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isItemAllowedByFilter(String blockKey) {
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
            this.setItemContainer(new SimpleItemContainer((short)1));
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
            if (this.data != null && this.w != null) {
                String wType = this.data.hopperType;
                if ("WirelessExport".equalsIgnoreCase(wType) || "WirelessImport".equalsIgnoreCase(wType)) {
                    if (this.data.wirelessTargetY != Integer.MIN_VALUE) {
                        Vector3i partner = new Vector3i(this.data.wirelessTargetX, this.data.wirelessTargetY, this.data.wirelessTargetZ);
                        WirelessHelpers.clearWirelessTargetOnly(this.w, partner);
                    }
                    Vector3i myPos = this.getBlockPosition();
                    if (myPos != null) {
                        WirelessRegistry.unregister(this.w, myPos);
                    }
                }
            }
        }
        catch (Throwable ignored) {}
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
        Store<EntityStore> entities;
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
            this.w = store.getExternalData().getWorld();
            entities = this.w.getEntityStore().getStore();
            this.es = entities;
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
            if (ARCIO_PRESENT && "EnableWhenSignal".equals(this.arcioMode) && !this.isArcioActive(this.w)) {
                return;
            }
            this.timerV += 1.0;
            boolean bl = this.drop = this.timerV >= (double)this.duration.max;
            if (this.drop) {
                this.timerV = 0.0;
            }
            Vector3i pos = this.getBlockPosition();
            this.configureWirelessFacesFromPlacement(pos);
            String _wt = this.data != null && this.data.hopperType != null ? this.data.hopperType : "Normal";
            boolean isWireless = "WirelessExport".equalsIgnoreCase(_wt) || "WirelessImport".equalsIgnoreCase(_wt);
            this.nearbyBuffer.clear();
            ++this.tickCounter;
            int phase = this.tickCounter % 180;
            boolean doExport = phase == 0;
            boolean bl2 = doImport = phase == 90;
            if (this.tickCounter % 180 == 0) {
                try {
                    List rawPlayers = SpatialResource.getThreadLocalReferenceList();
                    Vector3d center = new Vector3d((double)pos.x, (double)pos.y, (double)pos.z);
                    entities.getResource(EntityModule.get().getPlayerSpatialResourceType()).getSpatialStructure().collectCylinder(center, 4.0, Math.max(1.0f, this.data.height), rawPlayers);
                    this.playersNearbyCached = !rawPlayers.isEmpty();
                    rawPlayers.clear();
                }
                catch (Exception rawPlayers) {
                    // empty catch block
                }
            }
            if (doExport) {
                ItemStack have = this.getItemContainer().getItemStack((short)0);
                if (have != null && (this.playersNearbyCached || isWireless)) {
                    this.nearbyBuffer = HopperProcessor.getAllEntitiesInBox(this, pos, this.data.height, entities, this.data.players, this.data.entities, this.data.items);
                } else {
                    this.nearbyBuffer.clear();
                }
                this.runExportPhase(pos, entities);
            }
            if (doImport && !"WirelessImport".equalsIgnoreCase(_wt)) {
                boolean hopperHasSpace;
                ItemStack have2 = this.getItemContainer().getItemStack((short)0);
                boolean bl3 = hopperHasSpace = have2 == null || have2.getQuantity() < 100;
                if (hopperHasSpace && (this.playersNearbyCached || isWireless)) {
                    this.nearbyBuffer = HopperProcessor.getAllEntitiesInBox(this, pos, this.data.height, entities, this.data.players, this.data.entities, this.data.items);
                } else {
                    this.nearbyBuffer.clear();
                }
                for (ConnectedBlockPatternRule.AdjacentSide side : this.data.importFaces) {
                    Vector3i importPos = new Vector3i(pos.x + ((Vector3i)((Object)WorldHelper.rotate((ConnectedBlockPatternRule.AdjacentSide)side, (int)this.getRotationIndex()).relativePosition)).x, pos.y + ((Vector3i)((Object)WorldHelper.rotate((ConnectedBlockPatternRule.AdjacentSide)side, (int)this.getRotationIndex()).relativePosition)).y, pos.z + ((Vector3i)((Object)WorldHelper.rotate((ConnectedBlockPatternRule.AdjacentSide)side, (int)this.getRotationIndex()).relativePosition)).z);
                    WorldChunk chunk = this.w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(importPos.x, importPos.z));
                    if (chunk == null) continue;
                    int targetFluidId = EngineCompat.getFluidId(chunk, importPos.x, importPos.y, importPos.z);
                    Object state = EngineCompat.getState(chunk, importPos.x, importPos.y, importPos.z);
                    boolean hasContainer = state != null && (state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState") || state.getClass().getSimpleName().contains("ItemContainer") || this.getItemContainerFromState(state) != null) || state == null && this.getContainerViaECS(importPos) != null;
                    ItemStack currentItem = this.getItemContainer().getItemStack((short)0);
                    if (Ev0Config.isFluidTransferEnabled() && targetFluidId != 0 && currentItem == null && !hasContainer) {
                        // Resolve the fluid by its persistent string id, not the numeric
                        // asset-map index which shifts between versions/mod sets.
                        String fluidKey = EngineCompat.getFluidKey(chunk, importPos.x, importPos.y, importPos.z);
                        String bucketKey = EngineCompat.filledBucketForFluid(fluidKey);
                        ItemStack bucketStack = bucketKey == null ? null : new ItemStack(bucketKey, 1, null);
                        if (bucketStack != null) {
                            this.itemContainer.addItemStackToSlot((short)0, bucketStack);
                            this.pendingFluidRemovals.add(new long[]{importPos.x, importPos.y, importPos.z});
                            continue;
                        }
                    }
                    HopperProcessor.perfInfo("[Hopper][Import] side=" + String.valueOf((Object)side) + " importPos=" + String.valueOf(importPos) + " state=" + (state == null ? "null" : state.getClass().getSimpleName()) + " hasContainer=" + hasContainer);
                    if (this.tryImportFromContainer(chunk, importPos, entities, side)) {
                        HopperProcessor.perfInfo("[Hopper][Import] tryImportFromContainer SUCCESS side=" + String.valueOf((Object)side));
                        break;
                    }
                    HopperProcessor.perfInfo("[Hopper][Import] tryImportFromContainer failed, hasContainer=" + hasContainer + " -> will tryPickup=" + !hasContainer);
                    if (hasContainer || !this.tryPickupItemEntities(importPos, entities)) continue;
                    HopperProcessor.perfInfo("[Hopper][Import] tryPickupItemEntities SUCCESS at " + String.valueOf(importPos));
                    this.runExportPhase(pos, entities);
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
        if (state == null) {
            ItemStack have2;
            ItemStackSlotTransaction t;
            ItemContainer ecsContainer = this.getContainerViaECS(pos);
            if (ecsContainer == null) {
                return false;
            }
            if (!exportPhase) {
                for (int slot = 0; slot < ecsContainer.getCapacity(); ++slot) {
                    int transferAmount;
                    ItemStack stack = ecsContainer.getItemStack((short)slot);
                    if (stack == null) continue;
                    String probeKey = null;
                    try {
                        probeKey = stack.getBlockKey();
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    if (probeKey == null) {
                        probeKey = this.resolveItemStackKey(stack);
                    }
                    if (!this.isItemAllowedByFilter(probeKey)) continue;
                    int srcAvailable = stack.getQuantity();
                    if (this.isSingletonMode() && srcAvailable <= 1) continue;
                    int n = transferAmount = this.isSingletonMode() && (float)srcAvailable < this.data.tier * (float)Ev0Config.getTierMultiplier() ? srcAvailable - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)srcAvailable);
                    if (transferAmount <= 0 || !(t = this.getItemContainer().addItemStackToSlot((short)0, stack.withQuantity(transferAmount))).succeeded()) continue;
                    ecsContainer.removeItemStackFromSlot((short)slot, transferAmount);
                    return true;
                }
            }
            if (exportPhase && (have2 = this.getItemContainer().getItemStack((short)0)) != null && have2.getQuantity() > 0) {
                int haveQty = have2.getQuantity();
                if (this.isSingletonMode() && haveQty <= 1) {
                    return false;
                }
                int transferAmount = this.isSingletonMode() && (float)haveQty < this.data.tier * (float)Ev0Config.getTierMultiplier() ? haveQty - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)haveQty);
                ItemStack safeStack = have2.withQuantity(transferAmount);
                for (int slot = 0; slot < ecsContainer.getCapacity(); ++slot) {
                    t = ecsContainer.addItemStackToSlot((short)slot, safeStack);
                    if (!t.succeeded()) continue;
                    this.spawnVisualFor(safeStack, exportPhase, pos, side, entities);
                    try {
                        this.getItemContainer().removeItemStackFromSlot((short)0, transferAmount);
                    } catch (Throwable th) {
                        ecsContainer.removeItemStackFromSlot((short)slot, transferAmount);
                        continue;
                    }
                    return true;
                }
            }
            return false;
        }
        if (!state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState") && !state.getClass().getSimpleName().contains("ItemContainer") && this.getItemContainerFromState(state) == null) {
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
                    catch (Throwable t) {
                        // empty catch block
                    }
                    if (probeKeyPb == null) {
                        probeKeyPb = this.resolveItemStackKey(stack);
                    }
                    if (!this.isItemAllowedByFilter(probeKeyPb)) continue;
                    int pbAvailable = stack.getQuantity();
                    if (this.isSingletonMode() && pbAvailable <= 1) continue;
                    int n = transferAmount = this.isSingletonMode() && (float)pbAvailable < this.data.tier * (float)Ev0Config.getTierMultiplier() ? pbAvailable - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)pbAvailable);
                    if (transferAmount <= 0) continue;
                    ItemStack safeStack = stack.withQuantity(transferAmount);
                    ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short)0, safeStack);
                    if (!t.succeeded()) continue;
                    output.removeItemStackFromSlot((short)slot, transferAmount);
                    return true;
                }
            } else {
                ItemStack have3 = this.getItemContainer().getItemStack((short)0);
                if (have3 != null && have3.getQuantity() > 0) {
                    int transferAmount;
                    int haveQty = have3.getQuantity();
                    if (this.isSingletonMode() && haveQty <= 1) {
                        return false;
                    }
                    int n = transferAmount = this.isSingletonMode() && (float)haveQty < this.data.tier * (float)Ev0Config.getTierMultiplier() ? haveQty - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)haveQty);
                    if (transferAmount <= 0) {
                        return false;
                    }
                    ItemStack safeStack = have3.withQuantity(transferAmount);
                    for (int c = 0; c <= 1; ++c) {
                        ItemContainer input = this.getContainerFromItemContainerObject(this.getItemContainerFromState(bench), c);
                        for (int slot2 = 0; slot2 < input.getCapacity(); ++slot2) {
                            ItemStackSlotTransaction t = input.addItemStackToSlot((short)slot2, safeStack);
                            if (!t.succeeded()) continue;
                            this.spawnVisualFor(safeStack, exportPhase, pos, side, entities);
                            try {
                                this.getItemContainer().removeItemStackFromSlot((short)0, transferAmount);
                            } catch (Throwable th) {
                                input.removeItemStackFromSlot((short)slot2, transferAmount);
                                continue;
                            }
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
            IDrawerContainer drawerContainer = (IDrawerContainer)((Object)container);
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
                    catch (Throwable slot2) {
                        // empty catch block
                    }
                    if (probeKey == null) {
                        probeKey = this.resolveItemStackKey(slotItem);
                    }
                    if (!this.isItemAllowedByFilter(probeKey) || this.isSingletonMode() && slotQty <= 1) continue;
                    int n = transferAmount = this.isSingletonMode() && (float)slotQty < this.data.tier * (float)Ev0Config.getTierMultiplier() ? slotQty - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)slotQty);
                    if (transferAmount <= 0 || !(t = this.getItemContainer().addItemStackToSlot((short)0, slotItem.withQuantity(transferAmount))).succeeded()) continue;
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
            ItemStack have4 = this.getItemContainer().getItemStack((short)0);
            if (have4 != null && have4.getQuantity() > 0) {
                int room;
                int actualTransfer;
                int slotCap;
                int slotQty;
                ItemStack slotItem;
                short slot;
                int haveQty = have4.getQuantity();
                if (this.isSingletonMode() && haveQty <= 1) {
                    return false;
                }
                int transferAmount2 = this.isSingletonMode() && (float)haveQty < this.data.tier * (float)Ev0Config.getTierMultiplier() ? haveQty - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)haveQty);
                short matchedSlot = -1;
                int matchedQty = 0;
                int matchedCap = 0;
                for (slot = 0; slot < drawerContainer.getSlotCount(); slot = (short)(slot + 1)) {
                    slotItem = drawerContainer.getSlotItem(slot);
                    if (ItemStack.isEmpty(slotItem) || !slotItem.isStackableWith(have4)) continue;
                    slotQty = drawerContainer.getSlotQuantity(slot);
                    slotCap = drawerContainer.getSlotStackCapacity(slot);
                    if (slotCap - slotQty <= 0 || drawerContainer.testCantAddToSlot(slot, have4, slotItem)) continue;
                    matchedSlot = slot;
                    matchedQty = slotQty;
                    matchedCap = slotCap;
                    break;
                }
                if (matchedSlot == -1) {
                    for (slot = 0; slot < drawerContainer.getSlotCount(); slot = (short)(slot + 1)) {
                        slotItem = drawerContainer.getSlotItem(slot);
                        if (!ItemStack.isEmpty(slotItem)) continue;
                        slotQty = drawerContainer.getSlotQuantity(slot);
                        slotCap = drawerContainer.getSlotStackCapacity(slot);
                        if (slotCap - slotQty <= 0) continue;
                        matchedSlot = slot;
                        matchedQty = slotQty;
                        matchedCap = slotCap;
                        break;
                    }
                }
                if (matchedSlot != -1 && (actualTransfer = Math.min(transferAmount2, room = matchedCap - matchedQty)) > 0) {
                    short fSlot = matchedSlot;
                    int fNewQty = matchedQty + actualTransfer;
                    int fActual = actualTransfer;
                    ItemStack fHave = have4;
                    drawerContainer.writeAction(() -> {
                        drawerContainer.setSlot(fSlot, fHave.withQuantity(fNewQty));
                        return null;
                    });
                    this.spawnVisualFor(have4.withQuantity(fActual), exportPhase, pos, side, entities);
                    try {
                        this.getItemContainer().removeItemStackFromSlot((short)0, fActual);
                    } catch (Throwable th) {
                    }
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
                catch (Throwable transferAmount2) {
                    // empty catch block
                }
                if (probeKey == null) {
                    probeKey = this.resolveItemStackKey(stack);
                }
                if (!this.isItemAllowedByFilter(probeKey)) continue;
                int srcAvailable = stack.getQuantity();
                if (this.isSingletonMode() && srcAvailable <= 1) continue;
                int n = transferAmount = this.isSingletonMode() && (float)srcAvailable < this.data.tier * (float)Ev0Config.getTierMultiplier() ? srcAvailable - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)srcAvailable);
                if (transferAmount <= 0) continue;
                ItemStack safeStack = stack.withQuantity(transferAmount);
                ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short)0, safeStack);
                if (!t.succeeded()) continue;
                container.removeItemStackFromSlot((short)slot, transferAmount);
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
            for (int slot = 0; slot < container.getCapacity(); ++slot) {
                ItemStackSlotTransaction t = container.addItemStackToSlot((short)slot, safeStack);
                if (!t.succeeded()) continue;
                this.spawnVisualFor(safeStack, exportPhase, pos, side, entities);
                try {
                    this.getItemContainer().removeItemStackFromSlot((short)0, transferAmount);
                } catch (Throwable th) {
                    container.removeItemStackFromSlot((short)slot, transferAmount);
                    continue;
                }
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
        Vector3i rel = new Vector3i(WorldHelper.rotate((ConnectedBlockPatternRule.AdjacentSide)side, (int)this.getRotationIndex()).relativePosition);
        Vector3i hopperBlock = this.getBlockPosition();
        Vector3d hopperCenter = new Vector3d((double)hopperBlock.x + 0.5 + 1.0, (double)hopperBlock.y + 0.5, (double)hopperBlock.z + 0.5);
        Vector3d sourceCenter = new Vector3d((double)pos.x + 0.5 + 1.0, (double)pos.y + 0.5, (double)pos.z + 0.5);
        Vector3d spawnPos = exportPhase ? hopperCenter : sourceCenter;
        Vector3d vector3d = velocity = exportPhase ? new Vector3d((double)(-((Vector3i)((Object)rel)).x) * 0.35, 0.25, (double)(-((Vector3i)((Object)rel)).z) * 0.35) : new Vector3d((double)((Vector3i)((Object)rel)).x * 0.35, 0.25, (double)((Vector3i)((Object)rel)).z * 0.35);
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
                    Instant now = entities != null ? entities.getResource(WorldTimeResource.getResourceType()).getGameTime() : Instant.now();
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
        Holder itemEntityHolder = ItemComponent.generateItemDrop(entities, (ItemStack)safeStack, (Vector3d)new Vector3d(spawnPos.x, spawnPos.y, spawnPos.z), Rotation3f.ZERO, (float)0.0f, (float)-1.0f, (float)0.0f);
        if (itemEntityHolder == null) {
            return;
        }
        ItemComponent itemComponent = (ItemComponent) itemEntityHolder.getComponent(ItemComponent.getComponentType());
        if (itemComponent != null) {
            itemComponent.setPickupDelay(1.0E8f);
            itemComponent.setRemovedByPlayerPickup(false);
            itemComponent.computeDynamicLight();
        }
        try {
            ((PhysicsValues) itemEntityHolder.ensureAndGetComponent(PhysicsValues.getComponentType())).replaceValues(new PhysicsValues(0.0, 0.0, true));
            ((Velocity) itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(velocity.x, velocity.y, velocity.z);
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
        Ref<EntityStore> spawned = entities.addEntity(itemEntityHolder, AddReason.SPAWN);
        if (spawned != null) {
            TransformComponent tc = entities.getComponent(spawned, TransformComponent.getComponentType());
            if (tc != null) {
                tc.setPosition(new Vector3d(spawnPos.x, spawnPos.y, spawnPos.z));
            }
            this.l.add(spawned);
            try {
                this.visualMap.put(spawned, safeStack);
                Instant now = entities != null ? entities.getResource(WorldTimeResource.getResourceType()).getGameTime() : Instant.now();
                this.visualSpawnTimes.put(spawned, now);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private void runExportPhase(Vector3i pos, Store<EntityStore> entities) {
        int n;
        ItemContainer ic = this.getItemContainer();
        if (ic == null) {
            return;
        }
        int cap = ic.getCapacity();
        int foundSlot = -1;
        ItemStack currentItemFast = null;
        String fastKey = null;
        for (int slotIdx = 0; slotIdx < cap; slotIdx++) {
            ItemStack slotStack = ic.getItemStack((short)slotIdx);
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
                ItemStack slot0Stack = ic.getItemStack((short)0);
                int foundQty = currentItemFast.getQuantity();
                ic.removeItemStackFromSlot((short)foundSlot, foundQty);
                if (slot0Stack != null) {
                    int slot0Qty = slot0Stack.getQuantity();
                    ic.removeItemStackFromSlot((short)0, slot0Qty);
                    ic.addItemStackToSlot((short)foundSlot, slot0Stack);
                }
                ic.addItemStackToSlot((short)0, currentItemFast);
            } catch (Throwable throwable) {
                return;
            }
        }
        ConnectedBlockPatternRule.AdjacentSide[] exportFaces = this.data.exportFaces;
        int n2 = n = exportFaces == null ? 0 : exportFaces.length;
        if (n <= 0) {
            return;
        }
        int rrStart = Math.floorMod(this.exportFaceCursor, n);
        for (int i = 0; i < n; ++i) {
            String itemKey;
            ConnectedBlockPatternRule.AdjacentSide side = exportFaces[(rrStart + i) % n];
            boolean exportedThisFace = false;
            Vector3i exportPos = new Vector3i(pos.x + ((Vector3i)((Object)WorldHelper.rotate((ConnectedBlockPatternRule.AdjacentSide)side, (int)this.getRotationIndex()).relativePosition)).x, pos.y + ((Vector3i)((Object)WorldHelper.rotate((ConnectedBlockPatternRule.AdjacentSide)side, (int)this.getRotationIndex()).relativePosition)).y, pos.z + ((Vector3i)((Object)WorldHelper.rotate((ConnectedBlockPatternRule.AdjacentSide)side, (int)this.getRotationIndex()).relativePosition)).z);
            WorldChunk chunk = this.w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(exportPos.x, exportPos.z));
            if (chunk == null) continue;
            Object state = EngineCompat.getState(chunk, exportPos.x, exportPos.y, exportPos.z);
            int targetFluidId = EngineCompat.getFluidId(chunk, exportPos.x, exportPos.y, exportPos.z);
            boolean hasContainer = state != null && (state.getClass().getName().equals("com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState") || state.getClass().getSimpleName().contains("ItemContainer") || this.getItemContainerFromState(state) != null) || state == null && this.getContainerViaECS(exportPos) != null;
            ItemStack currentItem = this.getItemContainer().getItemStack((short)0);
            // Filled buckets are kept as-is: the hopper no longer empties a filled bucket
            // into an adjacent fluid (which left a plain Container_Bucket behind).
            boolean transferred = !exportedThisFace && this.tryTransferToOrFromContainer(state, exportPos, side, entities, true);
            boolean bl = exportedThisFace = exportedThisFace || transferred;
            if (!(exportedThisFace || transferred || currentItem == null || hasContainer || targetFluidId != 0)) {
                if (this.isSingletonMode() && currentItem.getQuantity() <= 1) continue;
                if (EngineCompat.getBlockType(chunk, exportPos.x, exportPos.y, exportPos.z) == null) {
                    EngineCompat.setBlock(chunk, exportPos.x, exportPos.y, exportPos.z, currentItem.getBlockKey());
                    try {
                        this.getItemContainer().removeItemStackFromSlot((short)0, 1);
                    } catch (Throwable th) {
                        EngineCompat.setBlock(chunk, exportPos.x, exportPos.y, exportPos.z, BlockType.EMPTY);
                        continue;
                    }
                    exportedThisFace = true;
                }
            }
            if (!exportedThisFace) continue;
            this.exportFaceCursor = Math.floorMod(rrStart + i + 1, n);
            if (this.data.exportOnce) break;
        }
    }

    private boolean handleExport(World world, Store<EntityStore> entities) {
        int transferAmount;
        ItemStack source = this.getItemContainer().getItemStack((short)0);
        if (source == null) {
            return false;
        }
        int srcQty = source.getQuantity();
        if (this.isSingletonMode() && srcQty <= 1) {
            return false;
        }
        int n = transferAmount = this.isSingletonMode() && (float)srcQty < this.data.tier * 2.0f ? srcQty - 1 : (int)Math.min(this.data.tier * 2.0f, (float)srcQty);
        if (transferAmount <= 0) {
            return false;
        }
        Vector3i pos = this.getBlockPosition();
        for (ConnectedBlockPatternRule.AdjacentSide side : this.data.exportFaces) {
            Vector3i targetPos = new Vector3i(pos.x + ((Vector3i)((Object)WorldHelper.rotate((ConnectedBlockPatternRule.AdjacentSide)side, (int)this.getRotationIndex()).relativePosition)).x, pos.y + ((Vector3i)((Object)WorldHelper.rotate((ConnectedBlockPatternRule.AdjacentSide)side, (int)this.getRotationIndex()).relativePosition)).y, pos.z + ((Vector3i)((Object)WorldHelper.rotate((ConnectedBlockPatternRule.AdjacentSide)side, (int)this.getRotationIndex()).relativePosition)).z);
            WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(targetPos.x, targetPos.z));
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
            int transferAmount;
            Object bench = state;
            ItemStack sourcex = this.getItemContainer().getItemStack((short)0);
            if (sourcex == null) {
                return false;
            }
            int srcxQty = sourcex.getQuantity();
            if (this.isSingletonMode() && srcxQty <= 1) {
                return false;
            }
            int n = transferAmount = this.isSingletonMode() && (float)srcxQty < this.data.tier * 2.0f ? srcxQty - 1 : (int)Math.min(this.data.tier * 2.0f, (float)srcxQty);
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
                    try {
                        this.getItemContainer().removeItemStackFromSlot((short)0, transferAmount);
                    } catch (Throwable th) {
                        input.removeItemStackFromSlot((short)slot, transferAmount);
                        continue;
                    }
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
            try {
                this.getItemContainer().removeItemStackFromSlot((short)0, amount);
            } catch (Throwable th) {
                target.removeItemStackFromSlot((short)slot, amount);
                continue;
            }
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
                if (EngineCompat.getBlockType(chunk, pos.x, pos.y, pos.z) == null) break block16;
                try {
                    this.getItemContainer().removeItemStackFromSlot((short)0, amount);
                } catch (Throwable th) {
                    EngineCompat.setBlock(chunk, pos.x, pos.y, pos.z, BlockType.EMPTY);
                    break block16;
                }
                try {
                    if (this.l == null || this.l.isEmpty() || this.es == null) break block16;
                    Iterator<Ref<EntityStore>> it = this.l.iterator();
                    while (it.hasNext()) {
                        Ref<EntityStore> ref = it.next();
                        try {
                            if (ref != null && ref.isValid()) {
                                TransformComponent tc = this.es.getComponent(ref, TransformComponent.getComponentType());
                                if (tc == null) continue;
                                Vector3d p = new Vector3d(tc.getPosition());
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
        Vector3i pos = this.getBlockPosition();
        for (ConnectedBlockPatternRule.AdjacentSide side : this.data.importFaces) {
            Vector3i targetPos = new Vector3i(pos.x + ((Vector3i)((Object)WorldHelper.rotate((ConnectedBlockPatternRule.AdjacentSide)side, (int)this.getRotationIndex()).relativePosition)).x, pos.y + ((Vector3i)((Object)WorldHelper.rotate((ConnectedBlockPatternRule.AdjacentSide)side, (int)this.getRotationIndex()).relativePosition)).y, pos.z + ((Vector3i)((Object)WorldHelper.rotate((ConnectedBlockPatternRule.AdjacentSide)side, (int)this.getRotationIndex()).relativePosition)).z);
            WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(targetPos.x, targetPos.z));
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
                Ref<EntityStore> ref;
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
                if (!this.isItemAllowedByFilter(blockKey)) continue;
                int available = stack.getQuantity();
                if (this.isSingletonMode() && available <= 1) continue;
                int n = transferAmount = this.isSingletonMode() && (float)available < this.data.tier * (float)Ev0Config.getTierMultiplier() ? available - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)available);
                if (transferAmount <= 0 || !(t = this.getItemContainer().addItemStackToSlot((short)0, stack.withQuantity(transferAmount))).succeeded()) continue;
                Vector3i relRot = new Vector3i(WorldHelper.rotate((ConnectedBlockPatternRule.AdjacentSide)side, (int)this.getRotationIndex()).relativePosition);
                Vector3d velRot = new Vector3d((double)((Vector3i)((Object)relRot)).x * 0.35, 0.25, (double)((Vector3i)((Object)relRot)).z * 0.35);
                Vector3i hopperBlock = this.getBlockPosition();
                Vector3d vector3d = new Vector3d((double)hopperBlock.x + 0.5, (double)hopperBlock.y + 0.5, (double)hopperBlock.z + 0.5);
                if (this.drop && !this.l.isEmpty() && this.l.getFirst() != null && (ref = this.l.getFirst()).isValid()) {
                    this.l.removeFirst();
                    try {
                        this.visualMap.remove(ref);
                        this.visualSpawnTimes.remove(ref);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    entities.removeEntity(ref, RemoveReason.REMOVE);
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
                int transferAmount;
                ItemStack otherStack = otherHopper.getItemContainer().getItemStack((short)n);
                if (otherStack == null) continue;
                String otherKey = null;
                try {
                    otherKey = otherStack.getBlockKey();
                }
                catch (Throwable available) {
                    // empty catch block
                }
                if (otherKey == null) {
                    otherKey = this.resolveItemStackKey(otherStack);
                }
                if (!this.isItemAllowedByFilter(otherKey)) continue;
                int otherAvailable = otherStack.getQuantity();
                if (this.isSingletonMode() && otherAvailable <= 1) continue;
                int n2 = transferAmount = this.isSingletonMode() && (float)otherAvailable < this.data.tier * (float)Ev0Config.getTierMultiplier() ? otherAvailable - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)otherAvailable);
                if (transferAmount <= 0 || !(t = this.getItemContainer().addItemStackToSlot((short)0, otherStack.withQuantity(transferAmount))).succeeded()) continue;
                if (!this.nearbyBuffer.isEmpty() && (taken = this.getItemContainer().getItemStack((short)0)) != null && !taken.isEmpty()) {
                    for (Ref<EntityStore> targetRef : this.nearbyBuffer) {
                        Ref<EntityStore> ref = ItemUtilsExtended.throwItem(this.getBlockType().getId(), side.toString(), new Vector3d((double)pos.x, (double)pos.y, (double)pos.z), targetRef, entities, taken, Vector3d.ZERO, 0.0f);
                        if (ref == null) continue;
                        this.l.add(ref);
                        try {
                            this.visualMap.put(ref, taken);
                            Instant instant = this.es != null ? this.es.getResource(WorldTimeResource.getResourceType()).getGameTime() : Instant.now();
                            this.visualSpawnTimes.put(ref, instant);
                        }
                        catch (Exception exception) {}
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
            ItemContainer ecsContainer = this.getContainerViaECS(pos);
            if (ecsContainer != null) {
                for (int slot = 0; slot < ecsContainer.getCapacity(); ++slot) {
                    ItemStackSlotTransaction t;
                    int transferAmount;
                    ItemStack stack = ecsContainer.getItemStack((short)slot);
                    if (stack == null) continue;
                    String probeKey = null;
                    try {
                        probeKey = stack.getBlockKey();
                    }
                    catch (Throwable otherAvailable) {
                        // empty catch block
                    }
                    if (probeKey == null) {
                        probeKey = this.resolveItemStackKey(stack);
                    }
                    if (!this.isItemAllowedByFilter(probeKey)) continue;
                    int srcAvailable = stack.getQuantity();
                    if (this.isSingletonMode() && srcAvailable <= 1) continue;
                    int n = transferAmount = this.isSingletonMode() && (float)srcAvailable < this.data.tier * (float)Ev0Config.getTierMultiplier() ? srcAvailable - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)srcAvailable);
                    if (transferAmount <= 0 || !(t = this.getItemContainer().addItemStackToSlot((short)0, stack.withQuantity(transferAmount))).succeeded()) continue;
                    ecsContainer.removeItemStackFromSlot((short)slot, transferAmount);
                    return true;
                }
            }
            return false;
        }
        ItemContainer sourceContainer = (ItemContainer)containerStateObj;
        if (SIMPLE_DRAWERS_PRESENT && sourceContainer instanceof IDrawerContainer) {
            IDrawerContainer drawerContainer = (IDrawerContainer)((Object)sourceContainer);
            for (short slot = 0; slot < drawerContainer.getSlotCount(); slot = (short)(slot + 1)) {
                int transferAmount;
                ItemStack slotItem = drawerContainer.getSlotItem(slot);
                int slotQty = drawerContainer.getSlotQuantity(slot);
                if (slotItem == null || slotQty <= 0) continue;
                String probeKey2 = null;
                try {
                    probeKey2 = slotItem.getBlockKey();
                }
                catch (Throwable t) {
                    // empty catch block
                }
                if (probeKey2 == null) {
                    probeKey2 = this.resolveItemStackKey(slotItem);
                }
                if (!this.isItemAllowedByFilter(probeKey2) || this.isSingletonMode() && slotQty <= 1) continue;
                int n = transferAmount = this.isSingletonMode() && (float)slotQty < this.data.tier * (float)Ev0Config.getTierMultiplier() ? slotQty - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)slotQty);
                if (transferAmount <= 0) continue;
                ItemStack safeStack = slotItem.withQuantity(transferAmount);
                ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short)0, safeStack);
                if (!t.succeeded()) continue;
                if (!this.nearbyBuffer.isEmpty()) {
                    String oppSide = switch ((ConnectedBlockPatternRule.AdjacentSide) side) {
                        case East -> "West";
                        case West -> "East";
                        case North -> "South";
                        case South -> "North";
                        case Up -> "Down";
                        case Down -> "Up";
                        default -> side.toString();
                    };
                    for (Ref<EntityStore> ref : this.nearbyBuffer) {
                        Ref<EntityStore> rs = ItemUtilsExtended.throwItem(this.getBlockType().getId(), oppSide, new Vector3d((double)pos.x, (double)pos.y, (double)pos.z), ref, entities, safeStack, Vector3d.ZERO, 0.0f);
                        if (rs == null) continue;
                        this.l.add(rs);
                        try {
                            this.visualMap.put(rs, safeStack);
                        }
                        catch (Exception exception) {}
                    }
                }
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
            if (!this.isItemAllowedByFilter(probeKey2)) continue;
            int srcAvailable = stack.getQuantity();
            if (this.isSingletonMode() && srcAvailable <= 1) continue;
            int n = transferAmount = this.isSingletonMode() && (float)srcAvailable < this.data.tier * (float)Ev0Config.getTierMultiplier() ? srcAvailable - 1 : (int)Math.min(this.data.tier * (float)Ev0Config.getTierMultiplier(), (float)srcAvailable);
            if (transferAmount <= 0) continue;
            ItemStack safeStack = stack.withQuantity(transferAmount);
            ItemStackSlotTransaction t = this.getItemContainer().addItemStackToSlot((short)0, safeStack);
            if (!t.succeeded()) continue;
            if (!this.nearbyBuffer.isEmpty()) {
                String oppSide = switch ((ConnectedBlockPatternRule.AdjacentSide) side) {
                    case East -> "West";
                    case West -> "East";
                    case North -> "South";
                    case South -> "North";
                    case Up -> "Down";
                    case Down -> "Up";
                    default -> side.toString();
                };
                for (Ref<EntityStore> ref : this.nearbyBuffer) {
                    Ref<EntityStore> ref2 = ItemUtilsExtended.throwItem(this.getBlockType().getId(), oppSide, new Vector3d((double)pos.x, (double)pos.y, (double)pos.z), ref, entities, safeStack, Vector3d.ZERO, 0.0f);
                    if (ref2 == null) continue;
                    this.l.add(ref2);
                    try {
                        this.visualMap.put(ref2, safeStack);
                    }
                    catch (Exception exception) {}
                }
                Ref<EntityStore> esx;
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
        entities.getResource(EntityModule.get().getItemSpatialResourceType()).getSpatialStructure().collectBox(boxMin, boxMax, rawResults);
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
        for (Ref ref : (java.util.ArrayList<Ref>) (java.util.ArrayList) itemRefs) {
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
            if ((ic = entities.getComponent(ref, ItemComponent.getComponentType())) == null) {
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
                TransformComponent tc = entities.getComponent(ref, TransformComponent.getComponentType());
                Vector3d dropPos = tc != null ? new Vector3d(tc.getPosition()) : new Vector3d((double)importPos.x + 0.5, (double)importPos.y + 0.5, (double)importPos.z + 0.5);
                entities.removeEntity(ref, RemoveReason.REMOVE);
                Holder newHolder = ItemComponent.generateItemDrop(entities, (ItemStack)stack.withQuantity(remaining), dropPos, Rotation3f.ZERO, (float)0.0f, (float)-1.0f, (float)0.0f);
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
            ArcioMechanismComponent mech;
            Ref<ChunkStore> blockRef;
            BlockComponentChunk bcc;
            Vector3i p = this.getBlockPosition();
            int bx = p.x;
            int by = p.y;
            int bz = p.z;
            Store<ChunkStore> cs = world.getChunkStore().getStore();
            Ref<ChunkStore> chunkRef = world.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(bx, bz));
            if (chunkRef != null && (bcc = cs.getComponent(chunkRef, BlockComponentChunk.getComponentType())) != null && (blockRef = bcc.getEntityReference(ChunkUtil.indexBlockInColumn(bx, by, bz))) != null && (mech = cs.getComponent(blockRef, ArcioMechanismComponent.getComponentType())) != null && mech.getStrongestInputSignal(world) > 0) {
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
                Ref<ChunkStore> chunkRef = world.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(nx, nz));
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
    public static List<Ref<EntityStore>> getAllEntitiesInBox(HopperProcessor hp, Vector3i pos, float height, @Nonnull ComponentAccessor<EntityStore> components, boolean players, boolean entities, boolean items) {
        List results = SpatialResource.getThreadLocalReferenceList();
        Vector3d center = new Vector3d((double)pos.x, (double)pos.y, (double)pos.z);
        double queryHeight = Math.max(1.0f, height);
        if (entities) {
            // empty if block
        }
        if (players) {
            components.getResource(EntityModule.get().getPlayerSpatialResourceType()).getSpatialStructure().collectCylinder(center, 4.0, queryHeight, results);
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
            components.getResource(EntityModule.get().getEntitySpatialResourceType()).getSpatialStructure().collectBox(min, max, results);
        }
        if (players) {
            // empty if block
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

    public static ComponentType<EntityStore, BlockEntity> getComponentType() {
        ComponentRegistryProxy<EntityStore> entityStoreRegistry = EntityModule.get().getEntityStoreRegistry();
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
            return this.w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(p.x, p.z));
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
                    f.set(object, comp.data);
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
        found = false;
        try {
            Class.forName("net.crepe.inventory.IDrawerContainer");
            found = true;
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        SIMPLE_DRAWERS_PRESENT = found;
        QUERY = Query.and(FluidSection.getComponentType(), ChunkSection.getComponentType());
        DEPENDENCIES = Set.of(new SystemDependency(Order.AFTER, FluidSystems.Ticking.class), new SystemDependency(Order.BEFORE, ChunkBlockTickSystem.Ticking.class));
    }

    public static class Data
    extends StateData {
        @Nonnull
        public static final BuilderCodec<Data> CODEC = BuilderCodec.builder(Data.class, Data::new)
            .append(new KeyedCodec<Float>("Force", Codec.FLOAT), (i, v) -> { ((Data)i).force = ((Float)v).floatValue(); }, i -> Float.valueOf(((Data)i).force)).add()
            .append(new KeyedCodec<Float>("Height", Codec.FLOAT), (i, v) -> { ((Data)i).height = ((Float)v).floatValue(); }, i -> Float.valueOf(((Data)i).height)).add()
            .append(new KeyedCodec<Float>("Tier", Codec.FLOAT), (i, v) -> { ((Data)i).tier = ((Float)v).floatValue(); }, i -> Float.valueOf(((Data)i).tier)).add()
            .append(new KeyedCodec<Boolean>("Players", Codec.BOOLEAN), (i, v) -> { ((Data)i).players = (Boolean)v; }, i -> ((Data)i).players).add()
            .append(new KeyedCodec<Boolean>("Items", Codec.BOOLEAN), (i, v) -> { ((Data)i).items = (Boolean)v; }, i -> ((Data)i).items).add()
            .append(new KeyedCodec<Boolean>("Entities", Codec.BOOLEAN), (i, v) -> { ((Data)i).entities = (Boolean)v; }, i -> ((Data)i).entities).add()
            .append(new KeyedCodec<ItemHandler>("Output", ItemHandler.CODEC), (i, v) -> { ((Data)i).output = (ItemHandler)v; }, i -> ((Data)i).output).add()
            .append(new KeyedCodec<ConnectedBlockPatternRule.AdjacentSide[]>("ExportFaces", Codecs.SIDE_ARRAY), (i, v) -> { ((Data)i).exportFaces = (ConnectedBlockPatternRule.AdjacentSide[])v; }, i -> ((Data)i).exportFaces).add()
            .append(new KeyedCodec<Boolean>("ExportOnce", Codec.BOOLEAN), (i, v) -> { ((Data)i).exportOnce = (Boolean)v; }, i -> ((Data)i).exportOnce).add()
            .append(new KeyedCodec<String[]>("Substitutions", Codec.STRING_ARRAY, true), (i, v) -> { ((Data)i).substitutions = (String[])v; }, i -> ((Data)i).substitutions).add()
            .append(new KeyedCodec<Rangef>("Cooldown", ProtocolCodecs.RANGEF), (i, v) -> { ((Data)i).duration = (Rangef)v; }, i -> ((Data)i).duration).add()
            .append(new KeyedCodec<ConnectedBlockPatternRule.AdjacentSide[]>("ImportFaces", Codecs.SIDE_ARRAY), (i, v) -> { ((Data)i).importFaces = (ConnectedBlockPatternRule.AdjacentSide[])v; }, i -> ((Data)i).importFaces).add()
            .build();
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
        public String hopperType = "Normal";
        public String wirelessName = "";
        public int wirelessTargetX = Integer.MIN_VALUE;
        public int wirelessTargetY = Integer.MIN_VALUE;
        public int wirelessTargetZ = Integer.MIN_VALUE;

        public void setWirelessTarget(@Nullable Vector3i target) {
            if (target == null) {
                this.wirelessTargetX = Integer.MIN_VALUE;
                this.wirelessTargetY = Integer.MIN_VALUE;
                this.wirelessTargetZ = Integer.MIN_VALUE;
            } else {
                this.wirelessTargetX = target.x;
                this.wirelessTargetY = target.y;
                this.wirelessTargetZ = target.z;
            }
        }
    }
}

