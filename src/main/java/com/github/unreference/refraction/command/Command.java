package com.github.unreference.refraction.command;

import java.util.List;
import org.bukkit.command.CommandSender;

public interface Command {

  String getName();

  String getPermission();

  List<String> getAliases();

  void setAliasUsed(String alias);

  void setMainAliasUsed(String alias);

  void trigger(CommandContext context);

  List<String> tab(CommandSender sender, String alias, String[] args);
}
