package me.unreference.refraction.managers;

import me.unreference.refraction.Refraction;

import java.sql.*;
import java.util.Map;

public class DatabaseManager {
    private static DatabaseManager instance;

    private Connection connection;
    private final String host;
    private final int port;
    private final String user;
    private final String password;
    private final String database;

    private DatabaseManager(String host, int port, String user, String password, String database) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.database = database;
    }

    public static DatabaseManager get(String host, int port, String user, String password, String database) {
        if (instance == null) {
            instance = new DatabaseManager(host, port, user, password, database);
        }

        return instance;
    }

    public void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port, user, password);
            Refraction.getPlugin().getLogger().info("Connected to server [" + host + ":" + port + "]");

            if (!databaseExists(connection)) {
                createDatabase(connection);
                Refraction.getPlugin().getLogger().info("Created database [" + database + "]");
            }

            connection.setCatalog(database);
            Refraction.getPlugin().getLogger().info("Connected to database [" + database + "]");
        }
    }

    private boolean databaseExists(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery("SHOW DATABASES LIKE '" + database + "'");
            return result.next();
        } catch (SQLException exception) {
            return false;
        }
    }

    private void createDatabase(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE DATABASE " + database);
        }
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
                Refraction.getPlugin().getLogger().info("Closed the database connection.");
            } catch (SQLException exception) {
                Refraction.getPlugin().getLogger().severe("Failed to close database connection: " + exception.getMessage());
            }
        }
    }

    public void createTable(String tableName, Map<String, String> columns) throws SQLException {
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (");
        query.append("id INT AUTO_INCREMENT PRIMARY KEY, ");
        columns.forEach((name, type) -> query.append(name).append(" ").append(type).append(", "));

        if (query.charAt(query.length() - 2) == ',') {
            query.setLength(query.length() - 2);
        }

        query.append(")");

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query.toString());
            Refraction.getPlugin().getLogger().info("Created table [" + tableName + "]");
        }
    }

    public void insertData(String tableName, Map<String, Object> data) throws SQLException {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();

        data.forEach((key, value) -> {
            columns.append(key).append(", ");
            values.append("?, ");
        });

        // Removes trailing comma and space
        String query = "INSERT INTO " + tableName + " (" + columns.substring(0, columns.length() - 2) +
                ") VALUES (" + values.substring(0, values.length() - 2) + ")";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            int i = 1;
            for (Object value : data.values()) {
                preparedStatement.setObject(i++, value);
            }

            preparedStatement.executeUpdate();
            Refraction.getPlugin().getLogger().info("Inserted data [" + tableName + "]");
        }
    }

    public boolean recordExists(String tableName, String columnName, Object value) throws SQLException {
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + columnName + " = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, value);

            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                return result.getInt(1) > 0;
            }
        }

        return false;
    }

    public void upsertData(String tableName, Map<String, Object> data) throws SQLException {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        StringBuilder updates = new StringBuilder();

        for (String key : data.keySet()) {
            columns.append(key).append(", ");
            values.append("?, ");
            updates.append(key).append(" = VALUES (").append(key).append("), ");
        }

        // Removes trailing commas
        columns.setLength(columns.length() - 2);
        values.setLength(values.length() - 2);
        updates.setLength(updates.length() - 2);

        String query = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ") " +
                "ON DUPLICATE KEY UPDATE " + updates;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            int i = 1;
            for (Object value : data.values()) {
                preparedStatement.setObject(i++, value);
            }

            preparedStatement.executeUpdate();
        }
    }

    public void updateData(String tableName, Map<String, Object> data, String keyColumn, String keyValue) throws SQLException {
        StringBuilder setClause = new StringBuilder();

        for (String key : data.keySet()) {
            setClause.append(key).append("= ?, ");
        }

        // Remove last comma and space
        setClause.setLength(setClause.length() - 2);

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
}
