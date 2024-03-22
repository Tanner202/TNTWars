package com.tanner.tntwars;

import com.tanner.tntwars.command.ArenaCommand;
import com.tanner.tntwars.command.AutoWinCommand;
import com.tanner.tntwars.command.ValueAdjustCommand;
import com.tanner.tntwars.listener.ConnectListener;
import com.tanner.tntwars.listener.GameListener;
import com.tanner.tntwars.listener.GameLobbyListener;
import com.tanner.tntwars.manager.ArenaManager;
import com.tanner.tntwars.manager.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class TNTWars extends JavaPlugin {

    private ArenaManager arenaManager;

    @Override
    public void onEnable() {
        ConfigManager.setupConfig(this);
        arenaManager = new ArenaManager(this);

        Bukkit.getPluginManager().registerEvents(new ConnectListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GameLobbyListener(this), this);

        GameListener gameListener = new GameListener(this);
        Bukkit.getPluginManager().registerEvents(gameListener, this);

        getCommand("arena").setExecutor(new ArenaCommand(this));
        getCommand("adjust-value").setExecutor(new ValueAdjustCommand(gameListener));
        getCommand("autowin").setExecutor(new AutoWinCommand(this));
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    public ArenaManager getArenaManager() { return arenaManager; }
}
