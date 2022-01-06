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
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
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
                    drawStringAsStackSize(level, xPosition, yPosition);
                    ci.cancel();
                }
            } else if (config.utilitiesBestiaryGlance && (title.equals("Bestiary") || title.contains(" ➜ "))) {
                if (StringUtils.stripControlCodes(stack.getDisplayName()).startsWith("Bestiary Milestone ")) {
                    String level = StringUtils.stripControlCodes(stack.getDisplayName()).replace("Bestiary Milestone ", "");
                    drawStringAsStackSize(level, xPosition, yPosition);
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
                    drawStringAsStackSize(progress.get(), xPosition, yPosition);
                    ci.cancel();
                }
            }
        }
    }

    private void drawStringAsStackSize(String text, int xPosition, int yPosition) {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(text, (float)(xPosition + 19 - 2 - Minecraft.getMinecraft().fontRendererObj.getStringWidth(text)), (float)(yPosition + 6 + 3), 16777215);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        System.out.println("");
    }
}
