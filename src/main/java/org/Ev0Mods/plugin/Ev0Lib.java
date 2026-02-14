package org.Ev0Mods.plugin;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.meta.BlockStateRegistry;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.Ev0Mods.plugin.api.block.state.HopperProcessor;
import org.Ev0Mods.plugin.api.codec.ItemHandler;
import org.Ev0Mods.plugin.api.component.FluidComponent;
import org.Ev0Mods.plugin.api.interactions.WrenchInteraction;
import org.Ev0Mods.plugin.api.interactions.HopperInteraction;
import org.Ev0Mods.plugin.api.system.LiquidPlacingSystem;

import javax.annotation.Nonnull;

/**
 * This class serves as the entrypoint for your plugin. Use the setup method to register into game registries or add
 * event listeners.
 */
public class Ev0Lib extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public static final String GROUP = "Ev0sMods";
    public static final String NAME = "Ev0Lib";
    private ComponentType<EntityStore, FluidComponent> FluidComponent;


    public Ev0Lib(@Nonnull JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Hello from " + this.getName() + " version " + this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up plugin " + this.getName());
        final var itemOutputCodec = this.getCodecRegistry(ItemHandler.CODEC);
        this.getChunkStoreRegistry().registerSystem(new LiquidPlacingSystem());
        final BlockStateRegistry blockStateRegistry = this.getBlockStateRegistry();
        blockStateRegistry.registerBlockState(HopperProcessor.class, idPascal("HopperCrafter"), HopperProcessor.CODEC, HopperProcessor.Data.class, HopperProcessor.Data.CODEC);
        this.FluidComponent = this.getEntityStoreRegistry()
                .registerComponent(FluidComponent.class, FluidComponent::new);
        this.getCodecRegistry(Interaction.CODEC).<WrenchInteraction>register("WrenchInteraction", WrenchInteraction.class, WrenchInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("HopperInteraction", HopperInteraction.class,  HopperInteraction.CODEC);
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

    public void setFluidComponent(ComponentType<EntityStore, FluidComponent> liquidComponent) {
        FluidComponent = liquidComponent;
    }


}