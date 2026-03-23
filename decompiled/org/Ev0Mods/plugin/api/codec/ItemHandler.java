/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.codec.lookup.CodecMapCodec
 *  com.hypixel.hytale.server.core.inventory.ItemStack
 */
package org.Ev0Mods.plugin.api.codec;

import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface ItemHandler {
    public static final CodecMapCodec<ItemHandler> CODEC = new CodecMapCodec("Type");

    public void output(Consumer<ItemStack> var1);

    public void input(Consumer<ItemStack> var1);

    default public List<ItemStack> outputList() {
        ArrayList<ItemStack> output = new ArrayList<ItemStack>();
        this.output(output::add);
        return output;
    }

    default public List<ItemStack> inputList() {
        ArrayList<ItemStack> input = new ArrayList<ItemStack>();
        this.input(input::add);
        return input;
    }
}

