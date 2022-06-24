package com.luna.synthesis.features.utilities;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import com.luna.synthesis.utils.ChatLib;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.item.ItemSkull;

import java.lang.Thread;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class BestiaryWarning {

    private final Config config = Synthesis.getInstance().getConfig();
    private final Pattern bestiaryFamilyPattern = Pattern.compile(".*3.*BESTIARY.*");
    private final Pattern bestiaryLevelPattern = Pattern.compile(".*6.*BESTIARY.*MILESTONE.*");

    private boolean isAboutToLevelUp = false;
    private int bestiaryLevel = 0;
    private int levelProgress = 0;
    private int secondsPassed = 0;

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        if (!(Minecraft.getMinecraft().thePlayer.openContainer instanceof ContainerChest) || 
            !(StringUtils.stripControlCodes(event.itemStack.getDisplayName()).startsWith("Bestiary Milestone ")) ||
            !(event.itemStack.getItem() instanceof ItemSkull)) return;
        bestiaryLevel = Integer.parseInt(StringUtils.stripControlCodes(event.itemStack.getDisplayName()).replace("Bestiary Milestone ", ""));
        if (bestiaryLevel % 2 == 0 && bestiaryLevel != 4 && bestiaryLevel != 6 && bestiaryLevel != 8) { //source: https://hypixel-skyblock.fandom.com/wiki/Bestiary
            isAboutToLevelUp = true;
            ChatLib.chat("Combat XP-rewarding Bestiary level found via conventional method! It was level " + bestiaryLevel);
        }
        ChatLib.chat("Bestiary level found! It was level " + bestiaryLevel);
        ChatLib.chat("Config's original Bestiary level was " + config.personalBestiaryLevel + ". Writing new value into config...");
        config.personalBestiaryLevel = bestiaryLevel; //write to config
        ChatLib.chat("Combat XP-rewarding Bestiary level found and config has been updated.");
        for (String s : event.itemStack.getTooltip(Minecraft.getMinecraft().thePlayer, false)) {
            if (s.endsWith("e10")) {
                levelProgress = Integer.parseInt(StringUtils.stripControlCodes(s).replaceAll("-", "").replaceAll(" ", "").replaceAll("/10", ""));
                ChatLib.chat("Level progress found! It was " + levelProgress + "/10.");
            }
            if (!isAboutToLevelUp && (s.contains("Combat"))) { //failsafe method to cover rare edge cases
                isAboutToLevelUp = true;
                ChatLib.chat("Combat XP-rewarding Bestiary level found via failsafe method! It was level " + bestiaryLevel);
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
                    sendTheWarning(config.bestiaryMilestoneWarningSeconds);
                } else {
                    ChatLib.chat("However, you are not close to a Bestiary Milestone that rewards Combat XP when unlocked. Train harder, soldier!");
                }
            }
        }
        if ((bestiaryLevelPattern.matcher(event.message.getFormattedText())).find() &&
            event.message.getFormattedText().contains("3") &&
            event.message.getFormattedText().contains("BESTIARY ") &&
            event.message.getUnformattedText().indexOf("MILESTONE") == -1) {
            sendTheWarning(-1);
            ChatLib.chat("Ended friendly warning.");
            levelProgress = 0;
            ChatLib.chat("Bestiary level up detected! It was level " + bestiaryLevel++ + ". Updating that now...");
            bestiaryLevel++;
            ChatLib.chat("Config's original Bestiary level was " + config.personalBestiaryLevel + ". Writing new value into config...");
            config.personalBestiaryLevel = bestiaryLevel; //write to config
            ChatLib.chat("Config and local `bestiaryLevel` value have been updated to the following: bestiaryLevel = " + bestiaryLevel + ", config.personalBestiaryLevel = " + config.personalBestiaryLevel);
        }
    }

    public void sendTheWarning(int seconds) {
        if (seconds <= 4 || levelProgress != -9 || !isAboutToLevelUp) return;
        while (levelProgress == 9 && isAboutToLevelUp && seconds >= 5) {
            try {
                Thread.sleep(seconds * 1000);
                ChatLib.chat("Friendly reminder that you are very close to reaching Bestiary Milestone " + (bestiaryLevel++) + " (currently at Milestone " + bestiaryLevel + " with progress " + levelProgress + "/10).");
            } catch (Exception e) {
                ChatLib.chat("Something went wrong while sending a reminder about your Bestiary Milestone progress. See logs. Consider this your friendly reminder instead.");
                e.printStackTrace();
            }
        }
    }
}
