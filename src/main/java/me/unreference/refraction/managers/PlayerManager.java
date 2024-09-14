package me.unreference.refraction.managers;

import me.unreference.refraction.data.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;

import static me.unreference.refraction.Refraction.log;

public class PlayerManager implements Listener {
    private final PlayerDataManager playerDataManager;

    public PlayerManager(DatabaseManager databaseManager) {
        this.playerDataManager = PlayerDataManager.get(databaseManager);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        String name = event.getPlayer().getName();
        String ip = event.getPlayer().getAddress().getAddress().getHostAddress();
        LocalDateTime now = LocalDateTime.now();

        try {
            if (playerDataManager.isNew(uuid)) {
                PlayerData data = new PlayerData(uuid, name, ip, now, now);
                playerDataManager.insertStatic(data);
            }
        } catch (SQLException exception) {
            log(2, "PlayerDataManager", "Failed to manage player data [" + name + "]");
            log(2, "PlayerDataManager", Arrays.toString(exception.getStackTrace()));
            event.getPlayer().kick();
        }

        event.joinMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        String ip = event.getPlayer().getAddress().getAddress().getHostAddress();
        LocalDateTime now = LocalDateTime.now();

        try {
            playerDataManager.updateDynamic(uuid, ip, now);
        } catch (SQLException exception) {
            String name = event.getPlayer().getName();
            log(2, "PlayerDataManager", "Failed to update dynamic data [" + name + "]");
            log(2, "PlayerDataManager", Arrays.toString(exception.getStackTrace()));
        }

        event.quitMessage(null);
    }
}
