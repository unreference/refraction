package me.unreference.refraction.commands;

import me.unreference.refraction.managers.DatabaseManager;
import me.unreference.refraction.managers.PlayerDataManager;
import me.unreference.refraction.managers.RankManager;
import me.unreference.refraction.models.RankModel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        DatabaseManager databaseManager = DatabaseManager.get();
        PlayerDataManager playerDataManager = PlayerDataManager.get(databaseManager);
        RankManager rankManager = RankManager.get();

        RankModel rank = rankManager.getRankFromId(args[2]);
        if (rank == null) {
            sender.sendMessage("Rank not found: " + args[2]);
            return;
        }

        playerDataManager.setPlayerRank(target.getUniqueId().toString(), rank.getId());
        sender.sendMessage("Set " + target.getName() + "'s rank to " + rank.getId() + ".");
        target.sendMessage("Your rank was set to " + rank.getId() + ".");
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
