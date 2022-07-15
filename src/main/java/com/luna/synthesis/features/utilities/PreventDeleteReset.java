package com.luna.synthesis.features.utilities;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import com.luna.synthesis.utils.ChatLib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.lwjgl.input.Mouse;

public class PreventDeleteReset {
    private final Config config = Synthesis.getInstance().getConfig();

    @SubscribeEvent
    public void onMouseClick(GuiScreenEvent.MouseInputEvent.Pre e) {
        if (!Mouse.getEventButtonState()) return;

        if (e.gui instanceof GuiContainer) {
            GuiContainer guiContainer = (GuiContainer) e.gui;
            if ((Mouse.getEventButton() > -1)) {
                Slot slot = guiContainer.getSlotUnderMouse();

                if (slot == null) return;
                if (!slot.getHasStack()) return;
    
                if (config.utilitiesPreventHOTMReset &&
                    (slot.getStack().getDisplayName().toLowerCase()
                    .contains("reset heart of the mountain"))) {
                        e.setCanceled(true);
                        preventThatClick("resetting", "HOTM tree");
                }
    
                if (config.utilitiesPreventProfileDeletion &&
                    slot.getStack().getDisplayName().toLowerCase()
                    .contains("delete profile")) {
                        e.setCanceled(true);
                        preventThatClick("deleting", "Skyblock profile");
                }
            }
        }
    }

    private void preventThatClick(String action, String type) {
        Minecraft.getMinecraft().thePlayer.playSound("note.bass", 1, ((float)(0.5)));
        ChatLib.chat("Synthesis has prevented you from " + action + " your " + type + ". Check your configs if you actually wanted to do this.");
    }
}
