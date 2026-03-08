package org.Ev0Mods.plugin.api.block.state;

import voidbond.arcio.ArcioPlugin;

/**
 * Isolated helper for ArcIO mechanism registration.
 *
 * This class is ONLY loaded via reflection after confirming ArcIO is present
 * on the classpath, so its ArcIO imports will never cause a
 * {@link NoClassDefFoundError} when ArcIO is absent at runtime.
 */
public class HopperArcioRegistration {

    public static void register() {
        ArcioPlugin.get().registerMechanism("Hopper", new HopperMechanismHandler());
    }
}
