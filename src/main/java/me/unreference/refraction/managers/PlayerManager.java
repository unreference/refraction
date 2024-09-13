package me.unreference.refraction.managers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerManager implements Listener {

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        event.joinMessage(null);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        event.quitMessage(null);
    }
}
