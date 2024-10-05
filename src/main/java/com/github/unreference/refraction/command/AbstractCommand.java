package com.github.unreference.refraction.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCommand extends org.bukkit.command.Command implements Command {
  private final String name;
  private final String prefix;
  private final String permission;
  private final List<String> aliases;
  protected String aliasUsed;
  protected String mainAliasUsed;

  protected AbstractCommand(String name, String prefix, String permission, String... aliases) {
    super(name);

    this.name = name;
    this.prefix = prefix;
    this.permission = permission;
    this.aliases = Arrays.asList(aliases);

    generatePermissions();
  }

  @Override
  public @NotNull List<String> tabComplete(
      @NotNull CommandSender sender, @NotNull String alias, String[] args) {
    return tab(sender, alias, args);
  }

  @Override
  public boolean execute(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
    setAliasUsed(alias);

    CommandContext context = new CommandContext(sender, args);

    trigger(context);
    return true;
  }

  @Override
  public @NotNull String getName() {
    return name;
  }

  public String getPrefix() {
    return prefix;
  }

  @Override
  public String getPermission() {
    return permission;
  }

  @Override
  public @NotNull List<String> getAliases() {
    return aliases;
  }

  protected String getAliasUsed() {
    return aliasUsed;
  }

  @Override
  public void setAliasUsed(String alias) {
    aliasUsed = alias;
  }

  protected String getMainAliasUsed() {
    return mainAliasUsed;
  }

  @Override
  public void setMainAliasUsed(String alias) {
    mainAliasUsed = alias;
  }

  public List<String> getOnlinePlayers() {
    return Bukkit.getOnlinePlayers().stream()
        .map(Player::getName)
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public void filterTab(List<String> suggestions, String arg) {
    suggestions.removeIf(suggestion -> !suggestion.toLowerCase().startsWith(arg.toLowerCase()));
  }

  protected abstract Component getUsageMessage();

  protected abstract void generatePermissions();
}
