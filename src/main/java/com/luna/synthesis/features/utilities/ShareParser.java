package com.luna.synthesis.features.utilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.luna.synthesis.utils.ChatLib;
import net.minecraft.client.Minecraft;
import net.minecraft.event.HoverEvent;
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
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShareParser {

    private final Pattern shareRegex = Pattern.compile("\\{SynthesisShare:([a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12})}", Pattern.CASE_INSENSITIVE);

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (event.type == 0 || event.type == 1) {
            String msg = StringUtils.stripControlCodes(event.message.getUnformattedText());
            if (!msg.contains("{SynthesisShare:") || !msg.contains("}")) return;
            Matcher matcher = shareRegex.matcher(msg);
            if (matcher.find() && matcher.groupCount() == 1) {
                String shareId = matcher.group(1);
                String message = event.message.getFormattedText().split(": ")[0] + ": ";
                event.setCanceled(true);
                (new Thread(() -> {
                    try {
                        HttpClient httpclient = HttpClients.createDefault();
                        HttpGet httpGet = new HttpGet("https://synthesis-share.antonio32a.workers.dev/share/" + shareId);

                        HttpResponse response = httpclient.execute(httpGet);
                        HttpEntity entity = response.getEntity();

                        if (entity != null) {
                            try (InputStream instream = entity.getContent()) {
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
                                IChatComponent newComp = new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "[Synthesis " + itemName + EnumChatFormatting.LIGHT_PURPLE + "]");
                                AtomicReference<String> s = new AtomicReference<>("");
                                itemLore.iterator().forEachRemaining(jsonElement -> {
                                    s.set(s.get() + jsonElement.getAsString() + "\n");
                                });
                                newComp.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(s.get())));
                                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message + (isItemVerified ? EnumChatFormatting.GREEN + "✔ " : EnumChatFormatting.RED + "✖ ")).appendSibling(newComp));
                            } catch (JsonParseException e) {
                                ChatLib.chat("Something went wrong trying to read share.");
                                e.printStackTrace();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })).start();
            }
        }
    }
}
