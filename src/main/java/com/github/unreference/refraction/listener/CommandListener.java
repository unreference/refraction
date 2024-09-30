package com.github.unreference.refraction.listener;

import com.github.unreference.refraction.Refraction;
import com.github.unreference.refraction.command.impl.DiceRollCommand;
import com.github.unreference.refraction.command.impl.RankCommand;
import com.github.unreference.refraction.command.impl.SetSpawnCommand;
import com.github.unreference.refraction.command.impl.SpawnCommand;
import com.github.unreference.refraction.model.Rank;
import com.github.unreference.refraction.service.PlayerDataRepositoryService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.Collection;
import java.util.HashSet;

public class CommandListener implements Listener {

    public CommandListener() {
        registerCommand(new SetSpawnCommand());
        registerCommand(new SpawnCommand());
        registerCommand(new RankCommand());
        registerCommand(new DiceRollCommand());
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        String[] commandParts = message.split("\\s+");
        String commandName = commandParts[0].substring(1);
        Player player = event.getPlayer();

        if (!getAllowedCommands(player).contains(commandName)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerCommandSend(PlayerCommandSendEvent event) {
        Player player = event.getPlayer();
        Collection<String> allowedCommands = getAllowedCommands(player);

        event.getCommands().clear();
        event.getCommands().addAll(allowedCommands);
        player.updateCommands();
    }

    private Collection<String> getAllowedCommands(Player player) {
        PlayerDataRepositoryService playerDataRepositoryService = Refraction.getPlayerDataRepositoryService();

        Rank rank = Rank.getRankFromId(playerDataRepositoryService.getRank(player.getName()));
        CommandMap commandMap = Bukkit.getCommandMap();
        Collection<String> allowedCommands = new HashSet<>();

        if (player.isOp()) {
            for (Command command : commandMap.getKnownCommands().values()) {
                allowedCommands.add(command.getName());
                allowedCommands.addAll(command.getAliases());
            }
        } else {
            for (Command command : commandMap.getKnownCommands().values()) {
                if (rank.isPermitted(command.getPermission())) {
                    allowedCommands.add(command.getName());
                    allowedCommands.addAll(command.getAliases());
                }
            }
        }

        return allowedCommands;

    }

    private void registerCommand(Command command) {
        Bukkit.getServer().getCommandMap().register(command.getName(), command);
    }
}
