package com.github.unreference.refraction.command;

import com.github.unreference.refraction.event.RankChangeEvent;
import com.github.unreference.refraction.manager.DatabaseManager;
import com.github.unreference.refraction.manager.PlayerDataManager;
import com.github.unreference.refraction.manager.RankManager;
import com.github.unreference.refraction.model.RankModel;
import com.github.unreference.refraction.utility.MessageUtility;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RankCommand extends AbstractCommand {

    public RankCommand() {
        super("rank", "refraction.command.rank");
    }

    @Override
    public void trigger(CommandSender sender, String[] args) {
        if (args.length < 1 || args.length > 2) {
            sender.sendMessage(getUsageMessage());
            return;
        }

        String targetInput = args[0];
        PlayerDataManager playerDataManager = PlayerDataManager.get(DatabaseManager.get());

        UUID targetUuid = playerDataManager.getUuid(targetInput);
        if (targetUuid == null) {
            sender.sendMessage(MessageUtility.getMessage("Player not found: %s"));
            return;
        }

        String targetName = playerDataManager.getName(targetUuid);
        RankManager rankManager = RankManager.get();

        if (args.length == 1) {
            try {
                RankModel rank = rankManager.getPlayerRank(targetName);
                sender.sendMessage(MessageUtility.getMessage("%s's rank: %s", targetName, rank.getId()));
            } catch (SQLException exception) {
                sender.sendMessage(MessageUtility.getMessage("A database error occurred while attempting to fetch the target's rank."));
            }
        } else {
            RankModel newRank = rankManager.getRankFromId(args[1]);
            if (newRank == null) {
                sender.sendMessage(MessageUtility.getMessage("Rank not found: %s", args[1]));
                return;
            }

            try {
                rankManager.setPlayerRank(targetName, newRank);
                sender.sendMessage(MessageUtility.getMessage("Updated %s's rank to %s.", targetName, newRank.getId()));

                Player targetPlayer = Bukkit.getPlayer(targetName);
                if (targetPlayer != null) {
                    Objects.requireNonNull(Bukkit.getPlayer(targetName)).sendMessage(MessageUtility.getMessage("Your rank has been updated to %s.", newRank.getId()));
                    Bukkit.getServer().getPluginManager().callEvent(new RankChangeEvent(targetPlayer, newRank));
                }
            } catch (SQLException exception) {
                sender.sendMessage(MessageUtility.getMessage("A database error occurred while attempting to set the target's rank."));
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
            for (RankModel rank : RankModel.values()) {
                suggestions.add(rank.getId());
                String currentArg = args[1];
                filterTab(suggestions, currentArg);
            }
        }

        return suggestions;
    }

    @Override
    protected Component getUsageMessage() {
        return MessageUtility.getMessage("Usage: /%s <player> [rank]", aliasUsed);
    }

    @Override
    protected void generatePermissions() {
        RankModel.ADMIN.grantPermission(getPermission(), true);
    }
}
