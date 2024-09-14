package me.unreference.refraction.managers;

import java.sql.*;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static me.unreference.refraction.Refraction.getPlugin;
import static me.unreference.refraction.Refraction.log;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;

    private final String host = getPlugin().getConfig().getString("database.host");
    private final int port = getPlugin().getConfig().getInt("database.port");
    private final String user = getPlugin().getConfig().getString("database.user");
    private final String password = getPlugin().getConfig().getString("database.password");
    private final String name = getPlugin().getConfig().getString("database.name");

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
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port, user, password);
            log(0, "DatabaseManager", "Connected to server [" + host + ":" + port + "]");
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
        log(0, "DatabaseManager", "Connected to database [" + name + "]");
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
        String query = "CREATE DATABASE " + name;
        executeUpdate(query);
        log(0, "DatabaseManager", "Created database [" + name + "]");
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
                log(0, "DatabaseManager", "Closed the database connection.");
            } catch (SQLException exception) {
                log(2, "DatabaseManager", "Failed to close database connection.");
                log(2, "DatabaseManager", Arrays.toString(exception.getStackTrace()));
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
            log(0, "DatabaseManager", "Found table [" + tableName + "]");
            return;
        }

        String columnDefinitions = columns.entrySet().stream()
                .map(entry -> entry.getKey() + " " + entry.getValue())
                .collect(Collectors.joining(", "));
        String query = "CREATE TABLE " + tableName + " (id INT AUTO_INCREMENT PRIMARY KEY, " + columnDefinitions + ")";

        executeUpdate(query);
        log(0, "DatabaseManager", "Created table [" + tableName + "]");
    }

    public boolean tableExists(String tableName) throws SQLException {
        String query = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '" + name + "' AND table_name = '" + tableName + "'";
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
        String query = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            int i = 1;
            for (Object value : data.values()) {
                preparedStatement.setObject(i++, value);
            }

            preparedStatement.executeUpdate();
            log(0, "DatabaseManager", "Inserted data into [" + tableName + "]");
        }
    }

    public boolean recordExists(String tableName, String columnName, Object value) throws SQLException {
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + columnName + " = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, value);
            ResultSet result = preparedStatement.executeQuery();
            return result.next() && result.getInt(1) > 0;
        }
    }

    public void updateData(String tableName, Map<String, Object> data, String keyColumn, Object keyValue) throws SQLException {
        String setClause = data.keySet().stream()
                .map(key -> key + " = ?")
                .collect(Collectors.joining(", "));

        String query = "UPDATE " + tableName + " SET " + setClause + " WHERE " + keyColumn + " = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            int i = 1;
            for (Object value : data.values()) {
                preparedStatement.setObject(i++, value);
            }

            preparedStatement.setObject(i, keyValue);
            preparedStatement.executeUpdate();
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
