package com.github.unreference.refraction.diceroll.perk;

import org.bukkit.entity.Player;

public interface Perk {
    void apply(Player player, int duration);
}
