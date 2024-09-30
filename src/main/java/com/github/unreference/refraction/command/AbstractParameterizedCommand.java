package com.github.unreference.refraction.command;

import com.github.unreference.refraction.Refraction;
import com.github.unreference.refraction.model.Rank;
import com.github.unreference.refraction.service.PlayerDataRepositoryService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractParameterizedCommand extends AbstractCommand {
    private final Map<String, Command> subcommands = new HashMap<>();
    private final boolean isPlayerRequired;

    protected AbstractParameterizedCommand(String name, String prefix, String permission, boolean isPlayerRequired, String... aliases) {
        super(name, prefix, permission, aliases);
        this.isPlayerRequired = isPlayerRequired;

        generatePermissions();
    }

    protected AbstractParameterizedCommand(String name, String prefix, String permission, String... aliases) {
        super(name, prefix, permission, aliases);
        this.isPlayerRequired = false;

        generatePermissions();
    }

    @Override
    public void trigger(CommandSender sender, String[] args) {

        if (args.length == 0) {
            execute(sender, args);
            return;
        }

        if (isPlayerRequired) {
            handleSubcommand(sender, args, 1);
        } else {
            handleSubcommand(sender, args, 0);
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String alias, String[] args) {
        if (isPlayerRequired) {
            switch (args.length) {
                case 1 -> {
                    List<String> suggestions = new ArrayList<>(getOnlinePlayers());
                    String currentArg = args[0];
                    filterTab(suggestions, currentArg);
                    return suggestions;
                }
                case 2 -> {
                    List<String> suggestions = new ArrayList<>(getPermittedSubcommands(sender));
                    String currentArg = args[1];
                    filterTab(suggestions, currentArg);
                    return suggestions;
                }
                default -> {
                    Command subcommand = subcommands.get(args[1]);
                    if (subcommand != null) {
                        return subcommand.tab(sender, alias, Arrays.copyOfRange(args, 2, args.length));
                    }
                }
            }
        } else {
            if (args.length == 1) {
                List<String> suggestions = new ArrayList<>(getPermittedSubcommands(sender));
                String currentArg = args[0];
                filterTab(suggestions, currentArg);
                return suggestions;
            } else {
                Command subcommand = subcommands.get(args[0]);
                if (subcommand != null) {
                    return subcommand.tab(sender, alias, Arrays.copyOfRange(args, 0, args.length));
                }
            }
        }

        return Collections.emptyList();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        setAliasUsed(alias);
        trigger(sender, args);
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return tab(sender, alias, args);
    }

    private boolean isPermitted(CommandSender sender, String permission) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        PlayerDataRepositoryService playerDataRepositoryService = Refraction.getPlayerDataRepositoryService();
        Rank rank = Rank.getRankFromId(playerDataRepositoryService.getRank(player.getName()));
        return rank.isPermitted(permission);
    }

    private void handleSubcommand(CommandSender sender, String[] args, int offset) {
        String action = args[offset];

        Command subcommand = subcommands.get(action);
        if (subcommand != null) {
            subcommand.setAliasUsed(action);
            subcommand.setMainAliasUsed(getMainAliasUsed());

            if (isPermitted(sender, subcommand.getPermission())) {
                subcommand.trigger(sender, Arrays.copyOfRange(args, offset + 1, args.length));
                return;
            }

            execute(sender, args);
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
