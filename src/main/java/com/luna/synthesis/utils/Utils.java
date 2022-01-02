package com.luna.synthesis.utils;

import com.luna.synthesis.Comment;
import com.luna.synthesis.mixins.GuiContainerMixin;
import com.luna.synthesis.mixins.accessors.GuiNewChatAccessor;
import com.luna.synthesis.mixins.accessors.ItemModelMesherAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.Item;

import java.util.List;

public class Utils {

    @Comment("Copied and adapted from Patcher")
    public static boolean isDivider(String message) {
        if (message.length() < 5) {
            return false;
        } else {
            for (int i = 0; i < message.length(); i++) {
                char c = message.charAt(i);
                if (c != '-' && c != '=' && c != '\u25AC') {
                    return false;
                }
            }
        }
        return true;
    }
}
