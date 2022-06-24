package com.luna.synthesis.mixins;

import com.luna.synthesis.events.MessageSentEvent;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
public class GuiScreenMixin {

    private String newMessage = "";

    @Inject(method = "sendChatMessage(Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    public void onMessageSent(String message, CallbackInfo ci) {
        MessageSentEvent event = new MessageSentEvent(message);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            ci.cancel();
        } else if (!event.message.equals(message)) {
            newMessage = event.message;
        }
    }

    @ModifyVariable(method = "sendChatMessage(Ljava/lang/String;)V", at = @At("HEAD"))
    public String overrideMessageSent(String msg) {
        if (!newMessage.equals("")) {
            msg = newMessage;
            newMessage = "";
        }
        return msg;
    }
}
