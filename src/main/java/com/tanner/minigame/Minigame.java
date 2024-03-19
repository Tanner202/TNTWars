package com.tanner.minigame;

import com.tanner.minigame.command.ArenaCommand;
import com.tanner.minigame.command.ValueAdjustCommand;
import com.tanner.minigame.listener.ConnectListener;
import com.tanner.minigame.listener.GameListener;
import com.tanner.minigame.manager.ArenaManager;
import com.tanner.minigame.manager.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Minigame extends JavaPlugin {

    private ArenaManager arenaManager;

    @Override
    public void onEnable() {
        ConfigManager.setupConfig(this);
        arenaManager = new ArenaManager(this);

        Bukkit.getPluginManager().registerEvents(new ConnectListener(this), this);

        GameListener gameListener = new GameListener(this);
        Bukkit.getPluginManager().registerEvents(gameListener, this);

        getCommand("arena").setExecutor(new ArenaCommand(this));
        getCommand("adjust-value").setExecutor(new ValueAdjustCommand(gameListener));
    }

    public ArenaManager getArenaManager() { return arenaManager; }
}
