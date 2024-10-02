package com.github.unreference.refraction;

import com.github.unreference.refraction.listener.CommandListener;
import com.github.unreference.refraction.listener.PlayerListener;
import com.github.unreference.refraction.manager.DatabaseManager;
import com.github.unreference.refraction.manager.PlayerDataRepositoryManager;
import com.github.unreference.refraction.model.Rank;
import com.github.unreference.refraction.util.MessageUtil;
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

  @Override
  public void onEnable() {
    saveDefaultConfig();

    try {
      DatabaseManager.get().connect();
      PlayerDataRepositoryManager.get().create();

      registerListener(new PlayerListener());
      registerListener(new CommandListener());
    } catch (SQLException | NullPointerException exception) {
      log(2, exception.getMessage());
      log(2, Arrays.toString(exception.getStackTrace()));
      isFatalError = true;
    }

    if (!isFatalError && getServer().hasWhitelist()) {
      log(1, "***************************************");
      log(1, "  The whitelist is currently enabled.  ");
      log(1, "          Is this intentional?         ");
      log(1, "***************************************");

      if (!getServer().getOnlinePlayers().isEmpty()) {
        for (Player player : Bukkit.getOnlinePlayers()) {
          Rank rank =
              Rank.getRankFromId(PlayerDataRepositoryManager.get().getRank(player.getName()));
          if (rank != Rank.ADMIN && rank != Rank.OWNER) {
            player.kick(
                Component.text(
                    "The server is currently offline for maintenance and upgrades.\nPlease check back soon!"));
          }
        }

        MessageUtil.broadcastMessage(
            MessageUtil.getPrefixedMessage(
                "Refraction", "The whitelist is currently enabled. Is this intentional?"));
      }
    }

    if (isFatalError) {
      log(2, "***************************************");
      log(2, "One or more fatal errors have occurred.");
      log(2, "          Whitelist enabled.           ");
      log(2, "***************************************");

      getServer().setWhitelist(true);
      getServer().setWhitelistEnforced(true);

      if (!getServer().getOnlinePlayers().isEmpty()) {
        for (Player player : Bukkit.getOnlinePlayers()) {
          Rank rank =
              Rank.getRankFromId(PlayerDataRepositoryManager.get().getRank(player.getName()));
          if (rank != Rank.ADMIN && rank != Rank.OWNER) {
            player.kick(
                Component.text(
                    "The server is currently offline for maintenance and upgrades.\nPlease check back soon!"));
          }
        }

        MessageUtil.broadcastMessage(
            MessageUtil.getPrefixedMessage(
                "Refraction", "One or more fatal errors have occurred. Whitelist enabled."));
      }
    }
  }

  @Override
  public void onDisable() {
    DatabaseManager.get().close();
  }

  private void registerListener(Listener listener) throws NullPointerException {
    this.getServer().getPluginManager().registerEvents(listener, this);
  }
}
