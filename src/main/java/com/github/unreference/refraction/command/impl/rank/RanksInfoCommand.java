package com.github.unreference.refraction.command.impl.rank;

import com.github.unreference.refraction.command.AbstractCommand;
import com.github.unreference.refraction.command.CommandContext;
import com.github.unreference.refraction.data.manager.AccountRepositoryManager;
import com.github.unreference.refraction.model.Rank;
import com.github.unreference.refraction.util.MessageUtil;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class RanksInfoCommand extends AbstractCommand {
  public RanksInfoCommand() {
    super("info", "Ranks", "refraction.command.rank.info", true, "i");
  }

  @Override
  protected Component getUsageMessage() {
    return MessageUtil.getPrefixedMessage(
        getPrefix(), "Usage: /%s %s <player>", getMainAliasUsed(), getAliasUsed());
  }

  @Override
  protected void generatePermissions() {
    Rank.ADMIN.grantPermission(getPermission(), true);
  }

  @Override
  public void trigger(CommandContext context) {
    String[] args = context.getArgs();
    CommandSender sender = context.getSender();

    if (args.length != 0) {
      sender.sendMessage(getUsageMessage());
      return;
    }

    String targetName = context.getTargetName();
    String primaryRank = AccountRepositoryManager.get().getPrimaryRank(targetName);

    sender.sendMessage(MessageUtil.getPrefixedMessage(getPrefix(), "Info: &e%s", targetName));
    sender.sendMessage(MessageUtil.getMessage("- Primary: &e%s", primaryRank));
  }

  @Override
  public List<String> tab(CommandSender sender, String alias, String[] args) {
    return List.of();
  }
}
