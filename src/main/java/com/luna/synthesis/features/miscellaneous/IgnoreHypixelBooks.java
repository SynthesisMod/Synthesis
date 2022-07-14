package com.luna.synthesis.features.miscellaneous;

import gg.essential.api.EssentialAPI;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.util.StringUtils;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class IgnoreHypixelBooks {

    private final Config config = Synthesis.getInstance().getConfig();
    private boolean isLobby = false;
    private int ticks = 0;

    @SubscribeEvent
    private void onTick(TickEvent.ClientTickEvent e) {
        if (!config.miscIgnoreHypixelBooks) {return;}
        if (e.phase == TickEvent.Phase.START && Minecraft.getMinecraft().thePlayer != null) {
            if (++ticks == 20) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/locraw");
            }
        }
    }

    @SubscribeEvent
    private void onMessage(ClientChatReceivedEvent e) {
        if (!config.miscIgnoreHypixelBooks) {return;}
        try {
            JsonObject locraw = new JsonParser().parse(e.message.getUnformattedText()).getAsJsonObject();
            if (locraw.has("server")) {
                if (locraw.get("server").getAsString().contains("lobby")) isLobby = true;
                else isLobby = false;
            }
        } catch (Exception ignored) {}
    }
    
    @SubscribeEvent
    private void onBook(GuiOpenEvent e) {
        if (!config.miscIgnoreHypixelBooks) {return;}
        if ((isLobby) && (EssentialAPI.getMinecraftUtil().isHypixel()) && (e.gui instanceof GuiScreenBook) && !(StringUtils.stripControlCodes(Minecraft.getMinecraft().theWorld.getScoreboard().getObjectiveInDisplaySlot(1).getDisplayName()).contains("SKYBLOCK"))) {
            e.setCanceled(true);
        }
    }
}
