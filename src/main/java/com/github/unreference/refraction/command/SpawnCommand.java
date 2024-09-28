package com.github.unreference.refraction.command;

import com.github.unreference.refraction.model.RankModel;
import com.github.unreference.refraction.utility.MessageUtility;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SpawnCommand extends AbstractCommand {

    public SpawnCommand() {
        super("spawn", "refraction.command.spawn");
    }

    @Override
    public void trigger(CommandSender sender, String[] args) {
        if (args.length != 0) {
            sender.sendMessage(getUsageMessage());
            return;
        }

        Player player = (Player) sender;
        Location spawn = player.getWorld().getSpawnLocation();
        player.teleport(spawn);
    }

    @Override
    public List<String> tab(CommandSender sender, String alias, String[] args) {
        return List.of();
    }

    @Override
    protected Component getUsageMessage() {
        return MessageUtility.getMessage("Usage: /" + getAliasUsed());
    }

    @Override
    protected void generatePermissions() {
        RankModel.DEFAULT.grantPermission(getPermission(), true);
    }
}
