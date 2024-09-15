package me.unreference.refraction.command;

import me.unreference.refraction.manager.RankManager;
import me.unreference.refraction.model.RankModel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("Player not found: " + args[0]);
            return;
        }

        RankManager rankManager = RankManager.get();

        if (args.length == 1) {
            try {
                RankModel rank = rankManager.getPlayerRank(target);
                sender.sendMessage(target.getName() + "'s rank: " + rank.getId());
            } catch (SQLException exception) {
                sender.sendMessage("A database error occurred while attempting to fetch the target's rank.");
            }
        } else {
            RankModel newRank = rankManager.getRankFromId(args[1]);
            if (newRank == null) {
                sender.sendMessage("Rank not found: " + args[1]);
                return;
            }

            try {
                rankManager.setPlayerRank(target, newRank);
                sender.sendMessage("Set " + target.getName() + "'s rank to " + newRank.getId() + ".");
                target.sendMessage("Your rank was set to " + newRank.getId() + ".");
            } catch (SQLException exception) {
                sender.sendMessage("A database error occurred while attempting to set the target's rank.");
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
    protected String getUsageMessage() {
        return "Usage: /" + aliasUsed + " <player> [rank]";
    }

    @Override
    protected void generatePermissions() {
        RankModel.ADMIN.grantPermission(getPermission(), true);
    }
}
