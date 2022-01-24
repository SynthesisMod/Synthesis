package com.luna.synthesis.features.utilities;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import com.luna.synthesis.utils.MixinUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class ContainerChat {

    private final Config config = Synthesis.getInstance().getConfig();

    private String historyBuffer = "";
    private int sentHistoryCursor = -1;

    // For chat transfer magic
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (!config.utilitiesContainerChat) return;
        if (config.utilitiesReopenContainerChat && MixinUtils.inputField != null) {
            if (event.gui == null) {
                if (Minecraft.getMinecraft().currentScreen instanceof GuiContainer) {
                    if (MixinUtils.inputField.isFocused()) {
                        event.gui = new GuiChat(MixinUtils.inputField.getText());
                    }
                } else {
                    MixinUtils.inputField = null;
                }
            }
        }
    }

    // Originally in mixin, had to rewrite because SBE and Cowlection would have bad compatibility issues.
    // I also need to fix this working when SBE's search bar is focused, but I don't think I'll be able to do that.
    @SubscribeEvent
    public void onKeyTyped(GuiScreenEvent.KeyboardInputEvent event) {
        if (!(event.gui instanceof GuiContainer)) return;
        if (!config.utilitiesContainerChat) return;
        if (!Keyboard.getEventKeyState()) return;
        int keyCode = Keyboard.getEventKey();
        if (MixinUtils.inputField == null) return;
        if (event instanceof GuiScreenEvent.KeyboardInputEvent.Pre) {
            if (MixinUtils.inputField.isFocused()) {
                if (keyCode == 1) {
                    MixinUtils.inputField.setFocused(false);
                    MixinUtils.inputField.setText("");
                    Keyboard.enableRepeatEvents(false);
                    Minecraft.getMinecraft().ingameGUI.getChatGUI().resetScroll();
                }
                if (keyCode != Minecraft.getMinecraft().gameSettings.keyBindScreenshot.getKeyCode()) {
                    event.setCanceled(true);
                }
            } else {
                if (keyCode == Minecraft.getMinecraft().gameSettings.keyBindChat.getKeyCode()) {
                    MixinUtils.inputField.setFocused(true);
                    Keyboard.enableRepeatEvents(true);
                    return;
                } else if (keyCode == Minecraft.getMinecraft().gameSettings.keyBindCommand.getKeyCode()) {
                    MixinUtils.inputField.setText("/");
                    MixinUtils.inputField.setFocused(true);
                    Keyboard.enableRepeatEvents(true);
                    return;
                }
            }

            if (keyCode != 28 && keyCode != 156) {
                if (keyCode == 200) {
                    getSentHistory(-1);
                } else if (keyCode == 208) {
                    getSentHistory(1);
                } else {
                    MixinUtils.inputField.textboxKeyTyped(Keyboard.getEventCharacter(), keyCode);
                }
            } else {
                if (!MixinUtils.inputField.isFocused()) return;
                String text = MixinUtils.inputField.getText().trim();

                if (!text.isEmpty()) {
                    event.gui.sendChatMessage(text);
                }
                sentHistoryCursor = Minecraft.getMinecraft().ingameGUI.getChatGUI().getSentMessages().size();
                MixinUtils.inputField.setText("");
                MixinUtils.inputField.setFocused(false);
                Minecraft.getMinecraft().ingameGUI.getChatGUI().resetScroll();
            }
        }
    }

    @SubscribeEvent
    public void onScroll(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (!config.utilitiesContainerChat) return;
        if (!(event.gui instanceof GuiContainer)) return;
        if (MixinUtils.inputField == null) return;
        if (!MixinUtils.inputField.isFocused()) return;
        int i = Mouse.getEventDWheel();
        if (i != 0) {
            if (i > 1) {
                i = 1;
            }

            if (i < -1) {
                i = -1;
            }

            if (!GuiScreen.isShiftKeyDown()) {
                i *= 7;
            }

            Minecraft.getMinecraft().ingameGUI.getChatGUI().scroll(i);
        }
    }

    private void getSentHistory(int msgPos) {
        int i = this.sentHistoryCursor + msgPos;
        int j = Minecraft.getMinecraft().ingameGUI.getChatGUI().getSentMessages().size();
        i = MathHelper.clamp_int(i, 0, j);

        if (i != this.sentHistoryCursor) {
            if (i == j) {
                this.sentHistoryCursor = j;
                MixinUtils.inputField.setText(this.historyBuffer);
            } else {
                if (this.sentHistoryCursor == j) {
                    this.historyBuffer = MixinUtils.inputField.getText();
                }
                MixinUtils.inputField.setText(Minecraft.getMinecraft().ingameGUI.getChatGUI().getSentMessages().get(i));
                this.sentHistoryCursor = i;
            }
        }
    }
}
