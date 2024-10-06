package com.github.unreference.refraction.command.impl.rank;

import com.github.unreference.refraction.command.AbstractCommand;
import com.github.unreference.refraction.command.CommandContext;
import com.github.unreference.refraction.model.Rank;
import com.github.unreference.refraction.util.MessageUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        MessageUtil.getMessage("- Primary: ").append(formatRanks(getPrimaryRanks())));
    sender.sendMessage(
        MessageUtil.getMessage("- Subsidiary: ").append(formatRanks(getSubsidiaryRanks())));
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

  private List<Rank> getSubsidiaryRanks() {
    return Arrays.stream(Rank.values())
        .filter(rank -> !rank.isPrimary())
        .collect(Collectors.toCollection(ArrayList::new));
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
