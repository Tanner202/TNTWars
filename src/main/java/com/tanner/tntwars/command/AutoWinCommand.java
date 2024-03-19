package com.tanner.tntwars.command;

import com.tanner.tntwars.GameState;
import com.tanner.tntwars.TNTWars;
import com.tanner.tntwars.instance.Arena;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AutoWinCommand implements CommandExecutor {

    private TNTWars tntWars;

    public AutoWinCommand(TNTWars tntWars) {
        this.tntWars = tntWars;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            Arena arena = tntWars.getArenaManager().getArena(player);
            if (arena != null && arena.getState().equals(GameState.LIVE)) {
                arena.sendMessage(ChatColor.GREEN + player.getName() + " Has Won the Game! Thanks for Playing");
                arena.reset(true);
            }
        }

        return false;
    }

}
