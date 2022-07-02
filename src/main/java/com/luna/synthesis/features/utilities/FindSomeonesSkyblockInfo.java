package com.luna.synthesis.features.utilities;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import com.luna.synthesis.events.MessageSentEvent;
import com.luna.synthesis.utils.ChatLib;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.*;

//because writing it in SynthesisCommand.java won't be fun!
public class FindSomeonesSkyblockInfo {
    private final Config config = Synthesis.getInstance().getConfig();

    @SubscribeEvent
    public void onMessageSent(MessageSentEvent event) {
        String nameToCheck = "";
        if (event.message.endsWith(config.utilitiesShareText) || event.message.endsWith(config.utilitiesShareBootsText) || event.message.endsWith(config.utilitiesShareHelmetText) || event.message.endsWith(config.utilitiesShareLeggingsText) || event.message.endsWith(config.utilitiesShareChestplateText)) {return;}
        if (!config.utilitiesCheckWeight && event.message.startsWith("[weight")) {event.setCanceled(true); ChatLib.chat("You have the setting disabled. Please enable it and try again, but do so with extreme caution.");return;}
        if (event.message.startsWith("[stats")) {checkSomeonesStats(event.message); event.setCanceled(true); return;}
        if (event.message.startsWith("[weight")) {
            if (event.message.endsWith("[weight]")) {
                nameToCheck = Minecraft.getMinecraft().thePlayer.getName();
            } else {
                nameToCheck = (event.message.toLowerCase().replace("[weight ", "").replace("]", "")).replace(" ", "");
            }
            event.setCanceled(true);
        } else {
            return;
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
                    ChatLib.chat("Synthesis did not get an A-OK response from SkyCrypt. The response code Synthesis got instead was " + http.getResponseCode() + ". Aborting mission.");
                    return;
                }
                JsonParser parser = new JsonParser();
                JsonObject data = parser.parse(new String(IOUtils.toByteArray(instream), StandardCharsets.UTF_8)).getAsJsonObject();
                if (!data.has("profiles")) {
                    ChatLib.chat("Synthesis failed in getting information from SkyCrypt. Either their API is down or this player doesn't have any profiles at all. Aborting mission.");
                    return;
                }

                JsonObject profiles = data.get("profiles").getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> profileSet = profiles.entrySet();
                JsonElement currentSbProfileWeightData = null;
                String displayName = "";

                for (Map.Entry<String,JsonElement> me : profileSet)
                {
                    if (me.getValue().getAsJsonObject().get("current").getAsBoolean()) {
                        displayName = ((JsonObject)(me.getValue().getAsJsonObject().get("data"))).get("display_name").getAsString();
                        currentSbProfileWeightData = ((JsonObject)(me.getValue().getAsJsonObject().get("data"))).get("weight");
                    }
                }

                int overallSenitherWeight = ((int)((JsonObject)(currentSbProfileWeightData).getAsJsonObject().get("senither")).get("overall").getAsDouble());
                int overallLilyWeight = ((int)((JsonObject)(currentSbProfileWeightData).getAsJsonObject().get("lily")).get("total").getAsDouble());

                ChatLib.chat(displayName + "'s Senither weight to the nearest integer is " + overallSenitherWeight + " and their Lily weight to the nearest integer is " + overallLilyWeight + ".");
                
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

    public void checkSomeonesStats(String thatOneParameter) {
        String theNameToCheck = "";
        if (thatOneParameter.endsWith(config.utilitiesShareText) || thatOneParameter.endsWith(config.utilitiesShareBootsText) || thatOneParameter.endsWith(config.utilitiesShareHelmetText) || thatOneParameter.endsWith(config.utilitiesShareLeggingsText) || thatOneParameter.endsWith(config.utilitiesShareChestplateText) || thatOneParameter.startsWith("[weight")) {return;}
        if (thatOneParameter.startsWith("[stats")) {
            if (thatOneParameter.endsWith("[stats]")) {
                theNameToCheck = Minecraft.getMinecraft().thePlayer.getName();
            } else {
                theNameToCheck = (thatOneParameter.toLowerCase().replace("[stats ", "").replace("]", "")).replace(" ", "");
            }
        }
        try {
            URL url = new URL("https://sky.shiiyu.moe/api/v2/profile/" + theNameToCheck);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setDoOutput(true);
            http.setDoInput(true);
            http.setRequestProperty("User-Agent", "SynthesisMod-NONCANON");
            http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("Method", "GET");
            http.connect();
            try (InputStream instream = http.getInputStream()) {
                if (http.getResponseCode() != 200) {
                    ChatLib.chat("Synthesis did not get an A-OK response from SkyCrypt. The response code Synthesis got instead was " + http.getResponseCode() + ". Aborting mission.");
                    return;
                }
                JsonParser parser = new JsonParser();
                JsonObject data = parser.parse(new String(IOUtils.toByteArray(instream), StandardCharsets.UTF_8)).getAsJsonObject();
                if (!data.has("profiles")) {
                    ChatLib.chat("Synthesis failed in getting information from SkyCrypt. Either their API is down or this player doesn't have any profiles at all. Aborting mission.");
                    return;
                }

                JsonObject profiles = data.get("profiles").getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> profileSet = profiles.entrySet();
                int currentSbProfileSkillAverage = 0;
                int currentSbProfileSlayerXp = 0;
                String displayName = "";
                String cuteName = "";
                String firstJoinText = "";
                int avgSkillXpRank = 0;
                int totalSkillXp = 0;
                int collectedFairySouls = 0;
                int totalFairySouls = 0;
                int catacombsLevel = 0;

                for (Map.Entry<String,JsonElement> me : profileSet)
                {
                    if (me.getValue().getAsJsonObject().get("current").getAsBoolean()) {
                        displayName = ((JsonObject)(me.getValue().getAsJsonObject().get("data"))).get("display_name").getAsString();
                        cuteName = me.getValue().getAsJsonObject().get("cute_name").getAsString();
                        currentSbProfileSkillAverage = ((JsonObject)(me.getValue().getAsJsonObject().get("data"))).get("average_level").getAsInt();
                        currentSbProfileSlayerXp = ((JsonObject)(me.getValue().getAsJsonObject().get("data"))).get("slayer_xp").getAsInt();
                        firstJoinText = (((JsonObject)((JsonObject)(me.getValue().getAsJsonObject().get("data"))).get("first_join")).get("text")).getAsString();
                        avgSkillXpRank = ((JsonObject)(me.getValue().getAsJsonObject().get("data"))).get("average_level_rank").getAsInt();
                        totalSkillXp = ((JsonObject)(me.getValue().getAsJsonObject().get("data"))).get("total_skill_xp").getAsInt();
                        collectedFairySouls = (((JsonObject)((JsonObject)(me.getValue().getAsJsonObject().get("data"))).get("fairy_souls")).get("collected")).getAsInt();
                        totalFairySouls = (((JsonObject)((JsonObject)(me.getValue().getAsJsonObject().get("data"))).get("fairy_souls")).get("total")).getAsInt();
                        catacombsLevel = (((JsonObject)((JsonObject)((JsonObject)((JsonObject)(me.getValue().getAsJsonObject().get("data"))).get("dungeons")).get("catacombs")).get("level")).get("level")).getAsInt();
                    }
                }
                
                String skillAvg = (EnumChatFormatting.BLUE + "" + currentSbProfileSkillAverage + " skill average");
                String slayerXP = (EnumChatFormatting.RED + "" + currentSbProfileSlayerXp + " Slayer XP");
                firstJoinText = (EnumChatFormatting.GREEN + "First joined " + firstJoinText);
                cuteName = (EnumChatFormatting.GOLD + cuteName);
                String totalXp = (EnumChatFormatting.DARK_AQUA + "" + totalSkillXp + " total skill experience");
                String avgXpRank = (EnumChatFormatting.YELLOW + "Ranked #" + avgSkillXpRank + " in skill average");
                String fairySoulFraction = (EnumChatFormatting.LIGHT_PURPLE + "Collected " + collectedFairySouls + "/" + totalFairySouls);
                String catacombsLvlString = (EnumChatFormatting.GRAY + "Catacombs Level" + catacombsLevel);

                ChatLib.chat("Here are " + displayName + "'s stats on their " + cuteName + "§rprofile:\n §7- " + skillAvg + "\n §7- " + avgXpRank + "\n §7- " + totalXp + "\n §7- " + slayerXP + "\n §7- " + firstJoinText + "\n §7- " + fairySoulFraction + "\n §7- " + catacombsLvlString);
            } catch (Exception e) {
                ChatLib.chat("Synthesis ran into a problem checking content from SkyCrypt. See logs.");
                System.out.println("Synthesis ran into a problem checking content from SkyCrypt. See logs.");
                e.printStackTrace();
            }
        } catch (Exception e) {
            ChatLib.chat("Synthesis ran into a problem checking " + theNameToCheck + "'s weight. See logs.");
            System.out.println("Synthesis ran into a problem checking " + theNameToCheck + "'s weight. See logs.");
            e.printStackTrace();
        }
    }
}
