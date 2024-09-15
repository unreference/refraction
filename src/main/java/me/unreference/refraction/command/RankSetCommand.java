package me.unreference.refraction.command;

import me.unreference.refraction.manager.RankManager;
import me.unreference.refraction.model.RankModel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RankSetCommand extends AbstractCommand {

    public RankSetCommand() {
        super("set", "refraction.command.rank.set");
    }

    @Override
    public void trigger(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(getUsageMessage());
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("Player not found: " + args[0]);
            return;
        }

        RankManager rankManager = RankManager.get();
        RankModel newRank = rankManager.getRankFromId(args[2]);
        if (newRank == null) {
            sender.sendMessage("Rank not found: " + args[2]);
            return;
        }

        try {
            rankManager.setPlayerRank(target, newRank);
            sender.sendMessage("Set " + target.getName() + "'s rank to " + newRank.getId() + ".");
            target.sendMessage("Your rank was set to " + newRank.getId() + ".");
        } catch (SQLException exception) {
            sender.sendMessage("An error occurred while attempting to set the target's rank.");
        }
    }

    @Override
    protected String getUsageMessage() {
        return "Usage: /" + getMainAliasUsed() + " <player> " + getAliasUsed() + " <rank>";
    }

    @Override
    protected void generatePermissions() {
        RankModel.ADMIN.grantPermission(getPermission(), true);
    }

    @Override
    public List<String> tab(CommandSender sender, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            for (RankModel rank : RankModel.values()) {
                suggestions.add(rank.getId());
            }
        }

        String currentArg = args[args.length - 1];
        suggestions.removeIf(
                suggestion -> !suggestion.toLowerCase().startsWith(currentArg.toLowerCase()));
        return suggestions;
    }
}
