package com.luna.synthesis.mixins;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLivingBase {

    private final Config config = Synthesis.getInstance().getConfig();

    public EntityPlayerMixin(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "isEntityInsideOpaqueBlock", at = @At("HEAD"), cancellable = true)
    public void isEntityInsideOpaqueBlock(CallbackInfoReturnable<Boolean> cir) {
        if (config.utilitiesArmadilloFix && this.isRiding() && this.ridingEntity instanceof EntityZombie) {
            cir.setReturnValue(false);
        }
    }
}
