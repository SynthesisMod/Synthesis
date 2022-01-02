package com.luna.synthesis.mixins.patcher;

import com.luna.synthesis.Comment;
import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import net.minecraft.client.gui.ChatLine;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Pseudo
@Mixin(targets = "club.sk1er.patcher.util.chat.ChatHandler", remap = false)
public class ChatHandlerMixin {

    private static final Config config = Synthesis.getInstance().getConfig();

    @Comment("ChatLine equals is bad. Calling GuiNewChat::refreshChat() creates a copy of the line, so equals doesn't actually work.")
    @Dynamic
    @Redirect(method = "deleteMessageByHash(I)Z", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"), remap = false)
    private static boolean contains(Set<ChatLine> toRemove, Object obj) {
        if (!config.patcherCompactChatFix) return toRemove.contains(obj);
        ChatLine cl2 = (ChatLine) obj;
        for (ChatLine cl : toRemove) {
            if (cl.getChatLineID() == cl2.getChatLineID() && cl.getUpdatedCounter() == cl2.getUpdatedCounter() && cl.getChatComponent().equals(cl2.getChatComponent())) {
                return true;
            }
        }
        return false;
    }
}