package com.luna.synthesis.features.miscellaneous;

import gg.essential.api.EssentialAPI;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;

import net.minecraft.client.gui.GuiScreenBook;

import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class IgnoreHypixelBooks {

    private final Config config = Synthesis.getInstance().getConfig();
    
    @SubscribeEvent
    private void onBook(GuiOpenEvent e) {
        if (config.miscIgnoreHypixelBooks && EssentialAPI.getMinecraftUtil().isHypixel() && e.gui instanceof GuiScreenBook) {
            e.setCanceled(true);
        }
    }
}
