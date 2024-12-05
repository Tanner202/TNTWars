package com.tanner.tntwars.listener;

import com.tanner.minigames.event.GameEndEvent;
import com.tanner.minigames.event.GameStartEvent;
import com.tanner.minigames.instance.Arena;
import com.tanner.tntwars.TNTWars;
import com.tanner.tntwars.instance.Game;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ArenaListener implements Listener {

    private Arena arena;
    private TNTWars tntWars;

    private Game game;

    public ArenaListener(TNTWars tntWars, Arena arena) {
        this.arena = arena;
        this.tntWars = tntWars;
    }

    @EventHandler
    public void onGameStart(GameStartEvent e) {
        game = new Game(arena, tntWars);
        Bukkit.broadcastMessage("TNTWARS: on game start");
    }

    @EventHandler
    public void onGameEnd(GameEndEvent e) {
        game.end();
    }
}
