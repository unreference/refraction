package com.github.unreference.refraction.utility;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public class MessageUtility {
    private MessageUtility() {}

    public static Component getPrefixedMessage(String prefix, String message, Object... args) {
        Component bodyPrefix = FormatUtility.getPrideMessage(prefix + ">");
        Component body = FormatUtility.getFormattedComponent(message, args);
        TextComponent.Builder builder = Component.text();

        builder.append(bodyPrefix);
        builder.appendSpace();
        builder.append(body).colorIfAbsent(NamedTextColor.GRAY);
        return builder.build();
    }

    public static Component getMessage(String message, Object... args) {
        Component body = FormatUtility.getFormattedComponent(message, args);
        TextComponent.Builder builder = Component.text();

        builder.append(body).colorIfAbsent(NamedTextColor.WHITE);
        return builder.build();
    }
}