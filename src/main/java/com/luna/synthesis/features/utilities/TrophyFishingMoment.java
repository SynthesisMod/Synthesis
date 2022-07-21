package com.luna.synthesis.features.utilities;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiChest;
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
    private String highestTrophyName = "";
    private List<String> itemLore;
    private List<Slot> slots;
    private int slotIndex = 0;
    
    @SubscribeEvent
    public void onGuiScreen(GuiScreenEvent.BackgroundDrawnEvent e) {
        if (!config.utilitiesTrophyFishingOverlay || Minecraft.getMinecraft().thePlayer == null || !(Minecraft.getMinecraft().currentScreen instanceof GuiChest) || !(StringUtils.stripControlCodes((((ContainerChest)((GuiChest)(Minecraft.getMinecraft().currentScreen)).inventorySlots).getLowerChestInventory().getDisplayName().getUnformattedText())).toLowerCase().contains("trophy fishing"))) return;

        slots = ((GuiChest)(Minecraft.getMinecraft().currentScreen)).inventorySlots.inventorySlots;

        for (Slot s : slots) {
            highestTrophyName = "";
            slotIndex = s.getSlotIndex();

            if (slotIndex == 45) break;
            if ((((slotIndex + 1) % 9) == 0) || (((slotIndex + 1) % 9) == 1) || (slotIndex < 8)) continue;

            if (s.getStack() != null && s.getStack().hasDisplayName() && !(s.getStack().getDisplayName().isEmpty()) && (!(s.getStack().getDisplayName().contains("§c§k")))) {
                itemLore = s.getStack().getTooltip(Minecraft.getMinecraft().thePlayer, false);
                for (String line : itemLore) {
                    if (line.contains("a\u2714")) {
                        highestTrophyName = StringUtils.stripControlCodes(line);
                        break;
                    }
                }

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
