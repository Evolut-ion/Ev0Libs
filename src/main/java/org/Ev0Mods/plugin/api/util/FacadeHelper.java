/*
 * Decompiled with CFR 0.152.
 */
package org.Ev0Mods.plugin.api.util;

import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockPatternRule;
import java.lang.reflect.Method;
import java.util.regex.Pattern;
import org.Ev0Mods.plugin.api.component.EngineCompat;

public final class FacadeHelper {
    private static final Pattern VARIANT_SUFFIX = Pattern.compile("(?i)_(?:Corner(?:_(?:NE|NW|SE|SW))?|Edge(?:_(?:NS|EW|UD|NE|NW|SE|SW))?|Straight(?:_(?:NS|EW|UD))?|T(?:_(?:N|E|S|W|U|D))?|Cross|End(?:_(?:N|E|S|W|U|D))?|Inner(?:_(?:NE|NW|SE|SW))?|Outer(?:_(?:NE|NW|SE|SW))?|NESW|NES|NEW|NSW|ESW|NS|EW|UD|NE|NW|SE|SW|[NESWUD])$");

    private FacadeHelper() {
    }

    public static String normalizeBlockId(String blockKey) {
        String prev;
        if (blockKey == null || blockKey.isEmpty()) {
            return blockKey;
        }
        String result = blockKey;
        do {
            prev = result;
        } while (!(result = VARIANT_SUFFIX.matcher(result).replaceFirst("")).equals(prev) && !result.isEmpty());
        return result.isEmpty() ? blockKey : result;
    }

    public static int computeConnectionMask(World world, Vector3i pos, String baseId) {
        if (world == null || pos == null || baseId == null || baseId.isEmpty()) {
            return 0;
        }
        int mask = 0;
        for (ConnectedBlockPatternRule.AdjacentSide side : ConnectedBlockPatternRule.AdjacentSide.values()) {
            Vector3i adj = new Vector3i(pos.x + ((Vector3i)((Object)side.relativePosition)).x, pos.y + ((Vector3i)((Object)side.relativePosition)).y, pos.z + ((Vector3i)((Object)side.relativePosition)).z);
            try {
                String adjId;
                Object bt;
                WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(adj.x, adj.z));
                if (chunk == null || (bt = EngineCompat.getBlockType(chunk, adj.x, adj.y, adj.z)) == null || (adjId = FacadeHelper.resolveBlockTypeId(bt)) == null || !baseId.equalsIgnoreCase(FacadeHelper.normalizeBlockId(adjId))) continue;
                mask |= 1 << side.ordinal();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return mask;
    }

    public static int rotateMask(int mask, int rotation) {
        if (rotation == 0) {
            return mask;
        }
        int result = mask & 3;
        for (int ord = 2; ord <= 5; ++ord) {
            if ((mask & 1 << ord) == 0) continue;
            int rotOrd = Math.floorMod(ord - 2 - rotation, 4) + 2;
            result |= 1 << rotOrd;
        }
        return result;
    }

    public static String buildVariantKey(String baseId, int connectionMask) {
        if (baseId == null || baseId.isEmpty()) {
            return baseId;
        }
        boolean n = (connectionMask & 4) != 0;
        boolean e = (connectionMask & 8) != 0;
        boolean s = (connectionMask & 0x10) != 0;
        boolean w = (connectionMask & 0x20) != 0;
        int hCount = (n ? 1 : 0) + (e ? 1 : 0) + (s ? 1 : 0) + (w ? 1 : 0);
        String suffix = switch (hCount) {
            case 1 -> {
                if (n) {
                    yield "_End_N";
                }
                if (e) {
                    yield "_End_E";
                }
                if (s) {
                    yield "_End_S";
                }
                yield "_End_W";
            }
            case 2 -> {
                if (n && s) {
                    yield "_Straight_NS";
                }
                if (e && w) {
                    yield "_Straight_EW";
                }
                if (n && e) {
                    yield "_Corner_NE";
                }
                if (n && w) {
                    yield "_Corner_NW";
                }
                if (s && e) {
                    yield "_Corner_SE";
                }
                yield "_Corner_SW";
            }
            case 3 -> {
                if (!n) {
                    yield "_T_N";
                }
                if (!e) {
                    yield "_T_E";
                }
                if (!s) {
                    yield "_T_S";
                }
                yield "_T_W";
            }
            case 4 -> "_Cross";
            default -> "";
        };
        return suffix.isEmpty() ? baseId : baseId + suffix;
    }

    public static String maskToString(int mask) {
        if (mask == 0) {
            return "none";
        }
        StringBuilder sb = new StringBuilder();
        for (ConnectedBlockPatternRule.AdjacentSide side : ConnectedBlockPatternRule.AdjacentSide.values()) {
            if ((mask & 1 << side.ordinal()) == 0) continue;
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(side.name());
        }
        return sb.toString();
    }

    private static String resolveBlockTypeId(Object blockType) {
        if (blockType == null) {
            return null;
        }
        for (String name : new String[]{"getId", "getKey", "getBlockKey", "getName"}) {
            try {
                String s;
                Method m = blockType.getClass().getMethod(name, new Class[0]);
                Object r = m.invoke(blockType, new Object[0]);
                if (!(r instanceof String) || (s = (String)r).isEmpty()) continue;
                return s;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        String raw = blockType.toString();
        return raw != null && !raw.isEmpty() ? raw : null;
    }
}

