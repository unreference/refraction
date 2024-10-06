package com.github.unreference.refraction.command;

import com.github.unreference.refraction.data.manager.AccountRanksRepositoryManager;
import com.github.unreference.refraction.data.manager.AccountsRepositoryManager;
import com.github.unreference.refraction.model.Rank;
import com.github.unreference.refraction.util.UtilMessage;
import java.util.*;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractParameterizedCommand extends AbstractCommand {
  private final Map<String, Command> subcommands = new HashMap<>();
  private final boolean isTargetRequired;

  protected AbstractParameterizedCommand(
      String name, String prefix, String permission, boolean isTargetRequired, String... aliases) {
    super(name, prefix, permission, aliases);
    this.isTargetRequired = isTargetRequired;

    generatePermissions();
  }

  @Override
  public void trigger(CommandContext context) {
    String[] args = context.getArgs();
    CommandSender sender = context.getSender();

    if (args.length == 0) {
      execute(sender, args);
      return;
    }

    String firstArg = args[0].toLowerCase();
    Command subcommand = subcommands.get(firstArg);

    if (subcommand != null) {
      if (subcommand.isTargetRequired()) {
        if (args.length < 2) {
          execute(sender, args);
          return;
        }

        handleSubcommandWithTarget(sender, args);
        return;
      } else {
        handleSubcommand(sender, args, 0);
        return;
      }
    }

    UUID targetId = AccountsRepositoryManager.get().getId(firstArg);
    boolean isFirstArgTarget = targetId != null;

    if (isFirstArgTarget && args.length > 1) {
      String secondArg = args[1].toLowerCase();
      subcommand = subcommands.get(secondArg);

      if (subcommand != null && subcommand.isTargetRequired()) {
        handleSubcommandWithTarget(sender, args);
        return;
      }
    }

    execute(sender, args);
  }

  @Override
  public List<String> tab(CommandSender sender, String alias, String[] args) {
    List<String> suggestions = new ArrayList<>();

    if (args.length == 1) {
      subcommands.values().stream()
          .filter(
              subcommand ->
                  !subcommand.isTargetRequired() && isPermitted(sender, subcommand.getPermission()))
          .forEach(subcommand -> suggestions.add(subcommand.getName().toLowerCase()));

      suggestions.addAll(getOnlinePlayers());
      filterTab(suggestions, args[0]);
      return suggestions;
    }

    if (args.length == 2) {
      UUID targetId = AccountsRepositoryManager.get().getId(args[0]);

      if (targetId != null) {
        subcommands.values().stream()
            .filter(
                subcommand ->
                    subcommand.isTargetRequired()
                        && isPermitted(sender, subcommand.getPermission()))
            .forEach(subcommand -> suggestions.add(subcommand.getName().toLowerCase()));

        filterTab(suggestions, args[1]);
      }

      return suggestions;
    } else {
      Command subcommand = subcommands.get(args[1]);
      if (subcommand != null) {
        return subcommand.tab(sender, alias, Arrays.copyOfRange(args, 2, args.length));
      }
    }

    return Collections.emptyList();
  }

  @Override
  public boolean execute(
      @NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
    setAliasUsed(alias);
    CommandContext context = new CommandContext(sender, null, null, args);
    trigger(context);
    return true;
  }

  @Override
  public @NotNull List<String> tabComplete(
      @NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
    return tab(sender, alias, args);
  }

  private boolean isPermitted(CommandSender sender, String permission) {
    if (!(sender instanceof Player player)) {
      return true;
    }

    Rank rank =
        Rank.getRankFromId(AccountRanksRepositoryManager.get().getRank(player.getUniqueId()));
    return rank.isPermitted(permission);
  }

  private void handleSubcommandWithTarget(CommandSender sender, String[] args) {
    String targetInput = args[0];
    UUID targetId = AccountsRepositoryManager.get().getId(targetInput);

    if (targetId == null) {
      sender.sendMessage(
          UtilMessage.getPrefixedMessage(getPrefix(), "Player not found: &e%s", args[0]));
      return;
    }

    String targetName = AccountsRepositoryManager.get().getName(targetId);
    handleSubcommand(sender, args, 1, targetName);
  }

  private void handleSubcommand(
      CommandSender sender, String[] args, int offset, String targetName) {
    if (args.length <= offset) {
      execute(sender, args);
      return;
    }

    String action = args[offset].toLowerCase();
    Command subcommand = subcommands.get(action);

    if (subcommand != null) {
      subcommand.setAliasUsed(action);
      subcommand.setMainAliasUsed(getAliasUsed());

      CommandContext context =
          new CommandContext(
              sender, targetName, action, Arrays.copyOfRange(args, offset + 1, args.length));

      if (isPermitted(sender, subcommand.getPermission())) {
        subcommand.trigger(context);
      } else {
        execute(sender, args);
      }
    } else {
      execute(sender, args);
    }
  }

  private void handleSubcommand(CommandSender sender, String[] args, int offset) {
    if (args.length <= offset) {
      execute(sender, args);
      return;
    }

    String action = args[offset];
    Command subcommand = subcommands.get(action);

    if (subcommand != null) {
      subcommand.setAliasUsed(action);
      subcommand.setMainAliasUsed(getAliasUsed());

      CommandContext context =
          new CommandContext(
              sender, null, action, Arrays.copyOfRange(args, offset + 1, args.length));

      if (isPermitted(sender, subcommand.getPermission())) {
        subcommand.trigger(context);
      } else {
        execute(sender, args);
      }
    }
  }

  private List<String> getPermittedSubcommands(CommandSender sender) {
    return subcommands.entrySet().stream()
        .filter(entry -> isPermitted(sender, entry.getValue().getPermission()))
        .map(Map.Entry::getKey)
        .collect(Collectors.toCollection(ArrayList::new));
  }

  protected void addSubcommand(Command subcommand) {
    subcommands.put(subcommand.getName().toLowerCase(), subcommand);
    for (String alias : subcommand.getAliases()) {
      subcommands.put(alias.toLowerCase(), subcommand);
    }
  }

  protected abstract void execute(CommandSender sender, String[] args);
}
