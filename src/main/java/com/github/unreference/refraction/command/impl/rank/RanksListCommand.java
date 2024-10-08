package com.github.unreference.refraction.command.impl.rank;

import com.github.unreference.refraction.command.AbstractCommand;
import com.github.unreference.refraction.command.CommandContext;
import com.github.unreference.refraction.domain.model.Rank;
import com.github.unreference.refraction.util.MessageUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class RanksListCommand extends AbstractCommand {
  public RanksListCommand() {
    super("list", "Ranks", "refraction.command.ranks.list", false, "ls");
  }

  @Override
  protected Component getUsageMessage() {
    return MessageUtil.getPrefixedMessage(
        getPrefix(), "Usage: /%s %s", getMainAliasUsed(), getAliasUsed());
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

    sender.sendMessage(MessageUtil.getPrefixedMessage(getPrefix(), "Ranks List:"));
    sender.sendMessage(
        MessageUtil.getMessage("- Primary: ").append(Rank.getFormattedList(getPrimaryRanks())));
    sender.sendMessage(
        MessageUtil.getMessage("- Subranks: ").append(Rank.getFormattedList(getSubranks())));
  }

  @Override
  public List<String> tab(CommandSender sender, String alias, String[] args) {
    return List.of();
  }

  private List<Rank> getPrimaryRanks() {
    return Arrays.stream(Rank.values())
        .filter(Rank::isPrimary)
        .collect(Collectors.toCollection(ArrayList::new));
  }

  private List<Rank> getSubranks() {
    return Arrays.stream(Rank.values())
        .filter(rank -> !rank.isPrimary())
        .collect(Collectors.toCollection(ArrayList::new));
  }
}
