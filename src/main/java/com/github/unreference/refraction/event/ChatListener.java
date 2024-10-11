package com.github.unreference.refraction.event;

import com.github.unreference.refraction.data.manager.AccountRanksRepositoryManager;
import com.github.unreference.refraction.domain.model.Rank;
import com.github.unreference.refraction.util.MessageUtil;
import com.github.unreference.refraction.util.ServerUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {
  private final String bypassChatManagementPermission = "refraction.server.chat.bypass";

  public ChatListener() {
    Rank.TRAINEE.grantPermission(bypassChatManagementPermission, true);
  }

  @EventHandler
  public void onAsyncChat(AsyncChatEvent event) {
    event.setCancelled(true);

    Player player = event.getPlayer();

    ServerUtil.runAsync(
        () -> {
          Rank rank =
              Rank.getRankFromId(AccountRanksRepositoryManager.get().getRank(player.getUniqueId()));

          if (ChatManager.get().isChatLocked()
              && !rank.isPermitted(bypassChatManagementPermission)) {
            player.sendMessage(
                MessageUtil.getPrefixedMessage(
                    "Chat",
                    "Shh... chat is currently locked "
                        + (ChatManager.get().getLockDuration() == -1
                            ? "&epermanently&7."
                            : "for &e%d seconds&7."),
                    ChatManager.get().getRemainingLockTime()));
            return;
          }

          Component playerLevel = Component.text(0).colorIfAbsent(NamedTextColor.GRAY);
          Component playerRank =
              rank.getPrefix() != null ? rank.getRankWithHover() : Component.empty();
          Component spaceMayhaps =
              rank.getPrefix() != null ? Component.empty().appendSpace() : Component.empty();
          Component playerName =
              MessageUtil.getMessage(player.getName()).color(NamedTextColor.YELLOW);
          Component playerMessage = event.message().colorIfAbsent(NamedTextColor.WHITE);

          Component finalMessage =
              Component.text()
                  .append(playerLevel)
                  .appendSpace()
                  .append(playerRank)
                  .append(spaceMayhaps)
                  .append(playerName)
                  .appendSpace()
                  .append(playerMessage)
                  .build();

          ServerUtil.runSync(() -> MessageUtil.broadcastMessage(finalMessage));
        });
  }
}
