package com.github.unreference.refraction.data.manager;

import com.github.unreference.refraction.Refraction;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class DatabaseManager {
  private static DatabaseManager instance;

  private final String host = Refraction.getPlugin().getConfig().getString("database.host");
  private final int port = Refraction.getPlugin().getConfig().getInt("database.port");
  private final String user = Refraction.getPlugin().getConfig().getString("database.user");
  private final String password = Refraction.getPlugin().getConfig().getString("database.password");
  private final String name = Refraction.getPlugin().getConfig().getString("database.name");
  private Connection connection;

  private DatabaseManager() {}

  public static DatabaseManager get() {
    if (instance == null) {
      instance = new DatabaseManager();
    }

    return instance;
  }

  public void connect() throws SQLException {
    if (isConnectionClosed()) {
      connection =
          DriverManager.getConnection(
              String.format("jdbc:mysql://%s:%d", host, port), user, password);
      Refraction.log(0, "Connected to server [%s:%d]", host, port);
      initialize();
    }
  }

  public void close() {
    if (connection != null) {
      try {
        connection.close();
        Refraction.log(0, "Closed the database connection.");
      } catch (SQLException exception) {
        Refraction.log(2, "Failed to close database connection: %s", exception.getMessage());
      }
    }
  }

  public void createTable(
      String tableName, Map<String, String> columns, Map<String, String> indexes)
      throws SQLException {
    if (isTableCreated(tableName)) {
      Refraction.log(0, "Found table [%s]", tableName);
      return;
    } else {
      String columnDefinitions =
          columns.entrySet().stream()
              .map(entry -> String.format("%s %s", entry.getKey(), entry.getValue()))
              .collect(Collectors.joining(", "));
      String query = String.format("CREATE TABLE %s (%s)", tableName, columnDefinitions);
      execute(query);
      Refraction.log(1, "Created table [%s]", tableName);
    }

    for (Map.Entry<String, String> entry : indexes.entrySet()) {
      String indexName = entry.getKey();
      String indexDefinition = entry.getValue();

      if (!indexExists(tableName, indexName)) {
        String query =
            String.format("CREATE INDEX %s ON %s(%s)", indexName, tableName, indexDefinition);
        if (indexName.toUpperCase().contains("UNIQUE")) {
          indexName = indexName.replace("UNIQUE", "").trim();
          query =
              String.format(
                  "CREATE UNIQUE INDEX %s ON %s(%s)", indexName, tableName, indexDefinition);
        }
        execute(query);
        Refraction.log(1, "Created index [%s] on table [%s]", indexName, tableName);
      }
    }
  }

  public void createTable(String tableName, Map<String, String> columns) throws SQLException {
    if (isTableCreated(tableName)) {
      Refraction.log(0, "Found table [%s]", tableName);
      return;
    }

    String columnDefinitions =
        columns.entrySet().stream()
            .map(entry -> String.format("%s %s", entry.getKey(), entry.getValue()))
            .collect(Collectors.joining(", "));

    String query = String.format("CREATE TABLE %s (%s)", tableName, columnDefinitions);
    execute(query);
    Refraction.log(1, "Created table [%s]", tableName);
  }

  public boolean indexExists(String tableName, String indexName) throws SQLException {
    String query = String.format("SHOW INDEX FROM %s WHERE Key_name = '%s'", tableName, indexName);
    try (Statement statement = getConnection().createStatement();
        ResultSet result = statement.executeQuery(query)) {
      return result.next();
    }
  }

  public void insert(String tableName, Map<String, Object> data) throws SQLException {
    String columns = String.join(", ", data.keySet());
    String placeholders = data.keySet().stream().map(key -> "?").collect(Collectors.joining(", "));

    String query =
        String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, placeholders);

    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      int i = 1;
      for (Object value : data.values()) {
        preparedStatement.setObject(i++, value);
      }

      preparedStatement.executeUpdate();
    }
  }

  public boolean isRecordCreated(String tableName, String columnName, Object value)
      throws SQLException {
    String query = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?", tableName, columnName);
    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setObject(1, value);
      ResultSet result = preparedStatement.executeQuery();
      if (result.next()) {
        int count = result.getInt(1);
        return count > 0;
      }

      return false;
    }
  }

  public void update(
      String table, Map<String, Object> data, String condition, Object... conditionValues)
      throws SQLException {
    StringBuilder sql = new StringBuilder("UPDATE " + table + " SET ");
    List<Object> params = new ArrayList<>();

    for (Map.Entry<String, Object> entry : data.entrySet()) {
      sql.append(entry.getKey()).append(" = ?, ");
      params.add(entry.getValue());
    }

    sql.setLength(sql.length() - 2);
    sql.append(" WHERE ").append(condition);

    params.addAll(Arrays.asList(conditionValues));

    try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
      for (int i = 0; i < params.size(); i++) {
        statement.setObject(i + 1, params.get(i));
      }

      statement.executeUpdate();
    }
  }

  public ResultSet query(String select, String from, String where, String... parameters)
      throws SQLException {
    String query = String.format("SELECT %s FROM %s WHERE %s", select, from, where);
    PreparedStatement statement = connection.prepareStatement(query);

    for (int i = 0; i < parameters.length; i++) {
      statement.setString(i + 1, parameters[i]);
    }

    return statement.executeQuery();
  }

  private boolean isConnectionClosed() throws SQLException {
    return connection == null || connection.isClosed();
  }

  private void initialize() throws SQLException {
    if (!isDatabaseCreated()) {
      createDatabase();
    }

    connection.setCatalog(name);
    Refraction.log(0, "Connected to database [%s]", name);
  }

  private boolean isDatabaseCreated() throws SQLException {
    String query = "SHOW DATABASES LIKE ?";
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, name);
      ResultSet result = statement.executeQuery();
      return result.next();
    }
  }

  private void createDatabase() throws SQLException {
    String query = String.format("CREATE DATABASE %s", name);
    execute(query);
    Refraction.log(1, "Created database [%s]", name);
  }

  public void execute(String query, Object... params) throws SQLException {
    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      for (int i = 0; i < params.length; i++) {
        preparedStatement.setObject(i + 1, params[i]);
      }
      preparedStatement.executeUpdate();
    }
  }

  private boolean isTableCreated(String tableName) throws SQLException {
    String query =
        String.format(
            "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '%s' AND table_name = '%s'",
            name, tableName);
    try (Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(query)) {
      if (result.next()) {
        return result.getInt(1) > 0;
      }
    }

    return false;
  }

  public Connection getConnection() {
    return connection;
  }
}
