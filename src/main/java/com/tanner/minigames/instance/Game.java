package com.tanner.minigames.instance;

import com.tanner.minigames.GameState;
import com.tanner.minigames.Minigames;
import com.tanner.minigames.team.Team;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Game {

    private Minigames minigames;

    private Arena arena;

    private long winWaitTime = 100;
    private int tntInterval = 200;
    private int snowballInterval = 25;

    private HashMap<Team, Location> teamSpawns;
    private BukkitTask giveTntTask;
    private BukkitTask giveSnowballTask;

    private List<UUID> remainingPlayers;

    public Game(Arena arena, Minigames minigames) {
        this.arena = arena;
        this.minigames = minigames;
        this.teamSpawns = new HashMap<>();
        this.remainingPlayers = new ArrayList<>();
    }

    public void start() {
        for (UUID uuid : arena.getPlayers()) {
            Bukkit.getPlayer(uuid).getInventory().clear();
        }

        for (Team team : arena.getTeams()) {
            teamSpawns.put(team, getTeamSpawn(team));
        }

        arena.setState(GameState.LIVE);
        arena.sendMessage(ChatColor.GREEN + "Game Has Started! Knock the other player off by launching TNT. Last team standing wins!");

        for (UUID uuid : arena.getKits().keySet()) {
            arena.getKits().get(uuid).onStart(Bukkit.getPlayer(uuid));
        }

        for (UUID uuid : arena.getPlayers()) {
            remainingPlayers.add(uuid);
            Player player = Bukkit.getPlayer(uuid);
            player.closeInventory();

            Team team = arena.getTeam(player);
            Location teamSpawnLocation = teamSpawns.get(team);
            player.teleport(teamSpawnLocation);

            player.setAllowFlight(true);
            player.setFlying(false);
        }

        giveTntTask = Bukkit.getScheduler().runTaskTimer(minigames, this::givePlayersTnt, 100, tntInterval);
        giveSnowballTask = Bukkit.getScheduler().runTaskTimer(minigames, this::givePlayersSnowball, 50, snowballInterval);
    }

    private Location getTeamSpawn(Team team) {
        FileConfiguration config = minigames.getConfig();
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

    private void givePlayersTnt() {
        ItemStack throwableTnt = new ItemStack(Material.TNT, 1);
        String message = ChatColor.GREEN + "+1 Throwable Tnt";
        Sound sound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;

        givePlayersItem(throwableTnt, message, sound);
    }

    private void givePlayersSnowball() {
        ItemStack explosiveSnowball = new ItemStack(Material.SNOWBALL, 1);
        String message = ChatColor.AQUA + "+1 Explosive Snowball";
        Sound sound = Sound.ENTITY_ITEM_PICKUP;

        givePlayersItem(explosiveSnowball, message, sound);
    }

    private void givePlayersItem(ItemStack item, String message, Sound sound) {
        for (UUID uuid : remainingPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            player.getInventory().addItem(item);

            player.sendMessage(message);
            player.playSound(player.getLocation(), sound, 1f, 1f);
        }
    }

    public void end() {
        giveTntTask.cancel();
        giveSnowballTask.cancel();
    }

    public List<UUID> getRemainingPlayers() { return remainingPlayers; }

    public void removeRemainingPlayer(UUID playerUUID) {
        remainingPlayers.remove(playerUUID);

        Team team = getWinningTeam();
        if (team != null) {
            arena.sendMessage(team.getDisplay() + ChatColor.GREEN + " Team has Won! Thanks for Playing!");
            Bukkit.getScheduler().runTaskLater(minigames, () -> arena.reset(), winWaitTime);
        }
    }

    public void testWin() {
        Team team = getWinningTeam();
        if (team != null) {
            arena.sendMessage(team.getDisplay() + ChatColor.GREEN + " Team has Won! Thanks for Playing!");
            Bukkit.getScheduler().scheduleSyncDelayedTask(minigames, () -> arena.reset(), winWaitTime);
        }
    }

    private Team getWinningTeam() {
        Team winningTeam = null;
        for (Team team : arena.getTeams()) {
            HashMap<Team, Integer> remainingPlayersPerTeam = getRemainingPlayersPerTeam();
            if (remainingPlayersPerTeam.get(team) > 0) {
                if (winningTeam == null) {
                    winningTeam = team;
                }
                else
                {
                    return null;
                }
            }
        }
        return winningTeam;
    }

    private HashMap<Team, Integer> getRemainingPlayersPerTeam() {
        HashMap<Team, Integer> remainingPlayersPerTeam = new HashMap<>();
        for (Team team : arena.getTeams()) {
            remainingPlayersPerTeam.put(team, 0);
        }
        for (UUID uuid : remainingPlayers) {
            Team playerTeam = arena.getTeam(Bukkit.getPlayer(uuid));
            Integer remainingTeamPlayers = remainingPlayersPerTeam.get(playerTeam);
            remainingPlayersPerTeam.replace(playerTeam, remainingTeamPlayers, remainingTeamPlayers + 1);
        }
        return remainingPlayersPerTeam;
    }
}
