package com.luna.synthesis.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class ChatLib {

    public static void chat(Object message) {
        if (message instanceof String) {
            for (String s : ((String) message).split("\n")) {
                s = "&d[Synthesis]&r " + s;
                Minecraft.getMinecraft().thePlayer.addChatMessage(
                        new ChatComponentText(s.replaceAll("&", "§"))
                );
            }
        } else {
            message = ("&d[Synthesis]&r " + message).replaceAll("&", "§");
            Minecraft.getMinecraft().thePlayer.addChatMessage(
                    new ChatComponentText(message.toString()));
        }
    }
}
