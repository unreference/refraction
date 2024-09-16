package me.unreference.refraction;

import me.unreference.refraction.manager.CommandManager;
import me.unreference.refraction.manager.DatabaseManager;
import me.unreference.refraction.manager.PlayerDataManager;
import me.unreference.refraction.manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

public final class Refraction extends JavaPlugin {
    boolean isFatalError = false;

    public static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("Refraction");
    }

    public static void log(int severity, String message, Object... args) {
        Optional<String> callingClassName = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(frames -> frames
                        .skip(1)
                        .findFirst()
                        .map(StackWalker.StackFrame::getDeclaringClass)
                        .map(Class::getSimpleName)
                );

        String caller = callingClassName.map(className -> "(" + className + "): ").orElse("");
        String msg = caller + String.format(message, args);

        switch (severity) {
            case 0 -> getPlugin().getLogger().info(msg);
            case 1 -> getPlugin().getLogger().warning(msg);
            default -> getPlugin().getLogger().severe(msg);
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
            log(2, exception.getMessage());
            log(2, Arrays.toString(exception.getStackTrace()));
            isFatalError = true;
        }

        if (isFatalError) {
            log(2, "***************************************");
            log(2, "One or more fatal errors have occurred.");
            log(2, "          Whitelist enabled.          ");
            log(2, "***************************************");

            getServer().setWhitelist(true);
            getServer().setWhitelistEnforced(true);
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
