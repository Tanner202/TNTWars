package com.tanner.minigame.instance;

import com.tanner.minigame.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Game {

    private Arena arena;
    private HashMap<UUID, Integer> points;

    public Game(Arena arena) {
        this.arena = arena;
        points = new HashMap<>();
    }

    public void start() {
        arena.setState(GameState.LIVE);
        arena.sendMessage(ChatColor.GREEN + "Game Has Started! Your objective is to break 20 blocks in the fastest time. Good Luck");

        for (UUID uuid : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            player.setAllowFlight(true);
            player.setFlying(false);
            points.put(uuid, 0);
        }
    }

    public void addPoint(Player player) {
        int playerPoints = points.get(player.getUniqueId()) + 1;
        if (playerPoints == 20) {
            arena.sendMessage(ChatColor.GOLD + player.getName() + " has Won! Thanks for Playing!");
            arena.reset(true);
            return;
        }

        player.sendMessage(ChatColor.GREEN + "+1 Point");
        points.replace(player.getUniqueId(), playerPoints);
    }
}
