package com.github.unreference.refraction.listener;

import com.github.unreference.refraction.diceroll.perk.SlownessPerk;
import com.github.unreference.refraction.diceroll.perk.SpeedPerk;
import com.github.unreference.refraction.event.DiceRollEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DiceRollListener implements Listener {
    @EventHandler
    public void onDiceRoll(DiceRollEvent event) {
        Player player = event.getPlayer();
        int result = event.getResult();

        if (result == 6) {
            new SpeedPerk().apply(player, 60 * 20);
        } else if (result == 4) {
            new SlownessPerk().apply(player, 60 * 20);
        }
    }
}
