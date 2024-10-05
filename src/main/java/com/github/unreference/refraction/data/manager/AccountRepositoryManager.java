package com.github.unreference.refraction.data.manager;

import com.github.unreference.refraction.Refraction;
import com.github.unreference.refraction.data.Account;
import com.github.unreference.refraction.data.repository.AccountRepository;
import com.github.unreference.refraction.model.Rank;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

public class AccountRepositoryManager {
  private static AccountRepositoryManager instance;

  private AccountRepositoryManager() {}

  public static AccountRepositoryManager get() {
    if (instance == null) {
      instance = new AccountRepositoryManager();
    }

    return instance;
  }

  public void create() {
    try {
      AccountRepository.get().createTable();
    } catch (SQLException exception) {
      Refraction.log(2, "Failed creating table: %s", exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
    }
  }

  public boolean isNew(UUID id) {
    try {
      return !AccountRepository.get().exists(id);
    } catch (SQLException exception) {
      Refraction.log(
          2, "Failed to check if player (uuid=%s) exists: %s", id, exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
      return false;
    }
  }

  public void register(Account data) {
    try {
      if (isNew(UUID.fromString(data.uuid()))) {
        AccountRepository.get().insert(data);
      } else {
        Refraction.log(1, "Player (name=%s) already exists", data.name());
      }
    } catch (SQLException exception) {
      Refraction.log(
          2, "Failed to register new player (uuid=%s): %s", data.uuid(), exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
    }
  }

  public void update(UUID id, String name, LocalDateTime lastPlayed) {
    try {
      AccountRepository.get().updateLastPlayed(id, name, lastPlayed);
    } catch (SQLException exception) {
      Refraction.log(2, "Failed to update last played (name=%s): %s", name, exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
    }
  }

  public UUID getId(String name) {
    try {
      return AccountRepository.get().getId(name);
    } catch (SQLException exception) {
      Refraction.log(2, "Failed getting ID (name=%s): %s", name, exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
      return null;
    }
  }

  public String getName(UUID id) {
    try {
      return AccountRepository.get().getName(id);
    } catch (SQLException exception) {
      Refraction.log(2, "Failed getting name (uuid=%s): %s", id, exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
      return null;
    }
  }

  public String getPrimaryRank(UUID id) {
    try {
      return AccountRepository.get().getPrimaryRank(id);
    } catch (SQLException exception) {
      Refraction.log(2, "Failed getting primary rank (uuid=%s): %s", id, exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
      return null;
    }
  }

  public String getPrimaryRank(String name) {
    try {
      return AccountRepository.get().getPrimaryRank(name);
    } catch (SQLException exception) {
      Refraction.log(2, "Failed getting primary rank (name=%s): %s", name, exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
      return null;
    }
  }

  public void setPrimaryRank(UUID id, Rank newPrimary) {
    try {
      AccountRepository.get().setRank(id, newPrimary);
    } catch (SQLException exception) {
      Refraction.log(2, "Failed setting primary rank (uuid=%s): %s", id, exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
    }
  }

  public void setPrimaryRank(String name, Rank newPrimary) {
    try {
      AccountRepository.get().setRank(name, newPrimary);
    } catch (SQLException exception) {
      Refraction.log(2, "Failed setting primary rank (name=%s): %s", name, exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
    }
  }
}
