package com.luna.synthesis.features.utilities;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.StringUtils;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;

public class CountdownCalculator {
    private final Config config = Synthesis.getInstance().getConfig();
    private final Pattern countdownPattern = Pattern.compile("(?<randomBS>.*): (?:(?<days>\\d+)d)? ?(?:(?<hours>\\d+)h)? ?(?:(?<minutes>\\d+)m)? ?(?:(?<seconds>\\d+)s)?");
    private final DateTimeFormatter format12h = DateTimeFormatter.ofPattern("EEEE, MMM d h:mm:ss a z");
    private final DateTimeFormatter format24h = DateTimeFormatter.ofPattern("EEEE, MMM d HH:mm:ss z");
    private Dictionary<String, String> countdownTypes = new Hashtable<String, String>();
    private boolean dictionaryFilled = false;
    private EntityPlayerSP mcgmtp = Minecraft.getMinecraft().thePlayer;
    private Matcher countdownMatcher;

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent e) {
        if (e.itemStack == null || mcgmtp == null || mcgmtp.openContainer == null) return;
        fillDict();
        List<String> tooltip = e.toolTip;
        long days = 0, hours = 0, minutes = 0, seconds = 0, totalSecs = 0;
        String prefix = "", prefixToAdd = "";
        boolean foundPreviousCountdown = false;
        for (int i = 0; i < tooltip.size(); i++) {
            String tooltipLine = tooltip.get(i);
            if (!(tooltipLine.contains("§e")) && !(tooltipLine.contains("§6"))) continue;
            countdownMatcher = countdownPattern.matcher(StringUtils.stripControlCodes(tooltipLine));
            if (!(countdownMatcher.find())) continue;
            if (tooltipLine.contains("Event lasts for") && !foundPreviousCountdown) continue;
            days = Long.parseLong(countdownMatcher.group("days"));
            hours = Long.parseLong(countdownMatcher.group("hours"));
            minutes = Long.parseLong(countdownMatcher.group("minutes"));
            seconds = Long.parseLong(countdownMatcher.group("seconds"));
            prefix = countdownMatcher.group("randomBS");
            prefixToAdd = countdownTypes.get(prefix);
            totalSecs = (days * 86400L) + (hours * 3600L) + (minutes * 60L) + (seconds);
        }
    }

    private void fillDict() {
        if (dictionaryFilled) return;
        countdownTypes.put("Starting in", "Starts at");
        countdownTypes.put("Starts in", "Starts at");
        countdownTypes.put("Interest in", "Interest at");
        countdownTypes.put("Until interest", "Interest at");
        countdownTypes.put("Ends in", "Ends at");
        countdownTypes.put("Remaining", "Ends at");
        countdownTypes.put("Duration", "Finishes at");
        countdownTypes.put("Time left", "Ends at");
        countdownTypes.put("(", "Starts at");
        countdownTypes.put("another ", "Your CH pass expires at");
        countdownTypes.put("Event lasts for", "Ends at");
        dictionaryFilled = true;
    }
}
