package com.github.unreference.refraction.command.impl;

import com.github.unreference.refraction.command.AbstractCommand;
import com.github.unreference.refraction.manager.PlayerDataRepositoryManager;
import com.github.unreference.refraction.model.Rank;
import com.github.unreference.refraction.roulette.manager.FlavorTextManager;
import com.github.unreference.refraction.roulette.manager.SpinManager;
import com.github.unreference.refraction.roulette.spin.AbstractSpin;
import com.github.unreference.refraction.util.MessageUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RouletteCommand extends AbstractCommand {
  private static final String PERMISSION_ROULETTE_FORCE = "refraction.command.roulette.force";

  public RouletteCommand() {
    super("roulette", "Roulette", "refraction.command.roulette", "spin");
  }

  @Override
  protected Component getUsageMessage() {
    return null;
  }

  @Override
  protected void generatePermissions() {
    Rank.DEFAULT.grantPermission(getPermission(), true);
    Rank.ADMIN.grantPermission(PERMISSION_ROULETTE_FORCE, true);
  }

  @Override
  public void trigger(CommandSender sender, String[] args) {
    Player player = (Player) sender;
    AbstractSpin spin = SpinManager.get().getRandomPerk();

    if (args.length == 0) {
      applySpin(player, spin);
      return;
    }

    if (!isPermittedToForce(player)) {
      player.sendMessage(getRealUsageMessage(player));
      return;
    }

    if (!isValidUsage(args)) {
      player.sendMessage(getRealUsageMessage(player));
      return;
    }

    Player target = getTarget(player, args[0]);
    if (target == null) return;

    if (args.length == 1) {
      getForcedMessage(player, target, spin);
      applySpin(target, spin);
    } else {
      applySpin(player, target, args[1]);
    }
  }

  private boolean isPermittedToForce(Player player) {
    Rank rank = Rank.getRankFromId(PlayerDataRepositoryManager.get().getRank(player.getName()));
    return rank.isPermitted(PERMISSION_ROULETTE_FORCE);
  }

  private boolean isValidUsage(String[] args) {
    return args.length <= 2;
  }

  private Player getTarget(Player player, String targetInput) {
    Player target = Bukkit.getPlayer(targetInput);
    if (target == null) {
      player.sendMessage(
          MessageUtil.getPrefixedMessage(getPrefix(), "Player not found: &b%s", targetInput));
    }

    return target;
  }

  private void applySpin(Player player, Player target, String spinId) {
    AbstractSpin perk = getSpinFromId(spinId);
    if (perk == null) {
      player.sendMessage(
          MessageUtil.getPrefixedMessage(getPrefix(), "Perk not found: &b%s", spinId));
      return;
    }

    getForcedMessage(player, target, perk);
    applySpin(target, perk);
  }

  private AbstractSpin getSpinFromId(String spinId) {
    for (AbstractSpin p : SpinManager.get().getSpins()) {
      if (Objects.equals(spinId, p.getId())) {
        return p;
      }
    }
    return null;
  }

  private void applySpin(Player player, AbstractSpin spin) {
    spin.apply(player);
    broadcastFlavorText(spin, player);
  }

  private void getForcedMessage(Player player, Player target, AbstractSpin spin) {
    player.sendMessage(
        MessageUtil.getPrefixedMessage(
            getPrefix(),
            "Forced %s%s &7to roll %s%s&7.",
            spin.isPositive() ? "&b" : "&c",
            target.getName(),
            spin.isPositive() ? "&b" : "&c",
            spin.getName()));
  }

  private Component getRealUsageMessage(Player player) {
    Rank rank = Rank.getRankFromId(PlayerDataRepositoryManager.get().getRank(player.getName()));
    boolean isPermittedToForce = rank.isPermitted(PERMISSION_ROULETTE_FORCE);
    return MessageUtil.getPrefixedMessage(
        getPrefix(),
        "Usage: /%s %s",
        getAliasUsed(),
        !isPermittedToForce ? "" : "[<player>] [<spin>]");
  }

  @Override
  public List<String> tab(CommandSender sender, String alias, String[] args) {
    Player player = (Player) sender;
    Rank rank = Rank.getRankFromId(PlayerDataRepositoryManager.get().getRank(player.getName()));

    if (!rank.isPermitted(PERMISSION_ROULETTE_FORCE)) {
      return List.of();
    }

    List<String> suggestions = new ArrayList<>();
    if (args.length == 1) {
      suggestions = getOnlinePlayers();
      String currentArg = args[0];
      filterTab(suggestions, currentArg);
    } else if (args.length == 2) {
      for (AbstractSpin perk : SpinManager.get().getSpins()) {
        suggestions.add(perk.getId());
        String currentArg = args[1];
        filterTab(suggestions, currentArg);
      }
    }

    return suggestions;
  }

  private void broadcastFlavorText(AbstractSpin perk, Player player) {
    String announcement;
    if (perk.isPositive()) {
      announcement = FlavorTextManager.get().getPositiveText(player.getName(), perk.getName());
    } else {
      announcement = FlavorTextManager.get().getNegativeText(player.getName(), perk.getName());
    }

    MessageUtil.broadcastMessage(MessageUtil.getPrefixedMessage(getPrefix(), announcement));
  }
}
