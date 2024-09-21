package com.github.unreference.refraction.utility;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtility {
    public static void sendMessage(Player target, String message, Object... args) {
        target.sendMessage(Component.text(String.format(message, args)));
    }

    public static void sendMessage(CommandSender target, String message, Object... args) {
        target.sendMessage(Component.text(String.format(message, args)));
    }
}
