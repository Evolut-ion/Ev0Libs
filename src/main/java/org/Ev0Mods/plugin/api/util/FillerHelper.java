package org.Ev0Mods.plugin.api.util;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.accessor.BlockAccessor;

public class FillerHelper {
    private World w;
    private BlockType bt;
    private Vector3d pos;
    private int x;
    private int y;
    private int z;


    public FillerHelper(World world, int x, int y, int z, BlockType b){
        this.bt = b;
        this.w = world;
        world.getBlock(x,y,z);
        this.pos = new Vector3d(x, y, z);

    }
    private Vector3d getFiller(){
        int ba = w.getBlock((int)pos.x +1, (int)pos.y, (int)pos.z);
        return new Vector3d(x,y,z);

    }
}
