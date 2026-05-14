/*
 * Decompiled with CFR 0.152.
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
    public static final BuilderCodec<HopperInteraction> CODEC = BuilderCodec.builder(HopperInteraction.class, HopperInteraction::new, SimpleBlockInteraction.CODEC).build();
    public Random r = new Random();

    protected void interactWithBlock(@NonNullDecl World world, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl Vector3i vector3i, @NonNullDecl CooldownHandler cooldownHandler) {
        block19: {
            Ref<EntityStore> playerEnt = interactionContext.getOwningEntity();
            Store<EntityStore> store = playerEnt.getStore();
            try {
                PlayerRef pref = store.getComponent(playerEnt, PlayerRef.getComponentType());
                Ev0Log.info(HytaleLogger.forEnclosingClass(), "HopperInteraction: interactWithBlock called, pref=" + String.valueOf(pref) + " pos=" + String.valueOf(vector3i));
                Ev0Log.warn(HytaleLogger.forEnclosingClass(), "[Ev0Lib][DIAG] HopperInteraction invoked for player=" + String.valueOf(pref) + " pos=" + String.valueOf(vector3i));
                if (pref != null) {
                    HopperUIPage.PLAYER_ENTITY_REFS.put(pref, playerEnt);
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
                            WorldChunk chunk = store.getExternalData().getWorld().getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(vector3i.x, vector3i.z));
                            if (chunk != null) {
                                Object bt = EngineCompat.getBlockType(chunk, vector3i.x, vector3i.y, vector3i.z);
                                Object st = EngineCompat.getState(chunk, vector3i.x, vector3i.y, vector3i.z);
                                Ev0Log.warn(HytaleLogger.forEnclosingClass(), "[Ev0Lib][DIAG] HopperInteraction: blockType=" + (String)(bt == null ? "null" : bt.getClass().getName() + " -> " + bt.toString()) + ", state=" + (st == null ? "null" : st.getClass().getName()));
                            } else {
                                Ev0Log.warn(HytaleLogger.forEnclosingClass(), "[Ev0Lib][DIAG] HopperInteraction: chunk null at pos=" + String.valueOf(vector3i));
                            }
                        }
                        catch (Throwable throwable) {
                            // empty catch block
                        }
                        HopperUIPage.open(pref, store, vector3i, heldItemId);
                        Ev0Log.info(HytaleLogger.forEnclosingClass(), "HopperInteraction: requested HopperUIPage.open for player=" + String.valueOf(pref) + " pos=" + String.valueOf(vector3i) + " held=" + heldItemId);
                        Ev0Log.warn(HytaleLogger.forEnclosingClass(), "[Ev0Lib][DIAG] HopperInteraction requested UI open for player=" + String.valueOf(pref) + " pos=" + String.valueOf(vector3i) + " held=" + heldItemId);
                    }
                    catch (Throwable t) {
                        Ev0Log.warn(HytaleLogger.forEnclosingClass(), "HopperInteraction: failed to open HopperUIPage: " + t.getMessage());
                    }
                    break block19;
                }
                Ev0Log.warn(HytaleLogger.forEnclosingClass(), "HopperInteraction: PlayerRef null when interacting");
                Ev0Log.warn(HytaleLogger.forEnclosingClass(), "[Ev0Lib][DIAG] HopperInteraction: PlayerRef null when interacting at pos=" + String.valueOf(vector3i));
            }
            catch (Throwable t) {
                Ev0Log.warn(HytaleLogger.forEnclosingClass(), "HopperInteraction: outer failure: " + t.getMessage());
                Ev0Log.warn(HytaleLogger.forEnclosingClass(), "[Ev0Lib][DIAG] HopperInteraction outer failure: " + String.valueOf(t));
            }
        }
    }

    protected void simulateInteractWithBlock(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl World world, @NonNullDecl Vector3i vector3i) {
    }
}

