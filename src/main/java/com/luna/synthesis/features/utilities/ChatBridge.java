package com.luna.synthesis.features.utilities;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatBridge {

    private final Config config = Synthesis.getInstance().getConfig();
    private final Pattern msgPattern = Pattern.compile("^Guild > (?<rank>\\[[A-Z+]+] )?(?<ign>[a-zA-Z0-9_]{3,16})(?<grank> \\[.+])?: (?<discordname>.{1,32})(?<separator>( > |: ))(?<msg>.+)");
    private final Pattern linkPattern = Pattern.compile("((?:[a-z0-9]{2,}:\\/\\/)?(?:(?:[0-9]{1,3}\\.){3}[0-9]{1,3}|(?:[-\\w_\\.]{1,}\\.[a-z]{2,}?))(?::[0-9]{1,5})?.*?(?=[!\"\u00A7 \n]|$))", Pattern.CASE_INSENSITIVE);

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClientChatMessage(ClientChatReceivedEvent event) {
        if (!config.utilitiesBridge) return;
        String message = StringUtils.stripControlCodes(event.message.getUnformattedText());
        if (!message.startsWith("Guild > ") || !message.contains(config.utilitiesBridgeBotName)) return;
        Matcher matcher = msgPattern.matcher(message);
        if (matcher.matches() && matcher.groupCount() == 7) {
            String msgSender = matcher.group(2);
            if (msgSender.equals(config.utilitiesBridgeBotName)) {
                String ign = matcher.group(4);
                String msg = matcher.group(7);
                event.message = newChatWithLinks(config.utilitiesBridgeMessageFormat.replaceAll("&", "§").replace("<ign>", ign).replace("<msg>", msg));
            }
        }
    }

    // Adapted from ForgeHooks::newChatWithLinks, may change linkPattern in the future
    private IChatComponent newChatWithLinks(String string) {
        IChatComponent ichat = null;
        Matcher matcher = linkPattern.matcher(string);
        int lastEnd = 0;

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            String part = string.substring(lastEnd, start);
            if (part.length() > 0) {
                if (ichat == null) {
                    ichat = new ChatComponentText(part);
                } else {
                    ichat.appendText(part);
                }
            }
            lastEnd = end;
            String url = string.substring(start, end);
            IChatComponent link = new ChatComponentText(url);

            try {
                if ((new URI(url)).getScheme() == null) {
                    url = "http://" + url;
                }
            } catch (URISyntaxException e) {
                if (ichat == null) ichat = new ChatComponentText(url);
                else ichat.appendText(url);
                continue;
            }

            ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
            link.getChatStyle().setChatClickEvent(click);
            if (ichat == null) {
                ichat = link;
            } else {
                ichat.appendSibling(link);
            }
        }

        String end = string.substring(lastEnd);
        if (ichat == null) {
            ichat = new ChatComponentText(end);
        } else if (end.length() > 0) {
            ichat.appendText(string.substring(lastEnd));
        }
        return ichat;
    }
}
