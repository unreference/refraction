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

    Component finalMessage =
        Component.text()
            .append(Component.text(0).colorIfAbsent(NamedTextColor.GRAY))
            .appendSpace()
            .append(FormatUtil.toUpperCase(rank.getFormattedPrefix()))
            .append(
                MessageUtil.getMessage(player.getName()).color(NamedTextColor.YELLOW).appendSpace())
            .append(event.message())
            .color(NamedTextColor.WHITE)
            .build();

    MessageUtil.broadcastMessage(finalMessage);
  }
}
