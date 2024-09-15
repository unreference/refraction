package me.unreference.refraction.manager;

import me.unreference.refraction.model.RankModel;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static me.unreference.refraction.Refraction.log;

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

    public RankModel getPlayerRank(Player player) {
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
            log(2, "PlayerDataManager", "Failed to find rank [" + player + "]: " + Arrays.toString(exception.getStackTrace()));
            log(2, "PlayerDataManager", Arrays.toString(exception.getStackTrace()));
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
            log(0, "PlayerDataManager", "Updated rank [" + uuid + "] -> " + newRank.getId());
        } catch (SQLException exception) {
            log(2, "PlayerDataManager", "Failed to update rank [" + uuid + "]: " + exception.getMessage());
            log(2, "PlayerDataManager", Arrays.toString(exception.getStackTrace()));
            throw exception;
        }
    }
}
