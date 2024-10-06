package com.github.unreference.refraction.command.impl.rank;

import com.github.unreference.refraction.command.AbstractCommand;
import com.github.unreference.refraction.command.CommandContext;
import com.github.unreference.refraction.data.manager.AccountRanksRepositoryManager;
import com.github.unreference.refraction.data.manager.AccountsRepositoryManager;
import com.github.unreference.refraction.model.Rank;
import com.github.unreference.refraction.util.MessageUtil;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class RanksInfoCommand extends AbstractCommand {
  public RanksInfoCommand() {
    super("info", "Ranks", "refraction.command.ranks.info", true, "i");
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
    UUID targetId = AccountsRepositoryManager.get().getId(targetName);
    String primaryRank = AccountRanksRepositoryManager.get().getRank(targetId);

    sender.sendMessage(MessageUtil.getPrefixedMessage(getPrefix(), "Info: %s", targetName));
    sender.sendMessage(MessageUtil.getMessage("- Primary: &e%s", primaryRank));
  }

  @Override
  public List<String> tab(CommandSender sender, String alias, String[] args) {
    return List.of();
  }
}
