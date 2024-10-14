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
  private static final String PERMISSION_CHAT_BYPASS = "refraction.server.chat.bypass";

  public ChatListener() {
    Rank.TRAINEE.grantPermission(PERMISSION_CHAT_BYPASS, true);
  }

  @EventHandler
  public void onAsyncChat(AsyncChatEvent event) {
    event.setCancelled(true);
    Player player = event.getPlayer();

    ServerUtil.runAsync(
        () -> {
          Rank rank = getPlayerRank(player);

          if (isChatLocked(player, rank)) return;

          if (isSlowModeActive(player, rank)) return;

          Component finalMessage = getMessage(event, player, rank);
          ServerUtil.runSync(() -> MessageUtil.broadcastMessage(finalMessage));
        });
  }

  private Rank getPlayerRank(Player player) {
    return Rank.getRankFromId(AccountRanksRepositoryManager.get().getRank(player.getUniqueId()));
  }

  private boolean isChatLocked(Player player, Rank rank) {
    if (ChatManager.get().isChatLocked() && !rank.isPermitted(PERMISSION_CHAT_BYPASS)) {
      player.sendMessage(
          MessageUtil.getPrefixedMessage(
              "Chat",
              "Shh... chat is currently locked %s&7.",
              (ChatManager.get().getLockDuration() == -1) ? "&epermanently" : "for &e%d %s",
              ChatManager.get().getRemainingLockTime(),
              (ChatManager.get().getLockDuration() > 1) ? "seconds" : "second"));

      return true;
    }

    return false;
  }

  private boolean isSlowModeActive(Player player, Rank rank) {
    if (ChatManager.get().getSlowModeDuration() > 0 && !rank.isPermitted(PERMISSION_CHAT_BYPASS)) {
      if (ChatManager.get().isOnCooldown(player)) {
        long remainingTime =
            ChatManager.get().getSlowModeDuration()
                - (System.currentTimeMillis()
                        - ChatManager.get().getLastMessageTimestamps().get(player.getUniqueId()))
                    / 1000;

        player.sendMessage(
            MessageUtil.getPrefixedMessage(
                "Chat",
                "Shh... slow mode is enabled. You can send another message in &e%d %s&7.",
                remainingTime,
                (remainingTime > 1) ? "seconds" : "second"));
        return true;
      }
    }
    return false;
  }

  private Component getMessage(AsyncChatEvent event, Player player, Rank rank) {
    Component playerLevel = Component.text(0).colorIfAbsent(NamedTextColor.GRAY);
    Component playerRank = rank.getPrefix() != null ? rank.getRankWithHover() : Component.empty();
    Component spaceMayhaps =
        rank.getPrefix() != null ? Component.empty().appendSpace() : Component.empty();
    Component playerName = MessageUtil.getMessage(player.getName()).color(NamedTextColor.YELLOW);
    Component playerMessage = event.message().colorIfAbsent(NamedTextColor.WHITE);

    return Component.text()
        .append(playerLevel)
        .appendSpace()
        .append(playerRank)
        .append(spaceMayhaps)
        .append(playerName)
        .appendSpace()
        .append(playerMessage)
        .build();
  }
}
