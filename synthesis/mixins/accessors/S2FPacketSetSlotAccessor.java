package com.luna.synthesis.mixins.accessors;

import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(S2FPacketSetSlot.class)
public interface S2FPacketSetSlotAccessor {

    @Accessor int getWindowId();
    @Accessor int getSlot();
    @Accessor(value = "item") ItemStack getItemStack();
}
