package com.luna.synthesis.utils;

import net.minecraftforge.fml.common.Loader;

import java.lang.reflect.Field;

public class ReflectionUtils {

    private static final String PATCHER_CONFIG_NAME = "club.sk1er.patcher.config.PatcherConfig";
    private static Field patcherChatField = null;

    public static void onInit() {
        if (Loader.isModLoaded("patcher")) {
            handlePatcherReflection();
        }
    }

    private static void handlePatcherReflection() {
        try {
            Class<?> cls = Class.forName(PATCHER_CONFIG_NAME);
            Field f = cls.getField("transparentChatInputField");
            if (f.getType() == boolean.class) {
                patcherChatField = f;
            }
        } catch (ReflectiveOperationException e) {
            // Why is there not a logger
            System.out.println("Unable to execute Patcher Reflection");
        }
    }

    public static boolean getPatcherChatField(){
        if (patcherChatField == null) return false;
        try {
            return patcherChatField.getBoolean(null);
        } catch (ReflectiveOperationException ignored) {}
        return false;
    }


}
