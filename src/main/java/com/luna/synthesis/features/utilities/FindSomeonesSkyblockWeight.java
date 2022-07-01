package com.luna.synthesis.features.utilities;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import com.luna.synthesis.events.MessageSentEvent;
import com.luna.synthesis.utils.ChatLib;

import net.minecraft.client.Minecraft;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.*;

//because writing it in SynthesisCommand.java won't be fun!
public class FindSomeonesSkyblockWeight {
    private final Config config = Synthesis.getInstance().getConfig();

    @SubscribeEvent
    public void onMessageSent(MessageSentEvent event) {
        String nameToCheck = "";
        if ((event.message.contains("/syn weight") || event.message.contains("/synth weight") || event.message.contains("/synthesis weight"))) {
            if (event.message.endsWith(" weight")) {
                nameToCheck = Minecraft.getMinecraft().thePlayer.getName().toLowerCase();
            } else {
                String[] split = (((event.message.toLowerCase().replace("/syn weight ", "")).replace("/synth weight ", "")).replace("/synthesis weight ", "")).split(" ");
                nameToCheck = split[0];
            }
        }
        try {
            URL url = new URL("https://sky.shiiyu.moe/api/v2/profile/" + nameToCheck);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setDoOutput(true);
            http.setDoInput(true);
            http.setRequestProperty("User-Agent", "SynthesisMod-NONCANON");
            http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("Method", "GET");
            http.connect();
            try (InputStream instream = http.getInputStream()) {
                if (http.getResponseCode() != 200) {
                    ChatLib.chat("Synthesis did not get an A-OK response from SkyCrypt. Aborting mission.");
                    return;
                }
                JsonParser parser = new JsonParser();
                JsonObject sbProfileAsJson = parser.parse(new String(IOUtils.toByteArray(instream), StandardCharsets.UTF_8)).getAsJsonObject();
                if (!sbProfileAsJson.has("profiles")) {
                    ChatLib.chat("Synthesis failed in getting information from SkyCrypt. Aborting mission.");
                    return;
                }

                JsonObject currentSbProfile = sbProfileAsJson.getAsJsonArray().get(0).getAsJsonObject();
                JsonObject weightData = ((JsonObject)((JsonObject)(currentSbProfile.get("data"))).get("weight"));

                int overallSenitherWeight = ((int)((JsonObject)(weightData.get("senither"))).get("overall").getAsDouble());
                int overallLilyWeight = ((int)((JsonObject)(weightData.get("lily"))).get("total").getAsDouble());

                ChatLib.chat(nameToCheck + "'s Senither weight to the nearest integer is " + overallSenitherWeight + " and their Lily weight to the nearest integer is " + overallLilyWeight + ".");
                
            } catch (Exception e) {
                ChatLib.chat("Synthesis ran into a problem checking content from SkyCrypt. See logs.");
                System.out.println("Synthesis ran into a problem checking content from SkyCrypt. See logs.");
                e.printStackTrace();
            }
        } catch (Exception e) {
            ChatLib.chat("Synthesis ran into a problem checking " + nameToCheck + "'s weight. See logs.");
            System.out.println("Synthesis ran into a problem checking " + nameToCheck + "'s weight. See logs.");
            e.printStackTrace();
        }
    }
}
