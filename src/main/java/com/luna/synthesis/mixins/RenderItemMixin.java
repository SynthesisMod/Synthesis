package com.luna.synthesis.mixins;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import com.luna.synthesis.managers.BackpackManager;
import com.luna.synthesis.utils.MixinUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(RenderItem.class)
public class RenderItemMixin {

    private final Config config = Synthesis.getInstance().getConfig();

    // Might make these two onto a forge event because they're very cool
    @Redirect(method = "renderItemIntoGUI", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemModelMesher;getItemModel(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/client/resources/model/IBakedModel;"))
    public IBakedModel iBakedModel(ItemModelMesher imm, ItemStack stack) {
        String name = StringUtils.stripControlCodes(stack.getDisplayName());
        if (config.utilitiesBackpackRetexturing && name.startsWith("Backpack Slot ")) {
            String number = name.replaceAll("Backpack Slot ", "");
            BackpackManager bpm = Synthesis.getInstance().getBackpackManager();
            if (!bpm.getName(number).equals("")) {
                return MixinUtils.getItemModel(imm, Item.getByNameOrId(bpm.getName(number)), bpm.getMeta(number));
            }
        }
        return imm.getItemModel(stack);
    }

    @Inject(method = "renderItemOverlayIntoGUI", at = @At(value = "JUMP", ordinal = 1, shift = At.Shift.BEFORE), cancellable = true)
    public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text, CallbackInfo ci) {
        if (Minecraft.getMinecraft().thePlayer.openContainer instanceof ContainerChest) {
            if (stack.getItem() == Item.getItemFromBlock(Blocks.glass_pane)) return;
            ContainerChest containerChest = (ContainerChest) Minecraft.getMinecraft().thePlayer.openContainer;
            String title = StringUtils.stripControlCodes(containerChest.getLowerChestInventory().getDisplayName().getUnformattedText());
            if (config.utilitiesPerkLevelDisplay && title.equals("Heart of the Mountain")) {
                if (stack.getItem() == Items.emerald || (config.utilitiesMaxPerkLevelDisplay && stack.getItem() == Items.diamond)) {
                    String level = StringUtils.stripControlCodes(stack.getTooltip(Minecraft.getMinecraft().thePlayer, true).get(1));
                    if (level.startsWith("Level ")) {
                        level = level.replaceAll("Level ", "");
                    } else {
                        return;
                    }
                    if (level.contains("/")) {
                        level = level.split("/")[0];
                    }
                    if (level.equals("1")) return;
                    drawAsStackSize(level, xPosition, yPosition);
                    ci.cancel();
                }
            } else if (config.utilitiesBestiaryGlance && (title.equals("Bestiary") || title.contains(" ➜ "))) {
                String dName = stack.getDisplayName();
                if (StringUtils.stripControlCodes(dName).startsWith("Bestiary Milestone ")) {
                    String level = StringUtils.stripControlCodes(dName).replace("Bestiary Milestone ", "");
                    drawAsStackSize(level, xPosition, yPosition);
                    ci.cancel();
                } else if (stack == containerChest.getInventory().get(52)) {
                    ItemStack bestiary = containerChest.getInventory().get(51);
                    if (bestiary == null || !bestiary.getDisplayName().contains("Bestiary Milestone ")) return;
                    AtomicReference<String> progress = new AtomicReference<>("");
                    Iterator<String> it = bestiary.getTooltip(Minecraft.getMinecraft().thePlayer, false).iterator();
                    it.forEachRemaining(s -> {
                        String line = StringUtils.stripControlCodes(s);
                        if (line.endsWith("/10")) {
                            progress.set(line.split(" ")[line.split(" ").length - 1].replace("/10", ""));
                        }
                    });
                    if (progress.get().equals("")) return;
                    drawAsStackSize(progress.get(), xPosition, yPosition);
                    ci.cancel();
                }
            } else if (config.utilitiesShowCollectionStackSize && (title.contains(" Collection"))) {
                String[] splitName = StringUtils.stripControlCodes(stack.getDisplayName()).split(" ");
                if (splitName.length < 1) return;
                String romanNumeral = splitName[(splitName.length - 1)];
                if (!((romanNumeral.contains("I") || romanNumeral.contains("V") || romanNumeral.contains("X") || romanNumeral.contains("L") || romanNumeral.contains("C") || romanNumeral.contains("D") || romanNumeral.contains("M")))) return;
                if ((stack.getDisplayName().contains(" Minion"))) return;
                int finalResult = 0;
                //BRUTEFORCE CONVERSION.
                //I SURE AS FUCK GOT NO TIME TO WRITE THAT ROMAN TO ARABIC NUMERAL CONVERSION FUNCTION.
                //S_A_D ISTG IF YOU SCREENSHOT THIS I WILL COMMIT COPIOUS AMOUNTS OF VIDEO GAME CRIMES -ERY
                if (romanNumeral.equals("I")) finalResult = 1; else if (romanNumeral.equals("II")) finalResult = 2; else if (romanNumeral.equals("III")) finalResult = 3; else if (romanNumeral.equals("IV")) finalResult = 4; else if (romanNumeral.equals("V")) finalResult = 5; else if (romanNumeral.equals("VI")) finalResult = 6; else if (romanNumeral.equals("VII")) finalResult = 7; else if (romanNumeral.equals("VIII")) finalResult = 8; else if (romanNumeral.equals("IX")) finalResult = 9; else if (romanNumeral.equals("X")) finalResult = 10; else if (romanNumeral.equals("XI")) finalResult = 11; else if (romanNumeral.equals("XII")) finalResult = 12; else if (romanNumeral.equals("XIII")) finalResult = 13; else if (romanNumeral.equals("XIV")) finalResult = 14; else if (romanNumeral.equals("XV")) finalResult = 15; else if (romanNumeral.equals("XVI")) finalResult = 16; else if (romanNumeral.equals("XVII")) finalResult = 17; else if (romanNumeral.equals("XVIII")) finalResult = 18; else if (romanNumeral.equals("XIX")) finalResult = 19; else if (romanNumeral.equals("XX")) finalResult = 20; else return;
                drawAsStackSize(finalResult, xPosition, yPosition);
                ci.cancel();
            } else if (config.utilitiesShowCraftedMinionsStackSize && title.contains("Crafted Minions")) {
                if (!(stack.getItem() == Items.skull)) return;
                if (!(stack.getDisplayName().endsWith(" Minion"))) return;
                int numTiers = 0;
                List<String> itemLore = stack.getTooltip(Minecraft.getMinecraft().thePlayer, false);
                for (String s : itemLore) if (s.contains("a")) numTiers++; else if (s.contains("c")) break;
                drawAsStackSize(numTiers, xPosition, yPosition);
                ci.cancel();
            } else if (config.utilitiesShowSkillStackSize && (title.equals("Your Skills") || title.equals("Dungeoneering"))) {
                String[] splitName = StringUtils.stripControlCodes(stack.getDisplayName()).split(" ");
                if (splitName.length < 2) return;
                String skillNumeral = splitName[(splitName.length - 1)];
                char c = skillNumeral.charAt(0);
                if (c < '0' || c > '9') return; //HUGE SHOUTOUT TO Jonas K from StackOverflow for this: https://stackoverflow.com/a/237204
                drawAsStackSize(skillNumeral, xPosition, yPosition);
                ci.cancel();
            }
        }
        if ((config.utilitiesShowSkillAverageStackSize != 0)) {
            if (!(stack.getDisplayName().contains("Your Skill"))) return;
            String skillAvg = "";
            List<String> itemLore = stack.getTooltip(Minecraft.getMinecraft().thePlayer, false);
            for (String s : itemLore) {
                if ((StringUtils.stripControlCodes(s)).contains(" Skill Avg")) {
                    String[] temp = s.split(" ");
                    if (temp.length < 1) break; //prevent crash
                    skillAvg = StringUtils.stripControlCodes(temp[0]);
                }
            }
            if (skillAvg == "") return;
            if (config.utilitiesShowSkillAverageStackSize == 1) drawAsStackSize(Math.round(Float.valueOf(skillAvg)), xPosition, yPosition);
            else if (config.utilitiesShowSkillAverageStackSize == 2) drawAsStackSize(skillAvg, xPosition, yPosition);
            ci.cancel();
        }
        if (config.utilitiesWishingCompassUsesLeft) {
            if (stack != null && stack.getDisplayName().contains("Wishing Compass")) {
                NBTTagCompound tag = stack.getTagCompound();
                if (tag.hasKey("ExtraAttributes")) {
                    if (tag.getCompoundTag("ExtraAttributes").hasKey("wishing_compass_uses")) {
                        drawAsStackSize((3 - tag.getCompoundTag("ExtraAttributes").getInteger("wishing_compass_uses")), xPosition, yPosition);
                    } else {
                        if (config.utilitiesWishingCompassAlwaysUsesLeft) {
                            drawAsStackSize(3, xPosition, yPosition);
                        }
                    }
                }
            }
        }
        if (config.utilitiesShowNYCakeStackSize) {
            if (stack != null && stack.getDisplayName().contains("cNew Year Cake (Year") && stack.getDisplayName().contains(")")) {
                NBTTagCompound tag = stack.getTagCompound();
                if (tag.hasKey("ExtraAttributes") && tag.getCompoundTag("ExtraAttributes").hasKey("new_years_cake")) {
                    drawAsStackSize((tag.getCompoundTag("ExtraAttributes").getInteger("new_years_cake")), xPosition, yPosition);
                }
            }
        }
        if (config.utilitiesShowSpookyPieStackSize < 3 && config.utilitiesShowSpookyPieStackSize > 0) {
            if (stack != null && stack.getDisplayName().contains("Spooky Pie")) {
                NBTTagCompound tag = stack.getTagCompound();
                if (tag.hasKey("ExtraAttributes")) {
                    List<String> itemLore = stack.getTooltip(Minecraft.getMinecraft().thePlayer, false);
                    for (String s : itemLore) {
                        if (s.contains("Obtained during the")) {
                            if (config.utilitiesShowSpookyPieStackSize == 2) {
                                drawAsStackSize((Integer.parseInt((tag.getCompoundTag("ExtraAttributes").getString("event").replace("spooky_festival_", ""))) + 1), xPosition, yPosition);
                            } else if (config.utilitiesShowSpookyPieStackSize == 1) {
                                drawAsStackSize((tag.getCompoundTag("ExtraAttributes").getInteger("new_years_cake") + 1), xPosition, yPosition);
                            }
                        }
                    }
                }
            }
        }
    }

    private void drawAsStackSize(String text, int xPosition, int yPosition) {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(text, (float)(xPosition + 19 - 2 - Minecraft.getMinecraft().fontRendererObj.getStringWidth(text)), (float)(yPosition + 6 + 3), 16777215);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }

    private void drawAsStackSize(int integer, int xPosition, int yPosition) {
        drawAsStackSize(Integer.toString(integer), xPosition, yPosition);
    }
}
