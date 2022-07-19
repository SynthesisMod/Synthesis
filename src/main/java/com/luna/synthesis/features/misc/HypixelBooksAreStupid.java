package com.luna.synthesis.features.misc;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;

import gg.essential.api.EssentialAPI;
import net.minecraft.client.gui.GuiScreenBook;

import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HypixelBooksAreStupid {

    private final Config config = Synthesis.getInstance().getConfig();
    
    @SubscribeEvent
    public void onBook(GuiOpenEvent e) {
        if (!config.miscIgnoreHypixelBooks) {return;}
        if ((e.gui instanceof GuiScreenBook) && (EssentialAPI.getMinecraftUtil().isHypixel())) {
            e.setCanceled(true);
            if (config.miscIgnoreHypixelBooksWarning) {
                EssentialAPI.getNotifications().push("§dSynthesis", ("Synthesis detected a book GUI and ignored it. Check the server you're on and review your configs if ignoring the book GUI was a mistake or if this notification was a mistake."), 5);
            }
        }
    }
}
