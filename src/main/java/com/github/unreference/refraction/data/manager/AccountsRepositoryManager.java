package com.github.unreference.refraction.data.manager;

import com.github.unreference.refraction.Refraction;
import com.github.unreference.refraction.data.repository.AccountsRepository;
import com.github.unreference.refraction.domain.model.AccountsRecord;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

public class AccountsRepositoryManager
    extends AbstractRepositoryManager<AccountsRecord, AccountsRepository> {
  private static AccountsRepositoryManager instance;

  private AccountsRepositoryManager() {}

  public static AccountsRepositoryManager get() {
    if (instance == null) {
      instance = new AccountsRepositoryManager();
    }

    return instance;
  }

  @Override
  protected AccountsRepository getRepository() {
    return AccountsRepository.get();
  }

  public boolean isNew(UUID id) {
    try {
      return !AccountsRepository.get().exists("account_id", id.toString());
    } catch (SQLException exception) {
      Refraction.log(
          2, "Failed to check if player (account_id=%s) exists: %s", id.toString(), exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
      return false;
    }
  }

  public void register(AccountsRecord data) {
    try {
      if (isNew(UUID.fromString(data.accountId()))) {
        AccountsRepository.get().insert(data);
      } else {
        Refraction.log(1, "Player (name=%s) already exists", data.name());
      }
    } catch (SQLException exception) {
      Refraction.log(2, "Failed to register (name=%s): %s", data.name(), exception.getMessage());
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
      Refraction.log(2, "Failed getting name (account_id=%s): %s", id.toString(), exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
      return null;
    }
  }
}
