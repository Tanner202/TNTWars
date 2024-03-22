package com.tanner.tntwars.instance;

import com.tanner.tntwars.GameState;
import com.tanner.tntwars.TNTWars;
import com.tanner.tntwars.team.Team;
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

    private TNTWars tntWars;

    private Arena arena;

    private int tntInterval = 200;
    private int snowballInterval = 50;

    private HashMap<Team, Location> teamSpawns;
    private BukkitTask giveTntTask;
    private BukkitTask giveSnowballTask;

    private List<UUID> remainingPlayers;

    public Game(Arena arena, TNTWars tntWars) {
        this.arena = arena;
        this.tntWars = tntWars;
        this.teamSpawns = new HashMap<>();
        this.remainingPlayers = new ArrayList<>();
    }

    public void start() {
        for (Team team : arena.getTeams()) {
            teamSpawns.put(team, getTeamSpawn(team));
        }

        arena.setState(GameState.LIVE);
        arena.sendMessage(ChatColor.GREEN + "Game Has Started! Knock the other player off by launching TNT. Last team standing wins!");

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

        giveTntTask = Bukkit.getScheduler().runTaskTimer(tntWars, this::givePlayersTnt, 100, tntInterval);
        giveSnowballTask = Bukkit.getScheduler().runTaskTimer(tntWars, this::givePlayersSnowball, 50, snowballInterval);
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
    }
}
