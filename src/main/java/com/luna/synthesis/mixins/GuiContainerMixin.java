package com.luna.synthesis.mixins;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import com.luna.synthesis.utils.MixinUtils;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(GuiContainer.class)
public class GuiContainerMixin extends GuiScreen {

    private final Config config = Synthesis.getInstance().getConfig();

    private GuiTextField inputField;
    private int sentHistoryCursor = -1;
    private boolean playerNamesFound;
    private boolean waitingOnAutocomplete;
    private int autocompleteIndex;
    private final List<String> foundPlayerNames = new ArrayList<>();
    private String historyBuffer = "";

    @Inject(method = "initGui", at = @At("RETURN"))
    public void initGui(CallbackInfo ci) {
        if (config.utilitiesContainerChat) {
            this.sentHistoryCursor = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
            this.inputField = new GuiTextField(0, this.fontRendererObj, 4, this.height - 12, this.width - 4, 12);
            this.inputField.setMaxStringLength(256);
            this.inputField.setEnableBackgroundDrawing(false);
            this.inputField.setText(config.utilitiesTransferContainerChat && MixinUtils.inputField != null && MixinUtils.inputField.isFocused() && !MixinUtils.inputField.getText().equals("") ? MixinUtils.inputField.getText() : "");
            this.inputField.setFocused(MixinUtils.inputField != null && MixinUtils.inputField.isFocused());
            this.inputField.setCanLoseFocus(false);
            MixinUtils.inputField = this.inputField;
            if (config.utilitiesResizeContainerChat) {
                mc.ingameGUI.getChatGUI().refreshChat();
            }
        }
    }

    @Inject(method = "updateScreen", at = @At("RETURN"))
    public void updateScreen(CallbackInfo ci) {
        if (config.utilitiesContainerChat) {
            this.inputField.updateCursorCounter();
        }
    }

    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/GuiScreen.drawScreen(IIF)V"))
    public void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (config.utilitiesContainerChat && this.inputField.isFocused()) {
            Gui.drawRect(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
            this.inputField.drawTextBox();
        }
    }
}
