package com.github.unreference.refraction.roulette.spin;

import org.bukkit.entity.Player;

public class BrushWithDeathSpin extends AbstractSpin {
  public BrushWithDeathSpin() {
    super("brushWithDeath", "Brush with Death", -1, false);
  }

  @Override
  public void apply(Player player) {
    player.getWorld().strikeLightningEffect(player.getLocation());
    player.setFoodLevel(1);
    player.damage(player.getHealth() - 1.0);
  }

  @Override
  public void remove(Player player) {
    // This method blank on purpose
  }
}
