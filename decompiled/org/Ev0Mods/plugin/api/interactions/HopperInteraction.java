/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.codec.builder.BuilderCodec
 *  com.hypixel.hytale.component.CommandBuffer
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.logger.HytaleLogger
 *  com.hypixel.hytale.logger.HytaleLogger$Api
 *  com.hypixel.hytale.math.util.ChunkUtil
 *  com.hypixel.hytale.math.vector.Vector3i
 *  com.hypixel.hytale.protocol.InteractionType
 *  com.hypixel.hytale.server.core.entity.InteractionContext
 *  com.hypixel.hytale.server.core.inventory.ItemStack
 *  com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler
 *  com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  com.hypixel.hytale.server.core.universe.world.World
 *  com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  org.checkerframework.checker.nullness.compatqual.NonNullDecl
 *  org.checkerframework.checker.nullness.compatqual.NullableDecl
 */
package org.Ev0Mods.plugin.api.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.lang.reflect.Method;
import java.util.Random;
import org.Ev0Mods.plugin.api.Ev0Log;
import org.Ev0Mods.plugin.api.component.EngineCompat;
import org.Ev0Mods.plugin.api.ui.HopperUIPage;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class HopperInteraction
extends SimpleBlockInteraction {
    public static final BuilderCodec<HopperInteraction> CODEC = BuilderCodec.builder(HopperInteraction.class, HopperInteraction::new).build();
    public Random r = new Random();

    protected void interactWithBlock(@NonNullDecl World world, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl Vector3i vector3i, @NonNullDecl CooldownHandler cooldownHandler) {
        block19: {
            Ref playerEnt = interactionContext.getOwningEntity();
            Store store = playerEnt.getStore();
            try {
                PlayerRef pref = (PlayerRef)store.getComponent(playerEnt, PlayerRef.getComponentType());
                Ev0Log.info(HytaleLogger.forEnclosingClass(), "HopperInteraction: interactWithBlock called, pref=" + String.valueOf(pref) + " pos=" + String.valueOf(vector3i));
                ((HytaleLogger.Api)HytaleLogger.forEnclosingClass().atWarning()).log("[Ev0Lib][DIAG] HopperInteraction invoked for player=" + String.valueOf(pref) + " pos=" + String.valueOf(vector3i));
                if (pref != null) {
                    try {
                        String heldItemId = null;
                        if (itemStack != null) {
                            try {
                                Object probe = null;
                                try {
                                    probe = itemStack.getBlockKey();
                                }
                                catch (Throwable throwable) {
                                    // empty catch block
                                }
                                if (probe == null) {
                                    for (String m : new String[]{"getItemId", "getItemKey", "getId", "getKey", "getName"}) {
                                        try {
                                            Method mm = itemStack.getClass().getMethod(m, new Class[0]);
                                            Object v = mm.invoke((Object)itemStack, new Object[0]);
                                            if (v == null) continue;
                                            probe = v;
                                            break;
                                        }
                                        catch (Throwable throwable) {
                                            // empty catch block
                                        }
                                    }
                                }
                                if (probe != null) {
                                    heldItemId = String.valueOf(probe);
                                }
                            }
                            catch (Throwable probe) {
                                // empty catch block
                            }
                        }
                        try {
                            WorldChunk chunk = ((EntityStore)store.getExternalData()).getWorld().getChunkIfInMemory(ChunkUtil.indexChunkFromBlock((int)vector3i.x, (int)vector3i.z));
                            if (chunk != null) {
                                Object bt = EngineCompat.getBlockType(chunk, vector3i.x, vector3i.y, vector3i.z);
                                Object st = EngineCompat.getState(chunk, vector3i.x, vector3i.y, vector3i.z);
                                ((HytaleLogger.Api)HytaleLogger.forEnclosingClass().atWarning()).log("[Ev0Lib][DIAG] HopperInteraction: blockType=" + (String)(bt == null ? "null" : bt.getClass().getName() + " -> " + bt.toString()) + ", state=" + (st == null ? "null" : st.getClass().getName()));
                            } else {
                                ((HytaleLogger.Api)HytaleLogger.forEnclosingClass().atWarning()).log("[Ev0Lib][DIAG] HopperInteraction: chunk null at pos=" + String.valueOf(vector3i));
                            }
                        }
                        catch (Throwable throwable) {
                            // empty catch block
                        }
                        HopperUIPage.open(pref, (Store<EntityStore>)store, vector3i, heldItemId);
                        Ev0Log.info(HytaleLogger.forEnclosingClass(), "HopperInteraction: requested HopperUIPage.open for player=" + String.valueOf(pref) + " pos=" + String.valueOf(vector3i) + " held=" + heldItemId);
                        ((HytaleLogger.Api)HytaleLogger.forEnclosingClass().atWarning()).log("[Ev0Lib][DIAG] HopperInteraction requested UI open for player=" + String.valueOf(pref) + " pos=" + String.valueOf(vector3i) + " held=" + heldItemId);
                    }
                    catch (Throwable t) {
                        Ev0Log.warn(HytaleLogger.forEnclosingClass(), "HopperInteraction: failed to open HopperUIPage: " + t.getMessage());
                    }
                    break block19;
                }
                Ev0Log.warn(HytaleLogger.forEnclosingClass(), "HopperInteraction: PlayerRef null when interacting");
                ((HytaleLogger.Api)HytaleLogger.forEnclosingClass().atWarning()).log("[Ev0Lib][DIAG] HopperInteraction: PlayerRef null when interacting at pos=" + String.valueOf(vector3i));
            }
            catch (Throwable t) {
                Ev0Log.warn(HytaleLogger.forEnclosingClass(), "HopperInteraction: outer failure: " + t.getMessage());
                ((HytaleLogger.Api)HytaleLogger.forEnclosingClass().atWarning()).log("[Ev0Lib][DIAG] HopperInteraction outer failure: " + String.valueOf(t));
            }
        }
    }

    protected void simulateInteractWithBlock(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl World world, @NonNullDecl Vector3i vector3i) {
    }
}

