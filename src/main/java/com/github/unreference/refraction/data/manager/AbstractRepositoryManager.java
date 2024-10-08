package com.github.unreference.refraction.data.manager;

import com.github.unreference.refraction.Refraction;
import com.github.unreference.refraction.data.repository.AbstractRepository;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

public abstract class AbstractRepositoryManager<T, R extends AbstractRepository<T>> {
  protected abstract R getRepository();

  public void create() {
    try {
      getRepository().create();
    } catch (SQLException exception) {
      Refraction.log(
          2, "Failed to create (table=%s): %s", getRepository().getName(), exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
    }
  }

  public boolean exists(String column, Object value) {
    try {
      return !getRepository().exists(column, value);
    } catch (SQLException exception) {
      Refraction.log(
          2,
          "Failed to ponder record existence (table=%s, column=%s): %s",
          getRepository().getName(),
          column,
          exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
      return false;
    }
  }

  public void update(Map<String, Object> data, String condition, Object... conditionValues) {
    try {
      getRepository().update(data, condition, conditionValues);
    } catch (SQLException exception) {
      Refraction.log(
          2,
          "Failed to update record (table=%s): %s",
          getRepository().getName(),
          exception.getMessage());
      Refraction.log(2, Arrays.toString(exception.getStackTrace()));
    }
  }
}
