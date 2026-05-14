/*
 * Decompiled with CFR 0.152.
 */
package org.Ev0Mods.plugin.api;

public interface HopperSlotPolicy {
    public int[] getHopperProtectedInputSlots();

    default public int[] getHopperProtectedOutputSlots() {
        return new int[0];
    }
}

