package com.github.unreference.refraction;

import com.github.unreference.refraction.data.repository.PlayerDataRepository;
import com.github.unreference.refraction.listener.CommandListener;
import com.github.unreference.refraction.listener.PlayerListener;
import com.github.unreference.refraction.service.DatabaseService;
import com.github.unreference.refraction.service.PlayerDataRepositoryService;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

public final class Refraction extends JavaPlugin {
    private static DatabaseService databaseService;
    private static PlayerDataRepositoryService playerDataRepositoryService;
    boolean isFatalError = false;


    public static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("Refraction");
    }

    public static void log(int severity, String message, Object... args) {
        Optional<String> callerInfo = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(frames -> frames
                        .skip(1)
                        .findFirst()
                        .map(frame -> {
                            String className = frame.getDeclaringClass().getSimpleName();
                            int lineNumber = frame.getLineNumber();
                            return className + ".java:" + lineNumber;
                        }));

        String callerDetails = callerInfo.map(className -> "(" + className + "): ").orElse("");
        String msg = callerDetails + String.format(message, args);

        switch (severity) {
            case 0 -> getPlugin().getLogger().info(msg);
            case 1 -> getPlugin().getLogger().warning(msg);
            default -> getPlugin().getLogger().severe(msg);
        }
    }

    public static DatabaseService getDatabaseService() {
        return databaseService;
    }

    public static PlayerDataRepositoryService getPlayerDataRepositoryService() {
        return playerDataRepositoryService;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        try {
            databaseService = new DatabaseService();

            PlayerDataRepository playerDataRepository = new PlayerDataRepository(databaseService);
            playerDataRepositoryService = new PlayerDataRepositoryService(playerDataRepository);

            databaseService.connect();
            playerDataRepositoryService.create();

            registerListener(new PlayerListener());
            registerListener(new CommandListener());
        } catch (SQLException | NullPointerException exception) {
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
        databaseService.close();
    }

    private void registerListener(Listener listener) throws NullPointerException {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }
}
