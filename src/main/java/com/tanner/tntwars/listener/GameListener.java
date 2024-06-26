package com.tanner.tntwars.listener;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.tanner.tntwars.GameState;
import com.tanner.tntwars.TNTWars;
import com.tanner.tntwars.instance.Arena;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GameListener implements Listener {

    private TNTWars tntWars;

    private float tntLaunchPower = 1.5f;
    private float tntHeight = 0.5f;
    private int fuseTime = 45;

    private float snowballExplosionPower = 2f;
    private float snowballLaunchPower = 1.5f;
    private float snowballHeight = 0.25f;

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
        if (arena != null && arena.isPlayerPlaying(player)) {
            if ((e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) && itemInMainHand.getType().equals(Material.TNT)) {
                itemInMainHand.setAmount(itemInMainHand.getAmount() - 1);

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
    public void onProjectileHitEvent(ProjectileHitEvent e) {
        if (e.getEntity().getShooter() instanceof Player) {
            Player player = (Player) e.getEntity().getShooter();
            Arena arena = tntWars.getArenaManager().getArena(player);

            if (arena != null && arena.isPlayerPlaying(player)) {
                World world = e.getEntity().getWorld();
                if (e.getEntity().getType().equals(EntityType.SNOWBALL)) {
                    if (e.getHitBlock() != null) {
                        Location hitLocation = e.getHitBlock().getLocation();
                        world.createExplosion(hitLocation, snowballExplosionPower, false, true);
                    } else if (e.getHitEntity() != null) {
                        Location hitLocation = e.getHitEntity().getLocation();
                        world.createExplosion(hitLocation, snowballExplosionPower, false, true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onProjectileLaunchEvent(ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() instanceof Player) {
            Player player = (Player) e.getEntity().getShooter();
            Arena arena = tntWars.getArenaManager().getArena(player);
            if (arena != null && arena.isPlayerPlaying(player)) {
                if (e.getEntity().getType().equals(EntityType.SNOWBALL)) {
                    Vector playerFacing = player.getEyeLocation().getDirection();

                    Vector heightVector = new Vector(0, snowballHeight, 0);
                    e.getEntity().setVelocity(playerFacing.multiply(snowballLaunchPower).add(heightVector));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent e) {
        Player player = e.getPlayer();

        Arena arena = tntWars.getArenaManager().getArena(player);
        if (arena != null && arena.isPlayerPlaying(player)) {
            Vector preEventVelocity = player.getVelocity();
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
                player.setVelocity(preEventVelocity);
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
            arena.setCanJoin(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();

        Arena arena = tntWars.getArenaManager().getArena(player);
        if (arena != null && arena.isPlayerPlaying(player)) {
            Material blockAtPlayerLocation = e.getPlayer().getLocation().getBlock().getType();
            if (blockAtPlayerLocation == Material.WATER) {
                player.setHealth(0);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();

        Arena arena = tntWars.getArenaManager().getArena(player);
        if (arena != null && arena.isPlayerPlaying(player)) {
            tntWars.getServer().getScheduler().scheduleSyncDelayedTask(tntWars, () -> {
                    if (player.isDead()) {
                        arena.getGame().removeRemainingPlayer(player.getUniqueId());
                        player.spigot().respawn();
                        player.teleport(arena.getSpawn());
                    }
            });
        }
    }


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();

        Arena arena = tntWars.getArenaManager().getArena(player);
        if (arena != null && arena.isPlayerPlaying(player)) {
            if (e.getBlock().getType().equals(Material.TNT)) {
                player.sendMessage(ChatColor.RED + "You cannot place this block.");
                e.setCancelled(true);
            }
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

    public void setSnowballExplosionPower(float snowballExplosionPower) {
        this.snowballExplosionPower = snowballExplosionPower;
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

    public float getSnowballExplosionPower() {
        return snowballExplosionPower;
    }
}
