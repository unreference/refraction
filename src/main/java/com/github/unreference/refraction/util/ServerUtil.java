package com.github.unreference.refraction.util;

import com.github.unreference.refraction.Refraction;
import org.bukkit.event.Event;

public class ServerUtil {
  private ServerUtil() {}

  public static void runAsync(Runnable runnable) {
    Refraction.getPlugin()
        .getServer()
        .getScheduler()
        .runTaskAsynchronously(Refraction.getPlugin(), runnable);
  }

  public static void runSync(Runnable runnable) {
    Refraction.getPlugin().getServer().getScheduler().runTask(Refraction.getPlugin(), runnable);
  }

  public static void callEvent(Event event) {
    Refraction.getPlugin().getServer().getPluginManager().callEvent(event);
  }
}
