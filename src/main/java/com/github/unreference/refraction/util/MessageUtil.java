package com.github.unreference.refraction.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public class MessageUtil {
    private MessageUtil() {
    }

    public static Component getPrefixedMessage(String prefix, String message, Object... args) {
        Component bodyPrefix = FormatUtil.getPrideMessage(prefix + ">");
        Component body = FormatUtil.getFormattedComponent(message, args);
        TextComponent.Builder builder = Component.text();

        builder.append(bodyPrefix);
        builder.appendSpace();
        builder.append(body).colorIfAbsent(NamedTextColor.GRAY);
        return builder.build();
    }

    public static Component getMessage(String message, Object... args) {
        Component body = FormatUtil.getFormattedComponent(message, args);
        TextComponent.Builder builder = Component.text();

        builder.append(body).colorIfAbsent(NamedTextColor.WHITE);
        return builder.build();
    }
}