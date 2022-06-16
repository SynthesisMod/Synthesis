package com.luna.synthesis.features.utilities;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.Color;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.lwjgl.opengl.GL11;

/**
 * <pre>
 * OccupancyOverlay
 *
 * A Java class by Erymanthus / RayDeeUx for the Hypixel Skyblock-specific
 * Minecraft Forge 1.8.9 mod named "Synthesis".
 *
 * Ever wondered how full a lobby is? Ever wanted a visual indicator of that metric?
 * Here you go!
 *
 * Originally a feature from SkyblockReinvented by theCudster,
 * which adapted it from an earlier Hypixel Skyblock mo
 * named "Nate's Secret Mod" by Nat3z.
 *
 * Except this time it doesn't just use the same two or three predefined colors!
 *
 * We truly are living in the future.
 * </pre>
 * @author Erymanthus(#5074) / [u/]RayDeeUx
 */

public class OccupancyOverlay {

    private final Config config = Synthesis.getInstance().getConfig();
    private final Pattern playerCapacity = Pattern.compile("(?<num>[1-9]+)/(?<denom>[0-9]+).*");
    private boolean hasFriend, hasGuildmate, alreadyConnected, couldNotConnect = false;
    /* alreadyConnected and couldNotConnect are for debugging purposes in case
    * i ever get back to refining the regex solution */
    private float r, g, b = 0F;
    private Float currentCapacity = 0F;
    private Float maxCapacity = 1F; //prevent ArithmeticExceptions
    private int x, y = 0;
    private String menuName = "";
    private String hubName = "";
    private List<String> itemLore;
    private List<Slot> slots;
    
    @SubscribeEvent
    public void onGuiScreen(GuiScreenEvent.BackgroundDrawnEvent e) {
        if (config.occupancyOverlay) {
            if (Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().currentScreen instanceof GuiChest) {
                menuName = StringUtils.stripControlCodes((((ContainerChest)((GuiChest)(Minecraft.getMinecraft().currentScreen)).inventorySlots).getLowerChestInventory().getDisplayName().getUnformattedText()));
                if (menuName.toLowerCase().contains("skyblock hub") || menuName.toLowerCase().contains("dungeon hub") || menuName.toLowerCase().startsWith("visit")) {
                    slots = ((GuiChest)(Minecraft.getMinecraft().currentScreen)).inventorySlots.inventorySlots;
                    for (Slot s : slots) {
    
                        /* PREVENT CACHING */
                        currentCapacity = 0F;
                        maxCapacity = 1F; //prevent ArithmeticExceptions
                        x = y = 0;
                        hubName = "";
                        currentCapacity = 0F;
                        maxCapacity = 1F;
                        hubName = "";
                        hasFriend = hasGuildmate = alreadyConnected = couldNotConnect = false;
    
                        if (s.getStack() != null && s.getStack().hasDisplayName() && !(s.getStack().getDisplayName().toLowerCase().contains(" skyblock hub")) && (s.getStack().getDisplayName().toLowerCase().contains("skyblock hub") || s.getStack().getDisplayName().toLowerCase().contains("dungeon hub") || s.getStack().getDisplayName().toLowerCase().contains("visit player "))) {
                            hubName = StringUtils.stripControlCodes(s.getStack().getDisplayName());
                            itemLore = s.getStack().getTooltip(Minecraft.getMinecraft().thePlayer, false);
                            if (itemLore != null) {
                                for (String line : itemLore) {
                                    if (line.toLowerCase().contains("online friend")) {
                                        hasFriend = true;
                                    } else if (line.toLowerCase().contains("online guild")) {
                                        hasGuildmate = true;
                                    } else if (line != null && (line.toLowerCase().contains("players: "))) {
                                        String apparentlyIHaveToDoThisNowToPreventFalseNegativeSearches = StringUtils.stripControlCodes(line);
                                        apparentlyIHaveToDoThisNowToPreventFalseNegativeSearches = apparentlyIHaveToDoThisNowToPreventFalseNegativeSearches.replace("Players: ", "");
                                        Matcher playerCapacityMatcher = playerCapacity.matcher(apparentlyIHaveToDoThisNowToPreventFalseNegativeSearches);
                                        if (playerCapacityMatcher.find()) {
                                            currentCapacity = Float.parseFloat(playerCapacityMatcher.group(1));
                                            maxCapacity = Float.parseFloat(playerCapacityMatcher.group(2));
                                        } else {
                                            //"I am responsible. I... Oh, crap. My backpack's gone." - [MCU] Spider-Man, Spider-Man: Homecoming (2017)
                                            System.out.println("[Synthesis — DEBUG] For some reason, Synthesis couldn't find the player capacity using regex. Here's the relevant info: Inside the menu named " + menuName + " at item slot " + s.getSlotIndex() + "'s item lore line `" + line + "` at itemLore.indexOf(line) " + itemLore.indexOf(line) +  " with hubName " + hubName + ". hasFriend was " + hasFriend + ", hasGuildmate was " + hasGuildmate + ", couldNotConnect was " + couldNotConnect + ", and alreadyConnected was " + alreadyConnected + ". -Erymanthus#5074");
                                            System.out.println("[Synthesis — DEBUG] Synthesis is now attempting a failsafe solution using Java's replace() and substring() methods. -Erymanthus#5074");
                                            String apparentlyJavaRegexNeverWorksConsistently = apparentlyIHaveToDoThisNowToPreventFalseNegativeSearches.replace("Players: ", "");
                                            currentCapacity = Float.parseFloat(apparentlyJavaRegexNeverWorksConsistently.substring(0, apparentlyJavaRegexNeverWorksConsistently.indexOf("/") + 1).replace("/", ""));
                                            maxCapacity = Float.parseFloat(apparentlyJavaRegexNeverWorksConsistently.substring(apparentlyJavaRegexNeverWorksConsistently.indexOf("/"), apparentlyJavaRegexNeverWorksConsistently.length()).replace("/", ""));
                                            //So why does Java regex never work 100% of the time? Beats me, but the user shouldn't be punished for it!
                                        }
                                        if (currentCapacity >= maxCapacity) {
                                            r = 255F;
                                            g = b = 0F;
                                        } else if (config.occupancyOverlayFriendAndGuildHighlght && hasFriend && hasGuildmate) {
                                            r = b = 255F;
                                            g = 85F;
                                        } else if (config.occupancyOverlayFriendHighlght && hasFriend) {
                                            r = g = 85F;
                                            b = 255F;
                                        } else if (config.occupancyOverlayGuildHighlght && hasGuildmate) {
                                            r = b = 0F;
                                            g = 170F;
                                        } else {
                                            //"You know what's cooler than magic? Math!" - [MCU] Spider-Man, Spider-Man: No Way Home (2021)
                                            r = ((currentCapacity / maxCapacity) * 255F);
                                            g = (((maxCapacity - currentCapacity) / maxCapacity) * 255F);
                                            b = 0F;
                                            //Instead of predefined RGB values, why not factor current occupancy in to make some degree of yellow?
                                            /*Disclaimer: usually not that noticable with skyblock hub selector npc because all hubs are usually close
                                            to full, try dungeon hub selector npc for better effect. i swear the code works as intended*/
                                        }
                                    } else if (line != null && (line.toLowerCase().contains("full") || line.toLowerCase().contains("offline") || line.toLowerCase().contains("doesn't support guests yet") || line.toLowerCase().contains("island disallows guests"))) {
                                        r = 255;
                                        g = b = 0F;
                                        couldNotConnect = true;
                                    } else if (line != null && (line.toLowerCase().contains("already "))) {
                                        if (config.occupancyOverlayAlreadyConnectedHighlght) {
                                            r = 85F;
                                            g = b = 255F;
                                        }
                                        alreadyConnected = true;
                                    }
                                }
                                Color bgColor = new Color(((int)(r)), ((int)(g)), ((int)(b)));
                                /**
                                 * The next six lines of code were taken from Danker's Skyblock Mod under the GPL 3.0 license.
                                 * https://github.com/bowser0000/SkyblockMod/blob/master/LICENSE
                                 * @author bowser0000
                                 */
                                //They are necessary to render the colors within the inventory menu.
                                x = (((new ScaledResolution(Minecraft.getMinecraft())).getScaledWidth() - 176) / 2) + s.xDisplayPosition;
                                y = (((new ScaledResolution(Minecraft.getMinecraft())).getScaledHeight() - 222) / 2) + s.yDisplayPosition;
                                if (slots.size() != 90) y += (6 - (slots.size() - 36) / 9) * 9;
                                GL11.glTranslated(0, 0, 1);
                                Gui.drawRect(x, y, x + 16, y + 16, bgColor.getRGB());
                                GL11.glTranslated(0, 0, -1);
                            }
                        }
                    }
                    return;
                }
            }
        }
    }
}
