package com.tanner.tntwars.instance;

import com.tanner.tntwars.GameState;
import com.tanner.tntwars.TNTWars;
import com.tanner.tntwars.manager.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class Countdown extends BukkitRunnable {

    private TNTWars tntWars;
    private Arena arena;
    private int countdownSeconds;

    public Countdown(TNTWars tntWars, Arena arena) {
        this.tntWars = tntWars;
        this.arena = arena;
        this.countdownSeconds = ConfigManager.getCountdownSeconds();
    }

    public void start() {
        arena.setState(GameState.COUNTDOWN);
        runTaskTimer(tntWars, 0, 20);
    }

    @Override
    public void run() {
        if (countdownSeconds == 0) {
            cancel();
            arena.start();
            return;
        }

        if (countdownSeconds <= 10 || countdownSeconds % 15 == 0) {
            arena.sendMessage(ChatColor.GREEN + "Game will start in " + countdownSeconds + " second" +
                    (countdownSeconds == 1 ? "" : "s") + ".");
            arena.playSound(Sound.BLOCK_TRIPWIRE_CLICK_ON);
        }

        countdownSeconds--;
    }
}
