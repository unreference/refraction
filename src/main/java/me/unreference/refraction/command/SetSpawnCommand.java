package me.unreference.refraction.command;

import me.unreference.refraction.model.RankModel;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetSpawnCommand extends AbstractCommand {

    public SetSpawnCommand() {
        super("setspawn", "refraction.command.setspawn");
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

    @Override
    protected String getUsageMessage() {
        return String.format("Usage: /%s", getAliasUsed());
    }

    @Override
    protected void generatePermissions() {
        RankModel.ADMIN.grantPermission(getPermission(), true);
    }
}
