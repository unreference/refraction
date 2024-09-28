package com.github.unreference.refraction.utility;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public class MessageUtility {
    public static Component getMessage(String message, Object... args) {
        Component body = FormatUtility.getFormattedComponent(message, args);
        TextComponent.Builder builder = Component.text();

        builder.append(body).colorIfAbsent(NamedTextColor.WHITE);
        return builder.build();
    }
}