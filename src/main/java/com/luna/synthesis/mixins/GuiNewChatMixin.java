package com.luna.synthesis.mixins;

import com.google.common.collect.Lists;
import com.luna.synthesis.Comment;
import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import com.luna.synthesis.features.utilities.SearchMode;
import com.luna.synthesis.mixins.accessors.GuiChatAccessor;
import com.luna.synthesis.mixins.accessors.GuiContainerAccessor;
import com.luna.synthesis.utils.ChatLib;
import com.luna.synthesis.utils.MixinUtils;
import com.luna.synthesis.utils.Utils;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin {

    private boolean needsRefresh = false;
    @Shadow private final List<ChatLine> chatLines = Lists.newArrayList();
    private final Config config = Synthesis.getInstance().getConfig();

    @Inject(method = "clearChatMessages", at = @At(value = "INVOKE", target = "Ljava/util/List;clear()V", ordinal = 2), cancellable = true)
    public void clearChatMessages(CallbackInfo ci) {
        if (Synthesis.getInstance() != null && Synthesis.getInstance().getConfig() != null && Synthesis.getInstance().getConfig().futureKeepSentMessages) {
            ci.cancel();
        }
    }

    @Inject(method = "getChatWidth", at = @At("HEAD"), cancellable = true)
    public void getChatWidth(CallbackInfoReturnable<Integer> cir) {
        if (Synthesis.getInstance() != null && Synthesis.getInstance().getConfig() != null && Synthesis.getInstance().getConfig().utilitiesContainerChat && Minecraft.getMinecraft().currentScreen instanceof GuiContainer && Synthesis.getInstance().getConfig().utilitiesResizeContainerChat) {
            cir.setReturnValue(((GuiContainerAccessor) Minecraft.getMinecraft().currentScreen).getGuiLeft());
            needsRefresh = true;
        } else {
            if (needsRefresh) {
                needsRefresh = false;
                Minecraft.getMinecraft().ingameGUI.getChatGUI().refreshChat();
            }
        }
    }

    @Inject(method = "getChatOpen", at = @At("HEAD"), cancellable = true)
    public void getChatOpen(CallbackInfoReturnable<Boolean> cir) {
        if (Synthesis.getInstance() != null && Synthesis.getInstance().getConfig() != null && Synthesis.getInstance().getConfig().utilitiesContainerChat && Minecraft.getMinecraft().currentScreen instanceof GuiContainer) {
            if (MixinUtils.inputField != null && MixinUtils.inputField.isFocused()) {
                cir.setReturnValue(true);
            }
        }
    }

    @Comment("Walmart solution to not having continue inside mixins. Kind of a cool mixin, prevents lag with a ton of chat lines when trying to search.")
    @ModifyVariable(method = "refreshChat", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;", shift = At.Shift.BEFORE), index = 1)
    public int i(int in) {
        GuiScreen gui = Minecraft.getMinecraft().currentScreen;
        if (SearchMode.isSearchMode && ((gui instanceof GuiChat) || (gui instanceof GuiContainer && config.utilitiesContainerChat && MixinUtils.inputField != null && MixinUtils.inputField.isFocused()))) {
            GuiTextField input = gui instanceof GuiChat ? ((GuiChatAccessor) gui).getInputField() : MixinUtils.inputField;
            if (!input.getText().equals("")) {
                String line = StringUtils.stripControlCodes(this.chatLines.get(in).getChatComponent().getUnformattedText()).toLowerCase();
                if (!line.contains(input.getText().toLowerCase()) || !line.equals("search mode on")) {
                    for (int i = in; i >= 0; i--) {
                        String line2 = StringUtils.stripControlCodes(this.chatLines.get(i).getChatComponent().getUnformattedText()).toLowerCase();
                        if (line2.contains(input.getText().toLowerCase()) || line2.equals("search mode on")) {
                            return i;
                        }
                    }
                    return -1;
                }
                return in;
            }
        }
        return in;
    }

    @Comment("And here's the failsafe to be able to 'continue' in the last iteration of the loop")
    @Inject(method = "refreshChat", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void refreshChat(CallbackInfo ci, int i) {
        if (i == -1) {
            ci.cancel();
        }
    }

    @Inject(method = "setChatLine", at = @At("HEAD"), cancellable = true)
    public void setChatLine(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        if (SearchMode.isSearchMode && Minecraft.getMinecraft().currentScreen instanceof GuiChat) {
            GuiTextField input = ((GuiChatAccessor) Minecraft.getMinecraft().currentScreen).getInputField();
            String text = chatComponent.getUnformattedText().toLowerCase();
            if (!input.getText().equals("") && !text.contains(input.getText().toLowerCase()) && !displayOnly) {
                this.chatLines.add(0, new ChatLine(updateCounter, chatComponent, chatLineId));
                ci.cancel();
            }
        }
    }
}
