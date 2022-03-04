package com.luna.synthesis.features.utilities;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class BetterWitherImpactPerspective {

    private final Config config = Synthesis.getInstance().getConfig();

    // InputEvent has to be the absolute worst event in forge
    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (!config.utilitiesWitherImpactPerspective) return;
        if (!Keyboard.getEventKeyState()) return;
        if (Keyboard.getEventKey() == Minecraft.getMinecraft().gameSettings.keyBindTogglePerspective.getKeyCode()) {
            if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) {
                if (config.utilitiesWitherImpactPerspectiveGlobal) {
                    Minecraft.getMinecraft().gameSettings.thirdPersonView = 0;
                } else {
                    ItemStack item = Minecraft.getMinecraft().thePlayer.getHeldItem();
                    if (item == null) return;
                    if (item.hasTagCompound()) {
                        if (item.getTagCompound().hasKey("ExtraAttributes")) {
                            if (item.getTagCompound().getCompoundTag("ExtraAttributes").hasKey("ability_scroll")) {
                                if (item.getTagCompound().getCompoundTag("ExtraAttributes").getTagList("ability_scroll", 8).tagCount() == 3) {
                                    Minecraft.getMinecraft().gameSettings.thirdPersonView = 0;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
