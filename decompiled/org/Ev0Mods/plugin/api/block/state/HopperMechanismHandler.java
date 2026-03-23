/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.server.core.universe.world.World
 *  voidbond.arcio.components.ArcioMechanismComponent
 *  voidbond.arcio.mechanisms.IMechanism
 */
package org.Ev0Mods.plugin.api.block.state;

import com.hypixel.hytale.server.core.universe.world.World;
import voidbond.arcio.components.ArcioMechanismComponent;
import voidbond.arcio.mechanisms.IMechanism;

public class HopperMechanismHandler
implements IMechanism {
    public int process(ArcioMechanismComponent mech, World world, int x, int y, int z) {
        return mech.getStrongestInputSignal(world);
    }

    public String getDefaultState() {
        return "Off";
    }
}

