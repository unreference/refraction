package com.github.unreference.refraction.utility;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtility {
    private static final Pattern legacyPattern = Pattern.compile("&[a-fA-F0-9klmnor]");
    private static final Pattern hexPattern = Pattern.compile("#[a-fA-F0-9]{6}");
    private static final Pattern formatPattern = Pattern.compile("&[a-fA-F0-9klmnor]|#[a-fA-F0-9]{6}");

    public static Component getFormattedComponent(Component component) {
        String plainText = PlainTextComponentSerializer.plainText().serialize(component);
        if (isOnlyFormatting(plainText)) {
            return Component.text(plainText);
        }

        TextComponent.Builder builder = Component.text();
        formatComponent(component, builder);
        return builder.build();
    }

    public static Component getFormattedComponent(String message, Object... args) {
        message = String.format(message, args);
        if (isOnlyFormatting(message)) {
            return Component.text(message);
        }

        TextComponent.Builder builder = Component.text();
        formatComponent(message, builder);
        return builder.build();
    }

    private static void formatComponent(Component component, TextComponent.Builder builder) {
        if (component instanceof TextComponent) {
            String content = ((TextComponent) component).content();
            int lastEnd = 0;
            Style currentStyle = Style.empty();

            Matcher matcher = formatPattern.matcher(content);
            while (matcher.find()) {
                builder.append(Component.text(content.substring(lastEnd, matcher.start()), currentStyle));

                String match = matcher.group();
                if (legacyPattern.matcher(match).matches()) {
                    char legacyCode = match.charAt(1);
                    if (legacyCode == 'r') {
                        currentStyle = Style.empty().color(NamedTextColor.WHITE);
                    } else {
                        NamedTextColor namedColor = getColorFromLegacy(legacyCode);
                        if (namedColor != null) {
                            currentStyle = currentStyle.color(namedColor);
                        } else {
                            TextDecoration decoration = getDecorationFromLegacy(legacyCode);
                            if (decoration != null) {
                                currentStyle = currentStyle.decorate(decoration);
                            }
                        }
                    }
                } else if (hexPattern.matcher(match).matches()) {
                    TextColor hexColor = TextColor.fromHexString(match);
                    currentStyle = currentStyle.color(hexColor);
                }

                lastEnd = matcher.end();
            }

            builder.append(Component.text(content.substring(lastEnd), currentStyle));
        }

        for (Component child : component.children()) {
            formatComponent(child, builder);
        }
    }

    private static void formatComponent(String message, TextComponent.Builder builder) {
        int lastEnd = 0;
        Style currentStyle = Style.empty();

        Matcher matcher = formatPattern.matcher(message);
        while (matcher.find()) {
            builder.append(Component.text(message.substring(lastEnd, matcher.start()), currentStyle));

            String match = matcher.group();
            if (legacyPattern.matcher(match).matches()) {
                char legacyCode = match.charAt(1);
                if (legacyCode == 'r') {
                    currentStyle = Style.empty().color(NamedTextColor.WHITE);
                } else {
                    NamedTextColor namedColor = getColorFromLegacy(legacyCode);
                    if (namedColor != null) {
                        currentStyle = currentStyle.color(namedColor);
                    } else {
                        TextDecoration decoration = getDecorationFromLegacy(legacyCode);
                        if (decoration != null) {
                            currentStyle = currentStyle.decorate(decoration);
                        }
                    }
                }
            } else if (hexPattern.matcher(match).matches()) {
                TextColor hexColor = TextColor.fromHexString(match);
                currentStyle = currentStyle.color(hexColor);
            }

            lastEnd = matcher.end();
        }

        builder.append(Component.text(message.substring(lastEnd), currentStyle));
    }

    private static boolean isOnlyFormatting(String message) {
        String strippedMessage = formatPattern.matcher(message).replaceAll("").trim();
        return strippedMessage.isEmpty();
    }

    private static NamedTextColor getColorFromLegacy(char legacyCode) {
        return switch (legacyCode) {
            case '0' -> NamedTextColor.BLACK;
            case '1' -> NamedTextColor.DARK_BLUE;
            case '2' -> NamedTextColor.DARK_GREEN;
            case '3' -> NamedTextColor.DARK_AQUA;
            case '4' -> NamedTextColor.DARK_RED;
            case '5' -> NamedTextColor.DARK_PURPLE;
            case '6' -> NamedTextColor.GOLD;
            case '7' -> NamedTextColor.GRAY;
            case '8' -> NamedTextColor.DARK_GRAY;
            case '9' -> NamedTextColor.BLUE;
            case 'a' -> NamedTextColor.GREEN;
            case 'b' -> NamedTextColor.AQUA;
            case 'c' -> NamedTextColor.RED;
            case 'd' -> NamedTextColor.LIGHT_PURPLE;
            case 'e' -> NamedTextColor.YELLOW;
            case 'f' -> NamedTextColor.WHITE;
            default -> null;
        };
    }

    private static TextDecoration getDecorationFromLegacy(char legacyCode) {
        return switch (legacyCode) {
            case 'k' -> TextDecoration.OBFUSCATED;
            case 'l' -> TextDecoration.BOLD;
            case 'm' -> TextDecoration.STRIKETHROUGH;
            case 'n' -> TextDecoration.UNDERLINED;
            case 'o' -> TextDecoration.ITALIC;
            default -> null;
        };
    }
}
