package com.tanner.minigame.listener;

import com.tanner.minigame.GameState;
import com.tanner.minigame.Minigame;
import com.tanner.minigame.instance.Arena;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

public class GameListener implements Listener {

    private Minigame minigame;

    private float tntLaunchPower = 3f;
    private float tntHeight = 5f;

    private float playerDoubleJumpPower = 3f;

    public GameListener(Minigame minigame) {
        this.minigame = minigame;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        player.setAllowFlight(true);
        player.setFlying(false);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Arena arena = minigame.getArenaManager().getArena(player);

        if (arena != null && arena.getState().equals(GameState.LIVE)) {
            if (e.getAction().equals(Action.LEFT_CLICK_AIR) && player.getInventory().getItemInMainHand().getType().equals(Material.TNT)) {
                World world = player.getWorld();
                TNTPrimed tntPrimed = (TNTPrimed) world.spawnEntity(player.getEyeLocation(), EntityType.PRIMED_TNT);
                Vector playerFacing = player.getEyeLocation().getDirection();

                Vector heightVector = new Vector(0, tntHeight, 0);
                tntPrimed.setVelocity(playerFacing.multiply(tntLaunchPower).add(heightVector));
            }
        }
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent e) {

        Player player = e.getPlayer();
        e.setCancelled(true);

        Arena arena = minigame.getArenaManager().getArena(player);
        if (arena != null && arena.getState().equals(GameState.LIVE)) {
            Vector playerVelocity = player.getVelocity();
            Vector doubleJumpVector = new Vector(playerVelocity.getX(), playerVelocity.getY() * playerDoubleJumpPower,
                    playerVelocity.getZ());
            player.setVelocity(doubleJumpVector);
        }
    }
}
