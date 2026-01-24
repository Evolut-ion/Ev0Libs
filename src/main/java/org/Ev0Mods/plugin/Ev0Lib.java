package org.Ev0Mods.plugin;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.meta.BlockStateRegistry;
import org.Ev0Mods.plugin.api.block.state.ConveyorProcessor;
import org.Ev0Mods.plugin.api.block.state.HopperProcessor;
import org.Ev0Mods.plugin.api.codec.ItemHandler;

import javax.annotation.Nonnull;

/**
 * This class serves as the entrypoint for your plugin. Use the setup method to register into game registries or add
 * event listeners.
 */
public class Ev0Lib extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public static final String GROUP = "Ev0sMods";
    public static final String NAME = "Ev0Lib";


    public Ev0Lib(@Nonnull JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Hello from " + this.getName() + " version " + this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up plugin " + this.getName());
        this.getCommandRegistry().registerCommand(new ExampleCommand(this.getName(), this.getManifest().getVersion().toString()));
        //Borrowed from Spellbook
        final var itemOutputCodec = this.getCodecRegistry(ItemHandler.CODEC);
        final BlockStateRegistry blockStateRegistry = this.getBlockStateRegistry();
        blockStateRegistry.registerBlockState(HopperProcessor.class, idPascal("HopperCrafter"), HopperProcessor.CODEC, HopperProcessor.Data.class, HopperProcessor.Data.CODEC);
    }
    public static String idPascal(String id) {
        return GROUP + NAME + id;
    }

    public static String idSnake(String id) {
        return GROUP + "_" + NAME + "_" + id;
    }
}