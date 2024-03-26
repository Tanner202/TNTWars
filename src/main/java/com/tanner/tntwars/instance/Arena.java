package com.tanner.tntwars.instance;

import com.google.common.collect.TreeMultimap;
import com.tanner.tntwars.GameState;
import com.tanner.tntwars.TNTWars;
import com.tanner.tntwars.kit.Kit;
import com.tanner.tntwars.kit.KitType;
import com.tanner.tntwars.kit.type.BuilderKit;
import com.tanner.tntwars.kit.type.LastChanceKit;
import com.tanner.tntwars.manager.ConfigManager;
import com.tanner.tntwars.team.Team;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Arena {

    private TNTWars tntWars;

    private int id;
    private Location spawn;
    private World world;

    private GameState state;
    private List<UUID> players;
    private HashMap<UUID, Kit> kits;
    private HashMap<UUID, Team> teams;
    private Countdown countdown;
    private Game game;
    private boolean canJoin;

    public Arena(TNTWars tntWars, int id) {
        this.tntWars = tntWars;

        this.id = id;
        setSpawnLocation();
        this.world = spawn.getWorld();

        this.state = GameState.RECRUITING;
        this.players = new ArrayList<>();
        this.kits = new HashMap<>();
        this.teams = new HashMap<>();
        this.countdown = new Countdown(tntWars, this);
        this.game = new Game(this, tntWars);
        canJoin = true;
    }

    public void start() {
        game.start();
    }

    public void reset() {
        if (state.equals(GameState.LIVE)) {
            game.end();
            canJoin = false;

            Location loc = ConfigManager.getLobbySpawn();
            for (UUID uuid : players) {
                Player player = Bukkit.getPlayer(uuid);
                player.getInventory().clear();
                player.teleport(loc);
                removeKit(player.getUniqueId());
            }
            players.clear();
            teams.clear();

            String worldName = world.getName();
            Bukkit.unloadWorld(worldName, false);

            World worldCopy = Bukkit.createWorld(new WorldCreator(worldName));
            worldCopy.setAutoSave(false);
        }
        kits.clear();
        sendTitle("", "");
        state = GameState.RECRUITING;
        countdown.cancel();
        countdown = new Countdown(tntWars, this);
        game = new Game(this, tntWars);
    }

    private void setSpawnLocation()
    {
        FileConfiguration config = tntWars.getConfig();
        spawn = new Location(
                Bukkit.getWorld(config.getString("arenas." + id + ".lobby-spawn.world")),
                config.getDouble("arenas." + id + ".lobby-spawn.x"),
                config.getDouble("arenas." + id + ".lobby-spawn.y"),
                config.getDouble("arenas." + id + ".lobby-spawn.z"),
                (float) config.getDouble("arenas." + id + ".lobby-spawn.yaw"),
                (float) config.getDouble("arenas." + id + ".lobby-spawn.pitch"));
    }

    public void sendMessage(String message) {
        for (UUID uuid : players) {
            Bukkit.getPlayer(uuid).sendMessage(message);
        }
    }

    public void sendTitle(String title, String subtitle) {
        for (UUID uuid : players) {
            Bukkit.getPlayer(uuid).sendTitle(title, subtitle);
        }
    }

    public void addPlayer(Player player) {
        player.getInventory().clear();
        players.add(player.getUniqueId());
        player.teleport(spawn);
        giveLobbyItems(player);

        player.sendMessage(ChatColor.GOLD + "Make sure to choose a kit before the game starts by doing /arena kit!");

        TreeMultimap<Integer, Team> teamCount = TreeMultimap.create();
        for (Team team : Team.values()) {
            teamCount.put(getTeamCount(team), team);
        }

        Team lowestPlayerTeam = (Team) teamCount.values().toArray()[0];
        setTeam(player, lowestPlayerTeam);

        player.sendMessage(ChatColor.AQUA + "You have been automatically placed on " + lowestPlayerTeam.getDisplay() + ChatColor.AQUA + " team.");

        if (state.equals(GameState.RECRUITING) && players.size() >= ConfigManager.getRequiredPlayers()) {
            countdown.start();
        }
    }

    public void removePlayer(Player player) {
        player.getInventory().clear();
        players.remove(player.getUniqueId());
        player.teleport(ConfigManager.getLobbySpawn());
        player.sendTitle("", "");

        removeKit(player.getUniqueId());
        removeTeam(player);

        if (state == GameState.COUNTDOWN && players.size() < ConfigManager.getRequiredPlayers()) {
            sendMessage(ChatColor.RED + "There are not enough players. Countdown stopped.");
            reset();
            return;
        }

        if (state == GameState.LIVE && players.size() < ConfigManager.getRequiredPlayers()) {
            sendMessage(ChatColor.RED + "The game has ended because too many players have left.");
            reset();
        }
    }

    public int getId() { return id; }

    public World getWorld() { return world; }
    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }
    public Game getGame() { return game; }
    public boolean canJoin() { return canJoin; }
    public void setCanJoin(boolean canJoin) { this.canJoin = canJoin; }
    public Location getSpawn() { return spawn; }

    public List<UUID> getPlayers() { return players;}
    public HashMap<UUID, Kit> getKits() { return kits; }
    public void setTeam(Player player, Team team) {
        removeTeam(player);
        teams.put(player.getUniqueId(), team);
    }

    public void removeTeam(Player player) {
        if (teams.containsKey(player.getUniqueId())) {
            teams.remove(player.getUniqueId());
        }
    }

    public Collection<Team> getTeams() {
        return teams.values();
    }

    public int getTeamCount(Team team) {
        int amount = 0;
        for (Team t : teams.values()) {
            if (t == team) {
                amount++;
            }
        }
        return amount;
    }

    public Team getTeam(Player player) {
        return teams.get(player.getUniqueId());
    }

    public void save() {
        canJoin = false;
        for (Player p : getWorld().getPlayers()) {
            p.teleport(ConfigManager.getLobbySpawn());
        }

        String worldName = world.getName();
        Bukkit.unloadWorld(worldName, true);
        World worldCopy = Bukkit.createWorld(new WorldCreator(worldName));
        worldCopy.setAutoSave(false);
    }

    public boolean isPlayerPlaying(Player player) {
        return state == GameState.LIVE && game.getRemainingPlayers().contains(player.getUniqueId());
    }

    public void removeKit(UUID uuid) {
        if (kits.containsKey(uuid)) {
            kits.get(uuid).remove();
            kits.remove(uuid);
        }
    }

    public void setKit(UUID uuid, KitType type) {
        removeKit(uuid);
        if (type == KitType.BUILDER) {
            kits.put(uuid, new BuilderKit(tntWars, uuid));
        } else if (type == KitType.LAST_CHANCE) {
            kits.put(uuid, new LastChanceKit(tntWars, uuid));
        }
    }

    public KitType getKit(Player player) {
        return kits.containsKey(player.getUniqueId()) ? kits.get(player.getUniqueId()).getType() : null;
    }

    private void giveLobbyItems(Player player) {
        ItemStack teamSelection = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemMeta teamSelectionMeta = teamSelection.getItemMeta();
        teamSelectionMeta.setDisplayName(ChatColor.GOLD + "Team Selection");
        teamSelectionMeta.setLocalizedName("Team Selection");
        teamSelection.setItemMeta(teamSelectionMeta);

        ItemStack kitSelection = new ItemStack(Material.DIAMOND);
        ItemMeta kitSelectionMeta = kitSelection.getItemMeta();
        kitSelectionMeta.setDisplayName(ChatColor.BLUE + "Kit Selection");
        kitSelectionMeta.setLocalizedName("Kit Selection");
        kitSelection.setItemMeta(kitSelectionMeta);

        player.getInventory().setItem(0, teamSelection);
        player.getInventory().setItem(1, kitSelection);
    }
}
