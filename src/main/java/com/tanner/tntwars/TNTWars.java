package com.tanner.tntwars;

import com.tanner.minigames.Minigames;
import com.tanner.tntwars.kit.TNTWarsKitType;
import com.tanner.tntwars.listener.ArenaListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class TNTWars extends JavaPlugin {

    public static Minigames minigamesAPI;

    @Override
    public void onEnable() {
        minigamesAPI = (Minigames) Bukkit.getPluginManager().getPlugin("Minigames");
        Bukkit.getPluginManager().registerEvents(new ArenaListener(this, minigamesAPI.getArena()), this);

        saveDefaultConfig();

        minigamesAPI.getArena().setKitTypes(TNTWarsKitType.values());
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }
}
