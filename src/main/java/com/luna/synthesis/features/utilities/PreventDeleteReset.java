package com.luna.synthesis.features.utilities;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import com.luna.synthesis.utils.ChatLib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.ContainerChest;

import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

import org.lwjgl.input.Mouse;

public class PreventDeleteReset {
    private final Config config = Synthesis.getInstance().getConfig();

    @SubscribeEvent
    public void onMouseClick(GuiScreenEvent.MouseInputEvent.Pre e) {
        boolean isBarry = false, isDiaz = false, isDante = false, isSpecialSus = false;
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

                if (slot.getStack().getDisplayName().toLowerCase()
                    .contains("confirm")) {
                        for (String string : slot.getStack().getTooltip(Minecraft.getMinecraft().thePlayer, false)) {
                            if (string.contains("portal") && config.utilitiesPreventPortalDestruction) {
                                preventThatClick(e, "destroying one of", "island's \"Warp to\" portals");
                                break;
                            }
                        }
                }

                if (config.utilitiesPreventVotingBarry &&
                    slot.getStack().getDisplayName().toLowerCase()
                    .endsWith("barry") && (!(slot.getStack().getDisplayName().toLowerCase()
                    .contains("mayor")))) {
                        preventThatClick(e, "voting Barry as", "Skyblock mayor");
                        isBarry = true;
                }

                if (config.utilitiesPreventVotingDiaz &&
                    slot.getStack().getDisplayName().toLowerCase()
                    .endsWith("diaz") && (!(slot.getStack().getDisplayName().toLowerCase()
                    .contains("mayor")))) {
                        preventThatClick(e, "voting Diaz as", "Skyblock mayor");
                        isDiaz = true;
                }

                if ((config.utilitiesPreventVotingDante || config.utilitiesPreventVotingSusSpecialMayors) &&
                    slot.getStack().getDisplayName().toLowerCase()
                    .endsWith("dante") && (!(slot.getStack().getDisplayName().toLowerCase()
                    .contains("mayor")))) {
                        preventThatClick(e, "voting Dante as", "Skyblock mayor");
                        isDante = true;
                }

                if ((config.utilitiesPreventVotingSusSpecialMayors) &&
                    slot.getStack().getDisplayName().toLowerCase()
                    .contains("\u00a7d")) {
                        if ((!(slot.getStack().getDisplayName().toLowerCase()
                            .contains("mayor"))) && (!(slot.getStack().getDisplayName().toLowerCase()
                            .contains("derpy"))) && (!(slot.getStack().getDisplayName().toLowerCase()
                            .contains("jerry"))) && (!(slot.getStack().getDisplayName().toLowerCase()
                            .contains("aatrox"))) && (!(slot.getStack().getDisplayName().toLowerCase()
                            .contains("barry"))) && (!(slot.getStack().getDisplayName().toLowerCase()
                            .contains("cole"))) && (!(slot.getStack().getDisplayName().toLowerCase()
                            .contains("diana"))) && (!(slot.getStack().getDisplayName().toLowerCase()
                            .contains("diaz"))) && (!(slot.getStack().getDisplayName().toLowerCase()
                            .contains("foxy"))) && (!(slot.getStack().getDisplayName().toLowerCase()
                            .contains("marina"))) && (!(slot.getStack().getDisplayName().toLowerCase()
                            .contains("paul"))) && (!(slot.getStack().getDisplayName().toLowerCase()
                            .contains("scorpius")))) {
                                preventThatClick(e, "voting an unknown special mayor as", "Skyblock mayor");
                                isSpecialSus = true;
                        }
                }

                if (StringUtils.stripControlCodes(
                    ((ContainerChest)(Minecraft.getMinecraft().thePlayer.openContainer))
                    .getLowerChestInventory().getDisplayName().getUnformattedText())
                    .contains("Election, Year ") &&
                        config.utilitiesPreventVotingXPerkMayors != 1) {
                    String colorCode = slot.getStack().getDisplayName().substring(0, 2);
                    List<String> itemLore = slot.getStack().getTooltip(Minecraft.getMinecraft().thePlayer, false);
                    int numPerks = 0;
                    for (String s : itemLore) {
                        s = s.replaceAll("§5§o", "");
                        if (s.startsWith(colorCode) &&
                            !(s.contains("You voted for this candidate!")) &&
                            !(s.contains("Leading in votes!")) &&
                            !(s.contains("Click to vote for "))) {
                                numPerks++;
                        }
                    }
                    if (isBarry || isDiaz || isDante || isSpecialSus) return;
                    if (numPerks < config.utilitiesPreventVotingXPerkMayors) {
                        preventThatClick(e, "voting for " + (StringUtils.stripControlCodes(slot.getStack().getDisplayName())) + " as", "Skyblock mayor because they only have " + numPerks + " perk" + ((numPerks != 1) ? "s" : ""));
                    }
                }
            }
        }
    }

    private void preventThatClick(GuiScreenEvent.MouseInputEvent.Pre e, String action, String type) {
        e.setCanceled(true);
        Minecraft.getMinecraft().thePlayer.playSound("note.bass", 1, ((float)(0.5)));
        ChatLib.chat(
            "Synthesis has prevented you from " + action + " your " + type + ". " +
            ((action.equals("voting Dante as")) ?
                "Why would you vote for that dictator again?" :
                "Check your configs if you actually wanted to do this."
            )
        );
    }
}
