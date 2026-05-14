/*
 * Decompiled with CFR 0.152.
 */
package org.Ev0Mods.plugin.api.block.state;

import org.Ev0Mods.plugin.api.block.state.HopperMechanismHandler;
import voidbond.arcio.ArcioPlugin;

public class HopperArcioRegistration {
    public static void register() {
        ArcioPlugin.get().registerMechanism("Hopper", new HopperMechanismHandler());
    }
}

