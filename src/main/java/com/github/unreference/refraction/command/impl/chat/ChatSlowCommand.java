package com.github.unreference.refraction.command.impl.chat;

import com.github.unreference.refraction.command.AbstractCommand;
import com.github.unreference.refraction.command.CommandContext;
import com.github.unreference.refraction.domain.model.Rank;
import com.github.unreference.refraction.event.ChatManager;
import com.github.unreference.refraction.util.MessageUtil;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class ChatSlowCommand extends AbstractCommand {
  public ChatSlowCommand() {
    super("slow", "Chat", "refraction.command.chat.slow", false, "s");
  }

  @Override
  protected Component getUsageMessage() {
    return MessageUtil.getPrefixedMessage(
        getPrefix(), "Usage: /%s /%s [duration]", getMainAliasUsed(), getAliasUsed());
  }

  @Override
  protected void generatePermissions() {
    Rank.SR_MOD.grantPermission(getPermission(), true);
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
      ChatManager.get().setSlowModeDuration(sender, 0);
      return;
    }

    try {
      int duration = Integer.parseInt(args[0]);

      if (duration == 0) {
        if (ChatManager.get().getSlowModeDuration() != 0) {
          ChatManager.get().setSlowModeDuration(sender, 0);
        }
      } else {
        ChatManager.get().setSlowModeDuration(sender, duration);
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
