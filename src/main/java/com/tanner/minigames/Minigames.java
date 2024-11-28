package com.tanner.minigames;

import com.tanner.minigames.command.ArenaCommand;
import com.tanner.minigames.command.AutoWinCommand;
import com.tanner.minigames.command.ValueAdjustCommand;
import com.tanner.minigames.listener.ConnectListener;
import com.tanner.minigames.listener.GameListener;
import com.tanner.minigames.listener.GameLobbyListener;
import com.tanner.minigames.manager.ArenaManager;
import com.tanner.minigames.manager.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Minigames extends JavaPlugin {

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
