package com.tanner.minigame.manager;

import com.tanner.minigame.instance.Arena;
import com.tanner.minigame.Minigame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArenaManager {

    private List<Arena> arenas = new ArrayList<>();

    private FileConfiguration config;

    public ArenaManager(Minigame minigame) {
        config = minigame.getConfig();

        addArenasFromConfig(minigame);
    }

    private void addArenasFromConfig(Minigame minigame) {
        for (String arenaID : config.getConfigurationSection("arenas").getKeys(false)) {
            arenas.add(new Arena(minigame, Integer.parseInt(arenaID), getArenaLocation(arenaID)));
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
}
