package com.luna.synthesis.features.utilities;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import com.luna.synthesis.utils.Utils;
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
                event.message = Utils.newChatWithLinks(config.utilitiesBridgeMessageFormat.replaceAll("&", "§").replace("<ign>", ign).replace("<msg>", msg));
            }
        }
    }
}
