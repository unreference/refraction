package com.github.unreference.refraction.util;

import com.github.unreference.refraction.Refraction;

public class UtilServer {
  private UtilServer() {}

  public static void runAsync(Runnable runnable) {
    Refraction.getPlugin()
        .getServer()
        .getScheduler()
        .runTaskAsynchronously(Refraction.getPlugin(), runnable);
  }

  public static void runSync(Runnable runnable) {
    Refraction.getPlugin().getServer().getScheduler().runTask(Refraction.getPlugin(), runnable);
  }
}
