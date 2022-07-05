package com.luna.synthesis.features.utilities;

import gg.essential.api.EssentialAPI;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import com.luna.synthesis.utils.ChatLib;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.util.StringUtils;
import net.minecraft.item.ItemSkull;

import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;
import java.util.regex.Pattern;

/**
 * <pre>
 * BestiaryWarning
 * A Java class by Erymanthus | RayDeeUx for Synthesis-NONCANON.
 * 
 * Suggestion #63 by minhperry#2803
 * warning if you are near the 1m combat xp bestiary rhing
 * </pre>
 */

public class BestiaryWarning {

    private final Config config = Synthesis.getInstance().getConfig();
    private final Pattern bestiaryFamilyPattern = Pattern.compile(".*3.*BESTIARY.*");
    private final Pattern bestiaryLevelPattern = Pattern.compile(".*6.*BESTIARY.*MILESTONE.*");

    private boolean isAboutToLevelUp = false;
    private int bestiaryLevel = 0;
    private int levelProgress = 0;
    private int ticks = 0;

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        if (!(Minecraft.getMinecraft().thePlayer.openContainer instanceof ContainerChest) || 
            !(StringUtils.stripControlCodes(event.itemStack.getDisplayName()).startsWith("Bestiary Milestone ")) ||
            !(event.itemStack.getItem() instanceof ItemSkull)) return;
        bestiaryLevel = Integer.parseInt(StringUtils.stripControlCodes(event.itemStack.getDisplayName()).replace("Bestiary Milestone ", ""));
        config.personalBestiaryLevel = bestiaryLevel; //write to config
        if ((((bestiaryLevel+1) % 2) == 0) && ((bestiaryLevel+1) != 4) && ((bestiaryLevel+1) != 6) && ((bestiaryLevel+1) != 8)) { //source: https://hypixel-skyblock.fandom.com/wiki/Bestiary
            isAboutToLevelUp = true;
        }
        List<String> lore = event.toolTip;
        if (lore != null) {
            for (String s : lore) {
                if (s.endsWith("e10")) {
                    levelProgress = Integer.parseInt(StringUtils.stripControlCodes(s).replaceAll("-", "").replaceAll(" ", "").replaceAll("/10", ""));
                }
                if (!isAboutToLevelUp && (s.contains("Combat"))) { //failsafe method to cover rare edge cases
                    isAboutToLevelUp = true;
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onChatRecievedEvent(ClientChatReceivedEvent event) {
        if ((bestiaryFamilyPattern.matcher(event.message.getFormattedText())).find() &&
            event.message.getFormattedText().contains("3") &&
            event.message.getFormattedText().contains("BESTIARY ") &&
            event.message.getUnformattedText().indexOf("MILESTONE") == -1) {
            ChatLib.chat("Level progress updating! Original value was " + levelProgress + "/10.");
            levelProgress++;
            ChatLib.chat("Level progress updated! Updated value should be " + levelProgress + "/10.");
            if (levelProgress == 9) {
                ChatLib.chat("Level progress is getting *very* close to 10/10.");
                if (isAboutToLevelUp) {
                    ChatLib.chat("Preparing friendly reminder.");
                } else {
                    ChatLib.chat("However, you are not close to a Bestiary Milestone that rewards Combat XP when unlocked. Train harder, soldier!");
                }
            }
        }
        if ((bestiaryLevelPattern.matcher(event.message.getFormattedText())).find() &&
            event.message.getFormattedText().contains("6") &&
            event.message.getFormattedText().contains("BESTIARY ") &&
            event.message.getUnformattedText().indexOf("MILESTONE") != -1) {
                ChatLib.chat("Ended friendly warning.");
                levelProgress = 0;
                ChatLib.chat("Bestiary level up detected! It was level " + (bestiaryLevel+1) + ". Updating that now...");
                bestiaryLevel++;
                ChatLib.chat("Config's original Bestiary level was " + config.personalBestiaryLevel + ". Writing new value into config...");
                config.personalBestiaryLevel = bestiaryLevel; //write to config
                ChatLib.chat("Config and local `bestiaryLevel` value have been updated to the following: bestiaryLevel = " + bestiaryLevel + ", config.personalBestiaryLevel = " + config.personalBestiaryLevel);
        }
    }

    @SubscribeEvent
    public void sendTheWarning(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().thePlayer == null) {return;}
        if (event.phase != TickEvent.Phase.START){return;}
        if (config.bestiaryMilestoneWarningDeliveryMethod == 0) {return;}
        if (config.bestiaryMilestoneWarningSeconds < 5 || config.bestiaryMilestoneWarningSeconds > 60){config.bestiaryMilestoneWarningSeconds = 5;}
        if (config.bestiaryMilestoneWarningDeliveryMethod != 0 && config.bestiaryMilestoneWarningDeliveryMethod != 1 && config.bestiaryMilestoneWarningDeliveryMethod != 2){config.bestiaryMilestoneWarningDeliveryMethod = 0;}
        if (config.bestiaryMilestoneWarningDuration < 5 || config.bestiaryMilestoneWarningDuration > 120){config.bestiaryMilestoneWarningDuration = 5;}
        if (Minecraft.getMinecraft().thePlayer != null && EssentialAPI.getMinecraftUtil().isHypixel()) {
            if (levelProgress != 9 || !isAboutToLevelUp) {ticks = 0;return;}
            ticks++;
            if (((ticks % (20 * config.bestiaryMilestoneWarningSeconds)) == 0 && (StringUtils.stripControlCodes(Minecraft.getMinecraft().theWorld.getScoreboard().getObjectiveInDisplaySlot(1).getDisplayName()).contains("SKYBLOCK")))) {
                if (config.bestiaryMilestoneWarningDeliveryMethod == 1) {
                    ChatLib.chat(("Here's a friendly reminder that you are very close to reaching Bestiary Milestone " + (config.personalBestiaryLevel+1) + " (currently at Milestone " + (config.personalBestiaryLevel) + " with progress " + (levelProgress) + "/10)."));
                } else if (config.bestiaryMilestoneWarningDeliveryMethod == 2 && (StringUtils.stripControlCodes(Minecraft.getMinecraft().theWorld.getScoreboard().getObjectiveInDisplaySlot(1).getDisplayName()).contains("SKYBLOCK"))) {
                    EssentialAPI.getNotifications().push("§dSynthesis", ("Here's a friendly reminder that you are very close to reaching Bestiary Milestone " + (config.personalBestiaryLevel+1) + " (currently at Milestone " + (config.personalBestiaryLevel) + " with progress " + (levelProgress) + "/10)."), config.bestiaryMilestoneWarningDuration);
                }
                ticks = 0;
            }
        } else {
            ticks = 0;
        }
    }
}
