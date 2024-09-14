package me.unreference.refraction.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractCommand extends Command implements CommandInterface {
    private final String name;
    private final String permission;
    private final List<String> aliases;
    protected String aliasUsed;

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


    protected abstract String getUsageMessage();

    protected abstract void generatePermissions();
}
