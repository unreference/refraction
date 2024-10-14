package com.github.unreference.refraction.command.impl.chat;

import com.github.unreference.refraction.command.AbstractCommand;
import com.github.unreference.refraction.command.CommandContext;
import com.github.unreference.refraction.domain.model.Rank;
import com.github.unreference.refraction.event.ChatManager;
import com.github.unreference.refraction.util.MessageUtil;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class ChatLockCommand extends AbstractCommand {
  public ChatLockCommand() {
    super("lock", "Chat", "refraction.command.chat.lock", false, "silence");
  }

  @Override
  protected Component getUsageMessage() {
    return MessageUtil.getPrefixedMessage(
        getPrefix(), "Usage: /%s %s [duration]", getMainAliasUsed(), getAliasUsed());
  }

  @Override
  protected void generatePermissions() {
    Rank.ADMIN.grantPermission(getPermission(), true);
  }

  @Override
  public void trigger(CommandContext context) {
    String[] args = context.getArgs();
    CommandSender sender = context.getSender();

    if (args.length > 1) {
      sender.sendMessage(getUsageMessage());
      return;
    }

    if (args.length == 0) {
      if (ChatManager.get().isChatLocked()) {
        ChatManager.get().unlockChat(sender);
      } else {
        ChatManager.get().lockChat(sender, 0);
      }

      return;
    }

    try {
      int duration = Integer.parseInt(args[0]);

      if (duration == 0) {
        ChatManager.get().unlockChat(sender);
      } else {
        ChatManager.get().lockChat(sender, duration);
      }
    } catch (NumberFormatException exception) {
      sender.sendMessage(
          MessageUtil.getPrefixedMessage(getPrefix(), "Invalid duration: &e%s", args[0]));
    }
  }

  @Override
  public List<String> tab(CommandSender sender, String alias, String[] args) {
    return List.of();
  }
}
