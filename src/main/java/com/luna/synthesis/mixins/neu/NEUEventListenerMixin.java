package com.luna.synthesis.mixins.neu;

import com.luna.synthesis.utils.MixinUtils;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(targets = "io.github.moulberry.notenoughupdates.NEUEventListener", remap = false)
public class NEUEventListenerMixin {

    @Dynamic
    @ModifyArg(method = "onTick", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;enableRepeatEvents(Z)V"))
    public boolean overrideKeyEvents(boolean in) {
        if (MixinUtils.inputField != null && MixinUtils.inputField.isFocused()) {
            return true;
        }
        return in;
    }
}
