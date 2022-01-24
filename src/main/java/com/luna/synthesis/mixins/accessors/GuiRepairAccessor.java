package com.luna.synthesis.mixins.accessors;

import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiRepair.class)
public interface GuiRepairAccessor {

    @Accessor GuiTextField getNameField();
}
