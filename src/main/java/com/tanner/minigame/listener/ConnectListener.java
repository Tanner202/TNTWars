package com.tanner.minigame.listener;

import com.tanner.minigame.Minigame;
import com.tanner.minigame.instance.Arena;
import com.tanner.minigame.manager.ArenaManager;
import com.tanner.minigame.manager.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectListener implements Listener {

    private Minigame minigame;

    public ConnectListener(Minigame minigame) {
        this.minigame = minigame;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        e.getPlayer().teleport(ConfigManager.getLobbySpawn());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {

        Player player = e.getPlayer();
        Arena arena = minigame.getArenaManager().getArena(player);

        if (arena != null) {
            arena.removePlayer(player);

        }
    }
}
