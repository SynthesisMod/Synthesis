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
                        preventThatClick(e, "resetting", "HOTM tree");
                }
    
                if (config.utilitiesPreventProfileDeletion &&
                    slot.getStack().getDisplayName().toLowerCase()
                    .contains("delete profile")) {
                        preventThatClick(e, "deleting", "Skyblock profile");
                }

                if (config.utilitiesPreventVotingBarry &&
                    slot.getStack().getDisplayName().toLowerCase()
                    .endsWith("barry") && (!(slot.getStack().getDisplayName().toLowerCase()
                    .contains("mayor")))) {
                        preventThatClick(e, "voting Barry as", "Skyblock mayor");
                }

                if (config.utilitiesPreventVotingDiaz &&
                    slot.getStack().getDisplayName().toLowerCase()
                    .endsWith("diaz") && (!(slot.getStack().getDisplayName().toLowerCase()
                    .contains("mayor")))) {
                        preventThatClick(e, "voting Diaz as", "Skyblock mayor");
                }

                if (slot.getStack().getDisplayName().toLowerCase()
                    .contains("confirm")) {
                        for (String string : slot.getStack().getTooltip(Minecraft.getMinecraft().thePlayer, false)) {
                            if (string.contains("portal") && config.utilitiesPreventPortalDestruction) {
                                preventThatClick(e, "destroying one of", "\"Warp to\" portals");
                                break;
                            }
                        }
                }
            }
        }
    }

    private void preventThatClick(GuiScreenEvent.MouseInputEvent.Pre e, String action, String type) {
        e.setCanceled(true);
        Minecraft.getMinecraft().thePlayer.playSound("note.bass", 1, ((float)(0.5)));
        ChatLib.chat("Synthesis has prevented you from " + action + " your " + type + ". Check your configs if you actually wanted to do this.");
    }
}
