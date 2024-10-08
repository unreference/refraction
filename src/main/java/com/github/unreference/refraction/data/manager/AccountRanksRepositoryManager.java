package com.github.unreference.refraction.data.manager;

import com.github.unreference.refraction.Refraction;
import com.github.unreference.refraction.data.repository.AccountRanksRepository;
import com.github.unreference.refraction.domain.model.AccountRanksRecord;
import com.github.unreference.refraction.domain.model.Rank;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AccountRanksRepositoryManager
    extends AbstractRepositoryManager<AccountRanksRecord, AccountRanksRepository> {
  private static AccountRanksRepositoryManager instance;

  private AccountRanksRepositoryManager() {}

  public static AccountRanksRepositoryManager get() {
    if (instance == null) {
      instance = new AccountRanksRepositoryManager();
    }

    return instance;
  }

  @Override
  protected AccountRanksRepository getRepository() {
    return AccountRanksRepository.get();
  }

  public boolean isNew(UUID id) {
    try {
      return !AccountRanksRepository.get().exists("account_id", id.toString());
    } catch (SQLException exception) {
      Refraction.log(
          2, "Failed to check if player (account_id=%s) exists: %s", id.toString(), exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
      return false;
    }
  }

  public void register(AccountRanksRecord data) {
    try {
      if (isNew(UUID.fromString(data.getAccountId()))) {
        AccountRanksRepository.get().insert(data);
      } else {
        Refraction.log(1, "Player (account_id=%s) already exists", data.getAccountId());
      }
    } catch (SQLException exception) {
      Refraction.log(
          2,
          "Failed to register new player (account_id=%s): %s",
          data.getAccountId(),
          exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
    }
  }

  public String getRank(UUID id) {
    try {
      return AccountRanksRepository.get().getRank(id);
    } catch (SQLException exception) {
      Refraction.log(2, "Failed getting rank (account_id=%s): %s", id.toString(), exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
      return null;
    }
  }

  public List<Rank> getSubranks(UUID id) {
    try {
      return AccountRanksRepository.get().getSubranks(id);
    } catch (SQLException exception) {
      Refraction.log(2, "Failed getting subranks (account_id=%s): %s", id.toString(), exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
      return List.of();
    }
  }

  public void setRank(UUID id, Rank rank) {
    try {
      AccountRanksRepository.get().setRank(id, rank);
    } catch (SQLException exception) {
      Refraction.log(2, "Failed setting rank (account_id=%s): %s", id.toString(), exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
    }
  }

  public void addSubrank(UUID id, Rank subrank) {
    try {
      AccountRanksRepository.get().addSubrank(id, subrank);
    } catch (SQLException exception) {
      Refraction.log(2, "Failed adding subrank (account_id=%s): %s", id.toString(), exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
    }
  }
}
