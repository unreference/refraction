package com.github.unreference.refraction.manager;

import com.github.unreference.refraction.Refraction;
import com.github.unreference.refraction.data.PlayerData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.unreference.refraction.Refraction.log;

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

    public boolean isNew(UUID uuid) throws SQLException {
        return !databaseManager.recordExists("players", "uuid", uuid.toString());
    }

    public void insertStatic(PlayerData data) throws SQLException {
        if (isNew(UUID.fromString(data.uuid()))) {
            Map<String, Object> player = buildPlayerMap(data);
            databaseManager.insertData("players", player);
        } else {
            log(1, "Static data already exists [%s]", data.name());
        }
    }

    public void updateDynamic(UUID uuid, LocalDateTime lastPlayed) throws SQLException {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("last_played", lastPlayed);
        databaseManager.updateData("players", data, "uuid", uuid.toString());
    }

    public void create() throws SQLException {
        Map<String, String> columns = new LinkedHashMap<>();
        columns.put("uuid", "VARCHAR(36) NOT NULL UNIQUE PRIMARY KEY");
        columns.put("name", "VARCHAR(16) NOT NULL");
        columns.put("first_played", "DATETIME(0) NOT NULL");
        columns.put("last_played", "DATETIME(0) NOT NULL");
        columns.put("rank", "VARCHAR(7) NOT NULL");
        databaseManager.createTable("players", columns);
    }

    public UUID getUuid(String name) {
        try (ResultSet result = databaseManager.queryData("uuid", "players", "name = ?", name)) {
            if (result.next()) {
                String uuid = result.getString("uuid");
                if (uuid != null) {
                    return UUID.fromString(uuid);
                }
            }
        } catch (SQLException exception) {
            Refraction.log(1, "Failed to find player [%s]", name);
        }

        return null;
    }

    public String getName(UUID uuid) {
        try (ResultSet result = databaseManager.queryData("name", "players", "uuid = ?", uuid.toString())) {
            if (result.next()) {
                String name = result.getString("name");
                if (name != null) {
                    return name;
                }
            }
        } catch (SQLException exception) {
            Refraction.log(1, "Failed to find name [%s]", uuid);
        }

        return null;
    }

    private Map<String, Object> buildPlayerMap(PlayerData data) {
        Map<String, Object> player = new LinkedHashMap<>();
        player.put("uuid", data.uuid());
        player.put("name", data.name());
        player.put("first_played", data.firstPlayed());
        player.put("last_played", data.lastPlayed());
        player.put("rank", data.rank());
        return player;
    }
}
