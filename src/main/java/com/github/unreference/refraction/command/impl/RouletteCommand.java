package com.github.unreference.refraction.command.impl;

import com.github.unreference.refraction.command.AbstractCommand;
import com.github.unreference.refraction.manager.PlayerDataRepositoryManager;
import com.github.unreference.refraction.model.Rank;
import com.github.unreference.refraction.roulette.manager.FlavorTextManager;
import com.github.unreference.refraction.roulette.manager.SpinManager;
import com.github.unreference.refraction.roulette.spin.AbstractSpin;
import com.github.unreference.refraction.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RouletteCommand extends AbstractCommand {
    private static final String PERMISSION_ROULETTE_FORCE = "refraction.command.roulette.force";

    public RouletteCommand() {
        super("roulette", "Roulette", "refraction.command.roulette", "rtd", "spin");
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
        AbstractSpin perk = SpinManager.get().getRandomPerk();

        if (args.length == 0) {
            perk.apply(player);
            broadcastFlavorText(perk, player);
            return;
        }

        Rank rank = Rank.getRankFromId(PlayerDataRepositoryManager.get().getRank(player.getName()));
        if (!rank.isPermitted(PERMISSION_ROULETTE_FORCE)) {
            player.sendMessage(getRealUsageMessage(player));
            return;
        }

        if (args.length > 2) {
            player.sendMessage(getRealUsageMessage(player));
            return;
        }

        String targetInput = args[0];
        Player target = Bukkit.getPlayer(targetInput);
        if (target == null) {
            player.sendMessage(MessageUtil.getPrefixedMessage(getPrefix(), "Player not found: &b%s", args[0]));
            return;
        }

        if (args.length == 1) {
            perk.apply(target);
            player.sendMessage(MessageUtil.getPrefixedMessage(getPrefix(), "Forced %s%s &7to roll %s%s&7.", perk.isPositive() ? "&b" : "&c", target.getName(), perk.isPositive() ? "&b" : "&c", perk.getName()));
            broadcastFlavorText(perk, target);
        } else {
            String perkId = args[1];
            for (AbstractSpin p : SpinManager.get().getPerks()) {
                if (Objects.equals(perkId, p.getId())) {
                    perk = p;
                    break;
                } else {
                    perk = null;
                }
            }

            if (perk == null) {
                player.sendMessage(MessageUtil.getPrefixedMessage(getPrefix(), "Perk not found: &b%s", args[1]));
                return;
            }

            perk.apply(target);
            player.sendMessage(MessageUtil.getPrefixedMessage(getPrefix(), "Forced %s%s &7to roll %s%s&7.", perk.isPositive() ? "&b" : "&c", target.getName(), perk.isPositive() ? "&b" : "&c", perk.getName()));
            broadcastFlavorText(perk, target);
        }
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
            for (AbstractSpin perk : SpinManager.get().getPerks()) {
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

    private Component getRealUsageMessage(Player player) {
        Rank rank = Rank.getRankFromId(PlayerDataRepositoryManager.get().getRank(player.getName()));
        boolean isPermittedToForce = rank.isPermitted(PERMISSION_ROULETTE_FORCE);
        return MessageUtil.getPrefixedMessage(getPrefix(),
                "Usage: /%s %s", getAliasUsed(), !isPermittedToForce ? "" : "[<player>] [<perk>]");
    }
}
