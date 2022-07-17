package com.luna.synthesis.features.utilities;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.util.StringUtils;

import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.Color;
import java.util.List;

import org.lwjgl.opengl.GL11;

public class TrophyFishingMoment {

    private final Config config = Synthesis.getInstance().getConfig();

    private float r, g, b = 0F;
    private String menuName = "";
    private String highestTrophyName = "";
    private boolean isTrophy = false;
    private List<String> itemLore;
    private List<Slot> slots;
    private String trophyFishName = "";
    private int slotIndex = 0;
    
    @SubscribeEvent
    public void onGuiScreen(GuiScreenEvent.BackgroundDrawnEvent e) {
        if (!config.utilitiesTrophyFishingOverlay) return;
        if (Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().currentScreen instanceof GuiChest) {
            menuName = StringUtils.stripControlCodes((((ContainerChest)((GuiChest)(Minecraft.getMinecraft().currentScreen)).inventorySlots).getLowerChestInventory().getDisplayName().getUnformattedText()));
            if (menuName.toLowerCase().contains("trophy fishing")) {
                slots = ((GuiChest)(Minecraft.getMinecraft().currentScreen)).inventorySlots.inventorySlots;

                for (Slot s : slots) {

                    highestTrophyName = "";
                    isTrophy = false;
                    trophyFishName = "";

                    if (s.getStack() != null && s.getStack().hasDisplayName() && (!(s.getStack().getDisplayName().contains("§k")))) {
                        trophyFishName = StringUtils.stripControlCodes(s.getStack().getDisplayName());
                        itemLore = s.getStack().getTooltip(Minecraft.getMinecraft().thePlayer, false);
                        slotIndex = s.getSlotIndex();

                        if (s.getStack().getItem() == Items.skull && slotIndex < 45) {
                            for (String line : itemLore) {
                                if (line.contains("How to catch")) {
                                    isTrophy = true;
                                }
                                if (line.contains("a\u2714")) {
                                    highestTrophyName = StringUtils.stripControlCodes(line);
                                    break;
                                }
                            }
                        }
                        if (!isTrophy) {continue;}
                        if (highestTrophyName.contains("Diamond")) {
                            r = 85; b = g = 255;
                        } else if (highestTrophyName.contains("Gold")) {
                            r = 255; b = 0; g = 170;
                        } else if (highestTrophyName.contains("Silver")) {
                            r = b = g = 170;
                        } else if (highestTrophyName.contains("Bronze")) {
                            r = b = g = 85;
                        }
                        Color bgColor = new Color(((int)(r)), ((int)(g)), ((int)(b)));
                        System.out.println("[Synthesis — DEBUG] Inside the menu named " + menuName + ", the color " + bgColor + " was selected for the trophy fish named " + trophyFishName + " because the line with its highest tier was recorded as " + highestTrophyName + " at chest index " + s.getSlotIndex() + " and isTrophy is " + isTrophy);
                        GL11.glTranslated(0, 0, 1);
                        Gui.drawRect(
                            ((((new ScaledResolution(Minecraft.getMinecraft())).getScaledWidth() - 176) / 2) + s.xDisplayPosition),
                            ((slots.size() != 90) ?
                                (((((new ScaledResolution(Minecraft.getMinecraft())).getScaledHeight() - 222) / 2) + s.yDisplayPosition) +
                                    ((6 - (slots.size() - 36) / 9) * 9)) :
                                ((((new ScaledResolution(Minecraft.getMinecraft())).getScaledHeight() - 222) / 2) + s.yDisplayPosition)),
                            (((((new ScaledResolution(Minecraft.getMinecraft())).getScaledWidth() - 176) / 2) + s.xDisplayPosition) + 16),
                            (((slots.size() != 90) ?
                                (((((new ScaledResolution(Minecraft.getMinecraft())).getScaledHeight() - 222) / 2) + s.yDisplayPosition) +
                                    ((6 - (slots.size() - 36) / 9) * 9)) :
                                ((((new ScaledResolution(Minecraft.getMinecraft())).getScaledHeight() - 222) / 2) + s.yDisplayPosition)) + 16),
                            bgColor.getRGB());
                        GL11.glTranslated(0, 0, -1);
                    }
                }
            }
        }
    }
}
