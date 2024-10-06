package com.github.unreference.refraction.command;

import org.bukkit.command.CommandSender;

public class CommandContext {
  private final CommandSender sender;
  private final String subcommand;
  private final String[] args;
  protected String targetName;

  protected CommandContext(
      CommandSender sender, String targetName, String subcommand, String[] args) {
    this.sender = sender;
    this.targetName = targetName;
    this.subcommand = subcommand;
    this.args = args;
  }

  public CommandSender getSender() {
    return sender;
  }

  public String getTargetName() {
    return targetName;
  }

  public void setTargetName(String name) {
    targetName = name;
  }

  public String getSubcommand() {
    return subcommand;
  }

  public String[] getArgs() {
    return args;
  }
}
