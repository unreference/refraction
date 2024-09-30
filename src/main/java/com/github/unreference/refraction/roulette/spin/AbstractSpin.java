package com.github.unreference.refraction.roulette.spin;

import org.bukkit.entity.Player;

public abstract class AbstractSpin {
    public final String id;
    public final String name;
    public final int duration;
    public final boolean isPositive;

    public AbstractSpin(String id, String name, int duration, boolean isPositive) {
        this.id = id;
        this.name = name;
        this.duration = duration;
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

    public boolean isPositive() {
        return isPositive;
    }

    public abstract void apply(Player player);

    public abstract void remove(Player player);
}
