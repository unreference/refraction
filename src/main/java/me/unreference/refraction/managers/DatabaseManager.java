package me.unreference.refraction.managers;

import me.unreference.refraction.Refraction;

import java.sql.*;

public class DatabaseManager {
    private Connection connection;
    private final String host;
    private final int port;
    private final String user;
    private final String password;
    private final String database;

    public DatabaseManager(String host, int port, String user, String password, String database) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.database = database;
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port, user, password);
        Refraction.getPlugin().getLogger().info("Successfully connected to server [" + host + ":" + port + "]");

        if (!databaseExists(connection)) {
            createDatabase(connection);
            Refraction.getPlugin().getLogger().info("Successfully created database [" + database + "]");
        }

        connection.setCatalog(database);
        Refraction.getPlugin().getLogger().info("Successfully connected to database [" + database + "]");
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
                Refraction.getPlugin().getLogger().info("Successfully closed the database connection.");
            } catch (SQLException exception) {
                Refraction.getPlugin().getLogger().severe("Failed to close database connection: " + exception.getMessage());
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
