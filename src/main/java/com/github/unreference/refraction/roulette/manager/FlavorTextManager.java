package com.github.unreference.refraction.roulette.manager;

import com.github.unreference.refraction.roulette.spin.AbstractSpin;
import java.util.List;
import java.util.Random;

public class FlavorTextManager {
  private static FlavorTextManager instance;

  private final Random random = new Random();

  private final List<String> positive =
      List.of(
          "&b%s &7called upon the spirits of chance and was granted a &b%s&7%s!",
          "&b%s &7spun the ethereal wheel of fate and emerged with a &b%s&7%s!",
          "&b%s &7invoked the magic of destiny and received a &b%s&7%s!",
          "&b%s &7gazed into the mystical void, and the cosmos blessed them with a &b%s&7%s!",
          "&b%s &7unleashed the forces of luck and found a &b%s&7%s awaiting them!",
          "&b%s &7enthralled the stars above, resulting in a delightful &b%s&7%s!",
          "&b%s &7dared to dream, and the universe rewarded them with a &b%s&7%s!",
          "&b%s &7whispered a wish to the winds and was gifted a &b%s&7%s!",
          "&b%s &7summoned fortune from beyond the veil, landing a &b%s&7%s!",
          "&b%s &7bargained with fate and struck a deal for a &b%s&7%s!");

  private final List<String> negative =
      List.of(
          "&c%s &7challenged fate but met with a twist; they ended up with a &c%s&7%s!",
          "&c%s &7spun the wheel of misfortune and the outcome was a &c%s&7%s!",
          "&c%s &7sought fortune in the void but found only a &c%s&7%s instead!",
          "&c%s &7tempted the spirits of luck and received a harsh &c%s&7%s!",
          "&c%s &7called upon the winds of fate, but they whispered back a &c%s&7%s!",
          "&c%s &7asked the universe for favor, but got a &c%s&7%s in return!",
          "&c%s &7reached for the stars, only to grasp a &c%s&7%s!",
          "&c%s &7rolled the cosmic dice but landed on a &c%s&7%s instead!",
          "&c%s &7navigated the paths of chance, but fate took a wrong turn to a &c%s&7%s!",
          "&c%s &7sought the treasure of luck but ended with a mere &c%s&7%s!");

  private FlavorTextManager() {}

  public static FlavorTextManager get() {
    if (instance == null) {
      instance = new FlavorTextManager();
    }

    return instance;
  }

  public String getPositiveText(String playerName, String spinName) {
    AbstractSpin spin = SpinManager.getSpinFromName(spinName);
    String start = positive.get(random.nextInt(positive.size()));
    String end =
        String.format(
            spin.isTimed() ? " for %s%s seconds&7" : "",
            spin.isPositive() ? "&b" : "&c",
            spin.getDuration());
    return String.format(start, playerName, spinName, end);
  }

  public String getNegativeText(String playerName, String spinName) {
    AbstractSpin spin = SpinManager.getSpinFromName(spinName);
    String begin = negative.get(random.nextInt(negative.size()));
    String end =
        String.format(
            spin.isTimed() ? " for %s%s seconds&7" : "",
            spin.isPositive() ? "&b" : "&c",
            spin.getDuration());
    return String.format(begin, playerName, spinName, end);
  }
}
