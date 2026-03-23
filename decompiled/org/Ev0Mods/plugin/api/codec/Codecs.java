/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.codec.Codec
 *  com.hypixel.hytale.codec.codecs.EnumCodec
 *  com.hypixel.hytale.codec.codecs.array.ArrayCodec
 *  com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockPatternRule$AdjacentSide
 */
package org.Ev0Mods.plugin.api.codec;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockPatternRule;

public class Codecs {
    public static final Codec<ConnectedBlockPatternRule.AdjacentSide> SIDE = new EnumCodec(ConnectedBlockPatternRule.AdjacentSide.class);
    public static final Codec<ConnectedBlockPatternRule.AdjacentSide[]> SIDE_ARRAY = new ArrayCodec(SIDE, ConnectedBlockPatternRule.AdjacentSide[]::new);
}

