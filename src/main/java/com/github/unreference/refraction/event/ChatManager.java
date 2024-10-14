package com.github.unreference.refraction.event;

import com.github.unreference.refraction.Refraction;
import com.github.unreference.refraction.util.MessageUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class ChatManager implements Listener {
  private static final String PREFIX = "Chat";

  private static ChatManager instance;
  private final Map<UUID, Long> lastMessageTimestamps = new HashMap<>();
  private int lockDuration = -1;
  private boolean isChatLocked = false;
  private BukkitRunnable unlockTask = null;
  private long lockStartTime;
  private int slowModeDuration = 0;

  private ChatManager() {}

  public static ChatManager get() {
    if (instance == null) {
      instance = new ChatManager();
    }

    return instance;
  }

  public void lockChat(CommandSender sender, int duration) {
    if (duration < 1) {
      if (isChatLocked) {
        unlockChat(sender);
      } else {
        lockDuration = -1;
        isChatLocked = true;
        sender.sendMessage(MessageUtil.getPrefixedMessage(PREFIX, "Locked chat &epermanently&7."));
        MessageUtil.broadcastMessage(
            MessageUtil.getPrefixedMessage(PREFIX, "The chat has been locked &epermanently&7!"));
      }

      return;
    }

    lockDuration = duration;
    lockStartTime = System.currentTimeMillis();
    isChatLocked = true;

    sender.sendMessage(
        MessageUtil.getPrefixedMessage(
            PREFIX, "Locked chat for &e%d %s&7.", duration, (duration > 1) ? "seconds" : "second"));
    MessageUtil.broadcastMessage(
        MessageUtil.getPrefixedMessage(
            PREFIX,
            "The chat has been locked for &e%d %s&7!",
            duration,
            (duration > 1) ? "seconds" : "second"));

    if (unlockTask != null) {
      unlockTask.cancel();
    }

    unlockTask =
        new BukkitRunnable() {
          @Override
          public void run() {
            unlockChat(null);
          }
        };

    unlockTask.runTaskLater(Refraction.getPlugin(), duration * 20L);
  }

  public void unlockChat(CommandSender sender) {
    if (!isChatLocked) {
      if (sender != null) {
        sender.sendMessage(MessageUtil.getPrefixedMessage(PREFIX, "Chat is not locked."));
      }

      return;
    }

    isChatLocked = false;

    if (unlockTask != null) {
      unlockTask.cancel();
      unlockTask = null;
    }

    if (sender != null) {
      sender.sendMessage(MessageUtil.getPrefixedMessage(PREFIX, "Unlocked chat."));
    }

    MessageUtil.broadcastMessage(
        MessageUtil.getPrefixedMessage(PREFIX, "The chat has been unlocked!"));
  }

  public boolean isChatLocked() {
    return isChatLocked;
  }

  public int getLockDuration() {
    return lockDuration;
  }

  public long getRemainingLockTime() {
    if (!isChatLocked || lockDuration < 0) {
      return -1;
    }

    long elapsedMilliseconds = System.currentTimeMillis() - lockStartTime;
    long elapsedSeconds = elapsedMilliseconds / 1000;
    long remainingTime = lockDuration - elapsedSeconds;

    return Math.max(remainingTime, 0);
  }

  public void setSlowModeDuration(CommandSender sender, int duration) {
    if (duration < 1) {
      if (this.slowModeDuration == 0) {
        sender.sendMessage(
            MessageUtil.getPrefixedMessage(PREFIX, "Slow mode is already disabled."));
        return;
      }

      this.slowModeDuration = 0;
      lastMessageTimestamps.clear();

      sender.sendMessage(MessageUtil.getPrefixedMessage(PREFIX, "Disabled slow mode."));
      MessageUtil.broadcastMessage(
          MessageUtil.getPrefixedMessage(PREFIX, "Slow mode has been disabled!"));
      return;
    }

    if (this.slowModeDuration > 0) {
      MessageUtil.broadcastMessage(
          MessageUtil.getPrefixedMessage(
              PREFIX,
              "Slow mode duration has been updated to &e%d %s&7.",
              duration,
              (duration > 1) ? "seconds" : "second"));
    }

    this.slowModeDuration = duration;

    sender.sendMessage(
        MessageUtil.getPrefixedMessage(
            PREFIX,
            "Set slow mode to &e%d %s&7.",
            duration,
            (duration > 1) ? "seconds" : "second"));

    MessageUtil.broadcastMessage(
        MessageUtil.getPrefixedMessage(
            PREFIX,
            "Slow mode has been enabled! You can only send a message once every &e%d %s&7.",
            duration,
            (duration > 1) ? "seconds" : "second"));
  }

  public boolean isOnCooldown(Player player) {
    UUID id = player.getUniqueId();
    long currentTime = System.currentTimeMillis();

    if (lastMessageTimestamps.containsKey(id)) {
      long lastMessageTime = lastMessageTimestamps.get(id);
      long elapsedTime = (currentTime - lastMessageTime) / 1000;

      if (elapsedTime < slowModeDuration) {
        return true;
      }
    }

    lastMessageTimestamps.put(id, currentTime);
    return false;
  }

  public int getSlowModeDuration() {
    return slowModeDuration;
  }

  public Map<UUID, Long> getLastMessageTimestamps() {
    return lastMessageTimestamps;
  }
}
