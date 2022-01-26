package com.luna.synthesis.mixins;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Container.class)
public class ContainerMixin {

    private final Config config = Synthesis.getInstance().getConfig();

    @ModifyArg(method = {"putStackInSlot", "putStacksInSlots"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Slot;putStack(Lnet/minecraft/item/ItemStack;)V"))
    public ItemStack overridePutStack(ItemStack in) {
        if (!config.utilitiesSuperpairsIDs) return in;
        if (Minecraft.getMinecraft().thePlayer.openContainer instanceof ContainerChest) {
            ContainerChest containerChest = (ContainerChest) Minecraft.getMinecraft().thePlayer.openContainer;
            String title = StringUtils.stripControlCodes(containerChest.getLowerChestInventory().getDisplayName().getUnformattedText());
            if (!title.startsWith("Superpairs (")) return in;
            if (in == null) return in;
            String itemName = StringUtils.stripControlCodes(in.getDisplayName());
            if (itemName.equals("Experiment the Fish")) return in;

            if (!in.getTagCompound().hasKey("ExtraAttributes")) {
                in.getTagCompound().setTag("ExtraAttributes", new NBTTagCompound());
            }
            NBTTagCompound ea = in.getTagCompound().getCompoundTag("ExtraAttributes");

            if (itemName.startsWith("+")) {
                if (itemName.endsWith(" Enchanting Exp")) {
                    ea.setTag("id", new NBTTagString("ENCHANTING_EXPERIENCE"));
                } else if (itemName.endsWith(" XP")) {
                    ea.setTag("id", new NBTTagString("POWER_UP_EXPERIENCE"));
                }
            }

            switch (itemName) {
                case "Gained +3 Clicks":
                    ea.setTag("id", new NBTTagString("POWER_UP_EXTRA_CLICKS"));
                    break;
                case "Instant Find":
                    ea.setTag("id", new NBTTagString("POWER_UP_INSTANT_FIND"));
                    break;
                case "Titanic Experience Bottle":
                    ea.setTag("id", new NBTTagString("TITANIC_EXP_BOTTLE"));
                    break;
                case "Grand Experience Bottle":
                    ea.setTag("id", new NBTTagString("GRAND_EXP_BOTTLE"));
                    break;
                case "Experience Bottle":
                    ea.setTag("id", new NBTTagString("EXP_BOTTLE"));
                    break;
                case "Enchanted Book":
                    ea.setTag("id", new NBTTagString("ENCHANTED_BOOK"));
                    ea.setTag("enchantments", new NBTTagCompound());
                    String enchant = StringUtils.stripControlCodes(in.getSubCompound("display", false).getTagList("Lore", 8).getStringTagAt(2));
                    String enchantName = "";
                    for (int i = 0; i < enchant.split(" ").length - 1; i++) {
                        enchantName += enchant.split(" ")[i];
                        if (i != enchant.split(" ").length - 2) {
                            enchantName += "_";
                        }
                    }
                    enchantName = enchantName.toLowerCase();
                    ea.getCompoundTag("enchantments").setTag(enchantName, new NBTTagInt(in.stackSize));
                    break;
            }
        }
        return in;
    }
}
