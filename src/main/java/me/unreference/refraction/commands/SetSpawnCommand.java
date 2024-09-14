package me.unreference.refraction.commands;

import me.unreference.refraction.models.RankModel;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static me.unreference.refraction.Refraction.log;

public class SetSpawnCommand extends AbstractCommand {

    public SetSpawnCommand() {
        super("setspawn", "refraction.command.setspawn");
    }

    @Override
    protected String getUsageMessage() {
        return "Usage: /" + getAliasUsed();
    }

    @Override
    protected void generatePermissions() {
        RankModel.ADMIN.grantPermission(getPermission(), true);
        log(1, "SetSpawnCommand", "isPermitted: " + RankModel.ADMIN.isPermitted(getPermission()));
    }

    @Override
    public void trigger(CommandSender sender, String[] args) {
        if (args.length != 0) {
            sender.sendMessage(getUsageMessage());
            return;
        }

        Player player = (Player) sender;
        Location location = player.getLocation();

        player.getWorld().setSpawnLocation(location);

        player.sendMessage("Spawn set.");
    }

    @Override
    public List<String> tab(CommandSender sender, String alias, String[] args) {
        return List.of();
    }
}
