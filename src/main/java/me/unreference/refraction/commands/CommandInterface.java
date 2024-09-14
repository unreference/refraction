package me.unreference.refraction.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface CommandInterface {

    String getName();

    String getPermission();

    List<String> getAliases();

    void setAliasUsed(String alias);

    void trigger(CommandSender sender, String[] args);

    List<String> tab(CommandSender sender, String alias, String[] args);
}
