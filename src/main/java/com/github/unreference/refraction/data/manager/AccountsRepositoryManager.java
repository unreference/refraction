package com.github.unreference.refraction.data.manager;

import com.github.unreference.refraction.Refraction;
import com.github.unreference.refraction.data.AccountsRecord;
import com.github.unreference.refraction.data.repository.AccountsRepository;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

public class AccountsRepositoryManager {
  private static AccountsRepositoryManager instance;

  private AccountsRepositoryManager() {}

  public static AccountsRepositoryManager get() {
    if (instance == null) {
      instance = new AccountsRepositoryManager();
    }

    return instance;
  }

  public void create() {
    try {
      AccountsRepository.get().createTable();
    } catch (SQLException exception) {
      Refraction.log(2, "Failed creating table: %s", exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
    }
  }

  public boolean isNew(UUID id) {
    try {
      return !AccountsRepository.get().exists(id);
    } catch (SQLException exception) {
      Refraction.log(
          2, "Failed to check if player (uuid=%s) exists: %s", id, exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
      return false;
    }
  }

  public void register(AccountsRecord data) {
    try {
      if (isNew(UUID.fromString(data.uuid()))) {
        AccountsRepository.get().insert(data);
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
      AccountsRepository.get().updateLastPlayed(id, name, lastPlayed);
    } catch (SQLException exception) {
      Refraction.log(2, "Failed to update last played (name=%s): %s", name, exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
    }
  }

  public UUID getId(String name) {
    try {
      return AccountsRepository.get().getId(name);
    } catch (SQLException exception) {
      Refraction.log(2, "Failed getting ID (name=%s): %s", name, exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
      return null;
    }
  }

  public String getName(UUID id) {
    try {
      return AccountsRepository.get().getName(id);
    } catch (SQLException exception) {
      Refraction.log(2, "Failed getting name (uuid=%s): %s", id, exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
      return null;
    }
  }

  public int getGems(UUID id) {
    try {
      return AccountsRepository.get().getGems(id);
    } catch (SQLException exception) {
      Refraction.log(2, "Failed getting gems (uuid=%s): %s", id, exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
      return -1;
    }
  }

  public int getGems(String name) {
    try {
      return AccountsRepository.get().getGems(name);
    } catch (SQLException exception) {
      Refraction.log(2, "Failed getting gems (name=%s): %s", name, exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
      return -1;
    }
  }
}
