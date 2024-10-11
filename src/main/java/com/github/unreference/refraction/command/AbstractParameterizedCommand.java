package com.github.unreference.refraction.command;

import com.github.unreference.refraction.data.manager.AccountRanksRepositoryManager;
import com.github.unreference.refraction.data.manager.AccountsRepositoryManager;
import com.github.unreference.refraction.domain.model.Rank;
import com.github.unreference.refraction.util.MessageUtil;
import java.util.*;
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

    if (isTargetRequired) {
      UUID targetId = AccountsRepositoryManager.get().getId(firstArg);

      if (targetId == null) {
        sender.sendMessage(
            MessageUtil.getPrefixedMessage(getPrefix(), "Player not found: &e%s", args[0]));
        return;
      }
    }

    if (args.length > 1) {
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
    if (args.length == 1) {
      return handleFirstArgument(sender, args[0]);
    }

    if (args.length == 2) {
      return handleSecondArgument(sender, alias, args[0], args[1]);
    }

    if (args.length == 3) {
      return handleThirdArgument(sender, alias, args[0], args[1], args[2]);
    }

    return Collections.emptyList();
  }

  private List<String> handleFirstArgument(CommandSender sender, String arg) {
    List<String> suggestions = new ArrayList<>();

    subcommands.values().stream()
        .filter(
            subcommand ->
                !subcommand.isTargetRequired() && isPermitted(sender, subcommand.getPermission()))
        .forEach(subcommand -> suggestions.add(subcommand.getName().toLowerCase()));

    if (isTargetRequired) {
      suggestions.addAll(getOnlinePlayers());
    }

    filterTab(suggestions, arg);
    return suggestions;
  }

  private List<String> handleSecondArgument(
      CommandSender sender, String alias, String firstArg, String secondArg) {
    List<String> suggestions = new ArrayList<>();

    Command subcommand = subcommands.get(firstArg.toLowerCase());
    if (subcommand != null && !subcommand.isTargetRequired()) {
      return subcommand.tab(
          sender, alias, Arrays.copyOfRange(new String[] {firstArg, secondArg}, 1, 2));
    }

    subcommands.values().stream()
        .filter(sub -> sub.isTargetRequired() && isPermitted(sender, sub.getPermission()))
        .forEach(sub -> suggestions.add(sub.getName().toLowerCase()));

    filterTab(suggestions, secondArg);
    return suggestions;
  }

  private List<String> handleThirdArgument(
      CommandSender sender, String alias, String firstArg, String secondArg, String thirdArg) {
    Command subcommand = subcommands.get(secondArg.toLowerCase());
    if (subcommand != null && subcommand.isTargetRequired()) {
      return subcommand.tab(
          sender, alias, Arrays.copyOfRange(new String[] {firstArg, secondArg, thirdArg}, 2, 3));
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
    List<Rank> subranks = AccountRanksRepositoryManager.get().getSubranks(player.getUniqueId());

    for (Rank sr : subranks) {
      if (sr.isPermitted(permission)) {
        return true;
      }
    }

    return rank.isPermitted(permission);
  }

  private void handleSubcommandWithTarget(CommandSender sender, String[] args) {
    String targetInput = args[0];
    UUID targetId = AccountsRepositoryManager.get().getId(targetInput);
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

  protected void addSubcommand(Command subcommand) {
    subcommands.put(subcommand.getName().toLowerCase(), subcommand);
    for (String alias : subcommand.getAliases()) {
      subcommands.put(alias.toLowerCase(), subcommand);
    }
  }

  protected abstract void execute(CommandSender sender, String[] args);
}
