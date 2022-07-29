package com.luna.synthesis.features.utilities;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import com.luna.synthesis.utils.ChatLib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.lwjgl.opengl.GL11;

public class HexatorumUtils {

    private final Config config = Synthesis.getInstance().getConfig();
    private final Pattern fractionRegex = Pattern.compile("(?<num>\\d+)\\/(?<denom>\\d+)");
    private EntityPlayerSP mcgmtp = Minecraft.getMinecraft().thePlayer;
    private List<String> itemLore = new ArrayList<String>();
    private ArrayList<Float> floats = new ArrayList<Float>();
    private List<Slot> slots;
    private String menuName = "";
    private float r = 0F, g = 0F, floatForArrayList = 0F;
    
    @SubscribeEvent
    public void onHexUI(GuiScreenEvent.BackgroundDrawnEvent e) {
        if (!(config.utilitiesHexatorumOverlay)) return;
        if ((Minecraft.getMinecraft().thePlayer == null) || !(Minecraft.getMinecraft().currentScreen instanceof GuiChest)) return;
        menuName = StringUtils.stripControlCodes((((ContainerChest)((GuiChest)(Minecraft.getMinecraft().currentScreen)).inventorySlots).getLowerChestInventory().getDisplayName().getUnformattedText()));
        if (!(menuName.equals("The Hex"))) return;
        int aMagicNumber = (((new ScaledResolution(Minecraft.getMinecraft())).getScaledWidth() - 176) / 2);
        int anotherMagicNumber = (((new ScaledResolution(Minecraft.getMinecraft())).getScaledHeight() - 222) / 2);
        slots = ((GuiChest)(Minecraft.getMinecraft().currentScreen)).inventorySlots.inventorySlots;
        for (Slot sl : slots) {
            boolean colorThisSlot = false;
            try {if (!(itemLore.isEmpty())) itemLore.clear();} catch (Exception ex) {}
            if (!(floats.isEmpty())) floats.clear();
            floatForArrayList = 0F;
            if (sl.getStack() == null || !(sl.getStack().hasDisplayName())) continue;
            ItemStack is = sl.getStack();
            String dName = is.getDisplayName();
            if (dName.contains("aEnchantments")) {
                itemLore = is.getTooltip(mcgmtp, false);
                for (String s : itemLore) {
                    if (s.contains("7Enchantments") && !(s.contains("and"))) {
                        s = StringUtils.stripControlCodes(s);
                        findFracInStrForFloatArrLst(s, false);
                        colorThisSlot = true;
                    }
                }
            }
            if (dName.contains("aUltimate Enchantments")) {
                itemLore = is.getTooltip(mcgmtp, false);
                for (String s : itemLore) {
                    s = StringUtils.stripControlCodes(s);
                    if (fractionRegex.matcher(s).find()) {
                        findFracInStrForFloatArrLst(s, false);
                        colorThisSlot = true;
                    }
                }
            }
            if (dName.contains("aReforges")) {
                itemLore = is.getTooltip(mcgmtp, false);
                for (String s : itemLore) {
                    s = StringUtils.stripControlCodes(s);
                    if (s.contains("Reforge")) {
                        containsCheckmark(s, false);
                        colorThisSlot = true;
                    }
                }
            }
            if (dName.contains("aModifiers") || dName.contains("aBooks")) {
                colorThisSlot = true;
                itemLore = is.getTooltip(mcgmtp, false);
                for (String s : itemLore) {
                    s = StringUtils.stripControlCodes(s);
                    containsCheckmark(s, true);
                    findFracInStrForFloatArrLst(s, true);
                }
            }
            if (dName.contains("aItem Upgrades")) {
                colorThisSlot = true;
                itemLore = is.getTooltip(mcgmtp, false);
                for (String s : itemLore) {
                    s = StringUtils.stripControlCodes(s);
                    containsCheckmark(s, true);
                    if (s.contains("Upgrade Level")) {
                        s = s.replaceAll("  ","").replace("§7Upgrade Level", "");
                        float currStars = 0F, maxStars = 10F;
                        if (s.contains("➎")) currStars = 10F;
                        else if (s.contains("➍")) currStars = 9F;
                        else if (s.contains("➌")) currStars = 8F;
                        else if (s.contains("➋")) currStars = 7F;
                        else if (s.contains("➊")) currStars = 6F;
                        String[] sa = s.split("§");
                        if (sa.length < 1) return;
                        else for (String str : sa) if (str.endsWith("✪")) currStars++;
                        floats.add(currStars / maxStars);
                    }
                }
            }
            if (dName.contains("aGemstones")) {
                colorThisSlot = true;
                itemLore = is.getTooltip(mcgmtp, false);
                String[] sa = {};
                for (String s : itemLore) if (s.contains("7Gemstones")) sa = s.split(" ");
                if (sa.length < 1) return;
                float filledGemstones = 0, availableGemstones = 0F;
                for (String sTwo : sa) {
                    if (sTwo.contains("[") && sTwo.contains("]")) {
                        availableGemstones++;
                        if (!(sTwo.contains("8"))) {
                            filledGemstones++;
                        }
                    }
                }
                compareFloats(filledGemstones, availableGemstones);
            }
            if (!colorThisSlot) continue;
            if (!(floats.isEmpty())) {
                for (float f : floats) {
                    if (f >= 1F) f = 1F;
                    floatForArrayList += f;
                }
                compareFloats(floatForArrayList, ((float)(floats.size())));
            }
            Color bgColor = new Color(((int)(r)), ((int)(g)), (0));
            if (config.utilitiesHexatorumDebug) {ChatLib.chat(bgColor.toString() + " was selected for item named " + dName);}
            GL11.glTranslated(0, 0, 1);
            Gui.drawRect(((aMagicNumber) + sl.xDisplayPosition), ((slots.size() != 90) ? (((anotherMagicNumber) + sl.yDisplayPosition) + ((6 - (slots.size() - 36) / 9) * 9)) : ((anotherMagicNumber) + sl.yDisplayPosition)), (((aMagicNumber) + sl.xDisplayPosition) + 16), (((slots.size() != 90) ? (((anotherMagicNumber) + sl.yDisplayPosition) + ((6 - (slots.size() - 36) / 9) * 9)) : ((anotherMagicNumber) + sl.yDisplayPosition)) + 16), bgColor.getRGB());
            GL11.glTranslated(0, 0, -1);
        }
    }

    private void containsCheckmark(String s, boolean isArrayList) {
        if (s.contains("✔")) {
            if (isArrayList){floats.add(1F);}
            else {g = 255F; r = 0F;}
        } else if (s.contains("✖")) {
            if (isArrayList){floats.add(0F);}
            else {r = 255F; g = 0F;}
        }
    }

    private void findFracInStrForFloatArrLst(String s, boolean isArrayList) {
        if (!(fractionRegex.matcher(s).find())) return;
        String[] sa = s.split(" ");
        s = sa[sa.length - 1];
        float numerator = Float.parseFloat(s.substring(0, s.indexOf("/") + 1).replace("/", ""));
        float denominator = Float.parseFloat(s.substring(s.indexOf("/"), s.length()).replace("/", ""));
        if (isArrayList) {
            if (denominator <= numerator) floats.add(1F);
            else floats.add((numerator / denominator));
        } else compareFloats(numerator, denominator);
    }

    private void compareFloats(float f1, float f2) {
        if (f1 >= f2) {
            f1 = f2;
            g = 255F;
            r = 0F;
        } else {
            float tempFloat = (f1 / f2);
            if (tempFloat == 0.5F) {
                r = g = 255F;
            } else {
                if (tempFloat > 0.5F) {
                    r = 255 - (tempFloat * 255F);
                    g = 255F;
                } else {
                    r = 255F;
                    g = (tempFloat * 255F);
                }
            }
        }
    }
}
