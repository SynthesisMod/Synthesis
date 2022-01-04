package com.luna.synthesis.features.cleanup;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DungeonCleanup {

    private final Config config = Synthesis.getInstance().getConfig();

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        if (event.type != 0) return;
        String msg = StringUtils.stripControlCodes(event.message.getUnformattedText());
        if (!msg.contains(":")) {
            if (config.cleanupDungeonPotionEffects && msg.equals("Your active Potion Effects have been paused and stored. They will be restored when you leave Dungeons! You are not allowed to use existing Potion Effects while in Dungeons.")) {
                event.setCanceled(true);
            }
            if (config.cleanupDungeonSoloClassMessage) {
                if (msg.startsWith("Your ") && msg.endsWith(" stats are doubled because you are the only player using this class!")) {
                    event.setCanceled(true);
                } else if (msg.startsWith("[Healer] ") || msg.startsWith("[Berserk] ") || msg.startsWith("[Mage] ") || msg.startsWith("[Archer] ") || msg.startsWith("[Tank] ")) {
                    event.setCanceled(true);
                }
            }
            if (config.cleanupDungeonUltimateMessage && msg.endsWith(" is ready to use! Press DROP to activate it!")) {
                event.setCanceled(true);
            }
            if (config.cleanupDungeonBlessingStatMessages) {
                if ((config.cleanupDungeonBlessingMessages && (msg.startsWith("A Blessing of ") || (msg.contains(" has obtained Blessing of ") && msg.endsWith("!")))) || msg.startsWith("DUNGEON BUFF! A Blessing of ") || msg.startsWith("DUNGEON BUFF! You found a Blessing of ") || msg.startsWith("     Grants you ") || msg.startsWith("     Granted you ")) {
                    event.setCanceled(true);
                }
            }
            if (config.cleanupDungeonSilverfishMessages && msg.equals("You cannot hit the silverfish while it's moving!")) {
                event.setCanceled(true);
            }
            if (config.cleanupDungeonKeyUsageMessages && (msg.equals("RIGHT CLICK on the BLOOD DOOR to open it. This key can only be used to open 1 door!") || msg.equals("RIGHT CLICK on a WITHER door to open it. This key can only be used to open 1 door!"))) {
                event.setCanceled(true);
            }
        }
        if (config.cleanupDungeonWatcherMessages && msg.startsWith("[BOSS] The Watcher: ") && !msg.equals("[BOSS] The Watcher: You have proven yourself. You may pass.")) {
            event.setCanceled(true);
        }
    }
}
