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
            HytaleLogger.getLogger().atInfo().log("HopperInteraction: interactWithBlock called, pref=" + pref);
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
                    // Open the HyUI-based hopper filter page
                    org.Ev0Mods.plugin.api.ui.HopperUIPage.open(pref, store, vector3i, heldItemId);
                    HytaleLogger.getLogger().atInfo().log("HopperInteraction: opened HopperUIPage via HyUI PageBuilder, heldItem=" + heldItemId);
                } catch (Throwable t) {
                    HytaleLogger.getLogger().at(Level.WARNING).log("HopperInteraction: failed to open HopperUIPage: " + t.getMessage());
                }
            } else {
                HytaleLogger.getLogger().at(Level.WARNING).log("HopperInteraction: PlayerRef null when interacting");
            }
        } catch (Throwable t) {
            HytaleLogger.getLogger().at(Level.WARNING).log("HopperInteraction: outer failure: " + t.getMessage());
        }
    }

    protected void simulateInteractWithBlock(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl World world, @NonNullDecl Vector3i vector3i) {
    }

    static {
        CODEC = BuilderCodec.builder(HopperInteraction.class, HopperInteraction::new).build();
    }
}