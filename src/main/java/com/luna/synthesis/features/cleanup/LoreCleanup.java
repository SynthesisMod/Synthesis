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
                    /**
                     * 
                     * Suggestion #84 by minhperry#2803
                     * "fix" black cat description
                     * 
                     */
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
                        //suggestion #84 implementation
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
                        //suggestion #84 implementation
                        event.toolTip.set(index, line.replace(line, line + " §7(Magic Find and Pet Luck increases are not additive, but rather percentage-based)"));
                    }
                }
            } else if ((config.cleanupLorePetType == 0) && (isBlackCat)) {
                //suggestion #84 implementation
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
        if ((item.getSubCompound("ExtraAttributes", false) != null) && !inPetsMenuAndIsAPet) {
            /*
             * Suggestion #69 by aHuman#6726
             * option to show uuid of an item in it's lore
             * 
             */
            // UUID — THIS MUST BE OUTSIDE THE WHILE LOOP TO PREVENT TOOLTIP CO-MODIFICATION CRASHES
            if (item.getSubCompound("ExtraAttributes", false).hasKey("uuid") && config.utilitiesLoreItemUUID) {
                event.toolTip.add("§d[Synthesis]§7 Item UUID: §l" + item.getSubCompound("ExtraAttributes", false).getString("uuid"));
            }

            // ITEM ORIGIN — MUST BE OUTSIDE WHILE LOOP TO PREVENT CRASHES
            if (config.utilitiesLoreOriginTag && item.getSubCompound("ExtraAttributes", false).getString("originTag") != null && item.getSubCompound("ExtraAttributes", false).getString("originTag") != "") {
                event.toolTip.add("§d[Synthesis]§7 Item origin: §l" + item.getSubCompound("ExtraAttributes", false).getString("originTag"));
            }
            /* 
             * 
             * Suggestion #99 by FledGuy#3686
             * old master stars
             * 
             */
            if ((item.getDisplayName().contains("§6✪§c"))) {
                //§c\u272a
                String masterPlan = item.getDisplayName()
                                    .replace("§c➎","")
                                    .replace("§c➍","")
                                    .replace("§c➌","")
                                    .replace("§c➋","")
                                    .replace("§c➊","");
                int numStarsOne = item.getSubCompound("ExtraAttributes", false).getInteger("upgrade_level");
                int numStarsTwo = item.getSubCompound("ExtraAttributes", false).getInteger("dungeon_item_level");
                int numMasters = (((numStarsTwo < numStarsOne ? numStarsOne : numStarsTwo)) - 5);
                String maxStars = "§6✪§6✪§6✪§6✪§6✪";
                if (numMasters == 1) {
                    masterPlan = masterPlan.replace(maxStars,"§c✪§6✪§6✪§6✪§6✪");
                } else if (numMasters == 2) {
                    masterPlan = masterPlan.replace(maxStars,"§c✪§c✪§6✪§6✪§6✪");
                } else if (numMasters == 3) {
                    masterPlan = masterPlan.replace(maxStars,"§c✪§c✪§c✪§6✪§6✪");
                } else if (numMasters == 4) {
                    masterPlan = masterPlan.replace(maxStars,"§c✪§c✪§c✪§c✪§6✪");
                } else if (numMasters == 5) {
                    masterPlan = masterPlan.replace(maxStars,"§c✪§c✪§c✪§c✪§c✪");
                }
                item.setStackDisplayName(masterPlan);
            }
        }
    }
}

/**
{
    id: "minecraft:iron_sword",
    Count: 1b,
    tag: {
        ench: [],
        Unbreakable: 1b,
        HideFlags: 254,
        display: {
            Lore: ["§7Gear Score: §d1191 §8(3213)", "§7Damage: §c+345 §e(+30) §8(+1,003.4)", "§7Strength: §c+250 §e(+30) §6[+5] §9(+50) §8(+789.6)", "§7Crit Damage: §c+100% §8(+336%)", "§7Bonus Attack Speed: §c+7% §9(+7%) §8(+10.5%)", "§7Intelligence: §a+628 §9(+125) §d(+60) §8(+1,855.6)", "§7Ferocity: §a+38 §8(+52.5)", " §6[§b✎§6] §6[§b⚔§6]", "", "§d§l§d§lUltimate Wise V§9, §9Cleave V§9, §9Critical VII", "§9Cubism V§9, §9Ender Slayer VII§9, §9Execute V", "§9Experience IV§9, §9First Strike IV§9, §9Giant Killer VII", "§9Impaling III§9, §9Lethality VI§9, §9Looting IV", "§9Luck VII§9, §9Mana Steal III§9, §9Scavenger IV", "§9Smite VII§9, §9Thunderlord VII§9, §9Vampirism VI", "§9Venomous V§9, §9Vicious V", "", "§b◆ Music Rune III", "", "§7Deals +§c50% §7damage to", "§7Withers. Grants §c+1 §c❁ Damage", "§c§7and §a+2 §b✎ Intelligence", "§b§7per §cCatacombs §7level.", "", "§aScroll Abilities:", "§6Ability: Wither Impact §e§lRIGHT CLICK", "§7Teleport §a10 blocks§7 ahead of", "§7you. Then implode dealing", "§7§c27,235 §7damage to nearby", "§7enemies. Also applies the wither", "§7shield scroll ability reducing", "§7damage taken and granting an", "§7absorption shield for §e5", "§e§7seconds.", "§8Mana Cost: §3150", "", "§d§l§ka§r §d§l§d§lMYTHIC DUNGEON SWORD §d§l§ka", "§8§m-----------------", "§7Seller: §b[MVP§f+§b] Harry663", "§7Buy it now: §61,600,000,000 coins", "", "§7Ends in: §e12d", "", "§eClick to inspect!"],
            Name: "§f§f§dHeroic Hyperion §c✪§c✪§c✪§c✪§c✪"
        },
        ExtraAttributes: {
            rarity_upgrades: 1,
            hot_potato_count: 15,
            gems: {
                COMBAT_0: "PERFECT",
                unlocked_slots: ["SAPPHIRE_0", "COMBAT_0"],
                COMBAT_0_gem: "SAPPHIRE",
                SAPPHIRE_0: "PERFECT"
            },
            runes: {
                MUSIC: 3
            },
            modifier: "heroic",
            art_of_war_count: 1,
            upgrade_level: 10,
            id: "HYPERION",
            enchantments: {
                impaling: 3,
                luck: 7,
                vicious: 5,
                critical: 7,
                cleave: 5,
                smite: 7,
                looting: 4,
                scavenger: 4,
                ender_slayer: 7,
                vampirism: 6,
                experience: 4,
                execute: 5,
                giant_killer: 7,
                mana_steal: 3,
                first_strike: 4,
                venomous: 5,
                thunderlord: 7,
                ultimate_wise: 5,
                cubism: 5,
                lethality: 6
            },
            uuid: "969d4dcd-1848-425e-a287-c03eda6aea78",
            ability_scroll: ["WITHER_SHIELD_SCROLL", "IMPLOSION_SCROLL", "SHADOW_WARP_SCROLL"],
            timestamp: "6/30/22 11:30 AM"
        }
    },
    Damage: 0s
}
 */
/*
{
    id: "minecraft:golden_sword",
    Count: 1b,
    tag: {
        ench: [],
        Unbreakable: 1b,
        HideFlags: 254,
        display: {
            Lore: ["§7Gear Score: §d1124 §8(3014)", "§7Damage: §c+250 §e(+30) §8(+772.8)", "§7Strength: §c+344 §e(+30) §6[+5] §9(+199) §8(+1,122.24)", "§7Crit Chance: §c+5.5% §8(+7.5%)", "§7Crit Damage: §c+133% §8(+436.8%)", "§7Intelligence: §a+330 §8(+1,008)", "§7Ferocity: §a+5 §8(+7.5)", "", "§d§l§d§lSoul Eater V§9, §9Cleave VI§9, §9Critical VII", "§9Cubism VI§9, §9Dragon Hunter V§9, §9Ender Slayer VII", "§9Experience IV§9, §9Fire Aspect III§9, §9Giant Killer VII", "§9Impaling III§9, §9Lethality VI§9, §9Looting V", "§9Luck VII§9, §9Mana Steal III§9, §9Prosecute VI", "§9Scavenger V§9, §9Sharpness VII§9, §9Smoldering V", "§9Thunderlord VII§9, §9Triple-Strike V§9, §9Vampirism VI", "§9Venomous VI§9, §9Vicious V", "", "§6Ability: Burning Souls §e§lRIGHT CLICK", "§7Gain §a+300❈ Defense §7for", "§7§a5s§7 and cast vortex of flames", "§7towards enemies, dealing up to", "§7§c58,558.5§7 over §a5§7 seconds.", "§8Mana Cost: §3400", "§8Cooldown: §a5s", "", "§9Withered Bonus", "§7Grants §a+1 §c❁ Strength §7per", "§7§cCatacombs §7level.", "", "§d§l§ka§r §d§l§d§lMYTHIC DUNGEON SWORD §d§l§ka", "§8§m-----------------", "§7Seller: §b[MVP§3+§b] NecronsWife", "§7Buy it now: §62,147,483,647 coins", "", "§7Ends in: §e9d", "", "§eClick to inspect!"],
            Name: "§f§f§dWithered Pigman Sword §c✪§c✪§c✪§c✪§c✪"
        },
        ExtraAttributes: {
            rarity_upgrades: 1,
            hot_potato_count: 15,
            dungeon_item: 1b,
            modifier: "withered",
            art_of_war_count: 1,
            upgrade_level: 10,
            id: "PIGMAN_SWORD",
            enchantments: {
                impaling: 3,
                luck: 7,
                critical: 7,
                vicious: 5,
                cleave: 6,
                smoldering: 5,
                looting: 5,
                ultimate_soul_eater: 5,
                scavenger: 5,
                ender_slayer: 7,
                experience: 4,
                fire_aspect: 3,
                vampirism: 6,
                giant_killer: 7,
                mana_steal: 3,
                dragon_hunter: 5,
                venomous: 6,
                triple_strike: 5,
                thunderlord: 7,
                sharpness: 7,
                cubism: 6,
                lethality: 6,
                PROSECUTE: 6
            },
            uuid: "4dffd4e6-6325-4850-b9fc-4cbe1674a731",
            timestamp: "7/3/22 4:15 AM"
        }
    },
    Damage: 0s
}
 */

 /*
{
    id: "minecraft:iron_sword",
    Count: 1b,
    tag: {
        ench: [],
        Unbreakable: 1b,
        HideFlags: 254,
        display: {
            Lore: ["§7Gear Score: §d913 §8(2175)", "§7Damage: §c+340.6 §e(+20) §8(+974.4)", "§7Strength: §c+220.6 §e(+20) §9(+15) §8(+615.8)", "§7Crit Damage: §c+70% §8(+228.2%)", "§7Bonus Attack Speed: §c+20% §9(+20%) §8(+28%)", "§7Intelligence: §a+64.8 §8(+195.6)", "§7Ferocity: §a+79.8 §9(+15) §8(+105)", " §8[§8❁§8] §8[§8⚔§8]", "", "§d§l§d§lUltimate Wise V§9, §9Cleave V§9, §9Critical VI", "§9Cubism V§9, §9Ender Slayer VI§9, §9Execute V", "§9Experience IV§9, §9Fire Aspect II§9, §9First Strike IV", "§9Giant Killer V§9, §9Impaling III§9, §9Lethality VI", "§9Looting IV§9, §9Luck VI§9, §9Scavenger IV", "§9Smite VII§9, §9Syphon IV§9, §9Thunderlord VI", "§9Vampirism VI§9, §9Venomous V", "", "§9◆ Lightning Rune III", "", "§7Deals +§c50% §7damage to", "§7Withers. Grants §c+1 §c❁ Damage", "§c§7and §a+1 §c❁ Strength §7per", "§7§cCatacombs §7level.", "", "§aScroll Abilities:", "§6Ability: Wither Impact §e§lRIGHT CLICK", "§7Teleport §a10 blocks§7 ahead of", "§7you. Then implode dealing", "§7§c27,235 §7damage to nearby", "§7enemies. Also applies the wither", "§7shield scroll ability reducing", "§7damage taken and granting an", "§7absorption shield for §e5", "§e§7seconds.", "§8Mana Cost: §3150", "", "§d§l§ka§r §d§l§d§lMYTHIC DUNGEON SWORD §d§l§ka", "§8§m-----------------", "§7Seller: §6[MVP§a++§6] DqddySloth", "§7Buy it now: §61,200,000,000 coins", "", "§7Ends in: §e28h", "", "§eClick to inspect!"],
            Name: "§f§f§dDirty Valkyrie §6✪§6✪§6✪§6✪"
        },
        ExtraAttributes: {
            rarity_upgrades: 1,
            hot_potato_count: 10,
            runes: {
                LIGHTNING: 3
            },
            modifier: "dirty",
            upgrade_level: 4,
            id: "VALKYRIE",
            enchantments: {
                impaling: 3,
                luck: 6,
                critical: 6,
                cleave: 5,
                smite: 7,
                looting: 4,
                syphon: 4,
                scavenger: 4,
                ender_slayer: 6,
                fire_aspect: 2,
                experience: 4,
                vampirism: 6,
                execute: 5,
                giant_killer: 5,
                first_strike: 4,
                venomous: 5,
                thunderlord: 6,
                ultimate_wise: 5,
                cubism: 5,
                lethality: 6
            },
            uuid: "174b0495-24be-4d89-94e6-6de5d6a37d7f",
            ability_scroll: ["IMPLOSION_SCROLL", "SHADOW_WARP_SCROLL", "WITHER_SHIELD_SCROLL"],
            timestamp: "5/22/22 5:58 AM"
        }
    },
    Damage: 0s
}
  */

  /*
   * {
    id: "minecraft:iron_sword",
    Count: 1b,
    tag: {
        ench: [],
        Unbreakable: 1b,
        HideFlags: 254,
        display: {
            Lore: ["§7Gear Score: §d1612 §8(4281)", "§7Damage: §c+356 §e(+30) §8(+1,037)", "§7Strength: §c+200 §e(+30) §6[+5] §8(+621.6)", "§7Crit Chance: §c+10% §9(+10%) §8(+15%)", "§7Crit Damage: §c+180% §9(+110%) §8(+604.8%)", "§7Defense: §a+333 §8(+898)", "§7Intelligence: §a+67 §d(+12) §8(+208.32)", "§7True Defense: §a+22 §8(+30)", "§7Ferocity: §a+33 §8(+45)", " §8[§7☤§8] §9[§b⚔§9]", "", "§d§l§d§lUltimate Wise V§9, §9Cleave V§9, §9Critical VI", "§9Cubism V§9, §9Ender Slayer VI§9, §9Execute V", "§9Experience IV§9, §9Fire Aspect III§9, §9First Strike IV", "§9Giant Killer VI§9, §9Impaling III§9, §9Knockback II", "§9Lethality VI§9, §9Looting IV§9, §9Luck VI", "§9Scavenger IV§9, §9Smite VII§9, §9Syphon IV", "§9Thunderlord VI§9, §9Vampirism VI§9, §9Venomous V", "", "§b◆ Music Rune II", "", "§7Deals +§c50% §7damage to", "§7Withers. Grants §c+1 §c❁ Damage", "§c§7and §a+2 §a❈ Defense §7per", "§7§cCatacombs §7level.", "", "§aScroll Abilities:", "§6Ability: Wither Impact §e§lRIGHT CLICK", "§7Teleport §a10 blocks§7 ahead of", "§7you. Then implode dealing", "§7§c27,235 §7damage to nearby", "§7enemies. Also applies the wither", "§7shield scroll ability reducing", "§7damage taken and granting an", "§7absorption shield for §e5", "§e§7seconds.", "§8Mana Cost: §3150", "", "§fKills: §646,576", "", "§9Suspicious Bonus", "§7Increases weapon damage by", "§7§c+15§7.", "", "§d§l§ka§r §d§l§d§lMYTHIC DUNGEON SWORD §d§l§ka", "§8§m-----------------", "§7Seller: §b[MVP§2+§b] OnegExists", "§7Buy it now: §61,300,000,000 coins", "", "§7Ends in: §e12d", "", "§eClick to inspect!"],
            Name: "§f§f§dSuspicious Astraea §6✪§6✪§6✪§6✪§6✪§c➊"
        },
        ExtraAttributes: {
            rarity_upgrades: 1,
            stats_book: 46576,
            runes: {
                MUSIC: 2
            },
            modifier: "suspicious",
            art_of_war_count: 1,
            upgrade_level: 6,
            enchantments: {
                impaling: 3,
                luck: 6,
                critical: 6,
                cleave: 5,
                looting: 4,
                syphon: 4,
                smite: 7,
                ender_slayer: 6,
                scavenger: 4,
                knockback: 2,
                fire_aspect: 3,
                vampirism: 6,
                experience: 4,
                execute: 5,
                giant_killer: 6,
                venomous: 5,
                first_strike: 4,
                thunderlord: 6,
                ultimate_wise: 5,
                cubism: 5,
                lethality: 6
            },
            uuid: "a36609f7-4dfd-4c50-b8d3-a33cf33708ec",
            ability_scroll: ["IMPLOSION_SCROLL", "WITHER_SHIELD_SCROLL", "SHADOW_WARP_SCROLL"],
            hot_potato_count: 15,
            gems: {
                COMBAT_0: "FINE",
                unlocked_slots: ["COMBAT_0", "COMBAT_1", "DEFENSIVE_0"],
                COMBAT_1_gem: "SAPPHIRE",
                COMBAT_1: "PERFECT",
                COMBAT_0_gem: "SAPPHIRE"
            },
            id: "ASTRAEA",
            timestamp: "6/8/22 10:59 PM"
        }
    },
    Damage: 0s
}
   */

   /*
    * {
    id: "minecraft:iron_sword",
    Count: 1b,
    tag: {
        ench: [],
        Unbreakable: 1b,
        HideFlags: 254,
        display: {
            Lore: ["§7Gear Score: §d1133 §8(2973)", "§7Damage: §c+345 §e(+30) §8(+1,003.4)", "§7Strength: §c+200 §e(+30) §6[+5] §8(+621.6)", "§7Crit Chance: §c+10% §9(+10%) §8(+15%)", "§7Crit Damage: §c+180% §9(+110%) §8(+604.8%)", "§7Intelligence: §a+443 §8(+1,234)", "§7Ferocity: §a+38 §8(+52.5)", " §8[§7✎§8] §8[§7⚔§8]", "", "§d§l§d§lUltimate Wise V§9, §9Critical VI§9, §9Cubism V", "§9Dragon Hunter V§9, §9Ender Slayer VI§9, §9Execute V", "§9Experience IV§9, §9Fire Aspect II§9, §9First Strike IV", "§9Giant Killer VI§9, §9Impaling III§9, §9Lethality VI", "§9Looting IV§9, §9Luck VI§9, §9Mana Steal III", "§9Scavenger IV§9, §9Smite VII§9, §9Thunderlord VI", "§9Vampirism VI§9, §9Venomous V§9, §9Vicious V", "", "§d◆ Hearts Rune III", "", "§7Deals +§c50% §7damage to", "§7Withers. Grants §c+1 §c❁ Damage", "§c§7and §a+2 §b✎ Intelligence", "§b§7per §cCatacombs §7level.", "", "§aScroll Abilities:", "§6Ability: Wither Impact §e§lRIGHT CLICK", "§7Teleport §a10 blocks§7 ahead of", "§7you. Then implode dealing", "§7§c27,235 §7damage to nearby", "§7enemies. Also applies the wither", "§7shield scroll ability reducing", "§7damage taken and granting an", "§7absorption shield for §e5", "§e§7seconds.", "§8Mana Cost: §3150", "", "§fKills: §611,865", "", "§9Suspicious Bonus", "§7Increases weapon damage by", "§7§c+15§7.", "", "§d§l§ka§r §d§l§d§lMYTHIC DUNGEON SWORD §d§l§ka", "§8§m-----------------", "§7Seller: §a[VIP] Jelly0nToast", "§7Buy it now: §61,300,000,000 coins", "", "§7Ends in: §e12d", "", "§eClick to inspect!"],
            Name: "§f§f§dSuspicious Hyperion §6✪§6✪§6✪§6✪§6✪§c➋"
        },
        ExtraAttributes: {
            rarity_upgrades: 1,
            stats_book: 11865,
            runes: {
                HEARTS: 3
            },
            modifier: "suspicious",
            art_of_war_count: 1,
            dungeon_item_level: 7,
            originTag: "VALKYRIE_UPGRADE",
            enchantments: {
                impaling: 3,
                luck: 6,
                critical: 6,
                vicious: 5,
                looting: 4,
                smite: 7,
                ender_slayer: 6,
                scavenger: 4,
                telekinesis: 1,
                vampirism: 6,
                experience: 4,
                fire_aspect: 2,
                execute: 5,
                giant_killer: 6,
                mana_steal: 3,
                first_strike: 4,
                venomous: 5,
                dragon_hunter: 5,
                thunderlord: 6,
                ultimate_wise: 5,
                cubism: 5,
                lethality: 6
            },
            uuid: "6cc73ae0-6dad-4346-8be6-cbc3834992d1",
            ability_scroll: ["WITHER_SHIELD_SCROLL", "SHADOW_WARP_SCROLL", "IMPLOSION_SCROLL"],
            hot_potato_count: 15,
            gems: {
            },
            id: "HYPERION",
            timestamp: "3/25/21 6:13 PM"
        }
    },
    Damage: 0s
}
    */

    /*
     * {
    id: "minecraft:bow",
    Count: 1b,
    tag: {
        ench: [],
        Unbreakable: 1b,
        HideFlags: 254,
        display: {
            Lore: ["§7Gear Score: §d1019 §8(2594)", "§7Damage: §c+371 §e(+30) §8(+1,142.4)", "§7Strength: §c+110 §e(+30) §6[+5] §9(+20) §8(+352.8)", "§7Crit Chance: §c+65% §9(+60%) §8(+97.5%)", "§7Crit Damage: §c+280% §8(+856.8%)", "§7Bonus Attack Speed: §c+44% §8(+60%)", "§7Ferocity: §a+5 §8(+7.5)", "", "§d§l§d§lSoul Eater V§9, §9Chance V§9, §9Cubism VI", "§9Dragon Hunter V§9, §9Dragon Tracer V§9, §9Impaling III", "§9Infinite Quiver X§9, §9Overload V§9, §9Piercing I", "§9Power VII§9, §9Snipe IV§9, §9Vicious V", "", "§5◆ End Rune III", "", "§6Shortbow: Instantly shoots!", "§7Shoots §b3 §7arrows at once.", "§7Can damage endermen.", "", "§cDivides your §9☣ Crit Chance §cby 4!", "", "§6Ability: Salvation §e§lRIGHT CLICK", "§7Can be casted after landing §63 §7hits.", "§7§7Shoot a beam, penetrating up", "§7to §e5 §7foes and dealing §c2x", "§c§7the damage an arrow would.", "§7The beam always crits.", "§8Soulflow Cost: §3§31⸎", "§8Cooldown: §a2s", "", "§7§4☠ §cRequires §5Enderman Slayer 7.", "§d§l§ka§r §d§l§d§lMYTHIC DUNGEON BOW §d§l§ka", "§8§m-----------------", "§7Seller: §b[MVP§2+§b] AspectoftheSteve", "§7Buy it now: §61,050,000,000 coins", "", "§7Ends in: §e12d", "", "§eClick to inspect!"],
            Name: "§f§f§dHasty Terminator §6✪§6✪§6✪§6✪§6✪§c➍"
        },
        ExtraAttributes: {
            rarity_upgrades: 1,
            hot_potato_count: 15,
            runes: {
                DRAGON: 3
            },
            modifier: "hasty",
            art_of_war_count: 1,
            dungeon_item_level: 9,
            originTag: "QUICK_CRAFTING",
            id: "TERMINATOR",
            enchantments: {
                impaling: 3,
                chance: 5,
                vicious: 5,
                piercing: 1,
                ultimate_soul_eater: 5,
                snipe: 4,
                telekinesis: 1,
                overload: 5,
                dragon_hunter: 5,
                infinite_quiver: 10,
                power: 7,
                aiming: 5,
                cubism: 6
            },
            uuid: "bc33cef1-484f-4b11-b6b8-a07c78520ece",
            timestamp: "10/23/21 11:10 PM"
        }
    },
    Damage: 0s
}
     */

     /*
      * 
      */