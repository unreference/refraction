package com.github.unreference.refraction.command.impl.rank;

import com.github.unreference.refraction.command.AbstractParameterizedCommand;
import com.github.unreference.refraction.domain.model.Rank;
import com.github.unreference.refraction.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class RanksCommand extends AbstractParameterizedCommand {
  public RanksCommand() {
    super("ranks", "Ranks", "refraction.command.ranks", true, "permissions", "perms");

    addSubcommand(new RanksSetCommand());
    addSubcommand(new RanksAddCommand());
    addSubcommand(new RanksRemoveCommand());
    addSubcommand(new RanksInfoCommand());
    addSubcommand(new RanksResetCommand());
    addSubcommand(new RanksListCommand());
  }

  @Override
  protected void execute(CommandSender sender, String[] args) {
    sender.sendMessage(MessageUtil.getPrefixedMessage(getPrefix(), "Command List:"));
    sender.sendMessage(
        MessageUtil.getMessageWithHover(
                "- /%s <player> set <rank>", "Sets a player's primary rank.", getAliasUsed())
            .color(Rank.ADMIN.getRankColor()));
    sender.sendMessage(
        MessageUtil.getMessageWithHover(
                "- /%s <player> add <rank>", "Adds a subrank to a player.", getAliasUsed())
            .color(Rank.ADMIN.getRankColor()));
    sender.sendMessage(
        MessageUtil.getMessageWithHover(
                "- /%s <player> remove <rank>", "Removes a subrank from a player.", getAliasUsed())
            .color(Rank.ADMIN.getRankColor()));
    sender.sendMessage(
        MessageUtil.getMessageWithHover(
                "- /%s <player> info", "Displays a player's rank information.", getAliasUsed())
            .color(Rank.ADMIN.getRankColor()));
    sender.sendMessage(
        MessageUtil.getMessageWithHover(
                "- /%s <player> reset", "Resets a player's rank to default.", getAliasUsed())
            .color(Rank.ADMIN.getRankColor()));
    sender.sendMessage(
        MessageUtil.getMessageWithHover(
                "- /%s list", "Lists all primary and subrank ranks.", getAliasUsed())
            .color(Rank.ADMIN.getRankColor()));
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
