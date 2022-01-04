package com.luna.synthesis.features.utilities;

import com.google.gson.*;
import com.luna.synthesis.events.MessageSentEvent;
import com.luna.synthesis.utils.ChatLib;
import net.minecraft.client.Minecraft;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Share {

    private final Pattern shareRegexPattern = Pattern.compile("\\{SynthesisShare:([a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12})}", Pattern.CASE_INSENSITIVE);

    @SubscribeEvent
    public void onMessageSent(MessageSentEvent event) {
        String message = event.message;
        if (message.contains("[item]") || message.contains("[share]")) {
            ItemStack item = Minecraft.getMinecraft().thePlayer.getHeldItem();
            if (item == null) return;
            event.setCanceled(true);

            NBTTagCompound extraAttributes = item.getSubCompound("ExtraAttributes", false);
            JsonArray loreArray = new JsonArray();
            NBTTagList lore = item.getSubCompound("display", false).getTagList("Lore", 8);
            for (int i = 0; i < lore.tagCount(); i++) {
                loreArray.add(new JsonPrimitive(lore.getStringTagAt(i)));
            }

            JsonObject itemJson = new JsonObject();
            itemJson.add("name", new JsonPrimitive(item.getSubCompound("display", false).getString("Name")));
            itemJson.add("lore", loreArray);
            if (extraAttributes != null && extraAttributes.hasKey("uuid")) {
                itemJson.add("uuid", new JsonPrimitive(extraAttributes.getString("uuid")));
            }

            JsonObject body = new JsonObject();
            body.add("owner", new JsonPrimitive(Minecraft.getMinecraft().getSession().getPlayerID()));
            body.add("item", itemJson);

            (new Thread(() -> {
                try {
                    URL url = new URL("https://synthesis-share.antonio32a.com/share");
                    HttpURLConnection http = (HttpURLConnection) url.openConnection();
                    http.setDoOutput(true);
                    http.setDoInput(true);
                    http.setRequestProperty("Content-Type", "application/json");
                    http.setRequestProperty("User-Agent", "SynthesisMod");
                    http.setRequestProperty("Accept", "application/json");
                    http.setRequestProperty("Method", "POST");
                    http.connect();
                    try (OutputStream os = http.getOutputStream()) {
                        os.write(body.toString().getBytes(StandardCharsets.UTF_8));
                        os.close();
                        if (http.getResponseCode() != 200) {
                            ChatLib.chat("Something went wrong trying to upload share. Check logs maybe?");
                            System.out.println(http.getResponseMessage());
                            return;
                        }
                        JsonParser parser = new JsonParser();
                        JsonObject shareJson = parser.parse(IOUtils.toString(http.getInputStream())).getAsJsonObject();
                        if (!shareJson.get("success").getAsBoolean()) {
                            ChatLib.chat("Share was not successful. Reason: " + shareJson.get("error").getAsString());
                            return;
                        }

                        String shareId = shareJson.get("share").getAsJsonObject().get("id").getAsString();
                        String share = "{SynthesisShare:" + shareId + "}";
                        //Can't write event.message because this is a thread
                        Minecraft.getMinecraft().thePlayer.sendChatMessage(message.replace("[item]", share).replace("[share]", share));
                    }
                } catch (IOException e) {
                    ChatLib.chat("Something went wrong trying to upload share. Check logs maybe?");
                    e.printStackTrace();
                }
            })).start();
        }
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (event.type == 0 || event.type == 1) {
            String msg = StringUtils.stripControlCodes(event.message.getUnformattedText());
            if (!msg.contains("{SynthesisShare:") || !msg.contains("}")) return;

            Matcher matcher = shareRegexPattern.matcher(msg);
            event.setCanceled(true);

            (new Thread(() -> {
                ArrayList<IChatComponent> shares = new ArrayList<>();
                while (matcher.find()) {
                    String shareId = matcher.group(1);
                    System.out.println("Share ID: " + shareId);

                    try {
                        URL url = new URL("https://synthesis-share.antonio32a.com/share/" + shareId);
                        HttpURLConnection http = (HttpURLConnection) url.openConnection();
                        http.setDoOutput(true);
                        http.setDoInput(true);
                        http.setRequestProperty("User-Agent", "SynthesisMod");
                        http.setRequestProperty("Accept", "application/json");
                        http.setRequestProperty("Method", "GET");
                        http.connect();
                        try (InputStream instream = http.getInputStream()) {
                            if (http.getResponseCode() != 200) {
                                ChatLib.chat("Something went wrong trying to read share. Check logs maybe?");
                                System.out.println(http.getResponseMessage());
                                return;
                            }
                            JsonParser parser = new JsonParser();
                            JsonObject shareJson = parser.parse(new String(IOUtils.toByteArray(instream), StandardCharsets.UTF_8)).getAsJsonObject();
                            if (!shareJson.get("success").getAsBoolean()) {
                                ChatLib.chat("Share was not successful. Reason: " + shareJson.get("error").getAsString());
                                return;
                            }

                            JsonObject shareItem = shareJson.get("share").getAsJsonObject().get("item").getAsJsonObject();
                            String itemName = shareItem.get("name").getAsString();
                            JsonArray itemLore = shareItem.get("lore").getAsJsonArray();
                            boolean isItemVerified = shareJson.get("share").getAsJsonObject().get("verified").getAsBoolean();

                            IChatComponent shareComponent = new ChatComponentText(
                                    (isItemVerified ? EnumChatFormatting.GREEN + "✔ " : EnumChatFormatting.RED + "✖ ")
                                            + EnumChatFormatting.LIGHT_PURPLE
                                            + "[Synthesis " + itemName + EnumChatFormatting.LIGHT_PURPLE + "]"
                            );

                            AtomicReference<String> s = new AtomicReference<>("");
                            itemLore.iterator().forEachRemaining(jsonElement -> s.set(s.get() + jsonElement.getAsString() + "\n"));
                            String shareLore = itemName + "\n" + s.get();

                            shareComponent.getChatStyle().setChatHoverEvent(new HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT,
                                    new ChatComponentText(shareLore.substring(0, shareLore.length() - 2))
                            ));
                            shares.add(shareComponent);
                        }
                        /*HttpClient httpclient = HttpClients.createDefault();
                        HttpGet httpGet = new HttpGet("https://synthesis-share.antonio32a.workers.dev/share/" + shareId);

                        HttpResponse response = httpclient.execute(httpGet);
                        HttpEntity entity = response.getEntity();

                        if (entity != null) {
                            InputStream instream = entity.getContent();
                            JsonParser parser = new JsonParser();
                            JsonObject shareJson = parser.parse(new String(IOUtils.toByteArray(instream), StandardCharsets.UTF_8)).getAsJsonObject();
                            if (!shareJson.get("success").getAsBoolean()) {
                                ChatLib.chat("Share was not successful. Reason: " + shareJson.get("error").getAsString());
                                return;
                            }

                            JsonObject shareItem = shareJson.get("share").getAsJsonObject().get("item").getAsJsonObject();
                            String itemName = shareItem.get("name").getAsString();
                            JsonArray itemLore = shareItem.get("lore").getAsJsonArray();
                            boolean isItemVerified = shareJson.get("share").getAsJsonObject().get("verified").getAsBoolean();

                            IChatComponent shareComponent = new ChatComponentText(
                                (isItemVerified ? EnumChatFormatting.GREEN + "✔ " : EnumChatFormatting.RED + "✖ ")
                                + EnumChatFormatting.LIGHT_PURPLE
                                + "[Synthesis " + itemName + EnumChatFormatting.LIGHT_PURPLE + "]"
                            );

                            AtomicReference<String> s = new AtomicReference<>("");
                            itemLore.iterator().forEachRemaining(jsonElement -> s.set(s.get() + jsonElement.getAsString() + "\n"));
                            String shareLore = itemName + "\n" + s.get();

                            shareComponent.getChatStyle().setChatHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                new ChatComponentText(shareLore.substring(0, shareLore.length() - 2))
                            ));
                            shares.add(shareComponent);
                        }*/
                    } catch (IOException | JsonParseException e) {
                        ChatLib.chat("Something went wrong trying to read share. Check logs maybe?");
                        e.printStackTrace();
                    }
                }

                IChatComponent toSend = new ChatComponentText("");
                ListIterator<String> it = Arrays.asList(event.message.getFormattedText().split(shareRegexPattern.pattern())).listIterator();
                while (it.hasNext()) {
                    String s = it.next();
                    toSend.appendSibling(new ChatComponentText(s));
                    if (it.hasNext()) {
                        toSend.appendSibling(shares.get(it.nextIndex() - 1));
                    }
                }

                Minecraft.getMinecraft().thePlayer.addChatMessage(toSend);
            })).start();

        }
    }
}
