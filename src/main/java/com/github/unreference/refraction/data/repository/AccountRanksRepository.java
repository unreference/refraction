package com.github.unreference.refraction.data.repository;

import com.github.unreference.refraction.data.AccountRanksRecord;
import com.github.unreference.refraction.data.manager.DatabaseManager;
import com.github.unreference.refraction.model.Rank;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class AccountRanksRepository {
  private static AccountRanksRepository instance;

  private AccountRanksRepository() {}

  public static AccountRanksRepository get() {
    if (instance == null) {
      instance = new AccountRanksRepository();
    }

    return instance;
  }

  public void createTable() throws SQLException {
    Map<String, String> columns = new LinkedHashMap<>();
    columns.put("id", "INT NOT NULL AUTO_INCREMENT PRIMARY KEY");
    columns.put("account_id", "VARCHAR(36) NOT NULL");
    columns.put("rank", "VARCHAR(10)");
    columns.put("is_primary", "BOOL");
    columns.put("parent_id", "INT DEFAULT NULL");

    DatabaseManager.get().createTable("account_ranks", columns);
    DatabaseManager.get().execute("CREATE INDEX account_index ON account_ranks(account_id)");
    DatabaseManager.get().execute("CREATE INDEX rank_index ON account_ranks(rank)");
    DatabaseManager.get().execute("CREATE INDEX parent_rank_index ON account_ranks(parent_id)");
    DatabaseManager.get()
        .execute(
            "CREATE UNIQUE INDEX subsidiary_index ON account_ranks(account_id, rank, is_primary)");
  }

  public boolean exists(UUID id) throws SQLException {
    return DatabaseManager.get().isRecordCreated("account_ranks", "account_id", id.toString());
  }

  public void insert(AccountRanksRecord data) throws SQLException {
    Map<String, Object> account = buildAccountRanks(data);
    DatabaseManager.get().insert("account_ranks", account);
  }

  public String getRank(UUID id) throws SQLException {
    try (ResultSet result =
        DatabaseManager.get().query("rank", "account_ranks", "account_id = ?", id.toString())) {
      if (result.next()) {
        return result.getString("rank");
      }
    }

    return null;
  }

  public void setRank(UUID id, Rank newRank) throws SQLException {
    if (newRank.isPrimary()) {
      Map<String, Object> data = new LinkedHashMap<>();
      data.put("rank", newRank.getId());
      data.put("is_primary", newRank.isPrimary());
      DatabaseManager.get().update("account_ranks", data, "account_id", id.toString());
    }
  }

  private Map<String, Object> buildAccountRanks(AccountRanksRecord data) {
    Map<String, Object> account = new LinkedHashMap<>();
    account.put("id", data.getId());
    account.put("account_id", data.getAccountId());
    account.put("rank", data.getRank());
    account.put("is_primary", data.isPrimary());
    account.put("parent_id", data.getParentId());
    return account;
  }
}
