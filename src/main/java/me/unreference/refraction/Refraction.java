package me.unreference.refraction;

import me.unreference.refraction.managers.DatabaseManager;
import me.unreference.refraction.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class Refraction extends JavaPlugin {
    private DatabaseManager databaseManager;

    public static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("Refraction");
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        String host = getConfig().getString("database.host");
        int port = getConfig().getInt("database.port");
        String database = getConfig().getString("database.name");
        String user = getConfig().getString("database.user");
        String password = getConfig().getString("database.password");

        databaseManager = new DatabaseManager(host, port, database, user, password);

        try {
            databaseManager.connect();
            addManager(new PlayerManager());
        } catch (SQLException exception) {
            getLogger().severe("FATAL (DatabaseManager): Failed to connect: " + exception.getMessage());
        }

        getLogger().severe("One of more fatal errors have occurred!  Disabling plugin!");
        getServer().getPluginManager().disablePlugin(this);
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
    }

    private void addManager(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }
}
