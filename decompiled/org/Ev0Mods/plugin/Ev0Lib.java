/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.codec.lookup.StringCodecMapCodec
 *  com.hypixel.hytale.component.ComponentRegistryProxy
 *  com.hypixel.hytale.component.ComponentType
 *  com.hypixel.hytale.component.system.ISystem
 *  com.hypixel.hytale.logger.HytaleLogger
 *  com.hypixel.hytale.logger.HytaleLogger$Api
 *  com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction
 *  com.hypixel.hytale.server.core.plugin.JavaPlugin
 *  com.hypixel.hytale.server.core.plugin.JavaPluginInit
 *  com.hypixel.hytale.server.core.plugin.registry.CodecMapRegistry
 *  com.hypixel.hytale.server.core.universe.world.storage.ChunkStore
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nonnull
 */
package org.Ev0Mods.plugin;

import com.hypixel.hytale.codec.lookup.StringCodecMapCodec;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.system.ISystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.plugin.registry.CodecMapRegistry;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.lang.reflect.Method;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.Ev0Mods.plugin.api.Ev0Config;
import org.Ev0Mods.plugin.api.Ev0Log;
import org.Ev0Mods.plugin.api.codec.IdOutput;
import org.Ev0Mods.plugin.api.codec.ItemHandler;
import org.Ev0Mods.plugin.api.component.FluidComponent;
import org.Ev0Mods.plugin.api.component.HopperComponent;
import org.Ev0Mods.plugin.api.interactions.HopperInteraction;
import org.Ev0Mods.plugin.api.interactions.WrenchInteraction;
import org.Ev0Mods.plugin.api.system.HopperComponentSystem;
import org.Ev0Mods.plugin.api.system.LiquidPlacingSystem;

public class Ev0Lib
extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public static final String GROUP = "Ev0sMods";
    public static final String NAME = "Ev0Lib";
    private ComponentType<EntityStore, FluidComponent> FluidComponent;
    private ComponentType<ChunkStore, HopperComponent> hopperComponentType;
    private static Ev0Lib INSTANCE;

    public Ev0Lib(@Nonnull JavaPluginInit init) {
        super(init);
        Ev0Log.info(LOGGER, "Hello from " + this.getName() + " version " + this.getManifest().getVersion().toString());
        INSTANCE = this;
    }

    protected void setup() {
        Ev0Log.info(LOGGER, "Setting up plugin " + this.getName());
        String configDir = this.getDataDirectory().toAbsolutePath().toString();
        Ev0Config.initialize(configDir);
        Ev0Log.info(LOGGER, "Config initialized with tierMultiplier=" + Ev0Config.getTierMultiplier() + ", fluidTransferEnabled=" + Ev0Config.isFluidTransferEnabled());
        try {
            ((HytaleLogger.Api)LOGGER.atInfo()).log("[Ev0Lib] Ev0Config.isLoggingEnabled()=" + Ev0Config.isLoggingEnabled());
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        CodecMapRegistry itemOutputCodec = this.getCodecRegistry((StringCodecMapCodec)ItemHandler.CODEC);
        try {
            itemOutputCodec.register(IdOutput.ID, IdOutput.class, IdOutput.CODEC);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        this.getChunkStoreRegistry().registerSystem((ISystem)new LiquidPlacingSystem());
        this.FluidComponent = this.getEntityStoreRegistry().registerComponent(FluidComponent.class, FluidComponent::new);
        try {
            this.hopperComponentType = this.getChunkStoreRegistry().registerComponent(HopperComponent.class, Ev0Lib.idPascal("HopperComponent"), HopperComponent.CODEC);
            Ev0Log.info(LOGGER, "Registered HopperComponent with codec on chunk store registry");
            try {
                ((HytaleLogger.Api)LOGGER.atInfo()).log("[Ev0Lib] hopperComponentType non-null after registration");
            }
            catch (Throwable throwable) {}
        }
        catch (Throwable t) {
            try {
                ComponentRegistryProxy csr = this.getChunkStoreRegistry();
                Method m = csr.getClass().getMethod("registerComponent", Class.class, Supplier.class);
                Object ret = m.invoke((Object)csr, HopperComponent.class, HopperComponent::new);
                if (ret != null && ret.getClass().getName().contains("ComponentType")) {
                    this.hopperComponentType = (ComponentType)ret;
                }
                Ev0Log.info(LOGGER, "Registered HopperComponent reflectively on chunk store registry (fallback)");
            }
            catch (Throwable ignored) {
                Ev0Log.warn(LOGGER, "Failed to register HopperComponent: " + ignored.getMessage());
            }
        }
        this.getChunkStoreRegistry().registerSystem((ISystem)new HopperComponentSystem(this.hopperComponentType));
        try {
            try {
                ((HytaleLogger.Api)LOGGER.atInfo()).log("[Ev0Lib] Attempting to register HopperComponentSystem, hopperComponentType=" + (this.hopperComponentType != null));
            }
            catch (Throwable t) {
                // empty catch block
            }
            if (this.hopperComponentType != null) {
                this.getChunkStoreRegistry().registerSystem((ISystem)new HopperComponentSystem(this.hopperComponentType));
                Ev0Log.info(LOGGER, "Registered HopperComponentSystem");
                try {
                    ((HytaleLogger.Api)LOGGER.atInfo()).log("[Ev0Lib] Registered HopperComponentSystem (unconditional log)");
                }
                catch (Throwable t) {}
            }
        }
        catch (Throwable t) {
            Ev0Log.warn(LOGGER, "Failed to register HopperComponentSystem: " + t.getMessage());
            try {
                ((HytaleLogger.Api)LOGGER.atWarning()).log("[Ev0Lib] Failed to register HopperComponentSystem: " + t.getMessage());
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        this.getCodecRegistry(Interaction.CODEC).register("WrenchInteraction", WrenchInteraction.class, WrenchInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("HopperInteraction", HopperInteraction.class, HopperInteraction.CODEC);
        try {
            Class.forName("voidbond.arcio.ArcioPlugin");
            Class.forName("org.Ev0Mods.plugin.api.block.state.HopperArcioRegistration").getMethod("register", new Class[0]).invoke(null, new Object[0]);
            Ev0Log.info(LOGGER, "[Ev0Lib] Registered ArcIO mechanism: Hopper");
        }
        catch (ClassNotFoundException ignored) {
            Ev0Log.info(LOGGER, "[Ev0Lib] ArcIO not found - skipping mechanism registration");
        }
        catch (Exception e) {
            Ev0Log.warn(LOGGER, "[Ev0Lib] Failed to register ArcIO mechanism: " + e.getMessage());
        }
    }

    public static String idPascal(String id) {
        return "Ev0sModsEv0Lib" + id;
    }

    public static String idSnake(String id) {
        return "Ev0sMods_Ev0Lib_" + id;
    }

    public ComponentType<EntityStore, FluidComponent> getFluidComponent() {
        return this.FluidComponent;
    }

    public ComponentType<ChunkStore, HopperComponent> getHopperComponentType() {
        return this.hopperComponentType;
    }

    public static Ev0Lib getInstance() {
        return INSTANCE;
    }

    public void setFluidComponent(ComponentType<EntityStore, FluidComponent> liquidComponent) {
        this.FluidComponent = liquidComponent;
    }
}

