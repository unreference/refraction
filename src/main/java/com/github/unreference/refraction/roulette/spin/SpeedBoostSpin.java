package com.github.unreference.refraction.roulette.spin;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedBoostSpin extends AbstractSpin {
  public SpeedBoostSpin(int duration) {
    super("speed_boost", "Speed Boost", duration, true);
  }

  @Override
  public void apply(Player player) {
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.SPEED, getDuration() * 20, 0, false, false, false));
  }

  @Override
  public void remove(Player player) {
    player.removePotionEffect(PotionEffectType.SPEED);
  }
}
