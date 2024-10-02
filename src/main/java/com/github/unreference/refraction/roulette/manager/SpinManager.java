package com.github.unreference.refraction.roulette.manager;

import com.github.unreference.refraction.roulette.spin.AbstractSpin;
import com.github.unreference.refraction.roulette.spin.BrushWithDeathSpin;
import com.github.unreference.refraction.roulette.spin.SnailsPaceSpin;
import com.github.unreference.refraction.roulette.spin.SpeedBoostSpin;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    addSpin(new SnailsPaceSpin(60));
  }

  public static SpinManager get() {
    if (instance == null) {
      instance = new SpinManager();
    }

    return instance;
  }

  public static AbstractSpin getSpinFromId(String spinId) {
    for (AbstractSpin spin : SpinManager.get().getSpins()) {
      if (Objects.equals(spinId, spin.getId())) {
        return spin;
      }
    }

    return null;
  }

  public static AbstractSpin getSpinFromName(String spinName) {
    for (AbstractSpin spin : SpinManager.get().getSpins()) {
      if (Objects.equals(spinName, spin.getName())) {
        return spin;
      }
    }

    return null;
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
