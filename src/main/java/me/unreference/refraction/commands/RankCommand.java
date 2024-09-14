package me.unreference.refraction.commands;

import me.unreference.refraction.managers.DatabaseManager;
import me.unreference.refraction.managers.PlayerDataManager;
import me.unreference.refraction.models.RankModel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankCommand extends AbstractParameterizedCommand {

    public RankCommand() {
        super("rank", "refraction.command.rank", true);

        addSubcommand(new RankSetCommand());
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 1 || args.length > 2) {
            sender.sendMessage(getUsageMessage());
            return;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage("Player not found: " + args[0]);
            return;
        }

        DatabaseManager databaseManager = DatabaseManager.get();
        PlayerDataManager playerDataManager = PlayerDataManager.get(databaseManager);
        RankModel rank = playerDataManager.getPlayerRank(player.getUniqueId().toString());
        sender.sendMessage(player.getName() + "'s rank: " + rank.getId());
    }

    @Override
    protected String getUsageMessage() {
        return "Usage: /" + aliasUsed + " <player> [set] <rank>";
    }

    @Override
    protected void generatePermissions() {
        RankModel.ADMIN.grantPermission(getPermission(), true);
    }
}
