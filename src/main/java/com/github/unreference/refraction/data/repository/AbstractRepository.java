package com.github.unreference.refraction.data.repository;

import com.github.unreference.refraction.data.manager.DatabaseManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRepository<T> {
  public abstract String getName();

  protected abstract Map<String, String> getColumns();

  protected abstract Map<String, Object> map(T record);

  protected Map<String, String> getIndexes() {
    return new HashMap<>();
  }

  public void create() throws SQLException {
    if (getIndexes().isEmpty()) {
      DatabaseManager.get().createTable(getName(), getColumns());
    } else {
      DatabaseManager.get().createTable(getName(), getColumns(), getIndexes());
    }
  }

  public void insert(T record) throws SQLException {
    Map<String, Object> data = map(record);
    DatabaseManager.get().insert(getName(), data);
  }

  public boolean exists(String column, Object value) throws SQLException {
    return DatabaseManager.get().isRecordCreated(getName(), column, value);
  }

  public ResultSet query(String select, String where, String... params) throws SQLException {
    return DatabaseManager.get().query(select, getName(), where, params);
  }

  public void update(Map<String, Object> data, String condition, Object... conditionValues)
      throws SQLException {
    DatabaseManager.get().update(getName(), data, condition, conditionValues);
  }
}
