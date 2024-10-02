package com.github.unreference.refraction.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RouletteEvent extends Event {
  public static final HandlerList handlerList = new HandlerList();
  private final Player player;
  private final int result;

  public RouletteEvent(Player player, int result) {
    this.player = player;
    this.result = result;
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

  public int getResult() {
    return result;
  }
}
