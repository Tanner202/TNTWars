package com.tanner.tntwars.instance;

import com.tanner.tntwars.GameState;
import com.tanner.tntwars.TNTWars;
import com.tanner.tntwars.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Game {

    private TNTWars tntWars;

    private Arena arena;

    private HashMap<Team, Location> teamSpawns;

    public Game(Arena arena, TNTWars tntWars) {
        this.arena = arena;
        this.tntWars = tntWars;
        this.teamSpawns = new HashMap<>();
    }

    public void start() {
        for (Team team : arena.getTeams()) {
            teamSpawns.put(team, getTeamSpawn(team));
        }

        arena.setState(GameState.LIVE);
        arena.sendMessage(ChatColor.GREEN + "Game Has Started! Knock the other player off by launching TNT. Last team standing wins!");

        for (UUID uuid : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            player.closeInventory();

            Team team = arena.getTeam(player);
            Location teamSpawnLocation = teamSpawns.get(team);
            player.teleport(teamSpawnLocation);

            player.setAllowFlight(true);
            player.setFlying(false);
        }
    }

    private Location getTeamSpawn(Team team) {
        FileConfiguration config = tntWars.getConfig();
        String teamName = ChatColor.stripColor(team.getDisplay());
        String teamSpawnPath = "arenas." + arena.getId() + ".team-spawns." + teamName.toLowerCase();
        return new Location(
                Bukkit.getWorld(config.getString(teamSpawnPath + ".world")),
                config.getDouble( teamSpawnPath + ".x"),
                config.getDouble(teamSpawnPath + ".y"),
                config.getDouble(teamSpawnPath + ".z"),
                (float) config.getDouble(teamSpawnPath + ".yaw"),
                (float) config.getDouble(teamSpawnPath + ".pitch"));
    }
}
