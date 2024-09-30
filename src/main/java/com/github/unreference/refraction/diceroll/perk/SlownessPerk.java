package com.github.unreference.refraction.diceroll.perk;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SlownessPerk implements Perk {
    @Override
    public void apply(Player player, int duration) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 0));
    }
}
