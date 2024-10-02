package com.github.unreference.refraction.roulette.spin;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedBoostSpin extends AbstractSpin {
  public SpeedBoostSpin(int duration) {
    super("speedBoost", "Speed Boost", duration, true);
  }

  @Override
  public void apply(Player player) {
    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration * 20, 0));
  }

  @Override
  public void remove(Player player) {
    player.removePotionEffect(PotionEffectType.SPEED);
  }
}
