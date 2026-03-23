/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  voidbond.arcio.ArcioPlugin
 *  voidbond.arcio.mechanisms.IMechanism
 */
package org.Ev0Mods.plugin.api.block.state;

import org.Ev0Mods.plugin.api.block.state.HopperMechanismHandler;
import voidbond.arcio.ArcioPlugin;
import voidbond.arcio.mechanisms.IMechanism;

public class HopperArcioRegistration {
    public static void register() {
        ArcioPlugin.get().registerMechanism("Hopper", (IMechanism)new HopperMechanismHandler());
    }
}

