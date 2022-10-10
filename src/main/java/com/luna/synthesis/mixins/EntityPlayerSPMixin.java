package com.luna.synthesis.mixins;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityPlayerSP.class)
public class EntityPlayerSPMixin {

    private final Config config = Synthesis.getInstance().getConfig();

    // It's possible (and very easy) to make portals never close any gui (since it closes it only clientside, server would not care)
    // BUT I'm not sure if Hypixel would like that/there's any potential false bans SO only chat for now.
    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;doesGuiPauseGame()Z"))
    public boolean overrideDoesGuiPauseGame(GuiScreen gui) {
        if (config.utilitiesPortalChat) {
            return gui.doesGuiPauseGame() || gui instanceof GuiChat; //|| gui instanceof GuiContainer;
        }
        return gui.doesGuiPauseGame();
    }
}
