package com.github.unreference.refraction.data.repository;

import com.github.unreference.refraction.data.PlayerData;
import com.github.unreference.refraction.model.Rank;
import com.github.unreference.refraction.service.DatabaseService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataRepository {
    private final DatabaseService databaseService;

    public PlayerDataRepository(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public void createTable() throws SQLException {
        Map<String, String> columns = new LinkedHashMap<>();
        columns.put("uuid", "VARCHAR(36) NOT NULL UNIQUE PRIMARY KEY");
        columns.put("name", "VARCHAR(16) NOT NULL");
        columns.put("first_played", "DATETIME(0) NOT NULL");
        columns.put("last_played", "DATETIME(0) NOT NULL");
        columns.put("rank", "VARCHAR(7) NOT NULL");
        databaseService.createTable("players", columns);
    }

    public boolean exists(UUID id) throws SQLException {
        return databaseService.recordExists("players", "uuid", id.toString());
    }

    public void insert(PlayerData data) throws SQLException {
        Map<String, Object> playerMap = buildPlayerMap(data);
        databaseService.insertData("players", playerMap);
    }

    public void updateLastPlayed(UUID id, String name, LocalDateTime lastPlayed) throws SQLException {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("name", name);
        data.put("last_played", lastPlayed);
        databaseService.updateData("players", data, "uuid", id.toString());
    }

    public UUID getId(String name) throws SQLException {
        try (ResultSet result = databaseService.queryData("uuid", "players", "name = ?", name)) {
            if (result.next()) {
                return UUID.fromString(result.getString("uuid"));
            }
        }

        return null;
    }

    public String getName(UUID id) throws SQLException {
        try (ResultSet result = databaseService.queryData("name", "players", "uuid = ?", id.toString())) {
            if (result.next()) {
                return result.getString("name");
            }
        }

        return null;
    }

    public String getRank(UUID id) throws SQLException {
        try (ResultSet result = databaseService.queryData("rank", "players", "uuid = ?", id.toString())) {
            if (result.next()) {
                return result.getString("rank");
            }
        }

        return null;
    }

    public String getRank(String name) throws SQLException {
        try (ResultSet result = databaseService.queryData("rank", "players", "name = ?", name)) {
            if (result.next()) {
                return result.getString("rank");
            }
        }

        return null;
    }

    public void setRank(UUID id, Rank newRank) throws SQLException {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("rank", newRank.getId());
        databaseService.updateData("players", data, "uuid", id.toString());
    }

    public void setRank(String name, Rank newRank) throws SQLException {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("rank", newRank.getId());
        databaseService.updateData("players", data, "name", name);
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
