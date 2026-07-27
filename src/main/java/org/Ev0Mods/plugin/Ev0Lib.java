/*
 * Decompiled with CFR 0.152.
 */
package org.Ev0Mods.plugin;

import java.lang.reflect.Method;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.Ev0Mods.plugin.api.Ev0Config;
import org.Ev0Mods.plugin.api.Ev0Log;
import org.Ev0Mods.plugin.api.codec.IdOutput;
import org.Ev0Mods.plugin.api.codec.ItemHandler;
import org.Ev0Mods.plugin.api.command.DemoCommand;
import org.Ev0Mods.plugin.api.command.ReportCommand;
import org.Ev0Mods.plugin.api.command.SuggestCommand;
import org.Ev0Mods.plugin.api.command.TelemetryOptCommand;
import org.Ev0Mods.plugin.api.command.ThrowCommand;
import org.Ev0Mods.plugin.api.component.FluidComponent;
import org.Ev0Mods.plugin.api.component.HopperComponent;
import org.Ev0Mods.plugin.api.interactions.HopperInteraction;
import org.Ev0Mods.plugin.api.interactions.WrenchInteraction;
import org.Ev0Mods.plugin.api.system.HopperComponentSystem;
import org.Ev0Mods.plugin.api.system.LiquidPlacingSystem;
import org.Ev0Mods.plugin.api.system.WirelessHopperPlaceSystem;
import org.Ev0Mods.plugin.api.system.WirelessRegistry;
import org.Ev0Mods.plugin.api.telemetry.Ev0Telemetry;
import org.Ev0Mods.plugin.api.ui.HopperUIPage;
import org.Ev0Mods.plugin.api.ui.TemplateDemoPage;

import com.hypixel.hytale.codec.lookup.StringCodecMapCodec;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.plugin.registry.CodecMapRegistry;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

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
        try {
            getCodecRegistry(Interaction.CODEC).register("WrenchInteraction", WrenchInteraction.class, WrenchInteraction.CODEC);
        } catch (Throwable th) {
            Ev0Log.warn(LOGGER, "Failed to register WrenchInteraction codec: " + th.getMessage());
        }
        try {
            getCodecRegistry(Interaction.CODEC).register("HopperInteraction", HopperInteraction.class, HopperInteraction.CODEC);
        } catch (Throwable th) {
            Ev0Log.warn(LOGGER, "Failed to register HopperInteraction codec: " + th.getMessage());
        }
    }

    @Override
    protected void shutdown() {
        try { Ev0Telemetry.shutdown(); } catch (Throwable ignored) {}
    }

    @Override
    protected void setup() {
        Ev0Log.info(LOGGER, "Setting up plugin " + this.getName());
        String configDir = this.getDataDirectory().toAbsolutePath().toString();
        Ev0Config.initialize(configDir);
        try {
            WirelessRegistry.initialize(this.getDataDirectory().toAbsolutePath());
        } catch (Throwable t) {
            Ev0Log.warn(LOGGER, "Failed to initialize WirelessRegistry: " + t.getMessage());
        }
        this.getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, event -> {
            try { HopperUIPage.cleanupStaleRefs(); } catch (Throwable ignored) {}
            try { TemplateDemoPage.cleanup(event.getPlayerRef()); } catch (Throwable ignored) {}
            try { Ev0Telemetry.notifyPlayerLeft(); } catch (Throwable ignored) {}
        });
        try {
            this.getEventRegistry().registerGlobal(PlayerReadyEvent.class,
                    event -> Ev0Telemetry.notifyPlayerJoined());
        } catch (Throwable t) {
            Ev0Log.warn(LOGGER, "Could not register PlayerReadyEvent: " + t.getMessage());
        }
        try {
            @SuppressWarnings("unchecked")
            Class<Object> spEvent = (Class<Object>) Class.forName(
                    "com.hypixel.hytale.server.core.modules.singleplayer.SingleplayerRequestAccessEvent");
            this.getEventRegistry().registerGlobal(spEvent,
                    event -> Ev0Telemetry.setServerType("singleplayer"));
        } catch (Throwable t) {
            // Not singleplayer or event unavailable — default "server" type is correct
        }
        try {
            Ev0Telemetry.initialize(this.getManifest().getVersion().toString());
        } catch (Throwable t) {
            Ev0Log.warn(LOGGER, "Failed to initialize Ev0Telemetry: " + t.getMessage());
        }
        try {
            getCommandRegistry().registerCommand(new ReportCommand());
            Ev0Log.info(LOGGER, "Registered /report command");
        } catch (Throwable t) {
            Ev0Log.warn(LOGGER, "Failed to register /report command: " + t.getMessage());
        }
        try {
            getCommandRegistry().registerCommand(new SuggestCommand());
            Ev0Log.info(LOGGER, "Registered /suggest command");
        } catch (Throwable t) {
            Ev0Log.warn(LOGGER, "Failed to register /suggest command: " + t.getMessage());
        }
        try {
            getCommandRegistry().registerCommand(new DemoCommand());
            Ev0Log.info(LOGGER, "Registered /demo command");
        } catch (Throwable t) {
            Ev0Log.warn(LOGGER, "Failed to register /demo command: " + t.getMessage());
        }
        try {
            getCommandRegistry().registerCommand(new ThrowCommand());
            Ev0Log.info(LOGGER, "Registered /throw command");
        } catch (Throwable t) {
            Ev0Log.warn(LOGGER, "Failed to register /throw command: " + t.getMessage());
        }
        try {
            getCommandRegistry().registerCommand(new TelemetryOptCommand());
            Ev0Log.info(LOGGER, "Registered /telemetry command");
        } catch (Throwable t) {
            Ev0Log.warn(LOGGER, "Failed to register /telemetry command: " + t.getMessage());
        }
        Ev0Log.info(LOGGER, "Config initialized with tierMultiplier=" + Ev0Config.getTierMultiplier() + ", fluidTransferEnabled=" + Ev0Config.isFluidTransferEnabled());
        CodecMapRegistry itemOutputCodec = getCodecRegistry((StringCodecMapCodec) ItemHandler.CODEC);
        try {
            itemOutputCodec.register(IdOutput.ID, IdOutput.class, IdOutput.CODEC);
        }
        catch (Throwable th) {
        }
        getChunkStoreRegistry().registerSystem(new LiquidPlacingSystem());
        this.FluidComponent = getEntityStoreRegistry().registerComponent(FluidComponent.class, FluidComponent::new);
        // Interaction codecs MUST be (re-)registered during setup() so they are present
        // when the asset pack (e.g. OmniHopper) is validated; the constructor registration
        // alone is not honored at validation time and causes the mod to fail to load.
        // Registering the same id twice is an idempotent map overwrite, so this does NOT
        // double-bind the interaction -- the hopper-UI duplication is prevented in the
        // Collect handler instead (remove-before-give).
        try {
            getCodecRegistry(Interaction.CODEC).register("WrenchInteraction", WrenchInteraction.class, WrenchInteraction.CODEC);
        }
        catch (Throwable th) {
        }
        try {
            getCodecRegistry(Interaction.CODEC).register("HopperInteraction", HopperInteraction.class, HopperInteraction.CODEC);
        }
        catch (Throwable th) {
        }
        try {
            try {
                this.hopperComponentType = this.getChunkStoreRegistry().registerComponent(HopperComponent.class, Ev0Lib.idPascal("HopperComponent"), HopperComponent.CODEC);
                Ev0Log.info(LOGGER, "Registered HopperComponent with codec on chunk store registry");
            }
            catch (Throwable t) {
                try {
                    ComponentRegistryProxy<ChunkStore> csr = this.getChunkStoreRegistry();
                    Method m = csr.getClass().getMethod("registerComponent", Class.class, Supplier.class);
                    Object ret = m.invoke(csr, HopperComponent.class, (Supplier<HopperComponent>) HopperComponent::new);
                    if (ret != null && ret.getClass().getName().contains("ComponentType")) {
                        this.hopperComponentType = (ComponentType)ret;
                    }
                    Ev0Log.info(LOGGER, "Registered HopperComponent reflectively on chunk store registry (fallback)");
                }
                catch (Throwable ignored) {
                    Ev0Log.warn(LOGGER, "Failed to register HopperComponent: " + ignored.getMessage());
                }
            }
        }
        catch (Throwable t) {
            // empty catch block
        }
        try {
            this.getChunkStoreRegistry().registerSystem(new HopperComponentSystem(this.hopperComponentType));
            Ev0Log.info(LOGGER, "Registered HopperComponentSystem");
        }
        catch (Throwable ignored) {
            Ev0Log.warn(LOGGER, "Failed to register HopperComponentSystem: " + ignored.getMessage());
        }
        try {
            this.getEntityStoreRegistry().registerSystem(new WirelessHopperPlaceSystem());
            Ev0Log.info(LOGGER, "Registered WirelessHopperPlaceSystem");
        }
        catch (Throwable ignored) {
            Ev0Log.warn(LOGGER, "Failed to register WirelessHopperPlaceSystem: " + ignored.getMessage());
        }
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