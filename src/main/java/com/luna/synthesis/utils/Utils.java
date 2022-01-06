package com.luna.synthesis.utils;

public class Utils {

    // Copied and adapted from Patcher
    public static boolean isDivider(String message) {
        if (message.length() < 5) {
            return false;
        } else {
            for (int i = 0; i < message.length(); i++) {
                char c = message.charAt(i);
                if (c != '-' && c != '=' && c != '\u25AC') {
                    return false;
                }
            }
        }
        return true;
    }
}
