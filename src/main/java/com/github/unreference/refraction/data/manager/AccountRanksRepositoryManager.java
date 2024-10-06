package com.github.unreference.refraction.data.manager;

import com.github.unreference.refraction.Refraction;
import com.github.unreference.refraction.data.AccountRanksRecord;
import com.github.unreference.refraction.data.repository.AccountRanksRepository;
import com.github.unreference.refraction.model.Rank;
import java.sql.SQLException;
import java.util.*;

public class AccountRanksRepositoryManager {
  private static AccountRanksRepositoryManager instance;

  private AccountRanksRepositoryManager() {}

  public static AccountRanksRepositoryManager get() {
    if (instance == null) {
      instance = new AccountRanksRepositoryManager();
    }

    return instance;
  }

  public void create() {
    try {
      AccountRanksRepository.get().createTable();
    } catch (SQLException exception) {
      Refraction.log(2, "Failed creating table: %s", exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
    }
  }

  public boolean isNew(UUID id) {
    try {
      return !AccountRanksRepository.get().exists(id);
    } catch (SQLException exception) {
      Refraction.log(
          2, "Failed to check if player (uuid=%s) exists: %s", id, exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
      return false;
    }
  }

  public void register(AccountRanksRecord data) {
    try {
      if (isNew(UUID.fromString(data.getAccountId()))) {
        AccountRanksRepository.get().insert(data);
      } else {
        Refraction.log(1, "Player (uuid=%s) already exists", data.getAccountId());
      }
    } catch (SQLException exception) {
      Refraction.log(
          2,
          "Failed to register new player (uuid=%s): %s",
          data.getAccountId(),
          exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
    }
  }

  public String getRank(UUID id) {
    try {
      return AccountRanksRepository.get().getRank(id);
    } catch (SQLException exception) {
      Refraction.log(2, "Failed getting rank (uuid=%s): %s", id, exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
      return null;
    }
  }

  public List<Rank> getSubranks(UUID id) {
    try {
      return AccountRanksRepository.get().getSubranks(id);
    } catch (SQLException exception) {
      Refraction.log(2, "Failed getting subranks (uuid=%s): %s", id, exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
      return List.of();
    }
  }

  public void setRank(UUID id, Rank newRank) {
    try {
      AccountRanksRepository.get().setRank(id, newRank);
    } catch (SQLException exception) {
      Refraction.log(2, "Failed setting rank (uuid=%s): %s", id, exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
    }
  }

  public void addRank(UUID id, Rank rank) {
    try {
      AccountRanksRepository.get().addRank(id, rank);
    } catch (SQLException exception) {
      Refraction.log(2, "Failed adding rank (uuid=%s): %s", id, exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
    }
  }
}
