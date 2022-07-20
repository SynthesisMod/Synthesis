package com.luna.synthesis.features.utilities;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

public class MasterModeSoulDetector {
    private final Config config = Synthesis.getInstance().getConfig();
    private int numSouls = 0;
    private ArrayList<Boolean> whichOnesAreMasterSouls = new ArrayList<Boolean>();
    private int r,g,b = 0;

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent e) {
        if (e.toolTip == null) return;

        ItemStack itemStack = e.itemStack;
        List<String> tooltip = e.toolTip;

        for (String s : tooltip) {
            if (s.contains("Absorbed Souls")) {
                findSouls(itemStack);
                int anchor = tooltip.indexOf(s);
                for (int h = 0; h < numSouls; h++) {
                    if (whichOnesAreMasterSouls.get(h)) {
                        tooltip.set(h+1+anchor, tooltip.get(h+1+anchor) + " §d(from Master Mode)");
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void guiRender(GuiScreenEvent.BackgroundDrawnEvent e) {
        if (!(Minecraft.getMinecraft().currentScreen instanceof GuiChest)) return;
        if ((!(StringUtils.stripControlCodes(
                ((ContainerChest)(Minecraft.getMinecraft().thePlayer.openContainer))
                .getLowerChestInventory().getDisplayName().getUnformattedText())
                .contains("You")
            )) &&
            (!(StringUtils.stripControlCodes(
                ((ContainerChest)(Minecraft.getMinecraft().thePlayer.openContainer))
                .getLowerChestInventory().getDisplayName().getUnformattedText())
                .contains("Auctions:")
            )) &&
            (!(StringUtils.stripControlCodes(
                ((ContainerChest)(Minecraft.getMinecraft().thePlayer.openContainer))
                .getLowerChestInventory().getDisplayName().getUnformattedText())
                .contains("Auction View")
            ))
        ) return;
        int numTrues = 0;
        List<Slot> slots = ((GuiChest)(e.gui)).inventorySlots.inventorySlots;
        for (Slot s : slots) {
            if (s.getStack() != null) {
                findSouls(s.getStack());
                for (boolean bool : whichOnesAreMasterSouls) {
                    if (bool) {
                        numTrues++;
                    }
                }
                if ((numTrues == whichOnesAreMasterSouls.size()) && (0 != whichOnesAreMasterSouls.size())) {
                    r = b = 0; g = 255;
                } else {
                    r = 0; g = b = 128;
                }
            }
            Color bgColor = new Color(r, g, b);
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

    private void findSouls(ItemStack is) {
        if (!(is.hasTagCompound())) return;
        if (is.getSubCompound("ExtraAttributes", false) == null || (!(is.getSubCompound("ExtraAttributes", false).hasKey("necromancer_souls")))) return;
        NBTTagList tl = ((NBTTagList)((is.getSubCompound("ExtraAttributes", false)).getTag("necromancer_souls")));
        if (tl.tagCount() == 0) return;
        if (!(whichOnesAreMasterSouls.isEmpty())) whichOnesAreMasterSouls.clear();
        numSouls = tl.tagCount();
        for (int i = 0; i < numSouls; i++) {
            whichOnesAreMasterSouls.add(tl.get(i).toString().contains("MASTER_"));
        }
    }
}
