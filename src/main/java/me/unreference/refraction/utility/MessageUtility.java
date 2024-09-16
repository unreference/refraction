package me.unreference.refraction.utility;

import net.kyori.adventure.text.Component;

public class MessageUtility {
    public static Component format(String message, Object... args) {
        return Component.text(String.format(message, args));
    }
}
