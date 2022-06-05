package com.luna.synthesis.features.cleanup;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Iterator;

public class LoreCleanup {

    private final Config config = Synthesis.getInstance().getConfig();
    // Some fucking tasty spaghetti

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        if (Minecraft.getMinecraft().thePlayer.openContainer instanceof ContainerChest) {
            ContainerChest containerChest = (ContainerChest) Minecraft.getMinecraft().thePlayer.openContainer;
            String title = StringUtils.stripControlCodes(containerChest.getLowerChestInventory().getDisplayName().getUnformattedText());
            if (config.cleanupLoreAuctionException) {
                if (title.startsWith("Auctions") || title.endsWith("Auction View") || title.endsWith("'s Auctions")) {
                    return;
                }
            }
        }
        ItemStack item = event.itemStack;
        if (!item.hasTagCompound() || !item.getTagCompound().hasKey("ExtraAttributes") || !item.getTagCompound().getCompoundTag("ExtraAttributes").hasKey("id")) return;
        Iterator<String> iterator = event.toolTip.iterator();
        int index = 0;
        boolean inEnchantments = false;
        boolean inAbility = false;
        while (iterator.hasNext()) {
            // Thank you vanilla, very cool
            String line = iterator.next().replace("§5§o", "");

            // GEAR SCORE, GEMSTONE SLOTS, SOULBOUND
            if (StringUtils.stripControlCodes(line).startsWith("Gear Score: ") && config.cleanupLoreGearScore) {
                index--;
                iterator.remove();
            } else if (StringUtils.stripControlCodes(line).startsWith(" [") && config.cleanupLoreGemstoneSlots) {
                index--;
                iterator.remove();
            } else if ((line.equals("§8§l* §8Co-op Soulbound §8§l*") || line.equals("§8§l* §8Soulbound §8§l*")) && config.cleanupLoreSoulbound) {
                index--;
                iterator.remove();
            }

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
                if (line.startsWith("§9") || line.startsWith("§d§l")) {
                    if (config.cleanupLoreEnchantmentDescriptions) {
                        inEnchantments = true;
                    }
                }
                if (inEnchantments) {
                    if (!config.cleanupLoreEnchantments && (line.startsWith("§9") || line.startsWith("§d§l"))) {
                        index++;
                        continue;
                    }
                    System.out.println(StringUtils.stripControlCodes(line));
                    if (StringUtils.stripControlCodes(line).equals("")) {
                        inEnchantments = false;
                        if (config.cleanupLoreEnchantments) {
                            index--;
                            iterator.remove();
                        }
                    } else {
                        index--;
                        iterator.remove();
                    }
                }
            }

            // ABILITIES
            if (line.endsWith("RIGHT CLICK") || line.endsWith("LEFT CLICK") || line.equals("§aScroll Abilities:")) {
                if (config.cleanupLoreAbilities) {
                    inAbility = true;
                }
            } else if (line.startsWith("§9") && line.endsWith(" Bonus")) {
                if (config.cleanupLoreReforgeAbility) {
                    inAbility = true;
                }
            } else if (line.startsWith("§6Full Set Bonus: ")) {
                if (config.cleanupLoreFullSetBonus) {
                    inAbility = true;
                }
            } else if (line.startsWith("§6Piece Bonus: ")) {
                if (config.cleanupLorePieceBonus) {
                    inAbility = true;
                }
            }
            if (inAbility) {
                index--;
                iterator.remove();
                if (line.equals("")) {
                    inAbility = false;
                }
            }

            index++;
        }

    }
}
