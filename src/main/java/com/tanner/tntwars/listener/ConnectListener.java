package com.tanner.tntwars.listener;

import com.tanner.tntwars.TNTWars;
import com.tanner.tntwars.instance.Arena;
import com.tanner.tntwars.manager.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectListener implements Listener {

    private TNTWars tntWars;

    public ConnectListener(TNTWars tntWars) {
        this.tntWars = tntWars;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        e.getPlayer().teleport(ConfigManager.getLobbySpawn());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {

        Player player = e.getPlayer();
        Arena arena = tntWars.getArenaManager().getArena(player);

        if (arena != null) {
            arena.removePlayer(player);

        }
    }
}
