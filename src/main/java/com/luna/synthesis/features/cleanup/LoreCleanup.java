package com.luna.synthesis.features.cleanup;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.item.ItemSkull;

import java.util.Iterator;

public class LoreCleanup {

    private final Config config = Synthesis.getInstance().getConfig();
    // Some fucking tasty spaghetti

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onItemTooltip(ItemTooltipEvent event) {
        if (Minecraft.getMinecraft().thePlayer.openContainer instanceof ContainerChest) {
            ContainerChest containerChest = (ContainerChest)Minecraft.getMinecraft().thePlayer.openContainer;
            String title = StringUtils.stripControlCodes(containerChest.getLowerChestInventory().getDisplayName().getUnformattedText());
            if (config.cleanupLoreAuctionException && (title.startsWith("Auctions") || title.endsWith("Auction View") || title.endsWith("'s Auctions"))) return;
        }
        ItemStack item = event.itemStack;
        if (!item.hasTagCompound() || !item.getTagCompound().hasKey("ExtraAttributes") || !item.getTagCompound().getCompoundTag("ExtraAttributes").hasKey("id")) return;
        Iterator<String> iterator = event.toolTip.iterator();
        int index = 0;
        boolean inEnchantments = false;
        boolean inAbility = false;
        boolean petHoldingItem = false;
        boolean inPetsMenuAndIsAPet = ((item.getItem() instanceof ItemSkull && Minecraft.getMinecraft().thePlayer.openContainer instanceof ContainerChest && StringUtils.stripControlCodes(((ContainerChest)(Minecraft.getMinecraft().thePlayer.openContainer)).getLowerChestInventory().getDisplayName().getUnformattedText()).endsWith("Pets")) && (item.getDisplayName().matches(".+\\[Lvl \\d+\\] (?<color>§[0-9a-fk-or]).+") || item.getDisplayName().matches(".+\\[\\d+\\] (?<color>§[0-9a-fk-or]).+")));
        boolean isBlackCat = false;
        String previousLine = "";
        while (iterator.hasNext()) {
            // Thank you vanilla, very cool
            String line = iterator.next().replace("§5§o", "");
            // GEAR SCORE, GEMSTONE SLOTS, SOULBOUND, PET STUFF
            if (config.cleanupPetDisplayName && inPetsMenuAndIsAPet && StringUtils.stripControlCodes(item.getDisplayName()).startsWith("[Lvl ") && StringUtils.stripControlCodes(item.getDisplayName()).contains("] ")){
                item.setStackDisplayName(item.getDisplayName().replace("Lvl ", ""));
                if (StringUtils.stripControlCodes(item.getDisplayName()).contains("Black Cat")) {
                    isBlackCat = true;
                }
            }
            /* CONDITIONAL TO SKIP BASE CASE LINES, EDIT WITH CAUTION! -ERY */
            else if (inPetsMenuAndIsAPet && (previousLine.startsWith("§6") && previousLine.contains("Held Item"))) {
                //(inPetsMenuAndIsAPet && (previousLine.startsWith("§6") && previousLine.contains("Held Item")) || line.matches(".*§7[a-z].*") || line.matches(".*§[abcde569].*")) //PLAN: THERE IS NONE. HYPIXEL IS SO INCONSISTENT WITH THEIR LINE SPACING I'M SURPRISED ANY OF THE UNCOMMENTED CODE I WROTE EVEN WORKS. -ERY
                previousLine = line;
            } else if (config.cleanupLorePetType > 0 && config.cleanupLorePetType < 4 && inPetsMenuAndIsAPet && line.startsWith("§8") && (line.endsWith(" Pet") || line.endsWith(" Mount") || line.endsWith(" Morph") || line.endsWith(" gain XP") || line.contains("All Skills"))) {
                previousLine = line;
                if (config.cleanupLorePetType == 3 || line.contains("All Skills"))
                    if (isBlackCat) {
                        event.toolTip.set(index, line.replace(line, "§7Note: Magic Find and Pet Luck increases are not additive, but rather percentage-based."));
                    } else {
                        iterator.remove();
                    }
                else {
                    if (config.cleanupLorePetType == 2 && !line.contains("All Skills")) {
                        event.toolTip.set(index, line.replace("Mining ", "").replace("Combat ", "").replace("Fishing ", "").replace("Farming ", "").replace("Foraging ", "").replace("Enchanting ", "").replace("Alchemy ", "").replace("Gabagool ", ""));
                    }
                    else if (config.cleanupLorePetType == 1) {
                        event.toolTip.set(index, line.replace(" Pet", "").replace(" Mount", "").replace(" Morph", "").replace(", feed to gain XP", ""));
                    }
                    if (isBlackCat) {
                        event.toolTip.set(index, line.replace(line, line + " §7(Magic Find and Pet Luck increases are not additive, but rather percentage-based)"));
                    }
                }
            } else if ((config.cleanupLorePetType == 0) && (isBlackCat)) {
                event.toolTip.set(index, line.replace(line, line + " §7(Magic Find and Pet Luck increases are not additive, but rather percentage-based)"));
            } else if (config.cleanupLorePetPerkName && inPetsMenuAndIsAPet && line.startsWith("§6") && !line.contains("Held Item")) {
                previousLine = line;
                iterator.remove();
            }
            /*  PLAN: SOMEHOW DETECT THE BEGINNING OF A NEW PERK DESCRIPTION AND ADD A HYPHEN TO THE BEGINNING OF IT.
                I ALREADY TRIED THE "inXYZ" CONDITIONAL STRATEGY AND IT WENT TERRIBLY WRONG BECAUSE, AGAIN, HYPIXEL
                PET MENU LORE HAS NO STANDARD CONVENTION WHATSOEVER. -ERY

                else if (config.cleanupLorePetPerkHyphens && inPetsMenuAndIsAPet && line.contains("§7§7") && !line.contains(":") && line.matches(".*§7§7[A-Z].*") && !previousLine.contains("Held Item")) {
                event.toolTip.set(index, ("§e§r§7- " + line));
                previousLine = line;
                continue;
            */
            else if (config.cleanupLorePetHeldItemPrefix && inPetsMenuAndIsAPet && line.contains("Held Item")) {
                previousLine = line;
                event.toolTip.set(index, line.replace("Held Item: ", ""));
                petHoldingItem = true;
            } else if (config.cleanupLorePetMaxLevel && inPetsMenuAndIsAPet && line.contains("MAX LEVEL")) {
                previousLine = line;
                iterator.remove();
            } else if (config.cleanupLorePetClickToSummon && inPetsMenuAndIsAPet && line.contains("Click to summon!")) {
                previousLine = line;
                iterator.remove();
            } else if (config.cleanupLorePetClickToDespawn && inPetsMenuAndIsAPet && line.contains("Click to despawn!")) {
                previousLine = line;
                iterator.remove();
            } else if (config.cleanupLorePetCandiesUsed && inPetsMenuAndIsAPet && line.contains("Pet Candy Used")) {
                previousLine = line;
                // begin the very hacky solution i wrote but it works on the pets that i own so im rolling with this unless anyone has better ideas -ery
                int ifPetHeldItemEnabledOffset = 2;
                if (!config.cleanupLorePetHeldItemPrefix)
                    if (!petHoldingItem)
                        ifPetHeldItemEnabledOffset = 0;
                    else
                        ifPetHeldItemEnabledOffset = 1;
                else if (!petHoldingItem)
                    ifPetHeldItemEnabledOffset = 0;
                // end hacky solution -ery
                String pluralOrSingular = "Candies";
                if (line.contains("1/") && !line.contains("10/"))
                    pluralOrSingular = "Candy";
                event.toolTip.set(index + ifPetHeldItemEnabledOffset, line.replace("/10", "").replace("Pet Candy Used", pluralOrSingular).replace("(", "").replace(")", ""));
            } else if (config.cleanupLorePetEmptyLines && inPetsMenuAndIsAPet && line.equals("")) {
                previousLine = line;
                iterator.remove();
            }
            /*  PLAN: SOMEHOW REMOVE PROGRESS BAR WITHOUT EDGE CASES OF PROGRESS COUNT DUPLICATING ITSELF.
                ATTEMPTED AND FAILED BECAUSE AAAAAAAAAAAAAAAAAA -ERY

                else if (config.cleanupLorePetLevelProgressBar && inPetsMenuAndIsAPet && line.contains("--") && (line.contains("f-") || line.contains("2-"))) {
                event.toolTip.set(index, line.replaceAll("-", ""));
                previousLine = line;
                // iterator.remove();
                continue;
            }
            */
            /*  PLAN: SOMEHOW REMOVE TEXT PRECEDING PERCENTAGE PROGRESS. 
                ATTEMPTED AND FAILED BECAUSE AAAAAAAAAAAAAAAAAA -ERY

                else if (config.cleanupLorePetLevelPercent && inPetsMenuAndIsAPet && line.contains("Progress to Level")) {
                event.toolTip.set(index, line.replaceAll(".*Progress to Level [0-9]{1,3}.*", "% of next Lvl"));
                // previousLine = line;
                continue;
            }
            */
            else if (StringUtils.stripControlCodes(line).startsWith("Gear Score: ") && config.cleanupLoreGearScore) {
                iterator.remove();
            } else if (StringUtils.stripControlCodes(line).startsWith(" [") && config.cleanupLoreGemstoneSlots) {
                iterator.remove();
            } else if ((line.contains("§8§l") && line.contains("*") && line.contains("8Co-op Soulbound")) && config.cleanupLoreCoopSoulbound) {
                iterator.remove();
            } else if ((line.contains("§8§l") && line.contains("*") && line.contains("8Soulbound")) && config.cleanupLoreSoloSoulbound) {
                iterator.remove();
            } else {
                // STAT BONUSES, RECOMBOBULATED TEXT
                if (line.contains(" ")) {
                    for (String s : line.split(" ")) {
                        String replacement = line.replace(s + " ", "").replace(s, "");
                        if (s.startsWith("§8(+") && config.cleanupLoreDungeon) {
                            event.toolTip.set(index, replacement);
                            line = replacement;
                        } else if (s.startsWith("§d(+") && config.cleanupLoreGemstones) {
                            event.toolTip.set(index, replacement);
                            line = replacement;
                        } else if (s.startsWith("§9(+") && config.cleanupLoreReforge) {
                            event.toolTip.set(index, replacement);
                            line = replacement;
                        } else if (s.startsWith("§e(+") && config.cleanupLoreHPB) {
                            event.toolTip.set(index, replacement);
                            line = replacement;
                        } else if (s.contains("§l§ka") && config.cleanupLoreRecombobulatedObfuscated) {
                            event.toolTip.set(index, replacement);
                            line = replacement;
                        }
                    }
                }

                // ENCHANTMENTS
                if (!StringUtils.stripControlCodes(item.getDisplayName()).equals("Enchanted Book")) {
                    if (((line.startsWith("§9") || line.startsWith("§d§l")) && config.cleanupLoreEnchantmentDescriptions && !(inPetsMenuAndIsAPet && item.getItem() instanceof ItemSkull))) inEnchantments = true;
                    if (inEnchantments) {
                        if (!config.cleanupLoreEnchantments && (line.startsWith("§9") || line.startsWith("§d§l"))) {
                            index++;
                            continue;
                        }
                        if (StringUtils.stripControlCodes(line).equals("")) {
                            iterator.remove();
                            continue;
                        }
                        inEnchantments = false;
                        if (config.cleanupLoreEnchantments) {
                            iterator.remove();
                            continue;
                        }
                    }
                }

                // ABILITIES
                if (!line.endsWith("RIGHT CLICK") && !line.endsWith("LEFT CLICK") && !line.equals("§aScroll Abilities:")) {
                    if (config.cleanupLoreReforgeAbility && line.startsWith("§9") && line.endsWith(" Bonus")) inAbility = true;
                    else if (config.cleanupLoreFullSetBonus && line.startsWith("§6Full Set Bonus: ")) inAbility = true;
                    else if (config.cleanupLorePieceBonus && line.startsWith("§6Piece Bonus: ")) inAbility = true;
                } else if (config.cleanupLoreAbilities) {
                    inAbility = true;
                }
                if (inAbility) {
                    iterator.remove();
                    if (line.equals("")) inAbility = false;
                } else {
                    index++;
                }
            }
        }

    }
}
