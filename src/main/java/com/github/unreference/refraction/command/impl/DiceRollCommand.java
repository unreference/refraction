package com.github.unreference.refraction.command.impl;

import com.github.unreference.refraction.command.AbstractCommand;
import com.github.unreference.refraction.event.DiceRollEvent;
import com.github.unreference.refraction.model.Rank;
import com.github.unreference.refraction.util.DiceUtil;
import com.github.unreference.refraction.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DiceRollCommand extends AbstractCommand {
    public DiceRollCommand() {
        super("diceroll", "Dice Roll", "refraction.command.diceroll", "dice", "dr", "roll", "rtd");
    }

    @Override
    protected Component getUsageMessage() {
        return MessageUtil.getPrefixedMessage(getPrefix(), "Usage: /%s", getAliasUsed());
    }

    @Override
    protected void generatePermissions() {
        Rank.DEFAULT.grantPermission(getPermission(), true);
    }

    @Override
    public void trigger(CommandSender sender, String[] args) {
        if (args.length != 0) {
            sender.sendMessage(getUsageMessage());
            return;
        }

        Player player = (Player) sender;
        int result = DiceUtil.roll(6);
        MessageUtil.broadcastMessage(MessageUtil.getPrefixedMessage(getPrefix(), "&b%s &7rolled a &b%d&7.", player.getName(), result));
        Bukkit.getPluginManager().callEvent(new DiceRollEvent(player, result));
    }

    @Override
    public List<String> tab(CommandSender sender, String alias, String[] args) {
        return List.of();
    }
}
