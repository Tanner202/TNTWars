package com.tanner.minigames.command;

import com.tanner.minigames.listener.GameListener;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ValueAdjustCommand implements CommandExecutor {

    private GameListener gameListener;

    public ValueAdjustCommand(GameListener gameListener) {
        this.gameListener = gameListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("tntwars.op")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to perform this command!");
                return false;
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
                player.sendMessage(ChatColor.GREEN + "- Forward Power: " + gameListener.getForwardPower());
                player.sendMessage(ChatColor.GREEN + "- Fuse Time: " + gameListener.getFuseTime());
                player.sendMessage(ChatColor.GREEN + "- Double Jump: " + gameListener.getPlayerDoubleJumpPower());
                player.sendMessage(ChatColor.GREEN + "- Launch Power: " + gameListener.getTntLaunchPower());
                player.sendMessage(ChatColor.GREEN + "- Tnt Height: " + gameListener.getTntHeight());
                player.sendMessage(ChatColor.GREEN + "- Snowball Explosion Power: " + gameListener.getSnowballExplosionPower());
                return false;
            }

            if (args.length != 2) {
                player.sendMessage(ChatColor.RED + "You did not input the correct amount of args");
                player.sendMessage(ChatColor.RED + "Format: adjust-value [variable] [value]");
                return false;
            }

            float value = Float.parseFloat(args[1]);

            if (args[0].equalsIgnoreCase("forward-power")) {
                gameListener.setForwardPower(value);
                player.sendMessage(ChatColor.GREEN + "Set Forward Power to: " + value);
            } else if (args[0].equalsIgnoreCase("fuse-time")) {
                gameListener.setFuseTime((int) value);
                player.sendMessage(ChatColor.GREEN + "Set Fuse Time to: " + value);
            } else if (args[0].equalsIgnoreCase("double-jump")) {
                gameListener.setPlayerDoubleJumpPower(value);
                player.sendMessage(ChatColor.GREEN + "Set Double Jump to: " + value);
            } else if (args[0].equalsIgnoreCase("tnt-launch")) {
                gameListener.setTntLaunchPower(value);
                player.sendMessage(ChatColor.GREEN + "Set Launch Power to: " + value);
            } else if (args[0].equalsIgnoreCase("tnt-height")) {
                gameListener.setTntHeight(value);
                player.sendMessage(ChatColor.GREEN + "Set Tnt Height to: " + value);
            } else if (args[0].equalsIgnoreCase("snowball-explosion-power")) {
                    gameListener.setSnowballExplosionPower(value);
                    player.sendMessage(ChatColor.GREEN + "Set Snowball Explosion Power to: " + value);
            } else {
                player.sendMessage(ChatColor.RED + "You did not input a valid variable");
                player.sendMessage(ChatColor.RED + "Here are the valid variables:");
                player.sendMessage(ChatColor.RED + "- forward-power");
                player.sendMessage(ChatColor.RED + "- fuse-time");
                player.sendMessage(ChatColor.RED + "- double-jump");
                player.sendMessage(ChatColor.RED + "- tnt-launch");
                player.sendMessage(ChatColor.RED + "- tnt-height");
                player.sendMessage(ChatColor.RED + "- snowball-explosion-power");
                return false;
            }
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        }
        return false;
    }
}
