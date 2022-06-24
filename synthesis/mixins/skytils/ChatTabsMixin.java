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
