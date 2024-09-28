package com.github.unreference.refraction.command;

import com.github.unreference.refraction.Refraction;
import com.github.unreference.refraction.model.RankModel;
import com.github.unreference.refraction.utility.MessageUtility;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SetSpawnCommand extends AbstractCommand {

    public SetSpawnCommand() {
        super("setspawn", "Spawn", "refraction.command.setspawn");
    }

    @Override
    public void trigger(CommandSender sender, String[] args) {
        if (args.length > 3 && args.length != 4) {
            sender.sendMessage(getUsageMessage());
            return;
        }

        Player player = (Player) sender;
        Location playerLocation = player.getLocation();
        World world = playerLocation.getWorld();

        if (args.length == 0) {
            world.setSpawnLocation(playerLocation);
            sender.sendMessage(MessageUtility.getPrefixedMessage(
                    getPrefix(),
                    "Set &b%s's &7spawn to &bXYZ&7: &b%.0f &7/ &b%.0f &7/ &b%.0f&7.",
                    world.getName(),
                    world.getSpawnLocation().getX(),
                    world.getSpawnLocation().getY(),
                    world.getSpawnLocation().getZ()));
            return;
        } else if (args.length == 3) {
            try {
                int x = Integer.parseInt(args[0]);
                int y = Integer.parseInt(args[1]);
                int z = Integer.parseInt(args[2]);

                Location location = new Location(world, x, y, z);
                world.setSpawnLocation(location);
                sender.sendMessage(MessageUtility.getPrefixedMessage(
                        getPrefix(),
                        "Set &b%s's &7spawn to &bXYZ&7: &b%.0f &7/ &b%.0f &7/ &b%.0f&7.",
                        world.getName(),
                        world.getSpawnLocation().getX(),
                        world.getSpawnLocation().getY(),
                        world.getSpawnLocation().getZ()));
                return;
            } catch (NumberFormatException exception) {
                sender.sendMessage(MessageUtility.getPrefixedMessage(getPrefix(), "Invalid coordinates."));
                return;
            }
        }

        String worldInput = args[0];
        world = Refraction.getPlugin().getServer().getWorld(worldInput);
        if (world == null) {
            sender.sendMessage(MessageUtility.getPrefixedMessage(getPrefix(), "World not found: &b%s", worldInput));
            return;
        }

        try {
            int x = Integer.parseInt(args[1]);
            int y = Integer.parseInt(args[2]);
            int z = Integer.parseInt(args[3]);

            Location location = new Location(world, x, y, z);
            world.setSpawnLocation(location);
            sender.sendMessage(MessageUtility.getPrefixedMessage(
                    getPrefix(),
                    "Set &b%s's &7spawn to &bXYZ&7: &b%.0f &7/ &b%.0f &7/ &b%.0f&7.",
                    world.getName(),
                    world.getSpawnLocation().getX(),
                    world.getSpawnLocation().getY(),
                    world.getSpawnLocation().getZ()));
        } catch (NumberFormatException exception) {
            sender.sendMessage(MessageUtility.getPrefixedMessage(getPrefix(), "Invalid coordinates."));
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions = Refraction.getPlugin().getServer().getWorlds().stream()
                    .map(World::getName)
                    .collect(Collectors.toCollection(ArrayList::new));
            String currentArg = args[0];
            filterTab(suggestions, currentArg);
        }

        return suggestions;
    }

    @Override
    protected Component getUsageMessage() {
        return MessageUtility.getPrefixedMessage(
                getPrefix(), "Usage: /%s [coordinates|world] <coordinates>", getAliasUsed());
    }

    @Override
    protected void generatePermissions() {
        RankModel.ADMIN.grantPermission(getPermission(), true);
    }
}
