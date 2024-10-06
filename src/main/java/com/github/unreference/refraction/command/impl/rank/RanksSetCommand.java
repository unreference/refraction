package com.github.unreference.refraction.command.impl.rank;

import com.github.unreference.refraction.command.AbstractCommand;
import com.github.unreference.refraction.command.CommandContext;
import com.github.unreference.refraction.data.manager.AccountRanksRepositoryManager;
import com.github.unreference.refraction.data.manager.AccountsRepositoryManager;
import com.github.unreference.refraction.event.RankChangeEvent;
import com.github.unreference.refraction.model.Rank;
import com.github.unreference.refraction.util.MessageUtil;
import java.util.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RanksSetCommand extends AbstractCommand {
  public RanksSetCommand() {
    super("set", "Ranks", "refraction.command.ranks.set", true);
  }

  @Override
  public void trigger(CommandContext context) {
    CommandSender sender = context.getSender();
    String[] args = context.getArgs();

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

    if (!rank.isPrimary()) {
      sender.sendMessage(
          MessageUtil.getPrefixedMessage(getPrefix(), "Invalid primary rank: &e%s", rank.getId()));
      return;
    }

    String targetName = context.getTargetName();
    UUID targetId = AccountsRepositoryManager.get().getId(targetName);

    AccountRanksRepositoryManager.get().setRank(targetId, rank);

    sender.sendMessage(
        MessageUtil.getPrefixedMessage(
            getPrefix(), "Set &e%s's &7rank to &e%s&7.", targetName, rank.getPrefix()));

    Player targetPlayer = Bukkit.getPlayer(targetName);

    if (targetPlayer != null) {
      Objects.requireNonNull(Bukkit.getPlayer(targetName))
          .sendMessage(
              MessageUtil.getPrefixedMessage(
                  getPrefix(), "Your rank has been updated to &e%s&7!", rank.getPrefix()));
      Bukkit.getServer().getPluginManager().callEvent(new RankChangeEvent(targetPlayer, rank));
    }
  }

  @Override
  public List<String> tab(CommandSender sender, String alias, String[] args) {
    List<String> suggestions = new ArrayList<>();

    if (args.length == 1) {
      for (Rank rank : Rank.values()) {
        if (!rank.isPrimary()) {
          continue;
        }

        suggestions.add(rank.getId());
        String currentArg = args[0];
        filterTab(suggestions, currentArg);
      }
    }

    return suggestions;
  }

  @Override
  protected Component getUsageMessage() {
    return MessageUtil.getPrefixedMessage(
        getPrefix(), "Usage: /%s <player> %s <rank>", getMainAliasUsed(), getAliasUsed());
  }

  @Override
  protected void generatePermissions() {
    Rank.ADMIN.grantPermission(getPermission(), true);
  }
}
