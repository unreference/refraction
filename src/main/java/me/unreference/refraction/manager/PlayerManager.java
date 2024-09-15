package me.unreference.refraction.manager;

import me.unreference.refraction.data.PlayerData;
import me.unreference.refraction.model.RankModel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

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
            if (playerDataManager.isNew(UUID.fromString(uuid))) {
                RankManager rankManager = RankManager.get();
                PlayerData data = new PlayerData(uuid, name, ip, now, now, rankManager.getId(RankModel.DEFAULT));
                playerDataManager.insertStatic(data);
            }
        } catch (SQLException exception) {
            log(2, "Failed to manage player data [" + name + "]: " + exception.getMessage());
            log(2, Arrays.toString(exception.getStackTrace()));
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
            playerDataManager.updateDynamic(UUID.fromString(uuid), ip, now);
        } catch (SQLException exception) {
            log(2, "Failed to update dynamic data [" + uuid + "]");
            log(2, Arrays.toString(exception.getStackTrace()));
        }

        event.quitMessage(null);
    }
}
