package com.github.unreference.refraction.command.impl.rank;

import com.github.unreference.refraction.command.AbstractCommand;
import com.github.unreference.refraction.command.CommandContext;
import com.github.unreference.refraction.data.manager.AccountRanksRepositoryManager;
import com.github.unreference.refraction.data.manager.AccountsRepositoryManager;
import com.github.unreference.refraction.domain.model.Rank;
import com.github.unreference.refraction.util.MessageUtil;
import com.github.unreference.refraction.util.ServerUtil;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RanksRemoveCommand extends AbstractCommand {
  public RanksRemoveCommand() {
    super("remove", "Ranks", "refraction.command.ranks.remove", true);
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

  @Override
  public void trigger(CommandContext context) {
    String[] args = context.getArgs();
    CommandSender sender = context.getSender();

    if (args.length != 1) {
      sender.sendMessage(getUsageMessage());
      return;
    }

    Rank rank = Rank.getRankFromId(args[0]);

    if (rank == null || rank.isPrimary()) {
      sender.sendMessage(
          MessageUtil.getPrefixedMessage(getPrefix(), "Subrank not found: &e%s", args[0]));
      return;
    }

    String targetName = context.getTargetName();

    ServerUtil.runAsync(
        () -> {
          try {
            UUID targetId = AccountsRepositoryManager.get().getId(targetName);
            List<Rank> targetSubranks = getSubranks(targetId);

            if (!targetSubranks.contains(rank)) {
              ServerUtil.runSync(
                  () ->
                      sender.sendMessage(
                          MessageUtil.getPrefixedMessage(
                              getPrefix(),
                              "&e%s &7is not part of the &e%s &7subrank.",
                              targetName,
                              rank.getId())));
              return;
            }

            AccountRanksRepositoryManager.get().removeSubrank(targetId, rank);

            ServerUtil.runSync(
                () -> {
                  sender.sendMessage(
                      MessageUtil.getPrefixedMessage(
                          getPrefix(),
                          "Removed &e%s &7from the &e%s &7subrank.",
                          targetName,
                          rank.getId()));

                  Player targetPlayer = Bukkit.getPlayer(targetName);

                  if (targetPlayer != null) {
                    targetPlayer.sendMessage(
                        MessageUtil.getPrefixedMessage(
                            getPrefix(),
                            "You were removed from the &e%s &7subrank!",
                            rank.getId()));
                  }
                });
          } catch (Exception exception) {
            sender.sendMessage(
                MessageUtil.getPrefixedMessage(
                    getPrefix(),
                    "An error occurred while attempting to remove a subrank from the player."));
          }
        });
  }

  @Override
  public List<String> tab(CommandSender sender, String alias, String[] args) {
    // TODO: Find a way to fetch the target's subranks to populate tab completion
    return List.of();
  }

  private List<Rank> getSubranks(UUID id) {
    return AccountRanksRepositoryManager.get().getSubranks(id);
  }
}
