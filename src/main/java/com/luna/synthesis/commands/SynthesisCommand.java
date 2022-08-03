package com.luna.synthesis.commands;

import gg.essential.api.EssentialAPI;
import gg.essential.api.commands.*;

// import com.google.gson.*;
import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import com.luna.synthesis.managers.BackpackManager;
import com.luna.synthesis.utils.*;

// import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.Loader;

import java.util.*;

// import java.io.InputStream;
// import java.net.HttpURLConnection;
// import java.net.URL;
// import java.nio.charset.StandardCharsets;

// import org.apache.commons.io.IOUtils;

public class SynthesisCommand extends Command {
    public SynthesisCommand() {
        super("synthesis");
    }

    private final Config config = Synthesis.getInstance().getConfig();

    @Override
    public Set<Alias> getCommandAliases() {
        return new HashSet<>(Arrays.asList(new Alias("syn"), new Alias("synth")));
    }

    @DefaultHandler
    public void handle() {
        EssentialAPI.getGuiUtil().openScreen(Synthesis.getInstance().getConfig().gui());
    }

    @SubCommand("bp")
    public void bp(@DisplayName("backpack number") int number, @DisplayName("texture name") Optional<String> name, @DisplayName("texture meta") Optional<Integer> meta) {
        BackpackManager bpm = Synthesis.getInstance().getBackpackManager();
        if (number < 1 || number > 18) {
            ChatLib.chat("That's not a valid backpack number.");
            return;
        }
        if (!name.isPresent()) {
            bpm.modifyData(String.valueOf(number), "", 0);
            ChatLib.chat("Removed custom texture from backpack " + number);
        } else {
            String itemName = name.get().toLowerCase();
            if (!itemName.startsWith("minecraft:")) {
                itemName = "minecraft:" + itemName;
            }
            Item item = Item.getByNameOrId(itemName);
            if (item == null) {
                ChatLib.chat("Item \"" + itemName + "\" does not exist.");
                return;
            }
            if (meta.isPresent()) {
                List<ItemStack> items = new ArrayList<>();
                item.getSubItems(item, item.getCreativeTab(), items);
                if (items.size() - 1 < meta.get()) {
                    ChatLib.chat("Meta can't be that high for this item.");
                    return;
                }
                bpm.modifyData(String.valueOf(number), itemName, meta.get());
                ChatLib.chat("Added " + itemName + " texture to backpack " + number + " with meta " + meta.get());
            } else {
                bpm.modifyData(String.valueOf(number), itemName, 0);
                ChatLib.chat("Added " + itemName + " texture to backpack " + number);
            }
        }
    }

    @SubCommand("domains")
    public void domains(@Options({"add", "remove", "list"}) String options, @DisplayName("domain") Optional<String> domain) {
        if (!Loader.isModLoaded("patcher")) {
            ChatLib.chat("You can only use this feature if you use patcher.");
            return;
        }
        switch (options) {
            case "add":
                if (!domain.isPresent()) {
                    ChatLib.chat("You need to specify a domain. Example: bigraccoon.monster");
                    return;
                }
                if (config.patcherCustomDomains.contains(domain.get().toLowerCase())) {
                    ChatLib.chat("That's already a trusted domain.");
                    return;
                }
                config.patcherCustomDomains += domain.get().toLowerCase() + ",";
                ChatLib.chat("Added &a" + domain.get() + "&r to the trusted domain list.");
                config.markDirty();
                config.writeData();
                break;
            case "remove":
                if (!domain.isPresent()) {
                    ChatLib.chat("You need to specify a domain. Example: bigraccoon.monster");
                    return;
                }
                if (!config.patcherCustomDomains.contains(domain.get().toLowerCase())) {
                    ChatLib.chat("That was not a trusted domain.");
                    return;
                }
                config.patcherCustomDomains = config.patcherCustomDomains.replace(domain.get().toLowerCase() + ",", "");
                ChatLib.chat("Removed &c" + domain.get() + "&r from the trusted domain list.");
                config.markDirty();
                config.writeData();
                break;
            case "list":
                if (config.patcherCustomDomains.equals("")) {
                    ChatLib.chat("There aren't any trusted domains.");
                    return;
                }
                ChatLib.chat("&aList of domains: ");
                for (String s : config.patcherCustomDomains.split(",")) {
                    ChatLib.chat(" &7- &a" + s);
                }
                if (domain.isPresent()) {
                    ChatLib.chat("&dYou don't need to specify another argument, btw.");
                }
                break;
        }
    }

    // @SubCommand("pronouns")
    // public void pronouns(@DisplayName("username") Optional<String> username) {
    //     new Thread(() -> {
    //         String nameToCheck = "", uuid = "", pronounResult = "", pronounsToBe = "", apostrophe = "'s";
    //         if (username.isPresent()) {
    //             nameToCheck = username.get();
    //         } else {
    //             nameToCheck = Minecraft.getMinecraft().thePlayer.getName();
    //         }
    //         if (!(nameToCheck.contains("-"))) {
    //             try {
    //                 URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + nameToCheck);
    //                 HttpURLConnection http = (HttpURLConnection) url.openConnection();
    //                 http.connect();
    //                 try (InputStream instream = http.getInputStream()) {
    //                     if (http.getResponseCode() != 200) {
    //                         ChatLib.chat("Something went wrong with grabbing this player's Minecraft UUID. Aborting mission.");
    //                         return;
    //                     }
    //                     JsonParser parser = new JsonParser();
    //                     JsonObject data = parser.parse(new String(IOUtils.toByteArray(instream), StandardCharsets.UTF_8)).getAsJsonObject();
    //                     if (!data.has("id") || !data.has("name")) {
    //                         ChatLib.chat("Synthesis failed in getting this player's UUID from Mojang's API. Either their API is down (most likely) or a player with this username doesn't exist. Aborting mission.");
    //                         return;
    //                     }

    //                     String uuidToBe = data.get("id").getAsString();
    //                     uuid = uuidToBe.substring(0, 8) + "-" + uuidToBe.substring(8, 12) + "-" + uuidToBe.substring(12, 16) + "-" + uuidToBe.substring(16, 20) + "-" + uuidToBe.substring(20);
    //                     nameToCheck = data.get("name").getAsString();
    //                 }
    //             } catch (Exception e) {
    //                 ChatLib.chat("Something went wrong with grabbing this player's Minecraft UUID via Mojang's API. Aborting mission.");
    //                 e.printStackTrace();
    //             }
    //         } else {
    //             uuid = nameToCheck;
    //         }
    //         ChatLib.chat(uuid);
    //         //b43d7457-9da4-408b-a9fb-51239022cec9
    //         //https://pronoundb.org/api/v1/lookup?platform=minecraft&id=b43d7457-9da4-408b-a9fb-51239022cec9
    //         //"https://pronoundb.org/api/v1/lookup?platform=minecraft&id=" + uuid
    //         try {
    //             URL url = new URL("https://pronoundb.org/api/v1/lookup?platform=minecraft&id=" + uuid);
    //             HttpURLConnection http = (HttpURLConnection) url.openConnection();
    //             http.setDoOutput(true);
    //             http.setDoInput(true);
    //             http.setRequestProperty("User-Agent", "Synthesis-NONCANON");
    //             http.setRequestProperty("Accept", "application/json");
    //             http.setRequestProperty("Method", "GET");
    //             http.connect();
    //             try (InputStream instream = http.getInputStream()) {
    //                 if (http.getResponseCode() != 200) {
    //                     ChatLib.chat("Something went wrong with grabbing this player's pronouns from PronounDB. Aborting mission.");
    //                     return;
    //                 }
    //                 JsonParser parser = new JsonParser();
    //                 JsonObject data = parser.parse(new String(IOUtils.toByteArray(instream), StandardCharsets.UTF_8)).getAsJsonObject();
    //                 if (!data.has("pronouns")) {
    //                     ChatLib.chat("Synthesis failed in getting this player's pronouns from PronounDB's API. Either their API is down (most likely) or a player with this UUID doesn't exist. Aborting mission.");
    //                     return;
    //                 }

    //                 pronounsToBe = data.get("pronouns").getAsString();
    //                 Pronouns.doTheThingWithPronouns();
    //                 pronounsToBe = Pronouns.dict.get(pronounsToBe);
    //             }
    //         } catch (Exception e) {
    //             ChatLib.chat("Something went wrong with grabbing this player's PronounDB data. Aborting mission.");
    //             e.printStackTrace();
    //             return;
    //         }
    //         if (nameToCheck.endsWith("s")) apostrophe = "'";
    //         if (nameToCheck.contains("-")) pronounResult += ("The player with UUID ");
    //         pronounResult += (nameToCheck);
    //         if (pronounsToBe == "any") {
    //             pronounResult += " doesn't mind what pronouns you use for them.";
    //         } else if (pronounsToBe == "other") {
    //             pronounResult += " has personal pronoun preferences.";
    //         } else if (pronounsToBe == "ask") {
    //             pronounResult += " says, \"ask me for my preferred pronouns\".";
    //         } else if (pronounsToBe == "avoid") {
    //             pronounResult += " says, \"use my name instead of any pronouns\".";
    //         } else if (pronounsToBe == "unspecified") {
    //             pronounResult += " has yet to set a pronoun preference.";
    //         } else {
                
    //             pronounResult += (apostrophe + " preferred pronouns are " + pronounsToBe);
    //         }
    //         ChatLib.chat(pronounResult);
    //     }).start();
    // }
}