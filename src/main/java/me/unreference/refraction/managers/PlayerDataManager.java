package me.unreference.refraction.managers;

import me.unreference.refraction.Refraction;
import me.unreference.refraction.data.PlayerData;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

public class PlayerDataManager {
    private static PlayerDataManager instance;
    private final DatabaseManager databaseManager;

    private PlayerDataManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public static PlayerDataManager get(DatabaseManager databaseManager) {
        if (instance == null) {
            instance = new PlayerDataManager(databaseManager);
        }

        return instance;
    }

    public boolean isNew(String uuid) throws SQLException {
        return !databaseManager.recordExists("players", "uuid", uuid);
    }

    public void insertStatic(PlayerData data) throws SQLException {
        if (!isNew(data.uuid())) {
            Refraction.getPlugin().getLogger().info("Static data already exists [" + data.name() + "]");
        } else {
            LinkedHashMap<String, Object> player = new LinkedHashMap<>();
            player.put("uuid", data.uuid());
            player.put("name", data.name());
            player.put("first_played", data.firstPlayed());

            databaseManager.insertData("players", player);
        }
    }

    public void updateDynamic(String uuid, LocalDateTime lastPlayed) throws SQLException {
        LinkedHashMap<String, Object> player = new LinkedHashMap<>();
        player.put("last_played", lastPlayed);

        databaseManager.updateData("players", player, "uuid", uuid);
    }

    public void create() throws SQLException {
        LinkedHashMap<String, String> columns = new LinkedHashMap<>();
        columns.put("uuid", "CHAR(36) NOT NULL UNIQUE");
        columns.put("name", "CHAR(16) NOT NULL");
        columns.put("first_played", "DATETIME(0) NOT NULL");
        columns.put("last_played", "DATETIME(0) NULL");

        databaseManager.createTable("players", columns);
    }

    public void insert(PlayerData data) throws SQLException {
        LinkedHashMap<String, Object> player = new LinkedHashMap<>();
        player.put("uuid", data.uuid());
        player.put("name", data.name());
        player.put("first_played", data.firstPlayed());
        player.put("last_played", data.lastPlayed());

        databaseManager.insertData("players", player);
    }
}
