package com.github.unreference.refraction.event;

import com.github.unreference.refraction.model.Rank;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RankChangeEvent extends Event {
  private static final HandlerList handlerList = new HandlerList();
  private final Player player;
  private final Rank newRank;

  public RankChangeEvent(Player player, Rank newRank) {
    this.player = player;
    this.newRank = newRank;
  }

  public static HandlerList getHandlerList() {
    return handlerList;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return handlerList;
  }

  public Player getPlayer() {
    return player;
  }

  public Rank getNewRank() {
    return newRank;
  }
}
