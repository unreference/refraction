package com.github.unreference.refraction.command.impl.world;

import com.github.unreference.refraction.command.AbstractCommand;
import com.github.unreference.refraction.command.CommandContext;
import com.github.unreference.refraction.domain.model.Rank;
import com.github.unreference.refraction.util.MessageUtil;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand extends AbstractCommand {

  public SpawnCommand() {
    super("spawn", "Spawn", "refraction.command.spawn");
  }

  @Override
  public void trigger(CommandContext context) {
    String[] args = context.getArgs();
    CommandSender sender = context.getSender();

    if (args.length != 0) {
      sender.sendMessage(getUsageMessage());
      return;
    }

    Player player = (Player) sender;
    Location spawn = player.getWorld().getSpawnLocation();
    player.teleport(spawn);
  }

  @Override
  public List<String> tab(CommandSender sender, String alias, String[] args) {
    return List.of();
  }

  @Override
  protected Component getUsageMessage() {
    return MessageUtil.getPrefixedMessage(getPrefix(), "Usage: /" + getAliasUsed());
  }

  @Override
  protected void generatePermissions() {
    Rank.PLAYER.grantPermission(getPermission(), true);
    Rank.STM.revokePermission(getPermission());
  }
}
