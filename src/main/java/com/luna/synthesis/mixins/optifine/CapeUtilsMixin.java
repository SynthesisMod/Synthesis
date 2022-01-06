package com.luna.synthesis.mixins.optifine;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import com.luna.synthesis.utils.ChatLib;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(targets = "net.optifine.player.CapeUtils")
public class CapeUtilsMixin {

    private static final Config config = Synthesis.getInstance().getConfig();

    // I want to give people the ability to toggle anything in the mod they don't want.
    // I don't know what would cause people to toggle them off.
    // I wouldn't be mad, just disappointed.
    @Dynamic
    @ModifyVariable(method = "downloadCape(Lnet/minecraft/client/entity/AbstractClientPlayer;)V", at = @At("STORE"), name = "username")
    private static String downloadCape(String in) {
        if (!config.utilitiesOptifineCustomCape.equals("") && in.equals(Minecraft.getMinecraft().getSession().getUsername())) {
            return config.utilitiesOptifineCustomCape;
        }
        if (config.utilitiesOptifineTransYeti && in.equals("Yeti ")) {
            return "Yeti";
        }
        if (config.utilitiesOptifineTransTerracotta && in.equals("Terracotta ")) {
            return "Terracotta";
        }
        if (config.utilitiesOptifineNonBinaryBonzo && in.equals("Bonzo ")) {
            return "Bonzo";
        }
        if (config.utilitiesOptifineCandyCaneGrinch && in.equals("Grinch ")) {
            return "Grinch";
        }
        return in;
    }
}
