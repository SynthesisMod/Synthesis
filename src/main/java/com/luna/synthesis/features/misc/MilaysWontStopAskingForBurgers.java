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
    private final boolean ISERY = Minecraft.getMinecraft().getSession().getPlayerID().equalsIgnoreCase("b43d74579da4408ba9fb51239022cec9");

    @SubscribeEvent
    public void onChatRecievedEvent(ClientChatReceivedEvent e) {
        if (!ISMILAYS && !ISERY) {config.miscMoreBurgers = false; return;}
        if (config.miscMoreBurgers) {
            String[] bruh = e.message.getFormattedText().replaceAll("§r","").split(" ");
            String newMessage = "";
            e.setCanceled(true);
            for (String s : bruh) {
                s = s.substring(0, 2);
                newMessage += s + "burger§r ";
            }
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(newMessage));
        }
    }
    
}
