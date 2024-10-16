package com.github.unreference.refraction.roulette.spin;

import org.bukkit.entity.Player;

public abstract class AbstractSpin {
  private final String id;
  private final String name;
  private final int duration;
  private final boolean isPositive;

  protected AbstractSpin(String id, String name, int duration, boolean isPositive) {
    this.id = id;
    this.name = name;
    this.duration = duration;
    this.isPositive = isPositive;
  }

  protected AbstractSpin(String id, String name, boolean isPositive) {
    this.id = id;
    this.name = name;
    this.duration = -1;
    this.isPositive = isPositive;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getDuration() {
    return duration;
  }

  public boolean isTimed() {
    return duration != -1;
  }

  public boolean isPositive() {
    return isPositive;
  }

  public abstract void apply(Player player);

  public abstract void remove(Player player);
}
