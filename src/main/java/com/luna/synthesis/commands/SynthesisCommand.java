package com.luna.synthesis.commands;

import com.google.gson.*;
import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import com.luna.synthesis.managers.BackpackManager;
import com.luna.synthesis.utils.ChatLib;
import gg.essential.api.EssentialAPI;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import gg.essential.api.commands.Options;
import gg.essential.api.commands.SubCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
    public void bp(int number, Optional<String> name, Optional<Integer> meta) {
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
    public void domains(@Options({"add", "remove", "list"}) String options, Optional<String> domain) {
        if (!Loader.isModLoaded("patcher")) {
            ChatLib.chat("You can only use this feature if you use patcher.");
            return;
        }
        switch (options) {
            case "add":
                if (!domain.isPresent()) {
                    ChatLib.chat("You need to specify a domain. Example: boob.li");
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
                    ChatLib.chat("You need to specify a domain. Example: boob.li");
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

    @SubCommand("share")
    public void share() {
        ItemStack item = Minecraft.getMinecraft().thePlayer.getHeldItem();
        if (item == null) {
            ChatLib.chat("You need to be holding something for this to work.");
            return;
        }
        NBTTagCompound extraAttributes = item.getSubCompound("ExtraAttributes", false);
        if (extraAttributes != null && extraAttributes.hasKey("uuid")) {
            JsonObject body = new JsonObject();
            body.add("owner", new JsonPrimitive(Minecraft.getMinecraft().getSession().getPlayerID()));
            JsonObject itemJson = new JsonObject();
            itemJson.add("name", new JsonPrimitive(item.getDisplayName()));
            JsonArray loreArray = new JsonArray();
            for (String s : item.getTooltip(Minecraft.getMinecraft().thePlayer, false)) {
                loreArray.add(new JsonPrimitive(s));
            }
            itemJson.add("lore", loreArray);
            itemJson.add("uuid", new JsonPrimitive(extraAttributes.getString("uuid")));
            body.add("item", itemJson);

            (new Thread(() -> {
                try {
                    HttpClient httpclient = HttpClients.createDefault();
                    HttpPost httppost = new HttpPost("https://synthesis-share.antonio32a.workers.dev/share");

                    StringEntity entity1 = new StringEntity(body.toString(), ContentType.APPLICATION_JSON);
                    httppost.setEntity(entity1);

                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity responseEntity = response.getEntity();

                    if (responseEntity != null) {
                        try (InputStream instream = responseEntity.getContent()) {
                            JsonParser parser = new JsonParser();
                            JsonObject shareJson = parser.parse(IOUtils.toString(instream)).getAsJsonObject();
                            if (!shareJson.get("success").getAsBoolean()) {
                                ChatLib.chat("Share was not successful. Reason: " + shareJson.get("error").getAsString());
                                return;
                            }
                            String shareId = shareJson.get("share").getAsJsonObject().get("id").getAsString();
                            Minecraft.getMinecraft().thePlayer.sendChatMessage("{SynthesisShare:" + shareId + "}");
                        } catch (JsonParseException e) {
                            ChatLib.chat("Something went wrong trying to read share.");
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            })).start();
        } else {
            ChatLib.chat("This item doesn't have a uuid.");
        }
    }
}