package me.unreference.refraction;

import me.unreference.refraction.managers.CommandManager;
import me.unreference.refraction.managers.DatabaseManager;
import me.unreference.refraction.managers.PlayerDataManager;
import me.unreference.refraction.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Arrays;

public final class Refraction extends JavaPlugin {
    boolean isFatalError = false;

    public static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("Refraction");
    }

    public static void log(int severity, String prefix, String message) {
        String msg = prefix.isBlank() ? message : "(" + prefix + "): " + message;

        switch (severity) {
            case 0:
                getPlugin().getLogger().info(msg);
                break;
            case 1:
                getPlugin().getLogger().warning(msg);
                break;
            default:
                getPlugin().getLogger().severe(msg);
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        DatabaseManager databaseManager = DatabaseManager.get();

        try {
            databaseManager.connect();

            PlayerDataManager playerDataManager = PlayerDataManager.get(databaseManager);
            playerDataManager.create();

            addListener(new PlayerManager(databaseManager));
            addListener(new CommandManager());
        } catch (SQLException exception) {
            getLogger().severe("FATAL (DatabaseManager): " + exception.getMessage());
            getLogger().severe("FATAL (DatabaseManager): " + Arrays.toString(exception.getStackTrace()));
            isFatalError = true;
        }

        if (isFatalError) {
            getLogger().severe("One of more fatal errors have occurred!  Disabling plugin!");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        DatabaseManager.get().close();
    }

    private void addListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }


}
