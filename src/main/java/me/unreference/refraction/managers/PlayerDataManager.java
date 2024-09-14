package me.unreference.refraction.managers;

import me.unreference.refraction.data.PlayerData;
import me.unreference.refraction.models.RankModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static me.unreference.refraction.Refraction.log;

public class PlayerDataManager {
    private static PlayerDataManager instance;
    private final DatabaseManager databaseManager;

    private PlayerDataManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public static synchronized PlayerDataManager get(DatabaseManager databaseManager) {
        if (instance == null) {
            instance = new PlayerDataManager(databaseManager);
        }

        return instance;
    }

    public boolean isNew(String uuid) throws SQLException {
        return !databaseManager.recordExists("players", "uuid", uuid);
    }

    public void insertStatic(PlayerData data) throws SQLException {
        if (isNew(data.uuid())) {
            Map<String, Object> player = buildPlayerMap(data);
            databaseManager.insertData("players", player);
        } else {
            log(1, "PlayerDataManager", "Static data already exists [" + data.name() + "]");
        }
    }

    public void updateDynamic(String uuid, String ip, LocalDateTime lastPlayed) throws SQLException {
        Map<String, Object> player = new LinkedHashMap<>();
        player.put("ip", ip);
        player.put("last_played", lastPlayed);
        databaseManager.updateData("players", player, "uuid", uuid);
    }

    public void create() throws SQLException {
        Map<String, String> columns = new LinkedHashMap<>();
        columns.put("uuid", "CHAR(36) NOT NULL UNIQUE");
        columns.put("name", "CHAR(16) NOT NULL");
        columns.put("ip", "VARCHAR(45) NOT NULL");
        columns.put("first_played", "DATETIME(0) NOT NULL");
        columns.put("last_played", "DATETIME(0) NOT NULL");
        columns.put("rank", "CHAR(6) NOT NULL");
        databaseManager.createTable("players", columns);
    }

    private Map<String, Object> buildPlayerMap(PlayerData data) {
        Map<String, Object> player = new LinkedHashMap<>();
        player.put("uuid", data.uuid());
        player.put("name", data.name());
        player.put("ip", data.ip());
        player.put("first_played", data.firstPlayed());
        player.put("last_played", data.lastPlayed());
        player.put("rank", data.rank());
        return player;
    }

    public RankModel getPlayerRank(String uuid) {
        try (ResultSet result = databaseManager.queryData("rank", "players", "uuid = ?", uuid)) {
            if (result.next()) {
                for (RankModel rank : RankModel.values()) {
                    if (rank.getId().equalsIgnoreCase(result.getString("rank"))) {
                        return rank;
                    }
                }
            }
        } catch (SQLException exception) {
            log(2, "PlayerDataManager", "Failed to find rank [" + uuid + "]: " + Arrays.toString(exception.getStackTrace()));
            log(2, "PlayerDataManager", Arrays.toString(exception.getStackTrace()));
        }

        return null;
    }
}
