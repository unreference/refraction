package com.github.unreference.refraction.command.impl;

import com.github.unreference.refraction.command.AbstractCommand;
import com.github.unreference.refraction.model.Rank;
import com.github.unreference.refraction.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetSpawnCommand extends AbstractCommand {

    public SetSpawnCommand() {
        super("setspawn", "Spawn", "refraction.command.setspawn");
    }

    @Override
    public void trigger(CommandSender sender, String[] args) {
        if (args.length != 0 && args.length != 3) {
            sender.sendMessage(getUsageMessage());
            return;
        }

        Player player = (Player) sender;
        Location playerLocation = player.getLocation();
        World world = playerLocation.getWorld();

        if (args.length == 0) {
            setSpawn(player, playerLocation);
        } else {
            try {
                int x = Integer.parseInt(args[0]);
                int y = Integer.parseInt(args[1]);
                int z = Integer.parseInt(args[2]);
                setSpawn(player, new Location(world, x, y, z));
            } catch (NumberFormatException exception) {
                sender.sendMessage(getUsageMessage());
            }
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String alias, String[] args) {
        return List.of();
    }

    @Override
    protected Component getUsageMessage() {
        return MessageUtil.getPrefixedMessage(
                getPrefix(), "Usage: /%s [<coordinates>]", getAliasUsed());
    }

    @Override
    protected void generatePermissions() {
        Rank.ADMIN.grantPermission(getPermission(), true);
    }

    private void setSpawn(CommandSender sender, Location location) {
        World world = location.getWorld();
        world.setSpawnLocation(location);
        sender.sendMessage(MessageUtil.getPrefixedMessage(
                getPrefix(),
                "Set &b%s's &7spawn to &b%.0f&7, &b%.0f&7, &b%.0f&7.",
                world.getName(),
                world.getSpawnLocation().getX(),
                world.getSpawnLocation().getY(),
                world.getSpawnLocation().getZ()));
    }
}
