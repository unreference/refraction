package com.github.unreference.refraction.command.impl.rank;

import com.github.unreference.refraction.command.AbstractCommand;
import com.github.unreference.refraction.command.CommandContext;
import com.github.unreference.refraction.data.manager.AccountRanksRepositoryManager;
import com.github.unreference.refraction.data.manager.AccountsRepositoryManager;
import com.github.unreference.refraction.domain.model.Rank;
import com.github.unreference.refraction.event.RankChangeEvent;
import com.github.unreference.refraction.util.MessageUtil;
import com.github.unreference.refraction.util.ServerUtil;
import java.util.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RanksResetCommand extends AbstractCommand {
  public RanksResetCommand() {
    super("reset", "Ranks", "refraction.command.ranks.reset", true, "clear");
  }

  @Override
  public void trigger(CommandContext context) {
    CommandSender sender = context.getSender();
    String[] args = context.getArgs();

    if (args.length != 0) {
      sender.sendMessage(getUsageMessage());
      return;
    }

    String targetName = context.getTargetName();

    ServerUtil.runAsync(
        () -> {
          try {
            UUID targetId = AccountsRepositoryManager.get().getId(targetName);
            AccountRanksRepositoryManager.get().setRank(targetId, Rank.PLAYER);

            ServerUtil.runSync(
                () -> {
                  sender.sendMessage(
                      MessageUtil.getPrefixedMessage(
                          getPrefix(), "Reset &e%s's &7ranks.", targetName));

                  Player targetPlayer = Bukkit.getPlayer(targetName);

                  if (targetPlayer != null) {
                    targetPlayer.sendMessage(
                        MessageUtil.getPrefixedMessage(getPrefix(), "Your ranks has been reset!"));
                    ServerUtil.callEvent(new RankChangeEvent(targetPlayer, Rank.PLAYER));
                  }
                });
          } catch (Exception exception) {
            sender.sendMessage(
                MessageUtil.getPrefixedMessage(
                    getPrefix(), "An error occurred while attempting to reset the player's rank."));
          }
        });
  }

  @Override
  public List<String> tab(CommandSender sender, String alias, String[] args) {
    return List.of();
  }

  @Override
  protected Component getUsageMessage() {
    return MessageUtil.getPrefixedMessage(
        getPrefix(), "Usage: /%s <player> %s", getMainAliasUsed(), getAliasUsed());
  }

  @Override
  protected void generatePermissions() {
    Rank.ADMIN.grantPermission(getPermission(), true);
  }
}
