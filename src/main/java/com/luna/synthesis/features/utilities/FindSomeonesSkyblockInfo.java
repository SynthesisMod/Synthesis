// File is now defunct as NEU's profile viewer displays both weight calculations.
// Source code remains in case someone wants to pick up this mess.
// - Erymanthus

// package com.luna.synthesis.features.utilities;

// import com.luna.synthesis.Synthesis;
// import com.luna.synthesis.core.Config;
// import com.luna.synthesis.events.MessageSentEvent;
// import com.luna.synthesis.utils.ChatLib;

// import net.minecraft.client.Minecraft;
// import net.minecraftforge.client.event.ClientChatReceivedEvent;
// import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// import org.apache.commons.io.IOUtils;

// import java.io.InputStream;
// import java.util.Calendar;
// import java.util.Date;
// import java.util.Map;
// import java.util.Set;
// import java.net.HttpURLConnection;
// import java.net.URL;
// import java.nio.charset.StandardCharsets;
// import java.text.SimpleDateFormat;

// import com.google.gson.*;


// /**
//  * <pre>
//  * FindSomeonesSkyblockInfo
//  * A Java class by Erymanthus | RayDeeUx for Synthesis-NONCANON.
//  * 
//  * Suggestion #33 by OutlawMC#0001
//  * Add the commands "/lilyweight" and "/senweight"
//  * to get ingame weight calcs (basically same as
//  * sbe has but add options to view both)
//  * 
//  * Suggestion #83 by Shy#9999
//  * Show a short version of a players stats in trade menu's
//  * so you can determine you (sic) SUSSY they are
//  * 
//  * </pre>
//  **/
// //to #33: SIKE! IT'S IN `[share]` FORMAT—writing it in SynthesisCommand.java won't be fun!
// //to #83: SIKE! it's shown in chat instead because i do NOT want to mess with the custom NEU trade menu.
// //event.setCanceled(true) abused because NO ONE should get muted over using this and going to appeals = going to limbo
// public class FindSomeonesSkyblockInfo {
//     private final Config config = Synthesis.getInstance().getConfig();
//     private final String skyCryptURL = ("https://sky.shiiyu.moe/api/v2/profile/");

//     @SubscribeEvent
//     public void onMessageSent(MessageSentEvent event) {
//         if (event.message.endsWith(config.utilitiesShareText) || event.message.endsWith(config.utilitiesShareBootsText) || event.message.endsWith(config.utilitiesShareHelmetText) || event.message.endsWith(config.utilitiesShareLeggingsText) || event.message.endsWith(config.utilitiesShareChestplateText)) {return;}
//         if (event.message.startsWith("[weight")) {event.setCanceled(true); if (!config.utilitiesCheckWeight){ChatLib.chat("You have the setting disabled. Please enable it and try again, but do so with extreme caution.");return;}}
//         if (event.message.startsWith("[stats") && event.message.endsWith("]")) {event.setCanceled(true); new Thread(() -> {checkSomeonesStats(event.message);}).start(); return;}
//         new Thread(() -> {
//             String nameToCheck = "";
//             if (event.message.startsWith("[weight")) {
//                 if (event.message.endsWith("[weight]")) {
//                     nameToCheck = Minecraft.getMinecraft().thePlayer.getName();
//                 } else {
//                     nameToCheck = (event.message.toLowerCase().replace("[weight ", "").replace("]", "")).replace(" ", "");
//                 }
//             } else {
//                 return;
//             }
//             try {
//                 URL url = new URL(skyCryptURL + nameToCheck);
//                 HttpURLConnection http = (HttpURLConnection) url.openConnection();
//                 http.setDoOutput(true);
//                 http.setDoInput(true);
//                 http.setRequestProperty("User-Agent", "SynthesisMod-NONCANON");
//                 http.setRequestProperty("Accept", "application/json");
//                 http.setRequestProperty("Method", "GET");
//                 http.connect();
//                 try (InputStream instream = http.getInputStream()) {
//                     JsonParser parser = new JsonParser();
//                     JsonObject data = parser.parse(new String(IOUtils.toByteArray(instream), StandardCharsets.UTF_8)).getAsJsonObject();
//                     if (!data.has("profiles")) {
//                         ChatLib.chat("Synthesis failed in getting information from SkyCrypt. Either their API is down or this player doesn't have any profiles at all. Aborting mission.");
//                         return;
//                     }

//                     JsonObject profiles = data.get("profiles").getAsJsonObject();
//                     Set<Map.Entry<String, JsonElement>> profileSet = profiles.entrySet();
//                     JsonElement currentSbProfileWeightData = null;
//                     String displayName = "";

//                     for (Map.Entry<String,JsonElement> me : profileSet)
//                     {
//                         if (me.getValue().getAsJsonObject().get("current").getAsBoolean()) {
//                             displayName = ((JsonObject)(me.getValue().getAsJsonObject().get("data"))).get("display_name").getAsString();
//                             currentSbProfileWeightData = ((JsonObject)(me.getValue().getAsJsonObject().get("data"))).get("weight");
//                         }
//                     }

//                     int overallSenitherWeight = ((int)((JsonObject)(currentSbProfileWeightData).getAsJsonObject().get("senither")).get("overall").getAsDouble());
//                     int overallLilyWeight = ((int)((JsonObject)(currentSbProfileWeightData).getAsJsonObject().get("lily")).get("total").getAsDouble());

//                     ChatLib.chat(displayName + "'s weight info is as follows: ");
//                     ChatLib.chat("Overall Lily Weight: " + overallLilyWeight + "\nOverall Senither Weight: " + overallSenitherWeight);
                    
//                 } catch (Exception e) {
//                     ChatLib.chat("Synthesis did not get an A-OK response from SkyCrypt. The response code Synthesis got instead was " + http.getResponseCode() + ". Aborting mission.");
//                     System.out.println("Synthesis did not get an A-OK response from SkyCrypt. The response code Synthesis got instead was " + http.getResponseCode() + ". Aborting mission, see below.");
//                     return;
//                 }
//             } catch (Exception e) {
//                 ChatLib.chat("Synthesis ran into a problem checking " + nameToCheck + "'s weight. See logs.");
//                 System.out.println("Synthesis ran into a problem checking " + nameToCheck + "'s weight. See below.");
//                 e.printStackTrace();
//             }
//         }).start();
//     }

//     @SubscribeEvent
//     public void onChatEvent(ClientChatReceivedEvent e) {
//         String message = e.message.getUnformattedText();
//         if (!(message.startsWith("You have sent a trade request to "))) {return;}
//         String theirName = message.replace("You have sent a trade request to ", "").replace(".", "");
//         if (!config.utilitiesCheckStats) {ChatLib.chat("You have currently disabled player analyses at this time. Run away as far as possible from " + theirName + " to cancel the trade and re-enable the setting in the config if you want to get an analysis on them."); /* Minecraft.getMinecraft().thePlayer.sendChatMessage("wait hold on a sec need to tweak my mod configs"); */ return;}
//         ChatLib.chat("Running analysis on " + theirName + "...");
//         new Thread(() -> {checkSomeonesStats("[stats " + theirName + "]");}).start();
//     }

//     public void checkSomeonesStats(String thatOneParameter) {
//         String theNameToCheck = "";
//         if (thatOneParameter.endsWith(config.utilitiesShareText) || thatOneParameter.endsWith(config.utilitiesShareBootsText) || thatOneParameter.endsWith(config.utilitiesShareHelmetText) || thatOneParameter.endsWith(config.utilitiesShareLeggingsText) || thatOneParameter.endsWith(config.utilitiesShareChestplateText) || thatOneParameter.startsWith("[weight")) {return;}
//         if (!config.utilitiesCheckStats) {ChatLib.chat("You have currently disabled player analyses at this time. Re-enable the setting in the config if you want to get an analysis on them."); return;}
//         if (thatOneParameter.startsWith("[stats") && thatOneParameter.endsWith("]")) {
//             if (thatOneParameter.endsWith("[stats]")) {
//                 theNameToCheck = Minecraft.getMinecraft().thePlayer.getName();
//             } else {
//                 theNameToCheck = (thatOneParameter.toLowerCase().replace("[stats ", "").replace("]", "")).replace(" ", "");
//             }
//         } else {
//             ChatLib.chat("Are you sure you have a coherent username whose stats you want to see?");
//             return;
//         }
//         try {
//             URL url = new URL(skyCryptURL + theNameToCheck);
//             HttpURLConnection http = (HttpURLConnection) url.openConnection();
//             http.setDoOutput(true);
//             http.setDoInput(true);
//             http.setRequestProperty("User-Agent", "SynthesisMod-NONCANON");
//             http.setRequestProperty("Accept", "application/json");
//             http.setRequestProperty("Method", "GET");
//             http.connect();
//             try (InputStream instream = http.getInputStream()) {
//                 if (http.getResponseCode() != 200) {
//                     ChatLib.chat("Synthesis did not get an A-OK response from SkyCrypt. The response code Synthesis got instead was " + http.getResponseCode() + ". Aborting mission.");
//                     return;
//                 }
//                 JsonParser parser = new JsonParser();
//                 JsonObject data = parser.parse(new String(IOUtils.toByteArray(instream), StandardCharsets.UTF_8)).getAsJsonObject();
//                 if (!data.has("profiles")) {
//                     ChatLib.chat("Synthesis failed in getting information from SkyCrypt. Either their API is down or this player doesn't have any profiles at all. Aborting mission.");
//                     return;
//                 }

//                 JsonObject profiles = data.get("profiles").getAsJsonObject();
//                 Set<Map.Entry<String, JsonElement>> profileSet = profiles.entrySet();
//                 long currentSbProfileSkillAverage = 0;
//                 long currentSbProfileSlayerXp = 0;
//                 String displayName = "";
//                 String uuidFromJson = "";
//                 String cuteName = "";
//                 String profileId = "";
//                 String firstJoinText = "";
//                 long totalSkillXp = 0;
//                 long collectedFairySouls = 0;
//                 long totalFairySouls = 0;
//                 long catacombsLevel = 0;
//                 long iceEssence = 0;
//                 long witherEssence = 0;
//                 long spiderEssence = 0;
//                 long undeadEssence = 0;
//                 long diamondEssence = 0;
//                 long dragonEssence = 0;
//                 long goldEssence = 0;
//                 long crimsonEssence = 0;
//                 long totalEssence = 0;
//                 JsonElement currentSbProfileWeightData = null;
//                 JsonObject currentSbProfileData = null;
//                 String rankPrefix = "";
//                 String gameMode = "";
//                 String catacombsLvlString = "";
//                 boolean isInGuild = false;
//                 boolean isCata = false;
//                 boolean currentAreaUpdatedFromJson = false;
//                 String guildName = "";
//                 String guildTag = "";
//                 long guildMembers = 0;
//                 long guildLevel = 0;
//                 String guildRank = "";
//                 String guildMaster = "";
//                 String guildInfo = "";
//                 long purse = 0;
//                 long bank = 0;
//                 String lastActiveString = "";
//                 String dClassesInfo = "";

//                 for (Map.Entry<String,JsonElement> me : profileSet)
//                 {
//                     if (me.getValue().getAsJsonObject().get("current").getAsBoolean()) {
//                         currentSbProfileData = ((me.getValue().getAsJsonObject().get("data").getAsJsonObject()));
//                     }
//                 }

//                 displayName = currentSbProfileData.get("display_name").getAsString();
//                 uuidFromJson = currentSbProfileData.get("uuid").getAsString();
//                 rankPrefix = currentSbProfileData.get("rank_prefix").getAsString();
//                 cuteName = currentSbProfileData.get("profile").getAsJsonObject().get("cute_name").getAsString();
//                 profileId = currentSbProfileData.get("profile").getAsJsonObject().get("profile_id").getAsString();
//                 try {
//                     gameMode = currentSbProfileData.get("profile").getAsJsonObject().get("game_mode").getAsString();
//                 } catch (Exception e) {
//                     gameMode = ("Classic");
//                 }
//                 currentSbProfileSkillAverage = currentSbProfileData.get("average_level").getAsLong();
//                 currentSbProfileSlayerXp = currentSbProfileData.get("slayer_xp").getAsLong();
//                 firstJoinText = currentSbProfileData.get("first_join").getAsJsonObject().get("text").getAsString();
//                 totalSkillXp = currentSbProfileData.get("total_skill_xp").getAsLong();
//                 collectedFairySouls = currentSbProfileData.get("fairy_souls").getAsJsonObject().get("collected").getAsLong();
//                 totalFairySouls = currentSbProfileData.get("fairy_souls").getAsJsonObject().get("total").getAsLong();
//                 try {
//                     JsonObject dungeonsData = currentSbProfileData.get("dungeons").getAsJsonObject();
//                     catacombsLevel = dungeonsData.get("catacombs").getAsJsonObject().get("level").getAsJsonObject().get("level").getAsLong();
//                     isCata = true;
//                     String currentClass = "";
//                     if (dungeonsData.get("used_classes").getAsBoolean()) {
//                         currentClass = dungeonsData.get("selected_class").getAsString();
//                         JsonObject classData = dungeonsData.get("classes").getAsJsonObject();
//                         long healerLvl = classData.get("healer").getAsJsonObject().get("experience").getAsJsonObject().get("level").getAsLong();
//                         long mageLvl = classData.get("mage").getAsJsonObject().get("experience").getAsJsonObject().get("level").getAsLong();
//                         long berserkLvl = classData.get("berserk").getAsJsonObject().get("experience").getAsJsonObject().get("level").getAsLong();
//                         long archerLvl = classData.get("archer").getAsJsonObject().get("experience").getAsJsonObject().get("level").getAsLong();
//                         long tankLvl = classData.get("tank").getAsJsonObject().get("experience").getAsJsonObject().get("level").getAsLong();
//                         long avgClassLvl = ((healerLvl+mageLvl+berserkLvl+archerLvl+tankLvl)/(5L));
//                         dClassesInfo = "§r | §aH§r: §a" + healerLvl + "§r, §dM§r: §d" + mageLvl + "§r, §6B§r: §6" + berserkLvl + "§r, §eA§r: §e" + archerLvl + "§r, §2T§r: §2" + tankLvl + "§r, AVG: " + avgClassLvl;
//                         currentClass = ("" + currentClass.charAt(0)).toUpperCase();
//                         dClassesInfo = dClassesInfo.replace(currentClass, "§l§n§o" + currentClass);
//                     }
//                 } catch (Exception e) {
//                     isCata = false;
//                     e.printStackTrace();
//                 }
//                 if (isCata) {
//                     JsonObject essenceData = currentSbProfileData.get("essence").getAsJsonObject();
//                     iceEssence = essenceData.get("ice").getAsLong();
//                     witherEssence = essenceData.get("wither").getAsLong();
//                     spiderEssence = essenceData.get("spider").getAsLong();
//                     undeadEssence = essenceData.get("undead").getAsLong();
//                     diamondEssence = essenceData.get("diamond").getAsLong();
//                     dragonEssence = essenceData.get("dragon").getAsLong();
//                     goldEssence = essenceData.get("gold").getAsLong();
//                     crimsonEssence = essenceData.get("crimson").getAsLong();
//                     totalEssence = iceEssence + witherEssence + spiderEssence + undeadEssence + diamondEssence + dragonEssence + goldEssence + crimsonEssence;
//                 }
//                 purse = currentSbProfileData.get("purse").getAsLong();
//                 try {
//                     bank = currentSbProfileData.get("bank").getAsLong();
//                 } catch (Exception e) {bank = -1;}
//                 try {
//                     currentAreaUpdatedFromJson = currentSbProfileData.get("current_area_updated").getAsBoolean();
//                 } catch (Exception e) {currentAreaUpdatedFromJson = false;}
//                 if (!currentAreaUpdatedFromJson) {
//                     lastActiveString = ("Last seen in SB on " + new SimpleDateFormat("EEEE, MMM d, yyy h:mm:ss a z").format((new Date(currentSbProfileData.get("last_updated").getAsJsonObject().get("unix").getAsLong()))));
//                 } else {
//                     lastActiveString = "Probably in Skyblock right now";
//                 }
//                 currentSbProfileWeightData = currentSbProfileData.get("weight");
//                 double overallSenitherWeight = (((JsonObject)(currentSbProfileWeightData).getAsJsonObject().get("senither")).get("overall").getAsDouble());
//                 double overallLilyWeight = (((JsonObject)(currentSbProfileWeightData).getAsJsonObject().get("lily")).get("total").getAsDouble());
//                 try {
//                     JsonObject guildData = currentSbProfileData.get("guild").getAsJsonObject();
//                     isInGuild = true;
//                     guildName = guildData.get("name").getAsString();
//                     guildTag = "[" + guildData.get("tag").getAsString() + "]";
//                     guildLevel = guildData.get("level").getAsLong();
//                     guildMembers = guildData.get("members").getAsLong();
//                     guildRank = guildData.get("rank").getAsString();
//                     guildMaster = guildData.get("gmUser").getAsJsonObject().get("display_name").getAsString();
//                     if (guildMaster.equals(displayName)) {
//                         guildRank = "§2as §6the guildmaster";
//                         guildMaster = "";
//                     } else {
//                         guildRank = "§2with guild rank §a\"" + guildRank + "\"";
//                         guildMaster = "Their guildmaster is §a" + guildMaster + "§2.";
//                     }
//                 } catch (Exception e) {
//                     isInGuild = false;
//                 }

//                 String linePrefix = ("\n §8- ");
//                 String skillAvg = ("§9" + currentSbProfileSkillAverage + " Skill Average");
//                 firstJoinText = ("§aProfile created " + firstJoinText);
//                 String activityString = (firstJoinText + ((config.utilitiesCheckStatsLvlOfDetail == 2) ? ("§r | §a" + lastActiveString) : ""));
//                 cuteName = ("§6" + cuteName);
//                 String uuidFromJsonString = ("§7UUID: §2" + uuidFromJson);
//                 String totalAndSlayerXp = ("§3" + totalSkillXp + " total Skill XP" + ((config.utilitiesCheckStatsLvlOfDetail == 2) ? ("§r | §c" + currentSbProfileSlayerXp + " Slayer XP") : ""));
//                 String fairySoulFraction = ("§dCollected " + collectedFairySouls + "/" + totalFairySouls + " fairy souls");
//                 catacombsLvlString = ("§4Catacombs Level " + catacombsLevel);
//                 String essenceString = ("§5Total essence: §r" + totalEssence + " Essence (§9" + iceEssence + " Ice§r, §7" + witherEssence + " Wither§r, §4" + spiderEssence + " Spider§r, §5" + undeadEssence + " Undead§r, §b" + diamondEssence + " Diamond§r, §e" + dragonEssence + " Dragon§r, §6" + goldEssence + " Gold§r, §c" + crimsonEssence + " Crimson§r)");
//                 String weightString = ("§eOverall Lily Weight: " + ((int)(overallLilyWeight)) + (config.utilitiesCheckStatsLvlOfDetail == 2 ? " §r| §eOverall Senither Weight: " + ((int)(overallSenitherWeight)) : ""));
//                 String coinsString = ("§6Purse: " + purse + " coin" + (purse != 1 ? "s" : "") + ((config.utilitiesCheckStatsLvlOfDetail > 0) ? " §r| " + (bank != -1 ? "§6Bank: " + bank + " coin" + (bank != 1 ? "s" : "") : "§cBank API disabled.") : ""));
//                 String profileIdString = ("§7Profile ID: §2" + profileId);
//                 gameMode = ("§6" + Character.toUpperCase(gameMode.charAt(0)) + gameMode.substring(1));
//                 if (rankPrefix.equals("")) {
//                     rankPrefix = ("§7");
//                 } else {
//                     try {
//                         // things to remove!
//                         // <div class=\"rank-tag nice-colors-dark\">
//                         // <div class=\"rank-name\" style=\"background-color: var(--
//                         // )\">
//                         // </div>
//                         // <div class=\"rank-plus\" style=\"background-color: var(--
//                         // \n
//                         // " "
//                         rankPrefix = rankPrefix.replace("<div class=\"rank-tag nice-colors-dark\">", "")
//                         .replace("<div class=\"rank-name\" style=\"background-color: var(--", "")
//                         .replace(")\">", "").replace("</div>", "")
//                         .replace("<div class=\"rank-plus\" style=\"background-color: var(--", "")
//                         .replaceAll("\n", "")
//                         .replaceAll(" ", "");
//                         rankPrefix = rankPrefix.substring(0, 2) + "[" + rankPrefix + rankPrefix.substring(0, 2) + "] ";
//                         rankPrefix = rankPrefix.replace("§cYOUTUBE","§fYOUTUBE"); //edge case YT rank because skycrypt devs
//                         if (rankPrefix.contains("YOUTUBE")) {
//                             int temp = ((int)(Math.random() * ((double)(3D))));
//                             if (temp == 1) {
//                                 rankPrefix = ("§6[YT] ");
//                             } else if (temp == 2) {
//                                 rankPrefix = ("§6[§cYOU§fTUBE§6] ");
//                             }
//                         }
//                     } catch (Exception e) {
//                         rankPrefix = "";
//                         ChatLib.chat("Synthesis tried to parse Hypixel rank information from SkyCrypt, but failed. See logs.");
//                         System.out.println("Synthesis tried to parse Hypixel rank information from SkyCrypt, but failed. See below.");
//                         e.printStackTrace();
//                     }
//                 }
//                 if (isInGuild) {
//                     guildInfo = " §2In §a" + guildName + " §7" + guildTag + " " + guildRank + "§2, at §a" + guildMembers + " §2members and guild level §a" + guildLevel + "§2. " + guildMaster;
//                 }
//                 String possessiveApostrophe = "'s";
//                 if (displayName.endsWith("s")) {
//                     possessiveApostrophe = "'";
//                 }

//                 ChatLib.chat("Here are " + rankPrefix + displayName + "§r" + possessiveApostrophe
//                             + " SkyCrypt stats on their " + gameMode + "§r profile named " + cuteName + " §r(their most recent profile, with §dLOD " + config.utilitiesCheckStatsLvlOfDetail + "§r):"
//                             + ((config.utilitiesCheckStatsLvlOfDetail > 0) ? linePrefix + (isInGuild ? guildInfo : "§2They do not appear to be in a guild.") : "")
//                             + linePrefix + coinsString
//                             + linePrefix + skillAvg + ((config.utilitiesCheckStatsLvlOfDetail > 0) ? ("§r | " + totalAndSlayerXp) : "")
//                             + linePrefix + (isCata ? catacombsLvlString + ((config.utilitiesCheckStatsLvlOfDetail > 0) ? dClassesInfo : "") : "§4They have not explored the Catacombs yet.")
//                             + ((isCata && config.utilitiesCheckStatsLvlOfDetail == 2) ? linePrefix + essenceString : "")
//                             + linePrefix + weightString
//                             + linePrefix + activityString
//                             + (config.utilitiesCheckStatsLvlOfDetail == 2 ? linePrefix + fairySoulFraction + linePrefix + uuidFromJsonString + linePrefix + profileIdString : "")
//                         );
//                 if (displayName.equals("Technoblade") || uuidFromJson.contains("b876ec32e396476ba1158438d83c67d4")) {
//                     Calendar c = Calendar.getInstance();
//                     ChatLib.chat("Please donate to the Sarcoma Foundation of America (https://www.curesarcoma.org/technoblade-tribute/), or buy his memorial merchandise at https://technoblade.com.");
//                     if (c.get(Calendar.YEAR) == 2022 && c.get(Calendar.MONTH) == 6) {
//                         ChatLib.chat("Make sure you visit Technoblade's memorial at the main Hypixel lobby and speak to the Book Keeper NPC there as well.");
//                     }
//                 } else if (displayName.equals("SirDesco") || uuidFromJson.contains("e710ff36fe334c0e8401bda9d24fa121")) {
//                     ChatLib.chat("Oh look, it's the Synthesis developer!");
//                 }
//                 if (uuidFromJson.equals(profileId) && config.utilitiesCheckStatsLvlOfDetail == 2) {
//                     ChatLib.chat("You may notice that their Minecraft UUID and Skyblock profile ID are the same. Why? Listen, I didn't make the rules, and neither did SkyCrypt devs. Don't ask any of us.");
//                 }
//             } catch (Exception e) {
//                 if (http.getResponseCode() != 200) {
//                     ChatLib.chat("Synthesis did not get an A-OK response from SkyCrypt. The response code Synthesis got instead was " + http.getResponseCode() + ". Aborting mission.");
//                     System.out.println("Synthesis did not get an A-OK response from SkyCrypt. The response code Synthesis got instead was " + http.getResponseCode() + ". See below.");
//                 } else {
//                     ChatLib.chat("Synthesis got information from SkyCrypt, but couldn't successfully parse it. See logs.");
//                     System.out.println("Synthesis got information from SkyCrypt, but couldn't successfully parse it. See below.");
//                 }
//                 e.printStackTrace();
//                 return;
//             }
//         } catch (Exception e) {
//             ChatLib.chat("Synthesis ran into a problem checking " + theNameToCheck + "'s weight. See logs.");
//             System.out.println("Synthesis ran into a problem checking " + theNameToCheck + "'s weight. See below.");
//             e.printStackTrace();
//         }
//     }
// }
