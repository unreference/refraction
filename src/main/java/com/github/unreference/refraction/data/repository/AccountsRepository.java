package com.github.unreference.refraction.data.repository;

import com.github.unreference.refraction.domain.model.AccountsRecord;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class AccountsRepository extends AbstractRepository<AccountsRecord> {
  private static AccountsRepository instance;

  private AccountsRepository() {}

  public static AccountsRepository get() {
    if (instance == null) {
      instance = new AccountsRepository();
    }

    return instance;
  }

  @Override
  public String getName() {
    return "accounts";
  }

  @Override
  protected Map<String, String> getColumns() {
    Map<String, String> columns = new LinkedHashMap<>();
    columns.put("account_id", "VARCHAR(36) NOT NULL UNIQUE");
    columns.put("name", "VARCHAR(16) NOT NULL");
    columns.put("gems", "INT NOT NULL");
    columns.put("shards", "INT NOT NULL");
    columns.put("first_played", "DATETIME(0) NOT NULL");
    columns.put("last_played", "DATETIME(0) NOT NULL");
    return columns;
  }

  @Override
  protected Map<String, String> getIndexes() {
    Map<String, String> indexes = new HashMap<>();
    indexes.put("UNIQUE name_index", "name");
    return indexes;
  }

  @Override
  protected Map<String, Object> map(AccountsRecord record) {
    Map<String, Object> accounts = new LinkedHashMap<>();
    accounts.put("account_id", record.accountId());
    accounts.put("name", record.name());
    accounts.put("gems", record.gems());
    accounts.put("shards", record.shards());
    accounts.put("first_played", record.firstPlayed());
    accounts.put("last_played", record.lastPlayed());
    return accounts;
  }

  public UUID getId(String name) throws SQLException {
    try (ResultSet result = query("account_id", "name = ?", name)) {
      if (result.next()) {
        return UUID.fromString(result.getString("account_id"));
      }
    }

    return null;
  }

  public String getName(UUID id) throws SQLException {
    try (ResultSet result = query("name", "account_id = ?", id.toString())) {
      if (result.next()) {
        return result.getString("name");
      }
    }

    return null;
  }

  public int getGems(UUID id) throws SQLException {
    try (ResultSet result = query("gems", "account_id = ?", id.toString())) {
      if (result.next()) {
        return result.getInt("gems");
      }
    }

    return -1;
  }

  public int getShards(UUID id) throws SQLException {
    try (ResultSet result = query("shards", "account_id = ?", id.toString())) {
      if (result.next()) {
        return result.getInt("shards");
      }
    }

    return -1;
  }

  public void updateLastPlayed(UUID id, String name, LocalDateTime lastPlayed) throws SQLException {
    Map<String, Object> data = new LinkedHashMap<>();
    data.put("name", name);
    data.put("last_played", lastPlayed);
    update(data, "account_id = ?", id.toString());
  }
}
