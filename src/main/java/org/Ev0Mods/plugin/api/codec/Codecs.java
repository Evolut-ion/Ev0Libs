/*
 * Decompiled with CFR 0.152.
 */
package org.Ev0Mods.plugin.api.codec;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockPatternRule;

public class Codecs {
    public static final Codec<ConnectedBlockPatternRule.AdjacentSide> SIDE = new EnumCodec<ConnectedBlockPatternRule.AdjacentSide>(ConnectedBlockPatternRule.AdjacentSide.class);
    public static final Codec<ConnectedBlockPatternRule.AdjacentSide[]> SIDE_ARRAY = new ArrayCodec<ConnectedBlockPatternRule.AdjacentSide>(SIDE, ConnectedBlockPatternRule.AdjacentSide[]::new);
}

