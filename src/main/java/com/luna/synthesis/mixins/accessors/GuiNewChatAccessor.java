package com.luna.synthesis.mixins.accessors;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(GuiNewChat.class)
public interface GuiNewChatAccessor {

    @Accessor List<ChatLine> getChatLines();
    @Accessor List<ChatLine> getDrawnChatLines();
    @Accessor int getScrollPos();
    @Invoker void invokeSetChatLine(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly);
}
