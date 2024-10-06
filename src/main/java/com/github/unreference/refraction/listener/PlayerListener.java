package com.github.unreference.refraction.listener;

import com.github.unreference.refraction.Refraction;
import com.github.unreference.refraction.data.AccountRanksRecord;
import com.github.unreference.refraction.data.AccountsRecord;
import com.github.unreference.refraction.data.manager.AccountRanksRepositoryManager;
import com.github.unreference.refraction.data.manager.AccountsRepositoryManager;
import com.github.unreference.refraction.event.RankChangeEvent;
import com.github.unreference.refraction.model.Rank;
import java.time.LocalDateTime;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
  private static final String PERMISSION_AUTO_OP = "refraction.server.auto-operator";

  public PlayerListener() {
    Rank.LEADER.grantPermission(PERMISSION_AUTO_OP, true);
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    UUID uuid = player.getUniqueId();
    String name = player.getName();
    LocalDateTime now = LocalDateTime.now();

    if (AccountsRepositoryManager.get().isNew(uuid)) {
      AccountsRecord accountsData = new AccountsRecord(uuid.toString(), name, 0, 0, now, now);
      AccountRanksRecord accountRanksData =
          new AccountRanksRecord(uuid.toString(), Rank.DEFAULT.getId(), Rank.DEFAULT.isPrimary());
      AccountsRepositoryManager.get().register(accountsData);
      AccountRanksRepositoryManager.get().register(accountRanksData);
    }

    AccountsRepositoryManager.get().update(uuid, name, now);

    Rank playerRank =
        Rank.getRankFromId(AccountRanksRepositoryManager.get().getRank(player.getUniqueId()));

    boolean wasOp = player.isOp();
    player.setOp(playerRank.isPermitted(PERMISSION_AUTO_OP));
    boolean isOp = player.isOp();

    if (isOp != wasOp) {
      Refraction.log(1, "Updated operator status [%s] -> %s", player.getName(), isOp);
    }

    event.joinMessage(null);
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

  @EventHandler
  public void onRankChange(RankChangeEvent event) {
    Player player = event.getPlayer();

    Rank rank =
        Rank.getRankFromId(AccountRanksRepositoryManager.get().getRank(player.getUniqueId()));
    boolean wasOp = player.isOp();
    player.setOp(rank.isPermitted(PERMISSION_AUTO_OP));
    boolean isOp = player.isOp();

    if (isOp != wasOp) {
      Refraction.log(1, "Updated operator status [%s] -> %s", player.getName(), isOp);
    }
  }
}
