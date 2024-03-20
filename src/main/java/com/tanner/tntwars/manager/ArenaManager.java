package com.tanner.tntwars.manager;

import com.tanner.tntwars.TNTWars;
import com.tanner.tntwars.instance.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArenaManager {

    private List<Arena> arenas = new ArrayList<>();

    private FileConfiguration config;

    public ArenaManager(TNTWars tntWars) {
        config = tntWars.getConfig();

        addArenasFromConfig(tntWars);
    }

    private void addArenasFromConfig(TNTWars TNTWars) {
        for (String arenaID : config.getConfigurationSection("arenas").getKeys(false)) {
            World world = Bukkit.createWorld(new WorldCreator(config.getString("arenas." + arenaID + ".world")));
            world.setAutoSave(false);
            arenas.add(new Arena(TNTWars, Integer.parseInt(arenaID), getArenaLocation(arenaID)));
        }
    }

    private Location getArenaLocation(String arenaID) {
        return new Location(
                Bukkit.getWorld(config.getString("arenas." + arenaID + ".world")),
                config.getDouble("arenas." + arenaID + ".x"),
                config.getDouble("arenas." + arenaID + ".y"),
                config.getDouble("arenas." + arenaID + ".z"),
                (float) config.getDouble("arenas." + arenaID + ".yaw"),
                (float) config.getDouble("arenas." + arenaID + ".pitch"));
    }

    public List<Arena> getArenas() { return arenas; }

    public Arena getArena(Player player) {
        for (Arena arena : arenas) {
            if (arena.getPlayers().contains(player.getUniqueId())) {
                return arena;
            }
        }
        return null;
    }

    public Arena getArena(int id) {
        for (Arena arena : arenas) {
            if (arena.getId() == id) {
                return arena;
            }
        }
        return null;
    }

    public Arena getArena(World world) {
        for (Arena arena : arenas) {
            if (arena.getWorld().getName().equals(world.getName())) {
                return arena;
            }
        }
        return null;
    }
}
