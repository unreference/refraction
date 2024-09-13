package me.unreference.refraction.managers;

import me.unreference.refraction.Refraction;
import me.unreference.refraction.data.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class PlayerManager implements Listener {
    private final PlayerDataManager playerDataManager;

    public PlayerManager(DatabaseManager databaseManager) {
        this.playerDataManager = PlayerDataManager.get(databaseManager);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        String name = event.getPlayer().getName();
        LocalDateTime now = LocalDateTime.now();

        try {
            if (playerDataManager.isNew(uuid)) {
                PlayerData data = new PlayerData(uuid, name, now, now);
                playerDataManager.insertStatic(data);
            }

            playerDataManager.updateDynamic(uuid, now);

        } catch (SQLException exception) {
            Refraction.getPlugin().getLogger().severe("Failed to manage player data [" + name + "]: " + exception.getMessage());
            event.getPlayer().kick();
        }

        event.joinMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        LocalDateTime now = LocalDateTime.now();

        try {
            playerDataManager.updateDynamic(uuid, now);
        } catch (SQLException exception) {
            String name = event.getPlayer().getName();
            Refraction.getPlugin().getLogger().severe("Failed to update dynamic data [" + name + "] " + exception.getMessage());
        }

        event.quitMessage(null);
    }
}
