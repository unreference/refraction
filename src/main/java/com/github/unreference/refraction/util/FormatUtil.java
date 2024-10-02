package com.github.unreference.refraction.util;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class FormatUtil {
  private static final Pattern legacyColorPattern = Pattern.compile("&[a-fA-F0-9klmnor]");
  private static final Pattern hexColorPattern = Pattern.compile("#[a-fA-F0-9]{6}");
  private static final Pattern formatPattern =
      Pattern.compile("&[a-fA-F0-9klmnor]|#[a-fA-F0-9]{6}");

  private static final List<TextColor> prideColors =
      List.of(
          Objects.requireNonNull(TextColor.fromHexString("#E40013")),
          Objects.requireNonNull(TextColor.fromHexString("#FD8D20")),
          Objects.requireNonNull(TextColor.fromHexString("#FEEE2E")),
          Objects.requireNonNull(TextColor.fromHexString("#0C8222")),
          Objects.requireNonNull(TextColor.fromHexString("#114EFC")),
          Objects.requireNonNull(TextColor.fromHexString("#770288")));

  private FormatUtil() {}

  public static Component getFormattedComponent(String message, Object... args) {
    message = String.format(message, args);
    if (isOnlyFormatting(message)) {
      return Component.text(message);
    }

    TextComponent.Builder builder = Component.text();
    formatComponent(message, builder);
    return builder.build();
  }

  public static Component getPrideMessage(String message, Object... args) {
    message = String.format(message, args);

    List<TextColor> adjustedColors = new java.util.ArrayList<>(List.of());

    for (TextColor prideColor : prideColors) {
      adjustedColors.add(adjustHsb(prideColor, 0.0f, 0.0f, 1.0f));
    }

    return getGradientMessage(message, adjustedColors);
  }

  public static Component getGradientMessage(
      String message, List<TextColor> colors, Object... args) {
    message = String.format(message, args);

    TextComponent.Builder builder = Component.text();
    int length = message.length();

    for (int i = 0; i < length; i++) {
      char c = message.charAt(i);

      float proportion = (float) i / (float) length;
      TextColor color = interpolateColors(proportion, colors);

      builder.append(Component.text(c, color));
    }

    return builder.build();
  }

  private static TextColor adjustHsb(
      TextColor color, float hue, float saturation, float brightness) {
    float[] hsb = new float[3];
    Color.RGBtoHSB(color.red(), color.green(), color.blue(), hsb);
    hsb[0] = Math.max(hsb[0], hue);
    hsb[1] = Math.max(hsb[1], saturation);
    hsb[2] = Math.max(hsb[2], brightness);

    int rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
    return TextColor.color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
  }

  private static TextColor interpolateColors(float proportion, List<TextColor> colors) {
    int numColors = colors.size();
    float proportionScaled = proportion * (numColors - 1);

    int index = (int) proportionScaled;
    float interpolation = proportionScaled - index;

    TextColor startColor = colors.get(index);
    TextColor endColor = colors.get(Math.min(index + 1, numColors - 1));

    return blendColors(startColor, endColor, interpolation);
  }

  private static TextColor blendColors(TextColor startColor, TextColor endColor, float proportion) {
    int red = (int) (startColor.red() + proportion * (endColor.red() - startColor.red()));
    int green = (int) (startColor.green() + proportion * (endColor.green() - startColor.green()));
    int blue = (int) (startColor.blue() + proportion * (endColor.blue() - startColor.blue()));

    return TextColor.color(red, green, blue);
  }

  private static void formatComponent(String message, TextComponent.Builder builder) {
    int lastEnd = 0;
    Style currentStyle = Style.empty();

    Matcher matcher = formatPattern.matcher(message);
    while (matcher.find()) {
      builder.append(Component.text(message.substring(lastEnd, matcher.start()), currentStyle));

      String match = matcher.group();
      if (legacyColorPattern.matcher(match).matches()) {
        currentStyle = handleLegacyColor(match, currentStyle);
      } else if (hexColorPattern.matcher(match).matches()) {
        currentStyle = handleHexColor(match, currentStyle);
      }

      lastEnd = matcher.end();
    }

    builder.append(Component.text(message.substring(lastEnd), currentStyle));
  }

  private static Style handleLegacyColor(String match, Style currentStyle) {
    char legacyCode = match.charAt(1);
    if (legacyCode == 'r') {
      return Style.empty().color(NamedTextColor.WHITE);
    } else {
      NamedTextColor namedColor = getColorFromLegacy(legacyCode);
      if (namedColor != null) {
        return currentStyle.color(namedColor);
      } else {
        TextDecoration decoration = getDecorationFromLegacy(legacyCode);
        if (decoration != null) {
          return currentStyle.decorate(decoration);
        }
      }
    }

    return currentStyle;
  }

  private static Style handleHexColor(String match, Style currentStyle) {
    TextColor hexColor = TextColor.fromHexString(match);
    return currentStyle.color(hexColor);
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
