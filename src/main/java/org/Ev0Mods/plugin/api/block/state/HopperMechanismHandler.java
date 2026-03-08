package org.Ev0Mods.plugin.api.block.state;

import com.hypixel.hytale.server.core.universe.world.World;
import voidbond.arcio.components.ArcioMechanismComponent;
import voidbond.arcio.mechanisms.IMechanism;

/**
 * ArcIO IMechanism handler for the Hopper block.
 * Passes the strongest incoming signal straight through so downstream
 * ArcIO cables/blocks receive the same signal level.
 *
 * This class is safe to load at all times — it is only ever instantiated
 * from {@link HopperArcioRegistration}, which is loaded via reflection after
 * confirming ArcIO is present.
 */
public class HopperMechanismHandler implements IMechanism {

    @Override
    public int process(ArcioMechanismComponent mech, World world, int x, int y, int z) {
        return mech.getStrongestInputSignal(world);
    }

    @Override
    public String getDefaultState() {
        return "Off";
    }
}
