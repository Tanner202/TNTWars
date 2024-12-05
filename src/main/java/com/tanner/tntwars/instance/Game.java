package com.tanner.tntwars.instance;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.tanner.minigames.GameState;
import com.tanner.minigames.Minigames;
import com.tanner.minigames.instance.Arena;
import com.tanner.minigames.kit.KitType;
import com.tanner.tntwars.TNTWars;
import com.tanner.minigames.team.Team;
import com.tanner.tntwars.kit.Kit;
import com.tanner.tntwars.kit.TNTWarsKitType;
import com.tanner.tntwars.kit.type.*;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
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
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Game implements Listener {

    private TNTWars tntWars;

    private Arena arena;
    private HashMap<UUID, Kit> playerKits;

    private long winWaitTime = 100;
    private int tntInterval = 200;
    private int snowballInterval = 25;

    private BukkitTask giveTntTask;
    private BukkitTask giveSnowballTask;

    private List<UUID> remainingPlayers;

    public Game(Arena arena, TNTWars tntWars) {
        this.arena = arena;
        this.tntWars = tntWars;
        this.remainingPlayers = new ArrayList<>();
        this.playerKits = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, tntWars);

        start();
    }

    public void start() {
        arena.sendMessage(ChatColor.GREEN + "Game Has Started! Knock the other player off by launching TNT. Last team standing wins!");

        giveKits();

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            remainingPlayers.add(player.getUniqueId());
            player.closeInventory();

            Team team = arena.getTeam(player);

            player.setAllowFlight(true);
            player.setFlying(false);
        }

        giveTntTask = Bukkit.getScheduler().runTaskTimer(tntWars, this::givePlayersTnt, 100, tntInterval);
        giveSnowballTask = Bukkit.getScheduler().runTaskTimer(tntWars, this::givePlayersSnowball, 50, snowballInterval);
    }

    private void giveKits() {
        for (UUID uuid : arena.getKits().keySet()) {
            HashMap<UUID, KitType> kits = arena.getKits();
            KitType playerKit = kits.get(uuid);
            Kit kit = null;
            if (playerKit == TNTWarsKitType.BLINDER) {
                kit = new BlinderKit(tntWars, uuid);
            } else if (playerKit == TNTWarsKitType.BUILDER) {
                kit = new BuilderKit(tntWars, uuid);
            } else if (playerKit == TNTWarsKitType.FREEZER) {
                kit = new FreezerKit(tntWars, uuid);
            } else if (playerKit == TNTWarsKitType.SNEAKY) {
                kit = new SneakyKit(tntWars, uuid);
            } else if (playerKit == TNTWarsKitType.LAST_CHANCE) {
                kit = new LastChanceKit(tntWars, uuid);
            } else if (playerKit == TNTWarsKitType.DEFLECTOR) {
                kit = new DeflectorKit(tntWars, uuid);
            }


            if (kit != null) {
                playerKits.put(uuid, kit);
            } else {
                Bukkit.broadcastMessage(ChatColor.RED + "There was an error giving kits to players");
            }
        }
    }

    private void givePlayersTnt() {
        ItemStack throwableTnt = new ItemStack(Material.TNT, 1);
        String message = ChatColor.GREEN + "+1 Throwable Tnt";
        Sound sound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;

        givePlayersItem(throwableTnt, message, sound);
    }

    private void givePlayersSnowball() {
        ItemStack explosiveSnowball = new ItemStack(Material.SNOWBALL, 1);
        String message = ChatColor.AQUA + "+1 Explosive Snowball";
        Sound sound = Sound.ENTITY_ITEM_PICKUP;

        givePlayersItem(explosiveSnowball, message, sound);
    }

    private void givePlayersItem(ItemStack item, String message, Sound sound) {
        for (UUID uuid : remainingPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            player.getInventory().addItem(item);

            player.sendMessage(message);
            player.playSound(player.getLocation(), sound, 1f, 1f);
        }
    }

    public void end() {
        unregisterEvents();

        for (UUID uuid : playerKits.keySet()) {
            playerKits.get(uuid).remove();
        }
        playerKits.clear();

        giveTntTask.cancel();
        giveSnowballTask.cancel();
    }

    public void unregisterEvents() {
        HandlerList.unregisterAll(this);
    }

    public List<UUID> getRemainingPlayers() { return remainingPlayers; }

    public void removeRemainingPlayer(UUID playerUUID) {
        remainingPlayers.remove(playerUUID);

        Team team = getWinningTeam();
        if (team != null) {
            arena.sendMessage(team.getDisplay() + ChatColor.GREEN + " Team has Won! Thanks for Playing!");
            Bukkit.getScheduler().runTaskLater(tntWars, () -> arena.reset(), winWaitTime);
        }
    }

    public void testWin() {
        Team team = getWinningTeam();
        if (team != null) {
            arena.sendMessage(team.getDisplay() + ChatColor.GREEN + " Team has Won! Thanks for Playing!");
            Bukkit.getScheduler().scheduleSyncDelayedTask(tntWars, () -> arena.reset(), winWaitTime);
        }
    }

    private Team getWinningTeam() {
        Team winningTeam = null;
        for (Team team : arena.getTeams()) {
            HashMap<Team, Integer> remainingPlayersPerTeam = getRemainingPlayersPerTeam();
            if (remainingPlayersPerTeam.get(team) > 0) {
                if (winningTeam == null) {
                    winningTeam = team;
                }
                else
                {
                    return null;
                }
            }
        }
        return winningTeam;
    }

    private HashMap<Team, Integer> getRemainingPlayersPerTeam() {
        HashMap<Team, Integer> remainingPlayersPerTeam = new HashMap<>();
        for (Team team : arena.getTeams()) {
            remainingPlayersPerTeam.put(team, 0);
        }
        for (UUID uuid : remainingPlayers) {
            Team playerTeam = arena.getTeam(Bukkit.getPlayer(uuid));
            Integer remainingTeamPlayers = remainingPlayersPerTeam.get(playerTeam);
            remainingPlayersPerTeam.replace(playerTeam, remainingTeamPlayers, remainingTeamPlayers + 1);
        }
        return remainingPlayersPerTeam;
    }

    private boolean isPlayerPlaying(Player player) {
        return (arena.getState() == GameState.LIVE && remainingPlayers.contains(player.getUniqueId()));
    }

    // ---------------- Game Events ----------------

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


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (arena != null && isPlayerPlaying(player)) {
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

            if (arena != null && isPlayerPlaying(player)) {
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
            if (arena != null && isPlayerPlaying(player)) {
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

        if (arena != null && isPlayerPlaying(player)) {
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

        if (arena != null) {
            arena.setCanJoin(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();

        if (arena != null && isPlayerPlaying(player)) {
            Material blockAtPlayerLocation = e.getPlayer().getLocation().getBlock().getType();
            if (blockAtPlayerLocation == Material.WATER) {
                player.setHealth(0);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();

        if (arena != null && isPlayerPlaying(player)) {
            tntWars.getServer().getScheduler().scheduleSyncDelayedTask(tntWars, () -> {
                if (player.isDead()) {
                    removeRemainingPlayer(player.getUniqueId());
                    player.spigot().respawn();
                    player.teleport(arena.getSpawn());
                }
            });
        }
    }


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();

        if (arena != null && isPlayerPlaying(player)) {
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
