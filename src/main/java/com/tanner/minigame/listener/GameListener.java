package com.tanner.minigame.listener;

import com.tanner.minigame.GameState;
import com.tanner.minigame.Minigame;
import com.tanner.minigame.instance.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class GameListener implements Listener {

    private Minigame minigame;

    public GameListener(Minigame minigame) {
        this.minigame = minigame;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Arena arena = minigame.getArenaManager().getArena(player);

        if (arena != null && arena.getState().equals(GameState.LIVE)) {
            arena.getGame().addPoint(player);
        }
    }

}
