package com.github.unreference.refraction.event;

import com.github.unreference.refraction.Refraction;
import com.github.unreference.refraction.data.manager.AccountRanksRepositoryManager;
import com.github.unreference.refraction.data.manager.AccountsRepositoryManager;
import com.github.unreference.refraction.domain.model.AccountRanksRecord;
import com.github.unreference.refraction.domain.model.AccountsRecord;
import com.github.unreference.refraction.domain.model.Rank;
import com.github.unreference.refraction.util.ServerUtil;
import java.time.LocalDateTime;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
  private static final String PERMISSION_AUTO_OP = "refraction.server.auto-operator";

  public PlayerListener() {
    Rank.LEADER.grantPermission(PERMISSION_AUTO_OP, true);
  }

  @EventHandler
  public void onPlayerLogin(PlayerLoginEvent event) {
    Player player = event.getPlayer();
    String name = player.getName();
    UUID uuid = player.getUniqueId();
    LocalDateTime now = LocalDateTime.now();

    ServerUtil.runAsync(
        () -> {
          try {
            if (AccountsRepositoryManager.get().isNew(uuid)) {
              AccountsRecord account = new AccountsRecord(uuid.toString(), name, 0, 0, now, now);
              AccountsRepositoryManager.get().register(account);

              AccountRanksRecord accountRank =
                  new AccountRanksRecord(
                      uuid.toString(), Rank.PLAYER.getId(), Rank.PLAYER.isPrimary());
              AccountRanksRepositoryManager.get().register(accountRank);
            }

            AccountsRepositoryManager.get().update(uuid, name, now);

            Rank rank = Rank.getRankFromId(AccountRanksRepositoryManager.get().getRank(uuid));
            setOp(player, rank);
          } catch (Exception exception) {
            player.kick(Component.text("Timed out"));
          }
        });
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    event.joinMessage(null);
  }

  @EventHandler
  public void onRankChange(RankChangeEvent event) {
    Player player = event.getPlayer();

    ServerUtil.runAsync(
        () -> {
          try {
            Rank rank =
                Rank.getRankFromId(
                    AccountRanksRepositoryManager.get().getRank(player.getUniqueId()));
            setOp(player, rank);
          } catch (Exception exception) {
            player.kick(Component.text("Timed out"));
          }
        });
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    String name = player.getName();
    UUID uuid = player.getUniqueId();
    LocalDateTime now = LocalDateTime.now();

    AccountsRepositoryManager.get().update(uuid, name, now);
    event.quitMessage(null);
  }

  private void setOp(Player player, Rank rank) {
    ServerUtil.runSync(
        () -> {
          boolean wasOp = player.isOp();
          player.setOp(rank.isPermitted(PERMISSION_AUTO_OP));
          boolean isOp = player.isOp();

          if (isOp != wasOp) {
            Refraction.log(1, "Updated operator status [%s] -> %s", player.getName(), isOp);
          }
        });
  }
}
