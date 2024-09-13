package me.unreference.refraction;

import me.unreference.refraction.managers.DatabaseManager;
import me.unreference.refraction.managers.PlayerDataManager;
import me.unreference.refraction.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class Refraction extends JavaPlugin {
    boolean isFatalError = false;

    public static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("Refraction");
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        String host = getConfig().getString("database.host");
        int port = getConfig().getInt("database.port");
        String user = getConfig().getString("database.user");
        String password = getConfig().getString("database.password");
        String database = getConfig().getString("database.name");

        DatabaseManager databaseManager = DatabaseManager.get(host, port, user, password, database);

        try {
            databaseManager.connect();

            PlayerDataManager playerDataManager = PlayerDataManager.get(databaseManager);
            playerDataManager.create();

            addListener(new PlayerManager(databaseManager));
        } catch (SQLException exception) {
            getLogger().severe("FATAL (DatabaseManager): " + exception.getMessage());
            isFatalError = true;
        }

        if (isFatalError) {
            getLogger().severe("One of more fatal errors have occurred!  Disabling plugin!");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        DatabaseManager.get(null, 0, null, null, null).close();
    }

    private void addListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }
}
