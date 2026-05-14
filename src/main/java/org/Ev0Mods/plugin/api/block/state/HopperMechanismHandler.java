/*
 * Decompiled with CFR 0.152.
 */
package org.Ev0Mods.plugin.api.block.state;

import com.hypixel.hytale.server.core.universe.world.World;
import voidbond.arcio.components.ArcioMechanismComponent;
import voidbond.arcio.mechanisms.IMechanism;

public class HopperMechanismHandler
implements IMechanism {
    @Override
    public int process(ArcioMechanismComponent mech, World world, int x, int y, int z) {
        return mech.getStrongestInputSignal(world);
    }

    @Override
    public String getDefaultState() {
        return "Off";
    }
}

