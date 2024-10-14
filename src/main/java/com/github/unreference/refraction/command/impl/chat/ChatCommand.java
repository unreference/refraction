package com.github.unreference.refraction.command.impl.chat;

import com.github.unreference.refraction.command.AbstractParameterizedCommand;
import com.github.unreference.refraction.data.manager.AccountRanksRepositoryManager;
import com.github.unreference.refraction.domain.model.Rank;
import com.github.unreference.refraction.util.MessageUtil;
import com.github.unreference.refraction.util.ServerUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatCommand extends AbstractParameterizedCommand {
  public ChatCommand() {
    super("chat", "Chat", "refraction.command.chat", false);

    addSubcommand(new ChatLockCommand());
    addSubcommand(new ChatSlowCommand());
  }

  @Override
  protected void execute(CommandSender sender, String[] args) {
    Player player = (Player) sender;

    ServerUtil.runAsync(
        () -> {
          Rank rank =
              Rank.getRankFromId(AccountRanksRepositoryManager.get().getRank(player.getUniqueId()));

          ServerUtil.runSync(
              () -> {
                sender.sendMessage(MessageUtil.getPrefixedMessage(getPrefix(), "Command List:"));

                if (rank == Rank.ADMIN) {
                  sender.sendMessage(
                      MessageUtil.getMessageWithHover(
                              "- /%s lock [duration]",
                              "Locks the chat for the specified duration.", getAliasUsed())
                          .color(Rank.ADMIN.getRankColor()));
                }

                sender.sendMessage(
                    MessageUtil.getMessageWithHover(
                            "- /%s slow [duration]",
                            "Adds a slow mode to the chat for the specified duration.",
                            getAliasUsed())
                        .color(Rank.SR_MOD.getRankColor()));
              });
        });
  }

  @Override
  protected Component getUsageMessage() {
    return null;
  }

  @Override
  protected void generatePermissions() {
    Rank.SR_MOD.grantPermission(getPermission(), true);
  }
}
