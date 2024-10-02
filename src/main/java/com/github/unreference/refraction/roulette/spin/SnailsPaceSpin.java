package com.github.unreference.refraction.roulette.spin;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SnailsPaceSpin extends AbstractSpin {
  public SnailsPaceSpin(int duration) {
    super("snail_pace", "Snail's Pace", duration, false);
  }

  @Override
  public void apply(Player player) {
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.SLOWNESS, duration * 20, 4, false, false, false));
  }

  @Override
  public void remove(Player player) {
    player.removePotionEffect(PotionEffectType.SLOWNESS);
  }
}
