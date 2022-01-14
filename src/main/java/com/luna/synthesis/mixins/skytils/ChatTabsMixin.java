package com.luna.synthesis.mixins.skytils;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import com.luna.synthesis.utils.Utils;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Pseudo
@Mixin(targets = "skytils.skytilsmod.features.impl.handlers.ChatTabs", remap = false)
public class ChatTabsMixin {

    private final Config config = Synthesis.getInstance().getConfig();
    private final Pattern msgPattern = Pattern.compile("^Guild > (?<rank>\\[[A-Z+]+] )?(?<ign>[a-zA-Z0-9_]{3,16})(?<grank> \\[.+])?: (?<discordname>.{1,32})(?<separator>( > |: ))(?<msg>.+)");

    @Dynamic
    @Redirect(method = "onChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/play/server/S02PacketChat;getChatComponent()Lnet/minecraft/util/IChatComponent;", ordinal = 0))
    public IChatComponent redirectComponent(S02PacketChat packet) {
        if (config.utilitiesBridgeGuildChatTab) {
            if (!config.utilitiesBridge) return packet.getChatComponent();
            String message = StringUtils.stripControlCodes(packet.getChatComponent().getUnformattedText());
            if (!message.startsWith("Guild > ") || !message.contains(config.utilitiesBridgeBotName)) return packet.getChatComponent();
            Matcher matcher = msgPattern.matcher(message);
            if (matcher.matches() && matcher.groupCount() == 7) {
                String msgSender = matcher.group(2);
                if (msgSender.equals(config.utilitiesBridgeBotName)) {
                    String ign = matcher.group(4);
                    String msg = matcher.group(7);
                    return Utils.newChatWithLinks(config.utilitiesBridgeMessageFormat.replaceAll("&", "§").replace("<ign>", ign).replace("<msg>", msg));
                }
            }
        }
        return packet.getChatComponent();
    }

    // Hacky way to add bridge messages to guild, shrug
    @Dynamic
    @ModifyVariable(method = "shouldAllow", at = @At("HEAD"))
    public IChatComponent overrideShouldAllow(IChatComponent in) {
        if (config.utilitiesBridgeGuildChatTab) {
            if (in.getFormattedText().startsWith(config.utilitiesBridgeMessageFormat.replaceAll("&", "§").split("<")[0])) {
                return new ChatComponentText("§r§2Guild > " + in.getFormattedText());
            }
        }
        return in;
    }
}
