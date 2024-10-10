package com.github.unreference.refraction.command.impl.rank;

import com.github.unreference.refraction.command.AbstractCommand;
import com.github.unreference.refraction.command.CommandContext;
import com.github.unreference.refraction.data.manager.AccountRanksRepositoryManager;
import com.github.unreference.refraction.data.manager.AccountsRepositoryManager;
import com.github.unreference.refraction.domain.model.Rank;
import com.github.unreference.refraction.util.MessageUtil;
import com.github.unreference.refraction.util.ServerUtil;
import java.util.*;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

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

    ServerUtil.runAsync(
        () -> {
          try {
            UUID targetId = AccountsRepositoryManager.get().getId(targetName);
            AccountRanksRepositoryManager.get().setRank(targetId, rank);

            ServerUtil.runSync(
                () -> {
                  sender.sendMessage(
                      MessageUtil.getPrefixedMessage(
                          getPrefix(), "Set &e%s's &7rank to &e%s&7.", targetName, rank.getId()));

                  //                  Player targetPlayer = Bukkit.getPlayer(targetName);
                  //
                  //                  if (targetPlayer != null) {
                  //                    targetPlayer.sendMessage(
                  //                        MessageUtil.getPrefixedMessage(
                  //                            getPrefix(), "Your rank has been updated to
                  // &e%s&7!", rank.getId()));
                  //                    ServerUtil.callEvent(new RankChangeEvent(targetPlayer,
                  // rank));
                  //                  }
                });
          } catch (Exception exception) {
            sender.sendMessage(
                MessageUtil.getPrefixedMessage(
                    getPrefix(), "An error occurred while attempting to set the player's rank."));
          }
        });
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
