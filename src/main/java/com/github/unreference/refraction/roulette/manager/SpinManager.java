package com.github.unreference.refraction.roulette.manager;

import com.github.unreference.refraction.roulette.spin.AbstractSpin;
import com.github.unreference.refraction.roulette.spin.BrushWithDeathSpin;
import com.github.unreference.refraction.roulette.spin.SpeedBoostSpin;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpinManager {
  private static SpinManager instance;

  private final List<AbstractSpin> perks = new ArrayList<>();
  private final Random random;

  private SpinManager() {
    this.random = new Random();

    // Positive
    addPerk(new SpeedBoostSpin(60));
    // Negative
    addPerk(new BrushWithDeathSpin());
  }

  public static SpinManager get() {
    if (instance == null) {
      instance = new SpinManager();
    }

    return instance;
  }

  public AbstractSpin getRandomPerk() {
    return perks.get(random.nextInt(perks.size()));
  }

  public List<AbstractSpin> getPerks() {
    return perks;
  }

  private void addPerk(AbstractSpin perk) {
    perks.add(perk);
  }
}
