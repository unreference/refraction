package com.github.unreference.refraction.command.impl;

import com.github.unreference.refraction.Refraction;
import com.github.unreference.refraction.command.AbstractCommand;
import com.github.unreference.refraction.event.RankChangeEvent;
import com.github.unreference.refraction.model.Rank;
import com.github.unreference.refraction.service.PlayerDataRepositoryService;
import com.github.unreference.refraction.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RankCommand extends AbstractCommand {

    public RankCommand() {
        super("rank", "Rank", "refraction.command.rank");
    }

    @Override
    public void trigger(CommandSender sender, String[] args) {
        if (args.length < 1 || args.length > 2) {
            sender.sendMessage(getUsageMessage());
            return;
        }

        String targetInput = args[0];
        PlayerDataRepositoryService playerDataManager = Refraction.getPlayerDataRepositoryService();

        UUID targetId = playerDataManager.getId(targetInput);
        if (targetId == null) {
            sender.sendMessage(MessageUtil.getPrefixedMessage(getPrefix(), "Player not found: &b%s", args[0]));
            return;
        }

        String targetName = playerDataManager.getName(targetId);
        PlayerDataRepositoryService playerDataRepositoryService = Refraction.getPlayerDataRepositoryService();

        if (args.length == 1) {
            Rank rank = Rank.getRankFromId(playerDataRepositoryService.getRank(targetName));
            sender.sendMessage(MessageUtil.getPrefixedMessage(getPrefix(), "&b%s's &7rank: &b%s", targetName, rank.getId()));
        } else {
            Rank newRank = Rank.getRankFromId(args[1]);
            if (newRank == null) {
                sender.sendMessage(MessageUtil.getPrefixedMessage(getPrefix(), "Rank not found: &b%s", args[1]));
                return;
            }

            playerDataRepositoryService.setRank(targetName, newRank);
            sender.sendMessage(MessageUtil.getPrefixedMessage(
                    getPrefix(), "Set &b%s's &7rank to &b%s&7.", targetName, newRank.getId()));

            Player targetPlayer = Bukkit.getPlayer(targetName);
            if (targetPlayer != null) {
                Objects.requireNonNull(Bukkit.getPlayer(targetName))
                        .sendMessage(MessageUtil.getPrefixedMessage(
                                getPrefix(), "Your rank has been updated to &b%s&7.", newRank.getId()));
                Bukkit.getServer().getPluginManager().callEvent(new RankChangeEvent(targetPlayer, newRank));
            }
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions = getOnlinePlayers();
            String currentArg = args[0];
            filterTab(suggestions, currentArg);
        } else if (args.length == 2) {
            for (Rank rank : Rank.values()) {
                suggestions.add(rank.getId());
                String currentArg = args[1];
                filterTab(suggestions, currentArg);
            }
        }

        return suggestions;
    }

    @Override
    protected Component getUsageMessage() {
        return MessageUtil.getPrefixedMessage(getPrefix(), "Usage: /%s <player> [rank]", aliasUsed);
    }

    @Override
    protected void generatePermissions() {
        Rank.ADMIN.grantPermission(getPermission(), true);
    }
}
