package com.github.unreference.refraction.command.impl.rank;

import com.github.unreference.refraction.command.AbstractParameterizedCommand;
import com.github.unreference.refraction.model.Rank;
import com.github.unreference.refraction.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class RanksCommand extends AbstractParameterizedCommand {
  public RanksCommand() {
    super("ranks", "Ranks", "refraction.command.ranks", true, "permissions", "perms");

    addSubcommand(new RanksSetCommand());
    addSubcommand(new RanksListCommand());
  }

  @Override
  protected void execute(CommandSender sender, String[] args) {
    sender.sendMessage(MessageUtil.getPrefixedMessage(getPrefix(), "Command List:"));
    sender.sendMessage(
        MessageUtil.getMessageWithHover(
                "- /%s <player> set <rank>", "Sets a player's primary rank.", getAliasUsed())
            .color(Rank.ADMIN.getPrefixColor()));
    sender.sendMessage(
        MessageUtil.getMessageWithHover(
                "- /%s list", "Lists all primary and subsidiary ranks.", getAliasUsed())
            .color(Rank.ADMIN.getPrefixColor()));
  }

  @Override
  protected Component getUsageMessage() {
    return null;
  }

  @Override
  protected void generatePermissions() {
    Rank.ADMIN.grantPermission(getPermission(), true);
  }
}
