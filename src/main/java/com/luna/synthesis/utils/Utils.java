package com.luna.synthesis.utils;

import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final Pattern linkPattern = Pattern.compile("((?:[a-z0-9]{2,}:\\/\\/)?(?:(?:[0-9]{1,3}\\.){3}[0-9]{1,3}|(?:[-\\w_\\.]{1,}\\.[a-z]{2,}?))(?::[0-9]{1,5})?.*?(?=[!\"\u00A7 \n]|$))", Pattern.CASE_INSENSITIVE);

    // Copied and adapted from Patcher
    public static boolean isDivider(String message) {
        if (message == null) return false;
        if (message.length() < 5) {
            return false;
        } else {
            for (int i = 0; i < message.length(); i++) {
                char c = message.charAt(i);
                if (c != '-' && c != '=' && c != '\u25AC') {
                    return false;
                }
            }
        }
        return true;
    }

    // Adapted from ForgeHooks::newChatWithLinks, may change linkPattern in the future
    public static IChatComponent newChatWithLinks(String string) {
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
