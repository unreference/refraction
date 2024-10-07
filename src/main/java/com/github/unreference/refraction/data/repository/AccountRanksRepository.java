package com.github.unreference.refraction.data.repository;

import com.github.unreference.refraction.data.AccountRanksRecord;
import com.github.unreference.refraction.data.manager.DatabaseManager;
import com.github.unreference.refraction.model.Rank;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

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

    if (!indexExists("account_ranks", "account_index")) {
      DatabaseManager.get().execute("CREATE INDEX account_index ON account_ranks(account_id)");
    }

    if (!indexExists("account_ranks", "rank_index")) {
      DatabaseManager.get().execute("CREATE INDEX rank_index ON account_ranks(rank)");
    }

    if (!indexExists("account_ranks", "parent_rank_index")) {
      DatabaseManager.get().execute("CREATE INDEX parent_rank_index ON account_ranks(parent_id)");
    }

    if (!indexExists("account_ranks", "subrank_index")) {
      DatabaseManager.get()
          .execute(
              "CREATE UNIQUE INDEX subrank_index ON account_ranks(account_id, rank, is_primary)");
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

  private Integer getId(String accountId) throws SQLException {
    try (ResultSet result =
        DatabaseManager.get()
            .query("id", "account_ranks", "account_id = ? AND is_primary = TRUE", accountId)) {
      if (result.next()) {
        return result.getInt("id");
      }

      return null;
    }
  }

  public void setRank(UUID id, Rank newRank) throws SQLException {
    Integer currentPrimaryId = getId(id.toString());
    Map<String, Object> data = new LinkedHashMap<>();
    data.put("rank", newRank.getId());
    data.put("is_primary", true);

    if (currentPrimaryId != null) {
      DatabaseManager.get()
          .update("account_ranks", data, "account_id = ? AND is_primary = ?", id.toString(), true);
    } else {
      AccountRanksRecord primary =
          new AccountRanksRecord(id.toString(), newRank.getId(), true, null);
      insert(primary);
    }

    updateSubranks(id);
  }

  private void updateSubranks(UUID id) throws SQLException {
    DatabaseManager.get()
        .execute(
            "DELETE FROM account_ranks WHERE account_id = ? AND is_primary = false", id.toString());
  }

  public List<Rank> getSubranks(UUID id) throws SQLException {
    List<Rank> subranks = new ArrayList<>();

    try (ResultSet result =
        DatabaseManager.get()
            .query(
                "rank", "account_ranks", "account_id = ? AND is_primary = FALSE", id.toString())) {
      while (result.next()) {
        String rankId = result.getString("rank");
        Rank rank = Rank.getRankFromId(rankId);

        if (rank != null) {
          subranks.add(rank);
        }
      }
    }

    return subranks;
  }

  public void addRank(UUID id, Rank rank) throws SQLException {
    Integer parentId = getId(id.toString());

      try (ResultSet result =
          DatabaseManager.get()
              .query(
                  "id",
                  "account_ranks",
                  "account_id = ? AND rank = ? AND is_primary = FALSE",
                  id.toString(),
                  rank.getId())) {

        if (!result.next()) {
          AccountRanksRecord subrank =
              new AccountRanksRecord(id.toString(), rank.getId(), false, parentId);
          insert(subrank);
        }
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
