package com.github.unreference.refraction.command.impl.chat;

import com.github.unreference.refraction.command.AbstractParameterizedCommand;
import com.github.unreference.refraction.domain.model.Rank;
import com.github.unreference.refraction.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class ChatCommand extends AbstractParameterizedCommand {
  public ChatCommand() {
    super("chat", "Chat", "refraction.command.chat", false);

    addSubcommand(new ChatLockCommand());
  }

  @Override
  protected void execute(CommandSender sender, String[] args) {
    sender.sendMessage(MessageUtil.getPrefixedMessage(getPrefix(), "Command List:"));
    sender.sendMessage(
        MessageUtil.getMessageWithHover(
                "- /%s lock [duration]",
                "Locks the chat for the specified duration.", getAliasUsed())
            .color(Rank.MOD.getRankColor()));
  }

  @Override
  protected Component getUsageMessage() {
    return null;
  }

  @Override
  protected void generatePermissions() {
    Rank.MOD.grantPermission(getPermission(), true);
  }
}
