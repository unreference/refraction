package com.github.unreference.refraction.data.repository;

import com.github.unreference.refraction.data.AccountsRecord;
import com.github.unreference.refraction.data.manager.DatabaseManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class AccountsRepository {
  private static AccountsRepository instance;

  private AccountsRepository() {}

  public static AccountsRepository get() {
    if (instance == null) {
      instance = new AccountsRepository();
    }

    return instance;
  }

  public void createTable() throws SQLException {
    Map<String, String> columns = new LinkedHashMap<>();
    columns.put("uuid", "VARCHAR(36) NOT NULL UNIQUE");
    columns.put("name", "VARCHAR(16) NOT NULL");
    columns.put("gems", "INT NOT NULL");
    columns.put("shards", "INT NOT NULL");
    columns.put("first_played", "DATETIME(0) NOT NULL");
    columns.put("last_played", "DATETIME(0) NOT NULL");

    DatabaseManager.get().createTable("accounts", columns);

    if (!indexExists("accounts", "name_index")) {
      DatabaseManager.get().execute("CREATE UNIQUE INDEX name_index ON accounts(name)");
    }
  }

  private boolean indexExists(String tableName, String indexName) throws SQLException {
    String query = String.format("SHOW INDEX FROM %s WHERE Key_name = '%s'", tableName, indexName);
    try (Statement statement = DatabaseManager.get().getConnection().createStatement();
        ResultSet result = statement.executeQuery(query)) {
      return result.next();
    }
  }

  public boolean exists(UUID id) throws SQLException {
    return DatabaseManager.get().isRecordCreated("accounts", "uuid", id.toString());
  }

  public void insert(AccountsRecord data) throws SQLException {
    Map<String, Object> account = buildAccounts(data);
    DatabaseManager.get().insert("accounts", account);
  }

  public void updateLastPlayed(UUID id, String name, LocalDateTime lastPlayed) throws SQLException {
    Map<String, Object> data = new LinkedHashMap<>();
    data.put("name", name);
    data.put("last_played", lastPlayed);
    DatabaseManager.get().update("accounts", data, "uuid = ?", id.toString());
  }

  public UUID getId(String name) throws SQLException {
    try (ResultSet result = DatabaseManager.get().query("uuid", "accounts", "name = ?", name)) {
      if (result.next()) {
        return UUID.fromString(result.getString("uuid"));
      }
    }

    return null;
  }

  public String getName(UUID id) throws SQLException {
    try (ResultSet result =
        DatabaseManager.get().query("name", "accounts", "uuid = ?", id.toString())) {
      if (result.next()) {
        return result.getString("name");
      }
    }

    return null;
  }

  public int getGems(UUID id) throws SQLException {
    try (ResultSet result =
        DatabaseManager.get().query("gems", "accounts", "uuid = ?", id.toString())) {
      if (result.next()) {
        return result.getInt("gems");
      }
    }

    return -1;
  }

  public int getGems(String name) throws SQLException {
    try (ResultSet result = DatabaseManager.get().query("gems", "accounts", "name = ?", name)) {
      if (result.next()) {
        return result.getInt("gems");
      }
    }

    return -1;
  }

  public int getShards(UUID id) throws SQLException {
    try (ResultSet result =
        DatabaseManager.get().query("shards", "accounts", "uuid = ?", id.toString())) {
      if (result.next()) {
        return result.getInt("shards");
      }
    }

    return -1;
  }

  public int getShards(String name) throws SQLException {
    try (ResultSet result = DatabaseManager.get().query("shards", "accounts", "name = ?", name)) {
      if (result.next()) {
        return result.getInt("shards");
      }
    }

    return -1;
  }

  private Map<String, Object> buildAccounts(AccountsRecord data) {
    Map<String, Object> account = new LinkedHashMap<>();
    account.put("uuid", data.uuid());
    account.put("name", data.name());
    account.put("gems", data.gems());
    account.put("shards", data.shards());
    account.put("first_played", data.firstPlayed());
    account.put("last_played", data.lastPlayed());
    return account;
  }
}
