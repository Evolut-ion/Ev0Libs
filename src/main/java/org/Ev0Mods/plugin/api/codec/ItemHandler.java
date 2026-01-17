package org.Ev0Mods.plugin.api.codec;

import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.server.core.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface ItemHandler {
    CodecMapCodec<ItemHandler> CODEC = new CodecMapCodec<>("Type");

    void output(Consumer<ItemStack> consumer);

    void input(Consumer <ItemStack> inputConsumer);

    default List<ItemStack> outputList() {
        final List<ItemStack> output = new ArrayList<>();
        this.output(output::add);
        return output;
    }
    default List<ItemStack> inputList() {
        final List<ItemStack> input = new ArrayList<>();
        this.input(input::add);
        return input;
    }
}
