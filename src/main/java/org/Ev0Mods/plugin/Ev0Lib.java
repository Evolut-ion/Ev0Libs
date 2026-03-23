package org.Ev0Mods.plugin;

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

import com.hypixel.hytale.codec.lookup.StringCodecMapCodec;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.plugin.registry.CodecMapRegistry;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

/**
 * This class serves as the entrypoint for your plugin. Use the setup method to register into game registries or add
 * event listeners.
 */
public class Ev0Lib extends JavaPlugin {

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

    @Override
    protected void setup() {
        Ev0Log.info(LOGGER, "Setting up plugin " + this.getName());
        
        // Initialize config system
        String configDir = this.getDataDirectory().toAbsolutePath().toString();
        Ev0Config.initialize(configDir);
        Ev0Log.info(LOGGER, "Config initialized with tierMultiplier=" + Ev0Config.getTierMultiplier() + 
                   ", fluidTransferEnabled=" + Ev0Config.isFluidTransferEnabled());
        
        final CodecMapRegistry itemOutputCodec = this.getCodecRegistry((StringCodecMapCodec)ItemHandler.CODEC);
        try { itemOutputCodec.register(IdOutput.ID, IdOutput.class, IdOutput.CODEC); } catch (Throwable ignored) {}
        this.getChunkStoreRegistry().registerSystem(new LiquidPlacingSystem());
        //final BlockStateRegistry blockStateRegistry = this.getBlockStateRegistry();
        //blockStateRegistry.registerBlockState(HopperProcessor.class, idPascal("HopperCrafter"), HopperProcessor.CODEC, HopperProcessor.Data.class, HopperProcessor.Data.CODEC);
        this.FluidComponent = this.getEntityStoreRegistry()
                .registerComponent(FluidComponent.class, FluidComponent::new);
        this.getCodecRegistry(Interaction.CODEC).<WrenchInteraction>register("WrenchInteraction", WrenchInteraction.class, WrenchInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("HopperInteraction", HopperInteraction.class,  HopperInteraction.CODEC);

        try {
            try {
                this.hopperComponentType = this.getChunkStoreRegistry().registerComponent(HopperComponent.class, Ev0Lib.idPascal("HopperComponent"), HopperComponent.CODEC);
                Ev0Log.info(LOGGER, "Registered HopperComponent with codec on chunk store registry");
            } catch (Throwable t) {
                try {
                    ComponentRegistryProxy csr = this.getChunkStoreRegistry();
                    java.lang.reflect.Method m = csr.getClass().getMethod("registerComponent", Class.class, java.util.function.Supplier.class);
                    Object ret = m.invoke((Object)csr, HopperComponent.class, (java.util.function.Supplier)HopperComponent::new);
                    if (ret != null && ret.getClass().getName().contains("ComponentType")) {
                        this.hopperComponentType = (ComponentType)ret;
                    }
                    Ev0Log.info(LOGGER, "Registered HopperComponent reflectively on chunk store registry (fallback)");
                } catch (Throwable ignored) {
                    Ev0Log.warn(LOGGER, "Failed to register HopperComponent: " + ignored.getMessage());
                }
            }
        } catch (Throwable ignored) {}

        try {
            this.getChunkStoreRegistry().registerSystem(new HopperComponentSystem(this.hopperComponentType));
            Ev0Log.info(LOGGER, "Registered HopperComponentSystem");
        } catch (Throwable ignored) { Ev0Log.warn(LOGGER, "Failed to register HopperComponentSystem: " + ignored.getMessage()); }

        // Register ArcIO mechanism if ArcIO is installed.
        // Uses reflection so the plugin loads normally when ArcIO is absent.
        try {
            Class.forName("voidbond.arcio.ArcioPlugin");
            Class.forName("org.Ev0Mods.plugin.api.block.state.HopperArcioRegistration")
                    .getMethod("register")
                    .invoke(null);
            Ev0Log.info(LOGGER, "[Ev0Lib] Registered ArcIO mechanism: Hopper");
        } catch (ClassNotFoundException ignored) {
            Ev0Log.info(LOGGER, "[Ev0Lib] ArcIO not found - skipping mechanism registration");
        } catch (Exception e) {
            Ev0Log.warn(LOGGER, "[Ev0Lib] Failed to register ArcIO mechanism: " + e.getMessage());
        }
    }
    public static String idPascal(String id) {
        return GROUP + NAME + id;
    }

    public static String idSnake(String id) {
        return GROUP + "_" + NAME + "_" + id;
    }

    public ComponentType<EntityStore, FluidComponent> getFluidComponent() {
        return FluidComponent;
    }

    public ComponentType<ChunkStore, HopperComponent> getHopperComponentType() {
        return this.hopperComponentType;
    }

    public static Ev0Lib getInstance() {
        return INSTANCE;
    }

    public void setFluidComponent(ComponentType<EntityStore, FluidComponent> liquidComponent) {
        FluidComponent = liquidComponent;
    }


}