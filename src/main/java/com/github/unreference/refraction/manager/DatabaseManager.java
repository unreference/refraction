package com.github.unreference.refraction.manager;

import com.github.unreference.refraction.Refraction;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DatabaseManager {
    private static DatabaseManager instance;
    private final String host = Refraction.getPlugin().getConfig().getString("database.host");
    private final int port = Refraction.getPlugin().getConfig().getInt("database.port");
    private final String user = Refraction.getPlugin().getConfig().getString("database.user");
    private final String password = Refraction.getPlugin().getConfig().getString("database.password");
    private final String name = Refraction.getPlugin().getConfig().getString("database.name");
    private Connection connection;

    private DatabaseManager() {
    }

    public static synchronized DatabaseManager get() {
        if (instance == null) {
            instance = new DatabaseManager();
        }

        return instance;
    }

    public void connect() throws SQLException {
        if (isConnectionClosed()) {
            connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d", host, port), user, password);
            Refraction.log(0, String.format("Connected to server [%s:%d]", host, port));
            initializeDatabase();
        }
    }

    private boolean isConnectionClosed() throws SQLException {
        return connection == null || connection.isClosed();
    }

    private void initializeDatabase() throws SQLException {
        if (!databaseExists()) {
            createDatabase();
        }

        connection.setCatalog(name);
        Refraction.log(0, String.format("Connected to database [%s]", name));
    }

    private boolean databaseExists() throws SQLException {
        String query = "SHOW DATABASES LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            ResultSet result = statement.executeQuery();
            return result.next();
        }
    }

    private void createDatabase() throws SQLException {
        String query = String.format("CREATE DATABASE %s", name);
        executeUpdate(query);
        Refraction.log(1, String.format("Created database [%s]", name));
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
                Refraction.log(0, "Closed the database connection.");
            } catch (SQLException exception) {
                Refraction.log(2, "Failed to close database connection: %s", exception.getMessage());
                Refraction.log(2, Arrays.toString(exception.getStackTrace()));
            }
        }
    }

    private void executeUpdate(String query) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        }
    }

    public void createTable(String tableName, Map<String, String> columns) throws SQLException {
        if (tableExists(tableName)) {
            Refraction.log(0, "Found table [%s]", tableName);
            return;
        }

        String columnDefinitions = columns.entrySet().stream()
                .map(entry -> String.format("%s %s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(", "));

        String query = String.format("CREATE TABLE %s (%s)", tableName, columnDefinitions);
        executeUpdate(query);
        Refraction.log(1, "Created table [%s]", tableName);
    }

    public boolean tableExists(String tableName) throws SQLException {
        String query = String.format("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '%s' AND table_name = '%s'", name, tableName);
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }

        return false;
    }

    public void insertData(String tableName, Map<String, Object> data) throws SQLException {
        String columns = String.join(", ", data.keySet());
        String placeholders = data.keySet().stream().map(key -> "?").collect(Collectors.joining(", "));

        String query = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, placeholders);
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            int i = 1;
            for (Object value : data.values()) {
                preparedStatement.setObject(i++, value);
            }

            preparedStatement.executeUpdate();
        }
    }

    public boolean recordExists(String tableName, String columnName, Object value) throws SQLException {
        String query = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?", tableName, columnName);
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, value);
            ResultSet result = preparedStatement.executeQuery();
            return result.next() && result.getInt(1) > 0;
        }
    }

    public void updateData(String table, Map<String, Object> data, String conditionColumn, Object conditionValue) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE " + table + " SET ");
        List<Object> params = new ArrayList<>();

        for (String key : data.keySet()) {
            sql.append(key).append(" = ?, ");
            params.add(data.get(key));
        }

        sql.setLength(sql.length() - 2); // Remove trailing comma
        sql.append(" WHERE ").append(conditionColumn).append(" = ?"); // Add WHERE clause
        params.add(conditionValue); // Add condition value to params

        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }

            statement.executeUpdate();
        }
    }


    public ResultSet queryData(String select, String from, String where, String... parameters) throws SQLException {
        String query = String.format("SELECT %s FROM %s WHERE %s", select, from, where);
        PreparedStatement statement = connection.prepareStatement(query);

        for (int i = 0; i < parameters.length; i++) {
            statement.setString(i + 1, parameters[i]);
        }

        return statement.executeQuery();
    }

    public Connection getConnection() {
        return connection;
    }
}
