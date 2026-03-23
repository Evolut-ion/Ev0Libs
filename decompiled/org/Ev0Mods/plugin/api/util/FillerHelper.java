/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.math.vector.Vector3d
 *  com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType
 *  com.hypixel.hytale.server.core.universe.world.World
 */
package org.Ev0Mods.plugin.api.util;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.world.World;

public class FillerHelper {
    private World w;
    private BlockType bt;
    private Vector3d pos;
    private int x;
    private int y;
    private int z;

    public FillerHelper(World world, int x, int y, int z, BlockType b) {
        this.bt = b;
        this.w = world;
        world.getBlock(x, y, z);
        this.pos = new Vector3d((double)x, (double)y, (double)z);
    }

    private Vector3d getFiller() {
        int ba = this.w.getBlock((int)this.pos.x + 1, (int)this.pos.y, (int)this.pos.z);
        return new Vector3d((double)this.x, (double)this.y, (double)this.z);
    }
}

