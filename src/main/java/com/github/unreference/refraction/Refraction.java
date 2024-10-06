package com.github.unreference.refraction;

import com.github.unreference.refraction.data.manager.AccountRanksRepositoryManager;
import com.github.unreference.refraction.data.manager.AccountsRepositoryManager;
import com.github.unreference.refraction.data.manager.DatabaseManager;
import com.github.unreference.refraction.listener.CommandListener;
import com.github.unreference.refraction.listener.PlayerListener;
import com.github.unreference.refraction.model.Rank;
import com.github.unreference.refraction.util.UtilMessage;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Refraction extends JavaPlugin {
  boolean isFatalError = false;

  public static Plugin getPlugin() {
    return Bukkit.getPluginManager().getPlugin("Refraction");
  }

  public static void log(int severity, String message, Object... args) {
    Optional<String> callerInfo =
        StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
            .walk(
                frames ->
                    frames
                        .skip(1)
                        .findFirst()
                        .map(
                            frame -> {
                              String className = frame.getDeclaringClass().getSimpleName();
                              int line = frame.getLineNumber();
                              return className + ".java:" + line;
                            }));

    String callerDetails = callerInfo.map(className -> "(" + className + "): ").orElse("");
    String msg = callerDetails + String.format(message, args);

    switch (severity) {
      case 0 -> getPlugin().getLogger().info(msg);
      case 1 -> getPlugin().getLogger().warning(msg);
      default -> getPlugin().getLogger().severe(msg);
    }
  }

  @Override
  public void onEnable() {
    saveDefaultConfig();

    if (isConnectionSuccessful()) {
      registerListeners();
      handleWhitelist();
    }
  }

  @Override
  public void onDisable() {
    DatabaseManager.get().close();
  }

  private boolean isConnectionSuccessful() {
    try {
      DatabaseManager.get().connect();
      AccountsRepositoryManager.get().create();
      AccountRanksRepositoryManager.get().create();
      return true;
    } catch (SQLException | NullPointerException exception) {
      logError(exception);
      return false;
    }
  }

  private void registerListeners() {
    registerListener(new PlayerListener());
    registerListener(new CommandListener());
  }

  private void handleWhitelist() {
    if (getServer().hasWhitelist()) {
      logWhitelistIntent();
      kickNonAdmins();
    }

    if (isFatalError) {
      logFatalError();
      enforceWhitelist();
      kickNonAdmins();
    }
  }

  private void logError(Exception exception) {
    log(2, exception.getMessage());
    log(2, Arrays.toString(exception.getStackTrace()));
    isFatalError = true;
  }

  private void logWhitelistIntent() {
    log(1, "***************************************");
    log(1, "  The whitelist is currently enabled.  ");
    log(1, "          Is this intentional?         ");
    log(1, "***************************************");
  }

  private void logFatalError() {
    log(2, "***************************************");
    log(2, "One or more fatal errors have occurred.");
    log(2, "          Whitelist enabled.           ");
    log(2, "***************************************");
  }

  private void enforceWhitelist() {
    getServer().setWhitelist(true);
    getServer().setWhitelistEnforced(true);
  }

  private void kickNonAdmins() {
    if (!getServer().getOnlinePlayers().isEmpty()) {
      for (Player player : Bukkit.getOnlinePlayers()) {
        Rank rank =
            Rank.getRankFromId(AccountRanksRepositoryManager.get().getRank(player.getUniqueId()));
        if (rank != Rank.ADMIN && rank != Rank.OWNER) {
          player.kick(
              Component.text(
                  "The server is currently offline for maintenance and upgrades.\n"
                      + "Please check back soon!"));
        }
      }

      UtilMessage.broadcastMessage(
          UtilMessage.getPrefixedMessage(
              "Refraction",
              isFatalError
                  ? "One or more fatal errors have occurred. Whitelist enabled."
                  : "The whitelist is currently enabled. Is this intentional?"));
    }
  }

  private void registerListener(Listener listener) throws NullPointerException {
    this.getServer().getPluginManager().registerEvents(listener, this);
  }
}
