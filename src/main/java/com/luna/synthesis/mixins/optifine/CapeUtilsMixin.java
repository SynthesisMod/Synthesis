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

    @Dynamic
    @ModifyVariable(method = "downloadCape(Lnet/minecraft/client/entity/AbstractClientPlayer;)V", at = @At("STORE"), name = "username")
    private static String downloadCape(String in) {
        if (!config.utilitiesCapesCustomCape.equals("") && in.equals(Minecraft.getMinecraft().getSession().getUsername())) {
            return config.utilitiesCapesCustomCape;
        }
        if (config.utilitiesCapesTransYeti && in.equals("Yeti ")) {
            return "Yeti";
        }
        if (config.utilitiesCapesTransTerracotta && in.equals("Terracotta ")) {
            return "Terracotta";
        }
        if (config.utilitiesCapesNonBinaryBonzo && in.equals("Bonzo ")) {
            return "Bonzo";
        }
        if (config.utilitiesCapesCandyCaneGrinch && in.equals("Grinch ")) {
            return "Grinch";
        }
        return in;
    }
}
