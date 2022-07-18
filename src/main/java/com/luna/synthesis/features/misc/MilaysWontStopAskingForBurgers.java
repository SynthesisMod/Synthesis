package com.luna.synthesis.features.misc;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MilaysWontStopAskingForBurgers {

    private final Config config = Synthesis.getInstance().getConfig();

    private final boolean ISMILAYS = Minecraft.getMinecraft().getSession().getPlayerID().equalsIgnoreCase("1f20d60f1bc242dd82669a173a7af77c");

    @SubscribeEvent
    public void onChatRecievedEvent(ClientChatReceivedEvent e) {
        if (ISMILAYS || config.miscMoreBurgers) {
            String[] bruh = e.message.getFormattedText().split(" ");
            String newMessage = "";
            e.setCanceled(true);
            for (String s : bruh) {
                s = s.replaceAll("§r", "");
                if (s.contains("§")) s = s.substring(0, 2); else s = "";
                newMessage += s + "burger§r ";
            }
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(newMessage));
        }
    }
    
}
