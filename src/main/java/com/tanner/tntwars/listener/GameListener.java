package com.tanner.tntwars.listener;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.tanner.tntwars.GameState;
import com.tanner.tntwars.TNTWars;
import com.tanner.tntwars.instance.Arena;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GameListener implements Listener {

    private TNTWars tntWars;

    private float tntLaunchPower = 2f;
    private float tntHeight = 0.5f;
    private int fuseTime = 45;

    private float playerDoubleJumpPower = 1f;
    private float forwardPower = 1f;
    private long jumpCooldown = 3;
    private Cache<UUID, Long> doubleJumpCooldown = CacheBuilder.newBuilder().expireAfterWrite(jumpCooldown, TimeUnit.SECONDS).build();

    public GameListener(TNTWars tntWars) {
        this.tntWars = tntWars;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Arena arena = tntWars.getArenaManager().getArena(player);

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (arena != null && arena.getState().equals(GameState.LIVE)) {
            if (e.getAction().equals(Action.LEFT_CLICK_AIR) && itemInMainHand.getType().equals(Material.TNT)) {
                player.getInventory().remove(itemInMainHand);

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

            UUID playerUniqueId = player.getUniqueId();
            if (!doubleJumpCooldown.asMap().containsKey(playerUniqueId)) {
                doubleJump(player);
                doubleJumpCooldown.put(playerUniqueId, System.currentTimeMillis() + jumpCooldown * 1000);
            } else {
                long distance = doubleJumpCooldown.asMap().get(playerUniqueId) - System.currentTimeMillis();
                long remainingSeconds = TimeUnit.MILLISECONDS.toSeconds(distance);
                player.sendMessage(ChatColor.RED + "Your double jump is on cooldown for " + remainingSeconds +
                        " more second" + (remainingSeconds == 1 ? "" : "s"));
            }
        }
    }

    private void doubleJump(Player player) {
        Vector playerDirection = player.getLocation().getDirection();
        Vector doubleJumpVector = new Vector(playerDirection.getX() * forwardPower, playerDoubleJumpPower,
                playerDirection.getZ() * forwardPower);
        player.setVelocity(doubleJumpVector);

        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1f, 1f);
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
