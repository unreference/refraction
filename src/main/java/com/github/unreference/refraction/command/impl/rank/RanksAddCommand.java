package com.github.unreference.refraction.command.impl.rank;

import com.github.unreference.refraction.command.AbstractCommand;
import com.github.unreference.refraction.command.CommandContext;
import com.github.unreference.refraction.data.manager.AccountRanksRepositoryManager;
import com.github.unreference.refraction.data.manager.AccountsRepositoryManager;
import com.github.unreference.refraction.model.Rank;
import com.github.unreference.refraction.util.MessageUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RanksAddCommand extends AbstractCommand {
  public RanksAddCommand() {
    super("add", "Ranks", "refraction.command.ranks.add", true);
  }

  @Override
  protected Component getUsageMessage() {
    return MessageUtil.getPrefixedMessage(
        getPrefix(), "/%s <player> %s <rank>", getMainAliasUsed(), getAliasUsed());
  }

  @Override
  protected void generatePermissions() {
    Rank.ADMIN.grantPermission(getPermission(), true);
  }

  @Override
  public void trigger(CommandContext context) {
    String[] args = context.getArgs();
    CommandSender sender = context.getSender();

    if (args.length != 1) {
      sender.sendMessage(getUsageMessage());
      return;
    }

    Rank rank = Rank.getRankFromId(args[0]);

    if (rank == null) {
      sender.sendMessage(
          MessageUtil.getPrefixedMessage(getPrefix(), "Rank not found: &e%s", args[0]));
      return;
    }

    if (rank.isPrimary()) {
      sender.sendMessage(
          MessageUtil.getPrefixedMessage(
              getPrefix(), "Invalid subsidiary rank: &e%s", rank.getId()));
      return;
    }

    String targetName = context.getTargetName();
    UUID targetId = AccountsRepositoryManager.get().getId(targetName);

    AccountRanksRepositoryManager.get().addRank(targetId, rank);

    sender.sendMessage(
        MessageUtil.getPrefixedMessage(
            getPrefix(),
            "Added &e%s &7to the &e%s &7subsidiary.",
            targetName,
            rank.getId().toUpperCase()));

    Player targetPlayer = Bukkit.getPlayer(targetName);

    if (targetPlayer != null) {
      Objects.requireNonNull(Bukkit.getPlayer(targetName))
          .sendMessage(
              MessageUtil.getPrefixedMessage(
                  getPrefix(),
                  "You were added to the &e%s &7subsidiary!",
                  rank.getId().toUpperCase()));
    }
  }

  @Override
  public List<String> tab(CommandSender sender, String alias, String[] args) {
    List<String> suggestions = new ArrayList<>();

    if (args.length == 1) {
      for (Rank rank : Rank.values()) {
        if (rank.isPrimary()) {
          continue;
        }

        suggestions.add(rank.getId());
        String currentArg = args[0];
        filterTab(suggestions, currentArg);
      }
    }

    return suggestions;
  }
}
