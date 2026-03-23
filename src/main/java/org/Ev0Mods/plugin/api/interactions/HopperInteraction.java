//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.Ev0Mods.plugin.api.interactions;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.PlaceBlockInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Random;
import java.util.logging.Level;
import org.Ev0Mods.plugin.api.Ev0Log;
import org.Ev0Mods.plugin.Ev0Lib;

import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

@SuppressWarnings("removal")
public class HopperInteraction extends SimpleBlockInteraction {
    public static final BuilderCodec<HopperInteraction> CODEC;
    public Random r = new Random();

    protected void interactWithBlock(@NonNullDecl World world, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl Vector3i vector3i, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> playerEnt = interactionContext.getOwningEntity();
        Store<EntityStore> store = playerEnt.getStore();

        try {
            PlayerRef pref = store.getComponent(playerEnt, PlayerRef.getComponentType());
            Ev0Log.info(HytaleLogger.forEnclosingClass(), "HopperInteraction: interactWithBlock called, pref=" + pref + " pos=" + vector3i);
            // Unconditional warning log for diagnostics (bypass Ev0Config gating)
            HytaleLogger.forEnclosingClass().atWarning().log("[Ev0Lib][DIAG] HopperInteraction invoked for player=" + pref + " pos=" + vector3i);
            if (pref != null) {
                try {
                    // Resolve held item key so the UI can offer quick-add
                    String heldItemId = null;
                    if (itemStack != null) {
                        try {
                            // Borrow HopperProcessor's reflection-based key resolver
                            Object probe = null;
                            try { probe = itemStack.getBlockKey(); } catch (Throwable ignored) {}
                            if (probe == null) {
                                for (String m : new String[]{"getItemId","getItemKey","getId","getKey","getName"}) {
                                    try {
                                        java.lang.reflect.Method mm = itemStack.getClass().getMethod(m);
                                        Object v = mm.invoke(itemStack);
                                        if (v != null) { probe = v; break; }
                                    } catch (Throwable ignored) {}
                                }
                            }
                            if (probe != null) heldItemId = String.valueOf(probe);
                        } catch (Throwable ignored) {}
                    }
                    // Diagnostic: log block type & state before opening UI
                    try {
                        Object chunk = store.getExternalData().getWorld().getChunkIfInMemory(com.hypixel.hytale.math.util.ChunkUtil.indexChunkFromBlock(vector3i.x, vector3i.z));
                        if (chunk != null) {
                            Object bt = org.Ev0Mods.plugin.api.component.EngineCompat.getBlockType(chunk, vector3i.x, vector3i.y, vector3i.z);
                            Object st = org.Ev0Mods.plugin.api.component.EngineCompat.getState(chunk, vector3i.x, vector3i.y, vector3i.z);
                            HytaleLogger.forEnclosingClass().atWarning().log("[Ev0Lib][DIAG] HopperInteraction: blockType=" + (bt==null?"null":bt.getClass().getName()+" -> "+bt.toString()) + ", state=" + (st==null?"null":st.getClass().getName()));
                        } else {
                            HytaleLogger.forEnclosingClass().atWarning().log("[Ev0Lib][DIAG] HopperInteraction: chunk null at pos=" + vector3i);
                        }
                    } catch (Throwable ignored) {}
                    // Open the HyUI-based hopper filter page
                    org.Ev0Mods.plugin.api.ui.HopperUIPage.open(pref, store, vector3i, heldItemId);
                    Ev0Log.info(HytaleLogger.forEnclosingClass(), "HopperInteraction: requested HopperUIPage.open for player=" + pref + " pos=" + vector3i + " held=" + heldItemId);
                    HytaleLogger.forEnclosingClass().atWarning().log("[Ev0Lib][DIAG] HopperInteraction requested UI open for player=" + pref + " pos=" + vector3i + " held=" + heldItemId);
                } catch (Throwable t) {
                    Ev0Log.warn(HytaleLogger.forEnclosingClass(), "HopperInteraction: failed to open HopperUIPage: " + t.getMessage());
                }
            } else {
                Ev0Log.warn(HytaleLogger.forEnclosingClass(), "HopperInteraction: PlayerRef null when interacting");
                HytaleLogger.forEnclosingClass().atWarning().log("[Ev0Lib][DIAG] HopperInteraction: PlayerRef null when interacting at pos=" + vector3i);
            }
        } catch (Throwable t) {
            Ev0Log.warn(HytaleLogger.forEnclosingClass(), "HopperInteraction: outer failure: " + t.getMessage());
            HytaleLogger.forEnclosingClass().atWarning().log("[Ev0Lib][DIAG] HopperInteraction outer failure: " + t);
        }
    }

    protected void simulateInteractWithBlock(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl World world, @NonNullDecl Vector3i vector3i) {
    }

    static {
        CODEC = BuilderCodec.builder(HopperInteraction.class, HopperInteraction::new).build();
    }
}