/*
 * Decompiled with CFR 0.152.
 */
package org.Ev0Mods.plugin.api.codec;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.Rangef;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.Ev0Mods.plugin.api.codec.ItemHandler;
import org.Ev0Mods.plugin.api.util.MathHelper;
import org.bson.BsonDocument;

public class IdOutput
implements ItemHandler {
    public static String ID = "Item";
    public static BuilderCodec<IdOutput> CODEC = BuilderCodec.builder(IdOutput.class, IdOutput::new).build();
    protected String itemId = null;
    protected Rangef amount = new Rangef(1.0f, 1.0f);
    @Nullable
    protected BsonDocument metadata = BsonDocument.parse("{}");

    @Override
    public void output(Consumer<ItemStack> consumer) {
        if (this.itemId != null && !this.itemId.isEmpty()) {
            consumer.accept(new ItemStack(this.itemId, MathHelper.fromRange(this.amount), this.metadata));
        }
    }

    @Override
    public void input(Consumer<ItemStack> inputConsumer) {
        if (this.itemId != null && !this.itemId.isEmpty()) {
            inputConsumer.accept(new ItemStack(this.itemId, MathHelper.fromRange(this.amount), this.metadata));
        }
    }

    public String itemId() {
        return this.itemId;
    }

    public Rangef amount() {
        return this.amount;
    }

    public String toString() {
        return "IdOutput{itemId='" + this.itemId + "', amount=" + String.valueOf(this.amount) + ", metadata=" + String.valueOf(this.metadata) + "}";
    }
}

