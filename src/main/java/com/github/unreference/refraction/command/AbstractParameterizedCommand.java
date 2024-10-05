package com.github.unreference.refraction.command;

import com.github.unreference.refraction.data.manager.AccountRepositoryManager;
import com.github.unreference.refraction.model.Rank;
import com.github.unreference.refraction.util.MessageUtil;
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

  protected AbstractParameterizedCommand(
      String name, String prefix, String permission, String... aliases) {
    super(name, prefix, permission, aliases);
    this.isTargetRequired = false;

    generatePermissions();
  }

  @Override
  public void trigger(CommandContext context) {
    if (context.getArgs().length == 0) {
      execute(context.getSender(), context.getArgs());
      return;
    }

    if (isTargetRequired) {
      String targetInput = context.getArgs()[0];
      UUID targetId = AccountRepositoryManager.get().getId(targetInput);

      if (targetId == null) {
        context
            .getSender()
            .sendMessage(
                MessageUtil.getPrefixedMessage(
                    getPrefix(), "Player not found: &e%s", context.getArgs()[0]));
        return;
      }

      context.setTargetName(AccountRepositoryManager.get().getName(targetId));
      handleSubcommand(context.getSender(), context.getArgs(), 1);
    } else {
      handleSubcommand(context.getSender(), context.getArgs(), 0);
    }
  }

  @Override
  public List<String> tab(CommandSender sender, String alias, String[] args) {
    if (isTargetRequired) {
      if (args.length == 1) {
        List<String> suggestions = new ArrayList<>(getOnlinePlayers());
        filterTab(suggestions, args[0]);
        return suggestions;
      } else if (args.length == 2) {
        List<String> suggestions = new ArrayList<>(getPermittedSubcommands(sender));
        filterTab(suggestions, args[1]);
        return suggestions;
      } else {
        Command subcommand = subcommands.get(args[1].toLowerCase());
        if (subcommand != null) {
          return subcommand.tab(sender, alias, Arrays.copyOfRange(args, 2, args.length));
        }
      }
    } else {
      if (args.length == 1) {
        List<String> suggestions = new ArrayList<>(getPermittedSubcommands(sender));
        filterTab(suggestions, args[0]);
        return suggestions;
      } else {
        Command subcommand = subcommands.get(args[0].toLowerCase());
        if (subcommand != null) {
          return subcommand.tab(sender, alias, Arrays.copyOfRange(args, 1, args.length));
        }
      }
    }

    return Collections.emptyList();
  }

  @Override
  public boolean execute(
      @NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
    setAliasUsed(alias);

    CommandContext context = new CommandContext(sender, args);

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

    Rank rank = Rank.getRankFromId(AccountRepositoryManager.get().getPrimaryRank(player.getName()));
    return rank.isPermitted(permission);
  }

  private void handleSubcommand(CommandSender sender, String[] args, int offset) {
    if (args.length <= offset) {
      execute(sender, args);
      return;
    }

    String targetName = isTargetRequired ? args[0] : null;
    String action = args[offset];
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
