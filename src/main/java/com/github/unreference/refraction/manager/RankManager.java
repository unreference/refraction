package com.github.unreference.refraction.manager;

import com.github.unreference.refraction.Refraction;
import com.github.unreference.refraction.model.RankModel;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class RankManager {
    private static RankManager instance;

    private RankManager() {
    }

    public static RankManager get() {
        if (instance == null) {
            instance = new RankManager();
        }

        return instance;
    }

    public String getId(RankModel rank) {
        return rank.getId();
    }

    public RankModel getRankFromId(String id) {
        for (RankModel rank : RankModel.values()) {
            if (rank.getId().equalsIgnoreCase(id)) {
                return rank;
            }
        }

        return null;
    }

    public RankModel getPlayerRank(Player player) throws SQLException {
        DatabaseManager databaseManager = DatabaseManager.get();
        String uuid = player.getUniqueId().toString();
        try (ResultSet result = databaseManager.queryData("rank", "players", "uuid = ?", uuid)) {
            if (result.next()) {
                for (RankModel rank : RankModel.values()) {
                    if (rank.getId().equalsIgnoreCase(result.getString("rank"))) {
                        return rank;
                    }
                }
            }
        } catch (SQLException exception) {
            Refraction.log(2, "Failed to find rank [%s]: %s", player, exception.getMessage());
            Refraction.log(2, Arrays.toString(exception.getStackTrace()));
            throw exception;
        }

        return null;
    }

    public void setPlayerRank(Player player, RankModel newRank) throws SQLException {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("rank", newRank.getId());
        DatabaseManager databaseManager = DatabaseManager.get();
        String uuid = player.getUniqueId().toString();
        try {
            databaseManager.updateData("players", data, "uuid", uuid);
            Refraction.log(0, "Update rank [%s] -> %s", player.getName(), newRank.getId());
        } catch (SQLException exception) {
            Refraction.log(2, "Failed to update rank [%s]: %s", player.getName(), exception.getMessage());
            Refraction.log(2, Arrays.toString(exception.getStackTrace()));
            throw exception;
        }
    }
}
