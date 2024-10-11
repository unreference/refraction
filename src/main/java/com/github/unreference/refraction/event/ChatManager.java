package com.github.unreference.refraction.event;

import com.github.unreference.refraction.Refraction;
import com.github.unreference.refraction.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class ChatManager implements Listener {

  private static ChatManager instance;

  private int lockDuration = -1; // Lock duration in seconds (-1 means permanent lock)
  private boolean isChatLocked = false;
  private BukkitRunnable unlockTask = null;
  private long lockStartTime;

  private ChatManager() {}

  public static ChatManager get() {
    if (instance == null) {
      instance = new ChatManager();
    }
    return instance;
  }

  public void lockChat(CommandSender sender, int duration) {
    if (duration == 0) {
      unlockChat(sender);
      return;
    }

    if (isChatLocked) {
      sender.sendMessage(MessageUtil.getPrefixedMessage("Chat", "Chat is already locked."));
      return;
    }

    lockDuration = duration;
    lockStartTime = System.currentTimeMillis();
    isChatLocked = true;

    if (duration < 0) {
      sender.sendMessage(MessageUtil.getPrefixedMessage("Chat", "Locked chat &epermanently&7."));
      MessageUtil.broadcastMessage(
          MessageUtil.getPrefixedMessage("Chat", "Chat has been locked &epermanently&7!"));
    } else {
      sender.sendMessage(
          MessageUtil.getPrefixedMessage("Chat", "Locked chat for &e%s seconds.", duration));
      MessageUtil.broadcastMessage(
          MessageUtil.getPrefixedMessage(
              "Chat", "Chat has been locked for &e%d seconds&7!", duration));

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

      unlockTask.runTaskLater(
          Refraction.getPlugin(), duration * 20L);
    }
  }

  public void unlockChat(CommandSender sender) {
    if (!isChatLocked) {
      if (sender != null) {
        sender.sendMessage(MessageUtil.getPrefixedMessage("Chat", "Chat is not locked."));
      }

      return;
    }

    isChatLocked = false;

    if (unlockTask != null) {
      unlockTask.cancel();
      unlockTask = null;
    }

    if (sender != null) {
      sender.sendMessage(MessageUtil.getPrefixedMessage("Chat", "Unlocked chat."));
    }

    MessageUtil.broadcastMessage(MessageUtil.getPrefixedMessage("Chat", "Chat has been unlocked!"));
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
}
