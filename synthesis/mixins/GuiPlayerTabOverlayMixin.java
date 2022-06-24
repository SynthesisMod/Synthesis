package com.luna.synthesis.mixins;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GuiPlayerTabOverlay.class)
public class GuiPlayerTabOverlayMixin {

    private final Config config = Synthesis.getInstance().getConfig();

    // mhm tasty spaghetti

    @ModifyVariable(method = "setFooter", at = @At("HEAD"), argsOnly = true)
    public IChatComponent overrideFooter(IChatComponent in) {
        if (config.cleanupTablistFooter) {
            String s = in.getFormattedText()
                    .replace("\n§r§r§r§r§s§r\n§r§r§aRanks, Boosters & MORE! §r§c§lSTORE.HYPIXEL.NET§r", "")
                    .replace("§r§aRanks, Boosters & MORE! §r§c§lSTORE.HYPIXEL.NET§r", "");
            return s.length() == 0 ? null : new ChatComponentText(s);
        }
        return in;
    }

    @ModifyVariable(method = "setHeader", at = @At("HEAD"), argsOnly = true)
    public IChatComponent overrideHeader(IChatComponent in) {
        if (config.cleanupTablistHeader) {
            return null;
        }
        return in;
    }
}
