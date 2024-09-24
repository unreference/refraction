package com.github.unreference.refraction.manager;

import com.github.unreference.refraction.Refraction;
import com.github.unreference.refraction.data.PlayerData;
import com.github.unreference.refraction.event.RankChangeEvent;
import com.github.unreference.refraction.model.RankModel;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

public class PlayerManager implements Listener {
    private final PlayerDataManager playerDataManager;
    private final String autoOpPermission = "refraction.server.auto-operator";

    public PlayerManager(DatabaseManager databaseManager) {
        this.playerDataManager = PlayerDataManager.get(databaseManager);
        RankModel.ADMIN.grantPermission(autoOpPermission, true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        LocalDateTime now = LocalDateTime.now();

        try {
            if (playerDataManager.isNew(uuid)) {
                RankManager rankManager = RankManager.get();
                PlayerData data = new PlayerData(uuid.toString(), name, now, now, rankManager.getId(RankModel.DEFAULT));
                playerDataManager.insertStatic(data);
            }

            RankManager rankManager = RankManager.get();
            RankModel playerRank = rankManager.getPlayerRank(player.getName());

            boolean wasOp = player.isOp();
            player.setOp(playerRank.isPermitted(autoOpPermission));
            boolean isOp = player.isOp();

            if (isOp != wasOp) {
                Refraction.log(1, "Updated operator status [%s] -> %s", player.getName(), isOp);
            }
        } catch (SQLException exception) {
            Refraction.log(2, "Failed to manage player data [%s]: %s", name, exception.getMessage());
            Refraction.log(2, Arrays.toString(exception.getStackTrace()));
            player.kick(Component.text("Timed out"));
        }

        event.joinMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        LocalDateTime now = LocalDateTime.now();

        try {
            playerDataManager.updateDynamic(UUID.fromString(uuid), now);
        } catch (SQLException exception) {
            Refraction.log(2, "Failed to update dynamic data [%s]", player.getName());
            Refraction.log(2, Arrays.toString(exception.getStackTrace()));
        }

        event.quitMessage(null);
    }

    @EventHandler
    public void onRankChange(RankChangeEvent event) {
        Player player = event.getPlayer();
        RankManager rankManager = RankManager.get();

        try {
            RankModel playerRank = rankManager.getPlayerRank(player.getName());
            boolean wasOp = player.isOp();
            player.setOp(playerRank.isPermitted(autoOpPermission));
            boolean isOp = player.isOp();

            if (isOp != wasOp) {
                Refraction.log(1, "Updated operator status [%s] -> %s", player.getName(), isOp);
            }
        } catch (SQLException exception) {
            Refraction.log(2, "Failed to update operator status [%s]: %s", player.getName(), exception.getMessage());
            Refraction.log(2, Arrays.toString(exception.getStackTrace()));
        }
    }
}
