package com.github.unreference.refraction.roulette.manager;

import com.github.unreference.refraction.roulette.spin.AbstractSpin;
import com.github.unreference.refraction.roulette.spin.BrushWithDeathSpin;
import com.github.unreference.refraction.roulette.spin.SpeedBoostSpin;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpinManager {
  private static SpinManager instance;

  private final List<AbstractSpin> spins = new ArrayList<>();
  private final Random random;

  private SpinManager() {
    this.random = new Random();

    // Positive
    addSpin(new SpeedBoostSpin(60));
    // Negative
    addSpin(new BrushWithDeathSpin());
  }

  public static SpinManager get() {
    if (instance == null) {
      instance = new SpinManager();
    }

    return instance;
  }

  public AbstractSpin getRandomPerk() {
    return spins.get(random.nextInt(spins.size()));
  }

  public List<AbstractSpin> getSpins() {
    return spins;
  }

  private void addSpin(AbstractSpin perk) {
    spins.add(perk);
  }
}
