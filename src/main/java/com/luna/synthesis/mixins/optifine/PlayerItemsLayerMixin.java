package com.luna.synthesis.mixins.optifine;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.optifine.player.PlayerItemsLayer")
public class PlayerItemsLayerMixin {

    private final Config config = Synthesis.getInstance().getConfig();

    // It's so great that I just made this almost after the santa hat is gone, but hey, here's to 2022 halloween if humanity hasn't collapsed by then.
    @Inject(method = "renderEquippedItems(Lnet/minecraft/entity/EntityLivingBase;FF)V", at = @At("HEAD"), cancellable = true)
    public void renderEquippedItems(EntityLivingBase entityLiving, float scale, float partialTicks, CallbackInfo ci) {
        if (config.utilitiesOptifineHideHats) {
            ci.cancel();
        }
    }
}
