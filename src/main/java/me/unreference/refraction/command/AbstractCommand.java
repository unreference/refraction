package me.unreference.refraction.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractCommand extends Command implements CommandInterface {
    private final String name;
    private final String permission;
    private final List<String> aliases;
    protected String aliasUsed;
    protected String mainAliasUsed;

    protected AbstractCommand(@NotNull String name, String permission, String... aliases) {
        super(name);

        this.name = name;
        this.permission = permission;
        this.aliases = Arrays.asList(aliases);

        generatePermissions();
    }

    @Override
    public abstract void trigger(CommandSender sender, String[] args);

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        return tab(sender, alias, args);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        setAliasUsed(alias);
        trigger(sender, args);
        return true;
    }

    public @NotNull String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

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
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

    public void filterTab(List<String> suggestions, String arg) {
        suggestions.removeIf(suggestion -> !suggestion.toLowerCase().startsWith(arg.toLowerCase()));
    }

    protected abstract String getUsageMessage();

    protected abstract void generatePermissions();
}
