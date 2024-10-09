package com.github.unreference.refraction.event;

import com.github.unreference.refraction.data.manager.AccountRanksRepositoryManager;
import com.github.unreference.refraction.domain.model.Rank;
import com.github.unreference.refraction.util.FormatUtil;
import com.github.unreference.refraction.util.MessageUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

  @EventHandler
  public void onAsyncChat(AsyncChatEvent event) {
    event.setCancelled(true);

    Player player = event.getPlayer();
    Rank rank =
        Rank.getRankFromId(AccountRanksRepositoryManager.get().getRank(player.getUniqueId()));

    Component playerLevel = Component.text(0).colorIfAbsent(NamedTextColor.GRAY);
    Component playerRank =
        rank.getPrefix() != null
            ? FormatUtil.toUpperCase(rank.getFormattedPrefix())
                .hoverEvent(
                    FormatUtil.toUpperCase(rank.getFormattedPrefix())
                        .appendNewline()
                        .append(MessageUtil.getMessage(rank.getDescription())))
                .appendSpace()
            : Component.empty();
    Component playerName = MessageUtil.getMessage(player.getName()).color(NamedTextColor.YELLOW);
    Component playerMessage = event.message().colorIfAbsent(NamedTextColor.WHITE);

    Component finalMessage =
        playerLevel
            .appendSpace()
            .append(playerRank)
            .append(playerName)
            .appendSpace()
            .append(playerMessage);

    MessageUtil.broadcastMessage(finalMessage);
  }
}
