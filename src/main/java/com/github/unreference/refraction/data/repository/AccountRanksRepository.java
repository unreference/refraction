package com.github.unreference.refraction.data.repository;

import com.github.unreference.refraction.data.manager.DatabaseManager;
import com.github.unreference.refraction.domain.model.AccountRanksRecord;
import com.github.unreference.refraction.domain.model.Rank;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class AccountRanksRepository extends AbstractRepository<AccountRanksRecord> {
  private static AccountRanksRepository instance;

  private AccountRanksRepository() {}

  public static AccountRanksRepository get() {
    if (instance == null) {
      instance = new AccountRanksRepository();
    }

    return instance;
  }

  @Override
  public String getName() {
    return "account_ranks";
  }

  @Override
  protected Map<String, String> getColumns() {
    Map<String, String> columns = new LinkedHashMap<>();
    columns.put("id", "INT NOT NULL AUTO_INCREMENT PRIMARY KEY");
    columns.put("account_id", "VARCHAR(36) NOT NULL");
    columns.put("rank", "VARCHAR(10)");
    columns.put("is_primary", "BOOL NOT NULL");
    columns.put("parent_id", "INT DEFAULT NULL");
    return columns;
  }

  @Override
  protected Map<String, String> getIndexes() {
    Map<String, String> indexes = new HashMap<>();
    indexes.put("account_index", "account_id");
    indexes.put("rank_index", "rank");
    indexes.put("parent_rank_index", "parent_id");
    indexes.put("UNIQUE subrank_index", "account_id, rank, is_primary");
    return indexes;
  }

  @Override
  protected Map<String, Object> map(AccountRanksRecord record) {
    Map<String, Object> accountRanks = new HashMap<>();
    accountRanks.put("id", record.getId());
    accountRanks.put("account_id", record.getAccountId());
    accountRanks.put("rank", record.getRank());
    accountRanks.put("is_primary", record.isPrimary());
    accountRanks.put("parent_id", record.getParentId());
    return accountRanks;
  }

  public String getRank(UUID id) throws SQLException {
    try (ResultSet result = query("rank", "account_id = ?", id.toString())) {
      if (result.next()) {
        return result.getString("rank");
      }
    }

    return null;
  }

  public void setRank(UUID id, Rank rank) throws SQLException {
    Integer currentPrimaryId = getId(id.toString());
    Map<String, Object> data = new LinkedHashMap<>();
    data.put("rank", rank.getId());
    data.put("is_primary", true);

    if (currentPrimaryId != null) {
      update(data, "account_id = ? AND is_primary = TRUE", id.toString());
    } else {
      AccountRanksRecord primary = new AccountRanksRecord(id.toString(), rank.getId(), true, null);
      insert(primary);
    }

    clearSubranks(id);
  }

  public void addSubrank(UUID id, Rank rank) throws SQLException {
    Integer parentId = getId(id.toString());

    try (ResultSet result =
        query(
            "id",
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

  public List<Rank> getSubranks(UUID id) throws SQLException {
    List<Rank> subranks = new ArrayList<>();

    try (ResultSet result = query("rank", "account_id = ? AND is_primary = FALSE", id.toString())) {
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

  private Integer getId(String accountId) throws SQLException {
    try (ResultSet result = query("id", "account_id = ?", accountId)) {
      if (result.next()) {
        return result.getInt("id");
      }
    }

    return null;
  }

  private void clearSubranks(UUID id) throws SQLException {
    DatabaseManager.get()
        .execute(
            String.format("DELETE FROM %s WHERE account_id = ? AND is_primary = FALSE", getName()),
            id.toString());
  }
}
