package com.github.unreference.refraction.data.repository;

import com.github.unreference.refraction.data.Account;
import com.github.unreference.refraction.data.manager.DatabaseManager;
import com.github.unreference.refraction.model.Rank;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class AccountRepository {
  private static AccountRepository instance;

  private AccountRepository() {}

  public static AccountRepository get() {
    if (instance == null) {
      instance = new AccountRepository();
    }

    return instance;
  }

  public void createTable() throws SQLException {
    Map<String, String> columns = new LinkedHashMap<>();
    columns.put("uuid", "VARCHAR(36) NOT NULL UNIQUE PRIMARY KEY");
    columns.put("name", "VARCHAR(16) NOT NULL");
    columns.put("first_played", "DATETIME(0) NOT NULL");
    columns.put("last_played", "DATETIME(0) NOT NULL");
    columns.put("primary_rank", "VARCHAR(10) NOT NULL");
    DatabaseManager.get().createTable("account", columns);
  }

  public boolean exists(UUID id) throws SQLException {
    return DatabaseManager.get().recordExists("account", "uuid", id.toString());
  }

  public void insert(Account data) throws SQLException {
    Map<String, Object> account = buildAccount(data);
    DatabaseManager.get().insertData("account", account);
  }

  public void updateLastPlayed(UUID id, String name, LocalDateTime lastPlayed) throws SQLException {
    Map<String, Object> data = new LinkedHashMap<>();
    data.put("name", name);
    data.put("last_played", lastPlayed);
    DatabaseManager.get().updateData("account", data, "uuid", id.toString());
  }

  public UUID getId(String name) throws SQLException {
    try (ResultSet result = DatabaseManager.get().queryData("uuid", "account", "name = ?", name)) {
      if (result.next()) {
        return UUID.fromString(result.getString("uuid"));
      }
    }

    return null;
  }

  public String getName(UUID id) throws SQLException {
    try (ResultSet result =
        DatabaseManager.get().queryData("name", "account", "uuid = ?", id.toString())) {
      if (result.next()) {
        return result.getString("name");
      }
    }

    return null;
  }

  public String getPrimaryRank(UUID id) throws SQLException {
    try (ResultSet result =
        DatabaseManager.get().queryData("primary_rank", "account", "uuid = ?", id.toString())) {
      if (result.next()) {
        return result.getString("primary_rank");
      }
    }

    return null;
  }

  public String getPrimaryRank(String name) throws SQLException {
    try (ResultSet result =
        DatabaseManager.get().queryData("primary_rank", "account", "name = ?", name)) {
      if (result.next()) {
        return result.getString("primary_rank");
      }
    }

    return null;
  }

  public void setRank(UUID id, Rank newRank) throws SQLException {
    Map<String, Object> data = new LinkedHashMap<>();
    data.put("primary_rank", newRank.getId());
    DatabaseManager.get().updateData("account", data, "uuid", id.toString());
  }

  public void setRank(String name, Rank newRank) throws SQLException {
    Map<String, Object> data = new LinkedHashMap<>();
    data.put("primary_rank", newRank.getId());
    DatabaseManager.get().updateData("account", data, "name", name);
  }

  private Map<String, Object> buildAccount(Account data) {
    Map<String, Object> account = new LinkedHashMap<>();
    account.put("uuid", data.uuid());
    account.put("name", data.name());
    account.put("first_played", data.firstPlayed());
    account.put("last_played", data.lastPlayed());
    account.put("primary_rank", data.primaryRank());
    return account;
  }
}
