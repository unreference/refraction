package com.github.unreference.refraction.manager;

import com.github.unreference.refraction.Refraction;
import com.github.unreference.refraction.model.RankModel;

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

    public RankModel getPlayerRank(String name) throws SQLException {
        DatabaseManager databaseManager = DatabaseManager.get();
        PlayerDataManager playerDataManager = PlayerDataManager.get(databaseManager);
        String uuid = playerDataManager.getUuid(name).toString();
        try (ResultSet result = databaseManager.queryData("rank", "players", "uuid = ?", uuid)) {
            if (result.next()) {
                for (RankModel rank : RankModel.values()) {
                    if (rank.getId().equalsIgnoreCase(result.getString("rank"))) {
                        return rank;
                    }
                }
            }
        } catch (SQLException exception) {
            Refraction.log(2, "Failed to find rank [%s]: %s", name, exception.getMessage());
            Refraction.log(2, Arrays.toString(exception.getStackTrace()));
            throw exception;
        }

        return null;
    }

    public void setPlayerRank(String name, RankModel newRank) throws SQLException {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("rank", newRank.getId());
        DatabaseManager databaseManager = DatabaseManager.get();
        PlayerDataManager playerDataManager = PlayerDataManager.get(databaseManager);
        String uuid = playerDataManager.getUuid(name).toString();
        try {
            databaseManager.updateData("players", data, "uuid", uuid);
            Refraction.log(0, "Updated rank [%s] -> %s", name, newRank.getId());
        } catch (SQLException exception) {
            Refraction.log(2, "Failed to update rank [%s]: %s", name, exception.getMessage());
            Refraction.log(2, Arrays.toString(exception.getStackTrace()));
            throw exception;
        }
    }
}
