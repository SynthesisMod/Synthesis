package com.luna.synthesis.features.utilities;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import com.luna.synthesis.events.MessageSentEvent;
import com.luna.synthesis.utils.ChatLib;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.*;


/**
 * <pre>
 * FindSomeonesSkyblockInfo
 * A Java class by Erymanthus | RayDeeUx for Synthesis-NONCANON.
 * 
 * Suggestion #33 by OutlawMC#0001
 * Add the commands "/lilyweight" and "/senweight"
 * to get ingame weight calcs (basically same as
 * sbe has but add options to view both)
 * 
 * Suggestion #83 by Shy#9999
 * Show a short version of a players stats in trade menu's
 * so you can determine you (sic) SUSSY they are
 * 
 * </pre>
 **/
//to #33: SIKE! IT'S IN `[share]` FORMAT—writing it in SynthesisCommand.java won't be fun!
//to #83: SIKE! it's shown in chat instead because i do NOT want to mess with the custom NEU trade menu.
//event.setCanceled(true) abused because NO ONE should get muted over using this and going to appeals = going to limbo
public class FindSomeonesSkyblockInfo {
    private final Config config = Synthesis.getInstance().getConfig();
    private final String skyCryptURL = ("https://sky.shiiyu.moe/api/v2/profile/");

    @SubscribeEvent
    public void onMessageSent(MessageSentEvent event) {
        String nameToCheck = "";
        if (event.message.endsWith(config.utilitiesShareText) || event.message.endsWith(config.utilitiesShareBootsText) || event.message.endsWith(config.utilitiesShareHelmetText) || event.message.endsWith(config.utilitiesShareLeggingsText) || event.message.endsWith(config.utilitiesShareChestplateText)) {return;}
        if (!config.utilitiesCheckWeight && event.message.startsWith("[weight")) {event.setCanceled(true); ChatLib.chat("You have the setting disabled. Please enable it and try again, but do so with extreme caution.");return;}
        if (event.message.startsWith("[stats") && event.message.endsWith("]")) {checkSomeonesStats(event.message); event.setCanceled(true); return;}
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
            URL url = new URL(skyCryptURL + nameToCheck);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setDoOutput(true);
            http.setDoInput(true);
            http.setRequestProperty("User-Agent", "SynthesisMod-NONCANON");
            http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("Method", "GET");
            http.connect();
            try (InputStream instream = http.getInputStream()) {
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

                ChatLib.chat(displayName + "'s weight info is as follows: ");
                ChatLib.chat("Overall Lily Weight: " + overallLilyWeight + "\nOverall Senither Weight: " + overallSenitherWeight);
                
            } catch (Exception e) {
                ChatLib.chat("Synthesis did not get an A-OK response from SkyCrypt. The response code Synthesis got instead was " + http.getResponseCode() + ". Aborting mission.");
                System.out.println("Synthesis did not get an A-OK response from SkyCrypt. The response code Synthesis got instead was " + http.getResponseCode() + ". Aborting mission, see below.");
                return;
            }
        } catch (Exception e) {
            ChatLib.chat("Synthesis ran into a problem checking " + nameToCheck + "'s weight. See logs.");
            System.out.println("Synthesis ran into a problem checking " + nameToCheck + "'s weight. See below.");
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onChatEvent(ClientChatReceivedEvent e) {
        String message = e.message.getUnformattedText();
        if (!(message.startsWith("You have sent a trade request to "))) {return;}
        String theirName = message.replace("You have sent a trade request to ", "").replace(".", "");
        if (!config.utilitiesCheckStats) {ChatLib.chat("You have currently disabled player analyses at this time. Run away as far as possible from " + theirName + " to cancel the trade and re-enable the setting in the config if you want to get an analysis on them."); /* Minecraft.getMinecraft().thePlayer.sendChatMessage("wait hold on a sec need to tweak my mod configs"); */ return;}
        ChatLib.chat("Running analysis on " + theirName + "...");
        checkSomeonesStats("[stats " + theirName + "]");
    }

    public void checkSomeonesStats(String thatOneParameter) {
        String theNameToCheck = "";
        if (thatOneParameter.endsWith(config.utilitiesShareText) || thatOneParameter.endsWith(config.utilitiesShareBootsText) || thatOneParameter.endsWith(config.utilitiesShareHelmetText) || thatOneParameter.endsWith(config.utilitiesShareLeggingsText) || thatOneParameter.endsWith(config.utilitiesShareChestplateText) || thatOneParameter.startsWith("[weight")) {return;}
        if (!config.utilitiesCheckStats) {ChatLib.chat("You have currently disabled player analyses at this time. Re-enable the setting in the config if you want to get an analysis on them."); return;}
        if (thatOneParameter.startsWith("[stats") && thatOneParameter.endsWith("]")) {
            if (thatOneParameter.endsWith("[stats]")) {
                theNameToCheck = Minecraft.getMinecraft().thePlayer.getName();
            } else {
                theNameToCheck = (thatOneParameter.toLowerCase().replace("[stats ", "").replace("]", "")).replace(" ", "");
            }
        } else {
            ChatLib.chat("Are you sure you have a coherent username whose stats you want to see?");
            return;
        }
        try {
            URL url = new URL(skyCryptURL + theNameToCheck);
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
                String uuidFromJson = "";
                String cuteName = "";
                String profileId = "";
                String firstJoinText = "";
                long totalSkillXp = 0;
                int collectedFairySouls = 0;
                int totalFairySouls = 0;
                int catacombsLevel = 0;
                int iceEssence = 0;
                int witherEssence = 0;
                int spiderEssence = 0;
                int undeadEssence = 0;
                int diamondEssence = 0;
                int dragonEssence = 0;
                int goldEssence = 0;
                int crimsonEssence = 0;
                int totalEssence = 0;
                JsonElement currentSbProfileWeightData = null;
                JsonObject currentSbProfileData = null;
                String rankPrefix = "";
                String gameMode = "";
                boolean isInGuild = false;
                String guildName = "";
                String guildTag = "";
                int guildMembers = 0;
                int guildLevel = 0;
                String guildRank = "";
                String guildMaster = "";
                String guildInfo = "";
                int purse = 0;

                for (Map.Entry<String,JsonElement> me : profileSet)
                {
                    if (me.getValue().getAsJsonObject().get("current").getAsBoolean()) {
                        currentSbProfileData = ((me.getValue().getAsJsonObject().get("data").getAsJsonObject()));
                    }
                }

                displayName = currentSbProfileData.get("display_name").getAsString();
                uuidFromJson = currentSbProfileData.get("uuid").getAsString();
                rankPrefix = currentSbProfileData.get("rank_prefix").getAsString();
                cuteName = currentSbProfileData.get("profile").getAsJsonObject().get("cute_name").getAsString();
                profileId = currentSbProfileData.get("profile").getAsJsonObject().get("profile_id").getAsString();
                try {
                    gameMode = currentSbProfileData.get("profile").getAsJsonObject().get("game_mode").getAsString();
                } catch (Exception e) {
                    gameMode = ("Classic");
                }
                currentSbProfileSkillAverage = currentSbProfileData.get("average_level").getAsInt();
                currentSbProfileSlayerXp = currentSbProfileData.get("slayer_xp").getAsInt();
                firstJoinText = currentSbProfileData.get("first_join").getAsJsonObject().get("text").getAsString();
                totalSkillXp = currentSbProfileData.get("total_skill_xp").getAsLong();
                collectedFairySouls = currentSbProfileData.get("fairy_souls").getAsJsonObject().get("collected").getAsInt();
                totalFairySouls = currentSbProfileData.get("fairy_souls").getAsJsonObject().get("total").getAsInt();
                try {
                    catacombsLevel = currentSbProfileData.get("dungeons").getAsJsonObject().get("catacombs").getAsJsonObject().get("level").getAsJsonObject().get("level").getAsInt();
                } catch (Exception e) {
                    ChatLib.chat("It looks like " + displayName + " has not explored the Catacombs yet!");
                    System.out.println("It looks like " + displayName + " has not explored the Catacombs yet! See below.");
                    e.printStackTrace();
                }
                JsonObject essenceData = currentSbProfileData.get("essence").getAsJsonObject();
                iceEssence = essenceData.get("ice").getAsInt();
                witherEssence = essenceData.get("wither").getAsInt();
                spiderEssence = essenceData.get("spider").getAsInt();
                undeadEssence = essenceData.get("undead").getAsInt();
                diamondEssence = essenceData.get("diamond").getAsInt();
                dragonEssence = essenceData.get("dragon").getAsInt();
                goldEssence = essenceData.get("gold").getAsInt();
                crimsonEssence = essenceData.get("crimson").getAsInt();
                totalEssence = iceEssence + witherEssence + spiderEssence + undeadEssence + diamondEssence + dragonEssence + goldEssence + crimsonEssence;
                purse = currentSbProfileData.get("purse").getAsInt();//purse
                currentSbProfileWeightData = currentSbProfileData.get("weight");
                double overallSenitherWeight = (((JsonObject)(currentSbProfileWeightData).getAsJsonObject().get("senither")).get("overall").getAsDouble());
                double overallLilyWeight = (((JsonObject)(currentSbProfileWeightData).getAsJsonObject().get("lily")).get("total").getAsDouble());
                try {
                    JsonObject guildData = currentSbProfileData.get("guild").getAsJsonObject();
                    isInGuild = true;
                    guildName = guildData.get("name").getAsString();
                    guildTag = "[" + guildData.get("tag").getAsString() + "]";
                    guildLevel = guildData.get("level").getAsInt();
                    guildMembers = guildData.get("members").getAsInt();
                    guildRank = guildData.get("rank").getAsString();
                    guildMaster = guildData.get("gmUser").getAsJsonObject().get("display_name").getAsString();
                    if (guildRank.toLowerCase().contains("master")) {
                        guildRank = "§2as §6the guildmaster";
                        guildMaster = "";
                    } else {
                        guildRank = "§2with guild rank §a\"" + guildRank + "\"";
                        guildMaster = "Their guildmaster is §a" + guildMaster + ".";
                    }
                } catch (Exception e) {
                    isInGuild = false;
                }

                String linePrefix = ("\n §8- ");
                String skillAvg = ("§9" + currentSbProfileSkillAverage + " Skill Average");
                firstJoinText = ("§aFirst joined " + firstJoinText);
                cuteName = ("§6" + cuteName);
                uuidFromJson = ("§7UUID: §2" + uuidFromJson);
                String totalAndSlayerXp = ("§3" + totalSkillXp + " total Skill XP§r | §c" + currentSbProfileSlayerXp + " Slayer XP");
                String fairySoulFraction = ("§dCollected " + collectedFairySouls + "/" + totalFairySouls + " fairy souls");
                String catacombsLvlString = ("§4Catacombs Level " + catacombsLevel);
                String essenceString = ("§5Total essence: §r" + totalEssence + " Essence (§9" + iceEssence + " Ice§r, §7" + witherEssence + " Wither§r, §4" + spiderEssence + " Spider§r, §5" + undeadEssence + " Undead§r, §b" + diamondEssence + " Diamond§r, §e" + dragonEssence + " Dragon§r, §6" + goldEssence + " Gold§r, §c" + crimsonEssence + " Crimson§r)");
                String weightString = ("§eOverall Lily Weight: " + ((int)(overallLilyWeight)) + " | Overall Senither Weight: " + ((int)(overallSenitherWeight)));
                String purseString = ("§6Purse: " + purse + " coins");
                profileId = ("§7Profile ID: §2" + profileId);
                gameMode = ("§6" + Character.toUpperCase(gameMode.charAt(0)) + gameMode.substring(1));
                if (rankPrefix.equals("")) {
                    rankPrefix = ("§7");
                } else {
                    try {
                        // things to remove!
                        // <div class=\"rank-tag nice-colors-dark\">
                        // <div class=\"rank-name\" style=\"background-color: var(--
                        // )\">
                        // </div>
                        // <div class=\"rank-plus\" style=\"background-color: var(--
                        // \n
                        // " "
                        rankPrefix = rankPrefix.replace("<div class=\"rank-tag nice-colors-dark\">", "")
                        .replace("<div class=\"rank-name\" style=\"background-color: var(--", "")
                        .replace(")\">", "").replace("</div>", "")
                        .replace("<div class=\"rank-plus\" style=\"background-color: var(--", "")
                        .replaceAll("\n", "")
                        .replaceAll(" ", "");
                        rankPrefix = rankPrefix.substring(0, 2) + "[" + rankPrefix + rankPrefix.substring(0, 2) + "] ";
                    } catch (Exception e) {
                        rankPrefix = "";
                        ChatLib.chat("Synthesis tried to parse Hypixel rank information from SkyCrypt, but failed. See logs.");
                        System.out.println("Synthesis tried to parse Hypixel rank information from SkyCrypt, but failed. See below.");
                        e.printStackTrace();
                    }
                }
                if (isInGuild) {
                    guildInfo = " §2In §a" + guildName + " §7" + guildTag + " " + guildRank + "§2, at §a" + guildMembers + " §2members and guild level §a" + guildLevel + "§2. " + guildMaster;
                }
                String possessiveApostrophe = "'s";
                if (displayName.endsWith("s")) {
                    possessiveApostrophe = "'";
                }

                ChatLib.chat("Here are " + rankPrefix + displayName + "§r" + possessiveApostrophe
                            + " SkyCrypt stats on their " + gameMode + "§r profile named " + cuteName + " §r(their most recent profile):"
                            + linePrefix + (isInGuild ? guildInfo : "§2They do not appear to be in a guild.")
                            + linePrefix + purseString
                            + linePrefix + skillAvg
                            + linePrefix + totalAndSlayerXp
                            + linePrefix + catacombsLvlString
                            + linePrefix + essenceString
                            + linePrefix + weightString
                            + linePrefix + firstJoinText
                            + linePrefix + fairySoulFraction
                            + linePrefix + uuidFromJson
                            + linePrefix + profileId
                        );
                if (displayName.equals("Technoblade") || uuidFromJson.contains("b876ec32e396476ba1158438d83c67d4")) {
                    Calendar c = Calendar.getInstance();
                    ChatLib.chat("Please donate to the Sarcoma Foundation of America (https://www.curesarcoma.org/technoblade-tribute/), or buy his memorial merchandise at https://technoblade.com.");
                    if (c.get(Calendar.YEAR) == 2022 && c.get(Calendar.MONTH) == 6) {
                        ChatLib.chat("Make sure you visit Technoblade's memorial at the main Hypixel lobby and speak to the Book Keeper NPC there as well.");
                    }
                } else if (displayName.equals("SirDesco") || uuidFromJson.contains("e710ff36fe334c0e8401bda9d24fa121")) {
                    ChatLib.chat("Oh look, it's the Synthesis developer!");
                }
            } catch (Exception e) {
                if (http.getResponseCode() != 200) {
                    ChatLib.chat("Synthesis did not get an A-OK response from SkyCrypt. The response code Synthesis got instead was " + http.getResponseCode() + ". Aborting mission.");
                    System.out.println("Synthesis did not get an A-OK response from SkyCrypt. The response code Synthesis got instead was " + http.getResponseCode() + ". See below.");
                } else {
                    ChatLib.chat("Synthesis got information from SkyCrypt, but couldn't successfully parse it. See logs.");
                    System.out.println("Synthesis got information from SkyCrypt, but couldn't successfully parse it. See below.");
                }
                e.printStackTrace();
                return;
            }
        } catch (Exception e) {
            ChatLib.chat("Synthesis ran into a problem checking " + theNameToCheck + "'s weight. See logs.");
            System.out.println("Synthesis ran into a problem checking " + theNameToCheck + "'s weight. See below.");
            e.printStackTrace();
        }
    }
}
