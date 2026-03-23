/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.protocol.Rangef
 */
package org.Ev0Mods.plugin.api.util;

import com.hypixel.hytale.protocol.Rangef;
import java.util.Random;

public class MathHelper {
    public static final Random RNG = new Random();

    public static int fromRange(Rangef range) {
        int low = (int)Math.ceil(range.min);
        int high = (int)Math.floor(range.max);
        return low + RNG.nextInt(high - low + 1);
    }
}

