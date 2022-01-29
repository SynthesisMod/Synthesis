package com.luna.synthesis.mixins;

import com.google.common.collect.Lists;
import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import com.luna.synthesis.features.utilities.SearchMode;
import com.luna.synthesis.mixins.accessors.GuiChatAccessor;
import com.luna.synthesis.mixins.accessors.GuiContainerAccessor;
import com.luna.synthesis.utils.MixinUtils;
import com.luna.synthesis.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin {

    private boolean needsRefresh = false;
    @Final @Shadow private final List<ChatLine> chatLines = Lists.newArrayList();
    @Final @Shadow private final List<ChatLine> drawnChatLines = Lists.newArrayList();
    @Shadow public abstract void deleteChatLine(int id);
    @Shadow protected abstract void setChatLine(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly);
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

    // A way to "continue" in loops from inside a mixin. Kinda proud of this one ngl, fixes lag when searching for a lot of lines, mostly with Patcher's void chat.
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

    // This just here so it's possible to "continue" in the last iteration of the loop
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

    // The section below is used so the SEARCH MODE ON message always stays at the bottom of the chat

    // Fix for refreshChat deleting messages with same id? For no reason either??
    // If you're not using the mod, go to a hub and tab player's names so the message of potential players shows up
    // Then, go to chat options and modify any setting, like opacity, doesn't matter if end value is the same, it just has to be changed
    // THE MESSAGE WILL DISAPPEAR, FOR NO REASON EITHER, WHAT THE FWICK
    @Redirect(method = "setChatLine", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;deleteChatLine(I)V"))
    public void deleteChatLine(GuiNewChat chat, int id, IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly) {
        if (!displayOnly) {
            this.deleteChatLine(id);
        }
    }

    @ModifyArg(method = "setChatLine", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", ordinal = 0))
    public int modifyIndex(int in, Object line) {
        ChatLine cl = (ChatLine) line;
        if (SearchMode.isSearchMode && !cl.getChatComponent().getFormattedText().contains(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.BOLD + "SEARCH MODE ON")) {
            if (this.drawnChatLines.size() == 0) {
                this.deleteChatLine("synthesissearchmode".hashCode());
                this.setChatLine(new ChatComponentText(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.BOLD + "SEARCH MODE ON"), "synthesissearchmode".hashCode(), Minecraft.getMinecraft().ingameGUI.getUpdateCounter(), true);
            }
            return 1;
        }
        return in;
    }

    @Redirect(method = "deleteChatLine", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ChatLine;getChatLineID()I"))
    public int fixCrashMaybe(ChatLine cl) {
        if (cl == null) {
            return -12;
        } else {
            return cl.getChatLineID();
        }
    }
}
