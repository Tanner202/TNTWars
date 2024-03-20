package com.tanner.tntwars.listener;

import com.tanner.tntwars.GameState;
import com.tanner.tntwars.TNTWars;
import com.tanner.tntwars.instance.Arena;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.util.Vector;

public class GameListener implements Listener {

    private TNTWars tntWars;

    private float tntLaunchPower = 2f;
    private float tntHeight = 0.5f;
    private int fuseTime = 45;

    private float playerDoubleJumpPower = 1f;
    private float forwardPower = 1f;

    public GameListener(TNTWars tntWars) {
        this.tntWars = tntWars;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Arena arena = tntWars.getArenaManager().getArena(player);

        if (arena != null && arena.getState().equals(GameState.LIVE)) {
            if (e.getAction().equals(Action.LEFT_CLICK_AIR) && player.getInventory().getItemInMainHand().getType().equals(Material.TNT)) {
                World world = player.getWorld();
                TNTPrimed tntPrimed = (TNTPrimed) world.spawnEntity(player.getEyeLocation(), EntityType.PRIMED_TNT);
                tntPrimed.setFuseTicks(fuseTime);
                Vector playerFacing = player.getEyeLocation().getDirection();

                Vector heightVector = new Vector(0, tntHeight, 0);
                tntPrimed.setVelocity(playerFacing.multiply(tntLaunchPower).add(heightVector));
            }
        }
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent e) {
        Player player = e.getPlayer();

        Arena arena = tntWars.getArenaManager().getArena(player);
        if (arena != null && arena.getState().equals(GameState.LIVE)) {
            e.setCancelled(true);
            Vector playerDirection = player.getLocation().getDirection();
            Vector doubleJumpVector = new Vector(playerDirection.getX() * forwardPower, playerDoubleJumpPower,
                    playerDirection.getZ() * forwardPower);
            player.setVelocity(doubleJumpVector);
        }
    }

    @EventHandler
    public void onWorldLoadEvent(WorldLoadEvent e) {

        Arena arena = tntWars.getArenaManager().getArena(e.getWorld());
        if (arena != null) {
            arena.toggleCanJoin();
        }
    }

    public void setTntLaunchPower(float tntLaunchPower) {
        this.tntLaunchPower = tntLaunchPower;
    }

    public void setTntHeight(float tntHeight) {
        this.tntHeight = tntHeight;
    }

    public void setFuseTime(int fuseTime) {
        this.fuseTime = fuseTime;
    }

    public void setPlayerDoubleJumpPower(float playerDoubleJumpPower) {
        this.playerDoubleJumpPower = playerDoubleJumpPower;
    }

    public void setForwardPower(float forwardPower) {
        this.forwardPower = forwardPower;
    }

    public float getTntLaunchPower() {
        return tntLaunchPower;
    }

    public float getTntHeight() {
        return tntHeight;
    }

    public int getFuseTime() {
        return fuseTime;
    }

    public float getPlayerDoubleJumpPower() {
        return playerDoubleJumpPower;
    }

    public float getForwardPower() {
        return forwardPower;
    }
}
