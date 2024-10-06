package com.github.unreference.refraction.command.impl.rank;

import com.github.unreference.refraction.command.AbstractCommand;
import com.github.unreference.refraction.command.CommandContext;
import com.github.unreference.refraction.data.manager.AccountRanksRepositoryManager;
import com.github.unreference.refraction.data.manager.AccountsRepositoryManager;
import com.github.unreference.refraction.model.Rank;
import com.github.unreference.refraction.util.UtilMessage;
import com.github.unreference.refraction.util.UtilServer;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public class RanksInfoCommand extends AbstractCommand {
  public RanksInfoCommand() {
    super("info", "Ranks", "refraction.command.ranks.info", true, "i");
  }

  @Override
  protected Component getUsageMessage() {
    return UtilMessage.getPrefixedMessage(
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

    UtilServer.runAsync(
        () -> {
          UUID targetId = AccountsRepositoryManager.get().getId(targetName);
          String rank = AccountRanksRepositoryManager.get().getRank(targetId);
          List<Rank> subranks = AccountRanksRepositoryManager.get().getSubranks(targetId);

          UtilServer.runSync(
              () -> {
                sender.sendMessage(
                    UtilMessage.getPrefixedMessage(
                        getPrefix(), "Rank Information for %s:", targetName));
                sender.sendMessage(UtilMessage.getMessage("- Primary: &e%s", rank));
                sender.sendMessage(
                    UtilMessage.getMessage("- Subranks &8(%s)&7: ", subranks.size())
                        .append(formatRanks(subranks)));
              });
        });
  }

  @Override
  public List<String> tab(CommandSender sender, String alias, String[] args) {
    return List.of();
  }

  private Component formatRanks(List<Rank> ranks) {
    Component ranksComponent = Component.empty().colorIfAbsent(NamedTextColor.GRAY);

    for (int i = 0; i < ranks.size(); i++) {
      Rank rank = ranks.get(i);

      ranksComponent = ranksComponent.append(Component.text(rank.getId(), NamedTextColor.YELLOW));

      if (i < ranks.size() - 1) {
        ranksComponent = ranksComponent.append(Component.text(", "));
      }
    }

    return ranksComponent;
  }
}
