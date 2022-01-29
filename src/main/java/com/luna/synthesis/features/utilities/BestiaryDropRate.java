package com.luna.synthesis.features.utilities;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class BestiaryDropRate {

    private final Config config = Synthesis.getInstance().getConfig();

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        if (config.utilitiesDropChanceToDropRate == 0) return;
        if (config.utilitiesDropChanceToDropRate == 1 && !GuiScreen.isShiftKeyDown()) return;
        if (Minecraft.getMinecraft().currentScreen instanceof GuiChest) {
            ContainerChest chest = (ContainerChest) ((GuiChest) Minecraft.getMinecraft().currentScreen).inventorySlots;
            if (chest.getLowerChestInventory().getDisplayName().getUnformattedText().contains(" ➜ ")) {
                Iterator<String> it = event.toolTip.iterator();
                AtomicInteger i = new AtomicInteger(0);
                it.forEachRemaining(s -> {
                    String line = StringUtils.stripControlCodes(s);
                    if (line.endsWith("%)")) {
                        try {
                            double number = Double.parseDouble(line.split("\\(")[1].replace("%)", ""));
                            if (number < 100) {
                                event.toolTip.set(i.get(), s.replaceAll("\\(§a[\\d.]+%§8\\)",
                                        EnumChatFormatting.DARK_GRAY + "(" + EnumChatFormatting.GREEN + "1/" + Math.floor(10000 / number + 0.5) / 100 + EnumChatFormatting.DARK_GRAY + ")"));
                            }
                        } catch (NumberFormatException ignored) {}
                    }
                    i.getAndIncrement();
                });
            }
        }
    }
}
