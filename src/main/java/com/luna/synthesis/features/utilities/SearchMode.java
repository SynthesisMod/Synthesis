package com.luna.synthesis.features.utilities;

import com.luna.synthesis.Comment;
import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import com.luna.synthesis.mixins.accessors.GuiNewChatAccessor;
import com.luna.synthesis.utils.ChatLib;
import com.luna.synthesis.utils.MixinUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.List;
import java.util.stream.Collectors;

public class SearchMode {

    public static boolean isSearchMode = false;
    private final Config config = Synthesis.getInstance().getConfig();

    @Comment("Ctrl is 29, f is 33")
    @SubscribeEvent
    public void onKeyTyped(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (!(event.gui instanceof GuiChat || (event.gui instanceof GuiContainer && config.utilitiesContainerChat && MixinUtils.inputField != null && MixinUtils.inputField.isFocused()))) return;
        if (!config.utilitiesChatSearchMode) return;
        if (!Keyboard.getEventKeyState()) return;
        if (Keyboard.isRepeatEvent()) return;
        if (Keyboard.getEventKey() == 33 && Keyboard.isKeyDown(29)) {
            isSearchMode = !isSearchMode;
            if (isSearchMode) {
                Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new ChatComponentText(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.BOLD + "SEARCH MODE ON"), "synthesis".hashCode());
            } else {
                Minecraft.getMinecraft().ingameGUI.getChatGUI().deleteChatLine("synthesis".hashCode());
            }
            event.setCanceled(true);
            if (!isSearchMode) {
                Minecraft.getMinecraft().ingameGUI.getChatGUI().refreshChat();
            }
        } else if (Keyboard.getEventKey() == 1) {
            if (isSearchMode) isSearchMode = false;
            Minecraft.getMinecraft().ingameGUI.getChatGUI().deleteChatLine("synthesis".hashCode());
            Minecraft.getMinecraft().ingameGUI.getChatGUI().refreshChat();
        } else if (Keyboard.getEventKey() == 28 && isSearchMode) {
            if (!config.utilitiesChatSearchKeyRefresh) {
                Minecraft.getMinecraft().ingameGUI.getChatGUI().refreshChat();
            }
            event.setCanceled(true);
        }
    }

    @Comment("Keyboard::getKeyEventState is always false inside a GuiContainer??")
    @SubscribeEvent
    public void onKeyTypedPost(GuiScreenEvent.KeyboardInputEvent.Post event) {
        if (!(event.gui instanceof GuiChat || (event.gui instanceof GuiContainer && config.utilitiesContainerChat && MixinUtils.inputField != null && MixinUtils.inputField.isFocused()))) return;
        if (!Keyboard.getEventKeyState() && event.gui instanceof GuiChat) return;
        if (config.utilitiesChatSearchKeyRefresh && isSearchMode) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().refreshChat();
        }
    }

    @SubscribeEvent
    public void onMouseClicked(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (!isSearchMode) return;
        if (!config.utilitiesChatScrollClick) return;
        if (Mouse.getEventButton() != 1) return;
        if (!Mouse.getEventButtonState()) return;
        int index = getChatLineIndex(Mouse.getX(), Mouse.getY());
        if (index == -1) return;
        List<ChatLine> chatLines = ((GuiNewChatAccessor) Minecraft.getMinecraft().ingameGUI.getChatGUI()).getDrawnChatLines();
        ChatLine cl = ((GuiNewChatAccessor) Minecraft.getMinecraft().ingameGUI.getChatGUI()).getDrawnChatLines().get(index);
        int newIndex = 0;
        if (chatLines.stream().map(chatLine -> chatLine.getChatComponent().equals(cl.getChatComponent())).count() > 1) {
            for (int i = 0; i < index; i++) {
                if (chatLines.get(i).getChatComponent().equals(cl.getChatComponent())) {
                    newIndex++;
                }
            }
        }
        isSearchMode = false;
        Minecraft.getMinecraft().ingameGUI.getChatGUI().deleteChatLine("synthesis".hashCode());
        Minecraft.getMinecraft().ingameGUI.getChatGUI().refreshChat();
        Minecraft.getMinecraft().ingameGUI.getChatGUI().resetScroll();
        chatLines = ((GuiNewChatAccessor) Minecraft.getMinecraft().ingameGUI.getChatGUI()).getDrawnChatLines();
        int scrollAmount = -1;
        if (newIndex == 0) {
            scrollAmount = chatLines.stream().map(ChatLine::getChatComponent).collect(Collectors.toList()).indexOf(cl.getChatComponent());
        }
        if (chatLines.stream().filter(chatLine -> chatLine.getChatComponent().equals(cl.getChatComponent())).count() > 1) {
            for (int i = 0; i < chatLines.size(); i++) {
                if (chatLines.get(i).getChatComponent().equals(cl.getChatComponent())) {
                    if (newIndex == 0) {
                        scrollAmount = i;
                        break;
                    } else {
                        newIndex--;
                    }
                }
            }
        }
        Minecraft.getMinecraft().ingameGUI.getChatGUI().scroll(scrollAmount);
    }

    private int getChatLineIndex(int mouseX, int mouseY) {
        if (!Minecraft.getMinecraft().ingameGUI.getChatGUI().getChatOpen()) {
            return -1;
        } else {
            ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
            int i = scaledresolution.getScaleFactor();
            float f = Minecraft.getMinecraft().ingameGUI.getChatGUI().getChatScale();
            int j = mouseX / i - 3;
            int k = mouseY / i - 27;
            j = MathHelper.floor_float(j / f);
            k = MathHelper.floor_float(k / f);

            if (j >= 0 && k >= 0) {
                //int l = Minecraft.getMinecraft().ingameGUI.getChatGUI().getLineCount();
                int l = Math.min(Minecraft.getMinecraft().ingameGUI.getChatGUI().getLineCount(), ((GuiNewChatAccessor) Minecraft.getMinecraft().ingameGUI.getChatGUI()).getDrawnChatLines().size());

                if (j <= MathHelper.floor_float(Minecraft.getMinecraft().ingameGUI.getChatGUI().getChatWidth() / Minecraft.getMinecraft().ingameGUI.getChatGUI().getChatScale()) && k < Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * l + l) {
                    int i1 = k / Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + ((GuiNewChatAccessor) Minecraft.getMinecraft().ingameGUI.getChatGUI()).getScrollPos();

                    if (i1 >= 0 && i1 < ((GuiNewChatAccessor) Minecraft.getMinecraft().ingameGUI.getChatGUI()).getDrawnChatLines().size()) {
                        return i1;
                        //return ((GuiNewChatAccessor) Minecraft.getMinecraft().ingameGUI.getChatGUI()).getDrawnChatLines().get(i1);
                    }

                    return -1;
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        }
    }
}
