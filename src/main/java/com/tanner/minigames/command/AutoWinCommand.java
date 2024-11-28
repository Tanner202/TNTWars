package com.tanner.minigames.command;

import com.tanner.minigames.GameState;
import com.tanner.minigames.Minigames;
import com.tanner.minigames.instance.Arena;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AutoWinCommand implements CommandExecutor {

    private Minigames minigames;

    public AutoWinCommand(Minigames minigames) {
        this.minigames = minigames;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            Arena arena = minigames.getArenaManager().getArena(player);
            if (arena != null && arena.getState().equals(GameState.LIVE)) {;
                arena.getGame().testWin();
            }
        }

        return false;
    }

}
